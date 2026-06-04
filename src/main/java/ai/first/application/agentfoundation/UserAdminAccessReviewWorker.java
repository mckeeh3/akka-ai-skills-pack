package ai.first.application.agentfoundation;

import ai.first.application.security.AccessReviewWorker;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import ai.first.domain.security.AccessReviewTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AgentRuntimeService;
import ai.first.application.foundation.agent.AgentRuntimeToolResolver;
import ai.first.application.foundation.agent.ModelProviderClient;
import ai.first.application.foundation.agent.ToolRegistry;
import ai.first.application.foundation.agent.WorkstreamRuntimeAgent;

/**
 * Minimal governed worker seam for SMB User Admin access-review tasks.
 *
 * <p>This class is intentionally named as an internal worker, not a request/response UserAdminAgent turn.
 * The normal durable access-review path now starts an Akka AutonomousAgent task through ComponentClient.
 * This worker remains a governed support seam for focused unit tests and fallback projection code; successful
 * results are still model-backed through the governed runtime boundary, provider/runtime failures fail closed as
 * blocked task state, and it exposes no direct mutation method for invitations,
 * memberships, roles, capabilities, authorization, provider config, or audit policy.
 */
public final class UserAdminAccessReviewWorker implements AccessReviewWorker {
  private static final String ACCESS_REVIEW_SKILL_ID = "ua.access-review-triage.v1";
  private static final String ACCESS_REVIEW_REFERENCE_ID = "ua.access-review-policy.v1";

  private final AgentRuntimeService runtimeService;
  private final AgentRuntimeToolResolver toolResolver;
  private final ModelProviderClient modelProviderClient;

  public UserAdminAccessReviewWorker(AgentRuntimeService runtimeService, AgentRuntimeToolResolver toolResolver, ModelProviderClient modelProviderClient) {
    this.runtimeService = Objects.requireNonNull(runtimeService);
    this.toolResolver = Objects.requireNonNull(toolResolver);
    this.modelProviderClient = Objects.requireNonNull(modelProviderClient);
  }

  @Override
  public WorkerResult execute(AuthContextResolver.ResolvedMe actor, AccessReviewTask task, String correlationId) {
    var traceIds = new ArrayList<String>();
    try {
      var tools = toolResolver.resolve(new AgentRuntimeToolResolver.ResolveRuntimeToolsRequest(
          task.tenantId(),
          AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
          actor.selectedContext(),
          "runtime",
          AgentRuntimeService.INVOKE_CAPABILITY,
          correlationId));
      requireGranted(tools, ToolRegistry.USER_ADMIN_EVIDENCE_TOOL_ID);
      requireGranted(tools, ToolRegistry.READ_SKILL_TOOL_ID);
      requireGranted(tools, ToolRegistry.READ_REFERENCE_DOC_TOOL_ID);

      var evidence = tools.runtimeTools().stream()
          .filter(UserAdminEvidenceTools.class::isInstance)
          .map(UserAdminEvidenceTools.class::cast)
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("userAdminEvidence.read binding unavailable"))
          .read("access review taskId=" + task.taskId() + " tenantId=" + task.tenantId() + " no direct mutation");
      var skill = runtimeService.readSkill(new AgentRuntimeService.SkillReadRequest(task.tenantId(), AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, actor.selectedContext(), "runtime", AgentRuntimeService.INVOKE_CAPABILITY, correlationId, ACCESS_REVIEW_SKILL_ID));
      traceIds.add(skill.traceId());
      if (skill.decision() != AgentRuntimeTrace.Decision.ALLOWED) {
        return WorkerResult.blocked("Access-review worker blocked because governed access-review skill could not be loaded.", "blocked_provider_or_runtime", traceIds);
      }
      var reference = runtimeService.readReferenceDoc(new AgentRuntimeService.ReferenceReadRequest(task.tenantId(), AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, actor.selectedContext(), "runtime", AgentRuntimeService.INVOKE_CAPABILITY, correlationId, ACCESS_REVIEW_REFERENCE_ID, "internal_context"));
      traceIds.add(reference.traceId());
      if (reference.decision() != AgentRuntimeTrace.Decision.ALLOWED) {
        return WorkerResult.blocked("Access-review worker blocked because governed access-review reference could not be loaded.", "blocked_provider_or_runtime", traceIds);
      }

      var userPrompt = "Run an SMB User Admin access review investigation for task " + task.taskId()
          + ". Use the scoped evidence, skill, and reference below. Return advisory recommendations only; no direct mutation.\n\n"
          + "# Scoped evidence from userAdminEvidence.read\n" + evidence + "\n\n"
          + "# Loaded skill " + ACCESS_REVIEW_SKILL_ID + "\n" + skill.content() + "\n\n"
          + "# Loaded reference " + ACCESS_REVIEW_REFERENCE_ID + "\n" + reference.content();
      var preparation = runtimeService.prepareWorkstreamAgentInvocation(new AgentRuntimeService.RuntimeInvocationRequest(task.tenantId(), AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, actor.selectedContext(), correlationId, userPrompt));
      traceIds.addAll(preparation.traceIds());
      if (preparation.decision() != AgentRuntimeTrace.Decision.ALLOWED) {
        return WorkerResult.blocked("Access-review worker blocked during governed prompt/model preparation: " + preparation.safeErrorSummary(), "blocked_provider_or_runtime", traceIds);
      }
      var provider = modelProviderClient.invoke(new ModelProviderClient.ModelProviderRequest(
          preparation.governedRequest().modelProviderAlias(),
          preparation.modelConfigRefId(),
          preparation.governedRequest().assembledSystemPrompt(),
          preparation.governedRequest().redactedUserInput(),
          task.tenantId(),
          AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
          correlationId,
          preparation.traceIds()));
      var completed = runtimeService.completeWorkstreamAgentInvocation(
          new AgentRuntimeService.RuntimeInvocationRequest(task.tenantId(), AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, actor.selectedContext(), correlationId, userPrompt),
          preparation,
          new WorkstreamRuntimeAgent.MarkdownResponse(provider.markdown(), AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, correlationId, "model-backed access-review worker result; no direct mutation", provider.safeSummary()));
      traceIds.addAll(completed.traceIds());
      return new WorkerResult(
          AccessReviewTask.Status.COMPLETED,
          100,
          "Model-backed access-review investigation completed through governed runtime; recommendations are advisory and require human review.",
          null,
          List.of(ToolRegistry.USER_ADMIN_EVIDENCE_TOOL_ID, "readSkill:" + ACCESS_REVIEW_SKILL_ID, "readReferenceDoc:" + ACCESS_REVIEW_REFERENCE_ID),
          List.of("model-backed-recommendation:" + safe(provider.markdown())),
          traceIds);
    } catch (ModelProviderClient.ModelProviderException failure) {
      return WorkerResult.blocked("Access-review worker provider invocation failed closed: " + failure.failure().safeSummary(), failure.failure().safeCode(), traceIds);
    } catch (RuntimeException failure) {
      return WorkerResult.blocked("Access-review worker runtime blocked before successful model-backed recommendations: " + safe(failure.getMessage()), "blocked_provider_or_runtime", traceIds);
    }
  }

  private static void requireGranted(AgentRuntimeToolResolver.ResolvedRuntimeTools tools, String toolId) {
    if (!tools.grantedToolIds().contains(toolId)) {
      throw new IllegalStateException("required governed tool not granted: " + toolId);
    }
  }

  private static String safe(String value) {
    var safe = value == null ? "" : value.replaceAll("(?i)(api[_-]?key|secret|token)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
    return safe.length() <= 240 ? safe : safe.substring(0, 240);
  }
}

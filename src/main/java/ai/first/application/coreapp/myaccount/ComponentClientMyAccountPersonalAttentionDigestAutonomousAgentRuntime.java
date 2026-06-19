package ai.first.application.coreapp.myaccount;

import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import ai.first.domain.foundation.identity.Account;
import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.client.ComponentClient;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.coreapp.myaccount.MyAccountPersonalAttentionDigestService;
import ai.first.application.coreapp.myaccount.MyAccountService;
import ai.first.domain.coreapp.myaccount.MyAccountPersonalAttentionDigestTask;
import java.util.List;
import java.util.Objects;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AgentRuntimeService;
import ai.first.application.foundation.agent.AgentRuntimeToolResolver;
import ai.first.application.foundation.agent.ToolRegistry;

/** ComponentClient-backed bridge from governed My Account capabilities to Akka AutonomousAgent personal attention digest tasks. */
public final class ComponentClientMyAccountPersonalAttentionDigestAutonomousAgentRuntime implements MyAccountPersonalAttentionDigestAutonomousAgentRuntime {
  private final ComponentClient componentClient;
  private final AgentRuntimeService runtimeService;
  private final AgentRuntimeToolResolver toolResolver;
  private final MyAccountService myAccountService;

  public ComponentClientMyAccountPersonalAttentionDigestAutonomousAgentRuntime(ComponentClient componentClient, AgentRuntimeService runtimeService, AgentRuntimeToolResolver toolResolver, MyAccountService myAccountService) {
    this.componentClient = Objects.requireNonNull(componentClient);
    this.runtimeService = Objects.requireNonNull(runtimeService);
    this.toolResolver = Objects.requireNonNull(toolResolver);
    this.myAccountService = Objects.requireNonNull(myAccountService);
  }

  @Override
  public StartOutcome start(AuthContextResolver.ResolvedMe actor, MyAccountPersonalAttentionDigestTask starterTask, String correlationId) {
    var traceIds = new java.util.ArrayList<String>();
    try {
      var tools = toolResolver.resolve(new AgentRuntimeToolResolver.ResolveRuntimeToolsRequest(
          starterTask.tenantId(),
          AgentBehaviorSeedLoader.MY_ACCOUNT_AGENT_ID,
          actor.selectedContext(),
          "autonomous_agent_task",
          MyAccountPersonalAttentionDigestService.START_CAPABILITY,
          correlationId));
      requireGranted(tools, ToolRegistry.MY_ACCOUNT_EVIDENCE_TOOL_ID);
      requireGranted(tools, ToolRegistry.READ_SKILL_TOOL_ID);
      requireGranted(tools, ToolRegistry.READ_REFERENCE_DOC_TOOL_ID);

      var preparation = runtimeService.prepareWorkstreamAgentInvocation(new AgentRuntimeService.RuntimeInvocationRequest(
          starterTask.tenantId(),
          AgentBehaviorSeedLoader.MY_ACCOUNT_AGENT_ID,
          actor.selectedContext(),
          correlationId,
          "Prepare governed context for My Account personal attention digest AutonomousAgent task " + starterTask.digestTaskId() + "; advisory only, redacted, no source attention mutation."));
      traceIds.addAll(preparation.traceIds());
      if (preparation.decision() != ai.first.domain.foundation.agent.AgentRuntimeTrace.Decision.ALLOWED) {
        return StartOutcome.blocked(
            "My Account personal attention digest AutonomousAgent start failed closed during governed prompt/model preparation: " + preparation.safeErrorSummary(),
            "blocked_provider_or_runtime",
            traceIds);
      }

      var summary = myAccountService.personalAttention(actor, correlationId);
      var evidenceItems = summary.stream()
          .map(item -> new MyAccountPersonalAttentionDigestTasks.PersonalAttentionEvidenceItem(
              "attention_item:" + safe(string(item.get("itemId"))),
              string(item.get("itemId")),
              string(item.get("sourceWorkstreamId")),
              string(item.get("label")),
              string(item.get("summary")),
              string(item.get("status")),
              string(item.get("severity")),
              string(item.get("category")),
              string(item.get("capabilityId")),
              surfaceRefId(item.get("surfaceRef")),
              string(item.get("redaction")),
              List.of("trace-personal-attention-evidence-" + stableSuffix(string(item.get("itemId")) + ":" + correlationId))))
          .toList();
      var evidenceRefs = evidenceItems.stream().map(MyAccountPersonalAttentionDigestTasks.PersonalAttentionEvidenceItem::evidenceId).toList();
      var request = new MyAccountPersonalAttentionDigestTasks.PersonalAttentionDigestRequest(
          starterTask.digestTaskId(),
          starterTask.tenantId(),
          starterTask.customerId(),
          starterTask.startedByAccountId(),
          starterTask.selectedAuthContextId(),
          evidenceItems,
          evidenceRefs,
          actor.selectedContext().capabilities().stream().filter(capability -> capability.startsWith("my_account.") || capability.startsWith("agent_admin.") || capability.startsWith("audit.trace") || capability.startsWith("governance.") || capability.equals("user_admin.view_overview")).sorted().toList(),
          starterTask.idempotencyKey(),
          correlationId,
          "modelProviderAlias=" + preparation.governedRequest().modelProviderAlias()
              + "; modelConfigRefId=" + preparation.modelConfigRefId()
              + "; promptTraceIds=" + String.join(",", preparation.traceIds())
              + "; advisory personal attention digest only; no source attention mutation allowed; redaction required");
      var autonomousAgentTaskId = componentClient
          .forAutonomousAgent(MyAccountPersonalAttentionDigestAutonomousAgent.class, agentInstanceId(starterTask))
          .runSingleTask(MyAccountPersonalAttentionDigestTasks.personalAttentionDigestInstructions(request));
      traceIds.add("autonomous_task:" + autonomousAgentTaskId);
      return StartOutcome.queued(
          autonomousAgentTaskId,
          "My Account personal attention digest AutonomousAgent task accepted by Akka runtime; result remains backend-projected, redacted, and advisory-only.",
          traceIds);
    } catch (RuntimeException failure) {
      return StartOutcome.blocked(
          "My Account personal attention digest AutonomousAgent start failed closed before successful model-backed task execution: " + safe(failure.getMessage()),
          "blocked_provider_or_runtime",
          traceIds);
    }
  }

  @Override
  public Projection project(MyAccountPersonalAttentionDigestTask starterTask, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return Projection.unchanged();
    try {
      var snapshot = componentClient.forTask(starterTask.autonomousAgentTaskId()).get(MyAccountPersonalAttentionDigestTasks.PERSONAL_ATTENTION_DIGEST);
      if (snapshot.status() == TaskStatus.COMPLETED) {
        var result = snapshot.result().orElse(null);
        if (result == null) return Projection.unchanged();
        var sectionRefs = result.sections().stream()
            .map(section -> "personal_attention_digest_section:" + safe(section.sectionId()) + ":" + safe(section.sourceWorkstreamId()))
            .toList();
        var status = result.authorizedAttentionCount() == 0
            ? MyAccountPersonalAttentionDigestTask.Status.COMPLETED_EMPTY
            : MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED;
        return new Projection(
            status,
            100,
            safe(result.summary()),
            null,
            result,
            result.authorizedAttentionCount(),
            result.evidenceRefs().isEmpty() ? starterTask.evidenceRefs() : result.evidenceRefs(),
            sectionRefs,
            withAutonomousTaskTrace(result.traceIds(), starterTask.autonomousAgentTaskId()));
      }
      if (snapshot.status() == TaskStatus.FAILED) {
        return new Projection(
            MyAccountPersonalAttentionDigestTask.Status.FAILED,
            starterTask.progressPercent(),
            "My Account personal attention digest AutonomousAgent task failed closed: " + snapshot.failureReason().orElse("no safe failure reason supplied"),
            "autonomous_agent_task_failed",
            null,
            starterTask.authorizedAttentionCount(),
            starterTask.evidenceRefs(),
            List.of(),
            List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
      }
      if (snapshot.status() == TaskStatus.CANCELLED) {
        return new Projection(
            MyAccountPersonalAttentionDigestTask.Status.CANCELLED,
            starterTask.progressPercent(),
            "My Account personal attention digest AutonomousAgent task was cancelled before completion; source attention and protected state unchanged.",
            null,
            null,
            starterTask.authorizedAttentionCount(),
            starterTask.evidenceRefs(),
            List.of(),
            List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
      }
      var running = snapshot.status() == TaskStatus.IN_PROGRESS || snapshot.status() == TaskStatus.RESULT_REJECTED
          ? MyAccountPersonalAttentionDigestTask.Status.RUNNING
          : MyAccountPersonalAttentionDigestTask.Status.QUEUED;
      return new Projection(
          running,
          running == MyAccountPersonalAttentionDigestTask.Status.RUNNING ? 50 : Math.max(5, starterTask.progressPercent()),
          "My Account personal attention digest AutonomousAgent task is " + snapshot.status().name().toLowerCase(java.util.Locale.ROOT) + "; backend task snapshot remains source of truth.",
          null,
          null,
          starterTask.authorizedAttentionCount(),
          starterTask.evidenceRefs(),
          starterTask.sectionRefs(),
          List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
    } catch (RuntimeException failure) {
      return new Projection(
          MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
          starterTask.progressPercent(),
          "My Account personal attention digest AutonomousAgent task query failed closed: " + safe(failure.getMessage()),
          "autonomous_agent_task_query_failed",
          null,
          starterTask.authorizedAttentionCount(),
          starterTask.evidenceRefs(),
          List.of(),
          List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
    }
  }

  @Override
  public void cancel(MyAccountPersonalAttentionDigestTask starterTask, String reason, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return;
    try {
      componentClient.forTask(starterTask.autonomousAgentTaskId()).fail("Cancelled by authorized My Account user: " + safe(reason));
    } catch (RuntimeException ignored) {
      // Starter projection remains authoritative for cancel; failed task termination must not resurrect digest work.
    }
  }

  private static void requireGranted(AgentRuntimeToolResolver.ResolvedRuntimeTools tools, String toolId) {
    if (!tools.grantedToolIds().contains(toolId)) {
      throw new IllegalStateException("required governed tool not granted for My Account personal attention digest AutonomousAgent task: " + toolId);
    }
  }

  private static String agentInstanceId(MyAccountPersonalAttentionDigestTask task) {
    return "my-account-personal-attention-digest:" + task.tenantId() + ":" + (task.customerId() == null ? "none" : task.customerId()) + ":" + task.digestTaskId();
  }

  private static List<String> withAutonomousTaskTrace(List<String> traces, String autonomousAgentTaskId) {
    var copy = new java.util.ArrayList<String>();
    if (traces != null) copy.addAll(traces);
    copy.add("autonomous_task:" + autonomousAgentTaskId);
    return copy;
  }

  private static String string(Object value) {
    return value == null ? "" : String.valueOf(value);
  }

  private static String surfaceRefId(Object value) {
    if (value == null) return "";
    var rendered = String.valueOf(value);
    var marker = "surfaceId=";
    var index = rendered.indexOf(marker);
    if (index < 0) return rendered;
    return rendered.substring(index + marker.length()).split("[,)]")[0];
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "my-account-personal-attention-digest").hashCode(), 36);
  }

  private static String safe(String value) {
    var safe = value == null ? "" : value.replaceAll("(?i)(api[_-]?key|secret|token|providerCredential|rawJwt|hiddenPromptText)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
    return safe.length() <= 280 ? safe : safe.substring(0, 280);
  }
}

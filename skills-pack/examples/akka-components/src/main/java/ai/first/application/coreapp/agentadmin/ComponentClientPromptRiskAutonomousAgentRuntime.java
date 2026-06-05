package ai.first.application.coreapp.agentadmin;

import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.client.ComponentClient;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.domain.coreapp.agentadmin.PromptRiskReviewTask;
import java.util.List;
import java.util.Objects;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AgentRuntimeService;
import ai.first.application.foundation.agent.AgentRuntimeToolResolver;
import ai.first.application.foundation.agent.ToolRegistry;

/** ComponentClient-backed bridge from governed Agent Admin capabilities to Akka AutonomousAgent prompt-risk tasks. */
public final class ComponentClientPromptRiskAutonomousAgentRuntime implements PromptRiskAutonomousAgentRuntime {
  private final ComponentClient componentClient;
  private final AgentRuntimeService runtimeService;
  private final AgentRuntimeToolResolver toolResolver;

  public ComponentClientPromptRiskAutonomousAgentRuntime(ComponentClient componentClient, AgentRuntimeService runtimeService, AgentRuntimeToolResolver toolResolver) {
    this.componentClient = Objects.requireNonNull(componentClient);
    this.runtimeService = Objects.requireNonNull(runtimeService);
    this.toolResolver = Objects.requireNonNull(toolResolver);
  }

  @Override
  public StartOutcome start(AuthContextResolver.ResolvedMe actor, PromptRiskReviewTask starterTask, String correlationId) {
    var traceIds = new java.util.ArrayList<String>();
    try {
      var tools = toolResolver.resolve(new AgentRuntimeToolResolver.ResolveRuntimeToolsRequest(
          starterTask.tenantId(),
          AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
          actor.selectedContext(),
          "autonomous_agent_task",
          AgentAdminPromptRiskReviewService.START_CAPABILITY,
          correlationId));
      requireGranted(tools, ToolRegistry.AGENT_ADMIN_EVIDENCE_TOOL_ID);
      requireGranted(tools, ToolRegistry.READ_SKILL_TOOL_ID);
      requireGranted(tools, ToolRegistry.READ_REFERENCE_DOC_TOOL_ID);

      var preparation = runtimeService.prepareWorkstreamAgentInvocation(new AgentRuntimeService.RuntimeInvocationRequest(
          starterTask.tenantId(),
          AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
          actor.selectedContext(),
          correlationId,
          "Prepare governed context for Agent Admin prompt-risk AutonomousAgent task " + starterTask.taskId() + "; advisory only, no direct mutation."));
      traceIds.addAll(preparation.traceIds());
      if (preparation.decision() != ai.first.domain.foundation.agent.AgentRuntimeTrace.Decision.ALLOWED) {
        return StartOutcome.blocked(
            "Agent Admin prompt-risk AutonomousAgent start failed closed during governed prompt/model preparation: " + preparation.safeErrorSummary(),
            "blocked_provider_or_runtime",
            traceIds);
      }

      var request = new AgentAdminPromptRiskTasks.PromptRiskReviewRequest(
          starterTask.taskId(),
          starterTask.tenantId(),
          starterTask.customerId(),
          starterTask.targetAgentDefinitionId(),
          starterTask.proposalId(),
          starterTask.startedByAccountId(),
          correlationId,
          AgentAdminPromptRiskReviewService.START_CAPABILITY,
          "modelProviderAlias=" + preparation.governedRequest().modelProviderAlias()
              + "; modelConfigRefId=" + preparation.modelConfigRefId()
              + "; promptTraceIds=" + String.join(",", preparation.traceIds())
              + "; advisory prompt-risk review only; no direct behavior mutation allowed",
          starterTask.proposedDeltas(),
          starterTask.evidenceRefs());
      var autonomousAgentTaskId = componentClient
          .forAutonomousAgent(AgentAdminPromptRiskAutonomousAgent.class, agentInstanceId(starterTask))
          .runSingleTask(AgentAdminPromptRiskTasks.promptRiskInstructions(request));
      traceIds.add("autonomous_task:" + autonomousAgentTaskId);
      return StartOutcome.queued(
          autonomousAgentTaskId,
          "Agent Admin prompt-risk AutonomousAgent task accepted by Akka runtime; result remains backend-projected and human-review gated.",
          traceIds);
    } catch (RuntimeException failure) {
      return StartOutcome.blocked(
          "Agent Admin prompt-risk AutonomousAgent start failed closed before successful model-backed task execution: " + safe(failure.getMessage()),
          "blocked_provider_or_runtime",
          traceIds);
    }
  }

  @Override
  public Projection project(PromptRiskReviewTask starterTask, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return Projection.unchanged();
    try {
      var snapshot = componentClient.forTask(starterTask.autonomousAgentTaskId()).get(AgentAdminPromptRiskTasks.PROMPT_RISK_REVIEW);
      if (snapshot.status() == TaskStatus.COMPLETED) {
        var result = snapshot.result().orElse(null);
        if (result == null) return Projection.unchanged();
        var findingRefs = result.findings().stream()
            .map(finding -> "prompt_risk_finding:" + safe(finding.findingId()) + ":" + safe(finding.category()))
            .toList();
        return new Projection(
            PromptRiskReviewTask.Status.COMPLETED_REVIEW_REQUIRED,
            100,
            safe(result.summary()),
            null,
            result,
            result.evidenceRefs(),
            findingRefs,
            withAutonomousTaskTrace(result.traceIds(), starterTask.autonomousAgentTaskId()));
      }
      if (snapshot.status() == TaskStatus.FAILED) {
        return new Projection(
            PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
            starterTask.progressPercent(),
            "Agent Admin prompt-risk AutonomousAgent task failed closed: " + snapshot.failureReason().orElse("no safe failure reason supplied"),
            "autonomous_agent_task_failed",
            null,
            starterTask.evidenceRefs(),
            List.of(),
            List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
      }
      if (snapshot.status() == TaskStatus.CANCELLED) {
        return new Projection(
            PromptRiskReviewTask.Status.CANCELLED,
            starterTask.progressPercent(),
            "Agent Admin prompt-risk AutonomousAgent task was cancelled before completion; behavior artifacts unchanged.",
            null,
            null,
            starterTask.evidenceRefs(),
            List.of(),
            List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
      }
      var running = snapshot.status() == TaskStatus.IN_PROGRESS || snapshot.status() == TaskStatus.RESULT_REJECTED
          ? PromptRiskReviewTask.Status.RUNNING
          : PromptRiskReviewTask.Status.QUEUED;
      return new Projection(
          running,
          running == PromptRiskReviewTask.Status.RUNNING ? 50 : Math.max(5, starterTask.progressPercent()),
          "Agent Admin prompt-risk AutonomousAgent task is " + snapshot.status().name().toLowerCase(java.util.Locale.ROOT) + "; backend task snapshot remains source of truth.",
          null,
          null,
          starterTask.evidenceRefs(),
          starterTask.findingRefs(),
          List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
    } catch (RuntimeException failure) {
      return new Projection(
          PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
          starterTask.progressPercent(),
          "Agent Admin prompt-risk AutonomousAgent task query failed closed: " + safe(failure.getMessage()),
          "autonomous_agent_task_query_failed",
          null,
          starterTask.evidenceRefs(),
          List.of(),
          List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
    }
  }

  @Override
  public void cancel(PromptRiskReviewTask starterTask, String reason, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return;
    try {
      componentClient.forTask(starterTask.autonomousAgentTaskId()).fail("Cancelled by authorized Agent Admin: " + safe(reason));
    } catch (RuntimeException ignored) {
      // Starter projection remains authoritative for cancel; failed task termination must not resurrect prompt-risk work.
    }
  }

  private static void requireGranted(AgentRuntimeToolResolver.ResolvedRuntimeTools tools, String toolId) {
    if (!tools.grantedToolIds().contains(toolId)) {
      throw new IllegalStateException("required governed tool not granted for prompt-risk AutonomousAgent task: " + toolId);
    }
  }

  private static String agentInstanceId(PromptRiskReviewTask task) {
    return "agent-admin-prompt-risk:" + task.tenantId() + ":" + (task.customerId() == null ? "none" : task.customerId()) + ":" + task.taskId();
  }

  private static List<String> withAutonomousTaskTrace(List<String> traces, String autonomousAgentTaskId) {
    var copy = new java.util.ArrayList<String>();
    if (traces != null) copy.addAll(traces);
    copy.add("autonomous_task:" + autonomousAgentTaskId);
    return copy;
  }

  private static String safe(String value) {
    var safe = value == null ? "" : value.replaceAll("(?i)(api[_-]?key|secret|token)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
    return safe.length() <= 240 ? safe : safe.substring(0, 240);
  }
}

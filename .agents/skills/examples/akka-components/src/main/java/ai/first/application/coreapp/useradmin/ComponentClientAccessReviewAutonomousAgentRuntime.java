package ai.first.application.coreapp.useradmin;

import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.client.ComponentClient;
import ai.first.application.coreapp.useradmin.AccessReviewAutonomousAgentRuntime;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.coreapp.useradmin.UserAdminAccessReviewService;
import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import java.util.List;
import java.util.Objects;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AgentRuntimeService;
import ai.first.application.foundation.agent.AgentRuntimeToolResolver;
import ai.first.application.foundation.agent.ToolRegistry;

/** ComponentClient-backed bridge from governed User Admin capabilities to Akka AutonomousAgent tasks. */
public final class ComponentClientAccessReviewAutonomousAgentRuntime implements AccessReviewAutonomousAgentRuntime {
  private final ComponentClient componentClient;
  private final AgentRuntimeService runtimeService;
  private final AgentRuntimeToolResolver toolResolver;

  public ComponentClientAccessReviewAutonomousAgentRuntime(ComponentClient componentClient, AgentRuntimeService runtimeService, AgentRuntimeToolResolver toolResolver) {
    this.componentClient = Objects.requireNonNull(componentClient);
    this.runtimeService = Objects.requireNonNull(runtimeService);
    this.toolResolver = Objects.requireNonNull(toolResolver);
  }

  @Override
  public StartOutcome start(AuthContextResolver.ResolvedMe actor, AccessReviewTask starterTask, String correlationId) {
    var traceIds = new java.util.ArrayList<String>();
    try {
      var tools = toolResolver.resolve(new AgentRuntimeToolResolver.ResolveRuntimeToolsRequest(
          starterTask.tenantId(),
          AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
          actor.selectedContext(),
          "autonomous_agent_task",
          UserAdminAccessReviewService.START_CAPABILITY,
          correlationId));
      requireGranted(tools, ToolRegistry.USER_ADMIN_EVIDENCE_TOOL_ID);
      requireGranted(tools, ToolRegistry.READ_SKILL_TOOL_ID);
      requireGranted(tools, ToolRegistry.READ_REFERENCE_DOC_TOOL_ID);

      var preparation = runtimeService.prepareWorkstreamAgentInvocation(new AgentRuntimeService.RuntimeInvocationRequest(
          starterTask.tenantId(),
          AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
          actor.selectedContext(),
          correlationId,
          "Prepare governed context for User Admin access-review AutonomousAgent task " + starterTask.taskId() + "; no direct mutation."));
      traceIds.addAll(preparation.traceIds());
      if (preparation.decision() != ai.first.domain.foundation.agent.AgentRuntimeTrace.Decision.ALLOWED) {
        return StartOutcome.blocked(
            "Access-review AutonomousAgent start failed closed during governed prompt/model preparation: " + preparation.safeErrorSummary(),
            "blocked_provider_or_runtime",
            traceIds);
      }

      var request = new UserAdminAccessReviewTasks.AccessReviewAutonomousAgentRequest(
          starterTask.taskId(),
          starterTask.tenantId(),
          starterTask.customerId(),
          starterTask.scopeType().name(),
          starterTask.startedByAccountId(),
          correlationId,
          UserAdminAccessReviewService.START_CAPABILITY,
          "modelProviderAlias=" + preparation.governedRequest().modelProviderAlias()
              + "; modelConfigRefId=" + preparation.modelConfigRefId()
              + "; promptTraceIds=" + String.join(",", preparation.traceIds())
              + "; no direct access mutation allowed",
          starterTask.evidenceRefs());
      var autonomousAgentTaskId = componentClient
          .forAutonomousAgent(UserAdminAccessReviewAutonomousAgent.class, agentInstanceId(starterTask))
          .runSingleTask(UserAdminAccessReviewTasks.accessReviewInstructions(request));
      traceIds.add("autonomous_task:" + autonomousAgentTaskId);
      return StartOutcome.queued(
          autonomousAgentTaskId,
          "Access-review AutonomousAgent task accepted by Akka runtime; progress and result remain backend-projected and human-review gated.",
          traceIds);
    } catch (RuntimeException failure) {
      return StartOutcome.blocked(
          "Access-review AutonomousAgent start failed closed before successful model-backed task execution: " + safe(failure.getMessage()),
          "blocked_provider_or_runtime",
          traceIds);
    }
  }

  @Override
  public Projection project(AccessReviewTask starterTask, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return Projection.unchanged();
    try {
      var snapshot = componentClient.forTask(starterTask.autonomousAgentTaskId()).get(UserAdminAccessReviewTasks.ACCESS_REVIEW);
      if (snapshot.status() == TaskStatus.COMPLETED) {
        var result = snapshot.result().orElse(null);
        if (result == null) return Projection.unchanged();
        var recommendationRefs = result.recommendations().stream()
            .map(recommendation -> "autonomous_agent_recommendation:" + safe(recommendation.recommendationId()) + ":" + safe(recommendation.summary()))
            .toList();
        return new Projection(
            AccessReviewTask.Status.COMPLETED,
            100,
            safe(result.summary()),
            null,
            result,
            result.evidenceRefs(),
            recommendationRefs,
            withAutonomousTaskTrace(result.traceIds(), starterTask.autonomousAgentTaskId()));
      }
      if (snapshot.status() == TaskStatus.FAILED) {
        return new Projection(
            AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
            starterTask.progressPercent(),
            "Access-review AutonomousAgent task failed closed: " + snapshot.failureReason().orElse("no safe failure reason supplied"),
            "autonomous_agent_task_failed",
            null,
            starterTask.evidenceRefs(),
            List.of(),
            List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
      }
      if (snapshot.status() == TaskStatus.CANCELLED) {
        return new Projection(
            AccessReviewTask.Status.CANCELLED,
            starterTask.progressPercent(),
            "Access-review AutonomousAgent task was cancelled before completion; access state unchanged.",
            null,
            null,
            starterTask.evidenceRefs(),
            List.of(),
            List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
      }
      var running = snapshot.status() == TaskStatus.IN_PROGRESS || snapshot.status() == TaskStatus.RESULT_REJECTED
          ? AccessReviewTask.Status.RUNNING
          : AccessReviewTask.Status.QUEUED;
      return new Projection(
          running,
          running == AccessReviewTask.Status.RUNNING ? 50 : Math.max(5, starterTask.progressPercent()),
          "Access-review AutonomousAgent task is " + snapshot.status().name().toLowerCase(java.util.Locale.ROOT) + "; backend task snapshot remains source of truth.",
          null,
          null,
          starterTask.evidenceRefs(),
          starterTask.recommendationRefs(),
          List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
    } catch (RuntimeException failure) {
      return new Projection(
          AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
          starterTask.progressPercent(),
          "Access-review AutonomousAgent task query failed closed: " + safe(failure.getMessage()),
          "autonomous_agent_task_query_failed",
          null,
          starterTask.evidenceRefs(),
          List.of(),
          List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
    }
  }

  @Override
  public void cancel(AccessReviewTask starterTask, String reason, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return;
    try {
      componentClient.forTask(starterTask.autonomousAgentTaskId()).fail("Cancelled by authorized User Admin: " + safe(reason));
    } catch (RuntimeException ignored) {
      // Starter projection is authoritative for cancel; failed task termination must not resurrect access-review work.
    }
  }

  private static void requireGranted(AgentRuntimeToolResolver.ResolvedRuntimeTools tools, String toolId) {
    if (!tools.grantedToolIds().contains(toolId)) {
      throw new IllegalStateException("required governed tool not granted for AutonomousAgent task: " + toolId);
    }
  }

  private static String agentInstanceId(AccessReviewTask task) {
    return "user-admin-access-review:" + task.tenantId() + ":" + (task.customerId() == null ? "none" : task.customerId()) + ":" + task.taskId();
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

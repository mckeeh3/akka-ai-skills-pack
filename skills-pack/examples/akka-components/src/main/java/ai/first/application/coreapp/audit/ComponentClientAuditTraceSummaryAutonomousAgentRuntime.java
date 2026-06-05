package ai.first.application.coreapp.audit;

import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.client.ComponentClient;
import ai.first.application.coreapp.audit.AuditTraceSummaryService;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.domain.coreapp.audit.AuditTraceSummaryTask;
import java.util.List;
import java.util.Objects;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AgentRuntimeService;
import ai.first.application.foundation.agent.AgentRuntimeToolResolver;
import ai.first.application.foundation.agent.ToolRegistry;

/** ComponentClient-backed bridge from governed Audit/Trace capabilities to Akka AutonomousAgent summary tasks. */
public final class ComponentClientAuditTraceSummaryAutonomousAgentRuntime implements AuditTraceSummaryAutonomousAgentRuntime {
  private final ComponentClient componentClient;
  private final AgentRuntimeService runtimeService;
  private final AgentRuntimeToolResolver toolResolver;

  public ComponentClientAuditTraceSummaryAutonomousAgentRuntime(ComponentClient componentClient, AgentRuntimeService runtimeService, AgentRuntimeToolResolver toolResolver) {
    this.componentClient = Objects.requireNonNull(componentClient);
    this.runtimeService = Objects.requireNonNull(runtimeService);
    this.toolResolver = Objects.requireNonNull(toolResolver);
  }

  @Override
  public StartOutcome start(AuthContextResolver.ResolvedMe actor, AuditTraceSummaryTask starterTask, String correlationId) {
    var traceIds = new java.util.ArrayList<String>();
    try {
      var tools = toolResolver.resolve(new AgentRuntimeToolResolver.ResolveRuntimeToolsRequest(
          starterTask.tenantId(),
          AgentBehaviorSeedLoader.AUDIT_TRACE_AGENT_ID,
          actor.selectedContext(),
          "autonomous_agent_task",
          AuditTraceSummaryService.START_CAPABILITY,
          correlationId));
      requireGranted(tools, ToolRegistry.AUDIT_TRACE_EVIDENCE_TOOL_ID);
      requireGranted(tools, ToolRegistry.READ_SKILL_TOOL_ID);
      requireGranted(tools, ToolRegistry.READ_REFERENCE_DOC_TOOL_ID);

      var preparation = runtimeService.prepareWorkstreamAgentInvocation(new AgentRuntimeService.RuntimeInvocationRequest(
          starterTask.tenantId(),
          AgentBehaviorSeedLoader.AUDIT_TRACE_AGENT_ID,
          actor.selectedContext(),
          correlationId,
          "Prepare governed context for Audit/Trace summary AutonomousAgent task " + starterTask.taskId() + "; advisory only, no direct mutation."));
      traceIds.addAll(preparation.traceIds());
      if (preparation.decision() != ai.first.domain.foundation.agent.AgentRuntimeTrace.Decision.ALLOWED) {
        return StartOutcome.blocked(
            "Audit/Trace summary AutonomousAgent start failed closed during governed prompt/model preparation: " + preparation.safeErrorSummary(),
            "blocked_provider_or_runtime",
            traceIds);
      }

      var request = new AuditTraceSummaryTasks.AuditTraceSummaryRequest(
          starterTask.taskId(),
          starterTask.tenantId(),
          starterTask.customerId(),
          starterTask.selectedAuthContextId(),
          starterTask.startedByAccountId(),
          starterTask.windowStart(),
          starterTask.windowEnd(),
          starterTask.evidenceCategories(),
          correlationId,
          AuditTraceSummaryService.START_CAPABILITY,
          "modelProviderAlias=" + preparation.governedRequest().modelProviderAlias()
              + "; modelConfigRefId=" + preparation.modelConfigRefId()
              + "; promptTraceIds=" + String.join(",", preparation.traceIds())
              + "; advisory audit summary only; no direct mutation allowed; redaction required",
          starterTask.evidenceRefs());
      var autonomousAgentTaskId = componentClient
          .forAutonomousAgent(AuditTraceSummaryAutonomousAgent.class, agentInstanceId(starterTask))
          .runSingleTask(AuditTraceSummaryTasks.summarizeAuditWindowInstructions(request));
      traceIds.add("autonomous_task:" + autonomousAgentTaskId);
      return StartOutcome.queued(
          autonomousAgentTaskId,
          "Audit/Trace summary AutonomousAgent task accepted by Akka runtime; result remains backend-projected, redacted, and human-review gated.",
          traceIds);
    } catch (RuntimeException failure) {
      return StartOutcome.blocked(
          "Audit/Trace summary AutonomousAgent start failed closed before successful model-backed task execution: " + safe(failure.getMessage()),
          "blocked_provider_or_runtime",
          traceIds);
    }
  }

  @Override
  public Projection project(AuditTraceSummaryTask starterTask, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return Projection.unchanged();
    try {
      var snapshot = componentClient.forTask(starterTask.autonomousAgentTaskId()).get(AuditTraceSummaryTasks.SUMMARIZE_AUDIT_WINDOW);
      if (snapshot.status() == TaskStatus.COMPLETED) {
        var result = snapshot.result().orElse(null);
        if (result == null) return Projection.unchanged();
        var findingRefs = result.findings().stream()
            .map(finding -> "audit_trace_summary_finding:" + safe(finding.findingId()) + ":" + safe(finding.category()))
            .toList();
        return new Projection(
            AuditTraceSummaryTask.Status.COMPLETED_REVIEW_REQUIRED,
            100,
            safe(result.executiveSummary()),
            null,
            result,
            result.evidenceRefs().isEmpty() ? starterTask.evidenceRefs() : result.evidenceRefs(),
            findingRefs,
            withAutonomousTaskTrace(result.traceRefs(), starterTask.autonomousAgentTaskId()));
      }
      if (snapshot.status() == TaskStatus.FAILED) {
        return new Projection(
            AuditTraceSummaryTask.Status.FAILED,
            starterTask.progressPercent(),
            "Audit/Trace summary AutonomousAgent task failed closed: " + snapshot.failureReason().orElse("no safe failure reason supplied"),
            "autonomous_agent_task_failed",
            null,
            starterTask.evidenceRefs(),
            List.of(),
            List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
      }
      if (snapshot.status() == TaskStatus.CANCELLED) {
        return new Projection(
            AuditTraceSummaryTask.Status.CANCELLED,
            starterTask.progressPercent(),
            "Audit/Trace summary AutonomousAgent task was cancelled before completion; traces and protected state unchanged.",
            null,
            null,
            starterTask.evidenceRefs(),
            List.of(),
            List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
      }
      var running = snapshot.status() == TaskStatus.IN_PROGRESS || snapshot.status() == TaskStatus.RESULT_REJECTED
          ? AuditTraceSummaryTask.Status.RUNNING
          : AuditTraceSummaryTask.Status.QUEUED;
      return new Projection(
          running,
          running == AuditTraceSummaryTask.Status.RUNNING ? 50 : Math.max(5, starterTask.progressPercent()),
          "Audit/Trace summary AutonomousAgent task is " + snapshot.status().name().toLowerCase(java.util.Locale.ROOT) + "; backend task snapshot remains source of truth.",
          null,
          null,
          starterTask.evidenceRefs(),
          starterTask.findingRefs(),
          List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
    } catch (RuntimeException failure) {
      return new Projection(
          AuditTraceSummaryTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
          starterTask.progressPercent(),
          "Audit/Trace summary AutonomousAgent task query failed closed: " + safe(failure.getMessage()),
          "autonomous_agent_task_query_failed",
          null,
          starterTask.evidenceRefs(),
          List.of(),
          List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
    }
  }

  @Override
  public void cancel(AuditTraceSummaryTask starterTask, String reason, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return;
    try {
      componentClient.forTask(starterTask.autonomousAgentTaskId()).fail("Cancelled by authorized Audit/Trace reviewer: " + safe(reason));
    } catch (RuntimeException ignored) {
      // Starter projection remains authoritative for cancel; failed task termination must not resurrect audit summary work.
    }
  }

  private static void requireGranted(AgentRuntimeToolResolver.ResolvedRuntimeTools tools, String toolId) {
    if (!tools.grantedToolIds().contains(toolId)) {
      throw new IllegalStateException("required governed tool not granted for Audit/Trace Summary AutonomousAgent task: " + toolId);
    }
  }

  private static String agentInstanceId(AuditTraceSummaryTask task) {
    return "audit-trace-summary:" + task.tenantId() + ":" + (task.customerId() == null ? "none" : task.customerId()) + ":" + task.taskId();
  }

  private static List<String> withAutonomousTaskTrace(List<String> traces, String autonomousAgentTaskId) {
    var copy = new java.util.ArrayList<String>();
    if (traces != null) copy.addAll(traces);
    copy.add("autonomous_task:" + autonomousAgentTaskId);
    return copy;
  }

  private static String safe(String value) {
    var safe = value == null ? "" : value.replaceAll("(?i)(api[_-]?key|secret|token|providerCredential)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
    return safe.length() <= 280 ? safe : safe.substring(0, 280);
  }
}

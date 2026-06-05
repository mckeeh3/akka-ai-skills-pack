package com.example.application;

import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.client.ComponentClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** ComponentClient-backed bridge from governed Audit/Trace capabilities to Akka AutonomousAgent summary tasks. */
public class ComponentClientAuditTraceSummaryAutonomousAgentRuntime implements AuditTraceSummaryAutonomousAgentRuntime {
  private final ComponentClient componentClient;
  private final boolean providerReady;
  private final boolean toolBoundaryGranted;
  private final boolean evidenceReadGranted;

  public ComponentClientAuditTraceSummaryAutonomousAgentRuntime(ComponentClient componentClient, boolean providerReady, boolean toolBoundaryGranted, boolean evidenceReadGranted) {
    this.componentClient = componentClient;
    this.providerReady = providerReady;
    this.toolBoundaryGranted = toolBoundaryGranted;
    this.evidenceReadGranted = evidenceReadGranted;
  }

  @Override
  public StartOutcome start(AuditTraceSummaryTasks.AuditTraceSummaryRequest request) {
    var traceIds = new ArrayList<String>();
    if (componentClient == null || !providerReady || !toolBoundaryGranted || !evidenceReadGranted) {
      return StartOutcome.blocked(
          "Audit/Trace summary AutonomousAgent start failed closed: missing ComponentClient, provider/model profile, ToolPermissionBoundary grants, readSkill/readReferenceDoc, or auditTraceSummaryEvidence.read. No deterministic/model-less fake success is returned.",
          "blocked_provider_or_runtime",
          List.of("trace-audit-summary-start-blocked-" + stableSuffix(request.summaryTaskId() + request.correlationId())));
    }
    try {
      var autonomousAgentTaskId = componentClient
          .forAutonomousAgent(AuditTraceSummaryAutonomousAgent.class, agentInstanceId(request))
          .runSingleTask(AuditTraceSummaryTasks.summarizeAuditWindowInstructions(request));
      traceIds.add("autonomous_task:" + autonomousAgentTaskId);
      traceIds.add("worker.task.queued");
      traceIds.add("workflow.audit_trace.summary_started");
      return StartOutcome.queued(
          autonomousAgentTaskId,
          "Audit/Trace summary AutonomousAgent task accepted by Akka runtime; backend projection and ComponentClient.forTask remain the source of truth.",
          traceIds);
    } catch (RuntimeException failure) {
      return StartOutcome.blocked(
          "Audit/Trace summary AutonomousAgent start failed closed before successful model-backed task execution: " + safe(failure.getMessage()),
          "blocked_provider_or_runtime",
          traceIds);
    }
  }

  @Override
  public Projection project(AuditTraceSummaryTaskProjection projection) {
    if (componentClient == null || projection.autonomousAgentTaskId() == null || projection.autonomousAgentTaskId().isBlank()) {
      return new Projection(AuditTraceSummaryTaskProjection.Status.BLOCKED_PROVIDER_OR_RUNTIME, projection.progressPercent(), "Audit/Trace summary task query failed closed because ComponentClient/autonomous task id is unavailable; no fake success.", "blocked_provider_or_runtime", null, projection.evidenceRefs(), List.of(), projection.traceIds());
    }
    try {
      var snapshot = componentClient.forTask(projection.autonomousAgentTaskId()).get(AuditTraceSummaryTasks.SUMMARIZE_AUDIT_WINDOW);
      if (snapshot.status() == TaskStatus.COMPLETED) {
        var result = snapshot.result().orElse(null);
        if (result == null) {
          return new Projection(AuditTraceSummaryTaskProjection.Status.FAILED, projection.progressPercent(), "Audit/Trace summary task completed without a valid typed result; no fake findings projected.", "missing_typed_result", null, projection.evidenceRefs(), List.of(), projection.traceIds());
        }
        var findingRefs = result.findings().stream()
            .map(finding -> "audit_trace_summary_finding:" + safe(finding.findingId()) + ":" + safe(finding.category()))
            .toList();
        var traces = new ArrayList<>(result.traceRefs());
        traces.add("autonomous_task:" + projection.autonomousAgentTaskId());
        traces.add("worker.task.completed_review_required");
        traces.add("workflow.audit_trace.summary_completed_review_required");
        return new Projection(AuditTraceSummaryTaskProjection.Status.COMPLETED_REVIEW_REQUIRED, 100, safe(result.executiveSummary()), null, result, result.evidenceRefs(), findingRefs, traces);
      }
      if (snapshot.status() == TaskStatus.FAILED) {
        return new Projection(AuditTraceSummaryTaskProjection.Status.FAILED, projection.progressPercent(), "Audit/Trace summary AutonomousAgent task failed closed: " + safe(snapshot.failureReason().orElse("no safe failure reason supplied")), "autonomous_agent_task_failed", null, projection.evidenceRefs(), List.of(), List.of("worker.task.failed", "workflow.audit_trace.summary_failed"));
      }
      if (snapshot.status() == TaskStatus.CANCELLED) {
        return new Projection(AuditTraceSummaryTaskProjection.Status.CANCELLED, projection.progressPercent(), "Audit/Trace summary AutonomousAgent task was cancelled; protected state unchanged.", null, null, projection.evidenceRefs(), projection.findingRefs(), List.of("worker.task.cancelled", "workflow.audit_trace.summary_cancelled"));
      }
      var status = snapshot.status() == TaskStatus.IN_PROGRESS || snapshot.status() == TaskStatus.RESULT_REJECTED
          ? AuditTraceSummaryTaskProjection.Status.RUNNING
          : AuditTraceSummaryTaskProjection.Status.QUEUED;
      return new Projection(status, status == AuditTraceSummaryTaskProjection.Status.RUNNING ? 50 : Math.max(5, projection.progressPercent()), "Audit/Trace summary AutonomousAgent task is " + snapshot.status().name().toLowerCase(Locale.ROOT) + "; backend task snapshot remains source of truth.", null, null, projection.evidenceRefs(), projection.findingRefs(), List.of(status == AuditTraceSummaryTaskProjection.Status.RUNNING ? "worker.task.running" : "worker.task.queued"));
    } catch (RuntimeException failure) {
      return new Projection(AuditTraceSummaryTaskProjection.Status.BLOCKED_PROVIDER_OR_RUNTIME, projection.progressPercent(), "Audit/Trace summary task query failed closed: " + safe(failure.getMessage()), "blocked_provider_or_runtime", null, projection.evidenceRefs(), List.of(), List.of("worker.task.blocked_provider_or_runtime", "workflow.audit_trace.summary_blocked_provider_or_runtime"));
    }
  }

  @Override
  public void cancel(AuditTraceSummaryTaskProjection projection, String reason) {
    if (componentClient == null || projection.autonomousAgentTaskId() == null || projection.autonomousAgentTaskId().isBlank()) return;
    componentClient.forTask(projection.autonomousAgentTaskId()).fail("Cancelled by authorized Audit/Trace reviewer: " + safe(reason));
  }

  private static String agentInstanceId(AuditTraceSummaryTasks.AuditTraceSummaryRequest request) {
    return "audit-trace-summary:" + request.tenantId() + ":" + Objects.toString(request.customerId(), "none") + ":" + request.summaryTaskId();
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "audit-trace-summary").hashCode(), 36);
  }

  private static String safe(String value) {
    var safe = value == null ? "" : value.replaceAll("(?i)(api[_-]?key|secret|token|credential|bearer|jwt)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
    return safe.length() <= 280 ? safe : safe.substring(0, 280);
  }
}

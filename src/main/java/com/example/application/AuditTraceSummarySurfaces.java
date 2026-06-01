package com.example.application;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/** Backend-owned v3 events, attention, and structured surfaces for Audit/Trace summary tasks. */
public final class AuditTraceSummarySurfaces {
  public static final String FUNCTIONAL_AGENT_ID = "agent-audit-trace";
  public static final String PROGRESS_CONTRACT = "audit.trace.summaryProgress.v1";
  public static final String REVIEW_CONTRACT = "audit.trace.summaryReview.v1";
  public static final String STARTED = "workflow.audit_trace.summary_started";
  public static final String BLOCKED = "workflow.audit_trace.summary_blocked_provider_or_runtime";
  public static final String FAILED = "workflow.audit_trace.summary_failed";
  public static final String COMPLETED = "workflow.audit_trace.summary_completed_review_required";
  public static final String CANCELLED = "workflow.audit_trace.summary_cancelled";
  public static final String ACCEPTED = "workflow.audit_trace.summary_result_accepted";
  public static final String REJECTED = "workflow.audit_trace.summary_result_rejected";

  private AuditTraceSummarySurfaces() {}

  public static AuditTraceSummaryTaskProjection applyRuntimeProjection(AuditTraceSummaryTaskProjection current, AuditTraceSummaryAutonomousAgentRuntime.Projection runtimeProjection, Instant now) {
    return new AuditTraceSummaryTaskProjection(
        current.summaryTaskId(),
        current.autonomousAgentTaskId(),
        current.tenantId(),
        current.customerId(),
        current.selectedAuthContextId(),
        current.startedByAccountId(),
        current.idempotencyKey(),
        runtimeProjection.status(),
        runtimeProjection.progressPercent(),
        safe(runtimeProjection.summary()),
        runtimeProjection.blockerCode(),
        runtimeProjection.result(),
        runtimeProjection.evidenceRefs().isEmpty() ? current.evidenceRefs() : runtimeProjection.evidenceRefs(),
        runtimeProjection.findingRefs(),
        merge(current.traceIds(), runtimeProjection.traceIds()),
        current.humanDisposition(),
        current.humanDispositionReason(),
        now);
  }

  public static AuditTraceSummaryTaskProjection withDisposition(AuditTraceSummaryTaskProjection current, AuditTraceSummaryTaskProjection.Status nextStatus, String reviewerDisposition, String reason, Instant now) {
    var workerTrace = switch (nextStatus) {
      case ACCEPTED -> "worker.task.accepted";
      case REJECTED_RESULT -> "worker.task.rejected_result";
      case CANCELLED -> "worker.task.cancelled";
      default -> "worker.task." + nextStatus.name().toLowerCase(Locale.ROOT);
    };
    var workflowTrace = workflowEventType(nextStatus);
    return new AuditTraceSummaryTaskProjection(
        current.summaryTaskId(), current.autonomousAgentTaskId(), current.tenantId(), current.customerId(), current.selectedAuthContextId(), current.startedByAccountId(), current.idempotencyKey(), nextStatus, current.progressPercent(), safe(current.progressSummary()), current.blockerCode(), current.result(), current.evidenceRefs(), current.findingRefs(), merge(current.traceIds(), List.of(workerTrace, workflowTrace)), reviewerDisposition, safe(reason), now);
  }

  public static List<WorkstreamEventEnvelope> eventsFor(AuditTraceSummaryTaskProjection projection) {
    var workerType = workerEventType(projection.status());
    var workflowType = workflowEventType(projection.status());
    return List.of(event(projection, workerType, "task", "worker"), event(projection, workflowType, "workflow", "process"));
  }

  public static Optional<AttentionItem> attentionFor(AuditTraceSummaryTaskProjection projection) {
    var attentionId = attentionId(projection.summaryTaskId());
    return switch (projection.status()) {
      case BLOCKED_PROVIDER_OR_RUNTIME -> Optional.of(active(attentionId, projection, "critical", "Runtime/provider configuration required", projection.progressSummary()));
      case FAILED -> Optional.of(active(attentionId, projection, "critical", "Audit summary failed", projection.progressSummary()));
      case COMPLETED_REVIEW_REQUIRED -> Optional.of(active(attentionId, projection, severityFor(projection.result()), "Audit summary ready for review", projection.progressSummary()));
      case REJECTED_RESULT -> Optional.of(active(attentionId, projection, "warning", "Audit summary rejected", Objects.requireNonNullElse(projection.humanDispositionReason(), projection.progressSummary())));
      case ACCEPTED, CANCELLED -> Optional.of(new AttentionItem(attentionId, projection.tenantId(), projection.customerId(), FUNCTIONAL_AGENT_ID, projection.summaryTaskId(), projection.status().name().toLowerCase(Locale.ROOT), "info", true, sourceRefs(projection), projection.traceIds(), "Task-state attention resolved by backend projection."));
      case QUEUED, RUNNING -> Optional.empty();
    };
  }

  public static SummaryProgressSurface progressSurface(AuditTraceSummaryTaskProjection projection) {
    return new SummaryProgressSurface(
        PROGRESS_CONTRACT,
        projection.summaryTaskId(),
        projection.autonomousAgentTaskId(),
        statusValue(projection.status()),
        projection.progressPercent(),
        safe(projection.progressSummary()),
        safe(blockerReason(projection)),
        projection.tenantId(),
        projection.customerId(),
        AuditTraceSummaryTasks.START_CAPABILITY,
        projection.evidenceRefs(),
        sourceRefs(projection),
        projection.traceIds(),
        "Raw JWTs, provider credentials, hidden prompts, raw tool payloads, invitation tokens, and cross-tenant evidence are omitted; hidden refs return not_found_or_redacted.",
        true,
        progressActions(projection.status()));
  }

  public static SummaryReviewSurface reviewSurface(AuditTraceSummaryTaskProjection projection) {
    var result = projection.result();
    return new SummaryReviewSurface(
        REVIEW_CONTRACT,
        projection.summaryTaskId(),
        projection.autonomousAgentTaskId(),
        statusValue(projection.status()),
        true,
        projection.status() == AuditTraceSummaryTaskProjection.Status.COMPLETED_REVIEW_REQUIRED || projection.status() == AuditTraceSummaryTaskProjection.Status.REJECTED_RESULT,
        result == null ? null : result.overallRisk().name().toLowerCase(Locale.ROOT),
        result == null ? safe(projection.progressSummary()) : safe(result.executiveSummary()),
        result == null ? List.of() : result.findings(),
        result == null ? List.of() : result.providerReadinessFindings(),
        result == null ? List.of() : result.authorizationDenialFindings(),
        result == null ? List.of() : result.agentWorkFindings(),
        result == null ? "not_found_or_redacted" : safe(result.omittedEvidenceSummary()),
        result == null ? "No raw prompts, raw tool payloads, raw JWTs, provider credentials, invitation tokens, or cross-tenant evidence are included." : safe(result.redactionSummary()),
        sourceRefs(projection),
        projection.traceIds(),
        reviewActions(projection.status()));
  }

  public static String attentionId(String summaryTaskId) {
    return "attention:worker-task:" + summaryTaskId + ":task-state";
  }

  private static WorkstreamEventEnvelope event(AuditTraceSummaryTaskProjection projection, String eventType, String eventFamily, String category) {
    return new WorkstreamEventEnvelope(
        "evt:" + projection.summaryTaskId() + ":" + eventType,
        eventType,
        eventFamily,
        category,
        projection.tenantId(),
        projection.customerId(),
        FUNCTIONAL_AGENT_ID,
        projection.summaryTaskId(),
        sourceRefs(projection),
        projection.traceIds(),
        projection.idempotencyKey(),
        projection.updatedAt() == null ? Instant.EPOCH : projection.updatedAt());
  }

  private static AttentionItem active(String attentionId, AuditTraceSummaryTaskProjection projection, String severity, String title, String summary) {
    return new AttentionItem(attentionId, projection.tenantId(), projection.customerId(), FUNCTIONAL_AGENT_ID, projection.summaryTaskId(), title, severity, false, sourceRefs(projection), projection.traceIds(), safe(summary));
  }

  private static String workerEventType(AuditTraceSummaryTaskProjection.Status status) {
    return switch (status) {
      case QUEUED -> "worker.task.queued";
      case RUNNING -> "worker.task.running";
      case BLOCKED_PROVIDER_OR_RUNTIME -> "worker.task.blocked_provider_or_runtime";
      case FAILED -> "worker.task.failed";
      case COMPLETED_REVIEW_REQUIRED -> "worker.task.completed_review_required";
      case CANCELLED -> "worker.task.cancelled";
      case ACCEPTED -> "worker.task.accepted";
      case REJECTED_RESULT -> "worker.task.rejected_result";
    };
  }

  private static String workflowEventType(AuditTraceSummaryTaskProjection.Status status) {
    return switch (status) {
      case QUEUED, RUNNING -> STARTED;
      case BLOCKED_PROVIDER_OR_RUNTIME -> BLOCKED;
      case FAILED -> FAILED;
      case COMPLETED_REVIEW_REQUIRED -> COMPLETED;
      case CANCELLED -> CANCELLED;
      case ACCEPTED -> ACCEPTED;
      case REJECTED_RESULT -> REJECTED;
    };
  }

  private static List<String> progressActions(AuditTraceSummaryTaskProjection.Status status) {
    return switch (status) {
      case QUEUED, RUNNING -> List.of("action-audit-trace-summary-task-read", "action-audit-trace-summary-task-cancel-result", "action-audit-trace-summary-task-open-evidence");
      case BLOCKED_PROVIDER_OR_RUNTIME, FAILED -> List.of("action-audit-trace-summary-task-read", "action-audit-trace-summary-task-open-evidence");
      default -> List.of("action-audit-trace-summary-task-read", "action-audit-trace-summary-task-open-evidence");
    };
  }

  private static List<String> reviewActions(AuditTraceSummaryTaskProjection.Status status) {
    return switch (status) {
      case COMPLETED_REVIEW_REQUIRED, REJECTED_RESULT -> List.of("action-audit-trace-summary-task-accept-result", "action-audit-trace-summary-task-reject-result", "action-audit-trace-summary-task-open-evidence");
      default -> List.of("action-audit-trace-summary-task-read", "action-audit-trace-summary-task-open-evidence");
    };
  }

  private static String severityFor(AuditTraceSummaryResult result) {
    if (result == null || result.overallRisk() == null) return "warning";
    return result.overallRisk() == AuditTraceSummaryResult.OverallRisk.CRITICAL_REVIEW_REQUIRED ? "critical" : "warning";
  }

  private static String statusValue(AuditTraceSummaryTaskProjection.Status status) {
    return status.name().toLowerCase(Locale.ROOT);
  }

  private static String blockerReason(AuditTraceSummaryTaskProjection projection) {
    return projection.blockerCode() == null ? null : projection.blockerCode() + ": configure governed model provider, AuditTraceSummaryAutonomousAgent binding, ToolPermissionBoundary grants, readSkill/readReferenceDoc, and auditTraceSummaryEvidence.read.";
  }

  private static List<String> sourceRefs(AuditTraceSummaryTaskProjection projection) {
    var refs = new java.util.ArrayList<String>();
    refs.add("projection:" + projection.summaryTaskId());
    if (projection.autonomousAgentTaskId() != null && !projection.autonomousAgentTaskId().isBlank()) refs.add("autonomous_task:" + projection.autonomousAgentTaskId());
    refs.add("capability:" + AuditTraceSummaryTasks.START_CAPABILITY);
    refs.add("capability:" + AuditTraceSummaryTasks.READ_CAPABILITY);
    refs.add("governed-tool:audit.trace.summaryTask.start");
    refs.add("governed-tool:audit.trace.summaryTask.read");
    return List.copyOf(refs);
  }

  private static List<String> merge(List<String> current, List<String> next) {
    var merged = new java.util.LinkedHashSet<String>();
    if (current != null) merged.addAll(current);
    if (next != null) merged.addAll(next);
    return List.copyOf(merged);
  }

  private static String safe(String value) {
    if (value == null) return null;
    var safe = value.replaceAll("(?i)(api[_-]?key|secret|token|credential|bearer|jwt)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
    return safe.length() <= 500 ? safe : safe.substring(0, 500);
  }

  public record WorkstreamEventEnvelope(String eventId, String eventType, String eventFamily, String category, String tenantId, String customerId, String functionalAgentId, String summaryTaskId, List<String> sourceRefs, List<String> traceIds, String idempotencyKey, Instant occurredAt) {
    public WorkstreamEventEnvelope {
      sourceRefs = List.copyOf(sourceRefs == null ? List.of() : sourceRefs);
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }
  }

  public record AttentionItem(String attentionId, String tenantId, String customerId, String functionalAgentId, String summaryTaskId, String title, String severity, boolean resolved, List<String> sourceRefs, List<String> traceIds, String safeSummary) {
    public AttentionItem {
      sourceRefs = List.copyOf(sourceRefs == null ? List.of() : sourceRefs);
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }
  }

  public record SummaryProgressSurface(String surfaceContract, String summaryTaskId, String autonomousAgentTaskId, String status, int progressPercent, String progressSummary, String blockerReason, String tenantId, String customerId, String initiatingCapabilityId, List<String> evidenceRefs, List<String> sourceRefs, List<String> traceRefs, String redactionSummary, boolean noDirectMutation, List<String> actions) {
    public SummaryProgressSurface {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
      sourceRefs = List.copyOf(sourceRefs == null ? List.of() : sourceRefs);
      traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
      actions = List.copyOf(actions == null ? List.of() : actions);
    }
  }

  public record SummaryReviewSurface(String surfaceContract, String summaryTaskId, String autonomousAgentTaskId, String status, boolean noDirectMutation, boolean humanDecisionRequired, String overallRisk, String executiveSummary, List<AuditTraceSummaryResult.Finding> findings, List<AuditTraceSummaryResult.Finding> providerReadinessFindings, List<AuditTraceSummaryResult.Finding> authorizationDenialFindings, List<AuditTraceSummaryResult.Finding> agentWorkFindings, String omittedEvidenceSummary, String redactionSummary, List<String> sourceRefs, List<String> traceRefs, List<String> actions) {
    public SummaryReviewSurface {
      findings = List.copyOf(findings == null ? List.of() : findings);
      providerReadinessFindings = List.copyOf(providerReadinessFindings == null ? List.of() : providerReadinessFindings);
      authorizationDenialFindings = List.copyOf(authorizationDenialFindings == null ? List.of() : authorizationDenialFindings);
      agentWorkFindings = List.copyOf(agentWorkFindings == null ? List.of() : agentWorkFindings);
      sourceRefs = List.copyOf(sourceRefs == null ? List.of() : sourceRefs);
      traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
      actions = List.copyOf(actions == null ? List.of() : actions);
    }
  }
}

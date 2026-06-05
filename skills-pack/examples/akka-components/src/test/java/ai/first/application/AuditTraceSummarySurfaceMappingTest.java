package ai.first.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class AuditTraceSummarySurfaceMappingTest {
  @Test
  void completedProjectionPublishesWorkerWorkflowEventsReviewAttentionAndReviewSurface() {
    var projection = completedProjection();

    var events = AuditTraceSummarySurfaces.eventsFor(projection);
    assertEquals(List.of("worker.task.completed_review_required", "workflow.audit_trace.summary_completed_review_required"), events.stream().map(AuditTraceSummarySurfaces.WorkstreamEventEnvelope::eventType).toList());
    assertTrue(events.getFirst().sourceRefs().contains("projection:audit-summary-surfaces"));
    assertTrue(events.getFirst().sourceRefs().contains("autonomous_task:akka-task-1"));
    assertTrue(events.getFirst().sourceRefs().contains("capability:audit.trace.summary_task.start"));
    assertTrue(events.getFirst().sourceRefs().contains("governed-tool:audit.trace.summaryTask.read"));
    assertTrue(events.getFirst().traceIds().contains("trace-provider-blocked"));

    var attention = AuditTraceSummarySurfaces.attentionFor(projection).orElseThrow();
    assertEquals("attention:worker-task:audit-summary-surfaces:task-state", attention.attentionId());
    assertFalse(attention.resolved());
    assertEquals("warning", attention.severity());

    var review = AuditTraceSummarySurfaces.reviewSurface(projection);
    assertEquals("audit.trace.summaryReview.v1", review.surfaceContract());
    assertEquals("completed_review_required", review.status());
    assertTrue(review.noDirectMutation());
    assertTrue(review.humanDecisionRequired());
    assertEquals("review_required", review.overallRisk());
    assertEquals(1, review.findings().size());
    assertTrue(review.actions().contains("action-audit-trace-summary-task-accept-result"));
    assertFalse(review.toString().toLowerCase().contains("secret="));
  }

  @Test
  void blockedProjectionProducesFailClosedProgressSurfaceAndConfigurationAttention() {
    var blocked = new AuditTraceSummaryTaskProjection(
        "audit-summary-blocked-surface",
        null,
        "tenant-1",
        null,
        "membership-1",
        "admin-1",
        "idem-blocked",
        AuditTraceSummaryTaskProjection.Status.BLOCKED_PROVIDER_OR_RUNTIME,
        0,
        "Provider secret=abc failed closed; no fake success.",
        "blocked_provider_or_runtime",
        null,
        List.of("auditTraceSummaryEvidence.read"),
        List.of(),
        List.of("worker.task.blocked_provider_or_runtime", "workflow.audit_trace.summary_blocked_provider_or_runtime"),
        null,
        null,
        Instant.parse("2026-05-08T00:00:00Z"));

    var events = AuditTraceSummarySurfaces.eventsFor(blocked);
    assertEquals("worker.task.blocked_provider_or_runtime", events.getFirst().eventType());
    assertEquals("workflow.audit_trace.summary_blocked_provider_or_runtime", events.get(1).eventType());

    var attention = AuditTraceSummarySurfaces.attentionFor(blocked).orElseThrow();
    assertEquals("critical", attention.severity());
    assertFalse(attention.safeSummary().contains("secret=abc"));
    assertTrue(attention.safeSummary().contains("[REDACTED]"));

    var progress = AuditTraceSummarySurfaces.progressSurface(blocked);
    assertEquals("audit.trace.summaryProgress.v1", progress.surfaceContract());
    assertEquals("blocked_provider_or_runtime", progress.status());
    assertTrue(progress.blockerReason().contains("ToolPermissionBoundary"));
    assertTrue(progress.blockerReason().contains("auditTraceSummaryEvidence.read"));
    assertTrue(progress.redactionSummary().contains("not_found_or_redacted"));
    assertTrue(progress.noDirectMutation());
    assertFalse(progress.toString().toLowerCase().contains("secret=abc"));
  }

  @Test
  void acceptedAndCancelledResolveAttentionWithoutDirectMutation() {
    var accepted = AuditTraceSummarySurfaces.withDisposition(
        completedProjection(),
        AuditTraceSummaryTaskProjection.Status.ACCEPTED,
        "accepted",
        "Reviewer accepted advisory summary; protected audit records unchanged.",
        Instant.parse("2026-05-08T00:10:00Z"));

    var attention = AuditTraceSummarySurfaces.attentionFor(accepted).orElseThrow();
    assertTrue(attention.resolved());
    assertEquals("accepted", accepted.humanDisposition());
    assertTrue(accepted.traceIds().contains("worker.task.accepted"));
    assertTrue(accepted.traceIds().contains("workflow.audit_trace.summary_result_accepted"));
    assertNotNull(AuditTraceSummarySurfaces.progressSurface(accepted));

    var cancelled = AuditTraceSummarySurfaces.withDisposition(
        completedProjection(),
        AuditTraceSummaryTaskProjection.Status.CANCELLED,
        "cancelled",
        "Cancelled by authorized reviewer; no direct mutation.",
        Instant.parse("2026-05-08T00:10:00Z"));
    assertTrue(AuditTraceSummarySurfaces.attentionFor(cancelled).orElseThrow().resolved());
    assertTrue(cancelled.traceIds().contains("worker.task.cancelled"));
    assertTrue(cancelled.traceIds().contains("workflow.audit_trace.summary_cancelled"));
  }

  private static AuditTraceSummaryTaskProjection completedProjection() {
    var result = AuditTraceSummaryAutonomousAgentIntegrationTest.result("audit-summary-surfaces");
    return new AuditTraceSummaryTaskProjection(
        "audit-summary-surfaces",
        "akka-task-1",
        "tenant-1",
        null,
        "membership-1",
        "admin-1",
        "idem-surfaces",
        AuditTraceSummaryTaskProjection.Status.COMPLETED_REVIEW_REQUIRED,
        100,
        "Model-backed summary completed; human review required.",
        null,
        result,
        result.evidenceRefs(),
        List.of("audit_trace_summary_finding:finding-1:provider_readiness"),
        List.of("trace-provider-blocked", "worker.task.completed_review_required", "workflow.audit_trace.summary_completed_review_required"),
        null,
        null,
        Instant.parse("2026-05-08T00:00:00Z"));
  }
}

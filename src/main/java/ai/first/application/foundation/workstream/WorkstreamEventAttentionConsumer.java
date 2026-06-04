package ai.first.application.foundation.workstream;

import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.agentfoundation.PromptRiskReviewTask;
import ai.first.domain.security.AccessReviewTask;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.security.AuditTraceSummaryTask;
import ai.first.domain.security.GovernancePolicyImpactTask;
import ai.first.domain.foundation.attention.AttentionCategory;
import ai.first.domain.foundation.attention.AttentionItem;
import ai.first.domain.foundation.attention.AttentionItemStatus;
import ai.first.domain.foundation.attention.AttentionRedactionLevel;
import ai.first.domain.foundation.attention.AttentionSeverity;
import ai.first.domain.foundation.attention.AttentionSourceRef;
import ai.first.domain.foundation.attention.AttentionSurfaceRef;
import ai.first.domain.foundation.invitation.Invitation;
import ai.first.domain.security.MyAccountPersonalAttentionDigestTask;
import ai.first.domain.foundation.workstream.WorkstreamEventEnvelope;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import ai.first.application.foundation.attention.AttentionProducerService;
import ai.first.application.foundation.attention.AttentionRepository;
import ai.first.application.foundation.identity.IdentityRepository;

/**
 * Bounded governed consumer/projection path from workstream events into the shared attention backbone.
 *
 * <p>This starter service models the same rules an Akka Consumer should enforce: allow-listed event types, tenant/customer
 * scope checks, source refs, idempotency, redaction, and audit evidence. Events update projections only; they do not perform
 * protected domain mutations.</p>
 */
public final class WorkstreamEventAttentionConsumer {
  public static final String CONSUMER_ID = "workstream.event.consumer.attention";
  private static final String INVITATION_CAPABILITY = "secure-tenant-user-foundation";

  private final AttentionRepository attentionRepository;
  private final IdentityRepository identityRepository;
  private final AttentionProducerService attentionProducerService;
  private final Clock clock;

  public WorkstreamEventAttentionConsumer(AttentionRepository attentionRepository, IdentityRepository identityRepository, AttentionProducerService attentionProducerService, Clock clock) {
    this.attentionRepository = Objects.requireNonNull(attentionRepository);
    this.identityRepository = Objects.requireNonNull(identityRepository);
    this.attentionProducerService = Objects.requireNonNull(attentionProducerService);
    this.clock = Objects.requireNonNull(clock);
  }

  public AttentionItem project(WorkstreamEventEnvelope event, Invitation sourceInvitation) {
    if (!"domain".equals(event.eventFamily()) || !event.eventType().startsWith("invitation.delivery.")) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "unsupported-event-type");
      return null;
    }
    if (!event.tenantId().equals(sourceInvitation.tenantId()) || !Objects.equals(event.customerId(), sourceInvitation.customerId())) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "scope-mismatch");
      return null;
    }
    if (!event.capabilityRefs().contains("user_admin.invitation.delivery")) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "missing-capability-ref");
      return null;
    }
    if (alreadyProjected(event)) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DUPLICATE", AdminAuditEvent.Result.NO_OP, event, event.idempotencyKey());
      return currentItem(event);
    }
    return projectSupported(event, switch (event.eventType()) {
      case "invitation.delivery.failed" -> attentionProducerService.upsertInvitationDelivery(sourceInvitation, event.correlationId());
      case "invitation.delivery.sent" -> attentionProducerService.resolveInvitationDelivery(sourceInvitation, "sent", event.correlationId());
      case "invitation.delivery.captured" -> attentionProducerService.resolveInvitationDelivery(sourceInvitation, "captured", event.correlationId());
      default -> null;
    });
  }

  public AttentionItem project(WorkstreamEventEnvelope event, AccessReviewTask sourceTask) {
    var workflowEvent = "workflow/process".equals(event.eventFamily()) && event.eventType().startsWith("workflow.access_review.");
    var taskEvent = "task/worker".equals(event.eventFamily()) && event.eventType().startsWith("worker.task.");
    if (!workflowEvent && !taskEvent) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "unsupported-event-type");
      return null;
    }
    if (!event.tenantId().equals(sourceTask.tenantId()) || !Objects.equals(event.customerId(), sourceTask.customerId())) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "scope-mismatch");
      return null;
    }
    if (!event.capabilityRefs().stream().anyMatch(capability -> capability.startsWith("user_admin.access_review."))) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "missing-access-review-capability-ref");
      return null;
    }
    if (alreadyProjected(event)) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DUPLICATE", AdminAuditEvent.Result.NO_OP, event, event.idempotencyKey());
      return currentItem(event);
    }
    return projectSupported(event, switch (event.eventType()) {
      case "workflow.access_review.started", "workflow.access_review.blocked_provider_or_runtime", "workflow.access_review.completed_review_required", "workflow.access_review.result_rejected",
          "worker.task.queued", "worker.task.running", "worker.task.blocked_provider_or_runtime", "worker.task.failed", "worker.task.completed_review_required", "worker.task.rejected_result" -> attentionProducerService.upsertWorkerTaskState(sourceTask, null, event.correlationId());
      case "workflow.access_review.cancelled", "workflow.access_review.result_accepted", "worker.task.cancelled", "worker.task.accepted" -> attentionProducerService.resolveWorkerTaskState(sourceTask, sourceTask.status().name().toLowerCase(java.util.Locale.ROOT), event.correlationId());
      default -> null;
    });
  }

  public AttentionItem project(WorkstreamEventEnvelope event, PromptRiskReviewTask sourceTask) {
    var workflowEvent = "workflow/process".equals(event.eventFamily()) && event.eventType().startsWith("workflow.agent_admin.prompt_risk_review.");
    var taskEvent = "task/worker".equals(event.eventFamily()) && event.eventType().startsWith("worker.task.");
    if (!workflowEvent && !taskEvent) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "unsupported-event-type");
      return null;
    }
    if (!event.tenantId().equals(sourceTask.tenantId()) || !Objects.equals(event.customerId(), sourceTask.customerId())) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "scope-mismatch");
      return null;
    }
    if (!event.capabilityRefs().stream().anyMatch(capability -> capability.startsWith("agent_admin.prompt_risk_review."))) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "missing-prompt-risk-capability-ref");
      return null;
    }
    if (alreadyProjected(event)) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DUPLICATE", AdminAuditEvent.Result.NO_OP, event, event.idempotencyKey());
      return currentItem(event);
    }
    return projectSupported(event, switch (event.eventType()) {
      case "workflow.agent_admin.prompt_risk_review.started", "workflow.agent_admin.prompt_risk_review.blocked_provider_or_runtime", "workflow.agent_admin.prompt_risk_review.completed_review_required", "workflow.agent_admin.prompt_risk_review.result_rejected",
          "worker.task.queued", "worker.task.running", "worker.task.blocked_provider_or_runtime", "worker.task.failed", "worker.task.completed_review_required", "worker.task.rejected_result" -> attentionProducerService.upsertWorkerTaskState(sourceTask, null, event.correlationId());
      case "workflow.agent_admin.prompt_risk_review.cancelled", "workflow.agent_admin.prompt_risk_review.result_accepted", "worker.task.cancelled", "worker.task.accepted" -> attentionProducerService.resolveWorkerTaskState(sourceTask, sourceTask.status().name().toLowerCase(java.util.Locale.ROOT), event.correlationId());
      default -> null;
    });
  }

  public AttentionItem project(WorkstreamEventEnvelope event, GovernancePolicyImpactTask sourceTask) {
    var workflowEvent = "workflow/process".equals(event.eventFamily()) && event.eventType().startsWith("workflow.governance_policy.impact_analysis.");
    var taskEvent = "task/worker".equals(event.eventFamily()) && event.eventType().startsWith("worker.task.");
    if (!workflowEvent && !taskEvent) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "unsupported-event-type");
      return null;
    }
    if (!event.tenantId().equals(sourceTask.tenantId()) || !Objects.equals(event.customerId(), sourceTask.customerId())) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "scope-mismatch");
      return null;
    }
    if (!event.capabilityRefs().stream().anyMatch(capability -> capability.startsWith("governance.policy.impact_analysis."))) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "missing-governance-impact-capability-ref");
      return null;
    }
    if (alreadyProjected(event)) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DUPLICATE", AdminAuditEvent.Result.NO_OP, event, event.idempotencyKey());
      return currentItem(event);
    }
    return projectSupported(event, switch (event.eventType()) {
      case "workflow.governance_policy.impact_analysis.started", "workflow.governance_policy.impact_analysis.blocked_provider_or_runtime", "workflow.governance_policy.impact_analysis.failed", "workflow.governance_policy.impact_analysis.completed_review_required", "workflow.governance_policy.impact_analysis.result_rejected", "workflow.governance_policy.impact_analysis.request_changes",
          "worker.task.queued", "worker.task.running", "worker.task.blocked_provider_or_runtime", "worker.task.failed", "worker.task.completed_review_required", "worker.task.rejected_result" -> attentionProducerService.upsertWorkerTaskState(sourceTask, null, event.correlationId());
      case "workflow.governance_policy.impact_analysis.cancelled", "workflow.governance_policy.impact_analysis.result_accepted", "worker.task.cancelled", "worker.task.accepted" -> attentionProducerService.resolveWorkerTaskState(sourceTask, sourceTask.status().name().toLowerCase(java.util.Locale.ROOT), event.correlationId());
      default -> null;
    });
  }

  public AttentionItem project(WorkstreamEventEnvelope event, MyAccountPersonalAttentionDigestTask sourceTask) {
    var workflowEvent = "workflow/process".equals(event.eventFamily()) && event.eventType().startsWith("workflow.my_account.personal_attention_digest.");
    var taskEvent = "task/worker".equals(event.eventFamily()) && event.eventType().startsWith("worker.task.");
    if (!workflowEvent && !taskEvent) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "unsupported-event-type");
      return null;
    }
    if (!event.tenantId().equals(sourceTask.tenantId()) || !Objects.equals(event.customerId(), sourceTask.customerId())) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "scope-mismatch");
      return null;
    }
    if (!event.capabilityRefs().stream().anyMatch(capability -> capability.startsWith("my_account.personal_attention_digest."))) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "missing-my-account-digest-capability-ref");
      return null;
    }
    if (alreadyProjected(event)) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DUPLICATE", AdminAuditEvent.Result.NO_OP, event, event.idempotencyKey());
      return currentItem(event);
    }
    return projectSupported(event, switch (event.eventType()) {
      case "workflow.my_account.personal_attention_digest.started", "workflow.my_account.personal_attention_digest.blocked_provider_or_runtime", "workflow.my_account.personal_attention_digest.failed", "workflow.my_account.personal_attention_digest.completed_review_required", "workflow.my_account.personal_attention_digest.result_rejected",
          "worker.task.queued", "worker.task.running", "worker.task.blocked_provider_or_runtime", "worker.task.failed", "worker.task.completed_review_required", "worker.task.rejected_result" -> attentionProducerService.upsertWorkerTaskState(sourceTask, null, event.correlationId());
      case "workflow.my_account.personal_attention_digest.cancelled", "workflow.my_account.personal_attention_digest.result_accepted", "worker.task.cancelled", "worker.task.accepted" -> attentionProducerService.resolveWorkerTaskState(sourceTask, sourceTask.status().name().toLowerCase(java.util.Locale.ROOT), event.correlationId());
      default -> null;
    });
  }

  public AttentionItem projectGovernedLifecycle(WorkstreamEventEnvelope event) {
    var metadata = governedLifecycleMetadata(event);
    if (metadata == null) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "unsupported-event-type");
      return null;
    }
    if (event.payload().containsKey("tenantId") && !event.tenantId().equals(event.payload().get("tenantId"))) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "scope-mismatch");
      return null;
    }
    if (event.payload().containsKey("customerId") && !Objects.equals(event.customerId(), blankToNull(event.payload().get("customerId")))) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "scope-mismatch");
      return null;
    }
    if (event.capabilityRefs().stream().noneMatch(capability -> capability.startsWith(metadata.capabilityPrefix()))) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "missing-capability-ref");
      return null;
    }
    if (alreadyProjected(event)) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DUPLICATE", AdminAuditEvent.Result.NO_OP, event, event.idempotencyKey());
      return currentItem(event);
    }
    var itemId = event.projectionHints().getOrDefault("attentionItemId", metadata.attentionItemId(event));
    var now = Instant.now(clock);
    var status = "resolve".equals(event.projectionHints().get("attentionAction")) ? AttentionItemStatus.RESOLVED : AttentionItemStatus.OPEN;
    var resolvedAt = status == AttentionItemStatus.RESOLVED ? now : null;
    var item = new AttentionItem(
        itemId,
        event.tenantId(),
        event.customerId(),
        event.owningWorkstreamId(),
        metadata.title(event),
        metadata.summary(event),
        metadata.category(),
        metadata.severity(),
        status,
        AttentionItem.AssigneeKind.ROLE,
        metadata.assigneeRole(),
        metadata.requiredCapability(),
        new AttentionSurfaceRef(event.owningWorkstreamId(), event.targetSurfaceId(), metadata.surfaceType(), event.payload().getOrDefault("sourceId", event.eventId()), metadata.surfaceAction(), metadata.requiredCapability()),
        List.of(),
        AttentionRedactionLevel.FULL,
        event.occurredAt() == null ? now : event.occurredAt(),
        now,
        now,
        null,
        null,
        resolvedAt,
        null,
        event.correlationId());
    return projectSupported(event, item);
  }

  public AttentionItem project(WorkstreamEventEnvelope event, AuditTraceSummaryTask sourceTask) {
    var workflowEvent = "workflow/process".equals(event.eventFamily()) && event.eventType().startsWith("workflow.audit_trace.summary_");
    var taskEvent = "task/worker".equals(event.eventFamily()) && event.eventType().startsWith("worker.task.");
    if (!workflowEvent && !taskEvent) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "unsupported-event-type");
      return null;
    }
    if (!event.tenantId().equals(sourceTask.tenantId()) || !Objects.equals(event.customerId(), sourceTask.customerId())) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "scope-mismatch");
      return null;
    }
    if (!event.capabilityRefs().stream().anyMatch(capability -> capability.startsWith("audit.trace.summary_task."))) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DENIED", AdminAuditEvent.Result.DENIED, event, "missing-audit-summary-capability-ref");
      return null;
    }
    if (alreadyProjected(event)) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_DUPLICATE", AdminAuditEvent.Result.NO_OP, event, event.idempotencyKey());
      return currentItem(event);
    }
    return projectSupported(event, switch (event.eventType()) {
      case "workflow.audit_trace.summary_started", "workflow.audit_trace.summary_blocked_provider_or_runtime", "workflow.audit_trace.summary_failed", "workflow.audit_trace.summary_completed_review_required", "workflow.audit_trace.summary_result_rejected",
          "worker.task.queued", "worker.task.running", "worker.task.blocked_provider_or_runtime", "worker.task.failed", "worker.task.completed_review_required", "worker.task.rejected_result" -> attentionProducerService.upsertWorkerTaskState(sourceTask, null, event.correlationId());
      case "workflow.audit_trace.summary_cancelled", "workflow.audit_trace.summary_result_accepted", "worker.task.cancelled", "worker.task.accepted" -> attentionProducerService.resolveWorkerTaskState(sourceTask, sourceTask.status().name().toLowerCase(java.util.Locale.ROOT), event.correlationId());
      default -> null;
    });
  }

  private AttentionItem projectSupported(WorkstreamEventEnvelope event, AttentionItem projected) {
    if (projected == null) {
      appendAudit("WORKSTREAM_EVENT_CONSUMER_NO_OP", AdminAuditEvent.Result.NO_OP, event, "no-supported-projection");
      return null;
    }
    var withEvidence = withEventEvidence(projected, event);
    attentionRepository.save(withEvidence);
    appendAudit("WORKSTREAM_EVENT_CONSUMER_PROJECT", AdminAuditEvent.Result.ALLOWED, event, CONSUMER_ID + ":" + withEvidence.itemId());
    return withEvidence;
  }

  private boolean alreadyProjected(WorkstreamEventEnvelope event) {
    var current = currentItem(event);
    return current != null && current.sourceRefs().stream().anyMatch(ref -> "workstream_event".equals(ref.kind()) && event.eventId().equals(ref.refId()));
  }

  private AttentionItem currentItem(WorkstreamEventEnvelope event) {
    if (event.payload().containsKey("invitationId")) {
      return attentionRepository.find(event.tenantId(), invitationDeliveryItemId(event.payload().get("invitationId"))).orElse(null);
    }
    if (event.payload().containsKey("taskId")) {
      return attentionRepository.find(event.tenantId(), workerTaskItemId(event.payload().get("taskId"))).orElse(null);
    }
    if (event.payload().containsKey("impactTaskId")) {
      return attentionRepository.find(event.tenantId(), workerTaskItemId(event.payload().get("impactTaskId"))).orElse(null);
    }
    if (event.payload().containsKey("digestTaskId")) {
      return attentionRepository.find(event.tenantId(), workerTaskItemId(event.payload().get("digestTaskId"))).orElse(null);
    }
    if (event.projectionHints().containsKey("attentionItemId")) {
      return attentionRepository.find(event.tenantId(), event.projectionHints().get("attentionItemId")).orElse(null);
    }
    if (event.payload().containsKey("sourceId")) {
      return attentionRepository.find(event.tenantId(), genericLifecycleItemId(event.eventFamily(), event.payload().get("sourceId"))).orElse(null);
    }
    return null;
  }

  private AttentionItem withEventEvidence(AttentionItem item, WorkstreamEventEnvelope event) {
    var refs = new ArrayList<>(item.sourceRefs());
    var capability = event.capabilityRefs().contains(INVITATION_CAPABILITY) ? INVITATION_CAPABILITY : event.capabilityRefs().get(0);
    refs.add(new AttentionSourceRef("workstream_event", event.eventId(), event.eventType() + " envelope", capability, firstTraceRef(event), event.correlationId()));
    refs.add(new AttentionSourceRef("idempotency", event.idempotencyKey(), "Event projection idempotency key", capability, "trace-idempotency-" + stableSuffix(event.idempotencyKey()), event.correlationId()));
    var now = Instant.now(clock);
    return new AttentionItem(item.itemId(), item.tenantId(), item.customerId(), item.owningWorkstreamId(), item.title(), item.summary(), item.category(), item.severity(), item.status(), item.assigneeKind(), item.assigneeId(), item.requiredCapabilityId(), item.surfaceRef(), List.copyOf(refs), item.redactionLevel(), item.createdAt(), now, now, item.expiresAt(), item.acknowledgedAt(), item.resolvedAt(), item.dismissedAt(), event.correlationId());
  }

  private void appendAudit(String action, AdminAuditEvent.Result result, WorkstreamEventEnvelope event, String reason) {
    identityRepository.appendAudit(new AdminAuditEvent(
        UUID.randomUUID().toString(),
        Instant.now(clock),
        event.correlationId(),
        "system",
        null,
        null,
        event.tenantId(),
        event.customerId(),
        null,
        null,
        action,
        result,
        reason,
        reason,
        "BROWSER_SAFE"));
  }

  private static String firstTraceRef(WorkstreamEventEnvelope event) {
    return event.traceRefs().isEmpty() ? "trace-event-" + stableSuffix(event.eventId()) : event.traceRefs().get(0);
  }

  private static String invitationDeliveryItemId(String invitationId) {
    return "attention:user-admin:invitation-delivery:" + invitationId;
  }

  private static String workerTaskItemId(String taskId) {
    return "attention:worker-task:" + taskId + ":task-state";
  }

  private static GovernedLifecycleMetadata governedLifecycleMetadata(WorkstreamEventEnvelope event) {
    var type = event.eventType();
    if (type.startsWith("membership.role.") || type.startsWith("membership.status.")) return new GovernedLifecycleMetadata("secure-tenant-user-foundation", AttentionCategory.SECURITY_REVIEW, AttentionSeverity.WARNING, "TENANT_ADMIN", "surface-user-admin-list", "list-search", "open_membership", "Membership lifecycle requires review", "Membership or role state changed through a backend-governed event.");
    if (type.startsWith("support_access.")) return new GovernedLifecycleMetadata("tenant.support_access.", AttentionCategory.SECURITY_REVIEW, AttentionSeverity.URGENT, "TENANT_ADMIN", "surface-user-admin-detail-admin", "detail-edit", "open_support_access", "Support access lifecycle requires review", "Support access changed; review expiry, scope, and audit evidence.");
    if (type.startsWith("governed_artifact.")) return new GovernedLifecycleMetadata("agent_admin.", AttentionCategory.SECURITY_REVIEW, AttentionSeverity.URGENT, "TENANT_ADMIN", "surface-agent-admin-detail", "dashboard", "open_governed_artifact", "Governed artifact lifecycle requires review", "Prompt, skill, reference, manifest, model, or tool-boundary artifact lifecycle changed without exposing raw content.");
    if (type.startsWith("policy.simulation.")) return new GovernedLifecycleMetadata("governance.policy.simulate", AttentionCategory.GOVERNANCE_APPROVAL, AttentionSeverity.WARNING, "TENANT_ADMIN", "surface-governance-policy-dashboard", "dashboard", "open_policy_simulation", "Policy simulation result needs review", "Policy simulation evidence is available; activation still requires explicit backend approval.");
    if (type.startsWith("export.")) return new GovernedLifecycleMetadata("audit.trace.export", AttentionCategory.AUDIT_FAILURE_EVIDENCE, AttentionSeverity.WARNING, "AUDITOR", "surface-audit-trace-dashboard", "dashboard", "open_export", "Export lifecycle requires audit review", "Audit/export lifecycle changed; review redaction, approval, and delivery status.");
    if (type.startsWith("notification.lifecycle.")) return new GovernedLifecycleMetadata("notification.", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.INFO, "TENANT_ADMIN", "surface-my-account-notification-center", "list-search", "open_notification", "Notification lifecycle changed", "Notification delivery or preference lifecycle changed through backend-owned state.");
    return null;
  }

  private static String genericLifecycleItemId(String eventFamily, String sourceId) {
    return "attention:workstream-event:" + stableSuffix(eventFamily) + ":" + stableSuffix(sourceId);
  }

  private static String blankToNull(String value) {
    return value == null || value.isBlank() ? null : value;
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "workstream-event").hashCode(), 36);
  }

  private record GovernedLifecycleMetadata(String capabilityPrefix, AttentionCategory category, AttentionSeverity severity, String assigneeRole, String defaultSurfaceId, String surfaceType, String surfaceAction, String defaultTitle, String defaultSummary) {
    String requiredCapability() {
      return capabilityPrefix.endsWith(".") ? capabilityPrefix.substring(0, capabilityPrefix.length() - 1) : capabilityPrefix;
    }

    String attentionItemId(WorkstreamEventEnvelope event) {
      return genericLifecycleItemId(event.eventFamily(), event.payload().getOrDefault("sourceId", event.eventId()));
    }

    String title(WorkstreamEventEnvelope event) {
      return event.payload().getOrDefault("safeTitle", defaultTitle);
    }

    String summary(WorkstreamEventEnvelope event) {
      return event.payload().getOrDefault("safeSummary", defaultSummary);
    }
  }
}

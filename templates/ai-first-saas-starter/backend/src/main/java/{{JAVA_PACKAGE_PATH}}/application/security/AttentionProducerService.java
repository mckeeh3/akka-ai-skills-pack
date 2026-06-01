package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.AccessReviewTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionCategory;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionItemStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionSeverity;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionSourceRef;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionSurfaceRef;
import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicyProposal;
import {{JAVA_BASE_PACKAGE}}.domain.security.Invitation;
import {{JAVA_BASE_PACKAGE}}.domain.security.InvitationStatus;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Producer-oriented boundary that maps real starter service state into the shared attention backbone. */
public final class AttentionProducerService {
  public static final String INVITATION_DELIVERY_PRODUCER_ID = "attention.producer.user_admin.invitation_delivery";
  public static final String GOVERNANCE_POLICY_APPROVAL_PRODUCER_ID = "attention.producer.governance.policy_approval";
  public static final String WORKER_TASK_STATE_PRODUCER_ID = "attention.producer.worker.task_state";

  private final AttentionRepository attentionRepository;
  private final IdentityRepository identityRepository;
  private final Clock clock;

  public AttentionProducerService(AttentionRepository attentionRepository, IdentityRepository identityRepository, Clock clock) {
    this.attentionRepository = Objects.requireNonNull(attentionRepository);
    this.identityRepository = Objects.requireNonNull(identityRepository);
    this.clock = Objects.requireNonNull(clock);
  }

  public AttentionItem upsertInvitationDelivery(Invitation invitation, String correlationId) {
    var itemId = invitationDeliveryItemId(invitation.invitationId());
    var title = "User Admin invitation delivery needs review";
    var attempts = Math.max(invitation.deliveryAttempts(), 1);
    var severity = attempts > 1 ? AttentionSeverity.URGENT : AttentionSeverity.WARNING;
    var summary = attempts + " delivery attempt(s) for " + invitation.normalizedEmail() + " need authorized review: " + safe(invitation.lastDeliveryErrorSummary(), "delivery failed") + ".";
    return upsert(item(
        itemId,
        invitation.tenantId(),
        invitation.customerId(),
        "agent-user-admin",
        title,
        summary,
        AttentionCategory.INVITATION_DELIVERY,
        severity,
        "secure-tenant-user-foundation",
        "surface-user-admin-invitation-panel",
        invitation.invitationId(),
        INVITATION_DELIVERY_PRODUCER_ID,
        "domain_event",
        "Invitation delivery failure for " + invitation.normalizedEmail(),
        invitation.expiresAt(),
        correlationId),
        INVITATION_DELIVERY_PRODUCER_ID,
        correlationId);
  }

  public AttentionItem resolveInvitationDelivery(Invitation invitation, String reason, String correlationId) {
    return resolve(invitation.tenantId(), invitationDeliveryItemId(invitation.invitationId()), INVITATION_DELIVERY_PRODUCER_ID, safe(reason, "source-cleared"), correlationId);
  }

  public AttentionItem upsertGovernanceApproval(GovernancePolicyProposal proposal, String correlationId) {
    var itemId = governanceApprovalItemId(proposal.proposalId());
    var title = "Governance policy decision awaits authorized review";
    var summary = "Governance/Policy proposal " + proposal.proposalId() + " is in review and requires " + proposal.requiredApprovalCapabilityId() + ".";
    return upsert(item(
        itemId,
        proposal.tenantId(),
        proposal.customerId(),
        "agent-governance-policy",
        title,
        summary,
        AttentionCategory.GOVERNANCE_APPROVAL,
        AttentionSeverity.URGENT,
        "governance.policy.read",
        "surface-governance-policy-dashboard",
        proposal.proposalId(),
        GOVERNANCE_POLICY_APPROVAL_PRODUCER_ID,
        "domain_event",
        "Governance proposal awaiting review",
        null,
        correlationId),
        GOVERNANCE_POLICY_APPROVAL_PRODUCER_ID,
        correlationId);
  }

  public AttentionItem resolveGovernanceApproval(GovernancePolicyProposal proposal, String reason, String correlationId) {
    return resolve(proposal.tenantId(), governanceApprovalItemId(proposal.proposalId()), GOVERNANCE_POLICY_APPROVAL_PRODUCER_ID, safe(reason, "source-cleared"), correlationId);
  }

  public List<AttentionItem> runInvitationDeliveryTimedCheck(InvitationRepository invitations, Duration nearExpiryWindow, String timerId, String correlationId) {
    var now = Instant.now(clock);
    var window = nearExpiryWindow == null ? Duration.ofHours(24) : nearExpiryWindow;
    return invitations.invitations().stream()
        .filter(invitation -> invitation.status() == InvitationStatus.DELIVERY_FAILED)
        .filter(invitation -> !invitation.terminal())
        .filter(invitation -> !invitation.expiresAt().isAfter(now.plus(window)))
        .map(invitation -> invitation.expiresAt().isAfter(now)
            ? upsertTimedInvitationDelivery(invitation, timerId, correlationId)
            : expire(invitation.tenantId(), invitationDeliveryItemId(invitation.invitationId()), INVITATION_DELIVERY_PRODUCER_ID, "timer-expired:" + safe(timerId, "invitation-delivery-expiry"), correlationId))
        .toList();
  }

  public AttentionItem upsertWorkerTaskState(AccessReviewTask task, String timerId, String correlationId) {
    if (task.status() == AccessReviewTask.Status.CANCELLED || task.status() == AccessReviewTask.Status.ACCEPTED) {
      return resolveWorkerTaskState(task, task.status().name().toLowerCase(), correlationId);
    }
    var attention = switch (task.status()) {
      case BLOCKED_PROVIDER_OR_RUNTIME -> workerTaskItem(task, AttentionSeverity.BLOCKED, AttentionCategory.WORKFLOW_BLOCKED,
          "User Admin access-review worker is blocked by provider/runtime readiness",
          "Access-review task " + task.taskId() + " is blocked_provider_or_runtime; the starter fails closed instead of returning model-less worker success.", timerId, correlationId);
      case REJECTED -> workerTaskItem(task, AttentionSeverity.WARNING, AttentionCategory.AGENT_TASK_FAILED,
          "User Admin access-review result was rejected",
          "Access-review task " + task.taskId() + " was rejected and needs authorized follow-up.", timerId, correlationId);
      case COMPLETED -> workerTaskItem(task, AttentionSeverity.URGENT, AttentionCategory.ACCESS_REVIEW,
          "User Admin access-review result awaits human review",
          "Access-review task " + task.taskId() + " completed through the governed runtime and requires human accept/reject review.", timerId, correlationId);
      case RUNNING, QUEUED -> workerTaskItem(task, AttentionSeverity.WARNING, AttentionCategory.WORKFLOW_BLOCKED,
          "User Admin access-review task is waiting for worker progress",
          "Access-review task " + task.taskId() + " is " + task.status().name().toLowerCase() + " and may need worker/runtime attention if it remains stale.", timerId, correlationId);
      case CANCELLED, ACCEPTED -> null;
    };
    return attention == null ? null : upsert(attention, WORKER_TASK_STATE_PRODUCER_ID, correlationId);
  }

  public AttentionItem resolveWorkerTaskState(AccessReviewTask task, String reason, String correlationId) {
    return resolve(task.tenantId(), workerTaskItemId(task.taskId()), WORKER_TASK_STATE_PRODUCER_ID, safe(reason, "source-cleared"), correlationId);
  }

  private AttentionItem upsertTimedInvitationDelivery(Invitation invitation, String timerId, String correlationId) {
    var item = upsertInvitationDelivery(invitation, correlationId);
    var refs = new java.util.ArrayList<>(item.sourceRefs());
    refs.add(new AttentionSourceRef("timer", safe(timerId, "invitation-delivery-expiry"), "Timed check: invitation delivery failure is near expiry", "secure-tenant-user-foundation", "trace-timer-" + stableSuffix(safe(timerId, "invitation-delivery-expiry") + invitation.invitationId()), correlationId));
    var updated = new AttentionItem(item.itemId(), item.tenantId(), item.customerId(), item.owningWorkstreamId(), item.title(), item.summary() + " Timed check shows the invitation expires at " + invitation.expiresAt() + ".", item.category(), AttentionSeverity.URGENT, item.status(), item.assigneeKind(), item.assigneeId(), item.requiredCapabilityId(), item.surfaceRef(), refs, item.redactionLevel(), item.createdAt(), Instant.now(clock), Instant.now(clock), item.expiresAt(), item.acknowledgedAt(), item.resolvedAt(), item.dismissedAt(), correlationId);
    attentionRepository.save(updated);
    appendSystemAudit("ATTENTION_PRODUCER_TIMED_CHECK", AdminAuditEvent.Result.ALLOWED, updated.tenantId(), updated.customerId(), INVITATION_DELIVERY_PRODUCER_ID + ":" + updated.itemId() + ":" + safe(timerId, "invitation-delivery-expiry"), correlationId);
    return updated;
  }

  private AttentionItem workerTaskItem(AccessReviewTask task, AttentionSeverity severity, AttentionCategory category, String title, String summary, String timerId, String correlationId) {
    return item(
        workerTaskItemId(task.taskId()),
        task.tenantId(),
        task.customerId(),
        "agent-user-admin",
        title,
        summary + " Evidence is limited to durable task state, governed tool refs, and work traces; no fake model-backed success is introduced.",
        category,
        severity,
        "secure-tenant-user-foundation",
        "surface-user-admin-access-review-task",
        task.taskId(),
        WORKER_TASK_STATE_PRODUCER_ID,
        timerId == null || timerId.isBlank() ? "autonomous_task" : "timer",
        "Access-review task state " + task.status().name().toLowerCase(),
        null,
        correlationId);
  }

  private AttentionItem upsert(AttentionItem next, String producerId, String correlationId) {
    var existing = attentionRepository.find(next.tenantId(), next.itemId()).orElse(null);
    var now = Instant.now(clock);
    var normalized = new AttentionItem(
        next.itemId(), next.tenantId(), next.customerId(), next.owningWorkstreamId(), next.title(), next.summary(), next.category(),
        next.severity(), AttentionItemStatus.OPEN, next.assigneeKind(), next.assigneeId(), next.requiredCapabilityId(), next.surfaceRef(),
        next.sourceRefs(), next.redactionLevel(), existing == null ? now : existing.createdAt(), now, now, next.expiresAt(), null, null, null,
        correlationId);
    var saved = attentionRepository.upsert(normalized);
    appendSystemAudit("ATTENTION_PRODUCER_UPSERT", existing == null ? AdminAuditEvent.Result.ALLOWED : (existing.equals(saved) ? AdminAuditEvent.Result.NO_OP : AdminAuditEvent.Result.ALLOWED), saved.tenantId(), saved.customerId(), producerId + ":" + saved.itemId(), correlationId);
    return saved;
  }

  private AttentionItem resolve(String tenantId, String itemId, String producerId, String reason, String correlationId) {
    var current = attentionRepository.find(tenantId, itemId).orElse(null);
    if (current == null) {
      appendSystemAudit("ATTENTION_PRODUCER_RESOLVE", AdminAuditEvent.Result.NO_OP, tenantId, null, producerId + ":missing:" + reason, correlationId);
      return null;
    }
    var resolved = current.resolve(Instant.now(clock), correlationId);
    var noOp = resolved.equals(current);
    if (!noOp) attentionRepository.save(resolved);
    appendSystemAudit("ATTENTION_PRODUCER_RESOLVE", noOp ? AdminAuditEvent.Result.NO_OP : AdminAuditEvent.Result.ALLOWED, current.tenantId(), current.customerId(), producerId + ":" + itemId + ":" + reason, correlationId);
    return noOp ? current : resolved;
  }

  private AttentionItem expire(String tenantId, String itemId, String producerId, String reason, String correlationId) {
    var current = attentionRepository.find(tenantId, itemId).orElse(null);
    if (current == null) {
      appendSystemAudit("ATTENTION_PRODUCER_EXPIRE", AdminAuditEvent.Result.NO_OP, tenantId, null, producerId + ":missing:" + reason, correlationId);
      return null;
    }
    var expired = current.expire(Instant.now(clock), correlationId);
    var noOp = expired.equals(current);
    if (!noOp) attentionRepository.save(expired);
    appendSystemAudit("ATTENTION_PRODUCER_EXPIRE", noOp ? AdminAuditEvent.Result.NO_OP : AdminAuditEvent.Result.ALLOWED, current.tenantId(), current.customerId(), producerId + ":" + itemId + ":" + reason, correlationId);
    return noOp ? current : expired;
  }

  private AttentionItem item(
      String itemId,
      String tenantId,
      String customerId,
      String workstreamId,
      String title,
      String summary,
      AttentionCategory category,
      AttentionSeverity severity,
      String capabilityId,
      String surfaceId,
      String sourceStateId,
      String producerId,
      String sourceKind,
      String sourceLabel,
      Instant expiresAt,
      String correlationId) {
    var now = Instant.now(clock);
    var idempotencyKey = "attention-producer:" + producerId + ":" + tenantId + ":" + safe(customerId, "none") + ":" + sourceStateId + ":" + category.name().toLowerCase();
    return new AttentionItem(
        itemId,
        tenantId,
        customerId,
        workstreamId,
        title,
        summary,
        category,
        severity,
        AttentionItemStatus.OPEN,
        AttentionItem.AssigneeKind.CAPABILITY,
        capabilityId,
        capabilityId,
        new AttentionSurfaceRef(workstreamId, surfaceId, "dashboard", sourceStateId, AttentionService.OPEN_ATTENTION_ITEM_TOOL, capabilityId),
        List.of(
            new AttentionSourceRef(sourceKind, sourceStateId, sourceLabel, capabilityId, "trace-" + stableSuffix(idempotencyKey), correlationId),
            new AttentionSourceRef("capability", capabilityId, "Required capability " + capabilityId, capabilityId, "trace-capability-" + stableSuffix(capabilityId), correlationId)),
        null,
        now,
        now,
        now,
        expiresAt,
        null,
        null,
        null,
        correlationId);
  }

  private void appendSystemAudit(String action, AdminAuditEvent.Result result, String tenantId, String customerId, String reason, String correlationId) {
    identityRepository.appendAudit(new AdminAuditEvent(
        UUID.randomUUID().toString(),
        Instant.now(clock),
        correlationId,
        "system",
        null,
        null,
        tenantId,
        customerId,
        null,
        null,
        action,
        result,
        reason,
        reason,
        "BROWSER_SAFE"));
  }

  private static String invitationDeliveryItemId(String invitationId) {
    return "attention:user-admin:invitation-delivery:" + invitationId;
  }

  private static String governanceApprovalItemId(String proposalId) {
    return "attention:governance:policy-approval:" + proposalId;
  }

  private static String workerTaskItemId(String taskId) {
    return "attention:worker-task:" + taskId + ":task-state";
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "attention-producer").hashCode(), 36);
  }

  private static String safe(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value.replaceAll("(?i)(api[_-]?key|secret|token)=[^\\s,;]+", "$1=[REDACTED]");
  }
}

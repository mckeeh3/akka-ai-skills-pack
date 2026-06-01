package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.AccessReviewTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.Invitation;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkstreamEventEnvelope;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkstreamEventSourceRef;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Publishes selected starter domain transitions into the governed workstream event backbone. */
public final class WorkstreamEventPublisher {
  public static final String EVENT_FAMILY_DOMAIN = "domain";
  public static final String EVENT_FAMILY_WORKFLOW_PROCESS = "workflow/process";
  public static final String PAYLOAD_INVITATION_DELIVERY = "InvitationDeliveryEventPayload";
  public static final String PAYLOAD_ACCESS_REVIEW_LIFECYCLE = "AccessReviewLifecycleEventPayload";

  private final WorkstreamEventRepository repository;
  private final WorkstreamEventAttentionConsumer attentionConsumer;
  private final Clock clock;

  public WorkstreamEventPublisher(WorkstreamEventRepository repository, WorkstreamEventAttentionConsumer attentionConsumer, Clock clock) {
    this.repository = Objects.requireNonNull(repository);
    this.attentionConsumer = Objects.requireNonNull(attentionConsumer);
    this.clock = Objects.requireNonNull(clock);
  }

  public WorkstreamEventEnvelope publishInvitationDelivery(Invitation invitation, boolean delivered, String deliveryAttemptId, String deliveryStatus, String safeErrorSummary, String correlationId) {
    var semanticTransition = delivered ? "sent" : "failed";
    var idempotencyKey = idempotencyKey(EVENT_FAMILY_DOMAIN, "invitation.delivery." + semanticTransition, invitation.tenantId(), invitation.customerId(), invitation.invitationId(), semanticTransition);
    var existing = repository.findByIdempotencyKey(invitation.tenantId(), idempotencyKey).orElse(null);
    var event = existing == null
        ? repository.publish(invitationDeliveryEnvelope(invitation, semanticTransition, deliveryAttemptId, safeErrorSummary, idempotencyKey, correlationId))
        : existing;
    attentionConsumer.project(event, invitation);
    return event;
  }

  public WorkstreamEventEnvelope publishAccessReviewLifecycle(AccessReviewTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    var eventType = "workflow.access_review." + semanticTransition;
    var idempotencyKey = idempotencyKey(EVENT_FAMILY_WORKFLOW_PROCESS, eventType, task.tenantId(), task.customerId(), task.taskId(), semanticTransition);
    var existing = repository.findByIdempotencyKey(task.tenantId(), idempotencyKey).orElse(null);
    var event = existing == null
        ? repository.publish(accessReviewLifecycleEnvelope(task, semanticTransition, capabilityId, actorAccountId, idempotencyKey, correlationId))
        : existing;
    attentionConsumer.project(event, task);
    return event;
  }

  private WorkstreamEventEnvelope invitationDeliveryEnvelope(Invitation invitation, String semanticTransition, String deliveryAttemptId, String safeErrorSummary, String idempotencyKey, String correlationId) {
    var eventType = "invitation.delivery." + semanticTransition;
    var eventId = "evt-" + stableSuffix(idempotencyKey);
    var traceId = "trace-" + stableSuffix(eventId + ":" + correlationId);
    var now = Instant.now(clock);
    var capabilityId = "user_admin.invitation.delivery";
    return new WorkstreamEventEnvelope(
        eventId,
        eventType,
        EVENT_FAMILY_DOMAIN,
        1,
        now,
        now,
        invitation.tenantId(),
        invitation.customerId(),
        Map.of(
            "scopeType", invitation.scopeType().name(),
            "tenantId", invitation.tenantId(),
            "customerId", safe(invitation.customerId(), ""),
            "capabilityIds", capabilityId + ",secure-tenant-user-foundation"),
        Map.of("actorType", "provider", "accountId", "system", "label", "Invitation delivery provider"),
        List.of(
            new WorkstreamEventSourceRef("domain_event", invitation.invitationId(), "Invitation delivery " + semanticTransition + " for " + invitation.normalizedEmail(), capabilityId, traceId, correlationId),
            new WorkstreamEventSourceRef("capability", capabilityId, "Invitation delivery capability", capabilityId, "trace-capability-" + stableSuffix(capabilityId), correlationId)),
        List.of(capabilityId, "secure-tenant-user-foundation"),
        correlationId,
        idempotencyKey,
        deliveryAttemptId,
        List.of(traceId),
        "agent-user-admin",
        "surface-user-admin-invitation-panel",
        PAYLOAD_INVITATION_DELIVERY,
        Map.of(
            "invitationId", invitation.invitationId(),
            "deliveryAttemptId", safe(deliveryAttemptId, ""),
            "deliveryStatus", semanticTransition,
            "providerDeliveryStatus", safe(invitation.deliveryStatus().name().toLowerCase(java.util.Locale.ROOT), ""),
            "normalizedEmail", invitation.normalizedEmail(),
            "attempts", Integer.toString(invitation.deliveryAttempts()),
            "safeErrorSummary", redact(safeErrorSummary)),
        Map.of("browserSafe", "true", "omitted", "rawToken,tokenHash,providerSecret", "minimumRedactionLevel", "FULL"),
        Map.of("attentionCategory", "INVITATION_DELIVERY", "attentionItemId", "attention:user-admin:invitation-delivery:" + invitation.invitationId()));
  }

  private WorkstreamEventEnvelope accessReviewLifecycleEnvelope(AccessReviewTask task, String semanticTransition, String capabilityId, String actorAccountId, String idempotencyKey, String correlationId) {
    var eventType = "workflow.access_review." + semanticTransition;
    var eventId = "evt-" + stableSuffix(idempotencyKey);
    var traceId = firstTraceRef(task, "trace-" + stableSuffix(eventId + ":" + correlationId));
    var now = Instant.now(clock);
    var safeCapability = safe(capabilityId, UserAdminAccessReviewService.READ_CAPABILITY);
    return new WorkstreamEventEnvelope(
        eventId,
        eventType,
        EVENT_FAMILY_WORKFLOW_PROCESS,
        1,
        task.updatedAt() == null ? now : task.updatedAt(),
        now,
        task.tenantId(),
        task.customerId(),
        Map.of(
            "scopeType", task.scopeType().name(),
            "tenantId", task.tenantId(),
            "customerId", safe(task.customerId(), ""),
            "capabilityIds", safeCapability + ",secure-tenant-user-foundation"),
        Map.of("actorType", actorAccountId == null || actorAccountId.isBlank() ? "worker" : "account", "accountId", safe(actorAccountId, "system"), "label", "User Admin access-review lifecycle"),
        List.of(
            new WorkstreamEventSourceRef("workflow", task.taskId(), "Access-review workflow " + semanticTransition, safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("autonomous_task", task.taskId(), "Access-review task state " + task.status().name().toLowerCase(java.util.Locale.ROOT), safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("capability", safeCapability, "Access-review lifecycle capability", safeCapability, "trace-capability-" + stableSuffix(safeCapability), correlationId)),
        List.of(safeCapability, "secure-tenant-user-foundation"),
        correlationId,
        idempotencyKey,
        task.idempotencyKey(),
        List.of(traceId),
        "agent-user-admin",
        "surface-user-admin-access-review",
        PAYLOAD_ACCESS_REVIEW_LIFECYCLE,
        Map.of(
            "taskId", task.taskId(),
            "status", task.status().name().toLowerCase(java.util.Locale.ROOT),
            "semanticTransition", semanticTransition,
            "progressPercent", Integer.toString(task.progressPercent()),
            "blockerCode", safe(task.blockerCode(), ""),
            "decision", safe(task.decision(), ""),
            "safeSummary", redact(safe(task.summary(), "")),
            "providerOrRuntimeState", task.status() == AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime:fail_closed" : "state_recorded_without_direct_access_mutation"),
        Map.of("browserSafe", "true", "omitted", "rawPrompt,rawToolPayload,providerSecret,providerCredential", "minimumRedactionLevel", "FULL"),
        Map.of("attentionCategory", "WORKFLOW_BLOCKED", "attentionItemId", "attention:worker-task:" + task.taskId() + ":task-state"));
  }

  private static String idempotencyKey(String eventFamily, String eventType, String tenantId, String customerId, String sourceRefId, String semanticTransition) {
    return "workstream-event:" + eventFamily + ":" + eventType + ":" + tenantId + ":" + safe(customerId, "none") + ":" + sourceRefId + ":" + semanticTransition;
  }

  private static String redact(String value) {
    return safe(value, "").replaceAll("(?i)(api[_-]?key|secret|token|providerCredential)=[^\\s,;]+", "$1=[REDACTED]");
  }

  private static String firstTraceRef(AccessReviewTask task, String fallback) {
    return task.traceIds().isEmpty() ? fallback : task.traceIds().get(0);
  }

  private static String safe(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "workstream-event").hashCode(), 36);
  }
}

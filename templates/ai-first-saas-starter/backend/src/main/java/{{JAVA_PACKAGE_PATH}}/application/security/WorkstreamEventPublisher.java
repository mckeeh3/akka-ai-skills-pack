package {{JAVA_BASE_PACKAGE}}.application.security;

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
  public static final String PAYLOAD_INVITATION_DELIVERY = "InvitationDeliveryEventPayload";

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

  private static String idempotencyKey(String eventFamily, String eventType, String tenantId, String customerId, String sourceRefId, String semanticTransition) {
    return "workstream-event:" + eventFamily + ":" + eventType + ":" + tenantId + ":" + safe(customerId, "none") + ":" + sourceRefId + ":" + semanticTransition;
  }

  private static String redact(String value) {
    return safe(value, "").replaceAll("(?i)(api[_-]?key|secret|token)=[^\\s,;]+", "$1=[REDACTED]");
  }

  private static String safe(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "workstream-event").hashCode(), 36);
  }
}

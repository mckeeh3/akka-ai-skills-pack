package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionSourceRef;
import {{JAVA_BASE_PACKAGE}}.domain.security.Invitation;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkstreamEventEnvelope;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    var projected = switch (event.eventType()) {
      case "invitation.delivery.failed" -> attentionProducerService.upsertInvitationDelivery(sourceInvitation, event.correlationId());
      case "invitation.delivery.sent" -> attentionProducerService.resolveInvitationDelivery(sourceInvitation, "sent", event.correlationId());
      case "invitation.delivery.captured" -> attentionProducerService.resolveInvitationDelivery(sourceInvitation, "captured", event.correlationId());
      default -> null;
    };
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
    return attentionRepository.find(event.tenantId(), invitationDeliveryItemId(event.payload().get("invitationId"))).orElse(null);
  }

  private AttentionItem withEventEvidence(AttentionItem item, WorkstreamEventEnvelope event) {
    var refs = new ArrayList<>(item.sourceRefs());
    refs.add(new AttentionSourceRef("workstream_event", event.eventId(), event.eventType() + " envelope", INVITATION_CAPABILITY, firstTraceRef(event), event.correlationId()));
    refs.add(new AttentionSourceRef("idempotency", event.idempotencyKey(), "Event projection idempotency key", INVITATION_CAPABILITY, "trace-idempotency-" + stableSuffix(event.idempotencyKey()), event.correlationId()));
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

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "workstream-event").hashCode(), 36);
  }
}

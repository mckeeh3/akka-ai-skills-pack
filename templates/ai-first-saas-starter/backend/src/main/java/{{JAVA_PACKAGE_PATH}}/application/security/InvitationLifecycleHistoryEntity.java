package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailDeliveryStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.Invitation;
import {{JAVA_BASE_PACKAGE}}.domain.security.InvitationLifecycleFact;
import {{JAVA_BASE_PACKAGE}}.domain.security.InvitationStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Append-only Event Sourced Entity for invitation lifecycle facts.
 *
 * <p>The current invitation repository remains the command/read compatibility seam. This component
 * stores audit-grade replayable history for create, resend, delivery, revoke, expire, accept,
 * stale/no-op, and denial facts. Fact payloads are browser-safe and intentionally omit raw invite
 * tokens and token hashes.
 */
@Component(id = "starter-invitation-lifecycle-history")
public class InvitationLifecycleHistoryEntity extends EventSourcedEntity<InvitationLifecycleHistoryEntity.State, InvitationLifecycleHistoryEntity.Event> {

  public static String entityId(String invitationId) {
    return "invitation-history-" + invitationId;
  }

  @Override
  public State emptyState() {
    return State.empty();
  }

  public Effect<InvitationLifecycleFact> recordSnapshot(Invitation invitation) {
    if (invitation == null) return effects().error("invitation-required");
    if (currentState().lastInvitationSnapshot().filter(invitation::equals).isPresent()) {
      return effects().reply(currentState().lastFact().orElseGet(() -> fact(invitation, "INVITATION_NO_OP", "system", "NO_OP", "idempotent-snapshot", null, "empty-idempotent-snapshot")));
    }
    var eventType = currentState().deriveEventType(invitation);
    var fingerprint = currentState().snapshotFingerprint(invitation, eventType);
    if (currentState().seenFingerprints().contains(fingerprint)) {
      return effects().reply(currentState().lastFact().orElseGet(() -> fact(invitation, "INVITATION_NO_OP", "system", "NO_OP", "idempotent-snapshot", null, fingerprint)));
    }
    var fact = fact(invitation, eventType, "system", "ALLOWED", eventType.toLowerCase(java.util.Locale.ROOT), null, fingerprint);
    return effects().persist(new Event.FactRecorded(fact, invitation, fingerprint)).thenReply(State::lastRecordedFact);
  }

  public Effect<InvitationLifecycleFact> recordEmailQueued(EmailOutboxMessage message) {
    if (message == null) return effects().error("email-message-required");
    var invitation = currentState().lastInvitationSnapshot().orElse(null);
    if (invitation == null) return effects().error("invitation-snapshot-required-before-email-history");
    var fingerprint = "EMAIL_QUEUED|" + message.outboxId();
    if (currentState().seenFingerprints().contains(fingerprint)) {
      return effects().reply(currentState().lastFact().orElseGet(() -> fact(invitation, "INVITATION_DELIVERY_QUEUED", "system", "NO_OP", "idempotent-email-queued", message.deliveryAttemptId(), fingerprint)));
    }
    var fact = fact(invitation, "INVITATION_DELIVERY_QUEUED", "system", "ALLOWED", "queued", message.deliveryAttemptId(), fingerprint);
    return effects().persist(new Event.EmailQueued(fact, message.outboxId(), fingerprint)).thenReply(State::lastRecordedFact);
  }

  public Effect<InvitationLifecycleFact> recordDecision(DecisionFact decision) {
    if (decision == null) return effects().error("decision-required");
    var invitation = currentState().lastInvitationSnapshot().orElse(decision.invitation());
    if (invitation == null) return effects().error("invitation-required");
    var fingerprint = "DECISION|" + decision.actionType() + "|" + decision.result() + "|" + safe(decision.correlationId()) + "|" + safe(decision.reasonCode());
    if (currentState().seenFingerprints().contains(fingerprint)) {
      return effects().reply(currentState().lastFact().orElseGet(() -> fact(invitation, decision.actionType(), decision.actorAccountId(), decision.result().name(), decision.reasonCode(), null, fingerprint)));
    }
    var fact = fact(invitation, decision.actionType(), decision.actorAccountId(), decision.result().name(), decision.reasonCode(), null, fingerprint);
    return effects().persist(new Event.DecisionRecorded(fact, fingerprint)).thenReply(State::lastRecordedFact);
  }

  public ReadOnlyEffect<List<InvitationLifecycleFact>> history() {
    return effects().reply(currentState().facts());
  }

  public ReadOnlyEffect<Optional<InvitationLifecycleFact>> latest() {
    return effects().reply(currentState().lastFact());
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.FactRecorded recorded -> currentState().record(recorded.fact(), recorded.invitation(), recorded.fingerprint());
      case Event.EmailQueued queued -> currentState().record(queued.fact(), null, queued.fingerprint());
      case Event.DecisionRecorded decision -> currentState().record(decision.fact(), null, decision.fingerprint());
    };
  }

  private InvitationLifecycleFact fact(Invitation invitation, String eventType, String actorAccountId, String result, String reasonCode, String deliveryAttemptId, String fingerprint) {
    return InvitationLifecycleFact.fromInvitation(
        Integer.toHexString(fingerprint.hashCode()),
        eventType,
        invitation,
        actorAccountId,
        result,
        reasonCode,
        deliveryAttemptId,
        currentState().clockNow());
  }

  private static String safe(String value) {
    return value == null ? "" : value;
  }

  public record State(List<InvitationLifecycleFact> facts, Invitation lastInvitation, List<String> seenFingerprints) {
    public State {
      facts = List.copyOf(facts == null ? List.of() : facts);
      seenFingerprints = List.copyOf(seenFingerprints == null ? List.of() : seenFingerprints);
    }

    static State empty() {
      return new State(List.of(), null, List.of());
    }

    State record(InvitationLifecycleFact fact, Invitation invitation, String fingerprint) {
      var nextFacts = new java.util.ArrayList<>(facts);
      nextFacts.add(fact);
      var nextSeen = new java.util.ArrayList<>(seenFingerprints);
      nextSeen.add(fingerprint);
      return new State(List.copyOf(nextFacts), invitation == null ? lastInvitation : invitation, List.copyOf(nextSeen));
    }

    Optional<Invitation> lastInvitationSnapshot() {
      return Optional.ofNullable(lastInvitation);
    }

    Optional<InvitationLifecycleFact> lastFact() {
      return facts.isEmpty() ? Optional.empty() : Optional.of(facts.get(facts.size() - 1));
    }

    InvitationLifecycleFact lastRecordedFact() {
      return lastFact().orElseThrow();
    }

    Instant clockNow() {
      return Instant.now();
    }

    String deriveEventType(Invitation invitation) {
      if (lastInvitation == null) return "INVITATION_CREATED";
      if (invitation.status() == InvitationStatus.ACCEPTED && lastInvitation.status() != InvitationStatus.ACCEPTED) return "INVITATION_ACCEPTED";
      if (invitation.status() == InvitationStatus.REVOKED && lastInvitation.status() != InvitationStatus.REVOKED) return "INVITATION_REVOKED";
      if (invitation.status() == InvitationStatus.EXPIRED && lastInvitation.status() != InvitationStatus.EXPIRED) return "INVITATION_EXPIRED";
      if (invitation.deliveryStatus() == EmailDeliveryStatus.FAILED && lastInvitation.deliveryStatus() != EmailDeliveryStatus.FAILED) return "INVITATION_DELIVERY_FAILED";
      if ((invitation.deliveryStatus() == EmailDeliveryStatus.SENT || invitation.deliveryStatus() == EmailDeliveryStatus.CAPTURED)
          && invitation.deliveryAttempts() > lastInvitation.deliveryAttempts()) return "INVITATION_DELIVERY_" + invitation.deliveryStatus().name();
      if (invitation.status() == InvitationStatus.PENDING_DELIVERY && invitation.resendCount() > lastInvitation.resendCount()) return "INVITATION_RESENT";
      return "INVITATION_SNAPSHOT_RECORDED";
    }

    String snapshotFingerprint(Invitation invitation, String eventType) {
      return String.join("|",
          eventType,
          invitation.invitationId(),
          invitation.status().name(),
          invitation.deliveryStatus().name(),
          Integer.toString(invitation.deliveryAttempts()),
          Integer.toString(invitation.resendCount()),
          safe(invitation.acceptedByWorkosSubject()),
          safe(invitation.revokedByAccountId()),
          safe(invitation.revokeReason()),
          safe(invitation.correlationId()));
    }
  }

  public sealed interface Event {
    InvitationLifecycleFact fact();

    @TypeName("starter-invitation-lifecycle-fact-recorded")
    record FactRecorded(InvitationLifecycleFact fact, Invitation invitation, String fingerprint) implements Event {}

    @TypeName("starter-invitation-email-queued")
    record EmailQueued(InvitationLifecycleFact fact, String outboxId, String fingerprint) implements Event {}

    @TypeName("starter-invitation-decision-recorded")
    record DecisionRecorded(InvitationLifecycleFact fact, String fingerprint) implements Event {}
  }

  public record DecisionFact(
      Invitation invitation,
      String actionType,
      AdminAuditEvent.Result result,
      String reasonCode,
      String actorAccountId,
      String correlationId) {}
}

package ai.first.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.EventSourcedTestKit;
import ai.first.domain.security.AdminAuditEvent;
import ai.first.domain.security.EmailDeliveryStatus;
import ai.first.domain.security.EmailOutboxMessage;
import ai.first.domain.security.FoundationRole;
import ai.first.domain.security.Invitation;
import ai.first.domain.security.InvitationLifecycleFact;
import ai.first.domain.security.InvitationStatus;
import ai.first.domain.security.ScopeType;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class InvitationLifecycleHistoryEntityTest {

  private EventSourcedTestKit<InvitationLifecycleHistoryEntity.State, InvitationLifecycleHistoryEntity.Event, InvitationLifecycleHistoryEntity> newTestKit(String invitationId) {
    return EventSourcedTestKit.of(InvitationLifecycleHistoryEntity.entityId(invitationId), InvitationLifecycleHistoryEntity::new);
  }

  @Test
  void recordsReplayableCreateQueueDeliveryAcceptHistoryWithoutTokenLeakage() {
    var testKit = newTestKit("inv-1");
    var created = invitation("inv-1", InvitationStatus.PENDING_DELIVERY, EmailDeliveryStatus.QUEUED, 0, 0, null, null);
    var email = email("inv-1:delivery-1", "inv-1", "https://app.example.test/accept?token=invite-token-raw-only-in-email");

    testKit.method(InvitationLifecycleHistoryEntity::recordSnapshot).invoke(created);
    testKit.method(InvitationLifecycleHistoryEntity::recordEmailQueued).invoke(email);
    testKit.method(InvitationLifecycleHistoryEntity::recordSnapshot).invoke(invitation("inv-1", InvitationStatus.SENT, EmailDeliveryStatus.CAPTURED, 1, 0, null, null));
    testKit.method(InvitationLifecycleHistoryEntity::recordSnapshot).invoke(invitation("inv-1", InvitationStatus.ACCEPTED, EmailDeliveryStatus.CAPTURED, 1, 0, "workos-new", null));

    var history = testKit.method(InvitationLifecycleHistoryEntity::history).invoke().getReply();

    assertEquals(List.of("INVITATION_CREATED", "INVITATION_DELIVERY_QUEUED", "INVITATION_DELIVERY_CAPTURED", "INVITATION_ACCEPTED"), history.stream().map(InvitationLifecycleFact::eventType).toList());
    assertEquals(InvitationStatus.ACCEPTED, history.get(history.size() - 1).invitationStatus());
    assertTrue(history.stream().allMatch(fact -> fact.tenantId().equals("tenant-1")));
    assertFalse(history.toString().contains("invite-token-raw-only-in-email"));
    assertFalse(history.toString().contains("hash-"));
  }

  @Test
  void duplicateSnapshotsAndQueuedMessagesAreIdempotentNoEventReplies() {
    var testKit = newTestKit("inv-2");
    var invite = invitation("inv-2", InvitationStatus.PENDING_DELIVERY, EmailDeliveryStatus.QUEUED, 0, 0, null, null);
    var email = email("inv-2:delivery-1", "inv-2", "https://app.example.test/accept?token=invite-token-email-only");

    var created = testKit.method(InvitationLifecycleHistoryEntity::recordSnapshot).invoke(invite);
    var replay = testKit.method(InvitationLifecycleHistoryEntity::recordSnapshot).invoke(invite);
    var queued = testKit.method(InvitationLifecycleHistoryEntity::recordEmailQueued).invoke(email);
    var queuedReplay = testKit.method(InvitationLifecycleHistoryEntity::recordEmailQueued).invoke(email);

    assertEquals("INVITATION_CREATED", created.getReply().eventType());
    assertFalse(replay.didPersistEvents());
    assertEquals("INVITATION_CREATED", replay.getReply().eventType());
    assertEquals("INVITATION_DELIVERY_QUEUED", queued.getReply().eventType());
    assertFalse(queuedReplay.didPersistEvents());
    assertEquals(2, testKit.method(InvitationLifecycleHistoryEntity::history).invoke().getReply().size());
  }

  @Test
  void recordsDenialAndTerminalNoOpFacts() {
    var testKit = newTestKit("inv-3");
    var revoked = invitation("inv-3", InvitationStatus.REVOKED, EmailDeliveryStatus.QUEUED, 0, 0, null, "admin@example.com");
    testKit.method(InvitationLifecycleHistoryEntity::recordSnapshot).invoke(revoked);

    var denied = testKit.method(InvitationLifecycleHistoryEntity::recordDecision).invoke(new InvitationLifecycleHistoryEntity.DecisionFact(
        revoked,
        "INVITATION_ACCEPT",
        AdminAuditEvent.Result.DENIED,
        "revoked",
        "system",
        "corr-denied"));
    var noop = testKit.method(InvitationLifecycleHistoryEntity::recordDecision).invoke(new InvitationLifecycleHistoryEntity.DecisionFact(
        revoked,
        "INVITATION_EXPIRE",
        AdminAuditEvent.Result.NO_OP,
        "terminal",
        "system",
        "corr-noop"));

    assertEquals("DENIED", denied.getReply().result());
    assertEquals("NO_OP", noop.getReply().result());
    assertEquals(List.of("INVITATION_CREATED", "INVITATION_ACCEPT", "INVITATION_EXPIRE"), testKit.method(InvitationLifecycleHistoryEntity::history).invoke().getReply().stream().map(InvitationLifecycleFact::eventType).toList());
  }

  @Test
  void rejectsUnsafeRawTokenInFacts() {
    assertThrows(IllegalArgumentException.class, () -> new InvitationLifecycleFact(
        "fact-token=raw",
        "inv-unsafe",
        "INVITATION_CREATED",
        ScopeType.TENANT,
        "tenant-1",
        null,
        "target@example.com",
        "membership-inv-unsafe",
        "target@example.com",
        InvitationStatus.PENDING_DELIVERY,
        EmailDeliveryStatus.QUEUED,
        0,
        0,
        List.of(),
        "system",
        "ALLOWED",
        "created",
        null,
        "corr-unsafe",
        Instant.parse("2026-05-20T10:15:30Z")));
  }

  private static Invitation invitation(String id, InvitationStatus status, EmailDeliveryStatus deliveryStatus, int attempts, int resendCount, String acceptedBy, String revokedBy) {
    return new Invitation(
        id,
        "new.user@example.com",
        ScopeType.TENANT,
        "tenant-1",
        null,
        List.of(FoundationRole.TENANT_EMPLOYEE),
        "new.user@example.com",
        "membership-" + id,
        status,
        deliveryStatus,
        attempts,
        deliveryStatus == EmailDeliveryStatus.SENT || deliveryStatus == EmailDeliveryStatus.CAPTURED ? List.of("captured-1") : List.of(),
        null,
        "accept-" + id,
        "hash-" + id,
        Instant.parse("2026-05-20T11:15:30Z"),
        status == InvitationStatus.ACCEPTED ? Instant.parse("2026-05-20T10:45:30Z") : null,
        acceptedBy,
        status == InvitationStatus.REVOKED ? Instant.parse("2026-05-20T10:35:30Z") : null,
        revokedBy,
        status == InvitationStatus.REVOKED ? "wrong recipient" : null,
        resendCount,
        "admin@example.com",
        Instant.parse("2026-05-20T10:15:30Z"),
        "idem-" + id,
        "corr-" + status.name().toLowerCase(java.util.Locale.ROOT));
  }

  private static EmailOutboxMessage email(String outboxId, String invitationId, String inviteUrl) {
    return new EmailOutboxMessage(
        outboxId,
        "INVITATION",
        invitationId,
        "delivery-1",
        ScopeType.TENANT,
        "tenant-1",
        null,
        "new.user@example.com",
        inviteUrl,
        Map.of("expiresAt", "2026-05-20T11:15:30Z"),
        "corr-email",
        Instant.parse("2026-05-20T10:15:30Z"));
  }
}

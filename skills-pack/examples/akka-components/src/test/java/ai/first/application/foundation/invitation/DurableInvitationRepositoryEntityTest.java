package ai.first.application.foundation.invitation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.KeyValueEntityTestKit;
import ai.first.domain.foundation.email.EmailDeliveryStatus;
import ai.first.domain.foundation.email.EmailOutboxMessage;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.invitation.Invitation;
import ai.first.domain.foundation.invitation.InvitationRepositoryState;
import ai.first.domain.foundation.invitation.InvitationStatus;
import ai.first.domain.foundation.identity.ScopeType;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DurableInvitationRepositoryEntityTest {

  private KeyValueEntityTestKit<InvitationRepositoryState, DurableInvitationRepositoryEntity> newTestKit() {
    return KeyValueEntityTestKit.of(
        DurableInvitationRepositoryEntity.ENTITY_ID,
        __ -> new DurableInvitationRepositoryEntity());
  }

  @Test
  void savesInvitationAndSupportsExistingRepositoryLookups() {
    var testKit = newTestKit();
    var invite = invitation("inv-1", "member@example.com", "idempotency-1", "accept-1", "hash-1", InvitationStatus.SENT);

    var save = testKit.method(DurableInvitationRepositoryEntity::saveInvitation).invoke(invite);

    assertTrue(save.stateWasUpdated());
    assertEquals(invite, save.getReply());
    assertEquals(invite, testKit.method(DurableInvitationRepositoryEntity::invitation).invoke("inv-1").getReply().orElseThrow());
    assertEquals(invite, testKit.method(DurableInvitationRepositoryEntity::findByIdempotencyKey).invoke("idempotency-1").getReply().orElseThrow());
    assertEquals(invite, testKit.method(DurableInvitationRepositoryEntity::findByAcceptanceContext).invoke("accept-1").getReply().orElseThrow());
    assertEquals(invite, testKit.method(DurableInvitationRepositoryEntity::findByTokenHash).invoke("hash-1").getReply().orElseThrow());
    assertEquals(List.of(invite), testKit.method(DurableInvitationRepositoryEntity::invitations).invoke().getReply());
  }

  @Test
  void findsOnlyActiveDuplicateWithinSameTenantScope() {
    var testKit = newTestKit();
    var active = invitation("inv-active", "dupe@example.com", "key-a", "accept-a", "hash-a", InvitationStatus.SENT);
    var accepted = invitation("inv-accepted", "dupe@example.com", "key-b", "accept-b", "hash-b", InvitationStatus.ACCEPTED);
    testKit.method(DurableInvitationRepositoryEntity::saveInvitation).invoke(accepted);
    testKit.method(DurableInvitationRepositoryEntity::saveInvitation).invoke(active);

    var match = testKit.method(DurableInvitationRepositoryEntity::findActiveDuplicate)
        .invoke(new DurableInvitationRepositoryEntity.FindActiveDuplicateQuery("dupe@example.com", ScopeType.TENANT, "tenant-1", null));
    var wrongTenant = testKit.method(DurableInvitationRepositoryEntity::findActiveDuplicate)
        .invoke(new DurableInvitationRepositoryEntity.FindActiveDuplicateQuery("dupe@example.com", ScopeType.TENANT, "tenant-2", null));

    assertEquals(active, match.getReply().orElseThrow());
    assertTrue(wrongTenant.getReply().isEmpty());
  }

  @Test
  void enqueuesOutboxMessagesIdempotentlyWithoutRawTokenInInvitationState() {
    var testKit = newTestKit();
    var invite = invitation("inv-email", "email@example.com", "key-email", "accept-email", "hash-email", InvitationStatus.PENDING_DELIVERY);
    var message = email("inv-email:delivery-1", "inv-email", "https://app.example.test/accept?token=invite-token-in-email-only");
    testKit.method(DurableInvitationRepositoryEntity::saveInvitation).invoke(invite);

    var first = testKit.method(DurableInvitationRepositoryEntity::enqueueEmail).invoke(message);
    var duplicate = testKit.method(DurableInvitationRepositoryEntity::enqueueEmail).invoke(message);

    assertTrue(first.stateWasUpdated());
    assertFalse(duplicate.stateWasUpdated());
    assertEquals("inv-email:delivery-1", duplicate.getReply());
    assertEquals(message, testKit.method(DurableInvitationRepositoryEntity::email).invoke("inv-email:delivery-1").getReply().orElseThrow());
    assertEquals(List.of(message), testKit.method(DurableInvitationRepositoryEntity::queuedEmails).invoke().getReply());
    assertFalse(testKit.method(DurableInvitationRepositoryEntity::invitation).invoke("inv-email").getReply().orElseThrow().toString().contains("invite-token-in-email-only"));
  }

  private static Invitation invitation(String id, String email, String idempotencyKey, String acceptanceContext, String tokenHash, InvitationStatus status) {
    return new Invitation(
        id,
        email,
        ScopeType.TENANT,
        "tenant-1",
        null,
        List.of(FoundationRole.TENANT_EMPLOYEE),
        email,
        "membership-" + id,
        status,
        EmailDeliveryStatus.QUEUED,
        0,
        List.of(),
        null,
        acceptanceContext,
        tokenHash,
        Instant.parse("2026-05-20T11:15:30Z"),
        status == InvitationStatus.ACCEPTED ? Instant.parse("2026-05-20T10:45:30Z") : null,
        status == InvitationStatus.ACCEPTED ? "workos-" + email : null,
        null,
        null,
        null,
        0,
        "admin@example.com",
        Instant.parse("2026-05-20T10:15:30Z"),
        idempotencyKey,
        "corr-" + id);
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
        "email@example.com",
        inviteUrl,
        Map.of("expiresAt", "2026-05-20T11:15:30Z"),
        "corr-email",
        Instant.parse("2026-05-20T10:15:30Z"));
  }
}

package ai.first.application.coreapp.useradmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import ai.first.application.foundation.email.ResendEmailService;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.InMemoryTestIdentityRepository;
import ai.first.application.foundation.invitation.InvitationService;
import ai.first.application.foundation.invitation.InMemoryTestInvitationRepository;
import ai.first.domain.foundation.email.EmailDeliveryStatus;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
import ai.first.domain.foundation.invitation.InvitationStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Optional live Resend provider smoke. Skips unless explicitly enabled with backend-only env. */
class RealResendProviderSmokeTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-20T10:15:30Z"), ZoneOffset.UTC);

  @Test
  void invitationDeliveryUsesRealResendProviderWithoutSecretExposure() {
    assumeTrue(Boolean.getBoolean("realResendProviderSmoke"), "Skipping live Resend smoke because -DrealResendProviderSmoke=true was not provided.");
    var apiKey = trimToNull(System.getenv("RESEND_API_KEY"));
    var from = firstNonBlank(System.getenv("INVITE_EMAIL_FROM"), System.getenv("RESEND_FROM_EMAIL"));
    assumeTrue(apiKey != null, "Skipping live Resend smoke because RESEND_API_KEY is not set.");
    assumeTrue(from != null, "Skipping live Resend smoke because INVITE_EMAIL_FROM/RESEND_FROM_EMAIL is not set.");
    var recipient = firstNonBlank(System.getenv("RESEND_SMOKE_TO"), System.getenv("RESEND_TEST_TO"), System.getenv("INVITE_EMAIL_TO"), extractEmail(from));
    assumeTrue(recipient != null, "Skipping live Resend smoke because no recipient can be derived from RESEND_SMOKE_TO/RESEND_TEST_TO/INVITE_EMAIL_TO or sender config.");

    var identityRepository = new InMemoryTestIdentityRepository();
    var invitationRepository = new InMemoryTestInvitationRepository();
    var resolver = new AuthContextResolver(identityRepository);
    var invitations = new InvitationService(identityRepository, invitationRepository, clock);
    identityRepository.putTenant(new Tenant("tenant-resend-smoke", "Resend Smoke Tenant", true));
    seedAdmin(identityRepository, "resend.admin@example.test", "membership-resend-admin", FoundationRole.TENANT_ADMIN);
    var tenantAdmin = resolver.resolveMe(new WorkosIdentity("workos-resend.admin@example.test", "resend.admin@example.test", "Resend Admin"), "membership-resend-admin", "corr-live-resend-admin");

    var invite = invitations.createInvitation(tenantAdmin, new InvitationService.CreateInvitationRequest(
        "idem-live-resend-smoke",
        ScopeType.TENANT,
        "tenant-resend-smoke",
        null,
        recipient,
        "Live Resend Smoke Recipient",
        List.of(FoundationRole.TENANT_EMPLOYEE),
        clock.instant().plus(7, ChronoUnit.DAYS),
        "live-resend-smoke",
        "corr-live-resend-create"));
    var message = invitationRepository.queuedEmails().get(0);

    var service = new ResendEmailService();
    var result = service.deliver(message, ResendEmailService.DeliveryMode.PRODUCTION);

    assertTrue(result.success(), "Expected live Resend delivery to be accepted by provider; safe error was " + result.safeErrorSummary());
    assertEquals(ResendEmailService.DeliveryKind.SENT, result.kind());
    assertNotNull(result.providerMessageId());
    assertFalse(result.providerMessageId().isBlank());
    assertFalse(result.toString().contains(apiKey), "Provider secret leaked into delivery result");

    var delivered = invitations.recordDeliveryResult(invite.invitationId(), message.deliveryAttemptId(), result.success(), result.providerMessageId(), result.safeErrorSummary(), "corr-live-resend-delivery");
    assertEquals(InvitationStatus.SENT, delivered.status());
    assertEquals(EmailDeliveryStatus.SENT, delivered.deliveryStatus());
    assertTrue(delivered.providerMessageIds().contains(result.providerMessageId()));
    assertFalse(delivered.toString().contains(apiKey), "Provider secret leaked into invitation state");
    assertFalse(message.toString().contains(apiKey), "Provider secret leaked into outbox message");
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_DELIVERY_SENT")));
    assertTrue(identityRepository.auditEvents().stream().noneMatch(event -> event.toString().contains(apiKey)), "Provider secret leaked into audit events");
  }

  private static void seedAdmin(InMemoryTestIdentityRepository identityRepository, String email, String membershipId, FoundationRole role) {
    identityRepository.saveAccount(new Account(email, "workos-" + email, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, "Resend Admin", null, null, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, "tenant-resend-smoke", null, List.of(role), MembershipStatus.ACTIVE, false, null));
  }

  private static String trimToNull(String value) {
    if (value == null || value.isBlank()) return null;
    return value.trim();
  }

  private static String firstNonBlank(String... values) {
    if (values == null) return null;
    for (var value : values) {
      var trimmed = trimToNull(value);
      if (trimmed != null) return trimmed;
    }
    return null;
  }

  private static String extractEmail(String value) {
    var trimmed = trimToNull(value);
    if (trimmed == null) return null;
    var start = trimmed.indexOf('<');
    var end = trimmed.indexOf('>', start + 1);
    if (start >= 0 && end > start) return trimToNull(trimmed.substring(start + 1, end));
    return trimmed.contains("@") ? trimmed : null;
  }
}

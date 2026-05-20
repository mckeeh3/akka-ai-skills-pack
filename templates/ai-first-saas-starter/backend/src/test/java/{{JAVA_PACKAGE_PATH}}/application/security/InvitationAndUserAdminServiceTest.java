package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailDeliveryStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.InvitationStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvitationAndUserAdminServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-20T10:15:30Z"), ZoneOffset.UTC);
  private InMemoryIdentityRepository identityRepository;
  private InMemoryInvitationRepository invitationRepository;
  private AuthContextResolver resolver;
  private InvitationService invitations;
  private UserAdminService userAdmin;
  private AuthContextResolver.ResolvedMe tenantAdmin;

  @BeforeEach
  void setUp() {
    identityRepository = new InMemoryIdentityRepository();
    invitationRepository = new InMemoryInvitationRepository();
    resolver = new AuthContextResolver(identityRepository);
    invitations = new InvitationService(identityRepository, invitationRepository, clock);
    userAdmin = new UserAdminService(identityRepository, clock);
    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    seedAdmin("admin@example.com", "membership-admin", FoundationRole.TENANT_ADMIN);
    tenantAdmin = resolver.resolveMe(new WorkosIdentity("workos-admin@example.com", "admin@example.com", "Admin"), null, "corr-admin");
  }

  @Test
  void inviteLifecycleQueuesCapturedOutboxRecordsDeliveryAndAcceptsIdempotently() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("key-1", "new.user@example.com"));

    assertEquals(InvitationStatus.PENDING_DELIVERY, invite.status());
    assertEquals(EmailDeliveryStatus.QUEUED, invite.deliveryStatus());
    assertEquals(1, invitationRepository.queuedEmails().size());
    assertFalse(invitationRepository.queuedEmails().get(0).inviteUrl().contains(invite.tokenHash()));
    assertTrue(invite.tokenHash().matches("[0-9a-f]{64}"));

    var delivered = invitations.recordDeliveryResult(invite.invitationId(), "delivery-1", true, "captured-1", null, "corr-delivery");
    assertEquals(InvitationStatus.SENT, delivered.status());
    assertEquals(EmailDeliveryStatus.CAPTURED, delivered.deliveryStatus());

    var accepted = invitations.accept(new WorkosIdentity("workos-new", "new.user@example.com", "New User"), delivered.acceptanceContextId(), "corr-accept");
    assertEquals(InvitationStatus.ACCEPTED, accepted.status());
    assertEquals("workos-new", accepted.acceptedByWorkosSubject());
    assertEquals(InvitationStatus.ACCEPTED, invitations.accept(new WorkosIdentity("workos-new", "new.user@example.com", "New User"), delivered.acceptanceContextId(), "corr-accept-replay").status());
    assertThrows(AuthorizationException.class, () -> invitations.accept(new WorkosIdentity("workos-other", "new.user@example.com", "Other"), delivered.acceptanceContextId(), "corr-other"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("MEMBERSHIP_ACTIVATE")));
  }

  @Test
  void browserAcceptanceReturnsSafeRecoveryStatesAndAcceptsRawToken() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("accept-token", "token.user@example.com"));
    var rawToken = rawTokenFromOutbox(invite.invitationId() + ":delivery-1");

    var wrongAccount = invitations.acceptForBrowser(
        new WorkosIdentity("workos-wrong", "wrong@example.com", "Wrong"),
        new InvitationService.AcceptInvitationRequest(rawToken, null),
        "corr-wrong-account");
    assertEquals("wrong-account", wrongAccount.status());
    assertFalse(wrongAccount.toString().contains(rawToken));

    var accepted = invitations.acceptForBrowser(
        new WorkosIdentity("workos-token", "token.user@example.com", "Token User"),
        new InvitationService.AcceptInvitationRequest(rawToken, null),
        "corr-token-accept");
    assertEquals("accepted", accepted.status());
    assertFalse(accepted.toString().contains(rawToken));

    var duplicate = invitations.acceptForBrowser(
        new WorkosIdentity("workos-token", "token.user@example.com", "Token User"),
        new InvitationService.AcceptInvitationRequest(rawToken, null),
        "corr-token-replay");
    assertEquals("already-accepted", duplicate.status());

    var expired = invitations.createInvitation(tenantAdmin, inviteRequest("accept-expired", "expired@example.com"));
    invitations.expire(expired.invitationId(), "tenant-1", null, "corr-expire-before-accept");
    assertEquals("expired", invitations.acceptForBrowser(
        new WorkosIdentity("workos-expired", "expired@example.com", "Expired"),
        new InvitationService.AcceptInvitationRequest(null, expired.acceptanceContextId()),
        "corr-expired-accept").status());

    var revoked = invitations.createInvitation(tenantAdmin, inviteRequest("accept-revoked", "revoked@example.com"));
    invitations.revoke(tenantAdmin, revoked.invitationId(), "mistake", "corr-revoke-before-accept");
    assertEquals("revoked", invitations.acceptForBrowser(
        new WorkosIdentity("workos-revoked", "revoked@example.com", "Revoked"),
        new InvitationService.AcceptInvitationRequest(null, revoked.acceptanceContextId()),
        "corr-revoked-accept").status());

    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_ACCEPT") && event.result() == {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent.Result.DENIED));
  }

  @Test
  void duplicateCreateResendRevokeAndExpiryAreIdempotentAndAudited() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("dup-key", "dupe@example.com"));
    var replay = invitations.createInvitation(tenantAdmin, inviteRequest("dup-key", "dupe@example.com"));
    assertEquals(invite.invitationId(), replay.invitationId());

    var resent = invitations.resend(tenantAdmin, invite.invitationId(), "resend-1", "repair failed delivery", "corr-resend");
    assertEquals(1, resent.resendCount());
    assertEquals(2, invitationRepository.queuedEmails().size());

    var revoked = invitations.revoke(tenantAdmin, invite.invitationId(), "wrong recipient", "corr-revoke");
    assertEquals(InvitationStatus.REVOKED, revoked.status());
    assertEquals(InvitationStatus.REVOKED, invitations.expire(invite.invitationId(), "tenant-1", null, "corr-expire-late").status());
    assertThrows(AuthorizationException.class, () -> invitations.accept(new WorkosIdentity("workos-dupe", "dupe@example.com", "Dupe"), revoked.acceptanceContextId(), "corr-accept-revoked"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_REVOKE")));
  }

  @Test
  void crossTenantAndRoleEscalationInviteAttemptsAreDenied() {
    identityRepository.putTenant(new Tenant("tenant-2", "Tenant Two", true));

    assertThrows(AuthorizationException.class, () -> invitations.createInvitation(tenantAdmin, new InvitationService.CreateInvitationRequest(
        "key-cross", ScopeType.TENANT, "tenant-2", null, "x@example.com", "X", List.of(FoundationRole.TENANT_ADMIN), clock.instant().plusSeconds(3600), "cross", "corr-cross")));
    assertThrows(AuthorizationException.class, () -> invitations.createInvitation(tenantAdmin, new InvitationService.CreateInvitationRequest(
        "key-role", ScopeType.TENANT, "tenant-1", null, "owner@example.com", "Owner", List.of(FoundationRole.SAAS_OWNER_ADMIN), clock.instant().plusSeconds(3600), "role", "corr-role")));
  }

  @Test
  void userAdminListsScopedUsersAndProtectsLastAdmin() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("list-key", "member@example.com"));
    invitations.accept(new WorkosIdentity("workos-member", "member@example.com", "Member"), invite.acceptanceContextId(), "corr-accept-member");

    var users = userAdmin.listUsers(tenantAdmin, ScopeType.TENANT, "tenant-1", null);
    assertTrue(users.stream().anyMatch(row -> row.accountId().equals("member@example.com")));

    var lastAdminError = assertThrows(AuthorizationException.class, () -> userAdmin.replaceRoles(tenantAdmin, "membership-admin", List.of(FoundationRole.TENANT_EMPLOYEE), "remove admin", "corr-last-admin"));
    assertEquals("last-admin-denied", lastAdminError.reasonCode());

    seedAdmin("second-admin@example.com", "membership-second-admin", FoundationRole.TENANT_ADMIN);
    var updated = userAdmin.replaceRoles(tenantAdmin, "membership-admin", List.of(FoundationRole.TENANT_EMPLOYEE), "handoff", "corr-role-replace");
    assertEquals(List.of(FoundationRole.TENANT_EMPLOYEE), updated.roles());
  }

  @Test
  void resendProductionReadinessFailsWithoutResendConfigurationAndLocalCaptureIsSafe() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("email-key", "email@example.com"));
    var emailService = new ResendEmailService();
    var message = invitationRepository.queuedEmails().get(0);

    var localResult = emailService.deliver(message, ResendEmailService.DeliveryMode.LOCAL_OR_TEST);
    assertTrue(localResult.success());
    assertTrue(localResult.providerMessageId().startsWith("captured-"));

    var prodResult = emailService.deliver(message, ResendEmailService.DeliveryMode.PRODUCTION);
    if (System.getenv("RESEND_API_KEY") == null) {
      assertFalse(prodResult.success());
      assertEquals("resend-config-missing", prodResult.safeErrorSummary());
    }
    assertEquals(invite.invitationId(), message.invitationId());
  }

  private String rawTokenFromOutbox(String outboxId) {
    var inviteUrl = invitationRepository.email(outboxId).orElseThrow().inviteUrl();
    return inviteUrl.substring(inviteUrl.indexOf("token=") + "token=".length());
  }

  private InvitationService.CreateInvitationRequest inviteRequest(String key, String email) {
    return new InvitationService.CreateInvitationRequest(
        key,
        ScopeType.TENANT,
        "tenant-1",
        null,
        email,
        "Invited User",
        List.of(FoundationRole.TENANT_EMPLOYEE),
        clock.instant().plusSeconds(3600),
        "onboarding",
        "corr-" + key);
  }

  private void seedAdmin(String email, String membershipId, FoundationRole role) {
    identityRepository.saveAccount(new Account(email, "workos-" + email, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, null, null, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, "tenant-1", null, List.of(role), MembershipStatus.ACTIVE, false, null));
  }
}

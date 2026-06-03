package ai.first.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.security.Account;
import ai.first.domain.security.AccountStatus;
import ai.first.domain.security.EmailDeliveryStatus;
import ai.first.domain.security.FoundationRole;
import ai.first.domain.security.InvitationStatus;
import ai.first.domain.security.Membership;
import ai.first.domain.security.MembershipStatus;
import ai.first.domain.security.ScopeType;
import ai.first.domain.security.Tenant;
import ai.first.domain.security.UserProfile;
import ai.first.domain.security.UserSettings;
import ai.first.domain.security.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvitationAndUserAdminServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-20T10:15:30Z"), ZoneOffset.UTC);
  private LocalDemoIdentityRepository identityRepository;
  private LocalDemoInvitationRepository invitationRepository;
  private AuthContextResolver resolver;
  private InvitationService invitations;
  private UserAdminService userAdmin;
  private AuthContextResolver.ResolvedMe tenantAdmin;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    invitationRepository = new LocalDemoInvitationRepository();
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

    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_ACCEPT") && event.result() == ai.first.domain.security.AdminAuditEvent.Result.DENIED));
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
    var updated = userAdmin.replaceRoles(tenantAdmin, "membership-second-admin", List.of(FoundationRole.TENANT_EMPLOYEE), "handoff", "corr-role-replace");
    assertEquals(List.of(FoundationRole.TENANT_EMPLOYEE), updated.roles());
  }

  @Test
  void userAdminStatusTransitionsAreAuthoritativeIdempotentAndGuarded() {
    seedAdmin("second-admin@example.com", "membership-second-admin", FoundationRole.TENANT_ADMIN);
    seedAdmin("member@example.com", "membership-member", FoundationRole.TENANT_EMPLOYEE);

    var disabled = userAdmin.updateMemberStatus(tenantAdmin, "membership-member", MembershipStatus.SUSPENDED, "offboarding", "idem-disable-member", "corr-disable-member");
    assertEquals("accepted", disabled.status());
    assertEquals(MembershipStatus.SUSPENDED, disabled.membership().status());
    assertTrue(disabled.traceId().contains("trace-useradmin-update-member-status"));

    var noOp = userAdmin.updateMemberStatus(tenantAdmin, "membership-member", MembershipStatus.SUSPENDED, "offboarding replay", "idem-disable-member-replay", "corr-disable-member-replay");
    assertEquals("no-op", noOp.status());
    assertTrue(noOp.message().contains("idempotency"));

    var reactivated = userAdmin.updateMemberStatus(tenantAdmin, "membership-member", MembershipStatus.ACTIVE, "return", "idem-reactivate-member", "corr-reactivate-member");
    assertEquals("accepted", reactivated.status());
    assertEquals(MembershipStatus.ACTIVE, reactivated.membership().status());

    var selfDisable = assertThrows(AuthorizationException.class, () -> userAdmin.updateMemberStatus(tenantAdmin, "membership-admin", MembershipStatus.SUSPENDED, "self", "idem-self-disable", "corr-self-disable"));
    assertEquals("self-disable-denied", selfDisable.reasonCode());
    identityRepository.putMembership(new Membership("membership-admin", "admin@example.com", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.ACTIVE, false, null));
    var lastAdmin = assertThrows(AuthorizationException.class, () -> userAdmin.updateMemberStatus(tenantAdmin, "membership-second-admin", MembershipStatus.SUSPENDED, "last admin", "idem-last-admin", "corr-last-admin-disable"));
    assertEquals("last-admin-denied", lastAdmin.reasonCode());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("USERADMIN_UPDATE_MEMBER_STATUS") && event.result() == ai.first.domain.security.AdminAuditEvent.Result.NO_OP));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.reasonCode().equals("self-disable-denied")));
  }

  @Test
  void userAdminRolePreviewShowsCapabilityDeltaAndDeniesSelfAdminRemoval() {
    seedAdmin("second-admin@example.com", "membership-second-admin", FoundationRole.TENANT_ADMIN);

    var preview = userAdmin.previewRoleChange(tenantAdmin, "membership-second-admin", List.of(FoundationRole.TENANT_EMPLOYEE), "least privilege", "corr-role-preview-delta");
    assertTrue(preview.allowed());
    assertFalse(preview.capabilityDelta().isEmpty());
    assertTrue(preview.affectedWorkstreams().contains("User Admin"));
    assertEquals("admin-coverage-preserved", preview.lastAdminImpact());

    var selfPreview = userAdmin.previewRoleChange(tenantAdmin, "membership-admin", List.of(FoundationRole.TENANT_EMPLOYEE), "self demotion", "corr-role-preview-self");
    assertFalse(selfPreview.allowed());
    assertEquals("self-admin-role-removal-denied", selfPreview.message());
    var selfChange = assertThrows(AuthorizationException.class, () -> userAdmin.changeMemberRoles(tenantAdmin, "membership-admin", List.of(FoundationRole.TENANT_EMPLOYEE), "self demotion", "idem-self-role", "corr-self-role"));
    assertEquals("self-admin-role-removal-denied", selfChange.reasonCode());
  }

  @Test
  void resendProductionReadinessFailsWithoutResendConfigurationAndLocalCaptureIsSafe() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("email-key", "email@example.com"));
    var emailService = new ResendEmailService(Map.of(), (message, config) -> ResendEmailService.DeliveryResult.sent("should-not-send"));
    var message = invitationRepository.queuedEmails().get(0);

    var localResult = emailService.deliver(message, ResendEmailService.DeliveryMode.LOCAL_OR_TEST);
    assertTrue(localResult.success());
    assertTrue(localResult.providerMessageId().startsWith("captured-"));
    assertEquals(ResendEmailService.DeliveryKind.CAPTURED, localResult.kind());

    var delivered = invitations.recordDeliveryResult(invite.invitationId(), message.deliveryAttemptId(), localResult.success(), localResult.providerMessageId(), localResult.safeErrorSummary(), "corr-captured-delivery");
    assertEquals(EmailDeliveryStatus.CAPTURED, delivered.deliveryStatus());

    var prodResult = emailService.deliver(message, ResendEmailService.DeliveryMode.PRODUCTION);
    assertFalse(prodResult.success());
    assertEquals("resend-config-missing", prodResult.safeErrorSummary());
    assertEquals(invite.invitationId(), message.invitationId());
  }

  @Test
  void resendProductionAdapterBuildsAuthorizedRequestAndRecordsSentStatus() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("resend-success", "resend@example.com"));
    var message = invitationRepository.queuedEmails().get(0);
    var transport = new CapturingTransport(202, "{\"id\":\"email-resend-123\"}");
    var service = new ResendEmailService(
        Map.of(
            "RESEND_API_KEY", "re_test_secret",
            "RESEND_FROM_EMAIL", "Starter <onboarding@example.com>",
            "INVITE_EMAIL_SUBJECT", "Starter invitation",
            "RESEND_API_BASE_URL", "https://api.resend.com"),
        new ResendEmailService.ResendHttpEmailDeliveryAdapter(transport));

    var result = service.deliver(message, ResendEmailService.DeliveryMode.PRODUCTION);

    assertTrue(result.success());
    assertEquals("email-resend-123", result.providerMessageId());
    assertEquals(ResendEmailService.DeliveryKind.SENT, result.kind());
    assertEquals("https://api.resend.com/emails", transport.request.uri().toString());
    assertEquals("Bearer re_test_secret", transport.request.headers().firstValue("Authorization").orElseThrow());
    assertEquals("application/json", transport.request.headers().firstValue("Content-Type").orElseThrow());

    var delivered = invitations.recordDeliveryResult(invite.invitationId(), message.deliveryAttemptId(), result.success(), result.providerMessageId(), result.safeErrorSummary(), "corr-resend-delivery");
    assertEquals(EmailDeliveryStatus.SENT, delivered.deliveryStatus());
    assertTrue(delivered.providerMessageIds().contains("email-resend-123"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_DELIVERY_SENT")));
  }

  @Test
  void resendProductionAdapterMapsFailuresToSafeDeliveryStatus() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("resend-failure", "fail@example.com"));
    var message = invitationRepository.queuedEmails().get(0);
    var service = new ResendEmailService(
        Map.of("RESEND_API_KEY", "re_test_secret", "INVITE_EMAIL_FROM", "Starter <onboarding@example.com>"),
        new ResendEmailService.ResendHttpEmailDeliveryAdapter(new CapturingTransport(401, "{\"message\":\"bad key\"}")));

    var result = service.deliver(message, ResendEmailService.DeliveryMode.PRODUCTION);

    assertFalse(result.success());
    assertEquals("resend-http-401", result.safeErrorSummary());
    assertFalse(result.toString().contains("re_test_secret"));

    var failed = invitations.recordDeliveryResult(invite.invitationId(), message.deliveryAttemptId(), result.success(), result.providerMessageId(), result.safeErrorSummary(), "corr-resend-failure");
    assertEquals(EmailDeliveryStatus.FAILED, failed.deliveryStatus());
    assertEquals("resend-http-401", failed.lastDeliveryErrorSummary());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_DELIVERY_FAILED")));
  }

  private static final class CapturingTransport implements ResendEmailService.HttpTransport {
    private final int status;
    private final String body;
    private HttpRequest request;

    private CapturingTransport(int status, String body) {
      this.status = status;
      this.body = body;
    }

    @Override
    public ResendEmailService.HttpTransportResponse send(HttpRequest request) {
      this.request = request;
      return new ResendEmailService.HttpTransportResponse(status, body);
    }
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
    identityRepository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, "tenant-1", null, List.of(role), MembershipStatus.ACTIVE, false, null));
  }
}

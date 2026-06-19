package ai.first.application.coreapp.useradmin;

import ai.first.domain.foundation.audit.AdminAuditEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.email.EmailDeliveryStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.invitation.InvitationStatus;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ai.first.application.foundation.email.ResendEmailService;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.InMemoryTestIdentityRepository;
import ai.first.application.foundation.invitation.InvitationService;
import ai.first.application.foundation.invitation.InMemoryTestInvitationRepository;

class InvitationAndUserAdminServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-20T10:15:30Z"), ZoneOffset.UTC);
  private InMemoryTestIdentityRepository identityRepository;
  private InMemoryTestInvitationRepository invitationRepository;
  private AuthContextResolver resolver;
  private InvitationService invitations;
  private UserAdminService userAdmin;
  private AuthContextResolver.ResolvedMe tenantAdmin;

  @BeforeEach
  void setUp() {
    identityRepository = new InMemoryTestIdentityRepository();
    invitationRepository = new InMemoryTestInvitationRepository();
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
  void inviteCreationCanDeliverQueuedEmailThroughConfiguredDeliveryBoundary() {
    var deliveredInvitations = new InvitationService(
        identityRepository,
        invitationRepository,
        clock,
        null,
        null,
        new ResendEmailService(Map.of(), (message, config) -> ResendEmailService.DeliveryResult.sent("should-not-send")),
        ResendEmailService.DeliveryMode.LOCAL_OR_TEST);

    var invite = deliveredInvitations.createInvitation(tenantAdmin, inviteRequest("auto-delivery", "auto.user@example.com"));

    assertEquals(InvitationStatus.SENT, invite.status());
    assertEquals(EmailDeliveryStatus.CAPTURED, invite.deliveryStatus());
    assertEquals(1, invite.deliveryAttempts());
    assertEquals(List.of("captured-" + invite.invitationId() + ":delivery-1"), invite.providerMessageIds());
    assertEquals(1, invitationRepository.queuedEmails().size());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_DELIVERY_CAPTURED")));
  }

  @Test
  void inviteCreationUsesConfiguredResendProviderAndFailsClosedWhenProductionConfigMissing() {
    var configuredInvitations = new InvitationService(
        identityRepository,
        invitationRepository,
        clock,
        null,
        null,
        new ResendEmailService(
            Map.of("RESEND_API_KEY", "re_test_secret", "INVITE_EMAIL_FROM", "Starter <onboarding@example.com>"),
            (message, config) -> ResendEmailService.DeliveryResult.sent("resend-message-123")),
        ResendEmailService.DeliveryMode.PRODUCTION);

    var sent = configuredInvitations.createInvitation(tenantAdmin, inviteRequest("configured-provider", "configured.provider@example.com"));

    assertEquals(InvitationStatus.SENT, sent.status());
    assertEquals(EmailDeliveryStatus.SENT, sent.deliveryStatus());
    assertEquals(List.of("resend-message-123"), sent.providerMessageIds());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_DELIVERY_SENT")));

    var failClosedInvitations = new InvitationService(
        identityRepository,
        invitationRepository,
        clock,
        null,
        null,
        new ResendEmailService(Map.of(), (message, config) -> ResendEmailService.DeliveryResult.sent("should-not-send")),
        ResendEmailService.DeliveryMode.PRODUCTION);

    var blocked = failClosedInvitations.createInvitation(tenantAdmin, inviteRequest("missing-provider", "missing.provider@example.com"));

    assertEquals(InvitationStatus.DELIVERY_FAILED, blocked.status());
    assertEquals(EmailDeliveryStatus.FAILED, blocked.deliveryStatus());
    assertEquals("resend-config-missing", blocked.lastDeliveryErrorSummary());
    assertTrue(blocked.providerMessageIds().isEmpty());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_DELIVERY_FAILED") && event.reasonCode().equals("resend-config-missing")));
    assertFalse(blocked.toString().contains("should-not-send"));
  }

  @Test
  void inviteEmailLinkUsesConfiguredPublicBaseUrl() {
    var configuredInvitations = new InvitationService(
        identityRepository,
        invitationRepository,
        clock,
        null,
        null,
        null,
        null,
        "https://tenant.example.com/app/");

    var invite = configuredInvitations.createInvitation(tenantAdmin, inviteRequest("configured-url", "configured.url@example.com"));
    var inviteUrl = invitationRepository.email(invite.invitationId() + ":delivery-1").orElseThrow().inviteUrl();

    assertTrue(inviteUrl.startsWith("https://tenant.example.com/app/accept?token=invite-token-"));
    assertFalse(inviteUrl.contains("app.example.test"));
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

    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_ACCEPT") && event.result() == ai.first.domain.foundation.audit.AdminAuditEvent.Result.DENIED));
  }

  @Test
  void duplicateCreateResendRevokeAndExpiryAreIdempotentAndAudited() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("dup-key", "dupe@example.com"));
    var replay = invitations.createInvitation(tenantAdmin, inviteRequest("dup-key", "dupe@example.com"));
    assertEquals(invite.invitationId(), replay.invitationId());

    var resent = invitations.resend(tenantAdmin, invite.invitationId(), "resend-1", "repair failed delivery", "corr-resend");
    assertEquals(1, resent.resendCount());
    assertEquals(2, invitationRepository.queuedEmails().size());
    var replayedResend = invitations.resend(tenantAdmin, invite.invitationId(), "resend-1", "replay", "corr-resend-replay");
    assertEquals(1, replayedResend.resendCount());
    assertEquals(2, invitationRepository.queuedEmails().size());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_RESEND") && event.result() == ai.first.domain.foundation.audit.AdminAuditEvent.Result.NO_OP));

    var revoked = invitations.revoke(tenantAdmin, invite.invitationId(), "wrong recipient", "corr-revoke");
    assertEquals(InvitationStatus.REVOKED, revoked.status());
    assertEquals(InvitationStatus.REVOKED, invitations.expire(invite.invitationId(), "tenant-1", null, "corr-expire-late").status());
    assertThrows(AuthorizationException.class, () -> invitations.accept(new WorkosIdentity("workos-dupe", "dupe@example.com", "Dupe"), revoked.acceptanceContextId(), "corr-accept-revoked"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_REVOKE")));
  }

  @Test
  void deliveryFailedInvitationRequiresAdminResendBeforeAcceptance() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("failed-accept", "failed.accept@example.com"));
    var failed = invitations.recordDeliveryResult(invite.invitationId(), "delivery-1", false, null, "resend-http-401", "corr-delivery-failed");

    var browserResult = invitations.acceptForBrowser(
        new WorkosIdentity("workos-failed", "failed.accept@example.com", "Failed Accept"),
        new InvitationService.AcceptInvitationRequest(null, failed.acceptanceContextId()),
        "corr-failed-browser");

    assertEquals(InvitationStatus.DELIVERY_FAILED, failed.status());
    assertEquals("delivery-failed", browserResult.status());
    assertThrows(AuthorizationException.class, () -> invitations.accept(new WorkosIdentity("workos-failed", "failed.accept@example.com", "Failed Accept"), failed.acceptanceContextId(), "corr-failed-direct"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.reasonCode().equals("delivery-failed-without-override")));
  }

  @Test
  void duplicateAndObsoleteDeliveryResultsAreSafeNoOps() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("delivery-no-op", "delivery.noop@example.com"));
    var sent = invitations.recordDeliveryResult(invite.invitationId(), "delivery-1", true, "resend-idempotent-1", null, "corr-delivery-sent");
    var duplicate = invitations.recordDeliveryResult(invite.invitationId(), "delivery-1", true, "resend-idempotent-1", null, "corr-delivery-sent-replay");

    assertEquals(1, sent.deliveryAttempts());
    assertEquals(1, duplicate.deliveryAttempts());
    assertEquals(List.of("resend-idempotent-1"), duplicate.providerMessageIds());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_DELIVERY_NO_OP") && event.reasonCode().equals("idempotent-delivery-result")));

    var revoked = invitations.revoke(tenantAdmin, invite.invitationId(), "wrong recipient", "corr-revoke-before-obsolete-delivery");
    var obsolete = invitations.recordDeliveryResult(revoked.invitationId(), "delivery-late", true, "resend-late", null, "corr-late-delivery");

    assertEquals(InvitationStatus.REVOKED, obsolete.status());
    assertEquals(List.of("resend-idempotent-1"), obsolete.providerMessageIds());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_DELIVERY_NO_OP") && event.reasonCode().equals("terminal-or-obsolete")));
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
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("USERADMIN_UPDATE_MEMBER_STATUS") && event.result() == ai.first.domain.foundation.audit.AdminAuditEvent.Result.NO_OP));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.reasonCode().equals("self-disable-denied")));
  }

  @Test
  void identityRecoveryLifecycleIsDurableAuditedIdempotentAndProviderRedacted() {
    seedAdmin("member@example.com", "membership-member", FoundationRole.TENANT_EMPLOYEE);

    var requested = userAdmin.requestIdentityRelink(tenantAdmin, "member@example.com", "provider mismatch", "idem-identity-request", "corr-identity-request");
    assertEquals("approval-required", requested.status());
    assertEquals("needs-review", requested.lifecycleStatus());
    assertTrue(requested.redactions().contains("workos-subject-redacted"));
    assertFalse(requested.toString().contains("workos-member@example.com"));

    var duplicate = userAdmin.requestIdentityRelink(tenantAdmin, "member@example.com", "replay", "idem-identity-request-replay", "corr-identity-request-replay");
    assertEquals("no-op", duplicate.status());
    assertEquals(requested.recoveryId(), duplicate.recoveryId());

    var read = userAdmin.readIdentityRelink(tenantAdmin, requested.recoveryId(), "corr-identity-read");
    assertEquals("needs-review", read.lifecycleStatus());

    var approved = userAdmin.approveIdentityRelink(tenantAdmin, requested.recoveryId(), "reviewed evidence", "approval-123", "idem-identity-approve", "corr-identity-approve");
    assertEquals("approved-for-recovery", approved.status());
    assertEquals("approved-for-recovery", approved.lifecycleStatus());

    var completed = userAdmin.completeIdentityRelink(tenantAdmin, "member@example.com", "approval-123", "idem-identity-complete", "corr-identity-complete");
    assertEquals("accepted", completed.status());
    assertEquals("completed", completed.lifecycleStatus());
    assertEquals("RECOVERY_COMPLETED", identityRepository.findAccountByEmail("member@example.com").orElseThrow().identityLinkState());

    var completionReplay = userAdmin.completeIdentityRelink(tenantAdmin, "member@example.com", "approval-123", "idem-identity-complete-replay", "corr-identity-complete-replay");
    assertEquals("no-op", completionReplay.status());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("IDENTITY_RELINK_REQUEST") && event.result() == AdminAuditEvent.Result.ALLOWED));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("IDENTITY_RELINK_APPROVE") && event.result() == AdminAuditEvent.Result.ALLOWED));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("IDENTITY_RELINK_COMPLETE") && event.result() == AdminAuditEvent.Result.NO_OP));
  }

  @Test
  void identityRecoveryDeniesHiddenCrossScopeAndTerminalReplaysSafely() {
    identityRepository.putTenant(new Tenant("tenant-2", "Tenant Two", true));
    identityRepository.saveAccount(new Account("other@example.com", "workos-other", "other@example.com", "other@example.com", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("other@example.com", "other@example.com", "Other", null, null, null));
    identityRepository.putSettings(new UserSettings("other@example.com", UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership("membership-other", "other@example.com", ScopeType.TENANT, "tenant-2", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.ACTIVE, false, null));

    var hidden = assertThrows(AuthorizationException.class, () -> userAdmin.requestIdentityRelink(tenantAdmin, "other@example.com", "cross", "idem-hidden", "corr-hidden-identity"));
    assertEquals("target-not-found-or-forbidden", hidden.reasonCode());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("IDENTITY_RELINK_REQUEST") && event.result() == AdminAuditEvent.Result.DENIED && event.targetAccountId() == null));

    seedAdmin("denied@example.com", "membership-denied", FoundationRole.TENANT_EMPLOYEE);
    var requested = userAdmin.requestIdentityRelink(tenantAdmin, "denied@example.com", "provider stale", "idem-deny-request", "corr-deny-request");
    var denied = userAdmin.denyIdentityRelink(tenantAdmin, requested.recoveryId(), "risk too high", "idem-deny", "corr-deny");
    assertEquals("denied", denied.status());
    var deniedReplay = userAdmin.denyIdentityRelink(tenantAdmin, requested.recoveryId(), "risk too high", "idem-deny-replay", "corr-deny-replay");
    assertEquals("no-op", deniedReplay.status());
    assertThrows(AuthorizationException.class, () -> userAdmin.completeIdentityRelink(tenantAdmin, "denied@example.com", "approval-denied", "idem-complete-denied", "corr-complete-denied"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("IDENTITY_RELINK_DENY") && event.result() == AdminAuditEvent.Result.NO_OP));
  }

  @Test
  void userAdminPermanentRemoveRequiresDeactivatedUserAndEnforcesPurgeBlockers() {
    seedAdmin("second-admin@example.com", "membership-second-admin", FoundationRole.TENANT_ADMIN);
    seedAdmin("member@example.com", "membership-member", FoundationRole.TENANT_EMPLOYEE);

    var activeDenied = assertThrows(AuthorizationException.class, () -> userAdmin.permanentlyRemoveUser(tenantAdmin, "membership-member", "active purge", "idem-active-purge", "corr-active-purge"));
    assertTrue(activeDenied.reasonCode().contains("not-deactivated"));

    var deactivated = userAdmin.updateMemberStatus(tenantAdmin, "membership-member", MembershipStatus.REMOVED, "offboard", "idem-deactivate", "corr-deactivate");
    assertEquals(MembershipStatus.REMOVED, deactivated.membership().status());
    var purged = userAdmin.permanentlyRemoveUser(tenantAdmin, "membership-member", "purge", "idem-purge", "corr-purge");
    assertEquals("accepted", purged.status());
    assertTrue(identityRepository.findMembership("membership-member").isEmpty());
    assertTrue(identityRepository.findAccountByEmail("member@example.com").isEmpty());
    assertEquals(null, identityRepository.profile("member@example.com"));
    assertEquals(null, identityRepository.settings("member@example.com"));

    var selfDenied = assertThrows(AuthorizationException.class, () -> userAdmin.permanentlyRemoveUser(tenantAdmin, "membership-admin", "self purge", "idem-self-purge", "corr-self-purge"));
    assertTrue(selfDenied.reasonCode().contains("self-purge-denied"));

    identityRepository.putMembership(new Membership("membership-admin", "admin@example.com", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.ACTIVE, false, null));
    identityRepository.putMembership(new Membership("membership-second-admin", "second-admin@example.com", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN), MembershipStatus.REMOVED, false, null));
    var lastAdminDenied = assertThrows(AuthorizationException.class, () -> userAdmin.permanentlyRemoveUser(tenantAdmin, "membership-second-admin", "last admin purge", "idem-last-admin-purge", "corr-last-admin-purge"));
    assertTrue(lastAdminDenied.reasonCode().contains("last-admin-denied"));

    seedAdmin("held@example.com", "membership-held", FoundationRole.TENANT_EMPLOYEE);
    identityRepository.putMembership(new Membership("membership-held", "held@example.com", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.REMOVED, false, null));
    identityRepository.appendAudit(new AdminAuditEvent("audit-legal-hold", clock.instant(), "corr-legal-hold", "compliance@example.com", "membership-admin", ScopeType.TENANT, "tenant-1", null, "held@example.com", "membership-held", "LEGAL_HOLD_ACTIVE", AdminAuditEvent.Result.ALLOWED, "legal-hold", "legal-hold", "BROWSER_SAFE"));
    var legalHoldDenied = assertThrows(AuthorizationException.class, () -> userAdmin.permanentlyRemoveUser(tenantAdmin, "membership-held", "legal hold purge", "idem-legal-hold-purge", "corr-legal-hold-purge"));
    assertTrue(legalHoldDenied.reasonCode().contains("legal-hold"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("USERADMIN_PERMANENTLY_REMOVE_USER") && event.result() == AdminAuditEvent.Result.DENIED));
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

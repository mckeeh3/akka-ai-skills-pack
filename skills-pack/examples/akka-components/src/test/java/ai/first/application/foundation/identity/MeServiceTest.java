package ai.first.application.foundation.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MeServiceTest {
  private LocalDemoIdentityRepository repository;
  private MeService meService;
  private AuthContextResolver resolver;

  @BeforeEach
  void setUp() {
    repository = new LocalDemoIdentityRepository();
    resolver = new AuthContextResolver(repository);
    meService = new MeService(resolver);
    repository.putTenant(new Tenant("tenant-1", "Tenant One", true));
  }

  @Test
  void meLinksInvitedAccountAndReturnsBrowserSafeSelectedContext() {
    inviteTenantAdmin("admin@example.com", AccountStatus.INVITED, MembershipStatus.ACTIVE, "tenant-1");

    var response = meService.me(identity("workos-admin", "admin@example.com"), null, "corr-1");

    assertEquals("admin@example.com", response.account().accountId());
    assertEquals("active", response.account().status());
    assertEquals("tenant-1", response.selectedAuthContext().tenantId());
    assertEquals(List.of("tenant-admin"), response.selectedAuthContext().roleIds());
    assertEquals("membership-admin@example.com", response.authorityBasis().selectedContextId());
    assertEquals("active membership in selected context", response.authorityBasis().primaryRoleBasis());
    assertTrue(response.authorityBasis().myAccountCapabilityIds().contains("my_account.view_context"));
    assertTrue(response.contextCapabilityGroups().stream().anyMatch(group -> group.groupId().equals("my_account")));
    assertTrue(response.traceRefs().stream().anyMatch(trace -> trace.capabilityId().equals("core.access.me") && trace.correlationId().equals("corr-1")));
    assertTrue(response.traceRefs().stream().anyMatch(trace -> trace.capabilityId().equals("core.access.context.select") && trace.correlationId().equals("corr-1")));
    assertTrue(response.visibleCapabilityIds().contains("profile.read"));
    assertTrue(response.visibleCapabilityIds().contains("profile.update"));
    assertTrue(response.visibleCapabilityIds().contains("core.access.me"));
    assertTrue(response.visibleCapabilityIds().contains("core.profile.update"));
    assertTrue(response.visibleCapabilityIds().contains("core.access.context.select"));
    assertTrue(response.visibleCapabilityIds().contains("tenant.user.manage"));
    assertTrue(response.visibleCapabilityIds().contains("secure-tenant-user-foundation"));
    assertEquals(
        List.of("agent-my-account", "agent-user-admin", "agent-agent-admin", "agent-audit-trace", "agent-governance-policy"),
        response.functionalAgents().stream().map(MeResponse.FunctionalAgentSummary::functionalAgentId).toList());
    assertTrue(response.functionalAgents().stream().allMatch(agent -> agent.availability().equals("visible")));
    var userAdminAgent = response.functionalAgents().stream().filter(agent -> agent.functionalAgentId().equals("agent-user-admin")).findFirst().orElseThrow();
    assertTrue(response.visibleCapabilityIds().containsAll(userAdminAgent.requiredCapabilityIds()));
    assertTrue(response.functionalAgents().stream().allMatch(agent -> agent.defaultSurfaceType().equals("markdown_response")));
    assertEquals("Audit/Trace", response.functionalAgents().stream().filter(agent -> agent.functionalAgentId().equals("agent-audit-trace")).findFirst().orElseThrow().label());
    assertFalse(response.visibleCapabilityIds().contains("WORKOS_API_KEY"));
    assertEquals("corr-1", response.auditCorrelationId());
  }

  @Test
  void activeMemberKeepsMyAccountAvailableWithoutAdminWorkstreams() {
    inviteTenantMember("member@example.com", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, "tenant-1");

    var response = meService.me(identity("workos-member", "member@example.com"), null, "corr-member");

    assertTrue(response.visibleCapabilityIds().contains("profile.read"));
    assertTrue(response.visibleCapabilityIds().contains("profile.update"));
    var myAccount = response.functionalAgents().stream().filter(agent -> agent.functionalAgentId().equals("agent-my-account")).findFirst().orElseThrow();
    assertEquals("visible", myAccount.availability());
    assertTrue(response.visibleCapabilityIds().containsAll(myAccount.requiredCapabilityIds()));
    var userAdmin = response.functionalAgents().stream().filter(agent -> agent.functionalAgentId().equals("agent-user-admin")).findFirst().orElseThrow();
    assertEquals("denied", userAdmin.availability());
  }

  @Test
  void configuredBootstrapAdminLinksOnlyExplicitLocalAccount() {
    BootstrapAdminSeeder.seedConfiguredAdmins(
        repository,
        "owner@example.com:SAAS_OWNER_ADMIN:OWNER,tenant-admin@example.com:TENANT_ADMIN:tenant-starter");

    var owner = meService.me(identity("workos-owner", "owner@example.com"), null, "corr-owner-bootstrap");

    assertEquals("owner@example.com", owner.account().accountId());
    assertEquals("active", owner.account().status());
    assertTrue(owner.selectedAuthContext().roleIds().contains("saas-owner-admin"));
    assertEquals(null, owner.selectedAuthContext().tenantId());
    assertTrue(owner.visibleCapabilityIds().contains("saas_owner.user.manage"));
    assertEquals("workos-owner", repository.findAccountByEmail("owner@example.com").orElseThrow().workosUserId());

    var tenantAdmin = meService.me(identity("workos-tenant-admin", "tenant-admin@example.com"), null, "corr-tenant-bootstrap");
    assertEquals("tenant-starter", tenantAdmin.selectedAuthContext().tenantId());
    assertTrue(tenantAdmin.visibleCapabilityIds().contains("tenant.user.manage"));
    assertTrue(tenantAdmin.visibleCapabilityIds().contains("secure-tenant-user-foundation"));

    var unknown = assertThrows(
        AuthorizationException.class,
        () -> meService.me(identity("workos-unknown", "unknown@example.com"), null, "corr-unknown"));
    assertEquals("no-local-account-or-invitation", unknown.reasonCode());
  }

  @Test
  void configuredBootstrapAdminDoesNotOverwriteExistingProfileOrSettings() {
    BootstrapAdminSeeder.seedConfiguredAdmins(repository, "tenant-admin@example.com:TENANT_ADMIN:tenant-starter");
    repository.saveProfile(new UserProfile("tenant-admin@example.com", "tenant-admin@example.com", "Renamed Admin", null, null, null));
    repository.saveSettings(new UserSettings("tenant-admin@example.com", UserSettings.ThemeId.OBSIDIAN_DARK));

    BootstrapAdminSeeder.seedConfiguredAdmins(repository, "tenant-admin@example.com:TENANT_ADMIN:tenant-starter");

    assertEquals("Renamed Admin", repository.profile("tenant-admin@example.com").displayName());
    assertEquals(UserSettings.ThemeId.OBSIDIAN_DARK, repository.settings("tenant-admin@example.com").themeId());
  }

  @Test
  void bootstrapConfigRejectsInvalidScopes() {
    assertThrows(
        IllegalArgumentException.class,
        () -> BootstrapAdminSeeder.seedConfiguredAdmins(repository, "owner@example.com:SAAS_OWNER_ADMIN:tenant-starter"));
    assertThrows(
        IllegalArgumentException.class,
        () -> BootstrapAdminSeeder.seedConfiguredAdmins(repository, "owner@example.com:CUSTOMER_ADMIN:tenant-only"));
    assertThrows(
        IllegalArgumentException.class,
        () -> BootstrapAdminSeeder.seedConfiguredAdmins(repository, "member@example.com:TENANT_EMPLOYEE:tenant-starter"));
  }

  @Test
  void missingWorkosClaimsAreDeniedAndAudited() {
    var error =
        assertThrows(
            AuthorizationException.class,
            () -> meService.me(new WorkosIdentity("workos-missing-email", null, "Missing Email"), null, "corr-missing-claims"));

    assertEquals("missing-workos-claims", error.reasonCode());
    assertTrue(repository.auditEvents().stream().anyMatch(event ->
        event.actionType().equals("AUTH_CONTEXT_RESOLVE")
            && event.result().name().equals("DENIED")
            && event.reasonCode().equals("missing-workos-claims")
            && event.correlationId().equals("corr-missing-claims")));
  }

  @Test
  void disabledAccountIsDeniedAndAudited() {
    inviteTenantAdmin("disabled@example.com", AccountStatus.DISABLED, MembershipStatus.ACTIVE, "tenant-1");

    var error =
        assertThrows(
            AuthorizationException.class,
            () -> meService.me(identity("workos-disabled", "disabled@example.com"), null, "corr-disabled"));

    assertEquals("account-disabled", error.reasonCode());
    assertTrue(repository.auditEvents().stream().anyMatch(event -> event.result().name().equals("DENIED")));
  }

  @Test
  void accountWithoutActiveMembershipIsDenied() {
    inviteTenantAdmin("no-membership@example.com", AccountStatus.ACTIVE, MembershipStatus.SUSPENDED, "tenant-1");

    var error =
        assertThrows(
            AuthorizationException.class,
            () -> meService.me(identity("workos-no-membership", "no-membership@example.com"), null, "corr-no-membership"));

    assertEquals("no-active-membership", error.reasonCode());
  }

  @Test
  void selectedMembershipMustBelongToTheSignedInAccount() {
    inviteTenantAdmin("admin@example.com", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, "tenant-1");

    var error =
        assertThrows(
            AuthorizationException.class,
            () -> meService.me(identity("workos-admin", "admin@example.com"), "membership-other", "corr-forbidden"));

    assertEquals("selected-membership-forbidden", error.reasonCode());
  }

  @Test
  void tenantMismatchIsBackendEnforcedForProtectedCapabilities() {
    inviteTenantAdmin("admin@example.com", AccountStatus.ACTIVE, MembershipStatus.ACTIVE, "tenant-1");
    var response = meService.me(identity("workos-admin", "admin@example.com"), null, "corr-tenant");
    var authContext =
        resolver
            .resolveMe(identity("workos-admin", "admin@example.com"), response.selectedAuthContext().membershipId(), "corr-tenant-2")
            .selectedContext();

    resolver.requireTenant(authContext, "tenant-1");
    var error = assertThrows(AuthorizationException.class, () -> resolver.requireTenant(authContext, "tenant-2"));

    assertEquals("tenant-mismatch", error.reasonCode());
  }

  private void inviteTenantAdmin(
      String email, AccountStatus accountStatus, MembershipStatus membershipStatus, String tenantId) {
    inviteTenantMemberWithRoles(email, accountStatus, membershipStatus, tenantId, List.of(FoundationRole.TENANT_ADMIN));
  }

  private void inviteTenantMember(
      String email, AccountStatus accountStatus, MembershipStatus membershipStatus, String tenantId) {
    inviteTenantMemberWithRoles(email, accountStatus, membershipStatus, tenantId, List.of(FoundationRole.TENANT_EMPLOYEE));
  }

  private void inviteTenantMemberWithRoles(
      String email, AccountStatus accountStatus, MembershipStatus membershipStatus, String tenantId, List<FoundationRole> roles) {
    repository.saveAccount(
        new Account(email, null, email, email, accountStatus, accountStatus == AccountStatus.ACTIVE ? "LINKED" : "UNLINKED"));
    repository.putProfile(new UserProfile(email, email, "Starter User", "Starter", "User", null));
    repository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    repository.putMembership(
        new Membership(
            "membership-" + email,
            email,
            ScopeType.TENANT,
            tenantId,
            null,
            roles,
            membershipStatus,
            false,
            null));
  }

  private WorkosIdentity identity(String subject, String email) {
    return new WorkosIdentity(subject, email, "Starter Admin");
  }
}

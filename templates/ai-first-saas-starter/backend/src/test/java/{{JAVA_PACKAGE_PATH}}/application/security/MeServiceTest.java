package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MeServiceTest {
  private InMemoryIdentityRepository repository;
  private MeService meService;
  private AuthContextResolver resolver;

  @BeforeEach
  void setUp() {
    repository = new InMemoryIdentityRepository();
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
    assertTrue(response.visibleCapabilityIds().contains("profile.read"));
    assertTrue(response.visibleCapabilityIds().contains("profile.update"));
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
    BootstrapAdminSeeder.seedConfiguredAdmins(repository, "owner@example.com:TENANT_ADMIN:tenant-starter");

    var linked = meService.me(identity("workos-owner", "owner@example.com"), null, "corr-bootstrap");

    assertEquals("owner@example.com", linked.account().accountId());
    assertEquals("active", linked.account().status());
    assertEquals("tenant-starter", linked.selectedAuthContext().tenantId());
    assertTrue(linked.visibleCapabilityIds().contains("tenant.user.manage"));
    assertTrue(linked.visibleCapabilityIds().contains("secure-tenant-user-foundation"));
    assertEquals("workos-owner", repository.findAccountByEmail("owner@example.com").orElseThrow().workosUserId());

    var unknown = assertThrows(
        AuthorizationException.class,
        () -> meService.me(identity("workos-unknown", "unknown@example.com"), null, "corr-unknown"));
    assertEquals("no-local-account-or-invitation", unknown.reasonCode());
  }

  @Test
  void bootstrapConfigRejectsImplicitPrivilegeAndWrongScope() {
    assertThrows(
        IllegalArgumentException.class,
        () -> BootstrapAdminSeeder.seedConfiguredAdmins(repository, "owner@example.com:SAAS_OWNER_ADMIN:OWNER"));
    assertThrows(
        IllegalArgumentException.class,
        () -> BootstrapAdminSeeder.seedConfiguredAdmins(repository, "owner@example.com:TENANT_ADMIN:other-tenant"));
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
    repository.putSettings(new UserSettings(email, UserSettings.UiMode.LIGHT));
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

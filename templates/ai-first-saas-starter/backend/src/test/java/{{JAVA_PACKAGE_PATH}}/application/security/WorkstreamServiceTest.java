package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorSeedLoader;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeService;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.InMemoryAgentBehaviorRepository;
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
import java.time.Clock;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkstreamServiceTest {
  private WorkstreamService service;

  @BeforeEach
  void setUp() {
    var identityRepository = new InMemoryIdentityRepository();
    var invitationRepository = new InMemoryInvitationRepository();
    var resolver = new AuthContextResolver(identityRepository);
    var meService = new MeService(resolver);
    var userAdminService = new UserAdminService(identityRepository, Clock.systemUTC());
    var invitationService = new InvitationService(identityRepository, invitationRepository, Clock.systemUTC());
    var agentRepository = new InMemoryAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(agentRepository, Clock.systemUTC()).importStarterDefaults("tenant-1", "bootstrap", "corr-agent-seed");
    var agentRuntimeService = new AgentRuntimeService(agentRepository, resolver, Clock.systemUTC());
    service = new WorkstreamService(meService, resolver, new UserDirectoryView(userAdminService), new InvitationView(invitationService), userAdminService, invitationService, agentRepository, agentRuntimeService);

    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.saveAccount(new Account("admin@example.test", null, "admin@example.test", "admin@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("admin@example.test", "admin@example.test", "Tenant Admin", "Tenant", "Admin", null));
    identityRepository.putSettings(new UserSettings("admin@example.test", UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership("membership-admin", "admin@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR), MembershipStatus.ACTIVE, false, null));
    identityRepository.saveAccount(new Account("member@example.test", null, "member@example.test", "member@example.test", AccountStatus.ACTIVE, "UNLINKED"));
    identityRepository.putProfile(new UserProfile("member@example.test", "member@example.test", "Member User", "Member", "User", null));
    identityRepository.putSettings(new UserSettings("member@example.test", UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership("membership-member", "member@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.ACTIVE, false, null));
  }

  @Test
  void bootstrapReturnsUserAdminSurfacesWithoutSecrets() {
    var bootstrap = service.bootstrap(identity(), null, "corr-bootstrap");

    assertEquals("membership-admin", bootstrap.me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-user-admin") && agent.availability().equals("visible")));
    assertTrue(bootstrap.items().stream().anyMatch(item -> item.surfaceId().equals("surface-user-admin-dashboard")));
    assertTrue(bootstrap.surfaces().stream().anyMatch(surface -> surface.surfaceId().equals("surface-my-account-dashboard") && surface.ownerFunctionalAgentId().equals("agent-my-account")));
    assertTrue(bootstrap.surfaces().stream().anyMatch(surface -> surface.surfaceId().equals("surface-my-profile") && surface.ownerFunctionalAgentId().equals("agent-my-account")));
    assertTrue(bootstrap.surfaces().stream().anyMatch(surface -> surface.surfaceId().equals("surface-my-settings") && surface.ownerFunctionalAgentId().equals("agent-my-account")));
    assertTrue(bootstrap.surfaces().stream().anyMatch(surface -> surface.surfaceId().equals("surface-user-admin-list")));
    assertTrue(bootstrap.surfaces().stream().anyMatch(surface -> surface.surfaceId().equals("surface-audit-timeline") && surface.surfaceType().equals("audit-timeline")));
    assertTrue(bootstrap.surfaces().stream().anyMatch(surface -> surface.surfaceId().equals("surface-governance-policy") && surface.surfaceType().equals("governance-diff")));
    assertTrue(bootstrap.surfaces().stream().anyMatch(surface -> surface.surfaceId().equals("surface-agent-admin-catalog") && surface.surfaceType().equals("list-search")));
    assertTrue(bootstrap.surfaces().stream().anyMatch(surface -> surface.surfaceId().equals("surface-agent-test-console") && surface.surfaceType().equals("workflow-status")));
    assertTrue(bootstrap.surfaces().stream().flatMap(surface -> surface.actions().stream()).anyMatch(action -> action.actionId().equals("action-invite-user") && action.idempotency().required()));
    assertFalse(bootstrap.toString().contains("invite-token"));
    assertFalse(bootstrap.toString().contains("tokenHash"));
    assertFalse(bootstrap.toString().contains("providerSecret"));
  }

  @Test
  void actionDispatcherRequiresSelectedContextAndIdempotency() {
    var missingKey = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "secure-tenant-user-foundation", null, null, "membership-admin", "surface-user-admin-dashboard", "corr-invite"));

    assertEquals("validation-error", missingKey.status());

    var mismatch = assertThrows(AuthorizationException.class, () -> service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-user-list", "secure-tenant-user-foundation", null, null, "membership-other", "surface-user-admin-dashboard", "corr-forbidden")));
    assertEquals("CONTEXT_FORBIDDEN", mismatch.reasonCode());
  }

  @Test
  void realtimeEventsAreScopedAndResumeWithStaleFallback() {
    var events = service.events(identity(), "membership-admin", null, null, "corr-events");

    assertTrue(events.stream().anyMatch(event -> event.eventType().equals("surface.stale") && event.surfaceId().equals("surface-user-admin-dashboard")));
    assertTrue(events.stream().allMatch(event -> event.tenantId().equals("tenant-1")));

    var resumed = service.events(identity(), "membership-admin", null, "evt-audit-appended-002", "corr-events");
    assertTrue(resumed.stream().noneMatch(event -> event.eventId().equals("evt-audit-appended-002")));
    assertTrue(resumed.stream().anyMatch(event -> event.eventId().equals("evt-user-admin-stale-003")));

    var staleFallback = service.events(identity(), "membership-admin", null, "evt-missing", "corr-events");
    assertEquals("surface.stale", staleFallback.get(0).eventType());
  }

  @Test
  void agentAdminActionsCreateGovernedResultsAndTraces() {
    var promptProposal = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-propose-prompt-diff", "agent.prompts.govern", null, "idem-prompt", "membership-admin", "surface-agent-prompt-governance", "corr-prompt-ui"));
    assertEquals("accepted", promptProposal.status());
    assertEquals("surface-agent-prompt-governance", promptProposal.resultSurface().surfaceId());

    var testRun = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-test-agent-prompt", "agent.runtime.test", null, "idem-test", "membership-admin", "surface-agent-test-console", "corr-test-ui"));
    assertEquals("accepted", testRun.status());
    assertEquals("surface-agent-test-console", testRun.resultSurface().surfaceId());

    var approval = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-approve-skill-manifest", "agent.skills.govern", null, "idem-approval", "membership-admin", "surface-agent-skill-manifest-diff", "corr-approval-ui"));
    assertEquals("approval-required", approval.status());
    assertTrue(approval.message().contains("governed review gate"));
  }

  @Test
  void disabledSurfaceActionsReturnDenialResultSurface() {
    var result = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-replace-membership-role", "secure-tenant-user-foundation", null, "idem-1", "membership-admin", "surface-user-admin-detail-admin", "corr-role"));

    assertEquals("denied", result.status());
    assertTrue(result.message().contains("last tenant admin"));
    assertEquals("surface-user-admin-detail-admin", result.resultSurface().surfaceId());
  }

  private WorkosIdentity identity() {
    return new WorkosIdentity("workos-admin", "admin@example.test", "Tenant Admin");
  }
}

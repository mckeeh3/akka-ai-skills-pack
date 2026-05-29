package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeToolResolver.ResolveRuntimeToolsRequest;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthContextResolver;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthorizationException;
import {{JAVA_BASE_PACKAGE}}.application.security.InMemoryIdentityRepository;
import {{JAVA_BASE_PACKAGE}}.application.security.InMemoryInvitationRepository;
import {{JAVA_BASE_PACKAGE}}.application.security.InvitationService;
import {{JAVA_BASE_PACKAGE}}.application.security.InvitationView;
import {{JAVA_BASE_PACKAGE}}.application.security.UserAdminService;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.AuthContext;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgentRuntimeToolResolverTest {
  private InMemoryAgentBehaviorRepository repository;
  private AgentRuntimeToolResolver resolver;
  private AuthContext tenantAdmin;

  @BeforeEach
  void setUp() {
    repository = new InMemoryAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(repository, fixedClock()).importStarterDefaults("tenant-1", "bootstrap", "corr-seed");
    var runtimeService = new AgentRuntimeService(repository, new AuthContextResolver(new InMemoryIdentityRepository()), fixedClock());
    resolver = new AgentRuntimeToolResolver(repository, runtimeService);
    tenantAdmin = new AuthContext(
        "admin-1",
        "workos-admin-1",
        "membership-1",
        ScopeType.TENANT,
        "tenant-1",
        null,
        List.of(FoundationRole.TENANT_ADMIN),
        List.of("agent.user_admin.use", "agent.behavior.manage", "tenant.user.read", "tenant.audit.read"));
  }

  @Test
  void resolvesApprovedStableToolIdsIntoDeterministicRuntimeTools() {
    var resolved = resolver.resolve(runtimeToolsRequest("corr-tools-1"));

    assertEquals(List.of("readReferenceDoc", "readSkill", "userAdminEvidence.read"), resolved.grantedToolIds());
    assertTrue(resolved.deniedToolIds().isEmpty());
    assertEquals(3, resolved.entries().size());
    assertEquals(List.of("readReferenceDoc", "readSkill", "userAdminEvidence.read"), resolved.entries().stream().map(entry -> entry.toolId()).toList());
    assertEquals(2, resolved.runtimeTools().size());
    assertTrue(resolved.runtimeTools().stream().anyMatch(AgentRuntimeLoaderTools.class::isInstance));
    assertTrue(resolved.runtimeTools().stream().anyMatch(UserAdminEvidenceTools.class::isInstance));
    var binding = resolved.runtimeTools().stream().filter(AgentRuntimeLoaderTools.class::isInstance).map(AgentRuntimeLoaderTools.class::cast).findFirst().orElseThrow();
    assertTrue(binding.readSkill("ua.access-review-triage.v1").contains("authority_note=Skill content is internal guidance only"));
  }

  @Test
  void userAdminEvidenceToolReadsScopedRedactedEvidenceWithoutMutation() {
    var identityRepository = seededIdentityRepository();
    var userAdminService = new UserAdminService(identityRepository, fixedClock());
    var invitationService = new InvitationService(identityRepository, new InMemoryInvitationRepository(), fixedClock());
    var tool = new UserAdminEvidenceTools(identityRepository, userAdminService, new InvitationView(invitationService), tenantAdmin, "corr-evidence");

    var beforeRoles = identityRepository.findMembership("membership-member").orElseThrow().roles();
    var evidence = tool.read("summarize current tenantId=tenant-1 user admin evidence; no direct mutation");

    assertTrue(evidence.contains("tool_id=userAdminEvidence.read"));
    assertTrue(evidence.contains("mode=read_only_no_direct_mutation"));
    assertTrue(evidence.contains("memberCount=2"));
    assertTrue(evidence.contains("membership-member"));
    assertTrue(evidence.contains("trace-useradmin-evidence"));
    assertTrue(evidence.contains("authority_note=Evidence is scoped deterministic data only"));
    assertFalse(evidence.toLowerCase().contains("tokenhash"));
    assertFalse(evidence.toLowerCase().contains("invite-token"));
    assertFalse(evidence.toLowerCase().contains("providersecret"));
    assertEquals(beforeRoles, identityRepository.findMembership("membership-member").orElseThrow().roles(), "Evidence reads must not mutate roles or membership state");
  }

  @Test
  void userAdminEvidenceToolDeniesMissingCapabilityAndCrossTenantRequests() {
    var identityRepository = seededIdentityRepository();
    var userAdminService = new UserAdminService(identityRepository, fixedClock());
    var invitationService = new InvitationService(identityRepository, new InMemoryInvitationRepository(), fixedClock());
    var noReadCapability = new AuthContext("admin-1", "workos-admin-1", "membership-1", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN), List.of("agent.user_admin.use"));
    var deniedCapabilityTool = new UserAdminEvidenceTools(identityRepository, userAdminService, new InvitationView(invitationService), noReadCapability, "corr-evidence-denied");

    var missingCapability = assertThrows(AuthorizationException.class, () -> deniedCapabilityTool.read("summarize"));
    assertTrue(missingCapability.getMessage().contains("missing-capability:tenant.user.read"));

    var tool = new UserAdminEvidenceTools(identityRepository, userAdminService, new InvitationView(invitationService), tenantAdmin, "corr-evidence-cross-tenant");
    var crossTenant = assertThrows(AuthorizationException.class, () -> tool.read("tenantId=tenant-other"));
    assertTrue(crossTenant.getMessage().contains("evidence-tenant-mismatch"));
  }

  @Test
  void deniesByDefaultWhenRegistryBindingIsMissing() {
    var boundary = repository.toolBoundary("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_BOUNDARY_ID).orElseThrow();
    var unknownGrant = new ToolPermissionBoundary.ToolGrant("tenantAdmin.deleteUser", ToolPermissionBoundary.Category.LOCAL_FUNCTION, "tenant.user.delete", List.of("execute"), List.of("runtime"), "security", "approval_required", true, "full_work_trace");
    repository.saveToolBoundary(new ToolPermissionBoundary(boundary.tenantId(), boundary.boundaryId(), boundary.agentDefinitionId(), boundary.status(), boundary.boundaryVersion() + 1, List.of(unknownGrant), "unknown-tool", boundary.seedProvenance(), boundary.createdAt(), boundary.updatedAt()));

    var resolved = resolver.resolve(runtimeToolsRequest("corr-tools-missing"));

    assertTrue(resolved.runtimeTools().isEmpty());
    assertEquals(List.of("tenantAdmin.deleteUser"), resolved.deniedToolIds());
  }

  @Test
  void deniesRegistryEntryWhenGrantCategoryCapabilityOperationOrModeDoNotMatch() {
    var boundary = repository.toolBoundary("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_BOUNDARY_ID).orElseThrow();
    var wrongCapability = new ToolPermissionBoundary.ToolGrant("readSkill", ToolPermissionBoundary.Category.READ_SKILL, "tenant.admin.override", List.of("read"), List.of("runtime"), "none", "bounded_autonomous", false, "full_work_trace");
    var wrongCategory = new ToolPermissionBoundary.ToolGrant("readReferenceDoc", ToolPermissionBoundary.Category.READ_SKILL, "agent.references.read", List.of("read"), List.of("runtime"), "none", "bounded_autonomous", false, "full_work_trace");
    var wrongMode = new ToolPermissionBoundary.ToolGrant("readSkill", ToolPermissionBoundary.Category.READ_SKILL, "agent.skills.read", List.of("read"), List.of("evaluation"), "none", "bounded_autonomous", false, "full_work_trace");
    repository.saveToolBoundary(new ToolPermissionBoundary(boundary.tenantId(), boundary.boundaryId(), boundary.agentDefinitionId(), boundary.status(), boundary.boundaryVersion() + 1, List.of(wrongCapability, wrongCategory, wrongMode), "denied-tools", boundary.seedProvenance(), boundary.createdAt(), boundary.updatedAt()));

    var resolved = resolver.resolve(runtimeToolsRequest("corr-tools-denied"));

    assertTrue(resolved.runtimeTools().isEmpty());
    assertEquals(List.of("readReferenceDoc", "readSkill", "readSkill"), resolved.deniedToolIds());
  }

  @Test
  void failsClosedWhenBoundaryIsInactiveOrMismatched() {
    var boundary = repository.toolBoundary("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_BOUNDARY_ID).orElseThrow();
    repository.saveToolBoundary(new ToolPermissionBoundary(boundary.tenantId(), boundary.boundaryId(), boundary.agentDefinitionId(), AgentLifecycleStatus.DISABLED, boundary.boundaryVersion(), boundary.allowedToolGrants(), boundary.checksum(), boundary.seedProvenance(), boundary.createdAt(), boundary.updatedAt()));

    var disabled = assertThrows(AuthorizationException.class, () -> resolver.resolve(runtimeToolsRequest("corr-tools-disabled")));
    assertTrue(disabled.getMessage().contains("boundary-not-active"));

    repository.saveToolBoundary(new ToolPermissionBoundary(boundary.tenantId(), boundary.boundaryId(), "some-other-agent", AgentLifecycleStatus.ACTIVE, boundary.boundaryVersion(), boundary.allowedToolGrants(), boundary.checksum(), boundary.seedProvenance(), boundary.createdAt(), boundary.updatedAt()));
    var mismatched = assertThrows(AuthorizationException.class, () -> resolver.resolve(runtimeToolsRequest("corr-tools-mismatch")));
    assertTrue(mismatched.getMessage().contains("boundary-agent-mismatch"));
  }

  private ResolveRuntimeToolsRequest runtimeToolsRequest(String correlationId) {
    return new ResolveRuntimeToolsRequest("tenant-1", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, tenantAdmin, "runtime", AgentRuntimeService.INVOKE_CAPABILITY, correlationId);
  }

  private InMemoryIdentityRepository seededIdentityRepository() {
    var identityRepository = new InMemoryIdentityRepository();
    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.saveAccount(new Account("admin-1", "workos-admin-1", "admin@example.test", "admin@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("admin-1", "admin@example.test", "Tenant Admin", "Tenant", "Admin", null));
    identityRepository.putSettings(new UserSettings("admin-1", UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership("membership-1", "admin-1", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN), MembershipStatus.ACTIVE, false, null));
    identityRepository.saveAccount(new Account("member-1", null, "member@example.test", "member@example.test", AccountStatus.ACTIVE, "UNLINKED"));
    identityRepository.putProfile(new UserProfile("member-1", "member@example.test", "Member One", "Member", "One", null));
    identityRepository.putSettings(new UserSettings("member-1", UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership("membership-member", "member-1", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.ACTIVE, false, null));
    return identityRepository;
  }

  private Clock fixedClock() {
    return Clock.fixed(Instant.parse("2026-05-20T00:00:00Z"), ZoneOffset.UTC);
  }
}

package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeToolResolver.ResolveRuntimeToolsRequest;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthContextResolver;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthorizationException;
import {{JAVA_BASE_PACKAGE}}.application.security.GovernancePolicyService;
import {{JAVA_BASE_PACKAGE}}.application.security.InMemoryIdentityRepository;
import {{JAVA_BASE_PACKAGE}}.application.security.InMemoryInvitationRepository;
import {{JAVA_BASE_PACKAGE}}.application.security.InvitationService;
import {{JAVA_BASE_PACKAGE}}.application.security.InvitationView;
import {{JAVA_BASE_PACKAGE}}.application.security.StarterSecurityComponents;
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
        List.of("agent.user_admin.use", "agent_admin.submit_turn", "audit.trace.explain", "agent.behavior.manage", "tenant.user.read", "tenant.audit.read", "audit.trace.search", "audit.trace.detail.read", "audit.trace.timeline.read", "audit.trace.failureEvidence.read", "agent_admin.list_definitions", "governance.policy.read"));
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
  void agentAdminEvidenceToolReadsScopedRedactedEvidenceWithoutMutation() {
    var resolved = resolver.resolve(agentAdminRuntimeToolsRequest("corr-agent-admin-evidence-tools"));

    assertEquals(List.of("agentAdminEvidence.read", "readReferenceDoc", "readSkill"), resolved.grantedToolIds());
    assertTrue(resolved.runtimeTools().stream().anyMatch(AgentAdminEvidenceTools.class::isInstance));
    var tool = resolved.runtimeTools().stream().filter(AgentAdminEvidenceTools.class::isInstance).map(AgentAdminEvidenceTools.class::cast).findFirst().orElseThrow();
    var beforeBoundary = repository.toolBoundary("tenant-1", "tool-boundary-agent-admin").orElseThrow().checksum();

    var evidence = tool.read("summarize current tenantId=tenant-1 Agent Admin provider readiness, tool boundary, proposal, activate, rollback evidence; no direct mutation");

    assertTrue(evidence.contains("tool_id=agentAdminEvidence.read"));
    assertTrue(evidence.contains("mode=read_only_no_direct_mutation"));
    assertTrue(evidence.contains("agentDefinitionCount=5"));
    assertTrue(evidence.contains("agent-agent-admin"));
    assertTrue(evidence.contains("tool-boundary-agent-admin"));
    assertTrue(evidence.contains("providerReadiness=ready:openai-low-temperature:credentials-redacted"));
    assertTrue(evidence.contains("proposalGuidance=Guidance may draft rationale"));
    assertTrue(evidence.contains("authority_note=Evidence is scoped deterministic data only"));
    assertTrue(evidence.contains("trace-agentadmin-evidence"));
    assertFalse(evidence.toLowerCase().contains("api_key"));
    assertFalse(evidence.toLowerCase().contains("providersecret"));
    assertEquals(beforeBoundary, repository.toolBoundary("tenant-1", "tool-boundary-agent-admin").orElseThrow().checksum(), "Evidence reads must not mutate tool boundaries");
  }

  @Test
  void auditTraceEvidenceToolReadsScopedRedactedEvidenceWithoutMutation() {
    var resolved = resolver.resolve(auditTraceRuntimeToolsRequest("corr-audit-trace-evidence-tools"));

    assertEquals(List.of("auditTraceEvidence.read", "readReferenceDoc", "readSkill"), resolved.grantedToolIds());
    assertTrue(resolved.runtimeTools().stream().anyMatch(AuditTraceEvidenceTools.class::isInstance));
    var tool = resolved.runtimeTools().stream().filter(AuditTraceEvidenceTools.class::isInstance).map(AuditTraceEvidenceTools.class::cast).findFirst().orElseThrow();
    var beforeBoundary = repository.toolBoundary("tenant-1", "tool-boundary-audit-trace").orElseThrow().checksum();

    var evidence = tool.read("summarize current tenantId=tenant-1 filter=provider correlationId=corr-audit-trace-evidence-tools failureCategory=provider_blocked; no direct mutation");

    assertTrue(evidence.contains("tool_id=auditTraceEvidence.read"));
    assertTrue(evidence.contains("mode=read_only_no_direct_mutation"));
    assertTrue(evidence.contains("audit.trace.search.v1"));
    assertTrue(evidence.contains("audit.trace.detail.v1"));
    assertTrue(evidence.contains("audit.trace.timeline.v1"));
    assertTrue(evidence.contains("audit.trace.failureEvidence.v1"));
    assertTrue(evidence.contains("trace-audittrace-evidence"));
    assertTrue(evidence.contains("authority_note=Evidence is scoped deterministic data only"));
    assertFalse(evidence.toLowerCase().contains("api_key"));
    assertFalse(evidence.toLowerCase().contains("providersecret"));
    assertFalse(evidence.toLowerCase().contains("bearer abc"));
    assertEquals(beforeBoundary, repository.toolBoundary("tenant-1", "tool-boundary-audit-trace").orElseThrow().checksum(), "Evidence reads must not mutate tool boundaries");
  }

  @Test
  void governancePolicyEvidenceToolReadsScopedRedactedEvidenceWithoutMutation() {
    var resolved = resolver.resolve(governancePolicyRuntimeToolsRequest("corr-governance-policy-evidence-tools"));

    assertEquals(List.of("governancePolicyEvidence.read", "readReferenceDoc", "readSkill"), resolved.grantedToolIds());
    assertTrue(resolved.runtimeTools().stream().anyMatch(GovernancePolicyEvidenceTools.class::isInstance));
    var tool = resolved.runtimeTools().stream().filter(GovernancePolicyEvidenceTools.class::isInstance).map(GovernancePolicyEvidenceTools.class::cast).findFirst().orElseThrow();
    var beforeBoundary = repository.toolBoundary("tenant-1", "tool-boundary-governance-policy").orElseThrow().checksum();

    var evidence = tool.read("summarize current tenantId=tenant-1 Governance/Policy proposal simulation decision activation rollback provider system_message trace no direct mutation evidence");

    assertTrue(evidence.contains("tool_id=governancePolicyEvidence.read"));
    assertTrue(evidence.contains("capability=governance.policy.read"));
    assertTrue(evidence.contains("mode=read_only_no_direct_mutation"));
    assertTrue(evidence.contains("governance.policy.dashboard.v1"));
    assertTrue(evidence.contains("governance.policy.inventory.v1"));
    assertTrue(evidence.contains("governance.policy.detail.v1"));
    assertTrue(evidence.contains("ToolPermissionBoundary"));
    assertTrue(evidence.contains("PromptAssemblyTrace"));
    assertTrue(evidence.contains("AgentWorkTrace"));
    assertTrue(evidence.contains("provider/model readiness is enforced"));
    assertTrue(evidence.contains("authority_note=Evidence is scoped deterministic data only"));
    assertTrue(evidence.contains("trace-governancepolicy-evidence"));
    assertFalse(evidence.toLowerCase().contains("api_key"));
    assertFalse(evidence.toLowerCase().contains("providersecret"));
    assertFalse(evidence.toLowerCase().contains("hidden prompt text="));
    assertEquals(beforeBoundary, repository.toolBoundary("tenant-1", "tool-boundary-governance-policy").orElseThrow().checksum(), "Evidence reads must not mutate tool boundaries");
  }

  @Test
  void governancePolicyEvidenceToolDeniesMissingCapabilityAndCrossTenantRequests() {
    var noReadCapability = new AuthContext("admin-1", "workos-admin-1", "membership-1", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN), List.of("agent.skills.read"));
    var deniedCapabilityTool = new GovernancePolicyEvidenceTools(StarterSecurityComponents.governancePolicyService(), noReadCapability, "corr-governance-policy-evidence-denied");

    var missingCapability = assertThrows(AuthorizationException.class, () -> deniedCapabilityTool.read("summarize"));
    assertTrue(missingCapability.getMessage().contains("missing-capability:" + GovernancePolicyService.READ_CAPABILITY));

    var tool = new GovernancePolicyEvidenceTools(StarterSecurityComponents.governancePolicyService(), tenantAdmin, "corr-governance-policy-evidence-cross-tenant");
    var crossTenant = assertThrows(AuthorizationException.class, () -> tool.read("tenantId=tenant-other"));
    assertTrue(crossTenant.getMessage().contains("governance-policy-evidence-tenant-mismatch"));
  }

  @Test
  void auditTraceEvidenceToolDeniesMissingCapabilityAndCrossTenantRequests() {
    var noSearchCapability = new AuthContext("admin-1", "workos-admin-1", "membership-1", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN), List.of("audit.trace.explain"));
    var deniedCapabilityTool = new AuditTraceEvidenceTools(StarterSecurityComponents.auditTraceService(), noSearchCapability, "corr-audit-evidence-denied");

    var missingCapability = assertThrows(AuthorizationException.class, () -> deniedCapabilityTool.read("summarize"));
    assertTrue(missingCapability.getMessage().contains("missing-capability:audit.trace.search"));

    var tool = new AuditTraceEvidenceTools(StarterSecurityComponents.auditTraceService(), tenantAdmin, "corr-audit-evidence-cross-tenant");
    var crossTenant = assertThrows(AuthorizationException.class, () -> tool.read("tenantId=tenant-other"));
    assertTrue(crossTenant.getMessage().contains("audit-trace-evidence-tenant-mismatch"));
  }

  @Test
  void agentAdminEvidenceToolDeniesMissingCapabilityAndCrossTenantRequests() {
    var noReadCapability = new AuthContext("admin-1", "workos-admin-1", "membership-1", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN), List.of("agent_admin.submit_turn"));
    var deniedCapabilityTool = new AgentAdminEvidenceTools(repository, noReadCapability, "corr-agent-admin-evidence-denied");

    var missingCapability = assertThrows(AuthorizationException.class, () -> deniedCapabilityTool.read("summarize"));
    assertTrue(missingCapability.getMessage().contains("missing-capability:agent_admin.list_definitions"));

    var tool = new AgentAdminEvidenceTools(repository, tenantAdmin, "corr-agent-admin-evidence-cross-tenant");
    var crossTenant = assertThrows(AuthorizationException.class, () -> tool.read("tenantId=tenant-other"));
    assertTrue(crossTenant.getMessage().contains("agent-admin-evidence-tenant-mismatch"));
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

  private ResolveRuntimeToolsRequest agentAdminRuntimeToolsRequest(String correlationId) {
    return new ResolveRuntimeToolsRequest("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, tenantAdmin, "runtime", AgentRuntimeService.AGENT_ADMIN_INVOKE_CAPABILITY, correlationId);
  }

  private ResolveRuntimeToolsRequest auditTraceRuntimeToolsRequest(String correlationId) {
    return new ResolveRuntimeToolsRequest("tenant-1", AgentBehaviorSeedLoader.AUDIT_TRACE_AGENT_ID, tenantAdmin, "runtime", AgentRuntimeService.AUDIT_TRACE_INVOKE_CAPABILITY, correlationId);
  }

  private ResolveRuntimeToolsRequest governancePolicyRuntimeToolsRequest(String correlationId) {
    return new ResolveRuntimeToolsRequest("tenant-1", AgentBehaviorSeedLoader.GOVERNANCE_POLICY_AGENT_ID, tenantAdmin, "runtime", AgentRuntimeService.GOVERNANCE_POLICY_INVOKE_CAPABILITY, correlationId);
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

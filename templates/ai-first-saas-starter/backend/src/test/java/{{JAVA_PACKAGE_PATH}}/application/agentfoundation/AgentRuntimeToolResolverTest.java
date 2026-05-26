package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeToolResolver.ResolveRuntimeToolsRequest;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthContextResolver;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthorizationException;
import {{JAVA_BASE_PACKAGE}}.application.security.InMemoryIdentityRepository;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import {{JAVA_BASE_PACKAGE}}.domain.security.AuthContext;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
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

    assertEquals(List.of("readReferenceDoc", "readSkill"), resolved.grantedToolIds());
    assertEquals(List.of("userAdminEvidence.read"), resolved.deniedToolIds());
    assertEquals(2, resolved.entries().size());
    assertEquals(List.of("readReferenceDoc", "readSkill"), resolved.entries().stream().map(entry -> entry.toolId()).toList());
    assertEquals(1, resolved.runtimeTools().size());
    assertTrue(resolved.runtimeTools().stream().allMatch(AgentRuntimeLoaderTools.class::isInstance));
    var binding = (AgentRuntimeLoaderTools) resolved.runtimeTools().get(0);
    assertTrue(binding.readSkill("ua.access-review-triage.v1").contains("authority_note=Skill content is internal guidance only"));
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

  private Clock fixedClock() {
    return Clock.fixed(Instant.parse("2026-05-20T00:00:00Z"), ZoneOffset.UTC);
  }
}

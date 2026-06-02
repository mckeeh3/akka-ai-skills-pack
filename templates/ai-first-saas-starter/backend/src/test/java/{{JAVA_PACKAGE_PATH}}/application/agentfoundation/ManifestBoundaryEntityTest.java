package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.EventSourcedTestKit;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.GovernedArtifactLifecycleFact;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentReferenceManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentSkillManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class ManifestBoundaryEntityTest {
  private static final Instant NOW = Instant.parse("2026-05-20T00:00:00Z");

  @Test
  void skillManifestActiveLookupPreservesAssignedCompactEntries() {
    var manifest = skillManifest("tenant-1", "manifest-user-admin-skills", AgentLifecycleStatus.ACTIVE);
    var testKit = EventSourcedTestKit.of(
        AgentSkillManifestEntity.entityId(manifest.tenantId(), manifest.manifestId()),
        AgentSkillManifestEntity::new);

    var saved = testKit.method(AgentSkillManifestEntity::save).invoke(manifest);
    var active = testKit.method(AgentSkillManifestEntity::activeRuntimeLookup)
        .invoke(new AgentSkillManifestEntity.ManifestQuery("tenant-1", manifest.manifestId()));

    assertEquals(manifest, saved.getReply());
    assertEquals(manifest, saved.getNextEventOfType(AgentSkillManifestEntity.Event.SkillManifestSaved.class).manifest());
    assertEquals("ua.access-review-triage.v1", active.getReply().orElseThrow().entries().get(0).stableSkillId());
  }

  @Test
  void inactiveSkillManifestRemainsInspectableButDeniedForRuntimeLookup() {
    var manifest = skillManifest("tenant-1", "manifest-user-admin-skills", AgentLifecycleStatus.DISABLED);
    var testKit = EventSourcedTestKit.of(
        AgentSkillManifestEntity.entityId(manifest.tenantId(), manifest.manifestId()),
        AgentSkillManifestEntity::new);
    testKit.method(AgentSkillManifestEntity::save).invoke(manifest);

    var detail = testKit.method(AgentSkillManifestEntity::detail)
        .invoke(new AgentSkillManifestEntity.ManifestQuery("tenant-1", manifest.manifestId()));
    var active = testKit.method(AgentSkillManifestEntity::activeRuntimeLookup)
        .invoke(new AgentSkillManifestEntity.ManifestQuery("tenant-1", manifest.manifestId()));

    assertEquals(AgentLifecycleStatus.DISABLED, detail.getReply().orElseThrow().status());
    assertTrue(active.getReply().isEmpty());
  }

  @Test
  void referenceManifestLookupIsTenantIsolated() {
    var manifest = referenceManifest("tenant-1", "manifest-user-admin-references", AgentLifecycleStatus.ACTIVE);
    var testKit = EventSourcedTestKit.of(
        AgentReferenceManifestEntity.entityId(manifest.tenantId(), manifest.manifestId()),
        AgentReferenceManifestEntity::new);
    testKit.method(AgentReferenceManifestEntity::save).invoke(manifest);

    var wrongTenant = testKit.method(AgentReferenceManifestEntity::detail)
        .invoke(new AgentReferenceManifestEntity.ManifestQuery("tenant-2", manifest.manifestId()));
    var activeWrongTenant = testKit.method(AgentReferenceManifestEntity::activeRuntimeLookup)
        .invoke(new AgentReferenceManifestEntity.ManifestQuery("tenant-2", manifest.manifestId()));

    assertTrue(wrongTenant.getReply().isEmpty());
    assertTrue(activeWrongTenant.getReply().isEmpty());
  }

  @Test
  void toolBoundaryPreservesSeparateReadSkillAndReadReferenceGrants() {
    var boundary = boundary("tenant-1", "boundary-user-admin", List.of(readSkillGrant(), readReferenceGrant()), AgentLifecycleStatus.ACTIVE);
    var testKit = EventSourcedTestKit.of(
        ToolPermissionBoundaryEntity.entityId(boundary.tenantId(), boundary.boundaryId()),
        ToolPermissionBoundaryEntity::new);

    var saved = testKit.method(ToolPermissionBoundaryEntity::save).invoke(boundary);
    var active = testKit.method(ToolPermissionBoundaryEntity::activeRuntimeLookup)
        .invoke(new ToolPermissionBoundaryEntity.BoundaryQuery("tenant-1", boundary.boundaryId()));

    assertEquals(boundary, saved.getReply());
    assertTrue(active.getReply().orElseThrow().allowedToolGrants().stream()
        .anyMatch(grant -> grant.category() == ToolPermissionBoundary.Category.READ_SKILL));
    assertTrue(active.getReply().orElseThrow().allowedToolGrants().stream()
        .anyMatch(grant -> grant.category() == ToolPermissionBoundary.Category.READ_REFERENCE));
  }

  @Test
  void manifestAndToolBoundaryHistoryCarriesRollbackAndAuthorityExpansionDenialFacts() {
    var manifestV1 = skillManifest("tenant-1", "manifest-user-admin-skills", AgentLifecycleStatus.ACTIVE);
    var manifestV2 = new AgentSkillManifest(
        manifestV1.tenantId(),
        manifestV1.manifestId(),
        manifestV1.agentDefinitionId(),
        AgentLifecycleStatus.ACTIVE,
        2,
        manifestV1.entries(),
        "skill-manifest-checksum-v2",
        null,
        NOW,
        NOW);
    var manifestKit = EventSourcedTestKit.of(
        AgentSkillManifestEntity.entityId(manifestV1.tenantId(), manifestV1.manifestId()),
        AgentSkillManifestEntity::new);
    manifestKit.method(AgentSkillManifestEntity::save).invoke(manifestV1);
    manifestKit.method(AgentSkillManifestEntity::save).invoke(manifestV2);
    manifestKit.method(AgentSkillManifestEntity::appendLifecycleFact).invoke(GovernedArtifactLifecycleFact.of(
        "tenant-1",
        GovernedArtifactLifecycleFact.ArtifactType.AGENT_SKILL_MANIFEST,
        manifestV1.manifestId(),
        manifestV1.agentDefinitionId(),
        GovernedArtifactLifecycleFact.Transition.ROLLED_BACK,
        AgentLifecycleStatus.ACTIVE,
        AgentLifecycleStatus.ACTIVE,
        2,
        1,
        manifestV1.manifestId() + ":v1",
        manifestV1.compactManifestChecksum(),
        "admin-1",
        "corr-manifest-rollback",
        "rollback manifest assignment after review",
        false,
        NOW));

    var boundary = boundary("tenant-1", "boundary-user-admin", List.of(readSkillGrant()), AgentLifecycleStatus.ACTIVE);
    var boundaryKit = EventSourcedTestKit.of(
        ToolPermissionBoundaryEntity.entityId(boundary.tenantId(), boundary.boundaryId()),
        ToolPermissionBoundaryEntity::new);
    boundaryKit.method(ToolPermissionBoundaryEntity::save).invoke(boundary);
    boundaryKit.method(ToolPermissionBoundaryEntity::appendLifecycleFact).invoke(GovernedArtifactLifecycleFact.of(
        "tenant-1",
        GovernedArtifactLifecycleFact.ArtifactType.TOOL_PERMISSION_BOUNDARY,
        boundary.boundaryId(),
        boundary.agentDefinitionId(),
        GovernedArtifactLifecycleFact.Transition.DENIED,
        AgentLifecycleStatus.ACTIVE,
        AgentLifecycleStatus.ACTIVE,
        1,
        1,
        boundary.boundaryId() + ":v1",
        boundary.checksum(),
        "admin-1",
        "corr-boundary-denial",
        "request attempted external-call authority expansion",
        true,
        NOW));

    var manifestHistory = manifestKit.method(AgentSkillManifestEntity::history)
        .invoke(new AgentSkillManifestEntity.ManifestQuery("tenant-1", manifestV1.manifestId()));
    var boundaryHistory = boundaryKit.method(ToolPermissionBoundaryEntity::history)
        .invoke(new ToolPermissionBoundaryEntity.BoundaryQuery("tenant-1", boundary.boundaryId()));

    assertEquals(GovernedArtifactLifecycleFact.Transition.ROLLED_BACK, manifestHistory.getReply().get(2).transition());
    assertEquals("skill-manifest-checksum-v2", manifestHistory.getReply().get(1).checksum());
    assertEquals(GovernedArtifactLifecycleFact.Transition.DENIED, boundaryHistory.getReply().get(1).transition());
    assertTrue(boundaryHistory.getReply().get(1).authorityExpansionDenied());
  }

  @Test
  void boundaryValidationRequiresGrantMetadataAndRejectsUnapprovedAuthorityExpansion() {
    var missingCapability = boundary("tenant-1", "boundary-bad", List.of(new ToolPermissionBoundary.ToolGrant(
        "readSkill", ToolPermissionBoundary.Category.READ_SKILL, "", List.of("read"), List.of("runtime"), "none", "bounded_autonomous", false, "access")), AgentLifecycleStatus.ACTIVE);
    var testKit = EventSourcedTestKit.of(
        ToolPermissionBoundaryEntity.entityId(missingCapability.tenantId(), missingCapability.boundaryId()),
        ToolPermissionBoundaryEntity::new);

    var missingCapabilityResult = testKit.method(ToolPermissionBoundaryEntity::save).invoke(missingCapability);
    var unsafeGrant = new ToolPermissionBoundary.ToolGrant(
        "email.send", ToolPermissionBoundary.Category.EXTERNAL_SIDE_EFFECT, "tenant.email.send", List.of("execute"), List.of("runtime"), "external_call", "bounded_autonomous", true, "full_work_trace");
    var authorityExpansion = testKit.method(ToolPermissionBoundaryEntity::save)
        .invoke(boundary("tenant-1", "boundary-bad", List.of(unsafeGrant), AgentLifecycleStatus.ACTIVE));

    assertTrue(missingCapabilityResult.isError());
    assertEquals("capability-id-required", missingCapabilityResult.getError());
    assertFalse(missingCapabilityResult.didPersistEvents());
    assertTrue(authorityExpansion.isError());
    assertEquals("authority expansion requires approval-required/proposal-only grant before activation", authorityExpansion.getError());
    assertFalse(authorityExpansion.didPersistEvents());
  }

  static AgentSkillManifest skillManifest(String tenantId, String manifestId, AgentLifecycleStatus status) {
    return new AgentSkillManifest(
        tenantId,
        manifestId,
        "agent-user-admin",
        status,
        1,
        List.of(new AgentSkillManifest.Entry(
            "ua.access-review-triage.v1",
            "skill-access-review",
            1,
            "Access Review Triage",
            "Review access risk signals.",
            "Use for access review requests.")),
        "skill-manifest-checksum",
        null,
        NOW,
        NOW);
  }

  static AgentReferenceManifest referenceManifest(String tenantId, String manifestId, AgentLifecycleStatus status) {
    return new AgentReferenceManifest(
        tenantId,
        manifestId,
        "agent-user-admin",
        "bundle-user-admin",
        status,
        1,
        List.of(new AgentReferenceManifest.Entry(
            "ua.access-review-policy.v1",
            "ref-access-review-policy",
            1,
            "Access Review Policy",
            "Tenant access review policy.",
            "Consult before recommending access changes.",
            "consult",
            "internal")),
        "reference-manifest-checksum",
        null,
        NOW,
        NOW);
  }

  static ToolPermissionBoundary boundary(String tenantId, String boundaryId, List<ToolPermissionBoundary.ToolGrant> grants, AgentLifecycleStatus status) {
    return new ToolPermissionBoundary(
        tenantId,
        boundaryId,
        "agent-user-admin",
        status,
        1,
        grants,
        "boundary-checksum",
        null,
        NOW,
        NOW);
  }

  static ToolPermissionBoundary.ToolGrant readSkillGrant() {
    return new ToolPermissionBoundary.ToolGrant(
        "readSkill",
        ToolPermissionBoundary.Category.READ_SKILL,
        "agent.read_skill",
        List.of("read"),
        List.of("runtime", "test"),
        "none",
        "bounded_autonomous",
        false,
        "access");
  }

  static ToolPermissionBoundary.ToolGrant readReferenceGrant() {
    return new ToolPermissionBoundary.ToolGrant(
        "readReferenceDoc",
        ToolPermissionBoundary.Category.READ_REFERENCE,
        "agent.read_reference",
        List.of("read"),
        List.of("runtime", "test"),
        "none",
        "bounded_autonomous",
        false,
        "access");
  }
}

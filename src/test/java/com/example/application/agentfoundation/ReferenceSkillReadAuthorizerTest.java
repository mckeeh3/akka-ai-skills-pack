package com.example.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.domain.agentfoundation.ReferenceAgentDefinition;
import com.example.domain.agentfoundation.ReferenceAgentSkillManifest;
import com.example.domain.agentfoundation.ReferencePromptVersion;
import com.example.domain.agentfoundation.ReferenceResolvedAgentRuntime;
import com.example.domain.agentfoundation.ReferenceSkillDocument;
import com.example.domain.agentfoundation.ReferenceSkillVersion;
import com.example.domain.agentfoundation.ReferenceToolPermissionBoundary;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ReferenceSkillReadAuthorizerTest {

  @Test
  void assignedActiveSkillCanBeLoadedAndTraced() {
    var traceSink = new ReferenceTraceSink();
    var runtime = resolvedRuntime(traceSink);
    var authorizer = authorizer(traceSink);

    var result =
        authorizer.readSkill(runtime, ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID);

    assertTrue(result.allowed());
    assertEquals("skill-version-active", result.skillVersionId());
    assertEquals(
        ReferenceAgentFoundationFixtures.activeAssignedSkillVersion().content(), result.content());
    assertEquals(1, traceSink.skillLoadTraces().size());
    assertTrue(traceSink.skillLoadTraces().getFirst().allowed());
    assertEquals("allowed", traceSink.skillLoadTraces().getFirst().decisionReason());
    assertEquals("corr-skill", traceSink.skillLoadTraces().getFirst().correlationId());
  }

  @Test
  void readSkillToolWrapperReturnsGuidanceOnlyAuthorityNote() {
    var traceSink = new ReferenceTraceSink();
    var tools = new ReferenceAgentSkillTools(resolvedRuntime(traceSink), authorizer(traceSink));

    var content = tools.readSkill(ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID);

    assertTrue(content.contains("Authority: guidance only"));
    assertTrue(content.contains(ReferenceAgentFoundationFixtures.activeAssignedSkillVersion().content()));
    assertTrue(traceSink.skillLoadTraces().getFirst().allowed());
  }

  @Test
  void unassignedSkillIsDeniedWithSafeStringAndTrace() {
    var traceSink = new ReferenceTraceSink();
    var authorizer = authorizer(traceSink);

    var result =
        authorizer.readSkill(resolvedRuntime(traceSink), ReferenceAgentFoundationFixtures.UNASSIGNED_SKILL_ID);

    assertFalse(result.allowed());
    assertEquals(ReferenceSkillReadAuthorizer.SAFE_DENIAL, result.content());
    assertEquals("skill not assigned", traceSink.skillLoadTraces().getFirst().decisionReason());
    assertFalse(traceSink.skillLoadTraces().getFirst().allowed());
  }

  @Test
  void inactiveSkillVersionIsDeniedWithSafeStringAndTrace() {
    var traceSink = new ReferenceTraceSink();
    var archivedVersion =
        new ReferenceSkillVersion(
            ReferenceAgentFoundationFixtures.TENANT_ID,
            ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID,
            "skill-version-active",
            ReferenceSkillVersion.VersionStatus.ARCHIVED,
            "Archived content must not load.",
            "archived-checksum");
    var authorizer =
        new ReferenceSkillReadAuthorizer(
            skillDocuments(), Map.of("skill-version-active", archivedVersion), traceSink);

    var result =
        authorizer.readSkill(resolvedRuntime(traceSink), ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID);

    assertFalse(result.allowed());
    assertEquals(ReferenceSkillReadAuthorizer.SAFE_DENIAL, result.content());
    assertEquals("skill version unavailable", traceSink.skillLoadTraces().getFirst().decisionReason());
  }

  @Test
  void crossTenantSkillDocumentIsDeniedWithoutLeakingExistence() {
    var traceSink = new ReferenceTraceSink();
    var crossTenantDocument =
        new ReferenceSkillDocument(
            ReferenceAgentFoundationFixtures.OTHER_TENANT_ID,
            ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID,
            "Cross Tenant Skill",
            "skill-version-active",
            true);
    var authorizer =
        new ReferenceSkillReadAuthorizer(
            Map.of(ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID, crossTenantDocument),
            skillVersions(),
            traceSink);

    var result =
        authorizer.readSkill(resolvedRuntime(traceSink), ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID);

    assertFalse(result.allowed());
    assertEquals(ReferenceSkillReadAuthorizer.SAFE_DENIAL, result.content());
    assertEquals("skill document unavailable", traceSink.skillLoadTraces().getFirst().decisionReason());
    assertEquals("", traceSink.skillLoadTraces().getFirst().resolvedSkillVersionId());
  }

  @Test
  void missingReadSkillToolGrantIsDeniedAndTraced() {
    var traceSink = new ReferenceTraceSink();
    var boundaryWithoutReadSkill =
        new ReferenceToolPermissionBoundary(
            ReferenceAgentFoundationFixtures.TENANT_ID,
            ReferenceAgentFoundationFixtures.TOOL_BOUNDARY_ID,
            "tool-boundary-version-no-read-skill",
            ReferenceAgentFoundationFixtures.AGENT_ID,
            Set.of(),
            Set.of(ReferenceAgentFoundationFixtures.RUNTIME_MODE),
            true);
    var runtime =
        new ReferenceResolvedAgentRuntime(
            true,
            "",
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.activeAgent(),
            ReferenceAgentFoundationFixtures.activePromptVersion(),
            ReferenceAgentFoundationFixtures.activeManifest(),
            boundaryWithoutReadSkill,
            "assembled prompt",
            "checksum",
            "corr-no-tool");

    var result =
        authorizer(traceSink).readSkill(runtime, ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID);

    assertFalse(result.allowed());
    assertEquals(ReferenceSkillReadAuthorizer.SAFE_DENIAL, result.content());
    assertEquals("readSkill tool not granted", traceSink.skillLoadTraces().getFirst().decisionReason());
    assertEquals(
        ReferenceAgentFoundationFixtures.TOOL_BOUNDARY_ID,
        traceSink.skillLoadTraces().getFirst().toolBoundaryId());
  }

  @Test
  void deniedResolvedRuntimeCannotLoadSkills() {
    var traceSink = new ReferenceTraceSink();
    var deniedRuntime =
        ReferenceResolvedAgentRuntime.denied(
            "agent is not active for runtime",
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.disabledAgent(),
            "corr-runtime-denied");

    var result =
        authorizer(traceSink).readSkill(deniedRuntime, ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID);

    assertFalse(result.allowed());
    assertEquals(ReferenceSkillReadAuthorizer.SAFE_DENIAL, result.content());
    assertEquals("runtime denied", traceSink.skillLoadTraces().getFirst().decisionReason());
    assertEquals("corr-runtime-denied", traceSink.skillLoadTraces().getFirst().correlationId());
  }

  private static ReferenceResolvedAgentRuntime resolvedRuntime(ReferenceTraceSink traceSink) {
    var resolver =
        new ReferenceAgentRuntimeResolver(
            agentDefinitions(),
            promptVersions(),
            manifests(),
            toolBoundaries(),
            new ReferencePromptAssembler(),
            traceSink);
    return resolver.resolve(
        ReferenceAgentFoundationFixtures.authContext(),
        ReferenceAgentFoundationFixtures.AGENT_ID,
        "corr-skill");
  }

  private static ReferenceSkillReadAuthorizer authorizer(ReferenceTraceSink traceSink) {
    return new ReferenceSkillReadAuthorizer(skillDocuments(), skillVersions(), traceSink);
  }

  private static Map<String, ReferenceAgentDefinition> agentDefinitions() {
    return Map.of(
        ReferenceAgentFoundationFixtures.AGENT_ID,
        ReferenceAgentFoundationFixtures.activeAgent(),
        ReferenceAgentFoundationFixtures.DISABLED_AGENT_ID,
        ReferenceAgentFoundationFixtures.disabledAgent());
  }

  private static Map<String, ReferencePromptVersion> promptVersions() {
    return Map.of(
        ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID,
        ReferenceAgentFoundationFixtures.activePromptVersion());
  }

  private static Map<String, ReferenceAgentSkillManifest> manifests() {
    return Map.of(
        ReferenceAgentFoundationFixtures.SKILL_MANIFEST_ID,
        ReferenceAgentFoundationFixtures.activeManifest());
  }

  private static Map<String, ReferenceToolPermissionBoundary> toolBoundaries() {
    return Map.of(
        ReferenceAgentFoundationFixtures.TOOL_BOUNDARY_ID,
        ReferenceAgentFoundationFixtures.activeToolBoundary());
  }

  private static Map<String, ReferenceSkillDocument> skillDocuments() {
    return Map.of(
        ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID,
        ReferenceAgentFoundationFixtures.activeAssignedSkillDocument(),
        ReferenceAgentFoundationFixtures.UNASSIGNED_SKILL_ID,
        ReferenceAgentFoundationFixtures.unassignedSkillDocument());
  }

  private static Map<String, ReferenceSkillVersion> skillVersions() {
    return Map.of(
        "skill-version-active",
        ReferenceAgentFoundationFixtures.activeAssignedSkillVersion(),
        "skill-version-unassigned",
        ReferenceAgentFoundationFixtures.unassignedSkillVersion());
  }
}

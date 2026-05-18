package com.example.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.domain.agentfoundation.ReferenceAgentDefinition;
import com.example.domain.agentfoundation.ReferenceAgentSkillManifest;
import com.example.domain.agentfoundation.ReferencePromptVersion;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReferenceAgentRuntimeResolverTest {

  @Test
  void activeProfileResolvesAndAssemblesCompactManifestOnly() {
    var traceSink = new ReferenceTraceSink();
    var resolver = resolver(traceSink);

    var resolved =
        resolver.resolve(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.AGENT_ID,
            "corr-active");

    assertTrue(resolved.allowed());
    assertEquals("corr-active", resolved.correlationId());
    assertTrue(resolved.assembledPrompt().contains("AgentSkillManifest compact"));
    assertTrue(
        resolved.assembledPrompt().contains(ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID));
    assertFalse(
        resolved
            .assembledPrompt()
            .contains(ReferenceAgentFoundationFixtures.activeAssignedSkillVersion().content()));
    assertEquals(resolved.assembledPromptChecksum(), traceSink.promptAssemblyTraces().getFirst().assembledPromptChecksum());
    assertTrue(traceSink.promptAssemblyTraces().getFirst().allowed());
  }

  @Test
  void disabledAgentIsDeniedBeforeModelInvocation() {
    var traceSink = new ReferenceTraceSink();
    var resolver = resolver(traceSink);

    var resolved =
        resolver.resolve(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.DISABLED_AGENT_ID,
            "corr-disabled");

    assertFalse(resolved.allowed());
    assertEquals("agent is not active for runtime", resolved.denialReason());
    assertEquals("", resolved.assembledPrompt());
    assertFalse(traceSink.promptAssemblyTraces().getFirst().allowed());
    assertEquals("corr-disabled", traceSink.promptAssemblyTraces().getFirst().correlationId());
  }

  @Test
  void crossTenantPromptIsDeniedWithSafeTrace() {
    var traceSink = new ReferenceTraceSink();
    var crossTenantPrompt =
        new ReferencePromptVersion(
            ReferenceAgentFoundationFixtures.OTHER_TENANT_ID,
            ReferenceAgentFoundationFixtures.PROMPT_DOCUMENT_ID,
            ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID,
            ReferencePromptVersion.VersionStatus.ACTIVE,
            "Cross tenant prompt must never assemble.",
            "cross-tenant-checksum");
    var resolver =
        resolver(
            traceSink,
            Map.of(
                ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID,
                crossTenantPrompt),
            manifests(),
            toolBoundaries());

    var resolved =
        resolver.resolve(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.AGENT_ID,
            "corr-cross-prompt");

    assertFalse(resolved.allowed());
    assertEquals("active prompt denied", resolved.denialReason());
    assertFalse(traceSink.promptAssemblyTraces().getFirst().allowed());
    assertEquals("active prompt denied", traceSink.promptAssemblyTraces().getFirst().decisionReason());
  }

  @Test
  void crossTenantManifestIsDeniedWithSafeTrace() {
    var traceSink = new ReferenceTraceSink();
    var activeManifest = ReferenceAgentFoundationFixtures.activeManifest();
    var crossTenantManifest =
        new ReferenceAgentSkillManifest(
            ReferenceAgentFoundationFixtures.OTHER_TENANT_ID,
            activeManifest.skillManifestId(),
            activeManifest.skillManifestVersionId(),
            activeManifest.agentDefinitionId(),
            activeManifest.assignedSkillIds(),
            activeManifest.skillVersionRefs(),
            true);
    var resolver =
        resolver(
            traceSink,
            promptVersions(),
            Map.of(ReferenceAgentFoundationFixtures.SKILL_MANIFEST_ID, crossTenantManifest),
            toolBoundaries());

    var resolved =
        resolver.resolve(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.AGENT_ID,
            "corr-cross-manifest");

    assertFalse(resolved.allowed());
    assertEquals("active manifest denied", resolved.denialReason());
    assertFalse(traceSink.promptAssemblyTraces().getFirst().allowed());
    assertEquals("active manifest denied", traceSink.promptAssemblyTraces().getFirst().decisionReason());
  }

  @Test
  void promptChecksumChangesWhenPromptInputChanges() {
    var assembler = new ReferencePromptAssembler();
    var auth = ReferenceAgentFoundationFixtures.authContext();
    var agent = ReferenceAgentFoundationFixtures.activeAgent();
    var manifest = ReferenceAgentFoundationFixtures.activeManifest();
    var boundary = ReferenceAgentFoundationFixtures.activeToolBoundary();

    var first =
        assembler.assemble(
            auth, agent, ReferenceAgentFoundationFixtures.activePromptVersion(), manifest, boundary);
    var changedPrompt =
        new ReferencePromptVersion(
            ReferenceAgentFoundationFixtures.TENANT_ID,
            ReferenceAgentFoundationFixtures.PROMPT_DOCUMENT_ID,
            ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID,
            ReferencePromptVersion.VersionStatus.ACTIVE,
            "Changed governed prompt content.",
            "changed-checksum");
    var second = assembler.assemble(auth, agent, changedPrompt, manifest, boundary);

    assertNotEquals(assembler.checksum(first), assembler.checksum(second));
  }

  private static ReferenceAgentRuntimeResolver resolver(ReferenceTraceSink traceSink) {
    return resolver(traceSink, promptVersions(), manifests(), toolBoundaries());
  }

  private static ReferenceAgentRuntimeResolver resolver(
      ReferenceTraceSink traceSink,
      Map<String, ReferencePromptVersion> promptVersions,
      Map<String, ReferenceAgentSkillManifest> manifests,
      Map<String, com.example.domain.agentfoundation.ReferenceToolPermissionBoundary> toolBoundaries) {
    return new ReferenceAgentRuntimeResolver(
        agentDefinitions(),
        promptVersions,
        manifests,
        toolBoundaries,
        new ReferencePromptAssembler(),
        traceSink);
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

  private static Map<String, com.example.domain.agentfoundation.ReferenceToolPermissionBoundary> toolBoundaries() {
    return Map.of(
        ReferenceAgentFoundationFixtures.TOOL_BOUNDARY_ID,
        ReferenceAgentFoundationFixtures.activeToolBoundary());
  }
}

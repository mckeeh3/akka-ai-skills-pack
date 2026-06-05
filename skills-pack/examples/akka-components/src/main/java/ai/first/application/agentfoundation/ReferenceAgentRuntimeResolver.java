package ai.first.application.agentfoundation;

import ai.first.domain.agentfoundation.ReferenceAgentDefinition;
import ai.first.domain.agentfoundation.ReferenceAgentSkillManifest;
import ai.first.domain.agentfoundation.ReferenceAuthContext;
import ai.first.domain.agentfoundation.ReferencePromptAssemblyTrace;
import ai.first.domain.agentfoundation.ReferencePromptVersion;
import ai.first.domain.agentfoundation.ReferenceResolvedAgentRuntime;
import ai.first.domain.agentfoundation.ReferenceToolPermissionBoundary;
import java.util.Map;

/** Reference-only resolver that fails closed before model invocation. */
public final class ReferenceAgentRuntimeResolver {
  public static final String INVOKE_CAPABILITY = "agent-runtime.invoke-managed-agent.reference";

  private final Map<String, ReferenceAgentDefinition> agentDefinitions;
  private final Map<String, ReferencePromptVersion> promptVersions;
  private final Map<String, ReferenceAgentSkillManifest> manifests;
  private final Map<String, ReferenceToolPermissionBoundary> toolBoundaries;
  private final ReferencePromptAssembler promptAssembler;
  private final ReferenceTraceSink traceSink;

  public ReferenceAgentRuntimeResolver(
      Map<String, ReferenceAgentDefinition> agentDefinitions,
      Map<String, ReferencePromptVersion> promptVersions,
      Map<String, ReferenceAgentSkillManifest> manifests,
      Map<String, ReferenceToolPermissionBoundary> toolBoundaries,
      ReferencePromptAssembler promptAssembler,
      ReferenceTraceSink traceSink) {
    this.agentDefinitions = Map.copyOf(agentDefinitions);
    this.promptVersions = Map.copyOf(promptVersions);
    this.manifests = Map.copyOf(manifests);
    this.toolBoundaries = Map.copyOf(toolBoundaries);
    this.promptAssembler = promptAssembler;
    this.traceSink = traceSink;
  }

  public ReferenceResolvedAgentRuntime resolve(
      ReferenceAuthContext authContext, String agentDefinitionId, String correlationId) {
    var agentDefinition = agentDefinitions.get(agentDefinitionId);
    if (agentDefinition == null) {
      return deny("agent unavailable", authContext, null, correlationId, null, null, null);
    }
    if (!authContext.hasCapability(INVOKE_CAPABILITY)) {
      return deny("missing invoke capability", authContext, agentDefinition, correlationId, null, null, null);
    }
    if (!agentDefinition.tenantId().equals(authContext.tenantId())) {
      return deny("agent unavailable", authContext, agentDefinition, correlationId, null, null, null);
    }
    if (!agentDefinition.activeForRuntime()) {
      return deny("agent is not active for runtime", authContext, agentDefinition, correlationId, null, null, null);
    }

    var promptVersion = promptVersions.get(agentDefinition.activePromptVersionId());
    if (promptVersion == null
        || !promptVersion.tenantId().equals(authContext.tenantId())
        || !promptVersion.promptDocumentId().equals(agentDefinition.promptDocumentId())
        || !promptVersion.activeForRuntime()) {
      return deny(
          "active prompt denied",
          authContext,
          agentDefinition,
          correlationId,
          promptVersion,
          null,
          null);
    }

    var manifest = manifests.get(agentDefinition.skillManifestId());
    if (manifest == null
        || !manifest.tenantId().equals(authContext.tenantId())
        || !manifest.agentDefinitionId().equals(agentDefinition.agentDefinitionId())
        || !manifest.active()) {
      return deny(
          "active manifest denied",
          authContext,
          agentDefinition,
          correlationId,
          promptVersion,
          manifest,
          null);
    }

    var toolBoundary = toolBoundaries.get(agentDefinition.toolBoundaryId());
    if (toolBoundary == null
        || !toolBoundary.tenantId().equals(authContext.tenantId())
        || !toolBoundary.agentDefinitionId().equals(agentDefinition.agentDefinitionId())
        || !toolBoundary.active()) {
      return deny(
          "tool boundary denied",
          authContext,
          agentDefinition,
          correlationId,
          promptVersion,
          manifest,
          toolBoundary);
    }

    var assembledPrompt =
        promptAssembler.assemble(authContext, agentDefinition, promptVersion, manifest, toolBoundary);
    var checksum = promptAssembler.checksum(assembledPrompt);
    traceSink.recordPromptAssembly(
        new ReferencePromptAssemblyTrace(
            authContext.tenantId(),
            agentDefinition.agentDefinitionId(),
            promptVersion.promptDocumentId(),
            promptVersion.promptVersionId(),
            manifest.skillManifestId(),
            toolBoundary.toolBoundaryId(),
            authContext.mode(),
            correlationId,
            true,
            "allowed",
            checksum));
    return new ReferenceResolvedAgentRuntime(
        true,
        "",
        authContext,
        agentDefinition,
        promptVersion,
        manifest,
        toolBoundary,
        assembledPrompt,
        checksum,
        correlationId);
  }

  private ReferenceResolvedAgentRuntime deny(
      String reason,
      ReferenceAuthContext authContext,
      ReferenceAgentDefinition agentDefinition,
      String correlationId,
      ReferencePromptVersion promptVersion,
      ReferenceAgentSkillManifest manifest,
      ReferenceToolPermissionBoundary toolBoundary) {
    traceSink.recordPromptAssembly(
        new ReferencePromptAssemblyTrace(
            authContext.tenantId(),
            agentDefinition == null ? "" : agentDefinition.agentDefinitionId(),
            promptVersion == null ? "" : promptVersion.promptDocumentId(),
            promptVersion == null ? "" : promptVersion.promptVersionId(),
            manifest == null ? "" : manifest.skillManifestId(),
            toolBoundary == null ? "" : toolBoundary.toolBoundaryId(),
            authContext.mode(),
            correlationId,
            false,
            reason,
            ""));
    return ReferenceResolvedAgentRuntime.denied(reason, authContext, agentDefinition, correlationId);
  }
}

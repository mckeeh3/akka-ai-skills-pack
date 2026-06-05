package ai.first.domain.agentfoundation;

/** Resolver output for a governed reference agent invocation before model/tool calls. */
public record ReferenceResolvedAgentRuntime(
    boolean allowed,
    String denialReason,
    ReferenceAuthContext authContext,
    ReferenceAgentDefinition agentDefinition,
    ReferencePromptVersion promptVersion,
    ReferenceAgentSkillManifest skillManifest,
    ReferenceToolPermissionBoundary toolPermissionBoundary,
    String assembledPrompt,
    String assembledPromptChecksum,
    String correlationId) {

  public static ReferenceResolvedAgentRuntime denied(
      String reason,
      ReferenceAuthContext authContext,
      ReferenceAgentDefinition agentDefinition,
      String correlationId) {
    return new ReferenceResolvedAgentRuntime(
        false, reason, authContext, agentDefinition, null, null, null, "", "", correlationId);
  }
}

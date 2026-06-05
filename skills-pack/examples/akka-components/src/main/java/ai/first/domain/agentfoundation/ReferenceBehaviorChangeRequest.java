package ai.first.domain.agentfoundation;

import java.util.Set;

/** Natural-language behavior maintenance request handled by an editing-agent reference flow. */
public record ReferenceBehaviorChangeRequest(
    String tenantId,
    String requestId,
    String requestedByAccountId,
    String targetAgentDefinitionId,
    String targetArtifactType,
    String targetArtifactId,
    String requestedChange,
    Set<String> requestedExpansionTypes,
    String correlationId) {

  public ReferenceBehaviorChangeRequest {
    requestedExpansionTypes = Set.copyOf(requestedExpansionTypes);
  }

  public boolean requestsAuthorityExpansion() {
    return !requestedExpansionTypes.isEmpty();
  }
}

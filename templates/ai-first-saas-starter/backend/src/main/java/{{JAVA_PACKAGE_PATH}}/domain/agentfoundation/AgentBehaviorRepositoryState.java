package {{JAVA_BASE_PACKAGE}}.domain.agentfoundation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Durable current-state projection for governed agent behavior records.
 *
 * <p>This state backs the first Akka component seam for the starter {@code AgentBehaviorRepository}
 * port. It stores tenant-scoped active AgentDefinition, PromptDocument, SkillDocument,
 * AgentSkillManifest, and ToolPermissionBoundary records used by deterministic prompt assembly,
 * {@code readSkill(skillId)}, seed import, and behavior proposal activation. It intentionally stores
 * governed behavior text only; provider keys and runtime credentials must remain outside these records.
 */
public record AgentBehaviorRepositoryState(
    Map<String, AgentDefinition> agentDefinitions,
    Map<String, PromptDocument> promptDocuments,
    Map<String, SkillDocument> skillDocuments,
    Map<String, AgentSkillManifest> skillManifests,
    Map<String, ToolPermissionBoundary> toolBoundaries) {

  public AgentBehaviorRepositoryState {
    agentDefinitions = Map.copyOf(agentDefinitions == null ? Map.of() : agentDefinitions);
    promptDocuments = Map.copyOf(promptDocuments == null ? Map.of() : promptDocuments);
    skillDocuments = Map.copyOf(skillDocuments == null ? Map.of() : skillDocuments);
    skillManifests = Map.copyOf(skillManifests == null ? Map.of() : skillManifests);
    toolBoundaries = Map.copyOf(toolBoundaries == null ? Map.of() : toolBoundaries);
  }

  public static AgentBehaviorRepositoryState empty() {
    return new AgentBehaviorRepositoryState(Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
  }

  public Optional<AgentDefinition> agentDefinition(String tenantId, String agentDefinitionId) {
    return Optional.ofNullable(agentDefinitions.get(key(tenantId, agentDefinitionId)));
  }

  public List<AgentDefinition> agentDefinitions(String tenantId) {
    return agentDefinitions.values().stream()
        .filter(agent -> tenantId.equals(agent.tenantId()))
        .toList();
  }

  public AgentBehaviorRepositoryState saveAgentDefinition(AgentDefinition definition) {
    var updated = new java.util.LinkedHashMap<>(agentDefinitions);
    updated.put(key(definition.tenantId(), definition.agentDefinitionId()), definition);
    return new AgentBehaviorRepositoryState(updated, promptDocuments, skillDocuments, skillManifests, toolBoundaries);
  }

  public Optional<PromptDocument> promptDocument(String tenantId, String promptDocumentId) {
    return Optional.ofNullable(promptDocuments.get(key(tenantId, promptDocumentId)));
  }

  public AgentBehaviorRepositoryState savePromptDocument(PromptDocument prompt) {
    var updated = new java.util.LinkedHashMap<>(promptDocuments);
    updated.put(key(prompt.tenantId(), prompt.promptDocumentId()), prompt);
    return new AgentBehaviorRepositoryState(agentDefinitions, updated, skillDocuments, skillManifests, toolBoundaries);
  }

  public Optional<SkillDocument> skillDocument(String tenantId, String skillDocumentId) {
    return Optional.ofNullable(skillDocuments.get(key(tenantId, skillDocumentId)));
  }

  public List<SkillDocument> skillDocuments(String tenantId) {
    return skillDocuments.values().stream()
        .filter(skill -> tenantId.equals(skill.tenantId()))
        .toList();
  }

  public AgentBehaviorRepositoryState saveSkillDocument(SkillDocument skill) {
    var updated = new java.util.LinkedHashMap<>(skillDocuments);
    updated.put(key(skill.tenantId(), skill.skillDocumentId()), skill);
    return new AgentBehaviorRepositoryState(agentDefinitions, promptDocuments, updated, skillManifests, toolBoundaries);
  }

  public Optional<AgentSkillManifest> skillManifest(String tenantId, String manifestId) {
    return Optional.ofNullable(skillManifests.get(key(tenantId, manifestId)));
  }

  public AgentBehaviorRepositoryState saveSkillManifest(AgentSkillManifest manifest) {
    var updated = new java.util.LinkedHashMap<>(skillManifests);
    updated.put(key(manifest.tenantId(), manifest.manifestId()), manifest);
    return new AgentBehaviorRepositoryState(agentDefinitions, promptDocuments, skillDocuments, updated, toolBoundaries);
  }

  public Optional<ToolPermissionBoundary> toolBoundary(String tenantId, String boundaryId) {
    return Optional.ofNullable(toolBoundaries.get(key(tenantId, boundaryId)));
  }

  public AgentBehaviorRepositoryState saveToolBoundary(ToolPermissionBoundary boundary) {
    var updated = new java.util.LinkedHashMap<>(toolBoundaries);
    updated.put(key(boundary.tenantId(), boundary.boundaryId()), boundary);
    return new AgentBehaviorRepositoryState(agentDefinitions, promptDocuments, skillDocuments, skillManifests, updated);
  }

  private static String key(String tenantId, String recordId) {
    return tenantId + ":" + recordId;
  }
}

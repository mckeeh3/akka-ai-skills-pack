package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentSkillManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SkillDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Test/local governed-agent store for the starter template. */
public final class InMemoryAgentBehaviorRepository implements AgentBehaviorRepository {
  private final Map<String, AgentDefinition> agents = new ConcurrentHashMap<>();
  private final Map<String, PromptDocument> prompts = new ConcurrentHashMap<>();
  private final Map<String, SkillDocument> skills = new ConcurrentHashMap<>();
  private final Map<String, AgentSkillManifest> manifests = new ConcurrentHashMap<>();
  private final Map<String, ToolPermissionBoundary> boundaries = new ConcurrentHashMap<>();

  @Override public Optional<AgentDefinition> agentDefinition(String tenantId, String agentDefinitionId) { return Optional.ofNullable(agents.get(key(tenantId, agentDefinitionId))); }
  @Override public AgentDefinition saveAgentDefinition(AgentDefinition definition) { agents.put(key(definition.tenantId(), definition.agentDefinitionId()), definition); return definition; }
  @Override public List<AgentDefinition> agentDefinitions(String tenantId) { return agents.values().stream().filter(agent -> tenantId.equals(agent.tenantId())).toList(); }

  @Override public Optional<PromptDocument> promptDocument(String tenantId, String promptDocumentId) { return Optional.ofNullable(prompts.get(key(tenantId, promptDocumentId))); }
  @Override public PromptDocument savePromptDocument(PromptDocument prompt) { prompts.put(key(prompt.tenantId(), prompt.promptDocumentId()), prompt); return prompt; }

  @Override public Optional<SkillDocument> skillDocument(String tenantId, String skillDocumentId) { return Optional.ofNullable(skills.get(key(tenantId, skillDocumentId))); }
  @Override public SkillDocument saveSkillDocument(SkillDocument skill) { skills.put(key(skill.tenantId(), skill.skillDocumentId()), skill); return skill; }
  @Override public List<SkillDocument> skillDocuments(String tenantId) { return skills.values().stream().filter(skill -> tenantId.equals(skill.tenantId())).toList(); }

  @Override public Optional<AgentSkillManifest> skillManifest(String tenantId, String manifestId) { return Optional.ofNullable(manifests.get(key(tenantId, manifestId))); }
  @Override public AgentSkillManifest saveSkillManifest(AgentSkillManifest manifest) { manifests.put(key(manifest.tenantId(), manifest.manifestId()), manifest); return manifest; }

  @Override public Optional<ToolPermissionBoundary> toolBoundary(String tenantId, String boundaryId) { return Optional.ofNullable(boundaries.get(key(tenantId, boundaryId))); }
  @Override public ToolPermissionBoundary saveToolBoundary(ToolPermissionBoundary boundary) { boundaries.put(key(boundary.tenantId(), boundary.boundaryId()), boundary); return boundary; }

  private String key(String tenantId, String recordId) { return tenantId + ":" + recordId; }
}

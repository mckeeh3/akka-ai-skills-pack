package ai.first.application.agentfoundation;

import ai.first.domain.agentfoundation.AgentDefinition;
import ai.first.domain.agentfoundation.AgentReferenceManifest;
import ai.first.domain.agentfoundation.AgentSkillManifest;
import ai.first.domain.agentfoundation.ModelConfigRef;
import ai.first.domain.agentfoundation.ModelPolicy;
import ai.first.domain.agentfoundation.PromptDocument;
import ai.first.domain.agentfoundation.ReferenceDocument;
import ai.first.domain.agentfoundation.SkillDocument;
import ai.first.domain.agentfoundation.ToolPermissionBoundary;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Test/local governed-agent store for the core app. */
public final class LocalDemoAgentBehaviorRepository implements AgentBehaviorRepository {
  private final Map<String, AgentDefinition> agents = new ConcurrentHashMap<>();
  private final Map<String, PromptDocument> prompts = new ConcurrentHashMap<>();
  private final Map<String, SkillDocument> skills = new ConcurrentHashMap<>();
  private final Map<String, ReferenceDocument> references = new ConcurrentHashMap<>();
  private final Map<String, AgentSkillManifest> manifests = new ConcurrentHashMap<>();
  private final Map<String, AgentReferenceManifest> referenceManifests = new ConcurrentHashMap<>();
  private final Map<String, ToolPermissionBoundary> boundaries = new ConcurrentHashMap<>();
  private final Map<String, ModelConfigRef> modelConfigRefs = new ConcurrentHashMap<>();
  private final Map<String, ModelPolicy> modelPolicies = new ConcurrentHashMap<>();

  @Override public Optional<AgentDefinition> agentDefinition(String tenantId, String agentDefinitionId) { return Optional.ofNullable(agents.get(key(tenantId, agentDefinitionId))); }
  @Override public AgentDefinition saveAgentDefinition(AgentDefinition definition) { agents.put(key(definition.tenantId(), definition.agentDefinitionId()), definition); return definition; }
  @Override public List<AgentDefinition> agentDefinitions(String tenantId) { return agents.values().stream().filter(agent -> tenantId.equals(agent.tenantId())).toList(); }

  @Override public Optional<PromptDocument> promptDocument(String tenantId, String promptDocumentId) { return Optional.ofNullable(prompts.get(key(tenantId, promptDocumentId))); }
  @Override public PromptDocument savePromptDocument(PromptDocument prompt) { prompts.put(key(prompt.tenantId(), prompt.promptDocumentId()), prompt); return prompt; }

  @Override public Optional<SkillDocument> skillDocument(String tenantId, String skillDocumentId) { return Optional.ofNullable(skills.get(key(tenantId, skillDocumentId))); }
  @Override public SkillDocument saveSkillDocument(SkillDocument skill) { skills.put(key(skill.tenantId(), skill.skillDocumentId()), skill); return skill; }
  @Override public List<SkillDocument> skillDocuments(String tenantId) { return skills.values().stream().filter(skill -> tenantId.equals(skill.tenantId())).toList(); }

  @Override public Optional<ReferenceDocument> referenceDocument(String tenantId, String referenceDocumentId) { return Optional.ofNullable(references.get(key(tenantId, referenceDocumentId))); }
  @Override public ReferenceDocument saveReferenceDocument(ReferenceDocument reference) { references.put(key(reference.tenantId(), reference.referenceDocumentId()), reference); return reference; }
  @Override public List<ReferenceDocument> referenceDocuments(String tenantId) { return references.values().stream().filter(reference -> tenantId.equals(reference.tenantId())).toList(); }

  @Override public Optional<AgentSkillManifest> skillManifest(String tenantId, String manifestId) { return Optional.ofNullable(manifests.get(key(tenantId, manifestId))); }
  @Override public AgentSkillManifest saveSkillManifest(AgentSkillManifest manifest) { manifests.put(key(manifest.tenantId(), manifest.manifestId()), manifest); return manifest; }

  @Override public Optional<AgentReferenceManifest> referenceManifest(String tenantId, String manifestId) { return Optional.ofNullable(referenceManifests.get(key(tenantId, manifestId))); }
  @Override public AgentReferenceManifest saveReferenceManifest(AgentReferenceManifest manifest) { referenceManifests.put(key(manifest.tenantId(), manifest.manifestId()), manifest); return manifest; }

  @Override public Optional<ToolPermissionBoundary> toolBoundary(String tenantId, String boundaryId) { return Optional.ofNullable(boundaries.get(key(tenantId, boundaryId))); }
  @Override public ToolPermissionBoundary saveToolBoundary(ToolPermissionBoundary boundary) { boundaries.put(key(boundary.tenantId(), boundary.boundaryId()), boundary); return boundary; }

  @Override public Optional<ModelConfigRef> modelConfigRef(String tenantId, String modelConfigRefId) { return Optional.ofNullable(modelConfigRefs.get(key(tenantId, modelConfigRefId))); }
  @Override public ModelConfigRef saveModelConfigRef(ModelConfigRef modelConfigRef) { modelConfigRefs.put(key(modelConfigRef.tenantId(), modelConfigRef.modelConfigRefId()), modelConfigRef); return modelConfigRef; }

  @Override public Optional<ModelPolicy> modelPolicy(String tenantId, String modelPolicyRefId) { return Optional.ofNullable(modelPolicies.get(key(tenantId, modelPolicyRefId))); }
  @Override public ModelPolicy saveModelPolicy(ModelPolicy modelPolicy) { modelPolicies.put(key(modelPolicy.tenantId(), modelPolicy.modelPolicyRefId()), modelPolicy); return modelPolicy; }

  private String key(String tenantId, String recordId) { return tenantId + ":" + recordId; }
}

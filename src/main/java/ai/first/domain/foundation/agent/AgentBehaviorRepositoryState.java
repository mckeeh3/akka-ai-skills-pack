package ai.first.domain.foundation.agent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Durable current-state projection for governed agent behavior and reference records.
 *
 * <p>This state backs the first Akka component seam for the starter {@code AgentBehaviorRepository}
 * port. It stores tenant-scoped active AgentDefinition, immutable AgentBehaviorProfileVersion,
 * PromptDocument, SkillDocument, ReferenceDocument, AgentSkillManifest, AgentReferenceManifest,
 * ToolPermissionBoundary, ModelConfigRef, and ModelPolicy records used by deterministic prompt assembly, model binding,
 * {@code readSkill(skillId)}, {@code readReferenceDoc(referenceId)},
 * seed import, and behavior proposal activation. It intentionally stores governed behavior/reference
 * text only; provider keys and runtime credentials must remain outside these records.
 */
public record AgentBehaviorRepositoryState(
    Map<String, AgentDefinition> agentDefinitions,
    Map<String, AgentBehaviorProfileVersion> behaviorProfileVersions,
    Map<String, PromptDocument> promptDocuments,
    Map<String, SkillDocument> skillDocuments,
    Map<String, ReferenceDocument> referenceDocuments,
    Map<String, AgentSkillManifest> skillManifests,
    Map<String, AgentReferenceManifest> referenceManifests,
    Map<String, ToolPermissionBoundary> toolBoundaries,
    Map<String, ModelConfigRef> modelConfigRefs,
    Map<String, ModelPolicy> modelPolicies) {

  public AgentBehaviorRepositoryState {
    agentDefinitions = Map.copyOf(agentDefinitions == null ? Map.of() : agentDefinitions);
    behaviorProfileVersions = Map.copyOf(behaviorProfileVersions == null ? Map.of() : behaviorProfileVersions);
    promptDocuments = Map.copyOf(promptDocuments == null ? Map.of() : promptDocuments);
    skillDocuments = Map.copyOf(skillDocuments == null ? Map.of() : skillDocuments);
    referenceDocuments = Map.copyOf(referenceDocuments == null ? Map.of() : referenceDocuments);
    skillManifests = Map.copyOf(skillManifests == null ? Map.of() : skillManifests);
    referenceManifests = Map.copyOf(referenceManifests == null ? Map.of() : referenceManifests);
    toolBoundaries = Map.copyOf(toolBoundaries == null ? Map.of() : toolBoundaries);
    modelConfigRefs = Map.copyOf(modelConfigRefs == null ? Map.of() : modelConfigRefs);
    modelPolicies = Map.copyOf(modelPolicies == null ? Map.of() : modelPolicies);
  }

  public static AgentBehaviorRepositoryState empty() {
    return new AgentBehaviorRepositoryState(Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
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
    return new AgentBehaviorRepositoryState(updated, behaviorProfileVersions, promptDocuments, skillDocuments, referenceDocuments, skillManifests, referenceManifests, toolBoundaries, modelConfigRefs, modelPolicies);
  }

  public Optional<AgentBehaviorProfileVersion> activeBehaviorProfile(String tenantId, String agentDefinitionId) {
    return behaviorProfileVersions.values().stream()
        .filter(profile -> tenantId.equals(profile.tenantId()))
        .filter(profile -> agentDefinitionId.equals(profile.agentDefinitionId()))
        .filter(profile -> profile.status() == AgentLifecycleStatus.ACTIVE)
        .max(java.util.Comparator.comparingInt(AgentBehaviorProfileVersion::profileVersion));
  }

  public List<AgentBehaviorProfileVersion> behaviorProfileVersions(String tenantId, String agentDefinitionId) {
    return behaviorProfileVersions.values().stream()
        .filter(profile -> tenantId.equals(profile.tenantId()))
        .filter(profile -> agentDefinitionId.equals(profile.agentDefinitionId()))
        .sorted(java.util.Comparator.comparingInt(AgentBehaviorProfileVersion::profileVersion))
        .toList();
  }

  public AgentBehaviorRepositoryState saveBehaviorProfileVersion(AgentBehaviorProfileVersion profileVersion) {
    var current = activeBehaviorProfile(profileVersion.tenantId(), profileVersion.agentDefinitionId());
    if (current.isPresent() && profileVersion.profileVersion() <= current.get().profileVersion()) {
      throw new IllegalStateException("stale-profile-version");
    }
    var updated = new java.util.LinkedHashMap<>(behaviorProfileVersions);
    updated.put(profileKey(profileVersion.tenantId(), profileVersion.agentDefinitionId(), profileVersion.profileVersion()), profileVersion);
    return new AgentBehaviorRepositoryState(agentDefinitions, updated, promptDocuments, skillDocuments, referenceDocuments, skillManifests, referenceManifests, toolBoundaries, modelConfigRefs, modelPolicies);
  }

  public Optional<PromptDocument> promptDocument(String tenantId, String promptDocumentId) {
    return Optional.ofNullable(promptDocuments.get(key(tenantId, promptDocumentId)));
  }

  public AgentBehaviorRepositoryState savePromptDocument(PromptDocument prompt) {
    var updated = new java.util.LinkedHashMap<>(promptDocuments);
    updated.put(key(prompt.tenantId(), prompt.promptDocumentId()), prompt);
    return new AgentBehaviorRepositoryState(agentDefinitions, behaviorProfileVersions, updated, skillDocuments, referenceDocuments, skillManifests, referenceManifests, toolBoundaries, modelConfigRefs, modelPolicies);
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
    return new AgentBehaviorRepositoryState(agentDefinitions, behaviorProfileVersions, promptDocuments, updated, referenceDocuments, skillManifests, referenceManifests, toolBoundaries, modelConfigRefs, modelPolicies);
  }

  public Optional<ReferenceDocument> referenceDocument(String tenantId, String referenceDocumentId) {
    return Optional.ofNullable(referenceDocuments.get(key(tenantId, referenceDocumentId)));
  }

  public List<ReferenceDocument> referenceDocuments(String tenantId) {
    return referenceDocuments.values().stream()
        .filter(reference -> tenantId.equals(reference.tenantId()))
        .toList();
  }

  public AgentBehaviorRepositoryState saveReferenceDocument(ReferenceDocument reference) {
    var updated = new java.util.LinkedHashMap<>(referenceDocuments);
    updated.put(key(reference.tenantId(), reference.referenceDocumentId()), reference);
    return new AgentBehaviorRepositoryState(agentDefinitions, behaviorProfileVersions, promptDocuments, skillDocuments, updated, skillManifests, referenceManifests, toolBoundaries, modelConfigRefs, modelPolicies);
  }

  public Optional<AgentSkillManifest> skillManifest(String tenantId, String manifestId) {
    return Optional.ofNullable(skillManifests.get(key(tenantId, manifestId)));
  }

  public AgentBehaviorRepositoryState saveSkillManifest(AgentSkillManifest manifest) {
    var updated = new java.util.LinkedHashMap<>(skillManifests);
    updated.put(key(manifest.tenantId(), manifest.manifestId()), manifest);
    return new AgentBehaviorRepositoryState(agentDefinitions, behaviorProfileVersions, promptDocuments, skillDocuments, referenceDocuments, updated, referenceManifests, toolBoundaries, modelConfigRefs, modelPolicies);
  }

  public Optional<AgentReferenceManifest> referenceManifest(String tenantId, String manifestId) {
    return Optional.ofNullable(referenceManifests.get(key(tenantId, manifestId)));
  }

  public AgentBehaviorRepositoryState saveReferenceManifest(AgentReferenceManifest manifest) {
    var updated = new java.util.LinkedHashMap<>(referenceManifests);
    updated.put(key(manifest.tenantId(), manifest.manifestId()), manifest);
    return new AgentBehaviorRepositoryState(agentDefinitions, behaviorProfileVersions, promptDocuments, skillDocuments, referenceDocuments, skillManifests, updated, toolBoundaries, modelConfigRefs, modelPolicies);
  }

  public Optional<ToolPermissionBoundary> toolBoundary(String tenantId, String boundaryId) {
    return Optional.ofNullable(toolBoundaries.get(key(tenantId, boundaryId)));
  }

  public AgentBehaviorRepositoryState saveToolBoundary(ToolPermissionBoundary boundary) {
    var updated = new java.util.LinkedHashMap<>(toolBoundaries);
    updated.put(key(boundary.tenantId(), boundary.boundaryId()), boundary);
    return new AgentBehaviorRepositoryState(agentDefinitions, behaviorProfileVersions, promptDocuments, skillDocuments, referenceDocuments, skillManifests, referenceManifests, updated, modelConfigRefs, modelPolicies);
  }

  public Optional<ModelConfigRef> modelConfigRef(String tenantId, String modelConfigRefId) {
    return Optional.ofNullable(modelConfigRefs.get(key(tenantId, modelConfigRefId)));
  }

  public AgentBehaviorRepositoryState saveModelConfigRef(ModelConfigRef modelConfigRef) {
    var updated = new java.util.LinkedHashMap<>(modelConfigRefs);
    updated.put(key(modelConfigRef.tenantId(), modelConfigRef.modelConfigRefId()), modelConfigRef);
    return new AgentBehaviorRepositoryState(agentDefinitions, behaviorProfileVersions, promptDocuments, skillDocuments, referenceDocuments, skillManifests, referenceManifests, toolBoundaries, updated, modelPolicies);
  }

  public Optional<ModelPolicy> modelPolicy(String tenantId, String modelPolicyRefId) {
    return Optional.ofNullable(modelPolicies.get(key(tenantId, modelPolicyRefId)));
  }

  public AgentBehaviorRepositoryState saveModelPolicy(ModelPolicy modelPolicy) {
    var updated = new java.util.LinkedHashMap<>(modelPolicies);
    updated.put(key(modelPolicy.tenantId(), modelPolicy.modelPolicyRefId()), modelPolicy);
    return new AgentBehaviorRepositoryState(agentDefinitions, behaviorProfileVersions, promptDocuments, skillDocuments, referenceDocuments, skillManifests, referenceManifests, toolBoundaries, modelConfigRefs, updated);
  }

  private static String key(String tenantId, String recordId) {
    return tenantId + ":" + recordId;
  }

  private static String profileKey(String tenantId, String agentDefinitionId, int profileVersion) {
    return key(tenantId, agentDefinitionId) + ":" + profileVersion;
  }
}

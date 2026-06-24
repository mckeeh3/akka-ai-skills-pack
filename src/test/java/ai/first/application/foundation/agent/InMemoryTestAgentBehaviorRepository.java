package ai.first.application.foundation.agent;

import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentReferenceManifest;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import ai.first.domain.foundation.agent.ModelConfigRef;
import ai.first.domain.foundation.agent.ModelPolicy;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.PromptDocument;
import ai.first.domain.foundation.agent.PromptVersion;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.agent.ReferenceVersion;
import ai.first.domain.foundation.agent.SkillDocument;
import ai.first.domain.foundation.agent.SkillVersion;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Test/local governed-agent store for the core app. */
public final class InMemoryTestAgentBehaviorRepository implements AgentBehaviorRepository {
  private final Map<String, AgentDefinition> agents = new ConcurrentHashMap<>();
  private final Map<String, PromptDocument> prompts = new ConcurrentHashMap<>();
  private final Map<String, Map<Integer, PromptVersion>> promptVersions = new ConcurrentHashMap<>();
  private final Map<String, SkillDocument> skills = new ConcurrentHashMap<>();
  private final Map<String, Map<Integer, SkillVersion>> skillVersions = new ConcurrentHashMap<>();
  private final Map<String, ReferenceDocument> references = new ConcurrentHashMap<>();
  private final Map<String, Map<Integer, ReferenceVersion>> referenceVersions = new ConcurrentHashMap<>();
  private final Map<String, AgentSkillManifest> manifests = new ConcurrentHashMap<>();
  private final Map<String, AgentReferenceManifest> referenceManifests = new ConcurrentHashMap<>();
  private final Map<String, ToolPermissionBoundary> boundaries = new ConcurrentHashMap<>();
  private final Map<String, ModelConfigRef> modelConfigRefs = new ConcurrentHashMap<>();
  private final Map<String, ModelPolicy> modelPolicies = new ConcurrentHashMap<>();

  @Override public Optional<AgentDefinition> agentDefinition(String tenantId, String agentDefinitionId) { return Optional.ofNullable(agents.get(key(tenantId, agentDefinitionId))); }
  @Override public AgentDefinition saveAgentDefinition(AgentDefinition definition) { agents.put(key(definition.tenantId(), definition.agentDefinitionId()), definition); return definition; }
  @Override public List<AgentDefinition> agentDefinitions(String tenantId) { return agents.values().stream().filter(agent -> tenantId.equals(agent.tenantId())).toList(); }

  @Override public Optional<PromptDocument> promptDocument(String tenantId, String promptDocumentId) { return Optional.ofNullable(prompts.get(key(tenantId, promptDocumentId))); }
  @Override public PromptDocument savePromptDocument(PromptDocument prompt) { prompts.put(key(prompt.tenantId(), prompt.promptDocumentId()), prompt); promptVersions.computeIfAbsent(key(prompt.tenantId(), prompt.promptDocumentId()), ignored -> new ConcurrentHashMap<>()).put(prompt.activeVersion(), toPromptVersion(prompt, actor(prompt), prompt.changeSummary())); return prompt; }
  @Override public Optional<PromptVersion> promptVersion(String tenantId, String promptDocumentId, int version) { return Optional.ofNullable(promptVersions.getOrDefault(key(tenantId, promptDocumentId), Map.of()).get(version)); }
  @Override public List<PromptVersion> promptVersions(String tenantId, String promptDocumentId) { return promptVersions.getOrDefault(key(tenantId, promptDocumentId), Map.of()).values().stream().sorted(Comparator.comparingInt(PromptVersion::version)).toList(); }
  @Override public PromptDocument savePromptDocumentVersion(DocumentVersionSave command) {
    var existing = promptDocument(command.tenantId(), command.documentId()).orElseThrow(() -> new IllegalStateException("prompt-document-not-found"));
    if (existing.activeVersion() != command.expectedCurrentVersion()) throw new IllegalStateException("stale-current-version");
    return savePromptDocument(new PromptDocument(existing.tenantId(), existing.promptDocumentId(), existing.agentDefinitionId(), existing.title(), existing.promptType(), existing.status(), existing.activeVersion() + 1, command.contentBody(), checksum(command.contentBody()), command.changeSummary(), existing.seedProvenance(), existing.createdAt(), timestamp(command.createdAt())), command.actorAccountId(), command.editSessionTranscriptSummary());
  }
  @Override public PromptDocument restorePromptDocumentVersion(DocumentVersionRestore command) {
    var existing = promptDocument(command.tenantId(), command.documentId()).orElseThrow(() -> new IllegalStateException("prompt-document-not-found"));
    if (command.version() < 1 || command.version() >= existing.activeVersion()) throw new IllegalStateException("historical-version-required");
    var restored = promptVersion(command.tenantId(), command.documentId(), command.version()).orElseThrow(() -> new IllegalStateException("prompt-version-not-found"));
    var summary = "Restored from version " + command.version();
    return savePromptDocument(new PromptDocument(existing.tenantId(), existing.promptDocumentId(), existing.agentDefinitionId(), existing.title(), existing.promptType(), existing.status(), existing.activeVersion() + 1, restored.contentBody(), checksum(restored.contentBody()), summary, existing.seedProvenance(), existing.createdAt(), timestamp(command.createdAt())), command.actorAccountId(), summary);
  }

  @Override public Optional<SkillDocument> skillDocument(String tenantId, String skillDocumentId) { return Optional.ofNullable(skills.get(key(tenantId, skillDocumentId))); }
  @Override public SkillDocument saveSkillDocument(SkillDocument skill) { skills.put(key(skill.tenantId(), skill.skillDocumentId()), skill); skillVersions.computeIfAbsent(key(skill.tenantId(), skill.skillDocumentId()), ignored -> new ConcurrentHashMap<>()).put(skill.activeVersion(), toSkillVersion(skill, actor(skill), skill.purpose())); return skill; }
  @Override public List<SkillDocument> skillDocuments(String tenantId) { return skills.values().stream().filter(skill -> tenantId.equals(skill.tenantId())).toList(); }
  @Override public Optional<SkillVersion> skillVersion(String tenantId, String skillDocumentId, int version) { return Optional.ofNullable(skillVersions.getOrDefault(key(tenantId, skillDocumentId), Map.of()).get(version)); }
  @Override public List<SkillVersion> skillVersions(String tenantId, String skillDocumentId) { return skillVersions.getOrDefault(key(tenantId, skillDocumentId), Map.of()).values().stream().sorted(Comparator.comparingInt(SkillVersion::version)).toList(); }
  @Override public SkillDocument saveSkillDocumentVersion(DocumentVersionSave command) {
    var existing = skillDocument(command.tenantId(), command.documentId()).orElseThrow(() -> new IllegalStateException("skill-document-not-found"));
    if (existing.activeVersion() != command.expectedCurrentVersion()) throw new IllegalStateException("stale-current-version");
    return saveSkillDocument(new SkillDocument(existing.tenantId(), existing.skillDocumentId(), existing.stableSkillId(), existing.title(), existing.purpose(), existing.whenToUse(), existing.tags(), existing.status(), existing.activeVersion() + 1, command.contentBody(), checksum(command.contentBody()), existing.seedProvenance(), existing.createdAt(), timestamp(command.createdAt())), command.actorAccountId(), command.editSessionTranscriptSummary());
  }
  @Override public SkillDocument restoreSkillDocumentVersion(DocumentVersionRestore command) {
    var existing = skillDocument(command.tenantId(), command.documentId()).orElseThrow(() -> new IllegalStateException("skill-document-not-found"));
    if (command.version() < 1 || command.version() >= existing.activeVersion()) throw new IllegalStateException("historical-version-required");
    var restored = skillVersion(command.tenantId(), command.documentId(), command.version()).orElseThrow(() -> new IllegalStateException("skill-version-not-found"));
    var summary = "Restored from version " + command.version();
    return saveSkillDocument(new SkillDocument(existing.tenantId(), existing.skillDocumentId(), existing.stableSkillId(), existing.title(), existing.purpose(), existing.whenToUse(), existing.tags(), existing.status(), existing.activeVersion() + 1, restored.contentBody(), checksum(restored.contentBody()), existing.seedProvenance(), existing.createdAt(), timestamp(command.createdAt())), command.actorAccountId(), summary);
  }
  @Override public void deleteSkillDocument(String tenantId, String skillDocumentId, String actorAccountId, Instant deletedAt) { skills.remove(key(tenantId, skillDocumentId)); skillVersions.remove(key(tenantId, skillDocumentId)); }

  @Override public Optional<ReferenceDocument> referenceDocument(String tenantId, String referenceDocumentId) { return Optional.ofNullable(references.get(key(tenantId, referenceDocumentId))); }
  @Override public ReferenceDocument saveReferenceDocument(ReferenceDocument reference) { references.put(key(reference.tenantId(), reference.referenceDocumentId()), reference); referenceVersions.computeIfAbsent(key(reference.tenantId(), reference.referenceDocumentId()), ignored -> new ConcurrentHashMap<>()).put(reference.activeVersion(), toReferenceVersion(reference, actor(reference), reference.summary())); return reference; }
  @Override public List<ReferenceDocument> referenceDocuments(String tenantId) { return references.values().stream().filter(reference -> tenantId.equals(reference.tenantId())).toList(); }
  @Override public Optional<ReferenceVersion> referenceVersion(String tenantId, String referenceDocumentId, int version) { return Optional.ofNullable(referenceVersions.getOrDefault(key(tenantId, referenceDocumentId), Map.of()).get(version)); }
  @Override public List<ReferenceVersion> referenceVersions(String tenantId, String referenceDocumentId) { return referenceVersions.getOrDefault(key(tenantId, referenceDocumentId), Map.of()).values().stream().sorted(Comparator.comparingInt(ReferenceVersion::version)).toList(); }
  @Override public ReferenceDocument saveReferenceDocumentVersion(DocumentVersionSave command) {
    var existing = referenceDocument(command.tenantId(), command.documentId()).orElseThrow(() -> new IllegalStateException("reference-document-not-found"));
    if (existing.activeVersion() != command.expectedCurrentVersion()) throw new IllegalStateException("stale-current-version");
    return saveReferenceDocument(new ReferenceDocument(existing.tenantId(), existing.referenceDocumentId(), existing.stableReferenceId(), existing.title(), existing.summary(), existing.whenToConsult(), existing.referenceType(), existing.accessLevel(), existing.tags(), existing.status(), existing.activeVersion() + 1, command.contentBody(), checksum(command.contentBody()), existing.seedProvenance(), existing.createdAt(), timestamp(command.createdAt())), command.actorAccountId(), command.editSessionTranscriptSummary());
  }
  @Override public ReferenceDocument restoreReferenceDocumentVersion(DocumentVersionRestore command) {
    var existing = referenceDocument(command.tenantId(), command.documentId()).orElseThrow(() -> new IllegalStateException("reference-document-not-found"));
    if (command.version() < 1 || command.version() >= existing.activeVersion()) throw new IllegalStateException("historical-version-required");
    var restored = referenceVersion(command.tenantId(), command.documentId(), command.version()).orElseThrow(() -> new IllegalStateException("reference-version-not-found"));
    var summary = "Restored from version " + command.version();
    return saveReferenceDocument(new ReferenceDocument(existing.tenantId(), existing.referenceDocumentId(), existing.stableReferenceId(), existing.title(), existing.summary(), existing.whenToConsult(), existing.referenceType(), existing.accessLevel(), existing.tags(), existing.status(), existing.activeVersion() + 1, restored.contentBody(), checksum(restored.contentBody()), existing.seedProvenance(), existing.createdAt(), timestamp(command.createdAt())), command.actorAccountId(), summary);
  }
  @Override public void deleteReferenceDocument(String tenantId, String referenceDocumentId, String actorAccountId, Instant deletedAt) { references.remove(key(tenantId, referenceDocumentId)); referenceVersions.remove(key(tenantId, referenceDocumentId)); }

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

  private PromptDocument savePromptDocument(PromptDocument prompt, String actorAccountId, String transcriptSummary) { prompts.put(key(prompt.tenantId(), prompt.promptDocumentId()), prompt); promptVersions.computeIfAbsent(key(prompt.tenantId(), prompt.promptDocumentId()), ignored -> new ConcurrentHashMap<>()).put(prompt.activeVersion(), toPromptVersion(prompt, actorAccountId, transcriptSummary)); return prompt; }
  private SkillDocument saveSkillDocument(SkillDocument skill, String actorAccountId, String transcriptSummary) { skills.put(key(skill.tenantId(), skill.skillDocumentId()), skill); skillVersions.computeIfAbsent(key(skill.tenantId(), skill.skillDocumentId()), ignored -> new ConcurrentHashMap<>()).put(skill.activeVersion(), toSkillVersion(skill, actorAccountId, transcriptSummary)); return skill; }
  private ReferenceDocument saveReferenceDocument(ReferenceDocument reference, String actorAccountId, String transcriptSummary) { references.put(key(reference.tenantId(), reference.referenceDocumentId()), reference); referenceVersions.computeIfAbsent(key(reference.tenantId(), reference.referenceDocumentId()), ignored -> new ConcurrentHashMap<>()).put(reference.activeVersion(), toReferenceVersion(reference, actorAccountId, transcriptSummary)); return reference; }

  private static PromptVersion toPromptVersion(PromptDocument document, String actorAccountId, String transcriptSummary) { return new PromptVersion(document.tenantId(), document.promptDocumentId(), document.activeVersion(), document.agentDefinitionId(), document.title(), document.promptType(), document.status(), document.contentBody(), document.contentChecksum(), document.changeSummary(), document.seedProvenance(), document.updatedAt(), document.updatedAt(), document.status() == AgentLifecycleStatus.ACTIVE ? document.updatedAt() : null, actorAccountId, transcriptSummary); }
  private static SkillVersion toSkillVersion(SkillDocument document, String actorAccountId, String transcriptSummary) { return new SkillVersion(document.tenantId(), document.skillDocumentId(), document.stableSkillId(), document.activeVersion(), document.title(), document.purpose(), document.whenToUse(), document.tags(), document.status(), document.contentBody(), document.contentChecksum(), document.seedProvenance(), document.updatedAt(), document.updatedAt(), document.status() == AgentLifecycleStatus.ACTIVE ? document.updatedAt() : null, actorAccountId, transcriptSummary); }
  private static ReferenceVersion toReferenceVersion(ReferenceDocument document, String actorAccountId, String transcriptSummary) { return new ReferenceVersion(document.tenantId(), document.referenceDocumentId(), document.stableReferenceId(), document.activeVersion(), document.title(), document.summary(), document.whenToConsult(), document.referenceType(), document.accessLevel(), document.tags(), document.status(), document.contentBody(), document.contentChecksum(), document.seedProvenance(), document.updatedAt(), document.updatedAt(), document.status() == AgentLifecycleStatus.ACTIVE ? document.updatedAt() : null, actorAccountId, transcriptSummary); }

  private static String actor(PromptDocument document) { return document.seedProvenance() == null ? "system" : document.seedProvenance().importerActor(); }
  private static String actor(SkillDocument document) { return document.seedProvenance() == null ? "system" : document.seedProvenance().importerActor(); }
  private static String actor(ReferenceDocument document) { return document.seedProvenance() == null ? "system" : document.seedProvenance().importerActor(); }
  private static Instant timestamp(Instant value) { return value == null ? Instant.EPOCH : value; }
  private static String checksum(String content) { try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(String.valueOf(content).getBytes(StandardCharsets.UTF_8))); } catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); } }

  private String key(String tenantId, String recordId) { return tenantId + ":" + recordId; }
}

package ai.first.application.foundation.agent;

import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentReferenceManifest;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import ai.first.domain.foundation.agent.ModelConfigRef;
import ai.first.domain.foundation.agent.ModelPolicy;
import ai.first.domain.foundation.agent.PromptDocument;
import ai.first.domain.foundation.agent.PromptVersion;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.agent.ReferenceVersion;
import ai.first.domain.foundation.agent.SkillDocument;
import ai.first.domain.foundation.agent.SkillVersion;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Persistence port for governed agent behavior records. Normal runtime must bind an Akka-backed implementation; in-memory test adapters are test or explicit inspection aids only. */
public interface AgentBehaviorRepository {
  Optional<AgentDefinition> agentDefinition(String tenantId, String agentDefinitionId);
  AgentDefinition saveAgentDefinition(AgentDefinition definition);
  List<AgentDefinition> agentDefinitions(String tenantId);

  Optional<PromptDocument> promptDocument(String tenantId, String promptDocumentId);
  PromptDocument savePromptDocument(PromptDocument prompt);
  default Optional<PromptVersion> promptVersion(String tenantId, String promptDocumentId, int version) { throw unsupportedVersionLifecycle(); }
  default List<PromptVersion> promptVersions(String tenantId, String promptDocumentId) { throw unsupportedVersionLifecycle(); }
  default PromptDocument savePromptDocumentVersion(DocumentVersionSave command) { throw unsupportedVersionLifecycle(); }
  default PromptDocument restorePromptDocumentVersion(DocumentVersionRestore command) { throw unsupportedVersionLifecycle(); }

  Optional<SkillDocument> skillDocument(String tenantId, String skillDocumentId);
  SkillDocument saveSkillDocument(SkillDocument skill);
  List<SkillDocument> skillDocuments(String tenantId);
  default Optional<SkillVersion> skillVersion(String tenantId, String skillDocumentId, int version) { throw unsupportedVersionLifecycle(); }
  default List<SkillVersion> skillVersions(String tenantId, String skillDocumentId) { throw unsupportedVersionLifecycle(); }
  default SkillDocument saveSkillDocumentVersion(DocumentVersionSave command) { throw unsupportedVersionLifecycle(); }
  default SkillDocument restoreSkillDocumentVersion(DocumentVersionRestore command) { throw unsupportedVersionLifecycle(); }
  default void deleteSkillDocument(String tenantId, String skillDocumentId, String actorAccountId, Instant deletedAt) { throw unsupportedVersionLifecycle(); }

  Optional<ReferenceDocument> referenceDocument(String tenantId, String referenceDocumentId);
  ReferenceDocument saveReferenceDocument(ReferenceDocument reference);
  List<ReferenceDocument> referenceDocuments(String tenantId);
  default Optional<ReferenceVersion> referenceVersion(String tenantId, String referenceDocumentId, int version) { throw unsupportedVersionLifecycle(); }
  default List<ReferenceVersion> referenceVersions(String tenantId, String referenceDocumentId) { throw unsupportedVersionLifecycle(); }
  default ReferenceDocument saveReferenceDocumentVersion(DocumentVersionSave command) { throw unsupportedVersionLifecycle(); }
  default ReferenceDocument restoreReferenceDocumentVersion(DocumentVersionRestore command) { throw unsupportedVersionLifecycle(); }
  default void deleteReferenceDocument(String tenantId, String referenceDocumentId, String actorAccountId, Instant deletedAt) { throw unsupportedVersionLifecycle(); }

  Optional<AgentSkillManifest> skillManifest(String tenantId, String manifestId);
  AgentSkillManifest saveSkillManifest(AgentSkillManifest manifest);

  Optional<AgentReferenceManifest> referenceManifest(String tenantId, String manifestId);
  AgentReferenceManifest saveReferenceManifest(AgentReferenceManifest manifest);

  Optional<ToolPermissionBoundary> toolBoundary(String tenantId, String boundaryId);
  ToolPermissionBoundary saveToolBoundary(ToolPermissionBoundary boundary);

  Optional<ModelConfigRef> modelConfigRef(String tenantId, String modelConfigRefId);
  ModelConfigRef saveModelConfigRef(ModelConfigRef modelConfigRef);

  Optional<ModelPolicy> modelPolicy(String tenantId, String modelPolicyRefId);
  ModelPolicy saveModelPolicy(ModelPolicy modelPolicy);

  private static UnsupportedOperationException unsupportedVersionLifecycle() {
    return new UnsupportedOperationException("agent-document-version-lifecycle-not-bound");
  }

  record DocumentVersionSave(String tenantId, String documentId, int expectedCurrentVersion, String contentBody, String actorAccountId, String changeSummary, String editSessionTranscriptSummary, Instant createdAt) {}
  record DocumentVersionRestore(String tenantId, String documentId, int version, String actorAccountId, Instant createdAt) {}
}

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
import java.util.Optional;

/** Persistence port for governed agent behavior records. Normal runtime must bind an Akka-backed implementation; local/demo adapters are test or explicit inspection aids only. */
public interface AgentBehaviorRepository {
  Optional<AgentDefinition> agentDefinition(String tenantId, String agentDefinitionId);
  AgentDefinition saveAgentDefinition(AgentDefinition definition);
  List<AgentDefinition> agentDefinitions(String tenantId);

  Optional<PromptDocument> promptDocument(String tenantId, String promptDocumentId);
  PromptDocument savePromptDocument(PromptDocument prompt);

  Optional<SkillDocument> skillDocument(String tenantId, String skillDocumentId);
  SkillDocument saveSkillDocument(SkillDocument skill);
  List<SkillDocument> skillDocuments(String tenantId);

  Optional<ReferenceDocument> referenceDocument(String tenantId, String referenceDocumentId);
  ReferenceDocument saveReferenceDocument(ReferenceDocument reference);
  List<ReferenceDocument> referenceDocuments(String tenantId);

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
}

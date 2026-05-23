package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentReferenceManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentSkillManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ModelConfigRef;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ModelPolicy;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ReferenceDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SkillDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import java.util.List;
import java.util.Optional;

/** Persistence port for governed agent behavior records. Replace the in-memory adapter with Akka entities/views in production slices. */
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

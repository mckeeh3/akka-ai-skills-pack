package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
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

/**
 * Akka-backed adapter for {@link AgentBehaviorRepository}.
 *
 * <p>This preserves the starter repository port while moving governed agent definitions into the
 * first-class {@link AgentDefinitionEntity}. The remaining governed records still use {@link
 * DurableAgentBehaviorRepositoryEntity} until their own first-class components are added. The static
 * demo wiring still uses the in-memory adapter so a scaffold runs immediately; production-ready
 * generated apps should bind this adapter where a {@link ComponentClient} is available.
 */
public final class AkkaAgentBehaviorRepository implements AgentBehaviorRepository {
  private final ComponentClient componentClient;
  private final String behaviorRepositoryEntityId;

  public AkkaAgentBehaviorRepository(ComponentClient componentClient) {
    this(componentClient, DurableAgentBehaviorRepositoryEntity.ENTITY_ID);
  }

  public AkkaAgentBehaviorRepository(ComponentClient componentClient, String behaviorRepositoryEntityId) {
    this.componentClient = componentClient;
    this.behaviorRepositoryEntityId = behaviorRepositoryEntityId;
  }

  @Override
  public Optional<AgentDefinition> agentDefinition(String tenantId, String agentDefinitionId) {
    return componentClient
        .forEventSourcedEntity(AgentDefinitionEntity.entityId(tenantId, agentDefinitionId))
        .method(AgentDefinitionEntity::detail)
        .invoke(new AgentDefinitionEntity.AgentDefinitionQuery(tenantId, agentDefinitionId));
  }

  @Override
  public AgentDefinition saveAgentDefinition(AgentDefinition definition) {
    return componentClient
        .forEventSourcedEntity(AgentDefinitionEntity.entityId(definition.tenantId(), definition.agentDefinitionId()))
        .method(AgentDefinitionEntity::save)
        .invoke(definition);
  }

  @Override
  public List<AgentDefinition> agentDefinitions(String tenantId) {
    return componentClient
        .forView()
        .method(AgentDefinitionView::agentCatalog)
        .invoke(new AgentDefinitionView.AgentCatalogQuery(tenantId))
        .agents()
        .stream()
        .map(AkkaAgentBehaviorRepository::toAgentDefinition)
        .toList();
  }

  @Override
  public Optional<PromptDocument> promptDocument(String tenantId, String promptDocumentId) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::promptDocument)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, promptDocumentId));
  }

  @Override
  public PromptDocument savePromptDocument(PromptDocument prompt) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::savePromptDocument).invoke(prompt);
  }

  @Override
  public Optional<SkillDocument> skillDocument(String tenantId, String skillDocumentId) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::skillDocument)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, skillDocumentId));
  }

  @Override
  public SkillDocument saveSkillDocument(SkillDocument skill) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::saveSkillDocument).invoke(skill);
  }

  @Override
  public List<SkillDocument> skillDocuments(String tenantId) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::skillDocuments).invoke(tenantId);
  }

  @Override
  public Optional<ReferenceDocument> referenceDocument(String tenantId, String referenceDocumentId) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::referenceDocument)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, referenceDocumentId));
  }

  @Override
  public ReferenceDocument saveReferenceDocument(ReferenceDocument reference) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::saveReferenceDocument).invoke(reference);
  }

  @Override
  public List<ReferenceDocument> referenceDocuments(String tenantId) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::referenceDocuments).invoke(tenantId);
  }

  @Override
  public Optional<AgentSkillManifest> skillManifest(String tenantId, String manifestId) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::skillManifest)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, manifestId));
  }

  @Override
  public AgentSkillManifest saveSkillManifest(AgentSkillManifest manifest) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::saveSkillManifest).invoke(manifest);
  }

  @Override
  public Optional<AgentReferenceManifest> referenceManifest(String tenantId, String manifestId) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::referenceManifest)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, manifestId));
  }

  @Override
  public AgentReferenceManifest saveReferenceManifest(AgentReferenceManifest manifest) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::saveReferenceManifest).invoke(manifest);
  }

  @Override
  public Optional<ToolPermissionBoundary> toolBoundary(String tenantId, String boundaryId) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::toolBoundary)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, boundaryId));
  }

  @Override
  public ToolPermissionBoundary saveToolBoundary(ToolPermissionBoundary boundary) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::saveToolBoundary).invoke(boundary);
  }

  @Override
  public Optional<ModelConfigRef> modelConfigRef(String tenantId, String modelConfigRefId) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::modelConfigRef)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, modelConfigRefId));
  }

  @Override
  public ModelConfigRef saveModelConfigRef(ModelConfigRef modelConfigRef) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::saveModelConfigRef).invoke(modelConfigRef);
  }

  @Override
  public Optional<ModelPolicy> modelPolicy(String tenantId, String modelPolicyRefId) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::modelPolicy)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, modelPolicyRefId));
  }

  @Override
  public ModelPolicy saveModelPolicy(ModelPolicy modelPolicy) {
    return componentClient.forKeyValueEntity(behaviorRepositoryEntityId).method(DurableAgentBehaviorRepositoryEntity::saveModelPolicy).invoke(modelPolicy);
  }

  private static AgentDefinition toAgentDefinition(AgentDefinitionView.AgentDefinitionRow row) {
    return new AgentDefinition(
        row.tenantId(),
        row.agentDefinitionId(),
        row.displayName(),
        row.description(),
        AgentDefinition.Placement.valueOf(row.placement()),
        row.functionalAreaId(),
        AgentDefinition.AuthorityLevel.valueOf(row.authorityLevel()),
        AgentLifecycleStatus.valueOf(row.lifecycleStatus()),
        row.promptDocumentId(),
        row.activePromptVersion(),
        row.skillManifestId(),
        row.activeSkillManifestVersion(),
        row.referenceManifestId(),
        row.activeReferenceManifestVersion(),
        row.toolBoundaryId(),
        row.activeToolBoundaryVersion(),
        row.modelConfigRefId(),
        row.modelPolicyRefId(),
        row.runtimeClassRef(),
        row.traceRequirements(),
        null,
        row.createdAt(),
        row.updatedAt());
  }
}

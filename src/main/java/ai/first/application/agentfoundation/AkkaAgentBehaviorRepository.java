package ai.first.application.agentfoundation;

import akka.javasdk.client.ComponentClient;
import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.AgentReferenceManifest;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import ai.first.domain.foundation.agent.ModelConfigRef;
import ai.first.domain.foundation.agent.ModelPolicy;
import ai.first.domain.foundation.agent.PromptDocument;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.agent.SkillDocument;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import java.util.List;
import java.util.Optional;

/**
 * Akka-backed adapter for {@link AgentBehaviorRepository}.
 *
 * <p>This preserves the starter repository port while moving governed agent definitions into the
 * first-class {@link AgentDefinitionEntity}. The remaining governed records still use {@link
 * DurableAgentBehaviorRepositoryEntity} until their own first-class components are added. Normal
 * endpoint and Akka Agent runtime wiring binds this adapter as soon as a {@link ComponentClient} is
 * available; test doubles live only in test source.
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
    return componentClient
        .forEventSourcedEntity(PromptDocumentEntity.entityId(tenantId, promptDocumentId))
        .method(PromptDocumentEntity::detail)
        .invoke(new PromptDocumentEntity.DocumentQuery(tenantId, promptDocumentId));
  }

  @Override
  public PromptDocument savePromptDocument(PromptDocument prompt) {
    return componentClient
        .forEventSourcedEntity(PromptDocumentEntity.entityId(prompt.tenantId(), prompt.promptDocumentId()))
        .method(PromptDocumentEntity::save)
        .invoke(prompt);
  }

  @Override
  public Optional<SkillDocument> skillDocument(String tenantId, String skillDocumentId) {
    return componentClient
        .forEventSourcedEntity(SkillDocumentEntity.entityId(tenantId, skillDocumentId))
        .method(SkillDocumentEntity::detail)
        .invoke(new SkillDocumentEntity.DocumentQuery(tenantId, skillDocumentId));
  }

  @Override
  public SkillDocument saveSkillDocument(SkillDocument skill) {
    return componentClient
        .forEventSourcedEntity(SkillDocumentEntity.entityId(skill.tenantId(), skill.skillDocumentId()))
        .method(SkillDocumentEntity::save)
        .invoke(skill);
  }

  @Override
  public List<SkillDocument> skillDocuments(String tenantId) {
    return componentClient
        .forView()
        .method(SkillDocumentView::catalog)
        .invoke(new SkillDocumentView.CatalogQuery(tenantId))
        .documents()
        .stream()
        .map(AkkaAgentBehaviorRepository::toSkillDocument)
        .toList();
  }

  @Override
  public Optional<ReferenceDocument> referenceDocument(String tenantId, String referenceDocumentId) {
    return componentClient
        .forEventSourcedEntity(ReferenceDocumentEntity.entityId(tenantId, referenceDocumentId))
        .method(ReferenceDocumentEntity::detail)
        .invoke(new ReferenceDocumentEntity.DocumentQuery(tenantId, referenceDocumentId));
  }

  @Override
  public ReferenceDocument saveReferenceDocument(ReferenceDocument reference) {
    return componentClient
        .forEventSourcedEntity(ReferenceDocumentEntity.entityId(reference.tenantId(), reference.referenceDocumentId()))
        .method(ReferenceDocumentEntity::save)
        .invoke(reference);
  }

  @Override
  public List<ReferenceDocument> referenceDocuments(String tenantId) {
    return componentClient
        .forView()
        .method(ReferenceDocumentView::catalog)
        .invoke(new ReferenceDocumentView.CatalogQuery(tenantId))
        .documents()
        .stream()
        .map(AkkaAgentBehaviorRepository::toReferenceDocument)
        .toList();
  }

  @Override
  public Optional<AgentSkillManifest> skillManifest(String tenantId, String manifestId) {
    return componentClient
        .forEventSourcedEntity(AgentSkillManifestEntity.entityId(tenantId, manifestId))
        .method(AgentSkillManifestEntity::detail)
        .invoke(new AgentSkillManifestEntity.ManifestQuery(tenantId, manifestId));
  }

  @Override
  public AgentSkillManifest saveSkillManifest(AgentSkillManifest manifest) {
    return componentClient
        .forEventSourcedEntity(AgentSkillManifestEntity.entityId(manifest.tenantId(), manifest.manifestId()))
        .method(AgentSkillManifestEntity::save)
        .invoke(manifest);
  }

  @Override
  public Optional<AgentReferenceManifest> referenceManifest(String tenantId, String manifestId) {
    return componentClient
        .forEventSourcedEntity(AgentReferenceManifestEntity.entityId(tenantId, manifestId))
        .method(AgentReferenceManifestEntity::detail)
        .invoke(new AgentReferenceManifestEntity.ManifestQuery(tenantId, manifestId));
  }

  @Override
  public AgentReferenceManifest saveReferenceManifest(AgentReferenceManifest manifest) {
    return componentClient
        .forEventSourcedEntity(AgentReferenceManifestEntity.entityId(manifest.tenantId(), manifest.manifestId()))
        .method(AgentReferenceManifestEntity::save)
        .invoke(manifest);
  }

  @Override
  public Optional<ToolPermissionBoundary> toolBoundary(String tenantId, String boundaryId) {
    return componentClient
        .forEventSourcedEntity(ToolPermissionBoundaryEntity.entityId(tenantId, boundaryId))
        .method(ToolPermissionBoundaryEntity::detail)
        .invoke(new ToolPermissionBoundaryEntity.BoundaryQuery(tenantId, boundaryId));
  }

  @Override
  public ToolPermissionBoundary saveToolBoundary(ToolPermissionBoundary boundary) {
    return componentClient
        .forEventSourcedEntity(ToolPermissionBoundaryEntity.entityId(boundary.tenantId(), boundary.boundaryId()))
        .method(ToolPermissionBoundaryEntity::save)
        .invoke(boundary);
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

  private static SkillDocument toSkillDocument(SkillDocumentView.SkillDocumentRow row) {
    return new SkillDocument(
        row.tenantId(),
        row.skillDocumentId(),
        row.stableSkillId(),
        row.title(),
        row.purpose(),
        row.whenToUse(),
        row.tags(),
        AgentLifecycleStatus.valueOf(row.lifecycleStatus()),
        row.activeVersion(),
        null,
        row.contentChecksum(),
        null,
        row.createdAt(),
        row.updatedAt());
  }

  private static ReferenceDocument toReferenceDocument(ReferenceDocumentView.ReferenceDocumentRow row) {
    return new ReferenceDocument(
        row.tenantId(),
        row.referenceDocumentId(),
        row.stableReferenceId(),
        row.title(),
        row.summary(),
        row.whenToConsult(),
        ReferenceDocument.ReferenceType.valueOf(row.referenceType()),
        row.accessLevel(),
        row.tags(),
        AgentLifecycleStatus.valueOf(row.lifecycleStatus()),
        row.activeVersion(),
        null,
        row.contentChecksum(),
        null,
        row.createdAt(),
        row.updatedAt());
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

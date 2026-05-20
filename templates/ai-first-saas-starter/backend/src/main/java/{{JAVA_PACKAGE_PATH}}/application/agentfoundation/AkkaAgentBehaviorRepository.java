package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentSkillManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SkillDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import java.util.List;
import java.util.Optional;

/**
 * Akka-backed adapter for {@link AgentBehaviorRepository}.
 *
 * <p>This preserves the starter repository port while moving governed agent behavior records into
 * {@link DurableAgentBehaviorRepositoryEntity}. The static demo wiring still uses the in-memory
 * adapter so a scaffold runs immediately; production-ready generated apps should bind this adapter
 * where a {@link ComponentClient} is available.
 */
public final class AkkaAgentBehaviorRepository implements AgentBehaviorRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaAgentBehaviorRepository(ComponentClient componentClient) {
    this(componentClient, DurableAgentBehaviorRepositoryEntity.ENTITY_ID);
  }

  public AkkaAgentBehaviorRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  @Override
  public Optional<AgentDefinition> agentDefinition(String tenantId, String agentDefinitionId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAgentBehaviorRepositoryEntity::agentDefinition)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, agentDefinitionId));
  }

  @Override
  public AgentDefinition saveAgentDefinition(AgentDefinition definition) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAgentBehaviorRepositoryEntity::saveAgentDefinition).invoke(definition);
  }

  @Override
  public List<AgentDefinition> agentDefinitions(String tenantId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAgentBehaviorRepositoryEntity::agentDefinitions).invoke(tenantId);
  }

  @Override
  public Optional<PromptDocument> promptDocument(String tenantId, String promptDocumentId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAgentBehaviorRepositoryEntity::promptDocument)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, promptDocumentId));
  }

  @Override
  public PromptDocument savePromptDocument(PromptDocument prompt) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAgentBehaviorRepositoryEntity::savePromptDocument).invoke(prompt);
  }

  @Override
  public Optional<SkillDocument> skillDocument(String tenantId, String skillDocumentId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAgentBehaviorRepositoryEntity::skillDocument)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, skillDocumentId));
  }

  @Override
  public SkillDocument saveSkillDocument(SkillDocument skill) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAgentBehaviorRepositoryEntity::saveSkillDocument).invoke(skill);
  }

  @Override
  public List<SkillDocument> skillDocuments(String tenantId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAgentBehaviorRepositoryEntity::skillDocuments).invoke(tenantId);
  }

  @Override
  public Optional<AgentSkillManifest> skillManifest(String tenantId, String manifestId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAgentBehaviorRepositoryEntity::skillManifest)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, manifestId));
  }

  @Override
  public AgentSkillManifest saveSkillManifest(AgentSkillManifest manifest) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAgentBehaviorRepositoryEntity::saveSkillManifest).invoke(manifest);
  }

  @Override
  public Optional<ToolPermissionBoundary> toolBoundary(String tenantId, String boundaryId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAgentBehaviorRepositoryEntity::toolBoundary)
        .invoke(new DurableAgentBehaviorRepositoryEntity.RecordQuery(tenantId, boundaryId));
  }

  @Override
  public ToolPermissionBoundary saveToolBoundary(ToolPermissionBoundary boundary) {
    return componentClient.forKeyValueEntity(entityId).method(DurableAgentBehaviorRepositoryEntity::saveToolBoundary).invoke(boundary);
  }
}

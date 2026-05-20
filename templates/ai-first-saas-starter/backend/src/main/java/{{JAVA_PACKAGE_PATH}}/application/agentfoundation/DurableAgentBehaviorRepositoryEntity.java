package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentBehaviorRepositoryState;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentSkillManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SkillDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import java.util.List;
import java.util.Optional;

/**
 * Durable Akka component seam for governed agent behavior repository state.
 *
 * <p>The scaffold keeps {@link InMemoryAgentBehaviorRepository} as the local/demo default, but
 * generated apps can bind {@link AkkaAgentBehaviorRepository} to this component without changing
 * seed import, prompt assembly, {@code readSkill(skillId)}, or behavior proposal services. This
 * Key Value Entity stores the current approved/active governed behavior records; later slices can
 * replace individual lifecycle-heavy artifacts with Event Sourced Entities and project the same port.
 */
@Component(id = "starter-agent-behavior-repository")
public class DurableAgentBehaviorRepositoryEntity extends KeyValueEntity<AgentBehaviorRepositoryState> {
  public static final String ENTITY_ID = "starter-agent-behavior-repository";

  @Override
  public AgentBehaviorRepositoryState emptyState() {
    return AgentBehaviorRepositoryState.empty();
  }

  public ReadOnlyEffect<Optional<AgentDefinition>> agentDefinition(RecordQuery query) {
    return effects().reply(currentState().agentDefinition(query.tenantId(), query.recordId()));
  }

  public Effect<AgentDefinition> saveAgentDefinition(AgentDefinition definition) {
    return effects().updateState(currentState().saveAgentDefinition(definition)).thenReply(() -> definition);
  }

  public ReadOnlyEffect<List<AgentDefinition>> agentDefinitions(String tenantId) {
    return effects().reply(currentState().agentDefinitions(tenantId));
  }

  public ReadOnlyEffect<Optional<PromptDocument>> promptDocument(RecordQuery query) {
    return effects().reply(currentState().promptDocument(query.tenantId(), query.recordId()));
  }

  public Effect<PromptDocument> savePromptDocument(PromptDocument prompt) {
    return effects().updateState(currentState().savePromptDocument(prompt)).thenReply(() -> prompt);
  }

  public ReadOnlyEffect<Optional<SkillDocument>> skillDocument(RecordQuery query) {
    return effects().reply(currentState().skillDocument(query.tenantId(), query.recordId()));
  }

  public Effect<SkillDocument> saveSkillDocument(SkillDocument skill) {
    return effects().updateState(currentState().saveSkillDocument(skill)).thenReply(() -> skill);
  }

  public ReadOnlyEffect<List<SkillDocument>> skillDocuments(String tenantId) {
    return effects().reply(currentState().skillDocuments(tenantId));
  }

  public ReadOnlyEffect<Optional<AgentSkillManifest>> skillManifest(RecordQuery query) {
    return effects().reply(currentState().skillManifest(query.tenantId(), query.recordId()));
  }

  public Effect<AgentSkillManifest> saveSkillManifest(AgentSkillManifest manifest) {
    return effects().updateState(currentState().saveSkillManifest(manifest)).thenReply(() -> manifest);
  }

  public ReadOnlyEffect<Optional<ToolPermissionBoundary>> toolBoundary(RecordQuery query) {
    return effects().reply(currentState().toolBoundary(query.tenantId(), query.recordId()));
  }

  public Effect<ToolPermissionBoundary> saveToolBoundary(ToolPermissionBoundary boundary) {
    return effects().updateState(currentState().saveToolBoundary(boundary)).thenReply(() -> boundary);
  }

  public record RecordQuery(String tenantId, String recordId) {}
}

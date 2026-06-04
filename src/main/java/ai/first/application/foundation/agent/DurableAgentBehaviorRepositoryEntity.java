package ai.first.application.foundation.agent;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import ai.first.domain.foundation.agent.AgentBehaviorRepositoryState;
import ai.first.domain.foundation.agent.AgentDefinition;
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
 * Durable Akka component seam for governed agent behavior repository state.
 *
 * <p>Normal endpoint and Akka Agent runtime wiring binds {@link AkkaAgentBehaviorRepository} to this
 * component without changing seed import, prompt assembly, {@code readSkill(skillId)},
 * {@code readReferenceDoc(referenceId)}, or behavior proposal services. Test-only substitutes live
 * under test source, not normal runtime wiring. This Key Value Entity stores the current
 * approved/active governed behavior records; later slices can
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

  public ReadOnlyEffect<Optional<ReferenceDocument>> referenceDocument(RecordQuery query) {
    return effects().reply(currentState().referenceDocument(query.tenantId(), query.recordId()));
  }

  public Effect<ReferenceDocument> saveReferenceDocument(ReferenceDocument reference) {
    return effects().updateState(currentState().saveReferenceDocument(reference)).thenReply(() -> reference);
  }

  public ReadOnlyEffect<List<ReferenceDocument>> referenceDocuments(String tenantId) {
    return effects().reply(currentState().referenceDocuments(tenantId));
  }

  public ReadOnlyEffect<Optional<AgentSkillManifest>> skillManifest(RecordQuery query) {
    return effects().reply(currentState().skillManifest(query.tenantId(), query.recordId()));
  }

  public Effect<AgentSkillManifest> saveSkillManifest(AgentSkillManifest manifest) {
    return effects().updateState(currentState().saveSkillManifest(manifest)).thenReply(() -> manifest);
  }

  public ReadOnlyEffect<Optional<AgentReferenceManifest>> referenceManifest(RecordQuery query) {
    return effects().reply(currentState().referenceManifest(query.tenantId(), query.recordId()));
  }

  public Effect<AgentReferenceManifest> saveReferenceManifest(AgentReferenceManifest manifest) {
    return effects().updateState(currentState().saveReferenceManifest(manifest)).thenReply(() -> manifest);
  }

  public ReadOnlyEffect<Optional<ToolPermissionBoundary>> toolBoundary(RecordQuery query) {
    return effects().reply(currentState().toolBoundary(query.tenantId(), query.recordId()));
  }

  public Effect<ToolPermissionBoundary> saveToolBoundary(ToolPermissionBoundary boundary) {
    return effects().updateState(currentState().saveToolBoundary(boundary)).thenReply(() -> boundary);
  }

  public ReadOnlyEffect<Optional<ModelConfigRef>> modelConfigRef(RecordQuery query) {
    return effects().reply(currentState().modelConfigRef(query.tenantId(), query.recordId()));
  }

  public Effect<ModelConfigRef> saveModelConfigRef(ModelConfigRef modelConfigRef) {
    return effects().updateState(currentState().saveModelConfigRef(modelConfigRef)).thenReply(() -> modelConfigRef);
  }

  public ReadOnlyEffect<Optional<ModelPolicy>> modelPolicy(RecordQuery query) {
    return effects().reply(currentState().modelPolicy(query.tenantId(), query.recordId()));
  }

  public Effect<ModelPolicy> saveModelPolicy(ModelPolicy modelPolicy) {
    return effects().updateState(currentState().saveModelPolicy(modelPolicy)).thenReply(() -> modelPolicy);
  }

  public record RecordQuery(String tenantId, String recordId) {}
}

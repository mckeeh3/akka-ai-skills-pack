package ai.first.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import java.util.Optional;

/**
 * First-class tenant-scoped Event Sourced Entity for managed agent lifecycle/profile state.
 *
 * <p>This component is the authoritative carrier for {@link AgentDefinition}. Runtime callers use
 * {@link #activeRuntimeLookup(AgentDefinitionQuery)} so disabled, archived, draft, or cross-tenant
 * definitions fail closed before model invocation. Agent Admin surfaces use {@link #detail} and the
 * companion views for catalog/filter access.
 */
@Component(id = "agent-definition")
public class AgentDefinitionEntity extends EventSourcedEntity<AgentDefinitionEntity.State, AgentDefinitionEntity.Event> {

  public static String entityId(String tenantId, String agentDefinitionId) {
    return tenantId + "__" + agentDefinitionId;
  }

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<AgentDefinition>> detail(AgentDefinitionQuery query) {
    return effects().reply(currentState().definitionForTenant(query.tenantId()));
  }

  public ReadOnlyEffect<Optional<AgentDefinition>> activeRuntimeLookup(AgentDefinitionQuery query) {
    return effects().reply(
        currentState()
            .definitionForTenant(query.tenantId())
            .filter(definition -> definition.status() == AgentLifecycleStatus.ACTIVE));
  }

  /** Compatibility write path for seed import and existing AgentBehaviorRepository adapters. */
  public Effect<AgentDefinition> save(AgentDefinition definition) {
    var validation = validateDefinition(definition);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (definition.equals(currentState().definition())) {
      return effects().reply(definition);
    }
    return effects()
        .persist(new Event.DefinitionSaved(definition))
        .thenReply(State::definition);
  }

  public Effect<AgentDefinition> disable(LifecycleCommand command) {
    var current = currentState().definitionForTenant(command.tenantId());
    if (current.isEmpty()) {
      return effects().error("agent-definition-not-found");
    }
    var definition = current.get();
    if (definition.status() == AgentLifecycleStatus.DISABLED) {
      return effects().reply(definition);
    }
    if (definition.status() == AgentLifecycleStatus.ARCHIVED) {
      return effects().error("agent-definition-archived");
    }
    var disabled = withStatus(definition, AgentLifecycleStatus.DISABLED);
    return effects()
        .persist(new Event.DefinitionDisabled(disabled, command.reason(), command.changedByAccountId()))
        .thenReply(State::definition);
  }

  public Effect<AgentDefinition> archive(LifecycleCommand command) {
    var current = currentState().definitionForTenant(command.tenantId());
    if (current.isEmpty()) {
      return effects().error("agent-definition-not-found");
    }
    var definition = current.get();
    if (definition.status() == AgentLifecycleStatus.ARCHIVED) {
      return effects().reply(definition);
    }
    if (definition.status() == AgentLifecycleStatus.ACTIVE) {
      return effects().error("active-agent-definition-must-be-disabled-before-archive");
    }
    var archived = withStatus(definition, AgentLifecycleStatus.ARCHIVED);
    return effects()
        .persist(new Event.DefinitionArchived(archived, command.reason(), command.changedByAccountId()))
        .thenReply(State::definition);
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.DefinitionSaved saved -> new State(saved.definition());
      case Event.DefinitionDisabled disabled -> new State(disabled.definition());
      case Event.DefinitionArchived archived -> new State(archived.definition());
    };
  }

  private Optional<String> validateDefinition(AgentDefinition definition) {
    if (definition == null) {
      return Optional.of("agent-definition-required");
    }
    if (blank(definition.tenantId())) {
      return Optional.of("tenant-required");
    }
    if (blank(definition.agentDefinitionId())) {
      return Optional.of("agent-definition-id-required");
    }
    if (definition.tenantId().contains("|") || definition.agentDefinitionId().contains("|")) {
      return Optional.of("agent-definition-id-contains-reserved-character");
    }
    if (definition.status() == null) {
      return Optional.of("lifecycle-status-required");
    }
    if (definition.placement() == null) {
      return Optional.of("agent-placement-required");
    }
    return Optional.empty();
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  private static AgentDefinition withStatus(AgentDefinition definition, AgentLifecycleStatus status) {
    return new AgentDefinition(
        definition.tenantId(),
        definition.agentDefinitionId(),
        definition.displayName(),
        definition.description(),
        definition.placement(),
        definition.functionalAreaId(),
        definition.authorityLevel(),
        status,
        definition.promptDocumentId(),
        definition.activePromptVersion(),
        definition.skillManifestId(),
        definition.activeSkillManifestVersion(),
        definition.referenceManifestId(),
        definition.activeReferenceManifestVersion(),
        definition.toolBoundaryId(),
        definition.activeToolBoundaryVersion(),
        definition.modelConfigRefId(),
        definition.modelPolicyRefId(),
        definition.runtimeClassRef(),
        definition.traceRequirements(),
        definition.seedProvenance(),
        definition.createdAt(),
        definition.updatedAt());
  }

  public record State(AgentDefinition definition) {
    static State empty() {
      return new State(null);
    }

    Optional<AgentDefinition> definitionForTenant(String tenantId) {
      return Optional.ofNullable(definition).filter(candidate -> candidate.tenantId().equals(tenantId));
    }
  }

  public sealed interface Event {
    AgentDefinition definition();

    @TypeName("agent-definition-saved")
    record DefinitionSaved(AgentDefinition definition) implements Event {}

    @TypeName("agent-definition-disabled")
    record DefinitionDisabled(AgentDefinition definition, String reason, String changedByAccountId) implements Event {}

    @TypeName("agent-definition-archived")
    record DefinitionArchived(AgentDefinition definition, String reason, String changedByAccountId) implements Event {}
  }

  public record AgentDefinitionQuery(String tenantId, String agentDefinitionId) {}

  public record LifecycleCommand(String tenantId, String reason, String changedByAccountId) {}
}

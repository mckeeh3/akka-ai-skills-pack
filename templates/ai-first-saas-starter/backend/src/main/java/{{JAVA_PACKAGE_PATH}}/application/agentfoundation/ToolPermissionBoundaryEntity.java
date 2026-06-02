package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.GovernedArtifactLifecycleFact;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import java.util.List;
import java.util.Optional;

/** First-class tenant-scoped Event Sourced Entity for managed-agent ToolPermissionBoundary state. */
@Component(id = "tool-permission-boundary")
public class ToolPermissionBoundaryEntity extends EventSourcedEntity<ToolPermissionBoundaryEntity.State, ToolPermissionBoundaryEntity.Event> {
  public static String entityId(String tenantId, String boundaryId) {
    return tenantId + "__" + boundaryId;
  }

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<ToolPermissionBoundary>> detail(BoundaryQuery query) {
    return effects().reply(currentState().boundaryForTenant(query.tenantId()));
  }

  public ReadOnlyEffect<Optional<ToolPermissionBoundary>> activeRuntimeLookup(BoundaryQuery query) {
    return effects().reply(currentState().boundaryForTenant(query.tenantId())
        .filter(boundary -> boundary.status() == AgentLifecycleStatus.ACTIVE));
  }

  public ReadOnlyEffect<List<GovernedArtifactLifecycleFact>> history(BoundaryQuery query) {
    return effects().reply(currentState().historyForTenant(query.tenantId()));
  }

  /** Compatibility write path for seed import and repository adapters. */
  public Effect<ToolPermissionBoundary> save(ToolPermissionBoundary boundary) {
    var validation = validate(boundary);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (boundary.equals(currentState().boundary())) {
      return effects().persist(new Event.LifecycleFactAppended(noOpFact(boundary))).thenReply(State::boundary);
    }
    return effects().persist(new Event.ToolBoundarySaved(boundary, lifecycleFact(boundary))).thenReply(State::boundary);
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.ToolBoundarySaved saved -> currentState().save(saved.boundary(), saved.lifecycleFact());
      case Event.LifecycleFactAppended appended -> currentState().appendHistory(appended.lifecycleFact());
    };
  }

  public Effect<List<GovernedArtifactLifecycleFact>> appendLifecycleFact(GovernedArtifactLifecycleFact fact) {
    var validation = validateLifecycleFact(fact);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    return effects().persist(new Event.LifecycleFactAppended(fact)).thenReply(State::history);
  }

  private Optional<String> validateLifecycleFact(GovernedArtifactLifecycleFact fact) {
    if (fact == null) return Optional.of("lifecycle-fact-required");
    if (blank(fact.tenantId())) return Optional.of("tenant-required");
    if (fact.artifactType() != GovernedArtifactLifecycleFact.ArtifactType.TOOL_PERMISSION_BOUNDARY) return Optional.of("artifact-type-mismatch");
    if (!currentState().boundaryForTenant(fact.tenantId()).map(boundary -> boundary.boundaryId().equals(fact.artifactId())).orElse(false)) {
      return Optional.of("artifact-not-found-for-tenant");
    }
    if (fact.transition() == null) return Optional.of("transition-required");
    if (fact.occurredAt() == null) return Optional.of("occurred-at-required");
    return Optional.empty();
  }

  private Optional<String> validate(ToolPermissionBoundary boundary) {
    if (boundary == null) return Optional.of("tool-boundary-required");
    if (blank(boundary.tenantId())) return Optional.of("tenant-required");
    if (blank(boundary.boundaryId())) return Optional.of("tool-boundary-id-required");
    if (blank(boundary.agentDefinitionId())) return Optional.of("agent-definition-id-required");
    if (boundary.status() == null) return Optional.of("lifecycle-status-required");
    if (boundary.boundaryVersion() < 1) return Optional.of("boundary-version-required");
    for (var grant : boundary.allowedToolGrants()) {
      if (blank(grant.toolId())) return Optional.of("tool-id-required");
      if (grant.category() == null) return Optional.of("tool-category-required");
      if (blank(grant.capabilityId())) return Optional.of("capability-id-required");
      if (grant.allowedOperations().isEmpty()) return Optional.of("allowed-operations-required");
      if (grant.allowedModes().isEmpty()) return Optional.of("allowed-modes-required");
      if (grant.category() == ToolPermissionBoundary.Category.EXTERNAL_SIDE_EFFECT
          || "external_call".equalsIgnoreCase(grant.sideEffectLevel())
          || "billing".equalsIgnoreCase(grant.sideEffectLevel())
          || "security".equalsIgnoreCase(grant.sideEffectLevel())
          || "irreversible".equalsIgnoreCase(grant.sideEffectLevel())) {
        if ("bounded_autonomous".equalsIgnoreCase(grant.autonomy())) {
          return Optional.of("authority expansion requires approval-required/proposal-only grant before activation");
        }
      }
    }
    return Optional.empty();
  }

  private GovernedArtifactLifecycleFact lifecycleFact(ToolPermissionBoundary boundary) {
    var previous = currentState().boundary();
    return GovernedArtifactLifecycleFact.of(
        boundary.tenantId(),
        GovernedArtifactLifecycleFact.ArtifactType.TOOL_PERMISSION_BOUNDARY,
        boundary.boundaryId(),
        boundary.agentDefinitionId(),
        transition(previous == null ? null : previous.status(), boundary.status(), boundary.seedProvenance() != null),
        previous == null ? null : previous.status(),
        boundary.status(),
        previous == null ? 0 : previous.boundaryVersion(),
        boundary.boundaryVersion(),
        boundary.boundaryId() + ":v" + boundary.boundaryVersion(),
        boundary.checksum(),
        "system",
        boundary.seedProvenance() == null ? null : boundary.seedProvenance().correlationId(),
        previous == null ? "initial lifecycle import" : "lifecycle update",
        false,
        boundary.updatedAt());
  }

  private GovernedArtifactLifecycleFact noOpFact(ToolPermissionBoundary boundary) {
    return GovernedArtifactLifecycleFact.of(
        boundary.tenantId(),
        GovernedArtifactLifecycleFact.ArtifactType.TOOL_PERMISSION_BOUNDARY,
        boundary.boundaryId(),
        boundary.agentDefinitionId(),
        GovernedArtifactLifecycleFact.Transition.NO_OP,
        boundary.status(),
        boundary.status(),
        boundary.boundaryVersion(),
        boundary.boundaryVersion(),
        boundary.boundaryId() + ":v" + boundary.boundaryVersion(),
        boundary.checksum(),
        "system",
        boundary.seedProvenance() == null ? null : boundary.seedProvenance().correlationId(),
        "duplicate-save-no-op",
        false,
        boundary.updatedAt());
  }

  private static GovernedArtifactLifecycleFact.Transition transition(AgentLifecycleStatus previous, AgentLifecycleStatus next, boolean seeded) {
    if (previous == null && seeded) return GovernedArtifactLifecycleFact.Transition.SEED_IMPORTED;
    if (next == AgentLifecycleStatus.ACTIVE) return GovernedArtifactLifecycleFact.Transition.ACTIVATED;
    if (previous == null || next == AgentLifecycleStatus.DRAFT) return GovernedArtifactLifecycleFact.Transition.DRAFTED;
    if (next == AgentLifecycleStatus.DISABLED || next == AgentLifecycleStatus.DEPRECATED) return GovernedArtifactLifecycleFact.Transition.DEPRECATED;
    if (next == AgentLifecycleStatus.ARCHIVED) return GovernedArtifactLifecycleFact.Transition.ARCHIVED;
    if (next == AgentLifecycleStatus.APPROVED) return GovernedArtifactLifecycleFact.Transition.APPROVED;
    if (next == AgentLifecycleStatus.IN_REVIEW) return GovernedArtifactLifecycleFact.Transition.SUBMITTED;
    return GovernedArtifactLifecycleFact.Transition.REVIEWED;
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  public record State(ToolPermissionBoundary boundary, List<GovernedArtifactLifecycleFact> history) {
    static State empty() {
      return new State(null, List.of());
    }

    public State {
      history = List.copyOf(history == null ? List.of() : history);
    }

    State save(ToolPermissionBoundary boundary, GovernedArtifactLifecycleFact lifecycleFact) {
      var updatedHistory = new java.util.ArrayList<>(history);
      updatedHistory.add(lifecycleFact);
      return new State(boundary, updatedHistory);
    }

    State appendHistory(GovernedArtifactLifecycleFact lifecycleFact) {
      var updatedHistory = new java.util.ArrayList<>(history);
      updatedHistory.add(lifecycleFact);
      return new State(boundary, updatedHistory);
    }

    List<GovernedArtifactLifecycleFact> historyForTenant(String tenantId) {
      return history.stream().filter(fact -> fact.tenantId().equals(tenantId)).toList();
    }

    Optional<ToolPermissionBoundary> boundaryForTenant(String tenantId) {
      return Optional.ofNullable(boundary).filter(candidate -> candidate.tenantId().equals(tenantId));
    }
  }

  public sealed interface Event {
    ToolPermissionBoundary boundary();
    default GovernedArtifactLifecycleFact lifecycleFact() { return null; }

    @TypeName("tool-permission-boundary-saved")
    record ToolBoundarySaved(ToolPermissionBoundary boundary, GovernedArtifactLifecycleFact lifecycleFact) implements Event {}
    @TypeName("tool-permission-boundary-lifecycle-fact-appended")
    record LifecycleFactAppended(GovernedArtifactLifecycleFact lifecycleFact) implements Event {
      @Override public ToolPermissionBoundary boundary() { return null; }
    }
  }

  public record BoundaryQuery(String tenantId, String boundaryId) {}
}

package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.TypeName;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
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

  /** Compatibility write path for seed import and repository adapters. */
  public Effect<ToolPermissionBoundary> save(ToolPermissionBoundary boundary) {
    var validation = validate(boundary);
    if (validation.isPresent()) {
      return effects().error(validation.get());
    }
    if (boundary.equals(currentState().boundary())) {
      return effects().reply(boundary);
    }
    return effects().persist(new Event.ToolBoundarySaved(boundary)).thenReply(State::boundary);
  }

  @Override
  public State applyEvent(Event event) {
    return switch (event) {
      case Event.ToolBoundarySaved saved -> new State(saved.boundary());
    };
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

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  public record State(ToolPermissionBoundary boundary) {
    static State empty() {
      return new State(null);
    }

    Optional<ToolPermissionBoundary> boundaryForTenant(String tenantId) {
      return Optional.ofNullable(boundary).filter(candidate -> candidate.tenantId().equals(tenantId));
    }
  }

  public sealed interface Event {
    ToolPermissionBoundary boundary();

    @TypeName("tool-permission-boundary-saved")
    record ToolBoundarySaved(ToolPermissionBoundary boundary) implements Event {}
  }

  public record BoundaryQuery(String tenantId, String boundaryId) {}
}

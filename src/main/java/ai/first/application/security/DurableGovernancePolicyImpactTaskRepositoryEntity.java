package ai.first.application.security;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import ai.first.domain.security.GovernancePolicyImpactTask;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Akka durable Governance/Policy impact task repository for start/read/cancel/result-review state. */
@Component(id = "starter-governance-policy-impact-task-repository")
public class DurableGovernancePolicyImpactTaskRepositoryEntity extends KeyValueEntity<DurableGovernancePolicyImpactTaskRepositoryEntity.State> {
  public static final String ENTITY_ID = "starter-governance-policy-impact-task-repository";

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<GovernancePolicyImpactTask>> find(String impactTaskId) {
    return effects().reply(Optional.ofNullable(currentState().tasks().get(impactTaskId)));
  }

  public ReadOnlyEffect<Optional<GovernancePolicyImpactTask>> findByIdempotencyKey(IdempotencyQuery query) {
    if (query.idempotencyKey() == null || query.idempotencyKey().isBlank()) return effects().reply(Optional.empty());
    var impactTaskId = currentState().idempotencyIndex().get(idempotencyKey(query.tenantId(), query.accountId(), query.idempotencyKey()));
    return effects().reply(impactTaskId == null ? Optional.empty() : Optional.ofNullable(currentState().tasks().get(impactTaskId)));
  }

  public Effect<GovernancePolicyImpactTask> save(GovernancePolicyImpactTask task) {
    return effects().updateState(currentState().save(task)).thenReply(() -> task);
  }

  private static String idempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return tenantId + ":" + accountId + ":" + idempotencyKey;
  }

  public record IdempotencyQuery(String tenantId, String accountId, String idempotencyKey) {}

  public record State(Map<String, GovernancePolicyImpactTask> tasks, Map<String, String> idempotencyIndex) {
    static State empty() {
      return new State(Map.of(), Map.of());
    }

    State save(GovernancePolicyImpactTask task) {
      var nextTasks = new HashMap<>(tasks);
      var nextIdempotency = new HashMap<>(idempotencyIndex);
      nextTasks.put(task.impactTaskId(), task);
      if (task.idempotencyKey() != null && !task.idempotencyKey().isBlank()) {
        nextIdempotency.put(idempotencyKey(task.tenantId(), task.startedByAccountId(), task.idempotencyKey()), task.impactTaskId());
      }
      return new State(Map.copyOf(nextTasks), Map.copyOf(nextIdempotency));
    }
  }
}

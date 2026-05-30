package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccessReviewTask;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Akka durable access-review task repository for start/read/cancel/worker-result/human-decision state. */
@Component(id = "starter-access-review-task-repository")
public class DurableAccessReviewTaskRepositoryEntity extends KeyValueEntity<DurableAccessReviewTaskRepositoryEntity.State> {
  public static final String ENTITY_ID = "starter-access-review-task-repository";

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<AccessReviewTask>> find(String taskId) {
    return effects().reply(Optional.ofNullable(currentState().tasks().get(taskId)));
  }

  public ReadOnlyEffect<Optional<AccessReviewTask>> findByIdempotencyKey(IdempotencyQuery query) {
    if (query.idempotencyKey() == null || query.idempotencyKey().isBlank()) return effects().reply(Optional.empty());
    var taskId = currentState().idempotencyIndex().get(idempotencyKey(query.tenantId(), query.accountId(), query.idempotencyKey()));
    return effects().reply(taskId == null ? Optional.empty() : Optional.ofNullable(currentState().tasks().get(taskId)));
  }

  public Effect<AccessReviewTask> save(AccessReviewTask task) {
    return effects().updateState(currentState().save(task)).thenReply(() -> task);
  }

  private static String idempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return tenantId + ":" + accountId + ":" + idempotencyKey;
  }

  public record IdempotencyQuery(String tenantId, String accountId, String idempotencyKey) {}

  public record State(Map<String, AccessReviewTask> tasks, Map<String, String> idempotencyIndex) {
    static State empty() {
      return new State(Map.of(), Map.of());
    }

    State save(AccessReviewTask task) {
      var nextTasks = new HashMap<>(tasks);
      var nextIdempotency = new HashMap<>(idempotencyIndex);
      nextTasks.put(task.taskId(), task);
      if (task.idempotencyKey() != null && !task.idempotencyKey().isBlank()) {
        nextIdempotency.put(idempotencyKey(task.tenantId(), task.startedByAccountId(), task.idempotencyKey()), task.taskId());
      }
      return new State(Map.copyOf(nextTasks), Map.copyOf(nextIdempotency));
    }
  }
}

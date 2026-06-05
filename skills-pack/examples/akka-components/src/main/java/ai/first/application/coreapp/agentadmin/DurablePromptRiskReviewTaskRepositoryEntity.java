package ai.first.application.coreapp.agentadmin;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import ai.first.domain.coreapp.agentadmin.PromptRiskReviewTask;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Akka durable prompt-risk task repository for start/read/cancel/result-review state. */
@Component(id = "starter-prompt-risk-review-task-repository")
public class DurablePromptRiskReviewTaskRepositoryEntity extends KeyValueEntity<DurablePromptRiskReviewTaskRepositoryEntity.State> {
  public static final String ENTITY_ID = "starter-prompt-risk-review-task-repository";

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<PromptRiskReviewTask>> find(String taskId) {
    return effects().reply(Optional.ofNullable(currentState().tasks().get(taskId)));
  }

  public ReadOnlyEffect<Optional<PromptRiskReviewTask>> findByIdempotencyKey(IdempotencyQuery query) {
    if (query.idempotencyKey() == null || query.idempotencyKey().isBlank()) return effects().reply(Optional.empty());
    var taskId = currentState().idempotencyIndex().get(idempotencyKey(query.tenantId(), query.accountId(), query.idempotencyKey()));
    return effects().reply(taskId == null ? Optional.empty() : Optional.ofNullable(currentState().tasks().get(taskId)));
  }

  public Effect<PromptRiskReviewTask> save(PromptRiskReviewTask task) {
    return effects().updateState(currentState().save(task)).thenReply(() -> task);
  }

  private static String idempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return tenantId + ":" + accountId + ":" + idempotencyKey;
  }

  public record IdempotencyQuery(String tenantId, String accountId, String idempotencyKey) {}

  public record State(Map<String, PromptRiskReviewTask> tasks, Map<String, String> idempotencyIndex) {
    static State empty() {
      return new State(Map.of(), Map.of());
    }

    State save(PromptRiskReviewTask task) {
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

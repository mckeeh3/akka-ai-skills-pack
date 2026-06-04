package ai.first.application.coreapp.myaccount;

import ai.first.domain.foundation.identity.Account;
import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import ai.first.domain.coreapp.myaccount.MyAccountPersonalAttentionDigestTask;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Akka durable My Account personal attention digest task repository for start/read/cancel/result-review state. */
@Component(id = "starter-my-account-personal-attention-digest-task-repository")
public class DurableMyAccountPersonalAttentionDigestTaskRepositoryEntity extends KeyValueEntity<DurableMyAccountPersonalAttentionDigestTaskRepositoryEntity.State> {
  public static final String ENTITY_ID = "starter-my-account-personal-attention-digest-task-repository";

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<MyAccountPersonalAttentionDigestTask>> find(String digestTaskId) {
    return effects().reply(Optional.ofNullable(currentState().tasks().get(digestTaskId)));
  }

  public ReadOnlyEffect<Optional<MyAccountPersonalAttentionDigestTask>> findByIdempotencyKey(IdempotencyQuery query) {
    if (query.idempotencyKey() == null || query.idempotencyKey().isBlank()) return effects().reply(Optional.empty());
    var taskId = currentState().idempotencyIndex().get(idempotencyKey(query.tenantId(), query.accountId(), query.idempotencyKey()));
    return effects().reply(taskId == null ? Optional.empty() : Optional.ofNullable(currentState().tasks().get(taskId)));
  }

  public Effect<MyAccountPersonalAttentionDigestTask> save(MyAccountPersonalAttentionDigestTask task) {
    return effects().updateState(currentState().save(task)).thenReply(() -> task);
  }

  private static String idempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return tenantId + ":" + accountId + ":" + idempotencyKey;
  }

  public record IdempotencyQuery(String tenantId, String accountId, String idempotencyKey) {}

  public record State(Map<String, MyAccountPersonalAttentionDigestTask> tasks, Map<String, String> idempotencyIndex) {
    static State empty() {
      return new State(Map.of(), Map.of());
    }

    State save(MyAccountPersonalAttentionDigestTask task) {
      var nextTasks = new HashMap<>(tasks);
      var nextIdempotency = new HashMap<>(idempotencyIndex);
      nextTasks.put(task.digestTaskId(), task);
      if (task.idempotencyKey() != null && !task.idempotencyKey().isBlank()) {
        nextIdempotency.put(idempotencyKey(task.tenantId(), task.startedByAccountId(), task.idempotencyKey()), task.digestTaskId());
      }
      return new State(Map.copyOf(nextTasks), Map.copyOf(nextIdempotency));
    }
  }
}

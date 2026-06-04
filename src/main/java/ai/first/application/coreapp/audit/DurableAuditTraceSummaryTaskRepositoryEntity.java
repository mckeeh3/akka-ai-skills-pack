package ai.first.application.coreapp.audit;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import ai.first.domain.coreapp.audit.AuditTraceSummaryTask;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Akka durable Audit/Trace summary task repository for start/read/cancel/result-review state. */
@Component(id = "starter-audit-trace-summary-task-repository")
public class DurableAuditTraceSummaryTaskRepositoryEntity extends KeyValueEntity<DurableAuditTraceSummaryTaskRepositoryEntity.State> {
  public static final String ENTITY_ID = "starter-audit-trace-summary-task-repository";

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<AuditTraceSummaryTask>> find(String taskId) {
    return effects().reply(Optional.ofNullable(currentState().tasks().get(taskId)));
  }

  public ReadOnlyEffect<Optional<AuditTraceSummaryTask>> findByIdempotencyKey(IdempotencyQuery query) {
    if (query.idempotencyKey() == null || query.idempotencyKey().isBlank()) return effects().reply(Optional.empty());
    var taskId = currentState().idempotencyIndex().get(idempotencyKey(query.tenantId(), query.accountId(), query.idempotencyKey()));
    return effects().reply(taskId == null ? Optional.empty() : Optional.ofNullable(currentState().tasks().get(taskId)));
  }

  public Effect<AuditTraceSummaryTask> save(AuditTraceSummaryTask task) {
    return effects().updateState(currentState().save(task)).thenReply(() -> task);
  }

  private static String idempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return tenantId + ":" + accountId + ":" + idempotencyKey;
  }

  public record IdempotencyQuery(String tenantId, String accountId, String idempotencyKey) {}

  public record State(Map<String, AuditTraceSummaryTask> tasks, Map<String, String> idempotencyIndex) {
    static State empty() {
      return new State(Map.of(), Map.of());
    }

    State save(AuditTraceSummaryTask task) {
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

package ai.first.application.coreapp.useradmin;

import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory access-review task store for local starter execution and tests. */
public final class InMemoryTestAccessReviewTaskRepository implements AccessReviewTaskRepository {
  private final Map<String, AccessReviewTask> tasks = new ConcurrentHashMap<>();
  private final Map<String, String> idempotencyIndex = new ConcurrentHashMap<>();

  @Override
  public Optional<AccessReviewTask> find(String taskId) {
    return Optional.ofNullable(tasks.get(taskId));
  }

  @Override
  public Optional<AccessReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) return Optional.empty();
    return Optional.ofNullable(idempotencyIndex.get(key(tenantId, accountId, idempotencyKey))).flatMap(this::find);
  }

  @Override
  public AccessReviewTask save(AccessReviewTask task) {
    tasks.put(task.taskId(), task);
    if (task.idempotencyKey() != null && !task.idempotencyKey().isBlank()) {
      idempotencyIndex.put(key(task.tenantId(), task.startedByAccountId(), task.idempotencyKey()), task.taskId());
    }
    return task;
  }

  private static String key(String tenantId, String accountId, String idempotencyKey) {
    return tenantId + ":" + accountId + ":" + idempotencyKey;
  }
}

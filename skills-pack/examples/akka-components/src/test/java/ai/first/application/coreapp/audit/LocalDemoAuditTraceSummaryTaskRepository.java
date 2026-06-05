package ai.first.application.coreapp.audit;

import ai.first.domain.coreapp.audit.AuditTraceSummaryTask;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Test-only Akka component-backed Audit/Trace summary task repository. */
public final class LocalDemoAuditTraceSummaryTaskRepository implements AuditTraceSummaryTaskRepository {
  private final Map<String, AuditTraceSummaryTask> tasks = new ConcurrentHashMap<>();

  @Override
  public Optional<AuditTraceSummaryTask> find(String taskId) {
    return Optional.ofNullable(tasks.get(taskId));
  }

  @Override
  public Optional<AuditTraceSummaryTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return tasks.values().stream()
        .filter(task -> task.tenantId().equals(tenantId))
        .filter(task -> task.startedByAccountId().equals(accountId))
        .filter(task -> task.idempotencyKey().equals(idempotencyKey))
        .findFirst();
  }

  @Override
  public AuditTraceSummaryTask save(AuditTraceSummaryTask task) {
    tasks.put(task.taskId(), task);
    return task;
  }
}

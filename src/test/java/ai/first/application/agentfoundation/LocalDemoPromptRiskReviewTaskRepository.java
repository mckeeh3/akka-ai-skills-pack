package ai.first.application.agentfoundation;

import ai.first.domain.agentfoundation.PromptRiskReviewTask;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** In-memory prompt-risk task repository for starter tests and local demos only. */
public final class LocalDemoPromptRiskReviewTaskRepository implements PromptRiskReviewTaskRepository {
  private final Map<String, PromptRiskReviewTask> tasks = new LinkedHashMap<>();

  @Override
  public Optional<PromptRiskReviewTask> find(String taskId) {
    return Optional.ofNullable(tasks.get(taskId));
  }

  @Override
  public Optional<PromptRiskReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return tasks.values().stream()
        .filter(task -> task.tenantId().equals(tenantId) && task.startedByAccountId().equals(accountId) && task.idempotencyKey().equals(idempotencyKey))
        .findFirst();
  }

  @Override
  public PromptRiskReviewTask save(PromptRiskReviewTask task) {
    tasks.put(task.taskId(), task);
    return task;
  }
}

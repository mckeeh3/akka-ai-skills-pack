package ai.first.application.security;

import ai.first.domain.security.GovernancePolicyImpactTask;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** In-memory Governance/Policy impact task repository for starter tests and local demos only. */
public final class LocalDemoGovernancePolicyImpactTaskRepository implements GovernancePolicyImpactTaskRepository {
  private final Map<String, GovernancePolicyImpactTask> tasks = new LinkedHashMap<>();

  @Override
  public Optional<GovernancePolicyImpactTask> find(String impactTaskId) {
    return Optional.ofNullable(tasks.get(impactTaskId));
  }

  @Override
  public Optional<GovernancePolicyImpactTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return tasks.values().stream()
        .filter(task -> task.tenantId().equals(tenantId) && task.startedByAccountId().equals(accountId) && task.idempotencyKey().equals(idempotencyKey))
        .findFirst();
  }

  @Override
  public GovernancePolicyImpactTask save(GovernancePolicyImpactTask task) {
    tasks.put(task.impactTaskId(), task);
    return task;
  }
}

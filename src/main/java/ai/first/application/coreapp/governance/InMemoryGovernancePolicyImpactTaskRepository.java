package ai.first.application.coreapp.governance;

import ai.first.domain.coreapp.governance.GovernancePolicyImpactTask;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory Governance/Policy impact task repository used only before an Akka durable repository is bound.
 * Normal endpoint runtime passes {@link AkkaGovernancePolicyImpactTaskRepository} from the component client.
 */
public final class InMemoryGovernancePolicyImpactTaskRepository implements GovernancePolicyImpactTaskRepository {
  private final Map<String, GovernancePolicyImpactTask> tasks = new LinkedHashMap<>();

  @Override
  public synchronized Optional<GovernancePolicyImpactTask> find(String impactTaskId) {
    return Optional.ofNullable(tasks.get(impactTaskId));
  }

  @Override
  public synchronized Optional<GovernancePolicyImpactTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return tasks.values().stream()
        .filter(task -> task.tenantId().equals(tenantId)
            && task.startedByAccountId().equals(accountId)
            && task.idempotencyKey().equals(idempotencyKey))
        .findFirst();
  }

  @Override
  public synchronized GovernancePolicyImpactTask save(GovernancePolicyImpactTask task) {
    tasks.put(task.impactTaskId(), task);
    return task;
  }
}

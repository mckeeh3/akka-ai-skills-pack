package ai.first.application.coreapp.governance;

import ai.first.domain.coreapp.governance.GovernancePolicyImpactTask;
import java.util.Optional;

/** Repository for durable Governance/Policy impact-analysis task projections. */
public interface GovernancePolicyImpactTaskRepository {
  Optional<GovernancePolicyImpactTask> find(String impactTaskId);

  Optional<GovernancePolicyImpactTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey);

  GovernancePolicyImpactTask save(GovernancePolicyImpactTask task);
}

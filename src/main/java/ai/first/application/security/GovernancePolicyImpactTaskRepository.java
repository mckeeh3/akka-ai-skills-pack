package ai.first.application.security;

import ai.first.domain.security.GovernancePolicyImpactTask;
import java.util.Optional;

/** Repository for durable Governance/Policy impact-analysis task projections. */
public interface GovernancePolicyImpactTaskRepository {
  Optional<GovernancePolicyImpactTask> find(String impactTaskId);

  Optional<GovernancePolicyImpactTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey);

  GovernancePolicyImpactTask save(GovernancePolicyImpactTask task);
}

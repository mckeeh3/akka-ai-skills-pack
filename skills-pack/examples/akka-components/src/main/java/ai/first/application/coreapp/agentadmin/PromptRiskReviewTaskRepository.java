package ai.first.application.coreapp.agentadmin;

import ai.first.domain.coreapp.agentadmin.PromptRiskReviewTask;
import java.util.Optional;

/** Repository for durable Agent Admin prompt-risk review task projections. */
public interface PromptRiskReviewTaskRepository {
  Optional<PromptRiskReviewTask> find(String taskId);

  Optional<PromptRiskReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey);

  PromptRiskReviewTask save(PromptRiskReviewTask task);
}

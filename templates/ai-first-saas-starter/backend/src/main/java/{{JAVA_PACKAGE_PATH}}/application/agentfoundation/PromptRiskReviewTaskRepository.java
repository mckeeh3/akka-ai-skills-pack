package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptRiskReviewTask;
import java.util.Optional;

/** Repository for durable Agent Admin prompt-risk review task projections. */
public interface PromptRiskReviewTaskRepository {
  Optional<PromptRiskReviewTask> find(String taskId);

  Optional<PromptRiskReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey);

  PromptRiskReviewTask save(PromptRiskReviewTask task);
}

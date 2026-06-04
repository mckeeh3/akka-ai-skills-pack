package ai.first.application.coreapp.useradmin;

import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import java.util.Optional;

/** Persistence port for durable User Admin access-review task lifecycle records. */
public interface AccessReviewTaskRepository {
  Optional<AccessReviewTask> find(String taskId);

  Optional<AccessReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey);

  AccessReviewTask save(AccessReviewTask task);
}

package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.AccessReviewTask;
import java.util.Optional;

/** Persistence port for durable User Admin access-review task lifecycle records. */
public interface AccessReviewTaskRepository {
  Optional<AccessReviewTask> find(String taskId);

  Optional<AccessReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey);

  AccessReviewTask save(AccessReviewTask task);
}

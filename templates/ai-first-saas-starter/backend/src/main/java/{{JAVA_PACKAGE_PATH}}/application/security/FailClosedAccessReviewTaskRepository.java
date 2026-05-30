package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.AccessReviewTask;
import java.util.Optional;

/** Fail-closed access-review task port until durable task state is bound. */
public final class FailClosedAccessReviewTaskRepository implements AccessReviewTaskRepository {
  private IllegalStateException unavailable() {
    return FailClosedFoundationRuntime.unavailable("AccessReviewTaskRepository");
  }

  @Override public Optional<AccessReviewTask> find(String taskId) { throw unavailable(); }
  @Override public Optional<AccessReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) { throw unavailable(); }
  @Override public AccessReviewTask save(AccessReviewTask task) { throw unavailable(); }
}

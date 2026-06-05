package ai.first.application.coreapp.useradmin;

import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import java.util.Optional;
import ai.first.application.foundation.identity.FailClosedFoundationRuntime;

/** Fail-closed access-review task port until durable task state is bound. */
public final class FailClosedAccessReviewTaskRepository implements AccessReviewTaskRepository {
  private IllegalStateException unavailable() {
    return FailClosedFoundationRuntime.unavailable("AccessReviewTaskRepository");
  }

  @Override public Optional<AccessReviewTask> find(String taskId) { throw unavailable(); }
  @Override public Optional<AccessReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) { throw unavailable(); }
  @Override public AccessReviewTask save(AccessReviewTask task) { throw unavailable(); }
}

package ai.first.application.coreapp.audit;

import ai.first.domain.coreapp.audit.AuditTraceSummaryTask;
import java.util.Optional;

/** Repository for durable Audit/Trace summary task projections. */
public interface AuditTraceSummaryTaskRepository {
  Optional<AuditTraceSummaryTask> find(String taskId);

  Optional<AuditTraceSummaryTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey);

  AuditTraceSummaryTask save(AuditTraceSummaryTask task);
}

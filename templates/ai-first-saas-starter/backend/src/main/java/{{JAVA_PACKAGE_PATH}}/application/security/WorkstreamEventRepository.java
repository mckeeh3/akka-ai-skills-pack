package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.WorkstreamEventEnvelope;
import java.util.List;
import java.util.Optional;

/** Durable port for the governed workstream event backbone. */
public interface WorkstreamEventRepository {
  WorkstreamEventEnvelope publish(WorkstreamEventEnvelope event);
  Optional<WorkstreamEventEnvelope> find(String tenantId, String eventId);
  Optional<WorkstreamEventEnvelope> findByIdempotencyKey(String tenantId, String idempotencyKey);
  List<WorkstreamEventEnvelope> listTenant(String tenantId);
}

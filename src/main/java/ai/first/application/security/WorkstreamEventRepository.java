package ai.first.application.security;

import ai.first.domain.security.WorkstreamEventEnvelope;
import java.util.List;
import java.util.Optional;

/** Durable port for the governed workstream event backbone. */
public interface WorkstreamEventRepository {
  WorkstreamEventEnvelope publish(WorkstreamEventEnvelope event);
  Optional<WorkstreamEventEnvelope> find(String tenantId, String eventId);
  Optional<WorkstreamEventEnvelope> findByIdempotencyKey(String tenantId, String idempotencyKey);
  List<WorkstreamEventEnvelope> listTenant(String tenantId);
}

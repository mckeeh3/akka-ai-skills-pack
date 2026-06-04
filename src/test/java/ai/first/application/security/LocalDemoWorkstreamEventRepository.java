package ai.first.application.security;

import ai.first.domain.foundation.workstream.WorkstreamEventEnvelope;
import java.util.List;
import java.util.Optional;

/** Unit-test/local adapter for governed workstream event backbone tests; production binds AkkaWorkstreamEventRepository. */
public final class LocalDemoWorkstreamEventRepository implements WorkstreamEventRepository {
  private WorkstreamEventRepositoryState state = WorkstreamEventRepositoryState.empty();

  public synchronized WorkstreamEventEnvelope publish(WorkstreamEventEnvelope event) {
    state = state.publish(event);
    return state.find(event.tenantId(), event.eventId()).orElse(event);
  }

  public synchronized Optional<WorkstreamEventEnvelope> find(String tenantId, String eventId) {
    return state.find(tenantId, eventId);
  }

  public synchronized Optional<WorkstreamEventEnvelope> findByIdempotencyKey(String tenantId, String idempotencyKey) {
    return state.findByIdempotencyKey(tenantId, idempotencyKey);
  }

  public synchronized List<WorkstreamEventEnvelope> listTenant(String tenantId) {
    return state.listTenant(tenantId);
  }
}

package ai.first.application.security;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import ai.first.domain.foundation.workstream.WorkstreamEventEnvelope;
import java.util.List;
import java.util.Optional;

/** Durable Akka Key Value Entity backing the governed workstream event backbone. */
@Component(id = "starter-workstream-event-backbone")
public class DurableWorkstreamEventRepositoryEntity extends KeyValueEntity<WorkstreamEventRepositoryState> {
  public static final String ENTITY_ID = "starter-workstream-event-backbone";

  @Override
  public WorkstreamEventRepositoryState emptyState() {
    return WorkstreamEventRepositoryState.empty();
  }

  public Effect<WorkstreamEventEnvelope> publish(WorkstreamEventEnvelope event) {
    return effects().updateState(currentState().publish(event)).thenReply(() -> event);
  }

  public ReadOnlyEffect<Optional<WorkstreamEventEnvelope>> find(FindQuery query) {
    return effects().reply(currentState().find(query.tenantId(), query.eventId()));
  }

  public ReadOnlyEffect<Optional<WorkstreamEventEnvelope>> findByIdempotencyKey(IdempotencyQuery query) {
    return effects().reply(currentState().findByIdempotencyKey(query.tenantId(), query.idempotencyKey()));
  }

  public ReadOnlyEffect<List<WorkstreamEventEnvelope>> listTenant(ListTenantQuery query) {
    return effects().reply(currentState().listTenant(query.tenantId()));
  }

  public record FindQuery(String tenantId, String eventId) {}
  public record IdempotencyQuery(String tenantId, String idempotencyKey) {}
  public record ListTenantQuery(String tenantId) {}
}

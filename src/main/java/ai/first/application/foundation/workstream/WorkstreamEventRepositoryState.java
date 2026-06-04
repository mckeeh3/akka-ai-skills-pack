package ai.first.application.foundation.workstream;

import ai.first.domain.foundation.workstream.WorkstreamEventEnvelope;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Durable state for the starter governed workstream event backbone. */
public record WorkstreamEventRepositoryState(Map<String, WorkstreamEventEnvelope> eventsByKey) {
  public WorkstreamEventRepositoryState {
    eventsByKey = Map.copyOf(eventsByKey == null ? Map.of() : eventsByKey);
  }

  public static WorkstreamEventRepositoryState empty() {
    return new WorkstreamEventRepositoryState(Map.of());
  }

  public Optional<WorkstreamEventEnvelope> find(String tenantId, String eventId) {
    return Optional.ofNullable(eventsByKey.get(key(tenantId, eventId)));
  }

  public Optional<WorkstreamEventEnvelope> findByIdempotencyKey(String tenantId, String idempotencyKey) {
    return eventsByKey.values().stream()
        .filter(event -> tenantId.equals(event.tenantId()))
        .filter(event -> idempotencyKey.equals(event.idempotencyKey()))
        .findFirst();
  }

  public List<WorkstreamEventEnvelope> listTenant(String tenantId) {
    return eventsByKey.values().stream()
        .filter(event -> tenantId.equals(event.tenantId()))
        .sorted(Comparator.comparing(WorkstreamEventEnvelope::occurredAt).reversed())
        .toList();
  }

  public WorkstreamEventRepositoryState publish(WorkstreamEventEnvelope event) {
    var next = new LinkedHashMap<>(eventsByKey);
    next.putIfAbsent(key(event.tenantId(), event.eventId()), event);
    return new WorkstreamEventRepositoryState(next);
  }

  private static String key(String tenantId, String eventId) {
    return tenantId + ":" + eventId;
  }
}

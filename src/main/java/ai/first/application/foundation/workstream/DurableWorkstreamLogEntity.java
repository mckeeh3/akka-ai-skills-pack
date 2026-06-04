package ai.first.application.foundation.workstream;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import java.util.List;
import java.util.Optional;
import ai.first.application.security.WorkstreamService;

/** Durable Akka Key Value Entity for tenant/context/functional-agent scoped workstream logs. */
@Component(id = "starter-workstream-log")
public class DurableWorkstreamLogEntity extends KeyValueEntity<WorkstreamLogState> {
  public static final String ENTITY_ID = "starter-workstream-log";

  @Override
  public WorkstreamLogState emptyState() {
    return WorkstreamLogState.empty();
  }

  public ReadOnlyEffect<List<WorkstreamService.WorkstreamItem>> items(ItemsQuery query) {
    return effects().reply(currentState().items(query.tenantId(), query.selectedContextId(), query.functionalAgentId()));
  }

  public ReadOnlyEffect<Optional<WorkstreamService.SurfaceEnvelope>> surface(SurfaceQuery query) {
    return effects().reply(currentState().surface(query.tenantId(), query.selectedContextId(), query.surfaceId()));
  }

  public ReadOnlyEffect<Optional<WorkstreamLogRepository.WorkstreamMessageLogEntry>> findByIdempotencyKey(IdempotencyQuery query) {
    return effects().reply(currentState().findByIdempotencyKey(query.tenantId(), query.selectedContextId(), query.functionalAgentId(), query.idempotencyKey()));
  }

  public Effect<WorkstreamLogRepository.WorkstreamMessageLogEntry> appendMessage(WorkstreamLogRepository.WorkstreamMessageLogEntry entry) {
    var existing = currentState().findByIdempotencyKey(entry.tenantId(), entry.selectedContextId(), entry.functionalAgentId(), entry.idempotencyKey());
    if (existing.isPresent()) return effects().reply(existing.orElseThrow());
    return effects().updateState(currentState().appendMessage(entry)).thenReply(() -> entry);
  }

  public Effect<WorkstreamService.WorkstreamItem> appendSystemEntry(SystemEntryCommand command) {
    return effects().updateState(currentState().appendSystemEntry(command.tenantId(), command.selectedContextId(), command.item(), command.surface()))
        .thenReply(command::item);
  }

  public record ItemsQuery(String tenantId, String selectedContextId, String functionalAgentId) {}
  public record SurfaceQuery(String tenantId, String selectedContextId, String surfaceId) {}
  public record IdempotencyQuery(String tenantId, String selectedContextId, String functionalAgentId, String idempotencyKey) {}
  public record SystemEntryCommand(String tenantId, String selectedContextId, WorkstreamService.WorkstreamItem item, WorkstreamService.SurfaceEnvelope surface) {}
}

package {{JAVA_BASE_PACKAGE}}.application.security;

import java.util.List;
import java.util.Optional;

/** Unit-test/local adapter for the durable workstream log port; production API paths bind AkkaWorkstreamLogRepository. */
public final class LocalDemoWorkstreamLogRepository implements WorkstreamLogRepository {
  private WorkstreamLogState state = WorkstreamLogState.empty();

  @Override
  public synchronized List<WorkstreamService.WorkstreamItem> items(String tenantId, String selectedContextId, String functionalAgentId) {
    return state.items(tenantId, selectedContextId, functionalAgentId);
  }

  @Override
  public synchronized Optional<WorkstreamService.SurfaceEnvelope> surface(String tenantId, String selectedContextId, String surfaceId) {
    return state.surface(tenantId, selectedContextId, surfaceId);
  }

  @Override
  public synchronized Optional<WorkstreamMessageLogEntry> findByIdempotencyKey(String tenantId, String selectedContextId, String functionalAgentId, String idempotencyKey) {
    return state.findByIdempotencyKey(tenantId, selectedContextId, functionalAgentId, idempotencyKey);
  }

  @Override
  public synchronized WorkstreamMessageLogEntry appendMessage(WorkstreamMessageLogEntry entry) {
    var existing = findByIdempotencyKey(entry.tenantId(), entry.selectedContextId(), entry.functionalAgentId(), entry.idempotencyKey());
    if (existing.isPresent()) return existing.orElseThrow();
    state = state.appendMessage(entry);
    return entry;
  }

  @Override
  public synchronized WorkstreamService.WorkstreamItem appendSystemEntry(String tenantId, String selectedContextId, WorkstreamService.WorkstreamItem item, WorkstreamService.SurfaceEnvelope surface) {
    state = state.appendSystemEntry(tenantId, selectedContextId, item, surface);
    return item;
  }
}

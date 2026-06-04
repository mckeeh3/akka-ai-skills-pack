package ai.first.application.foundation.workstream;

import java.util.List;
import java.util.Optional;
import ai.first.application.foundation.identity.FailClosedFoundationRuntime;
import ai.first.application.coreapp.workstream.WorkstreamService;

/** Test-only fail-closed workstream log port used to verify durable binding diagnostics. */
public final class FailClosedWorkstreamLogRepository implements WorkstreamLogRepository {
  private IllegalStateException unavailable() {
    return FailClosedFoundationRuntime.unavailable("WorkstreamLogRepository");
  }

  @Override public List<WorkstreamService.WorkstreamItem> items(String tenantId, String selectedContextId, String functionalAgentId) { throw unavailable(); }
  @Override public Optional<WorkstreamService.SurfaceEnvelope> surface(String tenantId, String selectedContextId, String surfaceId) { throw unavailable(); }
  @Override public Optional<WorkstreamMessageLogEntry> findByIdempotencyKey(String tenantId, String selectedContextId, String functionalAgentId, String idempotencyKey) { throw unavailable(); }
  @Override public WorkstreamMessageLogEntry appendMessage(WorkstreamMessageLogEntry entry) { throw unavailable(); }
  @Override public WorkstreamService.WorkstreamItem appendSystemEntry(String tenantId, String selectedContextId, WorkstreamService.WorkstreamItem item, WorkstreamService.SurfaceEnvelope surface) { throw unavailable(); }
}

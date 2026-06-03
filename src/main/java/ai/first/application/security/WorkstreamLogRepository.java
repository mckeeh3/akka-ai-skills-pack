package ai.first.application.security;

import java.util.List;
import java.util.Optional;

/** Durable port for tenant/context/functional-agent scoped workstream log reads and appends. */
public interface WorkstreamLogRepository {
  List<WorkstreamService.WorkstreamItem> items(String tenantId, String selectedContextId, String functionalAgentId);
  Optional<WorkstreamService.SurfaceEnvelope> surface(String tenantId, String selectedContextId, String surfaceId);
  Optional<WorkstreamMessageLogEntry> findByIdempotencyKey(String tenantId, String selectedContextId, String functionalAgentId, String idempotencyKey);
  WorkstreamMessageLogEntry appendMessage(WorkstreamMessageLogEntry entry);
  WorkstreamService.WorkstreamItem appendSystemEntry(String tenantId, String selectedContextId, WorkstreamService.WorkstreamItem item, WorkstreamService.SurfaceEnvelope surface);

  record WorkstreamMessageLogEntry(
      String tenantId,
      String selectedContextId,
      String functionalAgentId,
      String idempotencyKey,
      String correlationId,
      WorkstreamService.WorkstreamItem userItem,
      WorkstreamService.WorkstreamItem agentItem,
      WorkstreamService.SurfaceEnvelope surface) {}
}

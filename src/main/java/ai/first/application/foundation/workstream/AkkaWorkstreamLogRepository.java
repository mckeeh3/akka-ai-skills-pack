package ai.first.application.foundation.workstream;

import akka.javasdk.client.ComponentClient;
import java.util.List;
import java.util.Optional;
import ai.first.application.security.WorkstreamService;

/** Akka-backed workstream log adapter for real local runtime API paths. */
public final class AkkaWorkstreamLogRepository implements WorkstreamLogRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaWorkstreamLogRepository(ComponentClient componentClient) {
    this(componentClient, DurableWorkstreamLogEntity.ENTITY_ID);
  }

  public AkkaWorkstreamLogRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  @Override
  public List<WorkstreamService.WorkstreamItem> items(String tenantId, String selectedContextId, String functionalAgentId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableWorkstreamLogEntity::items)
        .invoke(new DurableWorkstreamLogEntity.ItemsQuery(tenantId, selectedContextId, functionalAgentId));
  }

  @Override
  public Optional<WorkstreamService.SurfaceEnvelope> surface(String tenantId, String selectedContextId, String surfaceId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableWorkstreamLogEntity::surface)
        .invoke(new DurableWorkstreamLogEntity.SurfaceQuery(tenantId, selectedContextId, surfaceId));
  }

  @Override
  public Optional<WorkstreamMessageLogEntry> findByIdempotencyKey(String tenantId, String selectedContextId, String functionalAgentId, String idempotencyKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableWorkstreamLogEntity::findByIdempotencyKey)
        .invoke(new DurableWorkstreamLogEntity.IdempotencyQuery(tenantId, selectedContextId, functionalAgentId, idempotencyKey));
  }

  @Override
  public WorkstreamMessageLogEntry appendMessage(WorkstreamMessageLogEntry entry) {
    return componentClient.forKeyValueEntity(entityId).method(DurableWorkstreamLogEntity::appendMessage).invoke(entry);
  }

  @Override
  public WorkstreamService.WorkstreamItem appendSystemEntry(String tenantId, String selectedContextId, WorkstreamService.WorkstreamItem item, WorkstreamService.SurfaceEnvelope surface) {
    return componentClient.forKeyValueEntity(entityId).method(DurableWorkstreamLogEntity::appendSystemEntry)
        .invoke(new DurableWorkstreamLogEntity.SystemEntryCommand(tenantId, selectedContextId, item, surface));
  }
}

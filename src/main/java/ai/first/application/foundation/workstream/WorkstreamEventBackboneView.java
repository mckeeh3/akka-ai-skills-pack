package ai.first.application.foundation.workstream;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import ai.first.domain.foundation.workstream.WorkstreamEventEnvelope;
import java.util.Comparator;
import java.util.List;

/** Live browser-safe view over the governed workstream event backbone for SSE subscriptions. */
@Component(id = "workstream-event-backbone-live-view")
public class WorkstreamEventBackboneView extends View {

  @Consume.FromKeyValueEntity(DurableWorkstreamEventRepositoryEntity.class)
  public static class Updater extends TableUpdater<WorkstreamEventRow> {
    public Effect<WorkstreamEventRow> onUpdate(WorkstreamEventRepositoryState state) {
      return state.eventsByKey().values().stream()
          .max(Comparator.comparing(WorkstreamEventEnvelope::occurredAt))
          .map(WorkstreamEventBackboneView::toRow)
          .map(effects()::updateRow)
          .orElseGet(() -> effects().ignore());
    }
  }

  @Query(value = "SELECT * FROM workstream_event_backbone_live_view WHERE tenantId = :tenantId AND customerId = :customerId", streamUpdates = true)
  public QueryStreamEffect<WorkstreamEventRow> streamContextEvents(ContextQuery query) {
    return queryStreamResult();
  }

  @Query(value = "SELECT * FROM workstream_event_backbone_live_view WHERE tenantId = :tenantId AND customerId = :customerId AND functionalAgentId = :functionalAgentId", streamUpdates = true)
  public QueryStreamEffect<WorkstreamEventRow> streamFunctionalAgentEvents(FunctionalAgentQuery query) {
    return queryStreamResult();
  }

  private static WorkstreamEventRow toRow(WorkstreamEventEnvelope event) {
    return new WorkstreamEventRow(
        event.eventId(),
        "projection.refresh.available",
        event.tenantId(),
        safe(event.customerId()),
        event.owningWorkstreamId(),
        event.targetSurfaceId(),
        surfaceTypeForEventBackedRefresh(event.targetSurfaceId()),
        "v1",
        event.correlationId(),
        event.traceRefs(),
        event.occurredAt().toString(),
        0,
        new Patch(
            "View-backed live SSE observed a backend event-backed projection refresh; reload backend-owned attention/dashboard surfaces instead of trusting frontend state.",
            "workstream.event.delivery.refresh",
            event.eventType(),
            event.eventFamily(),
            event.idempotencyKey()));
  }

  private static String surfaceTypeForEventBackedRefresh(String surfaceId) {
    if (surfaceId == null || surfaceId.isBlank()) return "dashboard";
    if (surfaceId.contains("invitation-panel") || surfaceId.contains("user-admin-list") || surfaceId.contains("notification-center")) return "list-search";
    if (surfaceId.contains("access-review") || surfaceId.contains("digest") || surfaceId.contains("summary") || surfaceId.contains("impact-analysis")) return "workflow-status";
    if (surfaceId.contains("agent-admin") || surfaceId.contains("governance-policy") || surfaceId.contains("audit-trace")) return "dashboard";
    return "dashboard";
  }

  private static String safe(String value) {
    return value == null ? "" : value;
  }

  public record ContextQuery(String tenantId, String customerId) {}

  public record FunctionalAgentQuery(String tenantId, String customerId, String functionalAgentId) {}

  public record Patch(String reason, String source, String eventType, String eventFamily, String idempotencyKey) {}

  public record WorkstreamEventRow(
      String eventId,
      String eventType,
      String tenantId,
      String customerId,
      String functionalAgentId,
      String surfaceId,
      String surfaceType,
      String surfaceVersion,
      String correlationId,
      List<String> traceIds,
      String occurredAt,
      int sequence,
      Patch patch) {}
}

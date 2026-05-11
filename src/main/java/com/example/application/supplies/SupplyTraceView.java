package com.example.application.supplies;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.annotations.SnapshotHandler;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import com.example.domain.supplies.Supply.SupplyTraceEvent;
import com.example.domain.supplies.SupplyDecision;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Event-sourced audit trace lookup for supply decisions. */
@Component(id = "supply-trace-view")
public class SupplyTraceView extends View {

  public record FindByTraceId(String traceId) {}

  public record TraceRows(List<TraceRow> traces) {}

  public record TraceEventRow(
      String eventId,
      String type,
      String idempotencyKey,
      Instant occurredAt,
      String actor,
      String summary,
      int policyClauseCount,
      String outcomeId) {}

  public record TraceRow(
      String decisionId,
      String status,
      String traceId,
      String correlationId,
      String outcomeId,
      List<TraceEventRow> events) {
    public TraceRow {
      events = List.copyOf(events == null ? List.of() : events);
    }
  }

  @Consume.FromEventSourcedEntity(SupplyDecisionEntity.class)
  public static class SupplyTraceUpdater extends TableUpdater<TraceRow> {

    public Effect<TraceRow> onEvent(SupplyDecision.Event event) {
      return switch (event) {
        case SupplyDecision.Event.RecommendationOpened opened ->
            effects().updateRow(firstRow(SupplyDecision.Status.OPEN, opened.card().trace().traceId(), opened.card().trace().correlationId(), opened.card().outcome().outcomeId(), opened.traceEvent()));
        case SupplyDecision.Event.ApprovalRequired approvalRequired ->
            effects().updateRow(append(SupplyDecision.Status.APPROVAL_REQUIRED, approvalRequired.traceEvent()));
        case SupplyDecision.Event.Approved approved ->
            effects().updateRow(append(SupplyDecision.Status.APPROVED, approved.traceEvent()));
        case SupplyDecision.Event.Rejected rejected ->
            effects().updateRow(append(SupplyDecision.Status.REJECTED, rejected.traceEvent()));
        case SupplyDecision.Event.Suppressed suppressed ->
            effects().updateRow(append(SupplyDecision.Status.SUPPRESSED, suppressed.traceEvent()));
        case SupplyDecision.Event.ShipmentPrepared shipmentPrepared ->
            effects().updateRow(append(SupplyDecision.Status.SHIPMENT_PREPARED, shipmentPrepared.traceEvent()));
        case SupplyDecision.Event.StaleEscalated staleEscalated ->
            effects().updateRow(append(SupplyDecision.Status.STALE_ESCALATED, staleEscalated.traceEvent()));
        case SupplyDecision.Event.OutcomeLinked outcomeLinked ->
            effects().updateRow(append(rowStatus(), outcomeLinked.traceEvent()));
      };
    }

    @SnapshotHandler
    public Effect<TraceRow> onSnapshot(SupplyDecision.State snapshot) {
      if (snapshot.card() == null) {
        return effects().ignore();
      }
      return effects()
          .updateRow(
              new TraceRow(
                  decisionId(),
                  snapshot.status().name(),
                  snapshot.card().trace().traceId(),
                  snapshot.card().trace().correlationId(),
                  snapshot.card().outcome().outcomeId(),
                  snapshot.traceEvents().stream().map(SupplyTraceUpdater::toEventRow).toList()));
    }

    private TraceRow firstRow(
        SupplyDecision.Status status,
        String traceId,
        String correlationId,
        String outcomeId,
        SupplyTraceEvent event) {
      return new TraceRow(decisionId(), status.name(), traceId, correlationId, outcomeId, List.of(toEventRow(event)));
    }

    private TraceRow append(SupplyDecision.Status status, SupplyTraceEvent event) {
      var current = rowState();
      if (current == null) {
        return firstRow(status, event.trace().traceId(), event.trace().correlationId(), outcomeId(event), event);
      }
      var events = new ArrayList<>(current.events());
      if (events.stream().noneMatch(existing -> existing.eventId().equals(event.eventId()))) {
        events.add(toEventRow(event));
      }
      var outcomeId = current.outcomeId().isBlank() ? outcomeId(event) : current.outcomeId();
      return new TraceRow(
          current.decisionId(),
          status.name(),
          current.traceId(),
          current.correlationId(),
          outcomeId,
          events);
    }

    private SupplyDecision.Status rowStatus() {
      if (rowState() == null) {
        return SupplyDecision.Status.EMPTY;
      }
      return SupplyDecision.Status.valueOf(rowState().status());
    }

    private String decisionId() {
      return updateContext().eventSubject().orElse("");
    }

    private static TraceEventRow toEventRow(SupplyTraceEvent event) {
      return new TraceEventRow(
          event.eventId(),
          event.type().name(),
          event.idempotencyKey(),
          event.occurredAt(),
          event.actor(),
          event.summary(),
          event.policyClauses().size(),
          outcomeId(event));
    }

    private static String outcomeId(SupplyTraceEvent event) {
      return event.outcome() == null ? "" : event.outcome().outcomeId();
    }
  }

  @Query(
      """
      SELECT * AS traces
      FROM supply_trace_view
      WHERE traceId = :traceId
      """)
  public QueryEffect<TraceRows> getByTraceId(FindByTraceId request) {
    return queryResult();
  }
}

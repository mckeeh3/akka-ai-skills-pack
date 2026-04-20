package com.example.application;

import akka.javasdk.agent.SessionMemoryEntity;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import java.time.Instant;
import java.util.List;

/** View that materializes session-memory threshold alerts for query and SSE streaming. */
@Component(id = "session-memory-alert-view")
public class SessionMemoryAlertView extends View {

  static final long ALERT_THRESHOLD_BYTES = 500L;

  public record FindByComponent(String componentId) {}

  public record AlertRow(
      String sessionId,
      String componentId,
      long historySizeInBytes,
      String reason,
      Instant observedAt) {}

  public record AlertRows(List<AlertRow> items) {}

  @Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
  public static class SessionMemoryAlertUpdater extends TableUpdater<AlertRow> {

    public Effect<AlertRow> onEvent(SessionMemoryEntity.Event event) {
      var sessionId = updateContext().eventSubject().orElse("");

      return switch (event) {
        case SessionMemoryEntity.Event.AiMessageAdded aiMessage
            when aiMessage.historySizeInBytes() > ALERT_THRESHOLD_BYTES ->
            effects()
                .updateRow(
                    new AlertRow(
                        sessionId,
                        aiMessage.componentId(),
                        aiMessage.historySizeInBytes(),
                        "session memory exceeded threshold",
                        Instant.now()));
        case SessionMemoryEntity.Event.HistoryCleared ignored -> effects().deleteRow();
        case SessionMemoryEntity.Event.Deleted ignored -> effects().deleteRow();
        default -> effects().ignore();
      };
    }
  }

  @Query(
      """
      SELECT * AS items
      FROM session_memory_alert_view
      WHERE componentId = :componentId
      ORDER BY observedAt
      """)
  public QueryEffect<AlertRows> getByComponent(FindByComponent request) {
    return queryResult();
  }

  @Query(
      value =
          """
          SELECT *
          FROM session_memory_alert_view
          WHERE componentId = :componentId
          ORDER BY observedAt
          """,
      streamUpdates = true)
  public QueryStreamEffect<AlertRow> continuousByComponent(FindByComponent request) {
    return queryStreamResult();
  }
}

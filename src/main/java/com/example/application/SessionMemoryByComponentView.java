package com.example.application;

import akka.javasdk.agent.SessionMemoryEntity;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import java.util.List;

/** View example built from SessionMemoryEntity events for session-level analytics queries. */
@Component(id = "session-memory-by-component")
public class SessionMemoryByComponentView extends View {

  public record FindByComponent(String componentId) {}

  public record SessionRow(
      String sessionId, String lastComponentId, int messageCount, long historySizeInBytes) {}

  public record SessionRows(List<SessionRow> sessions) {}

  @Consume.FromEventSourcedEntity(SessionMemoryEntity.class)
  public static class SessionMemoryUpdater extends TableUpdater<SessionRow> {

    public Effect<SessionRow> onEvent(SessionMemoryEntity.Event event) {
      var sessionId = updateContext().eventSubject().orElse("");
      var current = rowState() == null ? new SessionRow(sessionId, "", 0, 0) : rowState();

      return switch (event) {
        case SessionMemoryEntity.Event.UserMessageAdded userMessage ->
            effects()
                .updateRow(
                    new SessionRow(
                        sessionId,
                        userMessage.componentId(),
                        current.messageCount() + 1,
                        current.historySizeInBytes()));
        case SessionMemoryEntity.Event.MultimodalUserMessageAdded userMessage ->
            effects()
                .updateRow(
                    new SessionRow(
                        sessionId,
                        userMessage.componentId(),
                        current.messageCount() + 1,
                        current.historySizeInBytes()));
        case SessionMemoryEntity.Event.AiMessageAdded aiMessage ->
            effects()
                .updateRow(
                    new SessionRow(
                        sessionId,
                        aiMessage.componentId(),
                        current.messageCount() + 1,
                        aiMessage.historySizeInBytes()));
        case SessionMemoryEntity.Event.ToolResponseMessageAdded toolMessage ->
            effects()
                .updateRow(
                    new SessionRow(
                        sessionId,
                        toolMessage.componentId(),
                        current.messageCount() + 1,
                        current.historySizeInBytes()));
        case SessionMemoryEntity.Event.HistoryCleared ignored ->
            effects().updateRow(new SessionRow(sessionId, current.lastComponentId(), 0, 0));
        case SessionMemoryEntity.Event.Deleted ignored -> effects().deleteRow();
        case SessionMemoryEntity.Event.LimitedWindowSet ignored -> effects().ignore();
      };
    }
  }

  @Query(
      """
      SELECT * AS sessions
      FROM session_memory_by_component
      WHERE lastComponentId = :componentId
      ORDER BY sessionId
      """)
  public QueryEffect<SessionRows> getByComponent(FindByComponent request) {
    return queryResult();
  }
}

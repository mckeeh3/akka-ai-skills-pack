package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import java.time.Instant;
import java.util.List;

/** Topic-backed view for querying and streaming session-memory compaction audit events. */
@Component(id = "session-memory-compaction-audit-view")
public class SessionMemoryCompactionAuditView extends View {

  public record FindBySessionId(String sessionId) {}

  public record AuditRow(
      String sessionId,
      String compactedBy,
      long compactedHistorySizeInBytes,
      String reason,
      Instant observedAt) {}

  public record AuditRows(List<AuditRow> items) {}

  @Consume.FromTopic("session-memory-compactions")
  public static class SessionMemoryCompactionAuditUpdater extends TableUpdater<AuditRow> {

    public Effect<AuditRow> onEvent(SessionMemoryCompactionAudit event) {
      var sessionId = updateContext().eventSubject().orElse("");
      return effects()
          .updateRow(
              new AuditRow(
                  sessionId,
                  event.compactedBy(),
                  event.compactedHistorySizeInBytes(),
                  event.reason(),
                  Instant.now()));
    }
  }

  @Query(
      """
      SELECT * AS items
      FROM session_memory_compaction_audit_view
      WHERE sessionId = :sessionId
      ORDER BY observedAt
      """)
  public QueryEffect<AuditRows> getBySessionId(FindBySessionId request) {
    return queryResult();
  }

  @Query(
      value =
          """
          SELECT *
          FROM session_memory_compaction_audit_view
          WHERE sessionId = :sessionId
          ORDER BY observedAt
          """,
      streamUpdates = true)
  public QueryStreamEffect<AuditRow> continuousBySessionId(FindBySessionId request) {
    return queryStreamResult();
  }
}

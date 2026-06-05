package ai.first.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import ai.first.domain.SupervisedExportState;
import java.util.List;

/**
 * View-backed read/evidence capability for supervised customer exports.
 *
 * <p>The workflow remains the authority for {@code customer.data-export.prepare}. This View exposes
 * the curated {@code customer.data-export.evidence.list} capability for agent and UI consumers: rows
 * are tenant/customer scoped, status-filtered, and intentionally omit raw auth context,
 * idempotency keys, result URIs, and full audit traces.
 */
@Component(id = "supervised-export-evidence")
public class SupervisedExportEvidenceView extends View {

  public record FindExportEvidence(
      String tenantId, String customerId, String status, int minRiskScore, String minRequestId) {}

  public record ExportEvidence(
      String requestId,
      String tenantId,
      String customerId,
      String status,
      String exportType,
      int riskScore,
      boolean supervisionRequired,
      String supervisionReason,
      boolean resultReady,
      String traceId,
      int auditEventCount,
      String latestAuditEvent) {}

  public record ExportEvidenceResults(List<ExportEvidence> exports) {}

  @Consume.FromWorkflow(SupervisedExportWorkflow.class)
  public static class SupervisedExportEvidenceUpdater extends TableUpdater<ExportEvidence> {

    public Effect<ExportEvidence> onUpdate(SupervisedExportState state) {
      var row =
          new ExportEvidence(
              updateContext().eventSubject().orElse(state.requestId()),
              state.tenantId(),
              state.customerId(),
              state.status().name(),
              state.exportType(),
              state.riskScore(),
              state.status() == SupervisedExportState.Status.SUPERVISION_REQUIRED,
              state.supervisionReason(),
              state.status() == SupervisedExportState.Status.READY,
              state.traceId(),
              state.auditTrace().size(),
              latestAuditEvent(state.auditTrace()));
      return effects().updateRow(row);
    }

    private String latestAuditEvent(List<String> auditTrace) {
      if (auditTrace == null || auditTrace.isEmpty()) {
        return "";
      }
      return auditTrace.get(auditTrace.size() - 1);
    }
  }

  @Query(
      """
      SELECT * AS exports
      FROM supervised_export_evidence
      WHERE tenantId = :tenantId
        AND customerId = :customerId
        AND status = :status
        AND riskScore >= :minRiskScore
        AND requestId >= :minRequestId
      ORDER BY riskScore, requestId
      """)
  public QueryEffect<ExportEvidenceResults> findEvidence(FindExportEvidence request) {
    return queryResult();
  }
}

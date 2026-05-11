package com.example.application.supplies;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import java.util.List;

/** Workflow-backed supervision queue for supplies risk and work status. */
@Component(id = "supply-risk-view")
public class SupplyRiskView extends View {

  public record FindByStatus(String status, String minDecisionId) {}

  public record RiskRows(List<RiskRow> risks) {}

  public record RiskRow(
      String workflowId,
      String decisionId,
      String customerId,
      String deviceId,
      String status,
      int tonerPercent,
      double depletionRisk,
      String proposedAction,
      boolean staleDecisionTimerRequested,
      boolean shipmentPrepared,
      String traceId,
      String outcomeId) {}

  @Consume.FromWorkflow(SupplyAutopilotWorkflow.class)
  public static class SupplyRiskUpdater extends TableUpdater<RiskRow> {

    public Effect<RiskRow> onUpdate(SupplyAutopilotWorkflow.State state) {
      var workflowId = updateContext().eventSubject().orElse("");
      var telemetry = state.telemetry();
      var card = state.decisionCard();
      var recommendation = card == null ? null : card.recommendation();

      return effects()
          .updateRow(
              new RiskRow(
                  workflowId,
                  state.decisionId(),
                  telemetry.customerId(),
                  telemetry.deviceId(),
                  state.status().name(),
                  telemetry.tonerPercent(),
                  recommendation == null ? 0.0 : recommendation.depletionRisk(),
                  card == null ? "EVALUATING" : card.proposedAction().name(),
                  state.staleDecisionTimerRequested(),
                  state.shipmentPrepared(),
                  telemetry.trace().traceId(),
                  card == null ? "" : card.outcome().outcomeId()));
    }
  }

  @Query(
      """
      SELECT * AS risks
      FROM supply_risk_view
      WHERE status = :status
        AND decisionId >= :minDecisionId
      ORDER BY decisionId
      """)
  public QueryEffect<RiskRows> getByStatus(FindByStatus request) {
    return queryResult();
  }
}

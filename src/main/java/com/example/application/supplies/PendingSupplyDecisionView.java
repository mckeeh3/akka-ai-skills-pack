package com.example.application.supplies;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.annotations.SnapshotHandler;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import com.example.domain.supplies.Supply.SupplyDecisionCard;
import com.example.domain.supplies.SupplyDecision;
import java.util.List;

/** Event-sourced decision-card index for pending review and completed action history. */
@Component(id = "pending-supply-decision-view")
public class PendingSupplyDecisionView extends View {

  public record FindByStatus(String status, String minDecisionId) {}

  public record DecisionRows(List<DecisionRow> decisions) {}

  public record DecisionRow(
      String decisionId,
      String status,
      String proposedAction,
      String riskSummary,
      String impactSummary,
      double confidence,
      long estimatedCostCents,
      int evidenceCount,
      int policyClauseCount,
      String traceId,
      String outcomeId) {}

  @Consume.FromEventSourcedEntity(SupplyDecisionEntity.class)
  public static class PendingSupplyDecisionUpdater extends TableUpdater<DecisionRow> {

    public Effect<DecisionRow> onEvent(SupplyDecision.Event event) {
      return switch (event) {
        case SupplyDecision.Event.RecommendationOpened opened ->
            effects().updateRow(rowFromCard(SupplyDecision.Status.OPEN, opened.card()));
        case SupplyDecision.Event.ApprovalRequired approvalRequired ->
            effects().updateRow(rowWithStatus(SupplyDecision.Status.APPROVAL_REQUIRED));
        case SupplyDecision.Event.Approved approved -> effects().updateRow(rowWithStatus(SupplyDecision.Status.APPROVED));
        case SupplyDecision.Event.Rejected rejected -> effects().updateRow(rowWithStatus(SupplyDecision.Status.REJECTED));
        case SupplyDecision.Event.Suppressed suppressed -> effects().updateRow(rowWithStatus(SupplyDecision.Status.SUPPRESSED));
        case SupplyDecision.Event.ShipmentPrepared shipmentPrepared ->
            effects().updateRow(rowWithStatus(SupplyDecision.Status.SHIPMENT_PREPARED));
        case SupplyDecision.Event.StaleEscalated staleEscalated ->
            effects().updateRow(rowWithStatus(SupplyDecision.Status.STALE_ESCALATED));
        case SupplyDecision.Event.OutcomeLinked outcomeLinked -> effects().ignore();
      };
    }

    @SnapshotHandler
    public Effect<DecisionRow> onSnapshot(SupplyDecision.State snapshot) {
      return snapshot.card() == null
          ? effects().ignore()
          : effects().updateRow(rowFromCard(snapshot.status(), snapshot.card()));
    }

    private DecisionRow rowWithStatus(SupplyDecision.Status status) {
      var current = rowState();
      return current == null
          ? new DecisionRow(decisionId(), status.name(), "", "", "", 0.0, 0L, 0, 0, "", "")
          : new DecisionRow(
              current.decisionId(),
              status.name(),
              current.proposedAction(),
              current.riskSummary(),
              current.impactSummary(),
              current.confidence(),
              current.estimatedCostCents(),
              current.evidenceCount(),
              current.policyClauseCount(),
              current.traceId(),
              current.outcomeId());
    }

    private DecisionRow rowFromCard(SupplyDecision.Status status, SupplyDecisionCard card) {
      return new DecisionRow(
          decisionId(),
          status.name(),
          card.proposedAction().name(),
          card.riskSummary(),
          card.impactSummary(),
          card.recommendation().confidence(),
          card.recommendation().estimatedCostCents(),
          card.recommendation().evidence().size(),
          card.recommendation().policyClauses().size(),
          card.trace().traceId(),
          card.outcome().outcomeId());
    }

    private String decisionId() {
      return updateContext().eventSubject().orElse("");
    }
  }

  @Query(
      """
      SELECT * AS decisions
      FROM pending_supply_decision_view
      WHERE status = :status
        AND decisionId >= :minDecisionId
      ORDER BY decisionId
      """)
  public QueryEffect<DecisionRows> getByStatus(FindByStatus request) {
    return queryResult();
  }
}

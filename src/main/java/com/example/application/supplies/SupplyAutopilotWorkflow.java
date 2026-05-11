package com.example.application.supplies;

import static java.time.Duration.ofSeconds;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.StepName;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.workflow.Workflow;
import akka.javasdk.workflow.Workflow.RecoverStrategy;
import com.example.domain.supplies.Supply.DecisionAction;
import com.example.domain.supplies.Supply.DeviceTelemetry;
import com.example.domain.supplies.Supply.SupplyDecisionCard;
import com.example.domain.supplies.Supply.SupplyTraceEvent;
import com.example.domain.supplies.Supply.TraceEventType;
import com.example.domain.supplies.SupplyDecision;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Durable orchestration for the supplies autopilot reference slice. */
@Component(id = "supply-autopilot-workflow")
public class SupplyAutopilotWorkflow extends Workflow<SupplyAutopilotWorkflow.State> {

  public enum Status {
    EVALUATING,
    AUTO_SHIPMENT_PREPARED,
    WAITING_FOR_APPROVAL,
    APPROVED_SHIPMENT_PREPARED,
    REJECTED,
    SUPPRESSED,
    EVIDENCE_MISSING,
    STALE_ESCALATED,
    FAILED
  }

  public record State(
      String workflowId,
      String decisionId,
      DeviceTelemetry telemetry,
      SupplyDecisionCard decisionCard,
      Status status,
      boolean staleDecisionTimerRequested,
      boolean shipmentPrepared,
      String message,
      List<String> processedIdempotencyKeys) {

    public State {
      processedIdempotencyKeys =
          List.copyOf(processedIdempotencyKeys == null ? List.of() : processedIdempotencyKeys);
    }

    static State evaluating(String workflowId, DeviceTelemetry telemetry) {
      return new State(
          workflowId,
          workflowId,
          telemetry,
          null,
          Status.EVALUATING,
          false,
          false,
          "evaluating telemetry",
          List.of(telemetry.idempotencyKey()));
    }

    boolean processed(String idempotencyKey) {
      return processedIdempotencyKeys.contains(idempotencyKey);
    }

    State withDecision(SupplyDecisionCard card, Status nextStatus, boolean staleTimer, String message) {
      return new State(
          workflowId,
          decisionId,
          telemetry,
          card,
          nextStatus,
          staleTimer,
          shipmentPrepared,
          message,
          processedIdempotencyKeys);
    }

    State withStatus(Status nextStatus, boolean shipmentPrepared, String message, String idempotencyKey) {
      return new State(
          workflowId,
          decisionId,
          telemetry,
          decisionCard,
          nextStatus,
          staleDecisionTimerRequested,
          shipmentPrepared,
          message,
          appendIfMissing(processedIdempotencyKeys, idempotencyKey));
    }
  }

  public record ApproveDecision(String idempotencyKey, String actor, String rationale) {}

  public record RejectDecision(String idempotencyKey, String actor, String rationale) {}

  public record SuppressDecision(String idempotencyKey, String actor, String rationale) {}

  public record EscalateStaleDecision(String idempotencyKey, String actor, String rationale) {}

  private record AuthorityDecision(String idempotencyKey, String actor, String rationale) {}

  private final ComponentClient componentClient;
  private final SupplyForecastAgent forecastAgent;

  public SupplyAutopilotWorkflow(ComponentClient componentClient) {
    this.componentClient = componentClient;
    this.forecastAgent = new SupplyForecastAgent();
  }

  @Override
  public WorkflowSettings settings() {
    return WorkflowSettings.builder()
        .defaultStepTimeout(ofSeconds(10))
        .defaultStepRecovery(RecoverStrategy.maxRetries(1).failoverTo(SupplyAutopilotWorkflow::markFailedStep))
        .build();
  }

  public Effect<State> start(DeviceTelemetry telemetry) {
    if (telemetry == null) {
      return effects().error("telemetry is required");
    }
    if (currentState() != null) {
      if (currentState().processed(telemetry.idempotencyKey())) {
        return effects().reply(currentState());
      }
      return effects().error("supply autopilot already started");
    }

    var initial = State.evaluating(commandContext().workflowId(), telemetry);
    return effects()
        .updateState(initial)
        .transitionTo(SupplyAutopilotWorkflow::evaluateRecommendationStep)
        .thenReply(initial);
  }

  public Effect<State> approve(ApproveDecision approval) {
    var errors = validateStartedAuthorityCommand(approval.idempotencyKey(), approval.actor(), approval.rationale());
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    if (currentState().processed(approval.idempotencyKey())) {
      return effects().reply(currentState());
    }
    if (currentState().status() != Status.WAITING_FOR_APPROVAL) {
      return effects().error("decision is not waiting for approval");
    }
    return effects()
        .transitionTo(SupplyAutopilotWorkflow::approveAndShipStep)
        .withInput(new AuthorityDecision(approval.idempotencyKey(), approval.actor(), approval.rationale()))
        .thenReply(currentState());
  }

  public Effect<State> reject(RejectDecision rejection) {
    var errors = validateStartedAuthorityCommand(rejection.idempotencyKey(), rejection.actor(), rejection.rationale());
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    if (currentState().processed(rejection.idempotencyKey())) {
      return effects().reply(currentState());
    }
    if (currentState().status() != Status.WAITING_FOR_APPROVAL) {
      return effects().error("decision is not waiting for approval");
    }
    return effects()
        .transitionTo(SupplyAutopilotWorkflow::rejectStep)
        .withInput(new AuthorityDecision(rejection.idempotencyKey(), rejection.actor(), rejection.rationale()))
        .thenReply(currentState());
  }

  public Effect<State> suppress(SuppressDecision suppression) {
    var errors = validateStartedAuthorityCommand(
        suppression.idempotencyKey(), suppression.actor(), suppression.rationale());
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    if (currentState().processed(suppression.idempotencyKey())) {
      return effects().reply(currentState());
    }
    return effects()
        .transitionTo(SupplyAutopilotWorkflow::suppressStep)
        .withInput(new AuthorityDecision(suppression.idempotencyKey(), suppression.actor(), suppression.rationale()))
        .thenReply(currentState());
  }

  public Effect<State> escalateStale(EscalateStaleDecision stale) {
    var errors = validateStartedAuthorityCommand(stale.idempotencyKey(), stale.actor(), stale.rationale());
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    if (currentState().processed(stale.idempotencyKey())) {
      return effects().reply(currentState());
    }
    if (currentState().status() != Status.WAITING_FOR_APPROVAL) {
      return effects().error("decision is not waiting for approval");
    }
    return effects()
        .transitionTo(SupplyAutopilotWorkflow::escalateStaleStep)
        .withInput(new AuthorityDecision(stale.idempotencyKey(), stale.actor(), stale.rationale()))
        .thenReply(currentState());
  }

  public ReadOnlyEffect<State> get() {
    if (currentState() == null) {
      return effects().error("supply autopilot not started");
    }
    return effects().reply(currentState());
  }

  @StepName("evaluate-recommendation")
  private StepEffect evaluateRecommendationStep() {
    var draft = forecastAgent.recommend(currentState().telemetry(), currentState().decisionId());
    if (draft.missingRequiredEvidence()) {
      return stepEffects()
          .updateState(
              currentState()
                  .withDecision(
                      draft.decisionCard(),
                      Status.EVIDENCE_MISSING,
                      false,
                      "missing required entitlement evidence; escalated without side effect"))
          .thenEnd();
    }

    openDecision(draft.decisionCard());

    return switch (draft.recommendedAction()) {
      case AUTO_SHIP -> stepEffects()
          .updateState(currentState().withDecision(draft.decisionCard(), Status.EVALUATING, false, "auto shipment allowed"))
          .thenTransitionTo(SupplyAutopilotWorkflow::prepareAutoShipmentStep);
      case REQUIRE_APPROVAL -> stepEffects()
          .updateState(
              currentState()
                  .withDecision(
                      draft.decisionCard(),
                      Status.WAITING_FOR_APPROVAL,
                      true,
                      "approval required by supply policy"))
          .thenTransitionTo(SupplyAutopilotWorkflow::waitForApprovalStep);
      case SUPPRESS_SHIPMENT -> stepEffects()
          .updateState(currentState().withDecision(draft.decisionCard(), Status.EVALUATING, false, "shipment suppressed by policy"))
          .thenTransitionTo(SupplyAutopilotWorkflow::autoSuppressStep);
      default -> stepEffects()
          .updateState(
              currentState()
                  .withDecision(
                      draft.decisionCard(),
                      Status.EVIDENCE_MISSING,
                      false,
                      "policy exception requires human evidence before side effects"))
          .thenEnd();
    };
  }

  @StepName("prepare-auto-shipment")
  private StepEffect prepareAutoShipmentStep() {
    prepareShipment("idem-ship-" + currentState().decisionId(), "auto shipment prepared by workflow policy gate");
    return stepEffects()
        .updateState(
            currentState()
                .withStatus(
                    Status.AUTO_SHIPMENT_PREPARED,
                    true,
                    "auto shipment prepared after workflow-owned policy gate",
                    "idem-ship-" + currentState().decisionId()))
        .thenEnd();
  }

  @StepName("wait-for-approval")
  private StepEffect waitForApprovalStep() {
    return stepEffects().thenPause();
  }

  @StepName("approve-and-ship")
  private StepEffect approveAndShipStep(AuthorityDecision decision) {
    componentClient
        .forEventSourcedEntity(currentState().decisionId())
        .method(SupplyDecisionEntity::approve)
        .invoke(
            new SupplyDecision.Command.Approve(
                decision.idempotencyKey(),
                decision.actor(),
                decision.rationale(),
                traceEvent(TraceEventType.APPROVAL_RECORDED, decision.idempotencyKey(), decision.actor(), decision.rationale())));

    var shipKey = "idem-ship-approved-" + currentState().decisionId();
    prepareShipment(shipKey, "approved shipment prepared by workflow");
    return stepEffects()
        .updateState(
            currentState()
                .withStatus(
                    Status.APPROVED_SHIPMENT_PREPARED,
                    true,
                    "approved shipment prepared",
                    decision.idempotencyKey())
                .withStatus(
                    Status.APPROVED_SHIPMENT_PREPARED,
                    true,
                    "approved shipment prepared",
                    shipKey))
        .thenEnd();
  }

  @StepName("reject")
  private StepEffect rejectStep(AuthorityDecision decision) {
    componentClient
        .forEventSourcedEntity(currentState().decisionId())
        .method(SupplyDecisionEntity::reject)
        .invoke(
            new SupplyDecision.Command.Reject(
                decision.idempotencyKey(),
                decision.actor(),
                decision.rationale(),
                traceEvent(TraceEventType.REJECTION_RECORDED, decision.idempotencyKey(), decision.actor(), decision.rationale())));
    return stepEffects()
        .updateState(currentState().withStatus(Status.REJECTED, false, "decision rejected by human reviewer", decision.idempotencyKey()))
        .thenEnd();
  }

  @StepName("auto-suppress")
  private StepEffect autoSuppressStep() {
    var idempotencyKey = "idem-suppress-" + currentState().decisionId();
    suppressDecision(idempotencyKey, "supply-autopilot-workflow", "policy suppressed shipment before side effect");
    return stepEffects()
        .updateState(currentState().withStatus(Status.SUPPRESSED, false, "shipment suppressed by workflow policy gate", idempotencyKey))
        .thenEnd();
  }

  @StepName("suppress")
  private StepEffect suppressStep(AuthorityDecision decision) {
    suppressDecision(decision.idempotencyKey(), decision.actor(), decision.rationale());
    return stepEffects()
        .updateState(currentState().withStatus(Status.SUPPRESSED, false, "shipment suppressed by reviewer", decision.idempotencyKey()))
        .thenEnd();
  }

  @StepName("escalate-stale")
  private StepEffect escalateStaleStep(AuthorityDecision decision) {
    componentClient
        .forEventSourcedEntity(currentState().decisionId())
        .method(SupplyDecisionEntity::escalateStale)
        .invoke(
            new SupplyDecision.Command.EscalateStale(
                decision.idempotencyKey(),
                decision.actor(),
                decision.rationale(),
                traceEvent(TraceEventType.STALE_DECISION_ESCALATED, decision.idempotencyKey(), decision.actor(), decision.rationale())));
    return stepEffects()
        .updateState(currentState().withStatus(Status.STALE_ESCALATED, false, "pending approval escalated as stale", decision.idempotencyKey()))
        .thenEnd();
  }

  private StepEffect markFailedStep() {
    return stepEffects().updateState(currentState().withStatus(Status.FAILED, false, "workflow step failed after retry", "idem-failed-" + currentState().decisionId())).thenEnd();
  }

  private void openDecision(SupplyDecisionCard card) {
    var idempotencyKey = "idem-open-" + currentState().decisionId();
    componentClient
        .forEventSourcedEntity(currentState().decisionId())
        .method(SupplyDecisionEntity::openRecommendation)
        .invoke(
            new SupplyDecision.Command.OpenRecommendation(
                idempotencyKey,
                card,
                traceEventForCard(
                    card,
                    TraceEventType.RECOMMENDATION_CREATED,
                    idempotencyKey,
                    "supply-forecast-agent",
                    card.recommendation().rationale())));
  }

  private void prepareShipment(String idempotencyKey, String rationale) {
    componentClient
        .forEventSourcedEntity(currentState().decisionId())
        .method(SupplyDecisionEntity::prepareShipment)
        .invoke(
            new SupplyDecision.Command.PrepareShipment(
                idempotencyKey,
                "supply-autopilot-workflow",
                rationale,
                traceEvent(TraceEventType.SHIPMENT_PREPARED, idempotencyKey, "supply-autopilot-workflow", rationale)));
  }

  private void suppressDecision(String idempotencyKey, String actor, String rationale) {
    componentClient
        .forEventSourcedEntity(currentState().decisionId())
        .method(SupplyDecisionEntity::suppress)
        .invoke(
            new SupplyDecision.Command.Suppress(
                idempotencyKey,
                actor,
                rationale,
                traceEvent(TraceEventType.SHIPMENT_SUPPRESSED, idempotencyKey, actor, rationale)));
  }

  private SupplyTraceEvent traceEvent(
      TraceEventType type, String idempotencyKey, String actor, String summary) {
    return traceEventForCard(currentState().decisionCard(), type, idempotencyKey, actor, summary);
  }

  private SupplyTraceEvent traceEventForCard(
      SupplyDecisionCard card, TraceEventType type, String idempotencyKey, String actor, String summary) {
    return new SupplyTraceEvent(
        "trace-event-" + idempotencyKey.substring("idem-".length()),
        type,
        currentState().telemetry().trace(),
        idempotencyKey,
        Instant.now(),
        actor,
        summary,
        card == null ? List.of() : card.recommendation().policyClauses(),
        card == null ? null : card.outcome());
  }

  private List<String> validateAuthorityCommand(String idempotencyKey, String actor, String rationale) {
    var errors = validateStartedAuthorityCommand(idempotencyKey, actor, rationale);
    if (errors.isEmpty() && currentState().status() != Status.WAITING_FOR_APPROVAL) {
      errors.add("decision is not waiting for approval");
    }
    return errors;
  }

  private List<String> validateStartedAuthorityCommand(String idempotencyKey, String actor, String rationale) {
    var errors = new ArrayList<String>();
    if (currentState() == null) {
      errors.add("supply autopilot not started");
      return errors;
    }
    try {
      com.example.domain.supplies.Supply.requireIdempotencyKey(idempotencyKey);
    } catch (IllegalArgumentException ex) {
      errors.add(ex.getMessage());
    }
    try {
      com.example.domain.supplies.Supply.requireNonBlank(actor, "actor");
    } catch (IllegalArgumentException ex) {
      errors.add(ex.getMessage());
    }
    try {
      com.example.domain.supplies.Supply.requireNonBlank(rationale, "rationale");
    } catch (IllegalArgumentException ex) {
      errors.add(ex.getMessage());
    }
    return errors;
  }

  private static List<String> appendIfMissing(List<String> values, String value) {
    if (values.contains(value)) {
      return values;
    }
    var copy = new ArrayList<>(values);
    copy.add(value);
    return List.copyOf(copy);
  }
}

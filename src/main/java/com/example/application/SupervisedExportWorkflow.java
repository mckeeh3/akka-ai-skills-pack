package com.example.application;

import static java.time.Duration.ofSeconds;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.StepName;
import akka.javasdk.workflow.Workflow;
import com.example.domain.SupervisedExportState;

/**
 * Workflow-backed customer.data-export.prepare capability example.
 *
 * <p>The workflow preserves tenant/customer scope, authority basis, supervision state, and trace
 * events while preparing a long-running customer export. High-risk exports pause for human
 * supervision before the simulated export side effect is committed.
 */
@Component(id = "supervised-export-workflow")
public class SupervisedExportWorkflow extends Workflow<SupervisedExportState> {

  private static final int SUPERVISION_RISK_THRESHOLD = 75;

  public record StartExport(
      String idempotencyKey,
      String tenantId,
      String customerId,
      String requestedBy,
      String authContextId,
      String exportType,
      int riskScore) {}

  public record SupervisionDecision(String idempotencyKey, String supervisor, String rationale) {}

  @Override
  public WorkflowSettings settings() {
    return WorkflowSettings.builder().defaultStepTimeout(ofSeconds(5)).build();
  }

  public Effect<SupervisedExportState> start(StartExport request) {
    var validation = validateStart(request);
    if (!validation.isBlank()) {
      return effects().error(validation);
    }
    if (currentState() != null) {
      if (currentState().processed(request.idempotencyKey())) {
        return effects().reply(currentState());
      }
      return effects().error("export workflow already started");
    }

    var initial =
        SupervisedExportState.started(
            commandContext().workflowId(),
            request.tenantId(),
            request.customerId(),
            request.requestedBy(),
            request.authContextId(),
            request.idempotencyKey(),
            request.exportType(),
            request.riskScore());

    return effects()
        .updateState(initial)
        .transitionTo(SupervisedExportWorkflow::assessRiskStep)
        .thenReply(initial);
  }

  public Effect<SupervisedExportState> approve(SupervisionDecision decision) {
    var validation = validateDecision(decision);
    if (!validation.isBlank()) {
      return effects().error(validation);
    }
    if (currentState().processed(decision.idempotencyKey())) {
      return effects().reply(currentState());
    }
    if (currentState().status() != SupervisedExportState.Status.SUPERVISION_REQUIRED) {
      return effects().error("export is not waiting for supervision");
    }

    return effects()
        .updateState(currentState().approved(decision.idempotencyKey(), decision.supervisor(), decision.rationale()))
        .transitionTo(SupervisedExportWorkflow::generateExportStep)
        .thenReply(currentState());
  }

  public Effect<SupervisedExportState> deny(SupervisionDecision decision) {
    var validation = validateDecision(decision);
    if (!validation.isBlank()) {
      return effects().error(validation);
    }
    if (currentState().processed(decision.idempotencyKey())) {
      return effects().reply(currentState());
    }
    if (currentState().status() != SupervisedExportState.Status.SUPERVISION_REQUIRED) {
      return effects().error("export is not waiting for supervision");
    }

    return effects()
        .transitionTo(SupervisedExportWorkflow::denyExportStep)
        .withInput(decision)
        .thenReply(currentState());
  }

  public ReadOnlyEffect<SupervisedExportState> get() {
    if (currentState() == null) {
      return effects().error("export workflow not started");
    }
    return effects().reply(currentState());
  }

  @StepName("assess-risk")
  private StepEffect assessRiskStep() {
    if (currentState().riskScore() >= SUPERVISION_RISK_THRESHOLD) {
      return stepEffects()
          .updateState(
              currentState()
                  .riskAssessed("supervision-required")
                  .supervisionRequired("risk-score-" + currentState().riskScore()))
          .thenPause();
    }

    return stepEffects()
        .updateState(currentState().riskAssessed("auto-allowed"))
        .thenTransitionTo(SupervisedExportWorkflow::generateExportStep);
  }

  @StepName("deny-export")
  private StepEffect denyExportStep(SupervisionDecision decision) {
    return stepEffects()
        .updateState(currentState().denied(decision.idempotencyKey(), decision.supervisor(), decision.rationale()))
        .thenEnd();
  }

  @StepName("generate-export")
  private StepEffect generateExportStep() {
    return stepEffects()
        .updateState(currentState().generating())
        .thenTransitionTo(SupervisedExportWorkflow::markReadyStep);
  }

  @StepName("mark-ready")
  private StepEffect markReadyStep() {
    var uri =
        "export://"
            + currentState().tenantId()
            + "/"
            + currentState().customerId()
            + "/"
            + currentState().requestId();
    return stepEffects().updateState(currentState().ready(uri)).thenEnd();
  }

  private String validateStart(StartExport request) {
    if (request == null) {
      return "request is required";
    }
    if (blank(request.idempotencyKey())) {
      return "idempotencyKey is required";
    }
    if (blank(request.tenantId())) {
      return "tenantId is required";
    }
    if (blank(request.customerId())) {
      return "customerId is required";
    }
    if (blank(request.requestedBy())) {
      return "requestedBy is required";
    }
    if (blank(request.authContextId())) {
      return "authContextId is required";
    }
    if (blank(request.exportType())) {
      return "exportType is required";
    }
    if (request.riskScore() < 0 || request.riskScore() > 100) {
      return "riskScore must be between 0 and 100";
    }
    return "";
  }

  private String validateDecision(SupervisionDecision decision) {
    if (currentState() == null) {
      return "export workflow not started";
    }
    if (decision == null) {
      return "decision is required";
    }
    if (blank(decision.idempotencyKey())) {
      return "idempotencyKey is required";
    }
    if (blank(decision.supervisor())) {
      return "supervisor is required";
    }
    if (blank(decision.rationale())) {
      return "rationale is required";
    }
    return "";
  }

  private boolean blank(String value) {
    return value == null || value.isBlank();
  }
}

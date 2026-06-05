package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.StepName;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.timer.TimerScheduler;
import akka.javasdk.workflow.Workflow;
import com.example.domain.ApprovalDeadlineState;
import java.time.Duration;

/**
 * Workflow example that schedules a timeout timer from the workflow start command.
 *
 * <p>The workflow pauses while waiting for approval. A timer registered by the workflow start
 * command later triggers a timed action, which calls back into the workflow with a timeout command.
 */
@Component(id = "approval-deadline-workflow")
public class ApprovalDeadlineWorkflow extends Workflow<ApprovalDeadlineState> {

  public record StartApproval(String documentId, String requestedBy, int timeoutSeconds) {}

  public record Approve(String approvedBy, String comment) {}

  private record ApprovalDecision(String approvedBy, String comment) {}

  private final TimerScheduler timerScheduler;
  private final ComponentClient componentClient;

  public ApprovalDeadlineWorkflow(TimerScheduler timerScheduler, ComponentClient componentClient) {
    this.timerScheduler = timerScheduler;
    this.componentClient = componentClient;
  }

  public Effect<ApprovalDeadlineState> start(StartApproval request) {
    if (request.documentId().isBlank()) {
      return effects().error("documentId must not be blank");
    } else if (request.requestedBy().isBlank()) {
      return effects().error("requestedBy must not be blank");
    } else if (request.timeoutSeconds() <= 0) {
      return effects().error("timeoutSeconds must be greater than zero");
    } else if (currentState() != null) {
      return effects().error("approval deadline workflow already started");
    }

    var workflowId = commandContext().workflowId();
    timerScheduler.createSingleTimer(
        timerName(workflowId),
        Duration.ofSeconds(request.timeoutSeconds()),
        componentClient
            .forTimedAction()
            .method(ApprovalDeadlineTimedAction::timeoutApproval)
            .deferred(workflowId));

    var initialState =
        ApprovalDeadlineState.waiting(
            request.documentId(), request.requestedBy(), request.timeoutSeconds());

    return effects()
        .updateState(initialState)
        .transitionTo(ApprovalDeadlineWorkflow::waitForApprovalStep)
        .thenReply(initialState);
  }

  public Effect<String> approve(Approve request) {
    if (currentState() == null) {
      return effects().error("approval deadline workflow not started");
    } else if (currentState().status() != ApprovalDeadlineState.Status.WAITING_FOR_APPROVAL) {
      return effects().error("approval deadline workflow is not waiting for approval");
    } else if (request.approvedBy().isBlank()) {
      return effects().error("approvedBy must not be blank");
    }

    timerScheduler.delete(timerName(commandContext().workflowId()));

    return effects()
        .transitionTo(ApprovalDeadlineWorkflow::applyApprovalStep)
        .withInput(new ApprovalDecision(request.approvedBy(), request.comment()))
        .thenReply("approval accepted");
  }

  /**
   * Timer-safe command: never fails for missing or terminal states, so the timed action can return
   * success and the runtime can drop the timer.
   */
  public Effect<String> markTimedOut() {
    if (currentState() == null) {
      return effects().reply("approval deadline workflow not started");
    } else if (currentState().status() == ApprovalDeadlineState.Status.TIMED_OUT) {
      return effects().reply("approval already timed out");
    } else if (currentState().status() == ApprovalDeadlineState.Status.APPROVED) {
      return effects().reply("approval already completed");
    }

    return effects()
        .transitionTo(ApprovalDeadlineWorkflow::timeoutStep)
        .thenReply("approval timed out");
  }

  public ReadOnlyEffect<ApprovalDeadlineState> get() {
    if (currentState() == null) {
      return effects().error("approval deadline workflow not started");
    }
    return effects().reply(currentState());
  }

  @StepName("wait-for-approval")
  private StepEffect waitForApprovalStep() {
    return stepEffects().thenPause();
  }

  @StepName("apply-approval")
  private StepEffect applyApprovalStep(ApprovalDecision decision) {
    return stepEffects()
        .updateState(currentState().approved(decision.approvedBy(), decision.comment()))
        .thenEnd();
  }

  @StepName("timeout-approval")
  private StepEffect timeoutStep() {
    return stepEffects().updateState(currentState().timedOut()).thenEnd();
  }

  public static String timerName(String workflowId) {
    return "approval-deadline-" + workflowId;
  }
}

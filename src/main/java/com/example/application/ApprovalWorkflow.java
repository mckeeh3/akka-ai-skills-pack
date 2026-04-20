package com.example.application;

import akka.javasdk.NotificationPublisher;
import akka.javasdk.NotificationPublisher.NotificationStream;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.StepName;
import akka.javasdk.workflow.Workflow;
import com.example.domain.ApprovalState;

/** Workflow example that pauses until an explicit approval command resumes it. */
@Component(id = "approval-workflow")
public class ApprovalWorkflow extends Workflow<ApprovalState> {

  public record StartApproval(String documentId, String requestedBy) {}

  public record Approve(String approvedBy, String comment) {}

  public record ApprovalUpdate(String step, String status, String message) {}

  private record ApprovalDecision(String approvedBy, String comment) {}

  private final NotificationPublisher<ApprovalUpdate> notificationPublisher;

  public ApprovalWorkflow(NotificationPublisher<ApprovalUpdate> notificationPublisher) {
    this.notificationPublisher = notificationPublisher;
  }

  public Effect<ApprovalState> start(StartApproval request) {
    if (request.documentId().isBlank()) {
      return effects().error("documentId must not be blank");
    } else if (request.requestedBy().isBlank()) {
      return effects().error("requestedBy must not be blank");
    } else if (currentState() != null) {
      return effects().error("approval already started");
    }

    var initialState = ApprovalState.waiting(request.documentId(), request.requestedBy());

    return effects()
        .updateState(initialState)
        .transitionTo(ApprovalWorkflow::waitForApprovalStep)
        .thenReply(initialState);
  }

  public Effect<String> approve(Approve request) {
    if (currentState() == null) {
      return effects().error("approval not started");
    } else if (currentState().status() != ApprovalState.Status.WAITING_FOR_APPROVAL) {
      return effects().error("approval is not waiting for approval");
    } else if (request.approvedBy().isBlank()) {
      return effects().error("approvedBy must not be blank");
    }

    return effects()
        .transitionTo(ApprovalWorkflow::applyApprovalStep)
        .withInput(new ApprovalDecision(request.approvedBy(), request.comment()))
        .thenReply("approval accepted");
  }

  public ReadOnlyEffect<ApprovalState> get() {
    if (currentState() == null) {
      return effects().error("approval not started");
    }
    return effects().reply(currentState());
  }

  public NotificationStream<ApprovalUpdate> updates() {
    return notificationPublisher.stream();
  }

  @StepName("wait-for-approval")
  private StepEffect waitForApprovalStep() {
    notificationPublisher.publish(
        new ApprovalUpdate(
            "wait-for-approval",
            ApprovalState.Status.WAITING_FOR_APPROVAL.name(),
            "Approval is waiting for an approver"));
    return stepEffects().thenPause();
  }

  @StepName("apply-approval")
  private StepEffect applyApprovalStep(ApprovalDecision decision) {
    notificationPublisher.publish(
        new ApprovalUpdate(
            "apply-approval",
            ApprovalState.Status.APPROVED.name(),
            "Approval decision was applied"));
    return stepEffects()
        .updateState(currentState().approved(decision.approvedBy(), decision.comment()))
        .thenEnd();
  }
}

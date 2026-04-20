package com.example.application;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.StepName;
import akka.javasdk.workflow.Workflow;

/**
 * Minimal workflow used as a source for workflow-backed view examples.
 */
@Component(id = "review-workflow")
public class ReviewWorkflow extends Workflow<ReviewWorkflow.State> {

  public record Start(String requestId) {}

  public record State(String requestId, String status) {
    public State withStatus(String newStatus) {
      return new State(requestId, newStatus);
    }
  }

  public Effect<Done> start(Start request) {
    return effects()
        .updateState(new State(request.requestId(), "PENDING"))
        .transitionTo(ReviewWorkflow::completeStep)
        .thenReply(Done.getInstance());
  }

  @StepName("complete")
  private StepEffect completeStep() {
    return stepEffects().updateState(currentState().withStatus("COMPLETED")).thenEnd();
  }
}

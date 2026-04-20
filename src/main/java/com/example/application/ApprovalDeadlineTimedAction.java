package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.timedaction.TimedAction;

/** Timed action that invokes the timeout command on the deadline-aware approval workflow. */
@Component(id = "approval-deadline-timed-action")
public class ApprovalDeadlineTimedAction extends TimedAction {

  private final ComponentClient componentClient;

  public ApprovalDeadlineTimedAction(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public Effect timeoutApproval(String approvalId) {
    componentClient
        .forWorkflow(approvalId)
        .method(ApprovalDeadlineWorkflow::markTimedOut)
        .invoke();
    return effects().done();
  }
}

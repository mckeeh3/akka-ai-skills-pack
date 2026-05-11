package com.example.application.supplies;

import akka.javasdk.CommandException;
import akka.javasdk.annotations.Component;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.timedaction.TimedAction;

/** Timer callback for stale supply decisions that delegates authority to the workflow. */
@Component(id = "supply-decision-timed-action")
public class SupplyDecisionTimedAction extends TimedAction {

  private final ComponentClient componentClient;

  public SupplyDecisionTimedAction(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public Effect escalateStaleDecision(String workflowId) {
    try {
      componentClient
          .forWorkflow(workflowId)
          .method(SupplyAutopilotWorkflow::escalateStale)
          .invoke(
              new SupplyAutopilotWorkflow.EscalateStaleDecision(
                  idempotencyKey(workflowId), "supply-decision-timer", "approval SLA elapsed"));
      return effects().done();
    } catch (CommandException ignoredObsoleteTimer) {
      // The workflow owns the authoritative state. If the decision is already approved,
      // suppressed, rejected, shipped, stale-escalated, or absent, the callback is obsolete.
      return effects().done();
    }
  }

  public static String timerName(String workflowId) {
    return "supply-decision-stale-" + workflowId;
  }

  private static String idempotencyKey(String workflowId) {
    return "idem-stale-" + workflowId;
  }
}

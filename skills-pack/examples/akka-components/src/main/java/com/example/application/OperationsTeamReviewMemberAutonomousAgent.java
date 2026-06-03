package com.example.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** TeamLeadership member focused on operations and rollout review work. */
@Component(
    id = "operations-team-review-member-autonomous-agent",
    description = "Claims shared-backlog operations review work and completes typed rollout findings.")
public class OperationsTeamReviewMemberAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            "Claim operations or rollout review work from the shared backlog, coordinate when needed, and complete a concise typed finding.")
        .capability(TaskAcceptance.of(TeamLeadershipReviewTasks.REVIEW_WORK_ITEM).maxIterationsPerTask(4));
  }
}

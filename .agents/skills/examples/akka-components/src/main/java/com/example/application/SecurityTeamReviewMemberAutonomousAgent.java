package com.example.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** TeamLeadership member focused on security and authority review work. */
@Component(
    id = "security-team-review-member-autonomous-agent",
    description = "Claims shared-backlog security review work and completes typed authority findings.")
public class SecurityTeamReviewMemberAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            "Claim security or authority review work from the shared backlog, coordinate when needed, and complete a concise typed finding.")
        .capability(TaskAcceptance.of(TeamLeadershipReviewTasks.REVIEW_WORK_ITEM).maxIterationsPerTask(4));
  }
}

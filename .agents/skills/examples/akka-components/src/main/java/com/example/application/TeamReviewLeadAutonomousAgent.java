package com.example.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.agent.autonomous.capability.TeamLeadership;
import akka.javasdk.agent.autonomous.capability.TeamLeadership.TeamMember;
import akka.javasdk.annotations.Component;

/** Autonomous Agent example that leads a shared-backlog review team. */
@Component(
    id = "team-review-lead-autonomous-agent",
    description =
        "Leads a durable background review team by creating shared-backlog work, monitoring member completion, and synthesizing a typed result.")
public class TeamReviewLeadAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            "Create a small shared-backlog team for interdependent review work. Add focused work items, monitor status, and synthesize only after members complete their claimed tasks. TeamLeadership coordinates work; it does not expand backend authority or replace approval checks.")
        .capability(TaskAcceptance.of(TeamLeadershipReviewTasks.TEAM_REVIEW).maxIterationsPerTask(8))
        .capability(
            TeamLeadership.of(
                    TeamMember.of(SecurityTeamReviewMemberAutonomousAgent.class).maxInstances(1),
                    TeamMember.of(OperationsTeamReviewMemberAutonomousAgent.class).maxInstances(1))
                .maxConcurrentTeams(1));
  }
}

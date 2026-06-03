package com.example.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.Moderation;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** Autonomous Agent example that moderates a structured review conversation. */
@Component(
    id = "review-moderator-autonomous-agent",
    description =
        "Moderates a durable background review by asking technical and policy panelists for focused input, then returning a typed decision.")
public class ReviewModeratorAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            "Run a short structured review. Ask each panelist for a focused finding, then synthesize the transcript into the final typed review result. Moderation coordinates expert input; it does not replace backend approval or authority checks for consequential decisions.")
        .capability(TaskAcceptance.of(ReviewModerationTasks.REVIEW).maxIterationsPerTask(5))
        .capability(
            Moderation.of(
                    TechnicalReviewPanelistAutonomousAgent.class,
                    PolicyReviewPanelistAutonomousAgent.class)
                .maxRounds(3)
                .maxIterationsPerTurn(2)
                .maxConcurrentConversations(1));
  }
}

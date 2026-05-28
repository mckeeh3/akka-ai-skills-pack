package com.example.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.annotations.Component;

/** Participant in the Autonomous Agent moderation example. */
@Component(
    id = "policy-review-panelist-autonomous-agent",
    description = "Reviews proposals for policy fit, approval needs, and authority boundaries.")
public class PolicyReviewPanelistAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define();
  }
}

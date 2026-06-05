package ai.first.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.annotations.Component;

/** Participant in the Autonomous Agent moderation example. */
@Component(
    id = "technical-review-panelist-autonomous-agent",
    description = "Reviews proposals for technical feasibility, implementation risk, and missing evidence.")
public class TechnicalReviewPanelistAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define();
  }
}

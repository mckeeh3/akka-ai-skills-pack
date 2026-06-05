package ai.first.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** Autonomous Agent example that relies on a TaskRule to reject and retry weak evidence. */
@Component(
    id = "evidence-review-autonomous-agent",
    description = "Reviews one issue as a durable task and returns a sourced recommendation.")
public class EvidenceReviewAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            "Review the submitted issue. Return a concise recommendation and cite at least one evidence source.")
        .capability(TaskAcceptance.of(EvidenceReviewTasks.REVIEW).maxIterationsPerTask(3));
  }
}

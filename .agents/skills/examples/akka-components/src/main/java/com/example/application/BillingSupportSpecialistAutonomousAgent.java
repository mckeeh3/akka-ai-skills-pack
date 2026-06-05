package com.example.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** Autonomous Agent specialist that accepts handed-off support resolution tasks. */
@Component(
    id = "billing-support-specialist-autonomous-agent",
    description = "Resolves billing disputes, invoice questions, and payment correction requests.")
public class BillingSupportSpecialistAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            "Resolve billing support requests from handed-off triage context. Keep the result concise and include the billing category.")
        .capability(TaskAcceptance.of(HandoffTriageTasks.RESOLVE).maxIterationsPerTask(5));
  }
}

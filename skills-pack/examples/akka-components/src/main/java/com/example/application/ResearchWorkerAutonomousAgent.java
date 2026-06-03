package com.example.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** Autonomous Agent worker example that completes delegated research findings tasks. */
@Component(
    id = "research-worker-autonomous-agent",
    description = "Researches a focused topic and returns concise factual findings with source labels.")
public class ResearchWorkerAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions("Return factual findings with concise source labels for the assigned topic.")
        .capability(TaskAcceptance.of(ResearchCoordinationTasks.FINDINGS).maxIterationsPerTask(3));
  }
}

package com.example.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.Delegation;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** Autonomous Agent coordinator example that delegates focused research to a specialist worker. */
@Component(
    id = "research-coordinator-autonomous-agent",
    description =
        "Coordinates a durable background research task by delegating focused fact gathering and synthesizing a typed brief.")
public class ResearchCoordinatorAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            "Create concise research briefs. Delegate factual investigation when specialist findings will improve the result, then synthesize the worker result into the final brief.")
        .capability(TaskAcceptance.of(ResearchCoordinationTasks.BRIEF).maxIterationsPerTask(5))
        .capability(Delegation.to(ResearchWorkerAutonomousAgent.class).maxParallelWorkers(1));
  }
}

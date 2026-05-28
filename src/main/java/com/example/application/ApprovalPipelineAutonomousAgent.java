package com.example.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/**
 * Autonomous Agent example for work on either side of an external approval gate.
 *
 * <p>Use this pattern when the approval is part of the autonomous task graph. Prefer a Workflow
 * pause/resume boundary when product correctness depends on a fixed business process, explicit
 * timeouts, compensation, or broader deterministic orchestration around the approval.
 */
@Component(
    id = "approval-pipeline-autonomous-agent",
    description =
        "Investigates an issue, then finalizes the approved result after an external approval task completes.")
public class ApprovalPipelineAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            "Work only on the assigned task. For investigation tasks, return evidence and a recommendation. For publish tasks, finalize only after the dependency approval task has completed.")
        .capability(
            TaskAcceptance.of(ApprovalPipelineTasks.INVESTIGATE, ApprovalPipelineTasks.PUBLISH)
                .maxIterationsPerTask(3));
  }
}

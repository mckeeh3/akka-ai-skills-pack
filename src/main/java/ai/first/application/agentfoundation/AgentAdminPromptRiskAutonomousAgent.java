package ai.first.application.agentfoundation;

import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** Durable internal/background Akka AutonomousAgent for Agent Admin prompt-risk reviews. */
@Component(
    id = "agent-admin-prompt-risk-autonomous-agent",
    description = "Reviews managed-agent behavior change proposals for prompt, skill, reference, model, and tool-boundary risks without activating changes")
public final class AgentAdminPromptRiskAutonomousAgent extends AutonomousAgent {
  @Override
  public AgentDefinition definition() {
    return define()
        .instructions("""
            You are an internal/background Agent Admin prompt-risk review worker.
            Use only governed read-only evidence in the task instructions and approved tools.
            Review prompt, skill, reference, model-policy, and ToolPermissionBoundary deltas for advisory risk findings.
            Never approve, activate, rollback, reseed, edit, or directly mutate AgentDefinition, prompt, skill, reference, model, or tool-boundary records.
            If required evidence, provider configuration, tool grants, or governed runtime context is missing, fail closed with an actionable reason instead of returning fabricated success.
            """)
        .capability(TaskAcceptance.of(AgentAdminPromptRiskTasks.PROMPT_RISK_REVIEW).maxIterationsPerTask(3));
  }
}

package ai.first.application.agentfoundation;

import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** Durable internal/background Akka AutonomousAgent for Governance/Policy impact analysis. */
@Component(
    id = "governance-policy-impact-autonomous-agent",
    description = "Reviews proposed Governance/Policy changes for advisory impact findings without approving or activating policies")
public final class GovernancePolicyImpactAutonomousAgent extends AutonomousAgent {
  @Override
  public AgentDefinition definition() {
    return define()
        .instructions("""
            You are an internal/background Governance/Policy impact-analysis worker.
            Use only governed read-only evidence in the task instructions and approved tools.
            Analyze proposed policy, threshold, approval-rule, capability-boundary, provider/model, and ToolPermissionBoundary changes for advisory impact findings.
            Never approve, reject, activate, roll back, mutate policy state, mutate users or roles, expand authority, or update provider/runtime configuration.
            If required evidence, provider configuration, tool grants, governed runtime context, or redaction safety is missing, fail closed with an actionable reason instead of returning fabricated success.
            """)
        .capability(TaskAcceptance.of(GovernancePolicyImpactTasks.IMPACT_ANALYSIS).maxIterationsPerTask(3));
  }
}

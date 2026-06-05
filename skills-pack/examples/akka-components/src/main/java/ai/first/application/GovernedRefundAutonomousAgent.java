package ai.first.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/**
 * Autonomous Agent reference driver for side-effecting Akka component tools.
 *
 * <p>The agent is durable and task-oriented, but the authority boundary is still enforced by the
 * backend component tool. Prompt instructions explain the policy to the model; the
 * ToolPermissionBoundary check in {@link RefundApprovalWorkflow#requestFromGovernedTool} is the
 * control that decides whether the consequential refund may execute or must become an approval
 * proposal.
 */
@Component(
    id = "governed-refund-autonomous-agent",
    description =
        "Requests consequential refunds through a side-effecting component tool with governed boundaries.")
public class GovernedRefundAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            """
            Work only on the assigned governed refund task.
            Use RefundApprovalWorkflow_requestFromGovernedTool when a refund request is needed.
            The component tool uniqueId is the refund workflow id from the task instructions.
            Treat approval_required, denied, and executed tool results as authoritative.
            Never claim a refund was executed when the tool returns approval_required or denied.
            If ToolPermissionBoundary configuration is missing, fail closed with an actionable reason.
            Complete GovernedRefundRequest with tenantId, customerId, status, proposalId, and executed.
            """
                .stripIndent())
        .tools(RefundApprovalWorkflow.class)
        .capability(TaskAcceptance.of(GovernedRefundTasks.REQUEST_REFUND).maxIterationsPerTask(5));
  }
}

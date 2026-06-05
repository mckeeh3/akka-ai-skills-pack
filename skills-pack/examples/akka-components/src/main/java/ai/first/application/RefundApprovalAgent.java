package ai.first.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.annotations.AgentRole;
import akka.javasdk.annotations.Component;

/** Agent example that exposes a consequential capability as proposal-only tooling. */
@Component(
    id = "refund-approval-agent",
    name = "Refund Approval Agent",
    description = "Drafts refund.issue approval requests without autonomously committing refund side effects.")
@AgentRole("worker")
public class RefundApprovalAgent extends Agent {

  private static final String SYSTEM_MESSAGE =
      """
      You help draft refund.issue proposals.
      Use RefundProposalTools_proposeRefund when refund action is requested.
      The tool returns a proposal or approval request only; it never applies a refund.
      A refund side effect requires RefundApprovalWorkflow approval, unless the tool result says
      autonomousPolicyGrant is true for an explicitly bounded low-value policy.
      State the approval requirement and workflow component in the final answer.
      """
          .stripIndent();

  private final RefundProposalTools proposalTools = new RefundProposalTools();

  public Effect<String> reviewRefundRequest(String message) {
    return effects()
        .systemMessage(SYSTEM_MESSAGE)
        .tools(proposalTools)
        .userMessage(message)
        .thenReply();
  }
}

package ai.first.application;

import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;

/**
 * Agent tool facade for the consequential refund.issue capability.
 *
 * <p>The tool only drafts a proposal/approval request. It never applies a refund; commitment is handled by
 * {@link RefundApprovalWorkflow} after approval or an explicit autonomous policy grant.
 */
public class RefundProposalTools {

  private static final long AUTONOMOUS_LIMIT_CENTS = 2_500;

  @FunctionTool(
      description =
          "Draft a refund.issue proposal. This consequential capability does not apply the refund; "
              + "human approval is required unless policyGrantAutonomy is true and the amount is within policy.")
  public RefundProposal proposeRefund(
      @Description("Tenant/customer scope for the refund request.") String customerId,
      @Description("Order receiving the proposed refund.") String orderId,
      @Description("Refund amount in cents.") long amountCents,
      @Description("Reason shown to the approver and audit trace.") String reason,
      @Description("Human or agent actor requesting the proposal.") String requestedBy,
      @Description("True only when an accepted policy grants bounded autonomous refunds.") boolean policyGrantAutonomy) {
    var autonomousAllowed = policyGrantAutonomy && amountCents > 0 && amountCents <= AUTONOMOUS_LIMIT_CENTS;
    return new RefundProposal(
        "refund.issue",
        customerId,
        orderId,
        amountCents,
        requestedBy,
        reason,
        !autonomousAllowed,
        autonomousAllowed,
        false,
        autonomousAllowed
            ? "Policy permits autonomous refund within low-value limit."
            : "Approval required before committing refund side effect.",
        "refund-approval-workflow");
  }

  public record RefundProposal(
      String capabilityId,
      String customerId,
      String orderId,
      long amountCents,
      String requestedBy,
      String reason,
      boolean approvalRequired,
      boolean autonomousPolicyGrant,
      boolean sideEffectCommitted,
      String authorityBasis,
      String approvalWorkflowComponent) {}
}

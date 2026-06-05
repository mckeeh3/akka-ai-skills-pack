package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;
import akka.javasdk.annotations.StepName;
import akka.javasdk.workflow.Workflow;
import com.example.domain.RefundApprovalState;

/**
 * Minimal workflow-backed approval gate for the consequential refund.issue capability.
 *
 * <p>Requesting the capability creates a proposal. The refund side effect is committed only by an
 * explicit approval step or by a bounded autonomous policy grant carried in the request.
 */
@Component(id = "refund-approval-workflow")
public class RefundApprovalWorkflow extends Workflow<RefundApprovalState> {

  public record RequestRefund(
      String idempotencyKey,
      String customerId,
      String orderId,
      long amountCents,
      String requestedBy,
      String reason,
      boolean policyGrantAutonomy) {}

  public record ApproveRefund(String idempotencyKey, String approvedBy, String rationale) {}

  public record RejectRefund(String idempotencyKey, String rejectedBy, String rationale) {}

  private record AuthorityDecision(String idempotencyKey, String actor, String rationale) {}

  private final RefundProposalTools proposalTools = new RefundProposalTools();

  /**
   * Side-effecting component-tool reference surface for the {@code refund.request_consequential}
   * capability.
   *
   * <p>The generated component tool includes {@code uniqueId}; that id is the refund workflow id and
   * selects backend-owned refund context. Model-supplied tenant, customer, order, and amount values
   * are checked against that context and cannot grant authority. The method returns an
   * approval-required result when the active ToolPermissionBoundary grants the request-approval
   * operation; this reference method does not directly execute refunds.
   */
  @FunctionTool(
      description =
          "Capability refund.request_consequential: request a consequential refund through a "
              + "backend-governed component tool. The uniqueId is the refund workflow id. The "
              + "tool enforces ToolPermissionBoundary, tenant/customer scope, idempotency, approval "
              + "policy, and trace emission before any side effect.")
  public Effect<GovernedRefundToolBoundaryService.GovernedRefundToolResult> requestFromGovernedTool(
      @Description("Tenant/customer/order scoped refund request; checked against backend context")
          GovernedRefundToolBoundaryService.RefundToolRequest request) {
    return effects()
        .reply(
            GovernedRefundToolBoundaryService.INSTANCE.requestRefundViaComponentTool(
                commandContext().workflowId(), request));
  }

  public Effect<RefundApprovalState> request(RequestRefund request) {
    var validation = validateRequest(request);
    if (!validation.isBlank()) {
      return effects().error(validation);
    }
    if (currentState() != null) {
      if (currentState().processed(request.idempotencyKey())) {
        return effects().reply(currentState());
      }
      return effects().error("refund approval already started");
    }

    var proposal =
        proposalTools.proposeRefund(
            request.customerId(),
            request.orderId(),
            request.amountCents(),
            request.reason(),
            request.requestedBy(),
            request.policyGrantAutonomy());
    var initial =
        RefundApprovalState.waiting(
            request.customerId(),
            request.orderId(),
            request.amountCents(),
            request.requestedBy(),
            request.idempotencyKey());

    if (proposal.autonomousPolicyGrant()) {
      return effects()
          .updateState(initial)
          .transitionTo(RefundApprovalWorkflow::applyAutonomousRefundStep)
          .withInput(new AuthorityDecision(request.idempotencyKey(), "refund-policy", proposal.authorityBasis()))
          .thenReply(initial);
    }

    return effects()
        .updateState(initial)
        .transitionTo(RefundApprovalWorkflow::waitForApprovalStep)
        .thenReply(initial);
  }

  public Effect<RefundApprovalState> approve(ApproveRefund approval) {
    var validation = validateDecision(approval.idempotencyKey(), approval.approvedBy(), approval.rationale());
    if (!validation.isBlank()) {
      return effects().error(validation);
    }
    if (currentState().processed(approval.idempotencyKey())) {
      return effects().reply(currentState());
    }
    if (currentState().status() != RefundApprovalState.Status.WAITING_FOR_APPROVAL) {
      return effects().error("refund is not waiting for approval");
    }
    return effects()
        .transitionTo(RefundApprovalWorkflow::applyApprovedRefundStep)
        .withInput(new AuthorityDecision(approval.idempotencyKey(), approval.approvedBy(), approval.rationale()))
        .thenReply(currentState());
  }

  public Effect<RefundApprovalState> reject(RejectRefund rejection) {
    var validation = validateDecision(rejection.idempotencyKey(), rejection.rejectedBy(), rejection.rationale());
    if (!validation.isBlank()) {
      return effects().error(validation);
    }
    if (currentState().processed(rejection.idempotencyKey())) {
      return effects().reply(currentState());
    }
    if (currentState().status() != RefundApprovalState.Status.WAITING_FOR_APPROVAL) {
      return effects().error("refund is not waiting for approval");
    }
    return effects()
        .transitionTo(RefundApprovalWorkflow::rejectRefundStep)
        .withInput(new AuthorityDecision(rejection.idempotencyKey(), rejection.rejectedBy(), rejection.rationale()))
        .thenReply(currentState());
  }

  public ReadOnlyEffect<RefundApprovalState> get() {
    if (currentState() == null) {
      return effects().error("refund approval not started");
    }
    return effects().reply(currentState());
  }

  @StepName("wait-for-approval")
  private StepEffect waitForApprovalStep() {
    return stepEffects().thenPause();
  }

  @StepName("reject-refund")
  private StepEffect rejectRefundStep(AuthorityDecision rejection) {
    return stepEffects()
        .updateState(currentState().rejected(rejection.idempotencyKey(), rejection.actor(), rejection.rationale()))
        .thenEnd();
  }

  @StepName("apply-approved-refund")
  private StepEffect applyApprovedRefundStep(AuthorityDecision approval) {
    return stepEffects()
        .updateState(currentState().applied(approval.idempotencyKey(), approval.actor(), approval.rationale()))
        .thenEnd();
  }

  @StepName("apply-autonomous-refund")
  private StepEffect applyAutonomousRefundStep(AuthorityDecision authority) {
    return stepEffects()
        .updateState(currentState().applied(authority.idempotencyKey(), authority.actor(), authority.rationale()))
        .thenEnd();
  }

  private String validateRequest(RequestRefund request) {
    if (request == null) {
      return "request is required";
    }
    if (blank(request.idempotencyKey())) {
      return "idempotencyKey is required";
    }
    if (blank(request.customerId())) {
      return "customerId is required";
    }
    if (blank(request.orderId())) {
      return "orderId is required";
    }
    if (request.amountCents() <= 0) {
      return "amountCents must be positive";
    }
    if (blank(request.requestedBy())) {
      return "requestedBy is required";
    }
    return "";
  }

  private String validateDecision(String idempotencyKey, String actor, String rationale) {
    if (currentState() == null) {
      return "refund approval not started";
    }
    if (blank(idempotencyKey)) {
      return "idempotencyKey is required";
    }
    if (blank(actor)) {
      return "actor is required";
    }
    if (blank(rationale)) {
      return "rationale is required";
    }
    return "";
  }

  private boolean blank(String value) {
    return value == null || value.isBlank();
  }
}

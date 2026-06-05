package ai.first.application;

import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;

/** Local function-tool facade that enforces ToolPermissionBoundary checks before protected work. */
public class GovernedRiskReviewTools {

  private final GovernedToolBoundaryService boundaryService;

  public GovernedRiskReviewTools() {
    this(GovernedToolBoundaryService.INSTANCE);
  }

  GovernedRiskReviewTools(GovernedToolBoundaryService boundaryService) {
    this.boundaryService = boundaryService;
  }

  @FunctionTool(
      description =
          "Read redacted customer-risk evidence only when the active ToolPermissionBoundary grants "
              + "risk_review.read_customer_evidence for the task tenant/customer. Returns safe denied "
              + "results and emits traces for denied or allowed calls.")
  public GovernedToolBoundaryService.GovernedToolInvocationResult readCustomerEvidence(
      @Description("Governed review id/correlation id from the task context") String reviewId,
      @Description("Tenant requested by the model; checked against backend task context")
          String tenantId,
      @Description("Customer requested by the model; checked against backend task context")
          String customerId,
      @Description("Evidence id to read") String evidenceId,
      @Description("Trace correlation id") String correlationId) {
    return boundaryService.readCustomerEvidence(
        reviewId, tenantId, customerId, evidenceId, correlationId);
  }

  @FunctionTool(
      description =
          "Request approval for a customer-risk follow-up action. This tool never executes the "
              + "side effect directly; a granted call returns approval_required and creates a trace/proposal.")
  public GovernedToolBoundaryService.GovernedToolInvocationResult proposeCustomerFollowup(
      @Description("Governed review id/correlation id from the task context") String reviewId,
      @Description("Tenant requested by the model; checked against backend task context")
          String tenantId,
      @Description("Customer requested by the model; checked against backend task context")
          String customerId,
      @Description("Proposed follow-up action summary") String action,
      @Description("Trace correlation id") String correlationId) {
    return boundaryService.proposeCustomerFollowup(
        reviewId, tenantId, customerId, action, correlationId);
  }
}

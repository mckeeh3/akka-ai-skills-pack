package ai.first.application;

import akka.javasdk.agent.task.Task;
import akka.javasdk.annotations.Description;

/** Task definitions for {@link GovernedRefundAutonomousAgent}. */
public final class GovernedRefundTasks {

  private GovernedRefundTasks() {}

  public static final Task<GovernedRefundResult> REQUEST_REFUND =
      Task.name("GovernedRefundRequest")
          .description(
              "Request a consequential refund through a side-effecting component tool guarded by ToolPermissionBoundary")
          .resultConformsTo(GovernedRefundResult.class);

  public record GovernedRefundResult(
      @Description("Tenant that owns the refund context") String tenantId,
      @Description("Customer selected for the refund") String customerId,
      @Description("Final governed status such as approval_required, denied, or executed") String status,
      @Description("Approval proposal id when approval is required") String proposalId,
      @Description("Whether the refund side effect executed") boolean executed) {}
}

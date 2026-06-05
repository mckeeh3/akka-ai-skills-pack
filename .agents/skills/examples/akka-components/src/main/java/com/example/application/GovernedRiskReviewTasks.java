package com.example.application;

import akka.javasdk.annotations.Description;
import akka.javasdk.agent.task.Task;
import java.util.List;

/** Task definitions and typed records for {@link GovernedRiskReviewAutonomousAgent}. */
public final class GovernedRiskReviewTasks {

  private GovernedRiskReviewTasks() {}

  public static final String START_CAPABILITY = "risk_review.start_autonomous_task";
  public static final String QUERY_CAPABILITY = "risk_review.query_task";
  public static final String READ_EVIDENCE_CAPABILITY = "risk_review.read_customer_evidence";
  public static final String PROPOSE_FOLLOWUP_CAPABILITY = "risk_review.propose_customer_followup";

  public static final Task<GovernedRiskReviewResult> REVIEW =
      Task.name("GovernedRiskReview")
          .description(
              "Investigate customer risk with backend-governed tool permissions, tenant scope, approval gates, and traces")
          .resultConformsTo(GovernedRiskReviewResult.class);

  public record GovernedRiskReviewRequest(
      @Description("Tenant that owns the review") String tenantId,
      @Description("Customer selected for the review") String customerId,
      @Description("Stable review id used as the task correlation id") String reviewId,
      @Description("Question the autonomous agent should investigate") String question) {}

  public record GovernedRiskReviewResult(
      @Description("Tenant that owns the review") String tenantId,
      @Description("Customer selected for the review") String customerId,
      @Description("Agent recommendation or denial-safe conclusion") String recommendation,
      @Description("Evidence ids used in the recommendation") List<String> evidenceIds,
      @Description("Approval proposal id when a follow-up action needs approval") String proposedActionId) {
    public GovernedRiskReviewResult {
      evidenceIds = List.copyOf(evidenceIds == null ? List.of() : evidenceIds);
    }
  }
}

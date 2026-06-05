package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import java.util.List;

/**
 * Workflow-backed view that indexes review workflow state by status.
 */
@Component(id = "review-requests-by-status")
public class ReviewRequestsByStatusView extends View {

  public record FindByStatus(String status) {}

  public record ReviewRequestSummary(String workflowId, String requestId, String status) {}

  public record ReviewRequestSummaries(List<ReviewRequestSummary> entries) {}

  @Consume.FromWorkflow(ReviewWorkflow.class)
  public static class ReviewRequestsUpdater extends TableUpdater<ReviewRequestSummary> {

    public Effect<ReviewRequestSummary> onUpdate(ReviewWorkflow.State state) {
      var workflowId = updateContext().eventSubject().orElse("");
      return effects().updateRow(new ReviewRequestSummary(workflowId, state.requestId(), state.status()));
    }
  }

  @Query(
      """
      SELECT * AS entries
      FROM review_requests_by_status
      WHERE status = :status
      ORDER BY workflowId
      """)
  public QueryEffect<ReviewRequestSummaries> getByStatus(FindByStatus request) {
    return queryResult();
  }
}

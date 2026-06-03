package com.example.application;

import akka.javasdk.agent.task.Task;
import akka.javasdk.annotations.Description;
import java.util.List;

/** Task definitions and typed result records for the dependency and approval example. */
public final class ApprovalPipelineTasks {

  private ApprovalPipelineTasks() {}

  public static final Task<InvestigationResult> INVESTIGATE =
      Task.name("ApprovalInvestigation")
          .description("Investigate one request and recommend whether it should proceed")
          .resultConformsTo(InvestigationResult.class);

  public static final Task<ApprovalDecision> APPROVAL =
      Task.name("ApprovalGate")
          .description("External approval task completed or failed by a human or external process")
          .resultConformsTo(ApprovalDecision.class);

  public static final Task<PublishedDecision> PUBLISH =
      Task.name("ApprovalPublish")
          .description("Finalize the approved request after the approval dependency completes")
          .resultConformsTo(PublishedDecision.class);

  public record InvestigationResult(
      @Description("Recommendation from the autonomous investigation") String recommendation,
      @Description("Evidence references supporting the recommendation") List<String> evidence) {}

  public record ApprovalDecision(
      @Description("Human or external reviewer identifier") String reviewer,
      @Description("Approval decision, for example approved") String decision,
      @Description("Reviewer comment or rationale") String comment) {}

  public record PublishedDecision(
      @Description("Final title or label") String title,
      @Description("Final summary after approval") String summary,
      @Description("Whether the approved item was finalized") boolean published) {}
}

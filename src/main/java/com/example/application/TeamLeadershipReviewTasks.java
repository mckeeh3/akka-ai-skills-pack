package com.example.application;

import akka.javasdk.agent.task.Task;
import akka.javasdk.agent.task.TaskTemplate;
import akka.javasdk.annotations.Description;
import java.util.List;

/** Task definitions and typed result records for the Autonomous Agent TeamLeadership example. */
public final class TeamLeadershipReviewTasks {

  private TeamLeadershipReviewTasks() {}

  public static final Task<TeamReviewSummary> TEAM_REVIEW =
      Task.name("TeamLeadershipReview")
          .description("Lead a shared-backlog team review and return the synthesized result")
          .resultConformsTo(TeamReviewSummary.class);

  public static final TaskTemplate<TeamReviewFinding> REVIEW_WORK_ITEM =
      TaskTemplate.define("TeamLeadershipReviewWorkItem")
          .description("Complete one focused shared-backlog review work item")
          .resultConformsTo(TeamReviewFinding.class)
          .instructionTemplate("Review {subject} from the {focus} perspective. Required evidence: {evidence}.");

  public record TeamReviewSummary(
      @Description("Subject reviewed by the team") String subject,
      @Description("Lead synthesis across completed backlog work") String synthesis,
      @Description("Findings completed by team members") List<String> findings,
      @Description("Whether the team review is ready for the next stage") boolean readyForNextStage) {}

  public record TeamReviewFinding(
      @Description("Review perspective or focus area") String focus,
      @Description("Concise member finding") String finding,
      @Description("Evidence or coordination notes used by the member") List<String> evidence) {}
}

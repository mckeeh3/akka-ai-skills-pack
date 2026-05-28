package com.example.application;

import akka.javasdk.agent.task.Task;
import akka.javasdk.annotations.Description;
import java.util.List;

/** Task definitions and typed result records for the Autonomous Agent moderation example. */
public final class ReviewModerationTasks {

  private ReviewModerationTasks() {}

  public static final Task<ModeratedReview> REVIEW =
      Task.name("ModeratedReview")
          .description("Moderate a short multi-perspective review and return the final decision")
          .resultConformsTo(ModeratedReview.class);

  public record ModeratedReview(
      @Description("Document or proposal reviewed by the panel") String subject,
      @Description("Final moderator assessment") String assessment,
      @Description("Findings contributed by participant reviewers") List<String> reviewerFindings,
      @Description("Whether the reviewed item is approved for the next stage") boolean approved) {}
}

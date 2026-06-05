package ai.first.application;

import akka.javasdk.agent.task.Task;
import akka.javasdk.annotations.Description;
import java.util.List;

/** Task definitions and typed result records for {@link EvidenceReviewAutonomousAgent}. */
public final class EvidenceReviewTasks {

  private EvidenceReviewTasks() {}

  public static final Task<EvidenceReview> REVIEW =
      Task.name("EvidenceReview")
          .description("Review one issue and return a recommendation backed by cited evidence")
          .resultConformsTo(EvidenceReview.class)
          .rules(EvidenceReviewRule.class);

  public record EvidenceReview(
      @Description("Short recommendation produced by the review") String recommendation,
      @Description("Evidence references that support the recommendation")
          List<String> evidenceSources) {}
}

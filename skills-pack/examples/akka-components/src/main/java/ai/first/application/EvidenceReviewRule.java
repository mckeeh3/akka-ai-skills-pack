package ai.first.application;

import akka.javasdk.agent.task.TaskRule;

/** Rejects evidence review results that do not cite at least one evidence source. */
public class EvidenceReviewRule implements TaskRule<EvidenceReviewTasks.EvidenceReview> {

  @Override
  public Result onComplete(EvidenceReviewTasks.EvidenceReview review) {
    if (review.evidenceSources() == null || review.evidenceSources().isEmpty()) {
      return new Result.Rejected("evidenceSources must include at least one source");
    }
    return new Result.Accepted();
  }
}

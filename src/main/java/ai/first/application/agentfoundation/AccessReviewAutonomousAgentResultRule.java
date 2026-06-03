package ai.first.application.agentfoundation;

import akka.javasdk.agent.task.TaskRule;
import java.util.Objects;

/** Rejects unsafe or scope-mismatched access-review results before a task can complete. */
public final class AccessReviewAutonomousAgentResultRule implements TaskRule<AccessReviewAutonomousAgentResult> {
  @Override
  public Result onComplete(AccessReviewAutonomousAgentResult result) {
    if (result == null) return new Result.Rejected("access-review result is required");
    if (blank(result.taskId())) return new Result.Rejected("taskId is required");
    if (blank(result.tenantId())) return new Result.Rejected("tenantId is required");
    if (blank(result.summary())) return new Result.Rejected("browser-safe summary is required");
    var lowerSummary = result.summary().toLowerCase(java.util.Locale.ROOT);
    if (lowerSummary.contains("api_key") || lowerSummary.contains("secret=") || lowerSummary.contains("token=")) {
      return new Result.Rejected("summary must not expose provider secrets, tokens, or raw credentials");
    }
    if (result.safety() == null || !result.safety().toLowerCase(java.util.Locale.ROOT).contains("human")) {
      return new Result.Rejected("safety must state that human review is required before access changes");
    }
    if (result.recommendations().stream().anyMatch(rec -> Objects.toString(rec.requiredHumanAction(), "").isBlank())) {
      return new Result.Rejected("each recommendation must name the required human action");
    }
    return new Result.Accepted();
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }
}

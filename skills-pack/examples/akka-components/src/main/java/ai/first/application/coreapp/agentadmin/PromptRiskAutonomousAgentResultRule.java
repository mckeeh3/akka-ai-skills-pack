package ai.first.application.coreapp.agentadmin;

import akka.javasdk.agent.task.TaskRule;
import java.util.Locale;

/** Rejects unsafe or incomplete prompt-risk results before task completion. */
public final class PromptRiskAutonomousAgentResultRule implements TaskRule<PromptRiskAutonomousAgentResult> {
  @Override
  public Result onComplete(PromptRiskAutonomousAgentResult result) {
    if (result == null) return new Result.Rejected("prompt-risk result is required");
    if (blank(result.taskId())) return new Result.Rejected("taskId is required");
    if (blank(result.tenantId())) return new Result.Rejected("tenantId is required");
    if (blank(result.targetAgentDefinitionId())) return new Result.Rejected("targetAgentDefinitionId is required");
    if (blank(result.proposalId())) return new Result.Rejected("proposalId is required");
    if (blank(result.summary())) return new Result.Rejected("browser-safe summary is required");
    if (result.overallRisk() == null) return new Result.Rejected("overallRisk is required");
    if (result.findings().isEmpty()) return new Result.Rejected("at least one risk finding is required");
    if (containsSecret(result.toString())) return new Result.Rejected("result must not expose provider secrets, tokens, raw credentials, or hidden payloads");
    if (result.safety() == null || !result.safety().toLowerCase(Locale.ROOT).contains("human") || !result.safety().toLowerCase(Locale.ROOT).contains("advisory")) {
      return new Result.Rejected("safety must state advisory-only output and required human Agent Admin review");
    }
    if (result.recommendations().stream().anyMatch(recommendation -> !recommendation.blocksActivation())) {
      return new Result.Rejected("recommendations must keep activation blocked until a separate human decision");
    }
    return new Result.Accepted();
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  private static boolean containsSecret(String value) {
    var lower = value == null ? "" : value.toLowerCase(Locale.ROOT);
    return lower.contains("api_key") || lower.contains("secret=") || lower.contains("token=") || lower.contains("jwt=") || lower.contains("providercredential");
  }
}

package ai.first.application.coreapp.governance;

import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import akka.javasdk.agent.task.TaskRule;
import java.util.Locale;

/** Rejects unsafe or incomplete Governance/Policy impact results before task completion. */
public final class GovernancePolicyImpactAutonomousAgentResultRule implements TaskRule<GovernancePolicyImpactAutonomousAgentResult> {
  @Override
  public Result onComplete(GovernancePolicyImpactAutonomousAgentResult result) {
    if (result == null) return new Result.Rejected("governance policy impact result is required");
    if (blank(result.resultId())) return new Result.Rejected("resultId is required");
    if (blank(result.impactTaskId())) return new Result.Rejected("impactTaskId is required");
    if (blank(result.proposalId())) return new Result.Rejected("proposalId is required");
    if (blank(result.tenantId())) return new Result.Rejected("tenantId is required");
    if (blank(result.summary())) return new Result.Rejected("browser-safe summary is required");
    if (result.overallRisk() == null || result.reviewState() == null) return new Result.Rejected("risk and reviewState are required");
    if (!result.noDirectMutation() || !result.activationBlockedUntilHumanDecision()) return new Result.Rejected("impact result must preserve noDirectMutation and human activation gate");
    if (containsSecret(result.toString())) return new Result.Rejected("result must not expose raw prompts, JWTs, provider credentials, tokens, secrets, or raw tool payloads");
    if (claimsMutation(result.toString())) return new Result.Rejected("impact result must not claim approval, rejection, activation, rollback, role mutation, provider mutation, or ToolPermissionBoundary expansion");
    if (result.reviewState() == GovernancePolicyImpactAutonomousAgentResult.ReviewState.IMPACT_READY || result.reviewState() == GovernancePolicyImpactAutonomousAgentResult.ReviewState.NEEDS_HUMAN_REVIEW) {
      if (result.impactFindings().isEmpty()) return new Result.Rejected("impact-ready results require findings");
      if (result.traceRefs().isEmpty() || result.sourceRefs().isEmpty()) return new Result.Rejected("impact-ready results require traceRefs and sourceRefs");
      if (result.impactFindings().stream().anyMatch(finding -> finding.evidenceRefs().isEmpty())) return new Result.Rejected("each impact finding requires evidence refs");
    }
    if (result.reviewState() == GovernancePolicyImpactAutonomousAgentResult.ReviewState.BLOCKED_PROVIDER_OR_RUNTIME && !result.impactFindings().isEmpty()) {
      return new Result.Rejected("blocked provider/runtime state must not include successful impact findings");
    }
    return new Result.Accepted();
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  private static boolean containsSecret(String value) {
    var lower = value == null ? "" : value.toLowerCase(Locale.ROOT);
    return lower.contains("api_key") || lower.contains("secret=") || lower.contains("token=") || lower.contains("jwt=") || lower.contains("providercredential") || lower.contains("rawtoolpayload") || lower.contains("hidden prompt");
  }

  private static boolean claimsMutation(String value) {
    var lower = value == null ? "" : value.toLowerCase(Locale.ROOT);
    return lower.contains("activated policy") || lower.contains("approved proposal") || lower.contains("rejected proposal") || lower.contains("rolled back") || lower.contains("expanded toolpermissionboundary") || lower.contains("provider config updated") || lower.contains("role mutated");
  }
}

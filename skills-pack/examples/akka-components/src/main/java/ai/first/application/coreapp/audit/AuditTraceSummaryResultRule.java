package ai.first.application.coreapp.audit;

import akka.javasdk.agent.task.TaskRule;
import java.util.Locale;

/** Rejects unsafe or incomplete Audit/Trace summary results before task completion. */
public final class AuditTraceSummaryResultRule implements TaskRule<AuditTraceSummaryResult> {
  @Override
  public Result onComplete(AuditTraceSummaryResult result) {
    if (result == null) return new Result.Rejected("audit trace summary result is required");
    if (blank(result.summaryTaskId())) return new Result.Rejected("summaryTaskId is required");
    if (blank(result.tenantId())) return new Result.Rejected("tenantId is required");
    if (blank(result.correlationId())) return new Result.Rejected("correlationId is required");
    if (blank(result.windowStart()) || blank(result.windowEnd())) return new Result.Rejected("summary window is required");
    if (result.overallRisk() == null) return new Result.Rejected("overallRisk is required");
    if (blank(result.executiveSummary())) return new Result.Rejected("browser-safe executiveSummary is required");
    if (result.findings().isEmpty()) return new Result.Rejected("at least one evidence-backed finding is required");
    if (result.findings().stream().anyMatch(finding -> finding.evidenceRefs().isEmpty())) return new Result.Rejected("each finding must cite redacted evidence refs");
    if (!result.noDirectMutation()) return new Result.Rejected("noDirectMutation must remain true");
    if (containsSecret(result.toString())) return new Result.Rejected("result must not expose provider secrets, raw prompts, JWTs, tokens, credentials, or hidden tool payloads");
    if (claimsMutation(result.toString())) return new Result.Rejected("Audit/Trace summary result must not claim protected mutations");
    return new Result.Accepted();
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  private static boolean containsSecret(String value) {
    var lower = value == null ? "" : value.toLowerCase(Locale.ROOT);
    return lower.contains("api_key") || lower.contains("secret=") || lower.contains("token=") || lower.contains("jwt=") || lower.contains("providercredential");
  }

  private static boolean claimsMutation(String value) {
    var lower = value == null ? "" : value.toLowerCase(Locale.ROOT);
    return lower.contains("mutated roles") || lower.contains("changed policy") || lower.contains("updated provider config") || lower.contains("deleted trace");
  }
}

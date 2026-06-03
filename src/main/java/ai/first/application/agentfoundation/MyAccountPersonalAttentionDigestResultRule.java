package ai.first.application.agentfoundation;

import akka.javasdk.agent.task.TaskRule;
import java.util.Locale;

/** Rejects unsafe or incomplete personal attention digest results before task completion. */
public final class MyAccountPersonalAttentionDigestResultRule implements TaskRule<MyAccountPersonalAttentionDigestResult> {
  @Override
  public Result onComplete(MyAccountPersonalAttentionDigestResult result) {
    if (result == null) return new Result.Rejected("personal attention digest result is required");
    if (blank(result.digestTaskId())) return new Result.Rejected("digestTaskId is required");
    if (blank(result.tenantId())) return new Result.Rejected("tenantId is required");
    if (blank(result.accountId())) return new Result.Rejected("accountId is required");
    if (blank(result.selectedContextId())) return new Result.Rejected("selectedContextId is required");
    if (blank(result.summary())) return new Result.Rejected("browser-safe digest summary is required");
    if (result.authorizedAttentionCount() < 0) return new Result.Rejected("authorizedAttentionCount cannot be negative");
    if (result.highestUrgency() == null) return new Result.Rejected("highestUrgency is required");
    if (result.sections().stream().anyMatch(section -> section.authorizedItemCount() < 0 || section.evidenceRefs().isEmpty())) return new Result.Rejected("each non-empty section must cite authorized evidence refs");
    if (result.authorizedAttentionCount() > 0 && result.evidenceRefs().isEmpty()) return new Result.Rejected("non-empty digest must cite authorized evidence refs");
    if (blank(result.safety()) || !result.safety().toLowerCase(Locale.ROOT).contains("advisory")) return new Result.Rejected("safety must state advisory-only authority");
    var rendered = result.toString();
    if (containsSecret(rendered)) return new Result.Rejected("result must not expose provider secrets, raw prompts, JWTs, tokens, credentials, or hidden tool payloads");
    if (leaksHiddenAttention(rendered)) return new Result.Rejected("result must not leak hidden workstream or unauthorized attention existence");
    if (claimsSourceAttentionMutation(rendered)) return new Result.Rejected("digest result must not claim source attention lifecycle mutation");
    return new Result.Accepted();
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  private static boolean containsSecret(String value) {
    var lower = value == null ? "" : value.toLowerCase(Locale.ROOT);
    return lower.contains("api_key") || lower.contains("secret=") || lower.contains("token=") || lower.contains("jwt=") || lower.contains("providercredential") || lower.contains("rawprompt");
  }

  private static boolean leaksHiddenAttention(String value) {
    var lower = value == null ? "" : value.toLowerCase(Locale.ROOT);
    return lower.contains("hidden workstream") || lower.contains("unauthorized item") || lower.contains("redacted count") || lower.contains("omitted workstream");
  }

  private static boolean claimsSourceAttentionMutation(String value) {
    var lower = value == null ? "" : value.toLowerCase(Locale.ROOT);
    return lower.contains("acknowledged source attention") || lower.contains("resolved source attention") || lower.contains("dismissed source attention") || lower.contains("mutated source attention");
  }
}

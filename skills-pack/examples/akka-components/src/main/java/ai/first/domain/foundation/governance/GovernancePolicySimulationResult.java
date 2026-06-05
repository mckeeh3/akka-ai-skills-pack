package ai.first.domain.foundation.governance;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/** Browser-safe, tenant-scoped policy simulation evidence; advisory only and never an authority grant. */
public record GovernancePolicySimulationResult(
    String simulationId,
    String tenantId,
    String customerId,
    String proposalId,
    String requestedByAccountId,
    Status status,
    String scenarioInputSummary,
    List<String> expectedAllows,
    List<String> expectedDenials,
    List<String> warnings,
    List<String> riskFindings,
    List<String> evidenceRefs,
    List<String> requiredApprovalCapabilityIds,
    String idempotencyKey,
    String correlationId,
    Instant createdAt) {
  public enum Status {
    COMPLETED_REVIEW_REQUIRED,
    BLOCKED_MISSING_PROPOSAL,
    BLOCKED_INVALID_SCOPE
  }

  public GovernancePolicySimulationResult {
    Objects.requireNonNull(simulationId);
    Objects.requireNonNull(tenantId);
    Objects.requireNonNull(proposalId);
    Objects.requireNonNull(requestedByAccountId);
    Objects.requireNonNull(status);
    scenarioInputSummary = safe(scenarioInputSummary);
    expectedAllows = List.copyOf(expectedAllows == null ? List.of() : expectedAllows.stream().map(GovernancePolicySimulationResult::safe).toList());
    expectedDenials = List.copyOf(expectedDenials == null ? List.of() : expectedDenials.stream().map(GovernancePolicySimulationResult::safe).toList());
    warnings = List.copyOf(warnings == null ? List.of() : warnings.stream().map(GovernancePolicySimulationResult::safe).toList());
    riskFindings = List.copyOf(riskFindings == null ? List.of() : riskFindings.stream().map(GovernancePolicySimulationResult::safe).toList());
    evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs.stream().map(GovernancePolicySimulationResult::safe).toList());
    requiredApprovalCapabilityIds = List.copyOf(requiredApprovalCapabilityIds == null ? List.of() : requiredApprovalCapabilityIds);
    Objects.requireNonNull(correlationId);
    Objects.requireNonNull(createdAt);
  }

  private static String safe(String value) {
    if (value == null || value.isBlank()) return "unspecified";
    return value.replaceAll("(?i)(api[_-]?key|secret|token|jwt)=[^\\s,;]+", "$1=[REDACTED]");
  }
}

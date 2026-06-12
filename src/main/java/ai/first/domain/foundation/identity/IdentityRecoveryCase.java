package ai.first.domain.foundation.identity;

import java.time.Instant;
import java.util.List;

/** Durable, browser-safe identity exception/relink recovery lifecycle state. */
public record IdentityRecoveryCase(
    String recoveryId,
    String accountId,
    String membershipId,
    ScopeType scopeType,
    String tenantId,
    String customerId,
    IdentityRecoveryStatus status,
    String reason,
    String reviewReason,
    String riskSummary,
    List<String> evidenceRefs,
    String requestedByAccountId,
    String reviewedByAccountId,
    String completedByAccountId,
    String idempotencyKey,
    String approvalRef,
    String denialReason,
    String failureReason,
    List<String> traceRefs,
    Instant createdAt,
    Instant updatedAt) {

  public IdentityRecoveryCase {
    evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
  }

  public boolean terminal() {
    return status == IdentityRecoveryStatus.COMPLETED
        || status == IdentityRecoveryStatus.DENIED
        || status == IdentityRecoveryStatus.CANCELLED
        || status == IdentityRecoveryStatus.FAILED
        || status == IdentityRecoveryStatus.STALE;
  }
}

package ai.first.domain.foundation.identity;

/** Production identity exception/relink recovery states. */
public enum IdentityRecoveryStatus {
  REPORTED,
  NEEDS_REVIEW,
  APPROVED_FOR_RECOVERY,
  DENIED,
  RECOVERY_IN_PROGRESS,
  COMPLETED,
  FAILED,
  CANCELLED,
  STALE
}

package ai.first.domain.security;

/** Redacted delivery status for invitation/account emails. */
public enum EmailDeliveryStatus {
  NOT_ENQUEUED,
  QUEUED,
  CAPTURED,
  SENT,
  FAILED
}

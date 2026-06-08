package ai.first.domain.foundation.email;

/** Redacted delivery status for invitation/account emails. */
public enum EmailDeliveryStatus {
  NOT_ENQUEUED,
  QUEUED,
  CAPTURED,
  SENT,
  FAILED
}

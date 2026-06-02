package {{JAVA_BASE_PACKAGE}}.domain.security;

/** Redacted lifecycle state for governed notification email delivery. */
public enum EmailNotificationDeliveryStatus {
  NOT_ELIGIBLE,
  DEFERRED,
  QUEUED,
  CAPTURED,
  SENT,
  FAILED,
  CANCELLED
}

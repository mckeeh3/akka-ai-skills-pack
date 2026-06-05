package ai.first.domain.foundation.notification;

/** Provider-neutral notification delivery attempt lifecycle. */
public enum NotificationDeliveryAttemptStatus {
  QUEUED,
  CAPTURED_LOCAL_TEST,
  BLOCKED_PROVIDER_UNCONFIGURED,
  SENT,
  FAILED,
  NOT_ELIGIBLE
}

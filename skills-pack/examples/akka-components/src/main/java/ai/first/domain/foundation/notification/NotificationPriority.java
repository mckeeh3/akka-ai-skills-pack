package ai.first.domain.foundation.notification;

/** Notification priority mirrors source severity without granting source authority. */
public enum NotificationPriority {
  INFO,
  WARNING,
  URGENT,
  BLOCKED
}

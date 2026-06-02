package {{JAVA_BASE_PACKAGE}}.domain.security;

/** Notification priority mirrors source severity without granting source authority. */
public enum NotificationPriority {
  INFO,
  WARNING,
  URGENT,
  BLOCKED
}

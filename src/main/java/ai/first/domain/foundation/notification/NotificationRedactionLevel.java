package ai.first.domain.foundation.notification;

/** Redaction level for user-facing notification projection output. */
public enum NotificationRedactionLevel {
  FULL,
  SUMMARY_ONLY,
  NOT_FOUND_OR_REDACTED
}

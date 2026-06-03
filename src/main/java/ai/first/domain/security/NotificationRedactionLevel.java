package ai.first.domain.security;

/** Redaction level for user-facing notification projection output. */
public enum NotificationRedactionLevel {
  FULL,
  SUMMARY_ONLY,
  NOT_FOUND_OR_REDACTED
}

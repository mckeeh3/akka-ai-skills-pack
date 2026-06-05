package ai.first.domain.foundation.attention;

/** Redaction decision applied before attention data is returned to browser or agent callers. */
public enum AttentionRedactionLevel {
  FULL,
  SUMMARY_ONLY,
  NOT_FOUND_OR_REDACTED
}

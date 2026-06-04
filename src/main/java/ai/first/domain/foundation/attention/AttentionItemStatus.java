package ai.first.domain.foundation.attention;

/** Lifecycle status for shared backend-owned workstream attention items. */
public enum AttentionItemStatus {
  OPEN,
  ACKNOWLEDGED,
  RESOLVED,
  DISMISSED,
  EXPIRED
}

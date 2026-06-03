package ai.first.domain.security;

/** Lifecycle status for shared backend-owned workstream attention items. */
public enum AttentionItemStatus {
  OPEN,
  ACKNOWLEDGED,
  RESOLVED,
  DISMISSED,
  EXPIRED
}

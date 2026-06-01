package {{JAVA_BASE_PACKAGE}}.domain.security;

/** Lifecycle status for shared backend-owned workstream attention items. */
public enum AttentionItemStatus {
  OPEN,
  ACKNOWLEDGED,
  RESOLVED,
  DISMISSED,
  EXPIRED
}

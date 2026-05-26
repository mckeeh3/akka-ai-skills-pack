package {{JAVA_BASE_PACKAGE}}.domain.agentfoundation;

/** Backend-owned catalog metadata for one stable tool id that may be granted to managed agents. */
public record ToolCatalogEntry(
    String toolId,
    String displayName,
    ToolPermissionBoundary.Category category,
    String capabilityId,
    String purpose,
    SideEffectLevel sideEffectLevel,
    String implementationBindingKey) {
  public ToolCatalogEntry {
    if (toolId == null || toolId.isBlank()) throw new IllegalArgumentException("toolId is required");
    if (category == null) throw new IllegalArgumentException("category is required");
    if (capabilityId == null || capabilityId.isBlank()) throw new IllegalArgumentException("capabilityId is required");
    if (implementationBindingKey == null || implementationBindingKey.isBlank()) throw new IllegalArgumentException("implementationBindingKey is required");
  }

  public enum SideEffectLevel {
    NONE,
    INTERNAL_STATE,
    NOTIFICATION,
    EXTERNAL_CALL,
    BILLING,
    SECURITY,
    IRREVERSIBLE
  }
}

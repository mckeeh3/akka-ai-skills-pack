package {{JAVA_BASE_PACKAGE}}.domain.security;

/** Authorized workstream/surface target used by open_attention_item. */
public record AttentionSurfaceRef(
    String targetFunctionalAgentId,
    String targetSurfaceId,
    String targetSurfaceType,
    String targetItemId,
    String defaultActionId,
    String requiredCapabilityId) {}

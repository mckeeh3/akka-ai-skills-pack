package {{JAVA_BASE_PACKAGE}}.domain.security;

/** Browser-safe source/evidence pointer explaining why an attention item exists. */
public record AttentionSourceRef(
    String kind,
    String refId,
    String label,
    String capabilityId,
    String traceId,
    String correlationId) {}

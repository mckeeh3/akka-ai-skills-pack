package {{JAVA_BASE_PACKAGE}}.domain.security;

/** Browser-safe source evidence reference for a notification item. */
public record NotificationSourceRef(
    String sourceType,
    String sourceId,
    String label,
    String requiredCapabilityId,
    String traceId,
    String correlationId) {}

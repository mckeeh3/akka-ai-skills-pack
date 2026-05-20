package {{JAVA_BASE_PACKAGE}}.domain.security;

public record Customer(String tenantId, String customerId, String displayName, boolean active) {}

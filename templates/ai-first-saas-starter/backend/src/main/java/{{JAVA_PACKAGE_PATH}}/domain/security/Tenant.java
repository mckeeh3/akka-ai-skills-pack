package {{JAVA_BASE_PACKAGE}}.domain.security;

public record Tenant(String tenantId, String displayName, boolean active) {}

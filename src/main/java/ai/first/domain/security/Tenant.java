package ai.first.domain.security;

public record Tenant(String tenantId, String displayName, boolean active) {}

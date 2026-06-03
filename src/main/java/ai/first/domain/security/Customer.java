package ai.first.domain.security;

public record Customer(String tenantId, String customerId, String displayName, boolean active) {}

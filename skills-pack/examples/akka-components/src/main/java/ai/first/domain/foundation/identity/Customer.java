package ai.first.domain.foundation.identity;

public record Customer(String tenantId, String customerId, String displayName, boolean active) {}

package com.example.domain.agentfoundation;

import java.util.Set;

/** Reference-only auth context for governed runtime agent examples. */
public record ReferenceAuthContext(
    String tenantId,
    String accountId,
    Set<String> roles,
    Set<String> capabilityIds,
    String mode) {

  public ReferenceAuthContext {
    roles = Set.copyOf(roles);
    capabilityIds = Set.copyOf(capabilityIds);
  }

  public boolean hasCapability(String capabilityId) {
    return capabilityIds.contains(capabilityId);
  }

  public ReferenceAuthContext forTenant(String newTenantId) {
    return new ReferenceAuthContext(newTenantId, accountId, roles, capabilityIds, mode);
  }
}

package com.example.domain.agentfoundation;

import java.util.Set;

/** Deny-by-default tool permission boundary for governed agent reference examples. */
public record ReferenceToolPermissionBoundary(
    String tenantId,
    String toolBoundaryId,
    String boundaryVersionId,
    String agentDefinitionId,
    Set<String> allowedToolIds,
    Set<String> allowedModes,
    boolean active) {

  public ReferenceToolPermissionBoundary {
    allowedToolIds = Set.copyOf(allowedToolIds);
    allowedModes = Set.copyOf(allowedModes);
  }

  public boolean allowsTool(String toolId, String mode) {
    return active && allowedToolIds.contains(toolId) && allowedModes.contains(mode);
  }
}

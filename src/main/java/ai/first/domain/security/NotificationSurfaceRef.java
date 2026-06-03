package ai.first.domain.security;

/** Authorized target surface hint; source/open handlers must reauthorize before rendering. */
public record NotificationSurfaceRef(
    String functionalAgentId,
    String surfaceId,
    String surfaceType,
    String sourceItemId,
    String governedToolId,
    String requiredCapabilityId) {}

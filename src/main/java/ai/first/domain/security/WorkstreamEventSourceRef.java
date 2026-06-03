package ai.first.domain.security;

/** Browser-safe source/evidence pointer carried by a workstream event envelope. */
public record WorkstreamEventSourceRef(
    String refType,
    String refId,
    String label,
    String capabilityId,
    String traceId,
    String correlationId) {}

package ai.first.domain.foundation.attention;

/** Browser-safe source/evidence pointer explaining why an attention item exists. */
public record AttentionSourceRef(
    String kind,
    String refId,
    String label,
    String capabilityId,
    String traceId,
    String correlationId) {}

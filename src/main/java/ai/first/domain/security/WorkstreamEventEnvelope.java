package ai.first.domain.security;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Browser-safe governed workstream event envelope.
 *
 * <p>Events are projection evidence only. They preserve tenant/customer scope, capability provenance, source refs,
 * idempotency, trace refs, and redaction hints; they never grant mutation authority.</p>
 */
public record WorkstreamEventEnvelope(
    String eventId,
    String eventType,
    String eventFamily,
    int schemaVersion,
    Instant occurredAt,
    Instant publishedAt,
    String tenantId,
    String customerId,
    Map<String, String> authContext,
    Map<String, String> actor,
    List<WorkstreamEventSourceRef> sourceRefs,
    List<String> capabilityRefs,
    String correlationId,
    String idempotencyKey,
    String causationId,
    List<String> traceRefs,
    String owningWorkstreamId,
    String targetSurfaceId,
    String payloadClass,
    Map<String, String> payload,
    Map<String, String> redactionHints,
    Map<String, String> projectionHints) {
  public WorkstreamEventEnvelope {
    authContext = Map.copyOf(authContext == null ? Map.of() : authContext);
    actor = Map.copyOf(actor == null ? Map.of() : actor);
    sourceRefs = List.copyOf(sourceRefs == null ? List.of() : sourceRefs);
    capabilityRefs = List.copyOf(capabilityRefs == null ? List.of() : capabilityRefs);
    traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
    payload = Map.copyOf(payload == null ? Map.of() : payload);
    redactionHints = Map.copyOf(redactionHints == null ? Map.of() : redactionHints);
    projectionHints = Map.copyOf(projectionHints == null ? Map.of() : projectionHints);
    if (eventId == null || eventId.isBlank()) throw new IllegalArgumentException("eventId is required");
    if (eventType == null || eventType.isBlank()) throw new IllegalArgumentException("eventType is required");
    if (eventFamily == null || eventFamily.isBlank()) throw new IllegalArgumentException("eventFamily is required");
    if (tenantId == null || tenantId.isBlank()) throw new IllegalArgumentException("tenantId is required");
    if (correlationId == null || correlationId.isBlank()) throw new IllegalArgumentException("correlationId is required");
    if (idempotencyKey == null || idempotencyKey.isBlank()) throw new IllegalArgumentException("idempotencyKey is required");
    if (sourceRefs.isEmpty()) throw new IllegalArgumentException("sourceRefs are required");
    if (capabilityRefs.isEmpty()) throw new IllegalArgumentException("capabilityRefs are required");
    if (traceRefs.isEmpty()) throw new IllegalArgumentException("traceRefs are required");
  }
}

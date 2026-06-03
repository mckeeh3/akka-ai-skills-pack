package ai.first.domain.agentfoundation;

import java.time.Instant;

/** Browser-safe governed-agent trace fact for prompt assembly, skill loads, tool denials, and behavior edits. */
public record AgentRuntimeTrace(
    String traceId,
    Instant occurredAt,
    String tenantId,
    String agentDefinitionId,
    String correlationId,
    String workTraceId,
    String traceType,
    Decision decision,
    String actorId,
    String capabilityId,
    String targetId,
    String safeSummary,
    String checksum) {
  public enum Decision {
    ALLOWED,
    DENIED,
    APPROVAL_REQUIRED
  }
}

package ai.first.domain.foundation.agent;

import java.time.Instant;

/**
 * Audit-grade lifecycle fact for governed runtime artifacts.
 *
 * <p>Facts are browser-safe summaries: they link to immutable versions or checksums without exposing raw
 * prompt, skill, reference, invite-token, provider-secret, or credential material.
 */
public record GovernedArtifactLifecycleFact(
    String tenantId,
    ArtifactType artifactType,
    String artifactId,
    String agentDefinitionId,
    Transition transition,
    AgentLifecycleStatus fromStatus,
    AgentLifecycleStatus toStatus,
    int fromVersion,
    int toVersion,
    String snapshotRef,
    String checksum,
    String actorId,
    String correlationId,
    String reason,
    boolean authorityExpansionDenied,
    Instant occurredAt) {

  public GovernedArtifactLifecycleFact {
    snapshotRef = safe(snapshotRef);
    checksum = safe(checksum);
    reason = safe(reason);
    actorId = safe(actorId);
    correlationId = safe(correlationId);
  }

  public static GovernedArtifactLifecycleFact of(
      String tenantId,
      ArtifactType artifactType,
      String artifactId,
      String agentDefinitionId,
      Transition transition,
      AgentLifecycleStatus fromStatus,
      AgentLifecycleStatus toStatus,
      int fromVersion,
      int toVersion,
      String snapshotRef,
      String checksum,
      String actorId,
      String correlationId,
      String reason,
      boolean authorityExpansionDenied,
      Instant occurredAt) {
    return new GovernedArtifactLifecycleFact(
        tenantId,
        artifactType,
        artifactId,
        agentDefinitionId,
        transition,
        fromStatus,
        toStatus,
        fromVersion,
        toVersion,
        snapshotRef,
        checksum,
        actorId,
        correlationId,
        reason,
        authorityExpansionDenied,
        occurredAt);
  }

  private static String safe(String value) {
    if (value == null) return null;
    return value.matches("(?is).*(api[_-]?key|secret|token|password)\\s*[:=]\\s*[^\\s]+.*")
        ? "redacted-secret-like-value"
        : value;
  }

  public enum ArtifactType {
    PROMPT_DOCUMENT,
    SKILL_DOCUMENT,
    REFERENCE_DOCUMENT,
    AGENT_SKILL_MANIFEST,
    AGENT_REFERENCE_MANIFEST,
    TOOL_PERMISSION_BOUNDARY
  }

  public enum Transition {
    DRAFTED,
    SUBMITTED,
    REVIEWED,
    APPROVED,
    ACTIVATED,
    DEPRECATED,
    ROLLED_BACK,
    ARCHIVED,
    SEED_IMPORTED,
    DENIED,
    NO_OP
  }
}

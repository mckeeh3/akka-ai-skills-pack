package ai.first.domain.security;

import java.time.Instant;

/** Immutable browser-redacted audit fact for identity, authorization, and admin activity. */
public record AdminAuditEvent(
    String auditEventId,
    Instant timestamp,
    String correlationId,
    String actorAccountId,
    String actorMembershipId,
    ScopeType actorScopeType,
    String tenantId,
    String customerId,
    String targetAccountId,
    String targetMembershipId,
    String actionType,
    Result result,
    String reasonCode,
    String evidenceSummary,
    String dataClassification) {
  public enum Result {
    ALLOWED,
    DENIED,
    NO_OP,
    FAILED
  }
}

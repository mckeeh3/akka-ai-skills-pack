package ai.first.domain.security;

import java.time.Instant;
import java.util.List;

/** Audit-grade invitation state. Raw invite tokens are never stored or projected. */
public record Invitation(
    String invitationId,
    String normalizedEmail,
    ScopeType scopeType,
    String tenantId,
    String customerId,
    List<FoundationRole> requestedRoles,
    String accountId,
    String membershipId,
    InvitationStatus status,
    EmailDeliveryStatus deliveryStatus,
    int deliveryAttempts,
    List<String> providerMessageIds,
    String lastDeliveryErrorSummary,
    String acceptanceContextId,
    String tokenHash,
    Instant expiresAt,
    Instant acceptedAt,
    String acceptedByWorkosSubject,
    Instant revokedAt,
    String revokedByAccountId,
    String revokeReason,
    int resendCount,
    String createdByAccountId,
    Instant createdAt,
    String idempotencyKey,
    String correlationId) {

  public Invitation {
    requestedRoles = List.copyOf(requestedRoles == null ? List.of() : requestedRoles);
    providerMessageIds = List.copyOf(providerMessageIds == null ? List.of() : providerMessageIds);
    if (normalizedEmail == null || !normalizedEmail.contains("@")) {
      throw new IllegalArgumentException("Invitation requires a normalized email");
    }
    if (scopeType == ScopeType.TENANT && tenantId == null) {
      throw new IllegalArgumentException("Tenant invitations require tenantId");
    }
    if (scopeType == ScopeType.CUSTOMER && (tenantId == null || customerId == null)) {
      throw new IllegalArgumentException("Customer invitations require tenantId and customerId");
    }
    if (tokenHash == null || tokenHash.isBlank()) {
      throw new IllegalArgumentException("Invitation stores a token hash, not a raw token");
    }
  }

  public boolean terminal() {
    return status == InvitationStatus.ACCEPTED || status == InvitationStatus.EXPIRED || status == InvitationStatus.REVOKED;
  }

  public boolean resendable() {
    return !terminal();
  }

  public boolean expiredAt(Instant now) {
    return !terminal() && !expiresAt.isAfter(now);
  }
}

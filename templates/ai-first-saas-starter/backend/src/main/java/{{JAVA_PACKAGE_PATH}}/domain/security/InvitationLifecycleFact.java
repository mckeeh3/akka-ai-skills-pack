package {{JAVA_BASE_PACKAGE}}.domain.security;

import java.time.Instant;
import java.util.List;

/** Browser-safe, audit-grade invitation lifecycle fact. Raw invite tokens and token hashes are never stored here. */
public record InvitationLifecycleFact(
    String factId,
    String invitationId,
    String eventType,
    ScopeType scopeType,
    String tenantId,
    String customerId,
    String targetAccountId,
    String membershipId,
    String normalizedEmail,
    InvitationStatus invitationStatus,
    EmailDeliveryStatus deliveryStatus,
    int deliveryAttempts,
    int resendCount,
    List<String> safeProviderMessageIds,
    String actorAccountId,
    String result,
    String reasonCode,
    String deliveryAttemptId,
    String correlationId,
    Instant occurredAt) {

  public InvitationLifecycleFact {
    safeProviderMessageIds = List.copyOf(safeProviderMessageIds == null ? List.of() : safeProviderMessageIds);
    if (factId == null || factId.isBlank()) throw new IllegalArgumentException("factId is required");
    if (invitationId == null || invitationId.isBlank()) throw new IllegalArgumentException("invitationId is required");
    if (eventType == null || eventType.isBlank()) throw new IllegalArgumentException("eventType is required");
    if (normalizedEmail == null || !normalizedEmail.contains("@")) throw new IllegalArgumentException("normalizedEmail is required");
    if (containsUnsafeToken(factId)
        || containsUnsafeToken(reasonCode)
        || containsUnsafeToken(deliveryAttemptId)
        || containsUnsafeToken(correlationId)
        || safeProviderMessageIds.stream().anyMatch(InvitationLifecycleFact::containsUnsafeToken)) {
      throw new IllegalArgumentException("Invitation lifecycle facts must not contain raw invitation tokens");
    }
  }

  public static InvitationLifecycleFact fromInvitation(String factId, String eventType, Invitation invitation, String actorAccountId, String result, String reasonCode, String deliveryAttemptId, Instant occurredAt) {
    return new InvitationLifecycleFact(
        factId,
        invitation.invitationId(),
        eventType,
        invitation.scopeType(),
        invitation.tenantId(),
        invitation.customerId(),
        invitation.accountId(),
        invitation.membershipId(),
        invitation.normalizedEmail(),
        invitation.status(),
        invitation.deliveryStatus(),
        invitation.deliveryAttempts(),
        invitation.resendCount(),
        invitation.providerMessageIds(),
        actorAccountId,
        result,
        reasonCode,
        deliveryAttemptId,
        invitation.correlationId(),
        occurredAt);
  }

  public static boolean containsUnsafeToken(String value) {
    return value != null && value.matches("(?is).*(invite-token-|token=|tokenHash|token_hash|[?&]token).*");
  }
}

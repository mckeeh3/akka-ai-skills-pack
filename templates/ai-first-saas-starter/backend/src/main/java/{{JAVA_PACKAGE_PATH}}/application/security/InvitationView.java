package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.EmailDeliveryStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.InvitationStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import java.time.Instant;
import java.util.List;

/** Browser-safe scoped invitation read model seam. Raw token and token hash are intentionally absent. */
public final class InvitationView {
  private final InvitationService invitationService;

  public InvitationView(InvitationService invitationService) {
    this.invitationService = invitationService;
  }

  public List<InvitationRow> list(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    return invitationService.listScoped(actor, scopeType, tenantId, customerId).stream()
        .map(invite -> new InvitationRow(
            invite.invitationId(),
            invite.normalizedEmail(),
            invite.scopeType(),
            invite.tenantId(),
            invite.customerId(),
            invite.requestedRoles(),
            invite.status(),
            invite.deliveryStatus(),
            invite.deliveryAttempts(),
            invite.resendCount(),
            invite.lastDeliveryErrorSummary(),
            invite.expiresAt(),
            invite.createdAt(),
            invite.acceptedAt(),
            invite.revokedAt(),
            invite.createdByAccountId(),
            invite.resendable(),
            !invite.terminal()))
        .toList();
  }

  public List<InvitationHistoryRow> history(AuthContextResolver.ResolvedMe actor, String invitationId) {
    var invite = invitationService.invitationRepository().invitation(invitationId).orElseThrow(() -> new AuthorizationException(404, "invitation-not-found-or-forbidden"));
    invitationService.requireScopedRead(actor, invite.scopeType(), invite.tenantId(), invite.customerId());
    return invitationService.invitationRepository().lifecycleHistory(invitationId).stream()
        .map(fact -> new InvitationHistoryRow(
            fact.factId(),
            fact.invitationId(),
            fact.eventType(),
            fact.scopeType(),
            fact.tenantId(),
            fact.customerId(),
            fact.normalizedEmail(),
            fact.invitationStatus(),
            fact.deliveryStatus(),
            fact.deliveryAttempts(),
            fact.resendCount(),
            fact.actorAccountId(),
            fact.result(),
            fact.reasonCode(),
            fact.deliveryAttemptId(),
            fact.correlationId(),
            fact.occurredAt()))
        .toList();
  }

  public record InvitationRow(
      String invitationId,
      String targetEmail,
      ScopeType scopeType,
      String tenantId,
      String customerId,
      List<FoundationRole> requestedRoles,
      InvitationStatus status,
      EmailDeliveryStatus deliveryStatus,
      int deliveryAttempts,
      int resendCount,
      String lastDeliveryErrorSummary,
      Instant expiresAt,
      Instant createdAt,
      Instant acceptedAt,
      Instant revokedAt,
      String createdByAccountId,
      boolean canResend,
      boolean canRevoke) {}

  public record InvitationHistoryRow(
      String factId,
      String invitationId,
      String eventType,
      ScopeType scopeType,
      String tenantId,
      String customerId,
      String targetEmail,
      InvitationStatus invitationStatus,
      EmailDeliveryStatus deliveryStatus,
      int deliveryAttempts,
      int resendCount,
      String actorAccountId,
      String result,
      String reasonCode,
      String deliveryAttemptId,
      String correlationId,
      Instant occurredAt) {}
}

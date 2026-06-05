package ai.first.application.foundation.invitation;

import ai.first.domain.foundation.invitation.Invitation;
import ai.first.domain.foundation.invitation.InvitationLifecycleFact;
import ai.first.domain.foundation.email.EmailDeliveryStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.invitation.InvitationStatus;
import ai.first.domain.foundation.identity.ScopeType;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;

/** Browser-safe scoped invitation read model seam. Raw token and token hash are intentionally absent. */
public final class InvitationView {
  private final InvitationService invitationService;

  public InvitationView(InvitationService invitationService) {
    this.invitationService = invitationService;
  }

  public List<InvitationRow> list(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    return invitationService.listScoped(actor, scopeType, tenantId, customerId).stream()
        .sorted(Comparator.comparing(ai.first.domain.foundation.invitation.Invitation::createdAt).reversed().thenComparing(ai.first.domain.foundation.invitation.Invitation::invitationId))
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
        .sorted(Comparator.comparing(ai.first.domain.foundation.invitation.InvitationLifecycleFact::occurredAt).thenComparing(ai.first.domain.foundation.invitation.InvitationLifecycleFact::factId))
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

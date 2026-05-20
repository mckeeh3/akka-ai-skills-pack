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
}

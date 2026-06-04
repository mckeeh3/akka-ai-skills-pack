package ai.first.application.foundation.audit;

import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.identity.ScopeType;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.coreapp.useradmin.UserAdminService;

/** Browser-safe Admin Audit projection seam with scoped ordering, pagination, and redaction. */
public final class AdminAuditView {
  private static final int DEFAULT_LIMIT = 50;
  private static final int MAX_LIMIT = 100;

  private final UserAdminService userAdminService;

  public AdminAuditView(UserAdminService userAdminService) {
    this.userAdminService = userAdminService;
  }

  public List<AdminAuditRow> list(AuthContextResolver.ResolvedMe actor, int requestedLimit, String correlationId) {
    var limit = Math.max(1, Math.min(requestedLimit <= 0 ? DEFAULT_LIMIT : requestedLimit, MAX_LIMIT));
    return userAdminService.auditEvents(actor, limit, correlationId).stream()
        .sorted(Comparator.comparing(AdminAuditEvent::timestamp).reversed().thenComparing(AdminAuditEvent::auditEventId))
        .map(this::row)
        .toList();
  }

  private AdminAuditRow row(AdminAuditEvent event) {
    return new AdminAuditRow(
        event.auditEventId(),
        event.timestamp(),
        safe(event.correlationId()),
        safe(event.actorAccountId()),
        safe(event.actorMembershipId()),
        event.actorScopeType(),
        safe(event.tenantId()),
        safe(event.customerId()),
        safe(event.targetAccountId()),
        safe(event.targetMembershipId()),
        safe(event.actionType()),
        event.result(),
        safe(event.reasonCode()),
        safe(event.evidenceSummary()),
        safe(event.dataClassification()),
        "browser-safe; raw JWTs, invitation tokens, provider credentials, hidden prompts, and raw tool payloads omitted");
  }

  private String safe(String value) {
    if (value == null) return null;
    return value
        .replaceAll("(?i)(bearer\\s+)[^\\s,}]+", "$1[REDACTED]")
        .replaceAll("(?i)(api[_-]?key|secret|token)\\s*[:=]\\s*[^\\s,}]+", "$1=[REDACTED]")
        .replaceAll("(?i)(invitation[-_ ]?token)\\s*[:=]\\s*[^\\s,}]+", "$1=[REDACTED]");
  }

  public record AdminAuditRow(
      String auditEventId,
      Instant occurredAt,
      String correlationId,
      String actorAccountId,
      String actorMembershipId,
      ScopeType actorScopeType,
      String tenantId,
      String customerId,
      String targetAccountId,
      String targetMembershipId,
      String actionType,
      AdminAuditEvent.Result result,
      String reasonCode,
      String evidenceSummary,
      String dataClassification,
      String redactionSummary) {}
}

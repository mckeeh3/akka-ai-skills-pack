package ai.first.application.coreapp.myaccount;

import ai.first.domain.coreapp.myaccount.DigestExportRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.notification.NotificationRepository;

/** Governed digest/export platform lifecycle with redaction, approval gates, scheduling, idempotency, and audit traces. */
public final class DigestExportService {
  public static final String START_MANUAL_DIGEST_CAPABILITY = "digest.platform.manual.start";
  public static final String SCHEDULE_DIGEST_CAPABILITY = "digest.platform.schedule";
  public static final String REQUEST_EXPORT_CAPABILITY = "export.platform.request";
  public static final String APPROVE_EXPORT_CAPABILITY = "export.platform.approve";
  public static final String READ_CAPABILITY = "digest_export.platform.read";
  public static final String RUN_DUE_SCHEDULED_CAPABILITY = "digest.platform.run_due_scheduled";
  public static final String REQUEST_LEGAL_HOLD_CAPABILITY = "enterprise.audit.legal_hold.request";
  public static final String REQUEST_EDISCOVERY_EXPORT_CAPABILITY = "enterprise.audit.ediscovery_export.request";
  public static final String REQUEST_SIEM_EXPORT_CAPABILITY = "enterprise.audit.siem_export.request";
  public static final String REQUEST_COMPLIANCE_REPORT_CAPABILITY = "enterprise.audit.compliance_report.request";

  private final NotificationRepository repository;
  private final AuthContextResolver authContextResolver;
  private final Clock clock;

  public DigestExportService(NotificationRepository repository, AuthContextResolver authContextResolver, Clock clock) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.clock = Objects.requireNonNull(clock);
  }

  public DigestExportRequest startManualDigest(AuthContextResolver.ResolvedMe actor, DigestCommand command, String correlationId) {
    require(actor, START_MANUAL_DIGEST_CAPABILITY, correlationId);
    var duplicate = duplicate(actor, command.idempotencyKey());
    if (duplicate != null) return duplicate;
    var now = Instant.now(clock);
    var request = base(actor, DigestExportRequest.RequestType.MANUAL_DIGEST, command.idempotencyKey(), DigestExportRequest.Status.READY, profile(command.redactionProfile()), DigestExportRequest.ExportFormat.MARKDOWN, false, null, scope(command.evidenceScope()), "local://digest/" + stableSuffix(actor.account().accountId() + command.idempotencyKey()), "Manual digest ready from authorized backend evidence using " + profile(command.redactionProfile()).name().toLowerCase() + " redaction; source attention and audit records are unchanged.", null, correlationId, now);
    repository.saveDigestExportRequest(request);
    authContextResolver.appendProtectedReadTrace(actor, START_MANUAL_DIGEST_CAPABILITY, "manual-digest-ready:redacted:no-source-mutation", correlationId);
    return request;
  }

  public DigestExportRequest scheduleDigest(AuthContextResolver.ResolvedMe actor, DigestCommand command, String correlationId) {
    require(actor, SCHEDULE_DIGEST_CAPABILITY, correlationId);
    var duplicate = duplicate(actor, command.idempotencyKey());
    if (duplicate != null) return duplicate;
    var scheduledFor = command.scheduledFor() == null ? Instant.now(clock).plus(1, ChronoUnit.DAYS) : command.scheduledFor();
    if (!scheduledFor.isAfter(Instant.now(clock))) throw new AuthorizationException(400, "digest-schedule-must-be-future");
    var request = base(actor, DigestExportRequest.RequestType.SCHEDULED_DIGEST, command.idempotencyKey(), DigestExportRequest.Status.SCHEDULED, profile(command.redactionProfile()), DigestExportRequest.ExportFormat.MARKDOWN, false, scheduledFor, scope(command.evidenceScope()), null, "Digest scheduled; timer execution will re-enter the backend capability and produce only redacted local evidence.", null, correlationId, Instant.now(clock));
    repository.saveDigestExportRequest(request);
    authContextResolver.appendProtectedReadTrace(actor, SCHEDULE_DIGEST_CAPABILITY, "scheduled-digest-created", correlationId);
    return request;
  }

  public List<DigestExportRequest> runDueScheduledDigests(String tenantId, Instant dueAt, String correlationId) {
    var due = repository.listDueDigestExportRequests(tenantId, dueAt == null ? Instant.now(clock) : dueAt);
    return due.stream().map(request -> {
      var trace = "trace-digest-scheduled-run-" + stableSuffix(correlationId + request.requestId());
      var ready = new DigestExportRequest(request.requestId(), request.requestType(), request.tenantId(), request.customerId(), request.accountId(), request.membershipId(), request.idempotencyKey(), DigestExportRequest.Status.READY, request.redactionProfile(), request.exportFormat(), false, null, request.scheduledFor(), request.evidenceScope(), "local://digest/" + request.requestId(), "Scheduled digest ready from authorized backend evidence; source records unchanged and redacted profile applied.", null, append(request.traceIds(), trace), request.createdAt(), Instant.now(clock));
      repository.saveDigestExportRequest(ready);
      return ready;
    }).toList();
  }

  public DigestExportRequest requestExport(AuthContextResolver.ResolvedMe actor, ExportCommand command, String correlationId) {
    require(actor, REQUEST_EXPORT_CAPABILITY, correlationId);
    var duplicate = duplicate(actor, command.idempotencyKey());
    if (duplicate != null) return duplicate;
    var redaction = profile(command.redactionProfile());
    var sensitive = command.sensitiveApprovalRequired() || redaction != DigestExportRequest.RedactionProfile.STRICT;
    var status = sensitive ? DigestExportRequest.Status.PENDING_APPROVAL : DigestExportRequest.Status.READY;
    var result = status == DigestExportRequest.Status.READY ? "local://export/" + stableSuffix(actor.account().accountId() + command.idempotencyKey()) : null;
    var summary = sensitive ? "Export request created and paused for human approval before any result is released." : "Strictly redacted export ready for local download; secrets and raw provider tokens are not included.";
    var request = base(actor, DigestExportRequest.RequestType.EXPORT, command.idempotencyKey(), status, redaction, format(command.exportFormat()), sensitive, null, scope(command.evidenceScope()), result, summary, null, correlationId, Instant.now(clock));
    repository.saveDigestExportRequest(request);
    authContextResolver.appendProtectedReadTrace(actor, REQUEST_EXPORT_CAPABILITY, status == DigestExportRequest.Status.PENDING_APPROVAL ? "export-pending-approval" : "export-ready:strict-redaction", correlationId);
    return request;
  }

  public DigestExportRequest approveExport(AuthContextResolver.ResolvedMe actor, String requestId, String reason, String correlationId) {
    require(actor, APPROVE_EXPORT_CAPABILITY, correlationId);
    var request = read(actor, requestId, correlationId);
    if (!approvable(request.requestType())) throw new AuthorizationException(400, "export-or-enterprise-audit-request-required");
    if (request.status() != DigestExportRequest.Status.PENDING_APPROVAL) return request;
    if (reason == null || reason.isBlank()) throw new AuthorizationException(400, "export-approval-reason-required");
    var trace = "trace-export-approval-" + stableSuffix(correlationId + requestId);
    var resultUri = switch (request.requestType()) {
      case LEGAL_HOLD -> "local://legal-hold/" + request.requestId();
      case EDISCOVERY_EXPORT -> "local://ediscovery-export/" + request.requestId();
      default -> "local://export/" + request.requestId();
    };
    var summary = switch (request.requestType()) {
      case LEGAL_HOLD -> "Legal-hold marker approved by authorized reviewer; local marker preserves selected evidence scope without deleting, mutating, or widening retention outside this bounded request.";
      case EDISCOVERY_EXPORT -> "E-discovery export approved by authorized reviewer and ready as a redacted local evidence bundle; provider delivery and formal legal certification are not implied.";
      default -> "Export approved by authorized reviewer and ready with " + request.redactionProfile().name().toLowerCase() + " redaction; compliance-suite/legal-hold behavior is not implied.";
    };
    var approved = new DigestExportRequest(request.requestId(), request.requestType(), request.tenantId(), request.customerId(), request.accountId(), request.membershipId(), request.idempotencyKey(), DigestExportRequest.Status.READY, request.redactionProfile(), request.exportFormat(), true, actor.account().accountId(), request.scheduledFor(), request.evidenceScope(), resultUri, summary, null, append(request.traceIds(), trace), request.createdAt(), Instant.now(clock));
    repository.saveDigestExportRequest(approved);
    authContextResolver.appendProtectedReadTrace(actor, APPROVE_EXPORT_CAPABILITY, "export-approved:redacted", correlationId);
    return approved;
  }

  public DigestExportRequest requestLegalHold(AuthContextResolver.ResolvedMe actor, LegalHoldCommand command, String correlationId) {
    require(actor, REQUEST_LEGAL_HOLD_CAPABILITY, correlationId);
    var duplicate = duplicate(actor, command.idempotencyKey());
    if (duplicate != null) return duplicate;
    if (command.retentionUntil() == null || !command.retentionUntil().isAfter(Instant.now(clock))) throw new AuthorizationException(400, "legal-hold-retention-must-be-future");
    var scope = "legal_hold:" + scope(command.evidenceScope()) + ":retention_until=" + command.retentionUntil();
    var request = base(actor, DigestExportRequest.RequestType.LEGAL_HOLD, command.idempotencyKey(), DigestExportRequest.Status.PENDING_APPROVAL, DigestExportRequest.RedactionProfile.AUDIT_SAFE, DigestExportRequest.ExportFormat.JSON, true, command.retentionUntil(), scope, null, "Legal-hold marker request captured for human approval; no deletion, retention widening, provider delivery, or compliance certification is performed before approval.", null, correlationId, Instant.now(clock));
    repository.saveDigestExportRequest(request);
    authContextResolver.appendProtectedReadTrace(actor, REQUEST_LEGAL_HOLD_CAPABILITY, "legal-hold-pending-approval", correlationId);
    return request;
  }

  public DigestExportRequest requestEdiscoveryExport(AuthContextResolver.ResolvedMe actor, EnterpriseExportCommand command, String correlationId) {
    require(actor, REQUEST_EDISCOVERY_EXPORT_CAPABILITY, correlationId);
    var duplicate = duplicate(actor, command.idempotencyKey());
    if (duplicate != null) return duplicate;
    var request = base(actor, DigestExportRequest.RequestType.EDISCOVERY_EXPORT, command.idempotencyKey(), DigestExportRequest.Status.PENDING_APPROVAL, profile(command.redactionProfile()), format(command.exportFormat()), true, null, "ediscovery:" + scope(command.evidenceScope()), null, "E-discovery export request captured and paused for human approval; bundle output is redacted local evidence only, not provider delivery or legal certification.", null, correlationId, Instant.now(clock));
    repository.saveDigestExportRequest(request);
    authContextResolver.appendProtectedReadTrace(actor, REQUEST_EDISCOVERY_EXPORT_CAPABILITY, "ediscovery-export-pending-approval", correlationId);
    return request;
  }

  public DigestExportRequest requestSiemExport(AuthContextResolver.ResolvedMe actor, SiemExportCommand command, String correlationId) {
    require(actor, REQUEST_SIEM_EXPORT_CAPABILITY, correlationId);
    var duplicate = duplicate(actor, command.idempotencyKey());
    if (duplicate != null) return duplicate;
    var providerRequested = command.productionDeliveryRequested();
    var request = base(actor, DigestExportRequest.RequestType.SIEM_EXPORT, command.idempotencyKey(), providerRequested ? DigestExportRequest.Status.BLOCKED_PROVIDER_OR_RUNTIME : DigestExportRequest.Status.READY, DigestExportRequest.RedactionProfile.STRICT, DigestExportRequest.ExportFormat.JSON, false, null, "siem:" + scope(command.evidenceScope()), providerRequested ? null : "local://siem-export/" + stableSuffix(actor.account().accountId() + command.idempotencyKey()), providerRequested ? "SIEM provider delivery failed closed because no production SIEM webhook/provider is configured; no secret or outbound delivery is stored." : "Provider-neutral SIEM export prepared as a strict redacted local JSON handle; no vendor delivery is claimed.", providerRequested ? "siem-provider-not-configured" : null, correlationId, Instant.now(clock));
    repository.saveDigestExportRequest(request);
    authContextResolver.appendProtectedReadTrace(actor, REQUEST_SIEM_EXPORT_CAPABILITY, providerRequested ? "siem-export-provider-blocked" : "siem-export-local-ready", correlationId);
    return request;
  }

  public DigestExportRequest requestComplianceReport(AuthContextResolver.ResolvedMe actor, EnterpriseExportCommand command, String correlationId) {
    require(actor, REQUEST_COMPLIANCE_REPORT_CAPABILITY, correlationId);
    var duplicate = duplicate(actor, command.idempotencyKey());
    if (duplicate != null) return duplicate;
    var request = base(actor, DigestExportRequest.RequestType.COMPLIANCE_REPORT, command.idempotencyKey(), DigestExportRequest.Status.READY, DigestExportRequest.RedactionProfile.AUDIT_SAFE, DigestExportRequest.ExportFormat.MARKDOWN, false, null, "compliance_report:" + scope(command.evidenceScope()), "local://compliance-report/" + stableSuffix(actor.account().accountId() + command.idempotencyKey()), "Compliance report prepared from authorized audit/export evidence with audit-safe redaction; this is not a compliance-suite certification or vendor attestation.", null, correlationId, Instant.now(clock));
    repository.saveDigestExportRequest(request);
    authContextResolver.appendProtectedReadTrace(actor, REQUEST_COMPLIANCE_REPORT_CAPABILITY, "compliance-report-local-ready", correlationId);
    return request;
  }

  public DigestExportRequest read(AuthContextResolver.ResolvedMe actor, String requestId, String correlationId) {
    require(actor, READ_CAPABILITY, correlationId);
    var request = repository.findDigestExportRequest(actor.selectedContext().tenantId(), requestId).orElseThrow(() -> new AuthorizationException(404, "digest-export-request-not-found-or-forbidden"));
    if (!Objects.equals(actor.selectedContext().tenantId(), request.tenantId())) throw new AuthorizationException(404, "digest-export-request-not-found-or-forbidden");
    if (actor.selectedContext().customerId() != null && !Objects.equals(actor.selectedContext().customerId(), request.customerId())) throw new AuthorizationException(404, "digest-export-request-not-found-or-forbidden");
    if (!Objects.equals(actor.account().accountId(), request.accountId())) throw new AuthorizationException(404, "digest-export-request-not-found-or-forbidden");
    authContextResolver.appendProtectedReadTrace(actor, READ_CAPABILITY, "digest-export-read:browser-safe", correlationId);
    return request;
  }

  public List<DigestExportRequest> list(AuthContextResolver.ResolvedMe actor, String correlationId) {
    require(actor, READ_CAPABILITY, correlationId);
    return repository.listDigestExportRequests(actor.selectedContext().tenantId()).stream()
        .filter(request -> actor.selectedContext().customerId() == null || Objects.equals(actor.selectedContext().customerId(), request.customerId()))
        .filter(request -> Objects.equals(actor.account().accountId(), request.accountId()))
        .toList();
  }

  private DigestExportRequest duplicate(AuthContextResolver.ResolvedMe actor, String idempotencyKey) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) throw new AuthorizationException(400, "idempotency-key-required");
    return repository.findDigestExportRequestByIdempotencyKey(actor.selectedContext().tenantId(), actor.account().accountId(), idempotencyKey).orElse(null);
  }

  private DigestExportRequest base(AuthContextResolver.ResolvedMe actor, DigestExportRequest.RequestType type, String idempotencyKey, DigestExportRequest.Status status, DigestExportRequest.RedactionProfile profile, DigestExportRequest.ExportFormat format, boolean approvalRequired, Instant scheduledFor, String evidenceScope, String resultUri, String safeSummary, String blockerCode, String correlationId, Instant now) {
    var requestId = type.name().toLowerCase().replace('_', '-') + "-" + stableSuffix(actor.selectedContext().tenantId() + actor.account().accountId() + idempotencyKey);
    return new DigestExportRequest(requestId, type, actor.selectedContext().tenantId(), actor.selectedContext().customerId(), actor.account().accountId(), actor.selectedContext().membershipId(), idempotencyKey, status, profile, format, approvalRequired, null, scheduledFor, evidenceScope, resultUri, redact(safeSummary), blockerCode, List.of("trace-digest-export-" + stableSuffix(correlationId + requestId)), now, now);
  }

  private void require(AuthContextResolver.ResolvedMe actor, String capabilityId, String correlationId) {
    authContextResolver.requireTenant(actor.selectedContext(), actor.selectedContext().tenantId());
    authContextResolver.requireCapability(actor.selectedContext(), capabilityId);
    authContextResolver.appendProtectedReadTrace(actor, capabilityId, "digestExportPlatform.v1", correlationId);
  }

  private static DigestExportRequest.RedactionProfile profile(String requested) {
    if (requested == null || requested.isBlank()) return DigestExportRequest.RedactionProfile.STRICT;
    return DigestExportRequest.RedactionProfile.valueOf(requested.trim().toUpperCase());
  }

  private static boolean approvable(DigestExportRequest.RequestType type) {
    return type == DigestExportRequest.RequestType.EXPORT || type == DigestExportRequest.RequestType.LEGAL_HOLD || type == DigestExportRequest.RequestType.EDISCOVERY_EXPORT;
  }

  private static DigestExportRequest.ExportFormat format(String requested) {
    if (requested == null || requested.isBlank()) return DigestExportRequest.ExportFormat.MARKDOWN;
    return DigestExportRequest.ExportFormat.valueOf(requested.trim().toUpperCase());
  }

  private static String scope(String requested) {
    return requested == null || requested.isBlank() ? "authorized_attention_audit_workstream_events" : redact(requested);
  }

  private static String redact(String value) {
    if (value == null) return null;
    return value.replaceAll("(?i)(api[_-]?key|token|secret|password)=\\S+", "$1=[redacted]");
  }

  private static List<String> append(List<String> values, String value) {
    var next = new java.util.ArrayList<>(values == null ? List.of() : values);
    next.add(value);
    return List.copyOf(next);
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "digest-export").hashCode(), 36);
  }

  public record DigestCommand(String idempotencyKey, Instant scheduledFor, String redactionProfile, String evidenceScope) {}
  public record ExportCommand(String idempotencyKey, String redactionProfile, String exportFormat, boolean sensitiveApprovalRequired, String evidenceScope) {}
  public record LegalHoldCommand(String idempotencyKey, Instant retentionUntil, String evidenceScope, String reason) {}
  public record EnterpriseExportCommand(String idempotencyKey, String redactionProfile, String exportFormat, String evidenceScope, String reason) {}
  public record SiemExportCommand(String idempotencyKey, String evidenceScope, boolean productionDeliveryRequested) {}
}

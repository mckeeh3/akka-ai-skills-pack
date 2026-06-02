package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.DigestExportRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/** Governed digest/export platform lifecycle with redaction, approval gates, scheduling, idempotency, and audit traces. */
public final class DigestExportService {
  public static final String START_MANUAL_DIGEST_CAPABILITY = "digest.platform.manual.start";
  public static final String SCHEDULE_DIGEST_CAPABILITY = "digest.platform.schedule";
  public static final String REQUEST_EXPORT_CAPABILITY = "export.platform.request";
  public static final String APPROVE_EXPORT_CAPABILITY = "export.platform.approve";
  public static final String READ_CAPABILITY = "digest_export.platform.read";
  public static final String RUN_DUE_SCHEDULED_CAPABILITY = "digest.platform.run_due_scheduled";

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
    if (request.requestType() != DigestExportRequest.RequestType.EXPORT) throw new AuthorizationException(400, "export-request-required");
    if (request.status() != DigestExportRequest.Status.PENDING_APPROVAL) return request;
    if (reason == null || reason.isBlank()) throw new AuthorizationException(400, "export-approval-reason-required");
    var trace = "trace-export-approval-" + stableSuffix(correlationId + requestId);
    var approved = new DigestExportRequest(request.requestId(), request.requestType(), request.tenantId(), request.customerId(), request.accountId(), request.membershipId(), request.idempotencyKey(), DigestExportRequest.Status.READY, request.redactionProfile(), request.exportFormat(), true, actor.account().accountId(), request.scheduledFor(), request.evidenceScope(), "local://export/" + request.requestId(), "Export approved by authorized reviewer and ready with " + request.redactionProfile().name().toLowerCase() + " redaction; compliance-suite/legal-hold behavior is not implied.", null, append(request.traceIds(), trace), request.createdAt(), Instant.now(clock));
    repository.saveDigestExportRequest(approved);
    authContextResolver.appendProtectedReadTrace(actor, APPROVE_EXPORT_CAPABILITY, "export-approved:redacted", correlationId);
    return approved;
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
}

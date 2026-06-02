package {{JAVA_BASE_PACKAGE}}.domain.security;

import java.time.Instant;
import java.util.List;

/** Browser-safe lifecycle record for governed digest and export platform requests. */
public record DigestExportRequest(
    String requestId,
    RequestType requestType,
    String tenantId,
    String customerId,
    String accountId,
    String membershipId,
    String idempotencyKey,
    Status status,
    RedactionProfile redactionProfile,
    ExportFormat exportFormat,
    boolean sensitiveApprovalRequired,
    String approvedByAccountId,
    Instant scheduledFor,
    String evidenceScope,
    String resultUri,
    String safeSummary,
    String blockerCode,
    List<String> traceIds,
    Instant createdAt,
    Instant updatedAt) {
  public DigestExportRequest {
    traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
  }

  public DigestExportRequest withStatus(Status nextStatus, String safeSummary, String blockerCode, List<String> addedTraceIds, Instant now) {
    var traces = new java.util.ArrayList<>(traceIds);
    if (addedTraceIds != null) traces.addAll(addedTraceIds);
    return new DigestExportRequest(requestId, requestType, tenantId, customerId, accountId, membershipId, idempotencyKey, nextStatus, redactionProfile, exportFormat, sensitiveApprovalRequired, approvedByAccountId, scheduledFor, evidenceScope, resultUri, safeSummary, blockerCode, List.copyOf(traces), createdAt, now);
  }

  public DigestExportRequest withApproval(String approverAccountId, String safeSummary, List<String> addedTraceIds, Instant now) {
    var traces = new java.util.ArrayList<>(traceIds);
    if (addedTraceIds != null) traces.addAll(addedTraceIds);
    return new DigestExportRequest(requestId, requestType, tenantId, customerId, accountId, membershipId, idempotencyKey, Status.READY, redactionProfile, exportFormat, sensitiveApprovalRequired, approverAccountId, scheduledFor, evidenceScope, resultUri, safeSummary, null, List.copyOf(traces), createdAt, now);
  }

  public boolean terminal() {
    return status == Status.READY || status == Status.REJECTED || status == Status.CANCELLED || status == Status.BLOCKED_PROVIDER_OR_RUNTIME;
  }

  public enum RequestType { MANUAL_DIGEST, SCHEDULED_DIGEST, EXPORT, LEGAL_HOLD, EDISCOVERY_EXPORT, SIEM_EXPORT, COMPLIANCE_REPORT }
  public enum Status { SCHEDULED, QUEUED, PENDING_APPROVAL, READY, REJECTED, CANCELLED, BLOCKED_PROVIDER_OR_RUNTIME }
  public enum RedactionProfile { STANDARD, STRICT, AUDIT_SAFE }
  public enum ExportFormat { MARKDOWN, CSV, JSON }
}

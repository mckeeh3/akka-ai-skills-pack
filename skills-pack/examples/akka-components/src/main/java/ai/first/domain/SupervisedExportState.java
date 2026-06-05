package ai.first.domain;

import java.util.ArrayList;
import java.util.List;

/** State for the workflow-backed customer.data-export.prepare capability example. */
public record SupervisedExportState(
    String capabilityId,
    String tenantId,
    String customerId,
    String requestedBy,
    String authContextId,
    String requestId,
    String traceId,
    String idempotencyKey,
    String exportType,
    int riskScore,
    Status status,
    String supervisionReason,
    String approvedBy,
    String approvalBasis,
    String resultUri,
    List<String> auditTrace,
    List<String> processedIdempotencyKeys) {

  public enum Status {
    STARTED,
    SUPERVISION_REQUIRED,
    APPROVED,
    DENIED,
    GENERATING,
    READY
  }

  public SupervisedExportState {
    auditTrace = List.copyOf(auditTrace == null ? List.of() : auditTrace);
    processedIdempotencyKeys =
        List.copyOf(processedIdempotencyKeys == null ? List.of() : processedIdempotencyKeys);
  }

  public static SupervisedExportState started(
      String workflowId,
      String tenantId,
      String customerId,
      String requestedBy,
      String authContextId,
      String idempotencyKey,
      String exportType,
      int riskScore) {
    return new SupervisedExportState(
        "customer.data-export.prepare",
        tenantId,
        customerId,
        requestedBy,
        authContextId,
        workflowId,
        "trace-" + workflowId,
        idempotencyKey,
        exportType,
        riskScore,
        Status.STARTED,
        "",
        "",
        "",
        "",
        List.of("capability-started:" + idempotencyKey),
        List.of(idempotencyKey));
  }

  public boolean processed(String idempotencyKey) {
    return processedIdempotencyKeys.contains(idempotencyKey);
  }

  public SupervisedExportState supervisionRequired(String reason) {
    return copy(
        Status.SUPERVISION_REQUIRED,
        reason,
        approvedBy,
        approvalBasis,
        resultUri,
        append(auditTrace, "supervision-required:" + reason),
        processedIdempotencyKeys);
  }

  public SupervisedExportState approved(String idempotencyKey, String supervisor, String basis) {
    return copy(
        Status.APPROVED,
        supervisionReason,
        supervisor,
        basis,
        resultUri,
        append(auditTrace, "supervision-approved:" + supervisor + ":" + basis),
        appendIfMissing(processedIdempotencyKeys, idempotencyKey));
  }

  public SupervisedExportState denied(String idempotencyKey, String supervisor, String basis) {
    return copy(
        Status.DENIED,
        supervisionReason,
        supervisor,
        basis,
        resultUri,
        append(auditTrace, "supervision-denied:" + supervisor + ":" + basis),
        appendIfMissing(processedIdempotencyKeys, idempotencyKey));
  }

  public SupervisedExportState generating() {
    return copy(
        Status.GENERATING,
        supervisionReason,
        approvedBy,
        approvalBasis,
        resultUri,
        append(auditTrace, "export-generation-started:" + traceId),
        processedIdempotencyKeys);
  }

  public SupervisedExportState ready(String uri) {
    return copy(
        Status.READY,
        supervisionReason,
        approvedBy,
        approvalBasis,
        uri,
        append(auditTrace, "export-ready:" + uri),
        processedIdempotencyKeys);
  }

  public SupervisedExportState riskAssessed(String outcome) {
    return copy(
        status,
        supervisionReason,
        approvedBy,
        approvalBasis,
        resultUri,
        append(auditTrace, "risk-assessed:" + outcome + ":" + riskScore),
        processedIdempotencyKeys);
  }

  private SupervisedExportState copy(
      Status status,
      String supervisionReason,
      String approvedBy,
      String approvalBasis,
      String resultUri,
      List<String> auditTrace,
      List<String> processedIdempotencyKeys) {
    return new SupervisedExportState(
        capabilityId,
        tenantId,
        customerId,
        requestedBy,
        authContextId,
        requestId,
        traceId,
        idempotencyKey,
        exportType,
        riskScore,
        status,
        supervisionReason,
        approvedBy,
        approvalBasis,
        resultUri,
        auditTrace,
        processedIdempotencyKeys);
  }

  private static <T> List<T> append(List<T> values, T value) {
    var copy = new ArrayList<>(values);
    copy.add(value);
    return List.copyOf(copy);
  }

  private static List<String> appendIfMissing(List<String> values, String value) {
    return values.contains(value) ? values : append(values, value);
  }
}

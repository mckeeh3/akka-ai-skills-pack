package ai.first.domain;

import java.util.ArrayList;
import java.util.List;

/** State model for a consequential capability that must pause for approval before side effects. */
public record RefundApprovalState(
    String capabilityId,
    String customerId,
    String orderId,
    long amountCents,
    String requestedBy,
    Status status,
    boolean refundApplied,
    List<String> auditTrace,
    List<String> processedIdempotencyKeys) {

  public enum Status {
    WAITING_FOR_APPROVAL,
    APPLIED,
    REJECTED
  }

  public RefundApprovalState {
    auditTrace = List.copyOf(auditTrace == null ? List.of() : auditTrace);
    processedIdempotencyKeys =
        List.copyOf(processedIdempotencyKeys == null ? List.of() : processedIdempotencyKeys);
  }

  public static RefundApprovalState waiting(
      String customerId,
      String orderId,
      long amountCents,
      String requestedBy,
      String idempotencyKey) {
    return new RefundApprovalState(
        "refund.issue",
        customerId,
        orderId,
        amountCents,
        requestedBy,
        Status.WAITING_FOR_APPROVAL,
        false,
        List.of("proposal-created:" + idempotencyKey),
        List.of(idempotencyKey));
  }

  public boolean processed(String idempotencyKey) {
    return processedIdempotencyKeys.contains(idempotencyKey);
  }

  public RefundApprovalState applied(String idempotencyKey, String actor, String authorityBasis) {
    return new RefundApprovalState(
        capabilityId,
        customerId,
        orderId,
        amountCents,
        requestedBy,
        Status.APPLIED,
        true,
        append(auditTrace, "refund-applied:" + actor + ":" + authorityBasis),
        appendIfMissing(processedIdempotencyKeys, idempotencyKey));
  }

  public RefundApprovalState rejected(String idempotencyKey, String actor, String rationale) {
    return new RefundApprovalState(
        capabilityId,
        customerId,
        orderId,
        amountCents,
        requestedBy,
        Status.REJECTED,
        false,
        append(auditTrace, "refund-rejected:" + actor + ":" + rationale),
        appendIfMissing(processedIdempotencyKeys, idempotencyKey));
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

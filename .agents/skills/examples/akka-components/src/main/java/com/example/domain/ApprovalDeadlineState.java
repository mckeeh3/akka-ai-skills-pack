package com.example.domain;

/**
 * State for a workflow that waits for approval but schedules its timeout through a timer.
 *
 * <p>This complements the paused approval example by showing a workflow command handler that
 * explicitly registers a timer, which later calls a timed action that feeds a timeout command back
 * into the workflow.
 */
public record ApprovalDeadlineState(
    String documentId,
    String requestedBy,
    int timeoutSeconds,
    Status status,
    String approvedBy,
    String comment) {

  public enum Status {
    WAITING_FOR_APPROVAL,
    APPROVED,
    TIMED_OUT
  }

  public static ApprovalDeadlineState waiting(
      String documentId, String requestedBy, int timeoutSeconds) {
    return new ApprovalDeadlineState(
        documentId,
        requestedBy,
        timeoutSeconds,
        Status.WAITING_FOR_APPROVAL,
        "",
        "");
  }

  public ApprovalDeadlineState approved(String approvedBy, String comment) {
    return new ApprovalDeadlineState(
        documentId,
        requestedBy,
        timeoutSeconds,
        Status.APPROVED,
        approvedBy,
        comment == null ? "" : comment);
  }

  public ApprovalDeadlineState timedOut() {
    return new ApprovalDeadlineState(
        documentId,
        requestedBy,
        timeoutSeconds,
        Status.TIMED_OUT,
        approvedBy,
        comment);
  }
}

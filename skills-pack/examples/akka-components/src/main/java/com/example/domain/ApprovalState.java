package com.example.domain;

/** State model for the paused approval workflow example. */
public record ApprovalState(
    String documentId, String requestedBy, Status status, String approvedBy, String comment) {

  public enum Status {
    WAITING_FOR_APPROVAL,
    APPROVED
  }

  public static ApprovalState waiting(String documentId, String requestedBy) {
    return new ApprovalState(documentId, requestedBy, Status.WAITING_FOR_APPROVAL, "", "");
  }

  public ApprovalState approved(String approvedBy, String comment) {
    return new ApprovalState(documentId, requestedBy, Status.APPROVED, approvedBy, comment);
  }
}

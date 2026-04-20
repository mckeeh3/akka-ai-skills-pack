package com.example.domain;

/** State model for the transfer workflow example. */
public record TransferState(
    String workflowId,
    String fromWalletId,
    String toWalletId,
    int amount,
    String withdrawCommandId,
    String depositCommandId,
    String compensationCommandId,
    Status status,
    String failureReason) {

  public enum Status {
    STARTED,
    WITHDRAW_SUCCEEDED,
    COMPLETED,
    WITHDRAW_REJECTED,
    DEPOSIT_REJECTED,
    COMPENSATION_COMPLETED,
    UNEXPECTED_FAILURE
  }

  public static TransferState start(
      String workflowId, String fromWalletId, String toWalletId, int amount) {
    return new TransferState(
        workflowId,
        fromWalletId,
        toWalletId,
        amount,
        workflowId + "-withdraw",
        workflowId + "-deposit",
        workflowId + "-compensate",
        Status.STARTED,
        "");
  }

  public TransferState withStatus(Status newStatus) {
    return new TransferState(
        workflowId,
        fromWalletId,
        toWalletId,
        amount,
        withdrawCommandId,
        depositCommandId,
        compensationCommandId,
        newStatus,
        failureReason);
  }

  public TransferState withFailure(Status newStatus, String reason) {
    return new TransferState(
        workflowId,
        fromWalletId,
        toWalletId,
        amount,
        withdrawCommandId,
        depositCommandId,
        compensationCommandId,
        newStatus,
        reason);
  }
}

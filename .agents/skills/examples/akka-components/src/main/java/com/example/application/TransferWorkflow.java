package com.example.application;

import static java.time.Duration.ofSeconds;

import akka.javasdk.NotificationPublisher;
import akka.javasdk.NotificationPublisher.NotificationStream;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.StepName;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.workflow.Workflow;
import akka.javasdk.workflow.Workflow.RecoverStrategy;
import com.example.domain.TransferState;
import com.example.domain.Wallet;

/**
 * Workflow example that orchestrates two wallet entities and compensates a failed deposit.
 */
@Component(id = "transfer-workflow")
public class TransferWorkflow extends Workflow<TransferState> {

  public record StartTransfer(String fromWalletId, String toWalletId, int amount) {}

  public record Withdraw(String walletId, String commandId, int amount) {}

  public record Deposit(String walletId, String commandId, int amount) {}

  public record TransferUpdate(String step, String status, String message) {}

  private final ComponentClient componentClient;
  private final NotificationPublisher<TransferUpdate> notificationPublisher;

  public TransferWorkflow(
      ComponentClient componentClient, NotificationPublisher<TransferUpdate> notificationPublisher) {
    this.componentClient = componentClient;
    this.notificationPublisher = notificationPublisher;
  }

  @Override
  public WorkflowSettings settings() {
    return WorkflowSettings.builder()
        .defaultStepTimeout(ofSeconds(5))
        .defaultStepRecovery(
            RecoverStrategy.maxRetries(1).failoverTo(TransferWorkflow::markUnexpectedFailureStep))
        .stepRecovery(
            TransferWorkflow::depositStep,
            RecoverStrategy.maxRetries(1).failoverTo(TransferWorkflow::compensateWithdrawStep))
        .build();
  }

  public Effect<TransferState> start(StartTransfer transfer) {
    if (transfer.amount() <= 0) {
      return effects().error("transfer amount must be greater than zero");
    } else if (transfer.fromWalletId().equals(transfer.toWalletId())) {
      return effects().error("source and destination wallet must be different");
    } else if (currentState() != null) {
      return effects().error("transfer already started");
    }

    var initialState =
        TransferState.start(
            commandContext().workflowId(), transfer.fromWalletId(), transfer.toWalletId(), transfer.amount());
    var withdraw = new Withdraw(initialState.fromWalletId(), initialState.withdrawCommandId(), initialState.amount());

    return effects()
        .updateState(initialState)
        .transitionTo(TransferWorkflow::withdrawStep)
        .withInput(withdraw)
        .thenReply(initialState);
  }

  public ReadOnlyEffect<TransferState> get() {
    if (currentState() == null) {
      return effects().error("transfer not started");
    }
    return effects().reply(currentState());
  }

  public NotificationStream<TransferUpdate> updates() {
    return notificationPublisher.stream();
  }

  @StepName("withdraw")
  private StepEffect withdrawStep(Withdraw withdraw) {
    var result =
        componentClient
            .forEventSourcedEntity(withdraw.walletId())
            .method(WalletEntity::withdraw)
            .invoke(new Wallet.Command.Withdraw(withdraw.commandId(), withdraw.amount()));

    if (result.accepted()) {
      notificationPublisher.publish(
          new TransferUpdate(
              "withdraw",
              TransferState.Status.WITHDRAW_SUCCEEDED.name(),
              "Funds withdrawn from source wallet"));
      var deposit =
          new Deposit(currentState().toWalletId(), currentState().depositCommandId(), currentState().amount());
      return stepEffects()
          .updateState(currentState().withStatus(TransferState.Status.WITHDRAW_SUCCEEDED))
          .thenTransitionTo(TransferWorkflow::depositStep)
          .withInput(deposit);
    }

    notificationPublisher.publish(
        new TransferUpdate(
            "withdraw",
            TransferState.Status.WITHDRAW_REJECTED.name(),
            result.reason()));
    return stepEffects()
        .updateState(currentState().withFailure(TransferState.Status.WITHDRAW_REJECTED, result.reason()))
        .thenEnd();
  }

  @StepName("deposit")
  private StepEffect depositStep(Deposit deposit) {
    var result =
        componentClient
            .forEventSourcedEntity(deposit.walletId())
            .method(WalletEntity::deposit)
            .invoke(new Wallet.Command.Deposit(deposit.commandId(), deposit.amount()));

    if (result.accepted()) {
      notificationPublisher.publish(
          new TransferUpdate(
              "deposit", TransferState.Status.COMPLETED.name(), "Funds deposited to destination wallet"));
      return stepEffects().updateState(currentState().withStatus(TransferState.Status.COMPLETED)).thenEnd();
    }

    notificationPublisher.publish(
        new TransferUpdate(
            "deposit",
            TransferState.Status.DEPOSIT_REJECTED.name(),
            result.reason()));
    return stepEffects()
        .updateState(currentState().withFailure(TransferState.Status.DEPOSIT_REJECTED, result.reason()))
        .thenTransitionTo(TransferWorkflow::compensateWithdrawStep);
  }

  @StepName("compensate-withdraw")
  private StepEffect compensateWithdrawStep() {
    var result =
        componentClient
            .forEventSourcedEntity(currentState().fromWalletId())
            .method(WalletEntity::deposit)
            .invoke(
                new Wallet.Command.Deposit(
                    currentState().compensationCommandId(), currentState().amount()));

    if (result.accepted()) {
      notificationPublisher.publish(
          new TransferUpdate(
              "compensate-withdraw",
              TransferState.Status.COMPENSATION_COMPLETED.name(),
              "Compensation deposited funds back to the source wallet"));
      return stepEffects()
          .updateState(currentState().withStatus(TransferState.Status.COMPENSATION_COMPLETED))
          .thenEnd();
    }

    notificationPublisher.publish(
        new TransferUpdate(
            "compensate-withdraw",
            TransferState.Status.UNEXPECTED_FAILURE.name(),
            result.reason()));
    return stepEffects()
        .updateState(currentState().withFailure(TransferState.Status.UNEXPECTED_FAILURE, result.reason()))
        .thenEnd();
  }

  @StepName("mark-unexpected-failure")
  private StepEffect markUnexpectedFailureStep() {
    notificationPublisher.publish(
        new TransferUpdate(
            "mark-unexpected-failure",
            TransferState.Status.UNEXPECTED_FAILURE.name(),
            "step failed after retries"));
    return stepEffects()
        .updateState(
            currentState().withFailure(
                TransferState.Status.UNEXPECTED_FAILURE, "step failed after retries"))
        .thenEnd();
  }
}

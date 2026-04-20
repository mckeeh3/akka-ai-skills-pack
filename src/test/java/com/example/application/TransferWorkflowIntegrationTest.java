package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.CommandException;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.TransferState;
import com.example.domain.Wallet;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class TransferWorkflowIntegrationTest extends TestKitSupport {

  @Test
  void successfulTransferWithdrawsAndDepositsFunds() {
    var fromWallet = "wallet-success-a";
    var toWallet = "wallet-success-b";
    createWallet(fromWallet, 100);
    createWallet(toWallet, 20);

    var initial =
        componentClient
            .forWorkflow("transfer-1")
            .method(TransferWorkflow::start)
            .invoke(new TransferWorkflow.StartTransfer(fromWallet, toWallet, 30));

    assertEquals(TransferState.Status.STARTED, initial.status());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                assertEquals(
                    TransferState.Status.COMPLETED,
                    componentClient.forWorkflow("transfer-1").method(TransferWorkflow::get).invoke().status()));

    assertEquals(70, wallet(fromWallet).balance());
    assertEquals(50, wallet(toWallet).balance());
  }

  @Test
  void failedDepositTriggersCompensation() {
    var fromWallet = "wallet-compensate-a";
    createWallet(fromWallet, 100);

    componentClient
        .forWorkflow("transfer-2")
        .method(TransferWorkflow::start)
        .invoke(new TransferWorkflow.StartTransfer(fromWallet, "missing-wallet", 40));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var state = componentClient.forWorkflow("transfer-2").method(TransferWorkflow::get).invoke();
              assertEquals(TransferState.Status.COMPENSATION_COMPLETED, state.status());
              assertEquals("wallet does not exist", state.failureReason());
            });

    assertEquals(100, wallet(fromWallet).balance());
  }

  @Test
  void invalidTransferAmountIsRejected() {
    var fromWallet = "wallet-invalid-a";
    var toWallet = "wallet-invalid-b";
    createWallet(fromWallet, 100);
    createWallet(toWallet, 20);

    var error =
        assertThrows(
            CommandException.class,
            () ->
                componentClient
                    .forWorkflow("transfer-3")
                    .method(TransferWorkflow::start)
                    .invoke(new TransferWorkflow.StartTransfer(fromWallet, toWallet, 0)));

    assertTrue(error.getMessage().contains("transfer amount must be greater than zero"));
  }

  @Test
  void startingTheSameWorkflowTwiceIsRejected() {
    var fromWallet = "wallet-duplicate-a";
    var toWallet = "wallet-duplicate-b";
    createWallet(fromWallet, 100);
    createWallet(toWallet, 20);

    componentClient
        .forWorkflow("transfer-4")
        .method(TransferWorkflow::start)
        .invoke(new TransferWorkflow.StartTransfer(fromWallet, toWallet, 10));

    var error =
        assertThrows(
            CommandException.class,
            () ->
                componentClient
                    .forWorkflow("transfer-4")
                    .method(TransferWorkflow::start)
                    .invoke(new TransferWorkflow.StartTransfer(fromWallet, toWallet, 5)));

    assertTrue(error.getMessage().contains("transfer already started"));
  }

  private void createWallet(String walletId, int initialBalance) {
    componentClient
        .forEventSourcedEntity(walletId)
        .method(WalletEntity::create)
        .invoke(new Wallet.Command.Create(initialBalance));
  }

  private Wallet.State wallet(String walletId) {
    return componentClient.forEventSourcedEntity(walletId).method(WalletEntity::get).invoke();
  }
}

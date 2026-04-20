package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.Done;
import akka.javasdk.testkit.EventSourcedTestKit;
import com.example.domain.Wallet;
import org.junit.jupiter.api.Test;

class WalletEntityTest {

  private EventSourcedTestKit<Wallet.State, Wallet.Event, WalletEntity> newTestKit(String entityId) {
    return EventSourcedTestKit.of(entityId, WalletEntity::new);
  }

  @Test
  void createAndWithdrawPersistEvents() {
    var testKit = newTestKit("wallet-1");

    var createResult =
        testKit.method(WalletEntity::create).invoke(new Wallet.Command.Create(100));

    assertEquals(Done.getInstance(), createResult.getReply());
    assertEquals(new Wallet.Event.Created(100), createResult.getNextEventOfType(Wallet.Event.Created.class));

    var withdrawResult =
        testKit
            .method(WalletEntity::withdraw)
            .invoke(new Wallet.Command.Withdraw("withdraw-1", 30));

    assertEquals(Wallet.Result.success(70), withdrawResult.getReply());
    assertEquals(
        new Wallet.Event.Withdrawn("withdraw-1", 30),
        withdrawResult.getNextEventOfType(Wallet.Event.Withdrawn.class));
  }

  @Test
  void duplicateCommandIdBecomesIdempotentNoOp() {
    var testKit = newTestKit("wallet-1");

    testKit.method(WalletEntity::create).invoke(new Wallet.Command.Create(100));

    var first =
        testKit
            .method(WalletEntity::withdraw)
            .invoke(new Wallet.Command.Withdraw("withdraw-1", 30));
    var second =
        testKit
            .method(WalletEntity::withdraw)
            .invoke(new Wallet.Command.Withdraw("withdraw-1", 30));

    assertEquals(Wallet.Result.success(70), first.getReply());
    assertEquals(Wallet.Result.success(70), second.getReply());
    assertFalse(second.didPersistEvents());
  }

  @Test
  void insufficientBalanceReturnsRejectedResult() {
    var testKit = newTestKit("wallet-1");

    testKit.method(WalletEntity::create).invoke(new Wallet.Command.Create(10));

    var result =
        testKit
            .method(WalletEntity::withdraw)
            .invoke(new Wallet.Command.Withdraw("withdraw-1", 50));

    assertEquals(Wallet.Result.rejected("insufficient balance", 10), result.getReply());
    assertFalse(result.didPersistEvents());
    assertTrue(testKit.getState().exists());
    assertEquals(10, testKit.getState().balance());
  }
}

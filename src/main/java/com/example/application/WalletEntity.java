package com.example.application;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.example.domain.Wallet;

/**
 * Internal wallet entity used by workflow examples.
 *
 * <p>This entity returns explicit result types instead of business errors so workflows can decide
 * whether to stop, compensate, or retry.
 */
@Component(id = "workflow-wallet")
public class WalletEntity extends EventSourcedEntity<Wallet.State, Wallet.Event> {

  private final String walletId;

  public WalletEntity(EventSourcedEntityContext context) {
    this.walletId = context.entityId();
  }

  @Override
  public Wallet.State emptyState() {
    return Wallet.State.empty(walletId);
  }

  public Effect<Done> create(Wallet.Command.Create command) {
    if (command.initialBalance() < 0) {
      return effects().error("initial balance must be zero or greater");
    } else if (currentState().exists()) {
      return effects().error("wallet already exists");
    }

    return effects().persist(new Wallet.Event.Created(command.initialBalance())).thenReply(__ -> Done.getInstance());
  }

  public ReadOnlyEffect<Wallet.State> get() {
    if (!currentState().exists()) {
      return effects().error("wallet does not exist");
    }
    return effects().reply(currentState());
  }

  public Effect<Wallet.Result> withdraw(Wallet.Command.Withdraw command) {
    if (command.amount() <= 0) {
      return effects().reply(Wallet.Result.rejected("amount must be greater than zero", currentState().balance()));
    } else if (!currentState().exists()) {
      return effects().reply(Wallet.Result.rejected("wallet does not exist", currentState().balance()));
    } else if (currentState().hasProcessed(command.commandId())) {
      return effects().reply(Wallet.Result.success(currentState().balance()));
    } else if (!currentState().canWithdraw(command.amount())) {
      return effects().reply(Wallet.Result.rejected("insufficient balance", currentState().balance()));
    }

    return effects()
        .persist(new Wallet.Event.Withdrawn(command.commandId(), command.amount()))
        .thenReply(newState -> Wallet.Result.success(newState.balance()));
  }

  public Effect<Wallet.Result> deposit(Wallet.Command.Deposit command) {
    if (command.amount() <= 0) {
      return effects().reply(Wallet.Result.rejected("amount must be greater than zero", currentState().balance()));
    } else if (!currentState().exists()) {
      return effects().reply(Wallet.Result.rejected("wallet does not exist", currentState().balance()));
    } else if (currentState().hasProcessed(command.commandId())) {
      return effects().reply(Wallet.Result.success(currentState().balance()));
    }

    return effects()
        .persist(new Wallet.Event.Deposited(command.commandId(), command.amount()))
        .thenReply(newState -> Wallet.Result.success(newState.balance()));
  }

  @Override
  public Wallet.State applyEvent(Wallet.Event event) {
    return switch (event) {
      case Wallet.Event.Created created -> currentState().create(created.initialBalance());
      case Wallet.Event.Withdrawn withdrawn ->
          currentState().withdraw(withdrawn.commandId(), withdrawn.amount());
      case Wallet.Event.Deposited deposited ->
          currentState().deposit(deposited.commandId(), deposited.amount());
    };
  }
}

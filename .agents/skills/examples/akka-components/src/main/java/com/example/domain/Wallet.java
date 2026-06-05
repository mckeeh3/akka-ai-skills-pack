package com.example.domain;

import akka.javasdk.annotations.TypeName;
import java.util.HashSet;
import java.util.Set;

/**
 * Minimal wallet subdomain used by workflow examples.
 *
 * <p>The wallet tracks processed command ids so workflow step retries do not apply the same debit
 * or credit twice.
 */
public final class Wallet {

  private Wallet() {}

  public record State(String walletId, int balance, boolean created, Set<String> processedCommandIds) {

    public static State empty(String walletId) {
      return new State(walletId, 0, false, Set.of());
    }

    public boolean exists() {
      return created;
    }

    public boolean hasProcessed(String commandId) {
      return processedCommandIds.contains(commandId);
    }

    public boolean canWithdraw(int amount) {
      return balance >= amount;
    }

    public State create(int initialBalance) {
      return new State(walletId, initialBalance, true, processedCommandIds);
    }

    public State withdraw(String commandId, int amount) {
      return new State(walletId, balance - amount, created, addProcessed(commandId));
    }

    public State deposit(String commandId, int amount) {
      return new State(walletId, balance + amount, created, addProcessed(commandId));
    }

    private Set<String> addProcessed(String commandId) {
      var updated = new HashSet<>(processedCommandIds);
      updated.add(commandId);
      return Set.copyOf(updated);
    }
  }

  public sealed interface Command {
    record Create(int initialBalance) implements Command {}

    record Withdraw(String commandId, int amount) implements Command {}

    record Deposit(String commandId, int amount) implements Command {}
  }

  public record Result(boolean accepted, int balance, String reason) {
    public static Result success(int balance) {
      return new Result(true, balance, "");
    }

    public static Result rejected(String reason, int balance) {
      return new Result(false, balance, reason);
    }
  }

  public sealed interface Event {
    @TypeName("wallet-created")
    record Created(int initialBalance) implements Event {}

    @TypeName("wallet-withdrawn")
    record Withdrawn(String commandId, int amount) implements Event {}

    @TypeName("wallet-deposited")
    record Deposited(String commandId, int amount) implements Event {}
  }
}

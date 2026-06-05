package com.example.domain.security;

import java.util.Optional;

/** Pure local-account command decisions. Returns an updated state or an idempotent no-op. */
public final class LocalAccountCommandHandler {

  private LocalAccountCommandHandler() {}

  public static Optional<LocalAccount.State> onCommand(
      LocalAccount.State state, LocalAccount.Command.Invite command) {
    var email = LocalAccount.normalizeEmail(command.email());
    if (state.exists() && state.email().equals(email)) {
      return Optional.empty();
    }
    if (state.exists()) {
      throw new IllegalStateException("User already exists with a different email: " + state.userId());
    }
    return Optional.of(state.invited(email, command.profile(), command.roles(), command.at()));
  }

  public static Optional<LocalAccount.State> onCommand(
      LocalAccount.State state, LocalAccount.Command.LinkAndActivate command) {
    if (!state.exists()) {
      throw new IllegalStateException("User does not exist: " + state.userId());
    }
    if (state.status() == AccountStatus.DISABLED) {
      throw new IllegalStateException("User is disabled: " + state.userId());
    }
    if (state.isActive() && command.workosUserId().equals(state.workosUserId())) {
      return Optional.empty();
    }
    if (state.workosUserId() != null && !state.workosUserId().equals(command.workosUserId())) {
      throw new IllegalStateException("User is already linked to a different WorkOS identity: " + state.userId());
    }
    return Optional.of(state.activate(command.workosUserId(), command.at()));
  }

  public static Optional<LocalAccount.State> onCommand(
      LocalAccount.State state, LocalAccount.Command.ReplaceRoles command) {
    if (!state.exists()) {
      throw new IllegalStateException("User does not exist: " + state.userId());
    }
    var sortedRoles = LocalAccount.sortRoles(command.roles());
    if (state.roles().equals(sortedRoles)) {
      return Optional.empty();
    }
    return Optional.of(state.withRoles(sortedRoles, command.at()));
  }

  public static Optional<LocalAccount.State> onCommand(
      LocalAccount.State state, LocalAccount.Command.Disable command) {
    if (!state.exists()) {
      throw new IllegalStateException("User does not exist: " + state.userId());
    }
    if (state.status() == AccountStatus.DISABLED) {
      return Optional.empty();
    }
    return Optional.of(state.disabled(command.at()));
  }

  public static Optional<LocalAccount.State> onCommand(
      LocalAccount.State state, LocalAccount.Command.Reactivate command) {
    if (!state.exists()) {
      throw new IllegalStateException("User does not exist: " + state.userId());
    }
    if (state.status() == AccountStatus.ACTIVE) {
      return Optional.empty();
    }
    return Optional.of(state.reactivated(command.at()));
  }
}

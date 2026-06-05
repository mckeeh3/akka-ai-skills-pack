package ai.first.domain.security;

import java.util.Optional;

/** Pure tenant/customer command decisions. */
public final class TenantDirectoryCommandHandler {

  private TenantDirectoryCommandHandler() {}

  public static Optional<TenantDirectory.State> onCommand(
      TenantDirectory.State state, TenantDirectory.Command.UpsertTenant command) {
    var name = command.name().trim();
    if (state.exists() && state.active() && state.name().equals(name)) {
      return Optional.empty();
    }
    return Optional.of(state.upsertTenant(name, command.at()));
  }

  public static Optional<TenantDirectory.State> onCommand(
      TenantDirectory.State state, TenantDirectory.Command.DisableTenant command) {
    if (!state.exists()) {
      throw new IllegalStateException("Tenant does not exist: " + state.tenantId());
    }
    if (!state.active()) {
      return Optional.empty();
    }
    return Optional.of(state.disableTenant(command.at()));
  }

  public static Optional<TenantDirectory.State> onCommand(
      TenantDirectory.State state, TenantDirectory.Command.UpsertCustomer command) {
    if (!state.exists()) {
      throw new IllegalStateException("Tenant does not exist: " + state.tenantId());
    }
    var name = command.name().trim();
    var existing = state.findCustomer(command.customerId());
    if (existing.isPresent() && existing.get().active() && existing.get().name().equals(name)) {
      return Optional.empty();
    }
    return Optional.of(state.upsertCustomer(command.customerId(), name, command.at()));
  }
}

package ai.first.domain.security;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Tenant and customer current-state directory for scoped backend authorization. */
public final class TenantDirectory {

  private TenantDirectory() {}

  public record State(String tenantId, String name, boolean active, List<Customer> customers, Instant createdAt, Instant updatedAt) {

    public static State empty(String tenantId) {
      return new State(tenantId, "", false, List.of(), null, null);
    }

    public boolean exists() {
      return name != null && !name.isBlank();
    }

    public State upsertTenant(String tenantName, Instant at) {
      var created = createdAt == null ? at : createdAt;
      return new State(tenantId, tenantName.trim(), true, customers, created, at);
    }

    public State disableTenant(Instant at) {
      return new State(tenantId, name, false, customers, createdAt, at);
    }

    public State upsertCustomer(String customerId, String customerName, Instant at) {
      var replacement = new Customer(customerId, customerName.trim(), true);
      var merged =
          customers.stream()
              .filter(customer -> !customer.customerId().equals(customerId))
              .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
      merged.add(replacement);
      var sorted = merged.stream().sorted(Comparator.comparing(Customer::customerId)).toList();
      return new State(tenantId, name, active, sorted, createdAt, at);
    }

    public Optional<Customer> findCustomer(String customerId) {
      return customers.stream().filter(customer -> customer.customerId().equals(customerId)).findFirst();
    }
  }

  public record Customer(String customerId, String name, boolean active) {}

  public sealed interface Command {
    record UpsertTenant(String name, Instant at) implements Command {}

    record DisableTenant(Instant at) implements Command {}

    record UpsertCustomer(String customerId, String name, Instant at) implements Command {}
  }
}

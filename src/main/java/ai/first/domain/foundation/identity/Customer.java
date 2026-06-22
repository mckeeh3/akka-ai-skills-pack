package ai.first.domain.foundation.identity;

public record Customer(String tenantId, String customerId, String displayName, boolean active, boolean archived) {
  public Customer(String tenantId, String customerId, String displayName, boolean active) {
    this(tenantId, customerId, displayName, active, false);
  }
}

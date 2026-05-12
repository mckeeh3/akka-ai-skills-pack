package com.example.application;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import akka.javasdk.keyvalueentity.KeyValueEntityContext;
import com.example.domain.Customer;
import java.time.Instant;

@Component(id = "customer")
public class CustomerEntity extends KeyValueEntity<Customer> {
  private final String entityId;

  public CustomerEntity(KeyValueEntityContext context) {
    this.entityId = context.entityId();
  }

  @Override
  public Customer emptyState() {
    return Customer.empty(entityId);
  }

  public record UpsertCustomer(String tenantId, String name, boolean active) {}

  public ReadOnlyEffect<Customer> get() {
    return effects().reply(currentState());
  }

  public Effect<Customer> upsert(UpsertCustomer command) {
    var now = Instant.now();
    var createdAt = currentState().exists() ? currentState().createdAt() : now;
    var newState = new Customer(entityId, command.tenantId(), command.name(), command.active(), createdAt, now);
    return effects().updateState(newState).thenReply(newState);
  }

  public Effect<Done> deleteCustomer() {
    return effects().deleteEntity().thenReply(Done.getInstance());
  }
}

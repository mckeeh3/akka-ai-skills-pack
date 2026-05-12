package com.example.application;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import akka.javasdk.keyvalueentity.KeyValueEntityContext;
import com.example.domain.Tenant;
import java.time.Instant;

@Component(id = "tenant")
public class TenantEntity extends KeyValueEntity<Tenant> {
  private final String entityId;

  public TenantEntity(KeyValueEntityContext context) {
    this.entityId = context.entityId();
  }

  @Override
  public Tenant emptyState() {
    return Tenant.empty(entityId);
  }

  public record UpsertTenant(String name, boolean active) {}

  public ReadOnlyEffect<Tenant> get() {
    return effects().reply(currentState());
  }

  public Effect<Tenant> upsert(UpsertTenant command) {
    var now = Instant.now();
    var createdAt = currentState().exists() ? currentState().createdAt() : now;
    var newState = new Tenant(entityId, command.name(), command.active(), createdAt, now);
    return effects().updateState(newState).thenReply(newState);
  }

  public Effect<Done> deleteTenant() {
    return effects().deleteEntity().thenReply(Done.getInstance());
  }
}

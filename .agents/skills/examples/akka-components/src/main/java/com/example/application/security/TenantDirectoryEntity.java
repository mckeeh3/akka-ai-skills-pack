package com.example.application.security;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import akka.javasdk.keyvalueentity.KeyValueEntityContext;
import com.example.domain.security.TenantDirectory;
import com.example.domain.security.TenantDirectoryCommandHandler;
import com.example.domain.security.TenantDirectoryValidator;

/** Current-state tenant/customer directory used by backend scope checks. */
@Component(id = "security-tenant-directory")
public class TenantDirectoryEntity extends KeyValueEntity<TenantDirectory.State> {

  private final String tenantId;

  public TenantDirectoryEntity(KeyValueEntityContext context) {
    this.tenantId = context.entityId();
  }

  @Override
  public TenantDirectory.State emptyState() {
    return TenantDirectory.State.empty(tenantId);
  }

  public ReadOnlyEffect<TenantDirectory.State> get() {
    return effects().reply(currentState());
  }

  public Effect<Done> upsertTenant(TenantDirectory.Command.UpsertTenant command) {
    var errors = TenantDirectoryValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    var updatedState = TenantDirectoryCommandHandler.onCommand(currentState(), command);
    if (updatedState.isEmpty()) {
      return effects().reply(Done.getInstance());
    }
    return effects().updateState(updatedState.get()).thenReply(Done.getInstance());
  }

  public Effect<Done> disableTenant(TenantDirectory.Command.DisableTenant command) {
    var errors = TenantDirectoryValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    try {
      var updatedState = TenantDirectoryCommandHandler.onCommand(currentState(), command);
      if (updatedState.isEmpty()) {
        return effects().reply(Done.getInstance());
      }
      return effects().updateState(updatedState.get()).thenReply(Done.getInstance());
    } catch (IllegalStateException ex) {
      return effects().error(ex.getMessage());
    }
  }

  public Effect<Done> upsertCustomer(TenantDirectory.Command.UpsertCustomer command) {
    var errors = TenantDirectoryValidator.validate(command);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    try {
      var updatedState = TenantDirectoryCommandHandler.onCommand(currentState(), command);
      if (updatedState.isEmpty()) {
        return effects().reply(Done.getInstance());
      }
      return effects().updateState(updatedState.get()).thenReply(Done.getInstance());
    } catch (IllegalStateException ex) {
      return effects().error(ex.getMessage());
    }
  }
}

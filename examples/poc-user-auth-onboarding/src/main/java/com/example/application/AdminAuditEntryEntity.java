package com.example.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import akka.javasdk.keyvalueentity.KeyValueEntityContext;
import com.example.domain.AdminAuditEntry;

@Component(id = "admin-audit-entry")
public class AdminAuditEntryEntity extends KeyValueEntity<AdminAuditEntry> {
  private final String entityId;

  public AdminAuditEntryEntity(KeyValueEntityContext context) {
    this.entityId = context.entityId();
  }

  @Override
  public AdminAuditEntry emptyState() {
    return null;
  }

  public ReadOnlyEffect<AdminAuditEntry> get() {
    return effects().reply(currentState());
  }

  public Effect<AdminAuditEntry> create(AdminAuditEntry entry) {
    if (currentState() != null) {
      return effects().error("Audit entry already exists: " + entityId);
    }
    return effects().updateState(entry).thenReply(entry);
  }
}

package ai.first.application.security;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import akka.javasdk.keyvalueentity.KeyValueEntityContext;
import ai.first.domain.security.AdminAuditEntry;
import ai.first.domain.security.AdminAuditEntryValidator;

/** Append-only audit-entry entity. Each entity id stores one immutable admin/security audit fact. */
@Component(id = "security-admin-audit-entry")
public class AdminAuditEntryEntity extends KeyValueEntity<AdminAuditEntry> {

  private final String auditId;

  public AdminAuditEntryEntity(KeyValueEntityContext context) {
    this.auditId = context.entityId();
  }

  @Override
  public AdminAuditEntry emptyState() {
    return null;
  }

  public ReadOnlyEffect<AdminAuditEntry> get() {
    return effects().reply(currentState());
  }

  public Effect<AdminAuditEntry> create(AdminAuditEntry entry) {
    var errors = AdminAuditEntryValidator.validate(entry);
    if (!errors.isEmpty()) {
      return effects().error(String.join("; ", errors));
    }
    if (!auditId.equals(entry.auditId())) {
      return effects().error("auditId must match entity id: " + auditId);
    }
    if (currentState() != null) {
      if (currentState().equals(entry)) {
        return effects().reply(currentState());
      }
      return effects().error("Audit entry already exists: " + auditId);
    }
    return effects().updateState(entry).thenReply(entry);
  }
}

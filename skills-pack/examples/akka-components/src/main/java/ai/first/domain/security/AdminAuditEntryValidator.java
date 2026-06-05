package ai.first.domain.security;

import java.util.ArrayList;
import java.util.List;

/** Validation for append-only admin/security audit entries. */
public final class AdminAuditEntryValidator {

  private AdminAuditEntryValidator() {}

  public static List<String> validate(AdminAuditEntry entry) {
    var errors = new ArrayList<String>();
    if (entry == null) {
      return List.of("audit entry must not be null");
    }
    if (isBlank(entry.auditId())) {
      errors.add("auditId must not be blank");
    }
    if (entry.action() == null) {
      errors.add("action must not be null");
    }
    if (isBlank(entry.actorUserId())) {
      errors.add("actorUserId must not be blank");
    }
    if (entry.occurredAt() == null) {
      errors.add("occurredAt must not be null");
    }
    return List.copyOf(errors);
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}

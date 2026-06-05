package ai.first.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.KeyValueEntityTestKit;
import ai.first.domain.security.AdminAuditEntry;
import ai.first.domain.security.AdminAuditEntry.AdminAuditAction;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AdminAuditEntryEntityTest {

  private static final Instant T1 = Instant.parse("2026-01-01T00:00:00Z");

  private KeyValueEntityTestKit<AdminAuditEntry, AdminAuditEntryEntity> newTestKit(String auditId) {
    return KeyValueEntityTestKit.of(auditId, AdminAuditEntryEntity::new);
  }

  @Test
  void createStoresAppendOnlyAuditEntry() {
    var testKit = newTestKit("audit-1");
    var entry =
        new AdminAuditEntry(
            "audit-1",
            AdminAuditAction.INVITE_USER,
            "admin-1",
            "user-1",
            "tenant-1",
            null,
            T1,
            Map.of("email", "jane@example.com"));

    var result = testKit.method(AdminAuditEntryEntity::create).invoke(entry);

    assertEquals(entry, result.getReply());
    assertTrue(result.stateWasUpdated());
    assertEquals(entry, testKit.getState());
  }

  @Test
  void duplicateSameAuditEntryIsIdempotent() {
    var testKit = newTestKit("audit-1");
    var entry =
        new AdminAuditEntry(
            "audit-1", AdminAuditAction.REPLACE_ROLES, "admin-1", "user-1", null, null, T1, Map.of());

    testKit.method(AdminAuditEntryEntity::create).invoke(entry);
    var result = testKit.method(AdminAuditEntryEntity::create).invoke(entry);

    assertEquals(entry, result.getReply());
    assertFalse(result.stateWasUpdated());
  }

  @Test
  void conflictingAuditEntryCannotOverwriteExistingFact() {
    var testKit = newTestKit("audit-1");
    var entry =
        new AdminAuditEntry(
            "audit-1", AdminAuditAction.INVITE_USER, "admin-1", "user-1", null, null, T1, Map.of());
    var conflicting =
        new AdminAuditEntry(
            "audit-1", AdminAuditAction.DISABLE_USER, "admin-1", "user-1", null, null, T1, Map.of());

    testKit.method(AdminAuditEntryEntity::create).invoke(entry);
    var result = testKit.method(AdminAuditEntryEntity::create).invoke(conflicting);

    assertTrue(result.isError());
    assertEquals("Audit entry already exists: audit-1", result.getError());
    assertEquals(entry, testKit.getState());
  }
}

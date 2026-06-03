package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.KeyValueEntityTestKit;
import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.IdentityRepositoryState;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class DurableIdentityRepositoryEntityTest {

  private KeyValueEntityTestKit<IdentityRepositoryState, DurableIdentityRepositoryEntity> newTestKit() {
    return KeyValueEntityTestKit.of(
        DurableIdentityRepositoryEntity.ENTITY_ID,
        __ -> new DurableIdentityRepositoryEntity());
  }

  @Test
  void persistsAccountProfileSettingsMembershipTenantAndAuditThroughAkkaState() {
    var testKit = newTestKit();
    var account = new Account("admin@example.com", null, "admin@example.com", "admin@example.com", AccountStatus.INVITED, "UNLINKED");
    var linked = new Account("admin@example.com", "workos-admin", "admin@example.com", "admin@example.com", AccountStatus.ACTIVE, "LINKED");
    var profile = new UserProfile("admin@example.com", "admin@example.com", "Admin", "A", "User", null);
    var settings = new UserSettings("admin@example.com", UserSettings.ThemeId.OBSIDIAN_DARK);
    var tenant = new Tenant("tenant-1", "Tenant One", true);
    var membership = new Membership("membership-admin", "admin@example.com", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN), MembershipStatus.ACTIVE, false, null);
    var audit = audit("audit-1", "AUTH_CONTEXT_RESOLVE", AdminAuditEvent.Result.ALLOWED, membership);

    assertTrue(testKit.method(DurableIdentityRepositoryEntity::saveAccount).invoke(account).stateWasUpdated());
    assertTrue(testKit.method(DurableIdentityRepositoryEntity::saveAccount).invoke(linked).stateWasUpdated());
    assertTrue(testKit.method(DurableIdentityRepositoryEntity::saveProfile).invoke(profile).stateWasUpdated());
    assertTrue(testKit.method(DurableIdentityRepositoryEntity::saveSettings).invoke(settings).stateWasUpdated());
    assertTrue(testKit.method(DurableIdentityRepositoryEntity::saveTenant).invoke(tenant).stateWasUpdated());
    assertTrue(testKit.method(DurableIdentityRepositoryEntity::saveMembership).invoke(membership).stateWasUpdated());
    assertTrue(testKit.method(DurableIdentityRepositoryEntity::appendAudit).invoke(audit).stateWasUpdated());

    assertEquals(linked, testKit.method(DurableIdentityRepositoryEntity::findAccountByEmail).invoke("admin@example.com").getReply().orElseThrow());
    assertEquals(linked, testKit.method(DurableIdentityRepositoryEntity::findAccountByWorkosSubject).invoke("workos-admin").getReply().orElseThrow());
    assertEquals(profile, testKit.method(DurableIdentityRepositoryEntity::profile).invoke("admin@example.com").getReply().orElseThrow());
    assertEquals(settings, testKit.method(DurableIdentityRepositoryEntity::settings).invoke("admin@example.com").getReply().orElseThrow());
    assertEquals(tenant, testKit.method(DurableIdentityRepositoryEntity::tenant).invoke("tenant-1").getReply().orElseThrow());
    assertEquals(List.of(membership), testKit.method(DurableIdentityRepositoryEntity::membershipsByAccount).invoke("admin@example.com").getReply());
    assertEquals(membership, testKit.method(DurableIdentityRepositoryEntity::membership).invoke("membership-admin").getReply().orElseThrow());
    assertEquals(List.of(audit), testKit.method(DurableIdentityRepositoryEntity::auditEvents).invoke().getReply());
  }

  @Test
  void scopedRowsStayTenantFilteredForUserAdminDenialAndDirectoryTests() {
    var testKit = newTestKit();
    var tenantOne = new Membership("membership-1", "user-1", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN), MembershipStatus.ACTIVE, false, null);
    var tenantTwo = new Membership("membership-2", "user-2", ScopeType.TENANT, "tenant-2", null, List.of(FoundationRole.TENANT_ADMIN), MembershipStatus.ACTIVE, false, null);
    testKit.method(DurableIdentityRepositoryEntity::saveMembership).invoke(tenantOne);
    testKit.method(DurableIdentityRepositoryEntity::saveMembership).invoke(tenantTwo);

    var rows = testKit.method(DurableIdentityRepositoryEntity::membershipRows).invoke().getReply();

    assertEquals(2, rows.size());
    assertTrue(rows.stream().anyMatch(row -> row.tenantId().equals("tenant-1")));
    assertTrue(rows.stream().anyMatch(row -> row.tenantId().equals("tenant-2")));
    assertFalse(testKit.method(DurableIdentityRepositoryEntity::membershipsByAccount).invoke("missing").getReply().contains(tenantOne));
  }

  private static AdminAuditEvent audit(String id, String action, AdminAuditEvent.Result result, Membership membership) {
    return new AdminAuditEvent(
        id,
        Instant.parse("2026-05-30T00:00:00Z"),
        "corr-" + id,
        "admin@example.com",
        membership.membershipId(),
        membership.scopeType(),
        membership.tenantId(),
        membership.customerId(),
        membership.accountId(),
        membership.membershipId(),
        action,
        result,
        result.name().toLowerCase(),
        "browser-safe evidence",
        "BROWSER_SAFE");
  }
}

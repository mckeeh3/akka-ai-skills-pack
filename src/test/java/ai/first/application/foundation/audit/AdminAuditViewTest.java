package ai.first.application.foundation.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.LocalDemoIdentityRepository;
import ai.first.application.coreapp.useradmin.UserAdminService;

class AdminAuditViewTest {
  private LocalDemoIdentityRepository identityRepository;
  private AuthContextResolver resolver;
  private AdminAuditView view;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    resolver = new AuthContextResolver(identityRepository);
    view = new AdminAuditView(new UserAdminService(identityRepository, Clock.systemUTC()));
    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.putTenant(new Tenant("tenant-2", "Tenant Two", true));
    seedMember("admin@example.test", "membership-admin", "tenant-1", List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR));
    seedMember("other-admin@example.test", "membership-other-admin", "tenant-2", List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR));
  }

  @Test
  void listReturnsScopedRedactedRowsInStableReverseTimeOrder() {
    identityRepository.appendAudit(event("audit-old", "tenant-1", Instant.parse("2026-01-01T00:00:00Z"), "corr-old", "token=raw-secret bearer abc123"));
    identityRepository.appendAudit(event("audit-new", "tenant-1", Instant.parse("2026-01-02T00:00:00Z"), "corr-new", "api_key=secret-value invitationToken=raw-token"));
    identityRepository.appendAudit(event("audit-other", "tenant-2", Instant.parse("2026-01-03T00:00:00Z"), "corr-other", "other tenant"));

    var actor = resolver.resolveMe(new WorkosIdentity("workos-admin@example.test", "admin@example.test", "Admin"), "membership-admin", "corr-read");
    var rows = view.list(actor, 100, "corr-read");
    var testRows = rows.stream().filter(row -> row.actionType().equals("TEST_AUDIT")).toList();

    assertEquals("audit-new", testRows.get(0).auditEventId());
    assertEquals("audit-old", testRows.get(1).auditEventId());
    assertTrue(rows.stream().noneMatch(row -> "audit-other".equals(row.auditEventId())));
    assertTrue(rows.stream().allMatch(row -> row.tenantId().equals("tenant-1")));
    assertTrue(rows.stream().allMatch(row -> row.redactionSummary().contains("browser-safe")));
    assertFalse(rows.toString().contains("raw-secret"));
    assertFalse(rows.toString().contains("secret-value"));
    assertFalse(rows.toString().contains("raw-token"));
    assertTrue(rows.toString().contains("[REDACTED]"));
  }

  @Test
  void listDeniesActorsWithoutAuditCapability() {
    seedMember("employee@example.test", "membership-employee", "tenant-1", List.of(FoundationRole.TENANT_EMPLOYEE));
    var actor = resolver.resolveMe(new WorkosIdentity("workos-employee@example.test", "employee@example.test", "Employee"), "membership-employee", "corr-denied");

    var denied = assertThrows(AuthorizationException.class, () -> view.list(actor, 10, "corr-denied"));
    assertTrue(denied.reasonCode().contains("missing-capability:tenant.audit.read"));
  }

  private AdminAuditEvent event(String id, String tenantId, Instant occurredAt, String correlationId, String evidence) {
    return new AdminAuditEvent(id, occurredAt, correlationId, "admin@example.test", "membership-admin", ScopeType.TENANT, tenantId, null, null, null, "TEST_AUDIT", AdminAuditEvent.Result.ALLOWED, evidence, evidence, "BROWSER_SAFE");
  }

  private void seedMember(String email, String membershipId, String tenantId, List<FoundationRole> roles) {
    identityRepository.saveAccount(new Account(email, "workos-" + email, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, null, null, null));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, tenantId, null, roles, MembershipStatus.ACTIVE, false, null));
  }
}

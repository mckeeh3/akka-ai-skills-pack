package ai.first.application.coreapp.useradmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.LocalDemoIdentityRepository;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.Customer;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaasOwnerOrganizationAdminServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-06-01T10:00:00Z"), ZoneOffset.UTC);
  private LocalDemoIdentityRepository repository;
  private AuthContextResolver resolver;
  private SaasOwnerOrganizationAdminService service;
  private AuthContextResolver.ResolvedMe saasOwner;

  @BeforeEach
  void setUp() {
    repository = new LocalDemoIdentityRepository();
    resolver = new AuthContextResolver(repository);
    service = new SaasOwnerOrganizationAdminService(repository, clock);
    repository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    repository.putTenant(new Tenant("tenant-2", "Tenant Two", false));
    seedActor("owner@example.com", "membership-owner", ScopeType.SAAS_OWNER, null, null, FoundationRole.SAAS_OWNER_ADMIN);
    saasOwner = resolve("workos-owner", "owner@example.com", "membership-owner", "corr-owner");
  }

  @Test
  void saasOwnerCanListReadCreateRenameSuspendAndReactivateOrganizationsSafely() {
    var list = service.listOrganizations(saasOwner, "tenant", null, "corr-list");
    assertEquals(2, list.organizations().size());
    assertTrue(list.safeBoundaryNotice().contains("does not grant tenant/customer application-data access"));
    assertFalse(list.toString().contains("providerSecret"));

    var detail = service.readOrganization(saasOwner, "tenant-1", "corr-read");
    assertEquals("Tenant One", detail.organization().organizationName());
    assertEquals("active", detail.organization().status());
    assertTrue(detail.visibleActions().contains("suspend"));

    var created = service.createOrganization(saasOwner, "Acme Organization", "create-acme", "new customer", "corr-create");
    assertEquals("accepted", created.status());
    assertEquals("active", created.organization().organization().status());
    var createdId = created.organization().organization().organizationId();
    assertTrue(repository.tenant(createdId).orElseThrow().active());

    var replay = service.createOrganization(saasOwner, "Acme Organization", "create-acme", "replay", "corr-create-replay");
    assertEquals("no-op", replay.status());
    assertEquals(createdId, replay.organization().organization().organizationId());

    var renamed = service.renameOrganization(saasOwner, createdId, "Acme Renamed", "rename-acme", "display name correction", "corr-rename");
    assertEquals("accepted", renamed.status());
    assertEquals("Acme Renamed", repository.tenant(createdId).orElseThrow().displayName());

    var renameNoOp = service.renameOrganization(saasOwner, createdId, "Acme Renamed", "rename-acme-replay", "replay", "corr-rename-replay");
    assertEquals("no-op", renameNoOp.status());

    var suspended = service.suspendOrganization(saasOwner, createdId, "contract ended", "suspend-acme", "corr-suspend");
    assertEquals("accepted", suspended.status());
    assertEquals("suspended", suspended.organization().organization().status());
    assertFalse(repository.tenant(createdId).orElseThrow().active());

    var suspendNoOp = service.suspendOrganization(saasOwner, createdId, "contract ended", "suspend-acme-replay", "corr-suspend-replay");
    assertEquals("no-op", suspendNoOp.status());

    var reactivated = service.reactivateOrganization(saasOwner, createdId, "contract restored", "reactivate-acme", "corr-reactivate");
    assertEquals("accepted", reactivated.status());
    assertTrue(repository.tenant(createdId).orElseThrow().active());

    var actionTypes = repository.auditEvents().stream().map(AdminAuditEvent::actionType).toList();
    assertTrue(actionTypes.containsAll(List.of("ORGANIZATION_LIST", "ORGANIZATION_READ", "ORGANIZATION_CREATE", "ORGANIZATION_RENAME", "ORGANIZATION_SUSPEND", "ORGANIZATION_REACTIVATE")));
    assertTrue(repository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ORGANIZATION_CREATE") && event.result() == AdminAuditEvent.Result.NO_OP));
  }

  @Test
  void tenantCustomerAndMissingCapabilityActorsAreDeniedAndAuditedSafely() {
    seedActor("tenant-admin@example.com", "membership-tenant-admin", ScopeType.TENANT, "tenant-1", null, FoundationRole.TENANT_ADMIN);
    repository.putCustomer(new Customer("tenant-1", "customer-1", "Customer One", true));
    seedActor("customer-admin@example.com", "membership-customer-admin", ScopeType.CUSTOMER, "tenant-1", "customer-1", FoundationRole.CUSTOMER_ADMIN);
    seedActor("limited-owner@example.com", "membership-limited-owner", ScopeType.SAAS_OWNER, null, null, FoundationRole.TENANT_EMPLOYEE);

    var tenantAdmin = resolve("workos-tenant", "tenant-admin@example.com", "membership-tenant-admin", "corr-tenant-admin");
    var customerAdmin = resolve("workos-customer", "customer-admin@example.com", "membership-customer-admin", "corr-customer-admin");
    var limitedOwner = resolve("workos-limited", "limited-owner@example.com", "membership-limited-owner", "corr-limited-owner");

    var tenantDenied = assertThrows(AuthorizationException.class, () -> service.listOrganizations(tenantAdmin, null, null, "corr-deny-tenant"));
    assertEquals("scope-forbidden", tenantDenied.reasonCode());
    var customerDenied = assertThrows(AuthorizationException.class, () -> service.readOrganization(customerAdmin, "tenant-1", "corr-deny-customer"));
    assertEquals("scope-forbidden", customerDenied.reasonCode());
    var missingCapability = assertThrows(AuthorizationException.class, () -> service.listOrganizations(limitedOwner, null, null, "corr-deny-capability"));
    assertEquals("missing-capability:saas_owner.tenant.read", missingCapability.reasonCode());

    assertTrue(repository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ORGANIZATION_LIST") && event.result() == AdminAuditEvent.Result.DENIED && event.reasonCode().equals("scope-forbidden")));
    assertTrue(repository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ORGANIZATION_READ") && event.result() == AdminAuditEvent.Result.DENIED && event.reasonCode().equals("scope-forbidden")));
    assertTrue(repository.auditEvents().stream().anyMatch(event -> event.reasonCode().equals("missing-capability:saas_owner.tenant.read")));
  }

  @Test
  void mutationsRequireIdempotencyReasonsForSuspendAndHideMissingTargets() {
    var missingIdempotency = assertThrows(AuthorizationException.class, () -> service.renameOrganization(saasOwner, "tenant-1", "Tenant 1", "", "rename", "corr-missing-idem"));
    assertEquals("idempotency-key-required", missingIdempotency.reasonCode());

    var missingReason = assertThrows(AuthorizationException.class, () -> service.suspendOrganization(saasOwner, "tenant-1", " ", "suspend-key", "corr-missing-reason"));
    assertEquals("reason-required", missingReason.reasonCode());

    var hidden = assertThrows(AuthorizationException.class, () -> service.readOrganization(saasOwner, "missing-tenant", "corr-hidden"));
    assertEquals("target-not-found-or-forbidden", hidden.reasonCode());
    assertEquals(404, hidden.httpStatus());
    assertTrue(repository.auditEvents().stream().anyMatch(event -> event.actionType().equals("ORGANIZATION_READ") && event.result() == AdminAuditEvent.Result.DENIED && event.reasonCode().equals("target-not-found-or-forbidden")));
  }

  private void seedActor(String email, String membershipId, ScopeType scopeType, String tenantId, String customerId, FoundationRole role) {
    repository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "UNLINKED"));
    repository.putProfile(new UserProfile(email, email, email, null, null, null));
    repository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    repository.putMembership(new Membership(membershipId, email, scopeType, tenantId, customerId, List.of(role), MembershipStatus.ACTIVE, false, null));
  }

  private AuthContextResolver.ResolvedMe resolve(String subject, String email, String selectedContextId, String correlationId) {
    return resolver.resolveMe(new WorkosIdentity(subject, email, email), selectedContextId, correlationId);
  }
}

package ai.first.application.coreapp.useradmin;

import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.IdentityRepository;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/** Backend-authoritative SaaS Owner capability seam for Organization-facing Tenant lifecycle administration. */
public final class SaasOwnerOrganizationAdminService {
  private static final String READ_CAPABILITY = "saas_owner.tenant.read";
  private static final String MANAGE_CAPABILITY = "saas_owner.tenant.manage";
  private static final String BOUNDARY_NOTICE = "Organization administration manages the Tenant lifecycle boundary only; it does not grant tenant/customer application-data access, support access, provider secret access, or billing-derived authority.";

  private final IdentityRepository repository;
  private final Clock clock;

  public SaasOwnerOrganizationAdminService(IdentityRepository repository, Clock clock) {
    this.repository = repository;
    this.clock = clock;
  }

  public OrganizationListResult listOrganizations(AuthContextResolver.ResolvedMe actor, String query, String status, String correlationId) {
    requireRead(actor, "ORGANIZATION_LIST", correlationId);
    var normalizedQuery = normalizeOptional(query);
    var normalizedStatus = normalizeOptional(status);
    var organizations = repository.tenantRows().stream()
        .filter(tenant -> normalizedQuery.isBlank() || tenant.tenantId().toLowerCase(Locale.ROOT).contains(normalizedQuery) || tenant.displayName().toLowerCase(Locale.ROOT).contains(normalizedQuery))
        .filter(tenant -> normalizedStatus.isBlank() || lifecycleStatus(tenant).equals(normalizedStatus))
        .sorted(Comparator.comparing(Tenant::displayName).thenComparing(Tenant::tenantId))
        .map(this::summary)
        .toList();
    audit(actor, "ORGANIZATION_LIST", AdminAuditEvent.Result.ALLOWED, "browser-safe", null, correlationId);
    return new OrganizationListResult(organizations, BOUNDARY_NOTICE, trace("organization-list", correlationId), correlationId);
  }

  public OrganizationDetail readOrganization(AuthContextResolver.ResolvedMe actor, String organizationId, String correlationId) {
    requireRead(actor, "ORGANIZATION_READ", correlationId);
    var tenant = findTenantOrDeny(actor, organizationId, "ORGANIZATION_READ", correlationId);
    audit(actor, "ORGANIZATION_READ", AdminAuditEvent.Result.ALLOWED, "browser-safe", tenant.tenantId(), correlationId);
    return detail(tenant, "read", correlationId);
  }

  public OrganizationActionResult createOrganization(AuthContextResolver.ResolvedMe actor, String organizationName, String idempotencyKey, String reason, String correlationId) {
    requireIdempotency(idempotencyKey);
    var displayName = validatedName(organizationName);
    requireManage(actor, "ORGANIZATION_CREATE", correlationId);
    var tenantId = "org-" + stableSuffix(idempotencyKey);
    var existing = repository.tenant(tenantId);
    if (existing.isPresent()) {
      var tenant = existing.orElseThrow();
      if (tenant.displayName().equals(displayName)) {
        audit(actor, "ORGANIZATION_CREATE", AdminAuditEvent.Result.NO_OP, "idempotent-replay", tenant.tenantId(), correlationId);
        return action("no-op", "Organization create replay returned the existing browser-safe Organization.", tenant, "create", idempotencyKey, correlationId);
      }
      audit(actor, "ORGANIZATION_CREATE", AdminAuditEvent.Result.DENIED, "idempotency-key-conflict", tenant.tenantId(), correlationId);
      throw new AuthorizationException(409, "idempotency-key-conflict");
    }
    var duplicateVisibleName = repository.tenantRows().stream()
        .filter(tenant -> tenant.displayName().equalsIgnoreCase(displayName))
        .findFirst();
    if (duplicateVisibleName.isPresent()) {
      var tenant = duplicateVisibleName.orElseThrow();
      audit(actor, "ORGANIZATION_CREATE", AdminAuditEvent.Result.NO_OP, "duplicate-visible-organization-name", tenant.tenantId(), correlationId);
      return action("no-op", "A visible Organization already uses this name; returning its browser-safe detail without creating a second Tenant boundary.", tenant, "create", idempotencyKey, correlationId);
    }
    var tenant = repository.saveTenant(new Tenant(tenantId, displayName, true));
    audit(actor, "ORGANIZATION_CREATE", AdminAuditEvent.Result.ALLOWED, safeReason(reason, "organization-created"), tenant.tenantId(), correlationId);
    return action("accepted", "Organization created as an active Tenant lifecycle boundary.", tenant, "create", idempotencyKey, correlationId);
  }

  public OrganizationActionResult renameOrganization(AuthContextResolver.ResolvedMe actor, String organizationId, String organizationName, String idempotencyKey, String reason, String correlationId) {
    requireIdempotency(idempotencyKey);
    var displayName = validatedName(organizationName);
    requireManage(actor, "ORGANIZATION_RENAME", correlationId);
    var existing = findTenantOrDeny(actor, organizationId, "ORGANIZATION_RENAME", correlationId);
    if (existing.displayName().equals(displayName)) {
      audit(actor, "ORGANIZATION_RENAME", AdminAuditEvent.Result.NO_OP, "no-op idempotency", existing.tenantId(), correlationId);
      return action("no-op", "Requested Organization name already matches current state.", existing, "rename", idempotencyKey, correlationId);
    }
    var duplicateVisibleName = repository.tenantRows().stream()
        .filter(tenant -> !tenant.tenantId().equals(existing.tenantId()))
        .filter(tenant -> tenant.displayName().equalsIgnoreCase(displayName))
        .findFirst();
    if (duplicateVisibleName.isPresent()) {
      audit(actor, "ORGANIZATION_RENAME", AdminAuditEvent.Result.DENIED, "duplicate-visible-organization-name", existing.tenantId(), correlationId);
      return action("validation-error", "A visible Organization already uses this name; choose a distinct display name before renaming.", existing, "rename", idempotencyKey, correlationId);
    }
    var updated = repository.saveTenant(new Tenant(existing.tenantId(), displayName, existing.active()));
    audit(actor, "ORGANIZATION_RENAME", AdminAuditEvent.Result.ALLOWED, safeReason(reason, "organization-renamed"), updated.tenantId(), correlationId);
    return action("accepted", "Organization display name updated without changing Tenant isolation or support access.", updated, "rename", idempotencyKey, correlationId);
  }

  public OrganizationActionResult suspendOrganization(AuthContextResolver.ResolvedMe actor, String organizationId, String reason, String idempotencyKey, String correlationId) {
    requireIdempotency(idempotencyKey);
    if (reason == null || reason.isBlank()) {
      throw new AuthorizationException(400, "reason-required");
    }
    requireManage(actor, "ORGANIZATION_SUSPEND", correlationId);
    var existing = findTenantOrDeny(actor, organizationId, "ORGANIZATION_SUSPEND", correlationId);
    if (!existing.active()) {
      audit(actor, "ORGANIZATION_SUSPEND", AdminAuditEvent.Result.NO_OP, "already-suspended", existing.tenantId(), correlationId);
      return action("no-op", "Organization is already suspended; Tenant boundary remains unavailable.", existing, "suspend", idempotencyKey, correlationId);
    }
    var updated = repository.saveTenant(new Tenant(existing.tenantId(), existing.displayName(), false));
    audit(actor, "ORGANIZATION_SUSPEND", AdminAuditEvent.Result.ALLOWED, safeReason(reason, "organization-suspended"), updated.tenantId(), correlationId);
    return action("accepted", "Organization suspended at the Tenant lifecycle boundary without exposing tenant application data.", updated, "suspend", idempotencyKey, correlationId);
  }

  public OrganizationActionResult reactivateOrganization(AuthContextResolver.ResolvedMe actor, String organizationId, String reason, String idempotencyKey, String correlationId) {
    requireIdempotency(idempotencyKey);
    requireManage(actor, "ORGANIZATION_REACTIVATE", correlationId);
    var existing = findTenantOrDeny(actor, organizationId, "ORGANIZATION_REACTIVATE", correlationId);
    if (existing.active()) {
      audit(actor, "ORGANIZATION_REACTIVATE", AdminAuditEvent.Result.NO_OP, "already-active", existing.tenantId(), correlationId);
      return action("no-op", "Organization is already active; idempotency preserved.", existing, "reactivate", idempotencyKey, correlationId);
    }
    var updated = repository.saveTenant(new Tenant(existing.tenantId(), existing.displayName(), true));
    audit(actor, "ORGANIZATION_REACTIVATE", AdminAuditEvent.Result.ALLOWED, safeReason(reason, "organization-reactivated"), updated.tenantId(), correlationId);
    return action("accepted", "Organization reactivated at the Tenant lifecycle boundary.", updated, "reactivate", idempotencyKey, correlationId);
  }

  private void requireRead(AuthContextResolver.ResolvedMe actor, String action, String correlationId) {
    requireSaasOwner(actor, action, correlationId);
    if (!actor.selectedContext().hasCapability(READ_CAPABILITY)) {
      audit(actor, action, AdminAuditEvent.Result.DENIED, "missing-capability:" + READ_CAPABILITY, null, correlationId);
      throw new AuthorizationException(403, "missing-capability:" + READ_CAPABILITY);
    }
  }

  private void requireManage(AuthContextResolver.ResolvedMe actor, String action, String correlationId) {
    requireSaasOwner(actor, action, correlationId);
    if (!actor.selectedContext().hasCapability(MANAGE_CAPABILITY)) {
      audit(actor, action, AdminAuditEvent.Result.DENIED, "missing-capability:" + MANAGE_CAPABILITY, null, correlationId);
      throw new AuthorizationException(403, "missing-capability:" + MANAGE_CAPABILITY);
    }
  }

  private void requireSaasOwner(AuthContextResolver.ResolvedMe actor, String action, String correlationId) {
    if (actor.selectedContext().scopeType() != ScopeType.SAAS_OWNER) {
      audit(actor, action, AdminAuditEvent.Result.DENIED, "scope-forbidden", null, correlationId);
      throw new AuthorizationException(403, "scope-forbidden");
    }
  }

  private Tenant findTenantOrDeny(AuthContextResolver.ResolvedMe actor, String organizationId, String action, String correlationId) {
    var tenantId = normalizeId(organizationId);
    return repository.tenant(tenantId).orElseThrow(() -> {
      audit(actor, action, AdminAuditEvent.Result.DENIED, "target-not-found-or-forbidden", tenantId, correlationId);
      return new AuthorizationException(404, "target-not-found-or-forbidden");
    });
  }

  private void requireIdempotency(String idempotencyKey) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
      throw new AuthorizationException(400, "idempotency-key-required");
    }
  }

  private String validatedName(String organizationName) {
    var displayName = organizationName == null ? "" : organizationName.trim();
    if (displayName.length() < 2) {
      throw new AuthorizationException(400, "organization-name-too-short");
    }
    if (displayName.length() > 120) {
      throw new AuthorizationException(400, "organization-name-too-long");
    }
    return displayName;
  }

  private String normalizeId(String organizationId) {
    if (organizationId == null || organizationId.isBlank()) {
      throw new AuthorizationException(400, "organization-id-required");
    }
    return organizationId.trim();
  }

  private OrganizationActionResult action(String status, String message, Tenant tenant, String action, String idempotencyKey, String correlationId) {
    return new OrganizationActionResult(status, message, detail(tenant, action, correlationId), trace("organization-" + action, idempotencyKey), correlationId);
  }

  private OrganizationDetail detail(Tenant tenant, String action, String correlationId) {
    return new OrganizationDetail(summary(tenant), BOUNDARY_NOTICE, visibleActions(tenant), List.of(trace("organization-" + action, correlationId)), correlationId);
  }

  private OrganizationSummary summary(Tenant tenant) {
    return new OrganizationSummary(tenant.tenantId(), tenant.displayName(), lifecycleStatus(tenant), List.of(trace("organization-" + tenant.tenantId(), tenant.tenantId())));
  }

  private List<String> visibleActions(Tenant tenant) {
    return tenant.active()
        ? List.of("read", "rename", "suspend")
        : List.of("read", "rename", "reactivate");
  }

  private String lifecycleStatus(Tenant tenant) {
    return tenant.active() ? "active" : "suspended";
  }

  private String safeReason(String reason, String fallback) {
    return reason == null || reason.isBlank() ? fallback : reason.trim();
  }

  private String normalizeOptional(String value) {
    return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
  }

  private void audit(AuthContextResolver.ResolvedMe actor, String action, AdminAuditEvent.Result result, String reason, String targetTenantId, String correlationId) {
    repository.appendAudit(new AdminAuditEvent(
        UUID.randomUUID().toString(),
        Instant.now(clock),
        correlationId,
        actor.account().accountId(),
        actor.selectedContext().membershipId(),
        actor.selectedContext().scopeType(),
        targetTenantId,
        null,
        null,
        null,
        action,
        result,
        reason,
        reason,
        "BROWSER_SAFE"));
  }

  private static String trace(String prefix, String value) {
    return "trace-" + prefix + "-" + stableSuffix(value);
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "organization-admin").hashCode(), 36);
  }

  public record OrganizationSummary(String organizationId, String organizationName, String status, List<String> traceRefs) {}
  public record OrganizationDetail(OrganizationSummary organization, String safeBoundaryNotice, List<String> visibleActions, List<String> traceRefs, String correlationId) {}
  public record OrganizationListResult(List<OrganizationSummary> organizations, String safeBoundaryNotice, String traceId, String correlationId) {}
  public record OrganizationActionResult(String status, String message, OrganizationDetail organization, String traceId, String correlationId) {}
}

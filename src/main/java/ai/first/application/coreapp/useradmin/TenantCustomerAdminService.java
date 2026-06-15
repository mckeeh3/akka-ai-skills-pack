package ai.first.application.coreapp.useradmin;

import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.IdentityRepository;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.identity.Customer;
import ai.first.domain.foundation.identity.ScopeType;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/** Backend-authoritative Tenant/Organization capability seam for Customer lifecycle administration. */
public final class TenantCustomerAdminService {
  private static final String LIST_CAPABILITY = "tenant.customer.list";
  private static final String READ_CAPABILITY = "tenant.customer.read";
  private static final String CREATE_CAPABILITY = "tenant.customer.create";
  private static final String RENAME_CAPABILITY = "tenant.customer.rename";
  private static final String SUSPEND_CAPABILITY = "tenant.customer.suspend";
  private static final String REACTIVATE_CAPABILITY = "tenant.customer.reactivate";
  private static final String BOUNDARY_NOTICE = "Customer administration is scoped to the selected Organization/Tenant; sibling-customer facts, tenant application data, provider secrets, and hidden counts are omitted.";

  private final IdentityRepository repository;
  private final Clock clock;

  public TenantCustomerAdminService(IdentityRepository repository, Clock clock) {
    this.repository = repository;
    this.clock = clock;
  }

  public CustomerListResult listCustomers(AuthContextResolver.ResolvedMe actor, String query, String status, String correlationId) {
    requireTenantAdmin(actor, LIST_CAPABILITY, "CUSTOMER_LIST", correlationId);
    var normalizedQuery = normalizeOptional(query);
    var normalizedStatus = normalizeOptional(status);
    var tenantId = actor.selectedContext().tenantId();
    var customers = repository.customerRows().stream()
        .filter(customer -> tenantId.equals(customer.tenantId()))
        .filter(customer -> normalizedQuery.isBlank() || customer.customerId().toLowerCase(Locale.ROOT).contains(normalizedQuery) || customer.displayName().toLowerCase(Locale.ROOT).contains(normalizedQuery))
        .filter(customer -> normalizedStatus.isBlank() || lifecycleStatus(customer).equals(normalizedStatus))
        .sorted(Comparator.comparing(Customer::displayName).thenComparing(Customer::customerId))
        .map(this::summary)
        .toList();
    audit(actor, "CUSTOMER_LIST", AdminAuditEvent.Result.ALLOWED, "browser-safe", null, correlationId);
    return new CustomerListResult(customers, BOUNDARY_NOTICE, List.of(trace("customer-list", correlationId)), correlationId);
  }

  public CustomerDetail readCustomer(AuthContextResolver.ResolvedMe actor, String customerId, String correlationId) {
    requireTenantAdmin(actor, READ_CAPABILITY, "CUSTOMER_READ", correlationId);
    var customer = findCustomerOrDeny(actor, customerId, "CUSTOMER_READ", correlationId);
    audit(actor, "CUSTOMER_READ", AdminAuditEvent.Result.ALLOWED, "browser-safe", customer.customerId(), correlationId);
    return detail(customer, "read", correlationId);
  }

  public CustomerActionResult createCustomer(AuthContextResolver.ResolvedMe actor, String customerName, String idempotencyKey, String reason, String correlationId) {
    requireIdempotency(idempotencyKey);
    requireTenantAdmin(actor, CREATE_CAPABILITY, "CUSTOMER_CREATE", correlationId);
    var displayName = validatedName(customerName);
    var customerId = "cust-" + stableSuffix(actor.selectedContext().tenantId() + ":" + idempotencyKey);
    var existing = repository.customer(actor.selectedContext().tenantId(), customerId);
    if (existing.isPresent()) {
      var customer = existing.orElseThrow();
      if (customer.displayName().equals(displayName)) {
        audit(actor, "CUSTOMER_CREATE", AdminAuditEvent.Result.NO_OP, "idempotent-replay", customer.customerId(), correlationId);
        return action("no-op", "Customer create replay returned the existing browser-safe Customer.", customer, "create", idempotencyKey, correlationId);
      }
      audit(actor, "CUSTOMER_CREATE", AdminAuditEvent.Result.DENIED, "idempotency-key-conflict", customer.customerId(), correlationId);
      throw new AuthorizationException(409, "idempotency-key-conflict");
    }
    var customer = repository.saveCustomer(new Customer(actor.selectedContext().tenantId(), customerId, displayName, true));
    audit(actor, "CUSTOMER_CREATE", AdminAuditEvent.Result.ALLOWED, safeReason(reason, "customer-created"), customer.customerId(), correlationId);
    return action("accepted", "Customer created inside the selected Organization/Tenant boundary.", customer, "create", idempotencyKey, correlationId);
  }

  public CustomerActionResult renameCustomer(AuthContextResolver.ResolvedMe actor, String customerId, String customerName, String idempotencyKey, String reason, String correlationId) {
    requireIdempotency(idempotencyKey);
    requireTenantAdmin(actor, RENAME_CAPABILITY, "CUSTOMER_RENAME", correlationId);
    var displayName = validatedName(customerName);
    var existing = findCustomerOrDeny(actor, customerId, "CUSTOMER_RENAME", correlationId);
    if (existing.displayName().equals(displayName)) {
      audit(actor, "CUSTOMER_RENAME", AdminAuditEvent.Result.NO_OP, "no-op idempotency", existing.customerId(), correlationId);
      return action("no-op", "Requested Customer name already matches current state.", existing, "rename", idempotencyKey, correlationId);
    }
    var updated = repository.saveCustomer(new Customer(existing.tenantId(), existing.customerId(), displayName, existing.active()));
    audit(actor, "CUSTOMER_RENAME", AdminAuditEvent.Result.ALLOWED, safeReason(reason, "customer-renamed"), updated.customerId(), correlationId);
    return action("accepted", "Customer display name updated without exposing sibling Customer facts.", updated, "rename", idempotencyKey, correlationId);
  }

  public CustomerActionResult suspendCustomer(AuthContextResolver.ResolvedMe actor, String customerId, String reason, String idempotencyKey, String correlationId) {
    requireIdempotency(idempotencyKey);
    if (reason == null || reason.isBlank()) throw new AuthorizationException(400, "reason-required");
    requireTenantAdmin(actor, SUSPEND_CAPABILITY, "CUSTOMER_SUSPEND", correlationId);
    var existing = findCustomerOrDeny(actor, customerId, "CUSTOMER_SUSPEND", correlationId);
    if (!existing.active()) {
      audit(actor, "CUSTOMER_SUSPEND", AdminAuditEvent.Result.NO_OP, "already-suspended", existing.customerId(), correlationId);
      return action("no-op", "Customer is already suspended; idempotency preserved.", existing, "suspend", idempotencyKey, correlationId);
    }
    var updated = repository.saveCustomer(new Customer(existing.tenantId(), existing.customerId(), existing.displayName(), false));
    audit(actor, "CUSTOMER_SUSPEND", AdminAuditEvent.Result.ALLOWED, safeReason(reason, "customer-suspended"), updated.customerId(), correlationId);
    return action("accepted", "Customer suspended within the selected Organization/Tenant boundary.", updated, "suspend", idempotencyKey, correlationId);
  }

  public CustomerActionResult reactivateCustomer(AuthContextResolver.ResolvedMe actor, String customerId, String reason, String idempotencyKey, String correlationId) {
    requireIdempotency(idempotencyKey);
    requireTenantAdmin(actor, REACTIVATE_CAPABILITY, "CUSTOMER_REACTIVATE", correlationId);
    var existing = findCustomerOrDeny(actor, customerId, "CUSTOMER_REACTIVATE", correlationId);
    if (existing.active()) {
      audit(actor, "CUSTOMER_REACTIVATE", AdminAuditEvent.Result.NO_OP, "already-active", existing.customerId(), correlationId);
      return action("no-op", "Customer is already active; idempotency preserved.", existing, "reactivate", idempotencyKey, correlationId);
    }
    var updated = repository.saveCustomer(new Customer(existing.tenantId(), existing.customerId(), existing.displayName(), true));
    audit(actor, "CUSTOMER_REACTIVATE", AdminAuditEvent.Result.ALLOWED, safeReason(reason, "customer-reactivated"), updated.customerId(), correlationId);
    return action("accepted", "Customer reactivated within the selected Organization/Tenant boundary.", updated, "reactivate", idempotencyKey, correlationId);
  }

  private void requireTenantAdmin(AuthContextResolver.ResolvedMe actor, String capability, String action, String correlationId) {
    if (actor.selectedContext().scopeType() != ScopeType.TENANT) {
      audit(actor, action, AdminAuditEvent.Result.DENIED, "scope-forbidden", null, correlationId);
      throw new AuthorizationException(403, "scope-forbidden");
    }
    if (!actor.selectedContext().hasCapability(capability)) {
      audit(actor, action, AdminAuditEvent.Result.DENIED, "missing-capability:" + capability, null, correlationId);
      throw new AuthorizationException(403, "missing-capability:" + capability);
    }
  }

  private Customer findCustomerOrDeny(AuthContextResolver.ResolvedMe actor, String customerId, String action, String correlationId) {
    var normalizedCustomerId = normalizeId(customerId);
    return repository.customer(actor.selectedContext().tenantId(), normalizedCustomerId).orElseThrow(() -> {
      audit(actor, action, AdminAuditEvent.Result.DENIED, "target-not-found-or-forbidden", normalizedCustomerId, correlationId);
      return new AuthorizationException(404, "target-not-found-or-forbidden");
    });
  }

  private void requireIdempotency(String idempotencyKey) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) throw new AuthorizationException(400, "idempotency-key-required");
  }

  private String validatedName(String customerName) {
    var displayName = customerName == null ? "" : customerName.trim();
    if (displayName.length() < 2) throw new AuthorizationException(400, "customer-name-too-short");
    if (displayName.length() > 120) throw new AuthorizationException(400, "customer-name-too-long");
    return displayName;
  }

  private String normalizeId(String customerId) {
    if (customerId == null || customerId.isBlank()) throw new AuthorizationException(400, "customer-id-required");
    return customerId.trim();
  }

  private CustomerActionResult action(String status, String message, Customer customer, String action, String idempotencyKey, String correlationId) {
    return new CustomerActionResult(status, message, detail(customer, action, correlationId), List.of(trace("customer-" + action, idempotencyKey)), correlationId);
  }

  private CustomerDetail detail(Customer customer, String action, String correlationId) {
    return new CustomerDetail(summary(customer), BOUNDARY_NOTICE, visibleActions(customer), List.of(), List.of(trace("customer-" + action, correlationId)), correlationId);
  }

  private CustomerSummary summary(Customer customer) {
    return new CustomerSummary(customer.customerId(), customer.displayName(), lifecycleStatus(customer), List.of(trace("customer-" + customer.customerId(), customer.customerId())));
  }

  private List<String> visibleActions(Customer customer) {
    return customer.active() ? List.of("read", "rename", "suspend") : List.of("read", "rename", "reactivate");
  }

  private String lifecycleStatus(Customer customer) {
    return customer.active() ? "active" : "suspended";
  }

  private String normalizeOptional(String value) {
    return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
  }

  private String safeReason(String reason, String fallback) {
    return reason == null || reason.isBlank() ? fallback : reason.trim();
  }

  private void audit(AuthContextResolver.ResolvedMe actor, String action, AdminAuditEvent.Result result, String reason, String targetCustomerId, String correlationId) {
    repository.appendAudit(new AdminAuditEvent(
        UUID.randomUUID().toString(),
        Instant.now(clock),
        correlationId,
        actor.account().accountId(),
        actor.selectedContext().membershipId(),
        actor.selectedContext().scopeType(),
        actor.selectedContext().tenantId(),
        targetCustomerId,
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
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "customer-admin").hashCode(), 36);
  }

  public record CustomerSummary(String customerId, String customerName, String status, List<String> traceRefs) {}
  public record CustomerDetail(CustomerSummary customer, String safeBoundaryNotice, List<String> visibleActions, List<Object> recentAuditEvents, List<String> traceRefs, String correlationId) {}
  public record CustomerListResult(List<CustomerSummary> customers, String safeBoundaryNotice, List<String> traceRefs, String correlationId) {}
  public record CustomerActionResult(String status, String message, CustomerDetail customer, List<String> traceRefs, String correlationId) {}
}

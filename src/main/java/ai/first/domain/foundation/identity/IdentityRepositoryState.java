package ai.first.domain.foundation.identity;

import ai.first.domain.foundation.audit.AdminAuditEvent;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Durable current-state identity foundation for local authorization and /api/me. */
public record IdentityRepositoryState(
    Map<String, Account> accounts,
    Map<String, UserProfile> profiles,
    Map<String, UserSettings> settings,
    Map<String, Membership> memberships,
    Map<String, Tenant> tenants,
    Map<String, Customer> customers,
    List<AdminAuditEvent> auditEvents) {

  public IdentityRepositoryState {
    accounts = Map.copyOf(accounts == null ? Map.of() : accounts);
    profiles = Map.copyOf(profiles == null ? Map.of() : profiles);
    settings = Map.copyOf(settings == null ? Map.of() : settings);
    memberships = Map.copyOf(memberships == null ? Map.of() : memberships);
    tenants = Map.copyOf(tenants == null ? Map.of() : tenants);
    customers = Map.copyOf(customers == null ? Map.of() : customers);
    auditEvents = List.copyOf(auditEvents == null ? List.of() : auditEvents);
  }

  public static IdentityRepositoryState empty() {
    return new IdentityRepositoryState(Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), List.of());
  }

  public Optional<Account> findAccountByWorkosSubject(String workosUserId) {
    return accounts.values().stream().filter(account -> workosUserId != null && workosUserId.equals(account.workosUserId())).findFirst();
  }

  public Optional<Account> findAccountByEmail(String normalizedEmail) {
    return accounts.values().stream().filter(account -> normalizedEmail != null && normalizedEmail.equals(account.normalizedEmail())).findFirst();
  }

  public IdentityRepositoryState saveAccount(Account account) {
    var updated = new java.util.LinkedHashMap<>(accounts);
    updated.put(account.accountId(), account);
    return new IdentityRepositoryState(updated, profiles, settings, memberships, tenants, customers, auditEvents);
  }

  public UserProfile profile(String accountId) {
    return profiles.get(accountId);
  }

  public IdentityRepositoryState saveProfile(UserProfile profile) {
    var updated = new java.util.LinkedHashMap<>(profiles);
    updated.put(profile.accountId(), profile);
    return new IdentityRepositoryState(accounts, updated, settings, memberships, tenants, customers, auditEvents);
  }

  public UserSettings settings(String accountId) {
    return settings.get(accountId);
  }

  public IdentityRepositoryState saveSettings(UserSettings userSettings) {
    var updated = new java.util.LinkedHashMap<>(settings);
    updated.put(userSettings.accountId(), userSettings);
    return new IdentityRepositoryState(accounts, profiles, updated, memberships, tenants, customers, auditEvents);
  }

  public List<Membership> membershipsByAccount(String accountId) {
    return memberships.values().stream().filter(membership -> accountId != null && accountId.equals(membership.accountId())).toList();
  }

  public Optional<Membership> membership(String membershipId) {
    return Optional.ofNullable(memberships.get(membershipId));
  }

  public List<Membership> membershipRows() {
    return memberships.values().stream().toList();
  }

  public IdentityRepositoryState saveMembership(Membership membership) {
    var updated = new java.util.LinkedHashMap<>(memberships);
    updated.put(membership.membershipId(), membership);
    return new IdentityRepositoryState(accounts, profiles, settings, updated, tenants, customers, auditEvents);
  }

  public Optional<Tenant> tenant(String tenantId) {
    return Optional.ofNullable(tenants.get(tenantId));
  }

  public IdentityRepositoryState saveTenant(Tenant tenant) {
    var updated = new java.util.LinkedHashMap<>(tenants);
    updated.put(tenant.tenantId(), tenant);
    return new IdentityRepositoryState(accounts, profiles, settings, memberships, updated, customers, auditEvents);
  }

  public Optional<Customer> customer(String tenantId, String customerId) {
    return Optional.ofNullable(customers.get(tenantId + ":" + customerId));
  }

  public IdentityRepositoryState saveCustomer(Customer customer) {
    var updated = new java.util.LinkedHashMap<>(customers);
    updated.put(customer.tenantId() + ":" + customer.customerId(), customer);
    return new IdentityRepositoryState(accounts, profiles, settings, memberships, tenants, updated, auditEvents);
  }

  public IdentityRepositoryState appendAudit(AdminAuditEvent event) {
    var updated = new java.util.ArrayList<>(auditEvents);
    updated.add(event);
    return new IdentityRepositoryState(accounts, profiles, settings, memberships, tenants, customers, updated);
  }
}

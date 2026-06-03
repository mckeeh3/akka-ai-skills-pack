package ai.first.application.security;

import ai.first.domain.security.Account;
import ai.first.domain.security.AdminAuditEvent;
import ai.first.domain.security.Customer;
import ai.first.domain.security.Membership;
import ai.first.domain.security.Tenant;
import ai.first.domain.security.UserProfile;
import ai.first.domain.security.UserSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Test/local adapter for the starter template. */
public final class LocalDemoIdentityRepository implements IdentityRepository {
  private final Map<String, Account> accounts = new ConcurrentHashMap<>();
  private final Map<String, UserProfile> profiles = new ConcurrentHashMap<>();
  private final Map<String, UserSettings> settings = new ConcurrentHashMap<>();
  private final Map<String, Membership> memberships = new ConcurrentHashMap<>();
  private final Map<String, Tenant> tenants = new ConcurrentHashMap<>();
  private final Map<String, Customer> customers = new ConcurrentHashMap<>();
  private final List<AdminAuditEvent> auditEvents = new ArrayList<>();

  @Override
  public Optional<Account> findAccountByWorkosSubject(String workosUserId) {
    return accounts.values().stream().filter(account -> workosUserId.equals(account.workosUserId())).findFirst();
  }

  @Override
  public Optional<Account> findAccountByEmail(String normalizedEmail) {
    return Optional.ofNullable(accounts.get(normalizedEmail));
  }

  @Override
  public Account saveAccount(Account account) {
    accounts.put(account.accountId(), account);
    return account;
  }

  @Override
  public UserProfile profile(String accountId) {
    return profiles.get(accountId);
  }

  @Override
  public UserProfile saveProfile(UserProfile profile) {
    profiles.put(profile.accountId(), profile);
    return profile;
  }

  @Override
  public UserSettings settings(String accountId) {
    return settings.get(accountId);
  }

  @Override
  public UserSettings saveSettings(UserSettings userSettings) {
    settings.put(userSettings.accountId(), userSettings);
    return userSettings;
  }

  @Override
  public List<Membership> membershipsByAccount(String accountId) {
    return memberships.values().stream().filter(membership -> accountId.equals(membership.accountId())).toList();
  }

  @Override
  public Optional<Tenant> tenant(String tenantId) {
    return Optional.ofNullable(tenants.get(tenantId));
  }

  @Override
  public Optional<Customer> customer(String tenantId, String customerId) {
    return Optional.ofNullable(customers.get(tenantId + ":" + customerId));
  }

  @Override
  public synchronized void appendAudit(AdminAuditEvent event) {
    auditEvents.add(event);
  }

  @Override
  public synchronized List<AdminAuditEvent> auditEvents() {
    return List.copyOf(auditEvents);
  }

  public void putProfile(UserProfile profile) {
    saveProfile(profile);
  }

  public void putSettings(UserSettings userSettings) {
    saveSettings(userSettings);
  }

  @Override
  public Optional<Membership> membership(String membershipId) {
    return Optional.ofNullable(memberships.get(membershipId));
  }

  public Optional<Membership> findMembership(String membershipId) {
    return membership(membershipId);
  }

  @Override
  public List<Membership> membershipRows() {
    return memberships.values().stream().toList();
  }

  @Override
  public Membership saveMembership(Membership membership) {
    memberships.put(membership.membershipId(), membership);
    return membership;
  }

  public void putMembership(Membership membership) {
    saveMembership(membership);
  }

  @Override
  public Tenant saveTenant(Tenant tenant) {
    tenants.put(tenant.tenantId(), tenant);
    return tenant;
  }

  public void putTenant(Tenant tenant) {
    saveTenant(tenant);
  }

  @Override
  public Customer saveCustomer(Customer customer) {
    customers.put(customer.tenantId() + ":" + customer.customerId(), customer);
    return customer;
  }

  public void putCustomer(Customer customer) {
    saveCustomer(customer);
  }
}

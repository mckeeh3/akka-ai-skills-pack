package ai.first.application.security;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.identity.Customer;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import java.util.List;
import java.util.Optional;

/** Fail-closed normal-runtime identity port until an Akka-backed identity repository is bound. */
public final class FailClosedIdentityRepository implements IdentityRepository {
  private IllegalStateException unavailable() {
    return FailClosedFoundationRuntime.unavailable("IdentityRepository");
  }

  @Override public Optional<Account> findAccountByWorkosSubject(String workosUserId) { throw unavailable(); }
  @Override public Optional<Account> findAccountByEmail(String normalizedEmail) { throw unavailable(); }
  @Override public Account saveAccount(Account account) { throw unavailable(); }
  @Override public UserProfile profile(String accountId) { throw unavailable(); }
  @Override public UserProfile saveProfile(UserProfile profile) { throw unavailable(); }
  @Override public UserSettings settings(String accountId) { throw unavailable(); }
  @Override public UserSettings saveSettings(UserSettings settings) { throw unavailable(); }
  @Override public List<Membership> membershipsByAccount(String accountId) { throw unavailable(); }
  @Override public Optional<Membership> membership(String membershipId) { throw unavailable(); }
  @Override public List<Membership> membershipRows() { throw unavailable(); }
  @Override public Membership saveMembership(Membership membership) { throw unavailable(); }
  @Override public Optional<Tenant> tenant(String tenantId) { throw unavailable(); }
  @Override public Tenant saveTenant(Tenant tenant) { throw unavailable(); }
  @Override public Optional<Customer> customer(String tenantId, String customerId) { throw unavailable(); }
  @Override public Customer saveCustomer(Customer customer) { throw unavailable(); }
  @Override public void appendAudit(AdminAuditEvent event) { throw unavailable(); }
  @Override public List<AdminAuditEvent> auditEvents() { throw unavailable(); }
}

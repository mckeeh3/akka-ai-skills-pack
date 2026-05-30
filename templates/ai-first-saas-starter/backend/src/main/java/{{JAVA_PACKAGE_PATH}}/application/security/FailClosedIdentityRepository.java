package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import {{JAVA_BASE_PACKAGE}}.domain.security.Customer;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
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
  @Override public Optional<Tenant> tenant(String tenantId) { throw unavailable(); }
  @Override public Optional<Customer> customer(String tenantId, String customerId) { throw unavailable(); }
  @Override public void appendAudit(AdminAuditEvent event) { throw unavailable(); }
  @Override public List<AdminAuditEvent> auditEvents() { throw unavailable(); }
}

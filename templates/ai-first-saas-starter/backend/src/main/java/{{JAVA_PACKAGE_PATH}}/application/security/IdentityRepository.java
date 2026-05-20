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

/** Persistence port for the starter identity foundation. Replace the in-memory adapter with Akka entities/views in production slices. */
public interface IdentityRepository {
  Optional<Account> findAccountByWorkosSubject(String workosUserId);

  Optional<Account> findAccountByEmail(String normalizedEmail);

  Account saveAccount(Account account);

  UserProfile profile(String accountId);

  UserSettings settings(String accountId);

  List<Membership> membershipsByAccount(String accountId);

  Optional<Tenant> tenant(String tenantId);

  Optional<Customer> customer(String tenantId, String customerId);

  void appendAudit(AdminAuditEvent event);

  List<AdminAuditEvent> auditEvents();
}

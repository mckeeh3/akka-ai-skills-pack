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

/** Persistence port for the starter identity foundation. Normal runtime binds this port to Akka durable components. */
public interface IdentityRepository {
  Optional<Account> findAccountByWorkosSubject(String workosUserId);

  Optional<Account> findAccountByEmail(String normalizedEmail);

  Account saveAccount(Account account);

  UserProfile profile(String accountId);

  UserProfile saveProfile(UserProfile profile);

  UserSettings settings(String accountId);

  UserSettings saveSettings(UserSettings settings);

  List<Membership> membershipsByAccount(String accountId);

  Optional<Membership> membership(String membershipId);

  default Optional<Membership> findMembership(String membershipId) {
    return membership(membershipId);
  }

  List<Membership> membershipRows();

  Membership saveMembership(Membership membership);

  Optional<Tenant> tenant(String tenantId);

  Tenant saveTenant(Tenant tenant);

  Optional<Customer> customer(String tenantId, String customerId);

  Customer saveCustomer(Customer customer);

  void appendAudit(AdminAuditEvent event);

  List<AdminAuditEvent> auditEvents();
}

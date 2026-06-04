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

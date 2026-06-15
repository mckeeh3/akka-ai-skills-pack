package ai.first.application.foundation.identity;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.identity.Customer;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.IdentityRecoveryCase;
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

  void deleteAccount(String accountId);

  UserProfile profile(String accountId);

  UserProfile saveProfile(UserProfile profile);

  void deleteProfile(String accountId);

  UserSettings settings(String accountId);

  UserSettings saveSettings(UserSettings settings);

  void deleteSettings(String accountId);

  List<Membership> membershipsByAccount(String accountId);

  Optional<Membership> membership(String membershipId);

  default Optional<Membership> findMembership(String membershipId) {
    return membership(membershipId);
  }

  List<Membership> membershipRows();

  Membership saveMembership(Membership membership);

  void deleteMembership(String membershipId);

  Optional<Tenant> tenant(String tenantId);

  List<Tenant> tenantRows();

  Tenant saveTenant(Tenant tenant);

  Optional<Customer> customer(String tenantId, String customerId);

  default List<Customer> customerRows() {
    return List.of();
  }

  Customer saveCustomer(Customer customer);

  void appendAudit(AdminAuditEvent event);

  List<AdminAuditEvent> auditEvents();

  default Optional<IdentityRecoveryCase> identityRecovery(String recoveryId) {
    return Optional.empty();
  }

  default List<IdentityRecoveryCase> identityRecoveries() {
    return List.of();
  }

  default IdentityRecoveryCase saveIdentityRecovery(IdentityRecoveryCase recoveryCase) {
    throw new IllegalStateException("identity recovery repository is not configured");
  }
}

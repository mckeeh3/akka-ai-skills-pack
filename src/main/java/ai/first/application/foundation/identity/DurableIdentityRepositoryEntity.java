package ai.first.application.foundation.identity;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.identity.Customer;
import ai.first.domain.foundation.identity.IdentityRepositoryState;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import java.util.List;
import java.util.Optional;

/** Akka durable identity repository for account/profile/settings/membership current state and admin audit rows. */
@Component(id = "starter-identity-repository")
public class DurableIdentityRepositoryEntity extends KeyValueEntity<IdentityRepositoryState> {
  public static final String ENTITY_ID = "starter-identity-repository";

  @Override
  public IdentityRepositoryState emptyState() {
    return IdentityRepositoryState.empty();
  }

  public ReadOnlyEffect<Optional<Account>> findAccountByWorkosSubject(String workosUserId) {
    return effects().reply(currentState().findAccountByWorkosSubject(workosUserId));
  }

  public ReadOnlyEffect<Optional<Account>> findAccountByEmail(String normalizedEmail) {
    return effects().reply(currentState().findAccountByEmail(normalizedEmail));
  }

  public Effect<Account> saveAccount(Account account) {
    return effects().updateState(currentState().saveAccount(account)).thenReply(() -> account);
  }

  public Effect<String> deleteAccount(String accountId) {
    return effects().updateState(currentState().deleteAccount(accountId)).thenReply(() -> accountId);
  }

  public ReadOnlyEffect<Optional<UserProfile>> profile(String accountId) {
    return effects().reply(Optional.ofNullable(currentState().profile(accountId)));
  }

  public Effect<UserProfile> saveProfile(UserProfile profile) {
    return effects().updateState(currentState().saveProfile(profile)).thenReply(() -> profile);
  }

  public Effect<String> deleteProfile(String accountId) {
    return effects().updateState(currentState().deleteProfile(accountId)).thenReply(() -> accountId);
  }

  public ReadOnlyEffect<Optional<UserSettings>> settings(String accountId) {
    return effects().reply(Optional.ofNullable(currentState().settings(accountId)));
  }

  public Effect<UserSettings> saveSettings(UserSettings settings) {
    return effects().updateState(currentState().saveSettings(settings)).thenReply(() -> settings);
  }

  public Effect<String> deleteSettings(String accountId) {
    return effects().updateState(currentState().deleteSettings(accountId)).thenReply(() -> accountId);
  }

  public ReadOnlyEffect<List<Membership>> membershipsByAccount(String accountId) {
    return effects().reply(currentState().membershipsByAccount(accountId));
  }

  public ReadOnlyEffect<Optional<Membership>> membership(String membershipId) {
    return effects().reply(currentState().membership(membershipId));
  }

  public ReadOnlyEffect<List<Membership>> membershipRows() {
    return effects().reply(currentState().membershipRows());
  }

  public Effect<Membership> saveMembership(Membership membership) {
    return effects().updateState(currentState().saveMembership(membership)).thenReply(() -> membership);
  }

  public Effect<String> deleteMembership(String membershipId) {
    return effects().updateState(currentState().deleteMembership(membershipId)).thenReply(() -> membershipId);
  }

  public ReadOnlyEffect<Optional<Tenant>> tenant(String tenantId) {
    return effects().reply(currentState().tenant(tenantId));
  }

  public Effect<Tenant> saveTenant(Tenant tenant) {
    return effects().updateState(currentState().saveTenant(tenant)).thenReply(() -> tenant);
  }

  public ReadOnlyEffect<Optional<Customer>> customer(CustomerKey key) {
    return effects().reply(currentState().customer(key.tenantId(), key.customerId()));
  }

  public Effect<Customer> saveCustomer(Customer customer) {
    return effects().updateState(currentState().saveCustomer(customer)).thenReply(() -> customer);
  }

  public Effect<String> appendAudit(AdminAuditEvent event) {
    return effects().updateState(currentState().appendAudit(event)).thenReply(event::auditEventId);
  }

  public ReadOnlyEffect<List<AdminAuditEvent>> auditEvents() {
    return effects().reply(currentState().auditEvents());
  }

  public record CustomerKey(String tenantId, String customerId) {}
}

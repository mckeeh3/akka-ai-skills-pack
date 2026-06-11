package ai.first.application.foundation.identity;

import akka.javasdk.client.ComponentClient;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.identity.Customer;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import java.util.List;
import java.util.Optional;

/** Akka-backed adapter for the starter local authorization repository port. */
public final class AkkaIdentityRepository implements IdentityRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaIdentityRepository(ComponentClient componentClient) {
    this(componentClient, DurableIdentityRepositoryEntity.ENTITY_ID);
  }

  public AkkaIdentityRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  @Override
  public Optional<Account> findAccountByWorkosSubject(String workosUserId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::findAccountByWorkosSubject).invoke(workosUserId);
  }

  @Override
  public Optional<Account> findAccountByEmail(String normalizedEmail) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::findAccountByEmail).invoke(normalizedEmail);
  }

  @Override
  public Account saveAccount(Account account) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::saveAccount).invoke(account);
  }

  @Override
  public void deleteAccount(String accountId) {
    componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::deleteAccount).invoke(accountId);
  }

  @Override
  public UserProfile profile(String accountId) {
    Optional<UserProfile> profile = componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::profile).invoke(accountId);
    return profile.orElse(null);
  }

  @Override
  public UserProfile saveProfile(UserProfile profile) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::saveProfile).invoke(profile);
  }

  @Override
  public void deleteProfile(String accountId) {
    componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::deleteProfile).invoke(accountId);
  }

  @Override
  public UserSettings settings(String accountId) {
    Optional<UserSettings> settings = componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::settings).invoke(accountId);
    return settings.orElse(null);
  }

  @Override
  public UserSettings saveSettings(UserSettings settings) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::saveSettings).invoke(settings);
  }

  @Override
  public void deleteSettings(String accountId) {
    componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::deleteSettings).invoke(accountId);
  }

  @Override
  public List<Membership> membershipsByAccount(String accountId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::membershipsByAccount).invoke(accountId);
  }

  @Override
  public Optional<Membership> membership(String membershipId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::membership).invoke(membershipId);
  }

  @Override
  public List<Membership> membershipRows() {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::membershipRows).invoke();
  }

  @Override
  public Membership saveMembership(Membership membership) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::saveMembership).invoke(membership);
  }

  @Override
  public void deleteMembership(String membershipId) {
    componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::deleteMembership).invoke(membershipId);
  }

  @Override
  public Optional<Tenant> tenant(String tenantId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::tenant).invoke(tenantId);
  }

  @Override
  public List<Tenant> tenantRows() {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::tenantRows).invoke();
  }

  @Override
  public Tenant saveTenant(Tenant tenant) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::saveTenant).invoke(tenant);
  }

  @Override
  public Optional<Customer> customer(String tenantId, String customerId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::customer).invoke(new DurableIdentityRepositoryEntity.CustomerKey(tenantId, customerId));
  }

  @Override
  public Customer saveCustomer(Customer customer) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::saveCustomer).invoke(customer);
  }

  @Override
  public void appendAudit(AdminAuditEvent event) {
    componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::appendAudit).invoke(event);
  }

  @Override
  public List<AdminAuditEvent> auditEvents() {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::auditEvents).invoke();
  }
}

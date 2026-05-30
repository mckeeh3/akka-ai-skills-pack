package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import {{JAVA_BASE_PACKAGE}}.domain.security.Customer;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
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
  public UserProfile profile(String accountId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::profile).invoke(accountId);
  }

  @Override
  public UserProfile saveProfile(UserProfile profile) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::saveProfile).invoke(profile);
  }

  @Override
  public UserSettings settings(String accountId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::settings).invoke(accountId);
  }

  @Override
  public UserSettings saveSettings(UserSettings settings) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::saveSettings).invoke(settings);
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
  public Optional<Tenant> tenant(String tenantId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableIdentityRepositoryEntity::tenant).invoke(tenantId);
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

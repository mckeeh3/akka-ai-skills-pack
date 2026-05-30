package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.Invitation;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import java.util.List;
import java.util.Optional;

/**
 * Akka-backed adapter for {@link InvitationRepository}.
 *
 * <p>This preserves the synchronous starter repository port while moving state into
 * {@link DurableInvitationRepositoryEntity}. Normal endpoint wiring binds this adapter as soon as a
 * {@link ComponentClient} is available; the local/demo adapter is retained only for explicit
 * local/demo or test execution.
 */
public final class AkkaInvitationRepository implements InvitationRepository {
  private final ComponentClient componentClient;
  private final String entityId;

  public AkkaInvitationRepository(ComponentClient componentClient) {
    this(componentClient, DurableInvitationRepositoryEntity.ENTITY_ID);
  }

  public AkkaInvitationRepository(ComponentClient componentClient, String entityId) {
    this.componentClient = componentClient;
    this.entityId = entityId;
  }

  @Override
  public Optional<Invitation> invitation(String invitationId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableInvitationRepositoryEntity::invitation).invoke(invitationId);
  }

  @Override
  public Optional<Invitation> findByIdempotencyKey(String idempotencyKey) {
    return componentClient.forKeyValueEntity(entityId).method(DurableInvitationRepositoryEntity::findByIdempotencyKey).invoke(idempotencyKey);
  }

  @Override
  public Optional<Invitation> findActiveDuplicate(String normalizedEmail, ScopeType scopeType, String tenantId, String customerId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableInvitationRepositoryEntity::findActiveDuplicate)
        .invoke(new DurableInvitationRepositoryEntity.FindActiveDuplicateQuery(normalizedEmail, scopeType, tenantId, customerId));
  }

  @Override
  public Optional<Invitation> findByAcceptanceContext(String acceptanceContextId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableInvitationRepositoryEntity::findByAcceptanceContext).invoke(acceptanceContextId);
  }

  @Override
  public Optional<Invitation> findByTokenHash(String tokenHash) {
    return componentClient.forKeyValueEntity(entityId).method(DurableInvitationRepositoryEntity::findByTokenHash).invoke(tokenHash);
  }

  @Override
  public Invitation saveInvitation(Invitation invitation) {
    return componentClient.forKeyValueEntity(entityId).method(DurableInvitationRepositoryEntity::saveInvitation).invoke(invitation);
  }

  @Override
  public void enqueueEmail(EmailOutboxMessage message) {
    componentClient.forKeyValueEntity(entityId).method(DurableInvitationRepositoryEntity::enqueueEmail).invoke(message);
  }

  @Override
  public Optional<EmailOutboxMessage> email(String outboxId) {
    return componentClient.forKeyValueEntity(entityId).method(DurableInvitationRepositoryEntity::email).invoke(outboxId);
  }

  @Override
  public List<EmailOutboxMessage> queuedEmails() {
    return componentClient.forKeyValueEntity(entityId).method(DurableInvitationRepositoryEntity::queuedEmails).invoke();
  }

  @Override
  public List<Invitation> invitations() {
    return componentClient.forKeyValueEntity(entityId).method(DurableInvitationRepositoryEntity::invitations).invoke();
  }
}

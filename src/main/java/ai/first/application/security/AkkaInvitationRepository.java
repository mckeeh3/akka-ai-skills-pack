package ai.first.application.security;

import akka.javasdk.client.ComponentClient;
import ai.first.domain.security.EmailOutboxMessage;
import ai.first.domain.security.Invitation;
import ai.first.domain.security.InvitationLifecycleFact;
import ai.first.domain.security.ScopeType;
import java.util.List;
import java.util.Optional;

/**
 * Akka-backed adapter for {@link InvitationRepository}.
 *
 * <p>This preserves the synchronous starter repository port while moving state into
 * {@link DurableInvitationRepositoryEntity}. Normal endpoint wiring binds this adapter as soon as a
 * {@link ComponentClient} is available; test doubles live only in test source.
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
    var saved = componentClient.forKeyValueEntity(entityId).method(DurableInvitationRepositoryEntity::saveInvitation).invoke(invitation);
    componentClient.forEventSourcedEntity(InvitationLifecycleHistoryEntity.entityId(invitation.invitationId()))
        .method(InvitationLifecycleHistoryEntity::recordSnapshot)
        .invoke(invitation);
    return saved;
  }

  @Override
  public void enqueueEmail(EmailOutboxMessage message) {
    componentClient.forKeyValueEntity(entityId).method(DurableInvitationRepositoryEntity::enqueueEmail).invoke(message);
    componentClient.forEventSourcedEntity(InvitationLifecycleHistoryEntity.entityId(message.invitationId()))
        .method(InvitationLifecycleHistoryEntity::recordEmailQueued)
        .invoke(message);
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

  @Override
  public Optional<InvitationLifecycleFact> recordLifecycleDecision(InvitationLifecycleHistoryEntity.DecisionFact decision) {
    return Optional.of(componentClient.forEventSourcedEntity(InvitationLifecycleHistoryEntity.entityId(decision.invitation().invitationId()))
        .method(InvitationLifecycleHistoryEntity::recordDecision)
        .invoke(decision));
  }

  @Override
  public List<InvitationLifecycleFact> lifecycleHistory(String invitationId) {
    return componentClient.forEventSourcedEntity(InvitationLifecycleHistoryEntity.entityId(invitationId))
        .method(InvitationLifecycleHistoryEntity::history)
        .invoke();
  }
}

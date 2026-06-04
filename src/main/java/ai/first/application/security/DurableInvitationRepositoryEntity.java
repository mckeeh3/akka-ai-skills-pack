package ai.first.application.security;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import ai.first.domain.foundation.email.EmailOutboxMessage;
import ai.first.domain.foundation.invitation.Invitation;
import ai.first.domain.foundation.invitation.InvitationRepositoryState;
import ai.first.domain.foundation.identity.ScopeType;
import java.util.List;
import java.util.Optional;

/**
 * Durable Akka component seam for the starter invitation repository port.
 *
 * <p>Normal endpoint wiring binds {@link AkkaInvitationRepository} to this entity without changing
 * {@link InvitationService} or the browser API contracts. This Key Value Entity stores current
 * invitation/outbox state; a later event-sourced invitation lifecycle slice can preserve full
 * historical facts and project the same read methods through views. Test-only substitutes live under
 * test source, not normal runtime wiring.
 */
@Component(id = "starter-invitation-repository")
public class DurableInvitationRepositoryEntity extends KeyValueEntity<InvitationRepositoryState> {
  public static final String ENTITY_ID = "starter-invitation-repository";

  @Override
  public InvitationRepositoryState emptyState() {
    return InvitationRepositoryState.empty();
  }

  public ReadOnlyEffect<Optional<Invitation>> invitation(String invitationId) {
    return effects().reply(currentState().invitation(invitationId));
  }

  public ReadOnlyEffect<Optional<Invitation>> findByIdempotencyKey(String idempotencyKey) {
    return effects().reply(currentState().findByIdempotencyKey(idempotencyKey));
  }

  public ReadOnlyEffect<Optional<Invitation>> findActiveDuplicate(FindActiveDuplicateQuery query) {
    return effects().reply(currentState().findActiveDuplicate(query.normalizedEmail(), query.scopeType(), query.tenantId(), query.customerId()));
  }

  public ReadOnlyEffect<Optional<Invitation>> findByAcceptanceContext(String acceptanceContextId) {
    return effects().reply(currentState().findByAcceptanceContext(acceptanceContextId));
  }

  public ReadOnlyEffect<Optional<Invitation>> findByTokenHash(String tokenHash) {
    return effects().reply(currentState().findByTokenHash(tokenHash));
  }

  public Effect<Invitation> saveInvitation(Invitation invitation) {
    return effects().updateState(currentState().saveInvitation(invitation)).thenReply(() -> invitation);
  }

  public Effect<String> enqueueEmail(EmailOutboxMessage message) {
    var next = currentState().enqueueEmail(message);
    if (next == currentState()) {
      return effects().reply(message.outboxId());
    }
    return effects().updateState(next).thenReply(message::outboxId);
  }

  public ReadOnlyEffect<Optional<EmailOutboxMessage>> email(String outboxId) {
    return effects().reply(currentState().email(outboxId));
  }

  public ReadOnlyEffect<List<EmailOutboxMessage>> queuedEmails() {
    return effects().reply(currentState().queuedEmails());
  }

  public ReadOnlyEffect<List<Invitation>> invitations() {
    return effects().reply(currentState().invitationRows());
  }

  public record FindActiveDuplicateQuery(String normalizedEmail, ScopeType scopeType, String tenantId, String customerId) {}
}

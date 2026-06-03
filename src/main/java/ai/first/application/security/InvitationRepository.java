package ai.first.application.security;

import ai.first.domain.security.EmailOutboxMessage;
import ai.first.domain.security.Invitation;
import ai.first.domain.security.InvitationLifecycleFact;
import ai.first.domain.security.ScopeType;
import java.util.List;
import java.util.Optional;

/** Persistence port for the starter invitation lifecycle. Replace with Akka entities/views in generated apps. */
public interface InvitationRepository {
  Optional<Invitation> invitation(String invitationId);

  Optional<Invitation> findByIdempotencyKey(String idempotencyKey);

  Optional<Invitation> findActiveDuplicate(String normalizedEmail, ScopeType scopeType, String tenantId, String customerId);

  Optional<Invitation> findByAcceptanceContext(String acceptanceContextId);

  Optional<Invitation> findByTokenHash(String tokenHash);

  Invitation saveInvitation(Invitation invitation);

  void enqueueEmail(EmailOutboxMessage message);

  Optional<EmailOutboxMessage> email(String outboxId);

  List<EmailOutboxMessage> queuedEmails();

  List<Invitation> invitations();

  default Optional<InvitationLifecycleFact> recordLifecycleDecision(InvitationLifecycleHistoryEntity.DecisionFact decision) {
    return Optional.empty();
  }

  default List<InvitationLifecycleFact> lifecycleHistory(String invitationId) {
    return List.of();
  }
}

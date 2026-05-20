package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.EmailOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.Invitation;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
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
}

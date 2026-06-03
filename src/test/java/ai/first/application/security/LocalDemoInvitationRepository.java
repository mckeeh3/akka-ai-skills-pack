package ai.first.application.security;

import ai.first.domain.security.EmailOutboxMessage;
import ai.first.domain.security.Invitation;
import ai.first.domain.security.ScopeType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Test/local adapter for the starter invitation foundation. */
public final class LocalDemoInvitationRepository implements InvitationRepository {
  private final Map<String, Invitation> invitations = new ConcurrentHashMap<>();
  private final Map<String, EmailOutboxMessage> emails = new ConcurrentHashMap<>();

  @Override
  public Optional<Invitation> invitation(String invitationId) {
    return Optional.ofNullable(invitations.get(invitationId));
  }

  @Override
  public Optional<Invitation> findByIdempotencyKey(String idempotencyKey) {
    return invitations.values().stream().filter(invite -> idempotencyKey.equals(invite.idempotencyKey())).findFirst();
  }

  @Override
  public Optional<Invitation> findActiveDuplicate(String normalizedEmail, ScopeType scopeType, String tenantId, String customerId) {
    return invitations.values().stream()
        .filter(invite -> normalizedEmail.equals(invite.normalizedEmail()))
        .filter(invite -> scopeType == invite.scopeType())
        .filter(invite -> java.util.Objects.equals(tenantId, invite.tenantId()))
        .filter(invite -> java.util.Objects.equals(customerId, invite.customerId()))
        .filter(invite -> !invite.terminal())
        .findFirst();
  }

  @Override
  public Optional<Invitation> findByAcceptanceContext(String acceptanceContextId) {
    return invitations.values().stream().filter(invite -> acceptanceContextId.equals(invite.acceptanceContextId())).findFirst();
  }

  @Override
  public Optional<Invitation> findByTokenHash(String tokenHash) {
    return invitations.values().stream().filter(invite -> tokenHash.equals(invite.tokenHash())).findFirst();
  }

  @Override
  public Invitation saveInvitation(Invitation invitation) {
    invitations.put(invitation.invitationId(), invitation);
    return invitation;
  }

  @Override
  public void enqueueEmail(EmailOutboxMessage message) {
    emails.putIfAbsent(message.outboxId(), message);
  }

  @Override
  public Optional<EmailOutboxMessage> email(String outboxId) {
    return Optional.ofNullable(emails.get(outboxId));
  }

  @Override
  public List<EmailOutboxMessage> queuedEmails() {
    return emails.values().stream().toList();
  }

  @Override
  public List<Invitation> invitations() {
    return invitations.values().stream().toList();
  }
}

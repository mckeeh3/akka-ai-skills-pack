package {{JAVA_BASE_PACKAGE}}.domain.security;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Durable current-state projection for the starter invitation repository port.
 *
 * <p>This state backs the first Akka Key Value Entity replacement seam for the local/test
 * {@code InMemoryInvitationRepository}. It deliberately stores token hashes only; raw invitation
 * tokens remain confined to transient email outbox messages.
 */
public record InvitationRepositoryState(
    Map<String, Invitation> invitations,
    Map<String, EmailOutboxMessage> emails) {

  public InvitationRepositoryState {
    invitations = Map.copyOf(invitations == null ? Map.of() : invitations);
    emails = Map.copyOf(emails == null ? Map.of() : emails);
  }

  public static InvitationRepositoryState empty() {
    return new InvitationRepositoryState(Map.of(), Map.of());
  }

  public Optional<Invitation> invitation(String invitationId) {
    return Optional.ofNullable(invitations.get(invitationId));
  }

  public Optional<Invitation> findByIdempotencyKey(String idempotencyKey) {
    return invitations.values().stream()
        .filter(invite -> idempotencyKey != null && idempotencyKey.equals(invite.idempotencyKey()))
        .findFirst();
  }

  public Optional<Invitation> findActiveDuplicate(String normalizedEmail, ScopeType scopeType, String tenantId, String customerId) {
    return invitations.values().stream()
        .filter(invite -> normalizedEmail.equals(invite.normalizedEmail()))
        .filter(invite -> scopeType == invite.scopeType())
        .filter(invite -> java.util.Objects.equals(tenantId, invite.tenantId()))
        .filter(invite -> java.util.Objects.equals(customerId, invite.customerId()))
        .filter(invite -> !invite.terminal())
        .findFirst();
  }

  public Optional<Invitation> findByAcceptanceContext(String acceptanceContextId) {
    return invitations.values().stream()
        .filter(invite -> acceptanceContextId != null && acceptanceContextId.equals(invite.acceptanceContextId()))
        .findFirst();
  }

  public Optional<Invitation> findByTokenHash(String tokenHash) {
    return invitations.values().stream()
        .filter(invite -> tokenHash != null && tokenHash.equals(invite.tokenHash()))
        .findFirst();
  }

  public InvitationRepositoryState saveInvitation(Invitation invitation) {
    var updated = new java.util.LinkedHashMap<>(invitations);
    updated.put(invitation.invitationId(), invitation);
    return new InvitationRepositoryState(updated, emails);
  }

  public InvitationRepositoryState enqueueEmail(EmailOutboxMessage message) {
    if (emails.containsKey(message.outboxId())) {
      return this;
    }
    var updated = new java.util.LinkedHashMap<>(emails);
    updated.put(message.outboxId(), message);
    return new InvitationRepositoryState(invitations, updated);
  }

  public Optional<EmailOutboxMessage> email(String outboxId) {
    return Optional.ofNullable(emails.get(outboxId));
  }

  public List<EmailOutboxMessage> queuedEmails() {
    return emails.values().stream().toList();
  }

  public List<Invitation> invitationRows() {
    return invitations.values().stream().toList();
  }
}

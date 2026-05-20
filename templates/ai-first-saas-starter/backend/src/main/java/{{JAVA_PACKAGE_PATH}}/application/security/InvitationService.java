package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailDeliveryStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.Invitation;
import {{JAVA_BASE_PACKAGE}}.domain.security.InvitationStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Invitation workflow seam for the starter template.
 *
 * <p>The starter keeps this as an application service so the template remains easy to scaffold. Generated apps can
 * replace the repository ports with Akka Event Sourced Entities, Workflow, Consumer, TimedAction, and Views without
 * changing the capability contract or tests.
 */
public final class InvitationService {
  private final IdentityRepository identityRepository;
  private final InvitationRepository invitationRepository;
  private final Clock clock;

  public InvitationService(IdentityRepository identityRepository, InvitationRepository invitationRepository, Clock clock) {
    this.identityRepository = identityRepository;
    this.invitationRepository = invitationRepository;
    this.clock = clock;
  }

  public Invitation createInvitation(AuthContextResolver.ResolvedMe actor, CreateInvitationRequest request) {
    var auth = actor.selectedContext();
    requireScope(auth, request.scopeType(), request.tenantId(), request.customerId());
    requireInvitationCapability(auth, request.scopeType());
    ensureRolesAssignable(auth, request.requestedRoles());
    var normalizedEmail = normalizeEmail(request.email());
    if (request.expiresAt() == null || !request.expiresAt().isAfter(clock.instant())) {
      throw deny(actor, null, "INVITATION_CREATE", "invalid-expiry", request.correlationId());
    }

    var existingByKey = invitationRepository.findByIdempotencyKey(request.idempotencyKey());
    if (existingByKey.isPresent()) {
      audit(actor, existingByKey.get(), "INVITATION_DUPLICATE", AdminAuditEvent.Result.NO_OP, "idempotent-replay", request.correlationId());
      return existingByKey.get();
    }
    var duplicate = invitationRepository.findActiveDuplicate(normalizedEmail, request.scopeType(), request.tenantId(), request.customerId());
    if (duplicate.isPresent()) {
      audit(actor, duplicate.get(), "INVITATION_DUPLICATE", AdminAuditEvent.Result.NO_OP, "active-duplicate", request.correlationId());
      return duplicate.get();
    }

    var accountId = normalizedEmail;
    var account = identityRepository.findAccountByEmail(normalizedEmail).orElseGet(() ->
        identityRepository.saveAccount(new Account(accountId, null, normalizedEmail, request.email(), AccountStatus.INVITED, "UNLINKED")));
    if (account.status() == AccountStatus.DISABLED || account.status() == AccountStatus.REMOVED) {
      throw deny(actor, null, "INVITATION_CREATE", "target-account-disabled", request.correlationId());
    }
    if (identityRepository.profile(account.accountId()) == null) {
      putProfileIfSupported(new UserProfile(account.accountId(), account.displayEmail(), request.displayName(), null, null, null));
    }
    if (identityRepository.settings(account.accountId()) == null) {
      putSettingsIfSupported(new UserSettings(account.accountId(), UserSettings.UiMode.LIGHT));
    }

    var invitationId = "inv-" + UUID.randomUUID();
    var membershipId = "membership-" + invitationId;
    putMembershipIfSupported(
        new Membership(membershipId, account.accountId(), request.scopeType(), request.tenantId(), request.customerId(), request.requestedRoles(), MembershipStatus.INVITED, false, null));

    var rawToken = "invite-token-" + invitationId + "-" + UUID.randomUUID();
    var acceptanceContext = "accept-" + UUID.randomUUID();
    var now = clock.instant();
    var invitation =
        new Invitation(
            invitationId,
            normalizedEmail,
            request.scopeType(),
            request.tenantId(),
            request.customerId(),
            request.requestedRoles(),
            account.accountId(),
            membershipId,
            InvitationStatus.PENDING_DELIVERY,
            EmailDeliveryStatus.QUEUED,
            0,
            List.of(),
            null,
            acceptanceContext,
            sha256(rawToken),
            request.expiresAt(),
            null,
            null,
            null,
            null,
            null,
            0,
            actor.account().accountId(),
            now,
            request.idempotencyKey(),
            request.correlationId());
    invitationRepository.saveInvitation(invitation);
    invitationRepository.enqueueEmail(outboxMessage(invitation, rawToken, "delivery-1", request.correlationId()));
    audit(actor, invitation, "INVITATION_CREATE", AdminAuditEvent.Result.ALLOWED, "created", request.correlationId());
    audit(actor, invitation, "INVITATION_DELIVERY_QUEUED", AdminAuditEvent.Result.ALLOWED, "queued", request.correlationId());
    return invitation;
  }

  public Invitation recordDeliveryResult(String invitationId, String deliveryAttemptId, boolean delivered, String providerMessageId, String safeError, String correlationId) {
    var invite = requireInvitation(invitationId);
    if (invite.terminal()) {
      return invite;
    }
    var ids = delivered && providerMessageId != null ? append(invite.providerMessageIds(), providerMessageId) : invite.providerMessageIds();
    var updated = new Invitation(
        invite.invitationId(), invite.normalizedEmail(), invite.scopeType(), invite.tenantId(), invite.customerId(), invite.requestedRoles(),
        invite.accountId(), invite.membershipId(), delivered ? InvitationStatus.SENT : InvitationStatus.DELIVERY_FAILED,
        delivered ? EmailDeliveryStatus.CAPTURED : EmailDeliveryStatus.FAILED, invite.deliveryAttempts() + 1, ids,
        delivered ? null : safeError, invite.acceptanceContextId(), invite.tokenHash(), invite.expiresAt(), invite.acceptedAt(),
        invite.acceptedByWorkosSubject(), invite.revokedAt(), invite.revokedByAccountId(), invite.revokeReason(), invite.resendCount(),
        invite.createdByAccountId(), invite.createdAt(), invite.idempotencyKey(), correlationId);
    invitationRepository.saveInvitation(updated);
    appendSystemAudit(updated, "INVITATION_DELIVERY_" + (delivered ? "CAPTURED" : "FAILED"), delivered ? AdminAuditEvent.Result.ALLOWED : AdminAuditEvent.Result.FAILED, delivered ? "captured" : safeError, correlationId);
    return updated;
  }

  public Invitation resend(AuthContextResolver.ResolvedMe actor, String invitationId, String idempotencyKey, String reason, String correlationId) {
    var invite = requireInvitation(invitationId);
    requireScope(actor.selectedContext(), invite.scopeType(), invite.tenantId(), invite.customerId());
    requireInvitationCapability(actor.selectedContext(), invite.scopeType());
    if (!invite.resendable()) {
      throw deny(actor, invite, "INVITATION_RESEND", "not-resendable", correlationId);
    }
    var rawToken = "invite-token-" + invitationId + "-resend-" + idempotencyKey;
    var attempt = "delivery-" + (invite.resendCount() + 2);
    var updated = new Invitation(
        invite.invitationId(), invite.normalizedEmail(), invite.scopeType(), invite.tenantId(), invite.customerId(), invite.requestedRoles(),
        invite.accountId(), invite.membershipId(), InvitationStatus.PENDING_DELIVERY, EmailDeliveryStatus.QUEUED, invite.deliveryAttempts(),
        invite.providerMessageIds(), null, invite.acceptanceContextId(), sha256(rawToken), invite.expiresAt(), invite.acceptedAt(),
        invite.acceptedByWorkosSubject(), invite.revokedAt(), invite.revokedByAccountId(), invite.revokeReason(), invite.resendCount() + 1,
        invite.createdByAccountId(), invite.createdAt(), invite.idempotencyKey(), correlationId);
    invitationRepository.saveInvitation(updated);
    invitationRepository.enqueueEmail(outboxMessage(updated, rawToken, attempt, correlationId));
    audit(actor, updated, "INVITATION_RESEND", AdminAuditEvent.Result.ALLOWED, reason, correlationId);
    return updated;
  }

  public Invitation revoke(AuthContextResolver.ResolvedMe actor, String invitationId, String reason, String correlationId) {
    var invite = requireInvitation(invitationId);
    requireScope(actor.selectedContext(), invite.scopeType(), invite.tenantId(), invite.customerId());
    requireInvitationCapability(actor.selectedContext(), invite.scopeType());
    if (invite.status() == InvitationStatus.REVOKED) {
      return invite;
    }
    if (invite.status() == InvitationStatus.ACCEPTED) {
      throw deny(actor, invite, "INVITATION_REVOKE", "already-accepted", correlationId);
    }
    var updated = new Invitation(
        invite.invitationId(), invite.normalizedEmail(), invite.scopeType(), invite.tenantId(), invite.customerId(), invite.requestedRoles(),
        invite.accountId(), invite.membershipId(), InvitationStatus.REVOKED, invite.deliveryStatus(), invite.deliveryAttempts(),
        invite.providerMessageIds(), invite.lastDeliveryErrorSummary(), invite.acceptanceContextId(), invite.tokenHash(), invite.expiresAt(),
        invite.acceptedAt(), invite.acceptedByWorkosSubject(), clock.instant(), actor.account().accountId(), reason, invite.resendCount(),
        invite.createdByAccountId(), invite.createdAt(), invite.idempotencyKey(), correlationId);
    invitationRepository.saveInvitation(updated);
    audit(actor, updated, "INVITATION_REVOKE", AdminAuditEvent.Result.ALLOWED, reason, correlationId);
    return updated;
  }

  public Invitation expire(String invitationId, String tenantId, String customerId, String correlationId) {
    var invite = requireInvitation(invitationId);
    if (!java.util.Objects.equals(tenantId, invite.tenantId()) || !java.util.Objects.equals(customerId, invite.customerId())) {
      appendSystemAudit(invite, "INVITATION_EXPIRE", AdminAuditEvent.Result.DENIED, "scope-mismatch", correlationId);
      return invite;
    }
    if (invite.terminal()) {
      appendSystemAudit(invite, "INVITATION_EXPIRE", AdminAuditEvent.Result.NO_OP, "terminal", correlationId);
      return invite;
    }
    var updated = new Invitation(
        invite.invitationId(), invite.normalizedEmail(), invite.scopeType(), invite.tenantId(), invite.customerId(), invite.requestedRoles(),
        invite.accountId(), invite.membershipId(), InvitationStatus.EXPIRED, invite.deliveryStatus(), invite.deliveryAttempts(),
        invite.providerMessageIds(), invite.lastDeliveryErrorSummary(), invite.acceptanceContextId(), invite.tokenHash(), invite.expiresAt(),
        invite.acceptedAt(), invite.acceptedByWorkosSubject(), invite.revokedAt(), invite.revokedByAccountId(), invite.revokeReason(),
        invite.resendCount(), invite.createdByAccountId(), invite.createdAt(), invite.idempotencyKey(), correlationId);
    invitationRepository.saveInvitation(updated);
    appendSystemAudit(updated, "INVITATION_EXPIRE", AdminAuditEvent.Result.ALLOWED, "expired", correlationId);
    return updated;
  }

  public Invitation accept(WorkosIdentity identity, String acceptanceContext, String correlationId) {
    var invite = invitationRepository.findByAcceptanceContext(acceptanceContext).orElseThrow(() -> new AuthorizationException(403, "invitation-not-found-or-forbidden"));
    if (invite.status() == InvitationStatus.ACCEPTED) {
      if (identity.subject().equals(invite.acceptedByWorkosSubject())) {
        return invite;
      }
      appendSystemAudit(invite, "INVITATION_ACCEPT", AdminAuditEvent.Result.DENIED, "already-accepted-by-other-subject", correlationId);
      throw new AuthorizationException(403, "invitation-already-accepted-by-other-subject");
    }
    if (invite.status() == InvitationStatus.REVOKED || invite.status() == InvitationStatus.EXPIRED || invite.expiredAt(clock.instant())) {
      appendSystemAudit(invite, "INVITATION_ACCEPT", AdminAuditEvent.Result.DENIED, "not-acceptable", correlationId);
      throw new AuthorizationException(403, "invitation-not-acceptable");
    }
    if (!invite.normalizedEmail().equals(normalizeEmail(identity.email()))) {
      appendSystemAudit(invite, "INVITATION_ACCEPT", AdminAuditEvent.Result.DENIED, "workos-email-mismatch", correlationId);
      throw new AuthorizationException(403, "workos-email-mismatch");
    }
    var account = identityRepository.findAccountByEmail(invite.normalizedEmail()).orElseThrow();
    identityRepository.saveAccount(new Account(account.accountId(), identity.subject(), account.normalizedEmail(), account.displayEmail(), AccountStatus.ACTIVE, "LINKED"));
    activateMembershipIfSupported(invite.membershipId());
    var updated = new Invitation(
        invite.invitationId(), invite.normalizedEmail(), invite.scopeType(), invite.tenantId(), invite.customerId(), invite.requestedRoles(),
        invite.accountId(), invite.membershipId(), InvitationStatus.ACCEPTED, invite.deliveryStatus(), invite.deliveryAttempts(),
        invite.providerMessageIds(), invite.lastDeliveryErrorSummary(), invite.acceptanceContextId(), invite.tokenHash(), invite.expiresAt(),
        clock.instant(), identity.subject(), invite.revokedAt(), invite.revokedByAccountId(), invite.revokeReason(), invite.resendCount(),
        invite.createdByAccountId(), invite.createdAt(), invite.idempotencyKey(), correlationId);
    invitationRepository.saveInvitation(updated);
    appendSystemAudit(updated, "INVITATION_ACCEPT", AdminAuditEvent.Result.ALLOWED, "accepted", correlationId);
    appendSystemAudit(updated, "MEMBERSHIP_ACTIVATE", AdminAuditEvent.Result.ALLOWED, "invitation-accepted", correlationId);
    return updated;
  }

  public List<Invitation> listScoped(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    requireScope(actor.selectedContext(), scopeType, tenantId, customerId);
    var capability = scopeType == ScopeType.CUSTOMER ? "customer.user.read" : "tenant.user.read";
    if (scopeType == ScopeType.SAAS_OWNER) {
      capability = "saas_owner.user.manage";
    }
    if (!actor.selectedContext().hasCapability(capability) && !actor.selectedContext().hasCapability("tenant.audit.read")) {
      throw deny(actor, null, "INVITATION_READ", "missing-capability", actor.correlationId());
    }
    return invitationRepository.invitations().stream()
        .filter(invite -> scopeType == invite.scopeType())
        .filter(invite -> java.util.Objects.equals(tenantId, invite.tenantId()))
        .filter(invite -> java.util.Objects.equals(customerId, invite.customerId()))
        .toList();
  }

  private EmailOutboxMessage outboxMessage(Invitation invitation, String rawToken, String attemptId, String correlationId) {
    return new EmailOutboxMessage(
        invitation.invitationId() + ":" + attemptId,
        "INVITATION",
        invitation.invitationId(),
        attemptId,
        invitation.scopeType(),
        invitation.tenantId(),
        invitation.customerId(),
        invitation.normalizedEmail(),
        "https://app.example.test/accept?token=" + rawToken,
        Map.of("expiresAt", invitation.expiresAt().toString()),
        correlationId,
        clock.instant());
  }

  private void requireInvitationCapability({{JAVA_BASE_PACKAGE}}.domain.security.AuthContext auth, ScopeType targetScope) {
    var required = targetScope == ScopeType.CUSTOMER ? "customer.invitation.manage" : targetScope == ScopeType.SAAS_OWNER ? "saas_owner.user.manage" : "tenant.invitation.manage";
    if (!auth.hasCapability(required)) {
      throw new AuthorizationException(403, "missing-capability:" + required);
    }
  }

  private void requireScope({{JAVA_BASE_PACKAGE}}.domain.security.AuthContext auth, ScopeType scopeType, String tenantId, String customerId) {
    if (scopeType == ScopeType.SAAS_OWNER && auth.scopeType() != ScopeType.SAAS_OWNER) {
      throw new AuthorizationException(403, "scope-forbidden");
    }
    if (scopeType == ScopeType.TENANT && (auth.scopeType() == ScopeType.SAAS_OWNER || !tenantId.equals(auth.tenantId()))) {
      throw new AuthorizationException(403, "tenant-mismatch");
    }
    if (scopeType == ScopeType.CUSTOMER) {
      if (!tenantId.equals(auth.tenantId()) || (auth.scopeType() == ScopeType.CUSTOMER && !customerId.equals(auth.customerId()))) {
        throw new AuthorizationException(403, "customer-mismatch");
      }
    }
  }

  private void ensureRolesAssignable({{JAVA_BASE_PACKAGE}}.domain.security.AuthContext auth, List<FoundationRole> roles) {
    if (roles.contains(FoundationRole.SAAS_OWNER_ADMIN) && auth.scopeType() != ScopeType.SAAS_OWNER) {
      throw new AuthorizationException(403, "role-escalation-denied");
    }
    if (roles.stream().anyMatch(role -> role.defaultScopeType() == ScopeType.TENANT) && auth.scopeType() == ScopeType.CUSTOMER) {
      throw new AuthorizationException(403, "role-escalation-denied");
    }
  }

  private AuthorizationException deny(AuthContextResolver.ResolvedMe actor, Invitation invite, String action, String reason, String correlationId) {
    audit(actor, invite, action, AdminAuditEvent.Result.DENIED, reason, correlationId);
    return new AuthorizationException(403, reason);
  }

  private Invitation requireInvitation(String invitationId) {
    return invitationRepository.invitation(invitationId).orElseThrow(() -> new AuthorizationException(404, "invitation-not-found-or-forbidden"));
  }

  private void audit(AuthContextResolver.ResolvedMe actor, Invitation invite, String action, AdminAuditEvent.Result result, String reason, String correlationId) {
    identityRepository.appendAudit(new AdminAuditEvent(
        UUID.randomUUID().toString(), clock.instant(), correlationId, actor.account().accountId(), actor.selectedContext().membershipId(),
        actor.selectedContext().scopeType(), invite == null ? actor.selectedContext().tenantId() : invite.tenantId(),
        invite == null ? actor.selectedContext().customerId() : invite.customerId(), invite == null ? null : invite.accountId(),
        invite == null ? null : invite.membershipId(), action, result, reason, reason, "BROWSER_SAFE"));
  }

  private void appendSystemAudit(Invitation invite, String action, AdminAuditEvent.Result result, String reason, String correlationId) {
    identityRepository.appendAudit(new AdminAuditEvent(
        UUID.randomUUID().toString(), clock.instant(), correlationId, "system", null, invite.scopeType(), invite.tenantId(), invite.customerId(),
        invite.accountId(), invite.membershipId(), action, result, reason, reason, "BROWSER_SAFE"));
  }

  private void putProfileIfSupported(UserProfile profile) {
    if (identityRepository instanceof InMemoryIdentityRepository memory) {
      memory.putProfile(profile);
    }
  }

  private void putSettingsIfSupported(UserSettings settings) {
    if (identityRepository instanceof InMemoryIdentityRepository memory) {
      memory.putSettings(settings);
    }
  }

  private void putMembershipIfSupported(Membership membership) {
    if (identityRepository instanceof InMemoryIdentityRepository memory) {
      memory.putMembership(membership);
    }
  }

  private void activateMembershipIfSupported(String membershipId) {
    if (identityRepository instanceof InMemoryIdentityRepository memory) {
      memory.findMembership(membershipId).ifPresent(existing -> memory.putMembership(new Membership(
          existing.membershipId(), existing.accountId(), existing.scopeType(), existing.tenantId(), existing.customerId(), existing.roles(), MembershipStatus.ACTIVE, existing.supportAccess(), existing.expiresAt())));
    }
  }

  private static String normalizeEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("email is required");
    }
    return email.trim().toLowerCase();
  }

  private static List<String> append(List<String> ids, String id) {
    var copy = new java.util.ArrayList<>(ids);
    copy.add(id);
    return List.copyOf(copy);
  }

  private static String sha256(String value) {
    try {
      return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public record CreateInvitationRequest(
      String idempotencyKey,
      ScopeType scopeType,
      String tenantId,
      String customerId,
      String email,
      String displayName,
      List<FoundationRole> requestedRoles,
      Instant expiresAt,
      String reason,
      String correlationId) {
    public CreateInvitationRequest {
      requestedRoles = List.copyOf(requestedRoles == null ? List.of() : requestedRoles);
    }
  }
}

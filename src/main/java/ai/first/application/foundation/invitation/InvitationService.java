package ai.first.application.foundation.invitation;

import ai.first.domain.foundation.identity.AuthContext;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.email.EmailDeliveryStatus;
import ai.first.domain.foundation.email.EmailOutboxMessage;
import ai.first.application.foundation.email.ResendEmailService;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.invitation.Invitation;
import ai.first.domain.foundation.invitation.InvitationStatus;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
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
import ai.first.application.foundation.attention.AttentionProducerService;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.IdentityRepository;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;

/**
 * Invitation workflow seam for the core app.
 *
 * <p>The core app keeps this as an application service so downstream forks can replace the repository ports with Akka
 * Event Sourced Entities, Workflow, Consumer, TimedAction, and Views without changing the capability contract or tests.
 */
public final class InvitationService {
  private final IdentityRepository identityRepository;
  private final InvitationRepository invitationRepository;
  private final Clock clock;
  private final AttentionProducerService attentionProducerService;
  private final WorkstreamEventPublisher workstreamEventPublisher;
  private static final String DEFAULT_PUBLIC_BASE_URL = "https://app.example.test";

  private final ResendEmailService invitationEmailService;
  private final ResendEmailService.DeliveryMode invitationEmailDeliveryMode;
  private final String appPublicBaseUrl;

  public InvitationService(IdentityRepository identityRepository, InvitationRepository invitationRepository, Clock clock) {
    this(identityRepository, invitationRepository, clock, null, null);
  }

  public InvitationService(IdentityRepository identityRepository, InvitationRepository invitationRepository, Clock clock, AttentionProducerService attentionProducerService) {
    this(identityRepository, invitationRepository, clock, attentionProducerService, null);
  }

  public InvitationService(IdentityRepository identityRepository, InvitationRepository invitationRepository, Clock clock, AttentionProducerService attentionProducerService, WorkstreamEventPublisher workstreamEventPublisher) {
    this(identityRepository, invitationRepository, clock, attentionProducerService, workstreamEventPublisher, null, null);
  }

  public InvitationService(
      IdentityRepository identityRepository,
      InvitationRepository invitationRepository,
      Clock clock,
      AttentionProducerService attentionProducerService,
      WorkstreamEventPublisher workstreamEventPublisher,
      ResendEmailService invitationEmailService,
      ResendEmailService.DeliveryMode invitationEmailDeliveryMode) {
    this(identityRepository, invitationRepository, clock, attentionProducerService, workstreamEventPublisher, invitationEmailService, invitationEmailDeliveryMode, System.getenv("APP_PUBLIC_BASE_URL"));
  }

  public InvitationService(
      IdentityRepository identityRepository,
      InvitationRepository invitationRepository,
      Clock clock,
      AttentionProducerService attentionProducerService,
      WorkstreamEventPublisher workstreamEventPublisher,
      ResendEmailService invitationEmailService,
      ResendEmailService.DeliveryMode invitationEmailDeliveryMode,
      String appPublicBaseUrl) {
    this.identityRepository = identityRepository;
    this.invitationRepository = invitationRepository;
    this.clock = clock;
    this.attentionProducerService = attentionProducerService;
    this.workstreamEventPublisher = workstreamEventPublisher;
    this.invitationEmailService = invitationEmailService;
    this.invitationEmailDeliveryMode = invitationEmailDeliveryMode;
    this.appPublicBaseUrl = normalizePublicBaseUrl(appPublicBaseUrl);
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
      putSettingsIfSupported(new UserSettings(account.accountId(), UserSettings.ThemeId.AURORA_LIGHT));
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
    var emailMessage = outboxMessage(invitation, rawToken, "delivery-1", request.correlationId());
    invitationRepository.enqueueEmail(emailMessage);
    audit(actor, invitation, "INVITATION_CREATE", AdminAuditEvent.Result.ALLOWED, "created", request.correlationId());
    audit(actor, invitation, "INVITATION_DELIVERY_QUEUED", AdminAuditEvent.Result.ALLOWED, "queued", request.correlationId());
    return deliverInvitationEmailIfConfigured(invitation, emailMessage, request.correlationId());
  }

  public Invitation recordDeliveryResult(String invitationId, String deliveryAttemptId, boolean delivered, String providerMessageId, String safeError, String correlationId) {
    var invite = requireInvitation(invitationId);
    if (invite.terminal()) {
      appendSystemAudit(invite, "INVITATION_DELIVERY_NO_OP", AdminAuditEvent.Result.NO_OP, "terminal-or-obsolete", correlationId);
      return invite;
    }
    var safeProviderMessageId = providerMessageId == null || providerMessageId.isBlank() ? null : providerMessageId;
    var safeErrorSummary = safeError == null || safeError.isBlank() ? null : safeError;
    if (delivered && safeProviderMessageId != null && invite.providerMessageIds().contains(safeProviderMessageId)) {
      appendSystemAudit(invite, "INVITATION_DELIVERY_NO_OP", AdminAuditEvent.Result.NO_OP, "idempotent-delivery-result", correlationId);
      return invite;
    }
    var ids = delivered && safeProviderMessageId != null ? append(invite.providerMessageIds(), safeProviderMessageId) : invite.providerMessageIds();
    var deliveredStatus = safeProviderMessageId != null && safeProviderMessageId.startsWith("captured-") ? EmailDeliveryStatus.CAPTURED : EmailDeliveryStatus.SENT;
    var updated = new Invitation(
        invite.invitationId(), invite.normalizedEmail(), invite.scopeType(), invite.tenantId(), invite.customerId(), invite.requestedRoles(),
        invite.accountId(), invite.membershipId(), delivered ? InvitationStatus.SENT : InvitationStatus.DELIVERY_FAILED,
        delivered ? deliveredStatus : EmailDeliveryStatus.FAILED, invite.deliveryAttempts() + 1, ids,
        delivered ? null : safeErrorSummary, invite.acceptanceContextId(), invite.tokenHash(), invite.expiresAt(), invite.acceptedAt(),
        invite.acceptedByWorkosSubject(), invite.revokedAt(), invite.revokedByAccountId(), invite.revokeReason(), invite.resendCount(),
        invite.createdByAccountId(), invite.createdAt(), invite.idempotencyKey(), correlationId);
    invitationRepository.saveInvitation(updated);
    appendSystemAudit(updated, "INVITATION_DELIVERY_" + (delivered ? deliveredStatus.name() : "FAILED"), delivered ? AdminAuditEvent.Result.ALLOWED : AdminAuditEvent.Result.FAILED, delivered ? deliveredStatus.name().toLowerCase(java.util.Locale.ROOT) : safeErrorSummary, correlationId);
    if (workstreamEventPublisher != null) {
      workstreamEventPublisher.publishInvitationDelivery(updated, delivered, deliveryAttemptId, delivered ? deliveredStatus.name() : "FAILED", safeErrorSummary, correlationId);
    } else if (attentionProducerService != null) {
      if (delivered) attentionProducerService.resolveInvitationDelivery(updated, deliveredStatus.name().toLowerCase(java.util.Locale.ROOT), correlationId);
      else attentionProducerService.upsertInvitationDelivery(updated, correlationId);
    }
    return updated;
  }

  public Invitation resend(AuthContextResolver.ResolvedMe actor, String invitationId, String idempotencyKey, String reason, String correlationId) {
    var invite = requireInvitation(invitationId);
    requireScope(actor.selectedContext(), invite.scopeType(), invite.tenantId(), invite.customerId());
    requireInvitationCapability(actor.selectedContext(), invite.scopeType());
    if (!invite.resendable()) {
      throw deny(actor, invite, "INVITATION_RESEND", "not-resendable", correlationId);
    }
    var attempt = "delivery-resend-" + sha256(firstNonBlank(idempotencyKey, "missing-idempotency-key")).substring(0, 16);
    var existingOutboxId = invitationId + ":" + attempt;
    if (invitationRepository.email(existingOutboxId).isPresent()) {
      audit(actor, invite, "INVITATION_RESEND", AdminAuditEvent.Result.NO_OP, "idempotent-replay", correlationId);
      return invite;
    }
    var rawToken = "invite-token-" + invitationId + "-resend-" + firstNonBlank(idempotencyKey, "missing-idempotency-key");
    var updated = new Invitation(
        invite.invitationId(), invite.normalizedEmail(), invite.scopeType(), invite.tenantId(), invite.customerId(), invite.requestedRoles(),
        invite.accountId(), invite.membershipId(), InvitationStatus.PENDING_DELIVERY, EmailDeliveryStatus.QUEUED, invite.deliveryAttempts(),
        invite.providerMessageIds(), null, invite.acceptanceContextId(), sha256(rawToken), invite.expiresAt(), invite.acceptedAt(),
        invite.acceptedByWorkosSubject(), invite.revokedAt(), invite.revokedByAccountId(), invite.revokeReason(), invite.resendCount() + 1,
        invite.createdByAccountId(), invite.createdAt(), invite.idempotencyKey(), correlationId);
    invitationRepository.saveInvitation(updated);
    var emailMessage = outboxMessage(updated, rawToken, attempt, correlationId);
    invitationRepository.enqueueEmail(emailMessage);
    audit(actor, updated, "INVITATION_RESEND", AdminAuditEvent.Result.ALLOWED, reason, correlationId);
    return deliverInvitationEmailIfConfigured(updated, emailMessage, correlationId);
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
    if (attentionProducerService != null) attentionProducerService.resolveInvitationDelivery(updated, "revoked", correlationId);
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
    if (attentionProducerService != null) attentionProducerService.resolveInvitationDelivery(updated, "expired", correlationId);
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
    if (invite.status() == InvitationStatus.DELIVERY_FAILED || invite.deliveryStatus() == EmailDeliveryStatus.FAILED) {
      appendSystemAudit(invite, "INVITATION_ACCEPT", AdminAuditEvent.Result.DENIED, "delivery-failed-without-override", correlationId);
      throw new AuthorizationException(403, "invitation-delivery-failed");
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
    if (attentionProducerService != null) attentionProducerService.resolveInvitationDelivery(updated, "accepted", correlationId);
    return updated;
  }

  public InvitationAcceptanceResult acceptForBrowser(WorkosIdentity identity, AcceptInvitationRequest request, String correlationId) {
    var resolved = resolveInvitationForAcceptance(request);
    if (resolved.isEmpty()) {
      return InvitationAcceptanceResult.invalid("invitation-not-found-or-forbidden", "Request a fresh invitation from an administrator.", correlationId);
    }
    var invite = resolved.get();
    if (invite.status() == InvitationStatus.ACCEPTED) {
      if (identity.subject().equals(invite.acceptedByWorkosSubject())) {
        appendSystemAudit(invite, "INVITATION_ACCEPT", AdminAuditEvent.Result.NO_OP, "already-accepted-same-subject", correlationId);
        return InvitationAcceptanceResult.from(invite, "already-accepted", "This invitation was already accepted for your signed-in account.", correlationId);
      }
      appendSystemAudit(invite, "INVITATION_ACCEPT", AdminAuditEvent.Result.DENIED, "already-accepted-by-other-subject", correlationId);
      return InvitationAcceptanceResult.from(invite, "already-accepted-by-other-account", "This invitation was already accepted by another account. Sign in as the original user or request a new invitation.", correlationId);
    }
    if (invite.status() == InvitationStatus.REVOKED) {
      appendSystemAudit(invite, "INVITATION_ACCEPT", AdminAuditEvent.Result.DENIED, "revoked", correlationId);
      return InvitationAcceptanceResult.from(invite, "revoked", "This invitation was revoked. Ask an administrator to send a new invitation.", correlationId);
    }
    if (invite.status() == InvitationStatus.EXPIRED || invite.expiredAt(clock.instant())) {
      var expired = invite.status() == InvitationStatus.EXPIRED ? invite : expire(invite.invitationId(), invite.tenantId(), invite.customerId(), correlationId);
      appendSystemAudit(expired, "INVITATION_ACCEPT", AdminAuditEvent.Result.DENIED, "expired", correlationId);
      return InvitationAcceptanceResult.from(expired, "expired", "This invitation has expired. Ask an administrator to resend it.", correlationId);
    }
    if (invite.status() == InvitationStatus.DELIVERY_FAILED || invite.deliveryStatus() == EmailDeliveryStatus.FAILED) {
      appendSystemAudit(invite, "INVITATION_ACCEPT", AdminAuditEvent.Result.DENIED, "delivery-failed-without-override", correlationId);
      return InvitationAcceptanceResult.from(invite, "delivery-failed", "This invitation email failed delivery. Ask an administrator to resend it before accepting.", correlationId);
    }
    if (!invite.normalizedEmail().equals(normalizeEmail(identity.email()))) {
      appendSystemAudit(invite, "INVITATION_ACCEPT", AdminAuditEvent.Result.DENIED, "workos-email-mismatch", correlationId);
      return InvitationAcceptanceResult.from(invite, "wrong-account", "Sign in with the email address that received this invitation, or ask an administrator to resend it.", correlationId);
    }
    var accepted = accept(identity, invite.acceptanceContextId(), correlationId);
    return InvitationAcceptanceResult.from(accepted, "accepted", "Invitation accepted. Your membership is now active.", correlationId);
  }

  public List<Invitation> listScoped(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    requireScopedRead(actor, scopeType, tenantId, customerId);
    return invitationRepository.invitations().stream()
        .filter(invite -> scopeType == invite.scopeType())
        .filter(invite -> java.util.Objects.equals(tenantId, invite.tenantId()))
        .filter(invite -> java.util.Objects.equals(customerId, invite.customerId()))
        .toList();
  }

  InvitationRepository invitationRepository() {
    return invitationRepository;
  }

  void requireScopedRead(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    requireScope(actor.selectedContext(), scopeType, tenantId, customerId);
    var capability = scopeType == ScopeType.CUSTOMER
        ? (actor.selectedContext().scopeType() == ScopeType.TENANT ? "tenant.customer_admin.list" : "customer.user.read")
        : actor.selectedContext().scopeType() == ScopeType.SAAS_OWNER && scopeType == ScopeType.TENANT ? "saas_owner.organization_admin.list" : "tenant.user.read";
    if (scopeType == ScopeType.SAAS_OWNER) {
      capability = "saas_owner.user.manage";
    }
    if (!actor.selectedContext().hasCapability(capability) && !actor.selectedContext().hasCapability("tenant.audit.read")) {
      throw deny(actor, null, "INVITATION_READ", "missing-capability", actor.correlationId());
    }
  }

  private java.util.Optional<Invitation> resolveInvitationForAcceptance(AcceptInvitationRequest request) {
    if (request == null) return java.util.Optional.empty();
    if (request.token() != null && !request.token().isBlank()) {
      return invitationRepository.findByTokenHash(sha256(request.token().trim()));
    }
    if (request.acceptanceContextId() != null && !request.acceptanceContextId().isBlank()) {
      return invitationRepository.findByAcceptanceContext(request.acceptanceContextId().trim());
    }
    return java.util.Optional.empty();
  }

  private Invitation deliverInvitationEmailIfConfigured(Invitation invitation, EmailOutboxMessage message, String correlationId) {
    if (invitationEmailService == null || invitationEmailDeliveryMode == null) {
      return invitation;
    }
    var result = invitationEmailService.deliver(message, invitationEmailDeliveryMode);
    return recordDeliveryResult(invitation.invitationId(), message.deliveryAttemptId(), result.success(), result.providerMessageId(), result.safeErrorSummary(), correlationId);
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
        inviteUrl(rawToken),
        Map.of("expiresAt", invitation.expiresAt().toString()),
        correlationId,
        clock.instant());
  }

  private String inviteUrl(String rawToken) {
    return appPublicBaseUrl + "/accept?token=" + rawToken;
  }

  private static String normalizePublicBaseUrl(String value) {
    if (value == null || value.isBlank()) return DEFAULT_PUBLIC_BASE_URL;
    var trimmed = value.trim();
    while (trimmed.endsWith("/")) {
      trimmed = trimmed.substring(0, trimmed.length() - 1);
    }
    return trimmed.isBlank() ? DEFAULT_PUBLIC_BASE_URL : trimmed;
  }

  private void requireInvitationCapability(ai.first.domain.foundation.identity.AuthContext auth, ScopeType targetScope) {
    var required = targetScope == ScopeType.CUSTOMER
        ? (auth.scopeType() == ScopeType.TENANT ? "tenant.customer_admin.invite" : "customer.invitation.manage")
        : targetScope == ScopeType.SAAS_OWNER
            ? "saas_owner.user.manage"
            : auth.scopeType() == ScopeType.SAAS_OWNER ? "saas_owner.organization_admin.invite" : "tenant.invitation.manage";
    if (!auth.hasCapability(required)) {
      throw new AuthorizationException(403, "missing-capability:" + required);
    }
  }

  private void requireScope(ai.first.domain.foundation.identity.AuthContext auth, ScopeType scopeType, String tenantId, String customerId) {
    if (scopeType == ScopeType.SAAS_OWNER && auth.scopeType() != ScopeType.SAAS_OWNER) {
      throw new AuthorizationException(403, "scope-forbidden");
    }
    if (scopeType == ScopeType.TENANT) {
      if (auth.scopeType() == ScopeType.SAAS_OWNER) {
        if (tenantId == null || tenantId.isBlank()) throw new AuthorizationException(400, "tenant-id-required");
        return;
      }
      if (!tenantId.equals(auth.tenantId())) throw new AuthorizationException(403, "tenant-mismatch");
    }
    if (scopeType == ScopeType.CUSTOMER) {
      if (!tenantId.equals(auth.tenantId()) || (auth.scopeType() == ScopeType.CUSTOMER && !customerId.equals(auth.customerId()))) {
        throw new AuthorizationException(403, "customer-mismatch");
      }
    }
  }

  private void ensureRolesAssignable(ai.first.domain.foundation.identity.AuthContext auth, List<FoundationRole> roles) {
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
    recordLifecycleDecision(invite, action, result, reason, actor.account().accountId(), correlationId);
  }

  private void appendSystemAudit(Invitation invite, String action, AdminAuditEvent.Result result, String reason, String correlationId) {
    identityRepository.appendAudit(new AdminAuditEvent(
        UUID.randomUUID().toString(), clock.instant(), correlationId, "system", null, invite.scopeType(), invite.tenantId(), invite.customerId(),
        invite.accountId(), invite.membershipId(), action, result, reason, reason, "BROWSER_SAFE"));
    recordLifecycleDecision(invite, action, result, reason, "system", correlationId);
  }

  private void recordLifecycleDecision(Invitation invite, String action, AdminAuditEvent.Result result, String reason, String actorAccountId, String correlationId) {
    if (invite != null) {
      invitationRepository.recordLifecycleDecision(new InvitationLifecycleHistoryEntity.DecisionFact(invite, action, result, reason, actorAccountId, correlationId));
    }
  }

  private void putProfileIfSupported(UserProfile profile) {
    identityRepository.saveProfile(profile);
  }

  private void putSettingsIfSupported(UserSettings settings) {
    identityRepository.saveSettings(settings);
  }

  private void putMembershipIfSupported(Membership membership) {
    identityRepository.saveMembership(membership);
  }

  private void activateMembershipIfSupported(String membershipId) {
    identityRepository.membership(membershipId).ifPresent(existing -> identityRepository.saveMembership(new Membership(
        existing.membershipId(), existing.accountId(), existing.scopeType(), existing.tenantId(), existing.customerId(), existing.roles(), MembershipStatus.ACTIVE, existing.supportAccess(), existing.expiresAt())));
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

  private static String firstNonBlank(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value.trim();
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

  public record AcceptInvitationRequest(String token, String acceptanceContextId) {}

  public record InvitationAcceptanceResult(
      String status,
      String reasonCode,
      String recoveryHint,
      String invitationId,
      ScopeType scopeType,
      String tenantId,
      String customerId,
      String membershipId,
      Instant expiresAt,
      String correlationId) {
    static InvitationAcceptanceResult from(Invitation invite, String status, String recoveryHint, String correlationId) {
      return new InvitationAcceptanceResult(status, status, recoveryHint, invite.invitationId(), invite.scopeType(), invite.tenantId(), invite.customerId(), invite.membershipId(), invite.expiresAt(), correlationId);
    }

    static InvitationAcceptanceResult invalid(String reasonCode, String recoveryHint, String correlationId) {
      return new InvitationAcceptanceResult("invalid", reasonCode, recoveryHint, null, null, null, null, null, null, correlationId);
    }
  }
}

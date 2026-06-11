package ai.first.application.coreapp.useradmin;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.IdentityRepository;

/** Scoped User Admin capability seam for account, membership, role, audit, and last-admin safety. */
public final class UserAdminService {
  private final IdentityRepository repository;
  private final Clock clock;

  public UserAdminService(IdentityRepository repository, Clock clock) {
    this.repository = repository;
    this.clock = clock;
  }

  public List<UserDirectoryRow> listUsers(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    requireRead(actor, scopeType, tenantId, customerId);
    return repository.membershipRows().stream()
        .filter(m -> scopeType == m.scopeType())
        .filter(m -> java.util.Objects.equals(tenantId, m.tenantId()))
        .filter(m -> java.util.Objects.equals(customerId, m.customerId()))
        .map(m -> new UserDirectoryRow(m.accountId(), repository.profile(m.accountId()) == null ? m.accountId() : repository.profile(m.accountId()).displayName(), m.membershipId(), m.roles(), m.status(), m.scopeType(), m.tenantId(), m.customerId(), m.supportAccess(), m.expiresAt()))
        .toList();
  }

  public List<UserDirectoryRow> searchUsers(AuthContextResolver.ResolvedMe actor, String query, String correlationId) {
    var normalizedQuery = query == null ? "" : query.trim().toLowerCase();
    var rows = listUsers(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId()).stream()
        .filter(row -> normalizedQuery.isBlank()
            || row.accountId().toLowerCase().contains(normalizedQuery)
            || row.displayName().toLowerCase().contains(normalizedQuery))
        .toList();
    audit(actor, null, "USER_DIRECTORY_SEARCH", AdminAuditEvent.Result.ALLOWED, normalizedQuery.isBlank() ? "all" : "query", correlationId);
    return rows;
  }

  public List<AdminAuditEvent> auditEvents(AuthContextResolver.ResolvedMe actor, int limit, String correlationId) {
    requireAuditRead(actor);
    audit(actor, null, "AUDIT_EVENTS_SEARCH", AdminAuditEvent.Result.ALLOWED, "browser-safe", correlationId);
    return repository.auditEvents().stream()
        .filter(event -> actor.selectedContext().scopeType() == ScopeType.SAAS_OWNER || java.util.Objects.equals(actor.selectedContext().tenantId(), event.tenantId()))
        .filter(event -> actor.selectedContext().scopeType() != ScopeType.CUSTOMER || java.util.Objects.equals(actor.selectedContext().customerId(), event.customerId()))
        .sorted(Comparator.comparing(AdminAuditEvent::timestamp).reversed())
        .limit(Math.max(1, Math.min(limit, 100)))
        .toList();
  }

  public RoleChangePreview previewRoleChange(AuthContextResolver.ResolvedMe actor, String membershipId, List<FoundationRole> roles, String reason, String correlationId) {
    var existing = membership(membershipId);
    requireManage(actor, existing.scopeType(), existing.tenantId(), existing.customerId());
    ensureAssignable(actor, roles);
    var noOp = existing.roles().equals(roles);
    var lastAdminDenied = wouldRemoveLastAdmin(existing, roles, existing.status());
    var selfRemovalDenied = wouldRemoveOwnAdminRole(actor, existing, roles);
    var allowed = !lastAdminDenied && !selfRemovalDenied;
    var denialReason = lastAdminDenied ? "last-admin-denied" : selfRemovalDenied ? "self-admin-role-removal-denied" : null;
    audit(actor, existing, "USERADMIN_PREVIEW_ROLE_CHANGE", allowed ? noOp ? AdminAuditEvent.Result.NO_OP : AdminAuditEvent.Result.ALLOWED : AdminAuditEvent.Result.DENIED, denialReason == null ? reason : denialReason, correlationId);
    return new RoleChangePreview(allowed, noOp, denialReason == null ? noOp ? "requested roles already match current assignment" : "role change can proceed with backend authorization" : denialReason, "trace-useradmin-preview-role-change-" + stableSuffix(correlationId), capabilityDelta(existing.roles(), roles), affectedWorkstreams(existing.roles(), roles), List.of("SMB policy: backend authority and human approval required for role changes", "last-admin and self-admin-role-removal guardrails enforced"), lastAdminDenied ? "would-remove-final-admin" : "admin-coverage-preserved");
  }

  public RoleChangeResult changeMemberRoles(AuthContextResolver.ResolvedMe actor, String membershipId, List<FoundationRole> roles, String reason, String idempotencyKey, String correlationId) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
      throw new AuthorizationException(400, "idempotency-key-required");
    }
    var existing = membership(membershipId);
    requireManage(actor, existing.scopeType(), existing.tenantId(), existing.customerId());
    ensureAssignable(actor, roles);
    if (existing.roles().equals(roles)) {
      audit(actor, existing, "USERADMIN_CHANGE_MEMBER_ROLES", AdminAuditEvent.Result.NO_OP, "no-op", correlationId);
      return new RoleChangeResult("no-op", "Requested roles already match current assignment.", existing, "trace-useradmin-change-member-roles-" + stableSuffix(idempotencyKey));
    }
    if (wouldRemoveLastAdmin(existing, roles, existing.status())) {
      audit(actor, existing, "USERADMIN_CHANGE_MEMBER_ROLES", AdminAuditEvent.Result.DENIED, "last-admin-denied", correlationId);
      throw new AuthorizationException(403, "last-admin-denied");
    }
    if (wouldRemoveOwnAdminRole(actor, existing, roles)) {
      audit(actor, existing, "USERADMIN_CHANGE_MEMBER_ROLES", AdminAuditEvent.Result.DENIED, "self-admin-role-removal-denied", correlationId);
      throw new AuthorizationException(403, "self-admin-role-removal-denied");
    }
    var updated = new Membership(existing.membershipId(), existing.accountId(), existing.scopeType(), existing.tenantId(), existing.customerId(), roles, existing.status(), existing.supportAccess(), existing.expiresAt());
    put(updated);
    audit(actor, updated, "USERADMIN_CHANGE_MEMBER_ROLES", AdminAuditEvent.Result.ALLOWED, reason, correlationId);
    return new RoleChangeResult("accepted", "Member roles changed by backend-authoritative User Admin capability.", updated, "trace-useradmin-change-member-roles-" + stableSuffix(idempotencyKey));
  }

  public Membership replaceRoles(AuthContextResolver.ResolvedMe actor, String membershipId, List<FoundationRole> roles, String reason, String correlationId) {
    return changeMemberRoles(actor, membershipId, roles, reason, "legacy-role-replace-" + correlationId, correlationId).membership();
  }

  public StatusTransitionResult updateMemberStatus(AuthContextResolver.ResolvedMe actor, String membershipId, MembershipStatus targetStatus, String reason, String idempotencyKey, String correlationId) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
      throw new AuthorizationException(400, "idempotency-key-required");
    }
    if (targetStatus != MembershipStatus.ACTIVE && targetStatus != MembershipStatus.SUSPENDED && targetStatus != MembershipStatus.REMOVED) {
      throw new AuthorizationException(400, "unsupported-membership-status");
    }
    var existing = membership(membershipId);
    requireManage(actor, existing.scopeType(), existing.tenantId(), existing.customerId());
    if (actor.account().accountId().equals(existing.accountId()) && targetStatus != MembershipStatus.ACTIVE) {
      audit(actor, existing, "USERADMIN_UPDATE_MEMBER_STATUS", AdminAuditEvent.Result.DENIED, "self-disable-denied", correlationId);
      throw new AuthorizationException(403, "self-disable-denied");
    }
    if (existing.status() == targetStatus) {
      audit(actor, existing, "USERADMIN_UPDATE_MEMBER_STATUS", AdminAuditEvent.Result.NO_OP, "no-op idempotency", correlationId);
      return new StatusTransitionResult("no-op", "Requested member status already matches current assignment; idempotency preserved.", existing, "trace-useradmin-update-member-status-" + stableSuffix(idempotencyKey));
    }
    if (targetStatus != MembershipStatus.ACTIVE && wouldRemoveLastAdmin(existing, existing.roles(), targetStatus)) {
      audit(actor, existing, "USERADMIN_UPDATE_MEMBER_STATUS", AdminAuditEvent.Result.DENIED, "last-admin-denied", correlationId);
      throw new AuthorizationException(403, "last-admin-denied");
    }
    var updated = new Membership(existing.membershipId(), existing.accountId(), existing.scopeType(), existing.tenantId(), existing.customerId(), existing.roles(), targetStatus, existing.supportAccess(), existing.expiresAt());
    put(updated);
    audit(actor, updated, "USERADMIN_UPDATE_MEMBER_STATUS", AdminAuditEvent.Result.ALLOWED, reason, correlationId);
    var label = targetStatus == MembershipStatus.ACTIVE ? "reactivated" : targetStatus == MembershipStatus.REMOVED ? "deactivated" : "disabled";
    return new StatusTransitionResult("accepted", "Member " + label + " by backend-authoritative User Admin capability.", updated, "trace-useradmin-update-member-status-" + stableSuffix(idempotencyKey));
  }

  public PermanentRemoveResult permanentlyRemoveUser(AuthContextResolver.ResolvedMe actor, String membershipId, String reason, String idempotencyKey, String correlationId) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
      throw new AuthorizationException(400, "idempotency-key-required");
    }
    var existing = membership(membershipId);
    requireManage(actor, existing.scopeType(), existing.tenantId(), existing.customerId());
    var blockers = purgeBlockers(actor, existing);
    if (!blockers.isEmpty()) {
      audit(actor, existing, "USERADMIN_PERMANENTLY_REMOVE_USER", AdminAuditEvent.Result.DENIED, String.join(",", blockers), correlationId);
      throw new AuthorizationException(403, "purge-blocked:" + String.join(",", blockers));
    }
    repository.deleteMembership(existing.membershipId());
    if (repository.membershipsByAccount(existing.accountId()).isEmpty()) {
      repository.deleteSettings(existing.accountId());
      repository.deleteProfile(existing.accountId());
      repository.deleteAccount(existing.accountId());
    }
    audit(actor, existing, "USERADMIN_PERMANENTLY_REMOVE_USER", AdminAuditEvent.Result.ALLOWED, reason, correlationId);
    return new PermanentRemoveResult("accepted", "User record permanently removed; profile, settings, account, and membership data were discarded where no other memberships remain.", existing.accountId(), existing.membershipId(), "trace-useradmin-permanently-remove-user-" + stableSuffix(idempotencyKey));
  }

  private List<String> purgeBlockers(AuthContextResolver.ResolvedMe actor, Membership target) {
    var blockers = new java.util.ArrayList<String>();
    if (target.status() != MembershipStatus.REMOVED) blockers.add("not-deactivated");
    if (actor.account().accountId().equals(target.accountId())) blockers.add("self-purge-denied");
    if (wouldRemoveLastAdmin(target, target.roles(), MembershipStatus.REMOVED)) blockers.add("last-admin-denied");
    if (target.supportAccess()) blockers.add("active-support-access");
    if (repository.membershipsByAccount(target.accountId()).stream().anyMatch(m -> !m.membershipId().equals(target.membershipId()) && m.status() == MembershipStatus.ACTIVE)) blockers.add("active-membership-ownership");
    if (hasAuditBackedPurgeBlocker(target, "LEGAL_HOLD_ACTIVE")) blockers.add("legal-hold");
    if (hasAuditBackedPurgeBlocker(target, "RETENTION_POLICY_HOLD")) blockers.add("retention-policy-hold");
    if (hasAuditBackedPurgeBlocker(target, "BILLING_OBLIGATION_OPEN")) blockers.add("pending-billing");
    if (hasAuditBackedPurgeBlocker(target, "PENDING_APPROVAL_OWNERSHIP")) blockers.add("pending-approval");
    if (hasAuditBackedPurgeBlocker(target, "OPEN_WORKFLOW_OWNERSHIP")) blockers.add("open-workflow-ownership");
    if (hasAuditBackedPurgeBlocker(target, "CRITICAL_RESOURCE_OWNER")) blockers.add("critical-resource-owner");
    return blockers;
  }

  private boolean hasAuditBackedPurgeBlocker(Membership target, String actionType) {
    return repository.auditEvents().stream()
        .filter(event -> actionType.equals(event.actionType()))
        .anyMatch(event -> target.accountId().equals(event.targetAccountId()) || target.membershipId().equals(event.targetMembershipId()));
  }

  public Membership suspendMembership(AuthContextResolver.ResolvedMe actor, String membershipId, String reason, String correlationId) {
    return updateMemberStatus(actor, membershipId, MembershipStatus.SUSPENDED, reason, "legacy-suspend-" + correlationId, correlationId).membership();
  }

  public Membership reactivateMembership(AuthContextResolver.ResolvedMe actor, String membershipId, String reason, String correlationId) {
    return updateMemberStatus(actor, membershipId, MembershipStatus.ACTIVE, reason, "legacy-reactivate-" + correlationId, correlationId).membership();
  }

  public Account disableAccount(AuthContextResolver.ResolvedMe actor, String accountId, String reason, String correlationId) {
    var memberships = repository.membershipsByAccount(accountId);
    for (var membership : memberships) {
      requireManage(actor, membership.scopeType(), membership.tenantId(), membership.customerId());
      if (actor.account().accountId().equals(membership.accountId())) {
        audit(actor, membership, "ACCOUNT_DISABLE", AdminAuditEvent.Result.DENIED, "self-disable-denied", correlationId);
        throw new AuthorizationException(403, "self-disable-denied");
      }
      if (wouldRemoveLastAdmin(membership, membership.roles(), MembershipStatus.SUSPENDED)) {
        audit(actor, membership, "ACCOUNT_DISABLE", AdminAuditEvent.Result.DENIED, "last-admin-denied", correlationId);
        throw new AuthorizationException(403, "last-admin-denied");
      }
    }
    var account = account(accountId);
    if (account.status() == AccountStatus.DISABLED) {
      audit(actor, memberships.isEmpty() ? null : memberships.get(0), "ACCOUNT_DISABLE", AdminAuditEvent.Result.NO_OP, "already-disabled", correlationId);
      return account;
    }
    var disabled = new Account(account.accountId(), account.workosUserId(), account.normalizedEmail(), account.displayEmail(), AccountStatus.DISABLED, account.identityLinkState());
    repository.saveAccount(disabled);
    audit(actor, memberships.isEmpty() ? null : memberships.get(0), "ACCOUNT_DISABLE", AdminAuditEvent.Result.ALLOWED, reason, correlationId);
    return disabled;
  }

  public Account reactivateAccount(AuthContextResolver.ResolvedMe actor, String accountId, String reason, String correlationId) {
    var memberships = repository.membershipsByAccount(accountId);
    for (var membership : memberships) {
      requireManage(actor, membership.scopeType(), membership.tenantId(), membership.customerId());
    }
    var account = account(accountId);
    if (account.status() == AccountStatus.ACTIVE) {
      audit(actor, memberships.isEmpty() ? null : memberships.get(0), "ACCOUNT_REACTIVATE", AdminAuditEvent.Result.NO_OP, "already-active", correlationId);
      return account;
    }
    var active = new Account(account.accountId(), account.workosUserId(), account.normalizedEmail(), account.displayEmail(), AccountStatus.ACTIVE, account.identityLinkState());
    repository.saveAccount(active);
    audit(actor, memberships.isEmpty() ? null : memberships.get(0), "ACCOUNT_REACTIVATE", AdminAuditEvent.Result.ALLOWED, reason, correlationId);
    return active;
  }

  public Membership updateSupportAccess(AuthContextResolver.ResolvedMe actor, String membershipId, boolean enabled, Instant expiresAt, String reason, String idempotencyKey, String correlationId) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
      throw new AuthorizationException(400, "idempotency-key-required");
    }
    var existing = membership(membershipId);
    requireManage(actor, existing.scopeType(), existing.tenantId(), existing.customerId());
    var capability = existing.scopeType() == ScopeType.CUSTOMER ? "customer.user.manage" : existing.scopeType() == ScopeType.SAAS_OWNER ? "saas_owner.user.manage" : "tenant.support_access.manage";
    if (!actor.selectedContext().hasCapability(capability)) {
      audit(actor, existing, enabled ? "SUPPORT_ACCESS_GRANT" : "SUPPORT_ACCESS_REVOKE", AdminAuditEvent.Result.DENIED, "missing-capability:" + capability, correlationId);
      throw new AuthorizationException(403, "missing-capability:" + capability);
    }
    if (existing.supportAccess() == enabled && java.util.Objects.equals(existing.expiresAt(), expiresAt)) {
      audit(actor, existing, enabled ? "SUPPORT_ACCESS_GRANT" : "SUPPORT_ACCESS_REVOKE", AdminAuditEvent.Result.NO_OP, "no-op idempotency", correlationId);
      return existing;
    }
    var updated = new Membership(existing.membershipId(), existing.accountId(), existing.scopeType(), existing.tenantId(), existing.customerId(), existing.roles(), existing.status(), enabled, enabled ? expiresAt : null);
    put(updated);
    audit(actor, updated, enabled ? "SUPPORT_ACCESS_GRANT" : "SUPPORT_ACCESS_REVOKE", AdminAuditEvent.Result.ALLOWED, reason, correlationId);
    return updated;
  }

  public IdentityRelinkResult requestIdentityRelink(AuthContextResolver.ResolvedMe actor, String accountId, String reason, String idempotencyKey, String correlationId) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) throw new AuthorizationException(400, "idempotency-key-required");
    var memberships = repository.membershipsByAccount(accountId);
    for (var membership : memberships) requireManage(actor, membership.scopeType(), membership.tenantId(), membership.customerId());
    var target = account(accountId);
    audit(actor, memberships.isEmpty() ? null : memberships.get(0), "IDENTITY_RELINK_REQUEST", AdminAuditEvent.Result.ALLOWED, reason, correlationId);
    return new IdentityRelinkResult("approval-required", "Identity relink requested; completion requires backend policy approval evidence.", target.accountId(), "trace-useradmin-identity-relink-request-" + stableSuffix(idempotencyKey));
  }

  public IdentityRelinkResult completeIdentityRelink(AuthContextResolver.ResolvedMe actor, String accountId, String approvalRef, String idempotencyKey, String correlationId) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) throw new AuthorizationException(400, "idempotency-key-required");
    if (approvalRef == null || approvalRef.isBlank()) throw new AuthorizationException(400, "approval-ref-required");
    var memberships = repository.membershipsByAccount(accountId);
    for (var membership : memberships) requireManage(actor, membership.scopeType(), membership.tenantId(), membership.customerId());
    var target = account(accountId);
    audit(actor, memberships.isEmpty() ? null : memberships.get(0), "IDENTITY_RELINK_COMPLETE", AdminAuditEvent.Result.ALLOWED, "approval:" + approvalRef, correlationId);
    return new IdentityRelinkResult("accepted", "Identity relink completion recorded with approval evidence; WorkOS subject mutation remains provider-boundary controlled.", target.accountId(), "trace-useradmin-identity-relink-complete-" + stableSuffix(idempotencyKey));
  }

  void requireAccessReviewRead(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    requireRead(actor, scopeType, tenantId, customerId);
  }

  void requireAccessReviewManage(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    requireManage(actor, scopeType, tenantId, customerId);
  }

  void auditAccessReview(AuthContextResolver.ResolvedMe actor, ai.first.domain.coreapp.useradmin.AccessReviewTask task, String action, AdminAuditEvent.Result result, String reason, String correlationId) {
    repository.appendAudit(new AdminAuditEvent(UUID.randomUUID().toString(), Instant.now(clock), correlationId, actor.account().accountId(), actor.selectedContext().membershipId(), task.scopeType(), task.tenantId(), task.customerId(), task.startedByAccountId(), task.taskId(), action, result, reason, reason, "BROWSER_SAFE"));
  }

  private void requireRead(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    requireScope(actor, scopeType, tenantId, customerId);
    var capability = scopeType == ScopeType.CUSTOMER ? "customer.user.read" : scopeType == ScopeType.SAAS_OWNER ? "saas_owner.user.manage" : "tenant.user.read";
    if (!actor.selectedContext().hasCapability(capability)) {
      audit(actor, null, "USER_DIRECTORY_SEARCH", AdminAuditEvent.Result.DENIED, "missing-capability:" + capability, actor.correlationId());
      throw new AuthorizationException(403, "missing-capability:" + capability);
    }
  }

  private void requireManage(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    requireScope(actor, scopeType, tenantId, customerId);
    var capability = scopeType == ScopeType.CUSTOMER ? "customer.user.manage" : scopeType == ScopeType.SAAS_OWNER ? "saas_owner.user.manage" : "tenant.user.manage";
    if (!actor.selectedContext().hasCapability(capability)) {
      throw new AuthorizationException(403, "missing-capability:" + capability);
    }
  }

  private void requireAuditRead(AuthContextResolver.ResolvedMe actor) {
    var scopeType = actor.selectedContext().scopeType();
    var capability = scopeType == ScopeType.CUSTOMER ? "customer.audit.read" : scopeType == ScopeType.SAAS_OWNER ? "saas_owner.audit.read" : "tenant.audit.read";
    if (!actor.selectedContext().hasCapability(capability)) {
      audit(actor, null, "AUDIT_EVENTS_SEARCH", AdminAuditEvent.Result.DENIED, "missing-capability:" + capability, actor.correlationId());
      throw new AuthorizationException(403, "missing-capability:" + capability);
    }
  }

  private void requireScope(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    var auth = actor.selectedContext();
    if (scopeType == ScopeType.SAAS_OWNER && auth.scopeType() != ScopeType.SAAS_OWNER) {
      throw new AuthorizationException(403, "scope-forbidden");
    }
    if (scopeType == ScopeType.TENANT && (auth.scopeType() == ScopeType.SAAS_OWNER || !tenantId.equals(auth.tenantId()))) {
      throw new AuthorizationException(403, "tenant-mismatch");
    }
    if (scopeType == ScopeType.CUSTOMER && (!tenantId.equals(auth.tenantId()) || (auth.scopeType() == ScopeType.CUSTOMER && !customerId.equals(auth.customerId())))) {
      throw new AuthorizationException(403, "customer-mismatch");
    }
  }

  private void ensureAssignable(AuthContextResolver.ResolvedMe actor, List<FoundationRole> roles) {
    if (roles.contains(FoundationRole.SAAS_OWNER_ADMIN) && actor.selectedContext().scopeType() != ScopeType.SAAS_OWNER) {
      throw new AuthorizationException(403, "role-escalation-denied");
    }
    if (roles.stream().anyMatch(role -> role.defaultScopeType() == ScopeType.TENANT) && actor.selectedContext().scopeType() == ScopeType.CUSTOMER) {
      throw new AuthorizationException(403, "role-escalation-denied");
    }
  }

  private boolean wouldRemoveLastAdmin(Membership target, List<FoundationRole> newRoles, MembershipStatus newStatus) {
    if (!target.roles().contains(adminRole(target.scopeType()))) {
      return false;
    }
    var targetStillAdmin = newStatus == MembershipStatus.ACTIVE && newRoles.contains(adminRole(target.scopeType()));
    if (targetStillAdmin) {
      return false;
    }
    return repository.membershipRows().stream()
        .filter(m -> !m.membershipId().equals(target.membershipId()))
        .filter(m -> m.status() == MembershipStatus.ACTIVE)
        .filter(m -> target.scopeType() == m.scopeType())
        .filter(m -> java.util.Objects.equals(target.tenantId(), m.tenantId()))
        .filter(m -> java.util.Objects.equals(target.customerId(), m.customerId()))
        .noneMatch(m -> m.roles().contains(adminRole(target.scopeType())));
  }

  private boolean wouldRemoveOwnAdminRole(AuthContextResolver.ResolvedMe actor, Membership target, List<FoundationRole> newRoles) {
    return actor.account().accountId().equals(target.accountId())
        && target.roles().contains(adminRole(target.scopeType()))
        && !newRoles.contains(adminRole(target.scopeType()));
  }

  private FoundationRole adminRole(ScopeType scopeType) {
    return scopeType == ScopeType.CUSTOMER ? FoundationRole.CUSTOMER_ADMIN : scopeType == ScopeType.SAAS_OWNER ? FoundationRole.SAAS_OWNER_ADMIN : FoundationRole.TENANT_ADMIN;
  }

  private List<String> capabilityDelta(List<FoundationRole> currentRoles, List<FoundationRole> requestedRoles) {
    var current = capabilities(currentRoles);
    var requested = capabilities(requestedRoles);
    var added = requested.stream().filter(capability -> !current.contains(capability)).map(capability -> "+" + capability).toList();
    var removed = current.stream().filter(capability -> !requested.contains(capability)).map(capability -> "-" + capability).toList();
    return java.util.stream.Stream.concat(added.stream(), removed.stream()).toList();
  }

  private List<String> affectedWorkstreams(List<FoundationRole> currentRoles, List<FoundationRole> requestedRoles) {
    return capabilityDelta(currentRoles, requestedRoles).stream()
        .map(entry -> entry.startsWith("+") || entry.startsWith("-") ? entry.substring(1) : entry)
        .map(capability -> capability.startsWith("agent_admin") || capability.startsWith("agent.") ? "Agent Admin" : capability.startsWith("audit.") || capability.contains("audit") ? "Audit/Trace" : capability.startsWith("governance.") ? "Governance/Policy" : capability.contains("user") || capability.contains("invitation") || capability.contains("role") ? "User Admin" : "Application workstream")
        .distinct()
        .toList();
  }

  private Set<String> capabilities(List<FoundationRole> roles) {
    var capabilities = new LinkedHashSet<String>();
    for (var role : roles) capabilities.addAll(role.capabilities());
    return capabilities;
  }

  private Account account(String accountId) {
    return repository.findAccountByEmail(accountId).orElseThrow(() -> new AuthorizationException(404, "target-not-found-or-forbidden"));
  }

  private Membership membership(String membershipId) {
    return repository.membership(membershipId).orElseThrow(() -> new AuthorizationException(404, "target-not-found-or-forbidden"));
  }

  private void put(Membership membership) {
    repository.saveMembership(membership);
  }

  private void audit(AuthContextResolver.ResolvedMe actor, Membership membership, String action, AdminAuditEvent.Result result, String reason, String correlationId) {
    repository.appendAudit(new AdminAuditEvent(UUID.randomUUID().toString(), Instant.now(clock), correlationId, actor.account().accountId(), actor.selectedContext().membershipId(), actor.selectedContext().scopeType(), membership == null ? actor.selectedContext().tenantId() : membership.tenantId(), membership == null ? actor.selectedContext().customerId() : membership.customerId(), membership == null ? null : membership.accountId(), membership == null ? null : membership.membershipId(), action, result, reason, reason, "BROWSER_SAFE"));
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(java.util.Objects.requireNonNullElse(value, "user-admin").hashCode(), 36);
  }

  public record UserDirectoryRow(String accountId, String displayName, String membershipId, List<FoundationRole> roles, MembershipStatus status, ScopeType scopeType, String tenantId, String customerId, boolean supportAccess, Instant expiresAt) {}
  public record RoleChangePreview(boolean allowed, boolean noOp, String message, String traceId, List<String> capabilityDelta, List<String> affectedWorkstreams, List<String> policyHints, String lastAdminImpact) {}
  public record RoleChangeResult(String status, String message, Membership membership, String traceId) {}
  public record StatusTransitionResult(String status, String message, Membership membership, String traceId) {}
  public record PermanentRemoveResult(String status, String message, String accountId, String membershipId, String traceId) {}
  public record IdentityRelinkResult(String status, String message, String accountId, String traceId) {}
}

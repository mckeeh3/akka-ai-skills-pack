package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import {{JAVA_BASE_PACKAGE}}.domain.security.AuthContext;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** Resolves browser JWT identity into the local selected AuthContext used by every protected route. */
public final class AuthContextResolver {
  private final IdentityRepository repository;

  public AuthContextResolver(IdentityRepository repository) {
    this.repository = repository;
  }

  public ResolvedMe resolveMe(WorkosIdentity identity, String selectedMembershipId, String correlationId) {
    if (identity.subject() == null || identity.normalizedEmail() == null) {
      throw deny("AUTH_CONTEXT_RESOLVE", null, null, "missing-workos-claims", correlationId);
    }

    var account =
        repository
            .findAccountByWorkosSubject(identity.subject())
            .or(() -> repository.findAccountByEmail(identity.normalizedEmail()).map(found -> linkAccount(found, identity)))
            .orElseThrow(() -> deny("AUTH_CONTEXT_RESOLVE", null, null, "no-local-account-or-invitation", correlationId));

    if (account.status() == AccountStatus.DISABLED || account.status() == AccountStatus.REMOVED) {
      throw deny("AUTH_CONTEXT_RESOLVE", account, null, "account-disabled", correlationId);
    }
    if (account.status() == AccountStatus.INVITED) {
      account = repository.saveAccount(new Account(account.accountId(), account.workosUserId(), account.normalizedEmail(), account.displayEmail(), AccountStatus.ACTIVE, "LINKED"));
    }

    var memberships = repository.membershipsByAccount(account.accountId());
    var activeMemberships = memberships.stream().filter(m -> m.status() == MembershipStatus.ACTIVE).toList();
    if (activeMemberships.isEmpty()) {
      throw deny("AUTH_CONTEXT_RESOLVE", account, null, "no-active-membership", correlationId);
    }

    var selected = selectMembership(activeMemberships, selectedMembershipId, account, correlationId);
    validateScopeState(account, selected, correlationId);
    var authContext =
        new AuthContext(
            account.accountId(),
            account.workosUserId(),
            selected.membershipId(),
            selected.scopeType(),
            selected.tenantId(),
            selected.customerId(),
            selected.roles(),
            selected.capabilities());
    appendAudit("AUTH_CONTEXT_RESOLVE", AdminAuditEvent.Result.ALLOWED, account, selected, "allowed", correlationId);
    var profile = repository.profile(account.accountId());
    var settings = repository.settings(account.accountId()) == null ? new UserSettings(account.accountId(), UserSettings.UiMode.LIGHT) : repository.settings(account.accountId());
    return new ResolvedMe(account, profile, settings, memberships, authContext, correlationId);
  }

  public void requireCapability(AuthContext authContext, String capability) {
    if (!authContext.hasCapability(capability)) {
      throw new AuthorizationException(403, "missing-capability:" + capability);
    }
  }

  public void requireTenant(AuthContext authContext, String tenantId) {
    if (authContext.scopeType() == ScopeType.SAAS_OWNER || !tenantId.equals(authContext.tenantId())) {
      throw new AuthorizationException(403, "tenant-mismatch");
    }
  }

  public void requireCustomer(AuthContext authContext, String tenantId, String customerId) {
    requireTenant(authContext, tenantId);
    if (authContext.scopeType() == ScopeType.CUSTOMER && !customerId.equals(authContext.customerId())) {
      throw new AuthorizationException(403, "customer-mismatch");
    }
  }

  private Account linkAccount(Account account, WorkosIdentity identity) {
    if (account.workosUserId() != null && !account.workosUserId().equals(identity.subject())) {
      throw new AuthorizationException(403, "workos-subject-already-linked");
    }
    return repository.saveAccount(new Account(account.accountId(), identity.subject(), account.normalizedEmail(), account.displayEmail(), account.status(), "LINKED"));
  }

  private Membership selectMembership(List<Membership> activeMemberships, String selectedMembershipId, Account account, String correlationId) {
    if (selectedMembershipId != null && !selectedMembershipId.isBlank()) {
      return activeMemberships.stream()
          .filter(membership -> selectedMembershipId.equals(membership.membershipId()))
          .findFirst()
          .orElseThrow(() -> deny("AUTH_CONTEXT_SELECT", account, null, "selected-membership-forbidden", correlationId));
    }
    return activeMemberships.stream()
        .sorted(Comparator.comparing(Membership::membershipId))
        .findFirst()
        .orElseThrow();
  }

  private void validateScopeState(Account account, Membership membership, String correlationId) {
    if (membership.scopeType() == ScopeType.TENANT
        && repository.tenant(membership.tenantId()).filter(Tenant -> Tenant.active()).isEmpty()) {
      throw deny("AUTH_CONTEXT_RESOLVE", account, membership, "tenant-missing-or-disabled", correlationId);
    }
    if (membership.scopeType() == ScopeType.CUSTOMER
        && repository.customer(membership.tenantId(), membership.customerId()).filter(Customer -> Customer.active()).isEmpty()) {
      throw deny("AUTH_CONTEXT_RESOLVE", account, membership, "customer-missing-or-disabled", correlationId);
    }
  }

  private AuthorizationException deny(String action, Account account, Membership membership, String reason, String correlationId) {
    appendAudit(action, AdminAuditEvent.Result.DENIED, account, membership, reason, correlationId);
    return new AuthorizationException(403, reason);
  }

  private void appendAudit(
      String action,
      AdminAuditEvent.Result result,
      Account account,
      Membership membership,
      String reason,
      String correlationId) {
    repository.appendAudit(
        new AdminAuditEvent(
            UUID.randomUUID().toString(),
            Instant.now(),
            correlationId,
            account == null ? null : account.accountId(),
            membership == null ? null : membership.membershipId(),
            membership == null ? null : membership.scopeType(),
            membership == null ? null : membership.tenantId(),
            membership == null ? null : membership.customerId(),
            account == null ? null : account.accountId(),
            membership == null ? null : membership.membershipId(),
            action,
            result,
            reason,
            reason,
            "BROWSER_SAFE"));
  }

  public record ResolvedMe(
      Account account,
      {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile profile,
      UserSettings settings,
      List<Membership> memberships,
      AuthContext selectedContext,
      String correlationId) {}
}

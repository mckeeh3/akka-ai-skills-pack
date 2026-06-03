package ai.first.application.agentfoundation;

import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;
import ai.first.application.security.AuthContextResolver;
import ai.first.application.security.AuthorizationException;
import ai.first.application.security.IdentityRepository;
import ai.first.application.security.InvitationView;
import ai.first.application.security.UserAdminService;
import ai.first.domain.security.AccountStatus;
import ai.first.domain.security.AuthContext;
import ai.first.domain.security.UserSettings;
import java.util.List;
import java.util.Objects;

/** Request-scoped read-only User Admin evidence facade for UserAdminAgent. */
public final class UserAdminEvidenceTools {
  public static final String TOOL_ID = "userAdminEvidence.read";
  public static final String CAPABILITY_ID = "tenant.user.read";

  private final IdentityRepository identityRepository;
  private final UserAdminService userAdminService;
  private final InvitationView invitationView;
  private final AuthContext authContext;
  private final String correlationId;

  public UserAdminEvidenceTools(
      IdentityRepository identityRepository,
      UserAdminService userAdminService,
      InvitationView invitationView,
      AuthContext authContext,
      String correlationId) {
    this.identityRepository = Objects.requireNonNull(identityRepository);
    this.userAdminService = Objects.requireNonNull(userAdminService);
    this.invitationView = Objects.requireNonNull(invitationView);
    this.authContext = Objects.requireNonNull(authContext);
    this.correlationId = correlationId == null || correlationId.isBlank() ? "user-admin-evidence" : correlationId;
  }

  @FunctionTool(description = """
      Read scoped, browser-safe User Admin evidence for the selected AuthContext.
      This is a read-only DATA_LOOKUP tool: it summarizes members, invitations, role/status facts,
      and recent audit trace ids without mutating invitations, memberships, roles, accounts, authorization,
      provider configuration, or audit policy. It enforces tenant/customer scope and tenant.user.read before returning data.
      """)
  public String read(@Description("Optional plain-language evidence focus; may include tenantId=<id> only for the selected tenant") String evidenceRequest) {
    var actor = actor();
    requireReadCapability();
    requireRequestedScope(evidenceRequest);
    var members = userAdminService.listUsers(actor, authContext.scopeType(), authContext.tenantId(), authContext.customerId());
    var invitations = invitationView.list(actor, authContext.scopeType(), authContext.tenantId(), authContext.customerId());
    var audits = userAdminService.auditEvents(actor, 5, correlationId).stream()
        .map(event -> event.actionType() + ":" + event.result() + ":" + event.correlationId())
        .toList();
    var traceId = "trace-useradmin-evidence-" + stableSuffix(correlationId + ":" + authContext.membershipId());
    return "tool_id=" + TOOL_ID
        + "\ncapability=" + CAPABILITY_ID
        + "\nmode=read_only_no_direct_mutation"
        + "\nselectedTenantId=" + safe(authContext.tenantId())
        + "\nselectedCustomerId=" + safe(authContext.customerId())
        + "\nmemberCount=" + members.size()
        + "\nmembers=" + members.stream().limit(10).map(row -> "{membershipId=" + row.membershipId() + ", accountRef=" + safe(row.accountId()) + ", displayName=" + safe(row.displayName()) + ", roles=" + row.roles() + ", status=" + row.status() + ", traceId=trace-useradmin-user-" + stableSuffix(row.membershipId()) + "}").toList()
        + "\ninvitationCount=" + invitations.size()
        + "\ninvitations=" + invitations.stream().limit(10).map(invite -> "{invitationId=" + invite.invitationId() + ", targetEmail=" + safe(invite.targetEmail()) + ", requestedRoles=" + invite.requestedRoles() + ", status=" + invite.status() + ", delivery=" + invite.deliveryStatus() + ", canResend=" + invite.canResend() + ", canRevoke=" + invite.canRevoke() + ", traceId=trace-useradmin-invitation-" + stableSuffix(invite.invitationId()) + "}").toList()
        + "\nrecentAuditEvidence=" + audits
        + "\ntraceId=" + traceId
        + "\nredaction=raw invitation tokens, token hashes, provider credentials, JWTs, and cross-tenant data omitted"
        + "\nauthority_note=Evidence is scoped deterministic data only; UserAdminAgent cannot directly mutate access state.";
  }

  private AuthContextResolver.ResolvedMe actor() {
    var account = identityRepository.findAccountByEmail(authContext.accountId())
        .orElseThrow(() -> new AuthorizationException(403, "evidence-account-not-found"));
    if (account.status() != AccountStatus.ACTIVE) {
      throw new AuthorizationException(403, "evidence-account-not-active");
    }
    var memberships = identityRepository.membershipsByAccount(account.accountId());
    var selected = memberships.stream()
        .filter(membership -> membership.membershipId().equals(authContext.membershipId()))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(403, "evidence-membership-not-found"));
    if (!selected.tenantId().equals(authContext.tenantId()) || !Objects.equals(selected.customerId(), authContext.customerId())) {
      throw new AuthorizationException(403, "evidence-auth-context-mismatch");
    }
    var profile = identityRepository.profile(account.accountId());
    var settings = identityRepository.settings(account.accountId()) == null ? new UserSettings(account.accountId(), UserSettings.ThemeId.AURORA_LIGHT) : identityRepository.settings(account.accountId());
    return new AuthContextResolver.ResolvedMe(account, profile, settings, memberships, authContext, correlationId);
  }

  private void requireReadCapability() {
    if (!authContext.hasCapability(CAPABILITY_ID)) {
      throw new AuthorizationException(403, "missing-capability:" + CAPABILITY_ID);
    }
  }

  private void requireRequestedScope(String evidenceRequest) {
    if (evidenceRequest == null) return;
    var marker = "tenantId=";
    var index = evidenceRequest.indexOf(marker);
    if (index < 0) return;
    var requested = evidenceRequest.substring(index + marker.length()).split("[\\s,;]")[0].trim();
    if (!requested.isBlank() && !requested.equals(authContext.tenantId())) {
      throw new AuthorizationException(403, "evidence-tenant-mismatch");
    }
  }

  private static String safe(String value) {
    if (value == null) return "null";
    return value.replaceAll("(?i)(token|secret|api[_-]?key|credential)[^,\\s]*", "[REDACTED]");
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "user-admin-evidence").hashCode(), 36);
  }
}

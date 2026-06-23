package ai.first.application.coreapp.myaccount;

import ai.first.domain.foundation.identity.Account;
import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.IdentityRepository;
import ai.first.application.coreapp.myaccount.MyAccountService;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.AuthContext;
import ai.first.domain.foundation.identity.UserSettings;
import java.util.Objects;

/** Request-scoped read-only My Account evidence facade for MyAccountAgent. */
public final class MyAccountEvidenceTools {
  public static final String TOOL_ID = "myAccountEvidence.read";
  public static final String CAPABILITY_ID = MyAccountService.VIEW_SUMMARY_CAPABILITY;

  private final IdentityRepository identityRepository;
  private final MyAccountService myAccountService;
  private final AuthContext authContext;
  private final String correlationId;

  public MyAccountEvidenceTools(
      IdentityRepository identityRepository,
      MyAccountService myAccountService,
      AuthContext authContext,
      String correlationId) {
    this.identityRepository = Objects.requireNonNull(identityRepository);
    this.myAccountService = Objects.requireNonNull(myAccountService);
    this.authContext = Objects.requireNonNull(authContext);
    this.correlationId = correlationId == null || correlationId.isBlank() ? "my-account-evidence" : correlationId;
  }

  @FunctionTool(description = """
      Read scoped, browser-safe My Account evidence for the selected AuthContext.
      This is a read-only DATA_LOOKUP tool: it summarizes account/profile/settings, selected context,
      authority basis, personal attention, own trace refs, provider-blocked cues, and safe workstream navigation.
      It cannot update profile/settings, switch context, open workstreams, change roles, mutate policies,
      alter agent behavior, expose hidden prompts, or redact traces. It enforces selected tenant/customer scope
      plus My Account view, context, attention, and trace capabilities before returning data.
      """)
  public String read(@Description("Optional plain-language evidence focus; may include tenantId=<id> only for the selected tenant") String evidenceRequest) {
    requireRequestedScope(evidenceRequest);
    var actor = actor();
    var summary = myAccountService.summary(actor, correlationId);
    var dashboard = myAccountService.dashboardData(actor, correlationId);
    var traceId = "trace-myaccount-evidence-" + stableSuffix(correlationId + ":" + authContext.membershipId());
    return "tool_id=" + TOOL_ID
        + "\ncapability=" + CAPABILITY_ID
        + "\nmode=read_only_no_direct_mutation"
        + "\nselectedTenantId=" + safe(authContext.tenantId())
        + "\nselectedCustomerId=" + safe(authContext.customerId())
        + "\nselectedContextId=" + safe(authContext.membershipId())
        + "\nprofile={displayName=" + safe(actor.profile().displayName()) + ", preferredThemeId=" + actor.settings().themeId().id() + "}"
        + "\nauthorityBasis=" + summary.authorityBasis()
        + "\ncapabilityGroups=" + summary.capabilityGroups()
        + "\npersonalAttention=" + dashboard.attentionItems()
        + "\nsafeWorkstreamNavigation=" + dashboard.nextSteps()
        + "\nownTraceRefs=" + summary.traceRefs()
        + "\nproviderGuidance=blocked_provider_or_runtime must be returned as system_message guidance; provider secrets are never exposed"
        + "\ntraceId=" + traceId
        + "\nredaction=rawJwt, providerSecret, hiddenPromptText, cross-tenant data, hidden workstream names, and unauthorized trace evidence omitted"
        + "\nauthority_note=Evidence is scoped deterministic data only; MyAccountAgent has no direct mutation, context-switch, workstream-open, role, policy, behavior, or trace-redaction authority.";
  }

  private AuthContextResolver.ResolvedMe actor() {
    var account = identityRepository.findAccountByEmail(authContext.accountId())
        .orElseThrow(() -> new AuthorizationException(403, "my-account-evidence-account-not-found"));
    if (account.status() != AccountStatus.ACTIVE) {
      throw new AuthorizationException(403, "my-account-evidence-account-not-active");
    }
    var memberships = identityRepository.membershipsByAccount(account.accountId());
    var selected = memberships.stream()
        .filter(membership -> membership.membershipId().equals(authContext.membershipId()))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(403, "my-account-evidence-membership-not-found"));
    if (!Objects.equals(selected.tenantId(), authContext.tenantId()) || !Objects.equals(selected.customerId(), authContext.customerId())) {
      throw new AuthorizationException(403, "my-account-evidence-auth-context-mismatch");
    }
    var profile = identityRepository.profile(account.accountId());
    var settings = identityRepository.settings(account.accountId()) == null ? new UserSettings(account.accountId(), UserSettings.ThemeId.AURORA_LIGHT) : identityRepository.settings(account.accountId());
    return new AuthContextResolver.ResolvedMe(account, profile, settings, memberships, authContext, correlationId);
  }

  private void requireRequestedScope(String evidenceRequest) {
    if (evidenceRequest == null) return;
    var marker = "tenantId=";
    var index = evidenceRequest.indexOf(marker);
    if (index < 0) return;
    var requested = evidenceRequest.substring(index + marker.length()).split("[\\s,;]")[0].trim();
    if (!requested.isBlank() && !Objects.equals(requested, authContext.tenantId())) {
      throw new AuthorizationException(403, "my-account-evidence-tenant-mismatch");
    }
  }

  private static String safe(String value) {
    if (value == null) return "null";
    return value.replaceAll("(?i)(rawJwt|providerSecret|hiddenPromptText|api[_-]?key|secret|token|credential)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "my-account-evidence").hashCode(), 36);
  }
}

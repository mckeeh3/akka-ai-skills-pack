package ai.first.application.agentfoundation;

import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.governance.GovernancePolicyService;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.AuthContext;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Request-scoped read-only Governance/Policy evidence facade for GovernancePolicyAgent. */
public final class GovernancePolicyEvidenceTools {
  public static final String TOOL_ID = "governancePolicyEvidence.read";
  public static final String CAPABILITY_ID = GovernancePolicyService.READ_CAPABILITY;

  private final GovernancePolicyService governancePolicyService;
  private final AuthContext authContext;
  private final String correlationId;

  public GovernancePolicyEvidenceTools(GovernancePolicyService governancePolicyService, AuthContext authContext, String correlationId) {
    this.governancePolicyService = Objects.requireNonNull(governancePolicyService);
    this.authContext = Objects.requireNonNull(authContext);
    this.correlationId = correlationId == null || correlationId.isBlank() ? "governance-policy-evidence" : correlationId;
  }

  @FunctionTool(description = """
      Read scoped, browser-safe Governance/Policy evidence for the selected AuthContext.
      This is a read-only DATA_LOOKUP tool over deterministic GovernancePolicyService dashboard, inventory, policy detail, proposal, simulation, decision, activation-blocker, and rollback-blocker evidence.
      It cannot approve, reject, activate, roll back, mutate policies, mutate users, mutate agent behavior, alter ToolPermissionBoundary records, start workers, bypass authorization, or reveal hidden prompts/provider secrets.
      It enforces selected tenant/customer scope and governance.policy.read before returning model-visible evidence summaries.
      """)
  public String read(@Description("Optional evidence focus; may include tenantId=<selected tenant>, customerId=<selected customer>, policyId=<id>, proposalId=<id>, or filter=<text>") String evidenceRequest) {
    requireCapability(CAPABILITY_ID);
    requireRequestedScope(evidenceRequest);
    var actor = syntheticActor();
    var dashboard = governancePolicyService.dashboard(actor, correlationId);
    var inventory = governancePolicyService.inventory(actor, correlationId);
    var detail = governancePolicyService.detail(actor, Map.of("policyId", valueAfter(evidenceRequest, "policyId=").orElse("policy-human-approval")), correlationId);
    var traceId = "trace-governancepolicy-evidence-" + stableSuffix(correlationId + ":" + authContext.membershipId());
    return "tool_id=" + TOOL_ID
        + "\ncapability=" + CAPABILITY_ID
        + "\nmode=read_only_no_direct_mutation"
        + "\nselectedTenantId=" + safe(authContext.tenantId())
        + "\nselectedCustomerId=" + safe(authContext.customerId())
        + "\ndashboardSurface=" + safe(dashboard.surfaceId()) + ":" + safe(Objects.toString(dashboard.data().get("surfaceContract"), "governance.policy.dashboard.v1"))
        + "\ninventorySurface=" + safe(inventory.surfaceId()) + ":" + safe(Objects.toString(inventory.data().get("surfaceContract"), "governance.policy.inventory.v1"))
        + "\ndetailSurface=" + safe(detail.surfaceId()) + ":" + safe(Objects.toString(detail.data().get("surfaceContract"), "governance.policy.detail.v1"))
        + "\npolicyPosture=" + safe(Objects.toString(dashboard.data().get("cards"), "[]"))
        + "\nattentionItems=" + safe(Objects.toString(dashboard.data().get("attentionItems"), "[]"))
        + "\npolicyRows=" + safe(Objects.toString(inventory.data().get("rows"), "[]"))
        + "\nproposalLifecycle=" + safe(Objects.toString(dashboard.data().get("proposalLifecycle"), "[]"))
        + "\ntraceId=" + traceId
        + "\ntraceTypes=PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, ToolPermissionBoundary, AgentWorkTrace"
        + "\nprovider_boundary=provider/model readiness is enforced by governed Agent runtime; missing provider returns safe system_message, not a deterministic fake answer"
        + "\nredaction=raw prompt bodies, hidden prompt text, provider credentials, provider secret values, JWTs, raw tool payloads, support-only data, and cross-tenant data omitted"
        + "\nauthority_note=Evidence is scoped deterministic data only; GovernancePolicyAgent has no direct mutation, approval, activation, rollback, user-admin, agent-admin, tool-boundary, provider, or worker authority.";
  }

  private void requireCapability(String capabilityId) {
    if (!authContext.hasCapability(capabilityId)) {
      throw new AuthorizationException(403, "missing-capability:" + capabilityId);
    }
  }

  private void requireRequestedScope(String evidenceRequest) {
    if (evidenceRequest == null) return;
    var tenantId = valueAfter(evidenceRequest, "tenantId=").orElse(null);
    if (tenantId != null && !tenantId.isBlank() && !tenantId.equals(authContext.tenantId())) {
      throw new AuthorizationException(403, "governance-policy-evidence-tenant-mismatch");
    }
    var customerId = valueAfter(evidenceRequest, "customerId=").orElse(null);
    if (customerId != null && authContext.customerId() != null && !customerId.equals(authContext.customerId())) {
      throw new AuthorizationException(403, "governance-policy-evidence-customer-mismatch");
    }
  }

  private AuthContextResolver.ResolvedMe syntheticActor() {
    var account = new Account(authContext.accountId(), authContext.workosUserId(), authContext.accountId() + "@redacted.local", authContext.accountId() + "@redacted.local", AccountStatus.ACTIVE, "RUNTIME_TOOL_CONTEXT");
    var profile = new UserProfile(authContext.accountId(), account.displayEmail(), "Governance/Policy runtime actor", "Governance", "Policy", null);
    var settings = new UserSettings(authContext.accountId(), UserSettings.ThemeId.AURORA_LIGHT);
    var membership = new Membership(authContext.membershipId(), authContext.accountId(), authContext.scopeType(), authContext.tenantId(), authContext.customerId(), authContext.roles(), MembershipStatus.ACTIVE, false, null);
    return new AuthContextResolver.ResolvedMe(account, profile, settings, List.of(membership), authContext, correlationId);
  }

  private java.util.Optional<String> valueAfter(String text, String marker) {
    if (text == null) return java.util.Optional.empty();
    var index = text.indexOf(marker);
    if (index < 0) return java.util.Optional.empty();
    var value = text.substring(index + marker.length()).split("[\\s,;]")[0].trim();
    return value.isBlank() ? java.util.Optional.empty() : java.util.Optional.of(value);
  }

  private static String safe(String value) {
    if (value == null) return "null";
    return value.replaceAll("(?i)(api[_-]?key|secret|token|credential|bearer)\\s*[:=]?\\s*\\S+", "$1=[REDACTED]");
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "governance-policy-evidence").hashCode(), 36);
  }
}

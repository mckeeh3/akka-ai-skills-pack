package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Deterministic My Account boundary for browser-safe account, context, settings, trace, and navigation data. */
public final class MyAccountService {
  public static final String VIEW_SUMMARY_CAPABILITY = "my_account.view_summary";
  public static final String VIEW_CONTEXT_CAPABILITY = "my_account.view_context";
  public static final String UPDATE_PROFILE_SETTINGS_CAPABILITY = "my_account.update_profile_settings";
  public static final String LIST_PERSONAL_ATTENTION_CAPABILITY = "my_account.list_personal_attention";
  public static final String OPEN_AUTHORIZED_WORKSTREAM_CAPABILITY = "my_account.open_authorized_workstream";
  public static final String VIEW_OWN_TRACE_REFS_CAPABILITY = "my_account.view_own_trace_refs";

  private final AuthContextResolver authContextResolver;

  public MyAccountService(AuthContextResolver authContextResolver) {
    this.authContextResolver = authContextResolver;
  }

  public Summary summary(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), VIEW_SUMMARY_CAPABILITY);
    authContextResolver.requireCapability(actor.selectedContext(), VIEW_CONTEXT_CAPABILITY);
    authContextResolver.appendProtectedReadTrace(actor, "MY_ACCOUNT_VIEW_SUMMARY", "selected context authority summary", correlationId);
    return new Summary(
        authorityBasis(actor),
        capabilityGroups(actor),
        traceRefs(actor, correlationId));
  }

  public AuthContextResolver.ProfileSettingsUpdateResult updateProfileSettings(
      AuthContextResolver.ResolvedMe actor,
      String displayName,
      UserSettings.UiMode uiMode,
      String idempotencyKey,
      String correlationId) {
    return authContextResolver.updateOwnProfileSettings(actor, displayName, uiMode, idempotencyKey, correlationId);
  }

  public OpenWorkstreamDecision openAuthorizedWorkstream(
      AuthContextResolver.ResolvedMe actor,
      String actionId,
      String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), OPEN_AUTHORIZED_WORKSTREAM_CAPABILITY);
    var target = switch (actionId) {
      case "action-open-user-admin" -> target("agent-user-admin", "User Admin", "secure-tenant-user-foundation", "surface-user-admin-dashboard");
      case "action-open-agent-admin" -> target("agent-agent-admin", "Agent Admin", "agent_admin.list_definitions", "surface-agent-admin-catalog");
      case "action-open-audit-trace" -> target("agent-audit-trace", "Audit/Trace", "audit.trace.read", "surface-audit-trace-dashboard");
      case "action-open-governance-policy" -> target("agent-governance-policy", "Governance/Policy", "governance.policy.read", "surface-governance-policy-dashboard");
      default -> null;
    };
    if (target == null) {
      authContextResolver.appendDeniedTrace(actor, "MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM", "target-not-found-or-redacted", correlationId);
      return OpenWorkstreamDecision.denied("not_found_or_redacted", "The requested workstream is unavailable or redacted for this context.", correlationId);
    }
    var allowed = actor.selectedContext().capabilities().contains(target.requiredCapabilityId())
        || ("secure-tenant-user-foundation".equals(target.requiredCapabilityId()) && actor.selectedContext().capabilities().contains("secure-tenant-user-foundation"));
    if (!allowed) {
      authContextResolver.appendDeniedTrace(actor, "MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM", "not_found_or_redacted", correlationId);
      return OpenWorkstreamDecision.denied("not_found_or_redacted", "That workstream cannot be opened from this selected context. It may be unavailable or redacted.", correlationId);
    }
    authContextResolver.appendProtectedReadTrace(actor, "MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM", target.functionalAgentId(), correlationId);
    return new OpenWorkstreamDecision("accepted", "Opened authorized workstream through backend authority checks.", target.surfaceId(), target.functionalAgentId(), target.label(), target.requiredCapabilityId(), correlationId, List.of("trace-my-account-open-" + target.functionalAgentId()), null);
  }

  public DashboardData dashboardData(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var summary = summary(actor, correlationId);
    return new DashboardData(
        "my_account.dashboard.v1",
        List.of(
            mapOf("cardId", "card-my-profile", "label", "Profile", "value", actor.profile().displayName(), "severity", "info"),
            mapOf("cardId", "card-my-settings", "label", "Settings", "value", actor.settings().uiMode().name().toLowerCase(Locale.ROOT), "severity", "info"),
            mapOf("cardId", "card-current-context", "label", "Selected context", "value", actor.selectedContext().tenantId(), "severity", "info"),
            mapOf("cardId", "card-authority", "label", "Authority", "value", summary.authorityBasis().primaryRoleBasis(), "severity", "info")),
        List.of(
            mapOf("sectionId", "selected-context", "label", "Selected context", "summary", "selected context is resolved and authorized by the backend AuthContextResolver."),
            mapOf("sectionId", "security-boundary", "label", "Security boundary", "summary", "My Account can explain and route; roles, memberships, policy, and agent behavior changes stay in governed admin workstreams.")),
        personalAttention(actor, correlationId),
        nextSteps(actor),
        summary.traceRefs(),
        summary.authorityBasis(),
        summary.capabilityGroups());
  }

  public List<Map<String, Object>> personalAttention(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), LIST_PERSONAL_ATTENTION_CAPABILITY);
    var capabilities = actor.selectedContext().capabilities();
    var items = new java.util.ArrayList<Map<String, Object>>();
    if (capabilities.contains("secure-tenant-user-foundation")) items.add(mapOf("itemId", "personal-attention-user-admin-invitations", "label", "User Admin invitation delivery needs review", "status", "needs-review", "severity", "warning", "capabilityId", "secure-tenant-user-foundation", "traceId", "trace-my-account-attention-user-admin", "sourceWorkstreamId", "agent-user-admin"));
    if (capabilities.contains("agent_admin.list_definitions")) items.add(mapOf("itemId", "personal-attention-agent-admin-provider", "label", "Agent Admin provider readiness is blocked", "status", "blocked_provider_or_runtime", "severity", "critical", "capabilityId", "agent_admin.list_definitions", "traceId", "trace-my-account-attention-agent-admin", "sourceWorkstreamId", "agent-agent-admin"));
    if (capabilities.contains("governance.policy.read")) items.add(mapOf("itemId", "personal-attention-governance-policy", "label", "Governance policy decision awaits authorized review", "status", "approval-needed", "severity", "critical", "capabilityId", "governance.policy.read", "traceId", "trace-my-account-attention-governance", "sourceWorkstreamId", "agent-governance-policy"));
    if (capabilities.contains("audit.trace.read")) items.add(mapOf("itemId", "personal-attention-audit-trace", "label", "Audit/Trace has provider failure evidence available", "status", "ready", "severity", "info", "capabilityId", "audit.trace.read", "traceId", "trace-my-account-attention-audit", "sourceWorkstreamId", "agent-audit-trace"));
    authContextResolver.appendProtectedReadTrace(actor, "MY_ACCOUNT_LIST_PERSONAL_ATTENTION", "authorized personal attention", correlationId);
    return items;
  }

  public List<Map<String, Object>> nextSteps(AuthContextResolver.ResolvedMe actor) {
    return MeResponse.FunctionalAgentSummary.fromCapabilities(actor.selectedContext().capabilities()).stream()
        .filter(agent -> !"agent-my-account".equals(agent.functionalAgentId()))
        .map(agent -> mapOf(
            "workstreamId", agent.functionalAgentId(),
            "label", agent.label(),
            "allowed", "visible".equals(agent.availability()),
            "blockedReason", agent.deniedReason() == null ? "" : agent.deniedReason(),
            "capabilityIds", agent.requiredCapabilityIds()))
        .toList();
  }

  public List<TraceRef> traceRefs(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), VIEW_OWN_TRACE_REFS_CAPABILITY);
    return List.of(
        new TraceRef("trace-my-account-context-" + actor.selectedContext().membershipId(), "AuthContext", "Selected context resolved", "my_account.view_context", correlationId),
        new TraceRef("trace-my-account-profile-" + actor.account().accountId().hashCode(), "ProfileSettings", "Profile/settings browser-safe read", "my_account.view_summary", correlationId),
        new TraceRef("trace-my-account-personal-attention-" + actor.selectedContext().membershipId(), "PersonalAttention", "Authorized personal attention aggregation", "my_account.list_personal_attention", correlationId));
  }

  private AuthorityBasisSummary authorityBasis(AuthContextResolver.ResolvedMe actor) {
    return new AuthorityBasisSummary(
        actor.selectedContext().membershipId(),
        actor.selectedContext().tenantId(),
        actor.selectedContext().customerId(),
        actor.selectedContext().roles().stream().map(role -> role.name().toLowerCase(Locale.ROOT).replace('_', '-')).sorted().toList(),
        "active membership in selected context",
        actor.selectedContext().capabilities().stream().filter(capability -> capability.startsWith("my_account.")).sorted().toList());
  }

  private List<CapabilityGroupSummary> capabilityGroups(AuthContextResolver.ResolvedMe actor) {
    var capabilities = actor.selectedContext().capabilities();
    return List.of(
        new CapabilityGroupSummary("my_account", "My Account", capabilities.stream().filter(capability -> capability.startsWith("my_account.")).sorted().toList()),
        new CapabilityGroupSummary("workstreams", "Authorized workstream navigation", capabilities.stream().filter(capability -> capability.startsWith("agent_admin.") || capability.startsWith("audit.trace") || capability.startsWith("governance.") || capability.equals("secure-tenant-user-foundation")).sorted().toList()));
  }

  private Target target(String functionalAgentId, String label, String requiredCapabilityId, String surfaceId) {
    return new Target(functionalAgentId, label, requiredCapabilityId, surfaceId);
  }

  private static Map<String, Object> mapOf(Object... values) {
    var map = new LinkedHashMap<String, Object>();
    for (int i = 0; i + 1 < values.length; i += 2) map.put(String.valueOf(values[i]), values[i + 1]);
    return map;
  }

  private record Target(String functionalAgentId, String label, String requiredCapabilityId, String surfaceId) {}

  public record Summary(AuthorityBasisSummary authorityBasis, List<CapabilityGroupSummary> capabilityGroups, List<TraceRef> traceRefs) {}
  public record AuthorityBasisSummary(String selectedContextId, String tenantId, String customerId, List<String> roleIds, String primaryRoleBasis, List<String> myAccountCapabilityIds) {}
  public record CapabilityGroupSummary(String groupId, String label, List<String> capabilityIds) {}
  public record TraceRef(String traceId, String category, String label, String capabilityId, String correlationId) {}
  public record DashboardData(String surfaceContract, List<Map<String, Object>> cards, List<Map<String, Object>> sections, List<Map<String, Object>> attentionItems, List<Map<String, Object>> nextSteps, List<TraceRef> traceRefs, AuthorityBasisSummary authorityBasis, List<CapabilityGroupSummary> capabilityGroups) {}
  public record OpenWorkstreamDecision(String status, String message, String surfaceId, String functionalAgentId, String label, String requiredCapabilityId, String correlationId, List<String> traceIds, String safeReasonCode) {
    static OpenWorkstreamDecision denied(String safeReasonCode, String message, String correlationId) {
      return new OpenWorkstreamDecision("denied", message, "surface-my-account-open-denied", "agent-my-account", "Workstream unavailable", null, correlationId, List.of("trace-my-account-open-denied"), safeReasonCode);
    }
  }
}

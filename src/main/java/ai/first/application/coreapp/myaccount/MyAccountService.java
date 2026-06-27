package ai.first.application.coreapp.myaccount;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AuthContext;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.attention.AttentionCategory;
import ai.first.domain.foundation.attention.AttentionItem;
import ai.first.domain.foundation.attention.AttentionItemStatus;
import ai.first.domain.foundation.attention.AttentionSeverity;
import ai.first.domain.foundation.attention.AttentionSourceRef;
import ai.first.domain.foundation.attention.AttentionSurfaceRef;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.UserSettings;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import ai.first.application.foundation.attention.AttentionService;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.MeResponse;
import ai.first.application.foundation.identity.StarterSecurityComponents;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;

/** Deterministic My Account boundary for browser-safe account, context, settings, trace, and navigation data. */
public final class MyAccountService {
  public static final String CORE_ACCESS_ME_CAPABILITY = "core.access.me";
  public static final String CORE_PROFILE_UPDATE_CAPABILITY = "core.profile.update";
  public static final String CORE_ACCESS_CONTEXT_SELECT_CAPABILITY = "core.access.context.select";
  public static final String VIEW_SUMMARY_CAPABILITY = "my_account.view_summary";
  public static final String VIEW_CONTEXT_CAPABILITY = "my_account.view_context";
  public static final String UPDATE_PROFILE_SETTINGS_CAPABILITY = "my_account.update_profile_settings";
  public static final String LIST_PERSONAL_ATTENTION_CAPABILITY = "my_account.list_personal_attention";
  public static final String OPEN_AUTHORIZED_WORKSTREAM_CAPABILITY = "my_account.open_authorized_workstream";
  public static final String VIEW_OWN_TRACE_REFS_CAPABILITY = "my_account.view_own_trace_refs";

  private final AuthContextResolver authContextResolver;
  private final AttentionService attentionService;

  public MyAccountService(AuthContextResolver authContextResolver) {
    this(authContextResolver, StarterSecurityComponents.attentionService());
  }

  public MyAccountService(AuthContextResolver authContextResolver, AttentionService attentionService) {
    this.authContextResolver = authContextResolver;
    this.attentionService = attentionService;
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
      UserSettings.ThemeId themeId,
      String locale,
      String timeZone,
      String idempotencyKey,
      String correlationId) {
    return authContextResolver.updateOwnProfileSettings(actor, displayName, themeId, locale, timeZone, idempotencyKey, correlationId);
  }

  public OpenWorkstreamDecision openAuthorizedWorkstream(
      AuthContextResolver.ResolvedMe actor,
      String actionId,
      String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), OPEN_AUTHORIZED_WORKSTREAM_CAPABILITY);
    var target = switch (actionId) {
      case "action-open-user-admin" -> target("user-admin-agent", "User Admin", List.of("user_admin.view_overview", "saas_owner.admin.manage", "tenant.user.read", "tenant.user.manage", "customer.user.read", "customer.user.manage"), "surface-user-admin-dashboard");
      case "action-open-agent-admin" -> target("agent-admin-agent", "Agent Admin", List.of("agent_admin.list_definitions"), "surface-agent-admin-dashboard");
      case "action-open-audit-trace" -> target("audit-trace-agent", "Audit/Trace", List.of("audit.trace.read", "saas_owner.audit.read", "tenant.audit.read", "customer.audit.read"), "surface-audit-trace-dashboard");
      case "action-open-governance-policy" -> target("governance-policy-agent", "Governance/Policy", List.of("governance.policy.read"), "surface-governance-policy-dashboard");
      default -> null;
    };
    if (target == null) {
      authContextResolver.appendDeniedTrace(actor, "MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM", "target-not-found-or-redacted", correlationId);
      return OpenWorkstreamDecision.denied("not_found_or_redacted", "The requested workstream is unavailable or redacted for this context.", correlationId);
    }
    if ("action-open-agent-admin".equals(actionId) && !isSaasOwnerAdmin(actor)) {
      authContextResolver.appendDeniedTrace(actor, "MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM", "not_found_or_redacted", correlationId);
      return OpenWorkstreamDecision.denied("not_found_or_redacted", "That workstream cannot be opened from this selected context. It may be unavailable or redacted.", correlationId);
    }
    var allowed = target.requiredCapabilityIds().stream().anyMatch(actor.selectedContext().capabilities()::contains);
    if (!allowed) {
      authContextResolver.appendDeniedTrace(actor, "MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM", "not_found_or_redacted", correlationId);
      return OpenWorkstreamDecision.denied("not_found_or_redacted", "That workstream cannot be opened from this selected context. It may be unavailable or redacted.", correlationId);
    }
    authContextResolver.appendProtectedReadTrace(actor, "MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM", target.functionalAgentId(), correlationId);
    var grantedCapability = target.requiredCapabilityIds().stream().filter(actor.selectedContext().capabilities()::contains).findFirst().orElse(target.requiredCapabilityIds().get(0));
    return new OpenWorkstreamDecision("accepted", "Opened authorized workstream through backend authority checks.", target.surfaceId(), target.functionalAgentId(), target.label(), grantedCapability, correlationId, List.of("trace-my-account-open-" + target.functionalAgentId()), null);
  }

  private boolean isSaasOwnerAdmin(AuthContextResolver.ResolvedMe actor) {
    return actor.selectedContext().scopeType() == ScopeType.SAAS_OWNER
        && actor.selectedContext().roles().contains(FoundationRole.SAAS_OWNER_ADMIN);
  }

  public DashboardData dashboardData(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var summary = summary(actor, correlationId);
    return new DashboardData(
        "my_account.personal_command_center.v1",
        workstreamStatusCards(actor, correlationId),
        List.of(),
        List.of(), // personalAttention(actor, correlationId) is intentionally not rendered on the minimal daily dashboard.
        nextSteps(actor),
        summary.traceRefs(),
        summary.authorityBasis(),
        summary.capabilityGroups());
  }

  public List<Map<String, Object>> personalAttention(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), LIST_PERSONAL_ATTENTION_CAPABILITY);
    seedStarterCoreAttention(actor, correlationId);
    var summary = attentionService.listMyAccountItems(actor, correlationId);
    return summary.personalQueue().stream().map(this::attentionItemMap).toList();
  }

  private void seedStarterCoreAttention(AuthContextResolver.ResolvedMe actor, String correlationId) {
    if (actor.selectedContext().capabilities().contains("agent_admin.list_definitions")) {
      attentionService.upsertItem(actor, attentionItem(actor, "attention-agent-admin-readiness", "agent-admin-agent", "Agent Admin provider readiness is blocked", "Model/runtime provider readiness is blocked until governed provider configuration is available.", AttentionCategory.PROVIDER_READINESS, AttentionSeverity.BLOCKED, "agent_admin.list_definitions", "surface-agent-admin-catalog", "agent-admin-provider-readiness", correlationId), correlationId);
    }
    if (actor.selectedContext().capabilities().contains("governance.policy.read")) {
      attentionService.upsertItem(actor, attentionItem(actor, "attention-governance-policy-approval", "governance-policy-agent", "Governance policy decision awaits authorized review", "Governance/Policy has reviewable policy approval evidence for this selected context.", AttentionCategory.GOVERNANCE_APPROVAL, AttentionSeverity.URGENT, "governance.policy.read", "surface-governance-policy-dashboard", "governance-policy-approval", correlationId), correlationId);
    }
    if (actor.selectedContext().capabilities().contains("audit.trace.read")) {
      attentionService.upsertItem(actor, attentionItem(actor, "attention-audit-trace-failure-evidence", "audit-trace-agent", "Audit/Trace has provider failure evidence available", "Audit/Trace has provider failure or denial evidence available for authorized investigation.", AttentionCategory.AUDIT_FAILURE_EVIDENCE, AttentionSeverity.WARNING, "audit.trace.read", "surface-audit-trace-dashboard", "audit-trace-failure-evidence", correlationId), correlationId);
    }
  }

  private AttentionItem attentionItem(AuthContextResolver.ResolvedMe actor, String itemId, String workstreamId, String title, String summary, AttentionCategory category, AttentionSeverity severity, String capabilityId, String surfaceId, String sourceId, String correlationId) {
    var now = Instant.now();
    return new AttentionItem(itemId, selectedScopeTenantId(actor.selectedContext()), actor.selectedContext().customerId(), workstreamId, title, summary, category, severity, AttentionItemStatus.OPEN, AttentionItem.AssigneeKind.CAPABILITY, capabilityId, capabilityId, new AttentionSurfaceRef(workstreamId, surfaceId, "dashboard", itemId, actionIdForWorkstream(workstreamId), capabilityId), List.of(new AttentionSourceRef("capability", sourceId, title, capabilityId, "trace-" + sourceId, correlationId)), null, now, now, now, null, null, null, null, correlationId);
  }

  private static String selectedScopeTenantId(AuthContext authContext) {
    return authContext.scopeType() == ScopeType.SAAS_OWNER && (authContext.tenantId() == null || authContext.tenantId().isBlank())
        ? WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID
        : authContext.tenantId();
  }

  private Map<String, Object> attentionItemMap(AttentionItem item) {
    var traceId = item.sourceRefs().stream().map(ref -> ref.traceId()).filter(value -> value != null && !value.isBlank()).findFirst().orElse(item.correlationId());
    return mapOf(
        "itemId", item.itemId(),
        "label", item.title(),
        "summary", item.summary(),
        "status", item.status().name().toLowerCase(Locale.ROOT),
        "severity", item.severity().name().toLowerCase(Locale.ROOT),
        "category", item.category().name().toLowerCase(Locale.ROOT),
        "capabilityId", item.requiredCapabilityId(),
        "governedToolId", AttentionService.OPEN_ATTENTION_ITEM_TOOL,
        "traceId", traceId,
        "sourceWorkstreamId", item.owningWorkstreamId(),
        "surfaceRef", item.surfaceRef(),
        "redaction", item.redactionLevel().name().toLowerCase(Locale.ROOT));
  }

  public List<Map<String, Object>> nextSteps(AuthContextResolver.ResolvedMe actor) {
    return MeResponse.FunctionalAgentSummary.fromCapabilities(actor.selectedContext().capabilities()).stream()
        .filter(agent -> !"my-account-agent".equals(agent.functionalAgentId()))
        .filter(agent -> "visible".equals(agent.availability()))
        .map(agent -> mapOf(
            "workstreamId", agent.functionalAgentId(),
            "label", agent.label(),
            "allowed", true,
            "blockedReason", "",
            "capabilityIds", agent.requiredCapabilityIds(),
            "surfaceId", surfaceIdForWorkstream(agent.functionalAgentId()),
            "actionId", actionIdForWorkstream(agent.functionalAgentId())))
        .toList();
  }

  public List<Map<String, Object>> workstreamStatusCards(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var summaryByWorkstream = attentionService.listMyAccountItems(actor, correlationId).workstreams().stream()
        .collect(java.util.stream.Collectors.toMap(AttentionService.WorkstreamAttentionSummary::workstreamId, summary -> summary, (left, right) -> left, LinkedHashMap::new));
    return MeResponse.FunctionalAgentSummary.fromCapabilities(actor.selectedContext().capabilities()).stream()
        .filter(agent -> !"my-account-agent".equals(agent.functionalAgentId()))
        .filter(agent -> "visible".equals(agent.availability()))
        .map(agent -> {
          var summary = summaryByWorkstream.get(agent.functionalAgentId());
          var count = summary == null ? 0 : summary.attentionCount();
          var severity = summary == null ? "info" : summary.highestSeverity().name().toLowerCase(Locale.ROOT);
          return mapOf(
              "cardId", "card-workstream-" + agent.functionalAgentId(),
              "cardKind", "workstream-status",
              "workstreamId", agent.functionalAgentId(),
              "label", agent.label(),
              "value", count,
              "unit", "items need my attention",
              "status", count == 0 ? "No items need attention" : count == 1 ? "1 item needs attention" : count + " items need attention",
              "description", agent.purpose(),
              "severity", severity,
              "surfaceId", surfaceIdForWorkstream(agent.functionalAgentId()),
              "requiredCapabilityId", agent.requiredCapabilityIds().isEmpty() ? "" : agent.requiredCapabilityIds().get(0),
              "redaction", "authorized selected-context summary",
              "actionId", actionIdForWorkstream(agent.functionalAgentId()));
        })
        .toList();
  }

  private String actionIdForWorkstream(String functionalAgentId) {
    return switch (functionalAgentId) {
      case "user-admin-agent" -> "action-open-user-admin";
      case "agent-admin-agent" -> "action-open-agent-admin";
      case "audit-trace-agent" -> "action-open-audit-trace";
      case "governance-policy-agent" -> "action-open-governance-policy";
      default -> "action-show-my-account-dashboard";
    };
  }

  private String surfaceIdForWorkstream(String functionalAgentId) {
    return switch (functionalAgentId) {
      case "agent-admin-agent" -> "surface-agent-admin-dashboard";
      case "audit-trace-agent" -> "surface-audit-trace-dashboard";
      case "governance-policy-agent" -> "surface-governance-policy-dashboard";
      case "user-admin-agent" -> "surface-user-admin-dashboard";
      default -> "surface-my-account-dashboard";
    };
  }

  public List<TraceRef> traceRefs(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), VIEW_OWN_TRACE_REFS_CAPABILITY);
    return List.of(
        new TraceRef("trace-my-account-context-" + actor.selectedContext().membershipId(), "AuthContext", "Selected context resolved", CORE_ACCESS_ME_CAPABILITY, correlationId),
        new TraceRef("trace-my-account-context-select-" + actor.selectedContext().membershipId(), "AuthContext", "Selected context switch targets resolved", CORE_ACCESS_CONTEXT_SELECT_CAPABILITY, correlationId),
        new TraceRef("trace-my-account-profile-" + actor.account().accountId().hashCode(), "ProfileSettings", "Profile/settings browser-safe read", CORE_PROFILE_UPDATE_CAPABILITY, correlationId),
        new TraceRef("trace-my-account-personal-attention-" + actor.selectedContext().membershipId(), "PersonalAttention", "Authorized personal attention aggregation", "my_account.list_personal_attention", correlationId));
  }

  private AuthorityBasisSummary authorityBasis(AuthContextResolver.ResolvedMe actor) {
    return new AuthorityBasisSummary(
        actor.selectedContext().membershipId(),
        actor.selectedContext().tenantId(),
        actor.selectedContext().customerId(),
        actor.selectedContext().roles().stream().map(role -> role.name().toLowerCase(Locale.ROOT).replace('_', '-')).sorted().toList(),
        "active membership in selected context",
        myAccountAndCoreCapabilities(actor));
  }

  private List<CapabilityGroupSummary> capabilityGroups(AuthContextResolver.ResolvedMe actor) {
    var capabilities = actor.selectedContext().capabilities();
    return List.of(
        new CapabilityGroupSummary("my_account", "My Account", capabilities.stream().filter(capability -> capability.startsWith("my_account.")).sorted().toList()),
        new CapabilityGroupSummary("core_access_profile", "Core access/profile aliases", List.of(CORE_ACCESS_ME_CAPABILITY, CORE_PROFILE_UPDATE_CAPABILITY, CORE_ACCESS_CONTEXT_SELECT_CAPABILITY)),
        new CapabilityGroupSummary("workstreams", "Authorized workstream navigation", capabilities.stream().filter(capability -> capability.startsWith("agent_admin.") || capability.startsWith("audit.trace") || capability.startsWith("governance.") || capability.equals("user_admin.view_overview")).sorted().toList()));
  }

  private List<String> myAccountAndCoreCapabilities(AuthContextResolver.ResolvedMe actor) {
    var ids = new java.util.ArrayList<>(actor.selectedContext().capabilities().stream().filter(capability -> capability.startsWith("my_account.")).sorted().toList());
    ids.add(CORE_ACCESS_ME_CAPABILITY);
    ids.add(CORE_PROFILE_UPDATE_CAPABILITY);
    ids.add(CORE_ACCESS_CONTEXT_SELECT_CAPABILITY);
    return List.copyOf(ids);
  }

  private Target target(String functionalAgentId, String label, List<String> requiredCapabilityIds, String surfaceId) {
    return new Target(functionalAgentId, label, requiredCapabilityIds, surfaceId);
  }

  private static Map<String, Object> mapOf(Object... values) {
    var map = new LinkedHashMap<String, Object>();
    for (int i = 0; i + 1 < values.length; i += 2) map.put(String.valueOf(values[i]), values[i + 1]);
    return map;
  }

  private record Target(String functionalAgentId, String label, List<String> requiredCapabilityIds, String surfaceId) {}

  public record Summary(AuthorityBasisSummary authorityBasis, List<CapabilityGroupSummary> capabilityGroups, List<TraceRef> traceRefs) {}
  public record AuthorityBasisSummary(String selectedContextId, String tenantId, String customerId, List<String> roleIds, String primaryRoleBasis, List<String> myAccountCapabilityIds) {}
  public record CapabilityGroupSummary(String groupId, String label, List<String> capabilityIds) {}
  public record TraceRef(String traceId, String category, String label, String capabilityId, String correlationId) {}
  public record DashboardData(String surfaceContract, List<Map<String, Object>> cards, List<Map<String, Object>> sections, List<Map<String, Object>> attentionItems, List<Map<String, Object>> nextSteps, List<TraceRef> traceRefs, AuthorityBasisSummary authorityBasis, List<CapabilityGroupSummary> capabilityGroups) {}
  public record OpenWorkstreamDecision(String status, String message, String surfaceId, String functionalAgentId, String label, String requiredCapabilityId, String correlationId, List<String> traceIds, String safeReasonCode) {
    static OpenWorkstreamDecision denied(String safeReasonCode, String message, String correlationId) {
      return new OpenWorkstreamDecision("denied", message, "surface-my-account-open-denied", "my-account-agent", "Workstream unavailable", null, correlationId, List.of("trace-my-account-open-denied"), safeReasonCode);
    }
  }
}

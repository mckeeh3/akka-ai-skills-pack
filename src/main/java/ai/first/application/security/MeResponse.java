package ai.first.application.security;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AuthContext;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import java.util.List;
import java.util.Locale;

public record MeResponse(
    AccountSummary account,
    ProfileSummary profile,
    SettingsSummary settings,
    List<MembershipSummary> memberships,
    AuthContextSummary selectedAuthContext,
    List<AuthContextSummary> availableAuthContexts,
    List<String> visibleCapabilityIds,
    List<FunctionalAgentSummary> functionalAgents,
    MyAccountService.AuthorityBasisSummary authorityBasis,
    List<MyAccountService.CapabilityGroupSummary> contextCapabilityGroups,
    List<MyAccountService.TraceRef> traceRefs,
    String auditCorrelationId) {

  static MeResponse from(
      Account account,
      UserProfile profile,
      UserSettings settings,
      List<Membership> memberships,
      AuthContext selectedContext,
      MyAccountService.Summary myAccountSummary,
      String auditCorrelationId) {
    var contexts = memberships.stream().filter(Membership::active).map(AuthContextSummary::from).toList();
    var capabilities = browserCapabilityAliases(selectedContext.capabilities(), contexts.size());
    return new MeResponse(
        AccountSummary.from(account),
        ProfileSummary.from(profile),
        SettingsSummary.from(settings),
        memberships.stream().map(MembershipSummary::from).toList(),
        AuthContextSummary.from(selectedContext),
        contexts,
        capabilities,
        FunctionalAgentSummary.fromCapabilities(capabilities),
        myAccountSummary.authorityBasis(),
        myAccountSummary.capabilityGroups(),
        myAccountSummary.traceRefs(),
        auditCorrelationId);
  }

  private static List<String> browserCapabilityAliases(List<String> capabilities, int availableContextCount) {
    var browserCapabilities = new java.util.ArrayList<>(capabilities);
    addIfMissing(browserCapabilities, "core.access.me");
    if (capabilities.contains("profile.update") || capabilities.contains("my_account.update_profile_settings")) {
      addIfMissing(browserCapabilities, "core.profile.update");
    }
    if (capabilities.contains("my_account.view_context") && availableContextCount > 0) {
      addIfMissing(browserCapabilities, "core.access.context.select");
    }
    return List.copyOf(browserCapabilities);
  }

  private static void addIfMissing(List<String> capabilities, String capability) {
    if (!capabilities.contains(capability)) {
      capabilities.add(capability);
    }
  }

  public record AccountSummary(String accountId, String email, String displayName, String status) {
    static AccountSummary from(Account account) {
      return new AccountSummary(
          account.accountId(), account.displayEmail(), account.displayEmail(), accountStatus(account.status().name()));
    }
  }

  public record ProfileSummary(String displayName, String locale, String timeZone) {
    static ProfileSummary from(UserProfile profile) {
      return new ProfileSummary(profile.displayName(), null, null);
    }
  }

  public record SettingsSummary(String preferredThemeId) {
    static SettingsSummary from(UserSettings settings) {
      return new SettingsSummary(settings.themeId().id());
    }
  }

  public record MembershipSummary(
      String membershipId,
      String tenantId,
      String tenantName,
      String customerId,
      String customerName,
      String status,
      List<String> roleIds,
      List<String> capabilityIds) {
    static MembershipSummary from(Membership membership) {
      return new MembershipSummary(
          membership.membershipId(),
          membership.tenantId(),
          membership.tenantId(),
          membership.customerId(),
          membership.customerId(),
          membership.status().name().toLowerCase(Locale.ROOT),
          membership.roles().stream().map(role -> role.name().toLowerCase(Locale.ROOT).replace('_', '-')).sorted().toList(),
          membership.capabilities());
    }
  }

  public record AuthContextSummary(
      String selectedContextId,
      String tenantId,
      String tenantName,
      String customerId,
      String customerName,
      String membershipId,
      List<String> roleIds,
      List<String> capabilityIds,
      SupportAccessSummary supportAccess) {
    static AuthContextSummary from(AuthContext context) {
      return new AuthContextSummary(
          context.membershipId(),
          context.tenantId(),
          context.tenantId(),
          context.customerId(),
          context.customerId(),
          context.membershipId(),
          context.roles().stream().map(role -> role.name().toLowerCase(Locale.ROOT).replace('_', '-')).sorted().toList(),
          context.capabilities(),
          new SupportAccessSummary(false, null, null));
    }

    static AuthContextSummary from(Membership membership) {
      return new AuthContextSummary(
          membership.membershipId(),
          membership.tenantId(),
          membership.tenantId(),
          membership.customerId(),
          membership.customerId(),
          membership.membershipId(),
          membership.roles().stream().map(role -> role.name().toLowerCase(Locale.ROOT).replace('_', '-')).sorted().toList(),
          membership.capabilities(),
          new SupportAccessSummary(membership.supportAccess(), null, membership.expiresAt() == null ? null : membership.expiresAt().toString()));
    }
  }

  public record SupportAccessSummary(boolean active, String reason, String expiresAt) {}

  public record WorkstreamIconDescriptor(
      String workstreamId,
      String displayName,
      String iconId,
      String visualHint,
      String accentColorToken,
      String tooltip,
      String ariaLabel,
      String assetRef) {}

  public record FunctionalAgentSummary(
      String functionalAgentId,
      String label,
      String purpose,
      String icon,
      WorkstreamIconDescriptor workstreamIcon,
      String defaultSurfaceType,
      List<String> requiredCapabilityIds,
      FunctionalAgentAttention attention,
      String availability,
      String deniedReason) {
    public FunctionalAgentSummary withAttention(FunctionalAgentAttention attention) {
      return new FunctionalAgentSummary(functionalAgentId, label, purpose, icon, workstreamIcon, defaultSurfaceType, requiredCapabilityIds, attention, availability, deniedReason);
    }

    static List<FunctionalAgentSummary> fromCapabilities(List<String> capabilities) {
      var userAdminVisible = capabilities.contains("secure-tenant-user-foundation")
          || capabilities.stream().anyMatch(capability -> capability.endsWith("user.read") || capability.endsWith("user.manage"));
      var profileVisible = capabilities.contains("my_account.view_summary") || capabilities.contains("profile.read") || capabilities.stream().anyMatch(capability -> capability.endsWith("user.read") || capability.endsWith("user.manage"));
      var auditVisible = capabilities.stream().anyMatch(capability -> capability.endsWith("audit.read"));
      var governanceVisible = capabilities.stream().anyMatch(capability -> capability.startsWith("governance.") || capability.startsWith("improvements.") || capability.contains("policy"));
      var agentAdminVisible = capabilities.contains("agent_admin.list_definitions")
          && capabilities.contains("agent_admin.get_definition")
          && capabilities.contains("agent_admin.draft_behavior_change")
          && capabilities.contains("agent_admin.simulate_tool_boundary")
          && capabilities.contains("agent_admin.get_model_ref");
      return List.of(
          new FunctionalAgentSummary(
              "agent-my-account",
              "My Account",
              "Review the signed-in account, profile, settings, selected AuthContext, sign-out action, and browser-safe capability basis.",
              "my-account",
              icon("workstream-my-account", "My Account", "my-account", "user account avatar", "accent-account", "Open My Account workstream from the signed-in user tile"),
              "markdown_response",
              List.of("my_account.view_summary", "my_account.ask_agent"),
              null,
              profileVisible ? "visible" : "denied",
              profileVisible ? null : "No active profile context is available."),
          new FunctionalAgentSummary(
              "agent-user-admin",
              "User Admin",
              "Administer users, invitations, roles, memberships, support access, and access review with backend-authoritative policy checks.",
              "users",
              icon("workstream-user-admin", "User Admin", "users-admin", "users and roles", "accent-users", "Open User Admin workstream"),
              "markdown_response",
              List.of("secure-tenant-user-foundation"),
              null,
              userAdminVisible ? "visible" : "denied",
              userAdminVisible ? null : "Missing User Admin read capability."),
          new FunctionalAgentSummary(
              "agent-agent-admin",
              "Agent Admin",
              "Govern agent definitions, prompts, skills, tool boundaries, model refs, test runs, approvals, denials, and runtime traces.",
              "bot",
              icon("workstream-agent-admin", "Agent Admin", "bot-spark", "agent bot sparkle", "accent-agents", "Open Agent Admin workstream"),
              "markdown_response",
              List.of("agent_admin.submit_turn", "agent_admin.list_definitions", "agent_admin.get_definition", "agent_admin.get_prompt_version", "agent_admin.get_skill_version", "agent_admin.get_reference_version", "agent_admin.get_manifest", "agent_admin.get_model_ref", "agent_admin.get_tool_boundary", "agent_admin.simulate_tool_boundary", "agent_admin.draft_behavior_change", "agent_admin.submit_behavior_change_for_review", "agent_admin.approve_behavior_change", "agent_admin.reject_behavior_change", "agent_admin.activate_behavior_change", "agent_admin.cancel_behavior_change", "agent_admin.rollback_behavior_change", "agent_admin.list_seed_material", "agent_admin.reseed_missing_defaults", "agent_admin.start_behavior_review_task", "agent_admin.get_behavior_review_task", "agent_admin.cancel_behavior_review_task"),
              null,
              agentAdminVisible ? "visible" : "denied",
              agentAdminVisible ? null : "Missing governed Agent Admin capabilities."),
          new FunctionalAgentSummary(
              "agent-audit-trace",
              "Audit/Trace",
              "Review admin audit events and work traces for selected tenant/customer context.",
              "audit",
              icon("workstream-audit-trace", "Audit/Trace", "timeline-search", "audit timeline search", "accent-audit", "Open Audit/Trace workstream"),
              "markdown_response",
              List.of("audit.trace.read"),
              null,
              auditVisible ? "visible" : "hidden",
              null),
          new FunctionalAgentSummary(
              "agent-governance-policy",
              "Governance/Policy",
              "Review policy guardrails, improvement proposals, approval requirements, activation denials, traces, and outcome evidence.",
              "shield",
              icon("workstream-governance-policy", "Governance/Policy", "shield-checklist", "shield checklist", "accent-governance", "Open Governance/Policy workstream"),
              "markdown_response",
              List.of("governance.policy.read", "improvements.review"),
              null,
              governanceVisible ? "visible" : "denied",
              governanceVisible ? null : "Governance policy capabilities are not assigned in this context."));
    }

    public record FunctionalAgentAttention(int count, String severity, String source) {}

    private static WorkstreamIconDescriptor icon(String workstreamId, String displayName, String iconId, String visualHint, String accentColorToken, String label) {
      return new WorkstreamIconDescriptor(workstreamId, displayName, iconId, visualHint, accentColorToken, label, label, null);
    }
  }

  private static String accountStatus(String status) {
    return switch (status) {
      case "ACTIVE" -> "active";
      case "DISABLED", "REMOVED" -> "disabled";
      default -> "pending";
    };
  }
}

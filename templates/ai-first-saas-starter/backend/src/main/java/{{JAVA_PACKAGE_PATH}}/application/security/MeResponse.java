package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AuthContext;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
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
    String auditCorrelationId) {

  static MeResponse from(
      Account account,
      UserProfile profile,
      UserSettings settings,
      List<Membership> memberships,
      AuthContext selectedContext,
      String auditCorrelationId) {
    var contexts = memberships.stream().filter(Membership::active).map(AuthContextSummary::from).toList();
    var capabilities = selectedContext.capabilities();
    return new MeResponse(
        AccountSummary.from(account),
        ProfileSummary.from(profile),
        SettingsSummary.from(settings),
        memberships.stream().map(MembershipSummary::from).toList(),
        AuthContextSummary.from(selectedContext),
        contexts,
        capabilities,
        FunctionalAgentSummary.fromCapabilities(capabilities),
        auditCorrelationId);
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

  public record SettingsSummary(String preferredColorMode) {
    static SettingsSummary from(UserSettings settings) {
      return new SettingsSummary(settings.uiMode().name().toLowerCase(Locale.ROOT));
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

  public record FunctionalAgentSummary(
      String functionalAgentId,
      String label,
      String purpose,
      String icon,
      String defaultSurfaceType,
      List<String> requiredCapabilityIds,
      String availability,
      String deniedReason) {
    static List<FunctionalAgentSummary> fromCapabilities(List<String> capabilities) {
      var userAdminVisible = capabilities.stream().anyMatch(capability -> capability.endsWith("user.read") || capability.endsWith("user.manage"));
      return List.of(
          new FunctionalAgentSummary(
              "agent-user-admin",
              "User Admin",
              "Administer users, invitations, roles, memberships, support access, and access review with backend-authoritative policy checks.",
              "users",
              "dashboard",
              List.of("secure-tenant-user-foundation"),
              userAdminVisible ? "visible" : "denied",
              userAdminVisible ? null : "Missing User Admin read capability."),
          new FunctionalAgentSummary(
              "agent-audit-trace",
              "Audit & Trace",
              "Review admin audit events and work traces for selected tenant/customer context.",
              "audit",
              "audit-timeline",
              List.of("audit.trace.read"),
              capabilities.stream().anyMatch(capability -> capability.endsWith("audit.read")) ? "visible" : "hidden",
              null),
          new FunctionalAgentSummary(
              "agent-agent-admin",
              "Agent Admin",
              "Govern agent definitions, prompts, skills, tool boundaries, and runtime traces when the agent governance module is installed.",
              "bot",
              "governance-diff",
              List.of("agent.definitions.manage"),
              "disabled",
              "Agent governance backend is added in a later starter sprint."));
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

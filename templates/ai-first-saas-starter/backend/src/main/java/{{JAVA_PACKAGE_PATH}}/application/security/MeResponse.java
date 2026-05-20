package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AuthContext;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import java.util.List;

public record MeResponse(
    AccountSummary account,
    ProfileSummary profile,
    SettingsSummary settings,
    List<MembershipSummary> memberships,
    AuthContextSummary selectedContext,
    List<AuthContextSummary> availableContexts,
    List<FunctionalAgentSummary> functionalAgents,
    List<String> navigationCapabilities,
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
        FunctionalAgentSummary.fromCapabilities(capabilities),
        capabilities,
        auditCorrelationId);
  }

  public record AccountSummary(
      String accountId, String email, String displayName, String status, String identityLinkState) {
    static AccountSummary from(Account account) {
      return new AccountSummary(
          account.accountId(), account.displayEmail(), account.displayEmail(), account.status().name(), account.identityLinkState());
    }
  }

  public record ProfileSummary(String displayName, String givenName, String familyName, String avatarUrl) {
    static ProfileSummary from(UserProfile profile) {
      return new ProfileSummary(profile.displayName(), profile.givenName(), profile.familyName(), profile.avatarUrl());
    }
  }

  public record SettingsSummary(String uiMode) {
    static SettingsSummary from(UserSettings settings) {
      return new SettingsSummary(settings.uiMode().name());
    }
  }

  public record MembershipSummary(
      String membershipId,
      String scopeType,
      String tenantId,
      String customerId,
      List<String> roles,
      String status,
      boolean supportAccess) {
    static MembershipSummary from(Membership membership) {
      return new MembershipSummary(
          membership.membershipId(),
          membership.scopeType().name(),
          membership.tenantId(),
          membership.customerId(),
          membership.roles().stream().map(Enum::name).sorted().toList(),
          membership.status().name(),
          membership.supportAccess());
    }
  }

  public record AuthContextSummary(
      String membershipId,
      String scopeType,
      String tenantId,
      String customerId,
      List<String> roles,
      List<String> capabilities) {
    static AuthContextSummary from(AuthContext context) {
      return new AuthContextSummary(
          context.membershipId(),
          context.scopeType().name(),
          context.tenantId(),
          context.customerId(),
          context.roles().stream().map(Enum::name).sorted().toList(),
          context.capabilities());
    }

    static AuthContextSummary from(Membership membership) {
      return new AuthContextSummary(
          membership.membershipId(),
          membership.scopeType().name(),
          membership.tenantId(),
          membership.customerId(),
          membership.roles().stream().map(Enum::name).sorted().toList(),
          membership.capabilities());
    }
  }

  public record FunctionalAgentSummary(String agentId, String displayName, boolean available) {
    static List<FunctionalAgentSummary> fromCapabilities(List<String> capabilities) {
      return List.of(
          new FunctionalAgentSummary("user-admin", "User Admin Agent", capabilities.contains("agent.user_admin.use")),
          new FunctionalAgentSummary("main-workstream", "Main Workstream Agent", capabilities.contains("agent.workstream.use")));
    }
  }
}

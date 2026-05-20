package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorSeedLoader;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeService;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.InMemoryAgentBehaviorRepository;
import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import java.time.Clock;
import java.util.List;

/** Shared starter-template service registry for local/demo endpoints. Replace with dependency injection in production apps. */
public final class StarterSecurityComponents {
  private static final Clock CLOCK = Clock.systemUTC();
  private static final InMemoryIdentityRepository IDENTITY_REPOSITORY = new InMemoryIdentityRepository();
  private static final InMemoryInvitationRepository INVITATION_REPOSITORY = new InMemoryInvitationRepository();
  private static final AuthContextResolver AUTH_CONTEXT_RESOLVER = new AuthContextResolver(IDENTITY_REPOSITORY);
  private static final InMemoryAgentBehaviorRepository AGENT_BEHAVIOR_REPOSITORY = new InMemoryAgentBehaviorRepository();
  private static final AgentBehaviorSeedLoader AGENT_BEHAVIOR_SEED_LOADER = new AgentBehaviorSeedLoader(AGENT_BEHAVIOR_REPOSITORY, CLOCK);
  private static final AgentRuntimeService AGENT_RUNTIME_SERVICE = new AgentRuntimeService(AGENT_BEHAVIOR_REPOSITORY, AUTH_CONTEXT_RESOLVER, CLOCK);
  private static final MeService ME_SERVICE = new MeService(AUTH_CONTEXT_RESOLVER);
  private static final UserAdminService USER_ADMIN_SERVICE = new UserAdminService(IDENTITY_REPOSITORY, CLOCK);
  private static final InvitationService INVITATION_SERVICE = new InvitationService(IDENTITY_REPOSITORY, INVITATION_REPOSITORY, CLOCK);
  private static final UserDirectoryView USER_DIRECTORY_VIEW = new UserDirectoryView(USER_ADMIN_SERVICE);
  private static final InvitationView INVITATION_VIEW = new InvitationView(INVITATION_SERVICE);
  private static final WorkstreamService WORKSTREAM_SERVICE = new WorkstreamService(ME_SERVICE, AUTH_CONTEXT_RESOLVER, USER_DIRECTORY_VIEW, INVITATION_VIEW, USER_ADMIN_SERVICE, INVITATION_SERVICE);

  static {
    seedDemoTenantAdmin();
    AGENT_BEHAVIOR_SEED_LOADER.importStarterDefaults("tenant-starter", "starter-bootstrap", "corr-starter-agent-seed");
  }

  private StarterSecurityComponents() {}

  public static AuthContextResolver authContextResolver() {
    return AUTH_CONTEXT_RESOLVER;
  }

  public static MeService meService() {
    return ME_SERVICE;
  }

  public static WorkstreamService workstreamService() {
    return WORKSTREAM_SERVICE;
  }

  public static InMemoryIdentityRepository identityRepository() {
    return IDENTITY_REPOSITORY;
  }

  public static InMemoryAgentBehaviorRepository agentBehaviorRepository() {
    return AGENT_BEHAVIOR_REPOSITORY;
  }

  public static AgentBehaviorSeedLoader agentBehaviorSeedLoader() {
    return AGENT_BEHAVIOR_SEED_LOADER;
  }

  public static AgentRuntimeService agentRuntimeService() {
    return AGENT_RUNTIME_SERVICE;
  }

  private static void seedDemoTenantAdmin() {
    IDENTITY_REPOSITORY.putTenant(new Tenant("tenant-starter", "Starter Tenant", true));
    IDENTITY_REPOSITORY.saveAccount(new Account("admin@example.test", "workos-admin", "admin@example.test", "admin@example.test", AccountStatus.ACTIVE, "LINKED"));
    IDENTITY_REPOSITORY.putProfile(new UserProfile("admin@example.test", "admin@example.test", "Tenant Admin", "Tenant", "Admin", null));
    IDENTITY_REPOSITORY.putSettings(new UserSettings("admin@example.test", UserSettings.UiMode.LIGHT));
    IDENTITY_REPOSITORY.putMembership(new Membership("membership-tenant-admin", "admin@example.test", ScopeType.TENANT, "tenant-starter", null, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR), MembershipStatus.ACTIVE, false, null));
    IDENTITY_REPOSITORY.saveAccount(new Account("member@example.test", null, "member@example.test", "member@example.test", AccountStatus.ACTIVE, "UNLINKED"));
    IDENTITY_REPOSITORY.putProfile(new UserProfile("member@example.test", "member@example.test", "Member User", "Member", "User", null));
    IDENTITY_REPOSITORY.putSettings(new UserSettings("member@example.test", UserSettings.UiMode.LIGHT));
    IDENTITY_REPOSITORY.putMembership(new Membership("membership-member", "member@example.test", ScopeType.TENANT, "tenant-starter", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.ACTIVE, false, null));
  }
}

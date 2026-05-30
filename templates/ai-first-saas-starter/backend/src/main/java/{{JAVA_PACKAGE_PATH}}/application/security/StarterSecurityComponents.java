package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorSeedLoader;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeService;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeToolResolver;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.DefaultWorkstreamAgentRuntimeInvoker;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.InMemoryAgentBehaviorRepository;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.ModelProviderClient;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.OpenAiModelProviderClient;
import java.time.Clock;
import java.util.concurrent.atomic.AtomicBoolean;

/** Shared starter-template service registry for local/demo endpoints. Replace with dependency injection in production apps. */
public final class StarterSecurityComponents {
  private static final AtomicBoolean STARTED = new AtomicBoolean(false);
  private static final Clock CLOCK = Clock.systemUTC();
  private static final InMemoryIdentityRepository IDENTITY_REPOSITORY = new InMemoryIdentityRepository();
  private static final InMemoryInvitationRepository INVITATION_REPOSITORY = new InMemoryInvitationRepository();
  private static final AuthContextResolver AUTH_CONTEXT_RESOLVER = new AuthContextResolver(IDENTITY_REPOSITORY);
  private static final InMemoryAgentBehaviorRepository AGENT_BEHAVIOR_REPOSITORY = new InMemoryAgentBehaviorRepository();
  private static final AgentBehaviorSeedLoader AGENT_BEHAVIOR_SEED_LOADER = new AgentBehaviorSeedLoader(AGENT_BEHAVIOR_REPOSITORY, CLOCK);
  private static final ModelProviderClient MODEL_PROVIDER_CLIENT = new OpenAiModelProviderClient();
  private static final AgentRuntimeService AGENT_RUNTIME_SERVICE = new AgentRuntimeService(AGENT_BEHAVIOR_REPOSITORY, AUTH_CONTEXT_RESOLVER, CLOCK, MODEL_PROVIDER_CLIENT);
  private static final AgentRuntimeToolResolver AGENT_RUNTIME_TOOL_RESOLVER = new AgentRuntimeToolResolver(AGENT_BEHAVIOR_REPOSITORY, AGENT_RUNTIME_SERVICE);
  private static final MeService ME_SERVICE = new MeService(AUTH_CONTEXT_RESOLVER);
  private static final UserAdminService USER_ADMIN_SERVICE = new UserAdminService(IDENTITY_REPOSITORY, CLOCK);
  private static final InvitationService INVITATION_SERVICE = new InvitationService(IDENTITY_REPOSITORY, INVITATION_REPOSITORY, CLOCK);
  private static final UserDirectoryView USER_DIRECTORY_VIEW = new UserDirectoryView(USER_ADMIN_SERVICE);
  private static final InvitationView INVITATION_VIEW = new InvitationView(INVITATION_SERVICE);
  private static final WorkstreamLogRepository WORKSTREAM_LOG_REPOSITORY = new InMemoryWorkstreamLogRepository();
  private static final AuditTraceService AUDIT_TRACE_SERVICE = new AuditTraceService(AUTH_CONTEXT_RESOLVER, new InMemoryAuditTraceRepository(AGENT_RUNTIME_SERVICE, WORKSTREAM_LOG_REPOSITORY));
  private static final GovernancePolicyRepository GOVERNANCE_POLICY_REPOSITORY = new InMemoryGovernancePolicyRepository();
  private static final GovernancePolicyService GOVERNANCE_POLICY_SERVICE = new GovernancePolicyService(GOVERNANCE_POLICY_REPOSITORY, AUTH_CONTEXT_RESOLVER, CLOCK);
  private static final WorkstreamService WORKSTREAM_SERVICE = workstreamService(WORKSTREAM_LOG_REPOSITORY);

  static {
    startup();
  }

  private StarterSecurityComponents() {}

  /** Idempotent startup hook used by the Akka @Setup class and by local tests/endpoints. */
  public static void startup() {
    if (STARTED.compareAndSet(false, true)) {
      seedDemoTenantAdmin();
      AGENT_BEHAVIOR_SEED_LOADER.importStarterDefaults("tenant-starter", "starter-bootstrap", "corr-starter-agent-seed");
    }
  }

  public static AuthContextResolver authContextResolver() {
    return AUTH_CONTEXT_RESOLVER;
  }

  public static MeService meService() {
    return ME_SERVICE;
  }

  public static WorkstreamService workstreamService() {
    return WORKSTREAM_SERVICE;
  }

  public static WorkstreamService workstreamService(WorkstreamLogRepository workstreamLogRepository) {
    return new WorkstreamService(ME_SERVICE, AUTH_CONTEXT_RESOLVER, USER_DIRECTORY_VIEW, INVITATION_VIEW, USER_ADMIN_SERVICE, INVITATION_SERVICE, AGENT_BEHAVIOR_REPOSITORY, AGENT_RUNTIME_SERVICE, workstreamLogRepository);
  }

  public static WorkstreamService workstreamService(ComponentClient componentClient, WorkstreamLogRepository workstreamLogRepository) {
    return new WorkstreamService(ME_SERVICE, AUTH_CONTEXT_RESOLVER, USER_DIRECTORY_VIEW, INVITATION_VIEW, USER_ADMIN_SERVICE, INVITATION_SERVICE, AGENT_BEHAVIOR_REPOSITORY, AGENT_RUNTIME_SERVICE, new DefaultWorkstreamAgentRuntimeInvoker(AGENT_RUNTIME_SERVICE, componentClient), workstreamLogRepository);
  }

  public static InvitationService invitationService() {
    return INVITATION_SERVICE;
  }

  public static InvitationView invitationView() {
    return INVITATION_VIEW;
  }

  public static UserAdminService userAdminService() {
    return USER_ADMIN_SERVICE;
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

  public static AgentRuntimeToolResolver agentRuntimeToolResolver() {
    return AGENT_RUNTIME_TOOL_RESOLVER;
  }

  public static AuditTraceService auditTraceService() {
    return AUDIT_TRACE_SERVICE;
  }

  public static GovernancePolicyService governancePolicyService() {
    return GOVERNANCE_POLICY_SERVICE;
  }

  public static ModelProviderClient modelProviderClient() {
    return MODEL_PROVIDER_CLIENT;
  }

  private static void seedDemoTenantAdmin() {
    BootstrapAdminSeeder.seedConfiguredAdmins(IDENTITY_REPOSITORY, System.getenv("ADMIN_USERS"));
    BootstrapAdminSeeder.seedLocalDemoMember(IDENTITY_REPOSITORY);
  }
}

package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorSeedLoader;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeService;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeToolResolver;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.DefaultWorkstreamAgentRuntimeInvoker;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.FailClosedWorkstreamAgentRuntimeInvoker;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.InMemoryAgentBehaviorRepository;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.ModelProviderClient;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.OpenAiModelProviderClient;
import java.time.Clock;
import java.util.concurrent.atomic.AtomicBoolean;

/** Shared starter-template service registry. Normal runtime fails closed unless explicitly opted into local/demo repositories. */
public final class StarterSecurityComponents {
  private static final AtomicBoolean STARTED = new AtomicBoolean(false);
  private static final Clock CLOCK = Clock.systemUTC();
  private static final LocalDemoIdentityRepository IDENTITY_REPOSITORY = new LocalDemoIdentityRepository();
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
  private static final WorkstreamLogRepository LOCAL_DEMO_WORKSTREAM_LOG_REPOSITORY = new LocalDemoWorkstreamLogRepository();
  private static final GovernancePolicyRepository LOCAL_DEMO_GOVERNANCE_POLICY_REPOSITORY = new LocalDemoGovernancePolicyRepository();
  private static final AuditTraceService AUDIT_TRACE_SERVICE = new AuditTraceService(AUTH_CONTEXT_RESOLVER, auditTraceRepository());
  private static final GovernancePolicyService GOVERNANCE_POLICY_SERVICE = new GovernancePolicyService(governancePolicyRepository(), AUTH_CONTEXT_RESOLVER, CLOCK);
  private static final WorkstreamService WORKSTREAM_SERVICE = workstreamService(workstreamLogRepository());

  static {
    startup();
  }

  private StarterSecurityComponents() {}

  /** Idempotent startup hook used by the Akka @Setup class and by local tests/endpoints. */
  public static void startup() {
    if (STARTED.compareAndSet(false, true)) {
      requireDurableFoundationOrExplicitLocalDemo();
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
    return new WorkstreamService(ME_SERVICE, AUTH_CONTEXT_RESOLVER, USER_DIRECTORY_VIEW, INVITATION_VIEW, USER_ADMIN_SERVICE, INVITATION_SERVICE, AGENT_BEHAVIOR_REPOSITORY, AGENT_RUNTIME_SERVICE, new FailClosedWorkstreamAgentRuntimeInvoker(), workstreamLogRepository, accessReviewTaskRepository(), auditTraceRepository(workstreamLogRepository), governancePolicyRepository());
  }

  public static WorkstreamService workstreamService(ComponentClient componentClient, WorkstreamLogRepository workstreamLogRepository) {
    return new WorkstreamService(ME_SERVICE, AUTH_CONTEXT_RESOLVER, USER_DIRECTORY_VIEW, INVITATION_VIEW, USER_ADMIN_SERVICE, INVITATION_SERVICE, AGENT_BEHAVIOR_REPOSITORY, AGENT_RUNTIME_SERVICE, new DefaultWorkstreamAgentRuntimeInvoker(AGENT_RUNTIME_SERVICE, componentClient), workstreamLogRepository, accessReviewTaskRepository(), auditTraceRepository(workstreamLogRepository), governancePolicyRepository());
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

  public static LocalDemoIdentityRepository identityRepository() {
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
    if (FailClosedFoundationRuntime.localDemoOrTestEnabled()) {
      BootstrapAdminSeeder.seedConfiguredAdmins(IDENTITY_REPOSITORY, System.getenv("ADMIN_USERS"));
      BootstrapAdminSeeder.seedLocalDemoMember(IDENTITY_REPOSITORY);
    }
  }

  private static void requireDurableFoundationOrExplicitLocalDemo() {
    if (!FailClosedFoundationRuntime.localDemoOrTestEnabled()) throw FailClosedFoundationRuntime.unavailable("Starter foundation runtime");
  }

  private static WorkstreamLogRepository workstreamLogRepository() {
    return FailClosedFoundationRuntime.localDemoOrTestEnabled() ? LOCAL_DEMO_WORKSTREAM_LOG_REPOSITORY : new FailClosedWorkstreamLogRepository();
  }

  private static AccessReviewTaskRepository accessReviewTaskRepository() {
    return FailClosedFoundationRuntime.localDemoOrTestEnabled() ? new LocalDemoAccessReviewTaskRepository() : new FailClosedAccessReviewTaskRepository();
  }

  private static AuditTraceRepository auditTraceRepository() {
    return auditTraceRepository(workstreamLogRepository());
  }

  private static AuditTraceRepository auditTraceRepository(WorkstreamLogRepository workstreamLogRepository) {
    return FailClosedFoundationRuntime.localDemoOrTestEnabled() ? new LocalDemoAuditTraceRepository(AGENT_RUNTIME_SERVICE, workstreamLogRepository) : new FailClosedAuditTraceRepository();
  }

  private static GovernancePolicyRepository governancePolicyRepository() {
    return FailClosedFoundationRuntime.localDemoOrTestEnabled() ? LOCAL_DEMO_GOVERNANCE_POLICY_REPOSITORY : new FailClosedGovernancePolicyRepository();
  }
}

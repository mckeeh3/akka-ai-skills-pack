package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorRepository;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorSeedLoader;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeService;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeToolResolver;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeTraceSink;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AkkaAgentBehaviorRepository;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AkkaAgentRuntimeTraceSink;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.DefaultWorkstreamAgentRuntimeInvoker;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.FailClosedAgentRuntimeTraceSink;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.FailClosedWorkstreamAgentRuntimeInvoker;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.LocalDemoAgentBehaviorRepository;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.LocalDemoAgentRuntimeTraceSink;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.ModelProviderClient;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.OpenAiModelProviderClient;
import java.time.Clock;
import java.util.concurrent.atomic.AtomicBoolean;

/** Shared starter-template service registry. Normal runtime fails closed unless explicitly bound to Akka durable components or opted into local/demo repositories. */
public final class StarterSecurityComponents {
  private static final AtomicBoolean STARTED = new AtomicBoolean(false);
  private static final AtomicBoolean AKKA_RUNTIME_BOUND = new AtomicBoolean(false);
  private static final Clock CLOCK = Clock.systemUTC();
  private static final LocalDemoIdentityRepository IDENTITY_REPOSITORY = new LocalDemoIdentityRepository();
  private static final AuthContextResolver AUTH_CONTEXT_RESOLVER = new AuthContextResolver(IDENTITY_REPOSITORY);
  private static final ModelProviderClient MODEL_PROVIDER_CLIENT = new OpenAiModelProviderClient();
  private static final MeService ME_SERVICE = new MeService(AUTH_CONTEXT_RESOLVER);
  private static final UserAdminService USER_ADMIN_SERVICE = new UserAdminService(IDENTITY_REPOSITORY, CLOCK);
  private static final WorkstreamLogRepository LOCAL_DEMO_WORKSTREAM_LOG_REPOSITORY = new LocalDemoWorkstreamLogRepository();
  private static final GovernancePolicyRepository LOCAL_DEMO_GOVERNANCE_POLICY_REPOSITORY = new LocalDemoGovernancePolicyRepository();

  private static volatile InvitationRepository invitationRepository = new LocalDemoInvitationRepository();
  private static volatile AgentBehaviorRepository agentBehaviorRepository = new LocalDemoAgentBehaviorRepository();
  private static volatile AgentBehaviorSeedLoader agentBehaviorSeedLoader = new AgentBehaviorSeedLoader(agentBehaviorRepository, CLOCK);
  private static volatile AgentRuntimeService agentRuntimeService = new AgentRuntimeService(agentBehaviorRepository, AUTH_CONTEXT_RESOLVER, CLOCK, MODEL_PROVIDER_CLIENT, traceSinkBeforeAkkaBinding());
  private static volatile AgentRuntimeToolResolver agentRuntimeToolResolver = new AgentRuntimeToolResolver(agentBehaviorRepository, agentRuntimeService);
  private static volatile InvitationService invitationService = new InvitationService(IDENTITY_REPOSITORY, invitationRepository, CLOCK);
  private static volatile InvitationView invitationView = new InvitationView(invitationService);
  private static volatile AuditTraceService auditTraceService = new AuditTraceService(AUTH_CONTEXT_RESOLVER, auditTraceRepository());
  private static volatile GovernancePolicyService governancePolicyService = new GovernancePolicyService(governancePolicyRepository(), AUTH_CONTEXT_RESOLVER, CLOCK);
  private static volatile WorkstreamService workstreamService = workstreamService(workstreamLogRepository());

  static {
    startup();
  }

  private StarterSecurityComponents() {}

  /** Idempotent startup hook used by the Akka @Setup class and by local tests/endpoints. */
  public static void startup() {
    if (STARTED.compareAndSet(false, true)) {
      requireDurableFoundationOrExplicitLocalDemo();
      seedDemoTenantAdmin();
      if (FailClosedFoundationRuntime.localDemoOrTestEnabled()) {
        agentBehaviorSeedLoader.importStarterDefaults("tenant-starter", "starter-bootstrap", "corr-starter-agent-seed");
      }
    }
  }

  /** Bind normal endpoint/agent runtime seams to Akka durable repositories once a ComponentClient is available. */
  public static void bindAkkaRuntime(ComponentClient componentClient) {
    if (componentClient == null || !AKKA_RUNTIME_BOUND.compareAndSet(false, true)) return;
    var durableInvitations = new AkkaInvitationRepository(componentClient);
    var durableAgentBehavior = new AkkaAgentBehaviorRepository(componentClient);
    var durableRuntime = new AgentRuntimeService(durableAgentBehavior, AUTH_CONTEXT_RESOLVER, CLOCK, MODEL_PROVIDER_CLIENT, new AkkaAgentRuntimeTraceSink(componentClient));
    invitationRepository = durableInvitations;
    agentBehaviorRepository = durableAgentBehavior;
    agentBehaviorSeedLoader = new AgentBehaviorSeedLoader(durableAgentBehavior, CLOCK);
    agentBehaviorSeedLoader.importStarterDefaults("tenant-starter", "starter-bootstrap", "corr-starter-agent-seed");
    agentRuntimeService = durableRuntime;
    agentRuntimeToolResolver = new AgentRuntimeToolResolver(durableAgentBehavior, durableRuntime);
    invitationService = new InvitationService(IDENTITY_REPOSITORY, durableInvitations, CLOCK);
    invitationView = new InvitationView(invitationService);
    auditTraceService = new AuditTraceService(AUTH_CONTEXT_RESOLVER, auditTraceRepository());
    governancePolicyService = new GovernancePolicyService(governancePolicyRepository(), AUTH_CONTEXT_RESOLVER, CLOCK);
    workstreamService = workstreamService(workstreamLogRepository());
  }

  public static AuthContextResolver authContextResolver() {
    return AUTH_CONTEXT_RESOLVER;
  }

  public static MeService meService() {
    return ME_SERVICE;
  }

  public static WorkstreamService workstreamService() {
    return workstreamService;
  }

  public static WorkstreamService workstreamService(WorkstreamLogRepository workstreamLogRepository) {
    return new WorkstreamService(ME_SERVICE, AUTH_CONTEXT_RESOLVER, new UserDirectoryView(USER_ADMIN_SERVICE), invitationView, USER_ADMIN_SERVICE, invitationService, agentBehaviorRepository, agentRuntimeService, new FailClosedWorkstreamAgentRuntimeInvoker(), workstreamLogRepository, accessReviewTaskRepository(), auditTraceRepository(workstreamLogRepository), governancePolicyRepository());
  }

  public static WorkstreamService workstreamService(ComponentClient componentClient, WorkstreamLogRepository workstreamLogRepository) {
    bindAkkaRuntime(componentClient);
    return new WorkstreamService(ME_SERVICE, AUTH_CONTEXT_RESOLVER, new UserDirectoryView(USER_ADMIN_SERVICE), invitationView, USER_ADMIN_SERVICE, invitationService, agentBehaviorRepository, agentRuntimeService, new DefaultWorkstreamAgentRuntimeInvoker(agentRuntimeService, componentClient), workstreamLogRepository, accessReviewTaskRepository(), auditTraceRepository(workstreamLogRepository), governancePolicyRepository());
  }

  public static InvitationService invitationService() {
    return invitationService;
  }

  public static InvitationView invitationView() {
    return invitationView;
  }

  public static UserAdminService userAdminService() {
    return USER_ADMIN_SERVICE;
  }

  public static LocalDemoIdentityRepository identityRepository() {
    return IDENTITY_REPOSITORY;
  }

  public static AgentBehaviorRepository agentBehaviorRepository() {
    return agentBehaviorRepository;
  }

  public static AgentBehaviorSeedLoader agentBehaviorSeedLoader() {
    return agentBehaviorSeedLoader;
  }

  public static AgentRuntimeService agentRuntimeService() {
    return agentRuntimeService;
  }

  public static AgentRuntimeToolResolver agentRuntimeToolResolver() {
    return agentRuntimeToolResolver;
  }

  public static AuditTraceService auditTraceService() {
    return auditTraceService;
  }

  public static GovernancePolicyService governancePolicyService() {
    return governancePolicyService;
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
    return FailClosedFoundationRuntime.localDemoOrTestEnabled() ? new LocalDemoAuditTraceRepository(agentRuntimeService, workstreamLogRepository) : new FailClosedAuditTraceRepository();
  }

  private static AgentRuntimeTraceSink traceSinkBeforeAkkaBinding() {
    return FailClosedFoundationRuntime.localDemoOrTestEnabled() ? new LocalDemoAgentRuntimeTraceSink() : new FailClosedAgentRuntimeTraceSink();
  }

  private static GovernancePolicyRepository governancePolicyRepository() {
    return FailClosedFoundationRuntime.localDemoOrTestEnabled() ? LOCAL_DEMO_GOVERNANCE_POLICY_REPOSITORY : new FailClosedGovernancePolicyRepository();
  }
}

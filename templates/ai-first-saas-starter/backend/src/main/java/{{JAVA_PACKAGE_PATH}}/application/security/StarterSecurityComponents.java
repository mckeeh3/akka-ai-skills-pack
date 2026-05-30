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
import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import {{JAVA_BASE_PACKAGE}}.domain.security.Customer;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/** Shared starter-template service registry. Normal runtime binds identity and other foundation state to Akka components. */
public final class StarterSecurityComponents {
  private static final AtomicBoolean STARTED = new AtomicBoolean(false);
  private static final AtomicBoolean AKKA_RUNTIME_BOUND = new AtomicBoolean(false);
  private static final Clock CLOCK = Clock.systemUTC();
  private static final ModelProviderClient MODEL_PROVIDER_CLIENT = new OpenAiModelProviderClient();

  private static volatile IdentityRepository identityRepository = new UnboundIdentityRepository();
  private static volatile AuthContextResolver authContextResolver = new AuthContextResolver(identityRepository);
  private static volatile MeService meService = new MeService(authContextResolver);
  private static volatile UserAdminService userAdminService = new UserAdminService(identityRepository, CLOCK);
  private static volatile InvitationRepository invitationRepository = new LocalDemoInvitationRepository();
  private static volatile AgentBehaviorRepository agentBehaviorRepository = new LocalDemoAgentBehaviorRepository();
  private static volatile AgentBehaviorSeedLoader agentBehaviorSeedLoader = new AgentBehaviorSeedLoader(agentBehaviorRepository, CLOCK);
  private static volatile AgentRuntimeService agentRuntimeService = new AgentRuntimeService(agentBehaviorRepository, authContextResolver, CLOCK, MODEL_PROVIDER_CLIENT, traceSinkBeforeAkkaBinding());
  private static volatile AgentRuntimeToolResolver agentRuntimeToolResolver = new AgentRuntimeToolResolver(agentBehaviorRepository, agentRuntimeService);
  private static volatile InvitationService invitationService = new InvitationService(identityRepository, invitationRepository, CLOCK);
  private static volatile InvitationView invitationView = new InvitationView(invitationService);
  private static volatile AuditTraceService auditTraceService = new AuditTraceService(authContextResolver, auditTraceRepository());
  private static volatile GovernancePolicyService governancePolicyService = new GovernancePolicyService(governancePolicyRepository(), authContextResolver, CLOCK);
  private static volatile WorkstreamService workstreamService = workstreamService(workstreamLogRepository());

  static {
    startup();
  }

  private StarterSecurityComponents() {}

  /** Idempotent startup hook used by the Akka @Setup class and by local tests/endpoints. */
  public static void startup() {
    STARTED.compareAndSet(false, true);
    if (FailClosedFoundationRuntime.testRuntime()) {
      agentBehaviorSeedLoader.importStarterDefaults("tenant-starter", "starter-bootstrap", "corr-starter-agent-seed");
    }
  }

  /** Bind normal endpoint/agent runtime seams to Akka durable repositories once a ComponentClient is available. */
  public static void bindAkkaRuntime(ComponentClient componentClient) {
    if (componentClient == null || !AKKA_RUNTIME_BOUND.compareAndSet(false, true)) return;
    var durableIdentity = new AkkaIdentityRepository(componentClient);
    var durableInvitations = new AkkaInvitationRepository(componentClient);
    var durableAgentBehavior = new AkkaAgentBehaviorRepository(componentClient);
    identityRepository = durableIdentity;
    authContextResolver = new AuthContextResolver(durableIdentity);
    meService = new MeService(authContextResolver);
    userAdminService = new UserAdminService(durableIdentity, CLOCK);
    BootstrapAdminSeeder.seedConfiguredAdmins(durableIdentity, System.getenv("ADMIN_USERS"));
    var durableRuntime = new AgentRuntimeService(durableAgentBehavior, authContextResolver, CLOCK, MODEL_PROVIDER_CLIENT, new AkkaAgentRuntimeTraceSink(componentClient));
    invitationRepository = durableInvitations;
    agentBehaviorRepository = durableAgentBehavior;
    agentBehaviorSeedLoader = new AgentBehaviorSeedLoader(durableAgentBehavior, CLOCK);
    agentBehaviorSeedLoader.importStarterDefaults("tenant-starter", "starter-bootstrap", "corr-starter-agent-seed");
    agentRuntimeService = durableRuntime;
    agentRuntimeToolResolver = new AgentRuntimeToolResolver(durableAgentBehavior, durableRuntime);
    invitationService = new InvitationService(durableIdentity, durableInvitations, CLOCK);
    invitationView = new InvitationView(invitationService);
    auditTraceService = new AuditTraceService(authContextResolver, auditTraceRepository());
    governancePolicyService = new GovernancePolicyService(governancePolicyRepository(), authContextResolver, CLOCK);
    workstreamService = workstreamService(workstreamLogRepository());
  }

  public static AuthContextResolver authContextResolver() {
    return authContextResolver;
  }

  public static MeService meService() {
    return meService;
  }

  public static WorkstreamService workstreamService() {
    return workstreamService;
  }

  public static WorkstreamService workstreamService(WorkstreamLogRepository workstreamLogRepository) {
    return new WorkstreamService(meService, authContextResolver, new UserDirectoryView(userAdminService), invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, new FailClosedWorkstreamAgentRuntimeInvoker(), workstreamLogRepository, accessReviewTaskRepository(), auditTraceRepository(workstreamLogRepository), governancePolicyRepository());
  }

  public static WorkstreamService workstreamService(ComponentClient componentClient, WorkstreamLogRepository workstreamLogRepository) {
    bindAkkaRuntime(componentClient);
    return new WorkstreamService(meService, authContextResolver, new UserDirectoryView(userAdminService), invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, new DefaultWorkstreamAgentRuntimeInvoker(agentRuntimeService, componentClient), workstreamLogRepository, accessReviewTaskRepository(), auditTraceRepository(workstreamLogRepository), governancePolicyRepository());
  }

  public static InvitationService invitationService() {
    return invitationService;
  }

  public static InvitationView invitationView() {
    return invitationView;
  }

  public static UserAdminService userAdminService() {
    return userAdminService;
  }

  public static IdentityRepository identityRepository() {
    return identityRepository;
  }

  /** Test-only hook for unit tests that use explicit test-source doubles instead of Akka TestKit. */
  public static void bindTestIdentityRepository(IdentityRepository testRepository) {
    if (!FailClosedFoundationRuntime.testRuntime()) throw FailClosedFoundationRuntime.unavailable("Test identity repository binding");
    identityRepository = testRepository;
    authContextResolver = new AuthContextResolver(testRepository);
    meService = new MeService(authContextResolver);
    userAdminService = new UserAdminService(testRepository, CLOCK);
    invitationService = new InvitationService(testRepository, invitationRepository, CLOCK);
    invitationView = new InvitationView(invitationService);
    auditTraceService = new AuditTraceService(authContextResolver, auditTraceRepository());
    governancePolicyService = new GovernancePolicyService(governancePolicyRepository(), authContextResolver, CLOCK);
    workstreamService = workstreamService(workstreamLogRepository());
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

  private static WorkstreamLogRepository workstreamLogRepository() {
    return FailClosedFoundationRuntime.localDemoOrTestEnabled() ? new LocalDemoWorkstreamLogRepository() : new FailClosedWorkstreamLogRepository();
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
    return FailClosedFoundationRuntime.localDemoOrTestEnabled() ? new LocalDemoGovernancePolicyRepository() : new FailClosedGovernancePolicyRepository();
  }

  private static final class UnboundIdentityRepository implements IdentityRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("Identity foundation runtime");
    }

    public Optional<Account> findAccountByWorkosSubject(String workosUserId) { throw unavailable(); }
    public Optional<Account> findAccountByEmail(String normalizedEmail) { throw unavailable(); }
    public Account saveAccount(Account account) { throw unavailable(); }
    public UserProfile profile(String accountId) { throw unavailable(); }
    public UserProfile saveProfile(UserProfile profile) { throw unavailable(); }
    public UserSettings settings(String accountId) { throw unavailable(); }
    public UserSettings saveSettings(UserSettings settings) { throw unavailable(); }
    public List<Membership> membershipsByAccount(String accountId) { throw unavailable(); }
    public Optional<Membership> membership(String membershipId) { throw unavailable(); }
    public List<Membership> membershipRows() { throw unavailable(); }
    public Membership saveMembership(Membership membership) { throw unavailable(); }
    public Optional<Tenant> tenant(String tenantId) { throw unavailable(); }
    public Tenant saveTenant(Tenant tenant) { throw unavailable(); }
    public Optional<Customer> customer(String tenantId, String customerId) { throw unavailable(); }
    public Customer saveCustomer(Customer customer) { throw unavailable(); }
    public void appendAudit(AdminAuditEvent event) { throw unavailable(); }
    public List<AdminAuditEvent> auditEvents() { throw unavailable(); }
  }
}

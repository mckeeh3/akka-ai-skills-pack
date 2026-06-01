package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorRepository;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorSeedLoader;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeService;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeToolResolver;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeTraceSink;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentReferenceManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentRuntimeTrace;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentSkillManifest;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ModelConfigRef;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ModelPolicy;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ReferenceDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SkillDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AkkaAgentBehaviorRepository;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AkkaAgentRuntimeTraceSink;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.DefaultWorkstreamAgentRuntimeInvoker;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.FailClosedWorkstreamAgentRuntimeInvoker;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.ModelProviderClient;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.OpenAiModelProviderClient;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccessReviewTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.Customer;
import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicyProposal;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.Invitation;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
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
  private static final Clock CLOCK = Clock.systemUTC();
  private static final ModelProviderClient MODEL_PROVIDER_CLIENT = new OpenAiModelProviderClient();

  private static volatile IdentityRepository identityRepository = new UnboundIdentityRepository();
  private static volatile AuthContextResolver authContextResolver = new AuthContextResolver(identityRepository);
  private static volatile MeService meService = new MeService(authContextResolver);
  private static volatile UserAdminService userAdminService = new UserAdminService(identityRepository, CLOCK);
  private static volatile InvitationRepository invitationRepository = new UnboundInvitationRepository();
  private static volatile AgentBehaviorRepository agentBehaviorRepository = new UnboundAgentBehaviorRepository();
  private static volatile AgentBehaviorSeedLoader agentBehaviorSeedLoader = new AgentBehaviorSeedLoader(agentBehaviorRepository, CLOCK);
  private static volatile AgentRuntimeService agentRuntimeService = new AgentRuntimeService(agentBehaviorRepository, authContextResolver, CLOCK, MODEL_PROVIDER_CLIENT, traceSinkBeforeAkkaBinding());
  private static volatile AgentRuntimeToolResolver agentRuntimeToolResolver = new AgentRuntimeToolResolver(agentBehaviorRepository, agentRuntimeService);
  private static volatile InvitationService invitationService = new InvitationService(identityRepository, invitationRepository, CLOCK);
  private static volatile InvitationView invitationView = new InvitationView(invitationService);
  private static volatile AccessReviewTaskRepository accessReviewTaskRepository = new UnboundAccessReviewTaskRepository();
  private static volatile GovernancePolicyRepository governancePolicyRepository = new UnboundGovernancePolicyRepository();
  private static volatile AuditTraceService auditTraceService = new AuditTraceService(authContextResolver, auditTraceRepository());
  private static volatile GovernancePolicyService governancePolicyService = new GovernancePolicyService(governancePolicyRepository, authContextResolver, CLOCK);
  private static volatile AttentionRepository attentionRepository = new UnboundAttentionRepository();
  private static volatile AttentionService attentionService = new AttentionService(attentionRepository, authContextResolver, CLOCK);
  private static volatile WorkstreamService workstreamService = unboundWorkstreamService();

  static {
    startup();
  }

  private StarterSecurityComponents() {}

  /** Idempotent startup hook used by the Akka @Setup class and by local tests/endpoints. */
  public static void startup() {
    STARTED.compareAndSet(false, true);
  }

  /** Bind normal endpoint/agent runtime seams to Akka durable repositories once a ComponentClient is available. */
  public static void bindAkkaRuntime(ComponentClient componentClient) {
    if (componentClient == null) return;
    var durableIdentity = new AkkaIdentityRepository(componentClient);
    var durableInvitations = new AkkaInvitationRepository(componentClient);
    var durableAgentBehavior = new AkkaAgentBehaviorRepository(componentClient);
    identityRepository = durableIdentity;
    authContextResolver = new AuthContextResolver(durableIdentity);
    meService = new MeService(authContextResolver);
    userAdminService = new UserAdminService(durableIdentity, CLOCK);
    BootstrapAdminSeeder.seedConfiguredAdmins(durableIdentity, System.getenv("ADMIN_USERS"));
    var durableWorkstreamLog = new AkkaWorkstreamLogRepository(componentClient);
    var durableAccessReviews = new AkkaAccessReviewTaskRepository(componentClient);
    var durableGovernancePolicy = new AkkaGovernancePolicyRepository(componentClient);
    var durableAttention = new AkkaAttentionRepository(componentClient);
    var durableRuntime = new AgentRuntimeService(durableAgentBehavior, authContextResolver, CLOCK, MODEL_PROVIDER_CLIENT, new AkkaAgentRuntimeTraceSink(componentClient));
    invitationRepository = durableInvitations;
    agentBehaviorRepository = durableAgentBehavior;
    agentBehaviorSeedLoader = new AgentBehaviorSeedLoader(durableAgentBehavior, CLOCK);
    agentBehaviorSeedLoader.importStarterDefaults("tenant-starter", "starter-bootstrap", "corr-starter-agent-seed");
    agentRuntimeService = durableRuntime;
    agentRuntimeToolResolver = new AgentRuntimeToolResolver(durableAgentBehavior, durableRuntime);
    invitationService = new InvitationService(durableIdentity, durableInvitations, CLOCK);
    invitationView = new InvitationView(invitationService);
    accessReviewTaskRepository = durableAccessReviews;
    governancePolicyRepository = durableGovernancePolicy;
    attentionRepository = durableAttention;
    auditTraceService = new AuditTraceService(authContextResolver, new AkkaAuditTraceRepository(componentClient, durableWorkstreamLog));
    governancePolicyService = new GovernancePolicyService(durableGovernancePolicy, authContextResolver, CLOCK);
    attentionService = new AttentionService(durableAttention, authContextResolver, CLOCK);
    workstreamService = new WorkstreamService(meService, authContextResolver, new UserDirectoryView(userAdminService), invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, new DefaultWorkstreamAgentRuntimeInvoker(agentRuntimeService, componentClient), durableWorkstreamLog, durableAccessReviews, new AkkaAuditTraceRepository(componentClient, durableWorkstreamLog), durableGovernancePolicy);
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

  public static WorkstreamService workstreamService(ComponentClient componentClient, WorkstreamLogRepository workstreamLogRepository) {
    bindAkkaRuntime(componentClient);
    return new WorkstreamService(meService, authContextResolver, new UserDirectoryView(userAdminService), invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, new DefaultWorkstreamAgentRuntimeInvoker(agentRuntimeService, componentClient), workstreamLogRepository, accessReviewTaskRepository(), new AkkaAuditTraceRepository(componentClient, workstreamLogRepository), governancePolicyRepository());
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
    invitationRepository = new UnboundInvitationRepository();
    invitationService = new InvitationService(testRepository, invitationRepository, CLOCK);
    invitationView = new InvitationView(invitationService);
    accessReviewTaskRepository = new UnboundAccessReviewTaskRepository();
    governancePolicyRepository = new UnboundGovernancePolicyRepository();
    auditTraceService = new AuditTraceService(authContextResolver, auditTraceRepository());
    governancePolicyService = new GovernancePolicyService(governancePolicyRepository, authContextResolver, CLOCK);
    workstreamService = unboundWorkstreamService();
  }

  /** Test-only hook for unit tests that use explicit test-source invitation adapters. */
  public static void bindTestInvitationRepository(InvitationRepository testRepository) {
    if (!FailClosedFoundationRuntime.testRuntime()) throw FailClosedFoundationRuntime.unavailable("Test invitation repository binding");
    invitationRepository = testRepository;
    invitationService = new InvitationService(identityRepository, invitationRepository, CLOCK);
    invitationView = new InvitationView(invitationService);
  }

  /** Test-only hook for unit tests that use explicit test-source log/audit adapters. */
  public static void bindTestAuditTraceRepository(AuditTraceRepository testRepository) {
    if (!FailClosedFoundationRuntime.testRuntime()) throw FailClosedFoundationRuntime.unavailable("Test audit trace repository binding");
    auditTraceService = new AuditTraceService(authContextResolver, testRepository);
  }

  /** Test-only hook for unit tests that use explicit test-source governance policy adapters. */
  public static void bindTestGovernancePolicyRepository(GovernancePolicyRepository testRepository) {
    if (!FailClosedFoundationRuntime.testRuntime()) throw FailClosedFoundationRuntime.unavailable("Test governance policy repository binding");
    governancePolicyRepository = testRepository;
    governancePolicyService = new GovernancePolicyService(testRepository, authContextResolver, CLOCK);
  }

  /** Test-only hook for unit tests that use explicit test-source access-review task adapters. */
  public static void bindTestAccessReviewTaskRepository(AccessReviewTaskRepository testRepository) {
    if (!FailClosedFoundationRuntime.testRuntime()) throw FailClosedFoundationRuntime.unavailable("Test access-review task repository binding");
    accessReviewTaskRepository = testRepository;
    workstreamService = unboundWorkstreamService();
  }

  /** Test-only hook for unit tests that use explicit test-source governed-agent adapters. */
  public static void bindTestAgentBehaviorRepository(AgentBehaviorRepository testRepository) {
    if (!FailClosedFoundationRuntime.testRuntime()) throw FailClosedFoundationRuntime.unavailable("Test agent behavior repository binding");
    agentBehaviorRepository = testRepository;
    agentBehaviorSeedLoader = new AgentBehaviorSeedLoader(testRepository, CLOCK);
    agentRuntimeService = new AgentRuntimeService(testRepository, authContextResolver, CLOCK, MODEL_PROVIDER_CLIENT, traceSinkBeforeAkkaBinding());
    agentRuntimeToolResolver = new AgentRuntimeToolResolver(testRepository, agentRuntimeService);
  }

  /** Test-only hook for unit tests that need a test-source trace sink before Akka binding. */
  public static void bindTestAgentRuntimeTraceSink(AgentRuntimeTraceSink testTraceSink) {
    if (!FailClosedFoundationRuntime.testRuntime()) throw FailClosedFoundationRuntime.unavailable("Test agent runtime trace sink binding");
    agentRuntimeService = new AgentRuntimeService(agentBehaviorRepository, authContextResolver, CLOCK, MODEL_PROVIDER_CLIENT, testTraceSink);
    agentRuntimeToolResolver = new AgentRuntimeToolResolver(agentBehaviorRepository, agentRuntimeService);
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

  public static AttentionService attentionService() {
    return attentionService;
  }

  public static ModelProviderClient modelProviderClient() {
    return MODEL_PROVIDER_CLIENT;
  }

  private static WorkstreamService unboundWorkstreamService() {
    var workstreamLogRepository = new UnboundWorkstreamLogRepository();
    return new WorkstreamService(meService, authContextResolver, new UserDirectoryView(userAdminService), invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, new FailClosedWorkstreamAgentRuntimeInvoker(), workstreamLogRepository, accessReviewTaskRepository(), auditTraceRepository(workstreamLogRepository), governancePolicyRepository());
  }

  private static AccessReviewTaskRepository accessReviewTaskRepository() {
    return accessReviewTaskRepository;
  }

  private static AuditTraceRepository auditTraceRepository() {
    return new UnboundAuditTraceRepository();
  }

  private static AuditTraceRepository auditTraceRepository(WorkstreamLogRepository workstreamLogRepository) {
    return new UnboundAuditTraceRepository();
  }

  private static AgentRuntimeTraceSink traceSinkBeforeAkkaBinding() {
    return new UnboundAgentRuntimeTraceSink();
  }

  private static GovernancePolicyRepository governancePolicyRepository() {
    return governancePolicyRepository;
  }

  private static final class UnboundAgentRuntimeTraceSink implements AgentRuntimeTraceSink {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("AgentRuntimeTraceSink");
    }

    public AgentRuntimeTrace record(AgentRuntimeTrace trace) { throw unavailable(); }
    public List<AgentRuntimeTrace> traces() { throw unavailable(); }
  }

  private static final class UnboundInvitationRepository implements InvitationRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("InvitationRepository");
    }

    public Optional<Invitation> invitation(String invitationId) { throw unavailable(); }
    public Optional<Invitation> findByIdempotencyKey(String idempotencyKey) { throw unavailable(); }
    public Optional<Invitation> findActiveDuplicate(String normalizedEmail, ScopeType scopeType, String tenantId, String customerId) { throw unavailable(); }
    public Optional<Invitation> findByAcceptanceContext(String acceptanceContextId) { throw unavailable(); }
    public Optional<Invitation> findByTokenHash(String tokenHash) { throw unavailable(); }
    public Invitation saveInvitation(Invitation invitation) { throw unavailable(); }
    public void enqueueEmail(EmailOutboxMessage message) { throw unavailable(); }
    public Optional<EmailOutboxMessage> email(String outboxId) { throw unavailable(); }
    public List<EmailOutboxMessage> queuedEmails() { throw unavailable(); }
    public List<Invitation> invitations() { throw unavailable(); }
  }

  private static final class UnboundAgentBehaviorRepository implements AgentBehaviorRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("AgentBehaviorRepository");
    }

    public Optional<AgentDefinition> agentDefinition(String tenantId, String agentDefinitionId) { throw unavailable(); }
    public AgentDefinition saveAgentDefinition(AgentDefinition definition) { throw unavailable(); }
    public List<AgentDefinition> agentDefinitions(String tenantId) { throw unavailable(); }
    public Optional<PromptDocument> promptDocument(String tenantId, String promptDocumentId) { throw unavailable(); }
    public PromptDocument savePromptDocument(PromptDocument prompt) { throw unavailable(); }
    public Optional<SkillDocument> skillDocument(String tenantId, String skillDocumentId) { throw unavailable(); }
    public SkillDocument saveSkillDocument(SkillDocument skill) { throw unavailable(); }
    public List<SkillDocument> skillDocuments(String tenantId) { throw unavailable(); }
    public Optional<ReferenceDocument> referenceDocument(String tenantId, String referenceDocumentId) { throw unavailable(); }
    public ReferenceDocument saveReferenceDocument(ReferenceDocument reference) { throw unavailable(); }
    public List<ReferenceDocument> referenceDocuments(String tenantId) { throw unavailable(); }
    public Optional<AgentSkillManifest> skillManifest(String tenantId, String manifestId) { throw unavailable(); }
    public AgentSkillManifest saveSkillManifest(AgentSkillManifest manifest) { throw unavailable(); }
    public Optional<AgentReferenceManifest> referenceManifest(String tenantId, String manifestId) { throw unavailable(); }
    public AgentReferenceManifest saveReferenceManifest(AgentReferenceManifest manifest) { throw unavailable(); }
    public Optional<ToolPermissionBoundary> toolBoundary(String tenantId, String boundaryId) { throw unavailable(); }
    public ToolPermissionBoundary saveToolBoundary(ToolPermissionBoundary boundary) { throw unavailable(); }
    public Optional<ModelConfigRef> modelConfigRef(String tenantId, String modelConfigRefId) { throw unavailable(); }
    public ModelConfigRef saveModelConfigRef(ModelConfigRef modelConfigRef) { throw unavailable(); }
    public Optional<ModelPolicy> modelPolicy(String tenantId, String modelPolicyRefId) { throw unavailable(); }
    public ModelPolicy saveModelPolicy(ModelPolicy modelPolicy) { throw unavailable(); }
  }

  private static final class UnboundAccessReviewTaskRepository implements AccessReviewTaskRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("AccessReviewTaskRepository");
    }

    public Optional<AccessReviewTask> find(String taskId) { throw unavailable(); }
    public Optional<AccessReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) { throw unavailable(); }
    public AccessReviewTask save(AccessReviewTask task) { throw unavailable(); }
  }

  private static final class UnboundGovernancePolicyRepository implements GovernancePolicyRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("GovernancePolicyRepository");
    }

    public Optional<GovernancePolicyProposal> findProposal(String tenantId, String customerId, String proposalId) { throw unavailable(); }
    public Optional<GovernancePolicyProposal> findByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey) { throw unavailable(); }
    public GovernancePolicyProposal saveProposal(GovernancePolicyProposal proposal) { throw unavailable(); }
    public List<GovernancePolicyProposal> listProposals(String tenantId, String customerId) { throw unavailable(); }
  }

  private static final class UnboundAttentionRepository implements AttentionRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("AttentionRepository");
    }

    public AttentionItem upsert(AttentionItem item) { throw unavailable(); }
    public Optional<AttentionItem> find(String tenantId, String itemId) { throw unavailable(); }
    public List<AttentionItem> listTenant(String tenantId) { throw unavailable(); }
    public AttentionItem save(AttentionItem item) { throw unavailable(); }
  }

  private static final class UnboundWorkstreamLogRepository implements WorkstreamLogRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("WorkstreamLogRepository");
    }

    public List<WorkstreamService.WorkstreamItem> items(String tenantId, String selectedContextId, String functionalAgentId) { throw unavailable(); }
    public Optional<WorkstreamService.SurfaceEnvelope> surface(String tenantId, String selectedContextId, String surfaceId) { throw unavailable(); }
    public Optional<WorkstreamLogRepository.WorkstreamMessageLogEntry> findByIdempotencyKey(String tenantId, String selectedContextId, String functionalAgentId, String idempotencyKey) { throw unavailable(); }
    public WorkstreamLogRepository.WorkstreamMessageLogEntry appendMessage(WorkstreamLogRepository.WorkstreamMessageLogEntry entry) { throw unavailable(); }
    public WorkstreamService.WorkstreamItem appendSystemEntry(String tenantId, String selectedContextId, WorkstreamService.WorkstreamItem item, WorkstreamService.SurfaceEnvelope surface) { throw unavailable(); }
  }

  private static final class UnboundAuditTraceRepository implements AuditTraceRepository {
    @Override
    public List<AuditTraceService.TraceEvent> eventsFor(AuthContextResolver.ResolvedMe actor, String correlationId) {
      throw FailClosedFoundationRuntime.unavailable("AuditTraceRepository");
    }
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

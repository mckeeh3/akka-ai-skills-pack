package ai.first.application.foundation.identity;

import akka.javasdk.client.ComponentClient;
import ai.first.application.foundation.agent.AgentBehaviorRepository;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AgentRuntimeService;
import ai.first.application.foundation.agent.AgentRuntimeToolResolver;
import ai.first.application.foundation.agent.AgentRuntimeTraceSink;
import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentReferenceManifest;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import ai.first.domain.foundation.agent.ModelConfigRef;
import ai.first.domain.foundation.agent.ModelPolicy;
import ai.first.domain.foundation.agent.PromptDocument;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.agent.SkillDocument;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import ai.first.application.foundation.agent.AkkaAgentBehaviorRepository;
import ai.first.application.foundation.agent.AkkaAgentRuntimeTraceSink;
import ai.first.application.coreapp.useradmin.ComponentClientAccessReviewAutonomousAgentRuntime;
import ai.first.application.coreapp.governance.ComponentClientGovernancePolicyImpactAutonomousAgentRuntime;
import ai.first.application.coreapp.myaccount.ComponentClientMyAccountPersonalAttentionDigestAutonomousAgentRuntime;
import ai.first.application.foundation.agent.DefaultWorkstreamAgentRuntimeInvoker;
import ai.first.application.foundation.agent.FailClosedWorkstreamAgentRuntimeInvoker;
import ai.first.application.foundation.agent.ModelProviderClient;
import ai.first.application.foundation.agent.OpenAiModelProviderClient;
import ai.first.domain.coreapp.audit.AuditTraceSummaryTask;
import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import ai.first.domain.coreapp.agentadmin.PromptRiskReviewTask;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.attention.AttentionItem;
import ai.first.domain.foundation.identity.Customer;
import ai.first.domain.coreapp.myaccount.DigestExportRequest;
import ai.first.domain.foundation.governance.GovernancePolicyProposal;
import ai.first.domain.foundation.governance.GovernancePolicySimulationResult;
import ai.first.domain.foundation.email.EmailNotificationDelivery;
import ai.first.domain.foundation.email.EmailNotificationPreference;
import ai.first.domain.foundation.email.EmailOutboxMessage;
import ai.first.domain.foundation.invitation.Invitation;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.coreapp.myaccount.MyAccountPersonalAttentionDigestTask;
import ai.first.domain.foundation.notification.NotificationDeliveryAttempt;
import ai.first.domain.foundation.notification.NotificationExternalOutboxMessage;
import ai.first.domain.foundation.notification.NotificationItem;
import ai.first.domain.foundation.notification.NotificationPreference;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.workstream.WorkstreamEventEnvelope;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import ai.first.application.foundation.attention.AkkaAttentionRepository;
import ai.first.application.foundation.attention.AttentionProducerService;
import ai.first.application.foundation.attention.AttentionRepository;
import ai.first.application.foundation.attention.AttentionService;
import ai.first.application.foundation.audit.AkkaAuditTraceRepository;
import ai.first.application.foundation.audit.AuditTraceRepository;
import ai.first.application.foundation.audit.AuditTraceService;
import ai.first.application.foundation.email.EmailNotificationService;
import ai.first.application.foundation.email.ResendEmailService;
import ai.first.application.foundation.governance.AkkaGovernancePolicyRepository;
import ai.first.application.foundation.governance.GovernancePolicyRepository;
import ai.first.application.foundation.governance.GovernancePolicyService;
import ai.first.application.foundation.invitation.AkkaInvitationRepository;
import ai.first.application.foundation.invitation.InvitationRepository;
import ai.first.application.foundation.invitation.InvitationService;
import ai.first.application.foundation.invitation.InvitationView;
import ai.first.application.foundation.notification.AkkaNotificationRepository;
import ai.first.application.foundation.notification.NotificationRepository;
import ai.first.application.foundation.notification.NotificationService;
import ai.first.application.foundation.workstream.AkkaWorkstreamEventRepository;
import ai.first.application.foundation.workstream.AkkaWorkstreamLogRepository;
import ai.first.application.foundation.workstream.WorkstreamEventAttentionConsumer;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.application.foundation.workstream.WorkstreamEventRepository;
import ai.first.application.foundation.workstream.WorkstreamLogRepository;
import ai.first.application.coreapp.useradmin.AccessReviewTaskRepository;
import ai.first.application.coreapp.useradmin.AkkaAccessReviewTaskRepository;
import ai.first.application.coreapp.governance.AkkaGovernancePolicyImpactTaskRepository;
import ai.first.application.coreapp.governance.GovernancePolicyImpactTaskRepository;
import ai.first.application.coreapp.governance.InMemoryGovernancePolicyImpactTaskRepository;
import ai.first.application.coreapp.myaccount.AkkaMyAccountPersonalAttentionDigestTaskRepository;
import ai.first.application.coreapp.agentadmin.AkkaPromptRiskReviewTaskRepository;
import ai.first.application.coreapp.agentadmin.ComponentClientPromptRiskAutonomousAgentRuntime;
import ai.first.application.coreapp.agentadmin.PromptRiskReviewTaskRepository;
import ai.first.application.coreapp.myaccount.DigestExportService;
import ai.first.application.coreapp.audit.AkkaAuditTraceSummaryTaskRepository;
import ai.first.application.coreapp.audit.AuditTraceSummaryService;
import ai.first.application.coreapp.audit.AuditTraceSummaryTaskRepository;
import ai.first.application.coreapp.audit.ComponentClientAuditTraceSummaryAutonomousAgentRuntime;
import ai.first.application.coreapp.useradmin.FailClosedAccessReviewAutonomousAgentRuntime;
import ai.first.application.coreapp.myaccount.MyAccountPersonalAttentionDigestService;
import ai.first.application.coreapp.myaccount.MyAccountPersonalAttentionDigestTaskRepository;
import ai.first.application.coreapp.myaccount.MyAccountService;
import ai.first.application.coreapp.useradmin.UserAdminAccessReviewService;
import ai.first.application.coreapp.useradmin.UserAdminService;
import ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService;
import ai.first.application.coreapp.useradmin.TenantCustomerAdminService;
import ai.first.application.coreapp.useradmin.UserDirectoryView;
import ai.first.application.coreapp.workstream.WorkstreamService;

/** Shared starter-template service registry. Normal runtime binds identity and other foundation state to Akka components. */
public final class StarterSecurityComponents {
  private static final AtomicBoolean STARTED = new AtomicBoolean(false);
  private static final Clock CLOCK = Clock.systemUTC();
  private static final ModelProviderClient MODEL_PROVIDER_CLIENT = new OpenAiModelProviderClient();

  private static volatile IdentityRepository identityRepository = new UnboundIdentityRepository();
  private static volatile AuthContextResolver authContextResolver = new AuthContextResolver(identityRepository);
  private static volatile MeService meService;
  private static volatile UserAdminService userAdminService = new UserAdminService(identityRepository, CLOCK);
  private static volatile SaasOwnerOrganizationAdminService saasOwnerOrganizationAdminService = new SaasOwnerOrganizationAdminService(identityRepository, CLOCK);
  private static volatile TenantCustomerAdminService tenantCustomerAdminService = new TenantCustomerAdminService(identityRepository, CLOCK);
  private static volatile EnterpriseIdentityAdminService enterpriseIdentityAdminService = new EnterpriseIdentityAdminService(identityRepository, CLOCK);
  private static volatile InvitationRepository invitationRepository = new UnboundInvitationRepository();
  private static volatile AgentBehaviorRepository agentBehaviorRepository = new UnboundAgentBehaviorRepository();
  private static volatile AgentBehaviorSeedLoader agentBehaviorSeedLoader = new AgentBehaviorSeedLoader(agentBehaviorRepository, CLOCK);
  private static volatile AgentRuntimeService agentRuntimeService = new AgentRuntimeService(agentBehaviorRepository, authContextResolver, CLOCK, MODEL_PROVIDER_CLIENT, traceSinkBeforeAkkaBinding());
  private static volatile AgentRuntimeToolResolver agentRuntimeToolResolver = new AgentRuntimeToolResolver(agentBehaviorRepository, agentRuntimeService);
  private static volatile InvitationService invitationService = new InvitationService(identityRepository, invitationRepository, CLOCK);
  private static volatile InvitationView invitationView = new InvitationView(invitationService);
  private static volatile AccessReviewTaskRepository accessReviewTaskRepository = new UnboundAccessReviewTaskRepository();
  private static volatile PromptRiskReviewTaskRepository promptRiskReviewTaskRepository = new UnboundPromptRiskReviewTaskRepository();
  private static volatile AuditTraceSummaryTaskRepository auditTraceSummaryTaskRepository = new UnboundAuditTraceSummaryTaskRepository();
  private static volatile GovernancePolicyRepository governancePolicyRepository = new UnboundGovernancePolicyRepository();
  private static volatile GovernancePolicyImpactTaskRepository governancePolicyImpactTaskRepository = new InMemoryGovernancePolicyImpactTaskRepository();
  private static volatile AuditTraceService auditTraceService = new AuditTraceService(authContextResolver, auditTraceRepository());
  private static volatile GovernancePolicyService governancePolicyService = new GovernancePolicyService(governancePolicyRepository, authContextResolver, CLOCK);
  private static volatile AuditTraceSummaryService auditTraceSummaryService = new AuditTraceSummaryService(auditTraceSummaryTaskRepository, authContextResolver, CLOCK);
  private static volatile AttentionRepository attentionRepository = new UnboundAttentionRepository();
  private static volatile AttentionService attentionService = new AttentionService(attentionRepository, authContextResolver, CLOCK);
  private static volatile MyAccountPersonalAttentionDigestTaskRepository personalAttentionDigestTaskRepository = new UnboundMyAccountPersonalAttentionDigestTaskRepository();
  private static volatile NotificationRepository notificationRepository = new UnboundNotificationRepository();
  private static volatile NotificationService notificationService = new NotificationService(notificationRepository, authContextResolver, CLOCK);
  private static volatile EmailNotificationService emailNotificationService = new EmailNotificationService(notificationRepository, authContextResolver, new ResendEmailService(), ResendEmailService.DeliveryMode.LOCAL_OR_TEST, CLOCK);
  private static volatile DigestExportService digestExportService = new DigestExportService(notificationRepository, authContextResolver, CLOCK);
  private static volatile MyAccountPersonalAttentionDigestService personalAttentionDigestService = new MyAccountPersonalAttentionDigestService(personalAttentionDigestTaskRepository, authContextResolver, attentionService, CLOCK);
  private static volatile AttentionProducerService attentionProducerService = new AttentionProducerService(attentionRepository, identityRepository, CLOCK);
  private static volatile WorkstreamEventRepository workstreamEventRepository = new UnboundWorkstreamEventRepository();
  private static volatile WorkstreamEventAttentionConsumer workstreamEventAttentionConsumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, attentionProducerService, CLOCK);
  private static volatile WorkstreamEventPublisher workstreamEventPublisher = new WorkstreamEventPublisher(workstreamEventRepository, workstreamEventAttentionConsumer, CLOCK);
  static {
    meService = new MeService(authContextResolver, new MyAccountService(authContextResolver, attentionService));
  }
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
    meService = new MeService(authContextResolver, new MyAccountService(authContextResolver, attentionService));
    userAdminService = new UserAdminService(durableIdentity, CLOCK);
    saasOwnerOrganizationAdminService = new SaasOwnerOrganizationAdminService(durableIdentity, CLOCK);
    tenantCustomerAdminService = new TenantCustomerAdminService(durableIdentity, CLOCK);
    enterpriseIdentityAdminService = new EnterpriseIdentityAdminService(durableIdentity, CLOCK);
    BootstrapAdminSeeder.seedConfiguredAdmins(durableIdentity, System.getenv("ADMIN_USERS"));
    var durableWorkstreamLog = new AkkaWorkstreamLogRepository(componentClient);
    var durableAccessReviews = new AkkaAccessReviewTaskRepository(componentClient);
    var durablePromptRiskReviews = new AkkaPromptRiskReviewTaskRepository(componentClient);
    var durableAuditTraceSummaries = new AkkaAuditTraceSummaryTaskRepository(componentClient);
    var durableGovernancePolicy = new AkkaGovernancePolicyRepository(componentClient);
    var durableGovernancePolicyImpactTasks = new AkkaGovernancePolicyImpactTaskRepository(componentClient);
    var durableAttention = new AkkaAttentionRepository(componentClient);
    var durablePersonalAttentionDigests = new AkkaMyAccountPersonalAttentionDigestTaskRepository(componentClient);
    var durableWorkstreamEvents = new AkkaWorkstreamEventRepository(componentClient);
    var durableRuntime = new AgentRuntimeService(durableAgentBehavior, authContextResolver, CLOCK, MODEL_PROVIDER_CLIENT, new AkkaAgentRuntimeTraceSink(componentClient));
    invitationRepository = durableInvitations;
    agentBehaviorRepository = durableAgentBehavior;
    agentBehaviorSeedLoader = new AgentBehaviorSeedLoader(durableAgentBehavior, CLOCK);
    agentBehaviorSeedLoader.importStarterDefaults("tenant-starter", "starter-bootstrap", "corr-starter-agent-seed");
    agentRuntimeService = durableRuntime;
    agentRuntimeToolResolver = new AgentRuntimeToolResolver(durableAgentBehavior, durableRuntime);
    accessReviewTaskRepository = durableAccessReviews;
    promptRiskReviewTaskRepository = durablePromptRiskReviews;
    auditTraceSummaryTaskRepository = durableAuditTraceSummaries;
    governancePolicyRepository = durableGovernancePolicy;
    governancePolicyImpactTaskRepository = durableGovernancePolicyImpactTasks;
    attentionRepository = durableAttention;
    attentionService = new AttentionService(durableAttention, authContextResolver, CLOCK);
    personalAttentionDigestTaskRepository = durablePersonalAttentionDigests;
    notificationRepository = new AkkaNotificationRepository(componentClient);
    notificationService = new NotificationService(notificationRepository, authContextResolver, CLOCK);
    emailNotificationService = new EmailNotificationService(notificationRepository, authContextResolver, new ResendEmailService(), ResendEmailService.DeliveryMode.LOCAL_OR_TEST, CLOCK);
    digestExportService = new DigestExportService(notificationRepository, authContextResolver, CLOCK);
    meService = new MeService(authContextResolver, new MyAccountService(authContextResolver, attentionService));
    attentionProducerService = new AttentionProducerService(durableAttention, durableIdentity, CLOCK);
    workstreamEventRepository = durableWorkstreamEvents;
    workstreamEventAttentionConsumer = new WorkstreamEventAttentionConsumer(durableAttention, durableIdentity, attentionProducerService, CLOCK);
    workstreamEventPublisher = new WorkstreamEventPublisher(durableWorkstreamEvents, workstreamEventAttentionConsumer, CLOCK);
    personalAttentionDigestService = new MyAccountPersonalAttentionDigestService(durablePersonalAttentionDigests, authContextResolver, attentionService, CLOCK, new ComponentClientMyAccountPersonalAttentionDigestAutonomousAgentRuntime(componentClient, durableRuntime, agentRuntimeToolResolver, new MyAccountService(authContextResolver, attentionService)), attentionProducerService, workstreamEventPublisher);
    invitationService = new InvitationService(durableIdentity, durableInvitations, CLOCK, attentionProducerService, workstreamEventPublisher, new ResendEmailService(), invitationEmailDeliveryMode());
    invitationView = new InvitationView(invitationService);
    auditTraceService = new AuditTraceService(authContextResolver, new AkkaAuditTraceRepository(componentClient, durableWorkstreamLog));
    auditTraceSummaryService = new AuditTraceSummaryService(durableAuditTraceSummaries, authContextResolver, CLOCK, attentionProducerService, workstreamEventPublisher, new ComponentClientAuditTraceSummaryAutonomousAgentRuntime(componentClient, agentRuntimeService, agentRuntimeToolResolver));
    governancePolicyService = new GovernancePolicyService(durableGovernancePolicy, authContextResolver, CLOCK, attentionProducerService);
    workstreamService = new WorkstreamService(meService, authContextResolver, new UserDirectoryView(userAdminService), invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, new DefaultWorkstreamAgentRuntimeInvoker(agentRuntimeService, componentClient), durableWorkstreamLog, durableAccessReviews, new AkkaAuditTraceRepository(componentClient, durableWorkstreamLog), durableGovernancePolicy, attentionService, attentionProducerService, workstreamEventPublisher, workstreamEventRepository, new ComponentClientAccessReviewAutonomousAgentRuntime(componentClient, agentRuntimeService, agentRuntimeToolResolver), durablePromptRiskReviews, new ComponentClientPromptRiskAutonomousAgentRuntime(componentClient, agentRuntimeService, agentRuntimeToolResolver), notificationService, new ComponentClientGovernancePolicyImpactAutonomousAgentRuntime(componentClient, agentRuntimeService, agentRuntimeToolResolver));
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
    return new WorkstreamService(meService, authContextResolver, new UserDirectoryView(userAdminService), invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, new DefaultWorkstreamAgentRuntimeInvoker(agentRuntimeService, componentClient), workstreamLogRepository, accessReviewTaskRepository(), new AkkaAuditTraceRepository(componentClient, workstreamLogRepository), governancePolicyRepository(), attentionService(), attentionProducerService, workstreamEventPublisher, workstreamEventRepository, new ComponentClientAccessReviewAutonomousAgentRuntime(componentClient, agentRuntimeService, agentRuntimeToolResolver), promptRiskReviewTaskRepository(), new ComponentClientPromptRiskAutonomousAgentRuntime(componentClient, agentRuntimeService, agentRuntimeToolResolver), notificationService, new ComponentClientGovernancePolicyImpactAutonomousAgentRuntime(componentClient, agentRuntimeService, agentRuntimeToolResolver));
  }

  public static GovernancePolicyImpactTaskRepository governancePolicyImpactTaskRepository() {
    return governancePolicyImpactTaskRepository;
  }

  public static AuditTraceSummaryService auditTraceSummaryService() {
    return auditTraceSummaryService;
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

  public static SaasOwnerOrganizationAdminService saasOwnerOrganizationAdminService() {
    return saasOwnerOrganizationAdminService;
  }

  public static TenantCustomerAdminService tenantCustomerAdminService() {
    return tenantCustomerAdminService;
  }

  public static UserAdminAccessReviewService userAdminAccessReviewService() {
    return new UserAdminAccessReviewService(accessReviewTaskRepository(), userAdminService, CLOCK, attentionProducerService, workstreamEventPublisher, new FailClosedAccessReviewAutonomousAgentRuntime());
  }

  public static EnterpriseIdentityAdminService enterpriseIdentityAdminService() {
    return enterpriseIdentityAdminService;
  }

  public static IdentityRepository identityRepository() {
    return identityRepository;
  }

  /** Test-only hook for unit tests that use explicit test-source doubles instead of Akka TestKit. */
  public static void bindTestIdentityRepository(IdentityRepository testRepository) {
    if (!FailClosedFoundationRuntime.testRuntime()) throw FailClosedFoundationRuntime.unavailable("Test identity repository binding");
    identityRepository = testRepository;
    authContextResolver = new AuthContextResolver(testRepository);
    meService = new MeService(authContextResolver, new MyAccountService(authContextResolver, attentionService));
    userAdminService = new UserAdminService(testRepository, CLOCK);
    saasOwnerOrganizationAdminService = new SaasOwnerOrganizationAdminService(testRepository, CLOCK);
    tenantCustomerAdminService = new TenantCustomerAdminService(testRepository, CLOCK);
    enterpriseIdentityAdminService = new EnterpriseIdentityAdminService(testRepository, CLOCK);
    invitationRepository = new UnboundInvitationRepository();
    attentionProducerService = new AttentionProducerService(attentionRepository, testRepository, CLOCK);
    notificationService = new NotificationService(notificationRepository, authContextResolver, CLOCK);
    emailNotificationService = new EmailNotificationService(notificationRepository, authContextResolver, new ResendEmailService(), ResendEmailService.DeliveryMode.LOCAL_OR_TEST, CLOCK);
    digestExportService = new DigestExportService(notificationRepository, authContextResolver, CLOCK);
    workstreamEventAttentionConsumer = new WorkstreamEventAttentionConsumer(attentionRepository, testRepository, attentionProducerService, CLOCK);
    workstreamEventPublisher = new WorkstreamEventPublisher(workstreamEventRepository, workstreamEventAttentionConsumer, CLOCK);
    invitationService = new InvitationService(testRepository, invitationRepository, CLOCK, attentionProducerService);
    invitationView = new InvitationView(invitationService);
    accessReviewTaskRepository = new UnboundAccessReviewTaskRepository();
    promptRiskReviewTaskRepository = new UnboundPromptRiskReviewTaskRepository();
    auditTraceSummaryTaskRepository = new UnboundAuditTraceSummaryTaskRepository();
    governancePolicyRepository = new UnboundGovernancePolicyRepository();
    auditTraceService = new AuditTraceService(authContextResolver, auditTraceRepository());
    auditTraceSummaryService = new AuditTraceSummaryService(auditTraceSummaryTaskRepository, authContextResolver, CLOCK);
    governancePolicyService = new GovernancePolicyService(governancePolicyRepository, authContextResolver, CLOCK);
    attentionService = new AttentionService(attentionRepository, authContextResolver, CLOCK);
    meService = new MeService(authContextResolver, new MyAccountService(authContextResolver, attentionService));
    workstreamService = unboundWorkstreamService();
  }

  /** Test-only hook for unit tests that use explicit test-source attention adapters. */
  public static void bindTestAttentionRepository(AttentionRepository testRepository) {
    if (!FailClosedFoundationRuntime.testRuntime()) throw FailClosedFoundationRuntime.unavailable("Test attention repository binding");
    attentionRepository = testRepository;
    attentionService = new AttentionService(testRepository, authContextResolver, CLOCK);
    notificationService = new NotificationService(notificationRepository, authContextResolver, CLOCK);
    emailNotificationService = new EmailNotificationService(notificationRepository, authContextResolver, new ResendEmailService(), ResendEmailService.DeliveryMode.LOCAL_OR_TEST, CLOCK);
    digestExportService = new DigestExportService(notificationRepository, authContextResolver, CLOCK);
    personalAttentionDigestService = new MyAccountPersonalAttentionDigestService(personalAttentionDigestTaskRepository, authContextResolver, attentionService, CLOCK);
    attentionProducerService = new AttentionProducerService(testRepository, identityRepository, CLOCK);
    workstreamEventAttentionConsumer = new WorkstreamEventAttentionConsumer(testRepository, identityRepository, attentionProducerService, CLOCK);
    workstreamEventPublisher = new WorkstreamEventPublisher(workstreamEventRepository, workstreamEventAttentionConsumer, CLOCK);
    invitationService = new InvitationService(identityRepository, invitationRepository, CLOCK, attentionProducerService);
    invitationView = new InvitationView(invitationService);
    auditTraceSummaryService = new AuditTraceSummaryService(auditTraceSummaryTaskRepository, authContextResolver, CLOCK, attentionProducerService, workstreamEventPublisher, new ai.first.application.coreapp.audit.FailClosedAuditTraceSummaryAutonomousAgentRuntime());
    governancePolicyService = new GovernancePolicyService(governancePolicyRepository, authContextResolver, CLOCK, attentionProducerService);
    meService = new MeService(authContextResolver, new MyAccountService(authContextResolver, attentionService));
    workstreamService = unboundWorkstreamService();
  }

  /** Test-only hook for unit tests that use explicit test-source invitation adapters. */
  public static void bindTestInvitationRepository(InvitationRepository testRepository) {
    if (!FailClosedFoundationRuntime.testRuntime()) throw FailClosedFoundationRuntime.unavailable("Test invitation repository binding");
    invitationRepository = testRepository;
    invitationService = new InvitationService(identityRepository, invitationRepository, CLOCK, attentionProducerService);
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
    governancePolicyService = new GovernancePolicyService(testRepository, authContextResolver, CLOCK, attentionProducerService);
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

  public static MyAccountPersonalAttentionDigestService personalAttentionDigestService() {
    return personalAttentionDigestService;
  }

  public static NotificationService notificationService() {
    return notificationService;
  }

  public static EmailNotificationService emailNotificationService() {
    return emailNotificationService;
  }

  public static DigestExportService digestExportService() {
    return digestExportService;
  }

  /** Test-only hook for unit tests that use explicit test-source notification adapters. */
  public static void bindTestNotificationRepository(NotificationRepository testRepository) {
    if (!FailClosedFoundationRuntime.testRuntime()) throw FailClosedFoundationRuntime.unavailable("Test notification repository binding");
    notificationRepository = testRepository;
    notificationService = new NotificationService(testRepository, authContextResolver, CLOCK);
    emailNotificationService = new EmailNotificationService(testRepository, authContextResolver, new ResendEmailService(), ResendEmailService.DeliveryMode.LOCAL_OR_TEST, CLOCK);
    digestExportService = new DigestExportService(testRepository, authContextResolver, CLOCK);
  }

  public static WorkstreamEventRepository workstreamEventRepository() {
    return workstreamEventRepository;
  }

  public static WorkstreamEventPublisher workstreamEventPublisher() {
    return workstreamEventPublisher;
  }

  /** Test-only hook for unit tests that use explicit test-source workstream event adapters. */
  public static void bindTestWorkstreamEventRepository(WorkstreamEventRepository testRepository) {
    if (!FailClosedFoundationRuntime.testRuntime()) throw FailClosedFoundationRuntime.unavailable("Test workstream event repository binding");
    workstreamEventRepository = testRepository;
    workstreamEventPublisher = new WorkstreamEventPublisher(testRepository, workstreamEventAttentionConsumer, CLOCK);
    invitationService = new InvitationService(identityRepository, invitationRepository, CLOCK, attentionProducerService, workstreamEventPublisher);
    invitationView = new InvitationView(invitationService);
  }

  public static ModelProviderClient modelProviderClient() {
    return MODEL_PROVIDER_CLIENT;
  }

  private static ResendEmailService.DeliveryMode invitationEmailDeliveryMode() {
    var explicit = System.getenv("INVITATION_EMAIL_DELIVERY_MODE");
    if (explicit == null || explicit.isBlank()) explicit = System.getenv("EMAIL_DELIVERY_MODE");
    if (explicit != null && !explicit.isBlank()) {
      var normalized = explicit.trim().toLowerCase(java.util.Locale.ROOT);
      if (normalized.equals("production") || normalized.equals("resend")) return ResendEmailService.DeliveryMode.PRODUCTION;
      if (normalized.equals("local") || normalized.equals("test") || normalized.equals("captured") || normalized.equals("capture")) return ResendEmailService.DeliveryMode.LOCAL_OR_TEST;
    }
    var appEnv = System.getenv("APP_ENV");
    if (appEnv == null || appEnv.isBlank()) appEnv = System.getenv("AKKA_ENVIRONMENT");
    if (appEnv != null && appEnv.trim().equalsIgnoreCase("production")) return ResendEmailService.DeliveryMode.PRODUCTION;
    var hasResendConfig = hasText(System.getenv("RESEND_API_KEY"))
        && (hasText(System.getenv("INVITE_EMAIL_FROM")) || hasText(System.getenv("RESEND_FROM_EMAIL")));
    return hasResendConfig ? ResendEmailService.DeliveryMode.PRODUCTION : ResendEmailService.DeliveryMode.LOCAL_OR_TEST;
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  private static WorkstreamService unboundWorkstreamService() {
    var workstreamLogRepository = new UnboundWorkstreamLogRepository();
    return new WorkstreamService(meService, authContextResolver, new UserDirectoryView(userAdminService), invitationView, userAdminService, invitationService, agentBehaviorRepository, agentRuntimeService, new FailClosedWorkstreamAgentRuntimeInvoker(), workstreamLogRepository, accessReviewTaskRepository(), auditTraceRepository(workstreamLogRepository), governancePolicyRepository(), attentionService(), attentionProducerService, workstreamEventPublisher, workstreamEventRepository, new FailClosedAccessReviewAutonomousAgentRuntime(), notificationService);
  }

  private static AccessReviewTaskRepository accessReviewTaskRepository() {
    return accessReviewTaskRepository;
  }

  private static PromptRiskReviewTaskRepository promptRiskReviewTaskRepository() {
    return promptRiskReviewTaskRepository;
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

  private static final class UnboundPromptRiskReviewTaskRepository implements PromptRiskReviewTaskRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("PromptRiskReviewTaskRepository");
    }

    public Optional<PromptRiskReviewTask> find(String taskId) { throw unavailable(); }
    public Optional<PromptRiskReviewTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) { throw unavailable(); }
    public PromptRiskReviewTask save(PromptRiskReviewTask task) { throw unavailable(); }
  }

  private static final class UnboundAuditTraceSummaryTaskRepository implements AuditTraceSummaryTaskRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("AuditTraceSummaryTaskRepository");
    }

    public Optional<AuditTraceSummaryTask> find(String taskId) { throw unavailable(); }
    public Optional<AuditTraceSummaryTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) { throw unavailable(); }
    public AuditTraceSummaryTask save(AuditTraceSummaryTask task) { throw unavailable(); }
  }

  private static final class UnboundMyAccountPersonalAttentionDigestTaskRepository implements MyAccountPersonalAttentionDigestTaskRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("MyAccountPersonalAttentionDigestTaskRepository");
    }

    public Optional<MyAccountPersonalAttentionDigestTask> find(String digestTaskId) { throw unavailable(); }
    public Optional<MyAccountPersonalAttentionDigestTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) { throw unavailable(); }
    public MyAccountPersonalAttentionDigestTask save(MyAccountPersonalAttentionDigestTask task) { throw unavailable(); }
  }

  private static final class UnboundNotificationRepository implements NotificationRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("NotificationRepository");
    }

    public NotificationItem upsert(NotificationItem item) { throw unavailable(); }
    public NotificationItem save(NotificationItem item) { throw unavailable(); }
    public Optional<NotificationItem> find(String tenantId, String notificationId) { throw unavailable(); }
    public Optional<NotificationItem> findByDedupeKey(String tenantId, String dedupeKey) { throw unavailable(); }
    public List<NotificationItem> listTenant(String tenantId) { throw unavailable(); }
    public NotificationPreference savePreference(NotificationPreference preference) { throw unavailable(); }
    public Optional<NotificationPreference> findPreference(String tenantId, String preferenceId) { throw unavailable(); }
    public List<NotificationPreference> listPreferences(String tenantId, String accountId) { throw unavailable(); }
    public EmailNotificationPreference saveEmailPreference(EmailNotificationPreference preference) { throw unavailable(); }
    public List<EmailNotificationPreference> listEmailPreferences(String tenantId, String accountId) { throw unavailable(); }
    public EmailNotificationDelivery saveEmailDelivery(EmailNotificationDelivery delivery) { throw unavailable(); }
    public Optional<EmailNotificationDelivery> findEmailDelivery(String tenantId, String deliveryId) { throw unavailable(); }
    public Optional<EmailNotificationDelivery> findEmailDeliveryByDedupeKey(String tenantId, String dedupeKey) { throw unavailable(); }
    public EmailOutboxMessage saveEmailOutbox(EmailOutboxMessage message) { throw unavailable(); }
    public Optional<EmailOutboxMessage> findEmailOutbox(String tenantId, String outboxId) { throw unavailable(); }
    public List<EmailOutboxMessage> listEmailOutbox(String tenantId) { throw unavailable(); }
    public NotificationDeliveryAttempt saveDeliveryAttempt(NotificationDeliveryAttempt attempt) { throw unavailable(); }
    public Optional<NotificationDeliveryAttempt> findDeliveryAttempt(String tenantId, String attemptId) { throw unavailable(); }
    public Optional<NotificationDeliveryAttempt> findDeliveryAttemptByDedupeKey(String tenantId, String dedupeKey) { throw unavailable(); }
    public List<NotificationDeliveryAttempt> listDeliveryAttempts(String tenantId, String accountId) { throw unavailable(); }
    public NotificationExternalOutboxMessage saveExternalOutbox(NotificationExternalOutboxMessage message) { throw unavailable(); }
    public List<NotificationExternalOutboxMessage> listExternalOutbox(String tenantId, String accountId) { throw unavailable(); }
    public DigestExportRequest saveDigestExportRequest(DigestExportRequest request) { throw unavailable(); }
    public Optional<DigestExportRequest> findDigestExportRequest(String tenantId, String requestId) { throw unavailable(); }
    public Optional<DigestExportRequest> findDigestExportRequestByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) { throw unavailable(); }
    public List<DigestExportRequest> listDigestExportRequests(String tenantId) { throw unavailable(); }
    public List<DigestExportRequest> listDueDigestExportRequests(String tenantId, Instant dueAt) { throw unavailable(); }
  }

  private static final class UnboundGovernancePolicyRepository implements GovernancePolicyRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("GovernancePolicyRepository");
    }

    public Optional<GovernancePolicyProposal> findProposal(String tenantId, String customerId, String proposalId) { throw unavailable(); }
    public Optional<GovernancePolicyProposal> findByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey) { throw unavailable(); }
    public GovernancePolicyProposal saveProposal(GovernancePolicyProposal proposal) { throw unavailable(); }
    public List<GovernancePolicyProposal> listProposals(String tenantId, String customerId) { throw unavailable(); }
    public Optional<GovernancePolicySimulationResult> findSimulation(String tenantId, String customerId, String simulationId) { throw unavailable(); }
    public Optional<GovernancePolicySimulationResult> findSimulationByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey) { throw unavailable(); }
    public GovernancePolicySimulationResult saveSimulation(GovernancePolicySimulationResult simulation) { throw unavailable(); }
    public List<GovernancePolicySimulationResult> listSimulations(String tenantId, String customerId, String proposalId) { throw unavailable(); }
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

  private static final class UnboundWorkstreamEventRepository implements WorkstreamEventRepository {
    private IllegalStateException unavailable() {
      return FailClosedFoundationRuntime.unavailable("WorkstreamEventRepository");
    }

    public WorkstreamEventEnvelope publish(WorkstreamEventEnvelope event) { throw unavailable(); }
    public Optional<WorkstreamEventEnvelope> find(String tenantId, String eventId) { throw unavailable(); }
    public Optional<WorkstreamEventEnvelope> findByIdempotencyKey(String tenantId, String idempotencyKey) { throw unavailable(); }
    public List<WorkstreamEventEnvelope> listTenant(String tenantId) { throw unavailable(); }
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
    public void deleteAccount(String accountId) { throw unavailable(); }
    public UserProfile profile(String accountId) { throw unavailable(); }
    public UserProfile saveProfile(UserProfile profile) { throw unavailable(); }
    public void deleteProfile(String accountId) { throw unavailable(); }
    public UserSettings settings(String accountId) { throw unavailable(); }
    public UserSettings saveSettings(UserSettings settings) { throw unavailable(); }
    public void deleteSettings(String accountId) { throw unavailable(); }
    public List<Membership> membershipsByAccount(String accountId) { throw unavailable(); }
    public Optional<Membership> membership(String membershipId) { throw unavailable(); }
    public List<Membership> membershipRows() { throw unavailable(); }
    public Membership saveMembership(Membership membership) { throw unavailable(); }
    public void deleteMembership(String membershipId) { throw unavailable(); }
    public Optional<Tenant> tenant(String tenantId) { throw unavailable(); }
    public List<Tenant> tenantRows() { throw unavailable(); }
    public Tenant saveTenant(Tenant tenant) { throw unavailable(); }
    public Optional<Customer> customer(String tenantId, String customerId) { throw unavailable(); }
    public List<Customer> customerRows() { throw unavailable(); }
    public Customer saveCustomer(Customer customer) { throw unavailable(); }
    public void appendAudit(AdminAuditEvent event) { throw unavailable(); }
    public List<AdminAuditEvent> auditEvents() { throw unavailable(); }
  }
}

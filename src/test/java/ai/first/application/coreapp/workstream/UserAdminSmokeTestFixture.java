package ai.first.application.coreapp.workstream;

import ai.first.application.coreapp.myaccount.MyAccountService;
import ai.first.application.coreapp.useradmin.FailClosedAccessReviewAutonomousAgentRuntime;
import ai.first.application.coreapp.useradmin.InMemoryTestAccessReviewTaskRepository;
import ai.first.application.coreapp.useradmin.UserAdminService;
import ai.first.application.coreapp.useradmin.UserDirectoryView;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AgentRuntimeService;
import ai.first.application.foundation.agent.InMemoryTestAgentBehaviorRepository;
import ai.first.application.foundation.agent.InMemoryTestAgentRuntimeTraceSink;
import ai.first.application.foundation.agent.ModelProviderClient;
import ai.first.application.foundation.agent.WorkstreamAgentRuntimeInvoker;
import ai.first.application.foundation.attention.AttentionProducerService;
import ai.first.application.foundation.attention.AttentionService;
import ai.first.application.foundation.attention.InMemoryTestAttentionRepository;
import ai.first.application.foundation.audit.InMemoryTestAuditTraceRepository;
import ai.first.application.foundation.governance.InMemoryTestGovernancePolicyRepository;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.InMemoryTestIdentityRepository;
import ai.first.application.foundation.identity.MeService;
import ai.first.application.foundation.identity.StarterSecurityComponents;
import ai.first.application.foundation.invitation.InvitationService;
import ai.first.application.foundation.invitation.InvitationView;
import ai.first.application.foundation.invitation.InMemoryTestInvitationRepository;
import ai.first.application.foundation.notification.InMemoryTestNotificationRepository;
import ai.first.application.foundation.notification.NotificationService;
import ai.first.application.foundation.workstream.InMemoryTestWorkstreamEventRepository;
import ai.first.application.foundation.workstream.InMemoryTestWorkstreamLogRepository;
import ai.first.application.foundation.workstream.WorkstreamEventAttentionConsumer;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.time.Clock;
import java.util.List;

/**
 * Test-only deterministic User Admin workstream setup for local browser/API smoke tests.
 *
 * <p>This fixture is compiled from {@code src/test} and uses explicit local repositories, fake model
 * responses, and captured notification/email paths. It must not be used by production startup, where
 * {@link ai.first.application.foundation.identity.BootstrapAdminSeeder#seedConfiguredAdmins} remains
 * limited to SaaS Owner bootstrap.</p>
 */
public final class UserAdminSmokeTestFixture {
  public static final String TENANT_ID = "tenant-1";
  public static final String TENANT_ADMIN_CONTEXT_ID = "membership-admin";
  public static final String MEMBER_CONTEXT_ID = "membership-member";
  public static final String SAAS_OWNER_CONTEXT_ID = "membership-owner";

  private final InMemoryTestIdentityRepository identityRepository;
  private final InMemoryTestInvitationRepository invitationRepository;
  private final InMemoryTestWorkstreamEventRepository eventRepository;
  private final WorkstreamService service;

  private UserAdminSmokeTestFixture(
      InMemoryTestIdentityRepository identityRepository,
      InMemoryTestInvitationRepository invitationRepository,
      InMemoryTestWorkstreamEventRepository eventRepository,
      WorkstreamService service) {
    this.identityRepository = identityRepository;
    this.invitationRepository = invitationRepository;
    this.eventRepository = eventRepository;
    this.service = service;
  }

  public static UserAdminSmokeTestFixture create() {
    var identityRepository = new InMemoryTestIdentityRepository();
    var invitationRepository = new InMemoryTestInvitationRepository();
    var resolver = new AuthContextResolver(identityRepository);
    var attentionRepository = new InMemoryTestAttentionRepository();
    var attentionService = new AttentionService(attentionRepository, resolver, Clock.systemUTC());
    var attentionProducerService = new AttentionProducerService(attentionRepository, identityRepository, Clock.systemUTC());
    var eventRepository = new InMemoryTestWorkstreamEventRepository();
    var workstreamEventConsumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, attentionProducerService, Clock.systemUTC());
    var workstreamEventPublisher = new WorkstreamEventPublisher(eventRepository, workstreamEventConsumer, Clock.systemUTC());
    var meService = new MeService(resolver, new MyAccountService(resolver, attentionService));
    var userAdminService = new UserAdminService(identityRepository, Clock.systemUTC());
    var invitationService = new InvitationService(identityRepository, invitationRepository, Clock.systemUTC(), attentionProducerService, workstreamEventPublisher);
    var agentRepository = new InMemoryTestAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(agentRepository, Clock.systemUTC()).importStarterDefaults(TENANT_ID, "smoke-bootstrap", "corr-smoke-agent-seed");
    var agentRuntimeService = new AgentRuntimeService(
        agentRepository,
        resolver,
        Clock.systemUTC(),
        request -> new ModelProviderClient.ModelProviderResponse(
            "## " + request.functionalAgentId() + " smoke model response\n\nProvider-backed smoke fixture markdown.",
            "test-fake-provider",
            "test-fake-model",
            "fake-response-id",
            "stop",
            "test-only smoke fixture model invocation"),
        new InMemoryTestAgentRuntimeTraceSink());
    WorkstreamAgentRuntimeInvoker runtimeInvoker = agentRuntimeService::invokeWorkstreamAgent;
    var workstreamLogRepository = new InMemoryTestWorkstreamLogRepository();
    var notificationService = new NotificationService(new InMemoryTestNotificationRepository(), resolver, Clock.systemUTC());
    var service = new WorkstreamService(
        meService,
        resolver,
        new UserDirectoryView(userAdminService),
        new InvitationView(invitationService),
        userAdminService,
        invitationService,
        agentRepository,
        agentRuntimeService,
        runtimeInvoker,
        workstreamLogRepository,
        new InMemoryTestAccessReviewTaskRepository(),
        new InMemoryTestAuditTraceRepository(agentRuntimeService, workstreamLogRepository),
        new InMemoryTestGovernancePolicyRepository(),
        attentionService,
        attentionProducerService,
        workstreamEventPublisher,
        eventRepository,
        new FailClosedAccessReviewAutonomousAgentRuntime(),
        notificationService);

    seedUserAdminSmokeData(identityRepository);
    StarterSecurityComponents.bindTestIdentityRepository(identityRepository);
    return new UserAdminSmokeTestFixture(identityRepository, invitationRepository, eventRepository, service);
  }

  public WorkstreamService service() {
    return service;
  }

  public InMemoryTestIdentityRepository identityRepository() {
    return identityRepository;
  }

  public InMemoryTestInvitationRepository invitationRepository() {
    return invitationRepository;
  }

  public InMemoryTestWorkstreamEventRepository eventRepository() {
    return eventRepository;
  }

  public WorkosIdentity tenantAdminIdentity() {
    return new WorkosIdentity("workos-admin", "admin@example.test", "Tenant Admin");
  }

  public WorkosIdentity memberIdentity() {
    return new WorkosIdentity("workos-member", "member@example.test", "Member User");
  }

  public WorkosIdentity saasOwnerIdentity() {
    return new WorkosIdentity("workos-owner", "owner@example.test", "SaaS Owner");
  }

  private static void seedUserAdminSmokeData(InMemoryTestIdentityRepository repository) {
    repository.putTenant(new Tenant(TENANT_ID, "Tenant One", true));
    repository.putTenant(new Tenant("tenant-starter", "Starter Organization", true));
    repository.putTenant(new Tenant("tenant-suspended", "Suspended Organization", false));
    putAccount(repository, "admin@example.test", "Tenant Admin", List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR), ScopeType.TENANT, TENANT_ID, null, TENANT_ADMIN_CONTEXT_ID);
    putAccount(repository, "member@example.test", "Member User", List.of(FoundationRole.TENANT_EMPLOYEE), ScopeType.TENANT, TENANT_ID, null, MEMBER_CONTEXT_ID);
    putAccount(repository, "owner@example.test", "SaaS Owner", List.of(FoundationRole.SAAS_OWNER_ADMIN), ScopeType.SAAS_OWNER, null, null, SAAS_OWNER_CONTEXT_ID);
  }

  private static void putAccount(
      InMemoryTestIdentityRepository repository,
      String email,
      String displayName,
      List<FoundationRole> roles,
      ScopeType scopeType,
      String tenantId,
      String customerId,
      String membershipId) {
    repository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "UNLINKED"));
    repository.putProfile(new UserProfile(email, email, displayName, null, null, null));
    repository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    repository.putMembership(new Membership(membershipId, email, scopeType, tenantId, customerId, roles, MembershipStatus.ACTIVE, false, null));
  }
}

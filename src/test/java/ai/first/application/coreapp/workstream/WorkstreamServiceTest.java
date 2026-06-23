package ai.first.application.coreapp.workstream;

import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentReferenceManifest;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import ai.first.application.coreapp.agentadmin.AgentAdminPromptRiskReviewService;
import ai.first.domain.foundation.identity.AuthContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.coreapp.myaccount.MyAccountService;
import ai.first.application.coreapp.useradmin.AccessReviewAutonomousAgentRuntime;
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
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.Customer;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ai.first.application.foundation.agent.DefaultWorkstreamAgentRuntimeInvoker;
import ai.first.application.foundation.agent.WorkstreamRuntimeAgent;
import ai.first.application.foundation.attention.AttentionProducerService;
import ai.first.application.foundation.attention.AttentionService;
import ai.first.application.foundation.attention.InMemoryTestAttentionRepository;
import ai.first.application.foundation.audit.AkkaAuditTraceRepository;
import ai.first.application.foundation.audit.AuditTraceService;
import ai.first.application.foundation.audit.InMemoryTestAuditTraceRepository;
import ai.first.application.foundation.governance.InMemoryTestGovernancePolicyRepository;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.InMemoryTestIdentityRepository;
import ai.first.application.foundation.identity.MeService;
import ai.first.application.foundation.identity.StarterSecurityComponents;
import ai.first.application.foundation.invitation.InvitationService;
import ai.first.application.foundation.invitation.InvitationView;
import ai.first.application.foundation.invitation.InMemoryTestInvitationRepository;
import ai.first.application.foundation.notification.InMemoryTestNotificationRepository;
import ai.first.application.foundation.notification.NotificationService;
import ai.first.application.foundation.workstream.AkkaWorkstreamLogRepository;
import ai.first.application.foundation.workstream.InMemoryTestWorkstreamEventRepository;
import ai.first.application.foundation.workstream.InMemoryTestWorkstreamLogRepository;
import ai.first.application.foundation.workstream.WorkstreamEventAttentionConsumer;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.api.coreapp.workstream.WorkstreamEndpoint;

class WorkstreamServiceTest {
  private InMemoryTestIdentityRepository identityRepository;
  private InMemoryTestAgentBehaviorRepository agentRepository;
  private InMemoryTestWorkstreamEventRepository eventRepository;
  private InMemoryTestInvitationRepository invitationRepository;
  private InvitationService invitationService;
  private WorkstreamService service;
  private TrackingWorkstreamAgentRuntimeTestAdapter trackingRuntimeInvoker;

  @BeforeEach
  void setUp() {
    identityRepository = new InMemoryTestIdentityRepository();
    invitationRepository = new InMemoryTestInvitationRepository();
    var resolver = new AuthContextResolver(identityRepository);
    var attentionRepository = new InMemoryTestAttentionRepository();
    var attentionService = new AttentionService(attentionRepository, resolver, Clock.systemUTC());
    var attentionProducerService = new AttentionProducerService(attentionRepository, identityRepository, Clock.systemUTC());
    eventRepository = new InMemoryTestWorkstreamEventRepository();
    var workstreamEventConsumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, attentionProducerService, Clock.systemUTC());
    var workstreamEventPublisher = new WorkstreamEventPublisher(eventRepository, workstreamEventConsumer, Clock.systemUTC());
    var meService = new MeService(resolver, new MyAccountService(resolver, attentionService));
    var userAdminService = new UserAdminService(identityRepository, Clock.systemUTC());
    invitationService = new InvitationService(identityRepository, invitationRepository, Clock.systemUTC(), attentionProducerService, workstreamEventPublisher);
    agentRepository = new InMemoryTestAgentBehaviorRepository();
    var seedLoader = new AgentBehaviorSeedLoader(agentRepository, Clock.systemUTC());
    seedLoader.importStarterDefaults("tenant-1", "bootstrap", "corr-agent-seed");
    seedLoader.importStarterDefaults(ai.first.application.foundation.workstream.WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, "bootstrap-platform", "corr-agent-seed-platform");
    var agentRuntimeService = new AgentRuntimeService(agentRepository, resolver, Clock.systemUTC(), request -> new ModelProviderClient.ModelProviderResponse("## " + request.functionalAgentId() + " model response\n\nProvider-backed test markdown.", "test-fake-provider", "test-fake-model", "fake-response-id", "stop", "unit-test fake model invocation"), new InMemoryTestAgentRuntimeTraceSink());
    trackingRuntimeInvoker = new TrackingWorkstreamAgentRuntimeTestAdapter(agentRuntimeService);
    var workstreamLogRepository = new InMemoryTestWorkstreamLogRepository();
    var notificationService = new NotificationService(new InMemoryTestNotificationRepository(), resolver, Clock.systemUTC());
    service = new WorkstreamService(meService, resolver, new UserDirectoryView(userAdminService), new InvitationView(invitationService), userAdminService, invitationService, agentRepository, agentRuntimeService, trackingRuntimeInvoker, workstreamLogRepository, new InMemoryTestAccessReviewTaskRepository(), new InMemoryTestAuditTraceRepository(agentRuntimeService, workstreamLogRepository), new InMemoryTestGovernancePolicyRepository(), attentionService, attentionProducerService, workstreamEventPublisher, eventRepository, new FailClosedAccessReviewAutonomousAgentRuntime(), notificationService);

    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.putCustomer(new Customer("tenant-1", "customer-1", "Customer One", true));
    identityRepository.putTenant(new Tenant("tenant-starter", "Starter Organization", true));
    identityRepository.putTenant(new Tenant("tenant-suspended", "Suspended Organization", false));
    identityRepository.saveAccount(new Account("admin@example.test", null, "admin@example.test", "admin@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("admin@example.test", "admin@example.test", "Tenant Admin", "Tenant", "Admin", null));
    identityRepository.putSettings(new UserSettings("admin@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership("membership-admin", "admin@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR), MembershipStatus.ACTIVE, false, null));
    identityRepository.saveAccount(new Account("member@example.test", null, "member@example.test", "member@example.test", AccountStatus.ACTIVE, "UNLINKED"));
    identityRepository.putProfile(new UserProfile("member@example.test", "member@example.test", "Member User", "Member", "User", null));
    identityRepository.putSettings(new UserSettings("member@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership("membership-member", "member@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.ACTIVE, false, null));
    identityRepository.saveAccount(new Account("owner@example.test", null, "owner@example.test", "owner@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("owner@example.test", "owner@example.test", "SaaS Owner", "SaaS", "Owner", null));
    identityRepository.putSettings(new UserSettings("owner@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership("membership-owner", "owner@example.test", ScopeType.SAAS_OWNER, null, null, List.of(FoundationRole.SAAS_OWNER_ADMIN), MembershipStatus.ACTIVE, false, null));
    identityRepository.saveAccount(new Account("customer@example.test", null, "customer@example.test", "customer@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("customer@example.test", "customer@example.test", "Customer Admin", "Customer", "Admin", null));
    identityRepository.putSettings(new UserSettings("customer@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership("membership-customer", "customer@example.test", ScopeType.CUSTOMER, "tenant-1", "customer-1", List.of(FoundationRole.CUSTOMER_ADMIN), MembershipStatus.ACTIVE, false, null));
    StarterSecurityComponents.bindTestIdentityRepository(identityRepository);
  }

  @Test
  void deterministicUserAdminBrowserSmokeFixtureProducesAuthorizedContextWithoutProviderCredentials() {
    var smoke = UserAdminSmokeTestFixture.create();

    var bootstrap = smoke.service().bootstrap(smoke.tenantAdminIdentity(), UserAdminSmokeTestFixture.TENANT_ADMIN_CONTEXT_ID, "corr-smoke-bootstrap");

    assertEquals(UserAdminSmokeTestFixture.TENANT_ADMIN_CONTEXT_ID, bootstrap.me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("user-admin-agent") && agent.availability().equals("visible")));
    assertTrue(bootstrap.me().visibleCapabilityIds().contains("user_admin.view_overview"));
    var dashboard = smoke.service().surface(smoke.tenantAdminIdentity(), UserAdminSmokeTestFixture.TENANT_ADMIN_CONTEXT_ID, "surface-user-admin-dashboard", "corr-smoke-dashboard");
    assertEquals("surface-user-admin-tenant-dashboard", dashboard.surfaceId());
    var users = smoke.service().surface(smoke.tenantAdminIdentity(), UserAdminSmokeTestFixture.TENANT_ADMIN_CONTEXT_ID, "surface-user-admin-users", "corr-smoke-users");
    assertEquals("user_admin.users.v1", users.data().get("surfaceContract"));
    assertTrue(users.toString().contains("member@example.test"));
    assertBrowserPayloadSafe(dashboard);
    assertBrowserPayloadSafe(users);
    assertTrue(smoke.identityRepository().auditEvents().stream().anyMatch(event -> event.correlationId().equals("corr-smoke-bootstrap") && event.result().name().equals("ALLOWED")));
  }

  @Test
  void userAdminSystemMessageDirectRecoverySurfaceIsBackendAuthoredAndReturnsDashboard() {
    var recovery = service.surface(identity(), "membership-admin", "surface-user-admin-system-message", "corr-useradmin-system-message-direct");

    assertEquals("surface-user-admin-system-message", recovery.surfaceId());
    assertEquals("system-message", recovery.surfaceType());
    assertEquals("user_admin.system_message.v1", recovery.data().get("surfaceContract"));
    assertEquals("not_found_or_redacted", recovery.data().get("status"));
    assertEquals(true, recovery.data().get("noEnumeration"));
    assertEquals(true, recovery.data().get("noFakeSuccess"));
    assertEquals(true, recovery.data().get("noDirectMutation"));
    assertTrue(recovery.toString().contains("selectedAuthContext"));
    assertTrue(recovery.toString().contains("readinessSummary"));
    assertTrue(recovery.toString().contains("validationSummary"));
    assertTrue(recovery.actions().stream().anyMatch(action -> action.actionId().equals("action-user-admin-return-dashboard")));
    assertTrue(recovery.actions().stream().anyMatch(action -> action.actionId().equals("action-user-admin-show-users")));
    assertBrowserPayloadSafe(recovery);

    var returned = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-return-dashboard", "user-admin.return-dashboard", "search-user-directory", "user_admin.view_overview", null, null, "membership-admin", recovery.surfaceId(), "corr-useradmin-system-message-return"));

    assertEquals("accepted", returned.status());
    assertEquals("surface-user-admin-tenant-dashboard", returned.resultSurface().surfaceId());
    assertEquals("user_admin.tenant_dashboard.v1", returned.resultSurface().data().get("surfaceContract"));
    assertBrowserPayloadSafe(returned.resultSurface());
  }

  @Test
  void saasOwnerBootstrapUsesPlatformScopeForDefaultAttentionAndNotifications() {
    var ownerIdentity = new WorkosIdentity("workos-owner@example.test", "owner@example.test", "SaaS Owner");

    var bootstrap = service.bootstrap(ownerIdentity, "membership-owner", "corr-owner-bootstrap");

    assertEquals("membership-owner", bootstrap.me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("my-account-agent") && agent.availability().equals("visible")));
    assertEquals("surface-my-account-dashboard", bootstrap.surfaces().get(0).surfaceId());
    assertTrue(bootstrap.surfaces().get(0).toString().contains("notificationCenter"));
    assertTrue(bootstrap.surfaces().get(0).toString().contains("attentionCounters"));
    assertTrue(eventRepository.listTenant(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID).isEmpty(), "Owner bootstrap should use platform-scoped attention/notification storage without requiring a tenant id.");
    assertBrowserPayloadSafe(bootstrap.surfaces().get(0));
  }

  @Test
  void saasOwnerPlatformInvitationEventsDoNotBreakMyAccountNotificationCenter() {
    var ownerIdentity = new WorkosIdentity("workos-owner@example.test", "owner@example.test", "SaaS Owner");
    var owner = new AuthContextResolver(identityRepository).resolveMe(ownerIdentity, "membership-owner", "corr-owner-resolve");
    var invite = invitationService.createInvitation(owner, new InvitationService.CreateInvitationRequest(
        "invite-platform-owner-center",
        ScopeType.SAAS_OWNER,
        null,
        null,
        "new-platform-owner@example.test",
        "New Platform Owner",
        List.of(FoundationRole.SAAS_OWNER_ADMIN),
        Instant.now().plusSeconds(3600),
        "platform-owner-onboarding",
        "corr-platform-owner-invite"));
    invitationService.recordDeliveryResult(invite.invitationId(), "delivery-platform-owner", true, null, null, "corr-platform-owner-delivery");

    var surface = service.surface(ownerIdentity, "membership-owner", "surface-my-account-notification-center", "corr-owner-center");

    assertEquals("surface-my-account-notification-center", surface.surfaceId());
    assertEquals("notification-center", surface.surfaceType());
    assertTrue(eventRepository.listTenant(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID).stream()
        .anyMatch(event -> event.eventType().equals("invitation.delivery.sent")));
  }

  @Test
  void productionAdminUsersBootstrapStillRejectsTenantAndCustomerAdmins() {
    var repository = new InMemoryTestIdentityRepository();

    assertThrows(IllegalArgumentException.class, () -> ai.first.application.foundation.identity.BootstrapAdminSeeder.seedConfiguredAdmins(repository, "tenant-admin@example.test:TENANT_ADMIN:tenant-1"));
    assertThrows(IllegalArgumentException.class, () -> ai.first.application.foundation.identity.BootstrapAdminSeeder.seedConfiguredAdmins(repository, "customer-admin@example.test:CUSTOMER_ADMIN:tenant-1/customer-1"));
    ai.first.application.foundation.identity.BootstrapAdminSeeder.seedConfiguredAdmins(repository, "owner@example.test:SAAS_OWNER_ADMIN:OWNER");

    assertTrue(repository.findAccountByEmail("owner@example.test").isPresent());
    assertTrue(repository.tenant(ai.first.application.foundation.identity.BootstrapAdminSeeder.DEFAULT_TENANT_ID).isEmpty(), "Production ADMIN_USERS bootstrap must not create tenant/customer scope fixtures.");
  }

  @Test
  void starterSourceContainsConcreteAkkaWorkstreamRuntimeAgentAndInvokerSeam() throws Exception {
    var agentSource = findSource("WorkstreamRuntimeAgent.java");
    var agentText = Files.readString(agentSource);
    assertTrue(agentText.contains("import akka.javasdk.agent.Agent;"), "Workstream runtime must import the Akka Agent base class");
    assertTrue(agentText.contains("extends Agent"), "Workstream runtime must be a concrete Akka Agent component");
    assertTrue(agentText.contains("@Component"), "Workstream runtime must be discoverable as an Akka component");
    assertFalse(agentText.matches("(?is).*class\\s+.*Fake.*"), "Production workstream agent must not be a fake runtime");

    var serviceText = Files.readString(findSource("WorkstreamService.java"));
    assertTrue(serviceText.contains("WorkstreamAgentRuntimeInvoker"), "WorkstreamService must depend on the Akka Agent runtime invoker seam");
    assertTrue(serviceText.contains("workstreamAgentRuntimeInvoker.invokeWorkstreamAgent"), "Successful message submission must go through the runtime invoker seam");

    var defaultInvokerText = Files.readString(findSource("DefaultWorkstreamAgentRuntimeInvoker.java"));
    assertTrue(defaultInvokerText.contains("ComponentClient"), "Production invoker must use ComponentClient to call the Akka Agent component");
    assertTrue(defaultInvokerText.contains("forAgent()"), "Production invoker must call the Akka Agent runtime path");
    assertTrue(defaultInvokerText.contains("WorkstreamRuntimeAgent::respond"), "Production invoker must target the workstream Akka Agent component");

    var endpointText = Files.readString(findSource("WorkstreamEndpoint.java"));
    assertTrue(endpointText.contains("workstreamService(componentClient"), "Browser/API message path must construct WorkstreamService with the ComponentClient-backed invoker");
    assertTrue(endpointText.contains("viewQueryTenantId"), "SaaS-owner My Account SSE must not query the Akka View with a missing tenantId");

    var componentsText = Files.readString(findSource("StarterSecurityComponents.java"));
    assertTrue(componentsText.contains("AkkaAuditTraceRepository"), "Normal Audit/Trace runtime must bind the Akka-backed trace repository");
    assertTrue(componentsText.contains("AkkaWorkstreamLogRepository"), "Normal workstream runtime must bind the Akka-backed log repository");
  }

  @Test
  void bootstrapLoadsBackendAuthoritativeDefaultDashboardWithoutSecrets() {
    var bootstrap = service.bootstrap(identity(), null, "corr-bootstrap");

    assertEquals("membership-admin", bootstrap.me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("user-admin-agent") && agent.availability().equals("visible")));
    assertEquals(1, bootstrap.items().size());
    assertEquals(1, bootstrap.surfaces().size());
    assertEquals("surface-my-account-dashboard", bootstrap.surfaces().get(0).surfaceId());
    assertEquals("my-account-agent", bootstrap.surfaces().get(0).ownerFunctionalAgentId());
    assertEquals("surface", bootstrap.items().get(0).kind());
    assertEquals("surface-my-account-dashboard", bootstrap.items().get(0).surfaceId());
    assertEquals("ready", bootstrap.items().get(0).status());
    assertTrue(bootstrap.surfaces().get(0).toString().contains("my_account.view_context"));
    assertFalse(bootstrap.toString().contains("invite-token"));
    assertFalse(bootstrap.toString().contains("tokenHash"));
    assertFalse(bootstrap.toString().contains("providerSecret"));
  }

  @Test
  void actionDispatcherRequiresSelectedContextAndIdempotency() {
    var missingKey = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "user_admin.invite_user", "user_admin.invite_user", null, null, "membership-admin", "surface-user-admin-dashboard", "corr-invite"));

    assertEquals("validation-error", missingKey.status());

    var mismatch = assertThrows(AuthorizationException.class, () -> service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-user-list", "action-display-user-list", "user_admin.view_overview", "user_admin.view_overview", null, null, "membership-other", "surface-user-admin-dashboard", "corr-forbidden")));
    assertEquals("CONTEXT_FORBIDDEN", mismatch.reasonCode());
  }

  @Test
  void userAdminDashboardAndUsersListAreBackendDerivedAndScoped() {
    var dashboard = service.surface(identity(), "membership-admin", "surface-user-admin-dashboard", "corr-useradmin-dashboard");

    assertEquals("surface-user-admin-tenant-dashboard", dashboard.surfaceId());
    assertEquals("dashboard", dashboard.surfaceType());
    assertEquals("user_admin.tenant_dashboard.v1", dashboard.data().get("surfaceContract"));
    assertTrue(dashboard.toString().contains("Tenant Admin Dashboard"));
    assertTrue(dashboard.toString().contains("tenant.invitation.manage"));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-invitation-create") && action.resultSurface().updateSurfaceId().equals("surface-user-admin-invitation-create")));
    assertFalse(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-invite-user")), "Dashboard Invite user shortcut must open the invite form rather than firing the create command without invitee input.");
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-read-access-review") && action.resultSurface().updateSurfaceId().equals("surface-user-admin-access-review-task")));
    var accessReviewStatus = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-read-access-review", "action-useradmin-read-access-review", "user_admin.access_review.read", "user_admin.access_review.read", null, null, "membership-admin", dashboard.surfaceId(), "corr-dashboard-access-review-status"));
    assertEquals("surface-user-admin-access-review-task", accessReviewStatus.resultSurface().surfaceId());
    assertTrue(dashboard.toString().contains("blocked_provider_or_runtime"));
    assertTrue(dashboard.traceIds().stream().anyMatch(trace -> trace.contains("trace-surface-user-admin-tenant-dashboard")));
    assertEquals("surface-user-admin-dashboard", ((Map<?, ?>) dashboard.data().get("canonicalSurface")).get("canonicalSurfaceId"));
    assertTrue(((List<?>) dashboard.data().get("attentionCounts")).stream().anyMatch(count -> count.toString().contains("failed_invitation_delivery")));
    assertTrue(((List<?>) dashboard.data().get("administeredPopulations")).stream().anyMatch(population -> population.toString().contains("targetSurfaceId=surface-user-admin-users")));
    assertTrue(((List<?>) dashboard.data().get("authorizedActions")).stream().anyMatch(action -> action.toString().contains("browserToolId=user-admin.show-users") && action.toString().contains("resultSurfaceId=surface-user-admin-users")));
    assertTrue(dashboard.toString().contains("diagnosticMetadataVisible=false"));

    var users = service.surface(identity(), "membership-admin", "surface-user-admin-users", "corr-useradmin-users");
    assertEquals("list-search", users.surfaceType());
    assertEquals("user_admin.users.v1", users.data().get("surfaceContract"));
    assertTrue(users.toString().contains("active-user"));
    assertTrue(users.toString().contains("targetSurfaceId=surface-user-admin-user-detail"));
    assertTrue(users.toString().contains("targetSurfaceType=show-inspection"));
    assertTrue(users.toString().contains("openActionId=action-display-user-detail"));
    assertTrue(users.toString().contains("safeActionContext"));
    assertTrue(users.toString().contains("canMutateInline=false"));
    assertTrue(users.toString().contains("createAction"));
    assertFalse(users.toString().contains("invite-token"));
    assertFalse(users.toString().contains("tokenHash"));
  }

  @Test
  void userAdminBackendAuthoredFormOptionsAreExposedForFrontendRendering() {
    var invite = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-useradmin-invitation-create", "action-open-useradmin-invitation-create", "user_admin.invite_user", "user_admin.invite_user", null, null, "membership-admin", "surface-user-admin-users", "corr-options-invite"));
    assertEquals("surface-user-admin-invitation-create", invite.resultSurface().surfaceId());
    assertTrue(invite.resultSurface().toString().contains("roleOptions"));
    assertTrue(invite.resultSurface().toString().contains("roleId=TENANT_EMPLOYEE"));
    assertTrue(invite.resultSurface().toString().contains("expiryOptions"));

    var support = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-useradmin-support-access-grant", "action-open-useradmin-support-access-grant", "user_admin.support_access.grant_revoke_extend", "user_admin.support_access.grant_revoke_extend", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", "surface-user-admin-user-detail", "corr-options-support"));
    assertEquals("surface-user-admin-support-access-grant", support.resultSurface().surfaceId());
    assertTrue(support.resultSurface().toString().contains("supportExpiryOptions"));
    assertTrue(support.resultSurface().toString().contains("purposeOptions"));
    assertTrue(support.resultSurface().toString().contains("maxDurationHours=8"));

    var status = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-useradmin-membership-status-confirmation", "action-open-useradmin-membership-status-confirmation", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", "surface-user-admin-user-detail", "corr-options-status"));
    assertEquals("surface-user-admin-membership-status-confirmation", status.resultSurface().surfaceId());
    assertTrue(status.resultSurface().toString().contains("statusOptions"));
    assertTrue(status.resultSurface().toString().contains("action-useradmin-disable-member"));
  }

  @Test
  void userAdminConformancePathCoversBackendAuthoredRoutingTypedResultsAndSafePayloads() {
    var dashboard = service.surface(identity(), "membership-admin", "surface-user-admin-dashboard", "corr-conformance-dashboard");
    assertEquals("dashboard", dashboard.surfaceType());
    assertEquals("surface-user-admin-dashboard", ((Map<?, ?>) dashboard.data().get("canonicalSurface")).get("canonicalSurfaceId"));
    assertTrue(((List<?>) dashboard.data().get("attentionCounts")).stream().allMatch(count -> count.toString().contains("targetSurfaceId=") && count.toString().contains("openActionId=")));
    assertTrue(((List<?>) dashboard.data().get("administeredPopulations")).stream().allMatch(population -> population.toString().contains("targetSurfaceId=surface-user-admin-users") && population.toString().contains("openActionId=")));
    assertBrowserPayloadSafe(dashboard);

    var users = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-users", "user-admin.show-users", "search-user-directory", "user_admin.list_members", null, null, "membership-admin", dashboard.surfaceId(), "corr-conformance-users"));
    assertEquals("surface-user-admin-users", users.resultSurface().surfaceId());
    assertEquals("list-search", users.resultSurface().surfaceType());
    assertTrue(users.resultSurface().toString().contains("targetSurfaceId=surface-user-admin-user-detail"));
    assertTrue(users.resultSurface().toString().contains("openActionId=action-display-user-detail"));
    assertTrue(users.resultSurface().toString().contains("canMutateInline=false"));
    assertBrowserPayloadSafe(users.resultSurface());

    var detail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-user-detail", "action-display-user-detail", "user_admin.list_members", "user_admin.list_members", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", users.resultSurface().surfaceId(), "corr-conformance-detail"));
    assertEquals("surface-user-admin-user-detail", detail.resultSurface().surfaceId());
    assertEquals("show-inspection", detail.resultSurface().surfaceType());
    assertEquals(false, ((Map<?, ?>) detail.resultSurface().data().get("permissionState")).get("canMutateInline"));
    assertTrue(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-membership-status-confirmation")));
    assertFalse(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-disable-member")));
    assertFalse(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-change-member-roles")));
    assertBrowserPayloadSafe(detail.resultSurface());

    var inviteForm = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-useradmin-invitation-create", "action-open-useradmin-invitation-create", "user_admin.invite_user", "user_admin.invite_user", null, null, "membership-admin", users.resultSurface().surfaceId(), "corr-conformance-invite-form"));
    assertEquals("create-form", inviteForm.resultSurface().surfaceType());
    assertTrue(inviteForm.resultSurface().data().containsKey("roleOptions"));
    assertTrue(inviteForm.resultSurface().toString().contains("expiryOptions"));
    assertBrowserPayloadSafe(inviteForm.resultSurface());

    var hiddenInvitation = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-invitation-detail", "action-display-invitation-detail", "user_admin.acceptance_status.read", "user_admin.acceptance_status.read", Map.of("invitationId", "invitation-hidden-cross-scope"), null, "membership-admin", users.resultSurface().surfaceId(), "corr-conformance-hidden-invite"));
    assertEquals("denied", hiddenInvitation.status());
    assertEquals("surface-user-admin-system-message", hiddenInvitation.resultSurface().surfaceId());
    assertEquals("user_admin.system_message.v1", hiddenInvitation.resultSurface().data().get("surfaceContract"));
    assertEquals(true, hiddenInvitation.resultSurface().data().get("noFakeSuccess"));
    assertFalse(hiddenInvitation.resultSurface().toString().contains("invitation-hidden-cross-scope"));
    assertBrowserPayloadSafe(hiddenInvitation.resultSurface());

    var accessReview = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-start-access-review", "action-useradmin-start-access-review", "user_admin.access_review.start", "user_admin.access_review.start", Map.of("scope", "tenant"), "idem-conformance-access-review", "membership-admin", dashboard.surfaceId(), "corr-conformance-access-review"));
    assertEquals("blocked-runtime", accessReview.status());
    assertEquals("workflow-status", accessReview.resultSurface().surfaceType());
    assertEquals("blocked_provider_or_runtime", accessReview.resultSurface().data().get("status"));
    assertEquals(true, accessReview.resultSurface().data().get("noDirectMutation"));
    assertBrowserPayloadSafe(accessReview.resultSurface());
  }

  @Test
  void saasOwnerUserAdminDashboardExposesOrganizationAdminSurface() {
    var dashboard = service.surface(ownerIdentity(), "membership-owner", "surface-user-admin-dashboard", "corr-owner-dashboard");

    assertEquals("surface-user-admin-saas-owner-dashboard", dashboard.surfaceId());
    assertEquals("user_admin.saas_owner_dashboard.v1", dashboard.data().get("surfaceContract"));
    assertTrue(dashboard.toString().contains("SaaS Owner Admin Dashboard"));
    assertTrue(dashboard.toString().contains("Show organizations"));
    assertTrue(dashboard.toString().contains("saas_owner.organization.list"));
    assertTrue(dashboard.toString().contains("saas_owner.tenant.manage"));
    assertTrue(dashboard.toString().contains("navigationTree"));

    var owner = new AuthContextResolver(identityRepository).resolveMe(ownerIdentity(), "membership-owner", "corr-owner-admin-list-seed");
    invitationService.createInvitation(owner, new InvitationService.CreateInvitationRequest(
        "invite-saas-owner-admin-list",
        ScopeType.SAAS_OWNER,
        null,
        null,
        "new-owner-admin@example.test",
        "New Owner Admin",
        List.of(FoundationRole.SAAS_OWNER_ADMIN),
        Instant.now().plusSeconds(3600),
        "saas-owner-admin-list-smoke",
        "corr-owner-admin-list-invite"));

    var ownerAdmins = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-saas-owner-admins", "user-admin.show-saas-owner-admins", "manage-saas-owner-admins", "saas_owner.admin.list", null, null, "membership-owner", dashboard.surfaceId(), "corr-owner-admin-list"));

    assertEquals("accepted", ownerAdmins.status());
    assertEquals("surface-user-admin-saas-owner-admins", ownerAdmins.resultSurface().surfaceId());
    assertEquals("list-search", ownerAdmins.resultSurface().surfaceType());
    assertEquals("user_admin.saas_owner_admins.v1", ownerAdmins.resultSurface().data().get("surfaceContract"));
    assertTrue(ownerAdmins.resultSurface().toString().contains("scopeType=saas_owner"));
    assertTrue(ownerAdmins.resultSurface().toString().contains("recordKind=admin_membership"));
    assertTrue(ownerAdmins.resultSurface().toString().contains("recordKind=admin_invitation"));
    assertTrue(ownerAdmins.resultSurface().toString().contains("targetSurfaceId=surface-user-admin-user-detail"));
    assertTrue(ownerAdmins.resultSurface().toString().contains("targetSurfaceId=surface-user-admin-invitation-detail"));
    assertTrue(ownerAdmins.resultSurface().toString().contains("action-open-saas-owner-admin-invitation-create"));
    assertTrue(ownerAdmins.resultSurface().toString().contains("hidden-app-owner-counts-redacted"));
    assertFalse(ownerAdmins.resultSurface().toString().contains("invite-token"));
    assertFalse(ownerAdmins.resultSurface().toString().contains("tokenHash"));
    assertBrowserPayloadSafe(ownerAdmins.resultSurface());

    var createForm = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-open-saas-owner-admin-invitation-create", "user-admin.open-saas-owner-admin-invite", "manage-saas-owner-admins", "saas_owner.admin.invite", null, null, "membership-owner", ownerAdmins.resultSurface().surfaceId(), "corr-owner-admin-create-form"));
    assertEquals("accepted", createForm.status());
    assertEquals("surface-user-admin-saas-owner-admin-invitation-create", createForm.resultSurface().surfaceId());
    assertEquals("create-form", createForm.resultSurface().surfaceType());
    assertEquals("user_admin.saas_owner_admin_invitation_create.v1", createForm.resultSurface().data().get("surfaceContract"));
    assertTrue(createForm.resultSurface().toString().contains("action-submit-saas-owner-admin-invitation"));
    assertTrue(createForm.resultSurface().toString().contains("deliveryReadiness"));
    assertTrue(createForm.resultSurface().toString().contains("hidden-app-owner-counts-redacted"));
    assertBrowserPayloadSafe(createForm.resultSurface());

    var createdInvite = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-submit-saas-owner-admin-invitation", "user-admin.submit-saas-owner-admin-invite", "manage-saas-owner-admins", "saas_owner.admin.invite",
        Map.of("email", "second-owner-admin@example.test", "displayName", "Second Owner Admin", "roles", "SAAS_OWNER_ADMIN", "reason", "runtime implementation smoke"),
        "idem-saas-owner-admin-create", "membership-owner", createForm.resultSurface().surfaceId(), "corr-owner-admin-create-submit"));
    assertTrue(List.of("accepted", "blocked-runtime").contains(createdInvite.status()));
    assertEquals("surface-user-admin-invitation-detail", createdInvite.resultSurface().surfaceId());
    assertEquals("show-inspection", createdInvite.resultSurface().surfaceType());
    assertTrue(createdInvite.resultSurface().toString().contains("second-owner-admin@example.test"));
    assertTrue(createdInvite.resultSurface().toString().contains("Role"));
    assertFalse(createdInvite.resultSurface().toString().contains("invite-token"));
    assertFalse(createdInvite.resultSurface().toString().contains("tokenHash"));
    assertBrowserPayloadSafe(createdInvite.resultSurface());

    var tenantDenied = assertThrows(AuthorizationException.class, () -> service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-saas-owner-admins", "user-admin.show-saas-owner-admins", "manage-saas-owner-admins", "saas_owner.admin.list", null, null, "membership-admin", "surface-user-admin-dashboard", "corr-tenant-owner-admin-list-denied")));
    assertTrue(tenantDenied.reasonCode().contains("CAPABILITY_FORBIDDEN"));

    var organization = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-organizations", "user-admin.show-organizations", "manage-organizations", "saas_owner.organization.list", null, null, "membership-owner", dashboard.surfaceId(), "corr-open-org-admin"));

    assertEquals("accepted", organization.status());
    assertEquals("surface-user-admin-organization-directory", organization.resultSurface().surfaceId());
    assertEquals("user_admin.organization_directory.v1", organization.resultSurface().data().get("surfaceContract"));
    assertTrue(organization.resultSurface().toString().contains("Open Organization create form"));
    assertTrue(organization.resultSurface().toString().contains("Tenant lifecycle boundary"));
    assertTrue(organization.resultSurface().toString().contains("branchRootSurfaceId=surface-user-admin-organization-directory"));
  }

  @Test
  void tenantUserAdminOmitsOrganizationBranchAndDirectAccessIsDeniedSafely() {
    var dashboard = service.surface(identity(), "membership-admin", "surface-user-admin-dashboard", "corr-tenant-dashboard-no-org");

    assertFalse(dashboard.toString().contains("action-user-admin-show-organizations"));
    assertFalse(dashboard.toString().contains("saas_owner.organization.list"));
    assertTrue(dashboard.toString().contains("action-display-user-list"));

    var denied = assertThrows(AuthorizationException.class, () -> service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-organizations", "user-admin.show-organizations", "manage-organizations", "saas_owner.organization.list", null, null, "membership-admin", dashboard.surfaceId(), "corr-tenant-org-denied")));
    assertTrue(denied.reasonCode().contains("CAPABILITY_FORBIDDEN"));
  }

  @Test
  void userAdminNavigationTreeTraversesBranchesWithTraceCorrelationAndSafePayloads() {
    var tenantDashboard = service.surface(identity(), "membership-admin", "surface-user-admin-dashboard", "corr-tree-tenant-dashboard");
    var users = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-users", "user-admin.show-users", "search-user-directory", "user_admin.list_members", null, null, "membership-admin", tenantDashboard.surfaceId(), "corr-tree-users"));
    assertEquals("accepted", users.status());
    assertEquals("surface-user-admin-users", users.resultSurface().surfaceId());
    assertEquals("corr-tree-users", users.resultSurface().correlationId());
    assertTrue(users.resultSurface().traceIds().contains("trace-surface-user-admin-users"));
    assertTrue(users.resultSurface().toString().contains("action-display-user-detail"));
    assertBrowserPayloadSafe(users.resultSurface());

    var detail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-user-detail", "action-display-user-detail", "user_admin.list_members", "user_admin.list_members", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", users.resultSurface().surfaceId(), "corr-tree-user-detail"));
    assertEquals("surface-user-admin-user-detail", detail.resultSurface().surfaceId());
    assertEquals("corr-tree-user-detail", detail.resultSurface().correlationId());
    assertTrue(detail.resultSurface().toString().contains("branchRootSurfaceId=surface-user-admin-users"));
    assertTrue(detail.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-users"));
    assertBrowserPayloadSafe(detail.resultSurface());

    var returnedUsers = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-users", "user-admin.show-users", "search-user-directory", "user_admin.list_members", null, null, "membership-admin", detail.resultSurface().surfaceId(), "corr-tree-users-return"));
    assertEquals("surface-user-admin-users", returnedUsers.resultSurface().surfaceId());
    assertEquals("corr-tree-users-return", returnedUsers.resultSurface().correlationId());

    var ownerDashboard = service.surface(ownerIdentity(), "membership-owner", "surface-user-admin-dashboard", "corr-tree-owner-dashboard");
    var organizations = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-organizations", "user-admin.show-organizations", "manage-organizations", "saas_owner.organization.list", null, null, "membership-owner", ownerDashboard.surfaceId(), "corr-tree-orgs"));
    assertEquals("surface-user-admin-organization-directory", organizations.resultSurface().surfaceId());
    assertEquals("corr-tree-orgs", organizations.resultSurface().correlationId());
    assertTrue(organizations.resultSurface().toString().contains("branchRootSurfaceId=surface-user-admin-organization-directory"));
    assertTrue(organizations.resultSurface().toString().contains("action-user-admin-show-organizations"));
    assertBrowserPayloadSafe(organizations.resultSurface());

    var organizationDetail = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-organization-read", "action-organization-read", "saas_owner.organization.read", "saas_owner.organization.read", Map.of("organizationId", "tenant-starter"), null, "membership-owner", organizations.resultSurface().surfaceId(), "corr-tree-org-detail"));
    assertEquals("surface-user-admin-organization-detail", organizationDetail.resultSurface().surfaceId());
    assertTrue(organizationDetail.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-organizations"));
    assertTrue(organizationDetail.resultSurface().toString().contains("Back to organizations"));
    assertTrue(organizationDetail.resultSurface().toString().contains("action-user-admin-show-organization-admins"));
    assertTrue(organizationDetail.resultSurface().toString().contains("availableTaskActions"));
    assertBrowserPayloadSafe(organizationDetail.resultSurface());

    var orgAdmins = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-organization-admins", "user-admin.show-organization-admins", "manage-organization-admins", "saas_owner.organization_admin.list", Map.of("organizationId", "tenant-starter", "tenantId", "tenant-starter"), null, "membership-owner", organizationDetail.resultSurface().surfaceId(), "corr-tree-org-admins"));
    assertEquals("accepted", orgAdmins.status());
    assertEquals("surface-user-admin-organization-admins", orgAdmins.resultSurface().surfaceId());
    assertEquals("tenant-starter", orgAdmins.resultSurface().data().get("organizationId"));
    assertTrue(orgAdmins.resultSurface().toString().contains("user_admin.organization_admins.v1"));
    assertTrue(orgAdmins.resultSurface().toString().contains("TENANT_ADMIN"));
    assertTrue(orgAdmins.resultSurface().toString().contains("adminSummary"));
    assertTrue(orgAdmins.resultSurface().toString().contains("targetScope"));
    assertTrue(orgAdmins.resultSurface().toString().contains("tenant-app-data-redacted"));
    assertBrowserPayloadSafe(orgAdmins.resultSurface());

    var directOrganizationDetail = service.surface(ownerIdentity(), "membership-owner", "surface-user-admin-organization-detail", "corr-tree-org-detail-direct");
    assertTrue(directOrganizationDetail.toString().contains("action-open-organization-admin-invitation-create"));
    assertBrowserPayloadSafe(directOrganizationDetail);

    var orgAdminInviteForm = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-open-organization-admin-invitation-create", "user-admin.open-organization-admin-invite", "manage-organization-admins", "saas_owner.organization_admin.invite", Map.of("organizationId", "tenant-starter", "tenantId", "tenant-starter"), null, "membership-owner", organizationDetail.resultSurface().surfaceId(), "corr-tree-org-admin-invite-form"));
    assertEquals("accepted", orgAdminInviteForm.status());
    assertEquals("surface-user-admin-organization-admin-invitation-create", orgAdminInviteForm.resultSurface().surfaceId());
    assertEquals("tenant-starter", orgAdminInviteForm.resultSurface().data().get("tenantId"));
    assertTrue(orgAdminInviteForm.resultSurface().toString().contains("targetScope"));
    assertTrue(orgAdminInviteForm.resultSurface().toString().contains("action-submit-organization-admin-invitation"));
    assertBrowserPayloadSafe(orgAdminInviteForm.resultSurface());

    var orgAdminInvite = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-submit-organization-admin-invitation", "user-admin.invite-organization-admin", "manage-organization-admins", "saas_owner.organization_admin.invite", Map.of("tenantId", "tenant-starter", "organizationId", "tenant-starter", "email", "org.admin@example.test", "displayName", "Organization Admin", "roles", "TENANT_ADMIN", "reason", "bootstrap-org-admin"), "idem-org-admin-invite", "membership-owner", orgAdminInviteForm.resultSurface().surfaceId(), "corr-tree-org-admin-invite"));
    assertEquals("accepted", orgAdminInvite.status(), orgAdminInvite.message() + " " + orgAdminInvite.resultSurface().data());
    assertEquals("surface-user-admin-invitation-detail", orgAdminInvite.resultSurface().surfaceId());
    assertEquals("tenant-starter", orgAdminInvite.resultSurface().data().get("organizationId"));
    assertEquals("organization-admin-invitation", orgAdminInvite.resultSurface().data().get("recordKind"));
    assertTrue(orgAdminInvite.resultSurface().toString().contains("action-user-admin-show-organization-admins"));
    var tenantScopedInvitation = invitationRepository.invitations().stream()
        .filter(invitation -> "org.admin@example.test".equals(invitation.normalizedEmail()))
        .findFirst()
        .orElseThrow();
    assertEquals(ScopeType.TENANT, tenantScopedInvitation.scopeType());
    assertEquals("tenant-starter", tenantScopedInvitation.tenantId());
    assertNull(tenantScopedInvitation.customerId());
    assertEquals(List.of(FoundationRole.TENANT_ADMIN), tenantScopedInvitation.requestedRoles());

    var returnedOrganizations = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-organizations", "user-admin.show-organizations", "manage-organizations", "saas_owner.organization.list", null, null, "membership-owner", organizationDetail.resultSurface().surfaceId(), "corr-tree-orgs-return"));
    assertEquals("surface-user-admin-organization-directory", returnedOrganizations.resultSurface().surfaceId());
    assertEquals("corr-tree-orgs-return", returnedOrganizations.resultSurface().correlationId());
  }

  @Test
  void userAdminOrganizationDeepLinkDenialUsesSafeSystemMessageForHiddenTargets() {
    var denied = service.runShellRequest(memberIdentity(), "membership-member", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "deep_link", "Open Organization Directory", null, "user-admin-agent", "surface-user-admin-organization-directory", null, "my-account-agent", null, null, "current_workstream", "corr-member-org-deeplink", "membership-member"));

    assertEquals("denied", denied.status());
    assertEquals("system_message", denied.resultSurface().surfaceType());
    assertEquals("TARGET_NOT_FOUND_OR_FORBIDDEN", denied.resultSurface().data().get("code"));
    assertEquals("corr-member-org-deeplink", denied.resultSurface().correlationId());
    assertFalse(denied.resultSurface().toString().contains("tenant-starter"));
    assertFalse(denied.resultSurface().toString().contains("saas_owner.organization.list"));
    assertBrowserPayloadSafe(denied.resultSurface());
  }

  @Test
  void organizationSurfaceGraphRoutesBySelectedOrganizationLifecycleState() {
    var create = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-open-organization-create", "action-open-organization-create", "saas_owner.tenant.manage", "saas_owner.tenant.manage", null, null, "membership-owner", "surface-user-admin-organization-directory", "corr-open-create"));
    assertEquals("accepted", create.status());
    assertEquals("surface-user-admin-organization-create", create.resultSurface().surfaceId());

    var createdOrganization = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-submit-organization-create", "user-admin.submit-organization-create", "manage-organizations", "saas_owner.tenant.manage", Map.of("organizationName", "Runtime Prompt Ready Org", "reason", "seed tenant agent defaults"), "idem-runtime-prompt-ready-org", "membership-owner", create.resultSurface().surfaceId(), "corr-create-runtime-ready-org"));
    var createdOrganizationMap = (Map<?, ?>) createdOrganization.resultSurface().data().get("organizationDetail");
    var createdOrganizationId = String.valueOf(createdOrganizationMap.get("organizationId"));
    assertTrue(agentRepository.agentDefinition(createdOrganizationId, AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID).isPresent(), "New Organizations need tenant-scoped governed agent defaults so tenant/customer workstream prompts can run after bootstrap.");

    var activeDetail = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-organization-read", "action-organization-read", "saas_owner.organization.read", "saas_owner.organization.read", Map.of("organizationId", "tenant-starter"), null, "membership-owner", "surface-user-admin-organization-directory", "corr-read-active"));
    assertEquals("surface-user-admin-organization-detail", activeDetail.resultSurface().surfaceId());
    assertTrue(activeDetail.resultSurface().toString().contains("suspend"));
    assertTrue(activeDetail.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-organizations"));

    var suspend = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-open-organization-suspend", "action-open-organization-suspend", "manage-organizations", "saas_owner.organization.suspend", Map.of("organizationId", "tenant-starter"), null, "membership-owner", activeDetail.resultSurface().surfaceId(), "corr-open-suspend"));
    assertEquals("accepted", suspend.status());
    assertEquals("surface-user-admin-organization-suspend-confirmation", suspend.resultSurface().surfaceId());
    assertTrue(suspend.resultSurface().toString().contains("tenant-starter"));
    assertTrue(suspend.resultSurface().toString().contains("Back to organizations"));

    var suspendedDetail = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-organization-read", "action-organization-read", "saas_owner.organization.read", "saas_owner.organization.read", Map.of("organizationId", "tenant-suspended"), null, "membership-owner", "surface-user-admin-organization-directory", "corr-read-suspended"));
    assertEquals("surface-user-admin-organization-detail", suspendedDetail.resultSurface().surfaceId());
    assertTrue(suspendedDetail.resultSurface().toString().contains("reactivate"));

    var reactivate = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-open-organization-reactivate", "action-open-organization-reactivate", "manage-organizations", "saas_owner.organization.reactivate", Map.of("organizationId", "tenant-suspended"), null, "membership-owner", suspendedDetail.resultSurface().surfaceId(), "corr-open-reactivate"));
    assertEquals("accepted", reactivate.status());
    assertEquals("surface-user-admin-organization-reactivate-confirmation", reactivate.resultSurface().surfaceId());
    assertTrue(reactivate.resultSurface().toString().contains("tenant-suspended"));

    var wrongState = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-open-organization-reactivate", "action-open-organization-reactivate", "manage-organizations", "saas_owner.organization.reactivate", Map.of("organizationId", "tenant-starter"), null, "membership-owner", activeDetail.resultSurface().surfaceId(), "corr-wrong-reactivate"));
    assertEquals("denied", wrongState.status());
    assertEquals("system_message", wrongState.resultSurface().surfaceType());
  }

  @Test
  void tenantCustomerBranchUsesDurableCustomerLifecycleState() {
    var createForm = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-customer-create", "user-admin.open-customer-create", "manage-customers", "tenant.customer.create", null, null, "membership-admin", "surface-user-admin-customer-directory", "corr-customer-create-open"));
    assertEquals("accepted", createForm.status());
    assertEquals("surface-user-admin-customer-create", createForm.resultSurface().surfaceId());
    assertEquals("user_admin.customer_create.v1", createForm.resultSurface().data().get("surfaceContract"));
    assertTrue(createForm.resultSurface().toString().contains("action-submit-customer-create"));
    assertTrue(createForm.resultSurface().toString().contains("tenant.customer.create"));
    assertTrue(createForm.resultSurface().toString().contains("validationPolicy"));
    assertTrue(createForm.resultSurface().toString().contains("creationBoundary"));
    assertTrue(createForm.resultSurface().toString().contains("tenant-app-data-redacted"));

    var create = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-submit-customer-create", "user-admin.create-customer", "manage-customers", "tenant.customer.create", Map.of("customerName", "Acme Customer", "reason", "test-create"), "idem-customer-create", "membership-admin", createForm.resultSurface().surfaceId(), "corr-customer-create"));
    assertEquals("accepted", create.status());
    assertEquals("surface-user-admin-customer-detail", create.resultSurface().surfaceId());
    assertTrue(create.resultSurface().toString().contains("Acme Customer"));
    var customerId = ((Map<?, ?>) create.resultSurface().data().get("customerDetail")).get("customerId").toString();

    identityRepository.saveCustomer(new Customer("tenant-1", "customer-beta-visible", "Beta Visible Customer", true));
    var directory = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-customers", "user-admin.show-customers", "manage-customers", "tenant.customer.list", null, null, "membership-admin", "surface-user-admin-tenant-dashboard", "corr-customer-list"));
    assertEquals("surface-user-admin-customer-directory", directory.resultSurface().surfaceId());
    assertTrue(directory.resultSurface().toString().contains(customerId));
    assertTrue(directory.resultSurface().toString().contains("sibling-customers-redacted"));

    var queryFilteredDirectory = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-customers", "user-admin.show-customers", "manage-customers", "tenant.customer.list", Map.of("query", "Acme"), null, "membership-admin", directory.resultSurface().surfaceId(), "corr-customer-list-query"));
    assertEquals("surface-user-admin-customer-directory", queryFilteredDirectory.resultSurface().surfaceId());
    assertEquals("Acme", queryFilteredDirectory.resultSurface().data().get("query"));
    assertTrue(queryFilteredDirectory.resultSurface().toString().contains(customerId));
    assertFalse(queryFilteredDirectory.resultSurface().toString().contains("customer-beta-visible"));

    var emptyFilteredDirectory = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-customers", "user-admin.show-customers", "manage-customers", "tenant.customer.list", Map.of("query", "no-match", "status", "active"), null, "membership-admin", directory.resultSurface().surfaceId(), "corr-customer-list-empty-filter"));
    assertEquals("surface-user-admin-customer-directory", emptyFilteredDirectory.resultSurface().surfaceId());
    assertTrue(((List<?>) emptyFilteredDirectory.resultSurface().data().get("customers")).isEmpty());
    assertTrue(emptyFilteredDirectory.resultSurface().toString().contains("No visible Customers match"));
    assertFalse(emptyFilteredDirectory.resultSurface().toString().contains("hiddenCount"));

    var detail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-customer-read", "user-admin.read-customer", "manage-customers", "tenant.customer.read", Map.of("customerId", customerId), null, "membership-admin", directory.resultSurface().surfaceId(), "corr-customer-read"));
    assertEquals("surface-user-admin-customer-detail", detail.resultSurface().surfaceId());
    assertTrue(detail.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-customers"));
    assertTrue(detail.resultSurface().toString().contains("action-user-admin-show-customer-admins"));
    assertTrue(detail.resultSurface().toString().contains("action-open-customer-admin-invitation-create"));

    var customerAdmins = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-customer-admins", "user-admin.show-customer-admins", "manage-customer-admins", "tenant.customer_admin.list", Map.of("recordId", customerId), null, "membership-admin", detail.resultSurface().surfaceId(), "corr-customer-admins"));
    assertEquals("surface-user-admin-customer-admins", customerAdmins.resultSurface().surfaceId());
    assertEquals(customerId, customerAdmins.resultSurface().data().get("customerId"));
    assertTrue(customerAdmins.resultSurface().toString().contains("targetScopeProof"));
    assertTrue(customerAdmins.resultSurface().toString().contains("customerId=" + customerId));
    assertEquals(0, ((List<?>) customerAdmins.resultSurface().data().get("rows")).size());
    assertEquals(0, ((Map<?, ?>) customerAdmins.resultSurface().data().get("pageInfo")).get("visibleCount"));

    var customerAdminInviteForm = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-customer-admin-invitation-create", "user-admin.open-customer-admin-invite", "manage-customer-admins", "tenant.customer_admin.invite", Map.of("customerId", customerId), null, "membership-admin", detail.resultSurface().surfaceId(), "corr-customer-admin-invite-form"));
    assertEquals("surface-user-admin-customer-admin-invitation-create", customerAdminInviteForm.resultSurface().surfaceId());
    assertEquals(customerId, customerAdminInviteForm.resultSurface().data().get("customerId"));
    assertTrue(customerAdminInviteForm.resultSurface().toString().contains("action-customer-admin-invite"));
    assertFalse(customerAdminInviteForm.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-invite-user")), "Customer Admin invite form must not submit through generic tenant invite action.");

    var customerAdminInvite = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-customer-admin-invite", "user-admin.invite-customer-admin", "manage-customer-admins", "tenant.customer_admin.invite", Map.of("customerId", customerId, "email", "customer.admin@example.test", "displayName", "Customer Admin", "roles", "CUSTOMER_ADMIN"), "idem-customer-admin-invite", "membership-admin", customerAdminInviteForm.resultSurface().surfaceId(), "corr-customer-admin-invite"));
    assertEquals("accepted", customerAdminInvite.status());
    assertEquals("surface-user-admin-invitation-detail", customerAdminInvite.resultSurface().surfaceId());
    var customerScopedInvitation = invitationRepository.invitations().stream()
        .filter(invitation -> "customer.admin@example.test".equals(invitation.normalizedEmail()))
        .findFirst()
        .orElseThrow();
    assertEquals(ScopeType.CUSTOMER, customerScopedInvitation.scopeType());
    assertEquals("tenant-1", customerScopedInvitation.tenantId());
    assertEquals(customerId, customerScopedInvitation.customerId());
    assertEquals(List.of(FoundationRole.CUSTOMER_ADMIN), customerScopedInvitation.requestedRoles());

    var unsupportedCustomerAdminRole = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-customer-admin-invite", "user-admin.invite-customer-admin", "manage-customer-admins", "tenant.customer_admin.invite", Map.of("customerId", customerId, "email", "customer.user@example.test", "displayName", "Customer User", "roles", "CUSTOMER_USER"), "idem-customer-admin-invite-invalid-role", "membership-admin", customerAdminInviteForm.resultSurface().surfaceId(), "corr-customer-admin-invite-invalid-role"));
    assertEquals("validation-error", unsupportedCustomerAdminRole.status());
    assertEquals("surface-user-admin-customer-admin-invitation-create", unsupportedCustomerAdminRole.resultSurface().surfaceId());
    assertFalse(invitationRepository.invitations().stream().anyMatch(invitation -> "customer.user@example.test".equals(invitation.normalizedEmail())));

    identityRepository.saveAccount(new Account("existing.customer.admin@example.test", null, "existing.customer.admin@example.test", "existing.customer.admin@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("existing.customer.admin@example.test", "existing.customer.admin@example.test", "Existing Customer Admin", "Existing", "Customer Admin", null));
    identityRepository.putSettings(new UserSettings("existing.customer.admin@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership("membership-existing-customer-admin", "existing.customer.admin@example.test", ScopeType.CUSTOMER, "tenant-1", customerId, List.of(FoundationRole.CUSTOMER_ADMIN), MembershipStatus.ACTIVE, false, null));
    identityRepository.putMembership(new Membership("membership-sibling-customer-admin", "sibling.customer.admin@example.test", ScopeType.CUSTOMER, "tenant-1", "customer-sibling-hidden", List.of(FoundationRole.CUSTOMER_ADMIN), MembershipStatus.ACTIVE, false, null));
    var populatedCustomerAdmins = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-customer-admins", "user-admin.show-customer-admins", "manage-customer-admins", "tenant.customer_admin.list", Map.of("customerId", customerId), null, "membership-admin", detail.resultSurface().surfaceId(), "corr-customer-admins-populated"));
    assertEquals("surface-user-admin-customer-admins", populatedCustomerAdmins.resultSurface().surfaceId());
    assertTrue(((Number) ((Map<?, ?>) populatedCustomerAdmins.resultSurface().data().get("pageInfo")).get("visibleCount")).intValue() >= 2);
    assertTrue(populatedCustomerAdmins.resultSurface().toString().contains("existing.customer.admin@example.test"));
    assertTrue(populatedCustomerAdmins.resultSurface().toString().contains("customer.admin@example.test"));
    assertTrue(populatedCustomerAdmins.resultSurface().toString().contains("surface-user-admin-customer-admin-detail"));
    assertTrue(populatedCustomerAdmins.resultSurface().toString().contains("action-open-customer-admin-detail"));
    assertTrue(populatedCustomerAdmins.resultSurface().toString().contains("action-open-customer-admin-invitation-detail"));
    assertTrue(populatedCustomerAdmins.resultSurface().toString().contains("sibling-customers-redacted"));
    assertFalse(populatedCustomerAdmins.resultSurface().toString().contains("sibling.customer.admin@example.test"));

    var customerAdminMembershipDetail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-customer-admin-detail", "user-admin.open-customer-admin-detail", "manage-customer-admins", "tenant.customer_admin.list", Map.of("customerId", customerId, "membershipId", "membership-existing-customer-admin"), null, "membership-admin", populatedCustomerAdmins.resultSurface().surfaceId(), "corr-customer-admin-membership-detail"));
    assertEquals("accepted", customerAdminMembershipDetail.status());
    assertEquals("surface-user-admin-customer-admin-detail", customerAdminMembershipDetail.resultSurface().surfaceId());
    assertEquals("user_admin.customer_admin_detail.v1", customerAdminMembershipDetail.resultSurface().data().get("surfaceContract"));
    assertEquals("customer-admin-membership", customerAdminMembershipDetail.resultSurface().data().get("recordKind"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("tenant.customer_admin.list"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("action-open-user-admin-role-change-preview"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("action-open-user-admin-membership-status-confirmation"));
    assertTrue(customerAdminMembershipDetail.resultSurface().toString().contains("sibling-customers-redacted"));

    var customerAdminInvitationDetail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-customer-admin-invitation-detail", "user-admin.open-customer-admin-invitation-detail", "manage-customer-admins", "tenant.customer_admin.list", Map.of("customerId", customerId, "invitationId", customerScopedInvitation.invitationId()), null, "membership-admin", populatedCustomerAdmins.resultSurface().surfaceId(), "corr-customer-admin-invitation-detail"));
    assertEquals("accepted", customerAdminInvitationDetail.status());
    assertEquals("surface-user-admin-customer-admin-detail", customerAdminInvitationDetail.resultSurface().surfaceId());
    assertEquals("customer-admin-invitation", customerAdminInvitationDetail.resultSurface().data().get("recordKind"));
    assertTrue(customerAdminInvitationDetail.resultSurface().toString().contains("action-open-useradmin-invitation-resend-confirmation"));
    assertTrue(customerAdminInvitationDetail.resultSurface().toString().contains("action-open-useradmin-invitation-revoke-confirmation"));
    assertFalse(customerAdminInvitationDetail.resultSurface().toString().contains("rawToken"));

    var genericInvite = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "user_admin.invite_user", "user_admin.invite_user", Map.of("email", "tenant.invitee@example.test", "displayName", "Tenant Invitee", "roles", "TENANT_EMPLOYEE"), "idem-generic-tenant-invite", "membership-admin", "surface-user-admin-invitation-create", "corr-generic-tenant-invite"));
    assertEquals("accepted", genericInvite.status());
    var tenantScopedInvitation = invitationRepository.invitations().stream()
        .filter(invitation -> "tenant.invitee@example.test".equals(invitation.normalizedEmail()))
        .findFirst()
        .orElseThrow();
    assertEquals(ScopeType.TENANT, tenantScopedInvitation.scopeType());
    assertNull(tenantScopedInvitation.customerId());
    assertEquals(List.of(FoundationRole.TENANT_EMPLOYEE), tenantScopedInvitation.requestedRoles());

    var suspend = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-customer-suspend", "user-admin.suspend-customer", "manage-customers", "tenant.customer.suspend", Map.of("customerId", customerId, "reason", "test-suspend", "confirmation", "SUSPEND"), "idem-customer-suspend", "membership-admin", detail.resultSurface().surfaceId(), "corr-customer-suspend"));
    assertEquals("accepted", suspend.status());
    assertTrue(suspend.resultSurface().toString().contains("suspended"));

    var suspendedFilteredDirectory = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-customers", "user-admin.show-customers", "manage-customers", "tenant.customer.list", Map.of("status", "suspended"), null, "membership-admin", directory.resultSurface().surfaceId(), "corr-customer-list-suspended-filter"));
    assertEquals("suspended", suspendedFilteredDirectory.resultSurface().data().get("status"));
    assertTrue(suspendedFilteredDirectory.resultSurface().toString().contains(customerId));
    assertFalse(suspendedFilteredDirectory.resultSurface().toString().contains("customer-beta-visible"));

    var activeFilteredDirectory = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-customers", "user-admin.show-customers", "manage-customers", "tenant.customer.list", Map.of("status", "active"), null, "membership-admin", directory.resultSurface().surfaceId(), "corr-customer-list-active-filter"));
    assertTrue(activeFilteredDirectory.resultSurface().toString().contains("customer-beta-visible"));
    assertFalse(activeFilteredDirectory.resultSurface().toString().contains(customerId));

    var suspendedDetail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-customer-read", "user-admin.read-customer", "manage-customers", "tenant.customer.read", Map.of("customerId", customerId), null, "membership-admin", directory.resultSurface().surfaceId(), "corr-customer-suspended-read"));
    assertEquals("surface-user-admin-customer-detail", suspendedDetail.resultSurface().surfaceId());
    assertTrue(suspendedDetail.resultSurface().toString().contains("reactivate"));

    var directReactivate = service.surface(identity(), "membership-admin", "surface-user-admin-customer-reactivate-confirmation", "corr-customer-reactivate-direct");
    assertEquals("surface-user-admin-customer-reactivate-confirmation", directReactivate.surfaceId());
    assertEquals("user_admin.customer_reactivate_confirmation.v1", directReactivate.data().get("surfaceContract"));
    assertTrue(directReactivate.toString().contains("tenant.customer.reactivate"));
    assertTrue(directReactivate.toString().contains("missing-visible-customer"));
    assertTrue(directReactivate.toString().contains("noFakeSuccess=true"));

    var reactivateOpen = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-customer-reactivate", "user-admin.open-customer-reactivate", "manage-customers", "tenant.customer.reactivate", Map.of("customerId", customerId), null, "membership-admin", suspendedDetail.resultSurface().surfaceId(), "corr-customer-reactivate-open"));
    assertEquals("surface-user-admin-customer-reactivate-confirmation", reactivateOpen.resultSurface().surfaceId());
    assertEquals("user_admin.customer_reactivate_confirmation.v1", reactivateOpen.resultSurface().data().get("surfaceContract"));
    assertEquals("tenant", reactivateOpen.resultSurface().data().get("scopeType"));
    assertEquals(customerId, reactivateOpen.resultSurface().data().get("customerId"));
    assertTrue(reactivateOpen.resultSurface().toString().contains("reactivationEligibility"));
    assertTrue(reactivateOpen.resultSurface().toString().contains("action-customer-reactivate"));
    assertTrue(reactivateOpen.resultSurface().toString().contains("action-customer-read"));
    assertBrowserPayloadSafe(reactivateOpen.resultSurface());

    var suspendedAdmins = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-customer-admins", "user-admin.show-customer-admins", "manage-customer-admins", "tenant.customer_admin.list", Map.of("customerId", customerId), null, "membership-admin", suspendedDetail.resultSurface().surfaceId(), "corr-customer-admins-suspended"));
    assertEquals("denied", suspendedAdmins.status());
    assertEquals("surface-user-admin-system-message", suspendedAdmins.resultSurface().surfaceId());
    assertEquals("customer-suspended", suspendedAdmins.resultSurface().data().get("reasonCode"));
    assertEquals(true, suspendedAdmins.resultSurface().data().get("noFakeSuccess"));

    var suspendedInviteForm = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-customer-admin-invitation-create", "user-admin.open-customer-admin-invite", "manage-customer-admins", "tenant.customer_admin.invite", Map.of("customerId", customerId), null, "membership-admin", suspendedDetail.resultSurface().surfaceId(), "corr-customer-admin-invite-suspended"));
    assertEquals("denied", suspendedInviteForm.status());
    assertEquals("customer-suspended", suspendedInviteForm.resultSurface().data().get("reasonCode"));

    var suspendedInvite = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-customer-admin-invite", "user-admin.invite-customer-admin", "manage-customer-admins", "tenant.customer_admin.invite", Map.of("customerId", customerId, "email", "blocked.customer.admin@example.test", "displayName", "Blocked Customer Admin", "roles", "CUSTOMER_ADMIN"), "idem-customer-admin-invite-suspended", "membership-admin", suspendedDetail.resultSurface().surfaceId(), "corr-customer-admin-invite-suspended-submit"));
    assertEquals("denied", suspendedInvite.status());
    assertEquals("customer-suspended", suspendedInvite.resultSurface().data().get("reasonCode"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("CUSTOMER_ADMIN_INVITE") && event.reasonCode().equals("customer-suspended") && event.correlationId().equals("corr-customer-admin-invite-suspended-submit")));

    var reactivate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-customer-reactivate", "user-admin.reactivate-customer", "manage-customers", "tenant.customer.reactivate", Map.of("customerId", customerId, "reason", "test-reactivate"), "idem-customer-reactivate", "membership-admin", suspendedDetail.resultSurface().surfaceId(), "corr-customer-reactivate"));
    assertEquals("accepted", reactivate.status());
    assertTrue(reactivate.resultSurface().toString().contains("active"));

    var reactivatedAdmins = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-customer-admins", "user-admin.show-customer-admins", "manage-customer-admins", "tenant.customer_admin.list", Map.of("customerId", customerId), null, "membership-admin", reactivate.resultSurface().surfaceId(), "corr-customer-admins-reactivated"));
    assertEquals("accepted", reactivatedAdmins.status());
    assertEquals("surface-user-admin-customer-admins", reactivatedAdmins.resultSurface().surfaceId());

    assertThrows(AuthorizationException.class, () -> service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-customers", "user-admin.show-customers", "manage-customers", "tenant.customer.list", null, null, "membership-owner", "surface-user-admin-saas-owner-dashboard", "corr-owner-customer-denied")));
  }

  @Test
  void userAdminUserBranchDescendantsExposeBackendReturnAction() {
    var detail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-user-detail", "action-display-user-detail", "user_admin.list_members", "user_admin.list_members", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", "surface-user-admin-users", "corr-member-branch-detail"));

    assertEquals("surface-user-admin-user-detail", detail.resultSurface().surfaceId());
    assertTrue(detail.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-users"));
    assertTrue(detail.resultSurface().toString().contains("Show users"));

    var users = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-users", "user-admin.show-users", "search-user-directory", "user_admin.list_members", null, null, "membership-admin", detail.resultSurface().surfaceId(), "corr-member-branch-return"));
    assertEquals("accepted", users.status());
    assertEquals("surface-user-admin-users", users.resultSurface().surfaceId());
  }

  @Test
  void userAdminDedicatedUserBranchTaskSurfacesOpenWithBackendReturnMetadata() {
    var create = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-useradmin-invitation-create", "action-open-useradmin-invitation-create", "user_admin.invite_user", "user_admin.invite_user", null, null, "membership-admin", "surface-user-admin-users", "corr-open-invite-create"));
    assertUserBranchTaskSurface(create, "surface-user-admin-invitation-create", "user_admin.invitation_create.v1", "corr-open-invite-create");

    var created = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "user_admin.invite_user", "user_admin.invite_user", Map.of("email", "branch.invitee@example.test", "displayName", "Branch Invitee"), "idem-branch-invite", "membership-admin", create.resultSurface().surfaceId(), "corr-branch-invite"));
    assertEquals("surface-user-admin-invitation-detail", created.resultSurface().surfaceId());
    assertTrue(created.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-users"));
    var invitationId = created.resultSurface().data().get("recordId").toString();

    var descendants = List.of(
        service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
            "action-open-useradmin-invitation-resend-confirmation", "action-open-useradmin-invitation-resend-confirmation", "user_admin.resend_invitation", "user_admin.resend_invitation", Map.of("invitationId", invitationId), null, "membership-admin", created.resultSurface().surfaceId(), "corr-open-resend-confirmation")),
        service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
            "action-open-useradmin-invitation-revoke-confirmation", "action-open-useradmin-invitation-revoke-confirmation", "user_admin.revoke_invitation", "user_admin.revoke_invitation", Map.of("invitationId", invitationId), null, "membership-admin", created.resultSurface().surfaceId(), "corr-open-revoke-confirmation")),
        service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
            "action-open-useradmin-membership-status-confirmation", "action-open-useradmin-membership-status-confirmation", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("accountId", "member@example.test", "membershipId", "membership-member", "status", "removed"), null, "membership-admin", "surface-user-admin-user-detail", "corr-open-membership-confirmation")),
        service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
            "action-open-useradmin-support-access-grant", "action-open-useradmin-support-access-grant", "user_admin.support_access.grant_revoke_extend", "user_admin.support_access.grant_revoke_extend", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", "surface-user-admin-user-detail", "corr-open-support-grant")),
        service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
            "action-open-useradmin-support-access-revoke-confirmation", "action-open-useradmin-support-access-revoke-confirmation", "user_admin.support_access.grant_revoke_extend", "user_admin.support_access.grant_revoke_extend", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", "surface-user-admin-user-detail", "corr-open-support-revoke")),
        service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
            "action-open-useradmin-identity-exception-review", "action-open-useradmin-identity-exception-review", "user_admin.identity_relink.review", "user_admin.identity_relink.review", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", "surface-user-admin-user-detail", "corr-open-identity-exception"))
    );

    var expected = Map.of(
        "surface-user-admin-invitation-resend-confirmation", "user_admin.invitation_resend_confirmation.v1",
        "surface-user-admin-invitation-revoke-confirmation", "user_admin.invitation_revoke_confirmation.v1",
        "surface-user-admin-membership-status-confirmation", "user_admin.membership_status_confirmation.v1",
        "surface-user-admin-support-access-grant", "user_admin.support_access_grant.v1",
        "surface-user-admin-support-access-revoke-confirmation", "user_admin.support_access_revoke_confirmation.v1",
        "surface-user-admin-identity-exception-review", "user_admin.identity_exception_review.v1");
    for (var descendant : descendants) {
      assertUserBranchTaskSurface(descendant, descendant.resultSurface().surfaceId(), expected.get(descendant.resultSurface().surfaceId()), descendant.correlationId());
      var returnedUsers = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
          "action-user-admin-show-users", "user-admin.show-users", "search-user-directory", "user_admin.list_members", null, null, "membership-admin", descendant.resultSurface().surfaceId(), descendant.correlationId() + "-return"));
      assertEquals("accepted", returnedUsers.status());
      assertEquals("surface-user-admin-users", returnedUsers.resultSurface().surfaceId());
      assertEquals(descendant.correlationId() + "-return", returnedUsers.resultSurface().correlationId());
      assertBrowserPayloadSafe(returnedUsers.resultSurface());
    }
    assertTrue(descendants.get(0).resultSurface().toString().contains("invitation-token-redacted"));
    assertTrue(descendants.get(2).resultSurface().toString().contains("last-admin-denied"));
    assertEquals(true, descendants.get(5).resultSurface().data().get("noDirectMutation"));
  }

  @Test
  void userAdminSupportAccessGrantCanonicalFormAndSubmitUseGovernedRuntimePath() {
    var opened = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-user-admin-support-access-grant", "action-open-user-admin-support-access-grant", "user_admin.support_access.grant_revoke_extend", "user_admin.support_access.grant_revoke_extend", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", "surface-user-admin-user-detail", "corr-support-canonical-open"));
    assertEquals("accepted", opened.status());
    assertEquals("surface-user-admin-support-access-grant", opened.resultSurface().surfaceId());
    assertEquals("user_admin.support_access_grant.v1", opened.resultSurface().data().get("surfaceContract"));
    assertTrue(opened.resultSurface().toString().contains("targetSummary"));
    assertTrue(opened.resultSurface().toString().contains("grantRequestForm"));
    assertTrue(opened.resultSurface().toString().contains("action-submit-user-admin-support-access-grant"));
    assertTrue(opened.resultSurface().toString().contains("action-validate-user-admin-support-access-grant"));
    assertTrue(opened.resultSurface().toString().contains("noRoleMutation=true"));
    assertBrowserPayloadSafe(opened.resultSurface());

    var validation = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-validate-user-admin-support-access-grant", "action-validate-user-admin-support-access-grant", "user_admin.support_access.grant_revoke_extend", "user_admin.support_access.grant_revoke_extend", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", opened.resultSurface().surfaceId(), "corr-support-canonical-validation"));
    assertEquals("validation-error", validation.status());
    assertEquals("surface-user-admin-support-access-grant", validation.resultSurface().surfaceId());
    assertTrue(validation.resultSurface().toString().contains("Purpose is required"));

    var submitted = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-submit-user-admin-support-access-grant", "action-submit-user-admin-support-access-grant", "user_admin.support_access.grant_revoke_extend", "user_admin.support_access.grant_revoke_extend", Map.of("accountId", "member@example.test", "membershipId", "membership-member", "purpose", "customer-requested-support", "expiryHours", "2"), "idem-support-canonical-grant", "membership-admin", opened.resultSurface().surfaceId(), "corr-support-canonical-submit"));
    assertEquals("accepted", submitted.status());
    assertEquals("surface-user-admin-user-detail", submitted.resultSurface().surfaceId());
    assertTrue(submitted.resultSurface().toString().contains("supportAccess=true"));
    assertTrue(submitted.resultSurface().toString().contains("lastResult"));
    assertTrue(submitted.resultSurface().toString().contains("Support access granted or extended"));
    assertBrowserPayloadSafe(submitted.resultSurface());

    var revokeOpened = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-user-admin-support-access-revoke-confirmation", "action-open-user-admin-support-access-revoke-confirmation", "user_admin.support_access.grant_revoke_extend", "user_admin.support_access.grant_revoke_extend", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", submitted.resultSurface().surfaceId(), "corr-support-revoke-canonical-open"));
    assertEquals("accepted", revokeOpened.status());
    assertEquals("surface-user-admin-support-access-revoke-confirmation", revokeOpened.resultSurface().surfaceId());
    assertEquals("user_admin.support_access_revoke_confirmation.v1", revokeOpened.resultSurface().data().get("surfaceContract"));
    assertTrue(revokeOpened.resultSurface().toString().contains("targetSummary"));
    assertTrue(revokeOpened.resultSurface().toString().contains("activeSupportGrant"));
    assertTrue(revokeOpened.resultSurface().toString().contains("action-confirm-user-admin-support-access-revoke"));
    assertTrue(revokeOpened.resultSurface().toString().contains("noMembershipLifecycleMutation=true"));
    assertBrowserPayloadSafe(revokeOpened.resultSurface());

    var revoked = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-confirm-user-admin-support-access-revoke", "action-confirm-user-admin-support-access-revoke", "user_admin.support_access.grant_revoke_extend", "user_admin.support_access.grant_revoke_extend", Map.of("accountId", "member@example.test", "membershipId", "membership-member", "reason", "case resolved"), "idem-support-canonical-revoke", "membership-admin", revokeOpened.resultSurface().surfaceId(), "corr-support-revoke-canonical-confirm"));
    assertEquals("accepted", revoked.status());
    assertEquals("surface-user-admin-user-detail", revoked.resultSurface().surfaceId());
    assertTrue(revoked.resultSurface().toString().contains("Support access revoked"));
    assertTrue(revoked.resultSurface().toString().contains("supportAccess=false"));
    assertBrowserPayloadSafe(revoked.resultSurface());
  }

  @Test
  void userAdminMembershipStatusCanonicalConfirmMutatesThroughGovernedService() {
    var opened = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-user-admin-membership-status-confirmation", "action-open-user-admin-membership-status-confirmation", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("accountId", "member@example.test", "membershipId", "membership-member", "status", "suspended"), null, "membership-admin", "surface-user-admin-user-detail", "corr-status-canonical-open"));
    assertEquals("accepted", opened.status());
    assertEquals("surface-user-admin-membership-status-confirmation", opened.resultSurface().surfaceId());
    assertEquals("user_admin.membership_status_confirmation.v1", opened.resultSurface().data().get("surfaceContract"));
    assertTrue(opened.resultSurface().toString().contains("targetSummary"));
    assertTrue(opened.resultSurface().toString().contains("proposedLifecycleChange"));
    assertTrue(opened.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-confirm-user-admin-membership-status-change")));
    assertBrowserPayloadSafe(opened.resultSurface());

    var confirmed = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-confirm-user-admin-membership-status-change", "action-confirm-user-admin-membership-status-change", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("accountId", "member@example.test", "membershipId", "membership-member", "status", "suspended", "reason", "temporary access pause"), "idem-status-canonical", "membership-admin", opened.resultSurface().surfaceId(), "corr-status-canonical-confirm"));
    assertEquals("accepted", confirmed.status());
    assertEquals("surface-user-admin-user-detail", confirmed.resultSurface().surfaceId());
    assertTrue(confirmed.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-useradmin-update-member-status")));
    assertTrue(confirmed.resultSurface().toString().contains("membershipStatus"));
    assertTrue(confirmed.resultSurface().toString().contains("suspended"));
    assertBrowserPayloadSafe(confirmed.resultSurface());

    var replay = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-confirm-user-admin-membership-status-change", "action-confirm-user-admin-membership-status-change", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("accountId", "member@example.test", "membershipId", "membership-member", "status", "suspended", "reason", "duplicate pause"), "idem-status-canonical-replay", "membership-admin", opened.resultSurface().surfaceId(), "corr-status-canonical-replay"));
    assertEquals("no-op", replay.status());
    assertTrue(replay.message().contains("already matches"));
    assertBrowserPayloadSafe(replay.resultSurface());
  }

  @Test
  void userAdminIdentityRecoverySurfaceShowsDurableLifecycleAndSafeActions() {
    var opened = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-useradmin-identity-exception-review", "action-open-useradmin-identity-exception-review", "user_admin.identity_relink.review", "user_admin.identity_relink.review", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", "surface-user-admin-user-detail", "corr-identity-open"));
    assertEquals("accepted", opened.status());
    assertEquals("surface-user-admin-identity-exception-review", opened.resultSurface().surfaceId());
    assertEquals("request-required", opened.resultSurface().data().get("status"));
    assertTrue(opened.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-request-identity-relink")));
    assertBrowserPayloadSafe(opened.resultSurface());

    var requested = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-request-identity-relink", "action-useradmin-request-identity-relink", "user_admin.identity_relink.request", "user_admin.identity_relink.request", Map.of("accountId", "member@example.test", "reason", "browser smoke provider mismatch"), "idem-identity-request", "membership-admin", opened.resultSurface().surfaceId(), "corr-identity-request"));
    assertEquals("approval-required", requested.status());
    assertEquals("approval-required", requested.resultSurface().data().get("status"));
    assertEquals("needs-review", requested.resultSurface().data().get("lifecycleStatus"));
    assertTrue(requested.resultSurface().toString().contains("provider-boundary:redacted"));
    assertBrowserPayloadSafe(requested.resultSurface());

    var approved = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-approve-identity-relink", "action-useradmin-approve-identity-relink", "user_admin.identity_relink.approve", "user_admin.identity_relink.approve", Map.of("accountId", "member@example.test", "reason", "reviewed evidence", "approvalRef", "approval-identity-1"), "idem-identity-approve", "membership-admin", requested.resultSurface().surfaceId(), "corr-identity-approve"));
    assertEquals("approved-for-recovery", approved.status());
    assertEquals("approved-for-recovery", approved.resultSurface().data().get("lifecycleStatus"));
    assertBrowserPayloadSafe(approved.resultSurface());

    var completed = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-complete-identity-relink", "action-useradmin-complete-identity-relink", "user_admin.identity_relink.complete", "user_admin.identity_relink.complete", Map.of("accountId", "member@example.test", "approvalRef", "approval-identity-1"), "idem-identity-complete", "membership-admin", approved.resultSurface().surfaceId(), "corr-identity-complete"));
    assertEquals("accepted", completed.status());
    assertEquals("completed", completed.resultSurface().data().get("lifecycleStatus"));
    assertFalse(completed.resultSurface().toString().contains("workos-admin"));
    assertFalse(completed.resultSurface().toString().contains("Bearer "));
    assertBrowserPayloadSafe(completed.resultSurface());
  }

  @Test
  void userAdminDedicatedUserBranchTaskSurfacesDenyHiddenTargetsSafely() {
    var hidden = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-useradmin-membership-status-confirmation", "action-open-useradmin-membership-status-confirmation", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("accountId", "hidden@example.test", "membershipId", "membership-hidden"), null, "membership-admin", "surface-user-admin-user-detail", "corr-hidden-user-task"));

    assertEquals("denied", hidden.status());
    assertEquals("system_message", hidden.resultSurface().surfaceType());
    assertEquals("corr-hidden-user-task", hidden.resultSurface().correlationId());
    assertFalse(hidden.resultSurface().toString().contains("hidden@example.test"));
    assertFalse(hidden.resultSurface().toString().contains("membership-hidden"));
    assertBrowserPayloadSafe(hidden.resultSurface());

    var forbidden = assertThrows(AuthorizationException.class, () -> service.runAction(memberIdentity(), "membership-member", new WorkstreamService.CapabilityActionRequest(
        "action-open-useradmin-invitation-create", "action-open-useradmin-invitation-create", "user_admin.invite_user", "user_admin.invite_user", Map.of("email", "leak@example.test"), null, "membership-member", "surface-user-admin-users", "corr-forbidden-invite-create")));
    assertEquals("CAPABILITY_FORBIDDEN", forbidden.reasonCode());
    assertFalse(identityRepository.auditEvents().stream().anyMatch(event -> event.targetAccountId() != null && event.targetAccountId().contains("leak@example.test")));
  }

  private void assertUserBranchTaskSurface(WorkstreamService.CapabilityActionResult result, String surfaceId, String contract, String correlationId) {
    assertEquals("accepted", result.status());
    assertEquals(surfaceId, result.resultSurface().surfaceId());
    assertEquals(contract, result.resultSurface().data().get("surfaceContract"));
    assertEquals(correlationId, result.resultSurface().correlationId());
    assertEquals("surface-user-admin-users", ((Map<?, ?>) result.resultSurface().data().get("branchNavigation")).get("branchRootSurfaceId"));
    assertEquals("action-user-admin-show-users", ((Map<?, ?>) result.resultSurface().data().get("branchNavigation")).get("branchReturnActionId"));
    assertTrue(result.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-users"));
    assertTrue(result.resultSurface().toString().contains("traceRefs"));
    assertTrue(result.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-user-admin-show-users")));
    assertBrowserPayloadSafe(result.resultSurface());
  }

  @Test
  void userAdminUserRowOpensSelectedUserDetail() {
    var detail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-user-detail", "action-display-user-detail", "user_admin.list_members", "user_admin.list_members", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", "surface-user-admin-users", "corr-member-detail"));

    assertEquals("accepted", detail.status());
    assertEquals("surface-user-admin-user-detail", detail.resultSurface().surfaceId());
    assertEquals("show-inspection", detail.resultSurface().surfaceType());
    assertTrue(detail.resultSurface().toString().contains("recordLabel=Member User"));
    assertTrue(detail.resultSurface().toString().contains("membershipId=membership-member"));
    assertTrue(detail.resultSurface().toString().contains("canMutateInline=false"));
    assertTrue(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-membership-status-confirmation")));
    assertFalse(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-change-member-roles")));
    assertFalse(detail.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-disable-member")));
    assertFalse(detail.resultSurface().toString().contains("recordLabel=Tenant Admin"));
  }

  @Test
  void userAdminDashboardDoesNotCountAcceptedInvitationsAsPendingAttention() {
    var created = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "user_admin.invite_user", "user_admin.invite_user", Map.of("email", "accepted.invitee@example.test", "displayName", "Accepted Invitee"), "idem-accepted-invite", "membership-admin", "surface-user-admin-dashboard", "corr-accepted-invite"));
    assertEquals("accepted", created.status());

    var invite = invitationRepository.invitations().stream()
        .filter(invitation -> invitation.normalizedEmail().equals("accepted.invitee@example.test"))
        .findFirst()
        .orElseThrow();
    invitationService.accept(new WorkosIdentity("workos-accepted-invitee", "accepted.invitee@example.test", "Accepted Invitee"), invite.acceptanceContextId(), "corr-accept-invite");

    var dashboard = service.surface(identity(), "membership-admin", "surface-user-admin-dashboard", "corr-dashboard-after-accept");
    @SuppressWarnings("unchecked")
    var cards = (List<Map<String, Object>>) dashboard.data().get("cards");
    var pendingCard = cards.stream()
        .filter(card -> card.get("cardId").equals("card-pending-invitations"))
        .findFirst()
        .orElseThrow();

    assertEquals(0L, pendingCard.get("value"));
    assertEquals("info", pendingCard.get("severity"));

    var users = service.surface(identity(), "membership-admin", "surface-user-admin-users", "corr-users-after-accept");
    assertTrue(users.toString().contains("accepted.invitee@example.test"));
    assertTrue(users.toString().contains("status=accepted"));
  }

  @Test
  void userAdminInvitationActionsCreateResendRevokeAndReplayThroughDeterministicServices() {
    var created = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "user_admin.invite_user", "user_admin.invite_user", Map.of("email", "invitee@example.test", "displayName", "Invitee"), "idem-workstream-invite", "membership-admin", "surface-user-admin-dashboard", "corr-workstream-invite"));
    assertEquals("accepted", created.status());
    assertEquals("surface-user-admin-invitation-detail", created.resultSurface().surfaceId());
    assertEquals("show-inspection", created.resultSurface().surfaceType());
    assertTrue(created.traceIds().get(0).contains("trace-useradmin-invitation"));
    assertTrue(created.resultSurface().toString().contains("invitee@example.test"));
    assertTrue(created.resultSurface().toString().contains("canMutateInline=false"));
    assertTrue(created.resultSurface().toString().contains("deliveryState"));
    assertTrue(created.resultSurface().toString().contains("providerReadiness=ready_or_captured"));
    assertFalse(created.resultSurface().toString().contains("providerMessageId"));
    assertTrue(created.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-open-useradmin-invitation-resend-confirmation")));
    assertFalse(created.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-resend-invitation")));
    assertFalse(created.resultSurface().toString().contains("invite-token"));

    var duplicate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "user_admin.invite_user", "user_admin.invite_user", Map.of("email", "changed@example.test"), "idem-workstream-invite", "membership-admin", "surface-user-admin-dashboard", "corr-workstream-invite-replay"));
    assertEquals(created, duplicate);

    var invitationId = identityRepository.auditEvents().stream().filter(event -> event.actionType().equals("INVITATION_CREATE")).findFirst().orElseThrow().targetMembershipId().replace("membership-", "");
    var resent = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-resend-invitation", "action-useradmin-resend-invitation", "user_admin.resend_invitation", "user_admin.resend_invitation", Map.of("invitationId", invitationId, "reason", "delivery repair"), "idem-workstream-resend", "membership-admin", "surface-user-admin-invitation-detail", "corr-workstream-resend"));
    assertEquals("accepted", resent.status());
    assertTrue(resent.resultSurface().toString().contains("delivery"));
    assertTrue(resent.resultSurface().toString().contains("recoverySurfaceId=surface-user-admin-invitation-resend-confirmation"));

    var revoked = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-revoke-invitation", "action-useradmin-revoke-invitation", "user_admin.revoke_invitation", "user_admin.revoke_invitation", Map.of("invitationId", invitationId, "reason", "wrong recipient"), "idem-workstream-revoke", "membership-admin", "surface-user-admin-invitation-detail", "corr-workstream-revoke"));
    assertEquals("accepted", revoked.status());
    assertTrue(revoked.resultSurface().toString().contains("value=revoked"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_RESEND") && event.correlationId().equals("corr-workstream-resend")));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_REVOKE") && event.correlationId().equals("corr-workstream-revoke")));
  }

  @Test
  void userAdminInvitationDetailRendersProviderBlockedDeliveryAsTypedRecoveryState() {
    var created = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "user_admin.invite_user", "user_admin.invite_user", Map.of("email", "blocked.delivery@example.test", "displayName", "Blocked Delivery"), "idem-blocked-delivery", "membership-admin", "surface-user-admin-dashboard", "corr-blocked-delivery-create"));
    assertEquals("accepted", created.status());
    var invitationId = invitationRepository.invitations().stream()
        .filter(invitation -> invitation.normalizedEmail().equals("blocked.delivery@example.test"))
        .findFirst()
        .orElseThrow()
        .invitationId();

    invitationService.recordDeliveryResult(invitationId, "attempt-blocked", false, null, "resend-config-missing", "corr-blocked-provider-result");

    var detail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-invitation-detail", "action-display-invitation-detail", "user_admin.acceptance_status.read", "user_admin.acceptance_status.read", Map.of("invitationId", invitationId), null, "membership-admin", "surface-user-admin-users", "corr-blocked-delivery-detail"));

    assertEquals("accepted", detail.status());
    assertEquals("surface-user-admin-invitation-detail", detail.resultSurface().surfaceId());
    assertEquals("blocked_provider_or_runtime", detail.resultSurface().data().get("status"));
    assertEquals(true, detail.resultSurface().data().get("noFakeSuccess"));
    assertTrue(detail.resultSurface().toString().contains("user_admin.system_message.v1"));
    assertTrue(detail.resultSurface().toString().contains("resend-config-missing"));
    assertTrue(detail.resultSurface().toString().contains("recoverySurfaceId=surface-user-admin-invitation-resend-confirmation"));
    assertFalse(detail.resultSurface().toString().contains("invite-token"));
    assertFalse(detail.resultSurface().toString().contains("providerMessageId"));
    assertFalse(detail.resultSurface().toString().contains("RESEND_API_KEY"));
  }

  @Test
  void userAdminInvitationActionsDenyMissingCapabilityBeforeDataLeakage() {
    var denied = assertThrows(AuthorizationException.class, () -> service.runAction(memberIdentity(), "membership-member", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "user_admin.invite_user", "user_admin.invite_user", Map.of("email", "leak@example.test"), "idem-denied-invite", "membership-member", "surface-user-admin-dashboard", "corr-denied-invite")));

    assertEquals("CAPABILITY_FORBIDDEN", denied.reasonCode());
    assertFalse(identityRepository.auditEvents().stream().anyMatch(event -> event.targetAccountId() != null && event.targetAccountId().contains("leak@example.test")));
  }

  @Test
  void boundedRealtimeReplayEventsAreScopedAndResumeWithStaleFallback() {
    var events = service.events(identity(), "membership-admin", null, null, "corr-events");

    assertTrue(events.stream().allMatch(event -> event.tenantId().equals("tenant-1")));

    var resumed = service.events(identity(), "membership-admin", null, "evt-missing", "corr-events");
    assertEquals(1, resumed.size());
    assertEquals("surface.stale", resumed.get(0).eventType());
    assertEquals("surface-user-admin-dashboard", resumed.get(0).surfaceId());
    assertEquals("dashboard", resumed.get(0).surfaceType());
    assertTrue(resumed.get(0).patch().toString().contains("bounded SSE replay v1"));
  }

  @Test
  void eventBackedProjectionRefreshEventsAreBackendDerivedAndCapabilityScoped() {
    var started = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-start-access-review", "action-useradmin-start-access-review", "user_admin.access_review.start", "user_admin.access_review.start", Map.of("scope", "tenant"), "idem-event-refresh", "membership-admin", "surface-user-admin-dashboard", "corr-event-refresh"));
    assertEquals("blocked-runtime", started.status());
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("workflow.access_review.blocked_provider_or_runtime")));

    var events = service.events(identity(), "membership-admin", "user-admin-agent", null, "corr-event-refresh-read");

    assertTrue(events.stream().anyMatch(event -> event.eventType().equals("projection.refresh.available")
        && event.surfaceId().equals("surface-user-admin-access-review-task")
        && event.patch().toString().contains("workstream.event.delivery.refresh")
        && event.patch().toString().contains("Bounded SSE replay v1")
        && event.patch().toString().contains("idempotencyKey")
        && event.patch().toString().contains("sourceRefs")));
    assertTrue(service.functionalAgents(identity(), "membership-admin", "corr-refresh-rail").stream()
        .filter(agent -> agent.functionalAgentId().equals("user-admin-agent"))
        .findFirst()
        .orElseThrow()
        .attention()
        .source()
        .equals(AttentionService.LIST_RAIL_SUMMARIES_TOOL));
    assertTrue(service.surface(identity(), "membership-admin", "surface-user-admin-dashboard", "corr-refresh-dashboard").toString().contains("attention.list_workstream_items"));

    var hidden = service.events(memberIdentity(), "membership-member", "user-admin-agent", null, "corr-event-refresh-hidden");
    assertTrue(hidden.stream().noneMatch(event -> event.eventType().equals("projection.refresh.available")), "Members without User Admin capability must not receive event-backed refresh hints for hidden projections.");
  }

  @Test
  void agentAdminActionsCreateGovernedResultsAndTraces() {
    var promptProposal = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-propose-prompt-diff", "action-propose-prompt-diff", "agent_admin.draft_behavior_change", "agent_admin.draft_behavior_change", null, "idem-prompt", "membership-admin", "surface-agent-prompt-governance", "corr-prompt-ui"));
    assertEquals("accepted", promptProposal.status());
    assertEquals("surface-agent-behavior-proposal", promptProposal.resultSurface().surfaceId());
    assertTrue(promptProposal.message().contains("prompt text cannot grant authority"));

    var testRun = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-test-agent-prompt", "action-test-agent-prompt", "agent_admin.draft_behavior_change", "agent_admin.draft_behavior_change", null, "idem-test", "membership-admin", "surface-agent-test-console", "corr-test-ui"));
    assertEquals("accepted", testRun.status());
    assertEquals("surface-agent-test-console", testRun.resultSurface().surfaceId());
    assertTrue(testRun.resultSurface().toString().contains("assigned-skill-load"));
    assertTrue(testRun.resultSurface().toString().contains("unassigned-skill-denial"));
    assertTrue(testRun.resultSurface().toString().contains("agent.read_skill"));

    var toolBoundary = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-simulate-tool-boundary", "action-simulate-tool-boundary", "agent_admin.simulate_tool_boundary", "agent_admin.simulate_tool_boundary", null, "idem-tool-boundary", "membership-admin", "surface-agent-tool-boundary-diff", "corr-tool-boundary-ui"));
    assertEquals("denied", toolBoundary.status());
    assertTrue(toolBoundary.message().contains("retained human approval"));

    var approval = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-approve-skill-manifest", "action-approve-skill-manifest", "agent_admin.approve_behavior_change", "agent_admin.approve_behavior_change", null, "idem-approval", "membership-admin", "surface-agent-skill-manifest-diff", "corr-approval-ui"));
    assertEquals("approval-required", approval.status());
    assertTrue(approval.message().contains("governed review gate"));
    assertTrue(service.bootstrap(identity(), "membership-admin", "corr-agent-admin-caps").functionalAgents().stream()
        .filter(agent -> agent.functionalAgentId().equals("agent-admin-agent"))
        .findFirst()
        .orElseThrow()
        .requiredCapabilityIds()
        .contains("agent_admin.submit_turn"));
  }

  @Test
  void agentAdminCatalogDetailAndArtifactReadsAreBackendAuthoritativeAndRedacted() {
    var catalog = service.surface(identity(), "membership-admin", "surface-agent-admin-catalog", "corr-agent-catalog");
    var detail = service.surface(identity(), "membership-admin", "surface-agent-admin-detail", "corr-agent-detail");
    var prompt = service.surface(identity(), "membership-admin", "surface-agent-prompt-governance", "corr-agent-prompt");
    var skill = service.surface(identity(), "membership-admin", "surface-agent-skill-version", "corr-agent-skill");
    var manifest = service.surface(identity(), "membership-admin", "surface-agent-skill-manifest-diff", "corr-agent-manifest");
    var boundary = service.surface(identity(), "membership-admin", "surface-agent-tool-boundary-diff", "corr-agent-boundary");
    var model = service.surface(identity(), "membership-admin", "surface-agent-model-refs", "corr-agent-model");
    var seed = service.surface(identity(), "membership-admin", "surface-agent-seed-material", "corr-agent-seed-read");

    assertEquals("agent_admin.catalog.v1", catalog.data().get("surfaceContract"));
    assertEquals("agent-admin-agent", catalog.ownerFunctionalAgentId());
    assertTrue(catalog.toString().contains("agent_admin.list_definitions"));
    assertTrue(catalog.toString().contains("providerReadiness"));
    assertTrue(catalog.toString().contains("seedMaterial"));
    assertEquals("show-inspection", detail.surfaceType());
    assertEquals("agent_admin.detail.v1", detail.data().get("surfaceContract"));
    assertEquals("AgentDefinition", detail.data().get("recordKind"));
    assertTrue(detail.toString().contains("detailSummary"));
    assertTrue(detail.toString().contains("scopeSummary"));
    assertTrue(detail.toString().contains("readinessNarrative"));
    assertTrue(detail.toString().contains("behaviorArtifactCards"));
    assertTrue(detail.toString().contains("taskEntryPoints"));
    assertTrue(detail.toString().contains("safeRedactionSummary"));
    assertTrue(detail.toString().contains("authorizedActions"));
    assertTrue(detail.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-detail-refresh") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-detail")));
    assertTrue(detail.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-detail-open-model-refs") && action.resultSurface().updateSurfaceId().equals("surface-agent-model-refs")));
    assertTrue(detail.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-detail-open-trace") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-trace")));
    assertEquals(true, detail.data().get("noDirectMutation"));
    assertEquals("agent_admin.prompt_governance.v1", prompt.data().get("surfaceContract"));
    assertEquals("governance-diff", prompt.surfaceType());
    assertTrue(prompt.toString().contains("governanceSummary"));
    assertTrue(prompt.toString().contains("scopeSummary"));
    assertTrue(prompt.toString().contains("redactedPromptDiff"));
    assertTrue(prompt.toString().contains("impactSummary"));
    assertTrue(prompt.toString().contains("riskAndEvidence"));
    assertTrue(prompt.toString().contains("reviewState"));
    assertTrue(prompt.toString().contains("safeRedactionSummary"));
    assertTrue(prompt.toString().contains("authorizedActions"));
    assertTrue(prompt.toString().contains("beforeSummary"));
    assertTrue(prompt.toString().contains("afterSummary"));
    assertTrue(prompt.toString().contains("changes"));
    assertTrue(prompt.toString().contains("action-agent-prompt-governance-refresh"));
    assertTrue(prompt.toString().contains("action-agent-prompt-governance-submit-review"));
    assertTrue(prompt.toString().contains("action-agent-prompt-governance-open-risk-review"));
    assertEquals(true, prompt.data().get("noDirectMutation"));
    assertEquals(true, prompt.data().get("noDirectActivation"));
    assertEquals("agent_admin.skill_version.v1", skill.data().get("surfaceContract"));
    assertTrue(skill.toString().contains("readSkill"));
    assertTrue(skill.toString().contains("changes"));
    assertEquals("agent_admin.manifest.v1", manifest.data().get("surfaceContract"));
    assertTrue(manifest.toString().contains("skillManifest"));
    assertTrue(manifest.toString().contains("referenceManifest"));
    assertEquals("agent_admin.tool_boundary.v1", boundary.data().get("surfaceContract"));
    assertTrue(boundary.toString().contains("ToolPermissionBoundary"));
    assertEquals("agent_admin.model_ref.v1", model.data().get("surfaceContract"));
    assertTrue(model.toString().contains("[REDACTED]"));
    assertEquals("agent_admin.seed_material.v1", seed.data().get("surfaceContract"));
    assertTrue(catalog.toString().contains("catalogSummary"));
    assertTrue(catalog.toString().contains("scopeSummary"));
    assertTrue(catalog.toString().contains("action-agent-admin-refresh-catalog"));
    assertTrue(catalog.toString().contains("action-agent-admin-search-catalog"));
    assertTrue(catalog.toString().contains("action-agent-admin-reset-catalog-filters"));
    assertTrue(catalog.toString().contains("action-agent-admin-catalog-open-trace"));
    assertFalse(catalog.actions().stream().anyMatch(action -> action.actionId().equals("action-activate-agent-definition") || action.actionId().equals("action-deactivate-agent-definition") || action.actionId().equals("action-import-agent-seed-defaults")));

    var searchCatalog = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-admin-search-catalog", "action-agent-admin-search-catalog", "agent_admin.list_definitions", "agent_admin.list_definitions", Map.of("query", "User Admin"), null, "membership-admin", "surface-agent-admin-catalog", "corr-agent-catalog-search"));
    assertEquals("accepted", searchCatalog.status());
    assertEquals("surface-agent-admin-catalog", searchCatalog.resultSurface().surfaceId());
    assertTrue(searchCatalog.resultSurface().toString().contains("User Admin Agent"));
    assertFalse(searchCatalog.resultSurface().toString().contains("tenant:tenant-2"));

    var openSelectedDetail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-agent-detail", "action-open-agent-detail", "agent_admin.get_definition", "agent_admin.get_definition", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID), null, "membership-admin", "surface-agent-admin-catalog", "corr-agent-catalog-open-row"));
    assertEquals("accepted", openSelectedDetail.status());
    assertEquals(AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID, openSelectedDetail.resultSurface().data().get("recordId"));
    assertTrue(boundary.toString().contains("TOOL_BOUNDARY_DENIED"));
    assertTrue(seed.toString().contains("agent_admin.seed_material.v1"));
    for (var surface : List.of(catalog, detail, prompt, skill, manifest, boundary, model, seed)) {
      assertTrue(surface.traceIds().stream().anyMatch(trace -> trace.contains("trace-surface-agent")));
      assertFalse(surface.toString().toLowerCase().contains("api_key="));
      assertFalse(surface.toString().contains("sk-secret"));
      assertFalse(surface.toString().contains("rawProviderCredential="));
    }
  }

  @Test
  void agentAdminSeedMaterialRuntimeImplementationIsBackendOwnedAndRedacted() {
    var seed = service.surface(identity(), "membership-admin", "surface-agent-seed-material", "corr-agent-seed-implementation");

    assertEquals("surface-agent-seed-material", seed.surfaceId());
    assertEquals("list-search", seed.surfaceType());
    assertEquals("agent_admin.seed_material.v1", seed.data().get("surfaceContract"));
    assertTrue(seed.toString().contains("seedMaterialSummary"));
    assertTrue(seed.toString().contains("scopeSummary"));
    assertTrue(seed.toString().contains("seedRows"));
    assertTrue(seed.toString().contains("provenanceInspection"));
    assertTrue(seed.toString().contains("importWorkflow"));
    assertTrue(seed.toString().contains("customizationPreservation"));
    assertTrue(seed.toString().contains("safeRedactionSummary"));
    assertTrue(seed.toString().contains("action-agent-seed-material-refresh"));
    assertTrue(seed.toString().contains("action-agent-seed-material-search"));
    assertTrue(seed.toString().contains("action-agent-seed-material-prepare-import"));
    assertTrue(seed.toString().contains("action-agent-seed-material-start-import"));
    assertTrue(seed.toString().contains("action-agent-seed-material-open-trace"));
    assertFalse(seed.toString().contains("sk-secret"));
    assertFalse(seed.toString().contains("rawProviderCredential="));
    assertFalse(seed.toString().toLowerCase().contains("api_key="));

    var search = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-seed-material-search", "action-agent-seed-material-search", "agent_admin.list_seed_material", "agent_admin.list_seed_material", Map.of("query", "Agent Admin"), null, "membership-admin", "surface-agent-seed-material", "corr-agent-seed-search"));
    assertEquals("accepted", search.status());
    assertEquals("surface-agent-seed-material", search.resultSurface().surfaceId());
    assertTrue(search.resultSurface().toString().contains("Agent Admin"));
    assertFalse(search.resultSurface().toString().contains("tenant:tenant-2"));

    var prepare = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-seed-material-prepare-import", "action-agent-seed-material-prepare-import", "agent_admin.list_seed_material", "agent_admin.list_seed_material", Map.of("seedMaterialId", "seed-" + AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "targetAgentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-seed-prepare", "membership-admin", "surface-agent-seed-material", "corr-agent-seed-prepare"));
    assertEquals("accepted", prepare.status());
    assertTrue(prepare.message().contains("without mutating"));
    assertTrue(prepare.resultSurface().toString().contains("prepared"));
    assertEquals(AgentLifecycleStatus.ACTIVE, agentRepository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());

    var missingAcknowledgement = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-seed-material-start-import", "action-agent-seed-material-start-import", "agent_admin.reseed_missing_defaults", "agent_admin.reseed_missing_defaults", Map.of("seedMaterialId", "seed-" + AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-seed-start-missing-ack", "membership-admin", "surface-agent-seed-material", "corr-agent-seed-missing-ack"));
    assertEquals("validation-error", missingAcknowledgement.status());
    assertTrue(missingAcknowledgement.message().contains("acknowledgement=IMPORT"));

    var start = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-seed-material-start-import", "action-agent-seed-material-start-import", "agent_admin.reseed_missing_defaults", "agent_admin.reseed_missing_defaults", Map.of("seedMaterialId", "seed-" + AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "acknowledgement", "IMPORT"), "idem-seed-start", "membership-admin", "surface-agent-seed-material", "corr-agent-seed-start"));
    assertEquals("no-op", start.status());
    assertEquals("surface-agent-seed-material", start.resultSurface().surfaceId());
    assertTrue(start.message().contains("tenant customizations and active behavior were preserved"));
    assertEquals(AgentLifecycleStatus.ACTIVE, agentRepository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());
  }

  @Test
  void agentAdminDefinitionLifecycleAndSeedImportActionsAreGovernedAndIdempotent() {
    var deactivate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-deactivate-agent-definition", "action-deactivate-agent-definition", "agent.definitions.manage", "agent.definitions.manage", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-deactivate", "membership-admin", "surface-agent-admin-detail", "corr-agent-deactivate"));

    assertEquals("approval-required", deactivate.status());
    assertEquals("surface-agent-deactivation-confirmation", deactivate.resultSurface().surfaceId());
    assertEquals(AgentLifecycleStatus.ACTIVE, agentRepository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());

    var duplicateDeactivate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-deactivate-agent-definition", "action-deactivate-agent-definition", "agent.definitions.manage", "agent.definitions.manage", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-deactivate-2", "membership-admin", "surface-agent-admin-detail", "corr-agent-deactivate-again"));
    assertEquals("approval-required", duplicateDeactivate.status());

    var activate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-activate-agent-definition", "action-activate-agent-definition", "agent.definitions.manage", "agent.definitions.manage", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-activate", "membership-admin", "surface-agent-admin-detail", "corr-agent-activate"));
    assertEquals("approval-required", activate.status());
    assertEquals("surface-agent-activation-confirmation", activate.resultSurface().surfaceId());
    assertTrue(activate.resultSurface().toString().contains("agent_admin.activation_confirmation.v1"));
    assertTrue(activate.resultSurface().toString().contains("activationSummary"));
    assertTrue(activate.resultSurface().toString().contains("action-agent-activation-confirm"));
    assertTrue(activate.resultSurface().toString().contains("provider-fail-closed"));
    assertTrue(activate.resultSurface().toString().contains("noFakeSuccess=true"));
    assertEquals(AgentLifecycleStatus.ACTIVE, agentRepository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());

    var activationValidation = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-activation-confirm", "action-agent-activation-confirm", "agent_admin.activate_behavior_change", "agent_admin.activate_behavior_change", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-activation-missing-ack", "membership-admin", "surface-agent-activation-confirmation", "corr-agent-activation-missing-ack"));
    assertEquals("validation-error", activationValidation.status());
    assertEquals("surface-agent-activation-confirmation", activationValidation.resultSurface().surfaceId());
    assertEquals(AgentLifecycleStatus.ACTIVE, agentRepository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());

    var activationBlocked = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-activation-confirm", "action-agent-activation-confirm", "agent_admin.activate_behavior_change", "agent_admin.activate_behavior_change", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "acknowledgement", "ACTIVATE"), "idem-activation-confirm", "membership-admin", "surface-agent-activation-confirmation", "corr-agent-activation-confirm"));
    assertEquals("approval-required", activationBlocked.status());
    assertTrue(activationBlocked.message().contains("failed closed"));
    assertEquals("surface-agent-activation-confirmation", activationBlocked.resultSurface().surfaceId());
    assertEquals(AgentLifecycleStatus.ACTIVE, agentRepository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());

    var activationCancel = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-activation-cancel", "action-agent-activation-cancel", "agent.definitions.manage", "agent.definitions.manage", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-activation-cancel", "membership-admin", "surface-agent-activation-confirmation", "corr-agent-activation-cancel"));
    assertEquals("accepted", activationCancel.status());
    assertEquals("surface-agent-admin-detail", activationCancel.resultSurface().surfaceId());

    var seedImport = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-import-agent-seed-defaults", "action-import-agent-seed-defaults", "agent_admin.reseed_missing_defaults", "agent_admin.reseed_missing_defaults", null, "idem-seed-import", "membership-admin", "surface-agent-seed-material", "corr-agent-seed-import"));
    assertEquals("no-op", seedImport.status());
    assertTrue(seedImport.message().contains("skipped governed records"));
    assertEquals("surface-agent-seed-import-confirmation", seedImport.resultSurface().surfaceId());

    var deactivationRefresh = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-deactivation-refresh", "action-agent-deactivation-refresh", "agent.definitions.manage", "agent.definitions.manage", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-deactivation-refresh", "membership-admin", "surface-agent-deactivation-confirmation", "corr-agent-deactivation-refresh"));
    assertEquals("no-op", deactivationRefresh.status());
    assertEquals("surface-agent-deactivation-confirmation", deactivationRefresh.resultSurface().surfaceId());
    assertTrue(deactivationRefresh.resultSurface().toString().contains("deactivationSummary"));
    assertTrue(deactivationRefresh.resultSurface().toString().contains("policyAndApprovalSummary"));
    assertTrue(deactivationRefresh.resultSurface().toString().contains("action-agent-deactivation-confirm"));
    assertTrue(deactivationRefresh.resultSurface().toString().contains("DEACTIVATE"));

    var deactivationValidation = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-deactivation-confirm", "action-agent-deactivation-confirm", "agent.definitions.manage", "agent.definitions.manage", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-deactivation-missing-ack", "membership-admin", "surface-agent-deactivation-confirmation", "corr-agent-deactivation-missing-ack"));
    assertEquals("validation-error", deactivationValidation.status());
    assertEquals("surface-agent-deactivation-confirmation", deactivationValidation.resultSurface().surfaceId());
    assertEquals(AgentLifecycleStatus.ACTIVE, agentRepository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());

    var deactivationMissingReason = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-deactivation-confirm", "action-agent-deactivation-confirm", "agent.definitions.manage", "agent.definitions.manage", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "acknowledgement", "DEACTIVATE"), "idem-deactivation-missing-reason", "membership-admin", "surface-agent-deactivation-confirmation", "corr-agent-deactivation-missing-reason"));
    assertEquals("validation-error", deactivationMissingReason.status());
    assertEquals("surface-agent-deactivation-confirmation", deactivationMissingReason.resultSurface().surfaceId());
    assertEquals(AgentLifecycleStatus.ACTIVE, agentRepository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());

    var deactivationCancel = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-deactivation-cancel", "action-agent-deactivation-cancel", "agent.definitions.manage", "agent.definitions.manage", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-deactivation-cancel", "membership-admin", "surface-agent-deactivation-confirmation", "corr-agent-deactivation-cancel"));
    assertEquals("accepted", deactivationCancel.status());
    assertEquals("surface-agent-admin-detail", deactivationCancel.resultSurface().surfaceId());
    assertEquals(AgentLifecycleStatus.ACTIVE, agentRepository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());

    var deactivationConfirmed = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-agent-deactivation-confirm", "action-agent-deactivation-confirm", "agent.definitions.manage", "agent.definitions.manage", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "acknowledgement", "DEACTIVATE", "reason", "Retire unsafe behavior"), "idem-deactivation-confirm", "membership-admin", "surface-agent-deactivation-confirmation", "corr-agent-deactivation-confirm"));
    assertEquals("accepted", deactivationConfirmed.status());
    assertTrue(deactivationConfirmed.message().contains("no prompt, skill, reference, provider, or tenant override artifacts were deleted"));
    assertEquals("surface-agent-admin-detail", deactivationConfirmed.resultSurface().surfaceId());
    assertEquals(AgentLifecycleStatus.DISABLED, agentRepository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());
  }

  @Test
  void agentAdminReadSurfacesDenyMissingCapabilityBeforeArtifactLeakage() {
    var denied = assertThrows(AuthorizationException.class, () -> service.surface(memberIdentity(), "membership-member", "surface-agent-prompt-governance", "corr-member-agent-prompt"));

    assertEquals("agent-admin-requires-tenant-admin", denied.reasonCode());
  }

  @Test
  void customerAdminCannotAccessAgentAdminWorkstreamOrLegacyBehaviorManagement() {
    identityRepository.saveAccount(new Account("customer-admin@example.test", null, "customer-admin@example.test", "customer-admin@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("customer-admin@example.test", "customer-admin@example.test", "Customer Admin", "Customer", "Admin", null));
    identityRepository.putSettings(new UserSettings("customer-admin@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.saveCustomer(new Customer("tenant-1", "customer-1", "Customer One", true));
    identityRepository.putMembership(new Membership("membership-customer-admin", "customer-admin@example.test", ScopeType.CUSTOMER, "tenant-1", "customer-1", List.of(FoundationRole.CUSTOMER_ADMIN), MembershipStatus.ACTIVE, false, null));
    var customerIdentity = new WorkosIdentity("workos-customer-admin", "customer-admin@example.test", "Customer Admin");

    var bootstrap = service.bootstrap(customerIdentity, "membership-customer-admin", "corr-customer-agent-admin-bootstrap");
    assertTrue(bootstrap.me().visibleCapabilityIds().stream().noneMatch(capability -> capability.startsWith("agent_admin.")));
    assertTrue(bootstrap.me().visibleCapabilityIds().stream().noneMatch(capability -> capability.equals("agent.behavior.manage")));
    assertTrue(bootstrap.functionalAgents().stream().noneMatch(agent -> agent.functionalAgentId().equals("agent-admin-agent") && agent.availability().equals("visible")));

    var deniedSurface = assertThrows(AuthorizationException.class, () -> service.surface(customerIdentity, "membership-customer-admin", "surface-agent-admin-catalog", "corr-customer-agent-admin-catalog"));
    assertEquals("agent-admin-requires-tenant-admin", deniedSurface.reasonCode());

    var deniedAction = assertThrows(AuthorizationException.class, () -> service.runAction(customerIdentity, "membership-customer-admin", new WorkstreamService.CapabilityActionRequest(
        "action-propose-prompt-diff", "action-propose-prompt-diff", "agent_admin.draft_behavior_change", "agent_admin.draft_behavior_change", null, "idem-customer-agent-admin", "membership-customer-admin", "surface-agent-prompt-governance", "corr-customer-agent-admin-action")));
    assertEquals("CAPABILITY_FORBIDDEN", deniedAction.reasonCode());
  }

  @Test
  void agentAdminCatalogIsTenantScoped() {
    new AgentBehaviorSeedLoader(agentRepository, Clock.systemUTC()).importStarterDefaults("tenant-2", "bootstrap", "corr-agent-seed-tenant-2");

    var catalog = service.surface(identity(), "membership-admin", "surface-agent-admin-catalog", "corr-agent-tenant-scope");

    assertTrue(catalog.toString().contains("tenant:tenant-1"));
    assertFalse(catalog.toString().contains("tenant:tenant-2"));
    assertFalse(catalog.toString().contains("corr-agent-seed-tenant-2"));
  }

  @Test
  void disabledSurfaceActionsReturnDenialResultSurface() {
    var result = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-replace-membership-role", "action-replace-membership-role", "user_admin.view_overview", "user_admin.view_overview", null, "idem-1", "membership-admin", "surface-user-admin-user-detail", "corr-role"));

    assertEquals("denied", result.status());
    assertTrue(result.message().contains("last tenant admin"));
    assertEquals("surface-user-admin-user-detail", result.resultSurface().surfaceId());
  }

  @Test
  void userAdminContractCapabilityActionsPreviewApplyAuditAndIdempotency() {
    identityRepository.saveAccount(new Account("second-admin@example.test", null, "second-admin@example.test", "second-admin@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("second-admin@example.test", "second-admin@example.test", "Second Admin", "Second", "Admin", null));
    identityRepository.putSettings(new UserSettings("second-admin@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership("membership-second-admin", "second-admin@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN), MembershipStatus.ACTIVE, false, null));

    var preview = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-preview-role-change", "action-useradmin-preview-role-change", "user_admin.preview_role_change", "user_admin.preview_role_change", Map.of("membershipId", "membership-member", "roles", List.of("TENANT_ADMIN"), "reason", "promotion"), null, "membership-admin", "surface-user-admin-user-detail", "corr-useradmin-preview"));
    assertEquals("accepted", preview.status());
    assertTrue(preview.traceIds().get(0).contains("trace-useradmin-preview-role-change"));
    assertEquals("surface-user-admin-role-change-preview", preview.resultSurface().surfaceId());
    assertEquals("user_admin.role_change_preview.v1", preview.resultSurface().data().get("surfaceContract"));
    assertTrue(preview.resultSurface().toString().contains("capabilityDelta"));
    assertTrue(preview.resultSurface().toString().contains("affectedWorkstreams"));
    assertTrue(preview.resultSurface().toString().contains("targetSummary"));
    assertTrue(preview.resultSurface().toString().contains("policyDecision"));
    assertTrue(preview.resultSurface().toString().contains("confirmationForm"));
    assertTrue(preview.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-commit-user-admin-role-change")));

    var directPreview = service.surface(identity(), "membership-admin", "surface-user-admin-role-change-preview", "corr-useradmin-direct-preview");
    assertEquals("surface-user-admin-role-change-preview", directPreview.surfaceId());
    assertEquals("user_admin.role_change_preview.v1", directPreview.data().get("surfaceContract"));
    assertTrue(directPreview.toString().contains("backend-derived"));

    var aliasPreview = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-user-admin-role-change-preview", "action-open-user-admin-role-change-preview", "user_admin.preview_role_change", "user_admin.preview_role_change", Map.of("membershipId", "membership-member", "roles", List.of("TENANT_ADMIN"), "reason", "promotion"), null, "membership-admin", "surface-user-admin-user-detail", "corr-useradmin-preview-alias"));
    assertEquals("surface-user-admin-role-change-preview", aliasPreview.resultSurface().surfaceId());

    var changed = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-change-member-roles", "action-useradmin-change-member-roles", "user_admin.change_member_roles", "user_admin.change_member_roles", Map.of("membershipId", "membership-member", "roles", List.of("TENANT_ADMIN"), "reason", "promotion"), "idem-useradmin-change", "membership-admin", "surface-user-admin-user-detail", "corr-useradmin-change"));
    assertEquals("accepted", changed.status());
    assertEquals("surface-user-admin-user-detail", changed.resultSurface().surfaceId());

    var duplicate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-change-member-roles", "action-useradmin-change-member-roles", "user_admin.change_member_roles", "user_admin.change_member_roles", Map.of("membershipId", "membership-member", "roles", List.of("TENANT_EMPLOYEE"), "reason", "ignored replay"), "idem-useradmin-change", "membership-admin", "surface-user-admin-user-detail", "corr-useradmin-change-replay"));
    assertEquals(changed, duplicate);
    assertEquals(List.of(FoundationRole.TENANT_ADMIN), identityRepository.findMembership("membership-member").orElseThrow().roles());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("user_admin.change_member_roles") && event.correlationId().equals("corr-useradmin-change")));
  }

  @Test
  void userAdminStatusActionsDisableReactivateNoOpAndDenyManualSelfDisable() {
    var disabled = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("membershipId", "membership-member", "reason", "leave"), "idem-disable-member", "membership-admin", "surface-user-admin-users", "corr-disable-member"));
    assertEquals("accepted", disabled.status());
    assertEquals("surface-user-admin-user-detail", disabled.resultSurface().surfaceId());
    assertTrue(disabled.traceIds().get(0).contains("trace-useradmin-update-member-status"));
    assertTrue(disabled.resultSurface().toString().contains("value=removed"));

    var duplicate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("membershipId", "membership-member", "reason", "ignored replay"), "idem-disable-member", "membership-admin", "surface-user-admin-users", "corr-disable-member-replay"));
    assertEquals(disabled, duplicate);

    var reactivated = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-reactivate-member", "action-useradmin-reactivate-member", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("membershipId", "membership-member", "reason", "return"), "idem-reactivate-member", "membership-admin", "surface-user-admin-users", "corr-reactivate-member"));
    assertEquals("accepted", reactivated.status());
    assertTrue(reactivated.resultSurface().toString().contains("value=active"));

    var disabledFromActiveSelect = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("membershipId", "membership-member", "status", "active", "reason", "deactivate button default"), "idem-disable-member-active-select", "membership-admin", "surface-user-admin-users", "corr-disable-member-active-select"));
    assertEquals("accepted", disabledFromActiveSelect.status());
    assertTrue(disabledFromActiveSelect.resultSurface().toString().contains("value=removed"));
    var reactivatedAgain = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-reactivate-member", "action-useradmin-reactivate-member", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("membershipId", "membership-member", "reason", "return again"), "idem-reactivate-member-again", "membership-admin", "surface-user-admin-users", "corr-reactivate-member-again"));
    assertEquals("accepted", reactivatedAgain.status());

    identityRepository.saveAccount(new Account("purge@example.test", null, "purge@example.test", "purge@example.test", AccountStatus.ACTIVE, "UNLINKED"));
    identityRepository.putProfile(new UserProfile("purge@example.test", "purge@example.test", "Purge User", "Purge", "User", null));
    identityRepository.putSettings(new UserSettings("purge@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership("membership-purge", "purge@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.ACTIVE, false, null));
    var deactivatedForRemoval = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("membershipId", "membership-purge", "status", "removed", "reason", "offboard"), "idem-deactivate-before-purge", "membership-admin", "surface-user-admin-users", "corr-deactivate-before-purge"));
    assertEquals("accepted", deactivatedForRemoval.status());
    var removed = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-permanently-remove-user", "action-useradmin-permanently-remove-user", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("membershipId", "membership-purge", "reason", "purge"), "idem-permanent-remove", "membership-admin", "surface-user-admin-user-detail", "corr-permanent-remove"));
    assertEquals("accepted", removed.status());
    assertEquals("surface-user-admin-users", removed.resultSurface().surfaceId());
    assertTrue(identityRepository.findMembership("membership-purge").isEmpty());
    assertTrue(identityRepository.findAccountByEmail("purge@example.test").isEmpty());

    var selfDisable = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "user_admin.update_member_status", "user_admin.update_member_status", Map.of("membershipId", "membership-admin", "reason", "self-disable"), "idem-self-disable", "membership-admin", "surface-user-admin-users", "corr-self-disable"));
    assertEquals("denied", selfDisable.status());
    assertEquals("surface-user-admin-system-message", selfDisable.resultSurface().surfaceId());
    assertEquals("user_admin.system_message.v1", selfDisable.resultSurface().data().get("surfaceContract"));
    assertEquals("self-disable-denied", selfDisable.resultSurface().data().get("reasonCode"));
    assertEquals(true, selfDisable.resultSurface().data().get("noFakeSuccess"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("user_admin.update_member_status") && event.reasonCode().equals("self-disable-denied")));
  }

  @Test
  void userAdminSupportAccessActionsUseBackendAuthorityAndAuditTraceSurface() {
    var grant = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-grant-support-access", "action-useradmin-grant-support-access", "user_admin.support_access.grant_revoke_extend", "user_admin.support_access.grant_revoke_extend", Map.of("membershipId", "membership-member", "reason", "break glass"), "idem-support-grant", "membership-admin", "surface-user-admin-user-detail", "corr-support-grant"));

    assertEquals("accepted", grant.status());
    assertEquals("surface-user-admin-user-detail", grant.resultSurface().surfaceId());
    assertTrue(grant.resultSurface().toString().contains("supportAccess=true"));
    assertTrue(grant.resultSurface().toString().contains("supportAccess=true"));
    assertTrue(identityRepository.findMembership("membership-member").orElseThrow().supportAccess());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("SUPPORT_ACCESS_GRANT") && event.correlationId().equals("corr-support-grant")));

    var duplicate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-grant-support-access", "action-useradmin-grant-support-access", "user_admin.support_access.grant_revoke_extend", "user_admin.support_access.grant_revoke_extend", Map.of("membershipId", "membership-member", "reason", "ignored replay"), "idem-support-grant", "membership-admin", "surface-user-admin-user-detail", "corr-support-grant-replay"));
    assertEquals(grant, duplicate);

    var revoke = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-revoke-support-access", "action-useradmin-revoke-support-access", "user_admin.support_access.grant_revoke_extend", "user_admin.support_access.grant_revoke_extend", Map.of("membershipId", "membership-member", "reason", "done"), "idem-support-revoke", "membership-admin", "surface-user-admin-user-detail", "corr-support-revoke"));
    assertEquals("accepted", revoke.status());
    assertFalse(identityRepository.findMembership("membership-member").orElseThrow().supportAccess());
    assertEquals("surface-user-admin-user-detail", revoke.resultSurface().surfaceId());
    assertTrue(revoke.resultSurface().toString().contains("supportAccess=false"));
  }

  @Test
  void userAdminAccessReviewTaskLifecycleProducesTypedProviderBlockedSurface() {
    var result = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-start-access-review", "action-useradmin-start-access-review", "user_admin.access_review.start", "user_admin.access_review.start", Map.of("scope", "tenant"), "idem-access-review", "membership-admin", "surface-user-admin-dashboard", "corr-access-review"));

    assertEquals("blocked-runtime", result.status());
    assertTrue(result.message().contains("provider/runtime configuration"));
    assertEquals("surface-user-admin-access-review-task", result.resultSurface().surfaceId());
    assertEquals("user_admin.access_review_task.v1", result.resultSurface().data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", result.resultSurface().data().get("status"));
    assertEquals(true, result.resultSurface().data().get("noDirectMutation"));
    assertTrue(result.resultSurface().toString().contains("user_admin.access_review.read"));
    assertTrue(result.resultSurface().toString().contains("AccessReviewTask") || result.resultSurface().toString().contains("access-review"));

    var taskId = result.resultSurface().data().get("taskId").toString();
    var read = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-read-access-review", "action-useradmin-read-access-review", "user_admin.access_review.read", "user_admin.access_review.read", Map.of("taskId", taskId), null, "membership-admin", "surface-user-admin-access-review-task", "corr-access-review-read"));
    assertEquals("accepted", read.status());
    assertEquals(taskId, read.resultSurface().data().get("taskId"));

    var cancelled = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-cancel-access-review", "action-useradmin-cancel-access-review", "user_admin.access_review.cancel", "user_admin.access_review.cancel", Map.of("taskId", taskId, "reason", "not needed"), "idem-access-review-cancel", "membership-admin", "surface-user-admin-access-review-task", "corr-access-review-cancel"));
    assertEquals("accepted", cancelled.status());
    assertEquals("cancelled", cancelled.resultSurface().data().get("status"));
  }

  @Test
  void userAdminAccessReviewCompletedResultSurfaceShowsSafeModelToolTraceLinksAndHumanReviewActions() {
    var completedRuntimeService = serviceWithAccessReviewRuntime(new CompletedAccessReviewRuntime());
    var started = completedRuntimeService.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-start-access-review", "action-useradmin-start-access-review", "user_admin.access_review.start", "user_admin.access_review.start", Map.of("scope", "tenant"), "idem-access-review-completed", "membership-admin", "surface-user-admin-dashboard", "corr-access-review-completed-start"));
    assertEquals("queued", started.status());
    var taskId = started.resultSurface().data().get("taskId").toString();

    var read = completedRuntimeService.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-read-access-review", "action-useradmin-read-access-review", "user_admin.access_review.read", "user_admin.access_review.read", Map.of("taskId", taskId), null, "membership-admin", "surface-user-admin-access-review-task", "corr-access-review-completed-read"));

    assertEquals("accepted", read.status());
    assertEquals("completed", read.resultSurface().data().get("status"));
    assertEquals("completed_review_required", read.resultSurface().data().get("resultReviewState"));
    assertTrue(read.resultSurface().toString().contains("modelToolDataPolicyUsage"));
    assertTrue(read.resultSurface().toString().contains("Safe model, tool, data, and policy usage summary"));
    assertTrue(read.resultSurface().toString().contains("surface-audit-trace-detail"));
    assertTrue(read.resultSurface().toString().contains("trace-useradmin-access-review-model"));
    assertTrue(read.resultSurface().toString().contains("No stale admin membership found"));
    assertTrue(read.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-accept-access-review-result")));
    assertTrue(read.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-useradmin-reject-access-review-result")));
    assertBrowserPayloadSafe(read.resultSurface());

    var accepted = completedRuntimeService.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-accept-access-review-result", "action-useradmin-accept-access-review-result", "user_admin.access_review.accept_result", "user_admin.access_review.accept_result", Map.of("taskId", taskId, "reason", "reviewed advisory result"), "idem-access-review-accept", "membership-admin", "surface-user-admin-access-review-task", "corr-access-review-accepted"));
    assertEquals("accepted", accepted.status());
    assertEquals("result_accepted", accepted.resultSurface().data().get("resultReviewState"));
    assertTrue(accepted.resultSurface().toString().contains("access state unchanged"));
    assertBrowserPayloadSafe(accepted.resultSurface());
  }

  @Test
  @SuppressWarnings("unchecked")
  void chatToolPlanProposalRecordsPersistWithoutExecutingToolsAndReplayByIdempotency() {
    var tenantCountBefore = identityRepository.tenantRows().size();
    var invitationCountBefore = invitationRepository.invitations().size();
    var steps = List.of(
        new WorkstreamService.ChatToolPlanStep(
            "step-create-organization",
            1,
            "Create Organization Org 1",
            "action-submit-organization-create",
            "user-admin.submit-organization-create",
            "manage-organizations",
            "saas_owner.tenant.manage",
            "schema.organization-admin.create.submit.v1",
            Map.of("organizationName", "Org 1", "reason", "human_chat_tool_plan proposal only"),
            List.of(),
            Map.of("organizationId", "createdOrganization.organizationId"),
            "idem-plan-org-create",
            "independent-command",
            true,
            false,
            "show-inspection",
            "surface-user-admin-organization-detail",
            List.of("human_chat_tool_plan.proposed", "human_chat_tool_plan.step_pending")),
        new WorkstreamService.ChatToolPlanStep(
            "step-invite-organization-admin",
            2,
            "Invite Organization Admin",
            "action-submit-organization-admin-invitation",
            "user-admin.invite-organization-admin",
            "manage-organization-admins",
            "saas_owner.organization_admin.invite",
            "schema.organization-admin.invitation-create.v1",
            Map.of("organizationId", "${step-create-organization.organizationId}", "email", "mckee.hugh@gmail.com", "displayName", "Hugh McKee", "roles", List.of("TENANT_ADMIN"), "reason", "human_chat_tool_plan proposal only"),
            List.of("step-create-organization"),
            Map.of(),
            "idem-plan-org-admin-invite",
            "independent-command-after-dependency",
            true,
            false,
            "show-inspection",
            "surface-user-admin-invitation-detail",
            List.of("human_chat_tool_plan.proposed", "human_chat_tool_plan.step_pending")));

    var response = service.createChatToolPlanProposal(ownerIdentity(), "membership-owner", new WorkstreamService.ChatToolPlanProposalRequest(
        "membership-owner",
        "user-admin-agent",
        "create org \"Org 1\", and invite mckee.hugh@gmail.com as an org admin",
        "corr-chat-plan-proposal",
        "idem-chat-plan-proposal",
        null,
        "Create an Organization and invite its Organization Admin after confirmation.",
        steps,
        "Human confirmation, exact snapshot validation, capability authorization, and dispatcher execution are required later."));

    assertEquals("corr-chat-plan-proposal", response.correlationId());
    assertEquals("user-request", response.userItem().kind());
    assertEquals("chat_tool_plan_proposal", response.agentItem().kind());
    assertEquals("waiting-for-human", response.agentItem().status());
    assertEquals("chat_tool_plan_proposal", response.surface().surfaceType());
    assertEquals("chat_tool_plan.proposal.v1", response.surface().data().get("surfaceContract"));
    assertEquals(true, response.surface().data().get("noDirectMutation"));
    assertEquals(true, response.surface().data().get("noMutation"));
    assertEquals(false, response.surface().data().get("executionEnabled"));
    assertEquals("none: proposal record only; no governed tools are executed before exact snapshot confirmation.", response.surface().data().get("sideEffect"));
    assertEquals("membership-owner", response.surface().authContext().get("selectedContextId"));
    assertEquals("owner@example.test", response.surface().authContext().get("requestedByAccountId"));
    var proposal = (WorkstreamService.ChatToolPlanProposal) response.surface().data().get("proposal");
    assertEquals("waiting-for-human", proposal.status());
    assertEquals("membership-owner", proposal.selectedContextId());
    assertEquals("user-admin-agent", proposal.functionalAgentId());
    assertEquals("owner@example.test", proposal.requestedByAccountId());
    assertEquals(List.of("saas_owner.tenant.manage", "saas_owner.organization_admin.invite"), proposal.requiredCapabilities());
    assertEquals("idem-chat-plan-proposal", proposal.idempotencyRoot());
    assertTrue(proposal.noMutation());
    assertEquals(2, proposal.steps().size());
    assertEquals("manage-organizations", proposal.steps().get(0).governedToolId());
    assertEquals("saas_owner.tenant.manage", proposal.steps().get(0).capabilityId());
    assertEquals("Org 1", proposal.steps().get(0).input().get("organizationName"));
    assertEquals("manage-organization-admins", proposal.steps().get(1).governedToolId());
    assertEquals("saas_owner.organization_admin.invite", proposal.steps().get(1).capabilityId());
    assertEquals("mckee.hugh@gmail.com", proposal.steps().get(1).input().get("email"));
    var snapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) response.surface().data().get("confirmationSnapshot");
    assertEquals(proposal.planId(), snapshot.planId());
    assertEquals(proposal.planSnapshotId(), snapshot.planSnapshotId());
    assertEquals("membership-owner", snapshot.selectedContextId());
    assertEquals("user-admin-agent", snapshot.functionalAgentId());
    assertEquals("owner@example.test", snapshot.requestedByAccountId());
    assertEquals(List.of("saas_owner.tenant.manage", "saas_owner.organization_admin.invite"), snapshot.requiredCapabilities());
    assertEquals("manage-organizations", snapshot.steps().get(0).governedToolId());
    assertEquals("Org 1", snapshot.steps().get(0).input().get("organizationName"));
    assertEquals("idem-plan-org-create", snapshot.steps().get(0).idempotencyKey());
    assertEquals("manage-organization-admins", snapshot.steps().get(1).governedToolId());
    assertEquals("mckee.hugh@gmail.com", snapshot.steps().get(1).input().get("email"));
    assertEquals("idem-plan-org-admin-invite", snapshot.steps().get(1).idempotencyKey());
    assertFalse(snapshot.traceIds().isEmpty());
    assertTrue(snapshot.stepHashes().containsKey("step-create-organization"));
    assertTrue(snapshot.stepHashes().containsKey("step-invite-organization-admin"));
    assertTrue(response.surface().actions().stream().anyMatch(action -> action.actionId().equals("action-confirm-chat-tool-plan") && action.disabled() == null));
    assertEquals(tenantCountBefore, identityRepository.tenantRows().size(), "Plan proposal records must not create Organizations before confirmation.");
    assertEquals(invitationCountBefore, invitationRepository.invitations().size(), "Plan proposal records must not create invitations before confirmation.");
    assertEquals(0, trackingRuntimeInvoker.invocationCount(), "TASK-WCTE-03-001 only adds the proposal substrate; model-backed planning is later.");
    assertBrowserPayloadSafe(response.surface());

    var persistedSurface = service.surface(ownerIdentity(), "membership-owner", response.surface().surfaceId(), "corr-chat-plan-read");
    assertEquals(response.surface().surfaceId(), persistedSurface.surfaceId());
    var persistedItems = service.items(ownerIdentity(), "membership-owner", "user-admin-agent", "corr-chat-plan-items");
    assertTrue(persistedItems.stream().anyMatch(item -> item.itemId().equals(response.agentItem().itemId()) && item.kind().equals("chat_tool_plan_proposal")));

    var replay = service.createChatToolPlanProposal(ownerIdentity(), "membership-owner", new WorkstreamService.ChatToolPlanProposalRequest(
        "membership-owner",
        "user-admin-agent",
        "ignored duplicate prompt",
        "corr-chat-plan-duplicate",
        "idem-chat-plan-proposal",
        null,
        "duplicate ignored",
        List.of(),
        "duplicate ignored"));
    assertEquals(response.surface().surfaceId(), replay.surface().surfaceId());
    assertEquals(response.agentItem().itemId(), replay.agentItem().itemId());
    assertEquals("corr-chat-plan-proposal", replay.correlationId());
    assertEquals(tenantCountBefore, identityRepository.tenantRows().size());
    assertEquals(invitationCountBefore, invitationRepository.invitations().size());
  }

  @Test
  @SuppressWarnings("unchecked")
  void submitMessageRoutesUserAdminMotivatingPromptToModelBackedPlanProposalWithoutMutation() {
    trackingRuntimeInvoker.nextPlanResponse(new WorkstreamRuntimeAgent.ChatToolPlanProposalResponse(
        "proposed",
        "user-admin-agent",
        "corr-user-admin-chat-plan",
        "membership-owner",
        "Create Organization Org 1 and invite mckee.hugh@gmail.com as Organization Admin after confirmation.",
        List.of(
            new WorkstreamRuntimeAgent.ChatToolPlanStepProposal(
                "step-create-organization",
                1,
                "Create Organization Org 1",
                "action-submit-organization-create",
                "user-admin.submit-organization-create",
                "manage-organizations",
                "saas_owner.tenant.manage",
                "schema.organization-admin.create.submit.v1",
                "organizationName=Org 1; reason is browser-safe",
                List.of(),
                Map.of("organizationId", "organizationId"),
                "create-organization",
                "one backend action transaction boundary",
                true,
                false,
                "show-inspection",
                List.of("human_chat_tool_plan.proposed", "human_chat_tool_plan.step_started", "human_chat_tool_plan.step_completed")),
            new WorkstreamRuntimeAgent.ChatToolPlanStepProposal(
                "step-invite-organization-admin",
                2,
                "Invite Organization Admin",
                "action-submit-organization-admin-invitation",
                "user-admin.invite-organization-admin",
                "manage-organization-admins",
                "saas_owner.organization_admin.invite",
                "schema.organization-admin.invitation-create.v1",
                "email=mckee.hugh@gmail.com; roles=[TENANT_ADMIN]; organizationId from step-create-organization",
                List.of("step-create-organization"),
                Map.of(),
                "invite-organization-admin",
                "one backend action transaction boundary after dependency binding",
                true,
                false,
                "show-inspection",
                List.of("human_chat_tool_plan.proposed", "human_chat_tool_plan.step_started", "human_chat_tool_plan.step_completed", "invitation.outbox"))),
        List.of("saas_owner.tenant.manage", "saas_owner.organization_admin.invite"),
        "No mutation occurs until exact plan snapshot confirmation; every step reuses selected AuthContext, idempotency, backend authorization, and traces.",
        "Catalog-bound, no-mutation proposal only.",
        "trace-human-chat-tool-plan-model-proposed",
        null,
        true,
        false));
    var tenantCountBefore = identityRepository.tenantRows().size();
    var invitationCountBefore = invitationRepository.invitations().size();

    var response = service.submitMessage(ownerIdentity(), "membership-owner", new WorkstreamService.WorkstreamMessageRequest(
        "membership-owner", "user-admin-agent", "create org \"Org 1\", and invite mckee.hugh@gmail.com as an org admin", "corr-user-admin-chat-plan", "idem-user-admin-chat-plan"), "corr-header-plan");

    assertEquals("chat_tool_plan_proposal", response.agentItem().kind());
    assertEquals("waiting-for-human", response.agentItem().status());
    assertEquals("chat_tool_plan_proposal", response.surface().surfaceType());
    assertEquals("chat_tool_plan.proposal.v1", response.surface().data().get("surfaceContract"));
    assertEquals(true, response.surface().data().get("noDirectMutation"));
    assertEquals(true, response.surface().data().get("noMutation"));
    assertEquals(false, response.surface().data().get("executionEnabled"));
    var proposal = (WorkstreamService.ChatToolPlanProposal) response.surface().data().get("proposal");
    assertEquals("membership-owner", proposal.selectedContextId());
    assertEquals("user-admin-agent", proposal.functionalAgentId());
    assertEquals("owner@example.test", proposal.requestedByAccountId());
    assertEquals("idem-user-admin-chat-plan", proposal.idempotencyRoot());
    assertEquals(List.of("saas_owner.tenant.manage", "saas_owner.organization_admin.invite"), proposal.requiredCapabilities());
    assertEquals(2, proposal.steps().size());
    assertEquals("action-submit-organization-create", proposal.steps().get(0).actionId());
    assertEquals("manage-organizations", proposal.steps().get(0).governedToolId());
    assertEquals("Org 1", proposal.steps().get(0).input().get("organizationName"));
    assertEquals("action-submit-organization-admin-invitation", proposal.steps().get(1).actionId());
    assertEquals("manage-organization-admins", proposal.steps().get(1).governedToolId());
    assertEquals("${step-create-organization.organizationId}", proposal.steps().get(1).input().get("organizationId"));
    assertEquals("mckee.hugh@gmail.com", proposal.steps().get(1).input().get("email"));
    assertEquals(List.of("TENANT_ADMIN"), proposal.steps().get(1).input().get("roles"));
    var snapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) response.surface().data().get("confirmationSnapshot");
    assertEquals(proposal.planId(), snapshot.planId());
    assertEquals(proposal.planSnapshotId(), snapshot.planSnapshotId());
    assertEquals("membership-owner", snapshot.selectedContextId());
    assertTrue(snapshot.stepHashes().containsKey("step-create-organization"));
    assertTrue(snapshot.stepHashes().containsKey("step-invite-organization-admin"));
    assertTrue(response.surface().actions().stream().anyMatch(action -> action.actionId().equals("action-confirm-chat-tool-plan") && action.disabled() == null));
    assertEquals(tenantCountBefore, identityRepository.tenantRows().size(), "Chat proposal must not create the Organization before confirmation.");
    assertEquals(invitationCountBefore, invitationRepository.invitations().size(), "Chat proposal must not create the invitation before confirmation.");
    assertEquals(1, trackingRuntimeInvoker.planInvocationCount());
    assertEquals(0, trackingRuntimeInvoker.invocationCount(), "Plan prompts must not fall through to markdown runtime when planning succeeds.");
    assertTrue(trackingRuntimeInvoker.lastPlanRequest().backendCatalogSummary().contains("action-submit-organization-create"));
    assertBrowserPayloadSafe(response.surface());
  }

  @Test
  void submitMessageReturnsPlanUnavailableSystemMessageWhenPlanningRuntimeFailsClosed() {
    var tenantCountBefore = identityRepository.tenantRows().size();
    var invitationCountBefore = invitationRepository.invitations().size();

    var response = service.submitMessage(ownerIdentity(), "membership-owner", new WorkstreamService.WorkstreamMessageRequest(
        "membership-owner", "user-admin-agent", "create org \"Org 1\", and invite mckee.hugh@gmail.com as an org admin", "corr-user-admin-chat-plan-unavailable", "idem-user-admin-chat-plan-unavailable"), "corr-header-plan-unavailable");

    assertEquals("chat_tool_plan_system_message", response.agentItem().kind());
    assertEquals("blocked", response.agentItem().status());
    assertEquals("chat_tool_plan_system_message", response.surface().surfaceType());
    assertEquals("chat_tool_plan.system_message.v1", response.surface().data().get("surfaceContract"));
    assertEquals(true, response.surface().data().get("noDirectMutation"));
    assertEquals(true, response.surface().data().get("noMutation"));
    assertEquals(false, response.surface().data().get("executionEnabled"));
    assertTrue(response.surface().toString().contains("CHAT_TOOL_PLAN_RUNTIME_NOT_IMPLEMENTED"));
    assertEquals(tenantCountBefore, identityRepository.tenantRows().size());
    assertEquals(invitationCountBefore, invitationRepository.invitations().size());
    assertEquals(1, trackingRuntimeInvoker.planInvocationCount());
    assertEquals(0, trackingRuntimeInvoker.invocationCount());
    assertBrowserPayloadSafe(response.surface());
  }

  @Test
  void submitMessageReturnsPlanUnavailableSystemMessageWhenSelectedAuthContextCannotUseUserAdminPlanCapabilities() {
    trackingRuntimeInvoker.nextPlanResponse(new WorkstreamRuntimeAgent.ChatToolPlanProposalResponse(
        "proposed", "user-admin-agent", "corr-denied", "membership-admin", "should not be used", List.of(), List.of(), "", "", "", null, true, false));
    var tenantCountBefore = identityRepository.tenantRows().size();
    var invitationCountBefore = invitationRepository.invitations().size();

    var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "user-admin-agent", "create org \"Org 1\", and invite mckee.hugh@gmail.com as an org admin", "corr-user-admin-chat-plan-auth-denied", "idem-user-admin-chat-plan-auth-denied"), "corr-header-plan-auth-denied");

    assertEquals("chat_tool_plan_system_message", response.agentItem().kind());
    assertEquals("chat_tool_plan_system_message", response.surface().surfaceType());
    assertTrue(response.surface().toString().contains("backend authorization or tool-boundary checks failed closed"));
    assertEquals(tenantCountBefore, identityRepository.tenantRows().size());
    assertEquals(invitationCountBefore, invitationRepository.invitations().size());
    assertEquals(0, trackingRuntimeInvoker.planInvocationCount(), "Authorization/catalog denial must happen before model planning.");
    assertBrowserPayloadSafe(response.surface());
  }

  @Test
  void chatToolCatalogListsBoundedHumanChatPlanEntries() {
    var entries = service.chatToolCatalog(null);

    assertTrue(entries.stream().anyMatch(entry -> entry.workstreamId().equals("my-account-agent")
        && entry.actionId().equals("action-update-my-profile")
        && entry.governedToolId().equals("my_account.update_profile_settings")
        && entry.capabilityId().equals("my_account.update_profile_settings")
        && entry.exposureChannel().equals("human_chat_tool_plan")
        && entry.inputSchemaRef().equals("schema.my-account.profile.update.v1")
        && entry.classification().equals("chat-executable-now")
        && entry.riskLevel().equals("low")
        && entry.guardrails().contains("self-account-only")
        && entry.idempotencyRequired()
        && entry.requiresConfirmation()
        && !entry.requiresApproval()
        && entry.traceRequirements().contains("human_chat_tool_plan.step_completed")));
    assertTrue(entries.stream().anyMatch(entry -> entry.workstreamId().equals("my-account-agent")
        && entry.actionId().equals("action-update-my-settings")
        && entry.governedToolId().equals("my_account.update_profile_settings")
        && entry.capabilityId().equals("my_account.update_profile_settings")
        && entry.exposureChannel().equals("human_chat_tool_plan")
        && entry.inputSchemaRef().equals("schema.my-account.settings.update.v1")
        && entry.classification().equals("chat-executable-now")
        && entry.riskLevel().equals("low")
        && entry.rationale().contains("Self-scoped")
        && entry.guardrails().contains("deterministic-surface-routing-first")
        && entry.idempotencyRequired()
        && entry.requiresConfirmation()
        && !entry.requiresApproval()
        && entry.traceRequirements().contains("human_chat_tool_plan.step_completed")));
    assertTrue(entries.stream().anyMatch(entry -> entry.workstreamId().equals("my-account-agent")
        && entry.actionId().equals("action-notification-mark-read")
        && entry.governedToolId().equals("notification.mark_read")
        && entry.capabilityId().equals("notification.mark_read")
        && entry.inputSchemaRef().equals("schema.notification.mark-read.v1")
        && entry.guardrails().contains("visible-notification-id")
        && entry.guardrails().contains("source-state-unchanged")
        && entry.traceRequirements().contains("notification.lifecycle_trace")));
    assertTrue(entries.stream().anyMatch(entry -> entry.workstreamId().equals("my-account-agent")
        && entry.actionId().equals("action-notification-update-preferences")
        && entry.governedToolId().equals("notification.update_preferences")
        && entry.capabilityId().equals("notification.update_preferences")
        && entry.inputSchemaRef().equals("schema.notification.preferences.update.v1")
        && entry.guardrails().contains("external-channel-blocked")
        && entry.traceRequirements().contains("notification.preference_trace")));
    assertTrue(entries.stream().anyMatch(entry -> entry.workstreamId().equals("user-admin-agent")
        && entry.actionId().equals("action-submit-organization-create")
        && entry.governedToolId().equals("manage-organizations")
        && entry.capabilityId().equals("saas_owner.tenant.manage")
        && entry.exposureChannel().equals("human_chat_tool_plan")
        && entry.inputSchemaRef().equals("schema.organization-admin.create.submit.v1")
        && entry.policySummary().contains("selected AuthContext")));
    assertTrue(entries.stream().anyMatch(entry -> entry.workstreamId().equals("user-admin-agent")
        && entry.actionId().equals("action-submit-organization-admin-invitation")
        && entry.governedToolId().equals("manage-organization-admins")
        && entry.capabilityId().equals("saas_owner.organization_admin.invite")
        && entry.traceRequirements().contains("invitation.outbox")));
    assertTrue(entries.stream().anyMatch(entry -> entry.workstreamId().equals("agent-admin-agent")
        && entry.actionId().equals("action-agent-prompt-risk-review-start")
        && entry.classification().equals("approval-gated")
        && entry.guardrails().contains("approval-gate")
        && entry.requiresApproval()));
    assertTrue(entries.stream().anyMatch(entry -> entry.workstreamId().equals("audit-trace-agent")
        && entry.governedToolId().equals("draft-investigation-note")
        && entry.capabilityId().equals("audit.trace.investigation_note.append")));
    assertTrue(entries.stream().anyMatch(entry -> entry.workstreamId().equals("governance-policy-agent")
        && entry.governedToolId().equals("governance.policy.propose")
        && entry.capabilityId().equals("governance.policy.propose")
        && entry.classification().equals("chat-proposal-only")
        && entry.rationale().contains("inert proposal")));
  }

  @Test
  void expandedMyAccountChatToolPlansRequireExactConfirmationAndStaySelfScoped() {
    assertEquals("Tenant Admin", identityRepository.profile("admin@example.test").displayName());
    var response = submitRepresentativePlan(identity(), "membership-admin", "my-account-agent", "change my display name to Chat Catalog Admin", "idem-my-account-profile-chat", List.of(runtimeStep(
        "step-update-my-profile", 1, "action-update-my-profile", "action-update-my-profile", "my_account.update_profile_settings", "my_account.update_profile_settings", "schema.my-account.profile.update.v1", false)));
    var proposal = (WorkstreamService.ChatToolPlanProposal) response.surface().data().get("proposal");
    var snapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) response.surface().data().get("confirmationSnapshot");

    assertEquals("Chat Catalog Admin", proposal.steps().get(0).input().get("displayName"));
    assertEquals("Tenant Admin", identityRepository.profile("admin@example.test").displayName(), "Profile proposal must not mutate before exact confirmation.");
    var denied = assertThrows(AuthorizationException.class, () -> service.confirmChatToolPlan(identity(), "membership-admin", new WorkstreamService.ChatToolPlanConfirmationRequest(
        "membership-admin", proposal.planId(), proposal.planSnapshotId(), "confirm " + proposal.planSnapshotId(), snapshot.stepHashes(), "idem-my-account-profile-chat-denied", "corr-my-account-profile-chat-denied")));
    assertTrue(denied.reasonCode().contains("CHAT_TOOL_PLAN_CONFIRMATION_TEXT_REQUIRED"));
    assertEquals("Tenant Admin", identityRepository.profile("admin@example.test").displayName());

    var confirmed = confirmRepresentativePlan(identity(), "membership-admin", proposal, snapshot, "idem-my-account-profile-chat-confirm");
    var result = (WorkstreamService.ChatToolPlanExecutionResult) confirmed.surface().data().get("result");
    assertEquals("completed", result.status(), result.toString());
    assertTrue(result.completedSteps().stream().anyMatch(step -> step.actionId().equals("action-update-my-profile") && step.resultSurfaceId().equals("surface-my-profile")));
    assertEquals("Chat Catalog Admin", identityRepository.profile("admin@example.test").displayName());
    assertEquals("Member User", identityRepository.profile("member@example.test").displayName(), "My Account chat profile update must stay scoped to the signed-in account.");
    assertFalse(confirmed.surface().toString().contains("providerSecret"));

    var unsupported = assertThrows(AuthorizationException.class, () -> service.createChatToolPlanProposal(identity(), "membership-admin", new WorkstreamService.ChatToolPlanProposalRequest(
        "membership-admin",
        "my-account-agent",
        "change another account display name",
        "corr-my-account-profile-unsupported",
        "idem-my-account-profile-unsupported",
        null,
        "Unsafe My Account profile proposal must fail closed before proposal persistence.",
        List.of(new WorkstreamService.ChatToolPlanStep(
            "step-update-my-profile-unsupported",
            1,
            "Attempt cross-account profile update",
            "action-update-my-profile",
            "action-update-my-profile",
            "my_account.update_profile_settings",
            "my_account.update_profile_settings",
            "schema.my-account.profile.update.v1",
            Map.of("displayName", "Unsafe", "accountId", "member@example.test"),
            List.of(),
            Map.of(),
            "idem-my-account-profile-unsupported-step",
            "independent-command",
            true,
            false,
            "detail-edit",
            "surface-my-profile",
            List.of("human_chat_tool_plan.step_started"))),
        "Unsupported self-service fields must be denied.")));
    assertTrue(unsupported.reasonCode().contains("CHAT_TOOL_MY_ACCOUNT_UNSUPPORTED_FIELD"), unsupported.reasonCode());
  }

  @Test
  @SuppressWarnings("unchecked")
  void expandedMyAccountNotificationChatToolPlanExecutesVisibleNotificationOnlyAndPreservesSourceState() {
    var center = service.surface(identity(), "membership-admin", "surface-my-account-notification-center", "corr-my-account-notification-seed");
    var items = (List<Map<String, Object>>) center.data().get("items");
    assertFalse(items.isEmpty(), "Starter attention should project at least one visible in-app notification for My Account chat-tool coverage.");
    var notificationId = String.valueOf(items.get(0).get("notificationId"));

    var response = submitRepresentativePlan(identity(), "membership-admin", "my-account-agent", "mark notification " + notificationId + " read", "idem-my-account-notification-chat", List.of(runtimeStep(
        "step-mark-notification-read", 1, "action-notification-mark-read", "action-notification-mark-read", "notification.mark_read", "notification.mark_read", "schema.notification.mark-read.v1", false)));
    var proposal = (WorkstreamService.ChatToolPlanProposal) response.surface().data().get("proposal");
    var snapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) response.surface().data().get("confirmationSnapshot");
    assertEquals(notificationId, proposal.steps().get(0).input().get("notificationId"));
    assertTrue(service.surface(identity(), "membership-admin", "surface-my-account-notification-center", "corr-my-account-notification-before-confirm").toString().contains(notificationId), "Notification proposal must not mutate before confirmation.");

    var confirmed = confirmRepresentativePlan(identity(), "membership-admin", proposal, snapshot, "idem-my-account-notification-chat-confirm");
    var result = (WorkstreamService.ChatToolPlanExecutionResult) confirmed.surface().data().get("result");
    assertEquals("completed", result.status(), result.toString());
    assertTrue(result.completedSteps().stream().anyMatch(step -> step.actionId().equals("action-notification-mark-read") && step.governedToolId().equals("notification.mark_read") && step.resultSurfaceId().equals("surface-my-account-notification-center")));
    assertTrue(result.traceIds().stream().anyMatch(trace -> trace.contains("trace-human-chat-tool-plan-step-started")));
    assertFalse(service.surface(identity(), "membership-admin", "surface-my-account-notification-center", "corr-my-account-notification-after-confirm").toString().contains(notificationId), "Read notifications are hidden unless preferences include read items; source work is not resolved by the chat tool.");
    assertBrowserPayloadSafe(confirmed.surface());

    var hidden = assertThrows(AuthorizationException.class, () -> service.createChatToolPlanProposal(identity(), "membership-admin", new WorkstreamService.ChatToolPlanProposalRequest(
        "membership-admin",
        "my-account-agent",
        "archive a notification without a visible id",
        "corr-my-account-notification-missing-id",
        "idem-my-account-notification-missing-id",
        null,
        "Unsafe notification proposal must fail closed before proposal persistence.",
        List.of(new WorkstreamService.ChatToolPlanStep(
            "step-archive-notification-missing-id",
            1,
            "Archive notification without visible id",
            "action-notification-archive",
            "action-notification-archive",
            "notification.archive",
            "notification.archive",
            "schema.notification.archive.v1",
            Map.of(),
            List.of(),
            Map.of(),
            "idem-my-account-notification-missing-id-step",
            "independent-command",
            true,
            false,
            "notification-center",
            "surface-my-account-notification-center",
            List.of("human_chat_tool_plan.step_started"))),
        "Visible notification id is required.")));
    assertTrue(hidden.reasonCode().contains("CHAT_TOOL_NOTIFICATION_VISIBLE_ID_REQUIRED"), hidden.reasonCode());
  }

  @Test
  void expandedMyAccountNotificationPreferenceChatPlanValidatesInAppCategoryAndRejectsExternalControls() {
    var response = submitRepresentativePlan(identity(), "membership-admin", "my-account-agent", "disable my notification preferences for security alerts", "idem-my-account-notification-pref-chat", List.of(runtimeStep(
        "step-update-notification-preferences", 1, "action-notification-update-preferences", "action-notification-update-preferences", "notification.update_preferences", "notification.update_preferences", "schema.notification.preferences.update.v1", false)));
    var proposal = (WorkstreamService.ChatToolPlanProposal) response.surface().data().get("proposal");
    var snapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) response.surface().data().get("confirmationSnapshot");
    assertEquals("audit_or_security", proposal.steps().get(0).input().get("category"));
    assertEquals(false, proposal.steps().get(0).input().get("enabled"));

    var confirmed = confirmRepresentativePlan(identity(), "membership-admin", proposal, snapshot, "idem-my-account-notification-pref-chat-confirm");
    var result = (WorkstreamService.ChatToolPlanExecutionResult) confirmed.surface().data().get("result");
    assertEquals("completed", result.status(), result.toString());
    assertTrue(result.completedSteps().stream().anyMatch(step -> step.actionId().equals("action-notification-update-preferences") && step.resultSurfaceId().equals("surface-my-account-notification-center")));
    assertFalse(result.traceIds().isEmpty());
    assertBrowserPayloadSafe(confirmed.surface());

    var external = assertThrows(AuthorizationException.class, () -> service.createChatToolPlanProposal(identity(), "membership-admin", new WorkstreamService.ChatToolPlanProposalRequest(
        "membership-admin",
        "my-account-agent",
        "change email notification preferences",
        "corr-my-account-notification-pref-external",
        "idem-my-account-notification-pref-external",
        null,
        "Unsafe external preference proposal must fail closed before proposal persistence.",
        List.of(new WorkstreamService.ChatToolPlanStep(
            "step-update-external-notification-preferences",
            1,
            "Attempt external notification preference update",
            "action-notification-update-preferences",
            "action-notification-update-preferences",
            "notification.update_preferences",
            "notification.update_preferences",
            "schema.notification.preferences.update.v1",
            Map.of("channel", "email", "category", "audit_or_security", "enabled", false),
            List.of(),
            Map.of(),
            "idem-my-account-notification-pref-external-step",
            "independent-command",
            true,
            false,
            "notification-center",
            "surface-my-account-notification-center",
            List.of("human_chat_tool_plan.step_started"))),
        "External notification controls are outside My Account chat-tool catalog.")));
    assertTrue(external.reasonCode().contains("CHAT_TOOL_NOTIFICATION_PREFERENCE_UNSUPPORTED_FIELD"), external.reasonCode());
  }

  @Test
  void representativeChatToolPlansCoverAllFiveFoundationWorkstreamsWithConfirmationAndTraceSemantics() {
    assertEquals(UserSettings.ThemeId.AURORA_LIGHT, identityRepository.settings("admin@example.test").themeId());

    var myAccount = submitRepresentativePlan(identity(), "membership-admin", "my-account-agent", "change my theme to Obsidian Dark", "idem-all-my-account", List.of(runtimeStep(
        "step-update-my-settings", 1, "action-update-my-settings", "action-update-my-settings", "my_account.update_profile_settings", "my_account.update_profile_settings", "schema.my-account.settings.update.v1", false)));
    var myAccountProposal = (WorkstreamService.ChatToolPlanProposal) myAccount.surface().data().get("proposal");
    var myAccountSnapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) myAccount.surface().data().get("confirmationSnapshot");
    assertEquals("obsidian-dark", myAccountProposal.steps().get(0).input().get("preferredThemeId"));
    assertEquals(UserSettings.ThemeId.AURORA_LIGHT, identityRepository.settings("admin@example.test").themeId(), "My Account chat proposal must not mutate settings before confirmation.");
    var confirmedMyAccount = confirmRepresentativePlan(identity(), "membership-admin", myAccountProposal, myAccountSnapshot, "idem-all-my-account-confirm");
    var myAccountResult = (WorkstreamService.ChatToolPlanExecutionResult) confirmedMyAccount.surface().data().get("result");
    assertEquals("completed", myAccountResult.status(), myAccountResult.toString());
    assertTrue(myAccountResult.completedSteps().stream().anyMatch(step -> step.governedToolId().equals("my_account.update_profile_settings") && step.capabilityId().equals("my_account.update_profile_settings") && step.resultSurfaceId().equals("surface-my-settings")));
    assertTrue(myAccountResult.traceIds().stream().anyMatch(trace -> trace.contains("trace-human-chat-tool-plan-step-started")));
    assertEquals(UserSettings.ThemeId.OBSIDIAN_DARK, identityRepository.settings("admin@example.test").themeId());

    var agentAdmin = submitRepresentativePlan(identity(), "membership-admin", "agent-admin-agent", "start prompt risk review for the Agent Admin prompt proposal", "idem-all-agent-admin", List.of(runtimeStep(
        "step-start-prompt-risk-review", 1, "action-agent-prompt-risk-review-start", "action-agent-prompt-risk-review-start", AgentAdminPromptRiskReviewService.START_CAPABILITY, AgentAdminPromptRiskReviewService.START_CAPABILITY, "schema.agent-admin.prompt-risk-review.start.v1", true)));
    var agentAdminProposal = (WorkstreamService.ChatToolPlanProposal) agentAdmin.surface().data().get("proposal");
    var agentAdminSnapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) agentAdmin.surface().data().get("confirmationSnapshot");
    assertTrue(agentAdminProposal.steps().get(0).requiresApproval(), "Agent Admin prompt-risk review remains approval-gated in the chat catalog.");
    var confirmedAgentAdmin = confirmRepresentativePlan(identity(), "membership-admin", agentAdminProposal, agentAdminSnapshot, "idem-all-agent-admin-confirm");
    var agentAdminResult = (WorkstreamService.ChatToolPlanExecutionResult) confirmedAgentAdmin.surface().data().get("result");
    assertEquals("failed", agentAdminResult.status(), agentAdminResult.toString());
    assertEquals("approval_required", agentAdminResult.failedSteps().get(0).errorCode());
    assertTrue(agentAdminResult.failedSteps().get(0).traceIds().stream().anyMatch(trace -> trace.contains("trace-human-chat-tool-plan-step-approval-required")));

    var auditTrace = submitRepresentativePlan(identity(), "membership-admin", "audit-trace-agent", "append investigation note \"provider blocked; retry after config\" to this trace", "idem-all-audit-trace", List.of(runtimeStep(
        "step-append-investigation-note", 1, "action-audit-trace-append-investigation-note", "action-audit-trace-append-investigation-note", "draft-investigation-note", "audit.trace.investigation_note.append", "schema.audit-trace.investigation-note.v1", false)));
    var auditProposal = (WorkstreamService.ChatToolPlanProposal) auditTrace.surface().data().get("proposal");
    var auditSnapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) auditTrace.surface().data().get("confirmationSnapshot");
    assertEquals("provider blocked; retry after config", auditProposal.steps().get(0).input().get("note"));
    var confirmedAudit = confirmRepresentativePlan(identity(), "membership-admin", auditProposal, auditSnapshot, "idem-all-audit-trace-confirm");
    var auditResult = (WorkstreamService.ChatToolPlanExecutionResult) confirmedAudit.surface().data().get("result");
    assertEquals("completed", auditResult.status(), auditResult.toString());
    assertTrue(auditResult.completedSteps().stream().anyMatch(step -> step.governedToolId().equals("draft-investigation-note") && step.capabilityId().equals("audit.trace.investigation_note.append") && step.resultSurfaceId().equals("surface-audit-trace-investigation-note")));

    var governance = submitRepresentativePlan(identity(), "membership-admin", "governance-policy-agent", "draft a policy proposal to require approval before redacted exports", "idem-all-governance-policy", List.of(runtimeStep(
        "step-draft-policy-proposal", 1, "action-governance-policy-draft-proposal", "action-governance-policy-draft-proposal", "governance.policy.propose", "governance.policy.propose", "schema.governance-policy.proposal.draft.v1", false)));
    var governanceProposal = (WorkstreamService.ChatToolPlanProposal) governance.surface().data().get("proposal");
    var governanceSnapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) governance.surface().data().get("confirmationSnapshot");
    assertTrue(governanceProposal.approvalSummary().contains("No mutation"));
    var confirmedGovernance = confirmRepresentativePlan(identity(), "membership-admin", governanceProposal, governanceSnapshot, "idem-all-governance-policy-confirm");
    var governanceResult = (WorkstreamService.ChatToolPlanExecutionResult) confirmedGovernance.surface().data().get("result");
    assertEquals("completed", governanceResult.status(), governanceResult.toString());
    assertTrue(governanceResult.completedSteps().stream().anyMatch(step -> step.governedToolId().equals("governance.policy.propose") && step.capabilityId().equals("governance.policy.propose") && step.resultSurfaceId().equals("surface-governance-policy-proposal")));
    assertTrue(governanceProposal.steps().get(0).input().get("proposedContent").toString().contains("approval"));

    trackingRuntimeInvoker.nextPlanResponse(new WorkstreamRuntimeAgent.ChatToolPlanProposalResponse(
        "proposed",
        "user-admin-agent",
        "corr-all-user-admin",
        "membership-owner",
        "Create Organization and invite Organization Admin after confirmation.",
        List.of(
            runtimeStep("step-create-organization", 1, "action-submit-organization-create", "user-admin.submit-organization-create", "manage-organizations", "saas_owner.tenant.manage", "schema.organization-admin.create.submit.v1", false),
            runtimeStep("step-invite-organization-admin", 2, "action-submit-organization-admin-invitation", "user-admin.invite-organization-admin", "manage-organization-admins", "saas_owner.organization_admin.invite", "schema.organization-admin.invitation-create.v1", false)),
        List.of("saas_owner.tenant.manage", "saas_owner.organization_admin.invite"),
        "No mutation occurs until exact plan snapshot confirmation.",
        "Catalog-bound, no-mutation proposal only.",
        "trace-human-chat-tool-plan-all-user-admin",
        null,
        true,
        false));
    var userAdmin = service.submitMessage(ownerIdentity(), "membership-owner", new WorkstreamService.WorkstreamMessageRequest(
        "membership-owner", "user-admin-agent", "create org \"Org All Workstreams\", and invite mckee.hugh+allworkstreams@gmail.com as an org admin", "corr-all-user-admin", "idem-all-user-admin"), "corr-all-user-admin-header");
    assertEquals("chat_tool_plan_proposal", userAdmin.surface().surfaceType());
    var userAdminProposal = (WorkstreamService.ChatToolPlanProposal) userAdmin.surface().data().get("proposal");
    assertEquals(List.of("saas_owner.tenant.manage", "saas_owner.organization_admin.invite"), userAdminProposal.requiredCapabilities());
    assertEquals(2, userAdminProposal.steps().size());
    assertBrowserPayloadSafe(userAdmin.surface());
  }

  @Test
  void confirmedChatToolPlanRequiresExactSnapshotAndExplicitHumanConfirmationBeforeExecution() {
    var tenantCountBefore = identityRepository.tenantRows().size();
    var invitationCountBefore = invitationRepository.invitations().size();
    var response = service.createChatToolPlanProposal(ownerIdentity(), "membership-owner", new WorkstreamService.ChatToolPlanProposalRequest(
        "membership-owner",
        "user-admin-agent",
        "create org \"Org Confirm Gate\", and invite mckee.hugh+gate@gmail.com as an org admin",
        "corr-chat-confirm-gate-proposal",
        "idem-chat-confirm-gate-proposal",
        null,
        "Create an Organization and invite its Organization Admin after confirmation.",
        userAdminOrganizationInviteSteps("Org Confirm Gate", "mckee.hugh+gate@gmail.com", "Hugh Gate", "idem-chat-confirm-gate", List.of("TENANT_ADMIN"), false),
        "Human confirmation is required."));
    var proposal = (WorkstreamService.ChatToolPlanProposal) response.surface().data().get("proposal");
    var snapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) response.surface().data().get("confirmationSnapshot");

    var missingConfirmation = assertThrows(AuthorizationException.class, () -> service.confirmChatToolPlan(ownerIdentity(), "membership-owner", new WorkstreamService.ChatToolPlanConfirmationRequest(
        "membership-owner", proposal.planId(), proposal.planSnapshotId(), "confirm", snapshot.stepHashes(), "idem-chat-confirm-gate-execute", "corr-chat-confirm-gate-missing")));
    assertTrue(missingConfirmation.reasonCode().contains("CHAT_TOOL_PLAN_CONFIRMATION_TEXT_REQUIRED"));

    var tamperedHashes = new java.util.LinkedHashMap<>(snapshot.stepHashes());
    tamperedHashes.put("step-create-organization", "step-hash-tampered");
    var tampered = assertThrows(AuthorizationException.class, () -> service.confirmChatToolPlan(ownerIdentity(), "membership-owner", new WorkstreamService.ChatToolPlanConfirmationRequest(
        "membership-owner", proposal.planId(), proposal.planSnapshotId(), "CONFIRM " + proposal.planSnapshotId(), tamperedHashes, "idem-chat-confirm-gate-tampered", "corr-chat-confirm-gate-tampered")));
    assertTrue(tampered.reasonCode().contains("CHAT_TOOL_PLAN_STEP_HASH_MISMATCH"));
    assertEquals(tenantCountBefore, identityRepository.tenantRows().size(), "Plan must not execute without exact snapshot confirmation.");
    assertEquals(invitationCountBefore, invitationRepository.invitations().size(), "Plan must not invite without exact snapshot confirmation.");
  }

  @Test
  void confirmedUserAdminChatToolPlanExecutesOrganizationAndInvitationIdempotently() {
    var tenantCountBefore = identityRepository.tenantRows().size();
    var invitationCountBefore = invitationRepository.invitations().size();
    var response = service.createChatToolPlanProposal(ownerIdentity(), "membership-owner", new WorkstreamService.ChatToolPlanProposalRequest(
        "membership-owner",
        "user-admin-agent",
        "create org \"Org Confirmed\", and invite mckee.hugh+confirmed@gmail.com as an org admin",
        "corr-chat-confirm-proposal",
        "idem-chat-confirm-proposal",
        null,
        "Create an Organization and invite its Organization Admin after confirmation.",
        userAdminOrganizationInviteSteps("Org Confirmed", "mckee.hugh+confirmed@gmail.com", "Hugh Confirmed", "idem-chat-confirm", List.of("TENANT_ADMIN"), false),
        "Human confirmation, exact snapshot validation, per-step authorization, idempotency, and traces are required."));
    var proposal = (WorkstreamService.ChatToolPlanProposal) response.surface().data().get("proposal");
    var snapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) response.surface().data().get("confirmationSnapshot");

    var confirmed = service.confirmChatToolPlan(ownerIdentity(), "membership-owner", new WorkstreamService.ChatToolPlanConfirmationRequest(
        "membership-owner", proposal.planId(), proposal.planSnapshotId(), "CONFIRM " + proposal.planSnapshotId(), snapshot.stepHashes(), "idem-chat-confirm-execute", "corr-chat-confirm-execute"));

    assertEquals("chat_tool_plan_result", confirmed.agentItem().kind());
    assertEquals("completed", confirmed.agentItem().status());
    assertEquals("chat_tool_plan_result", confirmed.surface().surfaceType());
    assertEquals("chat_tool_plan.result.v1", confirmed.surface().data().get("surfaceContract"));
    var result = (WorkstreamService.ChatToolPlanExecutionResult) confirmed.surface().data().get("result");
    assertEquals("completed", result.status(), result.toString());
    assertEquals(2, result.completedSteps().size());
    assertEquals(0, result.failedSteps().size());
    assertEquals(0, result.skippedSteps().size());
    assertTrue(result.completedSteps().stream().anyMatch(step -> step.actionId().equals("action-submit-organization-create") && step.resultSurfaceId().equals("surface-user-admin-organization-detail")));
    assertTrue(result.completedSteps().stream().anyMatch(step -> step.actionId().equals("action-submit-organization-admin-invitation") && step.resultSurfaceId().equals("surface-user-admin-invitation-detail")));
    assertTrue(result.traceIds().stream().anyMatch(trace -> trace.contains("trace-human-chat-tool-plan-step-started")));
    assertTrue(confirmed.surface().data().get("sideEffect").toString().contains("external/account state may have changed"));
    assertEquals(tenantCountBefore + 1, identityRepository.tenantRows().size());
    assertEquals(invitationCountBefore + 1, invitationRepository.invitations().size());
    assertTrue(invitationRepository.invitations().stream().anyMatch(invitation -> "mckee.hugh+confirmed@gmail.com".equals(invitation.normalizedEmail())));
    assertBrowserPayloadSafe(confirmed.surface());

    var replay = service.confirmChatToolPlan(ownerIdentity(), "membership-owner", new WorkstreamService.ChatToolPlanConfirmationRequest(
        "membership-owner", proposal.planId(), proposal.planSnapshotId(), "CONFIRM " + proposal.planSnapshotId(), snapshot.stepHashes(), "idem-chat-confirm-execute", "corr-chat-confirm-replay"));
    assertEquals(confirmed.surface().surfaceId(), replay.surface().surfaceId());
    assertEquals("corr-chat-confirm-execute", replay.correlationId());
    assertEquals(tenantCountBefore + 1, identityRepository.tenantRows().size());
    assertEquals(invitationCountBefore + 1, invitationRepository.invitations().size());
  }

  @Test
  void confirmedUserAdminChatToolPlanReportsPartialFailureAndRecoveryWithoutRollingBackCompletedStep() {
    var tenantCountBefore = identityRepository.tenantRows().size();
    var invitationCountBefore = invitationRepository.invitations().size();
    var response = service.createChatToolPlanProposal(ownerIdentity(), "membership-owner", new WorkstreamService.ChatToolPlanProposalRequest(
        "membership-owner",
        "user-admin-agent",
        "create org \"Org Confirm Partial\", and invite mckee.hugh+partial@gmail.com as an org admin",
        "corr-chat-confirm-partial-proposal",
        "idem-chat-confirm-partial-proposal",
        null,
        "Create an Organization and invite its Organization Admin after confirmation.",
        userAdminOrganizationInviteSteps("Org Confirm Partial", "mckee.hugh+partial@gmail.com", "Hugh Partial", "idem-chat-confirm-partial", List.of("TENANT_EMPLOYEE"), true),
        "Human confirmation is required; invalid role requests fail closed."));
    var proposal = (WorkstreamService.ChatToolPlanProposal) response.surface().data().get("proposal");
    var snapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) response.surface().data().get("confirmationSnapshot");

    var confirmed = service.confirmChatToolPlan(ownerIdentity(), "membership-owner", new WorkstreamService.ChatToolPlanConfirmationRequest(
        "membership-owner", proposal.planId(), proposal.planSnapshotId(), "CONFIRM " + proposal.planSnapshotId(), snapshot.stepHashes(), "idem-chat-confirm-partial-execute", "corr-chat-confirm-partial-execute"));

    var result = (WorkstreamService.ChatToolPlanExecutionResult) confirmed.surface().data().get("result");
    assertEquals("partial-failure", result.status(), result.toString());
    assertEquals(1, result.completedSteps().size());
    assertEquals(1, result.failedSteps().size());
    assertEquals(1, result.skippedSteps().size());
    assertEquals("validation-error", result.failedSteps().get(0).errorCode(), result.toString());
    assertEquals("skipped", result.skippedSteps().get(0).status());
    assertFalse(result.recoverySteps().isEmpty());
    assertNotNull(confirmed.surface().data().get("systemMessage"));
    assertTrue(confirmed.surface().toString().contains("completed steps remain committed"));
    assertTrue(result.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-human-chat-tool-plan-step-started")));
    assertTrue(result.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-human-chat-tool-plan-step-skipped")));
    assertTrue(confirmed.surface().traceIds().stream().anyMatch(traceId -> traceId.contains("trace-human-chat-tool-plan-confirmed")));
    assertEquals(tenantCountBefore + 1, identityRepository.tenantRows().size(), "Committed Organization remains valid after invitation failure.");
    assertEquals(invitationCountBefore, invitationRepository.invitations().size(), "Invalid invitation step must not send an invitation.");
    assertBrowserPayloadSafe(confirmed.surface());
  }

  @Test
  void confirmedUserAdminChatToolPlanRejectsOutOfCatalogSnapshotBeforeAnyStepExecutes() {
    var tenantCountBefore = identityRepository.tenantRows().size();
    var invitationCountBefore = invitationRepository.invitations().size();
    var outOfCatalogStep = new WorkstreamService.ChatToolPlanStep(
        "step-out-of-catalog-agent-activation",
        1,
        "Attempt Agent Admin activation from User Admin chat plan",
        "action-agent-activation-confirm",
        "agent-admin.activate-agent",
        "agent_admin.activate_agent",
        "agent_admin.activate_agent",
        "schema.agent-admin.activation-confirm.v1",
        Map.of("agentDefinitionId", "user-admin-agent", "acknowledgement", "ACTIVATE"),
        List.of(),
        Map.of(),
        "idem-chat-out-of-catalog-step",
        "independent-command",
        true,
        false,
        "decision-card",
        "surface-agent-admin-activation-confirmation",
        List.of("human_chat_tool_plan.step_started"));
    var response = service.createChatToolPlanProposal(ownerIdentity(), "membership-owner", new WorkstreamService.ChatToolPlanProposalRequest(
        "membership-owner",
        "user-admin-agent",
        "create org \"Org Out Of Catalog\", then activate a managed agent",
        "corr-chat-out-of-catalog-proposal",
        "idem-chat-out-of-catalog-proposal",
        null,
        "Unsafe proposal fixture used to prove confirmation rejects out-of-catalog steps before mutation.",
        List.of(outOfCatalogStep),
        "Out-of-catalog steps must never execute."));
    var proposal = (WorkstreamService.ChatToolPlanProposal) response.surface().data().get("proposal");
    var snapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) response.surface().data().get("confirmationSnapshot");

    var denied = assertThrows(AuthorizationException.class, () -> service.confirmChatToolPlan(ownerIdentity(), "membership-owner", new WorkstreamService.ChatToolPlanConfirmationRequest(
        "membership-owner", proposal.planId(), proposal.planSnapshotId(), "CONFIRM " + proposal.planSnapshotId(), snapshot.stepHashes(), "idem-chat-out-of-catalog-execute", "corr-chat-out-of-catalog-execute")));

    assertTrue(denied.reasonCode().contains("CHAT_TOOL_OUT_OF_WORKSTREAM_CATALOG"), denied.reasonCode());
    assertEquals(tenantCountBefore, identityRepository.tenantRows().size(), "Out-of-catalog confirmed snapshots must be rejected before Organization creation.");
    assertEquals(invitationCountBefore, invitationRepository.invitations().size(), "Out-of-catalog confirmed snapshots must be rejected before invitation creation.");
  }

  @Test
  void submitMessageReturnsAuthorizedMarkdownResponseEnvelopeAndPersistsIt() {
    var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "user-admin-agent", "What can I do next?", "corr-message", "idem-message-1"), "corr-header");

    assertEquals("corr-message", response.correlationId());
    assertEquals("idem-message-1", response.idempotencyKey());
    assertEquals("user-admin-agent", response.userItem().functionalAgentId());
    assertEquals("user-request", response.userItem().kind());
    assertNull(response.userItem().title(), "Request acknowledgement surfaces render only the submitted prompt text.");
    assertEquals("ready", response.userItem().status());
    assertEquals("user-admin-agent", response.agentItem().functionalAgentId());
    assertEquals("markdown_response", response.agentItem().kind());
    assertEquals(response.surface().surfaceId(), response.agentItem().surfaceId());
    assertNull(response.agentItem().body(), "Successful model response text belongs in the rendered markdown_response surface, not placeholder item copy");
    assertEquals("markdown_response", response.surface().surfaceType());
    assertEquals("user-admin-agent", response.surface().ownerFunctionalAgentId());
    assertEquals("membership-admin", response.surface().authContext().get("selectedContextId"));
    assertEquals("corr-message", response.surface().correlationId());
    assertFalse(response.surface().traceIds().isEmpty());
    assertEquals("user-admin-agent", response.surface().data().get("producingAgentId"));
    assertEquals(response.agentItem().itemId(), response.surface().data().get("workstreamEntryId"));
    assertTrue(response.surface().data().get("markdown").toString().contains("## user-admin-agent model response"));
    assertEquals(1, trackingRuntimeInvoker.invocationCount(), "Successful markdown_response must be produced through the workstream Akka Agent runtime invoker seam");
    assertEquals("user-admin-agent", trackingRuntimeInvoker.lastRequest().agentDefinitionId());
    assertNotNull(response.surface().data().get("safety"));
    assertNotNull(response.surface().data().get("trace"));

    var persistedItems = service.items(identity(), "membership-admin", "user-admin-agent", "corr-read");
    assertTrue(persistedItems.stream().anyMatch(item -> item.itemId().equals(response.userItem().itemId())));
    assertTrue(persistedItems.stream().anyMatch(item -> item.itemId().equals(response.agentItem().itemId())));
    var persistedSurface = service.surface(identity(), "membership-admin", response.surface().surfaceId(), "corr-read");
    assertEquals(response.surface().surfaceId(), persistedSurface.surfaceId());
    assertEquals("corr-message", persistedSurface.correlationId());
  }

  @Test
  void submitMessageRoutesMatchedSurfaceIntentBeforeModelInvocation() {
    var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "my-account-agent", "open my account dashboard", "corr-route-my-account", "idem-route-my-account"), "corr-header");

    assertEquals("corr-route-my-account", response.correlationId());
    assertEquals("user-request", response.userItem().kind());
    assertEquals("surface_intent_route", response.agentItem().kind());
    assertEquals("ready", response.agentItem().status());
    assertEquals("surface-my-account-dashboard", response.surface().surfaceId());
    assertEquals(response.surface().surfaceId(), response.agentItem().surfaceId());
    assertTrue(response.agentItem().body().contains("No changes were made"));
    assertEquals(0, trackingRuntimeInvoker.invocationCount(), "Matched deterministic surface routes must not invoke the model-backed runtime");
    assertEquals(true, response.surface().data().get("noDirectMutation"));
    @SuppressWarnings("unchecked")
    var route = (Map<String, Object>) response.surface().data().get("surfaceIntentRoute");
    assertNotNull(route);
    assertEquals("surface_intent_route.v1", route.get("routerContract"));
    assertEquals("my-account-agent", route.get("functionalAgentId"));
    assertEquals("surface-my-account-dashboard", route.get("targetSurfaceId"));
    assertEquals("open my account dashboard", route.get("canonicalPrompt"));
    assertEquals(true, route.get("noMutation"));
    assertEquals("none", route.get("sideEffect"));
    assertEquals(Map.of(), route.get("prefill"));
    assertTrue(response.surface().data().get("lastResult").toString().contains("noMutation=true"));
    assertBrowserPayloadSafe(response.surface());

    var persistedItems = service.items(identity(), "membership-admin", "my-account-agent", "corr-route-read");
    assertTrue(persistedItems.stream().anyMatch(item -> item.itemId().equals(response.agentItem().itemId()) && item.kind().equals("surface_intent_route")));
    var persistedSurface = service.surface(identity(), "membership-admin", response.surface().surfaceId(), "corr-route-read");
    assertEquals("surface-my-account-dashboard", persistedSurface.surfaceId());
    assertEquals("surface_intent_route.v1", ((Map<?, ?>) persistedSurface.data().get("surfaceIntentRoute")).get("routerContract"));
  }

  @Test
  @SuppressWarnings("unchecked")
  void submitMessageRoutesUserAdminSurfaceIntentsWithSafePrefillAndNoMutation() {
    var tenantCountBeforeRoute = identityRepository.tenantRows().size();
    var create = service.submitMessage(ownerIdentity(), "membership-owner", new WorkstreamService.WorkstreamMessageRequest(
        "membership-owner", "user-admin-agent", "create organization \"Org 1\"", "corr-route-org-create", "idem-route-org-create"), "corr-header");

    assertEquals("surface_intent_route", create.agentItem().kind());
    assertEquals("surface-user-admin-organization-create", create.surface().surfaceId());
    assertEquals("create-form", create.surface().surfaceType());
    assertTrue(create.agentItem().body().contains("must review the surface and submit"));
    assertEquals(0, trackingRuntimeInvoker.invocationCount(), "Matched User Admin surface routes must not invoke the model-backed runtime");
    assertNull(trackingRuntimeInvoker.lastRequest(), "Matched User Admin surface routes must never build a model runtime invocation request");
    assertEquals(true, create.surface().data().get("noDirectMutation"));
    var createRoute = (Map<String, Object>) create.surface().data().get("surfaceIntentRoute");
    assertEquals("surface_intent_route.v1", createRoute.get("routerContract"));
    assertEquals("surface_create_prefill", createRoute.get("category"));
    assertEquals(true, createRoute.get("noMutation"));
    assertEquals("none", createRoute.get("sideEffect"));
    assertEquals("route-user-admin-organization-create-v1", ((Map<?, ?>) createRoute.get("metadata")).get("routeId"));
    assertEquals("membership-owner", ((Map<?, ?>) createRoute.get("metadata")).get("selectedContextId"));
    assertEquals("Org 1", ((Map<?, ?>) createRoute.get("prefill")).get("organizationName"));
    assertEquals("Org 1", ((Map<?, ?>) create.surface().data().get("prefill")).get("organizationName"));
    assertEquals("Org 1", ((Map<?, ?>) create.surface().data().get("draft")).get("organizationName"));
    assertEquals("Org 1", ((Map<?, ?>) create.surface().data().get("form")).get("organizationNameDraft"));
    assertEquals(true, ((Map<?, ?>) create.surface().data().get("form")).get("prefillReviewRequired"));
    assertEquals(tenantCountBeforeRoute, identityRepository.tenantRows().size(), "Router must not create an Organization before the user submits the form action");
    assertFalse(identityRepository.tenantRows().stream().anyMatch(tenant -> "Org 1".equals(tenant.displayName())), "Router must not create an Organization before the user submits the form action");
    assertBrowserPayloadSafe(create.surface());

    var organizations = service.submitMessage(ownerIdentity(), "membership-owner", new WorkstreamService.WorkstreamMessageRequest(
        "membership-owner", "user-admin-agent", "show organizations", "corr-route-orgs", "idem-route-orgs"), "corr-header");
    assertEquals("surface_intent_route", organizations.agentItem().kind());
    assertEquals("surface-user-admin-organization-directory", organizations.surface().surfaceId());
    assertEquals("user_admin.organization_directory.v1", organizations.surface().data().get("surfaceContract"));

    var users = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "user-admin-agent", "show users", "corr-route-users", "idem-route-users"), "corr-header");
    assertEquals("surface_intent_route", users.agentItem().kind());
    assertEquals("surface-user-admin-users", users.surface().surfaceId());
    assertEquals("user_admin.users.v1", users.surface().data().get("surfaceContract"));

    var invite = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "user-admin-agent", "invite user alice@example.com", "corr-route-invite", "idem-route-invite"), "corr-header");
    assertEquals("surface_intent_route", invite.agentItem().kind());
    assertEquals("surface-user-admin-invitation-create", invite.surface().surfaceId());
    assertEquals("alice@example.com", ((Map<?, ?>) invite.surface().data().get("prefill")).get("email"));
    assertEquals("alice@example.com", ((Map<?, ?>) invite.surface().data().get("form")).get("emailDraft"));
    assertFalse(invitationRepository.invitations().stream().anyMatch(invitation -> "alice@example.com".equals(invitation.normalizedEmail())), "Router must not create an invitation before the user submits the form action");
    assertBrowserPayloadSafe(invite.surface());
  }

  @Test
  @SuppressWarnings("unchecked")
  void submitMessageRoutesRepresentativeCoreWorkstreamSurfaceIntentsWithoutModelOrMutation() {
    var tenantCountBeforeRoute = identityRepository.tenantRows().size();

    var myAccount = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "my-account-agent", "show my account dashboard", "corr-route-core-my-account", "idem-route-core-my-account"), "corr-header");
    assertEquals("surface_intent_route", myAccount.agentItem().kind());
    assertEquals("surface-my-account-dashboard", myAccount.surface().surfaceId());
    assertEquals("route-my-account-dashboard-open-v1", ((Map<?, ?>) ((Map<?, ?>) myAccount.surface().data().get("surfaceIntentRoute")).get("metadata")).get("routeId"));

    var userAdmin = service.submitMessage(ownerIdentity(), "membership-owner", new WorkstreamService.WorkstreamMessageRequest(
        "membership-owner", "user-admin-agent", "create organization \"Core Route Org\"", "corr-route-core-user-admin", "idem-route-core-user-admin"), "corr-header");
    assertEquals("surface_intent_route", userAdmin.agentItem().kind());
    assertEquals("surface-user-admin-organization-create", userAdmin.surface().surfaceId());
    assertEquals("Core Route Org", ((Map<?, ?>) userAdmin.surface().data().get("prefill")).get("organizationName"));
    assertEquals(tenantCountBeforeRoute, identityRepository.tenantRows().size(), "Core User Admin route must open the create surface without creating an Organization");

    var agentAdmin = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-admin-agent", "show agent catalog", "corr-route-core-agent-admin", "idem-route-core-agent-admin"), "corr-header");
    assertEquals("surface_intent_route", agentAdmin.agentItem().kind());
    assertEquals("surface-agent-admin-catalog", agentAdmin.surface().surfaceId());
    assertEquals("agent_admin.catalog.v1", agentAdmin.surface().data().get("surfaceContract"));
    assertEquals("route-agent-admin-catalog-open-v1", ((Map<?, ?>) ((Map<?, ?>) agentAdmin.surface().data().get("surfaceIntentRoute")).get("metadata")).get("routeId"));

    var auditTrace = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "audit-trace-agent", "open audit trace", "corr-route-core-audit", "idem-route-core-audit"), "corr-header");
    assertEquals("surface_intent_route", auditTrace.agentItem().kind());
    assertEquals("surface-audit-trace-dashboard", auditTrace.surface().surfaceId());
    assertEquals("audit.trace.dashboard.v1", auditTrace.surface().data().get("surfaceContract"));
    assertEquals("route-audit-trace-dashboard-open-v1", ((Map<?, ?>) ((Map<?, ?>) auditTrace.surface().data().get("surfaceIntentRoute")).get("metadata")).get("routeId"));

    var governancePolicy = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "governance-policy-agent", "show policy dashboard", "corr-route-core-governance", "idem-route-core-governance"), "corr-header");
    assertEquals("surface_intent_route", governancePolicy.agentItem().kind());
    assertEquals("surface-governance-policy-dashboard", governancePolicy.surface().surfaceId());
    assertEquals("governance.policy.dashboard.v1", governancePolicy.surface().data().get("surfaceContract"));
    assertEquals("route-governance-policy-dashboard-open-v1", ((Map<?, ?>) ((Map<?, ?>) governancePolicy.surface().data().get("surfaceIntentRoute")).get("metadata")).get("routeId"));

    assertEquals(0, trackingRuntimeInvoker.invocationCount(), "Representative deterministic routes for all five core workstreams must not invoke the model-backed runtime");
    assertFalse(identityRepository.tenantRows().stream().anyMatch(tenant -> "Core Route Org".equals(tenant.displayName())), "Deterministic routing must not submit create commands");
  }

  @Test
  void submitMessageBlocksUnsupportedAndHighRiskChatToolPromptsAfterDeterministicRouting() {
    var tenantCountBefore = identityRepository.tenantRows().size();
    var invitationCountBefore = invitationRepository.invitations().size();

    var tenantCreate = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "user-admin-agent", "create organization \"Tenant Hidden\"", "corr-route-tenant-org-create", "idem-route-tenant-org-create"), "corr-header");

    assertEquals("chat_tool_plan_system_message", tenantCreate.agentItem().kind());
    assertEquals("chat_tool_plan_system_message", tenantCreate.surface().surfaceType());
    assertTrue(tenantCreate.toString().contains("CHAT_TOOL_PROMPT_OUT_OF_CATALOG"));
    assertNull(tenantCreate.surface().data().get("surfaceIntentRoute"));
    assertFalse(tenantCreate.toString().contains("surface-user-admin-organization-create"));

    var ambiguous = service.submitMessage(ownerIdentity(), "membership-owner", new WorkstreamService.WorkstreamMessageRequest(
        "membership-owner", "user-admin-agent", "create organization \"Org 2\" and invite user alice@example.com", "corr-route-ambiguous", "idem-route-ambiguous"), "corr-header");

    assertEquals("chat_tool_plan_system_message", ambiguous.agentItem().kind());
    assertEquals("chat_tool_plan_system_message", ambiguous.surface().surfaceType());
    assertTrue(ambiguous.toString().contains("CHAT_TOOL_PROMPT_OUT_OF_CATALOG"));
    assertNull(ambiguous.surface().data().get("surfaceIntentRoute"));

    var unsafeCompound = service.submitMessage(ownerIdentity(), "membership-owner", new WorkstreamService.WorkstreamMessageRequest(
        "membership-owner", "user-admin-agent", "create org \"Org Unsafe\", invite mckee.hugh+unsafe@gmail.com as an org admin, and activate agent", "corr-route-unsafe-compound", "idem-route-unsafe-compound"), "corr-header");
    assertEquals("chat_tool_plan_system_message", unsafeCompound.agentItem().kind());
    assertTrue(unsafeCompound.toString().contains("CHAT_TOOL_PROMPT_APPROVAL_GATED"));
    assertFalse(unsafeCompound.toString().contains("chat_tool_plan_proposal"), "High-risk suffixes must not be silently trimmed into executable catalog steps.");

    var highRiskAgentLifecycle = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-admin-agent", "activate agent", "corr-route-high-risk-agent", "idem-route-high-risk-agent"), "corr-header");
    assertEquals("chat_tool_plan_system_message", highRiskAgentLifecycle.agentItem().kind());
    assertTrue(highRiskAgentLifecycle.toString().contains("CHAT_TOOL_PROMPT_APPROVAL_GATED"));
    assertNull(highRiskAgentLifecycle.surface().data().get("surfaceIntentRoute"));
    assertFalse(highRiskAgentLifecycle.toString().contains("surface-agent-activation-confirmation"));

    var approvalGatedGovernance = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "governance-policy-agent", "approve proposal", "corr-route-high-risk-governance", "idem-route-high-risk-governance"), "corr-header");
    assertEquals("chat_tool_plan_system_message", approvalGatedGovernance.agentItem().kind());
    assertTrue(approvalGatedGovernance.toString().contains("CHAT_TOOL_PROMPT_APPROVAL_GATED"));
    assertNull(approvalGatedGovernance.surface().data().get("surfaceIntentRoute"));
    assertFalse(approvalGatedGovernance.toString().contains("action-governance-policy-decide"));

    assertEquals(0, trackingRuntimeInvoker.invocationCount(), "Unsupported/high-risk execution prompts must fail closed before model fallback or planning.");
    assertEquals(0, trackingRuntimeInvoker.planInvocationCount(), "Unsupported/high-risk execution prompts must not invoke model-backed planning.");
    assertEquals(tenantCountBefore, identityRepository.tenantRows().size());
    assertEquals(invitationCountBefore, invitationRepository.invitations().size());
    assertBrowserPayloadSafe(tenantCreate.surface());
    assertBrowserPayloadSafe(ambiguous.surface());
    assertBrowserPayloadSafe(unsafeCompound.surface());
    assertBrowserPayloadSafe(highRiskAgentLifecycle.surface());
    assertBrowserPayloadSafe(approvalGatedGovernance.surface());
  }

  @Test
  void submitMessageLeavesUnmatchedPromptOnGovernedModelFallback() {
    var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "user-admin-agent", "What can I do next?", "corr-fallback-message", "idem-fallback-message"), "corr-header");

    assertEquals("markdown_response", response.agentItem().kind());
    assertEquals("markdown_response", response.surface().surfaceType());
    assertTrue(response.surface().data().get("markdown").toString().contains("## user-admin-agent model response"));
    assertNull(response.surface().data().get("surfaceIntentRoute"));
    assertEquals(1, trackingRuntimeInvoker.invocationCount(), "Unmatched prompts must preserve the governed model-backed runtime path");
    assertEquals("user-admin-agent", trackingRuntimeInvoker.lastRequest().agentDefinitionId());
    assertEquals("What can I do next?", trackingRuntimeInvoker.lastRequest().userInput());
  }

  @Test
  void submitMessageSupportsSaasOwnerTenantAndCustomerRuntimeScopes() {
    var ownerResponse = service.submitMessage(ownerIdentity(), "membership-owner", new WorkstreamService.WorkstreamMessageRequest(
        "membership-owner", "user-admin-agent", "Summarize SaaS Owner User Admin options", "corr-owner-message", "idem-owner-message"), "corr-owner-header");
    assertEquals("markdown_response", ownerResponse.surface().surfaceType());
    assertEquals("membership-owner", ownerResponse.surface().authContext().get("selectedContextId"));
    assertEquals(ai.first.application.foundation.workstream.WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, trackingRuntimeInvoker.lastRequest().tenantId(), "SaaS Owner workstream prompts must use the platform governance scope instead of requiring a tenant.");
    assertEquals("user-admin-agent", trackingRuntimeInvoker.lastRequest().agentDefinitionId());

    var tenantResponse = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "user-admin-agent", "Summarize tenant users", "corr-tenant-message", "idem-tenant-message"), "corr-tenant-header");
    assertEquals("markdown_response", tenantResponse.surface().surfaceType());
    assertEquals("tenant-1", trackingRuntimeInvoker.lastRequest().tenantId());

    var customerResponse = service.submitMessage(customerIdentity(), "membership-customer", new WorkstreamService.WorkstreamMessageRequest(
        "membership-customer", "user-admin-agent", "Summarize customer users", "corr-customer-message", "idem-customer-message"), "corr-customer-header");
    assertEquals("markdown_response", customerResponse.surface().surfaceType());
    assertEquals("membership-customer", customerResponse.surface().authContext().get("selectedContextId"));
    assertEquals("tenant-1", trackingRuntimeInvoker.lastRequest().tenantId(), "Customer workstream prompts use the owning tenant governance scope while preserving the selected customer AuthContext.");
    assertEquals("customer-1", trackingRuntimeInvoker.lastRequest().authContext().customerId());
  }

  @Test
  void myAccountSurfacesAreBackendRetrievedWithAuthorityTraceAndContextData() {
    var dashboard = service.surface(identity(), "membership-admin", "surface-my-account-dashboard", "corr-my-account-dashboard");
    var profile = service.surface(identity(), "membership-admin", "surface-my-profile", "corr-my-account-profile");
    var settings = service.surface(identity(), "membership-admin", "surface-my-settings", "corr-my-account-settings");
    var context = service.surface(identity(), "membership-admin", "surface-my-context", "corr-my-account-context");
    var accessProfileContext = service.surface(identity(), "membership-admin", "surface.access.profile.context.v1", "corr-access-profile-context");

    assertEquals("dashboard", dashboard.surfaceType());
    assertEquals("my_account.personal_command_center.v1", dashboard.data().get("surfaceContract"));
    assertTrue(dashboard.toString().contains("attentionCounters"));
    assertTrue(dashboard.toString().contains("controlPanels"));
    assertTrue(dashboard.toString().contains("selected context"));
    assertTrue(dashboard.toString().contains("authorityBasis"));
    assertTrue(dashboard.toString().contains("my_account.view_context"));
    assertTrue(dashboard.toString().contains("traceRefs"));
    assertTrue(dashboard.toString().contains("attention-agent-admin-readiness"));
    assertTrue(dashboard.toString().contains("my_account.list_personal_attention"));
    assertTrue(dashboard.toString().contains("blocked_provider_or_runtime"));
    assertTrue(dashboard.toString().contains("not_found_or_redacted"));
    assertEquals("my-account-agent", dashboard.ownerFunctionalAgentId());
    assertEquals("detail-edit", profile.surfaceType());
    assertEquals("detail-edit", settings.surfaceType());
    assertEquals("detail-edit", context.surfaceType());
    assertEquals("surface-my-context", context.surfaceId());
    assertEquals("surface.access.profile.context.v1", accessProfileContext.surfaceId());
    assertEquals("my-account-agent", accessProfileContext.ownerFunctionalAgentId());
    assertTrue(accessProfileContext.toString().contains("core.access.me"));
    assertTrue(accessProfileContext.toString().contains("core.access.context.select"));
    assertTrue(profile.toString().contains("my_account.update_profile_settings"));
    assertTrue(profile.toString().contains("core.profile.update"));
    assertTrue(settings.toString().contains("preferredThemeId"));
    assertTrue(settings.toString().contains("locale"));
    assertTrue(settings.toString().contains("timeZone"));
    assertTrue(settings.toString().contains("notification.list_my_account_center"));
    assertTrue(context.toString().contains("/api/me?selectedContextId=membership-admin"));
    assertTrue(context.toString().contains("my_account.view_context"));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-show-my-account-dashboard")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-show-my-context") && action.capabilityId().equals("my_account.view_context")));
    assertTrue(context.actions().stream().anyMatch(action -> action.actionId().equals("action-select-my-context") && action.capabilityId().equals("core.access.context.select")));

    var selectContext = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-select-my-context", "action-select-my-context", "core.access.context.select", "core.access.context.select", null, null, "membership-admin", "surface-my-context", "corr-select-context"));
    assertEquals("no-op", selectContext.status());
    assertEquals("surface-my-context", selectContext.resultSurface().surfaceId());
    assertTrue(selectContext.resultSurface().toString().contains("context-authority"));
  }

  @Test
  void myAccountNotificationCenterSurfaceRendersBackendProjectionAndLifecycleActions() {
    var dashboard = service.surface(identity(), "membership-admin", "surface-my-account-dashboard", "corr-notification-dashboard");
    assertTrue(dashboard.toString().contains("card-my-account-notifications"));
    assertTrue(dashboard.toString().contains("notification.list_my_account_center"));
    assertTrue(dashboard.toString().contains("surface-my-account-notification-center"));

    var center = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-show-my-account-notification-center", "action-show-my-account-notification-center", "notification.list_my_account_center", "notification.list_my_account_center", null, null, "membership-admin", "surface-my-account-dashboard", "corr-notification-center"));

    assertEquals("accepted", center.status());
    assertEquals("surface-my-account-notification-center", center.resultSurface().surfaceId());
    assertEquals("notification-center", center.resultSurface().surfaceType());
    assertEquals("my_account.notification_center.v1", center.resultSurface().data().get("surfaceContract"));
    assertEquals("in_app", center.resultSurface().data().get("channel"));
    assertTrue(center.resultSurface().toString().contains("notification.mark_read"));
    assertTrue(center.resultSurface().toString().contains("notification.archive"));
    assertTrue(center.resultSurface().toString().contains("notification.update_preferences"));
    assertFalse(center.resultSurface().data().containsKey("emailPreferencesSummary"));
    assertFalse(center.resultSurface().data().containsKey("channelRegistry"));
    assertFalse(center.resultSurface().data().containsKey("deliveryAttempts"));
    assertFalse(center.resultSurface().data().containsKey("externalOutbox"));
    assertFalse(center.resultSurface().data().toString().contains("notification.email"));
    assertFalse(center.resultSurface().data().toString().contains("resend"));
    assertFalse(center.resultSurface().data().toString().contains("captured_outbox"));
    assertFalse(center.resultSurface().data().toString().contains("SMS, mobile push"));
    assertFalse(center.resultSurface().toString().contains("pushEnabled"));
    assertFalse(center.resultSurface().toString().contains("RESEND_API_KEY"));

    var firstNotificationId = ((List<?>) center.resultSurface().data().get("items")).get(0).toString().replaceFirst(".*notificationId=([^,}]+).*", "$1");
    var read = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-notification-mark-read", "action-notification-mark-read", "notification.mark_read", "notification.mark_read", Map.of("notificationId", firstNotificationId), null, "membership-admin", "surface-my-account-notification-center", "corr-notification-read"));

    assertEquals("full", read.status());
    assertEquals("surface-my-account-notification-center", read.resultSurface().surfaceId());
    assertTrue(read.resultSurface().toString().contains("notification.list_my_account_center"));
    assertTrue(read.message().contains("source attention/task/event state unchanged"));
  }

  @Test
  void myAccountProfileSettingsUpdatePersistsAllowedSelfServiceFieldsAndIsIdempotent() {
    var result = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-update-my-profile", "action-update-my-profile", "my_account.update_profile_settings", "my_account.update_profile_settings", Map.of("displayName", "Updated Admin", "preferredThemeId", "obsidian-dark"), "idem-my-account-update", "membership-admin", "surface-my-profile", "corr-my-account-update"));

    assertEquals("accepted", result.status());
    assertEquals("surface-my-profile", result.resultSurface().surfaceId());
    assertTrue(result.resultSurface().toString().contains("Updated Admin"));
    var me = service.bootstrap(identity(), "membership-admin", "corr-my-account-read").me();
    assertEquals("Updated Admin", me.profile().displayName());
    assertEquals("obsidian-dark", me.settings().preferredThemeId());

    var duplicate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-update-my-profile", "action-update-my-profile", "my_account.update_profile_settings", "my_account.update_profile_settings", Map.of("displayName", "Ignored Duplicate"), "idem-my-account-update", "membership-admin", "surface-my-profile", "corr-my-account-duplicate"));
    assertEquals(result, duplicate);
    assertEquals("Updated Admin", service.bootstrap(identity(), "membership-admin", "corr-my-account-read-2").me().profile().displayName());
  }

  @Test
  void myAccountRejectsUnsupportedSelfServiceFieldsBeforeMutation() {
    var denied = assertThrows(AuthorizationException.class, () -> service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-update-my-settings", "action-update-my-settings", "my_account.update_profile_settings", "my_account.update_profile_settings", Map.of("roleIds", List.of("tenant-admin")), "idem-my-account-denied", "membership-admin", "surface-my-settings", "corr-my-account-denied")));

    assertTrue(denied.reasonCode().contains("MY_ACCOUNT_UNSUPPORTED_SELF_SERVICE_FIELD"));
    assertEquals("Tenant Admin", service.bootstrap(identity(), "membership-admin", "corr-my-account-denied-read").me().profile().displayName());
  }

  @Test
  void myAccountProfileSettingsNoOpIsTracedAndReturnsCurrentSurface() {
    var result = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-update-my-settings", "action-update-my-settings", "my_account.update_profile_settings", "my_account.update_profile_settings", Map.of("displayName", "Tenant Admin", "preferredThemeId", "aurora-light", "locale", "en-US", "timeZone", "America/New_York"), "idem-my-account-noop", "membership-admin", "surface-my-settings", "corr-my-account-noop"));

    assertEquals("no-op", result.status());
    assertEquals("surface-my-settings", result.resultSurface().surfaceId());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("MY_ACCOUNT_PROFILE_SETTINGS_UPDATE") && event.reasonCode().equals("no-op") && event.correlationId().equals("corr-my-account-noop")));
  }

  @Test
  void myAccountSettingsUpdatePersistsThemeLocaleAndTimezone() {
    var result = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-update-my-settings", "action-update-my-settings", "my_account.update_profile_settings", "my_account.update_profile_settings", Map.of("preferredThemeId", "cobalt-light", "locale", "en-GB", "timeZone", "Europe/London"), "idem-my-account-settings", "membership-admin", "surface-my-settings", "corr-my-account-settings-save"));

    assertEquals("accepted", result.status());
    assertEquals("surface-my-settings", result.resultSurface().surfaceId());
    assertTrue(result.resultSurface().toString().contains("cobalt-light"));
    assertTrue(result.resultSurface().toString().contains("en-GB"));
    assertTrue(result.resultSurface().toString().contains("Europe/London"));
    var me = service.bootstrap(identity(), "membership-admin", "corr-my-account-settings-read").me();
    assertEquals("cobalt-light", me.settings().preferredThemeId());
    assertEquals("en-GB", me.settings().locale());
    assertEquals("Europe/London", me.settings().timeZone());
  }

  @Test
  void myAccountSettingsRejectInvalidTimezoneBeforeMutation() {
    var denied = assertThrows(AuthorizationException.class, () -> service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-update-my-settings", "action-update-my-settings", "my_account.update_profile_settings", "my_account.update_profile_settings", Map.of("timeZone", "Hidden/Provider"), "idem-my-account-settings-invalid", "membership-admin", "surface-my-settings", "corr-my-account-settings-invalid")));

    assertEquals(400, denied.httpStatus());
    assertTrue(denied.reasonCode().contains("MY_ACCOUNT_INVALID_PREFERENCE"));
    assertEquals("America/New_York", service.bootstrap(identity(), "membership-admin", "corr-my-account-settings-invalid-read").me().settings().timeZone());
  }

  @Test
  void myAccountOpenWorkstreamActionReturnsBackendResolvedSurface() {
    var result = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-agent-admin", "action-open-agent-admin", "my_account.open_authorized_workstream", "my_account.open_authorized_workstream", Map.of(), null, "membership-admin", "surface-my-account-dashboard", "corr-open-agent-admin"));

    assertEquals("accepted", result.status());
    assertEquals("surface-agent-admin-dashboard", result.resultSurface().surfaceId());
    assertEquals("agent-admin-agent", result.resultSurface().ownerFunctionalAgentId());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM") && event.correlationId().equals("corr-open-agent-admin")));
  }

  @Test
  void myAccountOpenUserAdminAcceptsSaasOwnerUserAdminCapability() {
    var result = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-open-user-admin", "action-open-user-admin", "my_account.open_authorized_workstream", "my_account.open_authorized_workstream", null, null, "membership-owner", "surface-my-account-dashboard", "corr-owner-open-user-admin"));

    assertEquals("accepted", result.status());
    assertEquals("surface-user-admin-saas-owner-dashboard", result.resultSurface().surfaceId());
    assertEquals("user-admin-agent", result.resultSurface().ownerFunctionalAgentId());
    assertTrue(result.traceIds().contains("trace-my-account-open-user-admin-agent"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM") && event.correlationId().equals("corr-owner-open-user-admin") && event.result().name().equals("ALLOWED")));
  }

  @Test
  void shellRequestsResolveRichSurfacesThroughBackendAndPreserveBootstrapGuard() {
    var bootstrap = service.bootstrap(identity(), "membership-admin", "corr-shell-bootstrap");
    assertEquals("surface-my-account-dashboard", bootstrap.surfaces().get(0).surfaceId());
    assertEquals("surface-my-account-dashboard", bootstrap.items().get(0).surfaceId());

    var show = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show user admin dashboard", null, "user-admin-agent", "surface-user-admin-dashboard", null, "user-admin-agent", null, null, "current_workstream", "corr-shell-show", "membership-admin"));
    assertEquals("accepted", show.status());
    assertEquals("dashboard", show.resultSurface().surfaceType());
    assertEquals("surface-user-admin-tenant-dashboard", show.resultSurface().surfaceId());
    assertEquals("user-admin-agent", show.requestItem().functionalAgentId());
    assertEquals("user-request", show.requestItem().kind());
    assertTrue(show.request().canonicalPrompt().contains("show surface"));

    var refresh = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "refresh_surface", "surface_action", "Refresh User Admin dashboard", null, "user-admin-agent", "surface-user-admin-dashboard", null, "user-admin-agent", "surface-user-admin-dashboard", "action-display-user-list", "current_workstream", "corr-shell-refresh", "membership-admin"));
    assertEquals("accepted", refresh.status());
    assertEquals("surface-user-admin-tenant-dashboard", refresh.resultSurface().surfaceId());

    var openAttention = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "open_attention_item", "my_account_panel", "Open Agent Admin readiness", null, "agent-admin-agent", "surface-agent-admin-catalog", "attention-agent-admin-readiness", "my-account-agent", "surface-my-account-dashboard", "action-open-agent-admin", "authorized_cross_workstream", "corr-shell-attention", "membership-admin"));
    assertEquals("accepted", openAttention.status());
    assertEquals("agent-admin-agent", openAttention.resultSurface().ownerFunctionalAgentId());

    var users = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show users", null, "user-admin-agent", null, null, "user-admin-agent", null, null, "current_workstream", "corr-shell-users", "membership-admin"));
    assertEquals("accepted", users.status());
    assertEquals("surface-user-admin-users", users.resultSurface().surfaceId());
    assertEquals("show users", users.request().canonicalPrompt());

    var notifications = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show notifications", null, "my-account-agent", null, null, "my-account-agent", null, null, "current_workstream", "corr-shell-notifications", "membership-admin"));
    assertEquals("accepted", notifications.status());
    assertEquals("surface-my-account-notification-center", notifications.resultSurface().surfaceId());

    var auditTimeline = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show audit timeline", null, "audit-trace-agent", null, null, "audit-trace-agent", null, null, "current_workstream", "corr-shell-audit-timeline", "membership-admin"));
    assertEquals("accepted", auditTimeline.status());
    assertEquals("surface-audit-trace-timeline", auditTimeline.resultSurface().surfaceId());

    var agentCatalog = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show agent catalog", null, "agent-admin-agent", null, null, "agent-admin-agent", null, null, "current_workstream", "corr-shell-agent-catalog", "membership-admin"));
    assertEquals("accepted", agentCatalog.status());
    assertEquals("surface-agent-admin-catalog", agentCatalog.resultSurface().surfaceId());

    var governancePolicies = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show governance policies", null, "governance-policy-agent", null, null, "governance-policy-agent", null, null, "current_workstream", "corr-shell-governance-policies", "membership-admin"));
    assertEquals("accepted", governancePolicies.status());
    assertEquals("surface-governance-policy-inventory", governancePolicies.resultSurface().surfaceId());
  }

  @Test
  void shellRequestsReturnSafeSystemMessageForHiddenTargets() {
    var denied = service.runShellRequest(memberIdentity(), "membership-member", new WorkstreamService.WorkstreamShellRequest(
        "open_workstream", "deep_link", "Open Agent Admin", null, "agent-admin-agent", null, null, "my-account-agent", null, null, "authorized_cross_workstream", "corr-shell-denied", "membership-member"));

    assertEquals("denied", denied.status());
    assertEquals("system_message", denied.resultSurface().surfaceType());
    assertEquals("TARGET_NOT_FOUND_OR_FORBIDDEN", denied.resultSurface().data().get("code"));
    assertFalse(denied.resultSurface().toString().contains("agent_admin.list_definitions"));

    var deniedAlias = service.runShellRequest(memberIdentity(), "membership-member", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show agent catalog", null, "my-account-agent", null, null, "my-account-agent", null, null, "current_workstream", "corr-shell-denied-alias", "membership-member"));
    assertEquals("denied", deniedAlias.status());
    assertEquals("system_message", deniedAlias.resultSurface().surfaceType());
    assertEquals("TARGET_NOT_FOUND_OR_FORBIDDEN", deniedAlias.resultSurface().data().get("code"));
    assertFalse(deniedAlias.resultSurface().toString().contains("agent_admin.list_definitions"));
  }

  @Test
  void shellRequestsDenyUnknownPromptAliasesWithoutDashboardFallback() {
    var unknown = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show payroll", null, "my-account-agent", null, null, "my-account-agent", null, null, "current_workstream", "corr-shell-unknown-alias", "membership-admin"));

    assertEquals("denied", unknown.status());
    assertEquals("system_message", unknown.resultSurface().surfaceType());
    assertEquals("TARGET_NOT_FOUND_OR_FORBIDDEN", unknown.resultSurface().data().get("code"));
    assertFalse(unknown.resultSurface().toString().contains("payroll"));
  }

  @Test
  void myAccountOpenWorkstreamDeniesHiddenTargetsWithSystemMessage() {
    var result = service.runAction(memberIdentity(), "membership-member", new WorkstreamService.CapabilityActionRequest(
        "action-open-agent-admin", "action-open-agent-admin", "my_account.open_authorized_workstream", "my_account.open_authorized_workstream", null, null, "membership-member", "surface-my-account-dashboard", "corr-member-open-agent-admin"));

    assertEquals("denied", result.status());
    assertEquals("system_message", result.resultSurface().surfaceType());
    assertEquals("surface-my-account-open-denied", result.resultSurface().surfaceId());
    assertEquals("my_account.open_denied.v1", result.resultSurface().data().get("surfaceContract"));
    assertEquals("not_found_or_redacted", result.resultSurface().data().get("status"));
    assertEquals("not_found_or_redacted", result.resultSurface().data().get("decision"));
    assertEquals(Boolean.TRUE, result.resultSurface().data().get("noEnumeration"));
    assertTrue(result.resultSurface().data().toString().contains("availableActions"));
    assertTrue(result.resultSurface().data().toString().contains("recoveryStepDetails"));
    assertTrue(result.resultSurface().data().toString().contains("selectedContextId=membership-member"));
    assertFalse(result.resultSurface().toString().contains("agent_admin.list_definitions"));
    assertFalse(result.resultSurface().toString().contains("Agent Admin"));
    assertFalse(result.resultSurface().data().toString().contains("rawJwt"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM") && event.result().name().equals("DENIED") && event.correlationId().equals("corr-member-open-agent-admin")));
  }

  @Test
  void myAccountOpenDeniedSurfaceIsDirectlyRetrievableThroughProtectedRuntimePath() {
    var surface = service.surface(memberIdentity(), "membership-member", "surface-my-account-open-denied", "corr-open-denied-direct");

    assertEquals("surface-my-account-open-denied", surface.surfaceId());
    assertEquals("system_message", surface.surfaceType());
    assertEquals("my_account.open_denied.v1", surface.data().get("surfaceContract"));
    assertEquals("not_found_or_redacted", surface.data().get("decision"));
    assertEquals("not_available_in_selected_context", surface.data().get("safeReasonCode"));
    assertEquals(Boolean.TRUE, surface.data().get("noEnumeration"));
    assertTrue(surface.data().toString().contains("action-show-my-account-dashboard"));
    assertTrue(surface.data().toString().contains("action-show-my-context"));
    assertTrue(surface.data().toString().contains("corr-open-denied-direct"));
    assertFalse(surface.toString().contains("providerSecret"));
    assertFalse(surface.toString().contains("stackTrace"));
  }

  @Test
  void regularMemberCanOpenOnlyMyAccountFromSignedInUserTile() {
    var response = service.submitMessage(new WorkosIdentity("workos-member", "member@example.test", "Member User"), "membership-member", new WorkstreamService.WorkstreamMessageRequest(
        "membership-member", "my-account-agent", "What can I do in My Account?", "corr-member-my-account", "idem-member-my-account"), "corr-header");

    assertEquals("my-account-agent", response.surface().ownerFunctionalAgentId());
    assertEquals("markdown_response", response.surface().surfaceType());
    assertEquals("my-account-agent", response.surface().data().get("producingAgentId"));

    var denied = assertThrows(AuthorizationException.class, () -> service.submitMessage(new WorkosIdentity("workos-member", "member@example.test", "Member User"), "membership-member", new WorkstreamService.WorkstreamMessageRequest(
        "membership-member", "user-admin-agent", "Can I administer users?", "corr-member-user-admin", "idem-member-user-admin"), "corr-header"));
    assertEquals("FUNCTIONAL_AGENT_FORBIDDEN", denied.reasonCode());
  }

  @Test
  void submitMessageSupportsEveryFiveCoreV0FunctionalAgent() {
    for (var agentId : List.of("my-account-agent", "user-admin-agent", "agent-admin-agent", "audit-trace-agent", "governance-policy-agent")) {
      var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
          "membership-admin", agentId, "Show five core v0 readiness", "corr-" + agentId, "idem-" + agentId), "corr-header");

      assertEquals(agentId, response.surface().ownerFunctionalAgentId());
      assertEquals("markdown_response", response.surface().surfaceType());
      assertEquals(agentId, response.surface().data().get("producingAgentId"));
      var expectedRuntimeId = agentId.equals("agent-admin-agent") ? AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID : agentId;
      assertTrue(response.surface().data().get("markdown").toString().contains(expectedRuntimeId + " model response"));
      if (agentId.equals("agent-admin-agent")) {
        assertEquals(AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, trackingRuntimeInvoker.lastRequest().agentDefinitionId());
      }
    }
  }

  @Test
  void submitMessageIsIdempotentForDuplicateClientKeys() {
    var first = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "user-admin-agent", "What can I do next?", "corr-idem-first", "idem-duplicate-message"), "corr-header");
    var duplicate = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "user-admin-agent", "Changed prompt should not append", "corr-idem-second", "idem-duplicate-message"), "corr-header");

    assertEquals(first.userItem().itemId(), duplicate.userItem().itemId());
    assertEquals(first.agentItem().itemId(), duplicate.agentItem().itemId());
    assertEquals(first.surface().surfaceId(), duplicate.surface().surfaceId());
    var persistedItems = service.items(identity(), "membership-admin", "user-admin-agent", "corr-read-idem").stream()
        .filter(item -> item.itemId().equals(first.userItem().itemId()) || item.itemId().equals(first.agentItem().itemId()))
        .toList();
    assertEquals(2, persistedItems.size());
  }

  @Test
  void submitMessageRequiresSelectedContextMatch() {
    var mismatch = assertThrows(AuthorizationException.class, () -> service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-other", "user-admin-agent", "create organization \"Forbidden Org\"", "corr-message", "idem-message-2"), "corr-header"));

    assertEquals("CONTEXT_FORBIDDEN", mismatch.reasonCode());
    assertEquals(0, trackingRuntimeInvoker.invocationCount(), "Mismatched selected contexts must be rejected before router fallback or model runtime invocation");
    assertNull(trackingRuntimeInvoker.lastRequest());
    assertFalse(identityRepository.tenantRows().stream().anyMatch(tenant -> "Forbidden Org".equals(tenant.displayName())), "Rejected contexts must not mutate Organization/Tenant state");
  }

  @Test
  void submitMessageRejectsDeniedFunctionalAgentBeforeModelResponseAndPersistsDenial() {
    var denied = assertThrows(AuthorizationException.class, () -> service.submitMessage(memberIdentity(), "membership-member", new WorkstreamService.WorkstreamMessageRequest(
        "membership-member", "user-admin-agent", "Invite someone", "corr-denied", "idem-message-3"), "corr-header"));

    assertEquals("FUNCTIONAL_AGENT_FORBIDDEN", denied.reasonCode());
    var deniedItems = service.items(memberIdentity(), "membership-member", "user-admin-agent", "corr-denied-read");
    assertTrue(deniedItems.stream().anyMatch(item -> item.kind().equals("system_message") && item.status().equals("blocked") && item.body().contains("FUNCTIONAL_AGENT_FORBIDDEN")));
  }

  @Test
  void submitMessagePropagatesFallbackCorrelationWhenBodyOmitsIt() {
    var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "audit-trace-agent", "Show trace status", null, "idem-message-4"), "corr-header");

    assertEquals("corr-header", response.correlationId());
    assertEquals("corr-header", response.surface().correlationId());
    assertFalse(response.surface().traceIds().isEmpty());
  }

  @Test
  void auditTraceActionsReturnScopedSearchDetailTimelineFailureAndGuidanceSurfaces() throws Exception {
    service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "audit-trace-agent", "Explain current trace status", "corr-audit-runtime", "idem-audit-runtime"), "corr-header");

    var dashboard = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-dashboard", "action-audit-trace-dashboard", "audit.trace.dashboard.read", "audit.trace.dashboard.read", null, null, "membership-admin", "surface-audit-trace-dashboard", "corr-audit-dashboard"));
    assertEquals("accepted", dashboard.status());
    assertEquals("surface-audit-trace-dashboard", dashboard.resultSurface().surfaceId());
    assertEquals("audit.trace.dashboard.v1", dashboard.resultSurface().data().get("surfaceContract"));
    assertTrue(dashboard.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-dashboard")));
    assertTrue(dashboard.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-summary-task-start")));
    assertTrue(dashboard.resultSurface().toString().contains("selectedScope"));
    assertTrue(dashboard.resultSurface().toString().contains("targetSurfaceId=surface-audit-trace-search"));
    assertTrue(dashboard.resultSurface().toString().contains("Raw JWTs") || dashboard.resultSurface().toString().contains("rawJwt"));

    var search = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-search", "action-audit-trace-search", "audit.trace.search", "audit.trace.search", Map.of("pageSize", 10, "filter", "runtime"), null, "membership-admin", "surface-audit-trace-dashboard", "corr-audit-search"));
    assertEquals("accepted", search.status());
    assertEquals("surface-audit-trace-search", search.resultSurface().surfaceId());
    assertEquals("list-search", search.resultSurface().surfaceType());
    assertEquals("audit.trace.search.v1", search.resultSurface().data().get("surfaceContract"));
    assertTrue(search.resultSurface().toString().contains("AuditTraceService") || Files.readString(findSource("AuditTraceService.java")).contains("not_found_or_redacted"));
    assertTrue(search.resultSurface().toString().contains("rawProviderCredential"));
    assertFalse(search.resultSurface().toString().contains("sk-"));

    var detail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-detail", "action-audit-trace-detail", "audit.trace.detail.read", "audit.trace.detail.read", Map.of("traceId", search.resultSurface().traceIds().get(0)), null, "membership-admin", "surface-audit-trace-search", "corr-audit-detail"));
    assertEquals("accepted", detail.status());
    assertEquals("surface-audit-trace-detail", detail.resultSurface().surfaceId());
    assertEquals("audit.trace.detail.v1", detail.resultSurface().data().get("surfaceContract"));
    assertTrue(detail.resultSurface().toString().contains("redactionMetadata"));

    var hidden = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-detail", "action-audit-trace-detail", "audit.trace.detail.read", "audit.trace.detail.read", Map.of("traceId", "trace-other-tenant-secret"), null, "membership-admin", "surface-audit-trace-search", "corr-audit-hidden"));
    assertEquals("not_found_or_redacted", hidden.resultSurface().data().get("decision"));

    var timeline = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-timeline", "action-audit-trace-timeline", "audit.trace.timeline.read", "audit.trace.timeline.read", Map.of("correlationId", "corr-audit-runtime"), null, "membership-admin", "surface-audit-trace-detail", "corr-audit-timeline"));
    assertEquals("accepted", timeline.status());
    assertEquals("audit-timeline", timeline.resultSurface().surfaceType());
    assertEquals("audit.trace.timeline.v1", timeline.resultSurface().data().get("surfaceContract"));
    assertTrue(timeline.resultSurface().toString().contains("corr-audit-runtime"));

    var failure = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-failure-evidence", "action-audit-trace-failure-evidence", "audit.trace.failureEvidence.read", "audit.trace.failureEvidence.read", Map.of("failureCategory", "provider_blocked"), null, "membership-admin", "surface-audit-trace-timeline", "corr-audit-failure"));
    assertEquals("accepted", failure.status());
    assertEquals("audit.trace.failureEvidence.v1", failure.resultSurface().data().get("surfaceContract"));
    assertTrue(failure.resultSurface().toString().contains("[REDACTED]"));
    assertTrue(failure.resultSurface().toString().contains("provider"));

    var guide = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-investigation-guide", "action-audit-trace-investigation-guide", "audit.trace.investigationGuide.read", "audit.trace.investigationGuide.read", Map.of("correlationId", "corr-audit-runtime"), null, "membership-admin", "surface-audit-trace-failure-evidence", "corr-audit-guide"));
    assertEquals("accepted", guide.status());
    assertEquals("decision", guide.resultSurface().surfaceType());
    assertEquals("audit.trace.investigationGuide.v1", guide.resultSurface().data().get("surfaceContract"));
    assertTrue(guide.resultSurface().toString().contains("audit.trace.summary_task.start"));
    assertTrue(guide.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-summary-task-start")));
    assertTrue(guide.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-request-redacted-export")));
    assertTrue(guide.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-append-investigation-note")));

    var export = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-request-redacted-export", "action-audit-trace-request-redacted-export", "audit.trace.export.request", "audit.trace.export.request", Map.of("reason", "Export provider_blocked evidence without bearer hidden-token", "format", "jsonl-redacted"), "idem-audit-export", "membership-admin", "surface-audit-trace-investigation-guide", "corr-audit-export"));
    assertEquals("accepted", export.status());
    assertEquals("surface-audit-trace-export-request", export.resultSurface().surfaceId());
    assertEquals("audit.trace.exportRequest.v1", export.resultSurface().data().get("surfaceContract"));
    assertTrue(export.resultSurface().toString().contains("approval_required"));
    assertTrue(export.resultSurface().toString().contains("Unredacted export"));
    assertFalse(export.resultSurface().toString().contains("hidden-token"));

    var directNote = service.surface(identity(), "membership-admin", "surface-audit-trace-investigation-note", "corr-audit-note-direct");
    assertEquals("surface-audit-trace-investigation-note", directNote.surfaceId());
    assertEquals("audit.trace.investigationNote.v1", directNote.data().get("surfaceContract"));
    assertEquals("not_found_or_redacted", directNote.data().get("status"));
    assertTrue(directNote.toString().contains("no annotation was recorded"));
    assertTrue(directNote.actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-search")));
    assertFalse(service.items(identity(), "membership-admin", "audit-trace-agent", "corr-audit-note-direct-read").stream().anyMatch(item -> "surface-audit-trace-investigation-note".equals(item.surfaceId())));

    var note = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-append-investigation-note", "action-audit-trace-append-investigation-note", "audit.trace.investigation_note.append", "audit.trace.investigation_note.append", Map.of("traceId", "trace-provider-blocked-002", "note", "Follow up without sk-test-secret or bearer hidden-token"), "idem-audit-note", "membership-admin", "surface-audit-trace-investigation-guide", "corr-audit-note"));
    assertEquals("recorded", note.status());
    assertEquals("surface-audit-trace-investigation-note", note.resultSurface().surfaceId());
    assertEquals("audit.trace.investigationNote.v1", note.resultSurface().data().get("surfaceContract"));
    assertTrue(note.resultSurface().data().containsKey("noteResult"));
    assertTrue(note.resultSurface().data().containsKey("targetEvidence"));
    assertTrue(note.resultSurface().data().containsKey("authorizationBasis"));
    assertTrue(note.resultSurface().data().containsKey("allowedActions"));
    assertTrue(note.resultSurface().toString().contains("do not mutate source traces"));
    assertTrue(note.resultSurface().toString().contains("sourceUnchanged"));
    assertTrue(note.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-request-redacted-export")));
    assertTrue(note.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-dashboard")));
    assertFalse(note.resultSurface().toString().contains("sk-test-secret"));
    assertFalse(note.resultSurface().toString().contains("hidden-token"));
    assertTrue(service.items(identity(), "membership-admin", "audit-trace-agent", "corr-audit-note-read").stream().anyMatch(item -> "surface-audit-trace-investigation-note".equals(item.surfaceId()) && "recorded".equals(item.status())));
  }

  @Test
  void auditTraceSummaryWorkerFailsClosedUntilRealAutonomousRuntimeExists() {
    var summary = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-summary-task-start", "action-audit-trace-summary-task-start", "start-audit-summary-task", "audit.trace.summary_task.start", Map.of("schedule", "weekly-owner-digest"), "idem-audit-summary", "membership-admin", "surface-audit-trace-investigation-guide", "corr-audit-summary"));

    assertEquals("blocked_provider_or_runtime", summary.status());
    assertEquals("surface-audit-trace-summary-progress", summary.resultSurface().surfaceId());
    assertEquals("workflow-status", summary.resultSurface().surfaceType());
    assertEquals("audit.trace.summaryProgress.v1", summary.resultSurface().data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", summary.resultSurface().data().get("status"));
    assertTrue(summary.resultSurface().toString().contains("AutonomousAgent"));
    assertTrue(summary.resultSurface().toString().contains("noDirectMutation=true"));
    assertTrue(summary.resultSurface().toString().contains("model-less successful worker result"));
    assertFalse(summary.resultSurface().toString().contains("completed"));
    assertFalse(summary.resultSurface().toString().contains("acceptedResult"));
  }

  @Test
  void auditTraceSearchValidatesInputAndDeniesCrossTenantScope() {
    var invalid = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-search", "action-audit-trace-search", "audit.trace.search", "audit.trace.search", Map.of("pageSize", 500), null, "membership-admin", "surface-audit-trace-dashboard", "corr-audit-invalid"));
    assertEquals("validation-error", invalid.status());
    assertEquals("validation-error", invalid.resultSurface().surfaceType());
    assertEquals("validation-error", invalid.resultSurface().data().get("status"));

    var denied = assertThrows(AuthorizationException.class, () -> service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-search", "action-audit-trace-search", "audit.trace.search", "audit.trace.search", Map.of("tenantId", "tenant-other", "pageSize", 10), null, "membership-admin", "surface-audit-trace-dashboard", "corr-audit-cross-tenant")));
    assertEquals("AUDIT_TRACE_TENANT_FORBIDDEN", denied.reasonCode());
  }

  @Test
  void auditTraceCapabilitiesAreForbiddenForMemberWithoutAuditAuthority() {
    var denied = assertThrows(AuthorizationException.class, () -> service.runAction(memberIdentity(), "membership-member", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-search", "action-audit-trace-search", "audit.trace.search", "audit.trace.search", Map.of("pageSize", 10), null, "membership-member", "surface-audit-trace-dashboard", "corr-member-audit")));

    assertEquals("CAPABILITY_FORBIDDEN", denied.reasonCode());

    var surfaceDenied = assertThrows(AuthorizationException.class, () -> service.surface(memberIdentity(), "membership-member", "surface-audit-trace-dashboard", "corr-member-audit-dashboard"));
    assertEquals("missing-capability:audit.trace.dashboard.read", surfaceDenied.reasonCode());
  }

  @Test
  void governancePolicyBackendActionsExposeReadProposalSimulationApprovalAndBlockedRuntimeSurfaces() {
    var dashboard = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-dashboard", "action-governance-policy-dashboard", "governance.policy.read", "governance.policy.read", null, null, "membership-admin", "surface-governance-policy-dashboard", "corr-gov-dashboard"));
    assertEquals("accepted", dashboard.status());
    assertEquals("surface-governance-policy-dashboard", dashboard.resultSurface().surfaceId());
    assertEquals("governance.policy.dashboard.v1", dashboard.resultSurface().data().get("surfaceContract"));
    assertTrue(dashboard.resultSurface().toString().contains("ready_with_fail_closed_advisory_workers"));
    assertTrue(dashboard.resultSurface().toString().contains("policy-simulations-required-or-blocked"));
    assertTrue(dashboard.resultSurface().toString().contains("proposalLifecycleSegments"));
    assertTrue(dashboard.resultSurface().toString().contains("governance.policy.activate"));
    assertEquals(true, dashboard.resultSurface().data().get("noFakeSuccess"));
    assertEquals(true, dashboard.resultSurface().data().get("noDirectMutation"));
    assertTrue(dashboard.resultSurface().toString().contains("omittedFieldKeys"));
    assertEquals("surface-governance-policy-dashboard", service.surface(identity(), "membership-admin", "surface-governance-policy-dashboard", "corr-gov-dashboard-direct").surfaceId());

    var inventory = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-list", "action-governance-policy-list", "governance.policy.read", "governance.policy.read", null, null, "membership-admin", "surface-governance-policy-dashboard", "corr-gov-list"));
    assertEquals("list-search", inventory.resultSurface().surfaceType());
    assertTrue(inventory.resultSurface().toString().contains("ToolPermissionBoundary"));

    var detail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-read", "action-governance-policy-read", "governance.policy.read", "governance.policy.read", Map.of("policyId", "policy-human-approval"), null, "membership-admin", "surface-governance-policy-inventory", "corr-gov-detail"));
    assertEquals("detail-edit", detail.resultSurface().surfaceType());
    assertTrue(detail.resultSurface().toString().contains("backend AuthContext"));

    var draft = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-draft-proposal", "action-governance-policy-draft-proposal", "governance.policy.propose", "governance.policy.propose", Map.of("rationale", "tighten approval copy"), "idem-gov-draft", "membership-admin", "surface-governance-policy-detail", "corr-gov-draft"));
    assertEquals("accepted", draft.status());
    assertEquals("surface-governance-policy-proposal", draft.resultSurface().surfaceId());
    assertFalse(service.bootstrap(identity(), "membership-admin", "corr-gov-after-draft").toString().contains("api_key"));

    var duplicateDraft = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-draft-proposal", "action-governance-policy-draft-proposal", "governance.policy.propose", "governance.policy.propose", Map.of("rationale", "ignored duplicate"), "idem-gov-draft", "membership-admin", "surface-governance-policy-detail", "corr-gov-draft-duplicate"));
    assertEquals("no-op", duplicateDraft.status());
    assertEquals(draft.resultSurface().data().get("proposalId"), duplicateDraft.resultSurface().data().get("proposalId"));

    var proposalId = draft.resultSurface().data().get("proposalId").toString();
    var submitted = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-submit-proposal", "action-governance-policy-submit-proposal", "governance.policy.propose", "governance.policy.propose", Map.of("proposalId", proposalId), "idem-gov-submit", "membership-admin", "surface-governance-policy-proposal", "corr-gov-submit"));
    assertEquals("accepted", submitted.status());
    assertTrue(submitted.resultSurface().toString().contains("in_review"));

    var directSimulationBeforeRun = service.surface(identity(), "membership-admin", "surface-governance-policy-simulation", "corr-gov-sim-direct-before");
    assertEquals("surface-governance-policy-simulation", directSimulationBeforeRun.surfaceId());
    assertEquals("empty/not-run", directSimulationBeforeRun.data().get("state"));
    assertEquals(true, directSimulationBeforeRun.data().get("noDirectMutation"));
    assertEquals(true, directSimulationBeforeRun.data().get("noFakeSuccess"));
    assertTrue(directSimulationBeforeRun.toString().contains("simulation_not_run"));

    var simulation = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-simulate", "action-governance-policy-simulate", "governance.policy.simulate", "governance.policy.simulate", Map.of("proposalId", proposalId), null, "membership-admin", "surface-governance-policy-proposal", "corr-gov-sim"));
    assertEquals("accepted", simulation.status());
    assertTrue(simulation.resultSurface().toString().contains("model cannot self-approve"));
    assertTrue(simulation.resultSurface().toString().contains("advisory deterministic simulation"));
    assertTrue(simulation.resultSurface().toString().contains("expectedAccessChanges"));
    assertTrue(simulation.resultSurface().actions().toString().contains("action-governance-policy-simulate"));
    assertTrue(simulation.resultSurface().actions().toString().contains("action-governance-policy-start-impact-analysis"));

    var directSimulationAfterRun = service.surface(identity(), "membership-admin", "surface-governance-policy-simulation", "corr-gov-sim-direct-after");
    assertEquals("ready", directSimulationAfterRun.data().get("state"));
    assertEquals(simulation.resultSurface().data().get("simulationId"), directSimulationAfterRun.data().get("simulationId"));

    var decision = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-decide", "action-governance-policy-decide", "governance.proposals.review", "governance.policy.approve", Map.of("proposalId", proposalId, "decision", "approve", "rationale", "bounded starter proof"), "idem-gov-decision", "membership-admin", "surface-governance-policy-simulation", "corr-gov-decision"));
    assertEquals("accepted", decision.status());
    assertEquals("surface-governance-policy-decision", decision.resultSurface().surfaceId());
    assertEquals("decision-card", decision.resultSurface().surfaceType());
    assertEquals("governance.policy.decision.v1", decision.resultSurface().data().get("surfaceContract"));
    assertEquals(true, decision.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, decision.resultSurface().data().get("noFakeSuccess"));
    assertTrue(decision.resultSurface().toString().contains("decisionSummary"));
    assertTrue(decision.resultSurface().toString().contains("riskAndImpact"));
    assertTrue(decision.resultSurface().toString().contains("rollback metadata"));
    assertTrue(decision.resultSurface().toString().contains("governance.proposals.review"));
    assertTrue(decision.resultSurface().actions().toString().contains("action-governance-policy-activate"));
    assertTrue(decision.resultSurface().actions().toString().contains("action-governance-policy-rollback"));
    assertTrue(decision.resultSurface().actions().toString().contains("action-governance-policy-outcome-note"));

    var directDecision = service.surface(identity(), "membership-admin", "surface-governance-policy-decision", "corr-gov-decision-direct");
    assertEquals("surface-governance-policy-decision", directDecision.surfaceId());
    assertEquals("decision-card", directDecision.surfaceType());
    assertEquals("governance.policy.decision.v1", directDecision.data().get("surfaceContract"));
    assertEquals(proposalId, directDecision.data().get("proposalId"));
    assertEquals("approved", directDecision.data().get("status"));
    assertTrue(directDecision.toString().contains("allowedActions"));
    assertTrue(directDecision.toString().contains("disabledActions"));
    assertTrue(directDecision.toString().contains("omittedFieldKeys"));

    var activationBlocked = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-activate", "action-governance-policy-activate", "governance.proposals.activate", "governance.policy.activate", Map.of("proposalId", proposalId), "idem-gov-activate-blocked", "membership-admin", "surface-governance-policy-decision", "corr-gov-activate-blocked"));
    assertEquals("approval-required", activationBlocked.status());
    assertTrue(activationBlocked.resultSurface().toString().contains("sideEffect=none"));

    var activation = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-activate", "action-governance-policy-activate", "governance.proposals.activate", "governance.policy.activate", Map.of("proposalId", proposalId, "rollbackReference", "rollback metadata v1"), "idem-gov-activate", "membership-admin", "surface-governance-policy-decision", "corr-gov-activate"));
    assertEquals("accepted", activation.status());
    assertTrue(activation.resultSurface().toString().contains("activated-with-rollback-metadata"));

    var rollback = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-rollback", "action-governance-policy-rollback", "governance.proposals.activate", "governance.policy.rollback", Map.of("proposalId", proposalId), "idem-gov-rollback", "membership-admin", "surface-governance-policy-decision", "corr-gov-rollback"));
    assertEquals("accepted", rollback.status());
    assertTrue(rollback.resultSurface().toString().contains("rolled_back"));

    var outcomeNote = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-outcome-note", "action-governance-policy-outcome-note", "governance.outcomes.record", "governance.outcomes.record", Map.of("proposalId", proposalId, "note", "Outcome reviewed after activation."), "idem-gov-outcome-note", "membership-admin", "surface-governance-policy-decision", "corr-gov-outcome-note"));
    assertEquals("accepted", outcomeNote.status());
    assertTrue(outcomeNote.resultSurface().toString().contains("Outcome reviewed after activation"));

    var analysis = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-start-impact-analysis", "action-governance-policy-start-impact-analysis", "governance.policy.impact_analysis.start", "governance.policy.impact_analysis.start", Map.of("proposalId", "starter-governance-policy-review"), "idem-gov-analysis", "membership-admin", "surface-governance-policy-dashboard", "corr-gov-analysis"));
    assertEquals("blocked_provider_or_runtime", analysis.status());
    assertEquals("workflow-status", analysis.resultSurface().surfaceType());
    assertEquals("governance.policy.impact_analysis.task.v1", analysis.resultSurface().data().get("surfaceContract"));
    assertEquals("provider_runtime_blocked_fail_closed", analysis.resultSurface().data().get("readinessDecision"));
    assertEquals(true, analysis.resultSurface().data().get("noFakeSuccess"));
    assertTrue(analysis.message().contains("no deterministic"));
    assertTrue(analysis.resultSurface().toString().contains("AutonomousAgent"));
    assertTrue(analysis.resultSurface().toString().contains("ToolPermissionBoundary"));
    assertTrue(analysis.resultSurface().toString().contains("governance.policy.impact_analysis.read"));
    assertTrue(analysis.resultSurface().toString().contains("forbiddenEffects"));
  }

  @Test
  void governancePolicyActionsReturnStructuredDenialSurfacesForMembersAndCrossTenantInput() {
    var denied = service.runAction(memberIdentity(), "membership-member", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-list", "action-governance-policy-list", "governance.policy.read", "governance.policy.read", null, null, "membership-member", "surface-governance-policy-dashboard", "corr-gov-member"));
    assertEquals("denied", denied.status());
    assertEquals("system-message", denied.resultSurface().surfaceType());
    assertEquals("governance.policy.system_message.v1", denied.resultSurface().data().get("surfaceContract"));
    assertTrue(denied.resultSurface().toString().contains("CAPABILITY_FORBIDDEN"));

    var crossTenant = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-simulate", "action-governance-policy-simulate", "governance.policy.simulate", "governance.policy.simulate", Map.of("tenantId", "tenant-other"), null, "membership-admin", "surface-governance-policy-proposal", "corr-gov-cross"));
    assertEquals("denied", crossTenant.status());
    assertEquals("system-message", crossTenant.resultSurface().surfaceType());
    assertTrue(crossTenant.resultSurface().toString().contains("GOVERNANCE_POLICY_TENANT_FORBIDDEN"));
  }

  @Test
  void governancePolicyMessageUsesGovernanceCapabilityForRuntimeTraces() {
    var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "governance-policy-agent", "Explain policy approval gates", "corr-governance-message", "idem-governance-message"), "corr-header");

    assertEquals("governance-policy-agent", response.surface().ownerFunctionalAgentId());
    assertEquals("markdown_response", response.surface().surfaceType());
    assertEquals("governance-policy-agent", trackingRuntimeInvoker.lastRequest().agentDefinitionId());
    assertTrue(service.bootstrap(identity(), "membership-admin", "corr-governance-trace-read").functionalAgents().stream()
        .filter(agent -> agent.functionalAgentId().equals("governance-policy-agent"))
        .findFirst()
        .orElseThrow()
        .requiredCapabilityIds()
        .contains("governance.policy.read"));
  }

  @Test
  void auditTraceMessageFailsClosedWhenRuntimeProviderBoundaryIsMissing() {
    var invitationRepository = new InMemoryTestInvitationRepository();
    var resolver = new AuthContextResolver(identityRepository);
    var attentionService = new AttentionService(new InMemoryTestAttentionRepository(), resolver, Clock.systemUTC());
    var meService = new MeService(resolver, new MyAccountService(resolver, attentionService));
    var userAdminService = new UserAdminService(identityRepository, Clock.systemUTC());
    var invitationService = new InvitationService(identityRepository, invitationRepository, Clock.systemUTC());
    var agentRepository = new InMemoryTestAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(agentRepository, Clock.systemUTC()).importStarterDefaults("tenant-1", "bootstrap", "corr-agent-seed-failclosed");
    var agentRuntimeService = new AgentRuntimeService(agentRepository, resolver, Clock.systemUTC(), request -> {
      throw new ModelProviderClient.ModelProviderException("model-provider-config-missing", "Model provider configuration is missing required backend variable OPENAI_API_KEY.");
    }, new InMemoryTestAgentRuntimeTraceSink());
    var failClosedWorkstreamLogRepository = new InMemoryTestWorkstreamLogRepository();
    var notificationService = new NotificationService(new InMemoryTestNotificationRepository(), resolver, Clock.systemUTC());
    var failClosedService = new WorkstreamService(meService, resolver, new UserDirectoryView(userAdminService), new InvitationView(invitationService), userAdminService, invitationService, agentRepository, agentRuntimeService, agentRuntimeService::invokeWorkstreamAgent, failClosedWorkstreamLogRepository, new InMemoryTestAccessReviewTaskRepository(), new InMemoryTestAuditTraceRepository(agentRuntimeService, failClosedWorkstreamLogRepository), new InMemoryTestGovernancePolicyRepository(), attentionService, null, null, null, new FailClosedAccessReviewAutonomousAgentRuntime(), notificationService);

    var response = failClosedService.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "audit-trace-agent", "Explain this provider failure", "corr-audit-failclosed", "idem-audit-failclosed"), "corr-header");

    assertEquals("blocked", response.agentItem().status());
    assertEquals("system_message", response.agentItem().kind());
    assertEquals("system_message", response.surface().surfaceType());
    assertEquals("blocked_provider_or_runtime", response.surface().data().get("status"));
    assertTrue(response.surface().data().get("message").toString().contains("blocked before a response was produced"));
    assertTrue(response.surface().toString().contains("model-provider-config-missing"));
    assertFalse(response.surface().toString().contains("should not be used"));
  }

  private WorkstreamService.WorkstreamMessageResponse submitRepresentativePlan(WorkosIdentity actorIdentity, String selectedContextId, String functionalAgentId, String prompt, String idempotencyKey, List<WorkstreamRuntimeAgent.ChatToolPlanStepProposal> runtimeSteps) {
    var requiredCapabilities = runtimeSteps.stream().map(WorkstreamRuntimeAgent.ChatToolPlanStepProposal::capabilityId).distinct().toList();
    trackingRuntimeInvoker.nextPlanResponse(new WorkstreamRuntimeAgent.ChatToolPlanProposalResponse(
        "proposed",
        functionalAgentId,
        "corr-" + idempotencyKey,
        selectedContextId,
        "Representative " + functionalAgentId + " confirmed chat tool plan; no mutation before confirmation.",
        runtimeSteps,
        requiredCapabilities,
        "No mutation occurs until exact plan snapshot confirmation; each step reuses selected AuthContext, idempotency, backend authorization, and traces.",
        "Catalog-bound, no-mutation proposal only.",
        "trace-human-chat-tool-plan-" + idempotencyKey,
        null,
        true,
        false));
    var response = service.submitMessage(actorIdentity, selectedContextId, new WorkstreamService.WorkstreamMessageRequest(
        selectedContextId, functionalAgentId, prompt, "corr-" + idempotencyKey, idempotencyKey), "corr-header-" + idempotencyKey);
    assertEquals("chat_tool_plan_proposal", response.agentItem().kind());
    assertEquals("waiting-for-human", response.agentItem().status());
    assertEquals("chat_tool_plan_proposal", response.surface().surfaceType());
    assertEquals("chat_tool_plan.proposal.v1", response.surface().data().get("surfaceContract"));
    assertEquals(true, response.surface().data().get("noDirectMutation"));
    assertEquals(true, response.surface().data().get("noMutation"));
    assertEquals(false, response.surface().data().get("executionEnabled"));
    assertTrue(response.surface().actions().stream().anyMatch(action -> action.actionId().equals("action-confirm-chat-tool-plan")));
    assertBrowserPayloadSafe(response.surface());
    return response;
  }

  private WorkstreamService.WorkstreamMessageResponse confirmRepresentativePlan(WorkosIdentity actorIdentity, String selectedContextId, WorkstreamService.ChatToolPlanProposal proposal, WorkstreamService.ChatToolPlanConfirmationSnapshot snapshot, String idempotencyKey) {
    var confirmed = service.confirmChatToolPlan(actorIdentity, selectedContextId, new WorkstreamService.ChatToolPlanConfirmationRequest(
        selectedContextId, proposal.planId(), proposal.planSnapshotId(), "CONFIRM " + proposal.planSnapshotId(), snapshot.stepHashes(), idempotencyKey, "corr-" + idempotencyKey));
    assertEquals("chat_tool_plan_result", confirmed.agentItem().kind());
    assertEquals("chat_tool_plan_result", confirmed.surface().surfaceType());
    assertEquals("chat_tool_plan.result.v1", confirmed.surface().data().get("surfaceContract"));
    assertTrue(confirmed.surface().traceIds().stream().anyMatch(trace -> trace.contains("trace-human-chat-tool-plan-confirmed")));
    assertBrowserPayloadSafe(confirmed.surface());
    return confirmed;
  }

  private WorkstreamRuntimeAgent.ChatToolPlanStepProposal runtimeStep(String stepId, int sequence, String actionId, String browserToolId, String governedToolId, String capabilityId, String inputSchemaRef, boolean requiresApproval) {
    return new WorkstreamRuntimeAgent.ChatToolPlanStepProposal(
        stepId,
        sequence,
        actionId + " representative step",
        actionId,
        browserToolId,
        governedToolId,
        capabilityId,
        inputSchemaRef,
        "backend canonical inputs are validated and redacted before proposal rendering",
        List.of(),
        Map.of(),
        stepId,
        requiresApproval ? "approval-gated transaction boundary" : "independent backend transaction boundary",
        true,
        requiresApproval,
        requiresApproval ? "workflow-status" : "inline",
        List.of("human_chat_tool_plan.proposed", "human_chat_tool_plan.step_started", requiresApproval ? "human_chat_tool_plan.step_failed" : "human_chat_tool_plan.step_completed"));
  }

  private List<WorkstreamService.ChatToolPlanStep> userAdminOrganizationInviteSteps(String organizationName, String email, String displayName, String idempotencyRoot, List<String> inviteRoles, boolean includeDependentAfterInvite) {
    var steps = new java.util.ArrayList<WorkstreamService.ChatToolPlanStep>();
    steps.add(new WorkstreamService.ChatToolPlanStep(
        "step-create-organization",
        1,
        "Create Organization " + organizationName,
        "action-submit-organization-create",
        "user-admin.submit-organization-create",
        "manage-organizations",
        "saas_owner.tenant.manage",
        "schema.organization-admin.create.submit.v1",
        Map.of("organizationName", organizationName, "reason", "human_chat_tool_plan confirmed execution test"),
        List.of(),
        Map.of("organizationId", "organizationId"),
        idempotencyRoot + "-org-create",
        "independent-command",
        true,
        false,
        "show-inspection",
        "surface-user-admin-organization-detail",
        List.of("human_chat_tool_plan.step_started", "human_chat_tool_plan.step_completed")));
    steps.add(new WorkstreamService.ChatToolPlanStep(
        "step-invite-organization-admin",
        2,
        "Invite Organization Admin",
        "action-submit-organization-admin-invitation",
        "user-admin.invite-organization-admin",
        "manage-organization-admins",
        "saas_owner.organization_admin.invite",
        "schema.organization-admin.invitation-create.v1",
        Map.of("organizationId", "${step-create-organization.organizationId}", "email", email, "displayName", displayName, "roles", inviteRoles, "reason", "human_chat_tool_plan confirmed execution test"),
        List.of("step-create-organization"),
        Map.of(),
        idempotencyRoot + "-org-admin-invite",
        "independent-command-after-dependency",
        true,
        false,
        "show-inspection",
        "surface-user-admin-invitation-detail",
        List.of("human_chat_tool_plan.step_started", "human_chat_tool_plan.step_completed", "invitation.outbox")));
    if (includeDependentAfterInvite) {
      steps.add(new WorkstreamService.ChatToolPlanStep(
          "step-dependent-after-failure",
          3,
          "Dependent retry placeholder",
          "action-submit-organization-admin-invitation",
          "user-admin.invite-organization-admin",
          "manage-organization-admins",
          "saas_owner.organization_admin.invite",
          "schema.organization-admin.invitation-create.v1",
          Map.of("organizationId", "${step-create-organization.organizationId}", "email", "second+partial@example.test", "displayName", "Second Partial", "roles", List.of("TENANT_ADMIN"), "reason", "must skip after failed dependency"),
          List.of("step-invite-organization-admin"),
          Map.of(),
          idempotencyRoot + "-dependent-after-failure",
          "independent-command-after-dependency",
          true,
          false,
          "show-inspection",
          "surface-user-admin-invitation-detail",
          List.of("human_chat_tool_plan.step_skipped")));
    }
    return List.copyOf(steps);
  }

  private WorkstreamService serviceWithAccessReviewRuntime(AccessReviewAutonomousAgentRuntime runtime) {
    var resolver = new AuthContextResolver(identityRepository);
    var attentionService = new AttentionService(new InMemoryTestAttentionRepository(), resolver, Clock.systemUTC());
    var attentionProducerService = new AttentionProducerService(new InMemoryTestAttentionRepository(), identityRepository, Clock.systemUTC());
    var meService = new MeService(resolver, new MyAccountService(resolver, attentionService));
    var userAdminService = new UserAdminService(identityRepository, Clock.systemUTC());
    var localInvitationService = new InvitationService(identityRepository, invitationRepository, Clock.systemUTC(), attentionProducerService, null);
    var workstreamLogRepository = new InMemoryTestWorkstreamLogRepository();
    var notificationService = new NotificationService(new InMemoryTestNotificationRepository(), resolver, Clock.systemUTC());
    return new WorkstreamService(meService, resolver, new UserDirectoryView(userAdminService), new InvitationView(localInvitationService), userAdminService, localInvitationService, agentRepository, trackingRuntimeInvoker.delegate, trackingRuntimeInvoker, workstreamLogRepository, new InMemoryTestAccessReviewTaskRepository(), new InMemoryTestAuditTraceRepository(trackingRuntimeInvoker.delegate, workstreamLogRepository), new InMemoryTestGovernancePolicyRepository(), attentionService, attentionProducerService, null, null, runtime, notificationService);
  }

  private static final class CompletedAccessReviewRuntime implements AccessReviewAutonomousAgentRuntime {
    @Override
    public StartOutcome start(AuthContextResolver.ResolvedMe actor, ai.first.domain.coreapp.useradmin.AccessReviewTask starterTask, String correlationId) {
      return StartOutcome.queued("aa-" + starterTask.taskId(), "Model-backed access-review AutonomousAgent accepted; awaiting projection.", List.of("trace-useradmin-access-review-model-start"));
    }

    @Override
    public Projection project(ai.first.domain.coreapp.useradmin.AccessReviewTask starterTask, String correlationId) {
      return new Projection(ai.first.domain.coreapp.useradmin.AccessReviewTask.Status.COMPLETED, 100, "Model-backed access-review advisory result completed; access state unchanged until human review.", null, null, List.of("evidence:userAdminEvidence.read:No stale admin membership found"), List.of("recommendation:review-dormant-admin:LOW:Review dormant admin evidence before any follow-up task"), List.of("trace-useradmin-access-review-model", "trace-useradmin-access-review-tool-userAdminEvidence", "trace-useradmin-access-review-policy"));
    }
  }

  private static void assertBrowserPayloadSafe(WorkstreamService.SurfaceEnvelope surface) {
    var rendered = surface.toString();
    assertNotNull(surface.correlationId());
    assertFalse(surface.traceIds().isEmpty());
    assertFalse(rendered.contains("invite-token"));
    assertFalse(rendered.contains("tokenHash"));
    assertFalse(rendered.contains("Bearer "));
    assertFalse(rendered.contains("Authorization="));
    assertFalse(rendered.contains("providerSecret"));
    assertFalse(rendered.contains("RESEND_API_KEY"));
    assertFalse(rendered.contains("sk-secret"));
    assertFalse(rendered.contains("api_key="));
  }

  private static Path findSource(String fileName) throws Exception {
    try (Stream<Path> paths = Files.walk(Path.of("src/main/java"))) {
      return paths
          .filter(path -> path.getFileName().toString().equals(fileName))
          .findFirst()
          .orElseThrow(() -> new AssertionError("Missing source file: " + fileName));
    }
  }

  private static final class TrackingWorkstreamAgentRuntimeTestAdapter implements WorkstreamAgentRuntimeInvoker {
    private final AgentRuntimeService delegate;
    private final AtomicInteger invocationCount = new AtomicInteger();
    private final AtomicInteger planInvocationCount = new AtomicInteger();
    private AgentRuntimeService.RuntimeInvocationRequest lastRequest;
    private AgentRuntimeService.PlanProposalInvocationRequest lastPlanRequest;
    private WorkstreamRuntimeAgent.ChatToolPlanProposalResponse nextPlanResponse;

    private TrackingWorkstreamAgentRuntimeTestAdapter(AgentRuntimeService delegate) {
      this.delegate = delegate;
    }

    @Override
    public AgentRuntimeService.RuntimeInvocationResult invokeWorkstreamAgent(AgentRuntimeService.RuntimeInvocationRequest request) {
      invocationCount.incrementAndGet();
      lastRequest = request;
      return delegate.invokeWorkstreamAgent(request);
    }

    @Override
    public AgentRuntimeService.PlanProposalInvocationResult proposeChatToolPlan(AgentRuntimeService.PlanProposalInvocationRequest request) {
      planInvocationCount.incrementAndGet();
      lastPlanRequest = request;
      if (nextPlanResponse == null) {
        return AgentRuntimeService.planProposalUnavailable(
            request,
            "CHAT_TOOL_PLAN_RUNTIME_NOT_IMPLEMENTED",
            "Workstream chat tool plan proposal requires the governed Akka Agent runtime path.",
            List.of("trace-chat-tool-plan-runtime-not-implemented"));
      }
      var response = nextPlanResponse;
      nextPlanResponse = null;
      return new AgentRuntimeService.PlanProposalInvocationResult(
          AgentRuntimeTrace.Decision.ALLOWED,
          response,
          List.of("trace-human-chat-tool-plan-test-provider"),
          null,
          null);
    }

    private void nextPlanResponse(WorkstreamRuntimeAgent.ChatToolPlanProposalResponse response) {
      this.nextPlanResponse = response;
    }

    private int invocationCount() {
      return invocationCount.get();
    }

    private int planInvocationCount() {
      return planInvocationCount.get();
    }

    private AgentRuntimeService.RuntimeInvocationRequest lastRequest() {
      return lastRequest;
    }

    private AgentRuntimeService.PlanProposalInvocationRequest lastPlanRequest() {
      return lastPlanRequest;
    }
  }

  private WorkosIdentity identity() {
    return new WorkosIdentity("workos-admin", "admin@example.test", "Tenant Admin");
  }

  private WorkosIdentity memberIdentity() {
    return new WorkosIdentity("workos-member", "member@example.test", "Member User");
  }

  private WorkosIdentity ownerIdentity() {
    return new WorkosIdentity("workos-owner", "owner@example.test", "SaaS Owner");
  }

  private WorkosIdentity customerIdentity() {
    return new WorkosIdentity("workos-customer", "customer@example.test", "Customer Admin");
  }
}

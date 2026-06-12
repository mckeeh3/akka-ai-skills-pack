package ai.first.application.coreapp.workstream;

import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentReferenceManifest;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import ai.first.domain.foundation.identity.AuthContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.coreapp.myaccount.MyAccountService;
import ai.first.application.coreapp.useradmin.FailClosedAccessReviewAutonomousAgentRuntime;
import ai.first.application.coreapp.useradmin.LocalDemoAccessReviewTaskRepository;
import ai.first.application.coreapp.useradmin.UserAdminService;
import ai.first.application.coreapp.useradmin.UserDirectoryView;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AgentRuntimeService;
import ai.first.application.foundation.agent.LocalDemoAgentBehaviorRepository;
import ai.first.application.foundation.agent.LocalDemoAgentRuntimeTraceSink;
import ai.first.application.foundation.agent.ModelProviderClient;
import ai.first.application.foundation.agent.WorkstreamAgentRuntimeInvoker;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
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
import ai.first.application.foundation.attention.LocalDemoAttentionRepository;
import ai.first.application.foundation.audit.AkkaAuditTraceRepository;
import ai.first.application.foundation.audit.AuditTraceService;
import ai.first.application.foundation.audit.LocalDemoAuditTraceRepository;
import ai.first.application.foundation.governance.LocalDemoGovernancePolicyRepository;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.LocalDemoIdentityRepository;
import ai.first.application.foundation.identity.MeService;
import ai.first.application.foundation.identity.StarterSecurityComponents;
import ai.first.application.foundation.invitation.InvitationService;
import ai.first.application.foundation.invitation.InvitationView;
import ai.first.application.foundation.invitation.LocalDemoInvitationRepository;
import ai.first.application.foundation.notification.LocalDemoNotificationRepository;
import ai.first.application.foundation.notification.NotificationService;
import ai.first.application.foundation.workstream.AkkaWorkstreamLogRepository;
import ai.first.application.foundation.workstream.LocalDemoWorkstreamEventRepository;
import ai.first.application.foundation.workstream.LocalDemoWorkstreamLogRepository;
import ai.first.application.foundation.workstream.WorkstreamEventAttentionConsumer;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.api.coreapp.workstream.WorkstreamEndpoint;

class WorkstreamServiceTest {
  private LocalDemoIdentityRepository identityRepository;
  private LocalDemoAgentBehaviorRepository agentRepository;
  private LocalDemoWorkstreamEventRepository eventRepository;
  private LocalDemoInvitationRepository invitationRepository;
  private InvitationService invitationService;
  private WorkstreamService service;
  private TrackingWorkstreamAgentRuntimeTestAdapter trackingRuntimeInvoker;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    invitationRepository = new LocalDemoInvitationRepository();
    var resolver = new AuthContextResolver(identityRepository);
    var attentionRepository = new LocalDemoAttentionRepository();
    var attentionService = new AttentionService(attentionRepository, resolver, Clock.systemUTC());
    var attentionProducerService = new AttentionProducerService(attentionRepository, identityRepository, Clock.systemUTC());
    eventRepository = new LocalDemoWorkstreamEventRepository();
    var workstreamEventConsumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, attentionProducerService, Clock.systemUTC());
    var workstreamEventPublisher = new WorkstreamEventPublisher(eventRepository, workstreamEventConsumer, Clock.systemUTC());
    var meService = new MeService(resolver, new MyAccountService(resolver, attentionService));
    var userAdminService = new UserAdminService(identityRepository, Clock.systemUTC());
    invitationService = new InvitationService(identityRepository, invitationRepository, Clock.systemUTC(), attentionProducerService, workstreamEventPublisher);
    agentRepository = new LocalDemoAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(agentRepository, Clock.systemUTC()).importStarterDefaults("tenant-1", "bootstrap", "corr-agent-seed");
    var agentRuntimeService = new AgentRuntimeService(agentRepository, resolver, Clock.systemUTC(), request -> new ModelProviderClient.ModelProviderResponse("## " + request.functionalAgentId() + " model response\n\nProvider-backed test markdown.", "test-fake-provider", "test-fake-model", "fake-response-id", "stop", "unit-test fake model invocation"), new LocalDemoAgentRuntimeTraceSink());
    trackingRuntimeInvoker = new TrackingWorkstreamAgentRuntimeTestAdapter(agentRuntimeService);
    var workstreamLogRepository = new LocalDemoWorkstreamLogRepository();
    var notificationService = new NotificationService(new LocalDemoNotificationRepository(), resolver, Clock.systemUTC());
    service = new WorkstreamService(meService, resolver, new UserDirectoryView(userAdminService), new InvitationView(invitationService), userAdminService, invitationService, agentRepository, agentRuntimeService, trackingRuntimeInvoker, workstreamLogRepository, new LocalDemoAccessReviewTaskRepository(), new LocalDemoAuditTraceRepository(agentRuntimeService, workstreamLogRepository), new LocalDemoGovernancePolicyRepository(), attentionService, attentionProducerService, workstreamEventPublisher, eventRepository, new FailClosedAccessReviewAutonomousAgentRuntime(), notificationService);

    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
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
    StarterSecurityComponents.bindTestIdentityRepository(identityRepository);
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
    assertTrue(bootstrap.functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-user-admin") && agent.availability().equals("visible")));
    assertEquals(1, bootstrap.items().size());
    assertEquals(1, bootstrap.surfaces().size());
    assertEquals("surface-my-account-dashboard", bootstrap.surfaces().get(0).surfaceId());
    assertEquals("agent-my-account", bootstrap.surfaces().get(0).ownerFunctionalAgentId());
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
        "action-invite-user", "action-invite-user", "USERADMIN_SEND_INVITATION", "USERADMIN_SEND_INVITATION", null, null, "membership-admin", "surface-user-admin-dashboard", "corr-invite"));

    assertEquals("validation-error", missingKey.status());

    var mismatch = assertThrows(AuthorizationException.class, () -> service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-user-list", "action-display-user-list", "secure-tenant-user-foundation", "secure-tenant-user-foundation", null, null, "membership-other", "surface-user-admin-dashboard", "corr-forbidden")));
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
    assertTrue(dashboard.toString().contains("action-invite-user"));
    assertTrue(dashboard.toString().contains("blocked_provider_or_runtime"));
    assertTrue(dashboard.traceIds().stream().anyMatch(trace -> trace.contains("trace-surface-user-admin-tenant-dashboard")));

    var users = service.surface(identity(), "membership-admin", "surface-user-admin-users", "corr-useradmin-users");
    assertEquals("list-search", users.surfaceType());
    assertEquals("user_admin.users.v1", users.data().get("surfaceContract"));
    assertTrue(users.toString().contains("active-user"));
    assertFalse(users.toString().contains("invite-token"));
    assertFalse(users.toString().contains("tokenHash"));
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
        "action-user-admin-show-users", "user-admin.show-users", "search-user-directory", "USERADMIN_LIST_MEMBERS", null, null, "membership-admin", tenantDashboard.surfaceId(), "corr-tree-users"));
    assertEquals("accepted", users.status());
    assertEquals("surface-user-admin-users", users.resultSurface().surfaceId());
    assertEquals("corr-tree-users", users.resultSurface().correlationId());
    assertTrue(users.resultSurface().traceIds().contains("trace-surface-user-admin-users"));
    assertTrue(users.resultSurface().toString().contains("action-display-user-detail"));
    assertBrowserPayloadSafe(users.resultSurface());

    var detail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-user-detail", "action-display-user-detail", "USERADMIN_LIST_MEMBERS", "USERADMIN_LIST_MEMBERS", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", users.resultSurface().surfaceId(), "corr-tree-user-detail"));
    assertEquals("surface-user-admin-user-detail", detail.resultSurface().surfaceId());
    assertEquals("corr-tree-user-detail", detail.resultSurface().correlationId());
    assertTrue(detail.resultSurface().toString().contains("branchRootSurfaceId=surface-user-admin-users"));
    assertTrue(detail.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-users"));
    assertBrowserPayloadSafe(detail.resultSurface());

    var returnedUsers = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-users", "user-admin.show-users", "search-user-directory", "USERADMIN_LIST_MEMBERS", null, null, "membership-admin", detail.resultSurface().surfaceId(), "corr-tree-users-return"));
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
    assertBrowserPayloadSafe(organizationDetail.resultSurface());

    var returnedOrganizations = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-organizations", "user-admin.show-organizations", "manage-organizations", "saas_owner.organization.list", null, null, "membership-owner", organizationDetail.resultSurface().surfaceId(), "corr-tree-orgs-return"));
    assertEquals("surface-user-admin-organization-directory", returnedOrganizations.resultSurface().surfaceId());
    assertEquals("corr-tree-orgs-return", returnedOrganizations.resultSurface().correlationId());
  }

  @Test
  void userAdminOrganizationDeepLinkDenialUsesSafeSystemMessageForHiddenTargets() {
    var denied = service.runShellRequest(memberIdentity(), "membership-member", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "deep_link", "Open Organization Directory", null, "agent-user-admin", "surface-user-admin-organization-directory", null, "agent-my-account", null, null, "current_workstream", "corr-member-org-deeplink", "membership-member"));

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

    var activeDetail = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-organization-read", "action-organization-read", "saas_owner.organization.read", "saas_owner.organization.read", Map.of("organizationId", "tenant-starter"), null, "membership-owner", "surface-user-admin-organization-directory", "corr-read-active"));
    assertEquals("surface-user-admin-organization-detail", activeDetail.resultSurface().surfaceId());
    assertTrue(activeDetail.resultSurface().toString().contains("suspend"));
    assertTrue(activeDetail.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-organizations"));

    var suspend = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-open-organization-suspend", "action-open-organization-suspend", "saas_owner.tenant.manage", "saas_owner.tenant.manage", Map.of("organizationId", "tenant-starter"), null, "membership-owner", activeDetail.resultSurface().surfaceId(), "corr-open-suspend"));
    assertEquals("accepted", suspend.status());
    assertEquals("surface-user-admin-organization-suspend-confirmation", suspend.resultSurface().surfaceId());
    assertTrue(suspend.resultSurface().toString().contains("tenant-starter"));
    assertTrue(suspend.resultSurface().toString().contains("Back to organizations"));

    var suspendedDetail = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-organization-read", "action-organization-read", "saas_owner.organization.read", "saas_owner.organization.read", Map.of("organizationId", "tenant-suspended"), null, "membership-owner", "surface-user-admin-organization-directory", "corr-read-suspended"));
    assertEquals("surface-user-admin-organization-detail", suspendedDetail.resultSurface().surfaceId());
    assertTrue(suspendedDetail.resultSurface().toString().contains("reactivate"));

    var reactivate = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-open-organization-reactivate", "action-open-organization-reactivate", "saas_owner.tenant.manage", "saas_owner.tenant.manage", Map.of("organizationId", "tenant-suspended"), null, "membership-owner", suspendedDetail.resultSurface().surfaceId(), "corr-open-reactivate"));
    assertEquals("accepted", reactivate.status());
    assertEquals("surface-user-admin-organization-reactivate-confirmation", reactivate.resultSurface().surfaceId());
    assertTrue(reactivate.resultSurface().toString().contains("tenant-suspended"));

    var wrongState = service.runAction(ownerIdentity(), "membership-owner", new WorkstreamService.CapabilityActionRequest(
        "action-open-organization-reactivate", "action-open-organization-reactivate", "saas_owner.tenant.manage", "saas_owner.tenant.manage", Map.of("organizationId", "tenant-starter"), null, "membership-owner", activeDetail.resultSurface().surfaceId(), "corr-wrong-reactivate"));
    assertEquals("denied", wrongState.status());
    assertEquals("system_message", wrongState.resultSurface().surfaceType());
  }

  @Test
  void userAdminUserBranchDescendantsExposeBackendReturnAction() {
    var detail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-user-detail", "action-display-user-detail", "USERADMIN_LIST_MEMBERS", "USERADMIN_LIST_MEMBERS", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", "surface-user-admin-users", "corr-member-branch-detail"));

    assertEquals("surface-user-admin-user-detail", detail.resultSurface().surfaceId());
    assertTrue(detail.resultSurface().toString().contains("branchReturnActionId=action-user-admin-show-users"));
    assertTrue(detail.resultSurface().toString().contains("Back to users"));

    var users = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-user-admin-show-users", "user-admin.show-users", "search-user-directory", "USERADMIN_LIST_MEMBERS", null, null, "membership-admin", detail.resultSurface().surfaceId(), "corr-member-branch-return"));
    assertEquals("accepted", users.status());
    assertEquals("surface-user-admin-users", users.resultSurface().surfaceId());
  }

  @Test
  void userAdminUserRowOpensSelectedUserDetail() {
    var detail = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-display-user-detail", "action-display-user-detail", "USERADMIN_LIST_MEMBERS", "USERADMIN_LIST_MEMBERS", Map.of("accountId", "member@example.test", "membershipId", "membership-member"), null, "membership-admin", "surface-user-admin-users", "corr-member-detail"));

    assertEquals("accepted", detail.status());
    assertEquals("surface-user-admin-user-detail", detail.resultSurface().surfaceId());
    assertTrue(detail.resultSurface().toString().contains("recordLabel=Member User"));
    assertTrue(detail.resultSurface().toString().contains("membershipId=membership-member"));
    assertFalse(detail.resultSurface().toString().contains("recordLabel=Tenant Admin"));
  }

  @Test
  void userAdminDashboardDoesNotCountAcceptedInvitationsAsPendingAttention() {
    var created = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "USERADMIN_SEND_INVITATION", "USERADMIN_SEND_INVITATION", Map.of("email", "accepted.invitee@example.test", "displayName", "Accepted Invitee"), "idem-accepted-invite", "membership-admin", "surface-user-admin-dashboard", "corr-accepted-invite"));
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
        "action-invite-user", "action-invite-user", "USERADMIN_SEND_INVITATION", "USERADMIN_SEND_INVITATION", Map.of("email", "invitee@example.test", "displayName", "Invitee"), "idem-workstream-invite", "membership-admin", "surface-user-admin-dashboard", "corr-workstream-invite"));
    assertEquals("accepted", created.status());
    assertEquals("surface-user-admin-invitation-detail", created.resultSurface().surfaceId());
    assertTrue(created.traceIds().get(0).contains("trace-useradmin-invitation"));
    assertTrue(created.resultSurface().toString().contains("invitee@example.test"));
    assertFalse(created.resultSurface().toString().contains("invite-token"));

    var duplicate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "USERADMIN_SEND_INVITATION", "USERADMIN_SEND_INVITATION", Map.of("email", "changed@example.test"), "idem-workstream-invite", "membership-admin", "surface-user-admin-dashboard", "corr-workstream-invite-replay"));
    assertEquals(created, duplicate);

    var invitationId = identityRepository.auditEvents().stream().filter(event -> event.actionType().equals("INVITATION_CREATE")).findFirst().orElseThrow().targetMembershipId().replace("membership-", "");
    var resent = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-resend-invitation", "action-useradmin-resend-invitation", "USERADMIN_RESEND_INVITATION", "USERADMIN_RESEND_INVITATION", Map.of("invitationId", invitationId, "reason", "delivery repair"), "idem-workstream-resend", "membership-admin", "surface-user-admin-invitation-detail", "corr-workstream-resend"));
    assertEquals("accepted", resent.status());
    assertTrue(resent.resultSurface().toString().contains("delivery"));

    var revoked = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-revoke-invitation", "action-useradmin-revoke-invitation", "USERADMIN_REVOKE_INVITATION", "USERADMIN_REVOKE_INVITATION", Map.of("invitationId", invitationId, "reason", "wrong recipient"), "idem-workstream-revoke", "membership-admin", "surface-user-admin-invitation-detail", "corr-workstream-revoke"));
    assertEquals("accepted", revoked.status());
    assertTrue(revoked.resultSurface().toString().contains("value=revoked"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_RESEND") && event.correlationId().equals("corr-workstream-resend")));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("INVITATION_REVOKE") && event.correlationId().equals("corr-workstream-revoke")));
  }

  @Test
  void userAdminInvitationActionsDenyMissingCapabilityBeforeDataLeakage() {
    var denied = assertThrows(AuthorizationException.class, () -> service.runAction(memberIdentity(), "membership-member", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "USERADMIN_SEND_INVITATION", "USERADMIN_SEND_INVITATION", Map.of("email", "leak@example.test"), "idem-denied-invite", "membership-member", "surface-user-admin-dashboard", "corr-denied-invite")));

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

    var events = service.events(identity(), "membership-admin", "agent-user-admin", null, "corr-event-refresh-read");

    assertTrue(events.stream().anyMatch(event -> event.eventType().equals("projection.refresh.available")
        && event.surfaceId().equals("surface-user-admin-access-review-task")
        && event.patch().toString().contains("workstream.event.delivery.refresh")
        && event.patch().toString().contains("Bounded SSE replay v1")
        && event.patch().toString().contains("idempotencyKey")
        && event.patch().toString().contains("sourceRefs")));
    assertTrue(service.functionalAgents(identity(), "membership-admin", "corr-refresh-rail").stream()
        .filter(agent -> agent.functionalAgentId().equals("agent-user-admin"))
        .findFirst()
        .orElseThrow()
        .attention()
        .source()
        .equals(AttentionService.LIST_RAIL_SUMMARIES_TOOL));
    assertTrue(service.surface(identity(), "membership-admin", "surface-user-admin-dashboard", "corr-refresh-dashboard").toString().contains("attention.list_workstream_items"));

    var hidden = service.events(memberIdentity(), "membership-member", "agent-user-admin", null, "corr-event-refresh-hidden");
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
        .filter(agent -> agent.functionalAgentId().equals("agent-agent-admin"))
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
    assertEquals("agent-agent-admin", catalog.ownerFunctionalAgentId());
    assertTrue(catalog.toString().contains("agent_admin.list_definitions"));
    assertTrue(catalog.toString().contains("providerReadiness"));
    assertTrue(catalog.toString().contains("seedMaterial"));
    assertEquals("AgentDefinition", detail.data().get("recordKind"));
    assertEquals(true, detail.data().get("noDirectMutation"));
    assertEquals("agent_admin.prompt_version.v1", prompt.data().get("surfaceContract"));
    assertTrue(prompt.toString().contains("surface.agent_admin.prompt_versions.v1"));
    assertTrue(prompt.toString().contains("surface.agent_admin.behavior_diff.v1"));
    assertTrue(prompt.toString().contains("redactedPreview"));
    assertEquals(false, prompt.data().get("fullContentAvailableInBrowser"));
    assertEquals("agent_admin.skill_version.v1", skill.data().get("surfaceContract"));
    assertTrue(skill.toString().contains("surface.agent_admin.skill_versions.v1"));
    assertTrue(skill.toString().contains("readSkillRuntime"));
    assertEquals("agent_admin.manifest.v1", manifest.data().get("surfaceContract"));
    assertTrue(manifest.toString().contains("AgentSkillManifest+AgentReferenceManifest"));
    assertTrue(manifest.toString().contains("surface.agent_admin.manifest_detail.v1"));
    assertEquals("agent_admin.tool_boundary.v1", boundary.data().get("surfaceContract"));
    assertTrue(boundary.toString().contains("ToolPermissionBoundary"));
    assertEquals("agent_admin.model_ref.v1", model.data().get("surfaceContract"));
    assertTrue(model.toString().contains("[REDACTED]"));
    assertEquals("agent_admin.seed_material.v1", seed.data().get("surfaceContract"));
    assertTrue(catalog.toString().contains("action-activate-agent-definition"));
    assertTrue(catalog.toString().contains("action-deactivate-agent-definition"));
    assertTrue(catalog.toString().contains("action-import-agent-seed-defaults"));
    assertTrue(catalog.toString().contains("agent.definitions.manage"));
    assertTrue(boundary.toString().contains("surface.agent_admin.tool_boundary.v1"));
    assertTrue(seed.toString().contains("surface.agent_admin.seed_import.v1"));
    for (var surface : List.of(catalog, detail, prompt, skill, manifest, boundary, model, seed)) {
      assertTrue(surface.traceIds().stream().anyMatch(trace -> trace.contains("trace-surface-agent")));
      assertFalse(surface.toString().toLowerCase().contains("api_key="));
      assertFalse(surface.toString().contains("sk-secret"));
      assertFalse(surface.toString().contains("rawProviderCredential="));
    }
  }

  @Test
  void agentAdminDefinitionLifecycleAndSeedImportActionsAreGovernedAndIdempotent() {
    var deactivate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-deactivate-agent-definition", "action-deactivate-agent-definition", "agent.definitions.manage", "agent.definitions.manage", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-deactivate", "membership-admin", "surface-agent-admin-detail", "corr-agent-deactivate"));

    assertEquals("accepted", deactivate.status());
    assertEquals("surface-agent-admin-detail", deactivate.resultSurface().surfaceId());
    assertEquals(AgentLifecycleStatus.DISABLED, agentRepository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());

    var duplicateDeactivate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-deactivate-agent-definition", "action-deactivate-agent-definition", "agent.definitions.manage", "agent.definitions.manage", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-deactivate-2", "membership-admin", "surface-agent-admin-detail", "corr-agent-deactivate-again"));
    assertEquals("no-op", duplicateDeactivate.status());

    var activate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-activate-agent-definition", "action-activate-agent-definition", "agent.definitions.manage", "agent.definitions.manage", Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID), "idem-activate", "membership-admin", "surface-agent-admin-detail", "corr-agent-activate"));
    assertEquals("accepted", activate.status());
    assertEquals(AgentLifecycleStatus.ACTIVE, agentRepository.agentDefinition("tenant-1", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());

    var seedImport = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-import-agent-seed-defaults", "action-import-agent-seed-defaults", "agent_admin.reseed_missing_defaults", "agent_admin.reseed_missing_defaults", null, "idem-seed-import", "membership-admin", "surface-agent-seed-material", "corr-agent-seed-import"));
    assertEquals("no-op", seedImport.status());
    assertTrue(seedImport.message().contains("skipped governed records"));
    assertEquals("surface-agent-seed-material", seedImport.resultSurface().surfaceId());
  }

  @Test
  void agentAdminReadSurfacesDenyMissingCapabilityBeforeArtifactLeakage() {
    var denied = assertThrows(AuthorizationException.class, () -> service.surface(memberIdentity(), "membership-member", "surface-agent-prompt-governance", "corr-member-agent-prompt"));

    assertTrue(denied.reasonCode().contains("missing-capability:agent_admin.get_prompt_version"));
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
        "action-replace-membership-role", "action-replace-membership-role", "secure-tenant-user-foundation", "secure-tenant-user-foundation", null, "idem-1", "membership-admin", "surface-user-admin-user-detail", "corr-role"));

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
        "action-useradmin-preview-role-change", "action-useradmin-preview-role-change", "USERADMIN_PREVIEW_ROLE_CHANGE", "USERADMIN_PREVIEW_ROLE_CHANGE", Map.of("membershipId", "membership-member", "roles", List.of("TENANT_ADMIN"), "reason", "promotion"), null, "membership-admin", "surface-user-admin-user-detail", "corr-useradmin-preview"));
    assertEquals("accepted", preview.status());
    assertTrue(preview.traceIds().get(0).contains("trace-useradmin-preview-role-change"));
    assertEquals("surface-user-admin-role-change-preview", preview.resultSurface().surfaceId());
    assertEquals("user_admin.role_change_preview.v1", preview.resultSurface().data().get("surfaceContract"));
    assertTrue(preview.resultSurface().toString().contains("capabilityDelta"));
    assertTrue(preview.resultSurface().toString().contains("affectedWorkstreams"));

    var changed = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-change-member-roles", "action-useradmin-change-member-roles", "USERADMIN_CHANGE_MEMBER_ROLES", "USERADMIN_CHANGE_MEMBER_ROLES", Map.of("membershipId", "membership-member", "roles", List.of("TENANT_ADMIN"), "reason", "promotion"), "idem-useradmin-change", "membership-admin", "surface-user-admin-user-detail", "corr-useradmin-change"));
    assertEquals("accepted", changed.status());
    assertEquals("surface-user-admin-user-detail", changed.resultSurface().surfaceId());

    var duplicate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-change-member-roles", "action-useradmin-change-member-roles", "USERADMIN_CHANGE_MEMBER_ROLES", "USERADMIN_CHANGE_MEMBER_ROLES", Map.of("membershipId", "membership-member", "roles", List.of("TENANT_EMPLOYEE"), "reason", "ignored replay"), "idem-useradmin-change", "membership-admin", "surface-user-admin-user-detail", "corr-useradmin-change-replay"));
    assertEquals(changed, duplicate);
    assertEquals(List.of(FoundationRole.TENANT_ADMIN), identityRepository.findMembership("membership-member").orElseThrow().roles());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("USERADMIN_CHANGE_MEMBER_ROLES") && event.correlationId().equals("corr-useradmin-change")));
  }

  @Test
  void userAdminStatusActionsDisableReactivateNoOpAndDenyManualSelfDisable() {
    var disabled = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "USERADMIN_UPDATE_MEMBER_STATUS", "USERADMIN_UPDATE_MEMBER_STATUS", Map.of("membershipId", "membership-member", "reason", "leave"), "idem-disable-member", "membership-admin", "surface-user-admin-users", "corr-disable-member"));
    assertEquals("accepted", disabled.status());
    assertEquals("surface-user-admin-user-detail", disabled.resultSurface().surfaceId());
    assertTrue(disabled.traceIds().get(0).contains("trace-useradmin-update-member-status"));
    assertTrue(disabled.resultSurface().toString().contains("value=removed"));

    var duplicate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "USERADMIN_UPDATE_MEMBER_STATUS", "USERADMIN_UPDATE_MEMBER_STATUS", Map.of("membershipId", "membership-member", "reason", "ignored replay"), "idem-disable-member", "membership-admin", "surface-user-admin-users", "corr-disable-member-replay"));
    assertEquals(disabled, duplicate);

    var reactivated = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-reactivate-member", "action-useradmin-reactivate-member", "USERADMIN_UPDATE_MEMBER_STATUS", "USERADMIN_UPDATE_MEMBER_STATUS", Map.of("membershipId", "membership-member", "reason", "return"), "idem-reactivate-member", "membership-admin", "surface-user-admin-users", "corr-reactivate-member"));
    assertEquals("accepted", reactivated.status());
    assertTrue(reactivated.resultSurface().toString().contains("value=active"));

    var disabledFromActiveSelect = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "USERADMIN_UPDATE_MEMBER_STATUS", "USERADMIN_UPDATE_MEMBER_STATUS", Map.of("membershipId", "membership-member", "status", "active", "reason", "deactivate button default"), "idem-disable-member-active-select", "membership-admin", "surface-user-admin-users", "corr-disable-member-active-select"));
    assertEquals("accepted", disabledFromActiveSelect.status());
    assertTrue(disabledFromActiveSelect.resultSurface().toString().contains("value=removed"));
    var reactivatedAgain = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-reactivate-member", "action-useradmin-reactivate-member", "USERADMIN_UPDATE_MEMBER_STATUS", "USERADMIN_UPDATE_MEMBER_STATUS", Map.of("membershipId", "membership-member", "reason", "return again"), "idem-reactivate-member-again", "membership-admin", "surface-user-admin-users", "corr-reactivate-member-again"));
    assertEquals("accepted", reactivatedAgain.status());

    identityRepository.saveAccount(new Account("purge@example.test", null, "purge@example.test", "purge@example.test", AccountStatus.ACTIVE, "UNLINKED"));
    identityRepository.putProfile(new UserProfile("purge@example.test", "purge@example.test", "Purge User", "Purge", "User", null));
    identityRepository.putSettings(new UserSettings("purge@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership("membership-purge", "purge@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.ACTIVE, false, null));
    var deactivatedForRemoval = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "USERADMIN_UPDATE_MEMBER_STATUS", "USERADMIN_UPDATE_MEMBER_STATUS", Map.of("membershipId", "membership-purge", "status", "removed", "reason", "offboard"), "idem-deactivate-before-purge", "membership-admin", "surface-user-admin-users", "corr-deactivate-before-purge"));
    assertEquals("accepted", deactivatedForRemoval.status());
    var removed = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-permanently-remove-user", "action-useradmin-permanently-remove-user", "USERADMIN_UPDATE_MEMBER_STATUS", "USERADMIN_UPDATE_MEMBER_STATUS", Map.of("membershipId", "membership-purge", "reason", "purge"), "idem-permanent-remove", "membership-admin", "surface-user-admin-user-detail", "corr-permanent-remove"));
    assertEquals("accepted", removed.status());
    assertEquals("surface-user-admin-users", removed.resultSurface().surfaceId());
    assertTrue(identityRepository.findMembership("membership-purge").isEmpty());
    assertTrue(identityRepository.findAccountByEmail("purge@example.test").isEmpty());

    var selfDisable = assertThrows(AuthorizationException.class, () -> service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "USERADMIN_UPDATE_MEMBER_STATUS", "USERADMIN_UPDATE_MEMBER_STATUS", Map.of("membershipId", "membership-admin", "reason", "self-disable"), "idem-self-disable", "membership-admin", "surface-user-admin-users", "corr-self-disable")));
    assertEquals("self-disable-denied", selfDisable.reasonCode());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("USERADMIN_UPDATE_MEMBER_STATUS") && event.reasonCode().equals("self-disable-denied")));
  }

  @Test
  void userAdminSupportAccessActionsUseBackendAuthorityAndAuditTraceSurface() {
    var grant = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-grant-support-access", "action-useradmin-grant-support-access", "USERADMIN_SUPPORT_ACCESS_GRANT", "USERADMIN_SUPPORT_ACCESS_GRANT", Map.of("membershipId", "membership-member", "reason", "break glass"), "idem-support-grant", "membership-admin", "surface-user-admin-user-detail", "corr-support-grant"));

    assertEquals("accepted", grant.status());
    assertEquals("surface-user-admin-user-detail", grant.resultSurface().surfaceId());
    assertTrue(grant.resultSurface().toString().contains("supportAccess=true"));
    assertTrue(grant.resultSurface().toString().contains("supportAccess=true"));
    assertTrue(identityRepository.findMembership("membership-member").orElseThrow().supportAccess());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("SUPPORT_ACCESS_GRANT") && event.correlationId().equals("corr-support-grant")));

    var duplicate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-grant-support-access", "action-useradmin-grant-support-access", "USERADMIN_SUPPORT_ACCESS_GRANT", "USERADMIN_SUPPORT_ACCESS_GRANT", Map.of("membershipId", "membership-member", "reason", "ignored replay"), "idem-support-grant", "membership-admin", "surface-user-admin-user-detail", "corr-support-grant-replay"));
    assertEquals(grant, duplicate);

    var revoke = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-revoke-support-access", "action-useradmin-revoke-support-access", "USERADMIN_SUPPORT_ACCESS_REVOKE", "USERADMIN_SUPPORT_ACCESS_REVOKE", Map.of("membershipId", "membership-member", "reason", "done"), "idem-support-revoke", "membership-admin", "surface-user-admin-user-detail", "corr-support-revoke"));
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
  void submitMessageReturnsAuthorizedMarkdownResponseEnvelopeAndPersistsIt() {
    var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-user-admin", "What can I do next?", "corr-message", "idem-message-1"), "corr-header");

    assertEquals("corr-message", response.correlationId());
    assertEquals("idem-message-1", response.idempotencyKey());
    assertEquals("agent-user-admin", response.userItem().functionalAgentId());
    assertEquals("user-request", response.userItem().kind());
    assertNull(response.userItem().title(), "Request acknowledgement surfaces render only the submitted prompt text.");
    assertEquals("ready", response.userItem().status());
    assertEquals("agent-user-admin", response.agentItem().functionalAgentId());
    assertEquals("markdown_response", response.agentItem().kind());
    assertEquals(response.surface().surfaceId(), response.agentItem().surfaceId());
    assertNull(response.agentItem().body(), "Successful model response text belongs in the rendered markdown_response surface, not placeholder item copy");
    assertEquals("markdown_response", response.surface().surfaceType());
    assertEquals("agent-user-admin", response.surface().ownerFunctionalAgentId());
    assertEquals("membership-admin", response.surface().authContext().get("selectedContextId"));
    assertEquals("corr-message", response.surface().correlationId());
    assertFalse(response.surface().traceIds().isEmpty());
    assertEquals("agent-user-admin", response.surface().data().get("producingAgentId"));
    assertEquals(response.agentItem().itemId(), response.surface().data().get("workstreamEntryId"));
    assertTrue(response.surface().data().get("markdown").toString().contains("## agent-user-admin model response"));
    assertEquals(1, trackingRuntimeInvoker.invocationCount(), "Successful markdown_response must be produced through the workstream Akka Agent runtime invoker seam");
    assertEquals("agent-user-admin", trackingRuntimeInvoker.lastRequest().agentDefinitionId());
    assertNotNull(response.surface().data().get("safety"));
    assertNotNull(response.surface().data().get("trace"));

    var persistedItems = service.items(identity(), "membership-admin", "agent-user-admin", "corr-read");
    assertTrue(persistedItems.stream().anyMatch(item -> item.itemId().equals(response.userItem().itemId())));
    assertTrue(persistedItems.stream().anyMatch(item -> item.itemId().equals(response.agentItem().itemId())));
    var persistedSurface = service.surface(identity(), "membership-admin", response.surface().surfaceId(), "corr-read");
    assertEquals(response.surface().surfaceId(), persistedSurface.surfaceId());
    assertEquals("corr-message", persistedSurface.correlationId());
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
    assertEquals("agent-my-account", dashboard.ownerFunctionalAgentId());
    assertEquals("detail-edit", profile.surfaceType());
    assertEquals("detail-edit", settings.surfaceType());
    assertEquals("detail-edit", context.surfaceType());
    assertEquals("surface-my-context", context.surfaceId());
    assertEquals("surface.access.profile.context.v1", accessProfileContext.surfaceId());
    assertEquals("agent-my-account", accessProfileContext.ownerFunctionalAgentId());
    assertTrue(accessProfileContext.toString().contains("core.access.me"));
    assertTrue(accessProfileContext.toString().contains("core.access.context.select"));
    assertTrue(profile.toString().contains("my_account.update_profile_settings"));
    assertTrue(profile.toString().contains("core.profile.update"));
    assertTrue(settings.toString().contains("preferredThemeId"));
    assertTrue(context.toString().contains("/api/me?selectedContextId=membership-admin"));
    assertTrue(context.toString().contains("my_account.view_context"));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-show-my-account-dashboard")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-show-my-context") && action.capabilityId().equals("my_account.view_context")));
    assertTrue(context.actions().stream().anyMatch(action -> action.actionId().equals("action-select-my-context") && action.capabilityId().equals("core.access.context.select")));

    var selectContext = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-select-my-context", "action-select-my-context", "core.access.context.select", "core.access.context.select", null, null, "membership-admin", "surface-my-context", "corr-select-context"));
    assertEquals("accepted", selectContext.status());
    assertEquals("surface.access.profile.context.v1", selectContext.resultSurface().surfaceId());
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
        "action-update-my-settings", "action-update-my-settings", "my_account.update_profile_settings", "my_account.update_profile_settings", Map.of("displayName", "Tenant Admin", "preferredThemeId", "aurora-light"), "idem-my-account-noop", "membership-admin", "surface-my-settings", "corr-my-account-noop"));

    assertEquals("no-op", result.status());
    assertEquals("surface-my-settings", result.resultSurface().surfaceId());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("MY_ACCOUNT_PROFILE_SETTINGS_UPDATE") && event.reasonCode().equals("no-op") && event.correlationId().equals("corr-my-account-noop")));
  }

  @Test
  void myAccountOpenWorkstreamActionReturnsBackendResolvedSurface() {
    var result = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-open-agent-admin", "action-open-agent-admin", "my_account.open_authorized_workstream", "my_account.open_authorized_workstream", null, null, "membership-admin", "surface-my-account-dashboard", "corr-open-agent-admin"));

    assertEquals("accepted", result.status());
    assertEquals("surface-agent-admin-catalog", result.resultSurface().surfaceId());
    assertEquals("agent-agent-admin", result.resultSurface().ownerFunctionalAgentId());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM") && event.correlationId().equals("corr-open-agent-admin")));
  }

  @Test
  void shellRequestsResolveRichSurfacesThroughBackendAndPreserveBootstrapGuard() {
    var bootstrap = service.bootstrap(identity(), "membership-admin", "corr-shell-bootstrap");
    assertEquals("surface-my-account-dashboard", bootstrap.surfaces().get(0).surfaceId());
    assertEquals("surface-my-account-dashboard", bootstrap.items().get(0).surfaceId());

    var show = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show user admin dashboard", null, "agent-user-admin", "surface-user-admin-dashboard", null, "agent-user-admin", null, null, "current_workstream", "corr-shell-show", "membership-admin"));
    assertEquals("accepted", show.status());
    assertEquals("dashboard", show.resultSurface().surfaceType());
    assertEquals("surface-user-admin-tenant-dashboard", show.resultSurface().surfaceId());
    assertEquals("agent-user-admin", show.requestItem().functionalAgentId());
    assertEquals("user-request", show.requestItem().kind());
    assertTrue(show.request().canonicalPrompt().contains("show surface"));

    var refresh = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "refresh_surface", "surface_action", "Refresh User Admin dashboard", null, "agent-user-admin", "surface-user-admin-dashboard", null, "agent-user-admin", "surface-user-admin-dashboard", "action-display-user-list", "current_workstream", "corr-shell-refresh", "membership-admin"));
    assertEquals("accepted", refresh.status());
    assertEquals("surface-user-admin-tenant-dashboard", refresh.resultSurface().surfaceId());

    var openAttention = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "open_attention_item", "my_account_panel", "Open Agent Admin readiness", null, "agent-agent-admin", "surface-agent-admin-catalog", "attention-agent-admin-readiness", "agent-my-account", "surface-my-account-dashboard", "action-open-agent-admin", "authorized_cross_workstream", "corr-shell-attention", "membership-admin"));
    assertEquals("accepted", openAttention.status());
    assertEquals("agent-agent-admin", openAttention.resultSurface().ownerFunctionalAgentId());

    var users = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show users", null, "agent-user-admin", null, null, "agent-user-admin", null, null, "current_workstream", "corr-shell-users", "membership-admin"));
    assertEquals("accepted", users.status());
    assertEquals("surface-user-admin-users", users.resultSurface().surfaceId());
    assertEquals("show users", users.request().canonicalPrompt());

    var notifications = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show notifications", null, "agent-my-account", null, null, "agent-my-account", null, null, "current_workstream", "corr-shell-notifications", "membership-admin"));
    assertEquals("accepted", notifications.status());
    assertEquals("surface-my-account-notification-center", notifications.resultSurface().surfaceId());

    var auditTimeline = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show audit timeline", null, "agent-audit-trace", null, null, "agent-audit-trace", null, null, "current_workstream", "corr-shell-audit-timeline", "membership-admin"));
    assertEquals("accepted", auditTimeline.status());
    assertEquals("surface-audit-trace-timeline", auditTimeline.resultSurface().surfaceId());

    var agentCatalog = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show agent catalog", null, "agent-agent-admin", null, null, "agent-agent-admin", null, null, "current_workstream", "corr-shell-agent-catalog", "membership-admin"));
    assertEquals("accepted", agentCatalog.status());
    assertEquals("surface-agent-admin-catalog", agentCatalog.resultSurface().surfaceId());

    var governancePolicies = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show governance policies", null, "agent-governance-policy", null, null, "agent-governance-policy", null, null, "current_workstream", "corr-shell-governance-policies", "membership-admin"));
    assertEquals("accepted", governancePolicies.status());
    assertEquals("surface-governance-policy-inventory", governancePolicies.resultSurface().surfaceId());
  }

  @Test
  void shellRequestsReturnSafeSystemMessageForHiddenTargets() {
    var denied = service.runShellRequest(memberIdentity(), "membership-member", new WorkstreamService.WorkstreamShellRequest(
        "open_workstream", "deep_link", "Open Agent Admin", null, "agent-agent-admin", null, null, "agent-my-account", null, null, "authorized_cross_workstream", "corr-shell-denied", "membership-member"));

    assertEquals("denied", denied.status());
    assertEquals("system_message", denied.resultSurface().surfaceType());
    assertEquals("TARGET_NOT_FOUND_OR_FORBIDDEN", denied.resultSurface().data().get("code"));
    assertFalse(denied.resultSurface().toString().contains("agent_admin.list_definitions"));

    var deniedAlias = service.runShellRequest(memberIdentity(), "membership-member", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show agent catalog", null, "agent-my-account", null, null, "agent-my-account", null, null, "current_workstream", "corr-shell-denied-alias", "membership-member"));
    assertEquals("denied", deniedAlias.status());
    assertEquals("system_message", deniedAlias.resultSurface().surfaceType());
    assertEquals("TARGET_NOT_FOUND_OR_FORBIDDEN", deniedAlias.resultSurface().data().get("code"));
    assertFalse(deniedAlias.resultSurface().toString().contains("agent_admin.list_definitions"));
  }

  @Test
  void shellRequestsDenyUnknownPromptAliasesWithoutDashboardFallback() {
    var unknown = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show payroll", null, "agent-my-account", null, null, "agent-my-account", null, null, "current_workstream", "corr-shell-unknown-alias", "membership-admin"));

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
    assertEquals("not_found_or_redacted", result.resultSurface().data().get("status"));
    assertFalse(result.resultSurface().toString().contains("agent_admin.list_definitions"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("MY_ACCOUNT_OPEN_AUTHORIZED_WORKSTREAM") && event.result().name().equals("DENIED") && event.correlationId().equals("corr-member-open-agent-admin")));
  }

  @Test
  void regularMemberCanOpenOnlyMyAccountFromSignedInUserTile() {
    var response = service.submitMessage(new WorkosIdentity("workos-member", "member@example.test", "Member User"), "membership-member", new WorkstreamService.WorkstreamMessageRequest(
        "membership-member", "agent-my-account", "What can I do in My Account?", "corr-member-my-account", "idem-member-my-account"), "corr-header");

    assertEquals("agent-my-account", response.surface().ownerFunctionalAgentId());
    assertEquals("markdown_response", response.surface().surfaceType());
    assertEquals("agent-my-account", response.surface().data().get("producingAgentId"));

    var denied = assertThrows(AuthorizationException.class, () -> service.submitMessage(new WorkosIdentity("workos-member", "member@example.test", "Member User"), "membership-member", new WorkstreamService.WorkstreamMessageRequest(
        "membership-member", "agent-user-admin", "Can I administer users?", "corr-member-user-admin", "idem-member-user-admin"), "corr-header"));
    assertEquals("FUNCTIONAL_AGENT_FORBIDDEN", denied.reasonCode());
  }

  @Test
  void submitMessageSupportsEveryFiveCoreV0FunctionalAgent() {
    for (var agentId : List.of("agent-my-account", "agent-user-admin", "agent-agent-admin", "agent-audit-trace", "agent-governance-policy")) {
      var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
          "membership-admin", agentId, "Show five core v0 readiness", "corr-" + agentId, "idem-" + agentId), "corr-header");

      assertEquals(agentId, response.surface().ownerFunctionalAgentId());
      assertEquals("markdown_response", response.surface().surfaceType());
      assertEquals(agentId, response.surface().data().get("producingAgentId"));
      assertTrue(response.surface().data().get("markdown").toString().contains(agentId + " model response"));
      if (agentId.equals("agent-agent-admin")) {
        assertEquals("agent-agent-admin", trackingRuntimeInvoker.lastRequest().agentDefinitionId());
      }
    }
  }

  @Test
  void submitMessageIsIdempotentForDuplicateClientKeys() {
    var first = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-user-admin", "What can I do next?", "corr-idem-first", "idem-duplicate-message"), "corr-header");
    var duplicate = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-user-admin", "Changed prompt should not append", "corr-idem-second", "idem-duplicate-message"), "corr-header");

    assertEquals(first.userItem().itemId(), duplicate.userItem().itemId());
    assertEquals(first.agentItem().itemId(), duplicate.agentItem().itemId());
    assertEquals(first.surface().surfaceId(), duplicate.surface().surfaceId());
    var persistedItems = service.items(identity(), "membership-admin", "agent-user-admin", "corr-read-idem").stream()
        .filter(item -> item.itemId().equals(first.userItem().itemId()) || item.itemId().equals(first.agentItem().itemId()))
        .toList();
    assertEquals(2, persistedItems.size());
  }

  @Test
  void submitMessageRequiresSelectedContextMatch() {
    var mismatch = assertThrows(AuthorizationException.class, () -> service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-other", "agent-user-admin", "Hello", "corr-message", "idem-message-2"), "corr-header"));

    assertEquals("CONTEXT_FORBIDDEN", mismatch.reasonCode());
  }

  @Test
  void submitMessageRejectsDeniedFunctionalAgentBeforeModelResponseAndPersistsDenial() {
    var denied = assertThrows(AuthorizationException.class, () -> service.submitMessage(memberIdentity(), "membership-member", new WorkstreamService.WorkstreamMessageRequest(
        "membership-member", "agent-user-admin", "Invite someone", "corr-denied", "idem-message-3"), "corr-header"));

    assertEquals("FUNCTIONAL_AGENT_FORBIDDEN", denied.reasonCode());
    var deniedItems = service.items(memberIdentity(), "membership-member", "agent-user-admin", "corr-denied-read");
    assertTrue(deniedItems.stream().anyMatch(item -> item.kind().equals("system_message") && item.status().equals("blocked") && item.body().contains("FUNCTIONAL_AGENT_FORBIDDEN")));
  }

  @Test
  void submitMessagePropagatesFallbackCorrelationWhenBodyOmitsIt() {
    var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-audit-trace", "Show trace status", null, "idem-message-4"), "corr-header");

    assertEquals("corr-header", response.correlationId());
    assertEquals("corr-header", response.surface().correlationId());
    assertFalse(response.surface().traceIds().isEmpty());
  }

  @Test
  void auditTraceActionsReturnScopedSearchDetailTimelineFailureAndGuidanceSurfaces() throws Exception {
    service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-audit-trace", "Explain current trace status", "corr-audit-runtime", "idem-audit-runtime"), "corr-header");

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
    assertTrue(guide.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-audit-trace-append-investigation-note")));

    var note = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-append-investigation-note", "action-audit-trace-append-investigation-note", "audit.trace.investigation_note.append", "audit.trace.investigation_note.append", Map.of("traceId", "trace-provider-blocked-002", "note", "Follow up without sk-test-secret or bearer hidden-token"), "idem-audit-note", "membership-admin", "surface-audit-trace-investigation-guide", "corr-audit-note"));
    assertEquals("recorded", note.status());
    assertEquals("surface-audit-trace-investigation-note", note.resultSurface().surfaceId());
    assertEquals("audit.trace.investigationNote.v1", note.resultSurface().data().get("surfaceContract"));
    assertTrue(note.resultSurface().toString().contains("do not mutate source traces"));
    assertFalse(note.resultSurface().toString().contains("sk-test-secret"));
    assertFalse(note.resultSurface().toString().contains("hidden-token"));
    assertTrue(service.items(identity(), "membership-admin", "agent-audit-trace", "corr-audit-note-read").stream().anyMatch(item -> "surface-audit-trace-investigation-note".equals(item.surfaceId()) && "recorded".equals(item.status())));
  }

  @Test
  void auditTraceSummaryWorkerFailsClosedUntilRealAutonomousRuntimeExists() {
    var summary = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-audit-trace-summary-task-start", "action-audit-trace-summary-task-start", "audit.trace.summaryTask.start", "audit.trace.summary_task.start", Map.of("schedule", "weekly-owner-digest"), "idem-audit-summary", "membership-admin", "surface-audit-trace-investigation-guide", "corr-audit-summary"));

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
  }

  @Test
  void governancePolicyBackendActionsExposeReadProposalSimulationApprovalAndBlockedRuntimeSurfaces() {
    var dashboard = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-dashboard", "action-governance-policy-dashboard", "governance.policy.read", "governance.policy.read", null, null, "membership-admin", "surface-governance-policy-dashboard", "corr-gov-dashboard"));
    assertEquals("accepted", dashboard.status());
    assertEquals("surface-governance-policy-dashboard", dashboard.resultSurface().surfaceId());
    assertTrue(dashboard.resultSurface().toString().contains("governance.policy.activate"));

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

    var simulation = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-simulate", "action-governance-policy-simulate", "governance.policy.simulate", "governance.policy.simulate", Map.of("proposalId", proposalId), null, "membership-admin", "surface-governance-policy-proposal", "corr-gov-sim"));
    assertEquals("accepted", simulation.status());
    assertTrue(simulation.resultSurface().toString().contains("model cannot self-approve"));
    assertTrue(simulation.resultSurface().toString().contains("advisory deterministic simulation"));

    var decision = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-decide", "action-governance-policy-decide", "governance.proposals.review", "governance.policy.approve", Map.of("proposalId", proposalId, "decision", "approve", "rationale", "bounded starter proof"), "idem-gov-decision", "membership-admin", "surface-governance-policy-simulation", "corr-gov-decision"));
    assertEquals("accepted", decision.status());
    assertEquals("surface-governance-policy-decision", decision.resultSurface().surfaceId());
    assertTrue(decision.resultSurface().toString().contains("rollback metadata"));
    assertTrue(decision.resultSurface().toString().contains("governance.proposals.review"));
    assertTrue(decision.resultSurface().actions().toString().contains("action-governance-policy-outcome-note"));

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
  void governancePolicyActionsDenyMembersAndCrossTenantInput() {
    var denied = assertThrows(AuthorizationException.class, () -> service.runAction(memberIdentity(), "membership-member", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-list", "action-governance-policy-list", "governance.policy.read", "governance.policy.read", null, null, "membership-member", "surface-governance-policy-dashboard", "corr-gov-member")));
    assertEquals("CAPABILITY_FORBIDDEN", denied.reasonCode());

    var crossTenant = assertThrows(AuthorizationException.class, () -> service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-simulate", "action-governance-policy-simulate", "governance.policy.simulate", "governance.policy.simulate", Map.of("tenantId", "tenant-other"), null, "membership-admin", "surface-governance-policy-proposal", "corr-gov-cross")));
    assertEquals("GOVERNANCE_POLICY_TENANT_FORBIDDEN", crossTenant.reasonCode());
  }

  @Test
  void governancePolicyMessageUsesGovernanceCapabilityForRuntimeTraces() {
    var response = service.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-governance-policy", "Explain policy approval gates", "corr-governance-message", "idem-governance-message"), "corr-header");

    assertEquals("agent-governance-policy", response.surface().ownerFunctionalAgentId());
    assertEquals("markdown_response", response.surface().surfaceType());
    assertEquals("agent-governance-policy", trackingRuntimeInvoker.lastRequest().agentDefinitionId());
    assertTrue(service.bootstrap(identity(), "membership-admin", "corr-governance-trace-read").functionalAgents().stream()
        .filter(agent -> agent.functionalAgentId().equals("agent-governance-policy"))
        .findFirst()
        .orElseThrow()
        .requiredCapabilityIds()
        .contains("governance.policy.read"));
  }

  @Test
  void auditTraceMessageFailsClosedWhenRuntimeProviderBoundaryIsMissing() {
    var invitationRepository = new LocalDemoInvitationRepository();
    var resolver = new AuthContextResolver(identityRepository);
    var attentionService = new AttentionService(new LocalDemoAttentionRepository(), resolver, Clock.systemUTC());
    var meService = new MeService(resolver, new MyAccountService(resolver, attentionService));
    var userAdminService = new UserAdminService(identityRepository, Clock.systemUTC());
    var invitationService = new InvitationService(identityRepository, invitationRepository, Clock.systemUTC());
    var agentRepository = new LocalDemoAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(agentRepository, Clock.systemUTC()).importStarterDefaults("tenant-1", "bootstrap", "corr-agent-seed-failclosed");
    var agentRuntimeService = new AgentRuntimeService(agentRepository, resolver, Clock.systemUTC(), request -> {
      throw new ModelProviderClient.ModelProviderException("model-provider-config-missing", "Model provider configuration is missing required backend variable OPENAI_API_KEY.");
    }, new LocalDemoAgentRuntimeTraceSink());
    var failClosedWorkstreamLogRepository = new LocalDemoWorkstreamLogRepository();
    var notificationService = new NotificationService(new LocalDemoNotificationRepository(), resolver, Clock.systemUTC());
    var failClosedService = new WorkstreamService(meService, resolver, new UserDirectoryView(userAdminService), new InvitationView(invitationService), userAdminService, invitationService, agentRepository, agentRuntimeService, agentRuntimeService::invokeWorkstreamAgent, failClosedWorkstreamLogRepository, new LocalDemoAccessReviewTaskRepository(), new LocalDemoAuditTraceRepository(agentRuntimeService, failClosedWorkstreamLogRepository), new LocalDemoGovernancePolicyRepository(), attentionService, null, null, null, new FailClosedAccessReviewAutonomousAgentRuntime(), notificationService);

    var response = failClosedService.submitMessage(identity(), "membership-admin", new WorkstreamService.WorkstreamMessageRequest(
        "membership-admin", "agent-audit-trace", "Explain this provider failure", "corr-audit-failclosed", "idem-audit-failclosed"), "corr-header");

    assertEquals("blocked", response.agentItem().status());
    assertEquals("system_message", response.agentItem().kind());
    assertEquals("system_message", response.surface().surfaceType());
    assertEquals("blocked_provider_or_runtime", response.surface().data().get("status"));
    assertTrue(response.surface().data().get("message").toString().contains("blocked before a response was produced"));
    assertTrue(response.surface().toString().contains("model-provider-config-missing"));
    assertFalse(response.surface().toString().contains("should not be used"));
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
    private AgentRuntimeService.RuntimeInvocationRequest lastRequest;

    private TrackingWorkstreamAgentRuntimeTestAdapter(AgentRuntimeService delegate) {
      this.delegate = delegate;
    }

    @Override
    public AgentRuntimeService.RuntimeInvocationResult invokeWorkstreamAgent(AgentRuntimeService.RuntimeInvocationRequest request) {
      invocationCount.incrementAndGet();
      lastRequest = request;
      return delegate.invokeWorkstreamAgent(request);
    }

    private int invocationCount() {
      return invocationCount.get();
    }

    private AgentRuntimeService.RuntimeInvocationRequest lastRequest() {
      return lastRequest;
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
}

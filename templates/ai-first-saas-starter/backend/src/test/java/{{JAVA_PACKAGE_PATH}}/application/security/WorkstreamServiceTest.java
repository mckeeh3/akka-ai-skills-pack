package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorSeedLoader;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentRuntimeService;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.LocalDemoAgentBehaviorRepository;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.LocalDemoAgentRuntimeTraceSink;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.ModelProviderClient;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.WorkstreamAgentRuntimeInvoker;
import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkstreamServiceTest {
  private LocalDemoIdentityRepository identityRepository;
  private LocalDemoAgentBehaviorRepository agentRepository;
  private LocalDemoWorkstreamEventRepository eventRepository;
  private WorkstreamService service;
  private TrackingWorkstreamAgentRuntimeTestAdapter trackingRuntimeInvoker;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    var invitationRepository = new LocalDemoInvitationRepository();
    var resolver = new AuthContextResolver(identityRepository);
    var attentionRepository = new LocalDemoAttentionRepository();
    var attentionService = new AttentionService(attentionRepository, resolver, Clock.systemUTC());
    var attentionProducerService = new AttentionProducerService(attentionRepository, identityRepository, Clock.systemUTC());
    eventRepository = new LocalDemoWorkstreamEventRepository();
    var workstreamEventConsumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, attentionProducerService, Clock.systemUTC());
    var workstreamEventPublisher = new WorkstreamEventPublisher(eventRepository, workstreamEventConsumer, Clock.systemUTC());
    var meService = new MeService(resolver, new MyAccountService(resolver, attentionService));
    var userAdminService = new UserAdminService(identityRepository, Clock.systemUTC());
    var invitationService = new InvitationService(identityRepository, invitationRepository, Clock.systemUTC(), attentionProducerService, workstreamEventPublisher);
    agentRepository = new LocalDemoAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(agentRepository, Clock.systemUTC()).importStarterDefaults("tenant-1", "bootstrap", "corr-agent-seed");
    var agentRuntimeService = new AgentRuntimeService(agentRepository, resolver, Clock.systemUTC(), request -> new ModelProviderClient.ModelProviderResponse("## " + request.functionalAgentId() + " model response\n\nProvider-backed test markdown.", "test-fake-provider", "test-fake-model", "fake-response-id", "stop", "unit-test fake model invocation"), new LocalDemoAgentRuntimeTraceSink());
    trackingRuntimeInvoker = new TrackingWorkstreamAgentRuntimeTestAdapter(agentRuntimeService);
    var workstreamLogRepository = new LocalDemoWorkstreamLogRepository();
    var notificationService = new NotificationService(new LocalDemoNotificationRepository(), resolver, Clock.systemUTC());
    service = new WorkstreamService(meService, resolver, new UserDirectoryView(userAdminService), new InvitationView(invitationService), userAdminService, invitationService, agentRepository, agentRuntimeService, trackingRuntimeInvoker, workstreamLogRepository, new LocalDemoAccessReviewTaskRepository(), new LocalDemoAuditTraceRepository(agentRuntimeService, workstreamLogRepository), new LocalDemoGovernancePolicyRepository(), attentionService, attentionProducerService, workstreamEventPublisher, eventRepository, new FailClosedAccessReviewAutonomousAgentRuntime(), notificationService);

    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.saveAccount(new Account("admin@example.test", null, "admin@example.test", "admin@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("admin@example.test", "admin@example.test", "Tenant Admin", "Tenant", "Admin", null));
    identityRepository.putSettings(new UserSettings("admin@example.test", UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership("membership-admin", "admin@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR), MembershipStatus.ACTIVE, false, null));
    identityRepository.saveAccount(new Account("member@example.test", null, "member@example.test", "member@example.test", AccountStatus.ACTIVE, "UNLINKED"));
    identityRepository.putProfile(new UserProfile("member@example.test", "member@example.test", "Member User", "Member", "User", null));
    identityRepository.putSettings(new UserSettings("member@example.test", UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership("membership-member", "member@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_EMPLOYEE), MembershipStatus.ACTIVE, false, null));
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

    var componentsText = Files.readString(findSource("StarterSecurityComponents.java"));
    assertTrue(componentsText.contains("AkkaAuditTraceRepository"), "Normal Audit/Trace runtime must bind the Akka-backed trace repository");
    assertTrue(componentsText.contains("AkkaWorkstreamLogRepository"), "Normal workstream runtime must bind the Akka-backed log repository");
  }

  @Test
  void bootstrapReturnsFiveCoreV0MarkdownSurfacesWithoutSecrets() {
    var bootstrap = service.bootstrap(identity(), null, "corr-bootstrap");

    assertEquals("membership-admin", bootstrap.me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-user-admin") && agent.availability().equals("visible")));
    assertEquals(5, bootstrap.items().size());
    assertEquals(5, bootstrap.surfaces().size());
    for (var surfaceId : List.of("surface-v0-my-account-markdown", "surface-v0-user-admin-markdown", "surface-v0-agent-admin-markdown", "surface-v0-audit-trace-markdown", "surface-v0-governance-policy-markdown")) {
      assertTrue(bootstrap.items().stream().anyMatch(item -> surfaceId.equals(item.surfaceId()) && item.kind().equals("markdown_response")));
      assertTrue(bootstrap.surfaces().stream().anyMatch(surface -> surfaceId.equals(surface.surfaceId()) && surface.surfaceType().equals("markdown_response")));
    }
    assertFalse(bootstrap.surfaces().stream().anyMatch(surface -> surface.surfaceId().equals("surface-user-admin-dashboard")));
    assertFalse(bootstrap.surfaces().stream().anyMatch(surface -> surface.surfaceType().equals("dashboard") || surface.surfaceType().equals("list-search") || surface.surfaceType().equals("governance-diff") || surface.surfaceType().equals("workflow-status")));
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
  void userAdminDashboardAndInvitationPanelAreBackendDerivedAndScoped() {
    var dashboard = service.surface(identity(), "membership-admin", "surface-user-admin-dashboard", "corr-useradmin-dashboard");

    assertEquals("surface-user-admin-dashboard", dashboard.surfaceId());
    assertEquals("dashboard", dashboard.surfaceType());
    assertEquals("user_admin.dashboard.v1", dashboard.data().get("surfaceContract"));
    assertTrue(dashboard.toString().contains("USERADMIN_VIEW_OVERVIEW"));
    assertTrue(dashboard.toString().contains("USERADMIN_LIST_INVITATIONS"));
    assertTrue(dashboard.toString().contains("USERADMIN_SEND_INVITATION"));
    assertTrue(dashboard.toString().contains("blocked_provider_or_runtime"));
    assertTrue(dashboard.traceIds().stream().anyMatch(trace -> trace.contains("trace-surface-user-admin-dashboard")));

    var invitationPanel = service.surface(identity(), "membership-admin", "surface-user-admin-invitation-panel", "corr-useradmin-invitations");
    assertEquals("list-search", invitationPanel.surfaceType());
    assertEquals("user_admin.invitation_panel.v1", invitationPanel.data().get("surfaceContract"));
    assertTrue(invitationPanel.toString().contains("system_message"));
    assertFalse(invitationPanel.toString().contains("invite-token"));
    assertFalse(invitationPanel.toString().contains("tokenHash"));
  }

  @Test
  void userAdminInvitationActionsCreateResendRevokeAndReplayThroughDeterministicServices() {
    var created = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "USERADMIN_SEND_INVITATION", "USERADMIN_SEND_INVITATION", Map.of("email", "invitee@example.test", "displayName", "Invitee"), "idem-workstream-invite", "membership-admin", "surface-user-admin-dashboard", "corr-workstream-invite"));
    assertEquals("accepted", created.status());
    assertEquals("surface-user-admin-invitation-panel", created.resultSurface().surfaceId());
    assertTrue(created.traceIds().get(0).contains("trace-useradmin-invitation"));
    assertTrue(created.resultSurface().toString().contains("invitee@example.test"));
    assertFalse(created.resultSurface().toString().contains("invite-token"));

    var duplicate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-invite-user", "action-invite-user", "USERADMIN_SEND_INVITATION", "USERADMIN_SEND_INVITATION", Map.of("email", "changed@example.test"), "idem-workstream-invite", "membership-admin", "surface-user-admin-dashboard", "corr-workstream-invite-replay"));
    assertEquals(created, duplicate);

    var invitationId = created.resultSurface().data().get("rows").toString().contains("invitee@example.test")
        ? identityRepository.auditEvents().stream().filter(event -> event.actionType().equals("INVITATION_CREATE")).findFirst().orElseThrow().targetMembershipId().replace("membership-", "")
        : "missing";
    var resent = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-resend-invitation", "action-useradmin-resend-invitation", "USERADMIN_RESEND_INVITATION", "USERADMIN_RESEND_INVITATION", Map.of("invitationId", invitationId, "reason", "delivery repair"), "idem-workstream-resend", "membership-admin", "surface-user-admin-invitation-panel", "corr-workstream-resend"));
    assertEquals("accepted", resent.status());
    assertTrue(resent.resultSurface().toString().contains("resendCount=1"));

    var revoked = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-revoke-invitation", "action-useradmin-revoke-invitation", "USERADMIN_REVOKE_INVITATION", "USERADMIN_REVOKE_INVITATION", Map.of("invitationId", invitationId, "reason", "wrong recipient"), "idem-workstream-revoke", "membership-admin", "surface-user-admin-invitation-panel", "corr-workstream-revoke"));
    assertEquals("accepted", revoked.status());
    assertTrue(revoked.resultSurface().toString().contains("status=revoked"));
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
  void realtimeEventsAreScopedAndResumeWithStaleFallback() {
    var events = service.events(identity(), "membership-admin", null, null, "corr-events");

    assertTrue(events.stream().anyMatch(event -> event.eventType().equals("surface.stale") && event.surfaceId().equals("surface-v0-user-admin-markdown") && event.surfaceType().equals("markdown_response")));
    assertTrue(events.stream().allMatch(event -> event.tenantId().equals("tenant-1")));

    var resumed = service.events(identity(), "membership-admin", null, "evt-audit-appended-002", "corr-events");
    assertTrue(resumed.stream().noneMatch(event -> event.eventId().equals("evt-audit-appended-002")));
    assertTrue(resumed.stream().anyMatch(event -> event.eventId().equals("evt-user-admin-stale-003")));

    var staleFallback = service.events(identity(), "membership-admin", null, "evt-missing", "corr-events");
    assertEquals("surface.stale", staleFallback.get(0).eventType());
  }

  @Test
  void eventBackedProjectionRefreshEventsAreBackendDerivedAndCapabilityScoped() {
    var started = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-start-access-review", "action-useradmin-start-access-review", "user_admin.access_review.start", "user_admin.access_review.start", Map.of("scope", "tenant"), "idem-event-refresh", "membership-admin", "surface-user-admin-dashboard", "corr-event-refresh"));
    assertEquals("blocked-runtime", started.status());
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("workflow.access_review.blocked_provider_or_runtime")));

    var events = service.events(identity(), "membership-admin", "agent-user-admin", null, "corr-event-refresh-read");

    assertTrue(events.stream().anyMatch(event -> event.eventType().equals("projection.refresh.available")
        && event.surfaceId().equals("surface-user-admin-access-review")
        && event.patch().toString().contains("workstream.event.delivery.refresh")
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
    assertEquals("surface-agent-prompt-governance", promptProposal.resultSurface().surfaceId());

    var testRun = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-test-agent-prompt", "action-test-agent-prompt", "agent_admin.draft_behavior_change", "agent_admin.draft_behavior_change", null, "idem-test", "membership-admin", "surface-agent-test-console", "corr-test-ui"));
    assertEquals("accepted", testRun.status());
    assertEquals("surface-agent-test-console", testRun.resultSurface().surfaceId());

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
    assertTrue(prompt.toString().contains("redactedPreview"));
    assertEquals(false, prompt.data().get("fullContentAvailableInBrowser"));
    assertEquals("agent_admin.manifest.v1", manifest.data().get("surfaceContract"));
    assertTrue(manifest.toString().contains("AgentSkillManifest+AgentReferenceManifest"));
    assertEquals("agent_admin.tool_boundary.v1", boundary.data().get("surfaceContract"));
    assertTrue(boundary.toString().contains("ToolPermissionBoundary"));
    assertEquals("agent_admin.model_ref.v1", model.data().get("surfaceContract"));
    assertTrue(model.toString().contains("[REDACTED]"));
    assertEquals("agent_admin.seed_material.v1", seed.data().get("surfaceContract"));
    for (var surface : List.of(catalog, detail, prompt, manifest, boundary, model, seed)) {
      assertTrue(surface.traceIds().stream().anyMatch(trace -> trace.contains("trace-surface-agent")));
      assertFalse(surface.toString().toLowerCase().contains("api_key="));
      assertFalse(surface.toString().contains("sk-secret"));
      assertFalse(surface.toString().contains("rawProviderCredential="));
    }
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
        "action-replace-membership-role", "action-replace-membership-role", "secure-tenant-user-foundation", "secure-tenant-user-foundation", null, "idem-1", "membership-admin", "surface-user-admin-detail-admin", "corr-role"));

    assertEquals("denied", result.status());
    assertTrue(result.message().contains("last tenant admin"));
    assertEquals("surface-user-admin-detail-admin", result.resultSurface().surfaceId());
  }

  @Test
  void userAdminContractCapabilityActionsPreviewApplyAuditAndIdempotency() {
    identityRepository.saveAccount(new Account("second-admin@example.test", null, "second-admin@example.test", "second-admin@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("second-admin@example.test", "second-admin@example.test", "Second Admin", "Second", "Admin", null));
    identityRepository.putSettings(new UserSettings("second-admin@example.test", UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership("membership-second-admin", "second-admin@example.test", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.TENANT_ADMIN), MembershipStatus.ACTIVE, false, null));

    var preview = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-preview-role-change", "action-useradmin-preview-role-change", "USERADMIN_PREVIEW_ROLE_CHANGE", "USERADMIN_PREVIEW_ROLE_CHANGE", Map.of("membershipId", "membership-member", "roles", List.of("TENANT_ADMIN"), "reason", "promotion"), null, "membership-admin", "surface-user-admin-detail-admin", "corr-useradmin-preview"));
    assertEquals("accepted", preview.status());
    assertTrue(preview.traceIds().get(0).contains("trace-useradmin-preview-role-change"));
    assertEquals("surface-user-admin-role-change-preview", preview.resultSurface().surfaceId());
    assertEquals("user_admin.role_change_preview.v1", preview.resultSurface().data().get("surfaceContract"));
    assertTrue(preview.resultSurface().toString().contains("capabilityDelta"));
    assertTrue(preview.resultSurface().toString().contains("affectedWorkstreams"));

    var changed = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-change-member-roles", "action-useradmin-change-member-roles", "USERADMIN_CHANGE_MEMBER_ROLES", "USERADMIN_CHANGE_MEMBER_ROLES", Map.of("membershipId", "membership-member", "roles", List.of("TENANT_ADMIN"), "reason", "promotion"), "idem-useradmin-change", "membership-admin", "surface-user-admin-detail-admin", "corr-useradmin-change"));
    assertEquals("accepted", changed.status());
    assertEquals("surface-user-admin-detail-admin", changed.resultSurface().surfaceId());

    var duplicate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-change-member-roles", "action-useradmin-change-member-roles", "USERADMIN_CHANGE_MEMBER_ROLES", "USERADMIN_CHANGE_MEMBER_ROLES", Map.of("membershipId", "membership-member", "roles", List.of("TENANT_EMPLOYEE"), "reason", "ignored replay"), "idem-useradmin-change", "membership-admin", "surface-user-admin-detail-admin", "corr-useradmin-change-replay"));
    assertEquals(changed, duplicate);
    assertEquals(List.of(FoundationRole.TENANT_ADMIN), identityRepository.findMembership("membership-member").orElseThrow().roles());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("USERADMIN_CHANGE_MEMBER_ROLES") && event.correlationId().equals("corr-useradmin-change")));
  }

  @Test
  void userAdminStatusActionsDisableReactivateNoOpAndDenyManualSelfDisable() {
    var disabled = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "USERADMIN_UPDATE_MEMBER_STATUS", "USERADMIN_UPDATE_MEMBER_STATUS", Map.of("membershipId", "membership-member", "reason", "leave"), "idem-disable-member", "membership-admin", "surface-user-admin-list", "corr-disable-member"));
    assertEquals("accepted", disabled.status());
    assertEquals("surface-user-admin-list", disabled.resultSurface().surfaceId());
    assertTrue(disabled.traceIds().get(0).contains("trace-useradmin-update-member-status"));
    assertTrue(disabled.resultSurface().toString().contains("status=suspended"));

    var duplicate = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "USERADMIN_UPDATE_MEMBER_STATUS", "USERADMIN_UPDATE_MEMBER_STATUS", Map.of("membershipId", "membership-member", "reason", "ignored replay"), "idem-disable-member", "membership-admin", "surface-user-admin-list", "corr-disable-member-replay"));
    assertEquals(disabled, duplicate);

    var reactivated = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-reactivate-member", "action-useradmin-reactivate-member", "USERADMIN_UPDATE_MEMBER_STATUS", "USERADMIN_UPDATE_MEMBER_STATUS", Map.of("membershipId", "membership-member", "reason", "return"), "idem-reactivate-member", "membership-admin", "surface-user-admin-list", "corr-reactivate-member"));
    assertEquals("accepted", reactivated.status());
    assertTrue(reactivated.resultSurface().toString().contains("status=active"));

    var selfDisable = assertThrows(AuthorizationException.class, () -> service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-disable-member", "action-useradmin-disable-member", "USERADMIN_UPDATE_MEMBER_STATUS", "USERADMIN_UPDATE_MEMBER_STATUS", Map.of("membershipId", "membership-admin", "reason", "self-disable"), "idem-self-disable", "membership-admin", "surface-user-admin-list", "corr-self-disable")));
    assertEquals("self-disable-denied", selfDisable.reasonCode());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("USERADMIN_UPDATE_MEMBER_STATUS") && event.reasonCode().equals("self-disable-denied")));
  }

  @Test
  void userAdminAccessReviewTaskLifecycleProducesTypedProviderBlockedSurface() {
    var result = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-start-access-review", "action-useradmin-start-access-review", "user_admin.access_review.start", "user_admin.access_review.start", Map.of("scope", "tenant"), "idem-access-review", "membership-admin", "surface-user-admin-dashboard", "corr-access-review"));

    assertEquals("blocked-runtime", result.status());
    assertTrue(result.message().contains("provider/runtime configuration"));
    assertEquals("surface-user-admin-access-review", result.resultSurface().surfaceId());
    assertEquals("user_admin.access_review_task.v1", result.resultSurface().data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", result.resultSurface().data().get("status"));
    assertEquals(true, result.resultSurface().data().get("noDirectMutation"));
    assertTrue(result.resultSurface().toString().contains("user_admin.access_review.read"));
    assertTrue(result.resultSurface().toString().contains("AccessReviewTask") || result.resultSurface().toString().contains("access-review"));

    var taskId = result.resultSurface().data().get("taskId").toString();
    var read = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-read-access-review", "action-useradmin-read-access-review", "user_admin.access_review.read", "user_admin.access_review.read", Map.of("taskId", taskId), null, "membership-admin", "surface-user-admin-access-review", "corr-access-review-read"));
    assertEquals("accepted", read.status());
    assertEquals(taskId, read.resultSurface().data().get("taskId"));

    var cancelled = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-useradmin-cancel-access-review", "action-useradmin-cancel-access-review", "user_admin.access_review.cancel", "user_admin.access_review.cancel", Map.of("taskId", taskId, "reason", "not needed"), "idem-access-review-cancel", "membership-admin", "surface-user-admin-access-review", "corr-access-review-cancel"));
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

    assertEquals("dashboard", dashboard.surfaceType());
    assertEquals("my_account.dashboard.v1", dashboard.data().get("surfaceContract"));
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
    assertTrue(profile.toString().contains("my_account.update_profile_settings"));
    assertTrue(settings.toString().contains("preferredColorMode"));
    assertTrue(context.toString().contains("/api/me?selectedContextId=membership-admin"));
    assertTrue(context.toString().contains("my_account.view_context"));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-show-my-account-dashboard")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-show-my-context") && action.capabilityId().equals("my_account.view_context")));
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
    assertTrue(center.resultSurface().toString().contains("notification.email.list_my_preferences"));
    assertTrue(center.resultSurface().toString().contains("notification.email.update_preferences"));
    assertTrue(center.resultSurface().toString().contains("resend"));
    assertTrue(center.resultSurface().toString().contains("captured_outbox"));
    assertTrue(center.resultSurface().toString().contains("SMS, mobile push"));
    assertFalse(center.resultSurface().toString().contains("pushEnabled"));
    assertFalse(center.resultSurface().toString().contains("RESEND_API_KEY"));

    var emailPref = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-notification-email-update-preferences", "action-notification-email-update-preferences", "notification.email.update_preferences", "notification.email.update_preferences", Map.of("category", "digest_ready", "enabled", true, "minimumPriority", "info"), null, "membership-admin", "surface-my-account-notification-center", "corr-email-pref"));
    assertEquals("accepted", emailPref.status());
    assertEquals("surface-my-account-notification-center", emailPref.resultSurface().surfaceId());
    assertTrue(emailPref.resultSurface().toString().contains("emailPreferencesSummary"));
    assertTrue(emailPref.resultSurface().toString().contains("digest_ready"));
    assertTrue(emailPref.message().contains("in-app notifications and future SMS/push/webhook channels are unchanged"));

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
        "action-update-my-profile", "action-update-my-profile", "my_account.update_profile_settings", "my_account.update_profile_settings", Map.of("displayName", "Updated Admin", "preferredColorMode", "dark"), "idem-my-account-update", "membership-admin", "surface-my-profile", "corr-my-account-update"));

    assertEquals("accepted", result.status());
    assertEquals("surface-my-profile", result.resultSurface().surfaceId());
    assertTrue(result.resultSurface().toString().contains("Updated Admin"));
    var me = service.bootstrap(identity(), "membership-admin", "corr-my-account-read").me();
    assertEquals("Updated Admin", me.profile().displayName());
    assertEquals("dark", me.settings().preferredColorMode());

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
        "action-update-my-settings", "action-update-my-settings", "my_account.update_profile_settings", "my_account.update_profile_settings", Map.of("displayName", "Tenant Admin", "preferredColorMode", "light"), "idem-my-account-noop", "membership-admin", "surface-my-settings", "corr-my-account-noop"));

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
    assertEquals(5, bootstrap.surfaces().size());
    assertTrue(bootstrap.surfaces().stream().allMatch(surface -> surface.surfaceType().equals("markdown_response")));

    var show = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "show_surface", "user_prompt", "show user admin dashboard", null, "agent-user-admin", "surface-user-admin-dashboard", null, "agent-user-admin", null, null, "current_workstream", "corr-shell-show", "membership-admin"));
    assertEquals("accepted", show.status());
    assertEquals("dashboard", show.resultSurface().surfaceType());
    assertEquals("surface-user-admin-dashboard", show.resultSurface().surfaceId());
    assertEquals("agent-user-admin", show.requestItem().functionalAgentId());
    assertEquals("user-request", show.requestItem().kind());
    assertTrue(show.request().canonicalPrompt().contains("show surface"));

    var refresh = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "refresh_surface", "surface_action", "Refresh User Admin dashboard", null, "agent-user-admin", "surface-user-admin-dashboard", null, "agent-user-admin", "surface-user-admin-dashboard", "action-display-user-list", "current_workstream", "corr-shell-refresh", "membership-admin"));
    assertEquals("accepted", refresh.status());
    assertEquals("surface-user-admin-dashboard", refresh.resultSurface().surfaceId());

    var openAttention = service.runShellRequest(identity(), "membership-admin", new WorkstreamService.WorkstreamShellRequest(
        "open_attention_item", "my_account_panel", "Open Agent Admin readiness", null, "agent-agent-admin", "surface-agent-admin-catalog", "attention-agent-admin-readiness", "agent-my-account", "surface-my-account-dashboard", "action-open-agent-admin", "authorized_cross_workstream", "corr-shell-attention", "membership-admin"));
    assertEquals("accepted", openAttention.status());
    assertEquals("agent-agent-admin", openAttention.resultSurface().ownerFunctionalAgentId());
  }

  @Test
  void shellRequestsReturnSafeSystemMessageForHiddenTargets() {
    var denied = service.runShellRequest(memberIdentity(), "membership-member", new WorkstreamService.WorkstreamShellRequest(
        "open_workstream", "deep_link", "Open Agent Admin", null, "agent-agent-admin", null, null, "agent-my-account", null, null, "authorized_cross_workstream", "corr-shell-denied", "membership-member"));

    assertEquals("denied", denied.status());
    assertEquals("system_message", denied.resultSurface().surfaceType());
    assertEquals("TARGET_NOT_FOUND_OR_FORBIDDEN", denied.resultSurface().data().get("code"));
    assertFalse(denied.resultSurface().toString().contains("agent_admin.list_definitions"));
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
        "action-governance-policy-decide", "action-governance-policy-decide", "governance.policy.approve", "governance.policy.approve", Map.of("proposalId", proposalId, "decision", "approve", "rationale", "bounded starter proof"), "idem-gov-decision", "membership-admin", "surface-governance-policy-simulation", "corr-gov-decision"));
    assertEquals("accepted", decision.status());
    assertEquals("surface-governance-policy-decision", decision.resultSurface().surfaceId());
    assertTrue(decision.resultSurface().toString().contains("rollback metadata"));

    var activationBlocked = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-activate", "action-governance-policy-activate", "governance.policy.activate", "governance.policy.activate", Map.of("proposalId", proposalId), "idem-gov-activate-blocked", "membership-admin", "surface-governance-policy-decision", "corr-gov-activate-blocked"));
    assertEquals("approval-required", activationBlocked.status());
    assertTrue(activationBlocked.resultSurface().toString().contains("sideEffect=none"));

    var activation = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-activate", "action-governance-policy-activate", "governance.policy.activate", "governance.policy.activate", Map.of("proposalId", proposalId, "rollbackReference", "rollback metadata v1"), "idem-gov-activate", "membership-admin", "surface-governance-policy-decision", "corr-gov-activate"));
    assertEquals("accepted", activation.status());
    assertTrue(activation.resultSurface().toString().contains("activated-with-rollback-metadata"));

    var rollback = service.runAction(identity(), "membership-admin", new WorkstreamService.CapabilityActionRequest(
        "action-governance-policy-rollback", "action-governance-policy-rollback", "governance.policy.rollback", "governance.policy.rollback", Map.of("proposalId", proposalId), "idem-gov-rollback", "membership-admin", "surface-governance-policy-decision", "corr-gov-rollback"));
    assertEquals("accepted", rollback.status());
    assertTrue(rollback.resultSurface().toString().contains("rolled_back"));

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
}

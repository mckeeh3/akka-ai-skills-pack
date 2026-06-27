package ai.first.application.coreapp.workstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.coreapp.myaccount.InMemoryTestMyAccountPersonalAttentionDigestTaskRepository;
import ai.first.application.coreapp.myaccount.MyAccountPersonalAttentionDigestService;
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
import ai.first.application.foundation.audit.AuditTraceService;
import ai.first.application.foundation.audit.InMemoryTestAuditTraceRepository;
import ai.first.application.foundation.governance.InMemoryTestGovernancePolicyRepository;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.InMemoryTestIdentityRepository;
import ai.first.application.foundation.identity.MeService;
import ai.first.application.foundation.invitation.InvitationService;
import ai.first.application.foundation.invitation.InvitationView;
import ai.first.application.foundation.invitation.InMemoryTestInvitationRepository;
import ai.first.application.foundation.notification.InMemoryTestNotificationRepository;
import ai.first.application.foundation.notification.NotificationService;
import ai.first.application.foundation.workstream.InMemoryTestWorkstreamEventRepository;
import ai.first.application.foundation.workstream.InMemoryTestWorkstreamLogRepository;
import ai.first.application.foundation.workstream.WorkstreamEventAttentionConsumer;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.domain.foundation.audit.AdminAuditEvent;
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
import java.util.Map;
import org.junit.jupiter.api.Test;

class MyAccountTraceAuditTest {
  private static final String TENANT_ID = "tenant-1";
  private static final String CONTEXT_ID = "membership-admin";
  private static final WorkosIdentity IDENTITY = new WorkosIdentity("workos-admin", "admin@example.test", "Tenant Admin");

  @Test
  void myAccountSurfaceDigestAndNotificationActionsEmitDurableBrowserSafeAdminAuditFacts() {
    var fixture = Fixture.create();
    var actor = fixture.resolver.resolveMe(IDENTITY, CONTEXT_ID, "corr-trace-actor");

    fixture.service.runAction(IDENTITY, CONTEXT_ID, action("action-show-my-account-dashboard", "my_account.view_summary", null, null, "surface-my-account-dashboard", "corr-trace-dashboard"));
    fixture.service.runAction(IDENTITY, CONTEXT_ID, action("action-show-my-profile", "my_account.view_summary", null, null, "surface-my-account-dashboard", "corr-trace-profile"));
    fixture.service.runAction(IDENTITY, CONTEXT_ID, action("action-show-my-settings", "my_account.view_summary", null, null, "surface-my-account-dashboard", "corr-trace-settings"));
    fixture.service.runAction(IDENTITY, CONTEXT_ID, action("action-show-my-context", "my_account.view_context", null, null, "surface-my-account-dashboard", "corr-trace-context"));
    fixture.service.runAction(IDENTITY, CONTEXT_ID, action("action-update-my-profile", "my_account.update_profile_settings", Map.of("displayName", "Trace Checked Admin"), "idem-profile-trace", "surface-my-profile", "corr-trace-profile-save"));

    var digest = fixture.digestService.start(actor, new MyAccountPersonalAttentionDigestService.StartPersonalAttentionDigestCommand("idem-digest-trace"), "corr-trace-digest-start");
    assertEquals("blocked_provider_or_runtime", digest.blockerCode());

    var center = fixture.service.runAction(IDENTITY, CONTEXT_ID, action("action-show-my-account-notification-center", "notification.list_my_account_center", null, null, "surface-my-account-dashboard", "corr-trace-notification-center"));
    @SuppressWarnings("unchecked")
    var notifications = (List<Map<String, Object>>) center.resultSurface().data().get("items");
    assertFalse(notifications.isEmpty(), "digest lifecycle event should project into a visible My Account notification");
    var notificationId = String.valueOf(notifications.get(0).get("notificationId"));
    fixture.service.runAction(IDENTITY, CONTEXT_ID, action("action-notification-mark-read", "notification.mark_read", Map.of("notificationId", notificationId), null, "surface-my-account-notification-center", "corr-trace-notification-read"));
    fixture.service.runAction(IDENTITY, CONTEXT_ID, action("action-notification-update-preferences", "notification.update_preferences", Map.of("category", "WORKSTREAM_UPDATE", "enabled", true, "minimumPriority", "INFO"), null, "surface-my-account-notification-center", "corr-trace-notification-pref"));

    assertMyAccountActionTrace(fixture, "corr-trace-dashboard", "action-show-my-account-dashboard", "surface_action", "my_account.view_summary", "accepted");
    assertMyAccountActionTrace(fixture, "corr-trace-profile", "action-show-my-profile", "surface_action", "my_account.view_summary", "accepted");
    assertMyAccountActionTrace(fixture, "corr-trace-settings", "action-show-my-settings", "surface_action", "my_account.view_summary", "accepted");
    assertMyAccountActionTrace(fixture, "corr-trace-context", "action-show-my-context", "surface_action", "my_account.view_context", "accepted");
    assertMyAccountActionTrace(fixture, "corr-trace-profile-save", "action-update-my-profile", "surface_action", "my_account.update_profile_settings", "accepted");
    assertMyAccountActionTrace(fixture, "corr-trace-notification-read", "action-notification-mark-read", "surface_action", "notification.mark_read", "full");
    assertMyAccountActionTrace(fixture, "corr-trace-notification-pref", "action-notification-update-preferences", "surface_action", "notification.update_preferences", "accepted");

    assertAudit(fixture, "corr-trace-profile-save", "MY_ACCOUNT_PROFILE_SETTINGS_UPDATE", AdminAuditEvent.Result.ALLOWED, "changed-fields");
    assertAudit(fixture, "corr-trace-digest-start", MyAccountPersonalAttentionDigestService.START_CAPABILITY, AdminAuditEvent.Result.ALLOWED, "provider-blocked-fail-closed");
    assertAudit(fixture, "corr-trace-notification-center", "NOTIFICATION_PROJECT_FROM_SOURCE", AdminAuditEvent.Result.ALLOWED, "notification-");
    assertAudit(fixture, "corr-trace-notification-read", "NOTIFICATION_MARK_READ", AdminAuditEvent.Result.ALLOWED, notificationId);
    assertBrowserSafeAuditPayloads(fixture);
  }

  @Test
  void humanChatToolPlanPersistsLifecycleTraceFactsAndAuditTimelineEvidence() {
    var fixture = Fixture.create();
    var step = new WorkstreamService.ChatToolPlanStep(
        "step-update-profile",
        1,
        "Update display name",
        "action-update-my-profile",
        "action-update-my-profile",
        "my_account.update_profile_settings",
        "my_account.update_profile_settings",
        "schema.my-account.profile.update.v1",
        Map.of("displayName", "Chat Planned Admin"),
        List.of(),
        Map.of(),
        "idem-chat-step-profile",
        "profile-settings",
        true,
        false,
        "detail-edit",
        "surface-my-profile",
        List.of("human_chat_tool_plan.proposed", "human_chat_tool_plan.confirmed", "human_chat_tool_plan.step_started", "human_chat_tool_plan.step_completed"));

    var proposed = fixture.service.createChatToolPlanProposal(IDENTITY, CONTEXT_ID, new WorkstreamService.ChatToolPlanProposalRequest(
        CONTEXT_ID,
        "my-account-agent",
        "Update my display name to Chat Planned Admin",
        "corr-chat-propose",
        "idem-chat-propose",
        null,
        "Update profile after confirmation only.",
        List.of(step),
        "No mutation until exact snapshot confirmation."));
    var proposal = (WorkstreamService.ChatToolPlanProposal) proposed.surface().data().get("proposal");
    var snapshot = (WorkstreamService.ChatToolPlanConfirmationSnapshot) proposed.surface().data().get("confirmationSnapshot");
    assertNotNull(proposal);
    assertNotNull(snapshot);

    var confirmed = fixture.service.confirmChatToolPlan(IDENTITY, CONTEXT_ID, new WorkstreamService.ChatToolPlanConfirmationRequest(
        CONTEXT_ID,
        proposal.planId(),
        snapshot.planSnapshotId(),
        "CONFIRM " + snapshot.planSnapshotId(),
        snapshot.stepHashes(),
        "idem-chat-confirm",
        "corr-chat-confirm"));

    assertEquals("chat_tool_plan_result", confirmed.surface().surfaceType());
    var result = (WorkstreamService.ChatToolPlanExecutionResult) confirmed.surface().data().get("result");
    assertEquals("completed", result.status());
    assertEquals("surface-my-profile", result.completedSteps().get(0).resultSurfaceId());

    assertAudit(fixture, "corr-chat-propose", "human_chat_tool_plan.proposed", AdminAuditEvent.Result.ALLOWED, "no-mutation");
    assertAudit(fixture, "corr-chat-confirm", "human_chat_tool_plan.confirmed", AdminAuditEvent.Result.ALLOWED, proposal.planSnapshotId());
    assertAudit(fixture, "corr-chat-confirm:step-update-profile", "human_chat_tool_plan.step_started", AdminAuditEvent.Result.ALLOWED, "my_account.update_profile_settings");
    assertAudit(fixture, "corr-chat-confirm:step-update-profile", "human_chat_tool_plan.step_completed", AdminAuditEvent.Result.ALLOWED, "surface-my-profile");
    assertMyAccountActionTrace(fixture, "corr-chat-confirm:step-update-profile", "action-update-my-profile", "human_chat_tool_plan", "my_account.update_profile_settings", "accepted");

    assertTrue(fixture.workstreamLogRepository.items(TENANT_ID, CONTEXT_ID, "my-account-agent").stream()
        .anyMatch(item -> item.kind().equals("chat_tool_plan_result") && item.correlationId().equals("corr-chat-confirm")));

    var auditTrace = new AuditTraceService(fixture.resolver, new InMemoryTestAuditTraceRepository(fixture.agentRuntimeService, fixture.workstreamLogRepository));
    var auditActor = fixture.resolver.resolveMe(IDENTITY, CONTEXT_ID, "corr-chat-audit-read");
    var timeline = auditTrace.timeline(auditActor, Map.of("correlationId", "corr-chat-confirm"), "corr-chat-audit-read");
    assertTrue(timeline.toString().contains("WORKSTREAM_LOG_ITEM"));
    assertTrue(timeline.toString().contains("chat tool plan"));
    assertTrue(timeline.toString().contains("browserSafe=true"));
    assertBrowserSafeAuditPayloads(fixture);
  }

  private static WorkstreamService.CapabilityActionRequest action(String actionId, String capabilityId, Object input, String idempotencyKey, String surfaceId, String correlationId) {
    return new WorkstreamService.CapabilityActionRequest(actionId, actionId, capabilityId, capabilityId, input, idempotencyKey, CONTEXT_ID, surfaceId, correlationId);
  }

  private static void assertMyAccountActionTrace(Fixture fixture, String correlationId, String actionId, String adapterSource, String capabilityId, String result) {
    var event = audit(fixture, correlationId, "MY_ACCOUNT_SURFACE_ACTION");
    assertEquals(AdminAuditEvent.Result.ALLOWED, event.result());
    assertEquals("admin@example.test", event.actorAccountId());
    assertEquals(CONTEXT_ID, event.actorMembershipId());
    assertEquals(TENANT_ID, event.tenantId());
    assertEquals("BROWSER_SAFE", event.dataClassification());
    assertTrue(event.evidenceSummary().contains("adapterSource=" + adapterSource), event.evidenceSummary());
    assertTrue(event.evidenceSummary().contains("actionId=" + actionId), event.evidenceSummary());
    assertTrue(event.evidenceSummary().contains("browserToolId=" + actionId), event.evidenceSummary());
    assertTrue(event.evidenceSummary().contains("governedToolId=" + capabilityId), event.evidenceSummary());
    assertTrue(event.evidenceSummary().contains("capabilityId=" + capabilityId), event.evidenceSummary());
    assertTrue(event.evidenceSummary().contains("selectedContextId=" + CONTEXT_ID), event.evidenceSummary());
    assertTrue(event.evidenceSummary().contains("tenantId=" + TENANT_ID), event.evidenceSummary());
    assertTrue(event.evidenceSummary().contains("actorAccountId=admin@example.test"), event.evidenceSummary());
    assertTrue(event.evidenceSummary().contains("result=" + result), event.evidenceSummary());
    assertTrue(event.evidenceSummary().contains("correlationId=" + correlationId), event.evidenceSummary());
    assertTrue(event.evidenceSummary().contains("redaction=browser-safe"), event.evidenceSummary());
  }

  private static void assertAudit(Fixture fixture, String correlationId, String actionType, AdminAuditEvent.Result result, String evidenceFragment) {
    var event = audit(fixture, correlationId, actionType);
    assertEquals(result, event.result(), event.toString());
    assertEquals("admin@example.test", event.actorAccountId());
    assertEquals(CONTEXT_ID, event.actorMembershipId());
    assertEquals(TENANT_ID, event.tenantId());
    assertEquals("BROWSER_SAFE", event.dataClassification());
    assertTrue(event.evidenceSummary().contains(evidenceFragment), event.evidenceSummary());
  }

  private static AdminAuditEvent audit(Fixture fixture, String correlationId, String actionType) {
    return fixture.identityRepository.auditEvents().stream()
        .filter(event -> correlationId.equals(event.correlationId()))
        .filter(event -> actionType.equals(event.actionType()))
        .reduce((first, second) -> second)
        .orElseThrow(() -> new AssertionError("missing audit event " + actionType + " for " + correlationId + " in " + fixture.identityRepository.auditEvents()));
  }

  private static void assertBrowserSafeAuditPayloads(Fixture fixture) {
    var rendered = fixture.identityRepository.auditEvents().toString().toLowerCase();
    assertFalse(rendered.contains("api_key"));
    assertFalse(rendered.contains("secret="));
    assertFalse(rendered.contains("bearer "));
    assertFalse(rendered.contains("rawjwt"));
    assertFalse(rendered.contains("providersecret"));
  }

  private record Fixture(
      InMemoryTestIdentityRepository identityRepository,
      AuthContextResolver resolver,
      AgentRuntimeService agentRuntimeService,
      InMemoryTestWorkstreamLogRepository workstreamLogRepository,
      WorkstreamService service,
      MyAccountPersonalAttentionDigestService digestService) {
    static Fixture create() {
      var identityRepository = new InMemoryTestIdentityRepository();
      var resolver = new AuthContextResolver(identityRepository);
      var attentionRepository = new InMemoryTestAttentionRepository();
      var attentionService = new AttentionService(attentionRepository, resolver, Clock.systemUTC());
      var attentionProducerService = new AttentionProducerService(attentionRepository, identityRepository, Clock.systemUTC());
      var eventRepository = new InMemoryTestWorkstreamEventRepository();
      var eventConsumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, attentionProducerService, Clock.systemUTC());
      var eventPublisher = new WorkstreamEventPublisher(eventRepository, eventConsumer, Clock.systemUTC());
      var notificationRepository = new InMemoryTestNotificationRepository();
      var notificationService = new NotificationService(notificationRepository, resolver, Clock.systemUTC());
      var meService = new MeService(resolver, new MyAccountService(resolver, attentionService));
      var userAdminService = new UserAdminService(identityRepository, Clock.systemUTC());
      var invitationRepository = new InMemoryTestInvitationRepository();
      var invitationService = new InvitationService(identityRepository, invitationRepository, Clock.systemUTC(), attentionProducerService, eventPublisher);
      var agentRepository = new InMemoryTestAgentBehaviorRepository();
      new AgentBehaviorSeedLoader(agentRepository, Clock.systemUTC()).importStarterDefaults(TENANT_ID, "trace-test-bootstrap", "corr-trace-agent-seed");
      var agentRuntimeService = new AgentRuntimeService(
          agentRepository,
          resolver,
          Clock.systemUTC(),
          request -> new ModelProviderClient.ModelProviderResponse("trace test response", "test-provider", "test-model", "response-id", "stop", "trace-test"),
          new InMemoryTestAgentRuntimeTraceSink());
      WorkstreamAgentRuntimeInvoker runtimeInvoker = agentRuntimeService::invokeWorkstreamAgent;
      var workstreamLogRepository = new InMemoryTestWorkstreamLogRepository();
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
          eventPublisher,
          eventRepository,
          new FailClosedAccessReviewAutonomousAgentRuntime(),
          notificationService);
      var digestService = new MyAccountPersonalAttentionDigestService(
          new InMemoryTestMyAccountPersonalAttentionDigestTaskRepository(),
          resolver,
          attentionService,
          Clock.systemUTC(),
          new ai.first.application.coreapp.myaccount.FailClosedMyAccountPersonalAttentionDigestAutonomousAgentRuntime(),
          attentionProducerService,
          eventPublisher);
      seedIdentity(identityRepository);
      return new Fixture(identityRepository, resolver, agentRuntimeService, workstreamLogRepository, service, digestService);
    }

    private static void seedIdentity(InMemoryTestIdentityRepository repository) {
      repository.putTenant(new Tenant(TENANT_ID, "Tenant One", true));
      repository.saveAccount(new Account("admin@example.test", null, "admin@example.test", "admin@example.test", AccountStatus.ACTIVE, "UNLINKED"));
      repository.putProfile(new UserProfile("admin@example.test", "admin@example.test", "Tenant Admin", "Tenant", "Admin", null));
      repository.putSettings(new UserSettings("admin@example.test", UserSettings.ThemeId.AURORA_LIGHT));
      repository.putMembership(new Membership(CONTEXT_ID, "admin@example.test", ScopeType.TENANT, TENANT_ID, null, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR), MembershipStatus.ACTIVE, false, null));
    }
  }
}

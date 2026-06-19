package ai.first.application.foundation.workstream;

import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.invitation.Invitation;
import ai.first.domain.foundation.workstream.WorkstreamEventEnvelope;
import ai.first.domain.foundation.workstream.WorkstreamEventSourceRef;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.coreapp.agentadmin.AgentAdminPromptRiskReviewService;
import ai.first.application.coreapp.audit.AuditTraceSummaryAutonomousAgentRuntime;
import ai.first.application.coreapp.governance.GovernancePolicyImpactAutonomousAgentRuntime;
import ai.first.application.coreapp.agentadmin.InMemoryTestPromptRiskReviewTaskRepository;
import ai.first.application.coreapp.agentadmin.PromptRiskAutonomousAgentRuntime;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.domain.coreapp.agentadmin.PromptRiskReviewTask;
import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.coreapp.audit.AuditTraceSummaryTask;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.attention.AttentionCategory;
import ai.first.domain.foundation.attention.AttentionItemStatus;
import ai.first.domain.foundation.email.EmailDeliveryStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.coreapp.governance.GovernancePolicyImpactTask;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ai.first.application.foundation.attention.AttentionProducerService;
import ai.first.application.foundation.attention.AttentionService;
import ai.first.application.foundation.attention.InMemoryTestAttentionRepository;
import ai.first.application.foundation.governance.GovernancePolicyService;
import ai.first.application.foundation.governance.InMemoryTestGovernancePolicyRepository;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.InMemoryTestIdentityRepository;
import ai.first.application.foundation.invitation.InvitationService;
import ai.first.application.foundation.invitation.InMemoryTestInvitationRepository;
import ai.first.application.coreapp.useradmin.AccessReviewAutonomousAgentRuntime;
import ai.first.application.coreapp.audit.AuditTraceSummaryService;
import ai.first.application.coreapp.governance.GovernancePolicyImpactService;
import ai.first.application.coreapp.useradmin.InMemoryTestAccessReviewTaskRepository;
import ai.first.application.coreapp.audit.InMemoryTestAuditTraceSummaryTaskRepository;
import ai.first.application.coreapp.governance.InMemoryTestGovernancePolicyImpactTaskRepository;
import ai.first.application.coreapp.useradmin.UserAdminAccessReviewService;
import ai.first.application.coreapp.useradmin.UserAdminService;

class WorkstreamEventBackboneServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-30T12:00:00Z"), ZoneOffset.UTC);
  private InMemoryTestIdentityRepository identityRepository;
  private InMemoryTestInvitationRepository invitationRepository;
  private InMemoryTestAttentionRepository attentionRepository;
  private InMemoryTestWorkstreamEventRepository eventRepository;
  private InMemoryTestAccessReviewTaskRepository accessReviewRepository;
  private AuthContextResolver resolver;
  private InvitationService invitations;
  private AttentionService attention;
  private AuthContextResolver.ResolvedMe tenantAdmin;

  @BeforeEach
  void setUp() {
    identityRepository = new InMemoryTestIdentityRepository();
    invitationRepository = new InMemoryTestInvitationRepository();
    attentionRepository = new InMemoryTestAttentionRepository();
    eventRepository = new InMemoryTestWorkstreamEventRepository();
    accessReviewRepository = new InMemoryTestAccessReviewTaskRepository();
    resolver = new AuthContextResolver(identityRepository);
    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, producers, clock);
    var publisher = new WorkstreamEventPublisher(eventRepository, consumer, clock);
    invitations = new InvitationService(identityRepository, invitationRepository, clock, producers, publisher);
    attention = new AttentionService(attentionRepository, resolver, clock);

    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    identityRepository.putTenant(new Tenant("tenant-2", "Tenant Two", true));
    seedAccount("admin@example.test", "membership-admin", "tenant-1", List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR));
    seedAccount("other@example.test", "membership-other", "tenant-2", List.of(FoundationRole.TENANT_ADMIN));
    tenantAdmin = resolver.resolveMe(new WorkosIdentity("workos-admin@example.test", "admin@example.test", "Admin"), "membership-admin", "corr-admin");
  }

  @Test
  void invitationDeliveryFailurePublishesEnvelopeAndConsumerProjectsAttentionIdempotently() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("invite-event", "evented@example.test"));

    var failed = invitations.recordDeliveryResult(invite.invitationId(), "delivery-1", false, null, "resend token=secret api_key=secret", "corr-event-failed");

    var events = eventRepository.listTenant("tenant-1");
    assertEquals(1, events.size());
    var event = events.get(0);
    assertEquals("invitation.delivery.failed", event.eventType());
    assertEquals("domain", event.eventFamily());
    assertEquals("InvitationDeliveryEventPayload", event.payloadClass());
    assertEquals(failed.invitationId(), event.payload().get("invitationId"));
    assertTrue(event.idempotencyKey().startsWith("workstream-event:domain:invitation.delivery.failed:tenant-1:none:"));
    assertTrue(event.sourceRefs().stream().anyMatch(ref -> ref.refType().equals("domain_event") && ref.refId().equals(failed.invitationId())));
    assertTrue(event.capabilityRefs().contains("user_admin.invitation.delivery"));
    assertFalse(event.toString().contains("api_key=secret"));
    assertFalse(event.toString().contains("token=secret"));

    var itemId = "attention:user-admin:invitation-delivery:" + failed.invitationId();
    var item = attentionRepository.find("tenant-1", itemId).orElseThrow();
    assertEquals(AttentionItemStatus.OPEN, item.status());
    assertEquals(AttentionCategory.INVITATION_DELIVERY, item.category());
    assertTrue(item.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("workstream_event") && ref.refId().equals(event.eventId())));
    assertTrue(item.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("idempotency") && ref.refId().equals(event.idempotencyKey())));

    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, new AttentionProducerService(attentionRepository, identityRepository, clock), clock);
    var replay = consumer.project(event, failed);
    assertNotNull(replay);
    assertEquals(item.lastChangedAt(), replay.lastChangedAt());
    assertEquals(1, attention.listWorkstreamItems(tenantAdmin, "user-admin-agent", "corr-list").size());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(audit -> audit.actionType().equals("WORKSTREAM_EVENT_CONSUMER_DUPLICATE") && audit.result() == ai.first.domain.foundation.audit.AdminAuditEvent.Result.NO_OP));
  }

  @Test
  void invitationDeliverySuccessPublishesSentEventAndResolvesExistingEventBackedAttention() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("invite-event-resolve", "resolved@example.test"));
    var failed = invitations.recordDeliveryResult(invite.invitationId(), "delivery-1", false, null, "resend-500", "corr-event-failed");
    var itemId = "attention:user-admin:invitation-delivery:" + failed.invitationId();
    assertEquals(AttentionItemStatus.OPEN, attentionRepository.find("tenant-1", itemId).orElseThrow().status());

    var delivered = invitations.recordDeliveryResult(invite.invitationId(), "delivery-2", true, "captured-2", null, "corr-event-sent");

    assertEquals(EmailDeliveryStatus.CAPTURED, delivered.deliveryStatus());
    assertEquals(2, eventRepository.listTenant("tenant-1").size());
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("invitation.delivery.sent")));
    var resolved = attentionRepository.find("tenant-1", itemId).orElseThrow();
    assertEquals(AttentionItemStatus.RESOLVED, resolved.status());
    assertTrue(resolved.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("workstream_event")));
  }

  @Test
  void saasOwnerInvitationDeliveryUsesPlatformEventScopeInsteadOfNullTenant() {
    identityRepository.saveAccount(new Account("owner@example.test", "workos-owner@example.test", "owner@example.test", "owner@example.test", AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile("owner@example.test", "Owner", "owner@example.test", null, null, null));
    identityRepository.putSettings(new UserSettings("owner@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership("membership-owner", "owner@example.test", ScopeType.SAAS_OWNER, null, null, List.of(FoundationRole.SAAS_OWNER_ADMIN), MembershipStatus.ACTIVE, false, null));
    var owner = resolver.resolveMe(new WorkosIdentity("workos-owner@example.test", "owner@example.test", "Owner"), "membership-owner", "corr-owner");
    var invite = invitations.createInvitation(owner, new InvitationService.CreateInvitationRequest(
        "invite-platform-owner",
        ScopeType.SAAS_OWNER,
        null,
        null,
        "platform-invite@example.test",
        "Platform Invite",
        List.of(FoundationRole.SAAS_OWNER_ADMIN),
        clock.instant().plusSeconds(3600),
        "platform-admin",
        "corr-platform-invite"));

    var failed = invitations.recordDeliveryResult(invite.invitationId(), "delivery-1", false, null, "provider unavailable", "corr-platform-delivery");

    var events = eventRepository.listTenant(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID);
    assertEquals(1, events.size());
    var event = events.get(0);
    assertEquals("invitation.delivery.failed", event.eventType());
    assertEquals(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, event.tenantId());
    assertEquals("SAAS_OWNER", event.authContext().get("scopeType"));
    assertEquals("", event.authContext().get("sourceTenantId"));
    assertEquals(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, event.authContext().get("tenantId"));
    assertTrue(event.idempotencyKey().startsWith("workstream-event:domain:invitation.delivery.failed:platform:none:"));
    var item = attentionRepository.find(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, "attention:user-admin:invitation-delivery:" + failed.invitationId()).orElseThrow();
    assertEquals(AttentionItemStatus.OPEN, item.status());
    assertTrue(item.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("workstream_event") && ref.refId().equals(event.eventId())));
  }

  @Test
  void accessReviewProviderBlockedLifecyclePublishesWorkflowEventAndProjectsAttention() {
    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, producers, clock);
    var publisher = new WorkstreamEventPublisher(eventRepository, consumer, clock);
    var accessReviews = new UserAdminAccessReviewService(accessReviewRepository, new UserAdminService(identityRepository, clock), clock, producers, publisher);

    var task = accessReviews.start(tenantAdmin, "access-review-event", "corr-access-review-event");

    assertEquals(AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, task.status());
    var events = eventRepository.listTenant("tenant-1");
    assertEquals(2, events.size());
    var event = events.stream().filter(candidate -> candidate.eventType().equals("workflow.access_review.blocked_provider_or_runtime")).findFirst().orElseThrow();
    var workerEvent = events.stream().filter(candidate -> candidate.eventType().equals("worker.task.blocked_provider_or_runtime")).findFirst().orElseThrow();
    assertEquals("worker.task.blocked_provider_or_runtime", workerEvent.eventType());
    assertEquals("task/worker", workerEvent.eventFamily());
    assertTrue(workerEvent.sourceRefs().stream().anyMatch(ref -> ref.refType().equals("autonomous_task") && ref.refId().equals(task.taskId())));
    assertEquals("workflow.access_review.blocked_provider_or_runtime", event.eventType());
    assertEquals("workflow/process", event.eventFamily());
    assertEquals("AccessReviewLifecycleEventPayload", event.payloadClass());
    assertEquals("blocked_provider_or_runtime:fail_closed", event.payload().get("providerOrRuntimeState"));
    assertTrue(event.sourceRefs().stream().anyMatch(ref -> ref.refType().equals("workflow") && ref.refId().equals(task.taskId())));
    assertTrue(event.sourceRefs().stream().anyMatch(ref -> ref.refType().equals("autonomous_task") && ref.refId().equals(task.taskId())));
    assertTrue(event.capabilityRefs().contains(UserAdminAccessReviewService.START_CAPABILITY));
    assertFalse(event.toString().contains("providerCredential="));

    var item = attentionRepository.find("tenant-1", "attention:worker-task:" + task.taskId() + ":task-state").orElseThrow();
    assertEquals(AttentionItemStatus.OPEN, item.status());
    assertEquals(AttentionCategory.WORKFLOW_BLOCKED, item.category());
    assertTrue(item.summary().contains("fails closed"));
    assertTrue(item.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("workstream_event") && ref.refId().equals(event.eventId())));

    var replay = consumer.project(event, task);
    assertNotNull(replay);
    assertEquals(item.lastChangedAt(), replay.lastChangedAt());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(audit -> audit.actionType().equals("WORKSTREAM_EVENT_CONSUMER_DUPLICATE") && audit.result() == ai.first.domain.foundation.audit.AdminAuditEvent.Result.NO_OP));
  }

  @Test
  void accessReviewCancelLifecycleResolvesEventBackedAttentionWithoutMutatingAccess() {
    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, producers, clock);
    var publisher = new WorkstreamEventPublisher(eventRepository, consumer, clock);
    var accessReviews = new UserAdminAccessReviewService(accessReviewRepository, new UserAdminService(identityRepository, clock), clock, producers, publisher);
    var beforeRoles = identityRepository.findMembership("membership-admin").orElseThrow().roles();
    var task = accessReviews.start(tenantAdmin, "access-review-cancel-event", "corr-access-review-cancel-start");
    var itemId = "attention:worker-task:" + task.taskId() + ":task-state";
    assertEquals(AttentionItemStatus.OPEN, attentionRepository.find("tenant-1", itemId).orElseThrow().status());

    var cancelled = accessReviews.cancel(tenantAdmin, task.taskId(), "operator cancelled", "corr-access-review-cancel");

    assertEquals(AccessReviewTask.Status.CANCELLED, cancelled.status());
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("workflow.access_review.cancelled")));
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("worker.task.cancelled")));
    var resolved = attentionRepository.find("tenant-1", itemId).orElseThrow();
    assertEquals(AttentionItemStatus.RESOLVED, resolved.status());
    assertTrue(resolved.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("workstream_event")));
    assertEquals(beforeRoles, identityRepository.findMembership("membership-admin").orElseThrow().roles());
  }

  @Test
  void accessReviewAutonomousAgentCompletionPublishesWorkerTaskEventsAndReviewAttention() {
    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, producers, clock);
    var publisher = new WorkstreamEventPublisher(eventRepository, consumer, clock);
    var runtime = new RecordingAccessReviewAutonomousAgentRuntime();
    var accessReviews = new UserAdminAccessReviewService(accessReviewRepository, new UserAdminService(identityRepository, clock), clock, producers, publisher, runtime);

    var task = accessReviews.start(tenantAdmin, "access-review-complete-event", "corr-access-review-complete-start");

    assertEquals(AccessReviewTask.Status.QUEUED, task.status());
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("workflow.access_review.started")));
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("worker.task.queued")));

    runtime.nextProjection = new AccessReviewAutonomousAgentRuntime.Projection(
        AccessReviewTask.Status.COMPLETED,
        100,
        "Access-review AutonomousAgent completed with advisory recommendations; User Admin human review is required.",
        null,
        null,
        List.of("userAdminEvidence.read", "trace-access-review-evidence"),
        List.of("autonomous_agent_recommendation:review dormant admin role"),
        List.of("autonomous_task:" + task.autonomousAgentTaskId(), "trace-model-result"));

    var completed = accessReviews.read(tenantAdmin, task.taskId(), "corr-access-review-complete-read");

    assertEquals(AccessReviewTask.Status.COMPLETED, completed.status());
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("workflow.access_review.completed_review_required")));
    var workerCompleted = eventRepository.listTenant("tenant-1").stream().filter(event -> event.eventType().equals("worker.task.completed_review_required")).findFirst().orElseThrow();
    assertEquals("task/worker", workerCompleted.eventFamily());
    assertTrue(workerCompleted.sourceRefs().stream().anyMatch(ref -> ref.refType().equals("autonomous_task") && ref.refId().equals(task.autonomousAgentTaskId())));
    var item = attentionRepository.find("tenant-1", "attention:worker-task:" + task.taskId() + ":task-state").orElseThrow();
    assertEquals(AttentionItemStatus.OPEN, item.status());
    assertEquals(AttentionCategory.ACCESS_REVIEW, item.category());
    assertTrue(item.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("workstream_event") && ref.refId().equals(workerCompleted.eventId())));
  }

  @Test
  void promptRiskLifecyclePublishesAgentAdminEventsAndProjectsReviewAttention() {
    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, producers, clock);
    var publisher = new WorkstreamEventPublisher(eventRepository, consumer, clock);
    var runtime = new RecordingPromptRiskAutonomousAgentRuntime();
    var promptRisk = new AgentAdminPromptRiskReviewService(new InMemoryTestPromptRiskReviewTaskRepository(), resolver, clock, producers, publisher, runtime);

    var task = promptRisk.start(tenantAdmin, promptRiskCommand("prompt-risk-event"), "corr-prompt-risk-start");

    assertEquals(PromptRiskReviewTask.Status.QUEUED, task.status());
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("workflow.agent_admin.prompt_risk_review.started")));
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("worker.task.queued")));

    runtime.nextProjection = new PromptRiskAutonomousAgentRuntime.Projection(
        PromptRiskReviewTask.Status.COMPLETED_REVIEW_REQUIRED,
        100,
        "Prompt-risk AutonomousAgent completed model-backed advisory review; human Agent Admin review is required and no behavior artifacts were activated.",
        null,
        null,
        List.of("agentAdminEvidence.read", "readSkill:agent-admin-prompt-risk-review", "readReferenceDoc:agent-admin-prompt-risk-review"),
        List.of("prompt_risk_finding:finding-1:ToolPermissionBoundary expansion"),
        List.of("autonomous_task:" + task.autonomousAgentTaskId(), "trace-prompt-risk-model-result"));

    var completed = promptRisk.read(tenantAdmin, task.taskId(), "corr-prompt-risk-read");

    assertEquals(PromptRiskReviewTask.Status.COMPLETED_REVIEW_REQUIRED, completed.status());
    var workflowCompleted = eventRepository.listTenant("tenant-1").stream().filter(event -> event.eventType().equals("workflow.agent_admin.prompt_risk_review.completed_review_required")).findFirst().orElseThrow();
    var workerCompleted = eventRepository.listTenant("tenant-1").stream().filter(event -> event.eventType().equals("worker.task.completed_review_required")).findFirst().orElseThrow();
    assertEquals("PromptRiskReviewLifecycleEventPayload", workflowCompleted.payloadClass());
    assertEquals("surface-agent-admin-prompt-risk-review", workflowCompleted.targetSurfaceId());
    assertEquals("true", workflowCompleted.payload().get("noDirectMutation"));
    assertEquals("true", workflowCompleted.payload().get("activationBlockedUntilHumanDecision"));
    assertTrue(workflowCompleted.sourceRefs().stream().anyMatch(ref -> ref.refType().equals("autonomous_task") && ref.refId().equals(task.autonomousAgentTaskId())));
    assertTrue(workflowCompleted.sourceRefs().stream().anyMatch(ref -> ref.refType().equals("behavior_proposal") && ref.refId().equals("proposal-1")));
    assertTrue(workflowCompleted.capabilityRefs().contains(AgentAdminPromptRiskReviewService.READ_CAPABILITY));
    assertFalse(workflowCompleted.toString().contains("providerCredential="));

    var item = attentionRepository.find("tenant-1", "attention:worker-task:" + task.taskId() + ":task-state").orElseThrow();
    assertEquals(AttentionItemStatus.OPEN, item.status());
    assertEquals(AttentionCategory.SECURITY_REVIEW, item.category());
    assertTrue(item.summary().contains("requires human accept/reject review before activation"));
    assertTrue(item.summary().contains("no direct activation"));
    assertTrue(item.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("workstream_event") && ref.refId().equals(workerCompleted.eventId())));

    var accepted = promptRisk.acceptResult(tenantAdmin, task.taskId(), "advisory result accepted only", "corr-prompt-risk-accept");
    assertEquals(PromptRiskReviewTask.Status.ACCEPTED, accepted.status());
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("workflow.agent_admin.prompt_risk_review.result_accepted")));
    assertEquals(AttentionItemStatus.RESOLVED, attentionRepository.find("tenant-1", "attention:worker-task:" + task.taskId() + ":task-state").orElseThrow().status());
  }

  @Test
  void auditTraceSummaryLifecyclePublishesEventsAttentionAndReviewSurfaceRefs() {
    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, producers, clock);
    var publisher = new WorkstreamEventPublisher(eventRepository, consumer, clock);
    var runtime = new RecordingAuditTraceSummaryAutonomousAgentRuntime();
    var summaries = new AuditTraceSummaryService(new InMemoryTestAuditTraceSummaryTaskRepository(), resolver, clock, producers, publisher, runtime);

    var task = summaries.start(tenantAdmin, auditSummaryCommand("audit-summary-event"), "corr-audit-summary-start");

    assertEquals(AuditTraceSummaryTask.Status.QUEUED, task.status());
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("workflow.audit_trace.summary_started")));
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("worker.task.queued")));

    runtime.nextProjection = new AuditTraceSummaryAutonomousAgentRuntime.Projection(
        AuditTraceSummaryTask.Status.COMPLETED_REVIEW_REQUIRED,
        100,
        "Audit/Trace AutonomousAgent completed model-backed redacted summary; human Audit/Trace review is required and no audit, policy, user, provider, or authorization mutation occurred.",
        null,
        null,
        List.of("auditTraceSummaryEvidence.read", "readSkill:audit-trace-summary-review", "readReferenceDoc:audit-trace-summary-review"),
        List.of("audit_trace_summary_finding:finding-1:provider_readiness"),
        List.of("autonomous_task:" + task.autonomousAgentTaskId(), "trace-audit-summary-model-result"));

    var completed = summaries.read(tenantAdmin, task.taskId(), "corr-audit-summary-read");

    assertEquals(AuditTraceSummaryTask.Status.COMPLETED_REVIEW_REQUIRED, completed.status());
    var workflowCompleted = eventRepository.listTenant("tenant-1").stream().filter(event -> event.eventType().equals("workflow.audit_trace.summary_completed_review_required")).findFirst().orElseThrow();
    var workerCompleted = eventRepository.listTenant("tenant-1").stream().filter(event -> event.eventType().equals("worker.task.completed_review_required")).findFirst().orElseThrow();
    assertEquals("AuditTraceSummaryLifecycleEventPayload", workflowCompleted.payloadClass());
    assertEquals("surface-audit-trace-summary-review", workflowCompleted.targetSurfaceId());
    assertEquals("true", workflowCompleted.payload().get("noDirectMutation"));
    assertEquals("true", workflowCompleted.payload().get("redactionRequired"));
    assertTrue(workflowCompleted.sourceRefs().stream().anyMatch(ref -> ref.refType().equals("autonomous_task") && ref.refId().equals(task.autonomousAgentTaskId())));
    assertTrue(workflowCompleted.sourceRefs().stream().anyMatch(ref -> ref.refType().equals("audit_trace") && ref.refId().contains("auditTraceSummaryEvidence.read")));
    assertTrue(workflowCompleted.capabilityRefs().contains(AuditTraceSummaryService.READ_CAPABILITY));
    assertFalse(workflowCompleted.toString().contains("providerCredential="));
    assertFalse(workflowCompleted.toString().contains("api_key="));

    var item = attentionRepository.find("tenant-1", "attention:worker-task:" + task.taskId() + ":task-state").orElseThrow();
    assertEquals(AttentionItemStatus.OPEN, item.status());
    assertEquals(AttentionCategory.AUDIT_FAILURE_EVIDENCE, item.category());
    assertEquals("surface-audit-trace-summary-review", item.surfaceRef().targetSurfaceId());
    assertTrue(item.summary().contains("requires human accept/reject review"));
    assertTrue(item.summary().contains("no audit, policy, user, provider, or authorization mutation"));
    assertTrue(item.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("workstream_event") && ref.refId().equals(workerCompleted.eventId())));

    var accepted = summaries.acceptResult(tenantAdmin, task.taskId(), "advisory audit summary accepted only", "corr-audit-summary-accept");
    assertEquals(AuditTraceSummaryTask.Status.ACCEPTED, accepted.status());
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("workflow.audit_trace.summary_result_accepted")));
    assertEquals(AttentionItemStatus.RESOLVED, attentionRepository.find("tenant-1", "attention:worker-task:" + task.taskId() + ":task-state").orElseThrow().status());
  }

  @Test
  void governancePolicyImpactLifecyclePublishesEventsAttentionAndResultSurfaceRefs() {
    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, producers, clock);
    var publisher = new WorkstreamEventPublisher(eventRepository, consumer, clock);
    var runtime = new RecordingGovernancePolicyImpactAutonomousAgentRuntime();
    var governancePolicyRepository = new InMemoryTestGovernancePolicyRepository();
    var impacts = new GovernancePolicyImpactService(new InMemoryTestGovernancePolicyImpactTaskRepository(), governancePolicyRepository, resolver, clock, producers, publisher, runtime);
    var proposalId = governancePolicyProposal(governancePolicyRepository, "governance-impact-event");

    var task = impacts.start(tenantAdmin, governanceImpactCommand(proposalId, "governance-impact-event"), "corr-governance-impact-start");

    assertEquals(GovernancePolicyImpactTask.Status.QUEUED, task.status());
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("workflow.governance_policy.impact_analysis.started")));
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("worker.task.queued")));

    runtime.nextProjection = new GovernancePolicyImpactAutonomousAgentRuntime.Projection(
        GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED,
        100,
        "Governance/Policy AutonomousAgent completed model-backed policy impact analysis; human approval is required before any policy decision or activation.",
        null,
        null,
        List.of("governancePolicyEvidence.read", "readSkill:governance-policy-impact-analysis", "readReferenceDoc:governance-policy-impact-analysis"),
        List.of("governance_policy_impact_finding:finding-1:approval-gate"),
        List.of("autonomous_task:" + task.autonomousAgentTaskId(), "trace-governance-impact-model-result"));

    var completed = impacts.read(tenantAdmin, task.impactTaskId(), "corr-governance-impact-read");

    assertEquals(GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED, completed.status());
    var workflowCompleted = eventRepository.listTenant("tenant-1").stream().filter(event -> event.eventType().equals("workflow.governance_policy.impact_analysis.completed_review_required")).findFirst().orElseThrow();
    var workerCompleted = eventRepository.listTenant("tenant-1").stream().filter(event -> event.eventType().equals("worker.task.completed_review_required")).findFirst().orElseThrow();
    assertEquals("GovernancePolicyImpactLifecycleEventPayload", workflowCompleted.payloadClass());
    assertEquals("surface-governance-policy-impact-analysis-result", workflowCompleted.targetSurfaceId());
    assertEquals("true", workflowCompleted.payload().get("noDirectMutation"));
    assertEquals("true", workflowCompleted.payload().get("activationBlockedUntilHumanDecision"));
    assertTrue(workflowCompleted.sourceRefs().stream().anyMatch(ref -> ref.refType().equals("autonomous_task") && ref.refId().equals(task.autonomousAgentTaskId())));
    assertTrue(workflowCompleted.sourceRefs().stream().anyMatch(ref -> ref.refType().equals("governance_policy_proposal") && ref.refId().equals(proposalId)));
    assertTrue(workflowCompleted.capabilityRefs().contains(GovernancePolicyImpactService.READ_CAPABILITY));
    assertFalse(workflowCompleted.toString().contains("api_key="));

    var item = attentionRepository.find("tenant-1", "attention:worker-task:" + task.impactTaskId() + ":task-state").orElseThrow();
    assertEquals(AttentionItemStatus.OPEN, item.status());
    assertEquals(AttentionCategory.GOVERNANCE_APPROVAL, item.category());
    assertEquals("surface-governance-policy-impact-analysis-result", item.surfaceRef().targetSurfaceId());
    assertTrue(item.summary().contains("requires human review before any policy decision or activation"));
    assertTrue(item.summary().contains("no direct approval, activation, rollback, policy mutation"));
    assertTrue(item.sourceRefs().stream().anyMatch(ref -> ref.kind().equals("workstream_event") && ref.refId().equals(workerCompleted.eventId())));

    var accepted = impacts.acceptResult(tenantAdmin, task.impactTaskId(), "advisory impact result accepted only", "corr-governance-impact-accept");
    assertEquals(GovernancePolicyImpactTask.Status.ACCEPTED, accepted.status());
    assertTrue(eventRepository.listTenant("tenant-1").stream().anyMatch(event -> event.eventType().equals("workflow.governance_policy.impact_analysis.result_accepted")));
    assertEquals(AttentionItemStatus.RESOLVED, attentionRepository.find("tenant-1", "attention:worker-task:" + task.impactTaskId() + ":task-state").orElseThrow().status());
  }

  @Test
  void promptRiskProviderBlockedPublishesFailClosedEventAndAttention() {
    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, producers, clock);
    var publisher = new WorkstreamEventPublisher(eventRepository, consumer, clock);
    var promptRisk = new AgentAdminPromptRiskReviewService(new InMemoryTestPromptRiskReviewTaskRepository(), resolver, clock, producers, publisher);

    var task = promptRisk.start(tenantAdmin, promptRiskCommand("prompt-risk-blocked"), "corr-prompt-risk-blocked");

    assertEquals(PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, task.status());
    var event = eventRepository.listTenant("tenant-1").stream().filter(candidate -> candidate.eventType().equals("workflow.agent_admin.prompt_risk_review.blocked_provider_or_runtime")).findFirst().orElseThrow();
    assertEquals("blocked_provider_or_runtime:fail_closed:no_fake_success", event.payload().get("providerOrRuntimeState"));
    assertTrue(event.idempotencyKey().startsWith("workstream-event:workflow/process:workflow.agent_admin.prompt_risk_review.blocked_provider_or_runtime:tenant-1:none:"));
    var item = attentionRepository.find("tenant-1", "attention:worker-task:" + task.taskId() + ":task-state").orElseThrow();
    assertEquals(AttentionItemStatus.OPEN, item.status());
    assertEquals(AttentionCategory.WORKFLOW_BLOCKED, item.category());
    assertTrue(item.summary().contains("fails closed"));
    assertTrue(item.summary().contains("model-less prompt-risk findings"));
  }

  @Test
  void broaderGovernedLifecycleFamiliesPublishRefreshAndAttentionSafely() {
    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, producers, clock);
    var publisher = new WorkstreamEventPublisher(eventRepository, consumer, clock);

    var membershipEvent = publisher.publishGovernedLifecycle(
        "tenant-1",
        null,
        WorkstreamEventPublisher.EVENT_FAMILY_DOMAIN,
        "membership.role.changed",
        "membership",
        "membership-admin",
        "Tenant Admin membership role changed token=secret",
        "user_admin.view_overview",
        tenantAdmin.account().accountId(),
        "user-admin-agent",
        "surface-user-admin-users",
        "changed",
        Map.of("safeTitle", "Membership role change needs review", "safeSummary", "Role membership changed through backend-governed command."),
        Map.of(),
        "corr-membership-role");
    var supportEvent = publisher.publishGovernedLifecycle("tenant-1", null, WorkstreamEventPublisher.EVENT_FAMILY_DOMAIN, "support_access.granted", "membership", "membership-admin", "Support access grant", "tenant.support_access.manage", tenantAdmin.account().accountId(), "user-admin-agent", "surface-user-admin-user-detail", "granted", Map.of("safeSummary", "Support access grant is visible for audit review."), Map.of(), "corr-support-access");
    var artifactEvent = publisher.publishGovernedLifecycle("tenant-1", null, WorkstreamEventPublisher.EVENT_FAMILY_DOMAIN, "governed_artifact.tool_boundary.activated", "tool_boundary", "agent-admin-tool-boundary", "Tool boundary activation providerCredential=secret", "agent_admin.activate_behavior_change", tenantAdmin.account().accountId(), "agent-agent-admin", "surface-agent-admin-detail", "activated", Map.of("artifactKind", "tool_boundary", "safeSummary", "Tool boundary activation metadata only."), Map.of(), "corr-artifact");
    var simulationEvent = publisher.publishGovernedLifecycle("tenant-1", null, "governance/simulation", "policy.simulation.completed", "policy_simulation", "simulation-1", "Policy simulation", "governance.policy.simulate", tenantAdmin.account().accountId(), "agent-governance-policy", "surface-governance-policy-dashboard", "completed", Map.of("safeSummary", "Simulation evidence is ready; no policy was activated."), Map.of("attentionAction", "open"), "corr-policy-simulation");
    var exportEvent = publisher.publishGovernedLifecycle("tenant-1", null, "audit/export", "export.failed", "export_request", "export-1", "Audit export", "audit.trace.export", tenantAdmin.account().accountId(), "agent-audit-trace", "surface-audit-trace-dashboard", "failed", Map.of("safeSummary", "Export failed closed without leaking delivery credentials."), Map.of(), "corr-export");
    var notificationEvent = publisher.publishGovernedLifecycle("tenant-1", null, "notification/lifecycle", "notification.lifecycle.failed", "notification", "notification-1", "Notification delivery", "notification.delivery", tenantAdmin.account().accountId(), "agent-my-account", "surface-my-account-notification-center", "failed", Map.of("safeSummary", "Notification channel delivery failed closed."), Map.of(), "corr-notification");

    assertEquals(6, eventRepository.listTenant("tenant-1").size());
    assertEquals("GovernedLifecycleEventPayload", artifactEvent.payloadClass());
    assertFalse(artifactEvent.toString().contains("providerCredential=secret"));
    assertFalse(membershipEvent.toString().contains("token=secret"));
    assertTrue(eventRepository.listTenant("tenant-1").stream().allMatch(event -> event.sourceRefs().stream().anyMatch(ref -> ref.refType().equals("capability"))));
    assertTrue(attentionRepository.find("tenant-1", membershipEvent.projectionHints().get("attentionItemId")).orElseThrow().sourceRefs().stream().anyMatch(ref -> ref.kind().equals("workstream_event")));
    assertEquals(AttentionCategory.SECURITY_REVIEW, attentionRepository.find("tenant-1", supportEvent.projectionHints().get("attentionItemId")).orElseThrow().category());
    assertEquals(AttentionCategory.SECURITY_REVIEW, attentionRepository.find("tenant-1", artifactEvent.projectionHints().get("attentionItemId")).orElseThrow().category());
    assertEquals(AttentionCategory.GOVERNANCE_APPROVAL, attentionRepository.find("tenant-1", simulationEvent.projectionHints().get("attentionItemId")).orElseThrow().category());
    assertEquals(AttentionCategory.AUDIT_FAILURE_EVIDENCE, attentionRepository.find("tenant-1", exportEvent.projectionHints().get("attentionItemId")).orElseThrow().category());
    assertEquals(AttentionCategory.PROVIDER_READINESS, attentionRepository.find("tenant-1", notificationEvent.projectionHints().get("attentionItemId")).orElseThrow().category());

    var replay = publisher.publishGovernedLifecycle("tenant-1", null, WorkstreamEventPublisher.EVENT_FAMILY_DOMAIN, "membership.role.changed", "membership", "membership-admin", "Tenant Admin membership role changed", "user_admin.view_overview", tenantAdmin.account().accountId(), "user-admin-agent", "surface-user-admin-users", "changed", Map.of(), Map.of(), "corr-membership-role-replay");
    assertEquals(membershipEvent.eventId(), replay.eventId());
    assertEquals(6, eventRepository.listTenant("tenant-1").size());
    assertEquals(1, attentionRepository.find("tenant-1", membershipEvent.projectionHints().get("attentionItemId")).orElseThrow().sourceRefs().stream().filter(ref -> ref.kind().equals("workstream_event") && ref.refId().equals(membershipEvent.eventId())).count());
  }

  @Test
  void genericLifecycleConsumerRejectsMalformedScopeAndMissingCapabilityWithoutAttention() {
    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, new AttentionProducerService(attentionRepository, identityRepository, clock), clock);
    var event = new ai.first.domain.foundation.workstream.WorkstreamEventEnvelope(
        "evt-generic-cross-tenant",
        "membership.role.changed",
        WorkstreamEventPublisher.EVENT_FAMILY_DOMAIN,
        1,
        clock.instant(),
        clock.instant(),
        "tenant-1",
        null,
        Map.of("tenantId", "tenant-1", "capabilityIds", "user_admin.view_overview"),
        Map.of("actorType", "account", "accountId", tenantAdmin.account().accountId()),
        List.of(new ai.first.domain.foundation.workstream.WorkstreamEventSourceRef("membership", "membership-admin", "Membership", "user_admin.view_overview", "trace-generic-cross-tenant", "corr-generic-cross-tenant")),
        List.of("user_admin.view_overview"),
        "corr-generic-cross-tenant",
        "workstream-event:domain:membership.role.changed:tenant-1:none:membership-admin:changed",
        "membership-admin",
        List.of("trace-generic-cross-tenant"),
        "user-admin-agent",
        "surface-user-admin-users",
        "GovernedLifecycleEventPayload",
        Map.of("tenantId", "tenant-2", "sourceId", "membership-admin"),
        Map.of("browserSafe", "true"),
        Map.of("attentionItemId", "attention:workstream-event:membership-admin"));

    assertEquals(null, consumer.projectGovernedLifecycle(event));
    assertFalse(attentionRepository.find("tenant-1", "attention:workstream-event:membership-admin").isPresent());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(audit -> audit.actionType().equals("WORKSTREAM_EVENT_CONSUMER_DENIED") && audit.reasonCode().equals("scope-mismatch")));

    var missingCapability = new ai.first.domain.foundation.workstream.WorkstreamEventEnvelope(
        "evt-generic-missing-capability",
        "notification.lifecycle.failed",
        "notification/lifecycle",
        1,
        clock.instant(),
        clock.instant(),
        "tenant-1",
        null,
        Map.of("tenantId", "tenant-1", "capabilityIds", "audit.trace.read"),
        Map.of("actorType", "system", "accountId", "system"),
        List.of(new ai.first.domain.foundation.workstream.WorkstreamEventSourceRef("notification", "notification-1", "Notification", "audit.trace.read", "trace-generic-missing-capability", "corr-generic-missing-capability")),
        List.of("audit.trace.read"),
        "corr-generic-missing-capability",
        "workstream-event:notification/lifecycle:notification.lifecycle.failed:tenant-1:none:notification-1:failed",
        "notification-1",
        List.of("trace-generic-missing-capability"),
        "agent-my-account",
        "surface-my-account-notification-center",
        "GovernedLifecycleEventPayload",
        Map.of("tenantId", "tenant-1", "sourceId", "notification-1"),
        Map.of("browserSafe", "true"),
        Map.of("attentionItemId", "attention:workstream-event:notification-1"));

    assertEquals(null, consumer.projectGovernedLifecycle(missingCapability));
    assertFalse(attentionRepository.find("tenant-1", "attention:workstream-event:notification-1").isPresent());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(audit -> audit.actionType().equals("WORKSTREAM_EVENT_CONSUMER_DENIED") && audit.reasonCode().equals("missing-capability-ref")));
  }

  @Test
  void consumerRejectsCrossTenantSourceMismatchWithoutProjectingAttention() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("invite-cross-tenant", "cross@example.test"));
    var event = new WorkstreamEventPublisher(eventRepository, new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, new AttentionProducerService(attentionRepository, identityRepository, clock), clock), clock)
        .publishInvitationDelivery(invite, false, "delivery-1", "FAILED", "safe", "corr-cross-event");
    var sourceFromOtherTenant = new ai.first.domain.foundation.invitation.Invitation(
        invite.invitationId(), invite.normalizedEmail(), invite.scopeType(), "tenant-2", invite.customerId(), invite.requestedRoles(), invite.accountId(), invite.membershipId(), invite.status(), invite.deliveryStatus(), invite.deliveryAttempts(), invite.providerMessageIds(), invite.lastDeliveryErrorSummary(), invite.acceptanceContextId(), invite.tokenHash(), invite.expiresAt(), invite.acceptedAt(), invite.acceptedByWorkosSubject(), invite.revokedAt(), invite.revokedByAccountId(), invite.revokeReason(), invite.resendCount(), invite.createdByAccountId(), invite.createdAt(), invite.idempotencyKey(), "corr-cross-source");

    var consumer = new WorkstreamEventAttentionConsumer(new InMemoryTestAttentionRepository(), identityRepository, new AttentionProducerService(new InMemoryTestAttentionRepository(), identityRepository, clock), clock);
    var projected = consumer.project(event, sourceFromOtherTenant);

    assertEquals(null, projected);
    assertTrue(identityRepository.auditEvents().stream().anyMatch(audit -> audit.actionType().equals("WORKSTREAM_EVENT_CONSUMER_DENIED") && audit.reasonCode().equals("scope-mismatch")));
  }

  private static final class RecordingGovernancePolicyImpactAutonomousAgentRuntime implements GovernancePolicyImpactAutonomousAgentRuntime {
    private Projection nextProjection = Projection.unchanged();

    @Override
    public StartOutcome start(AuthContextResolver.ResolvedMe actor, GovernancePolicyImpactTask starterTask, String evidenceRequest, String correlationId) {
      return StartOutcome.queued("akka-task-" + starterTask.impactTaskId(), "Governance/Policy impact Akka AutonomousAgent task queued; backend projection is authoritative and no fake success is returned.", List.of("autonomous_task:akka-task-" + starterTask.impactTaskId()));
    }

    @Override
    public Projection project(GovernancePolicyImpactTask starterTask, String correlationId) {
      return nextProjection;
    }
  }

  private String governancePolicyProposal(InMemoryTestGovernancePolicyRepository repository, String key) {
    var governance = new GovernancePolicyService(repository, resolver, clock);
    var draft = governance.draftProposal(tenantAdmin, Map.of("rationale", "tighten approval boundary", "proposedContent", "change ToolPermissionBoundary approval gate"), key, key + "-draft");
    var proposalId = draft.surface().data().get("proposalId").toString();
    governance.submitProposal(tenantAdmin, Map.of("proposalId", proposalId), key + "-submit", key + "-submit-corr");
    return proposalId;
  }

  private GovernancePolicyImpactService.StartGovernancePolicyImpactCommand governanceImpactCommand(String proposalId, String key) {
    return new GovernancePolicyImpactService.StartGovernancePolicyImpactCommand(
        proposalId,
        "policy-human-approval",
        "proposalId=" + proposalId + " focus=approval gates, redaction, and ToolPermissionBoundary",
        List.of("governance.policy.approve", "governance.policy.activate"),
        List.of("ToolPermissionBoundary", "AgentDefinition"),
        List.of("proposal-evidence:" + proposalId),
        key);
  }

  private static final class RecordingAuditTraceSummaryAutonomousAgentRuntime implements AuditTraceSummaryAutonomousAgentRuntime {
    private Projection nextProjection = Projection.unchanged();

    @Override
    public StartOutcome start(AuthContextResolver.ResolvedMe actor, AuditTraceSummaryTask starterTask, String correlationId) {
      return StartOutcome.queued("akka-task-" + starterTask.taskId(), "Audit/Trace Summary Akka AutonomousAgent task queued; backend projection is authoritative and no fake success is returned.", List.of("autonomous_task:akka-task-" + starterTask.taskId()));
    }

    @Override
    public Projection project(AuditTraceSummaryTask starterTask, String correlationId) {
      return nextProjection;
    }
  }

  private AuditTraceSummaryService.StartAuditTraceSummaryCommand auditSummaryCommand(String key) {
    return new AuditTraceSummaryService.StartAuditTraceSummaryCommand(
        Instant.parse("2026-05-20T00:00:00Z"),
        Instant.parse("2026-05-25T00:00:00Z"),
        List.of("admin_audit", "provider_readiness", "agent_work", "workstream_event"),
        key);
  }

  private static final class RecordingPromptRiskAutonomousAgentRuntime implements PromptRiskAutonomousAgentRuntime {
    private Projection nextProjection = Projection.unchanged();

    @Override
    public StartOutcome start(AuthContextResolver.ResolvedMe actor, PromptRiskReviewTask starterTask, String correlationId) {
      return StartOutcome.queued("akka-task-" + starterTask.taskId(), "Prompt-risk Akka AutonomousAgent task queued; backend projection is authoritative.", List.of("autonomous_task:akka-task-" + starterTask.taskId()));
    }

    @Override
    public Projection project(PromptRiskReviewTask starterTask, String correlationId) {
      return nextProjection;
    }
  }

  private AgentAdminPromptRiskReviewService.StartPromptRiskReviewCommand promptRiskCommand(String key) {
    return new AgentAdminPromptRiskReviewService.StartPromptRiskReviewCommand(
        AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        "proposal-1",
        List.of(new PromptRiskReviewTask.BehaviorArtifactDelta(PromptRiskReviewTask.ArtifactKind.TOOL_PERMISSION_BOUNDARY, "agent-admin-tool-boundary", 1, 2, "add side-effecting grant", "diff:proposal-1", "before", "after")),
        List.of("proposal-evidence:proposal-1"),
        key);
  }

  private static final class RecordingAccessReviewAutonomousAgentRuntime implements AccessReviewAutonomousAgentRuntime {
    private Projection nextProjection = Projection.unchanged();

    @Override
    public StartOutcome start(AuthContextResolver.ResolvedMe actor, AccessReviewTask starterTask, String correlationId) {
      return StartOutcome.queued("akka-task-" + starterTask.taskId(), "Access-review Akka AutonomousAgent task queued; backend projection is authoritative.", List.of("autonomous_task:akka-task-" + starterTask.taskId()));
    }

    @Override
    public Projection project(AccessReviewTask starterTask, String correlationId) {
      return nextProjection;
    }
  }

  private InvitationService.CreateInvitationRequest inviteRequest(String key, String email) {
    return new InvitationService.CreateInvitationRequest(
        key,
        ScopeType.TENANT,
        "tenant-1",
        null,
        email,
        "Invited User",
        List.of(FoundationRole.TENANT_EMPLOYEE),
        clock.instant().plusSeconds(3600),
        "onboarding",
        "corr-" + key);
  }

  private void seedAccount(String email, String membershipId, String tenantId, List<FoundationRole> roles) {
    identityRepository.saveAccount(new Account(email, "workos-" + email, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, null, null, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, tenantId, null, roles, MembershipStatus.ACTIVE, false, null));
  }
}

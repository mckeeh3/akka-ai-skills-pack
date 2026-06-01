package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentAdminPromptRiskReviewService;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AuditTraceSummaryAutonomousAgentRuntime;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.LocalDemoPromptRiskReviewTaskRepository;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.PromptRiskAutonomousAgentRuntime;
import {{JAVA_BASE_PACKAGE}}.application.agentfoundation.AgentBehaviorSeedLoader;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptRiskReviewTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccessReviewTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AuditTraceSummaryTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionCategory;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionItemStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailDeliveryStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkstreamEventBackboneServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-30T12:00:00Z"), ZoneOffset.UTC);
  private LocalDemoIdentityRepository identityRepository;
  private LocalDemoInvitationRepository invitationRepository;
  private LocalDemoAttentionRepository attentionRepository;
  private LocalDemoWorkstreamEventRepository eventRepository;
  private LocalDemoAccessReviewTaskRepository accessReviewRepository;
  private AuthContextResolver resolver;
  private InvitationService invitations;
  private AttentionService attention;
  private AuthContextResolver.ResolvedMe tenantAdmin;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    invitationRepository = new LocalDemoInvitationRepository();
    attentionRepository = new LocalDemoAttentionRepository();
    eventRepository = new LocalDemoWorkstreamEventRepository();
    accessReviewRepository = new LocalDemoAccessReviewTaskRepository();
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
    assertEquals(1, attention.listWorkstreamItems(tenantAdmin, "agent-user-admin", "corr-list").size());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(audit -> audit.actionType().equals("WORKSTREAM_EVENT_CONSUMER_DUPLICATE") && audit.result() == {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent.Result.NO_OP));
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
    assertTrue(identityRepository.auditEvents().stream().anyMatch(audit -> audit.actionType().equals("WORKSTREAM_EVENT_CONSUMER_DUPLICATE") && audit.result() == {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent.Result.NO_OP));
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
    var promptRisk = new AgentAdminPromptRiskReviewService(new LocalDemoPromptRiskReviewTaskRepository(), resolver, clock, producers, publisher, runtime);

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
    var summaries = new AuditTraceSummaryService(new LocalDemoAuditTraceSummaryTaskRepository(), resolver, clock, producers, publisher, runtime);

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
  void promptRiskProviderBlockedPublishesFailClosedEventAndAttention() {
    var producers = new AttentionProducerService(attentionRepository, identityRepository, clock);
    var consumer = new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, producers, clock);
    var publisher = new WorkstreamEventPublisher(eventRepository, consumer, clock);
    var promptRisk = new AgentAdminPromptRiskReviewService(new LocalDemoPromptRiskReviewTaskRepository(), resolver, clock, producers, publisher);

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
  void consumerRejectsCrossTenantSourceMismatchWithoutProjectingAttention() {
    var invite = invitations.createInvitation(tenantAdmin, inviteRequest("invite-cross-tenant", "cross@example.test"));
    var event = new WorkstreamEventPublisher(eventRepository, new WorkstreamEventAttentionConsumer(attentionRepository, identityRepository, new AttentionProducerService(attentionRepository, identityRepository, clock), clock), clock)
        .publishInvitationDelivery(invite, false, "delivery-1", "FAILED", "safe", "corr-cross-event");
    var sourceFromOtherTenant = new {{JAVA_BASE_PACKAGE}}.domain.security.Invitation(
        invite.invitationId(), invite.normalizedEmail(), invite.scopeType(), "tenant-2", invite.customerId(), invite.requestedRoles(), invite.accountId(), invite.membershipId(), invite.status(), invite.deliveryStatus(), invite.deliveryAttempts(), invite.providerMessageIds(), invite.lastDeliveryErrorSummary(), invite.acceptanceContextId(), invite.tokenHash(), invite.expiresAt(), invite.acceptedAt(), invite.acceptedByWorkosSubject(), invite.revokedAt(), invite.revokedByAccountId(), invite.revokeReason(), invite.resendCount(), invite.createdByAccountId(), invite.createdAt(), invite.idempotencyKey(), "corr-cross-source");

    var consumer = new WorkstreamEventAttentionConsumer(new LocalDemoAttentionRepository(), identityRepository, new AttentionProducerService(new LocalDemoAttentionRepository(), identityRepository, clock), clock);
    var projected = consumer.project(event, sourceFromOtherTenant);

    assertEquals(null, projected);
    assertTrue(identityRepository.auditEvents().stream().anyMatch(audit -> audit.actionType().equals("WORKSTREAM_EVENT_CONSUMER_DENIED") && audit.reasonCode().equals("scope-mismatch")));
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
    identityRepository.putSettings(new UserSettings(email, UserSettings.UiMode.LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, tenantId, null, roles, MembershipStatus.ACTIVE, false, null));
  }
}

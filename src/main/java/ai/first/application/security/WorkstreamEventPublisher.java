package ai.first.application.security;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.agentfoundation.PromptRiskReviewTask;
import ai.first.domain.security.AccessReviewTask;
import ai.first.domain.security.AuditTraceSummaryTask;
import ai.first.domain.security.GovernancePolicyImpactTask;
import ai.first.domain.foundation.invitation.Invitation;
import ai.first.domain.security.MyAccountPersonalAttentionDigestTask;
import ai.first.domain.foundation.workstream.WorkstreamEventEnvelope;
import ai.first.domain.foundation.workstream.WorkstreamEventSourceRef;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Publishes selected starter domain transitions into the governed workstream event backbone. */
public final class WorkstreamEventPublisher {
  public static final String EVENT_FAMILY_DOMAIN = "domain";
  public static final String EVENT_FAMILY_WORKFLOW_PROCESS = "workflow/process";
  public static final String EVENT_FAMILY_TASK_WORKER = "task/worker";
  public static final String PAYLOAD_INVITATION_DELIVERY = "InvitationDeliveryEventPayload";
  public static final String PAYLOAD_ACCESS_REVIEW_LIFECYCLE = "AccessReviewLifecycleEventPayload";
  public static final String PAYLOAD_PROMPT_RISK_REVIEW_LIFECYCLE = "PromptRiskReviewLifecycleEventPayload";
  public static final String PAYLOAD_AUDIT_TRACE_SUMMARY_LIFECYCLE = "AuditTraceSummaryLifecycleEventPayload";
  public static final String PAYLOAD_GOVERNANCE_POLICY_IMPACT_LIFECYCLE = "GovernancePolicyImpactLifecycleEventPayload";
  public static final String PAYLOAD_MY_ACCOUNT_PERSONAL_ATTENTION_DIGEST_LIFECYCLE = "MyAccountPersonalAttentionDigestLifecycleEventPayload";
  public static final String PAYLOAD_GOVERNED_LIFECYCLE = "GovernedLifecycleEventPayload";

  private final WorkstreamEventRepository repository;
  private final WorkstreamEventAttentionConsumer attentionConsumer;
  private final Clock clock;

  public WorkstreamEventPublisher(WorkstreamEventRepository repository, WorkstreamEventAttentionConsumer attentionConsumer, Clock clock) {
    this.repository = Objects.requireNonNull(repository);
    this.attentionConsumer = Objects.requireNonNull(attentionConsumer);
    this.clock = Objects.requireNonNull(clock);
  }

  public WorkstreamEventEnvelope publishInvitationDelivery(Invitation invitation, boolean delivered, String deliveryAttemptId, String deliveryStatus, String safeErrorSummary, String correlationId) {
    var semanticTransition = delivered ? "sent" : "failed";
    var idempotencyKey = idempotencyKey(EVENT_FAMILY_DOMAIN, "invitation.delivery." + semanticTransition, invitation.tenantId(), invitation.customerId(), invitation.invitationId(), semanticTransition);
    var existing = repository.findByIdempotencyKey(invitation.tenantId(), idempotencyKey).orElse(null);
    var event = existing == null
        ? repository.publish(invitationDeliveryEnvelope(invitation, semanticTransition, deliveryAttemptId, safeErrorSummary, idempotencyKey, correlationId))
        : existing;
    attentionConsumer.project(event, invitation);
    return event;
  }

  public WorkstreamEventEnvelope publishAccessReviewLifecycle(AccessReviewTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    var eventType = "workflow.access_review." + semanticTransition;
    var event = publishAccessReviewEvent(task, semanticTransition, eventType, EVENT_FAMILY_WORKFLOW_PROCESS, capabilityId, actorAccountId, correlationId);
    publishAccessReviewTaskEvent(task, semanticTransition, capabilityId, actorAccountId, correlationId);
    return event;
  }

  public WorkstreamEventEnvelope publishAccessReviewTaskEvent(AccessReviewTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    var taskTransition = workerTaskTransition(task, semanticTransition);
    var eventType = "worker.task." + taskTransition;
    return publishAccessReviewEvent(task, taskTransition, eventType, EVENT_FAMILY_TASK_WORKER, capabilityId, actorAccountId, correlationId);
  }

  public WorkstreamEventEnvelope publishPromptRiskReviewLifecycle(PromptRiskReviewTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    var eventType = "workflow.agent_admin.prompt_risk_review." + semanticTransition;
    var event = publishPromptRiskReviewEvent(task, semanticTransition, eventType, EVENT_FAMILY_WORKFLOW_PROCESS, capabilityId, actorAccountId, correlationId);
    publishPromptRiskReviewTaskEvent(task, semanticTransition, capabilityId, actorAccountId, correlationId);
    return event;
  }

  public WorkstreamEventEnvelope publishPromptRiskReviewTaskEvent(PromptRiskReviewTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    var taskTransition = workerTaskTransition(task, semanticTransition);
    var eventType = "worker.task." + taskTransition;
    return publishPromptRiskReviewEvent(task, taskTransition, eventType, EVENT_FAMILY_TASK_WORKER, capabilityId, actorAccountId, correlationId);
  }

  public WorkstreamEventEnvelope publishAuditTraceSummaryLifecycle(AuditTraceSummaryTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    var eventType = "workflow.audit_trace.summary_" + semanticTransition;
    var event = publishAuditTraceSummaryEvent(task, semanticTransition, eventType, EVENT_FAMILY_WORKFLOW_PROCESS, capabilityId, actorAccountId, correlationId);
    publishAuditTraceSummaryTaskEvent(task, semanticTransition, capabilityId, actorAccountId, correlationId);
    return event;
  }

  public WorkstreamEventEnvelope publishAuditTraceSummaryTaskEvent(AuditTraceSummaryTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    var taskTransition = workerTaskTransition(task, semanticTransition);
    var eventType = "worker.task." + taskTransition;
    return publishAuditTraceSummaryEvent(task, taskTransition, eventType, EVENT_FAMILY_TASK_WORKER, capabilityId, actorAccountId, correlationId);
  }

  public WorkstreamEventEnvelope publishGovernancePolicyImpactLifecycle(GovernancePolicyImpactTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    var eventType = "workflow.governance_policy.impact_analysis." + semanticTransition;
    var event = publishGovernancePolicyImpactEvent(task, semanticTransition, eventType, EVENT_FAMILY_WORKFLOW_PROCESS, capabilityId, actorAccountId, correlationId);
    publishGovernancePolicyImpactTaskEvent(task, semanticTransition, capabilityId, actorAccountId, correlationId);
    return event;
  }

  public WorkstreamEventEnvelope publishGovernancePolicyImpactTaskEvent(GovernancePolicyImpactTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    var taskTransition = workerTaskTransition(task, semanticTransition);
    var eventType = "worker.task." + taskTransition;
    return publishGovernancePolicyImpactEvent(task, taskTransition, eventType, EVENT_FAMILY_TASK_WORKER, capabilityId, actorAccountId, correlationId);
  }

  public WorkstreamEventEnvelope publishMyAccountPersonalAttentionDigestLifecycle(MyAccountPersonalAttentionDigestTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    var eventType = "workflow.my_account.personal_attention_digest." + semanticTransition;
    var event = publishMyAccountPersonalAttentionDigestEvent(task, semanticTransition, eventType, EVENT_FAMILY_WORKFLOW_PROCESS, capabilityId, actorAccountId, correlationId);
    publishMyAccountPersonalAttentionDigestTaskEvent(task, semanticTransition, capabilityId, actorAccountId, correlationId);
    return event;
  }

  public WorkstreamEventEnvelope publishMyAccountPersonalAttentionDigestTaskEvent(MyAccountPersonalAttentionDigestTask task, String semanticTransition, String capabilityId, String actorAccountId, String correlationId) {
    var taskTransition = workerTaskTransition(task, semanticTransition);
    var eventType = "worker.task." + taskTransition;
    return publishMyAccountPersonalAttentionDigestEvent(task, taskTransition, eventType, EVENT_FAMILY_TASK_WORKER, capabilityId, actorAccountId, correlationId);
  }

  public WorkstreamEventEnvelope publishGovernedLifecycle(
      String tenantId,
      String customerId,
      String eventFamily,
      String eventType,
      String sourceRefType,
      String sourceId,
      String sourceLabel,
      String capabilityId,
      String actorAccountId,
      String owningWorkstreamId,
      String targetSurfaceId,
      String semanticTransition,
      Map<String, String> safePayload,
      Map<String, String> projectionHints,
      String correlationId) {
    var safeFamily = safe(eventFamily, EVENT_FAMILY_DOMAIN);
    var safeSourceId = safe(sourceId, eventType);
    var idempotencyKey = idempotencyKey(safeFamily, eventType, tenantId, customerId, safeSourceId, safe(semanticTransition, "changed"));
    var existing = repository.findByIdempotencyKey(tenantId, idempotencyKey).orElse(null);
    var event = existing == null
        ? repository.publish(governedLifecycleEnvelope(tenantId, customerId, safeFamily, eventType, sourceRefType, safeSourceId, sourceLabel, capabilityId, actorAccountId, owningWorkstreamId, targetSurfaceId, semanticTransition, safePayload, projectionHints, idempotencyKey, correlationId))
        : existing;
    attentionConsumer.projectGovernedLifecycle(event);
    return event;
  }

  private WorkstreamEventEnvelope publishAccessReviewEvent(AccessReviewTask task, String semanticTransition, String eventType, String eventFamily, String capabilityId, String actorAccountId, String correlationId) {
    var idempotencyKey = idempotencyKey(eventFamily, eventType, task.tenantId(), task.customerId(), task.taskId(), semanticTransition);
    var existing = repository.findByIdempotencyKey(task.tenantId(), idempotencyKey).orElse(null);
    var event = existing == null
        ? repository.publish(accessReviewLifecycleEnvelope(task, semanticTransition, eventType, eventFamily, capabilityId, actorAccountId, idempotencyKey, correlationId))
        : existing;
    attentionConsumer.project(event, task);
    return event;
  }

  private WorkstreamEventEnvelope governedLifecycleEnvelope(
      String tenantId,
      String customerId,
      String eventFamily,
      String eventType,
      String sourceRefType,
      String sourceId,
      String sourceLabel,
      String capabilityId,
      String actorAccountId,
      String owningWorkstreamId,
      String targetSurfaceId,
      String semanticTransition,
      Map<String, String> safePayload,
      Map<String, String> projectionHints,
      String idempotencyKey,
      String correlationId) {
    var eventId = "evt-" + stableSuffix(idempotencyKey);
    var traceId = "trace-" + stableSuffix(eventId + ":" + correlationId);
    var now = Instant.now(clock);
    var safeCapability = safe(capabilityId, "workstream.event.read");
    var payload = new java.util.LinkedHashMap<String, String>();
    payload.put("tenantId", tenantId);
    payload.put("customerId", safe(customerId, ""));
    payload.put("sourceId", sourceId);
    payload.put("semanticTransition", safe(semanticTransition, "changed"));
    payload.putAll(safePayload == null ? Map.of() : safePayload);
    var hints = new java.util.LinkedHashMap<String, String>();
    hints.put("attentionItemId", "attention:workstream-event:" + stableSuffix(eventFamily) + ":" + stableSuffix(sourceId));
    hints.put("attentionAction", terminalLifecycleEvent(eventType) ? "resolve" : "open");
    if (projectionHints != null) hints.putAll(projectionHints);
    return new WorkstreamEventEnvelope(
        eventId,
        eventType,
        eventFamily,
        1,
        now,
        now,
        tenantId,
        customerId,
        Map.of("scopeType", customerId == null || customerId.isBlank() ? "TENANT" : "CUSTOMER", "tenantId", tenantId, "customerId", safe(customerId, ""), "capabilityIds", safeCapability),
        Map.of("actorType", actorAccountId == null || actorAccountId.isBlank() ? "system" : "account", "accountId", safe(actorAccountId, "system"), "label", "Governed lifecycle event publisher"),
        List.of(
            new WorkstreamEventSourceRef(safe(sourceRefType, "governed_lifecycle"), sourceId, redact(safe(sourceLabel, "Governed lifecycle " + eventType)), safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("capability", safeCapability, "Governed lifecycle capability", safeCapability, "trace-capability-" + stableSuffix(safeCapability), correlationId)),
        List.of(safeCapability),
        correlationId,
        idempotencyKey,
        sourceId,
        List.of(traceId),
        safe(owningWorkstreamId, "agent-my-account"),
        safe(targetSurfaceId, "surface-my-account-dashboard"),
        PAYLOAD_GOVERNED_LIFECYCLE,
        Map.copyOf(payload),
        Map.of("browserSafe", "true", "omitted", "rawPrompt,hiddenPromptText,rawToolPayload,rawJwt,invitationToken,providerSecret,providerCredential,crossTenantEvidence", "minimumRedactionLevel", "FULL"),
        Map.copyOf(hints));
  }

  private WorkstreamEventEnvelope invitationDeliveryEnvelope(Invitation invitation, String semanticTransition, String deliveryAttemptId, String safeErrorSummary, String idempotencyKey, String correlationId) {
    var eventType = "invitation.delivery." + semanticTransition;
    var eventId = "evt-" + stableSuffix(idempotencyKey);
    var traceId = "trace-" + stableSuffix(eventId + ":" + correlationId);
    var now = Instant.now(clock);
    var capabilityId = "user_admin.invitation.delivery";
    return new WorkstreamEventEnvelope(
        eventId,
        eventType,
        EVENT_FAMILY_DOMAIN,
        1,
        now,
        now,
        invitation.tenantId(),
        invitation.customerId(),
        Map.of(
            "scopeType", invitation.scopeType().name(),
            "tenantId", invitation.tenantId(),
            "customerId", safe(invitation.customerId(), ""),
            "capabilityIds", capabilityId + ",secure-tenant-user-foundation"),
        Map.of("actorType", "provider", "accountId", "system", "label", "Invitation delivery provider"),
        List.of(
            new WorkstreamEventSourceRef("domain_event", invitation.invitationId(), "Invitation delivery " + semanticTransition + " for " + invitation.normalizedEmail(), capabilityId, traceId, correlationId),
            new WorkstreamEventSourceRef("capability", capabilityId, "Invitation delivery capability", capabilityId, "trace-capability-" + stableSuffix(capabilityId), correlationId)),
        List.of(capabilityId, "secure-tenant-user-foundation"),
        correlationId,
        idempotencyKey,
        deliveryAttemptId,
        List.of(traceId),
        "agent-user-admin",
        "surface-user-admin-invitation-panel",
        PAYLOAD_INVITATION_DELIVERY,
        Map.of(
            "invitationId", invitation.invitationId(),
            "deliveryAttemptId", safe(deliveryAttemptId, ""),
            "deliveryStatus", semanticTransition,
            "providerDeliveryStatus", safe(invitation.deliveryStatus().name().toLowerCase(java.util.Locale.ROOT), ""),
            "normalizedEmail", invitation.normalizedEmail(),
            "attempts", Integer.toString(invitation.deliveryAttempts()),
            "safeErrorSummary", redact(safeErrorSummary)),
        Map.of("browserSafe", "true", "omitted", "rawToken,tokenHash,providerSecret", "minimumRedactionLevel", "FULL"),
        Map.of("attentionCategory", "INVITATION_DELIVERY", "attentionItemId", "attention:user-admin:invitation-delivery:" + invitation.invitationId()));
  }

  private WorkstreamEventEnvelope accessReviewLifecycleEnvelope(AccessReviewTask task, String semanticTransition, String eventType, String eventFamily, String capabilityId, String actorAccountId, String idempotencyKey, String correlationId) {
    var eventId = "evt-" + stableSuffix(idempotencyKey);
    var traceId = firstTraceRef(task, "trace-" + stableSuffix(eventId + ":" + correlationId));
    var now = Instant.now(clock);
    var safeCapability = safe(capabilityId, UserAdminAccessReviewService.READ_CAPABILITY);
    return new WorkstreamEventEnvelope(
        eventId,
        eventType,
        eventFamily,
        1,
        task.updatedAt() == null ? now : task.updatedAt(),
        now,
        task.tenantId(),
        task.customerId(),
        Map.of(
            "scopeType", task.scopeType().name(),
            "tenantId", task.tenantId(),
            "customerId", safe(task.customerId(), ""),
            "capabilityIds", safeCapability + ",secure-tenant-user-foundation"),
        Map.of("actorType", actorAccountId == null || actorAccountId.isBlank() ? "worker" : "account", "accountId", safe(actorAccountId, "system"), "label", "User Admin access-review lifecycle"),
        List.of(
            new WorkstreamEventSourceRef("workflow", task.taskId(), "Access-review workflow " + semanticTransition, safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("autonomous_task", safe(task.autonomousAgentTaskId(), task.taskId()), "Access-review task state " + task.status().name().toLowerCase(java.util.Locale.ROOT), safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("capability", safeCapability, "Access-review lifecycle capability", safeCapability, "trace-capability-" + stableSuffix(safeCapability), correlationId)),
        List.of(safeCapability, "secure-tenant-user-foundation"),
        correlationId,
        idempotencyKey,
        task.idempotencyKey(),
        List.of(traceId),
        "agent-user-admin",
        "surface-user-admin-access-review",
        PAYLOAD_ACCESS_REVIEW_LIFECYCLE,
        Map.of(
            "taskId", task.taskId(),
            "autonomousAgentTaskId", safe(task.autonomousAgentTaskId(), ""),
            "status", task.status().name().toLowerCase(java.util.Locale.ROOT),
            "semanticTransition", semanticTransition,
            "taskLifecycleEventType", eventType,
            "progressPercent", Integer.toString(task.progressPercent()),
            "blockerCode", safe(task.blockerCode(), ""),
            "decision", safe(task.decision(), ""),
            "safeSummary", redact(safe(task.summary(), "")),
            "providerOrRuntimeState", task.status() == AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime:fail_closed" : "state_recorded_without_direct_access_mutation"),
        Map.of("browserSafe", "true", "omitted", "rawPrompt,rawToolPayload,providerSecret,providerCredential", "minimumRedactionLevel", "FULL"),
        Map.of("attentionCategory", "WORKFLOW_BLOCKED", "attentionItemId", "attention:worker-task:" + task.taskId() + ":task-state"));
  }

  private WorkstreamEventEnvelope publishPromptRiskReviewEvent(PromptRiskReviewTask task, String semanticTransition, String eventType, String eventFamily, String capabilityId, String actorAccountId, String correlationId) {
    var idempotencyKey = idempotencyKey(eventFamily, eventType, task.tenantId(), task.customerId(), task.taskId(), semanticTransition);
    var existing = repository.findByIdempotencyKey(task.tenantId(), idempotencyKey).orElse(null);
    var event = existing == null
        ? repository.publish(promptRiskReviewLifecycleEnvelope(task, semanticTransition, eventType, eventFamily, capabilityId, actorAccountId, idempotencyKey, correlationId))
        : existing;
    attentionConsumer.project(event, task);
    return event;
  }

  private WorkstreamEventEnvelope promptRiskReviewLifecycleEnvelope(PromptRiskReviewTask task, String semanticTransition, String eventType, String eventFamily, String capabilityId, String actorAccountId, String idempotencyKey, String correlationId) {
    var eventId = "evt-" + stableSuffix(idempotencyKey);
    var traceId = firstTraceRef(task, "trace-" + stableSuffix(eventId + ":" + correlationId));
    var now = Instant.now(clock);
    var safeCapability = safe(capabilityId, "agent_admin.prompt_risk_review.read");
    return new WorkstreamEventEnvelope(
        eventId,
        eventType,
        eventFamily,
        1,
        task.updatedAt() == null ? now : task.updatedAt(),
        now,
        task.tenantId(),
        task.customerId(),
        Map.of(
            "scopeType", task.customerId() == null ? "TENANT" : "CUSTOMER",
            "tenantId", task.tenantId(),
            "customerId", safe(task.customerId(), ""),
            "capabilityIds", safeCapability + ",agent_admin.prompt_risk_review.read"),
        Map.of("actorType", actorAccountId == null || actorAccountId.isBlank() ? "worker" : "account", "accountId", safe(actorAccountId, "system"), "label", "Agent Admin prompt-risk review lifecycle"),
        List.of(
            new WorkstreamEventSourceRef("workflow", task.taskId(), "Prompt-risk review workflow " + semanticTransition, safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("autonomous_task", safe(task.autonomousAgentTaskId(), task.taskId()), "Prompt-risk AutonomousAgent task state " + task.status().name().toLowerCase(java.util.Locale.ROOT), safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("agent_definition", task.targetAgentDefinitionId(), "Target governed managed-agent definition", safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("behavior_proposal", task.proposalId(), "Behavior change proposal under prompt-risk review", safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("capability", safeCapability, "Prompt-risk review capability", safeCapability, "trace-capability-" + stableSuffix(safeCapability), correlationId)),
        List.of(safeCapability, "agent_admin.prompt_risk_review.read"),
        correlationId,
        idempotencyKey,
        task.idempotencyKey(),
        List.of(traceId),
        "agent-agent-admin",
        "surface-agent-admin-prompt-risk-review",
        PAYLOAD_PROMPT_RISK_REVIEW_LIFECYCLE,
        Map.ofEntries(
            Map.entry("taskId", task.taskId()),
            Map.entry("autonomousAgentTaskId", safe(task.autonomousAgentTaskId(), "")),
            Map.entry("targetAgentDefinitionId", task.targetAgentDefinitionId()),
            Map.entry("proposalId", task.proposalId()),
            Map.entry("status", task.status().name().toLowerCase(java.util.Locale.ROOT)),
            Map.entry("semanticTransition", semanticTransition),
            Map.entry("taskLifecycleEventType", eventType),
            Map.entry("progressPercent", Integer.toString(task.progressPercent())),
            Map.entry("blockerCode", safe(task.blockerCode(), "")),
            Map.entry("decision", safe(task.decision(), "")),
            Map.entry("safeSummary", redact(safe(task.summary(), ""))),
            Map.entry("providerOrRuntimeState", task.status() == PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime:fail_closed:no_fake_success" : "state_recorded_without_behavior_artifact_mutation"),
            Map.entry("noDirectMutation", "true"),
            Map.entry("activationBlockedUntilHumanDecision", "true")),
        Map.of("browserSafe", "true", "omitted", "rawPrompt,rawSkillDoc,rawReferenceDoc,rawToolPayload,providerSecret,providerCredential", "minimumRedactionLevel", "FULL"),
        Map.of("attentionCategory", "WORKFLOW_BLOCKED", "attentionItemId", "attention:worker-task:" + task.taskId() + ":task-state"));
  }

  private WorkstreamEventEnvelope publishAuditTraceSummaryEvent(AuditTraceSummaryTask task, String semanticTransition, String eventType, String eventFamily, String capabilityId, String actorAccountId, String correlationId) {
    var idempotencyKey = idempotencyKey(eventFamily, eventType, task.tenantId(), task.customerId(), task.taskId(), semanticTransition);
    var existing = repository.findByIdempotencyKey(task.tenantId(), idempotencyKey).orElse(null);
    var event = existing == null
        ? repository.publish(auditTraceSummaryLifecycleEnvelope(task, semanticTransition, eventType, eventFamily, capabilityId, actorAccountId, idempotencyKey, correlationId))
        : existing;
    attentionConsumer.project(event, task);
    return event;
  }

  private WorkstreamEventEnvelope auditTraceSummaryLifecycleEnvelope(AuditTraceSummaryTask task, String semanticTransition, String eventType, String eventFamily, String capabilityId, String actorAccountId, String idempotencyKey, String correlationId) {
    var eventId = "evt-" + stableSuffix(idempotencyKey);
    var traceId = firstTraceRef(task, "trace-" + stableSuffix(eventId + ":" + correlationId));
    var now = Instant.now(clock);
    var safeCapability = safe(capabilityId, AuditTraceSummaryService.READ_CAPABILITY);
    return new WorkstreamEventEnvelope(
        eventId,
        eventType,
        eventFamily,
        1,
        task.updatedAt() == null ? now : task.updatedAt(),
        now,
        task.tenantId(),
        task.customerId(),
        Map.of(
            "scopeType", task.customerId() == null ? "TENANT" : "CUSTOMER",
            "tenantId", task.tenantId(),
            "customerId", safe(task.customerId(), ""),
            "capabilityIds", safeCapability + "," + AuditTraceSummaryService.READ_CAPABILITY),
        Map.of("actorType", actorAccountId == null || actorAccountId.isBlank() ? "worker" : "account", "accountId", safe(actorAccountId, "system"), "label", "Audit/Trace summary lifecycle"),
        List.of(
            new WorkstreamEventSourceRef("workflow", task.taskId(), "Audit/Trace summary workflow " + semanticTransition, safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("autonomous_task", safe(task.autonomousAgentTaskId(), task.taskId()), "Audit/Trace summary task state " + task.status().name().toLowerCase(java.util.Locale.ROOT), safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("audit_trace", String.join(",", task.evidenceRefs()), "Redacted Audit/Trace summary evidence refs", AuditTraceSummaryService.OPEN_EVIDENCE_CAPABILITY, traceId, correlationId),
            new WorkstreamEventSourceRef("capability", safeCapability, "Audit/Trace summary capability", safeCapability, "trace-capability-" + stableSuffix(safeCapability), correlationId)),
        List.of(safeCapability, AuditTraceSummaryService.READ_CAPABILITY),
        correlationId,
        idempotencyKey,
        task.idempotencyKey(),
        List.of(traceId),
        "agent-audit-trace",
        task.status() == AuditTraceSummaryTask.Status.COMPLETED_REVIEW_REQUIRED ? "surface-audit-trace-summary-review" : "surface-audit-trace-summary-progress",
        PAYLOAD_AUDIT_TRACE_SUMMARY_LIFECYCLE,
        Map.ofEntries(
            Map.entry("taskId", task.taskId()),
            Map.entry("autonomousAgentTaskId", safe(task.autonomousAgentTaskId(), "")),
            Map.entry("status", task.status().name().toLowerCase(java.util.Locale.ROOT)),
            Map.entry("semanticTransition", semanticTransition),
            Map.entry("taskLifecycleEventType", eventType),
            Map.entry("progressPercent", Integer.toString(task.progressPercent())),
            Map.entry("blockerCode", safe(task.blockerCode(), "")),
            Map.entry("decision", safe(task.decision(), "")),
            Map.entry("safeSummary", redact(safe(task.summary(), ""))),
            Map.entry("providerOrRuntimeState", task.status() == AuditTraceSummaryTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime:fail_closed:no_fake_success" : "state_recorded_without_audit_or_policy_mutation"),
            Map.entry("noDirectMutation", "true"),
            Map.entry("redactionRequired", "true")),
        Map.of("browserSafe", "true", "omitted", "rawPrompt,rawToolPayload,rawJwt,invitationToken,providerSecret,providerCredential,crossTenantEvidence", "minimumRedactionLevel", "FULL"),
        Map.of("attentionCategory", "WORKFLOW_BLOCKED", "attentionItemId", "attention:worker-task:" + task.taskId() + ":task-state"));
  }

  private WorkstreamEventEnvelope publishMyAccountPersonalAttentionDigestEvent(MyAccountPersonalAttentionDigestTask task, String semanticTransition, String eventType, String eventFamily, String capabilityId, String actorAccountId, String correlationId) {
    var idempotencyKey = idempotencyKey(eventFamily, eventType, task.tenantId(), task.customerId(), task.digestTaskId(), semanticTransition);
    var existing = repository.findByIdempotencyKey(task.tenantId(), idempotencyKey).orElse(null);
    var event = existing == null
        ? repository.publish(myAccountPersonalAttentionDigestEnvelope(task, semanticTransition, eventType, eventFamily, capabilityId, actorAccountId, idempotencyKey, correlationId))
        : existing;
    attentionConsumer.project(event, task);
    return event;
  }

  private WorkstreamEventEnvelope publishGovernancePolicyImpactEvent(GovernancePolicyImpactTask task, String semanticTransition, String eventType, String eventFamily, String capabilityId, String actorAccountId, String correlationId) {
    var idempotencyKey = idempotencyKey(eventFamily, eventType, task.tenantId(), task.customerId(), task.impactTaskId(), semanticTransition);
    var existing = repository.findByIdempotencyKey(task.tenantId(), idempotencyKey).orElse(null);
    var event = existing == null
        ? repository.publish(governancePolicyImpactLifecycleEnvelope(task, semanticTransition, eventType, eventFamily, capabilityId, actorAccountId, idempotencyKey, correlationId))
        : existing;
    attentionConsumer.project(event, task);
    return event;
  }

  private WorkstreamEventEnvelope myAccountPersonalAttentionDigestEnvelope(MyAccountPersonalAttentionDigestTask task, String semanticTransition, String eventType, String eventFamily, String capabilityId, String actorAccountId, String idempotencyKey, String correlationId) {
    var eventId = "evt-" + stableSuffix(idempotencyKey);
    var traceId = firstTraceRef(task, "trace-" + stableSuffix(eventId + ":" + correlationId));
    var now = Instant.now(clock);
    var safeCapability = safe(capabilityId, MyAccountPersonalAttentionDigestService.READ_CAPABILITY);
    var completed = task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED || task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_EMPTY;
    return new WorkstreamEventEnvelope(
        eventId,
        eventType,
        eventFamily,
        1,
        task.updatedAt() == null ? now : task.updatedAt(),
        now,
        task.tenantId(),
        task.customerId(),
        Map.of("scopeType", task.customerId() == null ? "TENANT" : "CUSTOMER", "tenantId", task.tenantId(), "customerId", safe(task.customerId(), ""), "capabilityIds", safeCapability + "," + MyAccountService.LIST_PERSONAL_ATTENTION_CAPABILITY + "," + AttentionService.LIST_MY_ACCOUNT_ITEMS_TOOL),
        Map.of("actorType", actorAccountId == null || actorAccountId.isBlank() ? "worker" : "account", "accountId", safe(actorAccountId, "system"), "label", "My Account personal attention digest lifecycle"),
        List.of(
            new WorkstreamEventSourceRef("workflow", task.digestTaskId(), "My Account personal attention digest workflow " + semanticTransition, safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("autonomous_task", safe(task.autonomousAgentTaskId(), task.digestTaskId()), "My Account personal attention digest AutonomousAgent task state " + task.status().name().toLowerCase(java.util.Locale.ROOT), safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("attention_item", String.join(",", task.evidenceRefs().stream().filter(ref -> ref.startsWith("attention_item:")).toList()), "Authorized personal attention evidence refs only", MyAccountPersonalAttentionDigestService.OPEN_EVIDENCE_CAPABILITY, traceId, correlationId),
            new WorkstreamEventSourceRef("capability", safeCapability, "My Account personal attention digest capability", safeCapability, "trace-capability-" + stableSuffix(safeCapability), correlationId)),
        List.of(safeCapability, MyAccountService.LIST_PERSONAL_ATTENTION_CAPABILITY, AttentionService.LIST_MY_ACCOUNT_ITEMS_TOOL),
        correlationId,
        idempotencyKey,
        task.idempotencyKey(),
        List.of(traceId),
        "agent-my-account",
        completed ? "surface-my-account-personal-attention-digest-result" : (task.status() == MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "surface-my-account-personal-attention-digest-blocked" : "surface-my-account-personal-attention-digest-progress"),
        PAYLOAD_MY_ACCOUNT_PERSONAL_ATTENTION_DIGEST_LIFECYCLE,
        Map.ofEntries(
            Map.entry("digestTaskId", task.digestTaskId()),
            Map.entry("autonomousAgentTaskId", safe(task.autonomousAgentTaskId(), "")),
            Map.entry("status", task.status().name().toLowerCase(java.util.Locale.ROOT)),
            Map.entry("semanticTransition", semanticTransition),
            Map.entry("taskLifecycleEventType", eventType),
            Map.entry("progressPercent", Integer.toString(task.progressPercent())),
            Map.entry("authorizedAttentionCount", Integer.toString(task.authorizedAttentionCount())),
            Map.entry("blockerCode", safe(task.blockerCode(), "")),
            Map.entry("decision", safe(task.decision(), "")),
            Map.entry("safeSummary", redact(safe(task.summary(), ""))),
            Map.entry("providerOrRuntimeState", task.status() == MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime:fail_closed:no_fake_success" : "state_recorded_without_source_attention_mutation"),
            Map.entry("noDirectMutation", "true"),
            Map.entry("redactionRequired", "true")),
        Map.of("browserSafe", "true", "omitted", "rawPrompt,hiddenPromptText,rawToolPayload,providerSecret,providerCredential,rawJwt,invitationToken,crossTenantEvidence,hiddenWorkstreamIds", "minimumRedactionLevel", "FULL"),
        Map.of("attentionCategory", "WORKFLOW_BLOCKED", "attentionItemId", "attention:worker-task:" + task.digestTaskId() + ":task-state"));
  }

  private WorkstreamEventEnvelope governancePolicyImpactLifecycleEnvelope(GovernancePolicyImpactTask task, String semanticTransition, String eventType, String eventFamily, String capabilityId, String actorAccountId, String idempotencyKey, String correlationId) {
    var eventId = "evt-" + stableSuffix(idempotencyKey);
    var traceId = firstTraceRef(task, "trace-" + stableSuffix(eventId + ":" + correlationId));
    var now = Instant.now(clock);
    var safeCapability = safe(capabilityId, GovernancePolicyImpactService.READ_CAPABILITY);
    return new WorkstreamEventEnvelope(
        eventId,
        eventType,
        eventFamily,
        1,
        task.updatedAt() == null ? now : task.updatedAt(),
        now,
        task.tenantId(),
        task.customerId(),
        Map.of("scopeType", task.customerId() == null ? "TENANT" : "CUSTOMER", "tenantId", task.tenantId(), "customerId", safe(task.customerId(), ""), "capabilityIds", safeCapability + "," + GovernancePolicyImpactService.READ_CAPABILITY),
        Map.of("actorType", actorAccountId == null || actorAccountId.isBlank() ? "worker" : "account", "accountId", safe(actorAccountId, "system"), "label", "Governance/Policy impact lifecycle"),
        List.of(
            new WorkstreamEventSourceRef("workflow", task.impactTaskId(), "Governance/Policy impact workflow " + semanticTransition, safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("autonomous_task", safe(task.autonomousAgentTaskId(), task.impactTaskId()), "Governance/Policy impact AutonomousAgent task state " + task.status().name().toLowerCase(java.util.Locale.ROOT), safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("governance_policy_proposal", task.proposalId(), "Policy proposal under impact analysis", safeCapability, traceId, correlationId),
            new WorkstreamEventSourceRef("capability", safeCapability, "Governance/Policy impact capability", safeCapability, "trace-capability-" + stableSuffix(safeCapability), correlationId)),
        List.of(safeCapability, GovernancePolicyImpactService.READ_CAPABILITY),
        correlationId,
        idempotencyKey,
        task.idempotencyKey(),
        List.of(traceId),
        "agent-governance-policy",
        task.status() == GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED ? "surface-governance-policy-impact-analysis-result" : "surface-governance-policy-impact-analysis-task",
        PAYLOAD_GOVERNANCE_POLICY_IMPACT_LIFECYCLE,
        Map.ofEntries(
            Map.entry("impactTaskId", task.impactTaskId()),
            Map.entry("autonomousAgentTaskId", safe(task.autonomousAgentTaskId(), "")),
            Map.entry("proposalId", task.proposalId()),
            Map.entry("status", task.status().name().toLowerCase(java.util.Locale.ROOT)),
            Map.entry("semanticTransition", semanticTransition),
            Map.entry("taskLifecycleEventType", eventType),
            Map.entry("progressPercent", Integer.toString(task.progressPercent())),
            Map.entry("blockerCode", safe(task.blockerCode(), "")),
            Map.entry("decision", safe(task.decision(), "")),
            Map.entry("safeSummary", redact(safe(task.summary(), ""))),
            Map.entry("providerOrRuntimeState", task.status() == GovernancePolicyImpactTask.Status.BLOCKED_PROVIDER_OR_RUNTIME ? "blocked_provider_or_runtime:fail_closed:no_fake_success" : "state_recorded_without_policy_activation_or_mutation"),
            Map.entry("noDirectMutation", "true"),
            Map.entry("activationBlockedUntilHumanDecision", "true"),
            Map.entry("redactionRequired", "true")),
        Map.of("browserSafe", "true", "omitted", "rawPrompt,hiddenPromptText,rawToolPayload,rawJwt,providerSecret,providerCredential,crossTenantEvidence", "minimumRedactionLevel", "FULL"),
        Map.of("attentionCategory", "WORKFLOW_BLOCKED", "attentionItemId", "attention:worker-task:" + task.impactTaskId() + ":task-state"));
  }

  private static String idempotencyKey(String eventFamily, String eventType, String tenantId, String customerId, String sourceRefId, String semanticTransition) {
    return "workstream-event:" + eventFamily + ":" + eventType + ":" + tenantId + ":" + safe(customerId, "none") + ":" + sourceRefId + ":" + semanticTransition;
  }

  private static String workerTaskTransition(AccessReviewTask task, String semanticTransition) {
    if (task.status() == AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME && "autonomous_agent_task_failed".equals(task.blockerCode())) return "failed";
    return normalizedWorkerTaskTransition(semanticTransition);
  }

  private static String workerTaskTransition(PromptRiskReviewTask task, String semanticTransition) {
    if (task.status() == PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME && "autonomous_agent_task_failed".equals(task.blockerCode())) return "failed";
    return normalizedWorkerTaskTransition(semanticTransition);
  }

  private static String workerTaskTransition(AuditTraceSummaryTask task, String semanticTransition) {
    if (task.status() == AuditTraceSummaryTask.Status.FAILED || "autonomous_agent_task_failed".equals(task.blockerCode())) return "failed";
    return normalizedWorkerTaskTransition(semanticTransition);
  }

  private static String workerTaskTransition(GovernancePolicyImpactTask task, String semanticTransition) {
    if (task.status() == GovernancePolicyImpactTask.Status.FAILED || "autonomous_agent_task_failed".equals(task.blockerCode())) return "failed";
    return normalizedWorkerTaskTransition(semanticTransition);
  }

  private static String workerTaskTransition(MyAccountPersonalAttentionDigestTask task, String semanticTransition) {
    if (task.status() == MyAccountPersonalAttentionDigestTask.Status.FAILED || "autonomous_agent_task_failed".equals(task.blockerCode())) return "failed";
    return normalizedWorkerTaskTransition(semanticTransition);
  }

  private static String normalizedWorkerTaskTransition(String semanticTransition) {
    return switch (safe(semanticTransition, "running")) {
      case "started", "queued" -> "queued";
      case "result_accepted" -> "accepted";
      case "result_rejected" -> "rejected_result";
      default -> semanticTransition;
    };
  }

  private static boolean terminalLifecycleEvent(String eventType) {
    var type = safe(eventType, "");
    return type.endsWith(".resolved") || type.endsWith(".completed") || type.endsWith(".delivered") || type.endsWith(".archived") || type.endsWith(".revoked") || type.endsWith(".expired") || type.endsWith(".rolled_back") || type.endsWith(".accepted") || type.endsWith(".rejected");
  }

  private static String redact(String value) {
    return safe(value, "").replaceAll("(?i)(api[_-]?key|secret|token|providerCredential)=[^\\s,;]+", "$1=[REDACTED]");
  }

  private static String firstTraceRef(AccessReviewTask task, String fallback) {
    return task.traceIds().isEmpty() ? fallback : task.traceIds().get(0);
  }

  private static String firstTraceRef(PromptRiskReviewTask task, String fallback) {
    return task.traceIds().isEmpty() ? fallback : task.traceIds().get(0);
  }

  private static String firstTraceRef(AuditTraceSummaryTask task, String fallback) {
    return task.traceIds().isEmpty() ? fallback : task.traceIds().get(0);
  }

  private static String firstTraceRef(GovernancePolicyImpactTask task, String fallback) {
    return task.traceIds().isEmpty() ? fallback : task.traceIds().get(0);
  }

  private static String firstTraceRef(MyAccountPersonalAttentionDigestTask task, String fallback) {
    return task.traceIds().isEmpty() ? fallback : task.traceIds().get(0);
  }

  private static String safe(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString(Objects.requireNonNullElse(value, "workstream-event").hashCode(), 36);
  }
}

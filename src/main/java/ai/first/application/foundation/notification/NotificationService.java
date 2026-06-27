package ai.first.application.foundation.notification;

import ai.first.application.coreapp.agentadmin.AgentAdminPromptRiskReviewService;
import ai.first.domain.coreapp.agentadmin.PromptRiskReviewTask;
import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.attention.AttentionCategory;
import ai.first.domain.foundation.attention.AttentionItem;
import ai.first.domain.foundation.attention.AttentionRedactionLevel;
import ai.first.domain.foundation.attention.AttentionSeverity;
import ai.first.domain.coreapp.audit.AuditTraceSummaryTask;
import ai.first.domain.foundation.email.EmailNotificationPreference;
import ai.first.domain.coreapp.governance.GovernancePolicyImpactTask;
import ai.first.domain.coreapp.myaccount.MyAccountNotificationCenter;
import ai.first.domain.coreapp.myaccount.MyAccountPersonalAttentionDigestTask;
import ai.first.domain.foundation.notification.NotificationCategory;
import ai.first.domain.foundation.notification.NotificationChannel;
import ai.first.domain.foundation.notification.NotificationChannelRegistryEntry;
import ai.first.domain.foundation.notification.NotificationChannelStatus;
import ai.first.domain.foundation.notification.NotificationDeliveryAttempt;
import ai.first.domain.foundation.notification.NotificationDeliveryAttemptStatus;
import ai.first.domain.foundation.notification.NotificationExternalOutboxMessage;
import ai.first.domain.foundation.notification.NotificationItem;
import ai.first.domain.foundation.notification.NotificationLifecycleStatus;
import ai.first.domain.foundation.notification.NotificationPreference;
import ai.first.domain.foundation.notification.NotificationPriority;
import ai.first.domain.foundation.notification.NotificationProjectionInput;
import ai.first.domain.foundation.notification.NotificationRedactionLevel;
import ai.first.domain.foundation.notification.NotificationSourceRef;
import ai.first.domain.foundation.notification.NotificationSurfaceRef;
import ai.first.domain.foundation.workstream.WorkstreamEventEnvelope;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import ai.first.application.foundation.email.EmailNotificationService;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.domain.foundation.identity.AuthContext;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.application.coreapp.audit.AuditTraceSummaryService;
import ai.first.application.coreapp.governance.GovernancePolicyImpactService;
import ai.first.application.coreapp.useradmin.UserAdminAccessReviewService;

/** Backend-owned governed in-app notification projection, lifecycle, and preference service. */
public final class NotificationService {
  public static final String CAPABILITY_GROUP = "notification.in_app";
  public static final String LIST_MY_ACCOUNT_CENTER_TOOL = "notification.list_my_account_center";
  public static final String GET_NOTIFICATION_TOOL = "notification.get_notification";
  public static final String MARK_READ_TOOL = "notification.mark_read";
  public static final String DISMISS_TOOL = "notification.dismiss";
  public static final String ARCHIVE_TOOL = "notification.archive";
  public static final String SNOOZE_TOOL = "notification.snooze";
  public static final String UPDATE_PREFERENCES_TOOL = "notification.update_preferences";
  public static final String PROJECT_FROM_SOURCE_TOOL = "notification.project_from_source";
  public static final String LIST_DELIVERY_PLATFORM_TOOL = "notification.delivery.list_platform";
  public static final String EVALUATE_EXTERNAL_DELIVERY_TOOL = "notification.delivery.evaluate_external";

  private final NotificationRepository repository;
  private final AuthContextResolver authContextResolver;
  private final Clock clock;

  public NotificationService(NotificationRepository repository, AuthContextResolver authContextResolver, Clock clock) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.clock = Objects.requireNonNull(clock);
  }

  public NotificationItem projectFromAttention(AuthContextResolver.ResolvedMe actor, AttentionItem item, String correlationId) {
    requireVisibleSource(actor, item.tenantId(), item.customerId(), item.requiredCapabilityId());
    var input = new NotificationProjectionInput(
        item.itemId(), "attention", item.tenantId(), item.customerId(), actor.account().accountId(), authMap(actor), item.owningWorkstreamId(), item.requiredCapabilityId(),
        item.sourceRefs().stream().map(ref -> new NotificationSourceRef(ref.kind(), ref.refId(), safe(ref.label()), ref.capabilityId(), ref.traceId(), firstNonBlank(correlationId, ref.correlationId()))).toList(),
        item.sourceRefs().stream().map(ref -> ref.traceId()).filter(Objects::nonNull).distinct().toList(), safe(item.title()), safe(item.summary()), mapAttentionCategory(item.category()), mapSeverity(item.severity()),
        item.surfaceRef() == null ? null : new NotificationSurfaceRef(item.surfaceRef().targetFunctionalAgentId(), item.surfaceRef().targetSurfaceId(), item.surfaceRef().targetSurfaceType(), item.surfaceRef().targetItemId(), item.surfaceRef().defaultActionId(), item.surfaceRef().requiredCapabilityId()),
        dedupe(item.tenantId(), item.customerId(), actor.account().accountId(), "attention", item.itemId(), "attention_required"), firstNonBlank(correlationId, item.correlationId()));
    return projectFromSource(actor, input, correlationId);
  }

  public NotificationItem projectFromWorkstreamEvent(AuthContextResolver.ResolvedMe actor, WorkstreamEventEnvelope event, String correlationId) {
    var requiredCapability = event.capabilityRefs().get(0);
    requireVisibleSource(actor, event.tenantId(), event.customerId(), requiredCapability);
    var category = categoryFromHint(event.projectionHints().get("notificationCategory"));
    var input = new NotificationProjectionInput(event.eventId(), "workstream_event", event.tenantId(), event.customerId(), actor.account().accountId(), event.authContext(), event.owningWorkstreamId(), requiredCapability,
        event.sourceRefs().stream().map(ref -> new NotificationSourceRef(ref.refType(), ref.refId(), safe(ref.label()), requiredCapability, ref.traceId(), correlationId)).toList(), event.traceRefs(), safe(event.payload().getOrDefault("title", event.eventType())), safe(event.payload().getOrDefault("summary", "Authorized workstream update is available.")), category, priorityFromHint(event.projectionHints().get("notificationPriority")),
        new NotificationSurfaceRef(event.owningWorkstreamId(), event.targetSurfaceId(), "dashboard", event.eventId(), "my_account.open_authorized_workstream", requiredCapability), dedupe(event.tenantId(), event.customerId(), actor.account().accountId(), "workstream_event", event.eventId(), event.eventType()), firstNonBlank(correlationId, event.correlationId()));
    return projectFromSource(actor, input, correlationId);
  }

  public NotificationItem projectFromPersonalDigest(AuthContextResolver.ResolvedMe actor, MyAccountPersonalAttentionDigestTask task, String correlationId) {
    var blocked = task.status() == MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME || task.status() == MyAccountPersonalAttentionDigestTask.Status.FAILED || task.status() == MyAccountPersonalAttentionDigestTask.Status.REJECTED;
    var ready = task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED || task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_EMPTY;
    return projectWorkerTask(actor, new WorkerTaskNotification(
        task.digestTaskId(), task.autonomousAgentTaskId(), task.tenantId(), task.customerId(), task.selectedAuthContextId(), task.startedByAccountId(), "my-account-agent", "personal_attention_digest", "Personal attention digest", task.status().name(), task.progressPercent(), task.summary(), task.blockerCode(), task.evidenceRefs(), task.sectionRefs(), task.traceIds(), "my_account.personal_attention_digest.read", "surface-my-account-personal-attention-digest", "my_account.personal_attention_digest.open_evidence", ready ? "ready" : "blocked", blocked ? NotificationCategory.DIGEST_BLOCKED : NotificationCategory.DIGEST_READY, blocked ? NotificationPriority.BLOCKED : NotificationPriority.INFO), correlationId);
  }

  public NotificationItem projectFromAccessReviewTask(AuthContextResolver.ResolvedMe actor, AccessReviewTask task, String correlationId) {
    return projectWorkerTask(actor, new WorkerTaskNotification(
        task.taskId(), task.autonomousAgentTaskId(), task.tenantId(), task.customerId(), task.startedByMembershipId(), task.startedByAccountId(), "user-admin-agent", "access_review_task", "User Admin access-review task", task.status().name(), task.progressPercent(), task.summary(), task.blockerCode(), task.evidenceRefs(), task.recommendationRefs(), task.traceIds(), UserAdminAccessReviewService.READ_CAPABILITY, "surface-user-admin-access-review-task", "user_admin.access_review.open_result", workerSemanticKind(task.status().name()), workerCategory("user_admin", task.status().name()), workerPriority(task.status().name())), correlationId);
  }

  public NotificationItem projectFromPromptRiskReviewTask(AuthContextResolver.ResolvedMe actor, PromptRiskReviewTask task, String correlationId) {
    return projectWorkerTask(actor, new WorkerTaskNotification(
        task.taskId(), task.autonomousAgentTaskId(), task.tenantId(), task.customerId(), task.startedByMembershipId(), task.startedByAccountId(), "agent-admin-agent", "prompt_risk_review_task", "Agent Admin prompt-risk review task", task.status().name(), task.progressPercent(), task.summary(), task.blockerCode(), task.evidenceRefs(), task.findingRefs(), task.traceIds(), AgentAdminPromptRiskReviewService.READ_CAPABILITY, "surface-agent-admin-prompt-risk-review", "agent_admin.prompt_risk_review.open_result", workerSemanticKind(task.status().name()), workerCategory("agent_admin", task.status().name()), workerPriority(task.status().name())), correlationId);
  }

  public NotificationItem projectFromAuditTraceSummaryTask(AuthContextResolver.ResolvedMe actor, AuditTraceSummaryTask task, String correlationId) {
    return projectWorkerTask(actor, new WorkerTaskNotification(
        task.taskId(), task.autonomousAgentTaskId(), task.tenantId(), task.customerId(), task.selectedAuthContextId(), task.startedByAccountId(), "audit-trace-agent", "audit_trace_summary_task", "Audit/Trace summary task", task.status().name(), task.progressPercent(), task.summary(), task.blockerCode(), task.evidenceRefs(), task.findingRefs(), task.traceIds(), AuditTraceSummaryService.READ_CAPABILITY, "surface-audit-trace-summary-progress", AuditTraceSummaryService.OPEN_EVIDENCE_CAPABILITY, workerSemanticKind(task.status().name()), workerCategory("audit_trace", task.status().name()), workerPriority(task.status().name())), correlationId);
  }

  public NotificationItem projectFromGovernancePolicyImpactTask(AuthContextResolver.ResolvedMe actor, GovernancePolicyImpactTask task, String correlationId) {
    return projectWorkerTask(actor, new WorkerTaskNotification(
        task.impactTaskId(), task.autonomousAgentTaskId(), task.tenantId(), task.customerId(), task.startedByMembershipId(), task.startedByAccountId(), "governance-policy-agent", "governance_policy_impact_task", "Governance/Policy impact-analysis task", task.status().name(), task.progressPercent(), task.summary(), task.blockerCode(), task.evidenceRefs(), task.findingRefs(), task.traceIds(), GovernancePolicyImpactService.READ_CAPABILITY, "surface-governance-policy-impact-analysis-task", "governance.policy.impact_analysis.open_result", workerSemanticKind(task.status().name()), workerCategory("governance_policy", task.status().name()), workerPriority(task.status().name())), correlationId);
  }

  private NotificationItem projectWorkerTask(AuthContextResolver.ResolvedMe actor, WorkerTaskNotification task, String correlationId) {
    requireVisibleSource(actor, task.tenantId(), task.customerId(), task.requiredCapabilityId());
    if (!actor.account().accountId().equals(task.recipientAccountId())) throw new AuthorizationException(404, "not_found_or_redacted");
    var sourceRefs = java.util.stream.Stream.concat(
            task.evidenceRefs().stream().map(ref -> new NotificationSourceRef(task.origin(), ref, task.titlePrefix() + " evidence", task.requiredCapabilityId(), ref, correlationId)),
            task.findingOrSectionRefs().stream().map(ref -> new NotificationSourceRef(task.origin() + "_result", ref, task.titlePrefix() + " result", task.requiredCapabilityId(), ref, correlationId)))
        .toList();
    var traceRefs = task.traceRefs().stream().distinct().toList();
    var statusText = task.rawStatus().toLowerCase(Locale.ROOT);
    var title = switch (task.semanticKind()) {
      case "blocked" -> task.titlePrefix() + " is blocked";
      case "failed" -> task.titlePrefix() + " failed";
      case "review_required" -> task.titlePrefix() + " is ready for review";
      case "accepted" -> task.titlePrefix() + " was accepted";
      case "rejected" -> task.titlePrefix() + " was rejected";
      case "cancelled" -> task.titlePrefix() + " was cancelled";
      default -> task.titlePrefix() + " is " + statusText.replace('_', ' ');
    };
    var summary = firstNonBlank(task.summary(), task.blockerCode(), task.titlePrefix() + " progress is " + task.progressPercent() + "% with backend-derived status " + statusText + ".");
    var input = new NotificationProjectionInput(task.taskId(), task.origin(), task.tenantId(), task.customerId(), task.recipientAccountId(), Map.of("selectedContextId", firstNonBlank(task.selectedContextId(), task.startedByMembershipOrContextId())), task.owningWorkstreamId(), task.requiredCapabilityId(),
        sourceRefs, traceRefs, safe(title), safe(summary), task.category(), task.priority(),
        new NotificationSurfaceRef(task.owningWorkstreamId(), task.surfaceId(), "workflow-status", task.taskId(), task.defaultActionId(), task.requiredCapabilityId()),
        dedupe(task.tenantId(), task.customerId(), task.recipientAccountId(), task.origin(), task.taskId(), task.semanticKind()), firstNonBlank(correlationId, task.autonomousAgentTaskId()));
    return projectFromSource(actor, input, correlationId);
  }

  public NotificationItem projectFromSource(AuthContextResolver.ResolvedMe actor, NotificationProjectionInput input, String correlationId) {
    requireVisibleSource(actor, input.tenantId(), input.customerId(), input.requiredCapabilityId());
    if (!actor.account().accountId().equals(input.recipientAccountId())) throw new AuthorizationException(404, "not_found_or_redacted");
    var now = Instant.now(clock);
    var dedupeKey = firstNonBlank(input.idempotencyKey(), dedupe(input.tenantId(), input.customerId(), input.recipientAccountId(), input.inputFamily(), input.inputId(), input.category().name().toLowerCase(Locale.ROOT)));
    var existing = repository.findByDedupeKey(input.tenantId(), dedupeKey).orElse(null);
    var item = existing == null
        ? new NotificationItem("notification-" + Math.abs(dedupeKey.hashCode()), input.tenantId(), input.customerId(), input.recipientAccountId(), input.authContext().get("selectedContextId"), NotificationChannel.IN_APP, safe(input.title()), safe(input.summary()), input.category(), input.priority(), NotificationLifecycleStatus.UNREAD, input.sourceRefs(), input.surfaceRef(), input.requiredCapabilityId(), input.owningWorkstreamId(), input.inputFamily(), NotificationRedactionLevel.FULL, dedupeKey, firstNonBlank(correlationId, input.correlationId()), input.traceRefs(), now, now, now, null, null, null, null, null)
        : existing.updateFromProjection(safe(input.title()), safe(input.summary()), input.priority(), input.sourceRefs(), input.traceRefs(), now, firstNonBlank(correlationId, input.correlationId()));
    var saved = repository.upsert(item);
    appendAudit(actor, "NOTIFICATION_PROJECT_FROM_SOURCE", existing == null ? AdminAuditEvent.Result.ALLOWED : (saved.equals(existing) ? AdminAuditEvent.Result.NO_OP : AdminAuditEvent.Result.ALLOWED), saved.notificationId(), correlationId);
    return saved;
  }

  public MyAccountNotificationCenter listMyAccountCenter(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), LIST_MY_ACCOUNT_CENTER_TOOL);
    var now = Instant.now(clock);
    var prefs = repository.listPreferences(contextTenantId(actor.selectedContext()), actor.account().accountId());
    var includeRead = prefs.stream().filter(pref -> pref.category() == NotificationCategory.ALL).findFirst().map(NotificationPreference::includeReadInCenter).orElse(false);
    var items = visibleItems(actor).stream()
        .filter(item -> preferenceAllows(prefs, item, now))
        .filter(item -> item.activeForCenter(now, includeRead))
        .sorted(Comparator.comparing(NotificationItem::lastChangedAt).reversed())
        .toList();
    var unread = (int) items.stream().filter(item -> item.status() == NotificationLifecycleStatus.UNREAD).count();
    appendAudit(actor, "NOTIFICATION_LIST_MY_ACCOUNT_CENTER", AdminAuditEvent.Result.ALLOWED, "authorized in-app notifications", correlationId);
    return new MyAccountNotificationCenter("my_account.notification_center.v1", NotificationChannel.IN_APP, unread, items.size(), items, prefs, items.stream().collect(Collectors.groupingBy(NotificationItem::origin, LinkedHashMap::new, Collectors.counting())), NotificationRedactionLevel.FULL, items.stream().flatMap(item -> item.traceRefs().stream()).distinct().toList(), correlationId);
  }

  public NotificationItem getNotification(AuthContextResolver.ResolvedMe actor, String notificationId, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), GET_NOTIFICATION_TOOL);
    var item = authorizedItem(actor, notificationId, "NOTIFICATION_GET", correlationId);
    return item == null ? redacted(notificationId, correlationId) : item;
  }

  public NotificationItem markRead(AuthContextResolver.ResolvedMe actor, String notificationId, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), MARK_READ_TOOL);
    return lifecycle(actor, notificationId, "NOTIFICATION_MARK_READ", item -> item.markRead(Instant.now(clock), correlationId), correlationId);
  }

  public NotificationItem dismiss(AuthContextResolver.ResolvedMe actor, String notificationId, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), DISMISS_TOOL);
    return lifecycle(actor, notificationId, "NOTIFICATION_DISMISS", item -> item.dismiss(Instant.now(clock), correlationId), correlationId);
  }

  public NotificationItem archive(AuthContextResolver.ResolvedMe actor, String notificationId, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), ARCHIVE_TOOL);
    return lifecycle(actor, notificationId, "NOTIFICATION_ARCHIVE", item -> item.archive(Instant.now(clock), correlationId), correlationId);
  }

  public NotificationItem snooze(AuthContextResolver.ResolvedMe actor, String notificationId, Instant snoozedUntil, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), SNOOZE_TOOL);
    var now = Instant.now(clock);
    if (snoozedUntil == null || !snoozedUntil.isAfter(now) || snoozedUntil.isAfter(now.plusSeconds(60L * 60 * 24 * 30))) throw new AuthorizationException(400, "invalid-snooze-window");
    return lifecycle(actor, notificationId, "NOTIFICATION_SNOOZE", item -> item.snooze(snoozedUntil, now, correlationId), correlationId);
  }

  public NotificationPreference updatePreference(AuthContextResolver.ResolvedMe actor, NotificationCategory category, boolean enabled, NotificationPriority minimumPriority, Instant muteUntil, boolean includeReadInCenter, String correlationId) {
    var safeCategory = category == null ? NotificationCategory.ALL : category;
    if (safeCategory != NotificationCategory.ALL && visibleItems(actor).stream().noneMatch(item -> item.category() == safeCategory)) {
      appendAudit(actor, "NOTIFICATION_UPDATE_PREFERENCES", AdminAuditEvent.Result.DENIED, "hidden_or_unavailable_category", correlationId);
      throw new AuthorizationException(404, "not_found_or_redacted");
    }
    return updateChannelPreference(actor, NotificationChannel.IN_APP, safeCategory, enabled, minimumPriority, muteUntil, includeReadInCenter, correlationId);
  }

  public NotificationPreference updateChannelPreference(AuthContextResolver.ResolvedMe actor, NotificationChannel channel, NotificationCategory category, boolean enabled, NotificationPriority minimumPriority, Instant muteUntil, boolean includeReadInCenter, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), UPDATE_PREFERENCES_TOOL);
    var safeCategory = category == null ? NotificationCategory.ALL : category;
    var now = Instant.now(clock);
    var safeChannel = channel == null ? NotificationChannel.IN_APP : channel;
    if (safeChannel != NotificationChannel.IN_APP) {
      appendAudit(actor, "NOTIFICATION_UPDATE_PREFERENCES", AdminAuditEvent.Result.DENIED, "external_channel_controls_unsupported", correlationId);
      throw new AuthorizationException(400, "external-channel-controls-unsupported");
    }
    var tenantId = contextTenantId(actor.selectedContext());
    var pref = new NotificationPreference("notification-pref-" + tenantId + "-" + actor.account().accountId() + "-" + safeChannel.name().toLowerCase(Locale.ROOT) + "-" + safeCategory.name().toLowerCase(Locale.ROOT), tenantId, actor.selectedContext().customerId(), actor.account().accountId(), safeChannel, safeCategory, enabled, minimumPriority == null ? NotificationPriority.INFO : minimumPriority, muteUntil, includeReadInCenter, now, actor.account().accountId(), correlationId);
    var saved = repository.savePreference(pref);
    appendAudit(actor, "NOTIFICATION_UPDATE_PREFERENCES", AdminAuditEvent.Result.ALLOWED, safeChannel.name().toLowerCase(Locale.ROOT) + ":" + safeCategory.name().toLowerCase(Locale.ROOT), correlationId);
    return saved;
  }

  public List<NotificationChannelRegistryEntry> listChannelRegistry(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), LIST_DELIVERY_PLATFORM_TOOL);
    appendAudit(actor, "NOTIFICATION_DELIVERY_LIST_PLATFORM", AdminAuditEvent.Result.ALLOWED, "provider-neutral registry", correlationId);
    return List.of(
        new NotificationChannelRegistryEntry(NotificationChannel.IN_APP, NotificationChannelStatus.ACTIVE, "backend_projection", true, false, LIST_MY_ACCOUNT_CENTER_TOOL, UPDATE_PREFERENCES_TOOL, "Akka-backed in-app notification center is active."),
        new NotificationChannelRegistryEntry(NotificationChannel.EMAIL, NotificationChannelStatus.LOCAL_TEST_CAPTURED, "resend_or_captured_outbox", true, true, EmailNotificationService.ENQUEUE_TOOL, EmailNotificationService.UPDATE_PREFERENCES_TOOL, "Email uses the governed Resend/captured-outbox boundary."),
        unconfigured(NotificationChannel.WEBHOOK),
        unconfigured(NotificationChannel.SMS),
        unconfigured(NotificationChannel.MOBILE_PUSH),
        unconfigured(NotificationChannel.SLACK),
        unconfigured(NotificationChannel.TEAMS));
  }

  public NotificationDeliveryAttempt evaluateExternalDelivery(AuthContextResolver.ResolvedMe actor, String notificationId, NotificationChannel channel, String destinationSummary, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), EVALUATE_EXTERNAL_DELIVERY_TOOL);
    var safeChannel = channel == null ? NotificationChannel.WEBHOOK : channel;
    if (safeChannel == NotificationChannel.IN_APP || safeChannel == NotificationChannel.EMAIL) throw new AuthorizationException(400, "use-specific-notification-channel-capability");
    var item = authorizedItem(actor, notificationId, "NOTIFICATION_DELIVERY_EVALUATE_EXTERNAL", correlationId);
    if (item == null) throw new AuthorizationException(404, "not_found_or_redacted");
    var dedupeKey = "notification:delivery:" + safeChannel.name().toLowerCase(Locale.ROOT) + ":" + item.tenantId() + ":" + firstNonBlank(item.customerId(), "none") + ":" + item.accountId() + ":" + item.notificationId();
    var existing = repository.findDeliveryAttemptByDedupeKey(item.tenantId(), dedupeKey).orElse(null);
    if (existing != null) {
      appendAudit(actor, "NOTIFICATION_DELIVERY_DUPLICATE", AdminAuditEvent.Result.NO_OP, existing.status().name().toLowerCase(Locale.ROOT), correlationId);
      return existing;
    }
    var now = Instant.now(clock);
    var outboxId = "notification-external-outbox-" + Math.abs(dedupeKey.hashCode());
    var attemptId = "notification-delivery-" + Math.abs(dedupeKey.hashCode());
    var outbox = new NotificationExternalOutboxMessage(outboxId, item.tenantId(), item.customerId(), item.accountId(), safeChannel, safe(firstNonBlank(destinationSummary, safeChannel.name().toLowerCase(Locale.ROOT) + " destination withheld")), safe(item.title()), safe(item.summary()), Map.of("sourceNotificationId", item.notificationId(), "channel", safeChannel.name()), correlationId, now);
    repository.saveExternalOutbox(outbox);
    var attempt = new NotificationDeliveryAttempt(attemptId, item.tenantId(), item.customerId(), item.accountId(), safeChannel, item.category(), item.notificationId(), item.sourceRefs(), item.traceRefs(), item.requiredCapabilityId(), item.owningWorkstreamId(), outbox.destinationSummary(), "provider_unconfigured", NotificationDeliveryAttemptStatus.BLOCKED_PROVIDER_UNCONFIGURED, "Production provider is not configured for " + safeChannel.name().toLowerCase(Locale.ROOT) + "; local/test outbox captured the intent without reporting delivery success.", dedupeKey, outboxId, correlationId, now, now);
    var saved = repository.saveDeliveryAttempt(attempt);
    appendAudit(actor, "NOTIFICATION_DELIVERY_PROVIDER_UNCONFIGURED", AdminAuditEvent.Result.DENIED, safeChannel.name().toLowerCase(Locale.ROOT), correlationId);
    return saved;
  }

  public List<NotificationDeliveryAttempt> listDeliveryAttempts(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), LIST_DELIVERY_PLATFORM_TOOL);
    appendAudit(actor, "NOTIFICATION_DELIVERY_LIST_ATTEMPTS", AdminAuditEvent.Result.ALLOWED, "redacted attempts", correlationId);
    return repository.listDeliveryAttempts(contextTenantId(actor.selectedContext()), actor.account().accountId());
  }

  public List<NotificationExternalOutboxMessage> listExternalOutbox(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), LIST_DELIVERY_PLATFORM_TOOL);
    appendAudit(actor, "NOTIFICATION_DELIVERY_LIST_EXTERNAL_OUTBOX", AdminAuditEvent.Result.ALLOWED, "captured local/test outbox", correlationId);
    return repository.listExternalOutbox(contextTenantId(actor.selectedContext()), actor.account().accountId());
  }

  public List<EmailNotificationPreference> listEmailPreferences(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), EmailNotificationService.LIST_PREFERENCES_TOOL);
    appendAudit(actor, "EMAIL_NOTIFICATION_LIST_PREFERENCES", AdminAuditEvent.Result.ALLOWED, "email preference summary", correlationId);
    return repository.listEmailPreferences(contextTenantId(actor.selectedContext()), actor.account().accountId());
  }

  public EmailNotificationPreference updateEmailPreference(AuthContextResolver.ResolvedMe actor, NotificationCategory category, boolean enabled, NotificationPriority minimumPriority, Instant muteUntil, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), EmailNotificationService.UPDATE_PREFERENCES_TOOL);
    var safeCategory = category == null ? NotificationCategory.ALL : category;
    var now = Instant.now(clock);
    var tenantId = contextTenantId(actor.selectedContext());
    var pref = new EmailNotificationPreference("email-notification-pref-" + tenantId + "-" + actor.account().accountId() + "-" + safeCategory.name().toLowerCase(Locale.ROOT), tenantId, actor.selectedContext().customerId(), actor.account().accountId(), safeCategory, enabled, minimumPriority == null ? NotificationPriority.INFO : minimumPriority, muteUntil, now, actor.account().accountId(), correlationId);
    var saved = repository.saveEmailPreference(pref);
    appendAudit(actor, "EMAIL_NOTIFICATION_UPDATE_PREFERENCES", AdminAuditEvent.Result.ALLOWED, safeCategory.name().toLowerCase(Locale.ROOT), correlationId);
    return saved;
  }

  private NotificationItem lifecycle(AuthContextResolver.ResolvedMe actor, String notificationId, String action, UnaryOperator<NotificationItem> change, String correlationId) {
    var current = authorizedItem(actor, notificationId, action, correlationId);
    if (current == null) throw new AuthorizationException(404, "not_found_or_redacted");
    var next = change.apply(current);
    var noOp = next.equals(current);
    if (!noOp) repository.save(next);
    appendAudit(actor, action, noOp ? AdminAuditEvent.Result.NO_OP : AdminAuditEvent.Result.ALLOWED, notificationId, correlationId);
    return noOp ? current : next;
  }

  private NotificationItem authorizedItem(AuthContextResolver.ResolvedMe actor, String notificationId, String action, String correlationId) {
    var item = repository.find(contextTenantId(actor.selectedContext()), notificationId).orElse(null);
    if (item == null || !isVisible(actor, item)) {
      appendAudit(actor, action, AdminAuditEvent.Result.DENIED, "not_found_or_redacted", correlationId);
      return null;
    }
    return item;
  }

  private List<NotificationItem> visibleItems(AuthContextResolver.ResolvedMe actor) {
    return repository.listTenant(contextTenantId(actor.selectedContext())).stream().filter(item -> isVisible(actor, item)).map(this::redactIfNeeded).toList();
  }

  private boolean isVisible(AuthContextResolver.ResolvedMe actor, NotificationItem item) {
    return Objects.equals(contextTenantId(actor.selectedContext()), item.tenantId())
        && (actor.selectedContext().customerId() == null || item.customerId() == null || actor.selectedContext().customerId().equals(item.customerId()))
        && actor.account().accountId().equals(item.accountId())
        && actor.selectedContext().capabilities().contains(item.requiredCapabilityId());
  }

  private void requireVisibleSource(AuthContextResolver.ResolvedMe actor, String tenantId, String customerId, String capabilityId) {
    if (!Objects.equals(contextTenantId(actor.selectedContext()), tenantId)) throw new AuthorizationException(403, "tenant-mismatch");
    if (actor.selectedContext().customerId() != null && customerId != null && !actor.selectedContext().customerId().equals(customerId)) throw new AuthorizationException(403, "customer-mismatch");
    authContextResolver.requireCapability(actor.selectedContext(), capabilityId);
  }

  private boolean preferenceAllows(List<NotificationPreference> prefs, NotificationItem item, Instant now) {
    return prefs.stream().filter(pref -> pref.channel() == item.channel()).filter(pref -> pref.category() == item.category() || pref.category() == NotificationCategory.ALL).allMatch(pref -> pref.enabled() && rank(item.priority()) >= rank(pref.minimumPriority()) && (pref.muteUntil() == null || !pref.muteUntil().isAfter(now)));
  }

  private NotificationItem redactIfNeeded(NotificationItem item) {
    if (item.redactionLevel() == NotificationRedactionLevel.FULL) return item;
    return new NotificationItem(item.notificationId(), item.tenantId(), item.customerId(), item.accountId(), item.selectedContextId(), item.channel(), item.title(), item.summary(), item.category(), item.priority(), item.status(), List.of(), null, item.requiredCapabilityId(), item.owningWorkstreamId(), item.origin(), NotificationRedactionLevel.SUMMARY_ONLY, item.dedupeKey(), item.correlationId(), item.traceRefs(), item.createdAt(), item.updatedAt(), item.lastChangedAt(), item.readAt(), item.dismissedAt(), item.archivedAt(), item.snoozedUntil(), item.expiresAt());
  }

  private NotificationItem redacted(String notificationId, String correlationId) {
    return new NotificationItem(notificationId, null, null, null, null, NotificationChannel.IN_APP, null, null, null, null, NotificationLifecycleStatus.EXPIRED, List.of(), null, null, null, null, NotificationRedactionLevel.NOT_FOUND_OR_REDACTED, null, correlationId, List.of(), null, null, null, null, null, null, null, null);
  }

  private Map<String, String> authMap(AuthContextResolver.ResolvedMe actor) {
    return Map.of("selectedContextId", actor.selectedContext().membershipId(), "tenantId", contextTenantId(actor.selectedContext()), "accountId", actor.account().accountId());
  }

  private NotificationCategory mapAttentionCategory(AttentionCategory category) {
    if (category == AttentionCategory.PROVIDER_READINESS) return NotificationCategory.PROVIDER_READINESS;
    if (category == AttentionCategory.GOVERNANCE_APPROVAL) return NotificationCategory.POLICY_OR_GOVERNANCE;
    if (category == AttentionCategory.AUDIT_FAILURE_EVIDENCE) return NotificationCategory.AUDIT_OR_SECURITY;
    return NotificationCategory.ATTENTION_REQUIRED;
  }

  private NotificationCategory categoryFromHint(String value) {
    if (value == null || value.isBlank()) return NotificationCategory.WORKSTREAM_UPDATE;
    try { return NotificationCategory.valueOf(value.trim().toUpperCase(Locale.ROOT)); } catch (IllegalArgumentException ignored) { return NotificationCategory.WORKSTREAM_UPDATE; }
  }

  private NotificationPriority mapSeverity(AttentionSeverity severity) {
    return switch (severity) {
      case INFO -> NotificationPriority.INFO;
      case WARNING -> NotificationPriority.WARNING;
      case URGENT -> NotificationPriority.URGENT;
      case BLOCKED -> NotificationPriority.BLOCKED;
    };
  }

  private NotificationCategory workerCategory(String family, String rawStatus) {
    if (rawStatus == null) return NotificationCategory.WORKSTREAM_UPDATE;
    var status = rawStatus.toUpperCase(Locale.ROOT);
    if (status.contains("BLOCKED")) return NotificationCategory.PROVIDER_READINESS;
    if (status.contains("FAILED")) return NotificationCategory.AUDIT_OR_SECURITY;
    if ("governance_policy".equals(family)) return NotificationCategory.POLICY_OR_GOVERNANCE;
    if ("audit_trace".equals(family)) return NotificationCategory.AUDIT_OR_SECURITY;
    return NotificationCategory.WORKSTREAM_UPDATE;
  }

  private NotificationPriority workerPriority(String rawStatus) {
    if (rawStatus == null) return NotificationPriority.INFO;
    var status = rawStatus.toUpperCase(Locale.ROOT);
    if (status.contains("BLOCKED") || status.contains("FAILED")) return NotificationPriority.BLOCKED;
    if (status.contains("COMPLETED") || status.contains("REJECT")) return NotificationPriority.WARNING;
    return NotificationPriority.INFO;
  }

  private String workerSemanticKind(String rawStatus) {
    if (rawStatus == null) return "progress";
    var status = rawStatus.toUpperCase(Locale.ROOT);
    if (status.contains("BLOCKED")) return "blocked";
    if (status.contains("FAILED")) return "failed";
    if (status.contains("COMPLETED")) return "review_required";
    if (status.contains("ACCEPTED")) return "accepted";
    if (status.contains("REJECT")) return "rejected";
    if (status.contains("CANCELLED")) return "cancelled";
    return "progress";
  }

  private NotificationPriority priorityFromHint(String value) {
    if (value == null || value.isBlank()) return NotificationPriority.INFO;
    try { return NotificationPriority.valueOf(value.trim().toUpperCase(Locale.ROOT)); } catch (IllegalArgumentException ignored) { return NotificationPriority.INFO; }
  }

  private NotificationChannelRegistryEntry unconfigured(NotificationChannel channel) {
    return new NotificationChannelRegistryEntry(channel, NotificationChannelStatus.PROVIDER_UNCONFIGURED, "provider_neutral_fail_closed", false, true, EVALUATE_EXTERNAL_DELIVERY_TOOL, UPDATE_PREFERENCES_TOOL, "Q-001 has not selected a production provider; delivery attempts are blocked and captured locally for tests only.");
  }

  private int rank(NotificationPriority priority) {
    return switch (priority == null ? NotificationPriority.INFO : priority) {
      case INFO -> 0;
      case WARNING -> 1;
      case URGENT -> 2;
      case BLOCKED -> 3;
    };
  }

  private String dedupe(String tenantId, String customerId, String accountId, String inputFamily, String sourceId, String semanticKind) {
    return "notification:in_app:" + tenantId + ":" + firstNonBlank(customerId, "none") + ":" + accountId + ":" + inputFamily + ":" + sourceId + ":" + semanticKind;
  }

  private String safe(String value) {
    if (value == null) return "";
    return value.replaceAll("(?i)(bearer\\s+[a-z0-9._-]+|password=[^\\s]+|token=[^\\s]+|secret=[^\\s]+)", "[redacted]");
  }

  private void appendAudit(AuthContextResolver.ResolvedMe actor, String action, AdminAuditEvent.Result result, String reason, String correlationId) {
    var safeCorrelationId = firstNonBlank(correlationId, actor.correlationId(), "corr-notification-" + UUID.randomUUID());
    var safeReason = result.name().toLowerCase(Locale.ROOT) + ":" + reason;
    if (result == AdminAuditEvent.Result.DENIED) authContextResolver.appendDeniedTrace(actor, action, safeReason, safeCorrelationId);
    else authContextResolver.appendProtectedReadTrace(actor, action, safeReason, safeCorrelationId);
  }

  private static String contextTenantId(AuthContext authContext) {
    return authContext.scopeType() == ScopeType.SAAS_OWNER && (authContext.tenantId() == null || authContext.tenantId().isBlank())
        ? WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID
        : authContext.tenantId();
  }

  private String firstNonBlank(String... values) {
    for (var value : values) if (value != null && !value.isBlank()) return value;
    return "";
  }

  private record WorkerTaskNotification(
      String taskId,
      String autonomousAgentTaskId,
      String tenantId,
      String customerId,
      String startedByMembershipOrContextId,
      String recipientAccountId,
      String owningWorkstreamId,
      String origin,
      String titlePrefix,
      String rawStatus,
      int progressPercent,
      String summary,
      String blockerCode,
      List<String> evidenceRefs,
      List<String> findingOrSectionRefs,
      List<String> traceRefs,
      String requiredCapabilityId,
      String surfaceId,
      String defaultActionId,
      String semanticKind,
      NotificationCategory category,
      NotificationPriority priority) {
    private WorkerTaskNotification {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
      findingOrSectionRefs = List.copyOf(findingOrSectionRefs == null ? List.of() : findingOrSectionRefs);
      traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
    }

    String selectedContextId() {
      return startedByMembershipOrContextId;
    }
  }
}

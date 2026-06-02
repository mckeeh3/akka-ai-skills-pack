package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionCategory;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionRedactionLevel;
import {{JAVA_BASE_PACKAGE}}.domain.security.AttentionSeverity;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailNotificationPreference;
import {{JAVA_BASE_PACKAGE}}.domain.security.MyAccountNotificationCenter;
import {{JAVA_BASE_PACKAGE}}.domain.security.MyAccountPersonalAttentionDigestTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationCategory;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationChannel;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationChannelRegistryEntry;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationChannelStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationDeliveryAttempt;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationDeliveryAttemptStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationExternalOutboxMessage;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPreference;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPriority;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationProjectionInput;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationRedactionLevel;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationSourceRef;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationSurfaceRef;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkstreamEventEnvelope;
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
    requireVisibleSource(actor, task.tenantId(), task.customerId(), "my_account.personal_attention_digest.read");
    if (!actor.account().accountId().equals(task.startedByAccountId())) throw new AuthorizationException(404, "not_found_or_redacted");
    var blocked = task.status() == MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME || task.status() == MyAccountPersonalAttentionDigestTask.Status.FAILED || task.status() == MyAccountPersonalAttentionDigestTask.Status.REJECTED;
    var ready = task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED || task.status() == MyAccountPersonalAttentionDigestTask.Status.COMPLETED_EMPTY;
    var input = new NotificationProjectionInput(task.digestTaskId(), "personal_attention_digest", task.tenantId(), task.customerId(), task.startedByAccountId(), Map.of("selectedContextId", task.selectedAuthContextId()), "agent-my-account", "my_account.personal_attention_digest.read",
        task.evidenceRefs().stream().map(ref -> new NotificationSourceRef("personal_attention_digest", ref, "Personal attention digest evidence", "my_account.personal_attention_digest.read", ref, correlationId)).toList(), task.traceIds(), blocked ? "Personal attention digest is blocked" : "Personal attention digest is ready", safe(firstNonBlank(task.summary(), blocked ? task.blockerCode() : "Digest result is ready for review.")), blocked ? NotificationCategory.DIGEST_BLOCKED : NotificationCategory.DIGEST_READY, blocked ? NotificationPriority.BLOCKED : NotificationPriority.INFO,
        new NotificationSurfaceRef("agent-my-account", "surface-my-account-personal-attention-digest", "dashboard", task.digestTaskId(), "my_account.personal_attention_digest.open_evidence", "my_account.personal_attention_digest.read"), dedupe(task.tenantId(), task.customerId(), task.startedByAccountId(), "personal_attention_digest", task.digestTaskId(), ready ? "ready" : "blocked"), correlationId);
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
    var prefs = repository.listPreferences(actor.selectedContext().tenantId(), actor.account().accountId());
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
    return updateChannelPreference(actor, NotificationChannel.IN_APP, category, enabled, minimumPriority, muteUntil, includeReadInCenter, correlationId);
  }

  public NotificationPreference updateChannelPreference(AuthContextResolver.ResolvedMe actor, NotificationChannel channel, NotificationCategory category, boolean enabled, NotificationPriority minimumPriority, Instant muteUntil, boolean includeReadInCenter, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), UPDATE_PREFERENCES_TOOL);
    var safeCategory = category == null ? NotificationCategory.ALL : category;
    var now = Instant.now(clock);
    var safeChannel = channel == null ? NotificationChannel.IN_APP : channel;
    var pref = new NotificationPreference("notification-pref-" + actor.selectedContext().tenantId() + "-" + actor.account().accountId() + "-" + safeChannel.name().toLowerCase(Locale.ROOT) + "-" + safeCategory.name().toLowerCase(Locale.ROOT), actor.selectedContext().tenantId(), actor.selectedContext().customerId(), actor.account().accountId(), safeChannel, safeCategory, enabled, minimumPriority == null ? NotificationPriority.INFO : minimumPriority, muteUntil, includeReadInCenter, now, actor.account().accountId(), correlationId);
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
    return repository.listDeliveryAttempts(actor.selectedContext().tenantId(), actor.account().accountId());
  }

  public List<NotificationExternalOutboxMessage> listExternalOutbox(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), LIST_DELIVERY_PLATFORM_TOOL);
    appendAudit(actor, "NOTIFICATION_DELIVERY_LIST_EXTERNAL_OUTBOX", AdminAuditEvent.Result.ALLOWED, "captured local/test outbox", correlationId);
    return repository.listExternalOutbox(actor.selectedContext().tenantId(), actor.account().accountId());
  }

  public List<EmailNotificationPreference> listEmailPreferences(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), EmailNotificationService.LIST_PREFERENCES_TOOL);
    appendAudit(actor, "EMAIL_NOTIFICATION_LIST_PREFERENCES", AdminAuditEvent.Result.ALLOWED, "email preference summary", correlationId);
    return repository.listEmailPreferences(actor.selectedContext().tenantId(), actor.account().accountId());
  }

  public EmailNotificationPreference updateEmailPreference(AuthContextResolver.ResolvedMe actor, NotificationCategory category, boolean enabled, NotificationPriority minimumPriority, Instant muteUntil, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), EmailNotificationService.UPDATE_PREFERENCES_TOOL);
    var safeCategory = category == null ? NotificationCategory.ALL : category;
    var now = Instant.now(clock);
    var pref = new EmailNotificationPreference("email-notification-pref-" + actor.selectedContext().tenantId() + "-" + actor.account().accountId() + "-" + safeCategory.name().toLowerCase(Locale.ROOT), actor.selectedContext().tenantId(), actor.selectedContext().customerId(), actor.account().accountId(), safeCategory, enabled, minimumPriority == null ? NotificationPriority.INFO : minimumPriority, muteUntil, now, actor.account().accountId(), correlationId);
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
    var item = repository.find(actor.selectedContext().tenantId(), notificationId).orElse(null);
    if (item == null || !isVisible(actor, item)) {
      appendAudit(actor, action, AdminAuditEvent.Result.DENIED, "not_found_or_redacted", correlationId);
      return null;
    }
    return item;
  }

  private List<NotificationItem> visibleItems(AuthContextResolver.ResolvedMe actor) {
    return repository.listTenant(actor.selectedContext().tenantId()).stream().filter(item -> isVisible(actor, item)).map(this::redactIfNeeded).toList();
  }

  private boolean isVisible(AuthContextResolver.ResolvedMe actor, NotificationItem item) {
    return actor.selectedContext().tenantId().equals(item.tenantId())
        && (actor.selectedContext().customerId() == null || item.customerId() == null || actor.selectedContext().customerId().equals(item.customerId()))
        && actor.account().accountId().equals(item.accountId())
        && actor.selectedContext().capabilities().contains(item.requiredCapabilityId());
  }

  private void requireVisibleSource(AuthContextResolver.ResolvedMe actor, String tenantId, String customerId, String capabilityId) {
    if (!actor.selectedContext().tenantId().equals(tenantId)) throw new AuthorizationException(403, "tenant-mismatch");
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
    return Map.of("selectedContextId", actor.selectedContext().membershipId(), "tenantId", actor.selectedContext().tenantId(), "accountId", actor.account().accountId());
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

  private String firstNonBlank(String... values) {
    for (var value : values) if (value != null && !value.isBlank()) return value;
    return "";
  }
}

package ai.first.application.security;

import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.email.EmailNotificationDelivery;
import ai.first.domain.foundation.email.EmailNotificationDeliveryStatus;
import ai.first.domain.foundation.email.EmailNotificationPreference;
import ai.first.domain.foundation.email.EmailOutboxMessage;
import ai.first.domain.foundation.notification.NotificationCategory;
import ai.first.domain.foundation.notification.NotificationItem;
import ai.first.domain.foundation.notification.NotificationPriority;
import ai.first.domain.foundation.notification.NotificationRedactionLevel;
import ai.first.domain.foundation.identity.ScopeType;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Governed email delivery channel over authorized notification state. */
public final class EmailNotificationService {
  public static final String CAPABILITY_GROUP = "notification.email";
  public static final String EVALUATE_DELIVERY_TOOL = "notification.email.evaluate_delivery";
  public static final String ENQUEUE_TOOL = "notification.email.enqueue";
  public static final String DELIVER_OUTBOX_TOOL = "notification.email.deliver_outbox";
  public static final String GET_STATUS_TOOL = "notification.email.get_delivery_status";
  public static final String LIST_PREFERENCES_TOOL = "notification.email.list_my_preferences";
  public static final String UPDATE_PREFERENCES_TOOL = "notification.email.update_preferences";

  private final NotificationRepository repository;
  private final AuthContextResolver authContextResolver;
  private final ResendEmailService resendEmailService;
  private final ResendEmailService.DeliveryMode deliveryMode;
  private final Clock clock;

  public EmailNotificationService(NotificationRepository repository, AuthContextResolver authContextResolver, ResendEmailService resendEmailService, ResendEmailService.DeliveryMode deliveryMode, Clock clock) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.resendEmailService = Objects.requireNonNull(resendEmailService);
    this.deliveryMode = Objects.requireNonNull(deliveryMode);
    this.clock = Objects.requireNonNull(clock);
  }

  public EmailNotificationPreference updatePreference(AuthContextResolver.ResolvedMe actor, NotificationCategory category, boolean enabled, NotificationPriority minimumPriority, Instant muteUntil, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), UPDATE_PREFERENCES_TOOL);
    var safeCategory = category == null ? NotificationCategory.ALL : category;
    var now = Instant.now(clock);
    var preference = new EmailNotificationPreference(
        "email-notification-pref-" + actor.selectedContext().tenantId() + "-" + actor.account().accountId() + "-" + safeCategory.name().toLowerCase(Locale.ROOT),
        actor.selectedContext().tenantId(), actor.selectedContext().customerId(), actor.account().accountId(), safeCategory, enabled,
        minimumPriority == null ? NotificationPriority.INFO : minimumPriority, muteUntil, now, actor.account().accountId(), correlationId);
    var saved = repository.saveEmailPreference(preference);
    appendAudit(actor, "EMAIL_NOTIFICATION_UPDATE_PREFERENCES", AdminAuditEvent.Result.ALLOWED, safeCategory.name().toLowerCase(Locale.ROOT), correlationId);
    return saved;
  }

  public List<EmailNotificationPreference> listPreferences(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), LIST_PREFERENCES_TOOL);
    appendAudit(actor, "EMAIL_NOTIFICATION_LIST_PREFERENCES", AdminAuditEvent.Result.ALLOWED, "email preferences", correlationId);
    return repository.listEmailPreferences(actor.selectedContext().tenantId(), actor.account().accountId());
  }

  public EmailNotificationDelivery evaluateAndEnqueue(AuthContextResolver.ResolvedMe actor, String notificationId, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), EVALUATE_DELIVERY_TOOL);
    var item = repository.find(actor.selectedContext().tenantId(), notificationId).orElse(null);
    if (item == null || !visible(actor, item)) {
      appendAudit(actor, "EMAIL_NOTIFICATION_EVALUATE_DENIED", AdminAuditEvent.Result.DENIED, "not_found_or_redacted", correlationId);
      return notEligible(actor, notificationId, null, "not_found_or_redacted", correlationId);
    }
    var dedupeKey = dedupe(item);
    var existing = repository.findEmailDeliveryByDedupeKey(item.tenantId(), dedupeKey).orElse(null);
    if (existing != null) {
      appendAudit(actor, "EMAIL_NOTIFICATION_DUPLICATE", AdminAuditEvent.Result.NO_OP, existing.status().name().toLowerCase(Locale.ROOT), correlationId);
      return existing;
    }
    var denial = eligibilityDenial(actor, item);
    if (denial != null) {
      appendAudit(actor, "EMAIL_NOTIFICATION_EVALUATE_DENIED", AdminAuditEvent.Result.DENIED, denial, correlationId);
      return repository.saveEmailDelivery(notEligible(actor, item.notificationId(), item, denial, correlationId));
    }
    var now = Instant.now(clock);
    var outboxId = "email-notification-outbox-" + Math.abs(dedupeKey.hashCode());
    var deliveryId = "email-notification-" + Math.abs(dedupeKey.hashCode());
    var content = redactedContent(item);
    var outbox = new EmailOutboxMessage(outboxId, "NOTIFICATION_EMAIL", null, "delivery-1", ScopeType.TENANT, item.tenantId(), item.customerId(), actor.account().normalizedEmail(), surfaceUrl(item), content.subject(), content.bodyText(), null, Map.of("category", item.category().name(), "sourceNotificationId", item.notificationId()), correlationId, now);
    repository.saveEmailOutbox(outbox);
    var delivery = new EmailNotificationDelivery(deliveryId, item.tenantId(), item.customerId(), item.accountId(), actor.account().normalizedEmail(), actor.selectedContext().membershipId(), item.category(), item.notificationId(), item.sourceRefs(), item.traceRefs(), item.requiredCapabilityId(), item.owningWorkstreamId(), content.subject(), content.preview(), content.bodyText(), surfaceUrl(item), NotificationRedactionLevel.SUMMARY_ONLY, dedupeKey, "delivery-1", outboxId, null, null, EmailNotificationDeliveryStatus.QUEUED, null, correlationId, now, now);
    var saved = repository.saveEmailDelivery(delivery);
    appendAudit(actor, "EMAIL_NOTIFICATION_ENQUEUE", AdminAuditEvent.Result.ALLOWED, outboxId, correlationId);
    return saved;
  }

  public EmailNotificationDelivery deliverOutbox(AuthContextResolver.ResolvedMe actor, String deliveryId, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), DELIVER_OUTBOX_TOOL);
    var delivery = repository.findEmailDelivery(actor.selectedContext().tenantId(), deliveryId).orElseThrow(() -> new AuthorizationException(404, "email-delivery-not-found-or-redacted"));
    if (!actor.account().accountId().equals(delivery.accountId()) && !actor.selectedContext().hasCapability("tenant.audit.read")) throw new AuthorizationException(404, "email-delivery-not-found-or-redacted");
    if (delivery.status() == EmailNotificationDeliveryStatus.CAPTURED || delivery.status() == EmailNotificationDeliveryStatus.SENT || delivery.status() == EmailNotificationDeliveryStatus.FAILED) {
      appendAudit(actor, "EMAIL_NOTIFICATION_DELIVER_DUPLICATE", AdminAuditEvent.Result.NO_OP, delivery.status().name().toLowerCase(Locale.ROOT), correlationId);
      return delivery;
    }
    var outbox = repository.findEmailOutbox(delivery.tenantId(), delivery.outboxId()).orElseThrow(() -> new AuthorizationException(500, "email-outbox-missing"));
    var result = resendEmailService.deliver(outbox, deliveryMode);
    var now = Instant.now(clock);
    var provider = result.kind() == ResendEmailService.DeliveryKind.CAPTURED ? "captured_outbox" : "resend";
    var status = result.kind() == ResendEmailService.DeliveryKind.CAPTURED ? EmailNotificationDeliveryStatus.CAPTURED : result.kind() == ResendEmailService.DeliveryKind.SENT ? EmailNotificationDeliveryStatus.SENT : EmailNotificationDeliveryStatus.FAILED;
    var updated = delivery.withResult(status, provider, result.providerMessageId(), result.safeErrorSummary(), now, correlationId);
    repository.saveEmailDelivery(updated);
    appendAudit(actor, "EMAIL_NOTIFICATION_DELIVERY_" + status.name(), result.success() ? AdminAuditEvent.Result.ALLOWED : AdminAuditEvent.Result.FAILED, result.success() ? provider : result.safeErrorSummary(), correlationId);
    return updated;
  }

  public EmailNotificationDelivery getDeliveryStatus(AuthContextResolver.ResolvedMe actor, String deliveryId, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), GET_STATUS_TOOL);
    var delivery = repository.findEmailDelivery(actor.selectedContext().tenantId(), deliveryId).orElseThrow(() -> new AuthorizationException(404, "email-delivery-not-found-or-redacted"));
    if (!actor.account().accountId().equals(delivery.accountId()) && !actor.selectedContext().hasCapability("tenant.audit.read")) throw new AuthorizationException(404, "email-delivery-not-found-or-redacted");
    appendAudit(actor, "EMAIL_NOTIFICATION_GET_STATUS", AdminAuditEvent.Result.ALLOWED, delivery.status().name().toLowerCase(Locale.ROOT), correlationId);
    return delivery;
  }

  private boolean visible(AuthContextResolver.ResolvedMe actor, NotificationItem item) {
    return actor.selectedContext().tenantId().equals(item.tenantId())
        && (actor.selectedContext().customerId() == null || item.customerId() == null || actor.selectedContext().customerId().equals(item.customerId()))
        && actor.account().accountId().equals(item.accountId())
        && actor.selectedContext().capabilities().contains(item.requiredCapabilityId());
  }

  private String eligibilityDenial(AuthContextResolver.ResolvedMe actor, NotificationItem item) {
    if (actor.account().normalizedEmail() == null || actor.account().normalizedEmail().isBlank()) return "recipient-email-missing";
    if (!allowlisted(item)) return "category-not-allowlisted";
    var now = Instant.now(clock);
    var prefs = repository.listEmailPreferences(item.tenantId(), item.accountId());
    var matching = prefs.stream().filter(pref -> pref.category() == item.category() || pref.category() == NotificationCategory.ALL).toList();
    if (matching.isEmpty()) return "preference-required";
    var allowed = matching.stream().allMatch(pref -> pref.enabled() && rank(item.priority()) >= rank(pref.minimumPriority()) && (pref.muteUntil() == null || !pref.muteUntil().isAfter(now)));
    return allowed ? null : "preference-disabled-or-muted";
  }

  private boolean allowlisted(NotificationItem item) {
    return switch (item.category()) {
      case DIGEST_READY, DIGEST_BLOCKED, POLICY_OR_GOVERNANCE, AUDIT_OR_SECURITY -> true;
      case ATTENTION_REQUIRED, PROVIDER_READINESS -> rank(item.priority()) >= rank(NotificationPriority.URGENT);
      default -> false;
    };
  }

  private EmailNotificationDelivery notEligible(AuthContextResolver.ResolvedMe actor, String notificationId, NotificationItem item, String reason, String correlationId) {
    var now = Instant.now(clock);
    var tenantId = item == null ? actor.selectedContext().tenantId() : item.tenantId();
    var accountId = item == null ? actor.account().accountId() : item.accountId();
    var dedupeKey = "notification:email:" + tenantId + ":none:" + accountId + ":not_eligible:" + notificationId;
    return new EmailNotificationDelivery("email-notification-denied-" + Math.abs(dedupeKey.hashCode()), tenantId, item == null ? null : item.customerId(), accountId, actor.account().normalizedEmail(), actor.selectedContext().membershipId(), item == null ? null : item.category(), notificationId, List.of(), List.of(), item == null ? null : item.requiredCapabilityId(), item == null ? null : item.owningWorkstreamId(), null, null, null, null, NotificationRedactionLevel.NOT_FOUND_OR_REDACTED, dedupeKey, null, null, null, null, EmailNotificationDeliveryStatus.NOT_ELIGIBLE, reason, correlationId, now, now);
  }

  private String dedupe(NotificationItem item) {
    return "notification:email:" + item.tenantId() + ":" + firstNonBlank(item.customerId(), "none") + ":" + item.accountId() + ":" + item.category().name().toLowerCase(Locale.ROOT) + ":" + firstNonBlank(item.origin(), "notification") + ":" + item.notificationId() + ":notify";
  }

  private RenderedEmail redactedContent(NotificationItem item) {
    var title = safe(firstNonBlank(item.title(), "Notification update"));
    var preview = safe(firstNonBlank(item.summary(), "A notification requires your review."));
    var subject = "Notification: " + title;
    var body = title + "\n\n" + preview + "\n\nOpen the application to review authorized details. Raw source payloads, provider secrets, tokens, hidden prompts, and cross-tenant details are not included in email.";
    return new RenderedEmail(subject, preview, body);
  }

  private String surfaceUrl(NotificationItem item) {
    return item.surfaceRef() == null ? "https://app.example.test/my-account/notifications" : "https://app.example.test/my-account/notifications/" + item.notificationId();
  }

  private int rank(NotificationPriority priority) {
    return switch (priority == null ? NotificationPriority.INFO : priority) {
      case INFO -> 0;
      case WARNING -> 1;
      case URGENT -> 2;
      case BLOCKED -> 3;
    };
  }

  private String safe(String value) {
    if (value == null) return "";
    return value.replaceAll("(?i)(bearer\\s+[a-z0-9._-]+|password=[^\\s]+|token=[^\\s]+|secret=[^\\s]+|api[_-]?key=[^\\s,}]+|sk-[A-Za-z0-9_-]+)", "[redacted]");
  }

  private String firstNonBlank(String... values) {
    for (var value : values) if (value != null && !value.isBlank()) return value;
    return "";
  }

  private void appendAudit(AuthContextResolver.ResolvedMe actor, String action, AdminAuditEvent.Result result, String reason, String correlationId) {
    var safeCorrelationId = firstNonBlank(correlationId, actor.correlationId(), "corr-email-notification-" + UUID.randomUUID());
    var safeReason = result.name().toLowerCase(Locale.ROOT) + ":" + safe(reason);
    if (result == AdminAuditEvent.Result.DENIED) authContextResolver.appendDeniedTrace(actor, action, safeReason, safeCorrelationId);
    else authContextResolver.appendProtectedReadTrace(actor, action, safeReason, safeCorrelationId);
  }

  private record RenderedEmail(String subject, String preview, String bodyText) {}
}

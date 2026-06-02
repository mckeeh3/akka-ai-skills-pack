package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.domain.security.Account;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.EmailNotificationDeliveryStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.Membership;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationCategory;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPriority;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.Tenant;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile;
import {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmailNotificationServiceTest {
  private LocalDemoIdentityRepository identityRepository;
  private LocalDemoNotificationRepository notificationRepository;
  private AuthContextResolver resolver;
  private NotificationService notifications;
  private EmailNotificationService email;
  private Clock clock;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    notificationRepository = new LocalDemoNotificationRepository();
    resolver = new AuthContextResolver(identityRepository);
    clock = Clock.fixed(Instant.parse("2026-05-27T10:00:00Z"), ZoneOffset.UTC);
    notifications = new NotificationService(notificationRepository, resolver, clock);
    email = new EmailNotificationService(notificationRepository, resolver, new ResendEmailService(Map.of(), (message, config) -> ResendEmailService.DeliveryResult.sent("should-not-send")), ResendEmailService.DeliveryMode.LOCAL_OR_TEST, clock);

    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    addAccount("admin@example.test", "membership-admin", "tenant-1", List.of(FoundationRole.TENANT_ADMIN));
  }

  @Test
  void emailChannelRequiresOptInAllowlistAndWritesCapturedOutbox() {
    var actor = actor("corr-email");
    var source = notifications.projectFromPersonalDigest(actor, digest("digest-ready", {{JAVA_BASE_PACKAGE}}.domain.security.MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED, "Digest token=secret ready", null), "corr-project");

    var denied = email.evaluateAndEnqueue(actor, source.notificationId(), "corr-denied");
    assertEquals(EmailNotificationDeliveryStatus.NOT_ELIGIBLE, denied.status());
    assertEquals("preference-required", denied.safeErrorSummary());

    email.updatePreference(actor, NotificationCategory.DIGEST_READY, true, NotificationPriority.INFO, null, "corr-pref");
    var queued = email.evaluateAndEnqueue(actor, source.notificationId(), "corr-queue");
    assertEquals(EmailNotificationDeliveryStatus.QUEUED, queued.status());
    assertEquals("captured_outbox", email.deliverOutbox(actor, queued.deliveryId(), "corr-deliver").provider());
    assertEquals(EmailNotificationDeliveryStatus.CAPTURED, email.getDeliveryStatus(actor, queued.deliveryId(), "corr-status").status());
    assertEquals(1, notificationRepository.listEmailOutbox("tenant-1").size());
    assertTrue(notificationRepository.listEmailOutbox("tenant-1").get(0).bodyText().contains("[redacted]"));
    assertFalse(notificationRepository.listEmailOutbox("tenant-1").get(0).bodyText().contains("token=secret"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("EMAIL_NOTIFICATION_DELIVERY_CAPTURED")));
  }

  @Test
  void duplicateEmailProjectionReturnsExistingDeliveryWithoutDuplicateOutbox() {
    var actor = actor("corr-dup");
    email.updatePreference(actor, NotificationCategory.DIGEST_READY, true, NotificationPriority.INFO, null, "corr-pref");
    var source = notifications.projectFromPersonalDigest(actor, digest("digest-dup", {{JAVA_BASE_PACKAGE}}.domain.security.MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED, "Digest ready", null), "corr-project");

    var first = email.evaluateAndEnqueue(actor, source.notificationId(), "corr-first");
    var second = email.evaluateAndEnqueue(actor, source.notificationId(), "corr-second");

    assertEquals(first.deliveryId(), second.deliveryId());
    assertEquals(1, notificationRepository.listEmailOutbox("tenant-1").size());
    assertTrue(first.dedupeKey().startsWith("notification:email:"));
  }

  @Test
  void productionDeliveryFailsClosedWhenResendConfigMissing() {
    var actor = actor("corr-prod");
    var productionEmail = new EmailNotificationService(notificationRepository, resolver, new ResendEmailService(Map.of(), (message, config) -> ResendEmailService.DeliveryResult.sent("should-not-send")), ResendEmailService.DeliveryMode.PRODUCTION, clock);
    productionEmail.updatePreference(actor, NotificationCategory.DIGEST_READY, true, NotificationPriority.INFO, null, "corr-pref");
    var source = notifications.projectFromPersonalDigest(actor, digest("digest-prod", {{JAVA_BASE_PACKAGE}}.domain.security.MyAccountPersonalAttentionDigestTask.Status.COMPLETED_REVIEW_REQUIRED, "Digest ready", null), "corr-project");
    var queued = productionEmail.evaluateAndEnqueue(actor, source.notificationId(), "corr-queue");

    var failed = productionEmail.deliverOutbox(actor, queued.deliveryId(), "corr-fail-closed");

    assertEquals(EmailNotificationDeliveryStatus.FAILED, failed.status());
    assertEquals("resend", failed.provider());
    assertEquals("resend-config-missing", failed.safeErrorSummary());
    assertFalse(failed.toString().contains("should-not-send"));
  }

  @Test
  void nonAllowlistedWorkstreamUpdatesAreDeniedByDefault() {
    var actor = actor("corr-allowlist");
    var input = new {{JAVA_BASE_PACKAGE}}.domain.security.NotificationProjectionInput(
        "event-1", "workstream_event", "tenant-1", null, "admin@example.test", Map.of("selectedContextId", "membership-admin"), "agent-my-account", "my_account.view_summary",
        List.of(new {{JAVA_BASE_PACKAGE}}.domain.security.NotificationSourceRef("workstream_event", "event-1", "Routine update", "my_account.view_summary", "trace-event-1", "corr-event")),
        List.of("trace-event-1"), "Routine update", "No email", NotificationCategory.WORKSTREAM_UPDATE, NotificationPriority.INFO,
        new {{JAVA_BASE_PACKAGE}}.domain.security.NotificationSurfaceRef("agent-my-account", "surface-my-account", "dashboard", "event-1", "open", "my_account.view_summary"),
        "notification:in_app:tenant-1:none:admin@example.test:workstream_event:event-1:update", "corr-event");
    var notification = notifications.projectFromSource(actor, input, "corr-event");
    email.updatePreference(actor, NotificationCategory.ALL, true, NotificationPriority.INFO, null, "corr-pref");

    var denied = email.evaluateAndEnqueue(actor, notification.notificationId(), "corr-denied");

    assertEquals(EmailNotificationDeliveryStatus.NOT_ELIGIBLE, denied.status());
    assertEquals("category-not-allowlisted", denied.safeErrorSummary());
  }

  private {{JAVA_BASE_PACKAGE}}.domain.security.MyAccountPersonalAttentionDigestTask digest(String digestTaskId, {{JAVA_BASE_PACKAGE}}.domain.security.MyAccountPersonalAttentionDigestTask.Status status, String summary, String blockerCode) {
    var now = Instant.parse("2026-05-27T09:00:00Z");
    return new {{JAVA_BASE_PACKAGE}}.domain.security.MyAccountPersonalAttentionDigestTask(digestTaskId, "agent-task-" + digestTaskId, "tenant-1", null, "membership-admin", "admin@example.test", "membership-admin", "idem-" + digestTaskId, 3, status, 100, summary, blockerCode, null, null, List.of("evidence-" + digestTaskId), List.of("section-" + digestTaskId), List.of("trace-" + digestTaskId), now, now);
  }

  private AuthContextResolver.ResolvedMe actor(String correlationId) {
    return resolver.resolveMe(new WorkosIdentity("workos-admin@example.test", "admin@example.test", "Admin"), "membership-admin", correlationId);
  }

  private void addAccount(String email, String membershipId, String tenantId, List<FoundationRole> roles) {
    identityRepository.saveAccount(new Account(email, "workos-" + email, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, null, null, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, tenantId, null, roles, MembershipStatus.ACTIVE, false, null));
  }
}

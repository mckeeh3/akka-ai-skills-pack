package ai.first.application.foundation.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.KeyValueEntityTestKit;
import ai.first.domain.security.DigestExportRequest;
import ai.first.domain.foundation.notification.NotificationCategory;
import ai.first.domain.foundation.notification.NotificationChannel;
import ai.first.domain.foundation.notification.NotificationDeliveryAttempt;
import ai.first.domain.foundation.notification.NotificationDeliveryAttemptStatus;
import ai.first.domain.foundation.notification.NotificationExternalOutboxMessage;
import ai.first.domain.foundation.notification.NotificationItem;
import ai.first.domain.foundation.notification.NotificationLifecycleStatus;
import ai.first.domain.foundation.notification.NotificationPreference;
import ai.first.domain.foundation.notification.NotificationPriority;
import ai.first.domain.foundation.notification.NotificationRedactionLevel;
import ai.first.domain.foundation.notification.NotificationSourceRef;
import ai.first.domain.foundation.notification.NotificationSurfaceRef;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class DurableNotificationRepositoryEntityTest {
  private KeyValueEntityTestKit<NotificationRepositoryState, DurableNotificationRepositoryEntity> newTestKit() {
    return KeyValueEntityTestKit.of(DurableNotificationRepositoryEntity.ENTITY_ID, __ -> new DurableNotificationRepositoryEntity());
  }

  @Test
  void persistsFindsListsDedupeAndPreferencesForTenantScopedNotifications() {
    var testKit = newTestKit();
    var item = item("notification-1", "tenant-1", "notification:in_app:tenant-1:none:account-1:attention:attention-1:attention_required");

    var upsert = testKit.method(DurableNotificationRepositoryEntity::upsert).invoke(item);

    assertTrue(upsert.stateWasUpdated());
    assertEquals(item, upsert.getReply());
    assertEquals(item, testKit.method(DurableNotificationRepositoryEntity::find)
        .invoke(new DurableNotificationRepositoryEntity.FindQuery("tenant-1", "notification-1"))
        .getReply().orElseThrow());
    assertEquals(item, testKit.method(DurableNotificationRepositoryEntity::findByDedupeKey)
        .invoke(new DurableNotificationRepositoryEntity.FindDedupeQuery("tenant-1", item.dedupeKey()))
        .getReply().orElseThrow());
    assertEquals(List.of(item), testKit.method(DurableNotificationRepositoryEntity::listTenant)
        .invoke(new DurableNotificationRepositoryEntity.ListTenantQuery("tenant-1"))
        .getReply());
    assertTrue(testKit.method(DurableNotificationRepositoryEntity::listTenant)
        .invoke(new DurableNotificationRepositoryEntity.ListTenantQuery("tenant-2"))
        .getReply().isEmpty());

    var read = item.markRead(Instant.parse("2026-05-26T11:00:00Z"), "corr-read");
    testKit.method(DurableNotificationRepositoryEntity::save).invoke(read);
    assertEquals(NotificationLifecycleStatus.READ, testKit.method(DurableNotificationRepositoryEntity::find)
        .invoke(new DurableNotificationRepositoryEntity.FindQuery("tenant-1", "notification-1"))
        .getReply().orElseThrow().status());

    var preference = new NotificationPreference("pref-1", "tenant-1", null, "account-1", NotificationChannel.IN_APP, NotificationCategory.PROVIDER_READINESS, true, NotificationPriority.BLOCKED, null, false, Instant.parse("2026-05-26T10:00:00Z"), "account-1", "corr-pref");
    testKit.method(DurableNotificationRepositoryEntity::savePreference).invoke(preference);
    assertEquals(preference, testKit.method(DurableNotificationRepositoryEntity::findPreference)
        .invoke(new DurableNotificationRepositoryEntity.FindPreferenceQuery("tenant-1", "pref-1"))
        .getReply().orElseThrow());
    assertEquals(List.of(preference), testKit.method(DurableNotificationRepositoryEntity::listPreferences)
        .invoke(new DurableNotificationRepositoryEntity.ListPreferencesQuery("tenant-1", "account-1"))
        .getReply());
  }

  @Test
  void persistsProviderNeutralDeliveryAttemptsAndCapturedExternalOutbox() {
    var testKit = newTestKit();
    var now = Instant.parse("2026-05-26T10:00:00Z");
    var attempt = new NotificationDeliveryAttempt("attempt-1", "tenant-1", null, "account-1", NotificationChannel.WEBHOOK, NotificationCategory.PROVIDER_READINESS, "notification-1", List.of(), List.of("trace-1"), "agent_admin.list_definitions", "agent-agent-admin", "webhook redacted", "provider_unconfigured", NotificationDeliveryAttemptStatus.BLOCKED_PROVIDER_UNCONFIGURED, "Production provider is not configured.", "dedupe-webhook-1", "outbox-1", "corr-webhook", now, now);
    var outbox = new NotificationExternalOutboxMessage("outbox-1", "tenant-1", null, "account-1", NotificationChannel.WEBHOOK, "webhook redacted", "Provider readiness blocked", "Provider configuration needs attention.", java.util.Map.of("sourceNotificationId", "notification-1"), "corr-webhook", now);

    testKit.method(DurableNotificationRepositoryEntity::saveDeliveryAttempt).invoke(attempt);
    testKit.method(DurableNotificationRepositoryEntity::saveExternalOutbox).invoke(outbox);

    assertEquals(attempt, testKit.method(DurableNotificationRepositoryEntity::findDeliveryAttempt)
        .invoke(new DurableNotificationRepositoryEntity.FindQuery("tenant-1", "attempt-1"))
        .getReply().orElseThrow());
    assertEquals(attempt, testKit.method(DurableNotificationRepositoryEntity::findDeliveryAttemptByDedupeKey)
        .invoke(new DurableNotificationRepositoryEntity.FindDedupeQuery("tenant-1", "dedupe-webhook-1"))
        .getReply().orElseThrow());
    assertEquals(List.of(attempt), testKit.method(DurableNotificationRepositoryEntity::listDeliveryAttempts)
        .invoke(new DurableNotificationRepositoryEntity.ListPreferencesQuery("tenant-1", "account-1"))
        .getReply());
    assertEquals(List.of(outbox), testKit.method(DurableNotificationRepositoryEntity::listExternalOutbox)
        .invoke(new DurableNotificationRepositoryEntity.ListPreferencesQuery("tenant-1", "account-1"))
        .getReply());
  }

  @Test
  void persistsDigestExportRequestLifecycleAndDueScheduledQueries() {
    var testKit = newTestKit();
    var now = Instant.parse("2026-05-26T10:00:00Z");
    var scheduled = new DigestExportRequest("scheduled-digest-1", DigestExportRequest.RequestType.SCHEDULED_DIGEST, "tenant-1", null, "account-1", "membership-1", "idem-scheduled", DigestExportRequest.Status.SCHEDULED, DigestExportRequest.RedactionProfile.AUDIT_SAFE, DigestExportRequest.ExportFormat.MARKDOWN, false, null, Instant.parse("2026-05-26T11:00:00Z"), "audit", null, "scheduled", null, List.of("trace-scheduled"), now, now);

    testKit.method(DurableNotificationRepositoryEntity::saveDigestExportRequest).invoke(scheduled);

    assertEquals(scheduled, testKit.method(DurableNotificationRepositoryEntity::findDigestExportRequest)
        .invoke(new DurableNotificationRepositoryEntity.FindQuery("tenant-1", "scheduled-digest-1"))
        .getReply().orElseThrow());
    assertEquals(scheduled, testKit.method(DurableNotificationRepositoryEntity::findDigestExportRequestByIdempotencyKey)
        .invoke(new DurableNotificationRepositoryEntity.FindDigestExportDedupeQuery("tenant-1", "account-1", "idem-scheduled"))
        .getReply().orElseThrow());
    assertTrue(testKit.method(DurableNotificationRepositoryEntity::listDueDigestExportRequests)
        .invoke(new DurableNotificationRepositoryEntity.ListDueDigestExportQuery("tenant-1", Instant.parse("2026-05-26T10:59:59Z")))
        .getReply().isEmpty());
    assertEquals(List.of(scheduled), testKit.method(DurableNotificationRepositoryEntity::listDueDigestExportRequests)
        .invoke(new DurableNotificationRepositoryEntity.ListDueDigestExportQuery("tenant-1", Instant.parse("2026-05-26T11:00:00Z")))
        .getReply());
  }

  private NotificationItem item(String notificationId, String tenantId, String dedupeKey) {
    var now = Instant.parse("2026-05-26T10:00:00Z");
    return new NotificationItem(notificationId, tenantId, null, "account-1", "membership-1", NotificationChannel.IN_APP, "Provider readiness blocked", "Provider configuration needs attention.", NotificationCategory.PROVIDER_READINESS, NotificationPriority.BLOCKED, NotificationLifecycleStatus.UNREAD, List.of(new NotificationSourceRef("audit_trace", "trace-provider", "Provider trace", "agent_admin.list_definitions", "trace-provider", "corr-provider")), new NotificationSurfaceRef("agent-agent-admin", "surface-agent-admin-catalog", "dashboard", "attention-1", "attention.open_attention_item", "agent_admin.list_definitions"), "agent_admin.list_definitions", "agent-agent-admin", "attention", NotificationRedactionLevel.FULL, dedupeKey, "corr-provider", List.of("trace-provider"), now, now, now, null, null, null, null, null);
  }
}

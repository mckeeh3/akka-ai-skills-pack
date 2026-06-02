package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.KeyValueEntityTestKit;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationCategory;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationChannel;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationItem;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPreference;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationPriority;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationRedactionLevel;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationSourceRef;
import {{JAVA_BASE_PACKAGE}}.domain.security.NotificationSurfaceRef;
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

  private NotificationItem item(String notificationId, String tenantId, String dedupeKey) {
    var now = Instant.parse("2026-05-26T10:00:00Z");
    return new NotificationItem(notificationId, tenantId, null, "account-1", "membership-1", NotificationChannel.IN_APP, "Provider readiness blocked", "Provider configuration needs attention.", NotificationCategory.PROVIDER_READINESS, NotificationPriority.BLOCKED, NotificationLifecycleStatus.UNREAD, List.of(new NotificationSourceRef("audit_trace", "trace-provider", "Provider trace", "agent_admin.list_definitions", "trace-provider", "corr-provider")), new NotificationSurfaceRef("agent-agent-admin", "surface-agent-admin-catalog", "dashboard", "attention-1", "attention.open_attention_item", "agent_admin.list_definitions"), "agent_admin.list_definitions", "agent-agent-admin", "attention", NotificationRedactionLevel.FULL, dedupeKey, "corr-provider", List.of("trace-provider"), now, now, now, null, null, null, null, null);
  }
}

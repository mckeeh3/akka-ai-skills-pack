package ai.first.domain.security;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.notification.NotificationChannel;
import ai.first.domain.foundation.notification.NotificationItem;
import ai.first.domain.foundation.notification.NotificationPreference;
import ai.first.domain.foundation.notification.NotificationRedactionLevel;
import java.util.List;
import java.util.Map;

/** Backend-shaped My Account in-app notification center data. */
public record MyAccountNotificationCenter(
    String surfaceContract,
    NotificationChannel channel,
    int unreadCount,
    int visibleCount,
    List<NotificationItem> items,
    List<NotificationPreference> preferencesSummary,
    Map<String, Long> sourceSummary,
    NotificationRedactionLevel redaction,
    List<String> traceRefs,
    String correlationId) {
  public MyAccountNotificationCenter {
    items = List.copyOf(items == null ? List.of() : items);
    preferencesSummary = List.copyOf(preferencesSummary == null ? List.of() : preferencesSummary);
    sourceSummary = Map.copyOf(sourceSummary == null ? Map.of() : sourceSummary);
    traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
  }
}

package ai.first.domain.foundation.notification;

import java.util.List;
import java.util.Map;

/** Normalized backend-owned projection input for creating/updating in-app notifications. */
public record NotificationProjectionInput(
    String inputId,
    String inputFamily,
    String tenantId,
    String customerId,
    String recipientAccountId,
    Map<String, String> authContext,
    String owningWorkstreamId,
    String requiredCapabilityId,
    List<NotificationSourceRef> sourceRefs,
    List<String> traceRefs,
    String title,
    String summary,
    NotificationCategory category,
    NotificationPriority priority,
    NotificationSurfaceRef surfaceRef,
    String idempotencyKey,
    String correlationId) {
  public NotificationProjectionInput {
    authContext = Map.copyOf(authContext == null ? Map.of() : authContext);
    sourceRefs = List.copyOf(sourceRefs == null ? List.of() : sourceRefs);
    traceRefs = List.copyOf(traceRefs == null ? List.of() : traceRefs);
  }
}

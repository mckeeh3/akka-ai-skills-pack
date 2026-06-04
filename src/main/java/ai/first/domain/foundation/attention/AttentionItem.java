package ai.first.domain.foundation.attention;

import java.time.Instant;
import java.util.List;

/** Durable shared attention item owned by one workstream and projected into authorized aggregates. */
public record AttentionItem(
    String itemId,
    String tenantId,
    String customerId,
    String owningWorkstreamId,
    String title,
    String summary,
    AttentionCategory category,
    AttentionSeverity severity,
    AttentionItemStatus status,
    AssigneeKind assigneeKind,
    String assigneeId,
    String requiredCapabilityId,
    AttentionSurfaceRef surfaceRef,
    List<AttentionSourceRef> sourceRefs,
    AttentionRedactionLevel redactionLevel,
    Instant createdAt,
    Instant updatedAt,
    Instant lastChangedAt,
    Instant expiresAt,
    Instant acknowledgedAt,
    Instant resolvedAt,
    Instant dismissedAt,
    String correlationId) {
  public enum AssigneeKind {
    ACCOUNT,
    ROLE,
    CAPABILITY,
    WORKSTREAM,
    TENANT
  }

  public AttentionItem {
    sourceRefs = List.copyOf(sourceRefs == null ? List.of() : sourceRefs);
    redactionLevel = redactionLevel == null ? AttentionRedactionLevel.FULL : redactionLevel;
  }

  public AttentionItem acknowledge(Instant now, String correlationId) {
    if (status == AttentionItemStatus.ACKNOWLEDGED) return this;
    return new AttentionItem(itemId, tenantId, customerId, owningWorkstreamId, title, summary, category, severity,
        AttentionItemStatus.ACKNOWLEDGED, assigneeKind, assigneeId, requiredCapabilityId, surfaceRef, sourceRefs,
        redactionLevel, createdAt, now, now, expiresAt, now, resolvedAt, dismissedAt, correlationId);
  }

  public AttentionItem resolve(Instant now, String correlationId) {
    if (status == AttentionItemStatus.RESOLVED) return this;
    return new AttentionItem(itemId, tenantId, customerId, owningWorkstreamId, title, summary, category, severity,
        AttentionItemStatus.RESOLVED, assigneeKind, assigneeId, requiredCapabilityId, surfaceRef, sourceRefs,
        redactionLevel, createdAt, now, now, expiresAt, acknowledgedAt, now, dismissedAt, correlationId);
  }

  public AttentionItem dismiss(Instant now, String correlationId) {
    if (status == AttentionItemStatus.DISMISSED) return this;
    return new AttentionItem(itemId, tenantId, customerId, owningWorkstreamId, title, summary, category, severity,
        AttentionItemStatus.DISMISSED, assigneeKind, assigneeId, requiredCapabilityId, surfaceRef, sourceRefs,
        redactionLevel, createdAt, now, now, expiresAt, acknowledgedAt, resolvedAt, now, correlationId);
  }

  public AttentionItem expire(Instant now, String correlationId) {
    if (status == AttentionItemStatus.EXPIRED) return this;
    return new AttentionItem(itemId, tenantId, customerId, owningWorkstreamId, title, summary, category, severity,
        AttentionItemStatus.EXPIRED, assigneeKind, assigneeId, requiredCapabilityId, surfaceRef, sourceRefs,
        redactionLevel, createdAt, now, now, expiresAt, acknowledgedAt, resolvedAt, dismissedAt, correlationId);
  }

  public boolean countsAsActionable() {
    return status == AttentionItemStatus.OPEN || status == AttentionItemStatus.ACKNOWLEDGED;
  }
}

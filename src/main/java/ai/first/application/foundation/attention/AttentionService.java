package ai.first.application.foundation.attention;

import ai.first.domain.foundation.attention.AttentionSurfaceRef;
import ai.first.domain.foundation.audit.AdminAuditEvent;
import ai.first.domain.foundation.attention.AttentionCategory;
import ai.first.domain.foundation.attention.AttentionItem;
import ai.first.domain.foundation.attention.AttentionItemStatus;
import ai.first.domain.foundation.attention.AttentionRedactionLevel;
import ai.first.domain.foundation.attention.AttentionSeverity;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;

/** Shared backend-owned attention backbone with scoped projections and lifecycle operations. */
public final class AttentionService {
  public static final String CAPABILITY_GROUP = "attention.backbone";
  public static final String LIST_WORKSTREAM_ITEMS_TOOL = "attention.list_workstream_items";
  public static final String LIST_MY_ACCOUNT_ITEMS_TOOL = "attention.list_my_account_items";
  public static final String LIST_RAIL_SUMMARIES_TOOL = "attention.list_rail_summaries";
  public static final String OPEN_ATTENTION_ITEM_TOOL = "attention.open_attention_item";
  public static final String ACKNOWLEDGE_ITEM_TOOL = "attention.acknowledge_item";
  public static final String RESOLVE_ITEM_TOOL = "attention.resolve_item";
  public static final String DISMISS_ITEM_TOOL = "attention.dismiss_item";
  public static final String UPSERT_ITEM_TOOL = "attention.upsert_item";

  private final AttentionRepository repository;
  private final AuthContextResolver authContextResolver;
  private final Clock clock;

  public AttentionService(AttentionRepository repository, AuthContextResolver authContextResolver, Clock clock) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.clock = Objects.requireNonNull(clock);
  }

  public AttentionItem upsertItem(AuthContextResolver.ResolvedMe actor, AttentionItem item, String correlationId) {
    requireSameScope(actor, item);
    authContextResolver.requireCapability(actor.selectedContext(), item.requiredCapabilityId());
    var now = Instant.now(clock);
    var normalized = new AttentionItem(
        item.itemId(), item.tenantId(), item.customerId(), item.owningWorkstreamId(), item.title(), item.summary(),
        item.category(), item.severity(), item.status() == null ? AttentionItemStatus.OPEN : item.status(), item.assigneeKind(),
        item.assigneeId(), item.requiredCapabilityId(), item.surfaceRef(), item.sourceRefs(), item.redactionLevel(),
        item.createdAt() == null ? now : item.createdAt(), now, now, item.expiresAt(), item.acknowledgedAt(), item.resolvedAt(),
        item.dismissedAt(), firstNonBlank(correlationId, item.correlationId()));
    var saved = repository.upsert(normalized);
    appendAudit(actor, "ATTENTION_UPSERT_ITEM", AdminAuditEvent.Result.ALLOWED, saved.itemId(), correlationId);
    return saved;
  }

  public List<AttentionItem> listWorkstreamItems(AuthContextResolver.ResolvedMe actor, String workstreamId, String correlationId) {
    var visible = visibleItems(actor).stream()
        .filter(item -> workstreamId == null || workstreamId.isBlank() || workstreamId.equals(item.owningWorkstreamId()))
        .sorted(Comparator.comparing(AttentionItem::lastChangedAt).reversed())
        .toList();
    appendAudit(actor, "ATTENTION_LIST_WORKSTREAM_ITEMS", AdminAuditEvent.Result.ALLOWED, firstNonBlank(workstreamId, "all-visible"), correlationId);
    return visible;
  }

  public MyAccountAttentionSummary listMyAccountItems(AuthContextResolver.ResolvedMe actor, String correlationId) {
    authContextResolver.requireCapability(actor.selectedContext(), "my_account.list_personal_attention");
    var items = visibleItems(actor).stream().filter(AttentionItem::countsAsActionable).toList();
    var summaries = items.stream()
        .collect(Collectors.groupingBy(AttentionItem::owningWorkstreamId, LinkedHashMap::new, Collectors.toList()))
        .entrySet().stream()
        .map(entry -> summary(entry.getKey(), entry.getValue()))
        .toList();
    appendAudit(actor, "ATTENTION_LIST_MY_ACCOUNT_ITEMS", AdminAuditEvent.Result.ALLOWED, "authorized personal attention", correlationId);
    return new MyAccountAttentionSummary(items.size(), highestSeverity(items), summaries, items, traceRefs(items), AttentionRedactionLevel.FULL);
  }

  public List<WorkstreamAttentionSummary> listRailSummaries(AuthContextResolver.ResolvedMe actor, String correlationId) {
    var summaries = visibleItems(actor).stream().filter(AttentionItem::countsAsActionable)
        .collect(Collectors.groupingBy(AttentionItem::owningWorkstreamId, LinkedHashMap::new, Collectors.toList()))
        .entrySet().stream()
        .map(entry -> summary(entry.getKey(), entry.getValue()).withoutItems())
        .toList();
    appendAudit(actor, "ATTENTION_LIST_RAIL_SUMMARIES", AdminAuditEvent.Result.ALLOWED, "visible rail summaries", correlationId);
    return summaries;
  }

  public OpenAttentionItemResult openAttentionItem(AuthContextResolver.ResolvedMe actor, String itemId, String correlationId) {
    var item = authorizedItem(actor, itemId, "ATTENTION_OPEN_ITEM", correlationId);
    if (item == null) return OpenAttentionItemResult.redacted(correlationId);
    appendAudit(actor, "ATTENTION_OPEN_ITEM", AdminAuditEvent.Result.ALLOWED, item.itemId(), correlationId);
    return new OpenAttentionItemResult("accepted", item.itemId(), item.owningWorkstreamId(), item.surfaceRef(), item.sourceRefs().stream().map(ref -> ref.traceId()).filter(Objects::nonNull).toList(), AttentionRedactionLevel.FULL, correlationId);
  }

  public AttentionItem acknowledge(AuthContextResolver.ResolvedMe actor, String itemId, String correlationId) {
    return lifecycle(actor, itemId, "ATTENTION_ACKNOWLEDGE_ITEM", item -> item.acknowledge(Instant.now(clock), correlationId), correlationId);
  }

  public AttentionItem resolve(AuthContextResolver.ResolvedMe actor, String itemId, String correlationId) {
    return lifecycle(actor, itemId, "ATTENTION_RESOLVE_ITEM", item -> item.resolve(Instant.now(clock), correlationId), correlationId);
  }

  public AttentionItem dismiss(AuthContextResolver.ResolvedMe actor, String itemId, String correlationId) {
    return lifecycle(actor, itemId, "ATTENTION_DISMISS_ITEM", item -> item.dismiss(Instant.now(clock), correlationId), correlationId);
  }

  private AttentionItem lifecycle(AuthContextResolver.ResolvedMe actor, String itemId, String action, UnaryOperator<AttentionItem> change, String correlationId) {
    var current = authorizedItem(actor, itemId, action, correlationId);
    if (current == null) throw new AuthorizationException(404, "not_found_or_redacted");
    var next = change.apply(current);
    var noOp = next.equals(current);
    if (!noOp) repository.save(next);
    appendAudit(actor, action, noOp ? AdminAuditEvent.Result.NO_OP : AdminAuditEvent.Result.ALLOWED, itemId, correlationId);
    return noOp ? current : next;
  }

  private AttentionItem authorizedItem(AuthContextResolver.ResolvedMe actor, String itemId, String action, String correlationId) {
    var item = repository.find(actor.selectedContext().tenantId(), itemId).orElse(null);
    if (item == null || !isVisible(actor, item)) {
      appendAudit(actor, action, AdminAuditEvent.Result.DENIED, "not_found_or_redacted", correlationId);
      return null;
    }
    return item;
  }

  private List<AttentionItem> visibleItems(AuthContextResolver.ResolvedMe actor) {
    return repository.listTenant(actor.selectedContext().tenantId()).stream()
        .filter(item -> inSelectedScope(actor, item))
        .filter(item -> isVisible(actor, item))
        .map(item -> item.redactionLevel() == AttentionRedactionLevel.FULL ? item : redactSummary(item))
        .toList();
  }

  private boolean isVisible(AuthContextResolver.ResolvedMe actor, AttentionItem item) {
    return inSelectedScope(actor, item)
        && actor.selectedContext().capabilities().contains(item.requiredCapabilityId())
        && switch (item.assigneeKind()) {
          case ACCOUNT -> item.assigneeId() == null || item.assigneeId().equals(actor.account().accountId());
          case ROLE -> item.assigneeId() == null || actor.selectedContext().roles().stream().anyMatch(role -> role.name().equals(item.assigneeId()));
          case CAPABILITY -> item.assigneeId() == null || actor.selectedContext().capabilities().contains(item.assigneeId());
          case WORKSTREAM, TENANT -> true;
        };
  }

  private boolean inSelectedScope(AuthContextResolver.ResolvedMe actor, AttentionItem item) {
    return actor.selectedContext().tenantId().equals(item.tenantId())
        && (actor.selectedContext().customerId() == null || item.customerId() == null || actor.selectedContext().customerId().equals(item.customerId()));
  }

  private void requireSameScope(AuthContextResolver.ResolvedMe actor, AttentionItem item) {
    if (!actor.selectedContext().tenantId().equals(item.tenantId())) throw new AuthorizationException(403, "tenant-mismatch");
    if (actor.selectedContext().customerId() != null && item.customerId() != null && !actor.selectedContext().customerId().equals(item.customerId())) throw new AuthorizationException(403, "customer-mismatch");
  }

  private WorkstreamAttentionSummary summary(String workstreamId, List<AttentionItem> items) {
    return new WorkstreamAttentionSummary(workstreamId, workstreamId, items.size(), highestSeverity(items), categoryCounts(items), items.stream().map(AttentionItem::lastChangedAt).max(Comparator.naturalOrder()).orElse(null), items, traceRefs(items), AttentionRedactionLevel.FULL);
  }

  private AttentionSeverity highestSeverity(List<AttentionItem> items) {
    return items.stream().map(AttentionItem::severity).max(Comparator.comparingInt(this::severityRank)).orElse(AttentionSeverity.INFO);
  }

  private int severityRank(AttentionSeverity severity) {
    return switch (severity) {
      case INFO -> 0;
      case WARNING -> 1;
      case URGENT -> 2;
      case BLOCKED -> 3;
    };
  }

  private Map<AttentionCategory, Long> categoryCounts(List<AttentionItem> items) {
    return items.stream().collect(Collectors.groupingBy(AttentionItem::category, LinkedHashMap::new, Collectors.counting()));
  }

  private List<String> traceRefs(List<AttentionItem> items) {
    return items.stream().flatMap(item -> item.sourceRefs().stream()).map(ref -> ref.traceId()).filter(Objects::nonNull).distinct().toList();
  }

  private AttentionItem redactSummary(AttentionItem item) {
    return new AttentionItem(item.itemId(), item.tenantId(), item.customerId(), item.owningWorkstreamId(), item.title(), item.summary(), item.category(), item.severity(), item.status(), item.assigneeKind(), item.assigneeId(), item.requiredCapabilityId(), null, List.of(), AttentionRedactionLevel.SUMMARY_ONLY, item.createdAt(), item.updatedAt(), item.lastChangedAt(), item.expiresAt(), item.acknowledgedAt(), item.resolvedAt(), item.dismissedAt(), item.correlationId());
  }

  private void appendAudit(AuthContextResolver.ResolvedMe actor, String action, AdminAuditEvent.Result result, String reason, String correlationId) {
    var safeCorrelationId = firstNonBlank(correlationId, actor.correlationId(), "corr-attention-" + UUID.randomUUID());
    var safeReason = result.name().toLowerCase() + ":" + reason;
    if (result == AdminAuditEvent.Result.DENIED) {
      authContextResolver.appendDeniedTrace(actor, action, safeReason, safeCorrelationId);
    } else {
      authContextResolver.appendProtectedReadTrace(actor, action, safeReason, safeCorrelationId);
    }
  }

  private String firstNonBlank(String... values) {
    for (var value : values) if (value != null && !value.isBlank()) return value;
    return "";
  }

  public record WorkstreamAttentionSummary(String workstreamId, String displayName, int attentionCount, AttentionSeverity highestSeverity, Map<AttentionCategory, Long> categories, Instant lastChangedAt, List<AttentionItem> items, List<String> traceRefs, AttentionRedactionLevel redaction) {
    public WorkstreamAttentionSummary withoutItems() {
      return new WorkstreamAttentionSummary(workstreamId, displayName, attentionCount, highestSeverity, categories, lastChangedAt, List.of(), traceRefs, redaction);
    }
  }

  public record MyAccountAttentionSummary(int totalAttentionCount, AttentionSeverity highestSeverity, List<WorkstreamAttentionSummary> workstreams, List<AttentionItem> personalQueue, List<String> traceRefs, AttentionRedactionLevel redaction) {}

  public record OpenAttentionItemResult(String status, String itemId, String targetFunctionalAgentId, ai.first.domain.foundation.attention.AttentionSurfaceRef surfaceRef, List<String> traceRefs, AttentionRedactionLevel redaction, String correlationId) {
    static OpenAttentionItemResult redacted(String correlationId) {
      return new OpenAttentionItemResult("not_found_or_redacted", null, null, null, List.of(), AttentionRedactionLevel.NOT_FOUND_OR_REDACTED, correlationId);
    }
  }
}

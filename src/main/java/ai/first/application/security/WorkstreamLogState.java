package ai.first.application.security;

import ai.first.domain.foundation.identity.AuthContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Current durable workstream log state keyed by tenant, selected AuthContext, and functional agent. */
public record WorkstreamLogState(
    Map<String, WorkstreamService.WorkstreamItem> itemsByScopedId,
    Map<String, WorkstreamService.SurfaceEnvelope> surfacesByScopedId,
    Map<String, List<String>> itemScopedIdsByScope,
    Map<String, WorkstreamLogRepository.WorkstreamMessageLogEntry> messagesByIdempotencyKey) {

  public static WorkstreamLogState empty() {
    return new WorkstreamLogState(new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
  }

  public List<WorkstreamService.WorkstreamItem> items(String tenantId, String selectedContextId, String functionalAgentId) {
    var scopedIds = new ArrayList<String>();
    if (functionalAgentId == null || functionalAgentId.isBlank()) {
      var prefix = String.join(":", safe(tenantId), safe(selectedContextId), "");
      itemScopedIdsByScope.forEach((key, value) -> { if (key.startsWith(prefix)) scopedIds.addAll(value); });
    } else {
      scopedIds.addAll(itemScopedIdsByScope.getOrDefault(scopeKey(tenantId, selectedContextId, functionalAgentId), List.of()));
    }
    return scopedIds.stream()
        .map(itemsByScopedId::get)
        .filter(item -> item != null)
        .sorted(Comparator.comparing(WorkstreamService.WorkstreamItem::createdAt).thenComparing(WorkstreamService.WorkstreamItem::itemId))
        .toList();
  }

  public Optional<WorkstreamService.SurfaceEnvelope> surface(String tenantId, String selectedContextId, String surfaceId) {
    return Optional.ofNullable(surfacesByScopedId.get(scopedId(tenantId, selectedContextId, surfaceId)));
  }

  public Optional<WorkstreamLogRepository.WorkstreamMessageLogEntry> findByIdempotencyKey(String tenantId, String selectedContextId, String functionalAgentId, String idempotencyKey) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) return Optional.empty();
    return Optional.ofNullable(messagesByIdempotencyKey.get(messageKey(tenantId, selectedContextId, functionalAgentId, idempotencyKey)));
  }

  public WorkstreamLogState appendMessage(WorkstreamLogRepository.WorkstreamMessageLogEntry entry) {
    var hasIdempotencyKey = entry.idempotencyKey() != null && !entry.idempotencyKey().isBlank();
    var key = hasIdempotencyKey
        ? messageKey(entry.tenantId(), entry.selectedContextId(), entry.functionalAgentId(), entry.idempotencyKey())
        : messageKey(entry.tenantId(), entry.selectedContextId(), entry.functionalAgentId(), entry.agentItem().itemId());
    if (hasIdempotencyKey && messagesByIdempotencyKey.containsKey(key)) return this;
    var nextItems = new LinkedHashMap<>(itemsByScopedId);
    var scopeKey = scopeKey(entry.tenantId(), entry.selectedContextId(), entry.functionalAgentId());
    var userScopedId = scopedId(entry.tenantId(), entry.selectedContextId(), entry.userItem().itemId());
    var agentScopedId = scopedId(entry.tenantId(), entry.selectedContextId(), entry.agentItem().itemId());
    nextItems.put(userScopedId, entry.userItem());
    nextItems.put(agentScopedId, entry.agentItem());
    var nextSurfaces = new LinkedHashMap<>(surfacesByScopedId);
    nextSurfaces.put(scopedId(entry.tenantId(), entry.selectedContextId(), entry.surface().surfaceId()), entry.surface());
    var nextItemIdsByScope = copyIndex(itemScopedIdsByScope);
    addScopedItem(nextItemIdsByScope, scopeKey, userScopedId);
    addScopedItem(nextItemIdsByScope, scopeKey, agentScopedId);
    var nextMessages = new LinkedHashMap<>(messagesByIdempotencyKey);
    if (hasIdempotencyKey) nextMessages.put(key, entry);
    return new WorkstreamLogState(nextItems, nextSurfaces, nextItemIdsByScope, nextMessages);
  }

  public WorkstreamLogState appendSystemEntry(String tenantId, String selectedContextId, WorkstreamService.WorkstreamItem item, WorkstreamService.SurfaceEnvelope surface) {
    var scopedItemId = scopedId(tenantId, selectedContextId, item.itemId());
    if (itemsByScopedId.containsKey(scopedItemId)) return this;
    var nextItems = new LinkedHashMap<>(itemsByScopedId);
    nextItems.put(scopedItemId, item);
    var nextSurfaces = new LinkedHashMap<>(surfacesByScopedId);
    if (surface != null) nextSurfaces.put(scopedId(tenantId, selectedContextId, surface.surfaceId()), surface);
    var nextItemIdsByScope = copyIndex(itemScopedIdsByScope);
    addScopedItem(nextItemIdsByScope, scopeKey(tenantId, selectedContextId, item.functionalAgentId()), scopedItemId);
    return new WorkstreamLogState(nextItems, nextSurfaces, nextItemIdsByScope, new LinkedHashMap<>(messagesByIdempotencyKey));
  }

  static String messageKey(String tenantId, String selectedContextId, String functionalAgentId, String idempotencyKey) {
    return String.join(":", safe(tenantId), safe(selectedContextId), safe(functionalAgentId), safe(idempotencyKey));
  }

  private static String scopedId(String tenantId, String selectedContextId, String id) {
    return String.join(":", safe(tenantId), safe(selectedContextId), safe(id));
  }

  private static String scopeKey(String tenantId, String selectedContextId, String functionalAgentId) {
    return String.join(":", safe(tenantId), safe(selectedContextId), safe(functionalAgentId));
  }

  private static Map<String, List<String>> copyIndex(Map<String, List<String>> input) {
    var copy = new LinkedHashMap<String, List<String>>();
    input.forEach((key, value) -> copy.put(key, new ArrayList<>(value)));
    return copy;
  }

  private static void addScopedItem(Map<String, List<String>> index, String scopeKey, String scopedItemId) {
    var ids = new ArrayList<>(index.getOrDefault(scopeKey, List.of()));
    if (!ids.contains(scopedItemId)) ids.add(scopedItemId);
    index.put(scopeKey, ids);
  }

  private static String safe(String value) {
    return value == null ? "" : value;
  }
}

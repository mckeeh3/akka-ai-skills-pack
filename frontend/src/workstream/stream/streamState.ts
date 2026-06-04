import type { WorkstreamEvent, WorkstreamItem } from '../types';

export const DEFAULT_WORKSTREAM_SURFACE_STREAM_LIMIT = 40;

export type StreamMergeDiagnostics = {
  ignoredEventIds: string[];
  staleSurfaceIds: string[];
  diagnostics: string[];
};

export type StreamMergeResult = {
  items: WorkstreamItem[];
  diagnostics: StreamMergeDiagnostics;
};

export function appendOrUpdateWorkstreamItem(items: WorkstreamItem[], next: WorkstreamItem): WorkstreamItem[] {
  const existingIndex = items.findIndex((item) => item.itemId === next.itemId);
  if (existingIndex === -1) {
    return orderWorkstreamSurfaceStream([...items, next]);
  }
  return orderWorkstreamSurfaceStream(items.map((item, index) => (index === existingIndex ? { ...item, ...next, traceIds: Array.from(new Set([...item.traceIds, ...next.traceIds])) } : item)));
}

export function orderWorkstreamSurfaceStream(items: WorkstreamItem[]): WorkstreamItem[] {
  return [...items].sort((left, right) => {
    const createdAtOrder = left.createdAt.localeCompare(right.createdAt);
    return createdAtOrder === 0 ? left.itemId.localeCompare(right.itemId) : createdAtOrder;
  });
}

export function pruneWorkstreamSurfaceStream(items: WorkstreamItem[], maxSurfaces = DEFAULT_WORKSTREAM_SURFACE_STREAM_LIMIT): WorkstreamItem[] {
  const ordered = orderWorkstreamSurfaceStream(items);
  return ordered.length > maxSurfaces ? ordered.slice(ordered.length - maxSurfaces) : ordered;
}

export function pruneWorkstreamSurfaceStreamsByAgent(items: WorkstreamItem[], maxSurfacesPerAgent = DEFAULT_WORKSTREAM_SURFACE_STREAM_LIMIT): WorkstreamItem[] {
  const grouped = new Map<string, WorkstreamItem[]>();
  for (const item of items) {
    const agentItems = grouped.get(item.functionalAgentId) ?? [];
    agentItems.push(item);
    grouped.set(item.functionalAgentId, agentItems);
  }
  return orderWorkstreamSurfaceStream(Array.from(grouped.values()).flatMap((agentItems) => pruneWorkstreamSurfaceStream(agentItems, maxSurfacesPerAgent)));
}

export function markSurfaceItemsStale(items: WorkstreamItem[], surfaceId: string, reason = 'Realtime stream marked this surface stale.'): WorkstreamItem[] {
  return items.map((item) => (item.surfaceId === surfaceId ? { ...item, status: 'stale', body: item.body ?? reason } : item));
}

export function mergeWorkstreamEvents(items: WorkstreamItem[], events: WorkstreamEvent[], seenEventIds = new Set<string>()): StreamMergeResult {
  const diagnostics: StreamMergeDiagnostics = { ignoredEventIds: [], staleSurfaceIds: [], diagnostics: [] };
  let merged = [...items];

  for (const event of events) {
    if (!event?.eventId || seenEventIds.has(event.eventId)) {
      diagnostics.ignoredEventIds.push(event?.eventId ?? 'malformed');
      diagnostics.diagnostics.push('Ignored duplicate or malformed workstream event.');
      continue;
    }
    seenEventIds.add(event.eventId);

    if (event.eventType === 'surface.stale' && event.surfaceId) {
      diagnostics.staleSurfaceIds.push(event.surfaceId);
      merged = markSurfaceItemsStale(merged, event.surfaceId);
      continue;
    }

    if (event.eventType === 'projection.refresh.available' && event.surfaceId) {
      diagnostics.staleSurfaceIds.push(event.surfaceId);
      diagnostics.diagnostics.push('Backend projection refresh is available; reload the backend-owned surface before treating it as current.');
      merged = markSurfaceItemsStale(merged, event.surfaceId, 'Backend projection refresh is available; reload this surface.');
      continue;
    }

    if (event.eventType === 'workstream.item.appended' || event.eventType === 'workstream.item.updated') {
      const patch = event.patch as Partial<WorkstreamItem> | undefined;
      if (!patch?.itemId) {
        diagnostics.ignoredEventIds.push(event.eventId);
        diagnostics.diagnostics.push('Ignored workstream item event without item id.');
        continue;
      }
      merged = appendOrUpdateWorkstreamItem(merged, {
        functionalAgentId: event.functionalAgentId,
        kind: 'system-status',
        createdAt: event.occurredAt,
        correlationId: event.correlationId,
        traceIds: event.traceIds,
        ...patch
      } as WorkstreamItem);
    }
  }

  return { items: merged, diagnostics };
}

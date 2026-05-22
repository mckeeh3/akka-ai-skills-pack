import type { RealtimeConnectionState, WorkstreamEvent, WorkstreamItem } from '../types';
import { mergeWorkstreamEvents } from '../stream/streamState';

export type WorkstreamRealtimeViewState = {
  connection: RealtimeConnectionState;
  items: WorkstreamItem[];
  seenEventIds: Set<string>;
  diagnostics: string[];
  lastEventId?: string;
};

export function initialWorkstreamRealtimeViewState(items: WorkstreamItem[]): WorkstreamRealtimeViewState {
  return {
    connection: { status: 'connecting' },
    items,
    seenEventIds: new Set<string>(),
    diagnostics: [],
    lastEventId: undefined
  };
}

export function applyWorkstreamRealtimeEvent(state: WorkstreamRealtimeViewState, event: WorkstreamEvent, selectedTenantId: string): WorkstreamRealtimeViewState {
  if (event.tenantId !== selectedTenantId) {
    return { ...state, diagnostics: [...state.diagnostics, `Ignored cross-context event ${event.eventId}.`] };
  }
  if (state.lastEventId && event.sequence !== undefined) {
    const lastEventIdParts = state.lastEventId.split('-');
    const lastSequence = Number(lastEventIdParts[lastEventIdParts.length - 1]);
    if (Number.isFinite(lastSequence) && event.sequence < lastSequence) {
      return { ...state, diagnostics: [...state.diagnostics, `Ignored out-of-order event ${event.eventId}.`] };
    }
  }
  const seenEventIds = new Set(state.seenEventIds);
  const merged = mergeWorkstreamEvents(state.items, [event], seenEventIds);
  return {
    ...state,
    items: merged.items,
    seenEventIds,
    diagnostics: [...state.diagnostics, ...merged.diagnostics.diagnostics],
    lastEventId: event.eventId
  };
}

export function realtimeStatusLabel(state: RealtimeConnectionState): string {
  switch (state.status) {
    case 'connecting':
      return 'Connecting to fixture workstream events';
    case 'connected':
      return state.lastEventId ? `Live after ${state.lastEventId}` : 'Live fixture workstream events';
    case 'reconnecting':
      return 'Reconnecting to fixture workstream events';
    case 'stale':
      return `Stale: ${state.reason}`;
    case 'disconnected':
      return state.reason ? `Disconnected: ${state.reason}` : 'Disconnected from fixture workstream events';
  }
}

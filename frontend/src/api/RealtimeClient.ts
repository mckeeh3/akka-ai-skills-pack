import type { RealtimeEvent, RealtimeTopic } from './types';

export type RealtimeConnectionState = 'idle' | 'connecting' | 'connected' | 'stale' | 'closed' | 'error';

export type RealtimeEventHandler = (event: RealtimeEvent) => void;
export type RealtimeStateHandler = (state: RealtimeConnectionState) => void;

export interface RealtimeSubscription {
  unsubscribe(): void;
}

export interface RealtimeClient {
  connect(topics: RealtimeTopic[]): RealtimeSubscription;
  onEvent(handler: RealtimeEventHandler): RealtimeSubscription;
  onState(handler: RealtimeStateHandler): RealtimeSubscription;
  disconnect(): void;
}

export function mergeRealtimeEvent<T extends { id: string; version?: number }>(items: T[], incoming: T): T[] {
  const index = items.findIndex((item) => item.id === incoming.id);
  if (index < 0) return [incoming, ...items];
  const existing = items[index];
  if (existing.version !== undefined && incoming.version !== undefined && incoming.version < existing.version) return items;
  const next = [...items];
  next[index] = incoming;
  return next;
}

import type { MeResponse, SurfaceEnvelope, WorkstreamItem } from '../types';
import { pruneWorkstreamSurfaceStreamsByAgent } from '../stream/streamState';
import { createWorkstreamVisualSessionKey } from './visualSessionState';

export type PersistedWorkstreamSurfaceStream = {
  items: WorkstreamItem[];
  surfaces: SurfaceEnvelope<unknown>[];
  savedAt: string;
};

export type PersistedWorkstreamSurfaceStreamStore = Record<string, PersistedWorkstreamSurfaceStream>;

export const workstreamSurfaceStreamStorageKey = 'workstream-surface-streams-v2';

export function restoreDevicePersistedSurfaceStreams(input: { me: MeResponse; items: WorkstreamItem[]; surfaces: SurfaceEnvelope<unknown>[] }): { items: WorkstreamItem[]; surfaces: SurfaceEnvelope<unknown>[] } {
  const stored = readPersistedWorkstreamSurfaceStreamStore();
  const streamKeys = streamKeysForMe(input.me);
  const persistedItems = streamKeys.flatMap((key) => stored[key]?.items ?? []);
  const persistedSurfaces = streamKeys.flatMap((key) => stored[key]?.surfaces ?? []);
  return {
    items: pruneWorkstreamSurfaceStreamsByAgent(mergeById(input.items, persistedItems, (item) => item.itemId)),
    surfaces: mergeById(input.surfaces, persistedSurfaces, (surface) => surface.surfaceId)
  };
}

export function persistDeviceSurfaceStreams(input: { me: MeResponse; items: WorkstreamItem[]; surfaces: SurfaceEnvelope<unknown>[]; now?: string }) {
  const stored = readPersistedWorkstreamSurfaceStreamStore();
  const savedAt = input.now ?? new Date().toISOString();
  const nextStore: PersistedWorkstreamSurfaceStreamStore = { ...stored };

  for (const agent of input.me.functionalAgents) {
    const key = createWorkstreamVisualSessionKey({
      accountId: input.me.account.accountId,
      selectedContextId: input.me.selectedAuthContext.selectedContextId,
      functionalAgentId: agent.functionalAgentId,
      workstreamId: agent.functionalAgentId
    });
    const items = pruneWorkstreamSurfaceStreamsByAgent(input.items.filter((item) => item.functionalAgentId === agent.functionalAgentId));
    nextStore[key] = {
      items,
      surfaces: surfacesForItemsAndAgent(input.surfaces, items, agent.functionalAgentId),
      savedAt
    };
  }

  writePersistedWorkstreamSurfaceStreamStore(nextStore);
}

export function clearDeviceSurfaceStreamForSession(sessionKey: string) {
  const stored = readPersistedWorkstreamSurfaceStreamStore();
  const { [sessionKey]: _cleared, ...remaining } = stored;
  writePersistedWorkstreamSurfaceStreamStore(remaining);
}

export function readPersistedWorkstreamSurfaceStreamStore(): PersistedWorkstreamSurfaceStreamStore {
  if (typeof window === 'undefined') return {};
  try {
    const raw = window.localStorage.getItem(workstreamSurfaceStreamStorageKey);
    if (!raw) return {};
    const parsed = JSON.parse(raw) as PersistedWorkstreamSurfaceStreamStore;
    return parsed && typeof parsed === 'object' ? parsed : {};
  } catch {
    return {};
  }
}

function writePersistedWorkstreamSurfaceStreamStore(store: PersistedWorkstreamSurfaceStreamStore) {
  if (typeof window === 'undefined') return;
  try {
    window.localStorage.setItem(workstreamSurfaceStreamStorageKey, JSON.stringify(store));
  } catch {
    // Device persistence is best-effort; backend authorization and runtime state remain authoritative.
  }
}

function streamKeysForMe(me: MeResponse): string[] {
  return me.functionalAgents.map((agent) => createWorkstreamVisualSessionKey({
    accountId: me.account.accountId,
    selectedContextId: me.selectedAuthContext.selectedContextId,
    functionalAgentId: agent.functionalAgentId,
    workstreamId: agent.functionalAgentId
  }));
}

function surfacesForItemsAndAgent(surfaces: SurfaceEnvelope<unknown>[], items: WorkstreamItem[], functionalAgentId: string): SurfaceEnvelope<unknown>[] {
  const surfaceIds = new Set(items.flatMap((item) => item.surfaceId ? [item.surfaceId] : []));
  return surfaces.filter((surface) => surface.ownerFunctionalAgentId === functionalAgentId || surfaceIds.has(surface.surfaceId));
}

function mergeById<T>(left: T[], right: T[], id: (item: T) => string): T[] {
  const merged = new Map<string, T>();
  for (const item of left) merged.set(id(item), item);
  for (const item of right) merged.set(id(item), item);
  return Array.from(merged.values());
}

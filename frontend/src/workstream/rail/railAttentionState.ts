import type { AttentionSeverity, FunctionalAgentRailAttention, FunctionalAgentRailAttentionKind, FunctionalAgentRailAttentionStore } from '../types';

export function strongerRailAttentionSeverity(current: AttentionSeverity | undefined, next: AttentionSeverity): AttentionSeverity {
  const rank: Record<AttentionSeverity, number> = { info: 1, warning: 2, critical: 3 };
  return !current || rank[next] > rank[current] ? next : current;
}

export function shouldRecordUnseenForAgent(functionalAgentId: string, selectedFunctionalAgentId: string | undefined): boolean {
  return functionalAgentId !== selectedFunctionalAgentId;
}

export function recordUnseenRailResponse(
  store: FunctionalAgentRailAttentionStore,
  input: {
    functionalAgentId: string;
    selectedFunctionalAgentId?: string;
    lastItemId?: string;
    severity?: AttentionSeverity;
    kind?: FunctionalAgentRailAttentionKind;
    lastUpdatedAt?: string;
  }
): FunctionalAgentRailAttentionStore {
  if (!shouldRecordUnseenForAgent(input.functionalAgentId, input.selectedFunctionalAgentId)) return store;
  const previous = store[input.functionalAgentId];
  const nextAttention: FunctionalAgentRailAttention = {
    unseenResponseCount: (previous?.unseenResponseCount ?? 0) + 1,
    severity: strongerRailAttentionSeverity(previous?.severity, input.severity ?? 'info'),
    kind: input.kind ?? 'background-response',
    lastItemId: input.lastItemId,
    lastUpdatedAt: input.lastUpdatedAt ?? new Date().toISOString()
  };
  return { ...store, [input.functionalAgentId]: nextAttention };
}

export function clearRailAttentionForAgent(store: FunctionalAgentRailAttentionStore, functionalAgentId: string): FunctionalAgentRailAttentionStore {
  if (!store[functionalAgentId]) return store;
  const { [functionalAgentId]: _cleared, ...remaining } = store;
  return remaining;
}

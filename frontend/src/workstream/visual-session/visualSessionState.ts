import type { WorkstreamItem } from '../types';

export type WorkstreamTurnGroup = {
  turnGroupId: string;
  workstreamId: string;
  functionalAgentId: string;
  requestItem: WorkstreamItem;
  responseItems: WorkstreamItem[];
};

export type WorkstreamVisualSessionLimits = {
  maxTurnGroups: number;
  maxRenderedSurfaces: number;
};

export type WorkstreamVisualSessionSnapshot = {
  accountId?: string;
  selectedContextId: string;
  workstreamId: string;
  functionalAgentId: string;
  activeTurnGroupId?: string;
  anchorSurfaceId?: string;
  selectedSurfaceId?: string;
  loadedTurnGroupIds: string[];
  collapsedSurfaceIds: string[];
  userHasManualScroll: boolean;
  lastViewedAt: string;
};

export type WorkstreamVisualSession = WorkstreamVisualSessionSnapshot & {
  limits: WorkstreamVisualSessionLimits;
  turnGroups: WorkstreamTurnGroup[];
};

export const defaultVisualSessionLimits: WorkstreamVisualSessionLimits = {
  maxTurnGroups: 20,
  maxRenderedSurfaces: 200
};

export function createVisualSession(input: {
  accountId?: string;
  selectedContextId: string;
  workstreamId: string;
  functionalAgentId: string;
  now?: string;
  limits?: Partial<WorkstreamVisualSessionLimits>;
}): WorkstreamVisualSession {
  return {
    accountId: input.accountId,
    selectedContextId: input.selectedContextId,
    workstreamId: input.workstreamId,
    functionalAgentId: input.functionalAgentId,
    activeTurnGroupId: undefined,
    anchorSurfaceId: undefined,
    selectedSurfaceId: undefined,
    loadedTurnGroupIds: [],
    collapsedSurfaceIds: [],
    userHasManualScroll: false,
    lastViewedAt: input.now ?? new Date().toISOString(),
    limits: { ...defaultVisualSessionLimits, ...input.limits },
    turnGroups: []
  };
}

export function appendNewTurnGroup(
  session: WorkstreamVisualSession,
  requestItem: WorkstreamItem,
  options: { turnGroupId?: string; now?: string } = {}
): WorkstreamVisualSession {
  const turnGroupId = options.turnGroupId ?? requestItem.correlationId ?? requestItem.itemId;
  const withoutExisting = session.turnGroups.filter((group) => group.turnGroupId !== turnGroupId);
  const nextSession: WorkstreamVisualSession = {
    ...session,
    functionalAgentId: requestItem.functionalAgentId,
    activeTurnGroupId: turnGroupId,
    anchorSurfaceId: requestItem.surfaceId ?? requestItem.itemId,
    userHasManualScroll: false,
    lastViewedAt: options.now ?? new Date().toISOString(),
    turnGroups: [...withoutExisting, { turnGroupId, workstreamId: session.workstreamId, functionalAgentId: requestItem.functionalAgentId, requestItem, responseItems: [] }]
  };
  return enforceVisualSessionLimits(refreshLoadedTurnGroupIds(nextSession));
}

export function appendResponseToTurnGroup(
  session: WorkstreamVisualSession,
  turnGroupId: string,
  responseItem: WorkstreamItem,
  options: { now?: string } = {}
): WorkstreamVisualSession {
  const turnGroups = session.turnGroups.map((group) => {
    if (group.turnGroupId !== turnGroupId) return group;
    const existingIndex = group.responseItems.findIndex((item) => item.itemId === responseItem.itemId);
    const responseItems = existingIndex === -1
      ? [...group.responseItems, responseItem]
      : group.responseItems.map((item, index) => (index === existingIndex ? { ...item, ...responseItem } : item));
    return { ...group, responseItems: sortWorkstreamItemsByCreatedAt(responseItems) };
  });

  return enforceVisualSessionLimits(refreshLoadedTurnGroupIds({ ...session, turnGroups, lastViewedAt: options.now ?? new Date().toISOString() }));
}

export function buildTurnGroupsFromItems(input: {
  workstreamId: string;
  items: WorkstreamItem[];
}): WorkstreamTurnGroup[] {
  const groups: WorkstreamTurnGroup[] = [];
  const groupByCorrelationId = new Map<string, WorkstreamTurnGroup>();

  for (const item of sortWorkstreamItemsByCreatedAt(input.items)) {
    if (item.kind === 'user-request' || item.kind === 'user-message' || item.kind === 'surface-request') {
      const turnGroupId = item.correlationId || item.itemId;
      const group: WorkstreamTurnGroup = {
        turnGroupId,
        workstreamId: input.workstreamId,
        functionalAgentId: item.functionalAgentId,
        requestItem: item,
        responseItems: []
      };
      groups.push(group);
      groupByCorrelationId.set(turnGroupId, group);
      continue;
    }

    const turnGroupId = item.correlationId;
    const existing = groupByCorrelationId.get(turnGroupId);
    if (existing) {
      existing.responseItems = sortWorkstreamItemsByCreatedAt([...existing.responseItems, item]);
      continue;
    }

    const syntheticRequest: WorkstreamItem = {
      itemId: `synthetic-request-${item.itemId}`,
      functionalAgentId: item.functionalAgentId,
      kind: 'system-status',
      createdAt: item.createdAt,
      correlationId: turnGroupId || item.itemId,
      traceIds: item.traceIds,
      title: 'Loaded prior workstream turn',
      status: item.status
    };
    const group: WorkstreamTurnGroup = {
      turnGroupId: syntheticRequest.correlationId,
      workstreamId: input.workstreamId,
      functionalAgentId: item.functionalAgentId,
      requestItem: syntheticRequest,
      responseItems: [item]
    };
    groups.push(group);
    groupByCorrelationId.set(group.turnGroupId, group);
  }

  return groups;
}

export function enforceVisualSessionLimits(session: WorkstreamVisualSession): WorkstreamVisualSession {
  const maxTurnGroups = Math.max(1, session.limits.maxTurnGroups);
  const maxRenderedSurfaces = Math.max(1, session.limits.maxRenderedSurfaces);
  let turnGroups = session.turnGroups.slice(-maxTurnGroups);

  while (turnGroups.length > 1 && countRenderedSurfaces(turnGroups) > maxRenderedSurfaces) {
    turnGroups = turnGroups.slice(1);
  }

  const activeStillLoaded = session.activeTurnGroupId ? turnGroups.some((group) => group.turnGroupId === session.activeTurnGroupId) : false;
  const nextSession: WorkstreamVisualSession = {
    ...session,
    activeTurnGroupId: activeStillLoaded ? session.activeTurnGroupId : turnGroups[turnGroups.length - 1]?.turnGroupId,
    turnGroups
  };
  return refreshLoadedTurnGroupIds(nextSession);
}

export function toVisualSessionSnapshot(session: WorkstreamVisualSession): WorkstreamVisualSessionSnapshot {
  return {
    accountId: session.accountId,
    selectedContextId: session.selectedContextId,
    workstreamId: session.workstreamId,
    functionalAgentId: session.functionalAgentId,
    activeTurnGroupId: session.activeTurnGroupId,
    anchorSurfaceId: session.anchorSurfaceId,
    selectedSurfaceId: session.selectedSurfaceId,
    loadedTurnGroupIds: session.loadedTurnGroupIds,
    collapsedSurfaceIds: session.collapsedSurfaceIds,
    userHasManualScroll: session.userHasManualScroll,
    lastViewedAt: session.lastViewedAt
  };
}

export function recordManualScroll(session: WorkstreamVisualSession, options: { now?: string } = {}): WorkstreamVisualSession {
  return { ...session, userHasManualScroll: true, lastViewedAt: options.now ?? new Date().toISOString() };
}

export function countRenderedSurfaces(turnGroups: WorkstreamTurnGroup[]): number {
  return turnGroups.reduce((total, group) => total + 1 + group.responseItems.length, 0);
}

function refreshLoadedTurnGroupIds(session: WorkstreamVisualSession): WorkstreamVisualSession {
  return { ...session, loadedTurnGroupIds: session.turnGroups.map((group) => group.turnGroupId) };
}

function sortWorkstreamItemsByCreatedAt(items: WorkstreamItem[]): WorkstreamItem[] {
  return [...items].sort((left, right) => left.createdAt.localeCompare(right.createdAt) || left.itemId.localeCompare(right.itemId));
}

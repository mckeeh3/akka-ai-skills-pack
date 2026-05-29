import type { FunctionalAgentSummary, MeResponse, RegionState, WorkstreamItem, WorkstreamSelection, WorkstreamShellRequest, WorkstreamShellRequestOrigin } from '../types';
import { defaultSelectableAgentId } from '../rail/railState';

export type WorkstreamShellState = RegionState<{
  me: MeResponse;
  selection: WorkstreamSelection;
}>;

export function initialWorkstreamSelection(me: MeResponse, requestedFunctionalAgentId?: string): WorkstreamSelection {
  const selectedFunctionalAgentId =
    requestedFunctionalAgentId && me.functionalAgents.some((agent) => agent.functionalAgentId === requestedFunctionalAgentId)
      ? requestedFunctionalAgentId
      : defaultSelectableAgentId(me.functionalAgents, me.visibleCapabilityIds, me.account.status) ?? me.functionalAgents[0]?.functionalAgentId ?? 'none';

  return { selectedFunctionalAgentId };
}

export function selectedFunctionalAgent(agents: FunctionalAgentSummary[], selectedFunctionalAgentId: string): FunctionalAgentSummary | undefined {
  return agents.find((agent) => agent.functionalAgentId === selectedFunctionalAgentId);
}

export function readyShellState(me: MeResponse, requestedFunctionalAgentId?: string): WorkstreamShellState {
  if (me.account.status === 'disabled') return { status: 'forbidden', message: 'The signed-in account is disabled.', recovery: 'Contact a tenant administrator.' };
  if (me.memberships.length === 0) return { status: 'forbidden', message: 'No active tenant membership is available.', recovery: 'Request an invitation before opening protected workstreams.' };
  return { status: 'ready', value: { me, selection: initialWorkstreamSelection(me, requestedFunctionalAgentId) } };
}

export function normalizePromptToShellRequest(prompt: string, selectedFunctionalAgentId: string, correlationId = defaultShellRequestCorrelationId()): WorkstreamShellRequest | undefined {
  const trimmed = prompt.trim();
  const workstreamMatch = /^show\s+workstream\s+(.+)$/i.exec(trimmed);
  if (workstreamMatch) {
    const normalizedWorkstream = slugifyShellRequestToken(workstreamMatch[1]);
    return {
      requestType: 'open_workstream',
      origin: 'user_prompt',
      displayText: trimmed,
      canonicalPrompt: `show workstream ${normalizedWorkstream}`,
      sourceFunctionalAgentId: selectedFunctionalAgentId,
      targetFunctionalAgentId: normalizedWorkstream,
      scope: 'authorized_cross_workstream',
      correlationId
    };
  }

  const surfaceMatch = /^show\s+(?:surface\s+)?(.+)$/i.exec(trimmed);
  if (surfaceMatch) {
    const targetSurfaceId = slugifyShellRequestToken(surfaceMatch[1]);
    if (!targetSurfaceId) return undefined;
    return {
      requestType: 'show_surface',
      origin: 'user_prompt',
      displayText: trimmed,
      canonicalPrompt: `show surface ${targetSurfaceId}`,
      sourceFunctionalAgentId: selectedFunctionalAgentId,
      targetFunctionalAgentId: selectedFunctionalAgentId,
      targetSurfaceId,
      scope: 'current_workstream',
      correlationId
    };
  }

  return undefined;
}

export function buildShellRequestItem(request: WorkstreamShellRequest): WorkstreamItem {
  const targetFunctionalAgentId = request.targetFunctionalAgentId ?? request.sourceFunctionalAgentId ?? 'unknown';
  return {
    itemId: `shell-request-${request.correlationId}`,
    functionalAgentId: targetFunctionalAgentId,
    kind: 'surface-request',
    createdAt: new Date().toISOString(),
    correlationId: request.correlationId,
    traceIds: [],
    surfaceId: request.targetSurfaceId,
    title: request.canonicalPrompt,
    body: request.displayText === request.canonicalPrompt ? undefined : request.displayText,
    requestOrigin: request.origin,
    canonicalPrompt: request.canonicalPrompt
  };
}

export function shellRequestFromSurfaceAction(options: {
  requestType: WorkstreamShellRequest['requestType'];
  selectedFunctionalAgentId: string;
  sourceSurfaceId: string;
  sourceActionId: string;
  targetFunctionalAgentId?: string;
  targetSurfaceId?: string;
  targetItemId?: string;
  displayText?: string;
  origin?: WorkstreamShellRequestOrigin;
  correlationId?: string;
}): WorkstreamShellRequest {
  const targetFunctionalAgentId = options.targetFunctionalAgentId ?? options.selectedFunctionalAgentId;
  const canonicalPrompt = options.requestType === 'open_workstream'
    ? `show workstream ${targetFunctionalAgentId}`
    : `show surface ${options.targetSurfaceId ?? options.sourceSurfaceId}`;
  return {
    requestType: options.requestType,
    origin: options.origin ?? 'surface_action',
    displayText: options.displayText ?? canonicalPrompt,
    canonicalPrompt,
    sourceFunctionalAgentId: options.selectedFunctionalAgentId,
    sourceSurfaceId: options.sourceSurfaceId,
    sourceActionId: options.sourceActionId,
    targetFunctionalAgentId,
    targetSurfaceId: options.targetSurfaceId,
    targetItemId: options.targetItemId,
    scope: targetFunctionalAgentId === options.selectedFunctionalAgentId ? 'current_workstream' : 'authorized_cross_workstream',
    correlationId: options.correlationId ?? defaultShellRequestCorrelationId()
  };
}

export function selectionFromShellRequest(current: WorkstreamSelection, request: WorkstreamShellRequest): WorkstreamSelection {
  return {
    ...current,
    selectedFunctionalAgentId: request.targetFunctionalAgentId ?? current.selectedFunctionalAgentId,
    selectedItemId: request.targetItemId,
    selectedSurfaceId: request.targetSurfaceId,
    surfacePlacement: request.targetSurfaceId ? 'inline' : current.surfacePlacement
  };
}

function defaultShellRequestCorrelationId(): string {
  return `shell:${Date.now()}`;
}

function slugifyShellRequestToken(value: string): string {
  return value.trim().toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-+|-+$/g, '');
}

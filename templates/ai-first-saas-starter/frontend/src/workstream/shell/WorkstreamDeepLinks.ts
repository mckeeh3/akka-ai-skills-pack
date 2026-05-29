import type { WorkstreamSelection, WorkstreamShellRequest } from '../types';

export function parseWorkstreamDeepLink(search: string): Partial<WorkstreamSelection> {
  const params = new URLSearchParams(search);
  return {
    selectedFunctionalAgentId: params.get('agent') ?? undefined,
    selectedItemId: params.get('itemId') ?? undefined,
    selectedSurfaceId: params.get('surfaceId') ?? undefined,
    surfacePlacement: (params.get('placement') as WorkstreamSelection['surfacePlacement'] | null) ?? undefined
  };
}

export function parseDeepLinkShellRequest(search: string, correlationId = `deep-link:${Date.now()}`): WorkstreamShellRequest | undefined {
  const selection = parseWorkstreamDeepLink(search);
  if (!selection.selectedFunctionalAgentId && !selection.selectedSurfaceId) return undefined;
  const requestType = selection.selectedSurfaceId ? 'show_surface' : 'open_workstream';
  const targetFunctionalAgentId = selection.selectedFunctionalAgentId;
  const targetSurfaceId = selection.selectedSurfaceId;
  return {
    requestType,
    origin: 'deep_link',
    displayText: targetSurfaceId ? `Open linked surface ${targetSurfaceId}` : `Open linked workstream ${targetFunctionalAgentId}`,
    canonicalPrompt: targetSurfaceId ? `show surface ${targetSurfaceId}` : `show workstream ${targetFunctionalAgentId}`,
    targetFunctionalAgentId,
    targetSurfaceId,
    targetItemId: selection.selectedItemId,
    scope: 'authorized_cross_workstream',
    correlationId
  };
}

export function serializeWorkstreamDeepLink(selection: Partial<WorkstreamSelection>): string {
  const params = new URLSearchParams();
  if (selection.selectedFunctionalAgentId) params.set('agent', selection.selectedFunctionalAgentId);
  if (selection.selectedItemId) params.set('itemId', selection.selectedItemId);
  if (selection.selectedSurfaceId) params.set('surfaceId', selection.selectedSurfaceId);
  if (selection.surfacePlacement) params.set('placement', selection.surfacePlacement);
  const query = params.toString();
  return query ? `/ui?${query}` : '/ui';
}

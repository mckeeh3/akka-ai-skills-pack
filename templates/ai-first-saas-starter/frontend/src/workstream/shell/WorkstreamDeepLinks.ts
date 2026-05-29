import type { WorkstreamSelection } from '../types';

export function parseWorkstreamDeepLink(search: string): Partial<WorkstreamSelection> {
  const params = new URLSearchParams(search);
  return {
    selectedFunctionalAgentId: params.get('agent') ?? undefined,
    selectedItemId: params.get('itemId') ?? undefined,
    selectedSurfaceId: params.get('surfaceId') ?? undefined,
    surfacePlacement: (params.get('placement') as WorkstreamSelection['surfacePlacement'] | null) ?? undefined
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

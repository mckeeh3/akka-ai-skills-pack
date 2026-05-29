import type { FunctionalAgentSummary, MeResponse, RegionState, WorkstreamSelection } from '../types';
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

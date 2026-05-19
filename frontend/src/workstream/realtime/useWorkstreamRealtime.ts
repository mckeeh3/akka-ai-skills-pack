import { useEffect, useState } from 'react';
import type { WorkstreamRealtimeClient } from '../../api/WorkstreamRealtimeClient';
import type { WorkstreamItem } from '../types';
import { applyWorkstreamRealtimeEvent, initialWorkstreamRealtimeViewState, type WorkstreamRealtimeViewState } from './workstreamEvents';

export function useWorkstreamRealtime(
  client: WorkstreamRealtimeClient,
  options: { selectedContextId: string; tenantId: string; functionalAgentId?: string; initialItems: WorkstreamItem[] }
): WorkstreamRealtimeViewState {
  const [state, setState] = useState(() => initialWorkstreamRealtimeViewState(options.initialItems));

  useEffect(() => {
    setState((current) => ({ ...initialWorkstreamRealtimeViewState(options.initialItems), seenEventIds: current.seenEventIds }));
  }, [options.initialItems]);

  useEffect(() => {
    const stateSubscription = client.onState((connection) => setState((current) => ({ ...current, connection })));
    const eventSubscription = client.onEvent((event) => setState((current) => applyWorkstreamRealtimeEvent(current, event, options.tenantId)));
    const connectionSubscription = client.connect({
      selectedContextId: options.selectedContextId,
      functionalAgentId: options.functionalAgentId,
      lastEventId: state.lastEventId
    });
    return () => {
      connectionSubscription.unsubscribe();
      eventSubscription.unsubscribe();
      stateSubscription.unsubscribe();
    };
  }, [client, options.selectedContextId, options.tenantId, options.functionalAgentId]);

  return state;
}

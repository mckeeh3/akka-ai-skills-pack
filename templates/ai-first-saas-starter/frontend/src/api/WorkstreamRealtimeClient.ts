import type { RealtimeConnectionState, WorkstreamEvent } from '../workstream/types';

export type WorkstreamRealtimeEventHandler = (event: WorkstreamEvent) => void;
export type WorkstreamRealtimeStateHandler = (state: RealtimeConnectionState) => void;

export interface WorkstreamRealtimeSubscription {
  unsubscribe(): void;
}

export interface WorkstreamRealtimeClient {
  connect(options: { selectedContextId: string; functionalAgentId?: string; lastEventId?: string }): WorkstreamRealtimeSubscription;
  onEvent(handler: WorkstreamRealtimeEventHandler): WorkstreamRealtimeSubscription;
  onState(handler: WorkstreamRealtimeStateHandler): WorkstreamRealtimeSubscription;
  disconnect(): void;
}

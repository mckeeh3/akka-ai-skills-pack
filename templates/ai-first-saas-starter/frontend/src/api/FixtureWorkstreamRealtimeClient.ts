import type { WorkstreamRealtimeClient, WorkstreamRealtimeEventHandler, WorkstreamRealtimeStateHandler, WorkstreamRealtimeSubscription } from './WorkstreamRealtimeClient';
import type { RealtimeConnectionState, WorkstreamEvent } from '../workstream/types';
import { duplicateReplayEvent, outOfOrderEvent, workstreamEvents } from '../workstream/fixtures';

export class FixtureWorkstreamRealtimeClient implements WorkstreamRealtimeClient {
  private eventHandlers = new Set<WorkstreamRealtimeEventHandler>();
  private stateHandlers = new Set<WorkstreamRealtimeStateHandler>();
  private timers: number[] = [];
  private state: RealtimeConnectionState = { status: 'disconnected', reason: 'Fixture stream has not connected yet.' };
  private functionalAgentId?: string;

  connect(options: { selectedContextId: string; functionalAgentId?: string; lastEventId?: string }): WorkstreamRealtimeSubscription {
    this.disconnect();
    this.functionalAgentId = options.functionalAgentId;
    this.setState({ status: options.lastEventId ? 'reconnecting' : 'connecting', lastEventId: options.lastEventId });
    this.timers.push(window.setTimeout(() => this.setState({ status: 'connected', lastEventId: options.lastEventId }), 30));
    this.timers.push(window.setTimeout(() => this.emit(workstreamEvents[0]), 80));
    this.timers.push(window.setTimeout(() => this.emit(duplicateReplayEvent), 120));
    this.timers.push(window.setTimeout(() => this.emit(outOfOrderEvent), 160));
    this.timers.push(window.setTimeout(() => this.setState({ status: 'stale', lastEventId: workstreamEvents[0].eventId, reason: 'Fixture stream simulates reconnect and stale surface handling.' }), 210));
    this.timers.push(window.setTimeout(() => this.emit(workstreamEvents[7]), 240));
    this.timers.push(window.setTimeout(() => this.setState({ status: 'connected', lastEventId: workstreamEvents[7].eventId }), 320));
    this.timers.push(window.setTimeout(() => this.emit(workstreamEvents[8]), 360));
    return { unsubscribe: () => this.disconnect() };
  }

  onEvent(handler: WorkstreamRealtimeEventHandler): WorkstreamRealtimeSubscription {
    this.eventHandlers.add(handler);
    return { unsubscribe: () => this.eventHandlers.delete(handler) };
  }

  onState(handler: WorkstreamRealtimeStateHandler): WorkstreamRealtimeSubscription {
    this.stateHandlers.add(handler);
    handler(this.state);
    return { unsubscribe: () => this.stateHandlers.delete(handler) };
  }

  disconnect(): void {
    this.timers.forEach((timer) => window.clearTimeout(timer));
    this.timers = [];
    this.setState({ status: 'disconnected', reason: 'Fixture stream disconnected.' });
  }

  private setState(state: RealtimeConnectionState) {
    this.state = state;
    this.stateHandlers.forEach((handler) => handler(state));
  }

  private emit(event: WorkstreamEvent) {
    if (this.functionalAgentId && event.functionalAgentId !== this.functionalAgentId) return;
    this.eventHandlers.forEach((handler) => handler(event));
  }
}

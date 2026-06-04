import type { WorkstreamRealtimeClient, WorkstreamRealtimeEventHandler, WorkstreamRealtimeStateHandler, WorkstreamRealtimeSubscription } from './WorkstreamRealtimeClient';
import type { RealtimeConnectionState, WorkstreamEvent } from '../workstream/types';

export class HttpWorkstreamRealtimeClient implements WorkstreamRealtimeClient {
  private eventHandlers = new Set<WorkstreamRealtimeEventHandler>();
  private stateHandlers = new Set<WorkstreamRealtimeStateHandler>();
  private eventSource?: EventSource;
  private state: RealtimeConnectionState = { status: 'disconnected', reason: 'Realtime stream has not connected yet.' };

  connect(options: { selectedContextId: string; functionalAgentId?: string; lastEventId?: string }): WorkstreamRealtimeSubscription {
    this.disconnect();
    this.setState({ status: options.lastEventId ? 'reconnecting' : 'connecting', lastEventId: options.lastEventId });
    const params = new URLSearchParams({ selectedContextId: options.selectedContextId });
    if (options.functionalAgentId) params.set('functionalAgentId', options.functionalAgentId);
    if (options.lastEventId) params.set('lastEventId', options.lastEventId);
    const source = new EventSource(`/api/workstream/events?${params.toString()}`);
    this.eventSource = source;

    source.onopen = () => this.setState({ status: 'connected', lastEventId: options.lastEventId });
    source.onmessage = (message) => this.handleMessage(message);
    source.addEventListener('surface.stale', (message) => this.handleMessage(message as MessageEvent<string>));
    source.addEventListener('surface.reconnected', (message) => this.handleMessage(message as MessageEvent<string>));
    source.addEventListener('projection.refresh.available', (message) => this.handleMessage(message as MessageEvent<string>));
    source.addEventListener('workstream.item.appended', (message) => this.handleMessage(message as MessageEvent<string>));
    source.addEventListener('workstream.item.updated', (message) => this.handleMessage(message as MessageEvent<string>));
    source.onerror = () => this.setState({ status: 'stale', lastEventId: this.lastEventId(), reason: 'Bounded workstream event replay ended, disconnected, or could not authenticate; refresh backend-owned surfaces before treating data as current.' });
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
    this.eventSource?.close();
    this.eventSource = undefined;
    this.setState({ status: 'disconnected', reason: 'Realtime stream disconnected.' });
  }

  private handleMessage(message: MessageEvent<string>) {
    try {
      const event = JSON.parse(message.data) as WorkstreamEvent;
      if (!event?.eventId || !event.eventType || !event.tenantId || !event.functionalAgentId) {
        this.setState({ status: 'stale', lastEventId: this.lastEventId(), reason: 'Malformed realtime event was ignored; refresh may be required.' });
        return;
      }
      if (event.eventType === 'surface.stale') {
        this.setState({ status: 'stale', lastEventId: event.eventId, reason: 'Backend marked one or more workstream surfaces stale.' });
      } else if (event.eventType === 'surface.reconnected') {
        this.setState({ status: 'connected', lastEventId: event.eventId });
      } else {
        this.setState({ status: 'connected', lastEventId: event.eventId });
      }
      this.eventHandlers.forEach((handler) => handler(event));
    } catch {
      this.setState({ status: 'stale', lastEventId: this.lastEventId(), reason: 'Malformed realtime payload was ignored safely.' });
    }
  }

  private setState(state: RealtimeConnectionState) {
    this.state = state;
    this.stateHandlers.forEach((handler) => handler(state));
  }

  private lastEventId() {
    return 'lastEventId' in this.state ? this.state.lastEventId : undefined;
  }
}

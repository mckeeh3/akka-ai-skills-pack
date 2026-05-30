import type { RealtimeClient, RealtimeConnectionState, RealtimeEventHandler, RealtimeStateHandler, RealtimeSubscription } from '../../../api/RealtimeClient';
import type { RealtimeEvent, RealtimeTopic } from '../../../api/types';

export class FixtureRealtimeClient implements RealtimeClient {
  private eventHandlers = new Set<RealtimeEventHandler>();
  private stateHandlers = new Set<RealtimeStateHandler>();
  private timers: number[] = [];
  private connectedTopics: RealtimeTopic[] = [];
  private state: RealtimeConnectionState = 'idle';

  connect(topics: RealtimeTopic[]): RealtimeSubscription {
    this.disconnect();
    this.connectedTopics = topics;
    this.setState('connecting');
    this.timers.push(window.setTimeout(() => this.setState('connected'), 50));
    this.timers.push(window.setTimeout(() => this.emit(seedEvent('decision.created', 'decisions', 'decision-3', 1)), 120));
    this.timers.push(window.setTimeout(() => this.emit(seedEvent('decision.created', 'decisions', 'decision-3', 1)), 180));
    this.timers.push(window.setTimeout(() => this.setState('stale'), 260));
    this.timers.push(window.setTimeout(() => this.setState('connected'), 360));
    this.timers.push(window.setTimeout(() => this.emit(seedEvent('goal.updated', 'goals', 'goal-1', 2)), 440));
    return { unsubscribe: () => this.disconnect() };
  }

  onEvent(handler: RealtimeEventHandler): RealtimeSubscription {
    this.eventHandlers.add(handler);
    return { unsubscribe: () => this.eventHandlers.delete(handler) };
  }

  onState(handler: RealtimeStateHandler): RealtimeSubscription {
    this.stateHandlers.add(handler);
    handler(this.state);
    return { unsubscribe: () => this.stateHandlers.delete(handler) };
  }

  disconnect(): void {
    this.timers.forEach((timer) => window.clearTimeout(timer));
    this.timers = [];
    this.connectedTopics = [];
    if (this.state !== 'idle') this.setState('closed');
  }

  private setState(state: RealtimeConnectionState) {
    this.state = state;
    this.stateHandlers.forEach((handler) => handler(state));
  }

  private emit(event: RealtimeEvent) {
    if (!this.connectedTopics.includes(event.topic)) return;
    this.eventHandlers.forEach((handler) => handler(event));
  }
}

function seedEvent(type: string, topic: RealtimeTopic, subjectId: string, version: number): RealtimeEvent {
  return {
    eventId: `${type}-${subjectId}-${version}`,
    tenantId: 'seed-tenant',
    topic,
    type,
    subjectId,
    version,
    occurredAt: new Date().toISOString(),
    payload: { fixture: true, subjectId, version }
  };
}

import type { TokenProvider } from './HttpApiClient';
import type { WorkstreamRealtimeClient, WorkstreamRealtimeEventHandler, WorkstreamRealtimeStateHandler, WorkstreamRealtimeSubscription } from './WorkstreamRealtimeClient';
import type { RealtimeConnectionState, WorkstreamEvent } from '../workstream/types';

const WORKSTREAM_SSE_EVENT_TYPES = ['surface.stale', 'surface.reconnected', 'projection.refresh.available'] as const;

export class HttpWorkstreamRealtimeClient implements WorkstreamRealtimeClient {
  private eventHandlers = new Set<WorkstreamRealtimeEventHandler>();
  private stateHandlers = new Set<WorkstreamRealtimeStateHandler>();
  private abortController?: AbortController;
  private state: RealtimeConnectionState = { status: 'disconnected', reason: 'Realtime stream has not connected yet.' };

  constructor(private readonly tokenProvider?: TokenProvider) {}

  connect(options: { selectedContextId: string; functionalAgentId?: string; lastEventId?: string }): WorkstreamRealtimeSubscription {
    this.disconnect();
    this.setState({ status: options.lastEventId ? 'reconnecting' : 'connecting', lastEventId: options.lastEventId });
    const abortController = new AbortController();
    this.abortController = abortController;
    void this.openAuthenticatedStream(options, abortController);
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
    this.abortController?.abort();
    this.abortController = undefined;
    this.setState({ status: 'disconnected', reason: 'Realtime stream disconnected.' });
  }

  private async openAuthenticatedStream(
    options: { selectedContextId: string; functionalAgentId?: string; lastEventId?: string },
    abortController: AbortController
  ) {
    try {
      const token = await this.tokenProvider?.();
      if (!token) {
        this.setState({ status: 'stale', lastEventId: options.lastEventId, reason: 'Realtime stream could not authenticate because no bearer token was available.' });
        return;
      }

      const params = new URLSearchParams({ selectedContextId: options.selectedContextId });
      if (options.functionalAgentId) params.set('functionalAgentId', options.functionalAgentId);
      if (options.lastEventId) params.set('lastEventId', options.lastEventId);

      const headers = new Headers();
      headers.set('Accept', 'text/event-stream');
      headers.set('Authorization', `Bearer ${token}`);
      headers.set('X-Selected-Context-Id', options.selectedContextId);
      headers.set('X-Correlation-Id', `corr-browser-sse-${Date.now().toString(36)}`);

      const response = await fetch(`/api/workstream/events?${params.toString()}`, {
        method: 'GET',
        headers,
        signal: abortController.signal
      });

      if (!response.ok || !response.body) {
        this.setState({ status: 'stale', lastEventId: this.lastEventId() ?? options.lastEventId, reason: `Realtime stream could not authenticate or connect: HTTP ${response.status}.` });
        return;
      }
      const contentType = response.headers.get('content-type') ?? '';
      if (!contentType.toLowerCase().includes('text/event-stream')) {
        const preview = await response.text().catch(() => 'unreadable response body');
        this.setState({
          status: 'stale',
          lastEventId: this.lastEventId() ?? options.lastEventId,
          reason: `Realtime endpoint returned ${contentType || 'an unknown content type'} instead of text/event-stream: ${preview.slice(0, 80)}`
        });
        return;
      }

      this.setState({ status: 'connected', lastEventId: options.lastEventId });
      await this.readServerSentEvents(response.body, abortController);
      if (!abortController.signal.aborted) {
        this.setState({ status: 'stale', lastEventId: this.lastEventId(), reason: 'Bounded workstream event replay ended; refresh backend-owned surfaces before treating data as current.' });
      }
    } catch (error) {
      if (abortController.signal.aborted) return;
      this.setState({
        status: 'stale',
        lastEventId: this.lastEventId() ?? options.lastEventId,
        reason: error instanceof Error ? `Realtime stream failed: ${error.message}` : 'Realtime stream failed.'
      });
    }
  }

  private async readServerSentEvents(body: ReadableStream<Uint8Array>, abortController: AbortController) {
    const reader = body.getReader();
    const decoder = new TextDecoder();
    let buffer = '';
    try {
      while (!abortController.signal.aborted) {
        const { done, value } = await reader.read();
        if (done) break;
        buffer += decoder.decode(value, { stream: true });
        const normalized = buffer.replace(/\r\n/g, '\n');
        const frames = normalized.split('\n\n');
        buffer = frames.pop() ?? '';
        frames.forEach((frame) => this.handleSseFrame(frame));
      }
      buffer += decoder.decode();
      if (buffer.trim()) this.handleSseFrame(buffer);
    } finally {
      reader.releaseLock();
    }
  }

  private handleSseFrame(frame: string) {
    const dataLines: string[] = [];
    let eventId: string | undefined;
    for (const line of frame.split('\n')) {
      if (!line || line.startsWith(':')) continue;
      const separatorIndex = line.indexOf(':');
      const field = separatorIndex >= 0 ? line.slice(0, separatorIndex) : line;
      const rawValue = separatorIndex >= 0 ? line.slice(separatorIndex + 1) : '';
      const value = rawValue.startsWith(' ') ? rawValue.slice(1) : rawValue;
      if (field === 'data') dataLines.push(value);
      if (field === 'id') eventId = value;
    }
    if (dataLines.length === 0) return;
    this.handleMessage({ data: dataLines.join('\n'), lastEventId: eventId ?? '' } as MessageEvent<string>);
  }

  private handleMessage(message: MessageEvent<string>) {
    const resumeEventId = message.lastEventId || this.lastEventId();
    if (!message.data.trim()) return;
    try {
      const event = this.parseWorkstreamEvent(message.data);
      if (!event?.eventId || !event.eventType || !event.tenantId || !event.functionalAgentId) {
        this.setState({ status: 'stale', lastEventId: resumeEventId, reason: 'Malformed realtime event was ignored; refresh may be required.' });
        return;
      }
      if (event.eventType === WORKSTREAM_SSE_EVENT_TYPES[0]) {
        this.setState({ status: 'stale', lastEventId: resumeEventId, reason: 'Backend marked one or more workstream surfaces stale.' });
      } else {
        this.setState({ status: 'connected', lastEventId: resumeEventId });
      }
      this.eventHandlers.forEach((handler) => handler(event));
    } catch {
      this.setState({ status: 'stale', lastEventId: resumeEventId, reason: 'Malformed realtime payload was ignored safely.' });
    }
  }

  private parseWorkstreamEvent(data: string): WorkstreamEvent {
    const parsed = JSON.parse(data.trim()) as WorkstreamEvent | { value?: WorkstreamEvent; row?: WorkstreamEvent; data?: WorkstreamEvent };
    if (parsed && typeof parsed === 'object') {
      if ('eventId' in parsed) return parsed as WorkstreamEvent;
      if ('value' in parsed && parsed.value) return parsed.value;
      if ('row' in parsed && parsed.row) return parsed.row;
      if ('data' in parsed && parsed.data && typeof parsed.data === 'object') return parsed.data;
    }
    return parsed as WorkstreamEvent;
  }

  private setState(state: RealtimeConnectionState) {
    this.state = state;
    this.stateHandlers.forEach((handler) => handler(state));
  }

  private lastEventId() {
    return 'lastEventId' in this.state ? this.state.lastEventId : undefined;
  }
}

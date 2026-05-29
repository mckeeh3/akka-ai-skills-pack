import type { AuditTimelineSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type AuditTimelineSurfaceProps = {
  envelope: SurfaceEnvelope<AuditTimelineSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

type TimelineEvent = {
  eventId: string;
  occurredAt: string;
  actor: string;
  action: string;
  traceId: string;
  sourceType?: string;
  correlationId?: string;
  status?: string;
};

export function AuditTimelineSurface({ envelope, onAction }: AuditTimelineSurfaceProps) {
  const timelineEvents: TimelineEvent[] = envelope.data.events ?? envelope.data.nodes?.map((node) => ({
    eventId: node.nodeId,
    occurredAt: envelope.generatedAt,
    actor: node.sourceType,
    action: `${node.status}: ${node.summary}`,
    traceId: node.traceId ?? node.nodeId,
    sourceType: node.sourceType,
    correlationId: node.correlationId,
    status: node.status
  })) ?? [];

  return (
    <SurfaceStateFrame envelope={envelope}>
      {envelope.data.correlationId && <p className="capability-basis">Correlation timeline: {envelope.data.correlationId}</p>}
      {envelope.data.partial && <p className="surface-state-inline partial" role="status">Partial timeline: unauthorized evidence is omitted or redacted.</p>}
      {envelope.data.redactionSummary && <p className="redaction-note">{envelope.data.redactionSummary}</p>}
      {envelope.data.omittedCategories && envelope.data.omittedCategories.length > 0 && <p>Omitted categories: {envelope.data.omittedCategories.join(', ')}</p>}
      {timelineEvents.length === 0 ? <p>No authorized timeline events are available for this selected AuthContext.</p> : (
        <ol className="audit-timeline" aria-label="Audit correlation timeline">
          {timelineEvents.map((event) => (
            <li key={event.eventId} id={event.traceId}>
              <time dateTime={event.occurredAt}>{event.occurredAt}</time>
              <p><strong>{event.actor}</strong> {event.action}</p>
              {event.sourceType && <small>Source type: {event.sourceType} · Status: {event.status}</small>}
              {event.correlationId && <small>Correlation: {event.correlationId}</small>}
              <a href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(event.traceId)}`}>Open trace</a>
            </li>
          ))}
        </ol>
      )}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

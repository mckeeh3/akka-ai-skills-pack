import type { AuditTimelineSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type AuditTimelineSurfaceProps = {
  envelope: SurfaceEnvelope<AuditTimelineSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
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
  availableEventActionIds?: string[];
  recoveryText?: string;
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
    status: node.status,
    availableEventActionIds: ['action-audit-trace-detail']
  })) ?? [];
  const actionById = new Map(envelope.actions.map((action) => [action.actionId, action]));

  return (
    <SurfaceStateFrame envelope={envelope}>
      {envelope.data.correlationId && <p className="capability-basis">Correlation timeline: {envelope.data.correlationId}</p>}
      {envelope.data.correlationSummary?.nextStep && <p className="surface-summary">{envelope.data.correlationSummary.nextStep}</p>}
      {envelope.data.authorizationBasis?.redactionExplanation && <p className="capability-basis">{envelope.data.authorizationBasis.redactionExplanation}</p>}
      {envelope.data.partial && <p className="surface-state-inline partial" role="status">Partial timeline: unauthorized evidence is omitted or redacted.</p>}
      {envelope.data.redactionSummary && <p className="redaction-note">{envelope.data.redactionSummary}</p>}
      {envelope.data.omittedCategories && envelope.data.omittedCategories.length > 0 && <p>Omitted categories: {envelope.data.omittedCategories.join(', ')}</p>}
      {timelineEvents.length === 0 ? <p>No authorized timeline events are available for this selected AuthContext.</p> : (
        <ol className="audit-timeline" aria-label="Audit correlation timeline">
          {timelineEvents.map((event) => {
            const eventActions = (event.availableEventActionIds ?? ['action-audit-trace-detail'])
              .map((actionId) => actionById.get(actionId))
              .filter((action): action is SurfaceAction => Boolean(action));
            return (
              <li key={event.eventId} id={event.traceId}>
                <time dateTime={event.occurredAt}>{event.occurredAt}</time>
                <p><strong>{event.actor}</strong> {event.action}</p>
                {event.sourceType && <small>Source type: {event.sourceType} · Status: {event.status}</small>}
                {event.correlationId && <small>Correlation: {event.correlationId}</small>}
                {event.recoveryText && <p className="recovery-copy">{event.recoveryText}</p>}
                {eventActions.length > 0 && <SurfaceActionBar actions={eventActions} surfaceId={envelope.surfaceId} label={`Actions for ${event.traceId}`} actionInput={{ traceId: event.traceId, correlationId: event.correlationId ?? envelope.correlationId }} onAction={onAction} />}
              </li>
            );
          })}
        </ol>
      )}
      {envelope.data.links && envelope.data.links.length > 0 && (
        <section className="trace-link-list" aria-label="Authorized related timeline links">
          {envelope.data.links.map((link) => <span key={`${link.label}-${link.actionId ?? 'link'}`}>{link.label} · {link.relationship ?? 'related'}</span>)}
        </section>
      )}
      {envelope.data.recovery && <p className="recovery-copy">{envelope.data.recovery}</p>}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

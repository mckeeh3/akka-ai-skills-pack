import type { AuditTimelineSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type AuditTimelineSurfaceProps = {
  envelope: SurfaceEnvelope<AuditTimelineSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function AuditTimelineSurface({ envelope, onAction }: AuditTimelineSurfaceProps) {
  return (
    <SurfaceStateFrame envelope={envelope}>
      <ol className="audit-timeline">
        {envelope.data.events.map((event) => (
          <li key={event.eventId}>
            <time dateTime={event.occurredAt}>{event.occurredAt}</time>
            <p><strong>{event.actor}</strong> {event.action}</p>
            <a href={`/ui?traceId=${encodeURIComponent(event.traceId)}`}>Open trace</a>
          </li>
        ))}
      </ol>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

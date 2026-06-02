import type { NotificationCenterSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type NotificationCenterSurfaceProps = {
  envelope: SurfaceEnvelope<NotificationCenterSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function NotificationCenterSurface({ envelope, onAction }: NotificationCenterSurfaceProps) {
  const data = envelope.data;
  const items = data.items ?? [];
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="notification-center-summary" aria-label="In-app notification center summary" data-count-source="notification.list_my_account_center">
        <p className="capability-basis">Surface contract: {data.surfaceContract}</p>
        <p className="surface-state-inline partial">In-app only. Email and push delivery are future governed-channel work and are not active controls here.</p>
        <dl className="notification-center-counts">
          <div><dt>Channel</dt><dd>{data.channel}</dd></div>
          <div><dt>Unread</dt><dd>{data.unreadCount}</dd></div>
          <div><dt>Visible</dt><dd>{data.visibleCount}</dd></div>
        </dl>
        {data.redaction && <p className="redaction-note">Redaction: {data.redaction}</p>}
      </section>
      {items.length === 0 ? (
        <section className="surface-state-inline empty" aria-label="No in-app notifications">No authorized in-app notifications are visible for the selected context.</section>
      ) : (
        <section className="surface-section-list notification-center-items" aria-label="Backend-derived in-app notifications">
          {items.map((item) => (
            <article key={item.notificationId} className={`surface-section-card ${priorityClass(item.priority)} ${item.status}`} data-notification-status={item.status} data-notification-redaction={item.redactionLevel}>
              <h4>{item.title ?? 'Redacted notification'}</h4>
              {item.summary && <p>{item.summary}</p>}
              <p>Status: {item.status} · Category: {item.category ?? 'redacted'} · Priority: {item.priority ?? 'info'}</p>
              {item.owningWorkstreamId && <p className="capability-basis">Workstream: {item.owningWorkstreamId}</p>}
              {item.requiredCapabilityId && <p className="capability-basis">Capability: {item.requiredCapabilityId}</p>}
              {item.sourceRefs && item.sourceRefs.length > 0 && <p className="capability-basis">Source refs: {item.sourceRefs.map((ref) => ref.label ?? ref.refId).join(', ')}</p>}
              {item.surfaceRef?.targetSurfaceId && <p className="capability-basis">Open target: {item.surfaceRef.targetSurfaceId}</p>}
              {item.traceRefs && item.traceRefs.length > 0 && <section className="trace-link-list" aria-label="Notification trace links">{item.traceRefs.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>}
            </article>
          ))}
        </section>
      )}
      {data.preferencesSummary && data.preferencesSummary.length > 0 && (
        <section className="surface-section-list notification-preferences" aria-label="In-app notification preference summary">
          {data.preferencesSummary.map((pref) => (
            <article key={pref.preferenceId} className="surface-section-card">
              <h4>{pref.category} · {pref.channel}</h4>
              <p>Enabled: {String(pref.enabled)} · Minimum priority: {pref.minimumPriority} · Include read: {String(pref.includeReadInCenter)}</p>
            </article>
          ))}
        </section>
      )}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

function priorityClass(priority?: string): string {
  return priority === 'urgent' || priority === 'blocked' ? 'danger' : priority ?? 'info';
}

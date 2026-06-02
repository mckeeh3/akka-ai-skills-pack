import type { NotificationCenterSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type NotificationCenterSurfaceProps = {
  envelope: SurfaceEnvelope<NotificationCenterSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function NotificationCenterSurface({ envelope, onAction }: NotificationCenterSurfaceProps) {
  const data = envelope.data;
  const items = data.items ?? [];
  const emailPreferences = data.emailPreferencesSummary ?? [];
  const channelRegistry = data.channelRegistry ?? [];
  const deliveryAttempts = data.deliveryAttempts ?? [];
  const externalOutbox = data.externalOutbox ?? [];
  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="notification-center-summary" aria-label="In-app notification center summary" data-count-source="notification.list_my_account_center">
        <p className="capability-basis">Surface contract: {data.surfaceContract}</p>
        <p className="surface-state-inline partial">In-app notifications remain the source projection. Email is a governed Resend/captured-outbox channel. Webhook, SMS, mobile push, Slack, and Teams are provider-neutral fail-closed seams until Q-001 selects real production providers.</p>
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
      {channelRegistry.length > 0 && (
        <section className="surface-section-list notification-channel-registry" aria-label="Notification channel registry" data-governed-tool="notification.delivery.list_platform">
          {channelRegistry.map((channel) => (
            <article key={channel.channel} className="surface-section-card" data-channel-status={channel.status} data-production-configured={String(channel.productionConfigured)}>
              <h4>{channel.channel}</h4>
              <p>Status: {channel.status} · Provider: {channel.providerKind} · Local/test outbox: {String(channel.localTestOutboxAvailable)}</p>
              <p className="capability-basis">Delivery capability: {channel.deliveryCapabilityId} · Preference capability: {channel.preferenceCapabilityId}</p>
              <p>{channel.statusReason}</p>
            </article>
          ))}
        </section>
      )}
      {deliveryAttempts.length > 0 && (
        <section className="surface-section-list notification-delivery-attempts" aria-label="External notification delivery attempts">
          {deliveryAttempts.map((attempt) => (
            <article key={attempt.attemptId} className="surface-section-card" data-delivery-status={attempt.status}>
              <h4>{attempt.channel} delivery attempt</h4>
              <p>Status: {attempt.status} · Provider: {attempt.providerKind} · Destination: {attempt.destinationSummary ?? 'redacted'}</p>
              {attempt.safeErrorSummary && <p className="surface-state-inline blocked">{attempt.safeErrorSummary}</p>}
              <p className="capability-basis">Outbox: {attempt.outboxId ?? 'none'} · Correlation: {attempt.correlationId ?? 'backend-derived'}</p>
            </article>
          ))}
        </section>
      )}
      {externalOutbox.length > 0 && (
        <section className="surface-section-list notification-external-outbox" aria-label="Captured local test external notification outbox">
          {externalOutbox.map((message) => (
            <article key={message.outboxId} className="surface-section-card">
              <h4>{message.channel} captured outbox</h4>
              <p>{message.title ?? 'Notification'} · {message.previewText ?? 'Preview redacted'}</p>
              <p className="capability-basis">Destination: {message.destinationSummary ?? 'redacted'} · Correlation: {message.correlationId ?? 'backend-derived'}</p>
            </article>
          ))}
        </section>
      )}
      <section className="surface-section-list notification-email-preferences" aria-label="Email notification preference summary" data-channel="email" data-governed-tool="notification.email.list_my_preferences">
        <div className="surface-section-card">
          <h4>Email delivery channel</h4>
          <p>Provider: {data.emailChannel?.provider ?? 'resend'} · Local/test delivery: {data.emailChannel?.localTestDelivery ?? 'captured_outbox'}</p>
          <p className="capability-basis">Updates use {data.emailChannel?.updateCapabilityId ?? 'notification.email.update_preferences'}; provider secrets, Resend configuration, raw email bodies, SMS, push, and webhook controls are not rendered.</p>
          {data.futureDeliveryChannels && <p className="surface-state-inline partial">{data.futureDeliveryChannels}</p>}
        </div>
        {emailPreferences.length === 0 ? (
          <article className="surface-section-card empty">
            <h4>No email categories enabled</h4>
            <p>Email is opt-in or policy-required per backend category. Use the backend action to enable visible allowlisted categories such as digest_ready.</p>
          </article>
        ) : emailPreferences.map((pref) => (
          <article key={pref.preferenceId} className="surface-section-card" data-email-category={pref.category}>
            <h4>{pref.category} · {pref.channel}</h4>
            <p>Enabled: {String(pref.enabled)} · Minimum priority: {pref.minimumPriority} · Digest mode: {pref.digestMode ?? 'immediate'}</p>
            <p className="capability-basis">Boundary: {pref.deliveryBoundary ?? 'production_resend_or_local_captured_outbox'} · Correlation: {pref.correlationId ?? 'backend-derived'}</p>
          </article>
        ))}
      </section>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

function priorityClass(priority?: string): string {
  return priority === 'urgent' || priority === 'blocked' ? 'danger' : priority ?? 'info';
}

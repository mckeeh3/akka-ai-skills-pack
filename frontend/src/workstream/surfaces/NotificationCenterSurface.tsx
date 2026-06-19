import type { NotificationCenterSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type NotificationCenterSurfaceProps = {
  envelope: SurfaceEnvelope<NotificationCenterSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

type NotificationItem = NonNullable<NotificationCenterSurfaceData['items']>[number];

type TriageLane = {
  laneId: 'needs-attention' | 'awareness' | 'handled';
  title: string;
  intent: string;
  items: NotificationItem[];
};

const itemLifecycleActionIds = new Set([
  'action-notification-mark-read',
  'action-notification-dismiss',
  'action-notification-archive',
  'action-notification-snooze'
]);

const preferenceActionIds = new Set(['action-notification-update-preferences']);

const handledStatuses = new Set(['read', 'dismissed', 'archived', 'expired']);
const attentionPriorities = new Set(['blocked', 'urgent', 'warning']);

export function NotificationCenterSurface({ envelope, onAction }: NotificationCenterSurfaceProps) {
  const data = envelope.data;
  const items = sortNotifications(data.items ?? []);
  const refreshActions = envelope.actions.filter((action) => action.actionId === 'action-show-my-account-notification-center');
  const lifecycleActions = envelope.actions.filter((action) => itemLifecycleActionIds.has(action.actionId));
  const preferenceActions = envelope.actions.filter((action) => preferenceActionIds.has(action.actionId));
  const lanes = backendTriageLanes(data.triageSections) ?? buildTriageLanes(items);
  const primaryLane = lanes.find((lane) => lane.laneId === 'needs-attention') ?? lanes[0];
  const secondaryLanes = lanes.filter((lane) => lane.laneId !== 'needs-attention');
  const needsAttentionCount = primaryLane.items.length;
  const blockedCount = items.filter((item) => normalize(item.priority) === 'blocked').length;

  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="notification-triage-hero" aria-labelledby="notification-triage-title" data-count-source="notification.list_my_account_center">
        <div className="notification-triage-intent">
          <p className="eyebrow">Personal in-app triage</p>
          <h3 id="notification-triage-title">Notice what changed. Acknowledge what is done. Keep source work governed.</h3>
          <p>This center shows only authorized in-app notifications for the selected context. Lifecycle actions clear or defer notification visibility; they do not resolve source attention, tasks, workstream events, or audit records.</p>
        </div>
        <dl className="notification-triage-metrics" aria-label="Notification center counts">
          <div><dt>Needs attention</dt><dd>{needsAttentionCount}</dd></div>
          <div><dt>Unread</dt><dd>{data.unreadCount}</dd></div>
          <div><dt>Visible</dt><dd>{data.visibleCount}</dd></div>
          <div><dt>Blocked</dt><dd>{blockedCount}</dd></div>
        </dl>
        <div className="notification-triage-boundary" aria-label="Notification authority boundary">
          <span>Channel: {data.channel}</span>
          <span>Context: {envelope.authContext.selectedContextId}</span>
          <span>Redaction: {data.redaction ?? envelope.redaction.profile}</span>
          <span>Correlation: {data.correlationId ?? envelope.correlationId}</span>
        </div>
        {refreshActions.length > 0 && <SurfaceActionBar actions={refreshActions} surfaceId={envelope.surfaceId} label="Refresh personal notification triage" onAction={onAction} />}
      </section>

      {items.length === 0 ? (
        <section className="notification-triage-empty" aria-label="No personal notifications">
          <h4>Nothing needs acknowledgement right now.</h4>
          <p>No authorized in-app notifications are visible for this selected context. Hidden or unauthorized workstreams are not counted or named.</p>
        </section>
      ) : (
        <section className="notification-triage-board" aria-label="Notification triage lanes">
          <TriageLaneSection lane={primaryLane} actions={lifecycleActions} surfaceId={envelope.surfaceId} onAction={onAction} primary />
          <section className="notification-secondary-lanes" aria-label="Secondary notification lanes">
            {secondaryLanes.map((lane) => <TriageLaneSection key={lane.laneId} lane={lane} actions={lifecycleActions} surfaceId={envelope.surfaceId} onAction={onAction} />)}
          </section>
        </section>
      )}

      <section className="notification-triage-footer" aria-label="Notification center evidence and preferences">
        <article className="notification-triage-panel">
          <h4>In-app preference boundary</h4>
          <p>Preferences apply only to visible, authorized in-app notification categories. Hidden categories are not enumerated.</p>
          {data.preferencesSummary && data.preferencesSummary.length > 0 ? (
            <ul className="notification-preference-list">
              {data.preferencesSummary.map((pref) => (
                <li key={pref.preferenceId}><strong>{pref.category}</strong><span>{pref.enabled ? 'enabled' : 'muted'} · minimum {pref.minimumPriority} · include read {String(pref.includeReadInCenter)}</span></li>
              ))}
            </ul>
          ) : <p className="surface-state-inline empty">No in-app preference overrides are visible.</p>}
          {preferenceActions.length > 0 && <SurfaceActionBar actions={preferenceActions} surfaceId={envelope.surfaceId} label="Update in-app notification preferences" onAction={onAction} />}
        </article>

        <article className="notification-triage-panel">
          <h4>Evidence</h4>
          {data.sourceSummary && Object.keys(data.sourceSummary).length > 0 ? (
            <dl className="notification-source-counts">
              {Object.entries(data.sourceSummary).map(([source, count]) => <div key={source}><dt>{source}</dt><dd>{count}</dd></div>)}
            </dl>
          ) : <p className="surface-state-inline empty">No visible source summary is available.</p>}
          {data.traceRefs && data.traceRefs.length > 0 && <section className="trace-link-list" aria-label="Notification center trace links">{data.traceRefs.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>}
        </article>
      </section>
    </SurfaceStateFrame>
  );
}

function TriageLaneSection({ lane, actions, surfaceId, onAction, primary = false }: { lane: TriageLane; actions: SurfaceAction[]; surfaceId: string; onAction?: NotificationCenterSurfaceProps['onAction']; primary?: boolean }) {
  return (
    <section className={`notification-triage-lane ${lane.laneId}${primary ? ' primary' : ''}`} aria-labelledby={`${lane.laneId}-title`}>
      <header>
        <p className="eyebrow">{lane.items.length} {lane.items.length === 1 ? 'item' : 'items'}</p>
        <h4 id={`${lane.laneId}-title`}>{lane.title}</h4>
        <p>{lane.intent}</p>
      </header>
      {lane.items.length === 0 ? (
        <p className="surface-state-inline empty">No notifications in this lane.</p>
      ) : (
        <div className="notification-lane-items">
          {lane.items.map((item) => <NotificationTriageCard key={item.notificationId} item={item} actions={actions} surfaceId={surfaceId} onAction={onAction} compact={!primary} />)}
        </div>
      )}
    </section>
  );
}

function NotificationTriageCard({ item, actions, surfaceId, onAction, compact = false }: { item: NotificationItem; actions: SurfaceAction[]; surfaceId: string; onAction?: NotificationCenterSurfaceProps['onAction']; compact?: boolean }) {
  const state = notificationStateLabel(item);
  return (
    <article className={`notification-triage-card ${priorityClass(item.priority)} ${normalize(item.status)}${compact ? ' compact' : ''}`} data-notification-status={item.status} data-notification-redaction={item.redactionLevel}>
      <header>
        <div>
          <p className="eyebrow">{state}</p>
          <h5>{item.title ?? 'Redacted notification'}</h5>
        </div>
        <span className="notification-priority-pill">{item.priority ?? 'info'}</span>
      </header>
      {item.summary && <p className="notification-summary-copy">{item.summary}</p>}
      <dl className="notification-explain-grid" aria-label="Why this notification is visible">
        <div><dt>Status</dt><dd>{item.status}</dd></div>
        <div><dt>Category</dt><dd>{item.category ?? 'redacted'}</dd></div>
        {item.owningWorkstreamId && <div><dt>Workstream</dt><dd>{item.owningWorkstreamId}</dd></div>}
        {item.requiredCapabilityId && <div><dt>Capability</dt><dd>{item.requiredCapabilityId}</dd></div>}
        {item.lastChangedAt && <div><dt>Changed</dt><dd>{formatDateTime(item.lastChangedAt)}</dd></div>}
        {item.snoozedUntil && <div><dt>Snoozed until</dt><dd>{formatDateTime(item.snoozedUntil)}</dd></div>}
      </dl>
      {item.sourceRefs && item.sourceRefs.length > 0 && <p className="capability-basis">Why visible: {item.sourceRefs.map((ref) => ref.label ?? ref.refId).join(', ')}</p>}
      {item.surfaceRef?.targetSurfaceId && <p className="capability-basis">Source target requires reauthorization before opening: {item.surfaceRef.targetSurfaceId}</p>}
      {item.traceRefs && item.traceRefs.length > 0 && <section className="trace-link-list" aria-label="Notification trace links">{item.traceRefs.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>}
      {item.availableActions && item.availableActions.length > 0 && <p className="capability-basis">Backend-authored available actions: {item.availableActions.map((action) => `${action.label ?? action.actionId}${action.enabled === false ? ' (disabled)' : ''}`).join(', ')}</p>}
      {actions.length > 0 && <SurfaceActionBar actions={actions} surfaceId={surfaceId} label={`Lifecycle actions for ${item.title ?? item.notificationId}`} actionInput={{ notificationId: item.notificationId }} onAction={onAction} />}
    </article>
  );
}

function backendTriageLanes(sections: NotificationCenterSurfaceData['triageSections']): TriageLane[] | undefined {
  if (!sections?.length) return undefined;
  return sections.map((section) => ({
    laneId: section.sectionId === 'needs_attention' ? 'needs-attention' : section.sectionId === 'handled' ? 'handled' : 'awareness',
    title: section.label,
    intent: section.sectionIntent,
    items: section.items ?? []
  }));
}

function buildTriageLanes(items: NotificationItem[]): TriageLane[] {
  const needsAttention = items.filter((item) => isNeedsAttention(item));
  const handled = items.filter((item) => handledStatuses.has(normalize(item.status)));
  const awareness = items.filter((item) => !needsAttention.includes(item) && !handled.includes(item));
  return [
    { laneId: 'needs-attention', title: 'Needs attention', intent: 'Items to notice now because they are unread, blocked, urgent, warning, or deferred back into view.', items: needsAttention },
    { laneId: 'awareness', title: 'Awareness', intent: 'Visible informational items that can be read, snoozed, dismissed, or archived without changing source work.', items: awareness },
    { laneId: 'handled', title: 'Handled', intent: 'Recently read, dismissed, archived, or expired notifications retained as context.', items: handled }
  ];
}

function isNeedsAttention(item: NotificationItem): boolean {
  const status = normalize(item.status);
  if (handledStatuses.has(status)) return false;
  return status === 'unread' || status === 'snoozed' || attentionPriorities.has(normalize(item.priority));
}

function sortNotifications(items: NotificationItem[]): NotificationItem[] {
  return [...items].sort((left, right) => {
    const leftRank = isNeedsAttention(left) ? 0 : handledStatuses.has(normalize(left.status)) ? 2 : 1;
    const rightRank = isNeedsAttention(right) ? 0 : handledStatuses.has(normalize(right.status)) ? 2 : 1;
    if (leftRank !== rightRank) return leftRank - rightRank;
    return timestamp(right.lastChangedAt ?? right.updatedAt ?? right.createdAt) - timestamp(left.lastChangedAt ?? left.updatedAt ?? left.createdAt);
  });
}

function notificationStateLabel(item: NotificationItem): string {
  if (normalize(item.status) === 'snoozed') return 'Deferred reminder';
  if (handledStatuses.has(normalize(item.status))) return 'Handled notification';
  if (attentionPriorities.has(normalize(item.priority))) return 'Priority attention';
  return 'Visible notification';
}

function priorityClass(priority?: string): string {
  return normalize(priority) === 'urgent' || normalize(priority) === 'blocked' ? 'danger' : normalize(priority) || 'info';
}

function normalize(value?: string): string {
  return (value ?? '').toLowerCase();
}

function timestamp(value?: string): number {
  if (!value) return 0;
  const parsed = Date.parse(value);
  return Number.isNaN(parsed) ? 0 : parsed;
}

function formatDateTime(value: string): string {
  const parsed = Date.parse(value);
  if (Number.isNaN(parsed)) return value;
  return new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(parsed);
}

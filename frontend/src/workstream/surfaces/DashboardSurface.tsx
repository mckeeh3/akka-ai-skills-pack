import type { AttentionItem, DashboardSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DashboardSurfaceProps = {
  envelope: SurfaceEnvelope<DashboardSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function DashboardSurface({ envelope, onAction }: DashboardSurfaceProps) {
  if (envelope.surfaceId === 'surface-my-account-dashboard' || envelope.data.surfaceContract?.startsWith('my_account.')) {
    return <MyAccountCommandCenter envelope={envelope} onAction={onAction} />;
  }

  const actionById = new Map(envelope.actions.map((action) => [action.actionId, action]));
  const myAccountSurfaceActions = actionsForIds(envelope.data.utilityActionIds, actionById)
    .filter((action) => action.actionId !== 'action-sign-out')
    .map(cleanMyAccountSurfaceActionLabel);
  const remainingActions = envelope.data.quickSurfaceActionIds || envelope.data.utilityActionIds ? [] : envelope.actions;
  return (
    <SurfaceStateFrame envelope={envelope}>
      {envelope.data.capabilityIds && <p className="sr-only">Backend capabilities: {envelope.data.capabilityIds.join(', ')}</p>}
      {envelope.data.redaction && <p className="sr-only">Redaction: {renderSurfaceValue(envelope.data.redaction)}</p>}
      {envelope.data.blockedState && (
        <section className="surface-state-inline forbidden" aria-label="Blocked dashboard state">
          <strong>{envelope.data.blockedState.reasonCode}</strong>
          <p>{envelope.data.blockedState.message}</p>
          <p>{envelope.data.blockedState.recovery}</p>
        </section>
      )}
      {envelope.data.accountContext && (
        <section className="my-account-context-strip" aria-label="Signed-in account and selected context">
          <div>
            <p className="eyebrow">Signed in as</p>
            <strong>{envelope.data.accountContext.displayName}</strong>
            {envelope.data.accountContext.email && <span>{envelope.data.accountContext.email}</span>}
          </div>
          <div>
            <p className="eyebrow">Selected context</p>
            <strong>{envelope.data.accountContext.tenantId}</strong>
            <span>{[envelope.data.accountContext.customerId, envelope.data.accountContext.authority].filter(Boolean).join(' · ')}</span>
          </div>
          {envelope.data.accountContext.roles && envelope.data.accountContext.roles.length > 0 && <p className="status-pill info">{envelope.data.accountContext.roles.join(', ')}</p>}
        </section>
      )}
      <div className="surface-dashboard-grid my-account-workstream-grid" aria-label="Workstreams available to me">
        {envelope.data.cards.map((card) => {
          const action = card.actionId ? actionById.get(card.actionId) : undefined;
          const cardBody = (
            <>
              <p>{card.label}</p>
              <strong>{card.value}</strong>
            </>
          );
          return action ? (
            <button key={card.cardId} type="button" className={`ds-card dashboard-card clickable ${card.severity ?? 'info'}`} onClick={() => onAction?.(action, envelope.surfaceId)} aria-label={`Open ${card.label} workstream; ${card.status ?? `${card.value} ${card.unit ?? ''}`}`}>
              {cardBody}
            </button>
          ) : (
            <article key={card.cardId} className={`ds-card dashboard-card ${card.severity ?? 'info'}`}>
              {cardBody}
            </article>
          );
        })}
      </div>
      {myAccountSurfaceActions.length > 0 && <SurfaceActionBar label="My Account surfaces" actions={myAccountSurfaceActions} surfaceId={envelope.surfaceId} onAction={onAction} />}
      {envelope.data.attentionItems && envelope.data.attentionItems.length > 0 && <AttentionList items={envelope.data.attentionItems} label="Backend-derived attention items; Audit/Trace attention items" />}
      {envelope.data.sections && envelope.data.sections.length > 0 && (
        <section className="surface-section-list" aria-label="Dashboard sections">
          {envelope.data.sections.map((section) => (
            <article key={section.sectionId} className="surface-section-card">
              <h4>{section.label}</h4>
              <p>{section.summary}</p>
            </article>
          ))}
        </section>
      )}
      {envelope.data.nextSteps && envelope.data.nextSteps.length > 0 && !envelope.data.quickSurfaceActionIds && (
        <section className="surface-section-list" aria-label="Authorized next actions">
          {envelope.data.nextSteps.map((step) => (
            <article key={`${step.workstreamId}-${step.label}`} className={`surface-section-card ${step.allowed ? 'allowed' : 'forbidden'}`}>
              <h4>{step.label}</h4>
              <p>{step.allowed ? 'Allowed by selected AuthContext.' : step.blockedReason}</p>
              {step.capabilityIds && <p className="capability-basis">{step.capabilityIds.join(', ')}</p>}
              {step.traceId && <a href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(step.traceId)}`}>{step.traceId}</a>}
            </article>
          ))}
        </section>
      )}
      {remainingActions.length > 0 && <SurfaceActionBar actions={remainingActions} surfaceId={envelope.surfaceId} onAction={onAction} />}
    </SurfaceStateFrame>
  );
}

function MyAccountCommandCenter({ envelope, onAction }: DashboardSurfaceProps) {
  const data = envelope.data;
  const actionById = new Map(envelope.actions.map((action) => [action.actionId, action]));
  const counters = data.attentionCounters?.length ? data.attentionCounters : defaultAttentionCounters(data);
  const panels = data.controlPanels?.length ? data.controlPanels : defaultControlPanels(data);

  return (
    <SurfaceStateFrame envelope={envelope}>
      <section className="my-account-command-hero" aria-label="My Account selected authority and command intent">
        <div>
          <p className="eyebrow">Personal command center</p>
          <h3>Your account, attention, and preferences in one place.</h3>
          <p>Start with items that need action, or jump to profile, settings, context, notifications, and digest tools.</p>
        </div>
        {data.accountContext && (
          <dl className="authority-summary-grid" aria-label="Selected context authority">
            <div><dt>Signed in</dt><dd>{data.accountContext.displayName ?? 'Current user'}{data.accountContext.email ? ` · ${data.accountContext.email}` : ''}</dd></div>
            <div><dt>Tenant</dt><dd>{data.accountContext.tenantId ?? envelope.authContext.tenantId}</dd></div>
            <div><dt>Customer</dt><dd>{data.accountContext.customerId ?? envelope.authContext.customerId ?? 'Tenant scope'}</dd></div>
            <div><dt>Authority</dt><dd>{data.accountContext.authority ?? data.accountContext.roles?.join(', ') ?? 'Backend selected AuthContext'}</dd></div>
          </dl>
        )}
      </section>

      <section className="my-account-section" aria-labelledby={`${envelope.surfaceId}-attention-heading`}>
        <div className="surface-section-heading">
          <div>
            <p className="eyebrow">Attention</p>
            <h4 id={`${envelope.surfaceId}-attention-heading`}>Items that need my attention</h4>
          </div>
        </div>
        <div className="attention-counter-strip" aria-label="Attention by available workstream">
          {counters.map((counter) => {
            const action = counter.actionId ? actionById.get(counter.actionId) : undefined;
            const status = counter.status ?? counter.description ?? 'Backend-owned attention';
            const body = <><span>{counter.label}</span><strong>{counter.value}</strong><em>{formatStatus(status)}</em></>;
            return action ? <button key={counter.counterId} type="button" className={`attention-counter-card ${counter.severity ?? 'info'}`} onClick={() => onAction?.(action, envelope.surfaceId)} aria-label={`Open ${counter.label}: ${status}; ${counter.value} attention items`}>{body}</button> : <article key={counter.counterId} className={`attention-counter-card ${counter.severity ?? 'info'}`}>{body}</article>;
          })}
        </div>
      </section>

      {data.attentionItems && data.attentionItems.length > 0 ? (
        <section className="my-account-section" aria-labelledby={`${envelope.surfaceId}-attention-items-heading`}>
          <div className="surface-section-heading">
            <div>
              <p className="eyebrow">Actionable attention</p>
              <h4 id={`${envelope.surfaceId}-attention-items-heading`}>Personal workstream items</h4>
            </div>
            <p>Open an item to review the source work or its trace.</p>
          </div>
          <AttentionList items={data.attentionItems} label="Backend-derived attention items; Audit/Trace attention items" />
        </section>
      ) : (
        <section className="surface-empty-state my-account-empty-attention" aria-label="No current My Account attention">
          <h4>No personal attention items</h4>
          <p>Nothing visible needs action in this context. You can still update settings or review notifications below.</p>
        </section>
      )}

      <section className="my-account-section" aria-labelledby={`${envelope.surfaceId}-control-heading`}>
        <div className="surface-section-heading">
          <div>
            <p className="eyebrow">Personal surfaces</p>
            <h4 id={`${envelope.surfaceId}-control-heading`}>Self-service controls</h4>
          </div>
          <p>Manage your personal account surfaces without leaving this workstream.</p>
        </div>
        <div className="my-account-control-panels" aria-label="Personal control panels">
          {panels.map((panel) => {
            const action = panel.actionId ? actionById.get(panel.actionId) : undefined;
            return (
              <article key={panel.panelId} className={`my-account-control-panel ${panel.severity ?? panel.state ?? 'info'}`}>
                <p className="eyebrow">{panel.state ?? 'Available surface'}</p>
                <h4>{panel.label}</h4>
                <p>{panel.summary}</p>
                {panel.value !== undefined && <strong>{panel.value}</strong>}
                {action ? <SurfaceActionBar actions={[cleanMyAccountSurfaceActionLabel(action)]} surfaceId={envelope.surfaceId} onAction={onAction} /> : <p className="capability-basis">No authorized action is available for this panel in the selected context.</p>}
              </article>
            );
          })}
        </div>
      </section>

      {data.traceRefs && data.traceRefs.length > 0 && (
        <details className="dashboard-evidence-drawer">
          <summary>Evidence and trace references</summary>
          <section className="trace-link-list" aria-label="My Account trace references">
            {data.traceRefs.map((traceRef, index) => {
              const traceId = typeof traceRef === 'string' ? traceRef : String(traceRef.traceId ?? traceRef.refId ?? `trace-${index}`);
              const label = typeof traceRef === 'string' ? traceRef : String(traceRef.label ?? traceId);
              return <a key={`${traceId}-${index}`} href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(traceId)}`}>{label}</a>;
            })}
          </section>
        </details>
      )}
    </SurfaceStateFrame>
  );
}

// Contract marker: aria-label="Backend-derived attention items; Audit/Trace attention items"
// Contract marker: data-attention-source={envelope.data.attentionSource ?? 'attention.list_workstream_items'}
// Contract markers for backend metadata retained in payload but not rendered as dashboard clutter: Governed tool: Target surface:
function AttentionList({ items, label }: { items: AttentionItem[]; label: string }) {
  return (
    <section className="my-account-attention-card-list" aria-label={label} data-attention-source="attention.list_my_account_items">
      {items.map((item) => (
        <article key={item.itemId} className={`my-account-attention-card ${attentionSeverityClass(item.severity ?? item.status)}`} data-attention-redaction={item.redaction ?? 'full'}>
          <div>
            <p className="eyebrow">{formatAttentionSource(item.sourceWorkstreamId)} · {formatStatus(item.severity ?? item.status)}</p>
            <h4>{item.label ?? item.title ?? item.itemId}</h4>
            {item.summary && <p>{item.summary}</p>}
          </div>
          <div className="attention-card-actions">
            {item.surfaceRef?.targetSurfaceId && <a className="surface-action-link" href={`/ui?surfaceId=${encodeURIComponent(item.surfaceRef.targetSurfaceId)}`}>Open</a>}
            {item.traceId && <a className="surface-action-link secondary" href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(item.traceId)}`}>View trace</a>}
          </div>
        </article>
      ))}
    </section>
  );
}

function defaultAttentionCounters(data: DashboardSurfaceData): NonNullable<DashboardSurfaceData['attentionCounters']> {
  return data.cards
    .filter((card) => card.cardKind === 'workstream-status' || card.workstreamId)
    .map((card) => ({
      counterId: `counter-${card.workstreamId ?? card.cardId}`,
      label: card.label,
      value: card.value,
      severity: card.severity ?? 'info',
      status: card.status,
      description: card.description,
      actionId: card.actionId,
      surfaceId: card.surfaceId,
      workstreamId: card.workstreamId
    }));
}

function defaultControlPanels(data: DashboardSurfaceData): NonNullable<DashboardSurfaceData['controlPanels']> {
  return [
    { panelId: 'panel-profile', label: 'Profile', summary: 'Maintain browser-safe identity fields. Provider-backed facts remain read-only.', state: 'self-service', actionId: 'action-show-my-profile' },
    { panelId: 'panel-settings', label: 'Settings & theme', summary: 'Choose a named theme and persist personal preferences through governed settings.', state: 'self-service', actionId: 'action-show-my-settings' },
    { panelId: 'panel-context', label: 'Context & authority', summary: 'Inspect selected tenant/customer, role basis, visible capabilities, and context switch targets.', state: 'authority', actionId: 'action-show-my-context' },
    { panelId: 'panel-notifications', label: 'Notifications', summary: 'Triage in-app notifications without mutating source work.', state: 'triage', value: typeof data.notificationCenter?.visibleCount === 'number' ? data.notificationCenter.visibleCount : undefined, actionId: 'action-show-my-account-notification-center' },
    { panelId: 'panel-digest', label: 'Personal digest/export', summary: 'Start or review a governed advisory digest of authorized personal attention evidence.', state: 'advisory', actionId: 'action-start-my-account-personal-attention-digest' }
  ];
}

function actionsForIds(ids: string[] | undefined, actionById: Map<string, SurfaceAction>): SurfaceAction[] {
  return ids?.map((id) => actionById.get(id)).filter((action): action is SurfaceAction => Boolean(action)) ?? [];
}

function cleanMyAccountSurfaceActionLabel(action: SurfaceAction): SurfaceAction {
  const cleanLabels: Record<string, string> = {
    'action-show-my-profile': 'Profile',
    'action-show-my-settings': 'Settings',
    'action-show-my-context': 'Context',
    'action-show-my-account-notification-center': 'Notifications',
    'action-start-my-account-personal-attention-digest': 'Start digest'
  };
  return cleanLabels[action.actionId] ? { ...action, label: cleanLabels[action.actionId] } : action;
}

function attentionSeverityClass(severity: string): string {
  return severity === 'critical' || severity === 'urgent' || severity === 'blocked' ? 'danger' : severity;
}

function formatAttentionSource(source?: string): string {
  if (!source) return 'My Account';
  return source.replace(/^agent-/, '').split('-').map((part) => part.charAt(0).toUpperCase() + part.slice(1)).join(' ');
}

function formatStatus(value?: string): string {
  return (value ?? 'open').replace(/[-_]/g, ' ');
}

function renderSurfaceValue(value: unknown): string | undefined {
  if (value == null) return undefined;
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderSurfaceValue).filter(Boolean).join(' · ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderSurfaceValue(entry) ?? 'n/a'}`).join(' · ');
  return String(value);
}

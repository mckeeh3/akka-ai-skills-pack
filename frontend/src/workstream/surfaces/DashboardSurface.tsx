import type { DashboardSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DashboardSurfaceProps = {
  envelope: SurfaceEnvelope<DashboardSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function DashboardSurface({ envelope, onAction }: DashboardSurfaceProps) {
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
      {envelope.data.attentionItems && envelope.data.attentionItems.length > 0 && (
        <section className="surface-section-list" aria-label="Backend-derived attention items; Audit/Trace attention items" data-attention-source={envelope.data.attentionSource ?? 'attention.list_workstream_items'}>
          <h3 className="sr-only">Recent items needing my attention</h3>
          {envelope.data.attentionItems.map((item) => (
            <article key={item.itemId} className={`surface-section-card ${attentionSeverityClass(item.severity ?? item.status)}`} data-attention-redaction={item.redaction ?? 'full'}>
              <h4>{item.label ?? item.title ?? item.itemId}</h4>
              <span className="sr-only">Status: {item.status}{item.category ? ` · Category: ${item.category}` : ''}</span>
              {item.sourceWorkstreamId && <span className="sr-only">Source workstream: {item.sourceWorkstreamId}</span>}
              {item.capabilityId && <span className="sr-only">Capability: {item.capabilityId}</span>}
              {item.governedToolId && <span className="sr-only">Governed tool: {item.governedToolId}</span>}
              {item.surfaceRef?.targetSurfaceId && <span className="sr-only">Target surface: {item.surfaceRef.targetSurfaceId}</span>}
              {item.traceId && <a className="sr-only" href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(item.traceId)}`}>{item.traceId}</a>}
            </article>
          ))}
        </section>
      )}
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

function actionsForIds(ids: string[] | undefined, actionById: Map<string, SurfaceAction>): SurfaceAction[] {
  return ids?.map((id) => actionById.get(id)).filter((action): action is SurfaceAction => Boolean(action)) ?? [];
}

function cleanMyAccountSurfaceActionLabel(action: SurfaceAction): SurfaceAction {
  const cleanLabels: Record<string, string> = {
    'action-show-my-profile': 'Profile',
    'action-show-my-settings': 'Settings',
    'action-show-my-context': 'Context',
    'action-show-my-account-notification-center': 'Notifications'
  };
  return cleanLabels[action.actionId] ? { ...action, label: cleanLabels[action.actionId] } : action;
}

function attentionSeverityClass(severity: string): string {
  return severity === 'critical' || severity === 'urgent' || severity === 'blocked' ? 'danger' : severity;
}

function renderSurfaceValue(value: unknown): string | undefined {
  if (value == null) return undefined;
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderSurfaceValue).filter(Boolean).join(' · ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderSurfaceValue(entry) ?? 'n/a'}`).join(' · ');
  return String(value);
}

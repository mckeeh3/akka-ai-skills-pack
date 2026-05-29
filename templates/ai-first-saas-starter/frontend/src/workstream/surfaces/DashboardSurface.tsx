import type { DashboardSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DashboardSurfaceProps = {
  envelope: SurfaceEnvelope<DashboardSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function DashboardSurface({ envelope, onAction }: DashboardSurfaceProps) {
  const readiness = renderSurfaceValue(envelope.data.readiness);
  return (
    <SurfaceStateFrame envelope={envelope}>
      {readiness && <p className="surface-readiness">{readiness}</p>}
      {envelope.data.capabilityIds && <p className="capability-basis">Backend capabilities: {envelope.data.capabilityIds.join(', ')}</p>}
      {envelope.data.blockedState && (
        <section className="surface-state-inline forbidden" aria-label="Blocked dashboard state">
          <strong>{envelope.data.blockedState.reasonCode}</strong>
          <p>{envelope.data.blockedState.message}</p>
          <p>{envelope.data.blockedState.recovery}</p>
        </section>
      )}
      <div className="surface-dashboard-grid">
        {envelope.data.cards.map((card) => (
          <article key={card.cardId} className={`ds-card dashboard-card ${card.severity ?? 'info'}`}>
            <p>{card.label}</p>
            <strong>{card.value}</strong>
          </article>
        ))}
      </div>
      {envelope.data.sections && (
        <section className="surface-section-list" aria-label="Dashboard sections">
          {envelope.data.sections.map((section) => (
            <article key={section.sectionId} className="surface-section-card">
              <h4>{section.label}</h4>
              <p>{section.summary}</p>
            </article>
          ))}
        </section>
      )}
      {envelope.data.nextSteps && (
        <section className="surface-section-list" aria-label="Authorized next actions">
          {envelope.data.nextSteps.map((step) => (
            <article key={`${step.workstreamId}-${step.label}`} className={`surface-section-card ${step.allowed ? 'allowed' : 'forbidden'}`}>
              <h4>{step.label}</h4>
              <p>{step.allowed ? 'Allowed by selected AuthContext.' : step.blockedReason}</p>
              {step.capabilityIds && <p className="capability-basis">{step.capabilityIds.join(', ')}</p>}
              {step.traceId && <a href={`/ui?surfaceId=surface-audit-timeline#${encodeURIComponent(step.traceId)}`}>{step.traceId}</a>}
            </article>
          ))}
        </section>
      )}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

function renderSurfaceValue(value: unknown): string | undefined {
  if (value == null) return undefined;
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderSurfaceValue).filter(Boolean).join(' · ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderSurfaceValue(entry) ?? 'n/a'}`).join(' · ');
  return String(value);
}

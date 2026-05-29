import type { DashboardSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DashboardSurfaceProps = {
  envelope: SurfaceEnvelope<DashboardSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function DashboardSurface({ envelope, onAction }: DashboardSurfaceProps) {
  return (
    <SurfaceStateFrame envelope={envelope}>
      {envelope.data.blockedState && (
        <div className="surface-state-inline forbidden" role="status" aria-live="polite">
          <strong>{envelope.data.blockedState.reasonCode}</strong>
          <p>{envelope.data.blockedState.message}</p>
          <p>{envelope.data.blockedState.recovery}</p>
        </div>
      )}
      {envelope.data.readiness && <p className="surface-readiness">{envelope.data.readiness}</p>}
      {envelope.data.capabilityIds && <p className="capability-basis">Backend capabilities: {envelope.data.capabilityIds.join(', ')}</p>}
      <div className="surface-dashboard-grid">
        {envelope.data.cards.map((card) => (
          <article key={card.cardId} className={`ds-card dashboard-card ${card.severity ?? 'info'}`}>
            <p>{card.label}</p>
            <strong>{card.value}</strong>
          </article>
        ))}
      </div>
      {envelope.data.sections && (
        <section className="surface-dashboard-sections" aria-label="Surface sections">
          {envelope.data.sections.map((section) => (
            <article key={section.sectionId} className="surface-dashboard-section">
              <h4>{section.label}</h4>
              <p>{section.summary}</p>
            </article>
          ))}
        </section>
      )}
      {envelope.data.nextSteps && (
        <section className="surface-next-steps" aria-label="Authorized next steps">
          {envelope.data.nextSteps.map((step) => (
            <article key={step.workstreamId} className={`surface-next-step ${step.allowed ? 'allowed' : 'blocked'}`}>
              <h4>{step.label}</h4>
              <p>{step.allowed ? 'Backend capability summary allows opening this workstream.' : step.blockedReason ?? 'Backend capability summary blocks this workstream.'}</p>
              {step.capabilityIds && <small>Capabilities: {step.capabilityIds.join(', ')}</small>}
              {step.traceId && <small>Trace: {step.traceId}</small>}
            </article>
          ))}
        </section>
      )}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

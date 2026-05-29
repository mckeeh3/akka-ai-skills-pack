import type { DecisionSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DecisionSurfaceProps = {
  envelope: SurfaceEnvelope<DecisionSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function DecisionSurface({ envelope, onAction }: DecisionSurfaceProps) {
  const evidence = envelope.data.evidence ?? [];
  return (
    <SurfaceStateFrame envelope={envelope}>
      <article className="decision-card">
        <p>{envelope.data.recommendation}</p>
        <dl>
          <dt>Risk</dt><dd>{envelope.data.riskScore ?? envelope.data.risk ?? 'Not scored'}</dd>
          <dt>Confidence</dt><dd>{envelope.data.confidenceScore ?? 'Not scored'}</dd>
        </dl>
        {evidence.length > 0 && (
          <>
            <h4>Evidence</h4>
            <ul>{evidence.map((item) => <li key={item.evidenceId}><strong>{item.label}</strong>: {item.summary}</li>)}</ul>
          </>
        )}
        {envelope.data.allowedActions && (
          <section aria-label="Backend-authorized investigation actions">
            <h4>Allowed actions</h4>
            <ul>{envelope.data.allowedActions.map((action) => <li key={action.actionId}>{action.label} · {action.capabilityId}</li>)}</ul>
          </section>
        )}
        {envelope.data.disabledActions && (
          <section aria-label="Disabled or deferred investigation actions">
            <h4>Disabled actions</h4>
            <ul>{envelope.data.disabledActions.map((action) => <li key={action.actionId}>{action.actionId}: {action.reason}</li>)}</ul>
          </section>
        )}
        {envelope.data.traceLinks && <section className="trace-link-list" aria-label="Investigation guidance trace links">{envelope.data.traceLinks.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-timeline#${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>}
      </article>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

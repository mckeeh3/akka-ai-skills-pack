import type { GovernanceDiffSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type GovernanceDiffSurfaceProps = {
  envelope: SurfaceEnvelope<GovernanceDiffSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function GovernanceDiffSurface({ envelope, onAction }: GovernanceDiffSurfaceProps) {
  return (
    <SurfaceStateFrame envelope={envelope}>
      <div className="governance-diff-summary" aria-label="Policy proposal summary">
        <section><h4>Before</h4><p>{envelope.data.beforeSummary}</p></section>
        <section><h4>After</h4><p>{envelope.data.afterSummary}</p></section>
      </div>
      <dl className="governance-policy-metadata" aria-label="Policy proposal governance metadata">
        {envelope.data.lifecycleState && <><dt>Lifecycle state</dt><dd>{envelope.data.lifecycleState}</dd></>}
        {envelope.data.riskClassification && <><dt>Risk</dt><dd>{envelope.data.riskClassification}</dd></>}
        {envelope.data.requiredApproval && <><dt>Required approval</dt><dd>{envelope.data.requiredApproval}</dd></>}
        {envelope.data.activationStatus && <><dt>Activation status</dt><dd>{envelope.data.activationStatus}</dd></>}
      </dl>
      {envelope.data.simulationSummary && <p className="capability-basis">Simulation summary: {envelope.data.simulationSummary}</p>}
      {envelope.data.simulation && (
        <section className="governance-simulation" aria-labelledby={`${envelope.surfaceId}-simulation`}>
          <h4 id={`${envelope.surfaceId}-simulation`}>Deterministic simulation evidence</h4>
          <p>Confidence: {envelope.data.simulation.confidence}</p>
          <p>Affected capabilities: {envelope.data.simulation.affectedCapabilities.join(', ')}</p>
          <p>Expected allows: {envelope.data.simulation.expectedAllows.join(', ') || 'none'}</p>
          <p>Expected denials: {envelope.data.simulation.expectedDenials.join(', ') || 'none'}</p>
          {envelope.data.simulation.warnings.length > 0 && <p>Warnings: {envelope.data.simulation.warnings.join('; ')}</p>}
          <p>Evidence traces: {envelope.data.simulation.evidenceTraceIds.join(', ')}</p>
        </section>
      )}
      <table>
        <caption>Policy proposal changes</caption>
        <thead><tr><th scope="col">Path</th><th scope="col">Before</th><th scope="col">After</th><th scope="col">Impact</th></tr></thead>
        <tbody>{envelope.data.changes.map((change) => <tr key={change.path}><th scope="row">{change.path}</th><td>{change.before ?? ''}</td><td>{change.after ?? ''}</td><td>{change.impact}</td></tr>)}</tbody>
      </table>
      {envelope.data.traceLinks && <p className="surface-trace-summary">Trace links: {envelope.data.traceLinks.join(', ')}</p>}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

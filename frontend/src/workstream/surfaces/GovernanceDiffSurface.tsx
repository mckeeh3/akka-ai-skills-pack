import type { GovernanceDiffSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type GovernanceDiffSurfaceProps = {
  envelope: SurfaceEnvelope<GovernanceDiffSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

function humanize(value: unknown) {
  return String(value ?? '').replace(/[_-]/g, ' ').replace(/\b(agent_admin|ToolPermissionBoundary|readSkill|readReferenceDoc)\b/g, (match) => match === 'agent_admin' ? 'Agent Admin' : match);
}

export function GovernanceDiffSurface({ envelope, onAction }: GovernanceDiffSurfaceProps) {
  return (
    <SurfaceStateFrame envelope={envelope}>
      <div className="governance-diff-summary" aria-label="Policy proposal summary">
        <section><h4>Before</h4><p>{envelope.data.beforeSummary}</p></section>
        <section><h4>After</h4><p>{envelope.data.afterSummary}</p></section>
      </div>
      <dl className="governance-policy-metadata" aria-label="Policy proposal governance metadata and behavior change review summary">
        {envelope.data.lifecycleState && <><dt>Lifecycle state</dt><dd>{humanize(envelope.data.lifecycleState)}</dd></>}
        {envelope.data.riskClassification && <><dt>Risk</dt><dd>{humanize(envelope.data.riskClassification)}</dd></>}
        {envelope.data.activationStatus && <><dt>Activation status</dt><dd>{humanize(envelope.data.activationStatus)}</dd></>}
      </dl>
      {envelope.data.requiredApproval && <details className="dashboard-evidence-drawer"><summary>Role-gated approval diagnostics</summary><p>{envelope.data.requiredApproval}</p></details>}
      {envelope.data.simulationSummary && <p className="capability-basis">Simulation summary: {humanize(envelope.data.simulationSummary)}</p>}
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
        <caption>Behavior change summary</caption>
        <thead><tr><th scope="col">Change</th><th scope="col">Before</th><th scope="col">After</th><th scope="col">Impact</th></tr></thead>
        <tbody>{envelope.data.changes.map((change) => <tr key={change.path}><th scope="row">{humanize(change.path)}</th><td>{humanize(change.before ?? '')}</td><td>{humanize(change.after ?? '')}</td><td>{humanize(change.impact)}</td></tr>)}</tbody>
      </table>
      <details className="dashboard-evidence-drawer"><summary>Role-gated diff identifiers and trace diagnostics</summary><p>Paths: {envelope.data.changes.map((change) => change.path).join(', ')}</p>{envelope.data.traceLinks && <p>Trace links: {envelope.data.traceLinks.join(', ')}</p>}</details>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

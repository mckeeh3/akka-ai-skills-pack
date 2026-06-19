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
  const changeRows = envelope.data.changes ?? envelope.data.changeSet?.diffRows ?? [];
  const beforeSummary = envelope.data.beforeSummary ?? envelope.data.changeSet?.beforeSummary ?? 'No previous-state summary was provided.';
  const afterSummary = envelope.data.afterSummary ?? envelope.data.changeSet?.afterSummary ?? 'No proposed-state summary was provided.';
  const simulation = envelope.data.simulation;
  const affectedCapabilities = simulation?.affectedCapabilities ?? [];
  const expectedAllows = simulation?.expectedAllows ?? [];
  const expectedDenials = simulation?.expectedDenials ?? [];
  const simulationWarnings = simulation?.warnings ?? [];
  const evidenceTraceIds = simulation?.evidenceTraceIds ?? [];

  return (
    <SurfaceStateFrame envelope={envelope}>
      <div className="governance-diff-summary" aria-label="Policy proposal summary">
        <section><h4>Before</h4><p>{beforeSummary}</p></section>
        <section><h4>After</h4><p>{afterSummary}</p></section>
      </div>
      <dl className="governance-policy-metadata" aria-label="Policy proposal governance metadata and behavior change review summary">
        {envelope.data.proposalSummary?.title && <><dt>Proposal</dt><dd>{humanize(envelope.data.proposalSummary.title)}</dd></>}
        {envelope.data.lifecycleState && <><dt>Lifecycle state</dt><dd>{humanize(envelope.data.lifecycleState)}</dd></>}
        {envelope.data.riskClassification && <><dt>Risk</dt><dd>{humanize(envelope.data.riskClassification)}</dd></>}
        {envelope.data.proposalSummary?.freshnessStatus && <><dt>Freshness</dt><dd>{humanize(envelope.data.proposalSummary.freshnessStatus)}</dd></>}
        {envelope.data.activationStatus && <><dt>Activation status</dt><dd>{humanize(envelope.data.activationStatus)}</dd></>}
      </dl>
      {envelope.data.proposalSummary?.safeEmptyNewDraftCopy && <p className="capability-basis">{envelope.data.proposalSummary.safeEmptyNewDraftCopy}</p>}
      {envelope.data.draftFields && (
        <section className="governance-draft-fields" aria-label="Backend-authored proposal draft fields">
          <h4>Draft fields</h4>
          <p>Intent: {humanize(envelope.data.draftFields.draftIntent)}</p>
          <p>Rationale: {humanize(envelope.data.draftFields.rationale)}</p>
          <p>Requested scope: {humanize(envelope.data.draftFields.requestedEffectiveScopeSummary)}</p>
          <p>{envelope.data.draftFields.editable ? 'Editable through governed draft action.' : 'Read-only for this selected AuthContext or lifecycle state.'}</p>
        </section>
      )}
      {envelope.data.lifecycleGate && (
        <section className="governance-lifecycle-gate" aria-label="Proposal lifecycle gate and provider readiness">
          <h4>Lifecycle gate</h4>
          <p>Approvals: {(envelope.data.lifecycleGate.requiredApprovals ?? []).join(', ') || 'none'}</p>
          <p>Simulation evidence: {humanize(envelope.data.lifecycleGate.simulationEvidenceStatus)}</p>
          <p>Impact analysis: {humanize(envelope.data.lifecycleGate.impactAnalysisStatus)}</p>
          <p>Activation/rollback: {humanize(envelope.data.lifecycleGate.activationRollbackGateSummary)}</p>
        </section>
      )}
      {envelope.data.requiredApproval && <details className="dashboard-evidence-drawer"><summary>Role-gated approval diagnostics</summary><p>{envelope.data.requiredApproval}</p></details>}
      {envelope.data.simulationSummary && <p className="capability-basis">Simulation summary: {humanize(envelope.data.simulationSummary)}</p>}
      {simulation && (
        <section className="governance-simulation" aria-labelledby={`${envelope.surfaceId}-simulation`}>
          <h4 id={`${envelope.surfaceId}-simulation`}>Deterministic simulation evidence</h4>
          <p>Confidence: {simulation.confidence}</p>
          <p>Affected capabilities: {affectedCapabilities.join(', ') || 'none'}</p>
          <p>Expected allows: {expectedAllows.join(', ') || 'none'}</p>
          <p>Expected denials: {expectedDenials.join(', ') || 'none'}</p>
          {simulationWarnings.length > 0 && <p>Warnings: {simulationWarnings.join('; ')}</p>}
          <p>Evidence traces: {evidenceTraceIds.join(', ') || 'none'}</p>
        </section>
      )}
      <table>
        <caption>Behavior change summary</caption>
        <thead><tr><th scope="col">Change</th><th scope="col">Before</th><th scope="col">After</th><th scope="col">Impact</th></tr></thead>
        <tbody>{changeRows.length > 0 ? changeRows.map((change) => <tr key={change.path}><th scope="row">{humanize(change.path)}</th><td>{humanize(change.before ?? '')}</td><td>{humanize(change.after ?? '')}</td><td>{humanize(change.impact)}</td></tr>) : <tr><td colSpan={4}>No row-level diff was provided for this governed surface.</td></tr>}</tbody>
      </table>
      {envelope.data.availableTransitions && envelope.data.availableTransitions.length > 0 && (
        <section className="governance-available-transitions" aria-label="Backend-authorized proposal transitions">
          <h4>Authorized transitions</h4>
          <ul>{envelope.data.availableTransitions.map((transition) => <li key={transition.actionId}>{humanize(transition.label ?? transition.actionId)} · {humanize(transition.resultSurfaceId)}{transition.idempotencyRequired ? ' · idempotency required' : ''}</li>)}</ul>
        </section>
      )}
      <details className="dashboard-evidence-drawer"><summary>Role-gated diff identifiers and trace diagnostics</summary><p>Paths: {changeRows.map((change) => change.path).join(', ') || 'none provided'}</p>{envelope.data.traceLinks && <p>Trace links: {envelope.data.traceLinks.join(', ')}</p>}{envelope.data.traceRefs && <p>Trace refs: {envelope.data.traceRefs.join(', ')}</p>}</details>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} actionInput={envelope.data.proposalId ? { proposalId: envelope.data.proposalId } : undefined} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

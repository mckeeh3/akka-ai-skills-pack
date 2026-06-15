import type { DecisionSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DecisionSurfaceProps = {
  envelope: SurfaceEnvelope<DecisionSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function DecisionSurface({ envelope, onAction }: DecisionSurfaceProps) {
  const evidence = envelope.data.evidence ?? [];
  const disabledById = new Map((envelope.data.disabledActions ?? []).map((action) => [action.actionId, action.reason]));
  const guardedActions = envelope.actions.map((action) => disabledById.has(action.actionId)
    ? { ...action, disabled: action.disabled ?? { reasonCode: 'BACKEND_PREREQUISITE_REQUIRED', message: disabledById.get(action.actionId)! } }
    : action);
  return (
    <SurfaceStateFrame envelope={envelope}>
      <article className="decision-card">
        <p>{envelope.data.recommendation}</p>
        <dl>
          <dt>Risk</dt><dd>{envelope.data.riskScore ?? envelope.data.risk ?? 'Not scored'}</dd>
          <dt>Confidence</dt><dd>{envelope.data.confidenceScore ?? 'Not scored'}</dd>
          {envelope.data.impact && <><dt>Impact</dt><dd>{envelope.data.impact}</dd></>}
          {envelope.data.affectedTarget && <><dt>Affected target</dt><dd>{envelope.data.affectedTarget}</dd></>}
          {envelope.data.policyBasis && <><dt>Policy basis</dt><dd>{envelope.data.policyBasis}</dd></>}
          {envelope.data.idempotencyKeySource && <><dt>Idempotency</dt><dd>{envelope.data.idempotencyKeySource}</dd></>}
        </dl>
        {envelope.data.activationBlocker && <p className="surface-state-inline forbidden" role="status">Activation blocker: {envelope.data.activationBlocker}</p>}
        {envelope.data.noDirectMutation && <p className="surface-state-inline forbidden">Advisory output cannot directly mutate prompts, skills, references, model refs, tool boundaries, activation, rollback, or provider configuration.</p>}
        {envelope.data.alternatives && envelope.data.alternatives.length > 0 && <section aria-label="Decision alternatives"><h4>Alternatives</h4><ul>{envelope.data.alternatives.map((item) => <li key={item}>{item}</li>)}</ul></section>}
        {evidence.length > 0 && (
          <>
            <h4>Evidence</h4>
            <ul>{evidence.map((item) => <li key={item.evidenceId}><strong>{item.label}</strong>: {item.summary}</li>)}</ul>
          </>
        )}
        {envelope.data.allowedActions && (
          <section aria-label="Backend-authorized investigation actions">
            <h4>Allowed actions</h4>
            <ul>{envelope.data.allowedActions.map((action) => <li key={action.actionId}>{action.label}</li>)}</ul>
            <details className="dashboard-evidence-drawer">
              <summary>Role-gated action diagnostics</summary>
              <ul>{envelope.data.allowedActions.map((action) => <li key={`${action.actionId}-diagnostics`}>{action.browserToolId} · {action.governedToolId} · {action.capabilityId}</li>)}</ul>
            </details>
          </section>
        )}
        {envelope.data.disabledActions && (
          <section aria-label="Disabled or deferred investigation actions">
            <h4>Unavailable actions</h4>
            <ul>{envelope.data.disabledActions.map((action) => <li key={action.actionId}>{action.label ?? 'Action unavailable'}: {action.reason}</li>)}</ul>
            <details className="dashboard-evidence-drawer"><summary>Role-gated disabled action diagnostics</summary><ul>{envelope.data.disabledActions.map((action) => <li key={`${action.actionId}-diagnostics`}>{action.actionId}</li>)}</ul></details>
          </section>
        )}
        {envelope.data.traceLinks && <section className="trace-link-list" aria-label="Investigation guidance trace links">{envelope.data.traceLinks.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-timeline#${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>}
      </article>
      <SurfaceActionBar actions={guardedActions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

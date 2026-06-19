import { Fragment, useState } from 'react';
import type { DecisionSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

function displayValue(value: unknown): string {
  if (value === null || value === undefined) return 'Not provided';
  if (Array.isArray(value)) return value.map(displayValue).join(', ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, nested]) => `${key}: ${displayValue(nested)}`).join('; ');
  return String(value);
}

function DecisionMetadataSection({ title, data }: { title: string; data?: Record<string, unknown> }) {
  if (!data) return null;
  return (
    <section aria-label={title}>
      <h4>{title}</h4>
      <dl>{Object.entries(data).map(([key, value]) => <Fragment key={key}><dt>{key}</dt><dd>{displayValue(value)}</dd></Fragment>)}</dl>
    </section>
  );
}

type DecisionSurfaceProps = {
  envelope: SurfaceEnvelope<DecisionSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function DecisionSurface({ envelope, onAction }: DecisionSurfaceProps) {
  const [reviewerReason, setReviewerReason] = useState('');
  const isImpactResult = envelope.surfaceId === 'surface-governance-policy-impact-analysis-result';
  const recommendation = typeof envelope.data.recommendation === 'string'
    ? envelope.data.recommendation
    : envelope.data.recommendation?.rationale ?? envelope.data.recommendation?.outcome;
  const evidence = envelope.data.evidence ?? [];
  const evidenceSummary = envelope.data.evidenceSummary ?? [];
  const recommendedPath = envelope.data.recommendedPath ?? [];
  const disabledById = new Map((envelope.data.disabledActions ?? []).map((action) => [action.actionId, action.reason]));
  const impactTaskId = typeof envelope.data.impactTaskId === 'string' ? envelope.data.impactTaskId : undefined;
  const dispositionNeedsReason = (actionId: string) => isImpactResult && (actionId === 'action-governance-policy-reject-impact-result' || actionId === 'action-governance-policy-request-impact-changes');
  const impactActionInput = isImpactResult
    ? { impactTaskId: impactTaskId ?? '', taskId: impactTaskId ?? '', proposalId: typeof envelope.data.proposalId === 'string' ? envelope.data.proposalId : '', reason: reviewerReason.trim(), reviewerAcknowledgement: reviewerReason.trim() || 'Reviewed advisory impact result; policy activation remains separate.' }
    : undefined;
  const guardedActions = envelope.actions.map((action) => {
    if (disabledById.has(action.actionId)) return { ...action, disabled: action.disabled ?? { reasonCode: 'BACKEND_PREREQUISITE_REQUIRED', message: disabledById.get(action.actionId)! } };
    if (isImpactResult && !impactTaskId && action.actionId !== 'action-governance-policy-read-impact-analysis') return { ...action, disabled: action.disabled ?? { reasonCode: 'IMPACT_TASK_REQUIRED', message: 'Open a completed impact-analysis task before recording result disposition.' } };
    if (dispositionNeedsReason(action.actionId) && !reviewerReason.trim()) return { ...action, disabled: action.disabled ?? { reasonCode: 'REVIEWER_REASON_REQUIRED', message: 'Enter a reviewer reason before rejecting or requesting changes.' } };
    return action;
  });
  return (
    <SurfaceStateFrame envelope={envelope}>
      <article className="decision-card">
        <p>{recommendation ?? envelope.data.activationBlocker ?? envelope.data.summary ?? 'Backend returned a governed decision state for review.'}</p>
        <dl>
          <dt>Risk</dt><dd>{envelope.data.riskScore ?? envelope.data.risk ?? 'Not scored'}</dd>
          <dt>Confidence</dt><dd>{envelope.data.confidenceScore ?? 'Not scored'}</dd>
          {envelope.data.impact && <><dt>Impact</dt><dd>{envelope.data.impact}</dd></>}
          {envelope.data.affectedTarget && <><dt>Affected target</dt><dd>{envelope.data.affectedTarget}</dd></>}
          {envelope.data.policyBasis && <><dt>Policy basis</dt><dd>{envelope.data.policyBasis}</dd></>}
          {envelope.data.idempotencyKeySource && <><dt>Idempotency</dt><dd>{envelope.data.idempotencyKeySource}</dd></>}
        </dl>
        {envelope.data.activationBlocker && <p className="surface-state-inline forbidden" role="status">Activation blocker: {envelope.data.activationBlocker}</p>}
        {isImpactResult && (
          <label className="surface-field-stack" htmlFor={`${envelope.surfaceId}-reviewer-reason`}>
            Reviewer reason or acknowledgement
            <textarea
              id={`${envelope.surfaceId}-reviewer-reason`}
              value={reviewerReason}
              onChange={(event) => setReviewerReason(event.target.value)}
              placeholder="Required before rejecting or requesting changes; included with governed disposition actions."
            />
          </label>
        )}
        {envelope.data.noDirectMutation && <p className="surface-state-inline forbidden">Advisory output cannot directly mutate prompts, skills, references, model refs, tool boundaries, activation, rollback, provider configuration, retained evidence, policy, authorization, or export delivery. Advisory output cannot directly mutate prompts, skills, references, model refs, tool boundaries, activation, rollback, or provider configuration.</p>}
        <DecisionMetadataSection title="Export request" data={envelope.data.exportRequest} />
        <DecisionMetadataSection title="Export scope" data={envelope.data.exportScope} />
        <DecisionMetadataSection title="Authorization basis" data={envelope.data.authorizationBasis} />
        {typeof envelope.data.policyDecision === 'object' && <DecisionMetadataSection title="Policy decision" data={envelope.data.policyDecision} />}
        <DecisionMetadataSection title="Bundle metadata" data={envelope.data.bundleMetadata} />
        <DecisionMetadataSection title="Approval" data={envelope.data.approval} />
        <DecisionMetadataSection title="Delivery" data={envelope.data.delivery} />
        {recommendedPath.length > 0 && (
          <section aria-label="Recommended investigation path">
            <h4>Recommended path</h4>
            <ol>{recommendedPath.map((step) => <li key={step.stepId}><strong>{step.label}</strong>{step.rationale ? ` — ${step.rationale}` : ''}{step.targetActionId ? <span className="trace-link-list"> Target: {step.targetActionId}</span> : null}</li>)}</ol>
          </section>
        )}
        {envelope.data.recovery && typeof envelope.data.recovery !== 'string' && envelope.data.recovery.steps && (
          <section aria-label="Safe recovery path"><h4>Safe recovery</h4><ul>{envelope.data.recovery.steps.map((step) => <li key={step}>{step}</li>)}</ul>{envelope.data.recovery.failClosed && <p>{envelope.data.recovery.failClosed}</p>}</section>
        )}
        {envelope.data.alternatives && envelope.data.alternatives.length > 0 && <section aria-label="Decision alternatives"><h4>Alternatives</h4><ul>{envelope.data.alternatives.map((item) => <li key={item}>{item}</li>)}</ul></section>}
        {evidence.length > 0 && (
          <>
            <h4>Evidence</h4>
            <ul>{evidence.map((item) => <li key={item.evidenceId}><strong>{item.label}</strong>: {item.summary}</li>)}</ul>
          </>
        )}
        {evidenceSummary.length > 0 && (
          <>
            <h4>Evidence summary</h4>
            <ul>{evidenceSummary.map((item) => <li key={item.evidenceId}><strong>{item.label}</strong>: {item.summary}</li>)}</ul>
          </>
        )}
        {envelope.data.allowedActions && (
          <section aria-label="Backend-authorized investigation actions">
            <h4>Allowed actions</h4>
            <ul>{envelope.data.allowedActions.map((action) => <li key={action.actionId}>{action.label}{action.reason ? ` — ${action.reason}` : ''}</li>)}</ul>
            <details className="dashboard-evidence-drawer">
              <summary>Role-gated action diagnostics</summary>
              <ul>{envelope.data.allowedActions.map((action) => <li key={`${action.actionId}-diagnostics`}>{action.browserToolId} · {action.governedToolId} · {action.capabilityId}</li>)}</ul>
            </details>
          </section>
        )}
        {envelope.data.disabledActions && (
          <section aria-label="Disabled or deferred investigation actions">
            <h4>Unavailable actions</h4>
            <ul>{envelope.data.disabledActions.map((action) => <li key={action.actionId}>{action.label ?? 'Action unavailable'}: {action.reason}{action.recovery ? ` Recovery: ${action.recovery}` : ''}</li>)}</ul>
            <details className="dashboard-evidence-drawer"><summary>Role-gated disabled action diagnostics</summary><ul>{envelope.data.disabledActions.map((action) => <li key={`${action.actionId}-diagnostics`}>{action.actionId}</li>)}</ul></details>
          </section>
        )}
        {envelope.data.traceLinks && <section className="trace-link-list" aria-label="Investigation guidance trace links">{envelope.data.traceLinks.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-timeline#${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>}
      </article>
      <SurfaceActionBar actions={guardedActions} surfaceId={envelope.surfaceId} actionInput={impactActionInput} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

import type { SurfaceAction, SurfaceEnvelope, WorkflowStatusSurfaceData } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type WorkflowStatusSurfaceProps = {
  envelope: SurfaceEnvelope<WorkflowStatusSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

function formatStatus(value: string) {
  return value.replace(/[-_]/g, ' ');
}

function evidenceKey(evidence: string | { refId: string }) {
  return typeof evidence === 'string' ? evidence : evidence.refId;
}

function evidenceLabel(evidence: string | { refId: string; label?: string }) {
  return typeof evidence === 'string' ? evidence : (evidence.label ?? evidence.refId);
}

function evidenceSummary(evidence: string | { summary?: string }) {
  return typeof evidence === 'string' ? undefined : evidence.summary;
}

function evidenceTraceId(evidence: string | { traceId?: string }) {
  return typeof evidence === 'string' ? undefined : evidence.traceId;
}

function recommendationKey(recommendation: string | { recommendationId?: string; label?: string }) {
  return typeof recommendation === 'string' ? recommendation : (recommendation.recommendationId ?? recommendation.label ?? 'recommendation');
}

function recommendationLabel(recommendation: string | { label?: string }) {
  return typeof recommendation === 'string' ? recommendation : (recommendation.label ?? 'Recommendation');
}

export function WorkflowStatusSurface({ envelope, onAction }: WorkflowStatusSurfaceProps) {
  const steps = envelope.data.steps ?? [];
  const statusText = formatStatus(envelope.data.status);
  const accessReview = envelope.data.accessReview;
  const progressSnapshots = Array.isArray(envelope.data.progress) ? envelope.data.progress : [];
  const progressSummary = !Array.isArray(envelope.data.progress) ? envelope.data.progress : undefined;
  const blockers = accessReview?.blockers ?? envelope.data.blockers ?? [];
  const evidenceRefs = accessReview?.evidenceRefs ?? envelope.data.evidenceRefs ?? [];
  const recommendations = accessReview?.recommendations ?? envelope.data.recommendations ?? [];
  const providerFailures = accessReview?.providerFailures ?? envelope.data.providerFailures ?? [];
  const taskTraceIds = accessReview?.traceIds ?? envelope.data.traceIds ?? [];
  return (
    <SurfaceStateFrame envelope={envelope}>
      <p role="status">Workflow {envelope.data.workflowId} is {statusText}.</p>
      {envelope.data.surfaceContract && <p className="form-status">Surface contract: {envelope.data.surfaceContract}</p>}
      {envelope.data.taskId && <p className="form-status">Task id: {envelope.data.taskId}</p>}
      {envelope.data.summary && <p className="surface-state-inline forbidden">{envelope.data.summary}</p>}
      {envelope.data.requiredCapabilityId && <p className="form-status">Required capability: {envelope.data.requiredCapabilityId}</p>}
      {envelope.data.initiatingCapabilityId && <p className="form-status">Initiating capability: {envelope.data.initiatingCapabilityId}</p>}
      {progressSummary && <p className="form-status">Progress: {progressSummary.percent ?? 0}% · {progressSummary.summary ?? 'No progress summary available'}</p>}
      {envelope.data.resultSummary && <p className="form-status">Result: {envelope.data.resultSummary}</p>}
      {accessReview && (
        <section className="access-review-task" aria-label="Access review task details">
          <h4>Access review task</h4>
          <p>Status: {formatStatus(accessReview.lifecycleState)} · review state: {formatStatus(accessReview.resultReviewState ?? envelope.data.resultReviewState ?? 'pending_worker_result')}</p>
          <p>Progress: {accessReview.progressPercent ?? 0}%</p>
          <p className="form-status">{accessReview.safety}</p>
          {accessReview.noDirectMutation && <p className="surface-state-inline forbidden">No direct mutation: worker output cannot directly change invitations, memberships, roles, capabilities, authorization state, or provider configuration.</p>}
        </section>
      )}
      {providerFailures.length > 0 && (
        <section className="provider-failure-list" aria-label="Provider/runtime failures">
          <h4>Provider/runtime blockers</h4>
          <ul>{providerFailures.map((failure) => <li key={failure}>{formatStatus(failure)}</li>)}</ul>
        </section>
      )}
      {blockers.length > 0 && (
        <section className="provider-failure-list" aria-label="Access review blockers">
          <h4>Blockers</h4>
          <ul>{blockers.map((blocker) => <li key={`${blocker.code}-${blocker.message}`}><strong>{formatStatus(blocker.code)}</strong>: {blocker.message}</li>)}</ul>
        </section>
      )}
      {evidenceRefs.length > 0 && (
        <section className="evidence-ref-list" aria-label="Access review evidence references">
          <h4>Evidence references</h4>
          <ul>{evidenceRefs.map((evidence) => <li key={evidenceKey(evidence)}><span>{evidenceLabel(evidence)}</span>{evidenceSummary(evidence) && <span> — {evidenceSummary(evidence)}</span>}{evidenceTraceId(evidence) && <a href={`/ui?surfaceId=surface-audit-timeline#${evidenceTraceId(evidence)}`}>trace</a>}</li>)}</ul>
        </section>
      )}
      {recommendations.length > 0 && (
        <section className="recommendation-list" aria-label="Access review recommendations">
          <h4>Recommendations</h4>
          <ul>{recommendations.map((recommendation) => <li key={recommendationKey(recommendation)}><strong>{recommendationLabel(recommendation)}</strong>{typeof recommendation !== 'string' && recommendation.risk && <span> · risk {recommendation.risk}</span>}{typeof recommendation !== 'string' && recommendation.confidence && <span> · confidence {recommendation.confidence}</span>}{typeof recommendation !== 'string' && recommendation.summary && <p>{recommendation.summary}</p>}</li>)}</ul>
        </section>
      )}
      {taskTraceIds.length > 0 && (
        <section className="trace-link-list" aria-label="Workflow trace references">
          {taskTraceIds.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-timeline#${traceId}`}>{traceId}</a>)}
        </section>
      )}
      {progressSnapshots.length > 0 && (
        <ol className="workflow-steps" aria-label="Access review progress snapshots">
          {progressSnapshots.map((snapshot) => <li key={snapshot.snapshotId} className={snapshot.status}><span>{snapshot.label}</span><span>{formatStatus(snapshot.status)}</span>{snapshot.traceId && <a href={`/ui?surfaceId=surface-audit-timeline#${snapshot.traceId}`}>trace</a>}</li>)}
        </ol>
      )}
      {steps.length > 0 ? (
        <ol className="workflow-steps">
          {steps.map((step) => <li key={step.stepId} className={step.status}><span>{step.label}</span><span>{formatStatus(step.status)}</span></li>)}
        </ol>
      ) : (
        <p>No workflow steps are available for this state.</p>
      )}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

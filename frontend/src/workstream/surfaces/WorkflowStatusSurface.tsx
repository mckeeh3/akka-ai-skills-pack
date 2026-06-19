import type { SurfaceAction, SurfaceEnvelope, WorkflowStatusSurfaceData } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type WorkflowStatusSurfaceProps = {
  envelope: SurfaceEnvelope<WorkflowStatusSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
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
  const traceLinks = accessReview?.traceLinks ?? envelope.data.modelToolDataPolicyUsage?.traceLinks ?? envelope.data.traceLinks ?? [];
  const isMyAccountDigest = envelope.surfaceId === 'surface-my-account-personal-attention-digest-progress';
  const isGovernancePolicyImpact = envelope.surfaceId === 'surface-governance-policy-impact-analysis-task' || envelope.data.surfaceContract === 'governance.policy.impact_analysis.task.v1';
  const isAgentAdminPromptRisk = envelope.surfaceId === 'surface-agent-admin-prompt-risk-review' || envelope.data.surfaceContract === 'agent_admin.prompt_risk_review_task.v1';
  return (
    <SurfaceStateFrame envelope={envelope}>
      {isAgentAdminPromptRisk && <AgentAdminPromptRiskReview data={envelope.data} />}
      {isMyAccountDigest && <MyAccountDigestProgress data={envelope.data} />}
      {!isMyAccountDigest && !isAgentAdminPromptRisk && <p role="status">Workflow {envelope.data.workflowId} is {statusText}.</p>}
      {isMyAccountDigest && <p role="status">Personal attention digest {envelope.data.digestTaskId ?? envelope.data.workflowId ?? 'request'} is {statusText}.</p>}
      {isUserAdminWorkflow(envelope) && <UserAdminWorkflowBranchReturn envelope={envelope} onAction={onAction} />}
      {envelope.data.summary && <p className="surface-state-inline forbidden">{envelope.data.summary}</p>}
      {(envelope.data.surfaceContract || envelope.data.taskId || envelope.data.digestTaskId || envelope.data.impactTaskId || envelope.data.autonomousAgentTaskId || envelope.data.requiredCapabilityId || envelope.data.initiatingCapabilityId) && (
        <details className="dashboard-evidence-drawer">
          <summary>Role-gated runtime diagnostics</summary>
          {envelope.data.surfaceContract && <p className="form-status">Surface contract: {envelope.data.surfaceContract}</p>}
          {(envelope.data.taskId || envelope.data.digestTaskId || envelope.data.impactTaskId || envelope.data.autonomousAgentTaskId) && <p className="form-status">Task id: {envelope.data.taskId ?? envelope.data.digestTaskId ?? envelope.data.impactTaskId ?? envelope.data.autonomousAgentTaskId}</p>}
          {envelope.data.requiredCapabilityId && <p className="form-status">Required capability: {envelope.data.requiredCapabilityId}</p>}
          {envelope.data.initiatingCapabilityId && <p className="form-status">Initiating capability: {envelope.data.initiatingCapabilityId}</p>}
        </details>
      )}
      {progressSummary && <p className="form-status">Progress: {progressSummary.percent ?? 0}% · {progressSummary.summary ?? 'No progress summary available'}</p>}
      {envelope.data.resultSummary && <p className="form-status">Result: {envelope.data.resultSummary}</p>}
      {isGovernancePolicyImpact && <GovernancePolicyImpactTask data={envelope.data} />}
      {accessReview && (
        <section className="access-review-task" aria-label="Access review task details">
          <h4>Access review task</h4>
          <p>Status: {formatStatus(accessReview.lifecycleState)} · review state: {formatStatus(accessReview.resultReviewState ?? envelope.data.resultReviewState ?? 'pending_worker_result')}</p>
          <p>Progress: {accessReview.progressPercent ?? 0}%</p>
          <p className="form-status">{accessReview.safety}</p>
          {accessReview.noDirectMutation && <p className="surface-state-inline forbidden">No direct mutation: worker output cannot directly change invitations, memberships, roles, capabilities, authorization state, or provider configuration.</p>}
        </section>
      )}
      {envelope.data.modelToolDataPolicyUsage && (
        <details className="access-review-trace-summary dashboard-evidence-drawer" aria-label="Model tool data policy usage summary">
          <summary>Model, tool, data, and policy usage diagnostics</summary>
          <dl>
            {envelope.data.modelToolDataPolicyUsage.model && <><dt>Model</dt><dd>{envelope.data.modelToolDataPolicyUsage.model}</dd></>}
            {envelope.data.modelToolDataPolicyUsage.tools && <><dt>Tools</dt><dd>{envelope.data.modelToolDataPolicyUsage.tools.join(' · ')}</dd></>}
            {envelope.data.modelToolDataPolicyUsage.data && <><dt>Data</dt><dd>{envelope.data.modelToolDataPolicyUsage.data}</dd></>}
            {envelope.data.modelToolDataPolicyUsage.policy && <><dt>Policy</dt><dd>{envelope.data.modelToolDataPolicyUsage.policy}</dd></>}
            {envelope.data.modelToolDataPolicyUsage.redaction && <><dt>Redaction</dt><dd>{envelope.data.modelToolDataPolicyUsage.redaction}</dd></>}
          </dl>
        </details>
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
      {traceLinks.length > 0 && (
        <section className="trace-link-list" aria-label="Access review model tool data policy trace links">
          <h4>Safe trace links</h4>
          {traceLinks.map((trace) => <a key={trace.traceId} href={`/ui?surfaceId=${encodeURIComponent(trace.targetSurfaceId ?? 'surface-audit-trace-detail')}&traceId=${encodeURIComponent(trace.traceId)}`}>{trace.label ?? trace.traceId}<span>{trace.summary ? ` — ${trace.summary}` : ''}</span></a>)}
        </section>
      )}
      {taskTraceIds.length > 0 && (
        <section className="trace-link-list" aria-label="Workflow trace references">
          {taskTraceIds.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-timeline#${traceId}`}>{traceId}</a>)}
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
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} actionInput={workflowActionInput(envelope.data)} onAction={onAction} />
    </SurfaceStateFrame>
  );
}


function AgentAdminPromptRiskReview({ data }: { data: WorkflowStatusSurfaceData }) {
  const anyData = data as WorkflowStatusSurfaceData & Record<string, unknown>;
  const findings = Array.isArray(anyData.findings) ? anyData.findings as Array<Record<string, unknown>> : [];
  const requiredReasons = Array.isArray(anyData.requiredHumanReviewReasons) ? anyData.requiredHumanReviewReasons as string[] : [];
  const artifactDeltas = Array.isArray(anyData.artifactDeltas) ? anyData.artifactDeltas as Array<Record<string, unknown>> : [];
  return (
    <section className="agent-admin-prompt-risk-review" aria-label="Agent Admin prompt-risk advisory review">
      <div className="surface-section-heading">
        <div><p className="eyebrow">Autonomous advisory review</p><h3>Prompt-risk review requires human decision</h3></div>
        <p>{data.summary ?? 'Model-backed advisory analysis completed; it cannot activate or mutate behavior artifacts.'}</p>
      </div>
      <dl className="authority-summary-grid">
        <div><dt>Status</dt><dd>{formatStatus(data.status)}</dd></div>
        <div><dt>Overall risk</dt><dd>{formatStatus(String(anyData.overallRisk ?? 'not scored'))}</dd></div>
        <div><dt>Review state</dt><dd>{formatStatus(data.resultReviewState ?? 'completed_review_required')}</dd></div>
        <div><dt>Direct mutation</dt><dd>{data.noDirectMutation ? 'Not allowed' : 'Not reported'}</dd></div>
      </dl>
      {Boolean(anyData.riskSummary) && <p className="surface-state-inline forbidden">{String(anyData.riskSummary)}</p>}
      {requiredReasons.length > 0 && <section className="provider-failure-list" aria-label="Required human review reasons"><h4>Why human review is required</h4><ul>{requiredReasons.map((reason) => <li key={reason}>{formatStatus(reason)}</li>)}</ul></section>}
      {artifactDeltas.length > 0 && <section className="evidence-ref-list" aria-label="Redacted artifact deltas"><h4>Redacted artifact deltas</h4><ul>{artifactDeltas.map((delta, index) => <li key={String(delta.artifactId ?? index)}><strong>{formatStatus(String(delta.artifactKind ?? 'artifact'))}</strong> — {String(delta.changeSummary ?? 'Change summary redacted')}</li>)}</ul></section>}
      {findings.length > 0 && <section className="recommendation-list" aria-label="Prompt-risk findings"><h4>Findings</h4><ul>{findings.map((finding, index) => <li key={String(finding.findingId ?? index)}><strong>{formatStatus(String(finding.riskLevel ?? 'risk'))}: {String(finding.category ?? 'Finding')}</strong><p>{String(finding.browserSafeDescription ?? 'Browser-safe finding summary unavailable.')}</p>{finding.requiresHumanReview === true && <span className="status-pill warning">human review required</span>}</li>)}</ul></section>}
      {data.safety && <p className="surface-state-inline forbidden">{data.safety}</p>}
    </section>
  );
}

function workflowActionInput(data: WorkflowStatusSurfaceData): Record<string, string> | undefined {
  if (data.digestTaskId) return { digestTaskId: data.digestTaskId };
  if (data.impactTaskId) {
    return {
      impactTaskId: data.impactTaskId,
      taskId: data.impactTaskId,
      ...(data.proposalId ? { proposalId: data.proposalId } : {})
    };
  }
  return undefined;
}

function GovernancePolicyImpactTask({ data }: { data: WorkflowStatusSurfaceData }) {
  return (
    <section className="access-review-task" aria-label="Governance Policy impact-analysis task details">
      <h4>Governance/Policy impact analysis</h4>
      <p>Status: {formatStatus(data.status)} · proposal: {data.proposalId ?? 'not selected'}</p>
      {data.activationBlocked || data.activationBlockedUntilHumanDecision ? <p className="surface-state-inline forbidden">Activation remains blocked until backend-governed human review and policy decision paths complete.</p> : null}
      {data.noDirectMutation && <p className="surface-state-inline forbidden">No direct mutation: this task cannot approve, activate, roll back, weaken policy, expand scope, or fabricate evidence.</p>}
      {data.noFakeSuccess && <p className="form-status">Provider/runtime status is fail-closed; no model-less successful impact analysis is shown.</p>}
      {data.readiness && <p className="form-status">Readiness: {renderWorkflowValue(data.readiness)}</p>}
      {data.disabledActions && data.disabledActions.length > 0 && <p className="form-status">Disabled actions: {renderWorkflowValue(data.disabledActions)}</p>}
      {data.redaction && <p className="form-status">Redaction: {data.redaction}</p>}
    </section>
  );
}

function renderWorkflowValue(value: unknown): string {
  if (value == null) return 'n/a';
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderWorkflowValue).join(' · ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderWorkflowValue(entry)}`).join(' · ');
  return String(value);
}

function UserAdminWorkflowBranchReturn({ envelope, onAction }: { envelope: SurfaceEnvelope<WorkflowStatusSurfaceData>; onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void }) {
  const action = envelope.actions.find((candidate) => candidate.actionId === 'action-user-admin-show-users') ?? envelope.actions.find((candidate) => candidate.actionId === 'action-display-user-list');
  if (!action) return null;
  const branch = envelope.data.branchNavigation;
  return (
    <nav className="user-admin-branch-return" aria-label="User Admin branch navigation">
      <button type="button" className="surface-action-link secondary" onClick={() => onAction?.(action, envelope.surfaceId, {
        branchRootSurfaceId: branch?.branchRootSurfaceId ?? envelope.data.branchRootSurfaceId ?? 'surface-user-admin-users',
        branchReturnActionId: branch?.branchReturnActionId ?? envelope.data.branchReturnActionId ?? 'action-user-admin-show-users',
        safeFilterPreservation: branch?.safeFilterPreservation ?? envelope.data.safeFilterPreservation ?? 'backend-authored-only',
        correlationId: branch?.correlationId ?? envelope.correlationId
      })}>{branch?.branchReturnLabel ?? envelope.data.branchReturnLabel ?? action.label}</button>
      <p className="capability-basis">{branch?.capabilityId ?? action.capabilityId} · safe filters: {branch?.safeFilterPreservation ?? envelope.data.safeFilterPreservation ?? 'backend-authored-only'}</p>
    </nav>
  );
}

function isUserAdminWorkflow(envelope: SurfaceEnvelope<WorkflowStatusSurfaceData>) {
  return envelope.ownerFunctionalAgentId === 'user-admin-agent' || envelope.surfaceId.startsWith('surface-user-admin-') || envelope.data.surfaceContract?.startsWith('user_admin.');
}

function MyAccountDigestProgress({ data }: { data: WorkflowStatusSurfaceData }) {
  return (
    <section className="my-account-digest-progress" aria-label="Personal attention digest progress">
      <div>
        <p className="eyebrow">Autonomous personal briefing</p>
        <h3>Digest/export progress is advisory and source attention remains authoritative.</h3>
        <p>{data.summary ?? 'Start a backend-governed digest to summarize authorized personal attention evidence.'}</p>
      </div>
      <dl className="authority-summary-grid">
        <div><dt>Status</dt><dd>{formatStatus(data.status)}</dd></div>
        <div><dt>Phase</dt><dd>{formatStatus(data.phase ?? data.status)}</dd></div>
        <div><dt>Authorized items</dt><dd>{data.authorizedAttentionCount ?? 0}</dd></div>
        <div><dt>Direct mutation</dt><dd>{data.noDirectMutation ? 'Not allowed' : 'Not reported'}</dd></div>
        <div><dt>Redaction</dt><dd>{data.redaction ?? 'Hidden workstreams/items are not counted or named'}</dd></div>
      </dl>
      {data.progressEvents && data.progressEvents.length > 0 && <ol className="workflow-steps" aria-label="Personal digest progress events">{data.progressEvents.map((event, index) => {
        const key = typeof event === 'string' ? event : (event.eventId ?? event.label ?? `event-${index}`);
        const label = typeof event === 'string' ? event : (event.label ?? event.eventId ?? 'Digest progress event');
        const status = typeof event === 'string' ? data.status : (event.status ?? data.status);
        return <li key={`${key}-${index}`} className={status}><span>{formatStatus(label)}</span><span>{formatStatus(status)}</span>{typeof event !== 'string' && event.summary && <small>{event.summary}</small>}</li>;
      })}</ol>}
      {data.safety && <p className="surface-state-inline forbidden">{data.safety}</p>}
    </section>
  );
}

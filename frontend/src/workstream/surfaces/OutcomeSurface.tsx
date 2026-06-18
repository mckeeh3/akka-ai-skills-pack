import { useState } from 'react';
import type { OutcomeSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type OutcomeSurfaceProps = {
  envelope: SurfaceEnvelope<OutcomeSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function OutcomeSurface({ envelope, onAction }: OutcomeSurfaceProps) {
  const isMyAccountDigest = envelope.surfaceId === 'surface-my-account-personal-attention-digest-result' || envelope.data.surfaceContract === 'my_account.personal_attention_digest.result.v1';
  const isGovernancePolicyOutcome = envelope.surfaceId === 'surface-governance-policy-outcome' || envelope.data.surfaceContract === 'governance.policy.outcome.v1';
  const actionInput: Record<string, string> = envelope.data.digestTaskId
    ? { digestTaskId: envelope.data.digestTaskId }
    : envelope.data.proposalId
      ? { proposalId: envelope.data.proposalId, targetSurfaceId: envelope.surfaceId }
      : { targetSurfaceId: envelope.surfaceId };
  return (
    <SurfaceStateFrame envelope={envelope}>
      {isMyAccountDigest ? <MyAccountDigestResult data={envelope.data} /> : isGovernancePolicyOutcome ? <GovernancePolicyOutcomePanel envelope={envelope} onAction={onAction} /> : <MetricsOutcome data={envelope.data} />}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} actionInput={actionInput} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

function MetricsOutcome({ data }: { data: OutcomeSurfaceData }) {
  const metrics = data.metrics ?? [];
  return (
    <div className="outcome-metrics">
      {metrics.map((metric) => (
        <article key={metric.metricId} className="ds-card outcome-metric">
          <h4>{metric.label}</h4>
          <p>{metric.current}{metric.unit ? ` ${metric.unit}` : ''} / target {metric.target}{metric.unit ? ` ${metric.unit}` : ''}</p>
          <progress value={metric.current} max={metric.target}>{metric.current} of {metric.target}</progress>
        </article>
      ))}
    </div>
  );
}

function GovernancePolicyOutcomePanel({ envelope, onAction }: OutcomeSurfaceProps) {
  const data = envelope.data;
  const [note, setNote] = useState('');
  const outcomeAction = envelope.actions.find((action) => action.actionId === 'action-governance-policy-outcome-note');
  const canSubmitNote = Boolean(outcomeAction && data.proposalId && data.noteForm?.editable !== false);
  const noteHistory = data.noteHistory ?? [];
  return (
    <section className="governance-policy-outcome-panel" aria-label="Governance Policy outcome panel">
      <div className="outcome-briefing-hero">
        <p className="eyebrow">Governance/Policy outcome evidence</p>
        <h3>{data.proposalTitle ?? data.proposalDisplayRef ?? 'Governance outcome'}</h3>
        <p>{data.summary ?? 'Outcome evidence is backend-authored for the selected AuthContext.'}</p>
      </div>
      <dl className="authority-summary-grid" aria-label="Outcome authority and safety">
        <div><dt>Status</dt><dd>{formatStatus(data.status ?? data.decisionState ?? 'ready')}</dd></div>
        <div><dt>Proposal</dt><dd>{data.proposalDisplayRef ?? data.proposalId ?? 'No visible proposal selected'}</dd></div>
        <div><dt>Direct mutation</dt><dd>{data.noDirectMutation ? 'Not allowed' : 'Not reported'}</dd></div>
        <div><dt>Provider/runtime</dt><dd>{renderSurfaceValue(data.readiness?.providerRuntime ?? data.readiness?.impactAnalysis ?? 'not reported')}</dd></div>
      </dl>
      {data.outcomeSummary && <p className="capability-basis">Outcome summary: {renderSurfaceValue(data.outcomeSummary)}</p>}
      <MetricsOutcome data={data} />
      {data.recommendations && data.recommendations.length > 0 && (
        <section className="recommendation-list" aria-label="Governance outcome recommendations">
          <h4>Recommendations</h4>
          <ul>{data.recommendations.map((recommendation, index) => <li key={recommendationKey(recommendation, index)}><strong>{recommendationLabel(recommendation)}</strong>{typeof recommendation !== 'string' && recommendation.summary && <p>{recommendation.summary}</p>}</li>)}</ul>
        </section>
      )}
      {data.evidenceRefs && data.evidenceRefs.length > 0 && (
        <section className="evidence-ref-list" aria-label="Governance outcome evidence">
          <h4>Evidence and trace links</h4>
          <ul>{data.evidenceRefs.map((evidence, index) => <li key={evidenceKey(evidence, index)}><strong>{evidenceLabel(evidence)}</strong>{typeof evidence !== 'string' && evidence.summary && <span> — {evidence.summary}</span>}{typeof evidence !== 'string' && evidence.traceId && <a href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(evidence.traceId)}`}>trace</a>}</li>)}</ul>
        </section>
      )}
      <section className="surface-section-card" aria-label="Governed outcome note">
        <h4>Record outcome note</h4>
        <p>Outcome notes append governed feedback only. They cannot approve, activate, roll back, weaken policy, mutate metrics directly, or fabricate advisory evidence.</p>
        <textarea aria-label="Outcome note" value={note} onChange={(event) => setNote(event.currentTarget.value)} placeholder="Observation or recommendation" disabled={!canSubmitNote} />
        <button type="button" disabled={!canSubmitNote || note.trim().length === 0} onClick={() => outcomeAction && onAction?.(outcomeAction, envelope.surfaceId, { proposalId: data.proposalId ?? '', note: note.trim(), targetSurfaceId: envelope.surfaceId })}>Submit governed outcome note</button>
        {Boolean(data.noteForm?.validationMessages) && <p className="form-status">Backend validation: {renderSurfaceValue(data.noteForm?.validationMessages)}</p>}
      </section>
      {noteHistory.length > 0 && <section className="evidence-ref-list" aria-label="Outcome note history"><h4>Note history</h4><ul>{noteHistory.map((entry, index) => <li key={`${entry}-${index}`}>{entry}</li>)}</ul></section>}
      {data.disabledActions && data.disabledActions.length > 0 && <p className="form-status">Disabled action reasons: {renderSurfaceValue(data.disabledActions)}</p>}
      {data.redaction && <p className="capability-basis">Browser-safe redaction: {renderSurfaceValue(data.redaction)}</p>}
      {data.traceRefs && data.traceRefs.length > 0 && <section className="trace-link-list" aria-label="Governance outcome trace links">{data.traceRefs.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>}
    </section>
  );
}

function MyAccountDigestResult({ data }: { data: OutcomeSurfaceData }) {
  const evidenceRefs = data.evidenceRefs ?? [];
  const materialEvents = data.materialEvents ?? [];
  const recommendations = data.recommendations ?? [];
  const sectionRefs = data.sectionRefs ?? [];
  return (
    <section className="my-account-digest-result" aria-label="Personal attention digest advisory result">
      <div className="outcome-briefing-hero">
        <p className="eyebrow">Advisory personal briefing</p>
        <h3>{data.summary ?? 'Personal attention digest result is ready for review.'}</h3>
        <p>{data.safety ?? 'This digest is advisory. Source attention remains authoritative and source lifecycle changes require separate governed capabilities.'}</p>
      </div>
      <dl className="authority-summary-grid" aria-label="Digest result authority and redaction">
        <div><dt>Status</dt><dd>{formatStatus(data.status ?? data.decisionState ?? 'review-required')}</dd></div>
        <div><dt>Authorized items</dt><dd>{data.authorizedAttentionCount ?? 0}</dd></div>
        <div><dt>Direct mutation</dt><dd>{data.noDirectMutation ? 'Not allowed' : 'Not reported'}</dd></div>
        <div><dt>Redaction</dt><dd>{data.redaction ?? 'Hidden workstreams/items omitted'}</dd></div>
      </dl>
      {sectionRefs.length > 0 && (
        <section className="surface-section-list" aria-label="Digest sections">
          {sectionRefs.map((section) => <article key={section} className="surface-section-card"><h4>{formatStatus(section)}</h4><p>Included in the advisory digest output.</p></article>)}
        </section>
      )}
      {recommendations.length > 0 && (
        <section className="recommendation-list" aria-label="Digest recommendations">
          <h4>Recommendations</h4>
          <ul>{recommendations.map((recommendation, index) => <li key={recommendationKey(recommendation, index)}><strong>{recommendationLabel(recommendation)}</strong>{typeof recommendation !== 'string' && recommendation.summary && <p>{recommendation.summary}</p>}</li>)}</ul>
        </section>
      )}
      {materialEvents.length > 0 && (
        <section className="evidence-ref-list" aria-label="Digest material events">
          <h4>Material events</h4>
          <ul>{materialEvents.map((event, index) => <li key={evidenceKey(event, index)}><strong>{evidenceLabel(event)}</strong>{typeof event !== 'string' && event.summary && <span> — {event.summary}</span>}</li>)}</ul>
        </section>
      )}
      {data.omissions && <p className="form-status">Omissions/redaction: {renderSurfaceValue(data.omissions)}</p>}
      {data.authorizedSourceCounts && <p className="form-status">Authorized source counts: {renderSurfaceValue(data.authorizedSourceCounts)}</p>}
      {evidenceRefs.length > 0 && (
        <section className="evidence-ref-list" aria-label="Digest evidence references">
          <h4>Evidence and source references</h4>
          <ul>
            {evidenceRefs.map((evidence, index) => {
              const key = evidenceKey(evidence, index);
              const label = evidenceLabel(evidence);
              const summary = typeof evidence === 'string' ? undefined : evidence.summary;
              const traceId = typeof evidence === 'string' ? undefined : evidence.traceId;
              return <li key={key}><strong>{label}</strong>{summary && <span> — {summary}</span>}{traceId && <a href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(traceId)}`}>trace</a>}</li>;
            })}
          </ul>
        </section>
      )}
      {data.traceRefs && data.traceRefs.length > 0 && <section className="trace-link-list" aria-label="Digest trace links">{data.traceRefs.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-trace-detail&traceId=${encodeURIComponent(traceId)}`}>{traceId}</a>)}</section>}
    </section>
  );
}

function evidenceKey(evidence: string | { refId?: string; label?: string }, index: number) {
  return typeof evidence === 'string' ? evidence : (evidence.refId ?? evidence.label ?? `evidence-${index}`);
}

function evidenceLabel(evidence: string | { refId?: string; label?: string }) {
  return typeof evidence === 'string' ? evidence : (evidence.label ?? evidence.refId ?? 'Evidence');
}

function recommendationKey(recommendation: string | { recommendationId?: string; label?: string }, index: number) {
  return typeof recommendation === 'string' ? recommendation : (recommendation.recommendationId ?? recommendation.label ?? `recommendation-${index}`);
}

function recommendationLabel(recommendation: string | { label?: string }) {
  return typeof recommendation === 'string' ? recommendation : (recommendation.label ?? 'Recommendation');
}

function renderSurfaceValue(value: unknown): string {
  if (value == null) return 'not reported';
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') return String(value);
  if (Array.isArray(value)) return value.map(renderSurfaceValue).join(' · ');
  if (typeof value === 'object') return Object.entries(value as Record<string, unknown>).map(([key, entry]) => `${key}: ${renderSurfaceValue(entry)}`).join(' · ');
  return String(value);
}

function formatStatus(value: string) {
  return value.replace(/[-_]/g, ' ');
}

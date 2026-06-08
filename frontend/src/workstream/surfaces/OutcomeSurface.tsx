import type { OutcomeSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type OutcomeSurfaceProps = {
  envelope: SurfaceEnvelope<OutcomeSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function OutcomeSurface({ envelope, onAction }: OutcomeSurfaceProps) {
  const isMyAccountDigest = envelope.surfaceId === 'surface-my-account-personal-attention-digest-result' || envelope.data.surfaceContract === 'my_account.personal_attention_digest.result.v1';
  return (
    <SurfaceStateFrame envelope={envelope}>
      {isMyAccountDigest ? <MyAccountDigestResult data={envelope.data} /> : <MetricsOutcome data={envelope.data} />}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
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

function MyAccountDigestResult({ data }: { data: OutcomeSurfaceData }) {
  const evidenceRefs = data.evidenceRefs ?? [];
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
      {evidenceRefs.length > 0 && (
        <section className="evidence-ref-list" aria-label="Digest evidence references">
          <h4>Evidence and source references</h4>
          <ul>
            {evidenceRefs.map((evidence, index) => {
              const key = typeof evidence === 'string' ? evidence : (evidence.refId ?? evidence.label ?? `evidence-${index}`);
              const label = typeof evidence === 'string' ? evidence : (evidence.label ?? evidence.refId ?? 'Evidence');
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

function formatStatus(value: string) {
  return value.replace(/[-_]/g, ' ');
}

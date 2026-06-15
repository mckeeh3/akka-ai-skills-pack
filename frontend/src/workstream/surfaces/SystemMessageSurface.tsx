import type { SurfaceAction, SurfaceEnvelope, SystemMessageData } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type SystemMessageSurfaceProps = {
  envelope: SurfaceEnvelope<SystemMessageData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

const severityTone: Record<string, string> = {
  info: 'info',
  warning: 'warning',
  error: 'danger',
  critical: 'danger',
  blocked: 'danger',
  blocked_provider_or_runtime: 'danger'
};

function traceHref(traceId: string) {
  return `/ui?traceId=${encodeURIComponent(traceId)}`;
}

function boundaryCopy(envelope: SurfaceEnvelope<SystemMessageData>): string {
  if (envelope.surfaceId.startsWith('surface-my-account')) {
    return 'My Account recovery is browser-safe: hidden workstreams, hidden contexts, source details, provider secrets, and unauthorized tenant/customer evidence are not enumerated.';
  }
  if (envelope.ownerFunctionalAgentId === 'agent-admin-agent' || envelope.surfaceId.startsWith('surface-agent-') || envelope.data.producingAgentId === 'agent-admin-agent') {
    return 'AgentAdminAgent guidance is read-only: no direct mutation of prompts, skills, references, manifests, model refs, tool boundaries, activation, rollback, provider configuration, or authorization state occurred.';
  }
  return 'UserAdminAgent guidance is read-only: no direct mutation of invitations, memberships, roles, capabilities, authorization state, or provider configuration occurred.';
}

export function SystemMessageSurface({ envelope, onAction }: SystemMessageSurfaceProps) {
  const data = envelope.data;
  const traceIds = data.trace?.traceIds?.length ? data.trace.traceIds : data.traceRefs?.length ? data.traceRefs : envelope.traceIds;
  const sourceRefs = data.sourceRefs?.filter((ref) => ref.refType === 'trace') ?? [];
  const recoverySteps = data.recoverySteps?.length ? data.recoverySteps : ['Retry after backend configuration or authorization is restored.'];
  const severity = data.severity ?? 'warning';
  const tone = severityTone[severity] ?? 'warning';

  return (
    <SurfaceStateFrame envelope={envelope}>
      <article className={`system-message-surface ${data.status ?? 'blocked_provider_or_runtime'}`} role="alert" aria-label={data.title ?? envelope.title}>
        <div className="system-message-summary">
          <span className={`status-pill ${tone}`}>{data.status ?? 'system_message'}</span>
          <span className="status-pill neutral">{data.capabilityId ?? 'capability unavailable'}</span>
        </div>
        <p>{data.message ?? data.summary ?? 'The workstream response was blocked safely.'}</p>
        {(data.safeReasonCode || data.blockerCode || data.surfaceContract) && <dl className="system-message-boundary-grid"><div><dt>Contract</dt><dd>{data.surfaceContract ?? envelope.surfaceVersion}</dd></div><div><dt>Reason</dt><dd>{data.safeReasonCode ?? data.blockerCode ?? data.status}</dd></div><div><dt>Redaction</dt><dd>{data.redaction ?? 'Unauthorized target details are not enumerated.'}</dd></div></dl>}
        <section aria-label="Recovery steps">
          <h4>Recovery steps</h4>
          <ol>
            {recoverySteps.map((step) => <li key={step}>{step}</li>)}
          </ol>
        </section>
        {traceIds.length > 0 && (
          <details className="system-message-traces">
            <summary>Role-gated trace details</summary>
            <ul aria-label="Trace links">
              {traceIds.map((traceId, index) => <li key={traceId}><a href={traceHref(traceId)}>Trace reference {index + 1}</a></li>)}
            </ul>
          </details>
        )}
        {sourceRefs.length > 0 && (
          <section aria-label="Source references" className="system-message-source-refs">
            <h4>Source references</h4>
            <ul>
              {sourceRefs.map((ref) => <li key={ref.refId}>{ref.label}: <a href={traceHref(ref.refId)}>{ref.refId}</a></li>)}
            </ul>
          </section>
        )}
        <p className="system-message-redaction">{data.safety?.redactionNote ?? 'Provider secrets, raw JWTs, hidden prompts, invitation tokens, and unauthorized tenant/customer evidence are not shown.'}</p>
        <p className="system-message-boundary">{boundaryCopy(envelope)}</p>
        <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
      </article>
    </SurfaceStateFrame>
  );
}

import type { SurfaceAction, SurfaceEnvelope, SystemMessageData } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type SystemMessageSurfaceProps = {
  envelope: SurfaceEnvelope<SystemMessageData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

const severityTone: Record<string, string> = {
  info: 'info',
  warning: 'warning',
  error: 'danger',
  critical: 'danger'
};

function traceHref(traceId: string) {
  return `/ui?traceId=${encodeURIComponent(traceId)}`;
}

export function SystemMessageSurface({ envelope, onAction }: SystemMessageSurfaceProps) {
  const data = envelope.data;
  const traceIds = data.trace?.traceIds?.length ? data.trace.traceIds : envelope.traceIds;
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
        <section aria-label="Recovery steps">
          <h4>Recovery steps</h4>
          <ol>
            {recoverySteps.map((step) => <li key={step}>{step}</li>)}
          </ol>
        </section>
        {traceIds.length > 0 && (
          <section aria-label="Trace links" className="system-message-traces">
            <h4>Trace links</h4>
            <ul>
              {traceIds.map((traceId) => <li key={traceId}><a href={traceHref(traceId)}>{traceId}</a></li>)}
            </ul>
          </section>
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
        <p className="system-message-boundary">UserAdminAgent guidance is read-only: no direct mutation of invitations, memberships, roles, capabilities, authorization state, or provider configuration occurred.</p>
        <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
      </article>
    </SurfaceStateFrame>
  );
}

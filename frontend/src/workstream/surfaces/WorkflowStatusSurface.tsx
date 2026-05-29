import type { SurfaceAction, SurfaceEnvelope, WorkflowStatusSurfaceData } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type WorkflowStatusSurfaceProps = {
  envelope: SurfaceEnvelope<WorkflowStatusSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function WorkflowStatusSurface({ envelope, onAction }: WorkflowStatusSurfaceProps) {
  const steps = envelope.data.steps ?? [];
  const statusText = envelope.data.status.replace(/[-_]/g, ' ');
  return (
    <SurfaceStateFrame envelope={envelope}>
      <p role="status">Workflow {envelope.data.workflowId} is {statusText}.</p>
      {envelope.data.summary && <p className="surface-state-inline forbidden">{envelope.data.summary}</p>}
      {envelope.data.requiredCapabilityId && <p className="form-status">Required capability: {envelope.data.requiredCapabilityId}</p>}
      {envelope.data.traceIds && (
        <section className="trace-link-list" aria-label="Workflow trace references">
          {envelope.data.traceIds.map((traceId) => <a key={traceId} href={`/ui?surfaceId=surface-audit-timeline#${traceId}`}>{traceId}</a>)}
        </section>
      )}
      {steps.length > 0 ? (
        <ol className="workflow-steps">
          {steps.map((step) => <li key={step.stepId} className={step.status}><span>{step.label}</span><span>{step.status.replace(/[-_]/g, ' ')}</span></li>)}
        </ol>
      ) : (
        <p>No workflow steps are available for this state.</p>
      )}
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

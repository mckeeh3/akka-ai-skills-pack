import type { SurfaceAction, SurfaceEnvelope, WorkflowStatusSurfaceData } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type WorkflowStatusSurfaceProps = {
  envelope: SurfaceEnvelope<WorkflowStatusSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function WorkflowStatusSurface({ envelope, onAction }: WorkflowStatusSurfaceProps) {
  return (
    <SurfaceStateFrame envelope={envelope}>
      <p role="status">Workflow {envelope.data.workflowId} is {envelope.data.status.replace(/-/g, ' ')}.</p>
      <ol className="workflow-steps">
        {envelope.data.steps.map((step) => <li key={step.stepId} className={step.status}><span>{step.label}</span><span>{step.status.replace(/-/g, ' ')}</span></li>)}
      </ol>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

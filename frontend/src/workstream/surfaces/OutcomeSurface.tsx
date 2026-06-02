import type { OutcomeSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type OutcomeSurfaceProps = {
  envelope: SurfaceEnvelope<OutcomeSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function OutcomeSurface({ envelope, onAction }: OutcomeSurfaceProps) {
  return (
    <SurfaceStateFrame envelope={envelope}>
      <div className="outcome-metrics">
        {envelope.data.metrics.map((metric) => (
          <article key={metric.metricId} className="ds-card outcome-metric">
            <h4>{metric.label}</h4>
            <p>{metric.current}{metric.unit ? ` ${metric.unit}` : ''} / target {metric.target}{metric.unit ? ` ${metric.unit}` : ''}</p>
            <progress value={metric.current} max={metric.target}>{metric.current} of {metric.target}</progress>
          </article>
        ))}
      </div>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

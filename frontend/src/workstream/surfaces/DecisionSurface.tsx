import type { DecisionSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DecisionSurfaceProps = {
  envelope: SurfaceEnvelope<DecisionSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function DecisionSurface({ envelope, onAction }: DecisionSurfaceProps) {
  return (
    <SurfaceStateFrame envelope={envelope}>
      <article className="decision-card">
        <p>{envelope.data.recommendation}</p>
        <dl>
          <dt>Risk</dt><dd>{envelope.data.riskScore ?? 'Not scored'}</dd>
          <dt>Confidence</dt><dd>{envelope.data.confidenceScore ?? 'Not scored'}</dd>
        </dl>
        <h4>Evidence</h4>
        <ul>{envelope.data.evidence.map((item) => <li key={item.evidenceId}><strong>{item.label}</strong>: {item.summary}</li>)}</ul>
      </article>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

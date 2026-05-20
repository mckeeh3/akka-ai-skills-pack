import type { DashboardSurfaceData, SurfaceAction, SurfaceEnvelope } from '../types';
import { SurfaceActionBar } from './SurfaceActionBar';
import { SurfaceStateFrame } from './SurfaceStateFrame';

type DashboardSurfaceProps = {
  envelope: SurfaceEnvelope<DashboardSurfaceData>;
  onAction?: (action: SurfaceAction, surfaceId: string) => void;
};

export function DashboardSurface({ envelope, onAction }: DashboardSurfaceProps) {
  return (
    <SurfaceStateFrame envelope={envelope}>
      <div className="surface-dashboard-grid">
        {envelope.data.cards.map((card) => (
          <article key={card.cardId} className={`ds-card dashboard-card ${card.severity ?? 'info'}`}>
            <p>{card.label}</p>
            <strong>{card.value}</strong>
          </article>
        ))}
      </div>
      <SurfaceActionBar actions={envelope.actions} surfaceId={envelope.surfaceId} onAction={onAction} />
    </SurfaceStateFrame>
  );
}

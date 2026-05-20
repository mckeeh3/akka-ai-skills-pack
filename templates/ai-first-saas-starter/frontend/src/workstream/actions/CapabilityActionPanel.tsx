import type { CapabilityActionRequest, CapabilityActionResult, SurfaceAction } from '../types';
import { CapabilityActionButton } from './CapabilityActionButton';
import { mapCapabilityActionResult } from './capabilityActionState';

type CapabilityActionPanelProps = {
  actions: SurfaceAction[];
  surfaceId: string;
  selectedContextId: string;
  surfaceCorrelationId: string;
  submittingActionId?: string;
  onSubmit?: (request: CapabilityActionRequest, action: SurfaceAction) => Promise<CapabilityActionResult | void> | CapabilityActionResult | void;
  onResultSurface?: (result: ReturnType<typeof mapCapabilityActionResult>, action: SurfaceAction) => void;
};

export function CapabilityActionPanel({ actions, surfaceId, selectedContextId, surfaceCorrelationId, submittingActionId, onSubmit, onResultSurface }: CapabilityActionPanelProps) {
  if (actions.length === 0) {
    return <p className="capability-action-panel empty">No capability actions are currently available.</p>;
  }

  return (
    <section className="capability-action-panel" aria-label="Capability-backed actions">
      {actions.map((action) => (
        <CapabilityActionButton
          key={action.actionId}
          action={action}
          surfaceId={surfaceId}
          selectedContextId={selectedContextId}
          surfaceCorrelationId={surfaceCorrelationId}
          submitting={submittingActionId === action.actionId}
          onSubmit={onSubmit}
          onResult={(result) => onResultSurface?.(mapCapabilityActionResult(action, result), action)}
        />
      ))}
    </section>
  );
}

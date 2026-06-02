import type { SurfaceAction } from '../types';

type SurfaceActionBarProps = {
  actions: SurfaceAction[];
  surfaceId: string;
  actionInput?: Record<string, string>;
  onAction?: (action: SurfaceAction, surfaceId: string, input?: Record<string, string>) => void;
};

export function SurfaceActionBar({ actions, surfaceId, actionInput, onAction }: SurfaceActionBarProps) {
  if (actions.length === 0) {
    return <p className="surface-action-bar empty">No actions are currently available.</p>;
  }

  return (
    <div className="surface-action-bar" aria-label="Surface actions">
      {actions.map((action) => (
        <button
          key={action.actionId}
          type="button"
          disabled={Boolean(action.disabled)}
          aria-describedby={action.disabled ? `${action.actionId}-disabled` : undefined}
          onClick={() => onAction?.(action, surfaceId, actionInput)}
        >
          {action.label}
          {action.requiresConfirmation && ' · confirm'}
          {action.requiresApproval && ' · approval'}
        </button>
      ))}
      {actions.map((action) => action.disabled ? <p key={`${action.actionId}-disabled`} id={`${action.actionId}-disabled`} className="form-error">{action.disabled.message}</p> : null)}
    </div>
  );
}

import type { CapabilityActionRequest, CapabilityActionResult, SurfaceAction } from '../types';
import { auditTraceLabel, buildCapabilityActionRequest, classifyCapabilityAction, idempotencyLabel } from './capabilityActionState';

type CapabilityActionButtonProps = {
  action: SurfaceAction;
  surfaceId: string;
  selectedContextId: string;
  surfaceCorrelationId: string;
  input?: unknown;
  submitting?: boolean;
  confirmationMessage?: string;
  onSubmit?: (request: CapabilityActionRequest, action: SurfaceAction) => Promise<CapabilityActionResult | void> | CapabilityActionResult | void;
  onResult?: (result: CapabilityActionResult, action: SurfaceAction) => void;
};

export function CapabilityActionButton({ action, surfaceId, selectedContextId, surfaceCorrelationId, input, submitting = false, confirmationMessage, onSubmit, onResult }: CapabilityActionButtonProps) {
  const variant = classifyCapabilityAction(action);
  const denied = Boolean(action.disabled);
  const disabled = denied || submitting;
  const describedBy = [
    denied ? `${action.actionId}-denied` : undefined,
    action.requiresConfirmation ? `${action.actionId}-confirmation` : undefined,
    action.requiresApproval ? `${action.actionId}-approval` : undefined,
    `${action.actionId}-audit`,
    `${action.actionId}-idempotency`
  ].filter(Boolean).join(' ');

  async function submitAction() {
    if (disabled) return;
    if (action.requiresConfirmation && !globalThis.confirm?.(confirmationMessage ?? `Run ${action.label}?`)) return;
    const request = buildCapabilityActionRequest(action, { input, selectedContextId, surfaceId, surfaceCorrelationId });
    const result = await onSubmit?.(request, action);
    if (result) onResult?.(result, action);
  }

  return (
    <div className={`capability-action ${variant}`} data-capability-id={action.capabilityId} data-action-intent={action.intent}>
      <button
        type="button"
        className={`capability-action-button ${variant}`}
        disabled={disabled}
        aria-disabled={disabled}
        aria-describedby={describedBy}
        data-idempotency-required={action.idempotency.required}
        data-trace-required={action.audit.traceRequired}
        onClick={submitAction}
      >
        {submitting ? 'Submitting…' : action.label}
      </button>
      <div className="capability-action-affordances" aria-label={`${action.label} capability details`}>
        <span className="capability-chip capability-id">{action.capabilityId}</span>
        <span id={`${action.actionId}-idempotency`} className="capability-chip idempotency">{idempotencyLabel(action)}</span>
        <span id={`${action.actionId}-audit`} className="capability-chip audit-trace">{auditTraceLabel(action)}</span>
        {action.resultSurface?.openPlacement && <span className="capability-chip result-surface">Opens {action.resultSurface.openPlacement}</span>}
      </div>
      {action.requiresConfirmation && <p id={`${action.actionId}-confirmation`} className="capability-action-note">Confirmation required before submission; frontend controls are advisory and backend capability checks remain authoritative.</p>}
      {action.requiresApproval && <p id={`${action.actionId}-approval`} className="capability-action-note">Approval policy applies before side effects are committed.</p>}
      {action.disabled && <p id={`${action.actionId}-denied`} className="form-error denied-reason">{action.disabled.message}</p>}
    </div>
  );
}

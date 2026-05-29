import type { CapabilityActionRequest, CapabilityActionResult, SurfaceAction, SurfaceEnvelope, WorkstreamItem } from '../types';

export type CapabilityActionVariant = 'default' | 'approval' | 'destructive' | 'idempotent' | 'trace';

export type CapabilityActionSubmitOptions = {
  input?: unknown;
  selectedContextId: string;
  surfaceId: string;
  surfaceCorrelationId: string;
  idempotencyKey?: string;
  now?: () => string;
  generateClientIdempotencyKey?: () => string;
};

export type CapabilityActionResultMapping = {
  behavior: 'append-surface' | 'update-surface' | 'open-surface' | 'feedback-only';
  placement?: NonNullable<SurfaceAction['resultSurface']>['openPlacement'];
  targetSurfaceId?: string;
  surface?: SurfaceEnvelope<unknown>;
  status: CapabilityActionResult['status'];
  message: string;
  correlationId: string;
  traceIds: string[];
};

export function classifyCapabilityAction(action: SurfaceAction): CapabilityActionVariant {
  if (action.requiresApproval || action.intent === 'approval') return 'approval';
  if (action.requiresConfirmation && (action.intent === 'command' || action.intent === 'governance')) return 'destructive';
  if (action.audit.traceRequired || action.intent === 'trace') return 'trace';
  if (action.idempotency.required) return 'idempotent';
  return 'default';
}

export function isCapabilityActionDenied(action: SurfaceAction): boolean {
  return Boolean(action.disabled);
}

export function idempotencyLabel(action: SurfaceAction): string {
  if (!action.idempotency.required) return 'Idempotency not required';
  return `Idempotency required (${action.idempotency.keySource ?? 'unspecified'})`;
}

export function auditTraceLabel(action: SurfaceAction): string {
  return action.audit.traceRequired ? `Trace required · ${action.audit.eventType}` : `Audit event · ${action.audit.eventType}`;
}

function defaultClientIdempotencyKey() {
  const cryptoValue = globalThis.crypto?.randomUUID?.();
  return cryptoValue ?? `client-${Date.now()}-${Math.random().toString(36).slice(2)}`;
}

export function resolveIdempotencyKey(action: SurfaceAction, options: CapabilityActionSubmitOptions): string | undefined {
  if (!action.idempotency.required) return undefined;
  if (options.idempotencyKey) return options.idempotencyKey;
  if (action.idempotency.keySource === 'surface-item') return `${options.surfaceId}:${action.actionId}:${options.surfaceCorrelationId}`;
  if (action.idempotency.keySource === 'server-issued') return undefined;
  return (options.generateClientIdempotencyKey ?? defaultClientIdempotencyKey)();
}

export function buildCapabilityActionRequest(action: SurfaceAction, options: CapabilityActionSubmitOptions): CapabilityActionRequest {
  return {
    actionId: action.actionId,
    capabilityId: action.capabilityId,
    input: options.input ?? {},
    idempotencyKey: resolveIdempotencyKey(action, options),
    selectedContextId: options.selectedContextId,
    surfaceId: options.surfaceId,
    correlationId: options.surfaceCorrelationId
  };
}

export function mapCapabilityActionResult(action: SurfaceAction, result: CapabilityActionResult): CapabilityActionResultMapping {
  const resultSurface = action.resultSurface;
  if (result.resultSurface && resultSurface?.appendSurfaceType) {
    return { behavior: 'append-surface', placement: resultSurface.openPlacement, surface: result.resultSurface, status: result.status, message: result.message, correlationId: result.correlationId, traceIds: result.traceIds };
  }
  if (result.resultSurface && resultSurface?.updateSurfaceId) {
    return { behavior: 'update-surface', placement: resultSurface.openPlacement, targetSurfaceId: resultSurface.updateSurfaceId, surface: result.resultSurface, status: result.status, message: result.message, correlationId: result.correlationId, traceIds: result.traceIds };
  }
  if (result.resultSurface && resultSurface?.openPlacement) {
    return { behavior: 'open-surface', placement: resultSurface.openPlacement, targetSurfaceId: result.resultSurface.surfaceId, surface: result.resultSurface, status: result.status, message: result.message, correlationId: result.correlationId, traceIds: result.traceIds };
  }
  return { behavior: 'feedback-only', placement: resultSurface?.openPlacement, status: result.status, message: result.message, correlationId: result.correlationId, traceIds: result.traceIds };
}

export function capabilityActionResultToWorkstreamItem(action: SurfaceAction, mapping: CapabilityActionResultMapping, createdAt: string): WorkstreamItem {
  return {
    itemId: `action-feedback-${action.actionId}-${mapping.correlationId}`,
    functionalAgentId: mapping.surface?.ownerFunctionalAgentId ?? 'agent-system',
    kind: 'action-feedback',
    title: `${action.label}: ${mapping.status}`,
    body: mapping.message,
    surfaceId: mapping.surface?.surfaceId ?? mapping.targetSurfaceId,
    status: mapping.status === 'denied' || mapping.status === 'blocked-runtime' || mapping.status === 'blocked_provider_or_runtime' ? 'blocked' : mapping.status === 'failed' ? 'failed' : mapping.status === 'approval-required' ? 'waiting-for-human' : 'ready',
    createdAt,
    correlationId: mapping.correlationId,
    traceIds: mapping.traceIds
  };
}

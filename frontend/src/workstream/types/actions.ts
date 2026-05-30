import type { SurfaceEnvelope } from './surfaces';

export type SurfaceActionIntent = 'read' | 'surface-request' | 'command' | 'proposal' | 'approval' | 'workflow' | 'governance' | 'trace';
export type IdempotencyKeySource = 'client-generated' | 'surface-item' | 'server-issued';
export type ResultSurfacePlacement = 'inline' | 'modal' | 'side-panel' | 'deep-link';

export type DisabledReason = {
  reasonCode: string;
  message: string;
};

export type SurfaceAction = {
  actionId: string;
  label: string;
  intent: SurfaceActionIntent;
  capabilityId: string;
  shellRequest?: {
    requestType: 'show_surface' | 'open_workstream';
    targetFunctionalAgentId: string;
    targetSurfaceId: string;
    displayText: string;
  };
  inputSchemaRef?: string;
  requiresConfirmation?: boolean;
  requiresApproval?: boolean;
  disabled?: DisabledReason;
  idempotency: {
    required: boolean;
    keySource?: IdempotencyKeySource;
  };
  resultSurface?: {
    appendSurfaceType?: string;
    updateSurfaceId?: string;
    openPlacement?: ResultSurfacePlacement;
  };
  audit: {
    eventType: string;
    traceRequired: boolean;
  };
};

export type CapabilityActionRequest = {
  actionId: string;
  capabilityId: string;
  input: unknown;
  idempotencyKey?: string;
  selectedContextId: string;
  surfaceId?: string;
  correlationId: string;
};

export type CapabilityActionResultStatus = 'accepted' | 'denied' | 'validation-error' | 'approval-required' | 'conflict' | 'no-op' | 'failed' | 'blocked-runtime' | 'blocked_provider_or_runtime';

export type CapabilityActionResult = {
  status: CapabilityActionResultStatus;
  message: string;
  correlationId: string;
  traceIds: string[];
  resultSurface?: SurfaceEnvelope<unknown>;
};

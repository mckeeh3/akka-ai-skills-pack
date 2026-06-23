import type { SurfaceEnvelope } from './surfaces';

export type SurfaceActionIntent = 'read' | 'surface-request' | 'command' | 'proposal' | 'approval' | 'workflow' | 'governance' | 'trace';
export type ShellRequestType = 'show_surface' | 'open_workstream' | 'refresh_surface' | 'open_attention_item';
export type ShellRequestOrigin = 'user_prompt' | 'surface_action' | 'deep_link' | 'my_account_panel' | 'system_suggestion' | 'shell_button';
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
  browserToolId: string;
  governedToolId: string;
  capabilityId: string;
  shellRequest?: {
    requestType: ShellRequestType;
    targetFunctionalAgentId?: string;
    targetSurfaceId?: string;
    targetItemId?: string;
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
  browserToolId: string;
  governedToolId: string;
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

export type WorkstreamShellRequest = {
  requestType: ShellRequestType;
  origin: ShellRequestOrigin;
  displayText: string;
  canonicalPrompt?: string;
  targetFunctionalAgentId?: string;
  targetSurfaceId?: string;
  targetItemId?: string;
  sourceFunctionalAgentId?: string;
  sourceSurfaceId?: string;
  sourceActionId?: string;
  scope: 'current_workstream' | 'authorized_cross_workstream';
  correlationId: string;
  selectedContextId: string;
};

export type WorkstreamShellResponse = {
  request: WorkstreamShellRequest;
  status: 'accepted' | 'denied' | 'validation-error';
  message: string;
  correlationId: string;
  traceIds: string[];
  requestItem: import('./workstream').WorkstreamItem;
  resultSurface: SurfaceEnvelope<unknown>;
};

export type ChatToolPlanConfirmationRequest = {
  selectedContextId: string;
  planId: string;
  planSnapshotId: string;
  confirmationText: string;
  stepHashes: Record<string, string>;
  idempotencyKey: string;
  correlationId: string;
};

export type SurfaceActionInput = Record<string, string> | ChatToolPlanConfirmationRequest;

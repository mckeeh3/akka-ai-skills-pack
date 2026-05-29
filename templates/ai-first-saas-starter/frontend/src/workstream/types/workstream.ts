export type WorkstreamItemKind =
  | 'user-request'
  | 'user-message'
  | 'agent-response'
  | 'markdown_response'
  | 'surface'
  | 'capability-result'
  | 'workflow-status'
  | 'decision'
  | 'audit-trace'
  | 'action-feedback'
  | 'system-notification'
  | 'workstream-notification'
  | 'surface-request'
  | 'system-status';

export type WorkstreamItemStatus = 'working' | 'waiting-for-human' | 'blocked' | 'ready' | 'failed' | 'stale';

export type WorkstreamShellRequestOrigin = 'user_prompt' | 'surface_action' | 'deep_link' | 'my_account_panel' | 'system_suggestion';
export type WorkstreamShellRequestType = 'show_surface' | 'open_workstream' | 'refresh_surface' | 'open_attention_item';
export type SurfaceRequestScope = 'current_workstream' | 'authorized_cross_workstream';

export type WorkstreamShellRequest = {
  requestType: WorkstreamShellRequestType;
  origin: WorkstreamShellRequestOrigin;
  displayText: string;
  canonicalPrompt: string;
  targetFunctionalAgentId?: string;
  targetSurfaceId?: string;
  targetItemId?: string;
  sourceFunctionalAgentId?: string;
  sourceSurfaceId?: string;
  sourceActionId?: string;
  scope: SurfaceRequestScope;
  correlationId: string;
};

export type TraceLink = {
  traceId: string;
  label: string;
  href: string;
};

export type WorkstreamItem = {
  itemId: string;
  functionalAgentId: string;
  kind: WorkstreamItemKind;
  createdAt: string;
  correlationId: string;
  traceIds: string[];
  surfaceId?: string;
  title?: string;
  body?: string;
  status?: WorkstreamItemStatus;
  requestOrigin?: WorkstreamShellRequestOrigin;
  canonicalPrompt?: string;
  traceLinks?: TraceLink[];
};

export type ComposerRequest = {
  functionalAgentId: string;
  selectedContextId: string;
  prompt: string;
  attachedSurfaceId?: string;
  idempotencyKey: string;
};

export type WorkstreamSelection = {
  selectedFunctionalAgentId: string;
  selectedItemId?: string;
  selectedSurfaceId?: string;
  surfacePlacement?: 'inline' | 'modal' | 'side-panel' | 'deep-link';
};

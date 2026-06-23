export type WorkstreamItemKind =
  | 'user-request'
  | 'user-message'
  | 'agent-response'
  | 'markdown_response'
  | 'system_message'
  | 'chat_tool_plan_proposal'
  | 'chat_tool_plan_confirmation'
  | 'chat_tool_plan_result'
  | 'chat_tool_plan_system_message'
  | 'user-confirmation'
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

export type WorkstreamItemStatus = 'working' | 'waiting-for-human' | 'blocked' | 'ready' | 'failed' | 'stale' | 'completed' | 'partial-failure' | 'partial_failure' | 'skipped';

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
  traceId?: string;
  correlationId?: string;
};

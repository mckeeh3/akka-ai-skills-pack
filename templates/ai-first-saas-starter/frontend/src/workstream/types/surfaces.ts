import type { SurfaceAction } from './actions';

export type SurfaceRedactionProfile = 'self' | 'tenant-admin' | 'support' | 'auditor' | 'agent';
export type SurfaceUiStatus = 'loading' | 'empty' | 'ready' | 'submitting' | 'success' | 'pending' | 'approval-needed' | 'error' | 'forbidden' | 'conflict' | 'stale' | 'reconnecting' | 'partial-data' | 'no-op';
export type CanonicalSurfaceType = 'markdown_response' | 'dashboard' | 'list-search' | 'detail-edit' | 'decision' | 'audit-timeline' | 'workflow-status' | 'governance-diff' | 'outcome';

export type SurfaceLink = {
  label: string;
  href: string;
  rel: 'self' | 'deep-link' | 'trace' | 'evidence' | 'related';
};

export type SurfaceEnvelope<TData, TAction extends SurfaceAction = SurfaceAction> = {
  surfaceId: string;
  surfaceType: CanonicalSurfaceType | string;
  surfaceVersion: string;
  title: string;
  ownerFunctionalAgentId: string;
  reusableByFunctionalAgentIds?: string[];
  authContext: {
    tenantId: string;
    customerId?: string;
    selectedContextId: string;
    visibleCapabilityIds: string[];
  };
  correlationId: string;
  traceIds: string[];
  generatedAt: string;
  stale?: {
    isStale: boolean;
    reason?: string;
    lastKnownEventId?: string;
  };
  redaction: {
    profile: SurfaceRedactionProfile;
    omittedFieldKeys?: string[];
  };
  data: TData;
  actions: TAction[];
  links?: SurfaceLink[];
};

export type MarkdownResponseSourceRef = {
  refType: 'capability' | 'trace' | 'document' | 'evidence';
  refId: string;
  label: string;
};

export type MarkdownResponseSection = {
  anchor: string;
  title: string;
};

export type MarkdownResponseData = {
  markdown: string;
  title?: string;
  summary?: string;
  workstreamEntryId: string;
  producingAgentId: string;
  codeBlockLanguageHints?: string[];
  sourceRefs?: MarkdownResponseSourceRef[];
  sections?: MarkdownResponseSection[];
  safety?: {
    sanitized: boolean;
    blockedUnsafeLinks?: number;
    blockedRawHtml?: boolean;
    redactionNote?: string;
  };
  trace?: {
    correlationId?: string;
    traceIds?: string[];
  };
};

export type DashboardSurfaceData = {
  cards: Array<{ cardId: string; label: string; value: string | number; severity?: 'info' | 'warning' | 'critical' }>;
  sections?: Array<{ sectionId: string; label: string; summary: string }>;
  nextSteps?: Array<{ workstreamId: string; label: string; allowed: boolean; blockedReason?: string; capabilityIds?: string[]; traceId?: string }>;
  blockedState?: { reasonCode: string; message: string; recovery: string };
};

export type ListSearchSurfaceData = {
  query: string;
  rows: Array<Record<string, string | number | boolean | undefined>>;
  pageInfo?: { nextPageToken?: string; totalKnownCount?: number };
};

export type DetailEditSurfaceData = {
  recordId: string;
  recordLabel?: string;
  recordKind?: 'account' | 'membership' | 'invitation' | 'support-access' | string;
  summary?: string;
  fields: Array<{
    fieldId: string;
    label: string;
    value: string;
    editable: boolean;
    inputType?: 'text' | 'email' | 'select' | 'textarea';
    options?: Array<{ value: string; label: string }>;
    disabledReason?: string;
  }>;
  version: number;
  permissionState?: {
    canEdit: boolean;
    reason?: string;
    authoritativeCapabilityId: string;
  };
  audit?: {
    lastEventType: string;
    lastActor: string;
    traceIds: string[];
  };
};

export type DecisionSurfaceData = {
  decisionId: string;
  recommendation: string;
  riskScore?: number;
  confidenceScore?: number;
  evidence: Array<{ evidenceId: string; label: string; summary: string }>;
};

export type AuditTimelineSurfaceData = {
  events: Array<{ eventId: string; occurredAt: string; actor: string; action: string; traceId: string }>;
};

export type WorkflowStatusSurfaceData = {
  workflowId: string;
  status: 'running' | 'waiting-for-human' | 'blocked' | 'completed' | 'failed';
  steps: Array<{ stepId: string; label: string; status: string }>;
};

export type GovernanceDiffSurfaceData = {
  proposalId: string;
  beforeSummary: string;
  afterSummary: string;
  changes: Array<{ path: string; before?: string; after?: string; impact: string }>;
};

export type OutcomeSurfaceData = {
  outcomeId: string;
  metrics: Array<{ metricId: string; label: string; current: number; target: number; unit?: string }>;
};

export type RegionState<T> =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'ready'; value: T }
  | { status: 'empty'; message: string }
  | { status: 'forbidden'; message: string; recovery?: string }
  | { status: 'error'; message: string; retryable: boolean }
  | { status: 'stale'; value: T; message: string; lastKnownEventId?: string };

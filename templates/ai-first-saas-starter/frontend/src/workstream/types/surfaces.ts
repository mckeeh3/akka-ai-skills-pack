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
  cards: Array<{ cardId: string; label: string; value: string | number; severity?: 'info' | 'warning' | 'critical' | 'blocked_provider_or_runtime' }>;
  sections?: Array<{ sectionId: string; label: string; summary: string }>;
  nextSteps?: Array<{ workstreamId: string; label: string; allowed: boolean; blockedReason?: string; capabilityIds?: string[]; traceId?: string }>;
  blockedState?: { reasonCode: string; message: string; recovery: string };
  readiness?: string;
  capabilityIds?: string[];
};

export type ListSearchSurfaceData = {
  query: string | Record<string, string | number | boolean | undefined>;
  rows: Array<Record<string, string | number | boolean | undefined>>;
  pageInfo?: { nextPageToken?: string; nextCursor?: string; totalKnownCount?: number };
  partial?: boolean;
  redaction?: string;
};

export type DetailEditSurfaceData = {
  recordId?: string;
  recordLabel?: string;
  recordKind?: 'account' | 'membership' | 'invitation' | 'support-access' | string;
  summary?: string;
  fields?: Array<{
    fieldId: string;
    label: string;
    value: string;
    editable: boolean;
    inputType?: 'text' | 'email' | 'select' | 'textarea';
    options?: Array<{ value: string; label: string }>;
    disabledReason?: string;
  }>;
  version?: number;
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
  traceId?: string;
  eventKind?: string;
  timestamp?: string;
  actor?: string;
  source?: string;
  correlationIds?: string[];
  authorizationBasis?: string;
  decision?: string;
  redactedEvidence?: string;
  redactionMetadata?: Record<string, unknown>;
  category?: string;
  safeReason?: string;
  userActionableNextSteps?: string[];
  policyRefs?: string[];
  redactedDetails?: Record<string, string>;
  traceLinks?: string[];
  accessManagement?: {
    memberStatus?: {
      accountStatus: string;
      membershipStatus: string;
      statusActionIds: string[];
      denialHints: string[];
      noOpMessage?: string;
      idempotencyKeySource?: string;
      traceLinks: string[];
    };
    roleChangePreview?: {
      surfaceContract: 'user_admin.role_change_preview.v1';
      currentRoles: string[];
      proposedRoles: string[];
      capabilityDelta: { added: string[]; removed: string[]; unchanged?: string[] };
      affectedWorkstreams: string[];
      policyHints: string[];
      lastAdminImpact: string;
      approvalRequired: boolean;
      noOp: boolean;
      traceLinks: string[];
    };
    advisoryNotice: string;
  };
};

export type DecisionSurfaceData = {
  decisionId?: string;
  recommendation: string;
  riskScore?: number | string;
  confidenceScore?: number | string;
  evidence?: Array<{ evidenceId: string; label: string; summary: string }>;
  allowedActions?: Array<{ actionId: string; label: string; capabilityId: string }>;
  disabledActions?: Array<{ actionId: string; reason: string }>;
  risk?: string;
  traceLinks?: string[];
};

export type AuditTimelineSurfaceData = {
  events?: Array<{ eventId: string; occurredAt: string; actor: string; action: string; traceId: string }>;
  correlationId?: string;
  nodes?: Array<{ nodeId: string; sourceType: string; summary: string; correlationId: string; status: string; traceId?: string }>;
  partial?: boolean;
  omittedCategories?: string[];
  redactionSummary?: string;
};

export type WorkflowStatusSurfaceData = {
  workflowId: string;
  status: 'running' | 'waiting-for-human' | 'blocked' | 'blocked_provider_or_runtime' | 'completed' | 'failed' | 'cancelled';
  summary?: string;
  traceIds?: string[];
  requiredCapabilityId?: string;
  taskKind?: 'workflow' | 'autonomous-agent-analysis' | string;
  progress?: Array<{ snapshotId: string; label: string; status: string; traceId?: string }>;
  resultSummary?: string;
  steps?: Array<{ stepId: string; label: string; status: string }>;
};

export type GovernanceDiffSurfaceData = {
  proposalId: string;
  lifecycleState?: 'draft' | 'in_review' | 'approved' | 'rejected' | 'activated' | 'rolled_back' | 'blocked';
  source?: string;
  riskClassification?: 'low' | 'medium' | 'high' | 'critical';
  requiredApproval?: string;
  simulationSummary?: string;
  activationStatus?: string;
  traceLinks?: string[];
  beforeSummary: string;
  afterSummary: string;
  changes: Array<{ path: string; before?: string; after?: string; impact: string }>;
  simulation?: {
    affectedCapabilities: string[];
    expectedAllows: string[];
    expectedDenials: string[];
    warnings: string[];
    confidence: string;
    evidenceTraceIds: string[];
  };
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

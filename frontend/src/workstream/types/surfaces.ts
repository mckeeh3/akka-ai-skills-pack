import type { SurfaceAction } from './actions';

export type SurfaceRedactionProfile = 'self' | 'tenant-admin' | 'support' | 'auditor' | 'agent';
export type SurfaceUiStatus = 'loading' | 'empty' | 'ready' | 'submitting' | 'success' | 'pending' | 'approval-needed' | 'error' | 'forbidden' | 'conflict' | 'stale' | 'reconnecting' | 'partial-data' | 'no-op' | 'blocked_provider_or_runtime' | 'not_found_or_redacted' | 'validation-error';
export type CanonicalSurfaceType = 'markdown_response' | 'system_message' | 'system-message' | 'dashboard' | 'list-search' | 'show-inspection' | 'create-form' | 'edit-form' | 'destructive-lifecycle-confirmation' | 'lifecycle-confirmation' | 'detail-edit' | 'decision' | 'decision-card' | 'audit-timeline' | 'workflow-status' | 'governance-diff' | 'outcome' | 'outcome-panel' | 'notification-center';

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

export type SystemMessageData = {
  surfaceContract?: string;
  status?: 'blocked_provider_or_runtime' | 'forbidden' | 'validation-error' | 'error' | 'no-op' | 'not_found_or_redacted' | string;
  severity?: 'info' | 'warning' | 'error' | 'critical' | string;
  title?: string;
  summary?: string;
  message?: string;
  recoverySteps?: string[];
  safeReasonCode?: string;
  blockerCode?: string;
  redaction?: string;
  traceRefs?: string[];
  noFakeSuccess?: boolean;
  noDirectMutation?: boolean;
  workstreamEntryId?: string;
  producingAgentId?: string;
  capabilityId?: string;
  sourceRefs?: MarkdownResponseSourceRef[];
  safety?: {
    sanitized: boolean;
    redactionNote?: string;
  };
  trace?: {
    correlationId?: string;
    traceIds?: string[];
  };
};

export type AttentionItemStatus = 'open' | 'acknowledged' | 'resolved' | 'dismissed' | 'expired' | string;
export type AttentionCategory = 'invitation_delivery' | 'provider_readiness' | 'governance_approval' | 'audit_failure_evidence' | 'access_review' | 'policy_exception' | 'workflow_blocked' | 'agent_task_failed' | 'security_review' | string;
export type AttentionItemSeverity = 'info' | 'warning' | 'urgent' | 'blocked' | 'critical' | string;
export type AttentionRedaction = 'full' | 'summary_only' | 'not_found_or_redacted' | string;

export type AttentionSurfaceRef = {
  targetFunctionalAgentId?: string;
  targetSurfaceId?: string;
  targetSurfaceType?: string;
  targetItemId?: string;
  defaultActionId?: string;
  requiredCapabilityId?: string;
};

export type AttentionItem = {
  itemId: string;
  label?: string;
  title?: string;
  summary?: string;
  status: AttentionItemStatus;
  severity?: AttentionItemSeverity;
  category?: AttentionCategory;
  capabilityId?: string;
  governedToolId?: string;
  traceId?: string;
  sourceWorkstreamId?: string;
  surfaceRef?: AttentionSurfaceRef;
  redaction?: AttentionRedaction;
};

export type BrowserSafeRedactionMetadata = string | {
  browserSafe?: boolean;
  omittedFieldKeys?: string[];
  previewLimitChars?: number;
  [key: string]: unknown;
};

export type DashboardSurfaceData = {
  surfaceContract?: 'audit.trace.dashboard.v1' | string;
  cards: Array<{ cardId: string; label: string; value: string | number; unit?: string; status?: string; description?: string; cardKind?: 'workstream-status' | 'notification-center' | 'personal-command-center' | string; workstreamId?: string; surfaceId?: string; targetSurfaceId?: string; actionId?: string; severity?: 'info' | 'warning' | 'urgent' | 'critical' | 'blocked' | 'blocked_provider_or_runtime' }>;
  summaryCards?: Array<{ cardId: string; label: string; value: string | number; unit?: string; status?: string; description?: string; severity?: string; actionId?: string; targetSurfaceId?: string }>;
  attentionQueues?: Array<{ queueId: string; label: string; count?: string | number; severity?: string; statusText?: string; sourceCapabilityId?: string; targetSurfaceId?: string; filter?: string; actionId?: string; openActionId?: string; traceRefs?: string[]; redaction?: string }>;
  attentionCounts?: Array<{ attentionType: string; label: string; count: string | number; severity?: string; statusText?: string; administeredPopulationType?: string; targetSurfaceId?: string; filter?: string | Record<string, unknown>; sourceCapabilityId?: string; traceRefs?: string[]; redactionState?: string; openActionId?: string; actionId?: string }>;
  administeredPopulations?: Array<{ populationType: string; label: string; visibleCount: string | number; attentionCount?: string | number; activeCount?: string | number; pendingInvitationCount?: string | number; suspendedOrDisabledCount?: string | number; staleOrExpiredCount?: string | number; reviewCount?: string | number; roleCoverageSummary?: string; targetSurfaceId: string; openActionId: string; capabilityIds?: string[]; traceRefs?: string[] }>;
  authorizedActions?: Array<{ actionId: string; label: string; governedToolId?: string; capabilityId?: string; resultSurfaceId?: string; approvalRequired?: boolean; denialHint?: string }>;
  recentActivity?: Array<{ activityId: string; label: string; summary?: string; traceId?: string; redaction?: string; occurredAt?: string }>;
  attentionCounters?: Array<{ counterId: string; label: string; value: string | number; severity?: string; status?: string; source?: string; actionId?: string; surfaceId?: string; targetSurfaceId?: string; workstreamId?: string; description?: string; requiredCapabilityId?: string; redaction?: string; traceRefs?: string[] }>;
  needsAttention?: Array<AttentionItem>;
  controlPanels?: Array<{ panelId: string; label: string; summary: string; state?: string; value?: string | number; surfaceId?: string; actionId?: string; severity?: string }>;
  authorizedWorkstreamLinks?: Array<{ workstreamId: string; label: string; requiredCapabilityId?: string; surfaceId?: string; actionId?: string; status?: string }>;
  notificationCenter?: Record<string, unknown>;
  personalAttentionDigest?: Record<string, unknown>;
  scopeLabel?: string;
  selectedAuthContext?: Record<string, unknown>;
  adminLevel?: string;
  hero?: { title?: string; scopeLabel?: string; scopeType?: string; adminLevel?: string; administeredPopulationLabels?: string[]; supportAccessState?: string; redactionSummary?: string; traceRefs?: string[]; correlationId?: string };
  authorityBasis?: unknown;
  contextCapabilityGroups?: unknown;
  traceRefs?: Array<string | Record<string, unknown>>;
  systemStates?: string[];
  attentionItems?: Array<AttentionItem>;
  attentionSource?: 'attention.list_workstream_items' | string;
  accountContext?: { displayName?: string; email?: string; tenantId?: string; customerId?: string; selectedContextId?: string; tenantLabel?: string; customerLabel?: string; selectedContextLabel?: string; roles?: string[]; authority?: string };
  quickSurfaceActionIds?: string[];
  utilityActionIds?: string[];
  sections?: Array<{ sectionId: string; label: string; summary: string }>;
  nextSteps?: Array<{ workstreamId: string; label: string; allowed: boolean; blockedReason?: string; capabilityIds?: string[]; traceId?: string; surfaceId?: string; actionId?: string }>;
  blockedState?: { reasonCode: string; message: string; recovery: string };
  readiness?: string;
  capabilityIds?: string[];
  redaction?: BrowserSafeRedactionMetadata;
};

export type ListSearchSurfaceData = {
  surfaceContract?: 'audit.trace.search.v1' | string;
  surfaceContracts?: string[];
  query: string | Record<string, string | number | boolean | undefined | Record<string, unknown>>;
  rows: Array<Record<string, string | number | boolean | undefined | Record<string, unknown> | Array<unknown>>>;
  pageInfo?: { nextPageToken?: string; nextCursor?: string; totalKnownCount?: number };
  filterState?: Record<string, string | number | boolean | undefined>;
  mobileFallback?: string;
  dashboardOrigin?: Record<string, unknown>;
  emptyMessage?: string;
  capabilityIds?: string[];
  systemStates?: string[];
  partial?: boolean;
  redaction?: BrowserSafeRedactionMetadata;
};

export type OrganizationAdminSurfaceData = {
  surfaceContract: 'user_admin.organization_directory.v1' | 'user_admin.organization_detail.v1' | 'user_admin.organization_create.v1' | 'user_admin.organization_rename.v1' | 'user_admin.organization_suspend_confirmation.v1' | 'user_admin.organization_reactivate_confirmation.v1' | string;
  branchNavigation?: UserAdminBranchNavigation;
  branchRootSurfaceId?: string;
  branchReturnActionId?: string;
  branchReturnLabel?: string;
  safeFilterPreservation?: string;
  selectedAuthContext?: Record<string, unknown>;
  scopeLabel?: string;
  scopeType?: string;
  authorityBasis?: string;
  boundaryNotice?: string;
  safeBoundaryNotice?: string;
  traceRefs?: string[];
  correlationId?: string;
  redaction?: BrowserSafeRedactionMetadata;
  organizations?: Array<{
    organizationId: string;
    organizationName: string;
    status: string;
    updatedAt?: string;
    safeLifecycleSummary?: string;
    visibleTenantAdminCount?: number;
    actionAvailability?: string[];
    traceRefs?: string[];
  }>;
  organizationDetail?: {
    organizationId?: string;
    organizationName?: string;
    status?: string;
    safeBoundaryNotice?: string;
    visibleActions?: string[];
    recentAuditEvents?: Array<Record<string, unknown>>;
    traceRefs?: string[];
    correlationId?: string;
  };
  filters?: Record<string, string | number | boolean | undefined>;
  systemStates?: string[];
  emptyMessage?: string;
  forbiddenMessage?: string;
  lastResult?: { status: string; message: string; correlationId?: string; traceRefs?: string[] };
};

export type UserAdminBranchNavigation = {
  branchRootSurfaceId?: string;
  branchReturnActionId?: string;
  branchReturnLabel?: string;
  browserToolId?: string;
  governedToolId?: string;
  capabilityId?: string;
  safeFilterPreservation?: string;
  traceRefs?: string[];
  correlationId?: string;
};

export type DetailEditSurfaceData = {
  branchNavigation?: UserAdminBranchNavigation;
  branchRootSurfaceId?: string;
  branchReturnActionId?: string;
  branchReturnLabel?: string;
  safeFilterPreservation?: string;
  recordId?: string;
  recordLabel?: string;
  recordKind?: 'account' | 'membership' | 'invitation' | 'support-access' | string;
  summary?: string;
  fields?: Array<{
    fieldId: string;
    label: string;
    value: string;
    editable: boolean;
    inputType?: 'text' | 'email' | 'select' | 'textarea' | 'number';
    options?: Array<{ value: string; label: string }>;
    disabledReason?: string;
  }>;
  version?: number;
  permissionState?: {
    canEdit: boolean;
    reason?: string;
    authoritativeCapabilityId: string;
  };
  deliveryState?: Record<string, unknown>;
  recoverySteps?: string[];
  noFakeSuccess?: boolean;
  providerReadiness?: Record<string, unknown>;
  relatedArtifacts?: Array<Record<string, unknown>>;
  noDirectMutation?: boolean;
  providerBlockedSystemMessage?: Record<string, unknown> | null;
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
  relatedEvents?: Array<{ traceId: string; eventKind?: string; summary?: string; correlationId?: string; status?: string }>;
  surfaceContract?: string;
  profileSummary?: Record<string, unknown>;
  providerBoundarySummary?: string;
  settingsSummary?: Record<string, unknown>;
  preferredThemeId?: string;
  availableThemes?: Array<{ themeId?: string; value?: string; id?: string; name?: string; label?: string; tone?: string; selected?: boolean }>;
  notificationPreferenceSummary?: unknown;
  digestPreferenceSummary?: unknown;
  selectedContext?: Record<string, unknown>;
  authorityBasis?: unknown;
  roleSummary?: string[];
  visibleCapabilitySummary?: Record<string, unknown>;
  supportAccess?: Record<string, unknown>;
  availableContexts?: Array<Record<string, unknown>>;
  redaction?: BrowserSafeRedactionMetadata;
  capabilityAliases?: string[];
  actionContext?: Record<string, string>;
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
    supportAccess?: {
      supportAccess?: boolean;
      status?: string;
      expiresAt?: string;
      actionIds?: string[];
      denialHints?: string[];
      traceLinks?: string[];
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
  capabilityDelta?: { added?: string[]; removed?: string[]; unchanged?: string[] };
  affectedWorkstreams?: string[];
  policyHints?: string[];
  lastAdminImpact?: string;
  message?: string;
  status?: string;
  statusOptions?: Array<{ value?: string; status?: string; label?: string; actionId?: string }>;
  systemStates?: string[];
};

export type DecisionSurfaceData = {
  decisionId?: string;
  recommendation: string;
  riskScore?: number | string;
  confidenceScore?: number | string;
  evidence?: Array<{ evidenceId: string; label: string; summary: string }>;
  allowedActions?: Array<{ actionId: string; label: string; browserToolId: string; governedToolId: string; capabilityId: string }>;
  disabledActions?: Array<{ actionId: string; label?: string; reason: string }>;
  risk?: string;
  impact?: string;
  alternatives?: string[];
  affectedTarget?: string;
  policyBasis?: string;
  idempotencyKeySource?: string;
  activationBlocker?: string;
  noDirectMutation?: boolean;
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
  branchNavigation?: UserAdminBranchNavigation;
  branchRootSurfaceId?: string;
  branchReturnActionId?: string;
  branchReturnLabel?: string;
  safeFilterPreservation?: string;
  surfaceContract?: 'user_admin.access_review_task.v1' | string;
  workflowId?: string;
  digestTaskId?: string;
  autonomousAgentTaskId?: string;
  taskId?: string;
  status: 'not_started' | 'queued' | 'running' | 'working' | 'waiting-for-human' | 'waiting-for-provider-runtime' | 'blocked' | 'blocked_provider_or_runtime' | 'completed' | 'completed-review-required' | 'failed' | 'cancelled' | 'accepted' | 'rejected' | string;
  summary?: string;
  traceIds?: string[];
  traceLinks?: Array<{ traceId: string; category?: string; label?: string; summary?: string; targetSurfaceId?: string; correlationId?: string; redaction?: string }>;
  modelToolDataPolicyUsage?: {
    model?: string;
    tools?: string[];
    data?: string;
    policy?: string;
    traceLinks?: Array<{ traceId: string; category?: string; label?: string; summary?: string; targetSurfaceId?: string; correlationId?: string; redaction?: string }>;
    redaction?: string;
  };
  requiredCapabilityId?: string;
  initiatingCapabilityId?: string;
  taskKind?: 'workflow' | 'autonomous-agent-analysis' | string;
  progress?: Array<{ snapshotId: string; label: string; status: string; traceId?: string }> | { percent?: number; summary?: string };
  resultSummary?: string;
  steps?: Array<{ stepId: string; label: string; status: string }>;
  scope?: { scopeType?: string; tenantId?: string; customerId?: string };
  blockers?: Array<{ code: string; message: string }>;
  evidenceRefs?: Array<string | { refId: string; label?: string; summary?: string; traceId?: string }>;
  recommendations?: Array<string | { recommendationId?: string; label?: string; risk?: string; confidence?: string; summary?: string }>;
  providerFailures?: string[];
  resultReviewState?: string;
  noDirectMutation?: boolean;
  safety?: string;
  authorizedAttentionCount?: number;
  sectionRefs?: string[];
  phase?: string;
  progressEvents?: Array<string | { eventId?: string; label?: string; status?: string; summary?: string; traceId?: string }>;
  redaction?: string;
  accessReview?: {
    surfaceContract: 'user_admin.access_review_task.v1';
    taskId: string;
    lifecycleState: string;
    progressPercent?: number;
    blockers?: Array<{ code: string; message: string }>;
    evidenceRefs?: Array<{ refId: string; label: string; summary: string; traceId?: string }>;
    recommendations?: Array<{ recommendationId: string; label: string; risk: string; confidence: string; summary: string }>;
    providerFailures?: string[];
    resultReviewStates?: string[];
    resultReviewState?: string;
    noDirectMutation: boolean;
    safety: string;
    traceIds: string[];
    traceLinks?: Array<{ traceId: string; category?: string; label?: string; summary?: string; targetSurfaceId?: string; correlationId?: string; redaction?: string }>;
  };
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

export type NotificationCenterSurfaceData = {
  surfaceContract: 'my_account.notification_center.v1' | string;
  channel: 'in_app' | string;
  unreadCount: number;
  visibleCount: number;
  items?: Array<{
    notificationId: string;
    channel?: 'in_app' | string;
    title?: string;
    summary?: string;
    category?: string;
    priority?: 'info' | 'warning' | 'urgent' | 'blocked' | string;
    status: 'unread' | 'read' | 'dismissed' | 'archived' | 'snoozed' | 'expired' | string;
    origin?: string;
    redactionLevel?: 'full' | 'summary_only' | 'not_found_or_redacted' | string;
    requiredCapabilityId?: string;
    owningWorkstreamId?: string;
    surfaceRef?: AttentionSurfaceRef;
    sourceRefs?: Array<{ refType?: string; refId: string; label?: string; capabilityId?: string; traceId?: string; correlationId?: string }>;
    traceRefs?: string[];
    createdAt?: string;
    updatedAt?: string;
    lastChangedAt?: string;
    readAt?: string;
    dismissedAt?: string;
    archivedAt?: string;
    snoozedUntil?: string;
  }>;
  preferencesSummary?: Array<{ preferenceId: string; channel: 'in_app' | string; category: string; enabled: boolean; minimumPriority: string; muteUntil?: string; includeReadInCenter: boolean; updatedAt?: string; updatedBy?: string; correlationId?: string }>;
  sourceSummary?: Record<string, number>;
  redaction?: string;
  traceRefs?: string[];
  correlationId?: string;
  capabilityIds?: string[];
};

export type OutcomeSurfaceData = {
  outcomeId?: string;
  digestTaskId?: string;
  autonomousAgentTaskId?: string;
  surfaceContract?: string;
  status?: string;
  progressPercent?: number;
  summary?: string;
  authorizedAttentionCount?: number;
  sectionRefs?: string[];
  evidenceRefs?: Array<string | { refId?: string; label?: string; summary?: string; traceId?: string }>;
  materialEvents?: Array<string | { refId?: string; label?: string; summary?: string; traceId?: string }>;
  recommendations?: Array<string | { recommendationId?: string; label?: string; summary?: string; risk?: string; confidence?: string }>;
  omissions?: Record<string, unknown>;
  authorizedSourceCounts?: Record<string, number>;
  sourceSurfaceRefs?: Array<string | Record<string, unknown>>;
  traceRefs?: string[];
  redaction?: string;
  noDirectMutation?: boolean;
  safety?: string;
  decisionState?: string;
  metrics?: Array<{ metricId: string; label: string; current: number; target: number; unit?: string }>;
};

export type RegionState<T> =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'ready'; value: T }
  | { status: 'empty'; message: string }
  | { status: 'forbidden'; message: string; recovery?: string }
  | { status: 'error'; message: string; retryable: boolean }
  | { status: 'stale'; value: T; message: string; lastKnownEventId?: string };

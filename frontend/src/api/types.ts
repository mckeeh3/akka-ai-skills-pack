export type ThemePreference = 'aurora-light' | 'cobalt-light' | 'obsidian-dark' | 'midnight-dark' | 'dark-night';

export type ApiError = {
  code: string;
  message: string;
  correlationId: string;
  fieldErrors?: Record<string, string[]>;
};

export type ApiResult<T> = { ok: true; value: T } | { ok: false; error: ApiError };

export type Page<T> = {
  items: T[];
  nextPageToken?: string;
};

export type WorkStatus = 'draft' | 'planning' | 'active' | 'waiting_for_human' | 'blocked' | 'completed' | 'failed' | 'cancelled';

export type MeResponse = {
  user: {
    id: string;
    email: string;
    displayName: string;
  };
  memberships: Array<{
    tenantId: string;
    tenantName: string;
    status: 'active' | 'invited' | 'suspended';
    roles: string[];
  }>;
  activeTenantId?: string;
  preferences: {
    themeId: ThemePreference;
  };
};

export type UpdatePreferencesRequest = {
  themeId: ThemePreference;
};

export type UpdatePreferencesResponse = {
  preferences: { themeId: ThemePreference };
  correlationId: string;
};

export type AdminUser = {
  userId: string;
  email: string;
  displayName?: string;
  membershipStatus: 'active' | 'invited' | 'disabled';
  roles: string[];
  lastActiveAt?: string;
  version: number;
};

export type AdminUsersResponse = Page<AdminUser>;

export type InviteUserRequest = {
  email: string;
  displayName?: string;
  roleIds: string[];
  message?: string;
  idempotencyKey: string;
};

export type InviteUserResponse = {
  invitationId: string;
  status: 'pending' | 'resent';
  invitedEmail: string;
  correlationId: string;
};

export type UpdateRolesRequest = {
  roleIds: string[];
  reason?: string;
  expectedVersion: number;
};

export type UpdateRolesResponse = {
  userId: string;
  roleIds: string[];
  version: number;
  auditTraceId: string;
  correlationId: string;
};

export type GoalSummary = {
  goalId: string;
  objective: string;
  ownerDisplayName: string;
  priority: 'low' | 'normal' | 'high' | 'urgent';
  status: WorkStatus;
  activePlanId?: string;
  pendingDecisionCount: number;
  updatedAt: string;
};

export type GoalsResponse = Page<GoalSummary>;

export type CreateGoalRequest = {
  objective: string;
  priority: 'low' | 'normal' | 'high' | 'urgent';
  targetDate?: string;
  successCriteria: string[];
  constraints?: string[];
  requestedToolIds?: string[];
  idempotencyKey: string;
};

export type CreateGoalResponse = {
  goalId: string;
  status: 'draft';
  version: number;
  nextAction: 'draft_plan';
  correlationId: string;
};

export type ApprovalGateDto = {
  gateId: string;
  name: string;
  status: 'not_required' | 'required' | 'satisfied' | 'blocked';
  policyClauseId?: string;
};

export type TraceLinkDto = {
  traceId: string;
  label: string;
  href: string;
};

export type ExecutionPlanDto = {
  planId: string;
  version: number;
  status: 'draft' | 'under_review' | 'approved' | 'active' | 'superseded';
  steps: Array<{
    stepId: string;
    title: string;
    assignedAgent?: string;
    expectedSideEffects: string[];
    requiredApprovalGateIds: string[];
  }>;
};

export type GoalDetailResponse = {
  goal: GoalSummary & {
    successCriteria: string[];
    constraints: string[];
    targetDate?: string;
    version: number;
  };
  plan?: ExecutionPlanDto;
  approvalGates: ApprovalGateDto[];
  linkedDecisions: DecisionSummary[];
  traceLinks: TraceLinkDto[];
};

export type DraftPlanRequest = {
  guidance?: string;
  expectedGoalVersion: number;
  idempotencyKey: string;
};

export type DraftPlanResponse = {
  planJobId: string;
  status: 'queued' | 'running';
  correlationId: string;
};

export type LaunchGoalRequest = {
  planId: string;
  expectedGoalVersion: number;
  acknowledgement: boolean;
  idempotencyKey: string;
};

export type LaunchGoalResponse = {
  goalId: string;
  status: 'active';
  traceId: string;
  correlationId: string;
};

export type DecisionAction = 'approve' | 'reject' | 'request_changes' | 'escalate' | 'counter' | 'convert_to_policy_proposal';

export type DecisionSummary = {
  decisionId: string;
  type: 'recommendation_approval' | 'exception_resolution' | 'deviation_review' | 'policy_change_proposal';
  title: string;
  originatingAgent?: string;
  linkedGoalId?: string;
  priority: 'low' | 'normal' | 'high' | 'urgent';
  status: 'open' | 'resolved' | 'escalated' | 'superseded';
  riskScore?: number;
  confidenceScore?: number;
  impactEstimate?: string;
  policyTriggers: string[];
  dueAt?: string;
  version: number;
};

export type DecisionsResponse = Page<DecisionSummary>;

export type EvidenceItemDto = {
  evidenceId: string;
  label: string;
  summary: string;
  sourceType: 'trace' | 'document' | 'metric' | 'tool' | 'user_input';
  href?: string;
};

export type DecisionDetailResponse = DecisionSummary & {
  recommendation: string;
  evidenceItems: EvidenceItemDto[];
  alternativesConsidered: string[];
  allowedActions: DecisionAction[];
  traceLinks: TraceLinkDto[];
};

export type DecisionActionRequest = {
  expectedVersion: number;
  comment?: string;
  counterproposal?: string;
  acknowledgement?: boolean;
  idempotencyKey: string;
};

export type DecisionActionResponse = {
  decisionId: string;
  status: 'resolved' | 'escalated' | 'waiting_for_changes';
  action: DecisionAction;
  traceId: string;
  correlationId: string;
};

export type PolicySummary = {
  policyId: string;
  name: string;
  activeVersion: string;
  ownerRole: string;
  approvalGateCount: number;
  openProposalCount: number;
};

export type PoliciesResponse = Page<PolicySummary>;

export type CreatePolicyProposalRequest = {
  policyId: string;
  title: string;
  proposedChange: string;
  rationale: string;
  expectedImpact: string;
  simulationScope?: string;
  examples?: string[];
  idempotencyKey: string;
};

export type CreatePolicyProposalResponse = {
  proposalId: string;
  status: 'draft' | 'submitted';
  correlationId: string;
};

export type PolicySimulationResponse = {
  simulationJobId: string;
  status: 'queued' | 'running';
  correlationId: string;
};

export type TraceSummary = {
  traceId: string;
  category: 'work' | 'decision' | 'policy_invocation' | 'tool_invocation' | 'data_access' | 'approval' | 'outcome';
  timestamp: string;
  actorLabel: string;
  action: string;
  targetLabel: string;
  authorizationBasis: string;
  correlationId: string;
};

export type TraceSearchResponse = Page<TraceSummary>;

export type TraceDetailResponse = TraceSummary & {
  tenantId: string;
  policyReferences: Array<{ policyId: string; version: string; clauseId?: string }>;
  relatedLinks: TraceLinkDto[];
  safeDetails: Record<string, string | number | boolean | null>;
};

export type RealtimeTopic = 'mission-control' | 'goals' | 'decisions' | 'governance' | 'notifications';

export type RealtimeEvent = {
  eventId: string;
  tenantId: string;
  topic: RealtimeTopic;
  type: string;
  subjectId: string;
  version?: number;
  occurredAt: string;
  payload: Record<string, unknown>;
};

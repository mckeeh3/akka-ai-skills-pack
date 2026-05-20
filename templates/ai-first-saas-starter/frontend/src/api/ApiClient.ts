import type {
  AdminUsersResponse,
  ApiResult,
  CreateGoalRequest,
  CreateGoalResponse,
  CreatePolicyProposalRequest,
  CreatePolicyProposalResponse,
  DecisionAction,
  DecisionActionRequest,
  DecisionActionResponse,
  DecisionDetailResponse,
  DecisionsResponse,
  DraftPlanRequest,
  DraftPlanResponse,
  GoalDetailResponse,
  GoalsResponse,
  InviteUserRequest,
  InviteUserResponse,
  LaunchGoalRequest,
  LaunchGoalResponse,
  MeResponse,
  PoliciesResponse,
  PolicySimulationResponse,
  TraceDetailResponse,
  TraceSearchResponse,
  UpdatePreferencesRequest,
  UpdatePreferencesResponse,
  UpdateRolesRequest,
  UpdateRolesResponse
} from './types';

export interface SessionClient {
  getMe(): Promise<ApiResult<MeResponse>>;
  updatePreferences(request: UpdatePreferencesRequest): Promise<ApiResult<UpdatePreferencesResponse>>;
}

export interface AdminClient {
  listUsers(): Promise<ApiResult<AdminUsersResponse>>;
  inviteUser(request: InviteUserRequest): Promise<ApiResult<InviteUserResponse>>;
  updateRoles(userId: string, request: UpdateRolesRequest): Promise<ApiResult<UpdateRolesResponse>>;
}

export interface GoalsClient {
  listGoals(): Promise<ApiResult<GoalsResponse>>;
  getGoal(goalId: string): Promise<ApiResult<GoalDetailResponse>>;
  createGoal(request: CreateGoalRequest): Promise<ApiResult<CreateGoalResponse>>;
  draftPlan(goalId: string, request: DraftPlanRequest): Promise<ApiResult<DraftPlanResponse>>;
  launchGoal(goalId: string, request: LaunchGoalRequest): Promise<ApiResult<LaunchGoalResponse>>;
}

export interface DecisionsClient {
  listDecisions(): Promise<ApiResult<DecisionsResponse>>;
  getDecision(decisionId: string): Promise<ApiResult<DecisionDetailResponse>>;
  actOnDecision(decisionId: string, action: DecisionAction, request: DecisionActionRequest): Promise<ApiResult<DecisionActionResponse>>;
}

export interface GovernanceClient {
  listPolicies(): Promise<ApiResult<PoliciesResponse>>;
  createPolicyProposal(request: CreatePolicyProposalRequest): Promise<ApiResult<CreatePolicyProposalResponse>>;
  simulatePolicyProposal(proposalId: string): Promise<ApiResult<PolicySimulationResponse>>;
}

export interface AuditClient {
  searchTraces(query?: Record<string, string | undefined>): Promise<ApiResult<TraceSearchResponse>>;
  getTrace(traceId: string): Promise<ApiResult<TraceDetailResponse>>;
}

export interface ApiClient {
  session: SessionClient;
  admin: AdminClient;
  goals: GoalsClient;
  decisions: DecisionsClient;
  governance: GovernanceClient;
  audit: AuditClient;
}

import type {
  AdminUsersResponse,
  ApiResult,
  CreateGoalRequest,
  CreateGoalResponse,
  CreatePolicyProposalRequest,
  CreatePolicyProposalResponse,
  CustomerActionResponse,
  CustomerAdminListPayload,
  CustomerCreateRequest,
  CustomerDetailPayload,
  CustomerLifecycleRequest,
  CustomerListPayload,
  CustomerRenameRequest,
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
  OrganizationActionResponse,
  OrganizationCreateRequest,
  OrganizationDetailPayload,
  OrganizationAdminListPayload,
  OrganizationLifecycleRequest,
  OrganizationListPayload,
  OrganizationRenameRequest,
  PoliciesResponse,
  PolicySimulationResponse,
  TraceDetailResponse,
  SaasOwnerAdminListPayload,
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
  listOrganizations(query?: Record<string, string | undefined>): Promise<ApiResult<OrganizationListPayload>>;
  getOrganization(organizationId: string): Promise<ApiResult<OrganizationDetailPayload>>;
  createOrganization(request: OrganizationCreateRequest): Promise<ApiResult<OrganizationActionResponse>>;
  renameOrganization(organizationId: string, request: OrganizationRenameRequest): Promise<ApiResult<OrganizationActionResponse>>;
  suspendOrganization(organizationId: string, request: OrganizationLifecycleRequest): Promise<ApiResult<OrganizationActionResponse>>;
  reactivateOrganization(organizationId: string, request: OrganizationLifecycleRequest): Promise<ApiResult<OrganizationActionResponse>>;
  listSaasOwnerAdmins(query?: Record<string, string | undefined>): Promise<ApiResult<SaasOwnerAdminListPayload>>;
  listOrganizationAdmins(organizationId: string, query?: Record<string, string | undefined>): Promise<ApiResult<OrganizationAdminListPayload>>;
  listCustomers(query?: Record<string, string | undefined>): Promise<ApiResult<CustomerListPayload>>;
  getCustomer(customerId: string): Promise<ApiResult<CustomerDetailPayload>>;
  createCustomer(request: CustomerCreateRequest): Promise<ApiResult<CustomerActionResponse>>;
  renameCustomer(customerId: string, request: CustomerRenameRequest): Promise<ApiResult<CustomerActionResponse>>;
  suspendCustomer(customerId: string, request: CustomerLifecycleRequest): Promise<ApiResult<CustomerActionResponse>>;
  reactivateCustomer(customerId: string, request: CustomerLifecycleRequest): Promise<ApiResult<CustomerActionResponse>>;
  listCustomerAdmins(customerId: string, query?: Record<string, string | undefined>): Promise<ApiResult<CustomerAdminListPayload>>;
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

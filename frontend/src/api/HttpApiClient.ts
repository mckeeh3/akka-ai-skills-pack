import type { AdminClient, ApiClient, AuditClient, DecisionsClient, GoalsClient, GovernanceClient, SessionClient } from './ApiClient';
import type { ApiError, ApiResult, DecisionAction } from './types';

export type TokenProvider = () => Promise<string | undefined> | string | undefined;
export type SelectedContextIdProvider = () => string | undefined;

export class HttpApiClient implements ApiClient {
  session: SessionClient;
  admin: AdminClient;
  goals: GoalsClient;
  decisions: DecisionsClient;
  governance: GovernanceClient;
  audit: AuditClient;

  constructor(private readonly tokenProvider?: TokenProvider, private readonly selectedContextIdProvider?: SelectedContextIdProvider) {
    this.session = {
      getMe: () => this.get('/api/me'),
      updatePreferences: (request) => this.put('/api/me/preferences', request)
    };
    this.admin = {
      listUsers: () => this.get('/api/admin/users'),
      inviteUser: (request) => this.post('/api/admin/users/invitations', request),
      updateRoles: (userId, request) => this.put(`/api/admin/users/${encodeURIComponent(userId)}/roles`, request),
      listOrganizations: (query) => this.get(`/api/admin/organizations${queryString(query)}`),
      getOrganization: (organizationId) => this.get(`/api/admin/organizations/${encodeURIComponent(organizationId)}`),
      createOrganization: (request) => this.post('/api/admin/organizations', request),
      renameOrganization: (organizationId, request) => this.post(`/api/admin/organizations/${encodeURIComponent(organizationId)}/rename`, request),
      suspendOrganization: (organizationId, request) => this.post(`/api/admin/organizations/${encodeURIComponent(organizationId)}/suspend`, request),
      reactivateOrganization: (organizationId, request) => this.post(`/api/admin/organizations/${encodeURIComponent(organizationId)}/reactivate`, request),
      listSaasOwnerAdmins: (query) => this.get(`/api/admin/saas-owner-admins${queryString(query)}`),
      inviteSaasOwnerAdmin: (request) => this.post('/api/admin/saas-owner-admins/invitations', request),
      resendSaasOwnerAdminInvitation: (invitationId, request) => this.post(`/api/admin/saas-owner-admins/invitations/${encodeURIComponent(invitationId)}/resend`, request),
      revokeSaasOwnerAdminInvitation: (invitationId, request) => this.post(`/api/admin/saas-owner-admins/invitations/${encodeURIComponent(invitationId)}/revoke`, request),
      listOrganizationAdmins: (organizationId, query) => this.get(`/api/admin/organizations/${encodeURIComponent(organizationId)}/admins${queryString(query)}`),
      inviteOrganizationAdmin: (organizationId, request) => this.post(`/api/admin/organizations/${encodeURIComponent(organizationId)}/admins/invitations`, request),
      resendOrganizationAdminInvitation: (organizationId, invitationId, request) => this.post(`/api/admin/organizations/${encodeURIComponent(organizationId)}/admins/invitations/${encodeURIComponent(invitationId)}/resend`, request),
      revokeOrganizationAdminInvitation: (organizationId, invitationId, request) => this.post(`/api/admin/organizations/${encodeURIComponent(organizationId)}/admins/invitations/${encodeURIComponent(invitationId)}/revoke`, request),
      changeOrganizationAdminRoles: (organizationId, accountId, request) => this.put(`/api/admin/organizations/${encodeURIComponent(organizationId)}/admins/${encodeURIComponent(accountId)}/roles`, request),
      changeOrganizationAdminStatus: (organizationId, accountId, status, request) => this.post(`/api/admin/organizations/${encodeURIComponent(organizationId)}/admins/${encodeURIComponent(accountId)}/${status}`, request),
      listCustomers: (query) => this.get(`/api/admin/customers${queryString(query)}`),
      getCustomer: (customerId) => this.get(`/api/admin/customers/${encodeURIComponent(customerId)}`),
      createCustomer: (request) => this.post('/api/admin/customers', request),
      renameCustomer: (customerId, request) => this.post(`/api/admin/customers/${encodeURIComponent(customerId)}/rename`, request),
      suspendCustomer: (customerId, request) => this.post(`/api/admin/customers/${encodeURIComponent(customerId)}/suspend`, request),
      reactivateCustomer: (customerId, request) => this.post(`/api/admin/customers/${encodeURIComponent(customerId)}/reactivate`, request),
      listCustomerAdmins: (customerId, query) => this.get(`/api/admin/customers/${encodeURIComponent(customerId)}/admins${queryString(query)}`),
      inviteCustomerAdmin: (customerId, request) => this.post(`/api/admin/customers/${encodeURIComponent(customerId)}/admins/invitations`, request),
      resendCustomerAdminInvitation: (customerId, invitationId, request) => this.post(`/api/admin/customers/${encodeURIComponent(customerId)}/admins/invitations/${encodeURIComponent(invitationId)}/resend`, request),
      revokeCustomerAdminInvitation: (customerId, invitationId, request) => this.post(`/api/admin/customers/${encodeURIComponent(customerId)}/admins/invitations/${encodeURIComponent(invitationId)}/revoke`, request),
      changeCustomerAdminRoles: (customerId, accountId, request) => this.put(`/api/admin/customers/${encodeURIComponent(customerId)}/admins/${encodeURIComponent(accountId)}/roles`, request),
      changeCustomerAdminStatus: (customerId, accountId, status, request) => this.post(`/api/admin/customers/${encodeURIComponent(customerId)}/admins/${encodeURIComponent(accountId)}/${status}`, request)
    };
    this.goals = {
      listGoals: () => this.get('/api/goals'),
      getGoal: (goalId) => this.get(`/api/goals/${encodeURIComponent(goalId)}`),
      createGoal: (request) => this.post('/api/goals', request),
      draftPlan: (goalId, request) => this.post(`/api/goals/${encodeURIComponent(goalId)}/draft-plan`, request),
      launchGoal: (goalId, request) => this.post(`/api/goals/${encodeURIComponent(goalId)}/launch`, request)
    };
    this.decisions = {
      listDecisions: () => this.get('/api/decisions'),
      getDecision: (decisionId) => this.get(`/api/decisions/${encodeURIComponent(decisionId)}`),
      actOnDecision: (decisionId, action, request) => this.post(decisionActionPath(decisionId, action), request)
    };
    this.governance = {
      listPolicies: () => this.get('/api/governance/policies'),
      createPolicyProposal: (request) => this.post('/api/governance/policy-proposals', request),
      simulatePolicyProposal: (proposalId) => this.post(`/api/governance/policy-proposals/${encodeURIComponent(proposalId)}/simulate`, {})
    };
    this.audit = {
      searchTraces: (query) => this.get(`/api/audit/traces${queryString(query)}`),
      getTrace: (traceId) => this.get(`/api/audit/traces/${encodeURIComponent(traceId)}`)
    };
  }

  private get<T>(path: string): Promise<ApiResult<T>> {
    return this.request<T>(path);
  }

  private post<T>(path: string, body: unknown): Promise<ApiResult<T>> {
    return this.request<T>(path, { method: 'POST', body: JSON.stringify(body) });
  }

  private put<T>(path: string, body: unknown): Promise<ApiResult<T>> {
    return this.request<T>(path, { method: 'PUT', body: JSON.stringify(body) });
  }

  private async request<T>(path: string, init: RequestInit = {}): Promise<ApiResult<T>> {
    try {
      const token = await this.tokenProvider?.();
      const headers = new Headers(init.headers);
      headers.set('Accept', 'application/json');
      if (init.body) headers.set('Content-Type', 'application/json');
      if (token) headers.set('Authorization', `Bearer ${token}`);
      const selectedContextId = this.selectedContextIdProvider?.();
      if (selectedContextId) headers.set('X-Selected-Context-Id', selectedContextId);
      if (!headers.has('X-Correlation-Id')) headers.set('X-Correlation-Id', `corr-browser-${Date.now().toString(36)}`);
      const response = await fetch(path, { ...init, headers });
      if (!response.ok) return { ok: false, error: await mapApiError(response) };
      return { ok: true, value: await response.json() as T };
    } catch (error) {
      return {
        ok: false,
        error: {
          code: 'network_error',
          message: error instanceof Error ? error.message : String(error),
          correlationId: 'client-network-error'
        }
      };
    }
  }
}

function decisionActionPath(decisionId: string, action: DecisionAction) {
  const actionPath = action === 'request_changes' ? 'request-changes' : action.replace(/_/g, '-');
  return `/api/decisions/${encodeURIComponent(decisionId)}/${actionPath}`;
}

function queryString(query?: Record<string, string | undefined>) {
  const params = new URLSearchParams();
  Object.entries(query ?? {}).forEach(([key, value]) => {
    if (value) params.set(key, value);
  });
  const text = params.toString();
  return text ? `?${text}` : '';
}

async function mapApiError(response: Response): Promise<ApiError> {
  const parsed = await parseJsonOrText(response);
  return {
    code: parsed.code ?? `http_${response.status}`,
    message: parsed.message ?? `HTTP ${response.status}`,
    correlationId: parsed.correlationId ?? response.headers.get('x-correlation-id') ?? 'missing-correlation-id',
    fieldErrors: parsed.fieldErrors
  };
}

async function parseJsonOrText(response: Response): Promise<Partial<ApiError>> {
  const body = await response.text();
  if (!body) return {};
  try {
    const parsed = JSON.parse(body) as unknown;
    return parsed && typeof parsed === 'object' && !Array.isArray(parsed) ? parsed as Partial<ApiError> : { message: body };
  } catch {
    return { message: body };
  }
}

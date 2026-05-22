import type { AdminClient, ApiClient, AuditClient, DecisionsClient, GoalsClient, GovernanceClient, SessionClient } from './ApiClient';
import type { ApiError, ApiResult, DecisionAction } from './types';

export type TokenProvider = () => Promise<string | undefined> | string | undefined;

export class HttpApiClient implements ApiClient {
  session: SessionClient;
  admin: AdminClient;
  goals: GoalsClient;
  decisions: DecisionsClient;
  governance: GovernanceClient;
  audit: AuditClient;

  constructor(private readonly tokenProvider?: TokenProvider) {
    this.session = {
      getMe: () => this.get('/api/me'),
      updatePreferences: (request) => this.put('/api/me/preferences', request)
    };
    this.admin = {
      listUsers: () => this.get('/api/admin/users'),
      inviteUser: (request) => this.post('/api/admin/users/invitations', request),
      updateRoles: (userId, request) => this.put(`/api/admin/users/${encodeURIComponent(userId)}/roles`, request)
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
  const parsed = await parseErrorBody(response);
  return {
    code: typeof parsed.code === 'string' ? parsed.code : `http_${response.status}`,
    message: typeof parsed.message === 'string' ? parsed.message : `HTTP ${response.status}`,
    correlationId: typeof parsed.correlationId === 'string' ? parsed.correlationId : response.headers.get('x-correlation-id') ?? 'missing-correlation-id',
    fieldErrors: isFieldErrors(parsed.fieldErrors) ? parsed.fieldErrors : undefined
  };
}

async function parseErrorBody(response: Response): Promise<Record<string, unknown>> {
  const text = await response.text();
  if (!text) return {};
  try {
    const parsed = JSON.parse(text) as unknown;
    return parsed && typeof parsed === 'object' && !Array.isArray(parsed) ? parsed as Record<string, unknown> : { message: text };
  } catch {
    return { message: text };
  }
}

function isFieldErrors(value: unknown): value is Record<string, string[]> {
  if (!value || typeof value !== 'object' || Array.isArray(value)) return false;
  return Object.values(value).every((entry) => Array.isArray(entry) && entry.every((item) => typeof item === 'string'));
}

import type { WorkstreamBootstrapResponse, WorkstreamClient, WorkstreamMessageRequest, WorkstreamMessageResponse } from './WorkstreamApiClient';
import type { ApiError, ApiResult } from './types';
import type {
  CapabilityActionRequest,
  CapabilityActionResult,
  ChatToolPlanConfirmationRequest,
  FunctionalAgentSummary,
  MeResponse,
  SurfaceEnvelope,
  WorkstreamItem,
  WorkstreamShellRequest,
  WorkstreamShellResponse
} from '../workstream/types';
import type { TokenProvider } from './HttpApiClient';

export class HttpWorkstreamApiClient implements WorkstreamClient {
  private selectedContextId: string | undefined;

  constructor(private readonly tokenProvider?: TokenProvider) {}

  async bootstrap(): Promise<ApiResult<WorkstreamBootstrapResponse>> {
    const result = await this.get<WorkstreamBootstrapResponse>('/api/workstream/bootstrap');
    if (result.ok) this.selectedContextId = result.value.me.selectedAuthContext.selectedContextId;
    return result;
  }

  async getMe(): Promise<ApiResult<MeResponse>> {
    const result = await this.get<MeResponse>('/api/me');
    if (result.ok) this.selectedContextId = result.value.selectedAuthContext.selectedContextId;
    return result;
  }

  listFunctionalAgents(): Promise<ApiResult<FunctionalAgentSummary[]>> {
    return this.get('/api/workstream/functional-agents');
  }

  listWorkstreamItems(functionalAgentId?: string): Promise<ApiResult<WorkstreamItem[]>> {
    const query = functionalAgentId ? `?functionalAgentId=${encodeURIComponent(functionalAgentId)}` : '';
    return this.get(`/api/workstream/items${query}`);
  }

  getSurface(surfaceId: string): Promise<ApiResult<SurfaceEnvelope<unknown>>> {
    return this.get(`/api/workstream/surfaces/${encodeURIComponent(surfaceId)}`);
  }

  async runCapabilityAction(request: CapabilityActionRequest): Promise<ApiResult<CapabilityActionResult>> {
    if (request.selectedContextId) this.selectedContextId = request.selectedContextId;
    return this.post('/api/workstream/actions', request);
  }

  async runShellRequest(request: WorkstreamShellRequest): Promise<ApiResult<WorkstreamShellResponse>> {
    if (request.selectedContextId) this.selectedContextId = request.selectedContextId;
    return this.post('/api/workstream/shell-requests', request);
  }

  async submitWorkstreamMessage(request: WorkstreamMessageRequest): Promise<ApiResult<WorkstreamMessageResponse>> {
    if (request.selectedContextId) this.selectedContextId = request.selectedContextId;
    return this.post('/api/workstream/messages', request);
  }

  async confirmChatToolPlan(request: ChatToolPlanConfirmationRequest): Promise<ApiResult<WorkstreamMessageResponse>> {
    if (request.selectedContextId) this.selectedContextId = request.selectedContextId;
    return this.post('/api/workstream/chat-tool-plans/confirm', request);
  }

  private get<T>(path: string): Promise<ApiResult<T>> {
    return this.request<T>(path);
  }

  private post<T>(path: string, body: unknown): Promise<ApiResult<T>> {
    return this.request<T>(path, { method: 'POST', body: JSON.stringify(body) });
  }

  private async request<T>(path: string, init: RequestInit = {}): Promise<ApiResult<T>> {
    try {
      const token = await this.tokenProvider?.();
      const headers = new Headers(init.headers);
      headers.set('Accept', 'application/json');
      if (init.body) headers.set('Content-Type', 'application/json');
      if (token) headers.set('Authorization', `Bearer ${token}`);
      if (this.selectedContextId) headers.set('X-Selected-Context-Id', this.selectedContextId);
      headers.set('X-Correlation-Id', `corr-browser-${Date.now().toString(36)}`);
      const response = await fetch(path, { ...init, headers });
      if (!response.ok) return { ok: false, error: await mapWorkstreamApiError(response) };
      const value = response.status === 204 ? undefined : await response.json();
      return { ok: true, value: value as T };
    } catch (error) {
      return {
        ok: false,
        error: {
          code: 'network',
          message: error instanceof Error ? error.message : String(error),
          correlationId: 'client-network-error'
        }
      };
    }
  }
}

async function mapWorkstreamApiError(response: Response): Promise<ApiError> {
  const parsed = await parseJsonOrText(response);
  const reasonCode = typeof parsed.reasonCode === 'string' ? parsed.reasonCode : undefined;
  const code = reasonCode ?? (typeof parsed.code === 'string' ? parsed.code : httpCode(response.status));
  return {
    code,
    message: typeof parsed.message === 'string' ? parsed.message : reasonCode ?? `HTTP ${response.status}`,
    correlationId: typeof parsed.correlationId === 'string' ? parsed.correlationId : response.headers.get('x-correlation-id') ?? 'missing-correlation-id',
    fieldErrors: isFieldErrors(parsed.fieldErrors) ? parsed.fieldErrors : undefined
  };
}

async function parseJsonOrText(response: Response): Promise<Record<string, unknown>> {
  const body = await response.text();
  if (!body) return {};
  try {
    const parsed = JSON.parse(body) as unknown;
    return parsed && typeof parsed === 'object' && !Array.isArray(parsed) ? parsed as Record<string, unknown> : { message: body };
  } catch {
    return { message: body };
  }
}

function isFieldErrors(value: unknown): value is Record<string, string[]> {
  if (!value || typeof value !== 'object' || Array.isArray(value)) return false;
  return Object.values(value).every((entry) => Array.isArray(entry) && entry.every((item) => typeof item === 'string'));
}

function httpCode(status: number) {
  if (status === 401) return 'unauthorized';
  if (status === 403) return 'forbidden';
  if (status === 404) return 'not_found';
  if (status === 409) return 'conflict';
  if (status === 400) return 'validation';
  return `http_${status}`;
}

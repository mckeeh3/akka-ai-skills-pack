import type {
  CapabilityActionRequest,
  CapabilityActionResult,
  ComposerRequest,
  FunctionalAgentSummary,
  MeResponse as WorkstreamMeResponse,
  SurfaceEnvelope,
  WorkstreamItem,
  WorkstreamShellRequest,
  WorkstreamShellResponse
} from '../workstream/types';
import type { ApiResult } from './types';

export type WorkstreamBootstrapResponse = {
  me: WorkstreamMeResponse;
  functionalAgents: FunctionalAgentSummary[];
  items: WorkstreamItem[];
  surfaces: SurfaceEnvelope<unknown>[];
};

export type WorkstreamMessageRequest = ComposerRequest & {
  correlationId?: string;
};

export type WorkstreamMessageResponse = {
  correlationId: string;
  idempotencyKey: string;
  userItem: WorkstreamItem;
  agentItem: WorkstreamItem;
  surface: SurfaceEnvelope<unknown>;
};

export type WorkstreamClient = {
  bootstrap(): Promise<ApiResult<WorkstreamBootstrapResponse>>;
  getMe(): Promise<ApiResult<WorkstreamMeResponse>>;
  listFunctionalAgents(): Promise<ApiResult<FunctionalAgentSummary[]>>;
  listWorkstreamItems(functionalAgentId?: string): Promise<ApiResult<WorkstreamItem[]>>;
  getSurface(surfaceId: string): Promise<ApiResult<SurfaceEnvelope<unknown>>>;
  runCapabilityAction(request: CapabilityActionRequest): Promise<ApiResult<CapabilityActionResult>>;
  runShellRequest(request: WorkstreamShellRequest): Promise<ApiResult<WorkstreamShellResponse>>;
  submitWorkstreamMessage(request: WorkstreamMessageRequest): Promise<ApiResult<WorkstreamMessageResponse>>;
};

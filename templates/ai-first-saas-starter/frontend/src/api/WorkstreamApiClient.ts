import type {
  CapabilityActionRequest,
  CapabilityActionResult,
  FunctionalAgentSummary,
  MeResponse as WorkstreamMeResponse,
  SurfaceEnvelope,
  WorkstreamItem
} from '../workstream/types';
import type { ApiResult } from './types';

export type WorkstreamBootstrapResponse = {
  me: WorkstreamMeResponse;
  functionalAgents: FunctionalAgentSummary[];
  items: WorkstreamItem[];
  surfaces: SurfaceEnvelope<unknown>[];
};

export type WorkstreamClient = {
  bootstrap(): Promise<ApiResult<WorkstreamBootstrapResponse>>;
  getMe(): Promise<ApiResult<WorkstreamMeResponse>>;
  listFunctionalAgents(): Promise<ApiResult<FunctionalAgentSummary[]>>;
  listWorkstreamItems(functionalAgentId?: string): Promise<ApiResult<WorkstreamItem[]>>;
  getSurface(surfaceId: string): Promise<ApiResult<SurfaceEnvelope<unknown>>>;
  runCapabilityAction(request: CapabilityActionRequest): Promise<ApiResult<CapabilityActionResult>>;
};

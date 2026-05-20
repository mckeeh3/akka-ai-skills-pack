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

export type AcceptInvitationRequest = {
  token?: string;
  acceptanceContextId?: string;
};

export type InvitationAcceptanceStatus = 'accepted' | 'already-accepted' | 'already-accepted-by-other-account' | 'expired' | 'revoked' | 'wrong-account' | 'invalid';

export type InvitationAcceptanceResult = {
  status: InvitationAcceptanceStatus;
  reasonCode: string;
  recoveryHint: string;
  invitationId?: string;
  scopeType?: 'SAAS_OWNER' | 'TENANT' | 'CUSTOMER';
  tenantId?: string;
  customerId?: string;
  membershipId?: string;
  expiresAt?: string;
  correlationId: string;
};

export type WorkstreamClient = {
  bootstrap(): Promise<ApiResult<WorkstreamBootstrapResponse>>;
  getMe(): Promise<ApiResult<WorkstreamMeResponse>>;
  listFunctionalAgents(): Promise<ApiResult<FunctionalAgentSummary[]>>;
  listWorkstreamItems(functionalAgentId?: string): Promise<ApiResult<WorkstreamItem[]>>;
  getSurface(surfaceId: string): Promise<ApiResult<SurfaceEnvelope<unknown>>>;
  runCapabilityAction(request: CapabilityActionRequest): Promise<ApiResult<CapabilityActionResult>>;
  acceptInvitation(request: AcceptInvitationRequest): Promise<ApiResult<InvitationAcceptanceResult>>;
};

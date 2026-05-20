import type { AcceptInvitationRequest, InvitationAcceptanceResult, WorkstreamClient, WorkstreamBootstrapResponse } from './WorkstreamApiClient';
import type { ApiError, ApiResult } from './types';
import type { CapabilityActionRequest, CapabilityActionResult, SurfaceEnvelope, WorkstreamItem } from '../workstream/types';
import {
  actionResultsByStatus,
  allSurfaceActions,
  canonicalSurfaceEnvelopes,
  displayAgentCatalogActionResult,
  displayAgentDetailActionResult,
  displayUserDetailActionResult,
  displayUserListActionResult,
  initialWorkstreamItems,
  meTenantAdmin
} from '../workstream/fixtures';

export class FixtureWorkstreamApiClient implements WorkstreamClient {
  private items: WorkstreamItem[] = [...initialWorkstreamItems];
  private surfaces: SurfaceEnvelope<unknown>[] = canonicalSurfaceEnvelopes as SurfaceEnvelope<unknown>[];

  bootstrap(): Promise<ApiResult<WorkstreamBootstrapResponse>> {
    return delayedOk({
      me: meTenantAdmin,
      functionalAgents: meTenantAdmin.functionalAgents,
      items: this.items,
      surfaces: this.surfaces
    });
  }

  getMe() {
    return delayedOk(meTenantAdmin);
  }

  listFunctionalAgents() {
    return delayedOk(meTenantAdmin.functionalAgents);
  }

  listWorkstreamItems(functionalAgentId?: string) {
    const visibleItems = functionalAgentId ? this.items.filter((item) => item.functionalAgentId === functionalAgentId) : this.items;
    return delayedOk(visibleItems);
  }

  getSurface(surfaceId: string) {
    const surface = this.surfaces.find((candidate) => candidate.surfaceId === surfaceId);
    return surface ? delayedOk(surface) : delayedError('not_found', 'The requested workstream surface is not available in this context.');
  }

  acceptInvitation(request: AcceptInvitationRequest): Promise<ApiResult<InvitationAcceptanceResult>> {
    const status: InvitationAcceptanceResult['status'] = request.token === 'expired'
      ? 'expired'
      : request.token === 'revoked'
        ? 'revoked'
        : request.token === 'wrong-account'
          ? 'wrong-account'
          : 'accepted';
    return delayedOk({
      status,
      reasonCode: status,
      recoveryHint: status === 'accepted' ? 'Fixture invitation accepted.' : 'Fixture recovery state; request a fresh invitation or sign in with the invited account.',
      invitationId: status === 'accepted' ? 'fixture-invitation' : undefined,
      scopeType: 'TENANT',
      tenantId: meTenantAdmin.selectedAuthContext.tenantId,
      membershipId: meTenantAdmin.selectedAuthContext.membershipId,
      expiresAt: new Date(Date.now() + 3_600_000).toISOString(),
      correlationId: `corr-fixture-accept-${Date.now().toString(36)}`
    });
  }

  runCapabilityAction(request: CapabilityActionRequest): Promise<ApiResult<CapabilityActionResult>> {
    const action = allSurfaceActions.find((candidate) => candidate.actionId === request.actionId || candidate.capabilityId === request.capabilityId);
    if (!action) return delayedError('not_found', 'The requested capability action is not exposed by this surface.');
    if (action.disabled) return delayedOk({ ...actionResultsByStatus.denied, message: action.disabled.message, correlationId: request.correlationId });
    const result = request.actionId === 'action-display-user-detail'
      ? displayUserDetailActionResult
      : request.actionId === 'action-display-agent-detail' || request.actionId === 'action-open-agent-detail'
        ? displayAgentDetailActionResult
        : request.actionId === 'action-display-agent-catalog' || request.capabilityId === 'agent.definitions.manage'
          ? displayAgentCatalogActionResult
          : request.actionId === 'action-display-user-list' || request.actionId === 'action-search-users'
            ? displayUserListActionResult
            : request.capabilityId === 'governance-decisions-audit' || request.capabilityId === 'decision.approve'
            ? actionResultsByStatus['approval-required']
            : actionResultsByStatus.accepted;
    const response = { ...result, correlationId: request.correlationId };
    this.items = [
      ...this.items,
      {
        itemId: `fixture-action-${request.actionId}-${Date.now()}`,
        functionalAgentId: surfaceOwnerFor(request.surfaceId, this.surfaces) ?? 'agent-user-admin',
        kind: 'action-feedback',
        createdAt: new Date().toISOString(),
        correlationId: response.correlationId,
        traceIds: response.traceIds,
        surfaceId: response.resultSurface?.surfaceId ?? request.surfaceId,
        title: `${action.label} ${response.status}`,
        body: response.message,
        status: response.status === 'accepted' ? 'ready' : 'waiting-for-human'
      }
    ];
    if (response.resultSurface && !this.surfaces.some((surface) => surface.surfaceId === response.resultSurface?.surfaceId)) {
      this.surfaces = [...this.surfaces, response.resultSurface];
    }
    return delayedOk(response);
  }
}

function surfaceOwnerFor(surfaceId: string | undefined, surfaces: SurfaceEnvelope<unknown>[]) {
  return surfaces.find((surface) => surface.surfaceId === surfaceId)?.ownerFunctionalAgentId;
}

function delayedOk<T>(value: T): Promise<ApiResult<T>> {
  return new Promise((resolve) => window.setTimeout(() => resolve({ ok: true, value }), 20));
}

function delayedError(code: string, message: string): Promise<ApiResult<never>> {
  const error: ApiError = { code, message, correlationId: `corr-${Math.random().toString(16).slice(2)}` };
  return new Promise((resolve) => window.setTimeout(() => resolve({ ok: false, error }), 20));
}

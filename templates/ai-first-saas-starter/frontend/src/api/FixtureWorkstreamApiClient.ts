import type { WorkstreamClient, WorkstreamBootstrapResponse, WorkstreamMessageRequest, WorkstreamMessageResponse } from './WorkstreamApiClient';
import type { ApiError, ApiResult } from './types';
import type { CapabilityActionRequest, CapabilityActionResult, MarkdownResponseData, SurfaceEnvelope, WorkstreamItem } from '../workstream/types';
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

const fixtureMessageCapableAgentIds = ['agent-access-profile', 'agent-user-admin', 'agent-agent-admin', 'agent-audit-trace', 'agent-governance-policy'];

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

  submitWorkstreamMessage(request: WorkstreamMessageRequest): Promise<ApiResult<WorkstreamMessageResponse>> {
    const agent = meTenantAdmin.functionalAgents.find((candidate) => candidate.functionalAgentId === request.functionalAgentId);
    if (!fixtureMessageCapableAgentIds.includes(request.functionalAgentId) || !agent || agent.availability === 'hidden' || agent.availability === 'denied' || agent.availability === 'disabled') {
      return delayedError('forbidden', 'The selected functional agent is not available for this context.');
    }
    if (request.selectedContextId !== meTenantAdmin.selectedAuthContext.selectedContextId) {
      return delayedError('forbidden', 'The selected context does not match the active fixture session.');
    }
    const now = new Date().toISOString();
    const suffix = `${request.functionalAgentId}-${Math.abs(hashText(`${request.idempotencyKey}:${request.prompt}`)).toString(36)}`;
    const correlationId = request.correlationId || `corr-${suffix}`;
    const traceIds = [`trace-${suffix}`];
    const userItem: WorkstreamItem = {
      itemId: `user-${suffix}`,
      functionalAgentId: request.functionalAgentId,
      kind: 'user-request',
      createdAt: now,
      correlationId,
      traceIds: [],
      title: 'You',
      body: request.prompt,
      status: 'ready'
    };
    const surface: SurfaceEnvelope<MarkdownResponseData> = {
      surfaceId: `surface-message-${suffix}`,
      surfaceType: 'markdown_response',
      surfaceVersion: 'v1',
      title: `${agent.label} response`,
      ownerFunctionalAgentId: request.functionalAgentId,
      reusableByFunctionalAgentIds: ['agent-audit-trace'],
      authContext: {
        tenantId: meTenantAdmin.selectedAuthContext.tenantId,
        customerId: meTenantAdmin.selectedAuthContext.customerId,
        selectedContextId: request.selectedContextId,
        visibleCapabilityIds: meTenantAdmin.visibleCapabilityIds
      },
      correlationId,
      traceIds,
      generatedAt: now,
      redaction: { profile: 'tenant-admin' },
      data: {
        markdown: `## ${agent.label} v0 response\n\nYou asked: **${request.prompt.trim()}**\n\n- This fixture response uses the same \`markdown_response\` contract as \`/api/workstream/messages\`.\n- Richer surface actions remain explicit follow-up/full-core behavior.`,
        title: `${agent.label} v0 response`,
        summary: 'Fixture backend-equivalent markdown response for the selected functional agent.',
        workstreamEntryId: `agent-${suffix}`,
        producingAgentId: request.functionalAgentId,
        safety: { sanitized: true, blockedUnsafeLinks: 0, blockedRawHtml: true },
        trace: { correlationId, traceIds }
      },
      actions: [],
      links: [{ label: 'Trace', href: `/ui?traceId=${encodeURIComponent(traceIds[0])}`, rel: 'trace' }]
    };
    const agentItem: WorkstreamItem = {
      itemId: `agent-${suffix}`,
      functionalAgentId: request.functionalAgentId,
      kind: 'markdown_response',
      createdAt: now,
      correlationId,
      traceIds,
      surfaceId: surface.surfaceId,
      title: `${agent.label} response`,
      body: 'Fixture backend-equivalent markdown response returned through the workstream message API contract.',
      status: 'ready'
    };
    this.items = [...this.items, userItem, agentItem];
    this.surfaces = [...this.surfaces.filter((candidate) => candidate.surfaceId !== surface.surfaceId), surface];
    return delayedOk({ correlationId, idempotencyKey: request.idempotencyKey, userItem, agentItem, surface });
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

function hashText(value: string) {
  let hash = 0;
  for (let index = 0; index < value.length; index += 1) {
    hash = ((hash << 5) - hash) + value.charCodeAt(index);
    hash |= 0;
  }
  return hash;
}

function delayedOk<T>(value: T): Promise<ApiResult<T>> {
  return new Promise((resolve) => window.setTimeout(() => resolve({ ok: true, value }), 20));
}

function delayedError(code: string, message: string): Promise<ApiResult<never>> {
  const error: ApiError = { code, message, correlationId: `corr-${Math.random().toString(16).slice(2)}` };
  return new Promise((resolve) => window.setTimeout(() => resolve({ ok: false, error }), 20));
}

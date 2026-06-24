import type { WorkstreamClient, WorkstreamBootstrapResponse, WorkstreamMessageRequest, WorkstreamMessageResponse } from '../../../api/WorkstreamApiClient';
import type { ApiError, ApiResult } from '../../../api/types';
import type { CapabilityActionRequest, CapabilityActionResult, ChatToolPlanConfirmationRequest, ChatToolPlanExecutionResult, ChatToolPlanSurfaceData, MarkdownResponseData, SurfaceEnvelope, WorkstreamItem, WorkstreamShellRequest, WorkstreamShellResponse } from '../../../workstream/types';
import {
  actionResultsByStatus,
  allSurfaceActions,
  canonicalSurfaceEnvelopes,
  displayAgentAdminTraceActionResult,
  displayAgentActivationConfirmationActionResult,
  displayAgentBehaviorProposalActionResult,
  displayAgentBlankActionResult,
  displayAgentDashboardActionResult,
  displayAgentDeactivationConfirmationActionResult,
  displayAgentCatalogActionResult,
  displayAgentCreateReferenceDocActionResult,
  displayAgentCreateSkillActionResult,
  displayAgentDeleteReferenceDocActionResult,
  displayAgentDeleteSkillActionResult,
  displayAgentDetailActionResult,
  displayAgentEditSessionActionResult,
  displayAgentManifestActionResult,
  displayAgentModelRefsActionResult,
  displayAgentPromptDocActionResult,
  displayAgentPromptGovernanceActionResult,
  displayAgentPromptRiskReviewActionResult,
  displayAgentReferenceDocActionResult,
  displayAgentRollbackConfirmationActionResult,
  displayAgentRuntimeTracesActionResult,
  displayAgentSeedMaterialActionResult,
  displayAgentSkillDocActionResult,
  displayAgentTestConsoleActionResult,
  displayAgentToolBoundaryActionResult,
  displayAgentVersionDiffActionResult,
  displayAgentVersionHistoryActionResult,
  displayMyAccountDashboardActionResult,
  displayMyAccountProfileActionResult,
  displayMyAccountSettingsActionResult,
  updateMyAccountSettingsActionResult,
  userAdminAccessReviewSurface,
  userAdminInvitationActionStatusSurface,
  userAdminInvitationCreateSurface,
  userAdminMemberStatusActionSurface,
  userAdminRoleChangeActionSurface,
  userAdminRoleChangePreviewSurface,
  displayGovernancePolicyDashboardActionResult,
  displayGovernancePolicyInventoryActionResult,
  displayGovernancePolicyImpactResultActionResult,
  displayGovernancePolicyImpactTaskActionResult,
  displayGovernancePolicySimulationActionResult,
  displayOrganizationAdminActionResult,
  displayUserDetailActionResult,
  displayUserListActionResult,
  initialWorkstreamItems,
  meTenantAdmin
} from '../workstream';

const fixtureMessageCapableAgentIds = ['my-account-agent', 'user-admin-agent', 'agent-admin-agent', 'audit-trace-agent', 'governance-policy-agent'];

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
      reusableByFunctionalAgentIds: ['audit-trace-agent'],
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
        markdown: `## ${agent.label} response\n\nYou asked: **${request.prompt.trim()}**\n\n- This fixture response uses the same \`markdown_response\` contract as \`/api/workstream/messages\`.\n- Richer surface actions remain explicit follow-up/full-core behavior.`,
        title: `${agent.label} response`,
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

  confirmChatToolPlan(request: ChatToolPlanConfirmationRequest): Promise<ApiResult<WorkstreamMessageResponse>> {
    if (request.selectedContextId !== meTenantAdmin.selectedAuthContext.selectedContextId) {
      return delayedError('forbidden', 'The selected context does not match the active fixture session.');
    }
    if (request.confirmationText !== `CONFIRM ${request.planSnapshotId}`) {
      return delayedError('validation', 'The confirmation phrase must match the immutable plan snapshot.');
    }
    const now = new Date().toISOString();
    const traceIds = [`trace-chat-tool-plan-confirm-${Math.abs(hashText(request.idempotencyKey)).toString(36)}`];
    const result: ChatToolPlanExecutionResult = {
      planId: request.planId,
      planSnapshotId: request.planSnapshotId,
      status: 'completed',
      completedSteps: [{ stepId: 'step-1', status: 'completed', message: 'Fixture confirmation completed through the backend-equivalent plan confirmation contract.', actionId: 'action-submit-organization-create', governedToolId: 'manage-organizations', capabilityId: 'saas_owner.tenant.manage', resultSurfaceId: 'surface-user-admin-organization-detail', traceIds }],
      failedSteps: [],
      skippedSteps: [],
      recoverySteps: [],
      resultSurfaceIds: ['surface-user-admin-organization-detail'],
      traceIds,
      correlationId: request.correlationId
    };
    const surface: SurfaceEnvelope<ChatToolPlanSurfaceData> = {
      surfaceId: `surface-chat-tool-plan-result-${request.planId}`,
      surfaceType: 'chat_tool_plan_result',
      surfaceVersion: 'v1',
      title: 'Chat tool plan result',
      ownerFunctionalAgentId: 'user-admin-agent',
      reusableByFunctionalAgentIds: ['audit-trace-agent'],
      authContext: {
        tenantId: meTenantAdmin.selectedAuthContext.tenantId,
        customerId: meTenantAdmin.selectedAuthContext.customerId,
        selectedContextId: request.selectedContextId,
        visibleCapabilityIds: meTenantAdmin.visibleCapabilityIds
      },
      correlationId: request.correlationId,
      traceIds,
      generatedAt: now,
      redaction: { profile: 'tenant-admin' },
      data: { surfaceContract: 'chat_tool_plan.result.v1', status: 'completed', proposal: null, confirmationSnapshot: null, result, systemMessage: null, noDirectMutation: true, noMutation: false, executionEnabled: false, sideEffect: 'fixture result only', traceRefs: traceIds },
      actions: [],
      links: [{ label: 'Trace', href: `/ui?traceId=${encodeURIComponent(traceIds[0])}`, rel: 'trace' }]
    };
    const userItem: WorkstreamItem = { itemId: `fixture-chat-tool-plan-confirm-${Date.now()}`, functionalAgentId: 'user-admin-agent', kind: 'user-confirmation', createdAt: now, correlationId: request.correlationId, traceIds, surfaceId: `surface-chat-tool-plan-${request.planId}`, title: 'Confirmed chat tool plan', body: 'Fixture plan confirmation matched the exact snapshot.', status: 'ready' };
    const agentItem: WorkstreamItem = { itemId: `fixture-chat-tool-plan-result-${Date.now()}`, functionalAgentId: 'user-admin-agent', kind: 'chat_tool_plan_result', createdAt: now, correlationId: request.correlationId, traceIds, surfaceId: surface.surfaceId, title: 'Chat tool plan result', body: 'Fixture plan execution completed.', status: 'completed' };
    this.items = [...this.items, userItem, agentItem];
    this.surfaces = [...this.surfaces.filter((candidate) => candidate.surfaceId !== surface.surfaceId), surface];
    return delayedOk({ correlationId: request.correlationId, idempotencyKey: request.idempotencyKey, userItem, agentItem, surface });
  }

  runShellRequest(request: WorkstreamShellRequest): Promise<ApiResult<WorkstreamShellResponse>> {
    const targetSurfaceId = request.targetSurfaceId ?? (request.targetFunctionalAgentId === 'agent-admin-agent' ? 'surface-agent-admin-dashboard' : request.targetFunctionalAgentId === 'my-account-agent' ? 'surface-my-account-dashboard' : 'surface-user-admin-dashboard');
    const surface = this.surfaces.find((candidate) => candidate.surfaceId === targetSurfaceId);
    if (!surface) return delayedError('not_found', 'The requested shell surface is not available in this context.');
    const now = new Date().toISOString();
    const canonicalPrompt = request.canonicalPrompt ?? (request.requestType === 'open_workstream' ? `show workstream ${surface.ownerFunctionalAgentId}` : `${request.requestType.replace('_', ' ')} ${surface.surfaceId}`);
    const requestItem: WorkstreamItem = {
      itemId: `fixture-shell-${Math.abs(hashText(`${request.correlationId}:${canonicalPrompt}`)).toString(36)}`,
      functionalAgentId: surface.ownerFunctionalAgentId,
      kind: 'user-request',
      createdAt: now,
      correlationId: request.correlationId,
      traceIds: [`trace-shell-${Math.abs(hashText(request.correlationId)).toString(36)}`],
      surfaceId: surface.surfaceId,
      title: canonicalPrompt,
      body: request.displayText,
      status: 'ready'
    };
    this.items = [...this.items, requestItem];
    return delayedOk({ request: { ...request, canonicalPrompt, targetFunctionalAgentId: surface.ownerFunctionalAgentId, targetSurfaceId: surface.surfaceId }, status: 'accepted', message: 'Shell request resolved through fixture backend-equivalent surface path.', correlationId: request.correlationId, traceIds: surface.traceIds, requestItem, resultSurface: surface });
  }

  runCapabilityAction(request: CapabilityActionRequest): Promise<ApiResult<CapabilityActionResult>> {
    const action = allSurfaceActions.find((candidate) => candidate.actionId === request.actionId || candidate.capabilityId === request.capabilityId);
    if (!action) return delayedError('not_found', 'The requested capability action is not exposed by this surface.');
    if (action.disabled) return delayedOk({ ...actionResultsByStatus.denied, message: action.disabled.message, correlationId: request.correlationId });
    const result = request.actionId === 'action-show-my-account-dashboard'
      ? displayMyAccountDashboardActionResult
      : request.actionId === 'action-agent-admin-show-blank'
        ? displayAgentBlankActionResult
      : request.actionId === 'action-agent-admin-show-dashboard' || request.actionId === 'action-agent-admin-refresh-dashboard' || request.actionId === 'action-display-agent-admin-dashboard' || request.actionId === 'action-open-agent-admin'
        ? displayAgentDashboardActionResult
      : request.actionId === 'action-agent-admin-show-agents' || request.actionId === 'action-agent-admin-list-agents'
        ? displayAgentCatalogActionResult
      : request.actionId === 'action-agent-admin-open-agent-detail'
        ? displayAgentDetailActionResult
      : request.actionId === 'action-agent-admin-save-agent-profile'
        ? displayAgentDetailActionResult
      : request.actionId === 'action-agent-admin-open-prompt-doc'
        ? displayAgentPromptDocActionResult
      : request.actionId === 'action-agent-admin-open-skill-doc'
        ? displayAgentSkillDocActionResult
      : request.actionId === 'action-agent-admin-open-reference-doc'
        ? displayAgentReferenceDocActionResult
      : request.actionId === 'action-agent-doc-edit-start' || request.actionId === 'action-agent-doc-edit-revise' || request.actionId === 'action-agent-doc-edit-cancel'
        ? displayAgentEditSessionActionResult
      : request.actionId === 'action-agent-doc-edit-save'
        ? displayAgentPromptDocActionResult
      : request.actionId === 'action-agent-doc-version-history'
        ? displayAgentVersionHistoryActionResult
      : request.actionId === 'action-agent-doc-version-diff'
        ? displayAgentVersionDiffActionResult
      : request.actionId === 'action-agent-doc-version-restore'
        ? displayAgentPromptDocActionResult
      : request.actionId === 'action-agent-admin-open-create-skill'
        ? displayAgentCreateSkillActionResult
      : request.actionId === 'action-agent-admin-create-skill'
        ? displayAgentSkillDocActionResult
      : request.actionId === 'action-agent-admin-open-delete-skill'
        ? displayAgentDeleteSkillActionResult
      : request.actionId === 'action-agent-admin-delete-skill'
        ? displayAgentDetailActionResult
      : request.actionId === 'action-agent-admin-open-create-reference-doc'
        ? displayAgentCreateReferenceDocActionResult
      : request.actionId === 'action-agent-admin-create-reference-doc'
        ? displayAgentReferenceDocActionResult
      : request.actionId === 'action-agent-admin-open-delete-reference-doc'
        ? displayAgentDeleteReferenceDocActionResult
      : request.actionId === 'action-agent-admin-delete-reference-doc'
        ? displayAgentDetailActionResult
      : request.actionId === 'action-agent-admin-open-runtime-traces'
        ? displayAgentRuntimeTracesActionResult
      : request.actionId === 'action-show-my-profile'
        ? displayMyAccountProfileActionResult
        : request.actionId === 'action-show-my-settings'
          ? displayMyAccountSettingsActionResult
          : request.actionId === 'action-update-my-profile' || request.actionId === 'action-update-my-settings'
            ? updateMyAccountSettingsActionResult
            : request.actionId === 'action-display-user-detail'
      ? displayUserDetailActionResult
      : request.actionId === 'action-display-agent-detail' || request.actionId === 'action-open-agent-detail' || request.capabilityId === 'agent_admin.get_definition'
        ? displayAgentDetailActionResult
        : request.actionId === 'action-list-agent-seed-material' || request.capabilityId === 'agent_admin.list_seed_material'
          ? displayAgentSeedMaterialActionResult
          : request.actionId === 'action-agent-skill-manifest-back-to-detail'
            ? displayAgentDetailActionResult
            : request.actionId === 'action-agent-skill-manifest-refresh'
              ? displayAgentManifestActionResult
              : request.actionId === 'action-agent-skill-manifest-simulate'
                ? displayAgentTestConsoleActionResult
                : ['action-agent-skill-manifest-submit-review', 'action-agent-skill-manifest-approve', 'action-agent-skill-manifest-reject'].includes(request.actionId)
                  ? displayAgentBehaviorProposalActionResult
                  : request.actionId === 'action-agent-skill-manifest-open-tool-boundary'
                    ? displayAgentToolBoundaryActionResult
                    : request.actionId === 'action-agent-skill-manifest-open-model-refs'
                      ? displayAgentModelRefsActionResult
                      : request.actionId === 'action-agent-skill-manifest-open-trace'
                        ? displayAgentAdminTraceActionResult
                        : request.actionId === 'action-propose-prompt-diff' || request.capabilityId === 'agent_admin.draft_behavior_change'
            ? displayAgentPromptGovernanceActionResult
            : request.actionId === 'action-approve-skill-manifest' || request.capabilityId === 'agent_admin.approve_behavior_change'
              ? displayAgentManifestActionResult
              : request.actionId === 'action-simulate-tool-boundary' || request.capabilityId === 'agent_admin.simulate_tool_boundary'
                ? displayAgentToolBoundaryActionResult
                : request.actionId === 'action-manage-model-ref' || request.capabilityId === 'agent_admin.get_model_ref'
                  ? displayAgentModelRefsActionResult
                  : request.actionId === 'action-test-agent-prompt'
                    ? displayAgentTestConsoleActionResult
                    : request.actionId === 'action-activate-agent-definition' || request.actionId === 'action-activate-behavior-change'
                      ? displayAgentActivationConfirmationActionResult
                      : request.actionId === 'action-deactivate-agent-definition'
                        ? displayAgentDeactivationConfirmationActionResult
                        : request.actionId === 'action-rollback-behavior-change'
                          ? displayAgentRollbackConfirmationActionResult
                          : ['action-submit-behavior-change', 'action-reject-behavior-change', 'action-cancel-behavior-change'].includes(request.actionId)
                      ? displayAgentBehaviorProposalActionResult
                      : ['action-agentadmin-start-prompt-risk-review', 'action-agentadmin-read-prompt-risk-review', 'action-agentadmin-cancel-prompt-risk-review', 'action-agentadmin-accept-prompt-risk-review-result', 'action-agentadmin-reject-prompt-risk-review-result'].includes(request.actionId) || request.capabilityId?.startsWith('agent_admin.prompt_risk_review.')
                        ? displayAgentPromptRiskReviewActionResult
                      : request.actionId === 'action-open-agent-trace'
                        ? displayAgentAdminTraceActionResult
        : request.actionId === 'action-governance-policy-dashboard' || request.capabilityId === 'governance.policy.read'
          ? displayGovernancePolicyDashboardActionResult
          : request.actionId === 'action-governance-policy-list'
            ? displayGovernancePolicyInventoryActionResult
            : request.actionId === 'action-governance-policy-simulate' || request.capabilityId === 'governance.policy.simulate'
              ? displayGovernancePolicySimulationActionResult
              : ['action-governance-policy-start-impact-analysis', 'action-governance-policy-read-impact-analysis', 'action-governance-policy-cancel-impact-analysis'].includes(request.actionId) || request.capabilityId?.startsWith('governance.policy.impact_analysis.')
                ? (['action-governance-policy-accept-impact-result', 'action-governance-policy-reject-impact-result', 'action-governance-policy-request-impact-changes'].includes(request.actionId) ? displayGovernancePolicyImpactResultActionResult : displayGovernancePolicyImpactTaskActionResult)
              : request.actionId === 'action-display-agent-catalog' || request.capabilityId === 'agent_admin.list_definitions'
          ? displayAgentCatalogActionResult
          : request.actionId === 'action-display-user-list' || request.actionId === 'action-search-users'
            ? displayUserListActionResult
            : request.actionId === 'action-display-organization-admin' || request.actionId.startsWith('action-organization-') || request.capabilityId?.startsWith('saas_owner.organization.')
              ? displayOrganizationAdminActionResult
            : request.actionId === 'action-open-useradmin-invitation-create'
              ? { status: 'accepted' as const, message: 'Invite user surface opened as a separate user_admin.invitation_create.v1 task surface.', correlationId: request.correlationId, traceIds: ['trace-useradmin-invitation-create', 'trace-useradmin'], resultSurface: userAdminInvitationCreateSurface }
            : ['action-invite-user', 'action-useradmin-resend-invitation', 'action-useradmin-revoke-invitation'].includes(request.actionId)
              ? { status: request.actionId === 'action-useradmin-revoke-invitation' ? 'no-op' as const : 'accepted' as const, message: 'Invitation action result came from the backend-aligned workstream action contract with safe system_message fallback states.', correlationId: request.correlationId, traceIds: ['trace-useradmin-invitation-action', 'trace-useradmin'], resultSurface: userAdminInvitationActionStatusSurface }
              : ['action-useradmin-disable-member', 'action-useradmin-reactivate-member'].includes(request.actionId)
                ? { status: request.actionId === 'action-useradmin-disable-member' ? 'no-op' as const : 'accepted' as const, message: 'Member status action result came from the backend-aligned user_admin.update_member_status path with last-admin, self-disable, idempotency, trace, and system_message evidence.', correlationId: request.correlationId, traceIds: ['trace-useradmin-status-action', 'trace-useradmin'], resultSurface: userAdminMemberStatusActionSurface }
                : request.actionId === 'action-useradmin-preview-role-change'
                  ? { status: 'accepted' as const, message: 'Role-change preview returned user_admin.role_change_preview.v1 evidence with capability delta, affected workstreams, policy hints, last-admin impact, and trace links.', correlationId: request.correlationId, traceIds: ['trace-useradmin-role-preview', 'trace-useradmin'], resultSurface: userAdminRoleChangePreviewSurface }
                  : request.actionId === 'action-useradmin-change-member-roles'
                    ? { status: 'approval-required' as const, message: 'Role-change commit remains backend-authoritative and approval-gated; frontend controls are advisory only.', correlationId: request.correlationId, traceIds: ['trace-useradmin-role-change-action', 'trace-useradmin'], resultSurface: userAdminRoleChangeActionSurface }
                    : ['action-useradmin-start-access-review', 'action-useradmin-read-access-review', 'action-useradmin-cancel-access-review', 'action-useradmin-accept-access-review-result', 'action-useradmin-reject-access-review-result'].includes(request.actionId)
                      ? { status: request.actionId === 'action-useradmin-read-access-review' ? 'accepted' as const : 'blocked_provider_or_runtime' as const, message: 'Access-review task action returned backend-shaped user_admin.access_review_task.v1 state with provider/runtime blocker traces and no direct mutation.', correlationId: request.correlationId, traceIds: ['trace-useradmin-access-review-blocked', 'trace-useradmin-access-review-task-001'], resultSurface: userAdminAccessReviewSurface }
            : request.capabilityId === 'governance-decisions-audit' || request.capabilityId === 'decision.approve'
            ? actionResultsByStatus['approval-required']
            : actionResultsByStatus.accepted;
    const response = { ...result, correlationId: request.correlationId };
    this.items = [
      ...this.items,
      {
        itemId: `fixture-action-${request.actionId}-${Date.now()}`,
        functionalAgentId: surfaceOwnerFor(request.surfaceId, this.surfaces) ?? 'user-admin-agent',
        kind: 'action-feedback',
        createdAt: new Date().toISOString(),
        correlationId: response.correlationId,
        traceIds: response.traceIds,
        surfaceId: response.resultSurface?.surfaceId ?? request.surfaceId,
        title: `${action.label} ${response.status}`,
        body: response.message,
        status: response.status === 'accepted' || response.status === 'no-op' ? 'ready' : response.status === 'denied' || response.status === 'blocked-runtime' || response.status === 'blocked_provider_or_runtime' ? 'blocked' : 'waiting-for-human'
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

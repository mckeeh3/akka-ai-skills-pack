import type { CapabilityActionResult, SurfaceAction, SurfaceEnvelope } from '../types';
import { tenantAdminAuthContext } from './me';

const generatedAt = '2026-05-19T12:00:00.000Z';
const authContext = {
  tenantId: tenantAdminAuthContext.tenantId,
  customerId: tenantAdminAuthContext.customerId,
  selectedContextId: tenantAdminAuthContext.selectedContextId,
  visibleCapabilityIds: tenantAdminAuthContext.capabilityIds
};

export const surfaceActionsByIntent: Record<SurfaceAction['intent'], SurfaceAction> = {
  read: {
    actionId: 'action-refresh-users',
    label: 'Refresh users',
    intent: 'read',
    capabilityId: 'admin.users.read',
    idempotency: { required: false },
    audit: { eventType: 'AdminUsersRead', traceRequired: true }
  },
  command: {
    actionId: 'action-invite-user',
    label: 'Invite user',
    intent: 'command',
    capabilityId: 'admin.users.invite',
    inputSchemaRef: 'schema.invitation.create.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { appendSurfaceType: 'workflow-status', openPlacement: 'inline' },
    audit: { eventType: 'InvitationRequested', traceRequired: true }
  },
  proposal: {
    actionId: 'action-propose-policy',
    label: 'Propose policy change',
    intent: 'proposal',
    capabilityId: 'governance.policy.propose',
    inputSchemaRef: 'schema.policy.proposal.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { appendSurfaceType: 'governance-diff', openPlacement: 'side-panel' },
    audit: { eventType: 'PolicyProposalCreated', traceRequired: true }
  },
  approval: {
    actionId: 'action-approve-decision',
    label: 'Approve recommendation',
    intent: 'approval',
    capabilityId: 'decision.approve',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    audit: { eventType: 'DecisionApproved', traceRequired: true }
  },
  workflow: {
    actionId: 'action-resume-workflow',
    label: 'Resume workflow',
    intent: 'workflow',
    capabilityId: 'workflow.resume',
    disabled: { reasonCode: 'waiting-for-approval', message: 'Approval is required before this workflow can resume.' },
    idempotency: { required: true, keySource: 'server-issued' },
    resultSurface: { updateSurfaceId: 'surface-workflow-status', openPlacement: 'inline' },
    audit: { eventType: 'WorkflowResumeRequested', traceRequired: true }
  },
  governance: {
    actionId: 'action-simulate-policy',
    label: 'Run simulation',
    intent: 'governance',
    capabilityId: 'governance.policy.simulate',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-governance-diff', openPlacement: 'inline' },
    audit: { eventType: 'PolicySimulationRequested', traceRequired: true }
  },
  trace: {
    actionId: 'action-open-trace',
    label: 'Open trace',
    intent: 'trace',
    capabilityId: 'audit.trace.read',
    idempotency: { required: false },
    resultSurface: { appendSurfaceType: 'audit-timeline', openPlacement: 'deep-link' },
    audit: { eventType: 'TraceOpened', traceRequired: true }
  }
};

function envelope<TData>(surfaceId: string, surfaceType: string, title: string, ownerFunctionalAgentId: string, data: TData, actions: SurfaceAction[]): SurfaceEnvelope<TData> {
  return {
    surfaceId,
    surfaceType,
    surfaceVersion: 'v1',
    title,
    ownerFunctionalAgentId,
    reusableByFunctionalAgentIds: ['agent-audit-trace'],
    authContext,
    correlationId: `corr-${surfaceId}`,
    traceIds: [`trace-${surfaceId}`],
    generatedAt,
    redaction: { profile: 'tenant-admin' },
    data,
    actions,
    links: [{ label: 'Open surface', href: `/ui?surfaceId=${surfaceId}`, rel: 'deep-link' }]
  };
}

export const dashboardSurface = envelope('surface-dashboard', 'dashboard', 'Tenant attention dashboard', 'agent-access-profile', { cards: [{ cardId: 'card-open-decisions', label: 'Open decisions', value: 2, severity: 'warning' }] }, [surfaceActionsByIntent.read]);
export const listSearchSurface = envelope('surface-user-list', 'list-search', 'Users and invitations', 'agent-user-admin', { query: '', rows: [{ userId: 'acct-member', email: 'member@example.test', role: 'member', active: true }], pageInfo: { totalKnownCount: 1 } }, [surfaceActionsByIntent.read, surfaceActionsByIntent.command]);
export const detailEditSurface = envelope('surface-profile-detail', 'detail-edit', 'Profile and context', 'agent-access-profile', { recordId: 'acct-admin', fields: [{ fieldId: 'displayName', label: 'Display name', value: 'Tenant Admin', editable: true }], version: 1 }, [surfaceActionsByIntent.command]);
export const decisionSurface = envelope('surface-decision-card', 'decision', 'Approve bounded outreach plan', 'agent-governance-policy', { decisionId: 'decision-1', recommendation: 'Approve after evidence review.', riskScore: 72, confidenceScore: 84, evidence: [{ evidenceId: 'evidence-1', label: 'Trace summary', summary: 'Agent stayed within tool boundary.' }] }, [surfaceActionsByIntent.approval, surfaceActionsByIntent.trace]);
export const auditTimelineSurface = envelope('surface-audit-timeline', 'audit-timeline', 'Admin audit timeline', 'agent-audit-trace', { events: [{ eventId: 'audit-1', occurredAt: generatedAt, actor: 'Tenant Admin', action: 'invited user', traceId: 'trace-invite' }] }, [surfaceActionsByIntent.trace]);
export const workflowStatusSurface = envelope('surface-workflow-status', 'workflow-status', 'Invitation workflow status', 'agent-user-admin', { workflowId: 'workflow-invite-1', status: 'waiting-for-human', steps: [{ stepId: 'send-email', label: 'Send invitation email', status: 'waiting-for-human' }] }, [surfaceActionsByIntent.workflow]);
export const governanceDiffSurface = envelope('surface-governance-diff', 'governance-diff', 'Policy proposal diff', 'agent-governance-policy', { proposalId: 'proposal-1', beforeSummary: 'Manual approval over 75 risk.', afterSummary: 'Manual approval over 65 risk.', changes: [{ path: 'risk.approvalThreshold', before: '75', after: '65', impact: 'More decisions require human review.' }] }, [surfaceActionsByIntent.proposal, surfaceActionsByIntent.governance]);
export const outcomeSurface = envelope('surface-outcome-review', 'outcome', 'Outcome review', 'agent-governance-policy', { outcomeId: 'outcome-1', metrics: [{ metricId: 'decision-cycle-time', label: 'Decision cycle time', current: 4, target: 2, unit: 'hours' }] }, [surfaceActionsByIntent.read]);

export const canonicalSurfaceEnvelopes = [dashboardSurface, listSearchSurface, detailEditSurface, decisionSurface, auditTimelineSurface, workflowStatusSurface, governanceDiffSurface, outcomeSurface];

const resultBase = { correlationId: 'corr-action-result', traceIds: ['trace-action-result'] };
export const actionResultsByStatus: Record<CapabilityActionResult['status'], CapabilityActionResult> = {
  accepted: { status: 'accepted', message: 'Action accepted.', ...resultBase, resultSurface: workflowStatusSurface },
  denied: { status: 'denied', message: 'You do not have the required capability.', ...resultBase },
  'validation-error': { status: 'validation-error', message: 'Correct the highlighted fields.', ...resultBase },
  'approval-required': { status: 'approval-required', message: 'Human approval is required.', ...resultBase, resultSurface: decisionSurface },
  conflict: { status: 'conflict', message: 'The surface changed. Refresh and try again.', ...resultBase },
  'no-op': { status: 'no-op', message: 'No change was needed.', ...resultBase },
  failed: { status: 'failed', message: 'Action failed safely.', ...resultBase }
};

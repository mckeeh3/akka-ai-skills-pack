import type { CapabilityActionResult, SurfaceAction, SurfaceEnvelope } from '../types';
import { tenantAdminAuthContext } from './me';

const generatedAt = '2026-05-19T12:00:00.000Z';
const authContext = {
  tenantId: tenantAdminAuthContext.tenantId,
  customerId: tenantAdminAuthContext.customerId,
  selectedContextId: tenantAdminAuthContext.selectedContextId,
  visibleCapabilityIds: tenantAdminAuthContext.capabilityIds
};

const secureTenantUserFoundation = 'secure-tenant-user-foundation';

export const surfaceActionsByIntent: Record<SurfaceAction['intent'], SurfaceAction> = {
  read: {
    actionId: 'action-refresh-users',
    label: 'Refresh users',
    intent: 'read',
    capabilityId: secureTenantUserFoundation,
    idempotency: { required: false },
    audit: { eventType: 'AdminUsersRead', traceRequired: true }
  },
  command: {
    actionId: 'action-invite-user',
    label: 'Invite user',
    intent: 'command',
    capabilityId: secureTenantUserFoundation,
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
    capabilityId: 'governance-decisions-audit',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    audit: { eventType: 'DecisionApproved', traceRequired: true }
  },
  workflow: {
    actionId: 'action-resume-workflow',
    label: 'Resume workflow',
    intent: 'workflow',
    capabilityId: secureTenantUserFoundation,
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

export const userAdminSurfaceActions = {
  displayUserList: {
    actionId: 'action-display-user-list',
    label: 'Display user list view',
    intent: 'read',
    capabilityId: secureTenantUserFoundation,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-list', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminListDisplayed', traceRequired: true }
  },
  searchUsers: {
    actionId: 'action-search-users',
    label: 'Search users and invitations',
    intent: 'read',
    capabilityId: secureTenantUserFoundation,
    inputSchemaRef: 'schema.user-admin.search.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-list', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminDirectorySearched', traceRequired: true }
  },
  resendInvitation: {
    actionId: 'action-resend-invitation',
    label: 'Resend pending invitation',
    intent: 'command',
    capabilityId: secureTenantUserFoundation,
    inputSchemaRef: 'schema.invitation.resend.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { appendSurfaceType: 'workflow-status', openPlacement: 'inline' },
    audit: { eventType: 'InvitationResendRequested', traceRequired: true }
  },
  revokeInvitation: {
    actionId: 'action-revoke-invitation',
    label: 'Revoke expired invitation',
    intent: 'command',
    capabilityId: secureTenantUserFoundation,
    inputSchemaRef: 'schema.invitation.revoke.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-list', openPlacement: 'inline' },
    audit: { eventType: 'InvitationRevoked', traceRequired: true }
  },
  replaceRole: {
    actionId: 'action-replace-membership-role',
    label: 'Replace membership role',
    intent: 'command',
    capabilityId: secureTenantUserFoundation,
    inputSchemaRef: 'schema.membership.role.replace.v1',
    requiresConfirmation: true,
    disabled: { reasonCode: 'last-admin-risk', message: 'Cannot remove the last tenant admin without an approved replacement.' },
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-list', openPlacement: 'inline' },
    audit: { eventType: 'MembershipRoleReplacementDenied', traceRequired: true }
  },
  approveRiskyAccess: {
    actionId: 'action-approve-risky-access',
    label: 'Approve risky access decision',
    intent: 'approval',
    capabilityId: 'governance-decisions-audit',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { appendSurfaceType: 'decision', openPlacement: 'inline' },
    audit: { eventType: 'RiskyAccessDecisionApproved', traceRequired: true }
  }
} satisfies Record<string, SurfaceAction>;

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

export const userAdminDashboardSurface = envelope(
  'surface-user-admin-dashboard',
  'dashboard',
  'User Admin command center',
  'agent-user-admin',
  {
    cards: [
      { cardId: 'card-pending-invitations', label: 'Pending invitations', value: 3, severity: 'warning' },
      { cardId: 'card-active-users', label: 'Active users', value: 18, severity: 'info' },
      { cardId: 'card-access-review', label: 'Access review items', value: 2, severity: 'critical' },
      { cardId: 'card-support-access', label: 'Expiring support grants', value: 1, severity: 'warning' }
    ],
    sections: [
      { sectionId: 'invitation-queue', label: 'Invitation queue', summary: 'One delivery failure and one invite expiring within 24 hours.' },
      { sectionId: 'access-review', label: 'Access review', summary: 'Last-admin risk requires governance decision before role replacement.' },
      { sectionId: 'admin-audit', label: 'Admin audit excerpts', summary: 'Latest invitation and support-access events have trace links.' }
    ]
  },
  [userAdminSurfaceActions.displayUserList, surfaceActionsByIntent.command, userAdminSurfaceActions.approveRiskyAccess, surfaceActionsByIntent.trace]
);

// Legacy fixture alias preserved for stale-screen quarantine tests: surface-user-list now resolves conceptually to surface-user-admin-list.
export const userAdminListSearchSurface = envelope(
  'surface-user-admin-list',
  'list-search',
  'Users, invitations, and memberships',
  'agent-user-admin',
  {
    query: 'status:active OR invitation:pending',
    rows: [
      { id: 'user-acct-admin', rowType: 'user-directory', email: 'admin@example.test', displayName: 'Tenant Admin', role: 'Tenant Admin', status: 'active', lastAdmin: true, traceId: 'trace-user-admin-row' },
      { id: 'invite-robin', rowType: 'invitation-queue', email: 'robin@example.test', displayName: 'Robin Reviewer', role: 'Reviewer', status: 'pending', delivery: 'failed', expiresInHours: 18, traceId: 'trace-invite-robin' },
      { id: 'membership-member', rowType: 'membership', email: 'member@example.test', displayName: 'Member User', role: 'Member', status: 'active', supportAccess: false, traceId: 'trace-membership-member' },
      { id: 'support-grant-1', rowType: 'support-access', email: 'support@example.test', displayName: 'Support Engineer', role: 'Support', status: 'expiring', supportAccess: true, expiresInHours: 6, traceId: 'trace-support-grant' },
      { id: 'audit-invite-1', rowType: 'admin-audit-excerpt', email: 'admin@example.test', displayName: 'Tenant Admin', role: 'Audit actor', status: 'invited-user', traceId: 'trace-invite' }
    ],
    pageInfo: { totalKnownCount: 5 },
    emptyMessage: 'No users, invitations, memberships, or support grants match the current scoped query.',
    mobileFallback: 'table-to-card'
  },
  [
    userAdminSurfaceActions.searchUsers,
    surfaceActionsByIntent.command,
    userAdminSurfaceActions.resendInvitation,
    userAdminSurfaceActions.revokeInvitation,
    userAdminSurfaceActions.replaceRole,
    surfaceActionsByIntent.trace
  ]
);

export const dashboardSurface = envelope('surface-dashboard', 'dashboard', 'Tenant attention dashboard', 'agent-access-profile', { cards: [{ cardId: 'card-open-decisions', label: 'Open decisions', value: 2, severity: 'warning' }] }, [surfaceActionsByIntent.read]);
export const listSearchSurface = userAdminListSearchSurface;
export const detailEditSurface = envelope('surface-profile-detail', 'detail-edit', 'Profile and context', 'agent-access-profile', { recordId: 'acct-admin', fields: [{ fieldId: 'displayName', label: 'Display name', value: 'Tenant Admin', editable: true }], version: 1 }, [surfaceActionsByIntent.command]);
export const decisionSurface = envelope('surface-decision-card', 'decision', 'Approve bounded outreach plan', 'agent-governance-policy', { decisionId: 'decision-1', recommendation: 'Approve after evidence review.', riskScore: 72, confidenceScore: 84, evidence: [{ evidenceId: 'evidence-1', label: 'Trace summary', summary: 'Agent stayed within tool boundary.' }] }, [surfaceActionsByIntent.approval, surfaceActionsByIntent.trace]);
export const auditTimelineSurface = envelope('surface-audit-timeline', 'audit-timeline', 'Admin audit timeline', 'agent-audit-trace', { events: [{ eventId: 'audit-1', occurredAt: generatedAt, actor: 'Tenant Admin', action: 'invited user', traceId: 'trace-invite' }] }, [surfaceActionsByIntent.trace]);
export const workflowStatusSurface = envelope('surface-workflow-status', 'workflow-status', 'Invitation workflow status', 'agent-user-admin', { workflowId: 'workflow-invite-1', status: 'waiting-for-human', steps: [{ stepId: 'send-email', label: 'Send invitation email', status: 'waiting-for-human' }] }, [surfaceActionsByIntent.workflow]);
export const governanceDiffSurface = envelope('surface-governance-diff', 'governance-diff', 'Policy proposal diff', 'agent-governance-policy', { proposalId: 'proposal-1', beforeSummary: 'Manual approval over 75 risk.', afterSummary: 'Manual approval over 65 risk.', changes: [{ path: 'risk.approvalThreshold', before: '75', after: '65', impact: 'More decisions require human review.' }] }, [surfaceActionsByIntent.proposal, surfaceActionsByIntent.governance]);
export const outcomeSurface = envelope('surface-outcome-review', 'outcome', 'Outcome review', 'agent-governance-policy', { outcomeId: 'outcome-1', metrics: [{ metricId: 'decision-cycle-time', label: 'Decision cycle time', current: 4, target: 2, unit: 'hours' }] }, [surfaceActionsByIntent.read]);

export const canonicalSurfaceEnvelopes = [userAdminDashboardSurface, userAdminListSearchSurface, dashboardSurface, detailEditSurface, decisionSurface, auditTimelineSurface, workflowStatusSurface, governanceDiffSurface, outcomeSurface];
export const allSurfaceActions: SurfaceAction[] = [...Object.values(surfaceActionsByIntent), ...Object.values(userAdminSurfaceActions)];

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

export const displayUserListActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Display the user list view. Backend authority, scoped query, audit, and trace checks remain capability-backed.',
  correlationId: 'corr-display-user-list',
  traceIds: ['trace-display-user-list'],
  resultSurface: userAdminListSearchSurface
};

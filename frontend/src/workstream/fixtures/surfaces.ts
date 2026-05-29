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
const userAdminCapabilities = {
  overview: 'USERADMIN_VIEW_OVERVIEW',
  listInvitations: 'USERADMIN_LIST_INVITATIONS',
  sendInvitation: 'USERADMIN_SEND_INVITATION',
  resendInvitation: 'USERADMIN_RESEND_INVITATION',
  revokeInvitation: 'USERADMIN_REVOKE_INVITATION',
  listMembers: 'USERADMIN_LIST_MEMBERS',
  updateMemberStatus: 'USERADMIN_UPDATE_MEMBER_STATUS',
  listRolesCapabilities: 'USERADMIN_LIST_ROLES_CAPABILITIES',
  previewRoleChange: 'USERADMIN_PREVIEW_ROLE_CHANGE',
  changeMemberRoles: 'USERADMIN_CHANGE_MEMBER_ROLES',
  agentTurn: 'USERADMIN_AGENT_TURN',
  startAccessReviewTask: 'USERADMIN_START_ACCESS_REVIEW_TASK',
  viewAccessReviewTask: 'USERADMIN_VIEW_ACCESS_REVIEW_TASK',
  cancelAccessReviewTask: 'USERADMIN_CANCEL_ACCESS_REVIEW_TASK',
  viewAccessReviewResult: 'USERADMIN_VIEW_ACCESS_REVIEW_RESULT',
  viewTraceReference: 'USERADMIN_VIEW_TRACE_REFERENCE'
} as const;

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
    capabilityId: userAdminCapabilities.sendInvitation,
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
    capabilityId: userAdminCapabilities.sendInvitation,
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
  refreshDashboard: {
    actionId: 'action-refresh-user-admin-dashboard',
    label: 'Refresh User Admin dashboard',
    intent: 'read',
    capabilityId: userAdminCapabilities.overview,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'user-admin-dashboard', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminDashboardRead', traceRequired: true }
  },
  displayUserList: {
    actionId: 'action-display-user-list',
    label: 'Display user list view',
    intent: 'read',
    capabilityId: userAdminCapabilities.listMembers,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'user-admin-user-list', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminListDisplayed', traceRequired: true }
  },
  searchUsers: {
    actionId: 'action-search-users',
    label: 'Search users and invitations',
    intent: 'read',
    capabilityId: userAdminCapabilities.listMembers,
    inputSchemaRef: 'schema.user-admin.search.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'user-admin-user-list', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminDirectorySearched', traceRequired: true }
  },
  displayUserDetail: {
    actionId: 'action-display-user-detail',
    label: 'Display user account detail',
    intent: 'read',
    capabilityId: userAdminCapabilities.listMembers,
    inputSchemaRef: 'schema.user-admin.detail.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'user-admin-user-account', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminDetailDisplayed', traceRequired: true }
  },
  displayRoleCapabilityMatrix: {
    actionId: 'action-display-role-capability-matrix',
    label: 'Display role/capability matrix',
    intent: 'read',
    capabilityId: userAdminCapabilities.listRolesCapabilities,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'user-admin-role-capability-matrix', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminRoleCapabilityMatrixDisplayed', traceRequired: true }
  },
  previewRoleChange: {
    actionId: 'action-useradmin-preview-role-change',
    label: 'Preview role change',
    intent: 'proposal',
    capabilityId: userAdminCapabilities.previewRoleChange,
    inputSchemaRef: 'schema.user-admin.role-change.preview.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'user-admin-user-account', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminRoleChangePreviewed', traceRequired: true }
  },
  changeMemberRoles: {
    actionId: 'action-useradmin-change-member-roles',
    label: 'Apply role change',
    intent: 'command',
    capabilityId: userAdminCapabilities.changeMemberRoles,
    inputSchemaRef: 'schema.user-admin.role-change.apply.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'user-admin-user-account', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminMemberRolesChanged', traceRequired: true }
  },
  createInvitation: {
    actionId: 'action-create-invitation',
    label: 'Create invitation',
    intent: 'command',
    capabilityId: userAdminCapabilities.sendInvitation,
    inputSchemaRef: 'schema.invitation.create.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { appendSurfaceType: 'workflow-status', openPlacement: 'inline' },
    audit: { eventType: 'InvitationRequested', traceRequired: true }
  },
  resendInvitation: {
    actionId: 'action-resend-invitation',
    label: 'Resend pending invitation',
    intent: 'command',
    capabilityId: userAdminCapabilities.resendInvitation,
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
    capabilityId: userAdminCapabilities.revokeInvitation,
    inputSchemaRef: 'schema.invitation.revoke.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'user-admin-user-list', openPlacement: 'inline' },
    audit: { eventType: 'InvitationRevoked', traceRequired: true }
  },
  addMembership: {
    actionId: 'action-add-membership',
    label: 'Add membership',
    intent: 'command',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    inputSchemaRef: 'schema.membership.add.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'user-admin-user-account', openPlacement: 'inline' },
    audit: { eventType: 'MembershipAddRequested', traceRequired: true }
  },
  suspendMembership: {
    actionId: 'action-suspend-membership',
    label: 'Suspend membership',
    intent: 'command',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    inputSchemaRef: 'schema.membership.suspend.v1',
    requiresConfirmation: true,
    disabled: { reasonCode: 'last-admin', message: 'Backend authorization would deny this fixture action when it causes last-admin loss.' },
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'user-admin-user-account', openPlacement: 'inline' },
    audit: { eventType: 'MembershipSuspendDenied', traceRequired: true }
  },
  reactivateMembership: {
    actionId: 'action-reactivate-membership',
    label: 'Reactivate membership',
    intent: 'command',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    inputSchemaRef: 'schema.membership.reactivate.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'user-admin-user-account', openPlacement: 'inline' },
    audit: { eventType: 'MembershipReactivated', traceRequired: true }
  },
  removeMembership: {
    actionId: 'action-remove-membership',
    label: 'Remove membership',
    intent: 'command',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    inputSchemaRef: 'schema.membership.remove.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    disabled: { reasonCode: 'role escalation', message: 'Decision-card review required when removal could hide role escalation or last-admin risk.' },
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { appendSurfaceType: 'decision', openPlacement: 'inline' },
    audit: { eventType: 'MembershipRemovalDecisionRequired', traceRequired: true }
  },
  updateUserProfile: {
    actionId: 'action-update-user-profile',
    label: 'Save profile changes',
    intent: 'command',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    inputSchemaRef: 'schema.user-admin.profile.update.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'user-admin-user-account', openPlacement: 'inline' },
    audit: { eventType: 'UserProfileUpdateRequested', traceRequired: true }
  },
  replaceRole: {
    actionId: 'action-replace-membership-role',
    label: 'Replace membership role',
    intent: 'command',
    capabilityId: userAdminCapabilities.changeMemberRoles,
    inputSchemaRef: 'schema.membership.role.replace.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    disabled: { reasonCode: 'last-admin-risk', message: 'Backend authorization denied this fixture action: cannot remove the last tenant admin without an approved replacement.' },
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'user-admin-user-account', openPlacement: 'inline' },
    audit: { eventType: 'MembershipRoleReplacementDenied', traceRequired: true }
  },
  removeRole: {
    actionId: 'action-remove-membership-role',
    label: 'Remove membership role',
    intent: 'command',
    capabilityId: userAdminCapabilities.changeMemberRoles,
    inputSchemaRef: 'schema.membership.role.remove.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    disabled: { reasonCode: 'last-admin', message: 'Backend authorization denies role removal that would create last-admin loss.' },
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { appendSurfaceType: 'decision', openPlacement: 'inline' },
    audit: { eventType: 'MembershipRoleRemovalDenied', traceRequired: true }
  },
  disableAccount: {
    actionId: 'action-disable-account',
    label: 'Disable account',
    intent: 'command',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    inputSchemaRef: 'schema.user-admin.account.disable.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    disabled: { reasonCode: 'disabled actor', message: 'Disabled actors and inactive memberships cannot disable accounts.' },
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { appendSurfaceType: 'decision', openPlacement: 'inline' },
    audit: { eventType: 'AccountDisableDecisionRequired', traceRequired: true }
  },
  reactivateAccount: {
    actionId: 'action-reactivate-account',
    label: 'Reactivate account',
    intent: 'command',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    inputSchemaRef: 'schema.user-admin.account.reactivate.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'user-admin-user-account', openPlacement: 'inline' },
    audit: { eventType: 'AccountReactivateRequested', traceRequired: true }
  },
  requestIdentityRelink: {
    actionId: 'action-request-identity-relink',
    label: 'Request identity relink',
    intent: 'proposal',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    inputSchemaRef: 'schema.user-admin.identity-relink.request.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { appendSurfaceType: 'decision', openPlacement: 'inline' },
    audit: { eventType: 'IdentityRelinkDecisionRequested', traceRequired: true }
  },
  completeIdentityRelink: {
    actionId: 'action-complete-identity-relink',
    label: 'Complete identity relink',
    intent: 'approval',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    inputSchemaRef: 'schema.user-admin.identity-relink.complete.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'server-issued' },
    resultSurface: { updateSurfaceId: 'user-admin-user-account', openPlacement: 'inline' },
    audit: { eventType: 'IdentityRelinkCompleted', traceRequired: true }
  },
  readSupportAccess: {
    actionId: 'action-read-support-access',
    label: 'Read support access grants',
    intent: 'read',
    capabilityId: userAdminCapabilities.viewTraceReference,
    inputSchemaRef: 'schema.support-access.search.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'user-admin-user-list', openPlacement: 'inline' },
    audit: { eventType: 'SupportAccessRead', traceRequired: true }
  },
  grantSupportAccess: {
    actionId: 'action-grant-support-access',
    label: 'Grant support access',
    intent: 'command',
    capabilityId: userAdminCapabilities.viewTraceReference,
    inputSchemaRef: 'schema.support-access.grant.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    disabled: { reasonCode: 'SAAS_OWNER_NO_SUPPORT_ACCESS', message: 'SaaS Owner cannot self-grant Tenant support access; Tenant-created grant and decision-card approval are required.' },
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { appendSurfaceType: 'decision', openPlacement: 'inline' },
    audit: { eventType: 'SupportAccessGrantDecisionRequired', traceRequired: true }
  },
  revokeSupportAccess: {
    actionId: 'action-revoke-support-access',
    label: 'Revoke support access',
    intent: 'command',
    capabilityId: userAdminCapabilities.viewTraceReference,
    inputSchemaRef: 'schema.support-access.revoke.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'user-admin-user-account', openPlacement: 'inline' },
    audit: { eventType: 'SupportAccessRevoked', traceRequired: true }
  },
  extendSupportAccess: {
    actionId: 'action-extend-support-access',
    label: 'Extend support access',
    intent: 'command',
    capabilityId: userAdminCapabilities.viewTraceReference,
    inputSchemaRef: 'schema.support-access.extend.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { appendSurfaceType: 'decision', openPlacement: 'inline' },
    audit: { eventType: 'SupportAccessExtendDecisionRequired', traceRequired: true }
  },
  startAccessReview: {
    actionId: 'action-useradmin-start-access-review',
    label: 'Start access review',
    intent: 'workflow',
    capabilityId: userAdminCapabilities.startAccessReviewTask,
    inputSchemaRef: 'schema.user-admin.access-review.start.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    disabled: { reasonCode: 'blocked_provider_or_runtime', message: 'Durable AutonomousAgent access-review task runtime is unavailable in this starter slice; the UI must not fake progress.' },
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'user-admin-access-review', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminAccessReviewStartBlocked', traceRequired: true }
  },
  readAccessReview: {
    actionId: 'action-read-access-review',
    label: 'Read access review item',
    intent: 'read',
    capabilityId: userAdminCapabilities.viewAccessReviewTask,
    inputSchemaRef: 'schema.access-review.read.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'user-admin-access-review', openPlacement: 'inline' },
    audit: { eventType: 'AccessReviewRead', traceRequired: true }
  },
  cancelAccessReview: {
    actionId: 'action-useradmin-cancel-access-review',
    label: 'Cancel access review',
    intent: 'command',
    capabilityId: userAdminCapabilities.cancelAccessReviewTask,
    inputSchemaRef: 'schema.user-admin.access-review.cancel.v1',
    requiresConfirmation: true,
    disabled: { reasonCode: 'blocked_provider_or_runtime', message: 'No durable access-review task exists to cancel until the AutonomousAgent lifecycle is enabled.' },
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'user-admin-access-review', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminAccessReviewCancelBlocked', traceRequired: true }
  },
  approveRiskyAccess: {
    actionId: 'action-approve-risky-access',
    label: 'Approve risky access decision',
    intent: 'approval',
    capabilityId: userAdminCapabilities.viewAccessReviewResult,
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { appendSurfaceType: 'decision', openPlacement: 'inline' },
    audit: { eventType: 'RiskyAccessDecisionApproved', traceRequired: true }
  },
  openAdminAudit: {
    actionId: 'action-open-admin-audit',
    label: 'Open admin audit evidence',
    intent: 'trace',
    capabilityId: userAdminCapabilities.viewTraceReference,
    idempotency: { required: false },
    resultSurface: { appendSurfaceType: 'audit-timeline', openPlacement: 'deep-link' },
    audit: { eventType: 'AdminAuditOpened', traceRequired: true }
  }
} satisfies Record<string, SurfaceAction>;

const agentDefinitionsCapability = 'agent_admin.list_definitions';
const agentPromptsCapability = 'agent_admin.draft_behavior_change';
const agentSkillsCapability = 'agent_admin.approve_behavior_change';
const agentToolBoundariesCapability = 'agent_admin.simulate_tool_boundary';
const agentModelsReadCapability = 'agent_admin.get_model_ref';
const agentModelsManageCapability = 'agent_admin.activate_behavior_change';
const agentRuntimeTestCapability = 'agent_admin.draft_behavior_change';

export const agentAdminSurfaceActions = {
  displayCatalog: {
    actionId: 'action-display-agent-catalog',
    label: 'Display agent catalog',
    intent: 'read',
    capabilityId: agentDefinitionsCapability,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-catalog', openPlacement: 'inline' },
    audit: { eventType: 'AgentCatalogDisplayed', traceRequired: true }
  },
  openAgentDetail: {
    actionId: 'action-open-agent-detail',
    label: 'Open agent readiness detail',
    intent: 'read',
    capabilityId: agentDefinitionsCapability,
    inputSchemaRef: 'schema.agent-definition.detail.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-detail', openPlacement: 'inline' },
    audit: { eventType: 'AgentDefinitionDetailDisplayed', traceRequired: true }
  },
  proposePromptDiff: {
    actionId: 'action-propose-prompt-diff',
    label: 'Propose prompt diff',
    intent: 'proposal',
    capabilityId: agentPromptsCapability,
    inputSchemaRef: 'schema.prompt-version.proposal.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-prompt-governance', openPlacement: 'side-panel' },
    audit: { eventType: 'PromptVersionDraftProposed', traceRequired: true }
  },
  testPrompt: {
    actionId: 'action-test-agent-prompt',
    label: 'Run no-side-effect prompt test',
    intent: 'workflow',
    capabilityId: agentRuntimeTestCapability,
    inputSchemaRef: 'schema.agent-runtime.test.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { appendSurfaceType: 'workflow-status', openPlacement: 'inline' },
    audit: { eventType: 'AgentRuntimeTestRequested', traceRequired: true }
  },
  approveSkillManifest: {
    actionId: 'action-approve-skill-manifest',
    label: 'Approve manifest review',
    intent: 'approval',
    capabilityId: agentSkillsCapability,
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-agent-skill-manifest-diff', openPlacement: 'inline' },
    audit: { eventType: 'AgentSkillManifestApproved', traceRequired: true }
  },
  simulateToolBoundary: {
    actionId: 'action-simulate-tool-boundary',
    label: 'Simulate tool boundary change',
    intent: 'governance',
    capabilityId: agentToolBoundariesCapability,
    inputSchemaRef: 'schema.tool-boundary.simulation.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-tool-boundary-diff', openPlacement: 'inline' },
    audit: { eventType: 'ToolBoundarySimulationRequested', traceRequired: true }
  },
  manageModelRef: {
    actionId: 'action-manage-model-ref',
    label: 'Request model ref change',
    intent: 'proposal',
    capabilityId: agentModelsManageCapability,
    disabled: { reasonCode: 'MODEL_POLICY_DENIED', message: 'This fixture denies switching to a disabled provider alias; provider secrets remain redacted.' },
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { appendSurfaceType: 'decision', openPlacement: 'inline' },
    audit: { eventType: 'AgentModelRefChangeDenied', traceRequired: true }
  },
  openAgentTrace: {
    actionId: 'action-open-agent-trace',
    label: 'Open agent work trace',
    intent: 'trace',
    capabilityId: 'audit.trace.read',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-trace', openPlacement: 'deep-link' },
    audit: { eventType: 'AgentWorkTraceOpened', traceRequired: true }
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

function markdownResponseEnvelope(surfaceId: string, title: string, ownerFunctionalAgentId: string, markdown: string): SurfaceEnvelope<unknown> {
  return envelope(
    surfaceId,
    'markdown_response',
    title,
    ownerFunctionalAgentId,
    {
      markdown,
      title,
      summary: 'Five core v0 starter markdown_response surface; richer full-core structured surfaces remain follow-up/demo surfaces.',
      workstreamEntryId: `item-${surfaceId}`,
      producingAgentId: ownerFunctionalAgentId,
      sourceRefs: [
        { refType: 'capability', refId: 'five-core-v0', label: 'Five core v0 starter scope' },
        { refType: 'trace', refId: `trace-${surfaceId}`, label: 'Fixture workstream trace' }
      ],
      sections: [
        { anchor: 'available-now', title: 'Available now' },
        { anchor: 'full-core-follow-up', title: 'Full-core follow-up' }
      ],
      safety: {
        sanitized: true,
        blockedUnsafeLinks: 0,
        blockedRawHtml: true,
        redactionNote: 'Fixture markdown excludes provider secrets, raw JWTs, invitation tokens, and hidden capabilities.'
      },
      trace: { correlationId: `corr-${surfaceId}`, traceIds: [`trace-${surfaceId}`] }
    },
    [surfaceActionsByIntent.trace]
  );
}

export const myAccountMarkdownSurface = markdownResponseEnvelope(
  'surface-v0-my-account-markdown',
  'My Account v0 response',
  'agent-my-account',
  '## My Account\n\n### Available now\n- Review signed-in profile, settings, selected context, and browser-safe capability basis.\n- Use backend-authorized profile/settings actions for changes.\n\n### Full-core follow-up\nRicher profile and settings edit surfaces remain explicit full-core follow-up/demo behavior.'
);

export const userAdminMarkdownSurface = markdownResponseEnvelope(
  'surface-v0-user-admin-markdown',
  'User Admin v0 response',
  'agent-user-admin',
  '## User Admin\n\n### Available now\n- Ask about invitations, memberships, roles, and tenant-scoped access review.\n- Message submission returns a backend-authorized `markdown_response`.\n\n### Full-core follow-up\nStructured user tables, invitation workflows, and access-review actions remain full-core follow-up/demo surfaces.'
);

export const agentAdminMarkdownSurface = markdownResponseEnvelope(
  'surface-v0-agent-admin-markdown',
  'Agent Admin v0 response',
  'agent-agent-admin',
  '## Agent Admin\n\n### Available now\n- Review seeded agent definitions, prompts, skills, tool boundaries, model refs, and trace obligations at starter scope.\n\n### Full-core follow-up\nPrompt editors, manifest diffs, model governance, and test consoles remain full-core follow-up/demo surfaces.'
);

export const auditTraceMarkdownSurface = markdownResponseEnvelope(
  'surface-v0-audit-trace-markdown',
  'Audit/Trace v0 response',
  'agent-audit-trace',
  '## Audit/Trace\n\n### Available now\n- Search scoped audit/work traces for the selected AuthContext.\n- Open trace detail/evidence, correlation timelines, denial/provider/tool evidence, and investigation guidance.\n- Ask bounded explanations only through backend-governed Audit/Trace capability paths.\n\n### Runtime guardrail\nFrontend affordances never grant authority; backend capabilities, tenant/customer scope, redaction, trace links, and provider fail-closed surfaces remain authoritative.'
);

export const governancePolicyMarkdownSurface = markdownResponseEnvelope(
  'surface-v0-governance-policy-markdown',
  'Governance/Policy v0 response',
  'agent-governance-policy',
  '## Governance/Policy\n\n### Available now\n- Ask about policy guardrails, approval boundaries, deferred decisions, and safe next steps.\n\n### Full-core follow-up\nPolicy simulations, proposal diffs, and approval cards remain full-core follow-up/demo surfaces.'
);


export const auditTraceSurfaceActions = {
  showDashboard: {
    actionId: 'action-audit-trace-dashboard',
    label: 'Refresh Audit/Trace dashboard',
    intent: 'read',
    capabilityId: 'audit.trace.dashboard.read',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-dashboard', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceDashboardRead', traceRequired: true }
  },
  search: {
    actionId: 'action-audit-trace-search',
    label: 'Search scoped traces',
    intent: 'read',
    capabilityId: 'audit.trace.search',
    inputSchemaRef: 'schema.audit-trace.search.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-search', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceSearchRequested', traceRequired: true }
  },
  openDetail: {
    actionId: 'action-audit-trace-detail',
    label: 'Open trace detail',
    intent: 'read',
    capabilityId: 'audit.trace.detail.read',
    inputSchemaRef: 'schema.audit-trace.detail.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-detail', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceDetailRequested', traceRequired: true }
  },
  openTimeline: {
    actionId: 'action-audit-trace-timeline',
    label: 'Open correlation timeline',
    intent: 'trace',
    capabilityId: 'audit.trace.timeline.read',
    inputSchemaRef: 'schema.audit-trace.timeline.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-timeline', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceTimelineRequested', traceRequired: true }
  },
  openFailureEvidence: {
    actionId: 'action-audit-trace-failure-evidence',
    label: 'Open failure evidence',
    intent: 'read',
    capabilityId: 'audit.trace.failureEvidence.read',
    inputSchemaRef: 'schema.audit-trace.failure-evidence.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-failure-evidence', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceFailureEvidenceRequested', traceRequired: true }
  },
  showInvestigationGuide: {
    actionId: 'action-audit-trace-investigation-guide',
    label: 'Show investigation guidance',
    intent: 'read',
    capabilityId: 'audit.trace.investigationGuide.read',
    inputSchemaRef: 'schema.audit-trace.investigation-guide.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-investigation-guide', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceInvestigationGuideRequested', traceRequired: true }
  }
} satisfies Record<string, SurfaceAction>;

export const fiveCoreV0MarkdownSurfaces = [
  myAccountMarkdownSurface,
  userAdminMarkdownSurface,
  agentAdminMarkdownSurface,
  auditTraceMarkdownSurface,
  governancePolicyMarkdownSurface
];


export const auditTraceDashboardSurface = envelope(
  'surface-audit-trace-dashboard',
  'dashboard',
  'Audit/Trace dashboard',
  'agent-audit-trace',
  {
    cards: [
      { cardId: 'card-runtime-traces', label: 'Runtime traces', value: 12, severity: 'warning' },
      { cardId: 'card-denials-failures', label: 'Denials/failures', value: 3, severity: 'critical' },
      { cardId: 'card-redaction', label: 'Redaction', value: 'browser-safe', severity: 'info' }
    ],
    readiness: 'Trace search, detail, timeline, failure evidence, and guidance use backend-scoped capabilities for the selected AuthContext.',
    capabilityIds: ['audit.trace.dashboard.read', 'audit.trace.search', 'audit.trace.timeline.read'],
    sections: [
      { sectionId: 'recent-denials', label: 'Important denials', summary: 'Cross-tenant and missing capability denials show safe reasons without leaking row identities.' },
      { sectionId: 'provider-blocked', label: 'Provider/tool evidence', summary: 'Missing provider config and tool-boundary denials are actionable but never expose secrets.' },
      { sectionId: 'correlation-shortcuts', label: 'Correlation shortcuts', summary: 'Open a correlation timeline, trace detail, or bounded explanation from authorized evidence only.' }
    ],
    nextSteps: [
      { workstreamId: 'agent-audit-trace', label: 'Search scoped traces', allowed: true, capabilityIds: ['audit.trace.search'], traceId: 'trace-audit-dashboard-search' },
      { workstreamId: 'agent-audit-trace', label: 'Open failure evidence', allowed: true, capabilityIds: ['audit.trace.failureEvidence.read'], traceId: 'trace-audit-dashboard-failure' },
      { workstreamId: 'agent-audit-trace-summary-task', label: 'Start audit summary task', allowed: false, blockedReason: 'Autonomous audit-summary task lifecycle is deferred until backend runtime exists.', capabilityIds: ['audit.trace.summaryTask.start'] }
    ]
  },
  [auditTraceSurfaceActions.search, auditTraceSurfaceActions.openTimeline, auditTraceSurfaceActions.openFailureEvidence, auditTraceSurfaceActions.showInvestigationGuide]
);

export const auditTraceSearchSurface = envelope(
  'surface-audit-trace-search',
  'list-search',
  'Trace search results',
  'agent-audit-trace',
  {
    query: { filter: 'recent denials OR provider_blocked', pageSize: 10 },
    rows: [
      { traceId: 'trace-auth-denied-001', correlationId: 'corr-auth-denied-001', eventKind: 'AUTHORIZATION_DENIED', actor: 'member@example.test', workstream: 'user-admin', severity: 'warning', status: 'denied', redactionSummary: 'target account redacted' },
      { traceId: 'trace-provider-blocked-002', correlationId: 'corr-provider-blocked-002', eventKind: 'PROVIDER_BLOCKED', actor: 'WorkstreamRuntimeAgent', workstream: 'audit-trace', severity: 'critical', status: 'blocked_provider_or_runtime', redactionSummary: 'provider secret omitted' },
      { traceId: 'trace-tool-denied-003', correlationId: 'corr-tool-denied-003', eventKind: 'TOOL_BOUNDARY_DENIED', actor: 'readReferenceDoc(referenceId)', workstream: 'agent-admin', severity: 'warning', status: 'denied', redactionSummary: 'unassigned reference omitted' }
    ],
    pageInfo: { totalKnownCount: 3, nextCursor: undefined },
    partial: false,
    redaction: 'Raw JWTs, provider credentials, hidden prompts, and unauthorized tenant/customer evidence are omitted.'
  },
  [auditTraceSurfaceActions.openDetail, auditTraceSurfaceActions.openTimeline, auditTraceSurfaceActions.openFailureEvidence]
);

export const auditTraceDetailSurface = envelope(
  'surface-audit-trace-detail',
  'detail-edit',
  'Trace detail/evidence',
  'agent-audit-trace',
  {
    traceId: 'trace-provider-blocked-002',
    eventKind: 'PROVIDER_BLOCKED',
    timestamp: generatedAt,
    actor: 'WorkstreamRuntimeAgent',
    source: 'agent-audit-trace',
    correlationIds: ['corr-provider-blocked-002'],
    authorizationBasis: 'audit.trace.detail.read',
    decision: 'blocked_provider_or_runtime',
    redactedEvidence: 'Model-backed explanation was blocked because backend provider configuration was missing or blank.',
    redactionMetadata: { omittedFieldKeys: ['providerCredentialEnvVar', 'rawPrompt', 'hiddenPromptText', 'rawToolPayload'] },
    traceLinks: ['trace-provider-blocked-002']
  },
  [auditTraceSurfaceActions.openTimeline, auditTraceSurfaceActions.openFailureEvidence, auditTraceSurfaceActions.showInvestigationGuide]
);

export const auditTraceTimelineSurface = envelope(
  'surface-audit-trace-timeline',
  'audit-timeline',
  'Correlation timeline',
  'agent-audit-trace',
  {
    correlationId: 'corr-provider-blocked-002',
    nodes: [
      { nodeId: 'node-auth-context', sourceType: 'policy', summary: 'Selected AuthContext resolved and tenant/customer scope applied.', correlationId: 'corr-provider-blocked-002', status: 'allowed' },
      { nodeId: 'trace-provider-blocked-002', sourceType: 'model', summary: 'Provider configuration failed closed before model invocation.', correlationId: 'corr-provider-blocked-002', status: 'blocked_provider_or_runtime', traceId: 'trace-provider-blocked-002' },
      { nodeId: 'trace-agent-work-002', sourceType: 'request_response', summary: 'Audit/Trace workstream response returned actionable blocked-provider copy.', correlationId: 'corr-provider-blocked-002', status: 'ready', traceId: 'trace-agent-work-002' }
    ],
    partial: false,
    omittedCategories: [],
    redactionSummary: 'Unauthorized tenant/customer evidence and provider secrets are omitted.'
  },
  [auditTraceSurfaceActions.openDetail, auditTraceSurfaceActions.openFailureEvidence, auditTraceSurfaceActions.showInvestigationGuide]
);

export const auditTraceFailureEvidenceSurface = envelope(
  'surface-audit-trace-failure-evidence',
  'detail-edit',
  'Denial/provider/tool evidence',
  'agent-audit-trace',
  {
    category: 'provider_blocked',
    safeReason: 'Provider, tool, policy, and authorization failures are shown as redacted browser-safe evidence only.',
    userActionableNextSteps: ['Check selected AuthContext and required capability.', 'Open correlation timeline.', 'Ask Audit/Trace for an explanation after provider configuration is available.'],
    policyRefs: ['audit.trace.read', 'audit.trace.failureEvidence.read'],
    redactedDetails: { providerCredential: '[REDACTED]', rawPrompt: '[OMITTED]' },
    traceLinks: ['trace-provider-blocked-002', 'trace-agent-work-002']
  },
  [auditTraceSurfaceActions.openTimeline, auditTraceSurfaceActions.showInvestigationGuide]
);

export const auditTraceInvestigationGuideSurface = envelope(
  'surface-audit-trace-investigation-guide',
  'decision',
  'Investigation guidance',
  'agent-audit-trace',
  {
    recommendation: 'Continue only with backend-authorized, tenant-scoped evidence.',
    allowedActions: [
      { actionId: 'action-audit-trace-search', label: 'Refine search', capabilityId: 'audit.trace.search' },
      { actionId: 'action-audit-trace-timeline', label: 'Open timeline', capabilityId: 'audit.trace.timeline.read' }
    ],
    disabledActions: [
      { actionId: 'audit.trace.summaryTask.start', reason: 'Autonomous audit summary tasks are deferred until task lifecycle/provider/tool-boundary runtime is implemented.' }
    ],
    risk: 'low',
    traceLinks: ['corr-provider-blocked-002']
  },
  [auditTraceSurfaceActions.search, auditTraceSurfaceActions.openTimeline, auditTraceSurfaceActions.openFailureEvidence]
);

export const auditTraceStructuredSurfaces = [auditTraceDashboardSurface, auditTraceSearchSurface, auditTraceDetailSurface, auditTraceTimelineSurface, auditTraceFailureEvidenceSurface, auditTraceInvestigationGuideSurface];

export const userAdminDashboardSurface = envelope(
  'user-admin-dashboard',
  'dashboard',
  'User Admin dashboard',
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
    ],
    scopeVariants: [
      { role: 'SaaS Owner Admin', summary: 'Platform users visible; tenant user data is forbidden without active support access.', visibleActions: ['admin.users.dashboard.read', 'admin.audit.read'], deniedReason: 'SAAS_OWNER_SUPPORT_ACCESS_REQUIRED' },
      { role: 'Tenant Admin', summary: 'Tenant employees, Customer users, invitations, support access, access review, and scoped audit queues are visible.', visibleActions: ['admin.users.search', 'admin.invitations.create', 'admin.access_review.resolve'] },
      { role: 'Customer Admin', summary: 'Selected Customer users and invitations are visible; Tenant-level queues and support-access actions are forbidden.', visibleActions: ['admin.users.search'], deniedReason: 'CUSTOMER_ADMIN_TENANT_ACTION_DENIED' }
    ],
    stateFixtures: {
      loading: 'Loading dashboard cards and queues from scoped backend views.',
      empty: 'Empty tenant/customer scope with safe bootstrap invitation action only when admin.invitations.create is allowed.',
      error: 'Error state shows correlationId without privileged counts.',
      forbidden: 'Forbidden state hides counts and row identities for cross-tenant, disabled actor, or missing capability denial.',
      stale: 'Stale state preserves trace-user-admin-dashboard and disables mutations until refresh.'
    },
    expertiseManifest: {
      bundleId: 'user-admin-agent.expertise',
      skillManifestId: 'manifest-user-admin',
      referenceManifestId: 'reference-manifest-user-admin',
      compactManifestOnly: true,
      skills: [
        { skillId: 'ua.access-review-triage.v1', title: 'Access Review Triage', whenToUse: 'Use for stale memberships, risky roles, pending reviews, and proposed access remediation.' },
        { skillId: 'ua.invitation-drafting.v1', title: 'Invitation Drafting', whenToUse: 'Use when preparing invitation rationale or human-confirmed invitation actions.' },
        { skillId: 'ua.audit-summary.v1', title: 'Admin Audit Summary', whenToUse: 'Use when explaining changes, denials, and trace evidence.' }
      ],
      references: [
        { referenceId: 'ua.tenant-role-catalog.v1', title: 'Tenant Role and Capability Catalog', whenToConsult: 'Explain role meanings and capability ids.' },
        { referenceId: 'ua.access-review-policy.v1', title: 'Access Review Policy', whenToConsult: 'Evaluate stale access, review cadence, and escalation triggers.' },
        { referenceId: 'ua.last-admin-protection.v1', title: 'Last Admin Protection Rule', whenToConsult: 'Explain blocked removal, disable, or role downgrade.' }
      ],
      authorizedLoadExamples: [
        { tool: 'readSkill(skillId)', targetId: 'ua.access-review-triage.v1', decision: 'allowed', trace: 'SkillLoadTrace' },
        { tool: 'readReferenceDoc(referenceId)', targetId: 'ua.last-admin-protection.v1', decision: 'allowed', trace: 'ReferenceLoadTrace' }
      ],
      deniedLoadExamples: [
        { targetId: 'unassigned-skill', decision: 'denied', reason: 'unassigned skill denied', trace: 'SkillLoadTrace' },
        { targetId: 'unassigned-reference', decision: 'denied', reason: 'unassigned reference denied', trace: 'ReferenceLoadTrace' },
        { targetId: 'readReferenceDoc(referenceId)', decision: 'denied', reason: 'missing read_reference tool-boundary grant returns TOOL_BOUNDARY_DENIED', trace: 'AgentWorkTrace' }
      ],
      authorityBoundary: 'SkillDocument and ReferenceDocument text cannot grant roles, tenant scope, tool access, approval rights, or backend capabilities.',
      traceRequirements: ['PromptAssemblyTrace', 'SkillLoadTrace', 'ReferenceLoadTrace', 'AgentWorkTrace', 'AdminAuditEvent']
    }
  },
  [
    userAdminSurfaceActions.refreshDashboard,
    userAdminSurfaceActions.displayUserList,
    userAdminSurfaceActions.displayRoleCapabilityMatrix,
    userAdminSurfaceActions.createInvitation,
    userAdminSurfaceActions.previewRoleChange,
    userAdminSurfaceActions.startAccessReview,
    userAdminSurfaceActions.readAccessReview,
    userAdminSurfaceActions.openAdminAudit
  ]
);

// Legacy fixture alias preserved for stale-screen quarantine tests: surface-user-list now resolves conceptually to user-admin-user-list.
export const userAdminListSearchSurface = envelope(
  'user-admin-user-list',
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
    pageInfo: { totalKnownCount: 5, nextPageToken: 'page-token-user-admin-2' },
    emptyMessage: 'No users, invitations, memberships, or support grants match the current scoped query.',
    mobileFallback: 'table-to-card',
    dashboardOrigin: { surfaceId: 'user-admin-dashboard', queueId: 'access-review', traceId: 'trace-user-admin-dashboard' },
    scopeVariants: [
      { role: 'SaaS Owner Admin', rowPolicy: 'SaaS Owner users plus redacted tenant bootstrap metadata unless support access is active.', forbiddenActions: ['admin.support_access.grant', 'admin.roles.replace'] },
      { role: 'Tenant Admin', rowPolicy: 'Tenant employees, Customer Admins/Users, invitations, support grants, and access-review rows.', allowedActions: ['admin.users.detail.read', 'admin.invitations.resend', 'admin.roles.replace'] },
      { role: 'Customer Admin', rowPolicy: 'Selected Customer users and invitations only.', forbiddenActions: ['admin.support_access.grant', 'admin.roles.replace'] }
    ],
    stateFixtures: {
      loading: 'Loading table skeleton preserves filters and disables row mutations.',
      empty: 'Empty search distinguishes no matches from a redacted result set.',
      error: 'Error state keeps safe filters, pageToken, and correlation id.',
      forbidden: 'Forbidden list hides totals and identities for cross-tenant or Customer Admin Tenant-level denial.',
      stale: 'Stale page token disables row mutation actions until the list refreshes.'
    }
  },
  [
    userAdminSurfaceActions.searchUsers,
    userAdminSurfaceActions.displayUserDetail,
    userAdminSurfaceActions.displayRoleCapabilityMatrix,
    userAdminSurfaceActions.createInvitation,
    userAdminSurfaceActions.resendInvitation,
    userAdminSurfaceActions.revokeInvitation,
    userAdminSurfaceActions.addMembership,
    userAdminSurfaceActions.suspendMembership,
    userAdminSurfaceActions.reactivateMembership,
    userAdminSurfaceActions.removeMembership,
    userAdminSurfaceActions.replaceRole,
    userAdminSurfaceActions.removeRole,
    userAdminSurfaceActions.disableAccount,
    userAdminSurfaceActions.reactivateAccount,
    userAdminSurfaceActions.readSupportAccess,
    userAdminSurfaceActions.grantSupportAccess,
    userAdminSurfaceActions.revokeSupportAccess,
    userAdminSurfaceActions.extendSupportAccess,
    userAdminSurfaceActions.readAccessReview,
    userAdminSurfaceActions.approveRiskyAccess,
    userAdminSurfaceActions.openAdminAudit
  ]
);

export const userAdminDetailEditSurface = envelope(
  'user-admin-user-account',
  'detail-edit',
  'Tenant Admin account detail',
  'agent-user-admin',
  {
    recordId: 'user-acct-admin',
    recordLabel: 'Tenant Admin · admin@example.test',
    recordKind: 'account',
    summary: 'Scoped detail/edit surface for a tenant user account. Field editability is advisory; named User Admin capabilities remain authoritative.',
    fields: [
      { fieldId: 'displayName', label: 'Display name', value: 'Tenant Admin', editable: true, inputType: 'text' },
      { fieldId: 'email', label: 'Email', value: 'admin@example.test', editable: false, inputType: 'email', disabledReason: 'Email changes require an identity-provider reconciliation workflow.' },
      { fieldId: 'role', label: 'Membership role', value: 'Tenant Admin', editable: false, inputType: 'select', options: [{ value: 'tenant-admin', label: 'Tenant Admin' }, { value: 'member', label: 'Member' }], disabledReason: 'Backend authorization denied role replacement for the last tenant admin in this fixture.' },
      { fieldId: 'status', label: 'Account status', value: 'active', editable: false, inputType: 'text', disabledReason: 'Account suspension requires a governed support-access decision.' }
    ],
    version: 3,
    permissionState: {
      canEdit: true,
      reason: 'Profile fields are editable; role and status remain denied by backend policy for this fixture account.',
      authoritativeCapabilityId: userAdminCapabilities.listMembers
    },
    scopeVariants: [
      { role: 'SaaS Owner Admin', visibility: 'SaaS Owner account detail or redacted tenant target unless support access is active.', deniedReason: 'SAAS_OWNER_NO_SUPPORT_ACCESS' },
      { role: 'Tenant Admin', visibility: 'Tenant account detail, Customer memberships, invitations, support access, access review, and audit excerpts.', deniedReason: 'LAST_ADMIN_LOSS_DENIED for risky role replacement' },
      { role: 'Customer Admin', visibility: 'Selected Customer account detail only; Tenant employee memberships and support-access sections are forbidden.', deniedReason: 'CUSTOMER_ADMIN_TENANT_ACTION_DENIED' }
    ],
    stateFixtures: {
      loading: 'Loading account skeleton and disabled action panel.',
      empty: 'Empty scoped target with no memberships, invitations, or audit excerpts.',
      error: 'Error state shows retry and correlationId without cached sensitive detail.',
      forbidden: 'Forbidden detail hides target identity unless the backend marks it browser-safe.',
      stale: 'Stale detail disables profile, role, membership, account, support-access, and access-review mutations.'
    },
    denialExamples: ['cross-tenant', 'disabled actor', 'missing capability', 'role escalation', 'last-admin loss'],
    audit: {
      lastEventType: 'UserAdminDetailDisplayed',
      lastActor: 'Tenant Admin',
      traceIds: ['trace-user-admin-detail', 'trace-user-admin-row']
    }
  },
  [
    userAdminSurfaceActions.updateUserProfile,
    userAdminSurfaceActions.previewRoleChange,
    userAdminSurfaceActions.changeMemberRoles,
    userAdminSurfaceActions.requestIdentityRelink,
    userAdminSurfaceActions.completeIdentityRelink,
    userAdminSurfaceActions.addMembership,
    userAdminSurfaceActions.suspendMembership,
    userAdminSurfaceActions.reactivateMembership,
    userAdminSurfaceActions.removeMembership,
    userAdminSurfaceActions.replaceRole,
    userAdminSurfaceActions.removeRole,
    userAdminSurfaceActions.disableAccount,
    userAdminSurfaceActions.reactivateAccount,
    userAdminSurfaceActions.resendInvitation,
    userAdminSurfaceActions.revokeInvitation,
    userAdminSurfaceActions.readSupportAccess,
    userAdminSurfaceActions.grantSupportAccess,
    userAdminSurfaceActions.revokeSupportAccess,
    userAdminSurfaceActions.extendSupportAccess,
    userAdminSurfaceActions.readAccessReview,
    userAdminSurfaceActions.approveRiskyAccess,
    userAdminSurfaceActions.openAdminAudit
  ]
);

export const userAdminRoleCapabilityMatrixSurface = envelope(
  'user-admin-role-capability-matrix',
  'list-search',
  'User Admin role/capability matrix',
  'agent-user-admin',
  {
    query: 'assignable:true OR protected:last-admin',
    rows: [
      { id: 'role-tenant-admin', rowType: 'role-capability', role: 'Tenant Admin', capabilityId: 'USERADMIN_CHANGE_MEMBER_ROLES', assignable: false, policy: 'last-admin and delegation checks required', traceId: 'trace-useradmin-role-matrix-tenant-admin' },
      { id: 'role-tenant-employee', rowType: 'role-capability', role: 'Tenant Employee', capabilityId: 'USERADMIN_PREVIEW_ROLE_CHANGE', assignable: true, policy: 'preview before commit', traceId: 'trace-useradmin-role-matrix-employee' },
      { id: 'access-review', rowType: 'autonomous-task-capability', role: 'Access Review', capabilityId: 'USERADMIN_START_ACCESS_REVIEW_TASK', assignable: false, policy: 'blocked_provider_or_runtime until durable AutonomousAgent lifecycle is enabled', traceId: 'trace-useradmin-access-review-blocked' }
    ],
    pageInfo: { totalKnownCount: 3 },
    emptyMessage: 'No role/capability rows are visible for this scoped AuthContext.',
    mobileFallback: 'table-to-card',
    stateFixtures: {
      loading: 'Loading role/capability policy evidence from backend-authoritative scope.',
      forbidden: 'Forbidden matrix hides roles and capabilities when USERADMIN_LIST_ROLES_CAPABILITIES is denied.',
      validation: 'Invalid role deltas return server validation copy with correlation id.',
      trace: 'Every preview, denial, no-op, and applied role change carries trace links.'
    }
  },
  [userAdminSurfaceActions.previewRoleChange, userAdminSurfaceActions.changeMemberRoles, userAdminSurfaceActions.openAdminAudit]
);

export const userAdminAccessReviewSurface = envelope(
  'user-admin-access-review',
  'workflow-status',
  'User Admin access review',
  'agent-user-admin',
  {
    workflowId: 'user-admin-access-review',
    status: 'blocked_provider_or_runtime',
    summary: 'Durable access-review investigation is unavailable until the governed AutonomousAgent task lifecycle, model provider, and tool boundary are enabled. This is a blocked surface, not fake progress.',
    traceIds: ['trace-useradmin-access-review-blocked'],
    requiredCapabilityId: userAdminCapabilities.startAccessReviewTask,
    steps: [
      { stepId: 'authorize-task-start', label: 'Authorize USERADMIN_START_ACCESS_REVIEW_TASK in selected AuthContext', status: 'blocked' },
      { stepId: 'resolve-autonomous-agent', label: 'Resolve durable AutonomousAgent runtime and provider configuration', status: 'blocked' },
      { stepId: 'emit-trace', label: 'Emit blocked-provider/runtime trace reference for the user', status: 'completed' }
    ]
  },
  [userAdminSurfaceActions.startAccessReview, userAdminSurfaceActions.readAccessReview, userAdminSurfaceActions.cancelAccessReview, userAdminSurfaceActions.openAdminAudit]
);

export const agentAdminCatalogSurface = envelope(
  'surface-agent-admin-catalog',
  'list-search',
  'Agent Admin catalog',
  'agent-agent-admin',
  {
    query: 'tenant:tenant-acme status:active OR review',
    rows: [
      { id: 'agent-agent-admin', rowType: 'agent-definition', displayName: 'Agent Admin Agent', status: 'ACTIVE', authorityLevel: 'APPROVAL_REQUIRED', functionalAreaId: 'agent-admin', tracePolicy: 'PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace' },
      { id: 'agent-user-admin', rowType: 'agent-definition', displayName: 'User Admin Agent', status: 'ACTIVE', authorityLevel: 'APPROVAL_REQUIRED', functionalAreaId: 'user-admin', tracePolicy: 'PromptAssemblyTrace, SkillLoadTrace, AgentWorkTrace' }
    ],
    pageInfo: { totalKnownCount: 2 },
    emptyCopy: 'Empty when no governed AgentDefinition records are seeded.',
    mobileFallback: 'table-to-card',
    stateFixtures: {
      loading: 'Loading surface while backend list_definitions reads scoped AgentDefinition projections.',
      empty: 'Empty when no governed AgentDefinition records are seeded.',
      forbidden: 'Cross-tenant AgentDefinition ids return TARGET_NOT_FOUND_OR_FORBIDDEN and keep draft content hidden.',
      validation: 'validation-error preserves correlation id and input for behavior-change actions.',
      approvalRequired: 'approval-required is shown before prompt, manifest, model, or tool-boundary activation.',
      providerBlocked: 'MODEL_POLICY_DENIED and missing provider configuration render as safe blocked states.',
      traceLinked: 'PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, and AgentWorkTrace links are surfaced without provider secrets.'
    }
  },
  [agentAdminSurfaceActions.displayCatalog, agentAdminSurfaceActions.openAgentDetail, agentAdminSurfaceActions.openAgentTrace]
);

export const agentAdminDetailSurface = envelope(
  'surface-agent-admin-detail',
  'detail-edit',
  'Procurement Assistant readiness',
  'agent-agent-admin',
  {
    recordId: 'agent-definition-procurement-assistant',
    recordLabel: 'Procurement Assistant · active runtime binding',
    recordKind: 'agent-definition',
    summary: 'Effective behavior detail with active prompt, skill manifest, tool boundary, model ref, approval state, and trace links. Activation is blocked when any governed artifact is unapproved, stale, or missing.',
    fields: [
      { fieldId: 'status', label: 'Lifecycle status', value: 'active', editable: false, inputType: 'text', disabledReason: 'Disabled AgentDefinitions cannot be invoked or load skills.' },
      { fieldId: 'authorityLevel', label: 'Authority level', value: 'recommend-and-draft', editable: false, inputType: 'text', disabledReason: 'Authority expansion requires decision-card approval.' },
      { fieldId: 'activePromptRef', label: 'Active prompt/version', value: 'prompt-procurement-assistant@v7', editable: false, inputType: 'text' },
      { fieldId: 'manifestRef', label: 'Skill manifest', value: 'manifest-procurement-assistant@v4', editable: false, inputType: 'text' },
      { fieldId: 'toolBoundaryRef', label: 'Tool boundary', value: 'tool-boundary-procurement-assistant@v3', editable: false, inputType: 'text' },
      { fieldId: 'modelRef', label: 'Model ref', value: 'model-safe-default', editable: false, inputType: 'text', disabledReason: 'Provider secret values are never browser-visible.' }
    ],
    version: 9,
    permissionState: {
      canEdit: false,
      reason: 'Metadata is readable; changing prompts, skills, manifests, tool boundaries, models, or authority uses governed proposal surfaces.',
      authoritativeCapabilityId: agentDefinitionsCapability
    },
    audit: {
      lastEventType: 'AgentDefinitionReadinessDisplayed',
      lastActor: 'Tenant Admin',
      traceIds: ['trace-prompt-assembly-42', 'trace-skill-load-17', 'trace-agent-work-88']
    }
  },
  [agentAdminSurfaceActions.proposePromptDiff, agentAdminSurfaceActions.simulateToolBoundary, agentAdminSurfaceActions.manageModelRef, agentAdminSurfaceActions.openAgentTrace]
);

export const agentPromptGovernanceSurface = envelope(
  'surface-agent-prompt-governance',
  'governance-diff',
  'Prompt governance review',
  'agent-agent-admin',
  {
    proposalId: 'prompt-proposal-42',
    beforeSummary: 'Active prompt instructs the agent to draft recommendations and request approval before side effects.',
    afterSummary: 'Draft prompt adds clearer evidence citation rules but validation flags secret-like content and authority expansion language.',
    changes: [
      { path: 'prompt.body.evidenceRules', before: 'Summarize evidence.', after: 'Cite trace ids, source freshness, and confidence.', impact: 'Improves review quality.' },
      { path: 'validation.secretBoundary', before: 'pass', after: 'validation-error: secret-like token placeholder detected', impact: 'Blocks activation until removed.' },
      { path: 'review.status', before: 'draft', after: 'approval-required', impact: 'Human prompt steward must review before activation.' }
    ]
  },
  [agentAdminSurfaceActions.proposePromptDiff, agentAdminSurfaceActions.testPrompt, agentAdminSurfaceActions.openAgentTrace]
);

export const agentSkillManifestSurface = envelope(
  'surface-agent-skill-manifest-diff',
  'governance-diff',
  'Skill manifest and readSkill review',
  'agent-agent-admin',
  {
    proposalId: 'manifest-proposal-7',
    beforeSummary: 'Manifest exposes three compact skill hints and denies unassigned readSkill requests.',
    afterSummary: 'Proposal adds supplier-risk-review skill; approval is required because it broadens data interpretation guidance.',
    changes: [
      { path: 'assignedSkills[+]', before: undefined, after: 'skill-supplier-risk-review@v2', impact: 'New compact hint appears during prompt assembly.' },
      { path: 'denialHistory', before: 'unassigned skill denied', after: 'unassigned skill denied with SkillLoadTrace link', impact: 'Denial remains traceable.' },
      { path: 'review.state', before: 'draft', after: 'approval-required', impact: 'Reviewer approval required before activation.' }
    ]
  },
  [agentAdminSurfaceActions.approveSkillManifest, agentAdminSurfaceActions.testPrompt, agentAdminSurfaceActions.openAgentTrace]
);

export const agentToolBoundarySurface = envelope(
  'surface-agent-tool-boundary-diff',
  'governance-diff',
  'Tool boundary simulation review',
  'agent-agent-admin',
  {
    proposalId: 'tool-boundary-proposal-5',
    beforeSummary: 'Agent can read approved supplier records and draft recommendations only.',
    afterSummary: 'Proposal requests external email side effect; approval-required and policy simulation must pass before activation.',
    changes: [
      { path: 'toolGrants.email.send', before: 'not assigned', after: 'requested', impact: 'External side effect requires decision-card approval.' },
      { path: 'simulation.result', before: 'not run', after: 'policy-blocked: TOOL_BOUNDARY_DENIED for unknown recipient domain', impact: 'Activation denied until policy issue is resolved.' },
      { path: 'dataScope.customer', before: 'customer-northwind', after: 'customer-northwind', impact: 'No cross-customer expansion.' }
    ]
  },
  [agentAdminSurfaceActions.simulateToolBoundary, agentAdminSurfaceActions.openAgentTrace]
);

export const agentModelRefsSurface = envelope(
  'surface-agent-model-refs',
  'list-search',
  'Model refs and policy aliases',
  'agent-agent-admin',
  {
    query: 'providerAlias:safe status:active OR disabled',
    rows: [
      { id: 'model-safe-default', rowType: 'model-ref', providerAlias: 'approved-primary', mode: 'reasoning', status: 'active', secretVisibility: 'redacted', traceId: 'trace-model-read' },
      { id: 'model-disabled-experimental', rowType: 'model-ref', providerAlias: 'experimental-disabled', mode: 'tool-use', status: 'disabled', deniedReason: 'MODEL_POLICY_DENIED', secretVisibility: 'redacted', traceId: 'trace-model-denied' }
    ],
    pageInfo: { totalKnownCount: 2 },
    emptyMessage: 'No model refs match the scoped query; create provider aliases server-side before exposing browser-safe refs.',
    mobileFallback: 'table-to-card'
  },
  [agentAdminSurfaceActions.manageModelRef, agentAdminSurfaceActions.openAgentTrace]
);

export const agentTestConsoleSurface = envelope(
  'surface-agent-test-console',
  'workflow-status',
  'No-side-effect agent test console',
  'agent-agent-admin',
  {
    workflowId: 'agent-runtime-test-procurement-assistant',
    status: 'waiting-for-human',
    steps: [
      { stepId: 'resolve-agent-definition', label: 'Resolve active AgentDefinition and AuthContext', status: 'completed' },
      { stepId: 'assemble-prompt', label: 'Assemble prompt with compact manifest and redactions', status: 'completed' },
      { stepId: 'read-skill-denial', label: 'Unassigned readSkill(skillId) denied and linked to SkillLoadTrace', status: 'blocked' },
      { stepId: 'tool-boundary-check', label: 'Side-effecting tools disabled in test mode', status: 'waiting-for-human' }
    ]
  },
  [agentAdminSurfaceActions.testPrompt, agentAdminSurfaceActions.openAgentTrace]
);

export const agentBehaviorProposalSurface = envelope(
  'surface-agent-behavior-proposal',
  'decision',
  'Review agent behavior proposal',
  'agent-agent-admin',
  {
    decisionId: 'decision-agent-behavior-12',
    recommendation: 'Approve prompt evidence rules, reject email tool expansion until simulation passes.',
    riskScore: 81,
    confidenceScore: 77,
    evidence: [
      { evidenceId: 'evidence-prompt-diff', label: 'Prompt diff', summary: 'Diff improves citations but initially failed validation for secret-like content.' },
      { evidenceId: 'evidence-tool-simulation', label: 'Tool simulation', summary: 'External email side effect remains approval-required and currently policy-blocked.' },
      { evidenceId: 'evidence-traces', label: 'Trace links', summary: 'PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace are linked for review.' }
    ]
  },
  [agentAdminSurfaceActions.approveSkillManifest, agentAdminSurfaceActions.openAgentTrace]
);

export const agentAdminTraceSurface = envelope(
  'surface-agent-admin-trace',
  'audit-timeline',
  'Agent admin traces',
  'agent-agent-admin',
  {
    events: [
      { eventId: 'prompt-assembly-42', occurredAt: generatedAt, actor: 'AgentRuntimeResolver', action: 'PromptAssemblyTrace emitted with compact manifest and redactions', traceId: 'trace-prompt-assembly-42' },
      { eventId: 'skill-load-17', occurredAt: generatedAt, actor: 'readSkill(skillId)', action: 'Unassigned skill denied safely', traceId: 'trace-skill-load-17' },
      { eventId: 'agent-work-88', occurredAt: generatedAt, actor: 'Procurement Assistant', action: 'AgentWorkTrace recorded no-side-effect test run', traceId: 'trace-agent-work-88' }
    ]
  },
  [agentAdminSurfaceActions.openAgentTrace]
);

export const dashboardSurface = envelope('surface-dashboard', 'dashboard', 'Tenant attention dashboard', 'agent-my-account', { cards: [{ cardId: 'card-open-decisions', label: 'Open decisions', value: 2, severity: 'warning' }], scopeNote: 'Full-core/demo surface; the default five core v0 starter acceptance target is markdown_response.' }, [surfaceActionsByIntent.read]);
export const listSearchSurface = userAdminListSearchSurface;
export const detailEditSurface = userAdminDetailEditSurface;
export const decisionSurface = envelope('surface-decision-card', 'decision', 'Approve bounded outreach plan', 'agent-governance-policy', { decisionId: 'decision-1', recommendation: 'Approve after evidence review.', riskScore: 72, confidenceScore: 84, evidence: [{ evidenceId: 'evidence-1', label: 'Trace summary', summary: 'Agent stayed within tool boundary.' }] }, [surfaceActionsByIntent.approval, surfaceActionsByIntent.trace]);
export const auditTimelineSurface = envelope('surface-audit-timeline', 'audit-timeline', 'Admin audit timeline', 'agent-audit-trace', { events: [{ eventId: 'audit-1', occurredAt: generatedAt, actor: 'Tenant Admin', action: 'invited user', traceId: 'trace-invite' }] }, [surfaceActionsByIntent.trace]);
export const workflowStatusSurface = envelope('surface-workflow-status', 'workflow-status', 'Invitation workflow status', 'agent-user-admin', { workflowId: 'workflow-invite-1', status: 'waiting-for-human', steps: [{ stepId: 'send-email', label: 'Send invitation email', status: 'waiting-for-human' }] }, [surfaceActionsByIntent.workflow]);
export const governanceDiffSurface = envelope('surface-governance-diff', 'governance-diff', 'Policy proposal diff', 'agent-governance-policy', { proposalId: 'proposal-1', beforeSummary: 'Manual approval over 75 risk.', afterSummary: 'Manual approval over 65 risk.', changes: [{ path: 'risk.approvalThreshold', before: '75', after: '65', impact: 'More decisions require human review.' }] }, [surfaceActionsByIntent.proposal, surfaceActionsByIntent.governance]);
export const outcomeSurface = envelope('surface-outcome-review', 'outcome', 'Outcome review', 'agent-governance-policy', { outcomeId: 'outcome-1', metrics: [{ metricId: 'decision-cycle-time', label: 'Decision cycle time', current: 4, target: 2, unit: 'hours' }] }, [surfaceActionsByIntent.read]);

export const fullCoreDemoSurfaceEnvelopes = [
  userAdminDashboardSurface,
  userAdminListSearchSurface,
  userAdminRoleCapabilityMatrixSurface,
  userAdminAccessReviewSurface,
  agentAdminCatalogSurface,
  agentAdminDetailSurface,
  agentPromptGovernanceSurface,
  agentSkillManifestSurface,
  agentToolBoundarySurface,
  agentModelRefsSurface,
  agentTestConsoleSurface,
  agentBehaviorProposalSurface,
  agentAdminTraceSurface,
  ...auditTraceStructuredSurfaces,
  dashboardSurface,
  detailEditSurface,
  decisionSurface,
  auditTimelineSurface,
  workflowStatusSurface,
  governanceDiffSurface,
  outcomeSurface
];

export const canonicalSurfaceEnvelopes = [
  ...fiveCoreV0MarkdownSurfaces
];
export const allSurfaceActions: SurfaceAction[] = [...Object.values(surfaceActionsByIntent), ...Object.values(userAdminSurfaceActions), ...Object.values(agentAdminSurfaceActions)];

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

export const displayUserDetailActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Display user account detail for admin@example.test. Backend authority, scoped read, audit, and trace checks remain capability-backed.',
  correlationId: 'corr-display-user-detail',
  traceIds: ['trace-display-user-detail', 'trace-user-admin-detail'],
  resultSurface: userAdminDetailEditSurface
};

export const displayAgentCatalogActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Display the Agent Admin catalog with loading, empty, forbidden, approval-required, validation, and trace-linked reference states.',
  correlationId: 'corr-display-agent-catalog',
  traceIds: ['trace-display-agent-catalog'],
  resultSurface: agentAdminCatalogSurface
};

export const displayAgentDetailActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Display AgentDefinition readiness, governed refs, approval gates, redacted model refs, and trace links.',
  correlationId: 'corr-display-agent-detail',
  traceIds: ['trace-display-agent-detail', 'trace-agent-work-88'],
  resultSurface: agentAdminDetailSurface
};

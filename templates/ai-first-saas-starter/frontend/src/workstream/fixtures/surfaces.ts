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
  'surface-request': {
    actionId: 'action-show-user-list',
    label: 'Show users list',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.listMembers,
    idempotency: { required: false },
    resultSurface: { appendSurfaceType: 'table', openPlacement: 'inline' },
    shellRequest: {
      requestType: 'show_surface',
      targetFunctionalAgentId: 'agent-user-admin',
      targetSurfaceId: 'surface-user-admin-list',
      displayText: 'Show users list'
    },
    audit: { eventType: 'UserListSurfaceRequested', traceRequired: true }
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-dashboard', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminDashboardRead', traceRequired: true }
  },
  displayUserList: {
    actionId: 'action-display-user-list',
    label: 'Display user list view',
    intent: 'read',
    capabilityId: userAdminCapabilities.listMembers,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-list', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminListDisplayed', traceRequired: true }
  },
  searchUsers: {
    actionId: 'action-search-users',
    label: 'Search users and invitations',
    intent: 'read',
    capabilityId: userAdminCapabilities.listMembers,
    inputSchemaRef: 'schema.user-admin.search.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-list', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminDirectorySearched', traceRequired: true }
  },
  displayUserDetail: {
    actionId: 'action-display-user-detail',
    label: 'Display user account detail',
    intent: 'read',
    capabilityId: userAdminCapabilities.listMembers,
    inputSchemaRef: 'schema.user-admin.detail.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-detail-admin', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-role-change-preview', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-detail-admin', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminMemberRolesChanged', traceRequired: true }
  },
  createInvitation: {
    actionId: 'action-invite-user',
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
    actionId: 'action-useradmin-resend-invitation',
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
    actionId: 'action-useradmin-revoke-invitation',
    label: 'Revoke expired invitation',
    intent: 'command',
    capabilityId: userAdminCapabilities.revokeInvitation,
    inputSchemaRef: 'schema.invitation.revoke.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-list', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-detail-admin', openPlacement: 'inline' },
    audit: { eventType: 'MembershipAddRequested', traceRequired: true }
  },
  suspendMembership: {
    actionId: 'action-useradmin-disable-member',
    label: 'Disable member',
    intent: 'command',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    inputSchemaRef: 'schema.user-admin.member-status.update.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-list', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminMemberStatusChanged', traceRequired: true }
  },
  reactivateMembership: {
    actionId: 'action-useradmin-reactivate-member',
    label: 'Reactivate member',
    intent: 'command',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    inputSchemaRef: 'schema.user-admin.member-status.update.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-list', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminMemberStatusChanged', traceRequired: true }
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-detail-admin', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-detail-admin', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-detail-admin', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-detail-admin', openPlacement: 'inline' },
    audit: { eventType: 'IdentityRelinkCompleted', traceRequired: true }
  },
  readSupportAccess: {
    actionId: 'action-read-support-access',
    label: 'Read support access grants',
    intent: 'read',
    capabilityId: userAdminCapabilities.viewTraceReference,
    inputSchemaRef: 'schema.support-access.search.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-list', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-detail-admin', openPlacement: 'inline' },
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


const myAccountCapabilities = {
  viewSummary: 'my_account.view_summary',
  viewContext: 'my_account.view_context',
  updateProfileSettings: 'my_account.update_profile_settings',
  listNextSteps: 'my_account.list_next_steps',
  openAuthorizedWorkstream: 'my_account.open_authorized_workstream',
  askAgent: 'my_account.ask_agent',
  viewOwnTraceRefs: 'my_account.view_own_trace_refs'
} as const;

export const myAccountSurfaceActions = {
  showDashboard: {
    actionId: 'action-show-my-account-dashboard',
    label: 'Refresh My Account summary',
    intent: 'read',
    capabilityId: myAccountCapabilities.viewSummary,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-my-account-dashboard', openPlacement: 'inline' },
    audit: { eventType: 'MyAccountSummaryDisplayed', traceRequired: true }
  },
  showProfile: {
    actionId: 'action-show-my-profile',
    label: 'Show user profile',
    intent: 'read',
    capabilityId: myAccountCapabilities.viewSummary,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-my-profile', openPlacement: 'inline' },
    audit: { eventType: 'UserProfileDisplayed', traceRequired: true }
  },
  showSettings: {
    actionId: 'action-show-my-settings',
    label: 'Show user settings',
    intent: 'read',
    capabilityId: myAccountCapabilities.viewSummary,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-my-settings', openPlacement: 'inline' },
    audit: { eventType: 'UserSettingsDisplayed', traceRequired: true }
  },
  updateProfile: {
    actionId: 'action-update-my-profile',
    label: 'Save profile changes',
    intent: 'command',
    capabilityId: myAccountCapabilities.updateProfileSettings,
    inputSchemaRef: 'schema.my-account.profile.update.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-my-profile', openPlacement: 'inline' },
    audit: { eventType: 'UserProfileUpdateRequested', traceRequired: true }
  },
  updateSettings: {
    actionId: 'action-update-my-settings',
    label: 'Save settings changes',
    intent: 'command',
    capabilityId: myAccountCapabilities.updateProfileSettings,
    inputSchemaRef: 'schema.my-account.settings.update.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-my-settings', openPlacement: 'inline' },
    audit: { eventType: 'UserSettingsUpdateRequested', traceRequired: true }
  },
  openUserAdmin: {
    actionId: 'action-open-user-admin',
    label: 'Open User Admin',
    intent: 'surface-request',
    capabilityId: myAccountCapabilities.openAuthorizedWorkstream,
    inputSchemaRef: 'schema.my-account.open-workstream.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-list', openPlacement: 'deep-link' },
    shellRequest: { requestType: 'open_workstream', targetFunctionalAgentId: 'agent-user-admin', targetSurfaceId: 'surface-user-admin-list', displayText: 'Open User Admin' },
    audit: { eventType: 'MyAccountOpenUserAdminRequested', traceRequired: true }
  },
  openAgentAdmin: {
    actionId: 'action-open-agent-admin',
    label: 'Open Agent Admin',
    intent: 'surface-request',
    capabilityId: myAccountCapabilities.openAuthorizedWorkstream,
    inputSchemaRef: 'schema.my-account.open-workstream.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-catalog', openPlacement: 'deep-link' },
    shellRequest: { requestType: 'open_workstream', targetFunctionalAgentId: 'agent-agent-admin', targetSurfaceId: 'surface-agent-admin-catalog', displayText: 'Open Agent Admin' },
    audit: { eventType: 'MyAccountOpenAgentAdminRequested', traceRequired: true }
  },
  openAuditTrace: {
    actionId: 'action-open-audit-trace',
    label: 'Open audit timeline',
    intent: 'trace',
    capabilityId: myAccountCapabilities.viewOwnTraceRefs,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-timeline', openPlacement: 'inline' },
    audit: { eventType: 'AuditTimelineOpened', traceRequired: true }
  },
  signOut: {
    actionId: 'action-sign-out',
    label: 'Sign out',
    intent: 'command',
    capabilityId: myAccountCapabilities.viewSummary,
    requiresConfirmation: true,
    disabled: { reasonCode: 'AUTHKIT_SESSION_ACTION', message: 'Sign-out is handled by the shell AuthKit session boundary, not by granting backend authority.' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-my-account-dashboard', openPlacement: 'inline' },
    audit: { eventType: 'SessionSignOutRequested', traceRequired: true }
  }
} satisfies Record<string, SurfaceAction>;

export const myAccountDashboardSurface = envelope(
  'surface-my-account-dashboard',
  'dashboard',
  'My Account',
  'agent-my-account',
  {
    cards: [
      { cardId: 'card-my-profile', label: 'Signed-in profile', value: 'Tenant Admin', severity: 'info' },
      { cardId: 'card-my-settings', label: 'Color mode', value: 'system', severity: 'info' },
      { cardId: 'card-current-context', label: 'Selected AuthContext', value: tenantAdminAuthContext.tenantName, severity: 'info' }
    ],
    sections: [
      { sectionId: 'authority-basis', label: 'Authority basis', summary: 'Backend-visible capabilities, selected membership, and Tenant/Customer scope determine available actions.' },
      { sectionId: 'self-service', label: 'Self-service only', summary: 'Profile/settings edits are limited to allowed self-service fields; role, membership, and security changes stay in admin workstreams.' },
      { sectionId: 'traceability', label: 'Traceability', summary: 'Summary reads, denials, settings writes, workstream opens, and model-backed turns preserve trace and correlation references.' }
    ],
    nextSteps: [
      { workstreamId: 'agent-user-admin', label: 'Review users and invitations', allowed: true, capabilityIds: ['secure-tenant-user-foundation'], traceId: 'trace-my-account-next-user-admin' },
      { workstreamId: 'agent-agent-admin', label: 'Review governed agent readiness', allowed: true, capabilityIds: ['agent_admin.list_definitions'], traceId: 'trace-my-account-next-agent-admin' },
      { workstreamId: 'agent-audit-trace', label: 'Open My Account traces', allowed: true, capabilityIds: [myAccountCapabilities.viewOwnTraceRefs], traceId: 'trace-my-account-next-audit' },
      { workstreamId: 'agent-billing', label: 'Billing', allowed: false, blockedReason: 'Billing workstream remains hidden unless backend capability summary grants billing.read.', capabilityIds: ['billing.read'] }
    ],
    blockedState: { reasonCode: 'NO_SELECTED_CONTEXT_SAFE_GLOBAL_ONLY', message: 'Account-global safe fields can render without selected context, but scoped actions are blocked until a Tenant/Customer context is selected.', recovery: 'Select an active membership context before running scoped actions.' }
  },
  [myAccountSurfaceActions.showProfile, myAccountSurfaceActions.showSettings, myAccountSurfaceActions.openUserAdmin, myAccountSurfaceActions.openAgentAdmin, myAccountSurfaceActions.openAuditTrace, myAccountSurfaceActions.signOut]
);

export const myAccountProfileSurface = envelope(
  'surface-my-profile',
  'detail-edit',
  'User profile',
  'agent-my-account',
  {
    recordId: 'acct-admin-profile',
    recordLabel: 'Tenant Admin · admin@example.test',
    recordKind: 'profile',
    summary: 'Signed-in user profile. Editable fields are advisory UI hints; backend validation enforces allowed self-service fields and tenant isolation.',
    fields: [
      { fieldId: 'displayName', label: 'Display name', value: 'Tenant Admin', editable: true, inputType: 'text' },
      { fieldId: 'email', label: 'Email', value: 'admin@example.test', editable: false, inputType: 'email', disabledReason: 'Email is owned by WorkOS/AuthKit identity reconciliation.' },
      { fieldId: 'locale', label: 'Locale', value: 'en-US', editable: false, inputType: 'select', disabledReason: 'Locale changes are deferred beyond My Account v0.' },
      { fieldId: 'timeZone', label: 'Time zone', value: 'America/New_York', editable: false, inputType: 'text', disabledReason: 'Time zone changes are deferred beyond My Account v0.' }
    ],
    version: 1,
    permissionState: { canEdit: true, reason: 'Only self-owned display name is mutable in My Account v0.', authoritativeCapabilityId: myAccountCapabilities.updateProfileSettings },
    audit: { lastEventType: 'UserProfileDisplayed', lastActor: 'Tenant Admin', traceIds: ['trace-my-profile', 'trace-my-account-profile-settings'] },
    denialExamples: ['unsupported self-service field', 'cross-user target', 'disabled account', 'inactive membership']
  },
  [myAccountSurfaceActions.updateProfile, myAccountSurfaceActions.openAuditTrace]
);

export const myAccountSettingsSurface = envelope(
  'surface-my-settings',
  'detail-edit',
  'User settings',
  'agent-my-account',
  {
    recordId: 'acct-admin-settings',
    recordLabel: 'Tenant Admin settings',
    recordKind: 'settings',
    summary: 'Personal settings for the workstream shell. Backend persistence, no-op, idempotency, and validation errors are reflected as capability action results.',
    fields: [
      { fieldId: 'preferredColorMode', label: 'Color mode', value: 'system', editable: true, inputType: 'select', options: [{ value: 'system', label: 'System' }, { value: 'light', label: 'Light' }, { value: 'dark', label: 'Dark' }] },
      { fieldId: 'notificationDigest', label: 'Notification digest', value: 'daily', editable: false, inputType: 'select', disabledReason: 'Notification digest is deferred beyond My Account v0.' },
      { fieldId: 'composerDensity', label: 'Composer density', value: 'comfortable', editable: false, inputType: 'select', disabledReason: 'Composer density is deferred beyond My Account v0.' }
    ],
    version: 1,
    permissionState: { canEdit: true, reason: 'Only allowed preference fields are sent to the backend; role and capability fields are not browser-editable.', authoritativeCapabilityId: myAccountCapabilities.updateProfileSettings },
    audit: { lastEventType: 'UserSettingsDisplayed', lastActor: 'Tenant Admin', traceIds: ['trace-my-settings', 'trace-my-account-profile-settings'] },
    stateFixtures: { validation: 'Unsupported fields return validation/forbidden copy with correlation id.', noOp: 'Submitting unchanged preferences returns no-op with trace.', forbidden: 'Disabled account or inactive membership returns a safe denial.' }
  },
  [myAccountSurfaceActions.updateSettings, myAccountSurfaceActions.openAuditTrace]
);

export const myAccountTraceSurface = envelope(
  'surface-my-account-trace',
  'audit-timeline',
  'My Account traces',
  'agent-my-account',
  {
    events: [
      { eventId: 'trace-my-summary', occurredAt: generatedAt, actor: 'My Account Agent', action: 'Protected summary read emitted my_account.view_summary trace', traceId: 'trace-surface-my-account-dashboard' },
      { eventId: 'trace-my-settings-write', occurredAt: generatedAt, actor: 'Tenant Admin', action: 'Self-service settings update audited with idempotency key', traceId: 'trace-my-account-profile-settings' },
      { eventId: 'trace-my-agent-turn', occurredAt: generatedAt, actor: 'WorkstreamRuntimeAgent', action: 'PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, and AgentWorkTrace linked to Ask My Account', traceId: 'trace-surface-v0-my-account-markdown' }
    ]
  },
  [myAccountSurfaceActions.openAuditTrace]
);

export const myAccountStructuredSurfaces = [myAccountDashboardSurface, myAccountProfileSurface, myAccountSettingsSurface, myAccountTraceSurface];

export const displayMyAccountDashboardActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Displayed My Account summary with backend-visible authority, selected context, next steps, and trace links.',
  correlationId: 'corr-display-my-account-dashboard',
  traceIds: ['trace-display-my-account-dashboard'],
  resultSurface: myAccountDashboardSurface
};

export const displayMyAccountProfileActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Displayed signed-in profile with self-service field boundaries and audit trace links.',
  correlationId: 'corr-display-my-profile',
  traceIds: ['trace-display-my-profile'],
  resultSurface: myAccountProfileSurface
};

export const displayMyAccountSettingsActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Displayed signed-in settings with validation, no-op, and idempotency guidance.',
  correlationId: 'corr-display-my-settings',
  traceIds: ['trace-display-my-settings'],
  resultSurface: myAccountSettingsSurface
};

export const updateMyAccountSettingsActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'My Account profile/settings changes were accepted by the backend-authoritative self-service capability.',
  correlationId: 'corr-update-my-account-settings',
  traceIds: ['trace-my-account-profile-settings'],
  resultSurface: myAccountSettingsSurface
};

export const userAdminMarkdownSurface = markdownResponseEnvelope(
  'surface-v0-user-admin-markdown',
  'User Admin v0 response',
  'agent-user-admin',
  '## User Admin\n\n### Available now\n- Ask UserAdminAgent for bounded guidance about invitations, memberships, role changes, last-admin protection, and access-risk summaries.\n- Successful message submission returns a backend-authorized `markdown_response` with scoped evidence from `userAdminEvidence.read`, `readSkill`, and `readReferenceDoc` through governed tool boundaries.\n- Guidance is read-only: no direct mutation of invitations, memberships, roles, capabilities, authorization state, or provider configuration. Open deterministic User Admin surfaces for actual changes.\n\n### Provider/runtime blocked\nMissing provider or runtime configuration returns a typed `system_message` with safe recovery steps and trace links, never provider secrets or deterministic canned success.'
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
  '## Governance/Policy\n\n### Available now\n- Ask about policy guardrails, approval boundaries, pending proposals, simulations, and safe next steps.\n- Open backend-authoritative dashboard, inventory, proposal, simulation, decision, and trace-linked blocked-task surfaces.\n\n### Authority guardrail\nFrontend actions only reflect backend capability state; approval, activation, rollback, and analysis remain denied or blocked safely when backend authority/runtime is missing.'
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
  'surface-user-admin-dashboard',
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

// Legacy fixture alias preserved for stale-screen quarantine tests: old user-admin-user-list now resolves conceptually to surface-user-admin-list.
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
    pageInfo: { totalKnownCount: 5, nextPageToken: 'page-token-user-admin-2' },
    emptyMessage: 'No users, invitations, memberships, or support grants match the current scoped query.',
    mobileFallback: 'table-to-card',
    dashboardOrigin: { surfaceId: 'surface-user-admin-dashboard', legacySurfaceId: 'user-admin-dashboard', queueId: 'access-review', traceId: 'trace-user-admin-dashboard' },
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
  'surface-user-admin-detail-admin',
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
    accessManagement: {
      advisoryNotice: 'Frontend controls are advisory only; /api/workstream/actions and backend User Admin services remain authoritative for USERADMIN_UPDATE_MEMBER_STATUS, USERADMIN_PREVIEW_ROLE_CHANGE, and USERADMIN_CHANGE_MEMBER_ROLES.',
      memberStatus: {
        accountStatus: 'active',
        membershipStatus: 'active',
        statusActionIds: ['action-useradmin-disable-member', 'action-useradmin-reactivate-member'],
        denialHints: ['last-admin protection blocks disabling the final Tenant Admin', 'self-disable is denied without an explicit handoff policy', 'disabled actors cannot submit access-management actions'],
        noOpMessage: 'Repeated disable/reactivate submissions return no-op system_message feedback with idempotency evidence instead of duplicate side effects.',
        idempotencyKeySource: 'client-generated',
        traceLinks: ['trace-useradmin-status-preview', 'trace-useradmin-self-disable-denied']
      },
      roleChangePreview: {
        surfaceContract: 'user_admin.role_change_preview.v1',
        currentRoles: ['Tenant Admin'],
        proposedRoles: ['Tenant Employee'],
        capabilityDelta: { added: [], removed: ['USERADMIN_CHANGE_MEMBER_ROLES', 'USERADMIN_UPDATE_MEMBER_STATUS'], unchanged: ['USERADMIN_LIST_MEMBERS'] },
        affectedWorkstreams: ['User Admin', 'Governance/Policy', 'Audit/Trace'],
        policyHints: ['preview before commit', 'last-admin preservation required', 'approval policy applies before side effects'],
        lastAdminImpact: 'denied: this change would remove the final effective Tenant Admin',
        approvalRequired: true,
        noOp: false,
        traceLinks: ['trace-useradmin-role-preview', 'trace-useradmin-last-admin-denied']
      }
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

export const userAdminRoleChangePreviewSurface = envelope(
  'surface-user-admin-role-change-preview',
  'detail-edit',
  'User Admin role change preview',
  'agent-user-admin',
  {
    recordId: 'membership-admin-role-preview',
    recordLabel: 'Role change preview · Tenant Admin to Tenant Employee',
    recordKind: 'role-change-preview',
    summary: 'Backend-shaped user_admin.role_change_preview.v1 evidence for capability delta, affected workstreams, policy hints, last-admin impact, no-op/idempotency, and trace links before mutation.',
    permissionState: {
      canEdit: false,
      reason: 'Preview is advisory evidence; applying roles must submit action-useradmin-change-member-roles and backend authorization may still deny.',
      authoritativeCapabilityId: userAdminCapabilities.previewRoleChange
    },
    accessManagement: {
      advisoryNotice: 'This preview does not grant authority. Backend USERADMIN_CHANGE_MEMBER_ROLES enforces tenant/customer scope, last-admin preservation, disabled-user denial, approval policy, idempotency, and audit traces.',
      roleChangePreview: {
        surfaceContract: 'user_admin.role_change_preview.v1',
        currentRoles: ['Tenant Admin'],
        proposedRoles: ['Tenant Employee'],
        capabilityDelta: { added: [], removed: ['USERADMIN_CHANGE_MEMBER_ROLES', 'USERADMIN_UPDATE_MEMBER_STATUS'], unchanged: ['USERADMIN_LIST_MEMBERS', 'USERADMIN_PREVIEW_ROLE_CHANGE'] },
        affectedWorkstreams: ['User Admin', 'Agent Admin', 'Governance/Policy', 'Audit/Trace'],
        policyHints: ['last-admin preservation blocks commit', 'approval required for role downgrade', 'manual replay with the same idempotency key returns no-op evidence'],
        lastAdminImpact: 'last-admin-denied: would remove the final effective Tenant Admin for tenant-acme',
        approvalRequired: true,
        noOp: false,
        traceLinks: ['trace-useradmin-role-preview', 'trace-useradmin-last-admin-denied', 'trace-useradmin']
      },
      memberStatus: {
        accountStatus: 'active',
        membershipStatus: 'active',
        statusActionIds: ['action-useradmin-disable-member', 'action-useradmin-reactivate-member'],
        denialHints: ['self-disable remains denied by backend even if a button is visible', 'disabled-user actors receive system_message denial'],
        noOpMessage: 'Role preview is read-only; commit idempotency belongs to action-useradmin-change-member-roles.',
        idempotencyKeySource: 'client-generated',
        traceLinks: ['trace-useradmin-role-preview']
      }
    },
    traceLinks: ['trace-useradmin-role-preview', 'trace-useradmin-last-admin-denied']
  },
  [userAdminSurfaceActions.previewRoleChange, userAdminSurfaceActions.changeMemberRoles, userAdminSurfaceActions.openAdminAudit]
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


const governancePolicyCapabilities = {
  readDashboard: 'governance.policy.read',
  simulateProposal: 'governance.policy.simulate',
  draftProposal: 'governance.policy.propose',
  approveProposal: 'governance.policy.approve',
  activatePolicyChange: 'governance.policy.activate',
  rollbackPolicyChange: 'governance.policy.rollback',
  startImpactAnalysis: 'governance.policy.analysis.start',
  readImpactAnalysis: 'governance.policy.analysis.read',
  openTrace: 'audit.trace.read'
} as const;

export const governancePolicySurfaceActions = {
  showDashboard: {
    actionId: 'action-govpol-show-dashboard',
    label: 'Refresh governance dashboard',
    intent: 'read',
    capabilityId: governancePolicyCapabilities.readDashboard,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-dashboard', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyDashboardRead', traceRequired: true }
  },
  showInventory: {
    actionId: 'action-govpol-show-policy-inventory',
    label: 'Show policy inventory',
    intent: 'read',
    capabilityId: governancePolicyCapabilities.readDashboard,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-inventory', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyInventoryRead', traceRequired: true }
  },
  draftProposal: {
    actionId: 'action-govpol-draft-proposal',
    label: 'Draft policy proposal',
    intent: 'proposal',
    capabilityId: governancePolicyCapabilities.draftProposal,
    inputSchemaRef: 'schema.governance-policy.proposal.draft.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-proposal', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyProposalDrafted', traceRequired: true }
  },
  simulateProposal: {
    actionId: 'action-govpol-simulate-proposal',
    label: 'Run deterministic simulation',
    intent: 'governance',
    capabilityId: governancePolicyCapabilities.simulateProposal,
    inputSchemaRef: 'schema.governance-policy.simulation.v1',
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-simulation', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicySimulationRequested', traceRequired: true }
  },
  decideProposal: {
    actionId: 'action-govpol-decide-proposal',
    label: 'Approve proposal',
    intent: 'approval',
    capabilityId: governancePolicyCapabilities.approveProposal,
    inputSchemaRef: 'schema.governance-policy.decision.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-decision', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyProposalApproved', traceRequired: true }
  },
  activateProposal: {
    actionId: 'action-govpol-activate-policy-change',
    label: 'Activate approved change',
    intent: 'command',
    capabilityId: governancePolicyCapabilities.activatePolicyChange,
    inputSchemaRef: 'schema.governance-policy.activation.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-decision', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyChangeActivated', traceRequired: true }
  },
  rollbackProposal: {
    actionId: 'action-govpol-rollback-policy-change',
    label: 'Roll back change',
    intent: 'command',
    capabilityId: governancePolicyCapabilities.rollbackPolicyChange,
    inputSchemaRef: 'schema.governance-policy.rollback.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-decision', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyChangeRolledBack', traceRequired: true }
  },
  startImpactAnalysis: {
    actionId: 'action-govpol-start-impact-analysis',
    label: 'Start policy impact analysis',
    intent: 'workflow',
    capabilityId: governancePolicyCapabilities.startImpactAnalysis,
    inputSchemaRef: 'schema.governance-policy.impact-analysis.start.v1',
    requiresConfirmation: true,
    disabled: { reasonCode: 'blocked_provider_or_runtime', message: 'Durable AutonomousAgent policy-impact analysis is unavailable in this starter slice; the UI must not fake task progress.' },
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-analysis-task', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactAnalysisStartBlocked', traceRequired: true }
  },
  openTrace: {
    actionId: 'action-govpol-open-trace',
    label: 'Open governance trace',
    intent: 'trace',
    capabilityId: governancePolicyCapabilities.openTrace,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-decision-trace', openPlacement: 'deep-link' },
    audit: { eventType: 'GovernancePolicyTraceOpened', traceRequired: true }
  }
} satisfies Record<string, SurfaceAction>;

export const governancePolicyDashboardSurface = envelope(
  'surface-governance-policy-dashboard',
  'dashboard',
  'Governance/Policy dashboard',
  'agent-governance-policy',
  {
    cards: [
      { cardId: 'card-pending-proposals', label: 'Pending proposals', value: 1, severity: 'warning' },
      { cardId: 'card-active-policies', label: 'Active policy concepts', value: 4, severity: 'info' },
      { cardId: 'card-blocked-analysis', label: 'Analysis tasks', value: 'blocked until runtime enabled', severity: 'blocked_provider_or_runtime' }
    ],
    readiness: 'Governance reads, proposal review, simulations, decisions, activation, rollback, and trace links are rendered from backend-scoped capability surfaces.',
    capabilityIds: Object.values(governancePolicyCapabilities),
    sections: [
      { sectionId: 'posture', label: 'Governance posture', summary: 'Selected AuthContext has read and proposal authority; backend remains authoritative for approval, activation, and rollback.' },
      { sectionId: 'attention', label: 'Attention items', summary: 'One policy-boundary proposal requires human approval after deterministic simulation evidence is reviewed.' },
      { sectionId: 'blocked-runtime', label: 'Blocked runtime', summary: 'AutonomousAgent impact analysis is visibly blocked until a real durable task path exists; no simulated progress is shown.' }
    ],
    nextSteps: [
      { workstreamId: 'agent-governance-policy', label: 'Review policy inventory', allowed: true, capabilityIds: [governancePolicyCapabilities.readDashboard], traceId: 'trace-govpol-inventory' },
      { workstreamId: 'agent-governance-policy', label: 'Simulate proposal impact', allowed: true, capabilityIds: [governancePolicyCapabilities.simulateProposal], traceId: 'trace-govpol-simulation' },
      { workstreamId: 'agent-governance-policy-analysis', label: 'Start impact analysis', allowed: false, blockedReason: 'Durable AutonomousAgent runtime is not enabled; backend must fail closed.', capabilityIds: [governancePolicyCapabilities.startImpactAnalysis], traceId: 'trace-govpol-analysis-blocked' }
    ],
    blockedState: { reasonCode: 'FRONTEND_NOT_AUTHORITY', message: 'Launcher visibility and action buttons are convenience signals only.', recovery: 'Backend capability checks decide every protected action and return safe denial surfaces when authority is missing.' }
  },
  [governancePolicySurfaceActions.showInventory, governancePolicySurfaceActions.draftProposal, governancePolicySurfaceActions.simulateProposal, governancePolicySurfaceActions.startImpactAnalysis, governancePolicySurfaceActions.openTrace]
);

export const governancePolicyInventorySurface = envelope(
  'surface-governance-policy-inventory',
  'list-search',
  'Policy inventory',
  'agent-governance-policy',
  {
    query: { status: 'active OR pending', type: 'approval-gate OR tool-boundary OR capability' },
    rows: [
      { policyId: 'policy-approval-threshold', name: 'Approval threshold', type: 'approval-gate', status: 'active', affectedCapabilityIds: 'governance.policy.approve,governance.policy.activate', sourceArtifact: 'GovernancePolicyService', lastChangeTraceId: 'trace-govpol-active-threshold' },
      { policyId: 'policy-tool-boundary', name: 'Side-effecting tool boundary', type: 'tool-boundary', status: 'active', affectedCapabilityIds: 'agent_admin.simulate_tool_boundary,governance.policy.simulate', sourceArtifact: 'ToolPermissionBoundary', lastChangeTraceId: 'trace-govpol-tool-boundary' },
      { policyId: 'proposal-govpol-001', name: 'Lower approval threshold proposal', type: 'proposal', status: 'in_review', affectedCapabilityIds: 'governance.policy.approve', sourceArtifact: 'PolicyProposalEntity', lastChangeTraceId: 'trace-govpol-proposal-submitted' }
    ],
    pageInfo: { totalKnownCount: 3 },
    partial: false,
    redaction: 'Raw prompts, backend secrets, and cross-tenant evidence are omitted from browser inventory rows.'
  },
  [governancePolicySurfaceActions.showDashboard, governancePolicySurfaceActions.openTrace]
);

export const governancePolicyProposalSurface = envelope(
  'surface-governance-policy-proposal',
  'governance-diff',
  'Policy proposal review',
  'agent-governance-policy',
  {
    proposalId: 'proposal-govpol-001',
    lifecycleState: 'in_review',
    source: 'Governance/Policy Agent drafted text; human submitted for review',
    riskClassification: 'high',
    requiredApproval: 'Tenant Admin with governance.policy.approve',
    simulationSummary: 'Simulation completed with one expected additional human-approval denial.',
    activationStatus: 'not active until separately approved and activated',
    beforeSummary: 'Manual approval is required only when risk score is above 75.',
    afterSummary: 'Manual approval is required when risk score is above 65 and side-effecting tools are requested.',
    changes: [
      { path: 'approval.riskThreshold', before: '75', after: '65', impact: 'More authority-changing proposals pause for human review.' },
      { path: 'toolBoundary.sideEffects', before: 'approval for external email only', after: 'approval for external email and external ticket creation', impact: 'Expands governed side-effect coverage before activation.' }
    ],
    traceLinks: ['trace-govpol-proposal-created', 'trace-govpol-proposal-submitted']
  },
  [governancePolicySurfaceActions.simulateProposal, governancePolicySurfaceActions.decideProposal, governancePolicySurfaceActions.openTrace]
);

export const governancePolicySimulationSurface = envelope(
  'surface-governance-policy-simulation',
  'governance-diff',
  'Policy simulation results',
  'agent-governance-policy',
  {
    proposalId: 'proposal-govpol-001',
    lifecycleState: 'in_review',
    riskClassification: 'high',
    requiredApproval: 'Tenant Admin approval still required; simulation is advisory only.',
    simulationSummary: 'Deterministic simulation predicts additional denials for two authority-changing actions and no automatic activation.',
    activationStatus: 'blocked until approved',
    beforeSummary: 'Existing policy allows the sample side-effecting ticket tool after one approval gate.',
    afterSummary: 'Proposed policy denies the same action until both risk and tool-boundary gates pass.',
    changes: [
      { path: 'sample.GOVPOL-ACTIVATE-POLICY-CHANGE', before: 'allowed after approval', after: 'denied until simulation evidence is attached', impact: 'Activation path becomes stricter.' },
      { path: 'sample.GOVPOL-ROLLBACK-POLICY-CHANGE', before: 'manual rollback allowed', after: 'manual rollback allowed with rollback reference', impact: 'Rollback remains human-controlled and trace-linked.' }
    ],
    simulation: {
      affectedCapabilities: ['GOVPOL-SIMULATE-PROPOSAL', 'GOVPOL-DECIDE-PROPOSAL', 'GOVPOL-ACTIVATE-POLICY-CHANGE', 'GOVPOL-ROLLBACK-POLICY-CHANGE'],
      expectedAllows: ['governance.policy.read', 'governance.policy.simulate'],
      expectedDenials: ['governance.policy.activate without approved proposal', 'governance.policy.analysis.start while runtime unavailable'],
      warnings: ['Simulation is deterministic/advisory and never grants authority.', 'Unsupported evidence scopes are omitted with trace links.'],
      confidence: 'bounded fixture confidence based on scoped policy rows',
      evidenceTraceIds: ['trace-govpol-simulation', 'trace-govpol-analysis-blocked']
    },
    traceLinks: ['trace-govpol-simulation']
  },
  [governancePolicySurfaceActions.decideProposal, governancePolicySurfaceActions.activateProposal, governancePolicySurfaceActions.openTrace]
);

export const governancePolicyDecisionSurface = envelope(
  'surface-governance-policy-decision',
  'decision',
  'Governance decision',
  'agent-governance-policy',
  {
    decisionId: 'decision-govpol-001',
    recommendation: 'Approve the stricter threshold only after reviewing simulation evidence; keep activation as a separate human action.',
    riskScore: 82,
    confidenceScore: 76,
    evidence: [
      { evidenceId: 'evidence-simulation', label: 'Simulation trace', summary: 'Deterministic simulation showed expected denials for unapproved activation.' },
      { evidenceId: 'evidence-authority', label: 'Authority basis', summary: 'Actor needs governance.policy.approve; frontend button visibility is not authority.' },
      { evidenceId: 'evidence-redaction', label: 'Redaction', summary: 'Prompt text, backend secrets, and cross-tenant evidence remain omitted.' }
    ],
    allowedActions: [
      { actionId: 'action-govpol-decide-proposal', label: 'Approve proposal', capabilityId: governancePolicyCapabilities.approveProposal },
      { actionId: 'action-govpol-open-trace', label: 'Open decision trace', capabilityId: governancePolicyCapabilities.openTrace }
    ],
    disabledActions: [
      { actionId: 'action-govpol-activate-policy-change', reason: 'Activation stays blocked until backend confirms approved version and idempotency key.' },
      { actionId: 'action-govpol-start-impact-analysis', reason: 'AutonomousAgent analysis runtime is not enabled; no fake progress is rendered.' }
    ],
    risk: 'Authority-changing approval',
    traceLinks: ['trace-govpol-decision', 'trace-govpol-approval-basis']
  },
  [governancePolicySurfaceActions.decideProposal, governancePolicySurfaceActions.activateProposal, governancePolicySurfaceActions.rollbackProposal, governancePolicySurfaceActions.openTrace]
);

export const governancePolicyAnalysisTaskSurface = envelope(
  'surface-governance-policy-analysis-task',
  'workflow-status',
  'Policy impact analysis task',
  'agent-governance-policy',
  {
    workflowId: 'govpol-analysis-task-blocked',
    taskKind: 'autonomous-agent-analysis',
    status: 'blocked_provider_or_runtime',
    summary: 'Policy-impact analysis is a durable AutonomousAgent follow-up only; this fixture fails closed until the backend task lifecycle exists.',
    requiredCapabilityId: governancePolicyCapabilities.startImpactAnalysis,
    traceIds: ['trace-govpol-analysis-blocked'],
    progress: [{ snapshotId: 'blocked-start', label: 'Start denied before task creation', status: 'blocked_provider_or_runtime', traceId: 'trace-govpol-analysis-blocked' }],
    resultSummary: 'No model-less or deterministic fake analysis result is produced.'
  },
  [governancePolicySurfaceActions.openTrace]
);

export const governancePolicyDecisionTraceSurface = envelope(
  'surface-governance-policy-decision-trace',
  'audit-timeline',
  'Governance decision traces',
  'agent-governance-policy',
  {
    events: [
      { eventId: 'govpol-proposal-created', occurredAt: generatedAt, actor: 'Tenant Admin', action: 'Draft proposal created with idempotency key and redacted diff summary', traceId: 'trace-govpol-proposal-created' },
      { eventId: 'govpol-simulation', occurredAt: generatedAt, actor: 'GovernancePolicyService', action: 'Deterministic simulation recorded expected allows, denials, warnings, and evidence refs', traceId: 'trace-govpol-simulation' },
      { eventId: 'govpol-analysis-blocked', occurredAt: generatedAt, actor: 'Governance/Policy Agent', action: 'AutonomousAgent impact analysis failed closed because durable task runtime is unavailable', traceId: 'trace-govpol-analysis-blocked' }
    ]
  },
  [governancePolicySurfaceActions.showDashboard]
);

export const governancePolicyStructuredSurfaces = [
  governancePolicyDashboardSurface,
  governancePolicyInventorySurface,
  governancePolicyProposalSurface,
  governancePolicySimulationSurface,
  governancePolicyDecisionSurface,
  governancePolicyAnalysisTaskSurface,
  governancePolicyDecisionTraceSurface
];

export const displayGovernancePolicyDashboardActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Displayed Governance/Policy dashboard with backend-visible capabilities, pending proposals, blocked runtime state, and trace links.',
  correlationId: 'corr-display-governance-policy-dashboard',
  traceIds: ['trace-display-governance-policy-dashboard'],
  resultSurface: governancePolicyDashboardSurface
};

export const displayGovernancePolicyInventoryActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Displayed browser-safe policy inventory. Backend scoping, redaction, and trace checks remain authoritative.',
  correlationId: 'corr-display-governance-policy-inventory',
  traceIds: ['trace-display-governance-policy-inventory'],
  resultSurface: governancePolicyInventorySurface
};

export const displayGovernancePolicySimulationActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Displayed deterministic simulation evidence. Simulation is advisory and grants no authority.',
  correlationId: 'corr-display-governance-policy-simulation',
  traceIds: ['trace-govpol-simulation'],
  resultSurface: governancePolicySimulationSurface
};

export const dashboardSurface = envelope('surface-dashboard', 'dashboard', 'Tenant attention dashboard', 'agent-my-account', { cards: [{ cardId: 'card-open-decisions', label: 'Open decisions', value: 2, severity: 'warning' }], scopeNote: 'Full-core/demo surface; the default five core v0 starter acceptance target is markdown_response.' }, [surfaceActionsByIntent.read]);
export const listSearchSurface = userAdminListSearchSurface;
export const detailEditSurface = userAdminDetailEditSurface;
export const decisionSurface = envelope('surface-decision-card', 'decision', 'Approve bounded outreach plan', 'agent-governance-policy', { decisionId: 'decision-1', recommendation: 'Approve after evidence review.', riskScore: 72, confidenceScore: 84, evidence: [{ evidenceId: 'evidence-1', label: 'Trace summary', summary: 'Agent stayed within tool boundary.' }] }, [surfaceActionsByIntent.approval, surfaceActionsByIntent.trace]);
export const auditTimelineSurface = envelope('surface-audit-timeline', 'audit-timeline', 'Admin audit timeline', 'agent-audit-trace', { events: [{ eventId: 'audit-1', occurredAt: generatedAt, actor: 'Tenant Admin', action: 'invited user', traceId: 'trace-invite' }] }, [surfaceActionsByIntent.trace]);
export const workflowStatusSurface = envelope('surface-workflow-status', 'workflow-status', 'Invitation workflow status', 'agent-user-admin', { workflowId: 'workflow-invite-1', status: 'waiting-for-human', summary: 'Fixture fallback for non-authoritative workflows. User Admin invitation actions use /api/workstream/actions and backend-aligned action ids.', traceIds: ['trace-useradmin-invitation-workflow'], requiredCapabilityId: userAdminCapabilities.sendInvitation, steps: [{ stepId: 'send-email', label: 'Send invitation email', status: 'waiting-for-human' }] }, [surfaceActionsByIntent.workflow]);
export const userAdminInvitationActionStatusSurface = envelope('surface-user-admin-invitation-action-status', 'workflow-status', 'User Admin invitation action status', 'agent-user-admin', { workflowId: 'user-admin-invitation-action', status: 'completed', summary: 'Invitation create/resend/revoke action feedback is rendered from backend-authoritative /api/workstream/actions results with idempotency, audit, trace, and outbox status references.', traceIds: ['trace-useradmin-invitation-action', 'trace-useradmin'], requiredCapabilityId: userAdminCapabilities.sendInvitation, steps: [{ stepId: 'authorize-selected-auth-context', label: 'Backend selected AuthContext and USERADMIN_* capability authorized', status: 'completed' }, { stepId: 'enqueue-outbox', label: 'Invitation outbox/provider result surfaced without fixture-only success substitution', status: 'completed' }, { stepId: 'system-message-denials', label: 'system_message denials preserve correlation id for forbidden, stale, validation, no-op, or blocked_provider_or_runtime states', status: 'completed' }] }, [userAdminSurfaceActions.createInvitation, userAdminSurfaceActions.resendInvitation, userAdminSurfaceActions.revokeInvitation, userAdminSurfaceActions.openAdminAudit]);
export const userAdminMemberStatusActionSurface = envelope('surface-user-admin-member-status-action', 'workflow-status', 'User Admin member status action', 'agent-user-admin', { workflowId: 'user-admin-member-status-action', status: 'completed', summary: 'Disable/reactivate feedback is rendered from backend-authoritative USERADMIN_UPDATE_MEMBER_STATUS results with last-admin, self-disable, disabled-user, idempotency, no-op, audit, and system_message evidence.', traceIds: ['trace-useradmin-status-action', 'trace-useradmin'], requiredCapabilityId: userAdminCapabilities.updateMemberStatus, steps: [{ stepId: 'authorize-selected-auth-context', label: 'Backend selected AuthContext, tenant/customer scope, active actor, and USERADMIN_UPDATE_MEMBER_STATUS checked', status: 'completed' }, { stepId: 'validate-guardrails', label: 'last-admin and self-disable guardrails decide allow, denial, validation-error, or no-op idempotency result', status: 'completed' }, { stepId: 'emit-trace', label: 'Browser-safe trace links and system_message denial/no-op text returned with the action result', status: 'completed' }] }, [userAdminSurfaceActions.suspendMembership, userAdminSurfaceActions.reactivateMembership, userAdminSurfaceActions.openAdminAudit]);
export const userAdminRoleChangeActionSurface = envelope('surface-user-admin-role-change-action', 'workflow-status', 'User Admin role change action', 'agent-user-admin', { workflowId: 'user-admin-role-change-action', status: 'waiting-for-human', summary: 'Role preview/commit feedback preserves user_admin.role_change_preview.v1, capability delta, affected workstreams, approval policy, idempotency, no-op, and trace links without letting frontend state grant authority.', traceIds: ['trace-useradmin-role-preview', 'trace-useradmin'], requiredCapabilityId: userAdminCapabilities.changeMemberRoles, steps: [{ stepId: 'preview-role-change', label: 'USERADMIN_PREVIEW_ROLE_CHANGE returned user_admin.role_change_preview.v1 capability delta and affected workstreams', status: 'completed' }, { stepId: 'approval-policy', label: 'Apply role change remains approval-gated and backend-authoritative', status: 'waiting-for-human' }, { stepId: 'idempotency-trace', label: 'Commit uses client-generated idempotency and trace-useradmin links for no-op/replay evidence', status: 'completed' }] }, [userAdminSurfaceActions.previewRoleChange, userAdminSurfaceActions.changeMemberRoles, userAdminSurfaceActions.openAdminAudit]);
export const userAdminAgentBlockedSystemMessageSurface = envelope('surface-user-admin-agent-provider-blocked', 'system_message', 'UserAdminAgent unavailable', 'agent-user-admin', { status: 'blocked_provider_or_runtime', severity: 'warning', title: 'UserAdminAgent unavailable', summary: 'Model-backed UserAdminAgent guidance was blocked before a response was produced.', message: 'Model-backed UserAdminAgent guidance was blocked before a response was produced. Backend provider/runtime configuration must be restored before guidance can run.', recoverySteps: ['Verify model provider configuration and active ModelConfigRef on the backend.', 'Review PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, and userAdminEvidence.read tool traces for this correlation id.', 'Retry after backend configuration is restored; use deterministic User Admin surfaces for invite, member status, and role changes.'], workstreamEntryId: 'item-user-admin-agent-provider-blocked', producingAgentId: 'agent-user-admin', capabilityId: userAdminCapabilities.agentTurn, sourceRefs: [{ refType: 'trace', refId: 'trace-useradmin-agent-provider-blocked', label: 'Blocked UserAdminAgent runtime trace' }, { refType: 'capability', refId: userAdminCapabilities.overview, label: 'Scoped User Admin overview capability' }], safety: { sanitized: true, redactionNote: 'Provider secrets, raw JWTs, hidden prompts, invitation tokens, and unauthorized tenant/customer evidence are omitted.' }, trace: { correlationId: 'corr-useradmin-agent-provider-blocked', traceIds: ['trace-useradmin-agent-provider-blocked', 'trace-useradmin-agent-work-blocked'] } }, [userAdminSurfaceActions.openAdminAudit]);
export const governanceDiffSurface = envelope('surface-governance-diff', 'governance-diff', 'Policy proposal diff', 'agent-governance-policy', { proposalId: 'proposal-1', beforeSummary: 'Manual approval over 75 risk.', afterSummary: 'Manual approval over 65 risk.', changes: [{ path: 'risk.approvalThreshold', before: '75', after: '65', impact: 'More decisions require human review.' }] }, [surfaceActionsByIntent.proposal, surfaceActionsByIntent.governance]);
export const outcomeSurface = envelope('surface-outcome-review', 'outcome', 'Outcome review', 'agent-governance-policy', { outcomeId: 'outcome-1', metrics: [{ metricId: 'decision-cycle-time', label: 'Decision cycle time', current: 4, target: 2, unit: 'hours' }] }, [surfaceActionsByIntent.read]);

export const fullCoreDemoSurfaceEnvelopes = [
  ...myAccountStructuredSurfaces,
  userAdminDashboardSurface,
  userAdminListSearchSurface,
  userAdminRoleChangePreviewSurface,
  userAdminRoleCapabilityMatrixSurface,
  userAdminAccessReviewSurface,
  userAdminInvitationActionStatusSurface,
  userAdminMemberStatusActionSurface,
  userAdminRoleChangeActionSurface,
  userAdminAgentBlockedSystemMessageSurface,
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
  ...governancePolicyStructuredSurfaces,
  dashboardSurface,
  detailEditSurface,
  decisionSurface,
  auditTimelineSurface,
  workflowStatusSurface,
  governanceDiffSurface,
  outcomeSurface
];

export const canonicalSurfaceEnvelopes = [
  ...fiveCoreV0MarkdownSurfaces,
  ...governancePolicyStructuredSurfaces
];
export const allSurfaceActions: SurfaceAction[] = [...Object.values(surfaceActionsByIntent), ...Object.values(myAccountSurfaceActions), ...Object.values(userAdminSurfaceActions), ...Object.values(agentAdminSurfaceActions), ...Object.values(auditTraceSurfaceActions), ...Object.values(governancePolicySurfaceActions)];

const resultBase = { correlationId: 'corr-action-result', traceIds: ['trace-action-result'] };
export const actionResultsByStatus: Record<CapabilityActionResult['status'], CapabilityActionResult> = {
  accepted: { status: 'accepted', message: 'Action accepted.', ...resultBase, resultSurface: workflowStatusSurface },
  denied: { status: 'denied', message: 'You do not have the required capability.', ...resultBase },
  'validation-error': { status: 'validation-error', message: 'Correct the highlighted fields.', ...resultBase },
  'approval-required': { status: 'approval-required', message: 'Human approval is required.', ...resultBase, resultSurface: decisionSurface },
  conflict: { status: 'conflict', message: 'The surface changed. Refresh and try again.', ...resultBase },
  'no-op': { status: 'no-op', message: 'No change was needed.', ...resultBase, resultSurface: userAdminInvitationActionStatusSurface },
  failed: { status: 'failed', message: 'Action failed safely.', ...resultBase },
  'blocked-runtime': { status: 'blocked-runtime', message: 'Runtime/provider prerequisite blocked safely.', ...resultBase, resultSurface: userAdminAccessReviewSurface },
  'blocked_provider_or_runtime': { status: 'blocked_provider_or_runtime', message: 'Provider or runtime prerequisite blocked safely.', ...resultBase, resultSurface: userAdminAccessReviewSurface }
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

import type { CapabilityActionResult, SurfaceAction, SurfaceEnvelope } from '../../../workstream/types';
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
  startAccessReviewTask: 'user_admin.access_review.start',
  viewAccessReviewTask: 'user_admin.access_review.read',
  cancelAccessReviewTask: 'user_admin.access_review.cancel',
  acceptAccessReviewResult: 'user_admin.access_review.accept_result',
  rejectAccessReviewResult: 'user_admin.access_review.reject_result',
  viewTraceReference: 'USERADMIN_VIEW_TRACE_REFERENCE'
} as const;

export const surfaceActionsByIntent: Record<SurfaceAction['intent'], SurfaceAction> = {
  read: {
    actionId: 'action-refresh-users',
    label: 'Refresh users',
    intent: 'read',
    capabilityId: secureTenantUserFoundation,
    governedToolId: secureTenantUserFoundation,
    browserToolId: 'action-refresh-users',
    idempotency: { required: false },
    audit: { eventType: 'AdminUsersRead', traceRequired: true }
  },
  'surface-request': {
    actionId: 'action-show-user-list',
    label: 'Show users list',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.listMembers,
    governedToolId: userAdminCapabilities.listMembers,
    browserToolId: 'action-show-user-list',
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
    governedToolId: userAdminCapabilities.sendInvitation,
    browserToolId: 'action-invite-user',
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
    governedToolId: 'governance.policy.propose',
    browserToolId: 'action-propose-policy',
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
    governedToolId: 'governance-decisions-audit',
    browserToolId: 'action-approve-decision',
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
    governedToolId: userAdminCapabilities.sendInvitation,
    browserToolId: 'action-resume-workflow',
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
    governedToolId: 'governance.policy.simulate',
    browserToolId: 'action-simulate-policy',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-governance-diff', openPlacement: 'inline' },
    audit: { eventType: 'PolicySimulationRequested', traceRequired: true }
  },
  trace: {
    actionId: 'action-open-trace',
    label: 'Open trace',
    intent: 'trace',
    capabilityId: 'audit.trace.read',
    governedToolId: 'audit.trace.read',
    browserToolId: 'action-open-trace',
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
    governedToolId: userAdminCapabilities.overview,
    browserToolId: 'action-refresh-user-admin-dashboard',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-dashboard', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminDashboardRead', traceRequired: true }
  },
  displayUserList: {
    actionId: 'action-display-user-list',
    label: 'Display user list view',
    intent: 'read',
    capabilityId: userAdminCapabilities.listMembers,
    governedToolId: userAdminCapabilities.listMembers,
    browserToolId: 'action-display-user-list',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-list', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminListDisplayed', traceRequired: true }
  },
  searchUsers: {
    actionId: 'action-search-users',
    label: 'Search users and invitations',
    intent: 'read',
    capabilityId: userAdminCapabilities.listMembers,
    governedToolId: userAdminCapabilities.listMembers,
    browserToolId: 'action-search-users',
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
    governedToolId: userAdminCapabilities.listMembers,
    browserToolId: 'action-display-user-detail',
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
    governedToolId: userAdminCapabilities.listRolesCapabilities,
    browserToolId: 'action-display-role-capability-matrix',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'user-admin-role-capability-matrix', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminRoleCapabilityMatrixDisplayed', traceRequired: true }
  },
  previewRoleChange: {
    actionId: 'action-useradmin-preview-role-change',
    label: 'Preview role change',
    intent: 'proposal',
    capabilityId: userAdminCapabilities.previewRoleChange,
    governedToolId: userAdminCapabilities.previewRoleChange,
    browserToolId: 'action-useradmin-preview-role-change',
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
    governedToolId: userAdminCapabilities.changeMemberRoles,
    browserToolId: 'action-useradmin-change-member-roles',
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
    governedToolId: userAdminCapabilities.sendInvitation,
    browserToolId: 'action-invite-user',
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
    governedToolId: userAdminCapabilities.resendInvitation,
    browserToolId: 'action-useradmin-resend-invitation',
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
    governedToolId: userAdminCapabilities.revokeInvitation,
    browserToolId: 'action-useradmin-revoke-invitation',
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
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-add-membership',
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
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-useradmin-disable-member',
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
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-useradmin-reactivate-member',
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
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-remove-membership',
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
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-update-user-profile',
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
    governedToolId: userAdminCapabilities.changeMemberRoles,
    browserToolId: 'action-replace-membership-role',
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
    governedToolId: userAdminCapabilities.changeMemberRoles,
    browserToolId: 'action-remove-membership-role',
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
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-disable-account',
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
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-reactivate-account',
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
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-request-identity-relink',
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
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-complete-identity-relink',
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
    governedToolId: userAdminCapabilities.viewTraceReference,
    browserToolId: 'action-read-support-access',
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
    governedToolId: userAdminCapabilities.viewTraceReference,
    browserToolId: 'action-grant-support-access',
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
    governedToolId: userAdminCapabilities.viewTraceReference,
    browserToolId: 'action-revoke-support-access',
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
    governedToolId: userAdminCapabilities.viewTraceReference,
    browserToolId: 'action-extend-support-access',
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
    governedToolId: userAdminCapabilities.startAccessReviewTask,
    browserToolId: 'action-useradmin-start-access-review',
    inputSchemaRef: 'schema.user-admin.access-review.start.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-access-review', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminAccessReviewStarted', traceRequired: true }
  },
  readAccessReview: {
    actionId: 'action-useradmin-read-access-review',
    label: 'Read access review item',
    intent: 'read',
    capabilityId: userAdminCapabilities.viewAccessReviewTask,
    governedToolId: userAdminCapabilities.viewAccessReviewTask,
    browserToolId: 'action-useradmin-read-access-review',
    inputSchemaRef: 'schema.user-admin.access-review.read.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-access-review', openPlacement: 'inline' },
    audit: { eventType: 'AccessReviewRead', traceRequired: true }
  },
  cancelAccessReview: {
    actionId: 'action-useradmin-cancel-access-review',
    label: 'Cancel access review',
    intent: 'command',
    capabilityId: userAdminCapabilities.cancelAccessReviewTask,
    governedToolId: userAdminCapabilities.cancelAccessReviewTask,
    browserToolId: 'action-useradmin-cancel-access-review',
    inputSchemaRef: 'schema.user-admin.access-review.cancel.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-access-review', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminAccessReviewCancelled', traceRequired: true }
  },
  acceptAccessReviewResult: {
    actionId: 'action-useradmin-accept-access-review-result',
    label: 'Accept access review result',
    intent: 'approval',
    capabilityId: userAdminCapabilities.acceptAccessReviewResult,
    governedToolId: userAdminCapabilities.acceptAccessReviewResult,
    browserToolId: 'action-useradmin-accept-access-review-result',
    inputSchemaRef: 'schema.user-admin.access-review.accept-result.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-access-review', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminAccessReviewResultAccepted', traceRequired: true }
  },
  rejectAccessReviewResult: {
    actionId: 'action-useradmin-reject-access-review-result',
    label: 'Reject access review result',
    intent: 'approval',
    capabilityId: userAdminCapabilities.rejectAccessReviewResult,
    governedToolId: userAdminCapabilities.rejectAccessReviewResult,
    browserToolId: 'action-useradmin-reject-access-review-result',
    inputSchemaRef: 'schema.user-admin.access-review.reject-result.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-access-review', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminAccessReviewResultRejected', traceRequired: true }
  },
  openAdminAudit: {
    actionId: 'action-open-admin-audit',
    label: 'Open admin audit evidence',
    intent: 'trace',
    capabilityId: userAdminCapabilities.viewTraceReference,
    governedToolId: userAdminCapabilities.viewTraceReference,
    browserToolId: 'action-open-admin-audit',
    idempotency: { required: false },
    resultSurface: { appendSurfaceType: 'audit-timeline', openPlacement: 'deep-link' },
    audit: { eventType: 'AdminAuditOpened', traceRequired: true }
  }
} satisfies Record<string, SurfaceAction>;

const agentDefinitionsCapability = 'agent_admin.list_definitions';
const agentDefinitionReadCapability = 'agent_admin.get_definition';
const agentPromptReadCapability = 'agent_admin.get_prompt_version';
const agentSkillReadCapability = 'agent_admin.get_skill_version';
const agentReferenceReadCapability = 'agent_admin.get_reference_version';
const agentManifestReadCapability = 'agent_admin.get_manifest';
const agentPromptsCapability = 'agent_admin.draft_behavior_change';
const agentSubmitReviewCapability = 'agent_admin.submit_behavior_change_for_review';
const agentSkillsCapability = 'agent_admin.approve_behavior_change';
const agentRejectCapability = 'agent_admin.reject_behavior_change';
const agentActivateCapability = 'agent_admin.activate_behavior_change';
const agentCancelCapability = 'agent_admin.cancel_behavior_change';
const agentRollbackCapability = 'agent_admin.rollback_behavior_change';
const agentToolBoundariesCapability = 'agent_admin.simulate_tool_boundary';
const agentModelsReadCapability = 'agent_admin.get_model_ref';
const agentModelsManageCapability = 'agent_admin.activate_behavior_change';
const agentSeedReadCapability = 'agent_admin.list_seed_material';
const agentRuntimeTestCapability = 'agent_admin.draft_behavior_change';
const agentPromptRiskStartCapability = 'agent_admin.prompt_risk_review.start';
const agentPromptRiskReadCapability = 'agent_admin.prompt_risk_review.read';
const agentPromptRiskCancelCapability = 'agent_admin.prompt_risk_review.cancel';
const agentPromptRiskAcceptCapability = 'agent_admin.prompt_risk_review.accept_result';
const agentPromptRiskRejectCapability = 'agent_admin.prompt_risk_review.reject_result';

export const agentAdminSurfaceActions = {
  displayCatalog: {
    actionId: 'action-display-agent-catalog',
    label: 'Display agent catalog',
    intent: 'read',
    capabilityId: agentDefinitionsCapability,
    governedToolId: agentDefinitionsCapability,
    browserToolId: 'action-display-agent-catalog',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-catalog', openPlacement: 'inline' },
    audit: { eventType: 'AgentCatalogDisplayed', traceRequired: true }
  },
  openAgentDetail: {
    actionId: 'action-open-agent-detail',
    label: 'Open agent readiness detail',
    intent: 'read',
    capabilityId: agentDefinitionReadCapability,
    governedToolId: agentDefinitionReadCapability,
    browserToolId: 'action-open-agent-detail',
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
    governedToolId: agentPromptsCapability,
    browserToolId: 'action-propose-prompt-diff',
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
    governedToolId: agentRuntimeTestCapability,
    browserToolId: 'action-test-agent-prompt',
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
    governedToolId: agentSkillsCapability,
    browserToolId: 'action-approve-skill-manifest',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-agent-skill-manifest-diff', openPlacement: 'inline' },
    audit: { eventType: 'AgentSkillManifestApproved', traceRequired: true }
  },
  submitBehaviorChange: {
    actionId: 'action-submit-behavior-change',
    label: 'Submit behavior change for review',
    intent: 'proposal',
    capabilityId: agentSubmitReviewCapability,
    governedToolId: agentSubmitReviewCapability,
    browserToolId: 'action-submit-behavior-change',
    inputSchemaRef: 'schema.agent-admin.behavior-change.submit.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentBehaviorChangeSubmitted', traceRequired: true }
  },
  rejectBehaviorChange: {
    actionId: 'action-reject-behavior-change',
    label: 'Reject behavior change',
    intent: 'approval',
    capabilityId: agentRejectCapability,
    governedToolId: agentRejectCapability,
    browserToolId: 'action-reject-behavior-change',
    inputSchemaRef: 'schema.agent-admin.behavior-change.reject.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentBehaviorChangeRejected', traceRequired: true }
  },
  activateBehaviorChange: {
    actionId: 'action-activate-behavior-change',
    label: 'Activate approved behavior change',
    intent: 'command',
    capabilityId: agentActivateCapability,
    governedToolId: agentActivateCapability,
    browserToolId: 'action-activate-behavior-change',
    inputSchemaRef: 'schema.agent-admin.behavior-change.activate.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentBehaviorChangeActivated', traceRequired: true }
  },
  cancelBehaviorChange: {
    actionId: 'action-cancel-behavior-change',
    label: 'Cancel behavior change',
    intent: 'command',
    capabilityId: agentCancelCapability,
    governedToolId: agentCancelCapability,
    browserToolId: 'action-cancel-behavior-change',
    inputSchemaRef: 'schema.agent-admin.behavior-change.cancel.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentBehaviorChangeCancelled', traceRequired: true }
  },
  rollbackBehaviorChange: {
    actionId: 'action-rollback-behavior-change',
    label: 'Rollback activated behavior change',
    intent: 'command',
    capabilityId: agentRollbackCapability,
    governedToolId: agentRollbackCapability,
    browserToolId: 'action-rollback-behavior-change',
    inputSchemaRef: 'schema.agent-admin.behavior-change.rollback.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentBehaviorChangeRolledBack', traceRequired: true }
  },
  simulateToolBoundary: {
    actionId: 'action-simulate-tool-boundary',
    label: 'Simulate tool boundary change',
    intent: 'governance',
    capabilityId: agentToolBoundariesCapability,
    governedToolId: agentToolBoundariesCapability,
    browserToolId: 'action-simulate-tool-boundary',
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
    governedToolId: agentModelsManageCapability,
    browserToolId: 'action-manage-model-ref',
    disabled: { reasonCode: 'MODEL_POLICY_DENIED', message: 'This fixture denies switching to a disabled provider alias; provider secrets remain redacted.' },
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { appendSurfaceType: 'decision', openPlacement: 'inline' },
    audit: { eventType: 'AgentModelRefChangeDenied', traceRequired: true }
  },
  listSeedMaterial: {
    actionId: 'action-list-agent-seed-material',
    label: 'List seed material',
    intent: 'read',
    capabilityId: agentSeedReadCapability,
    governedToolId: agentSeedReadCapability,
    browserToolId: 'action-list-agent-seed-material',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-seed-material', openPlacement: 'inline' },
    audit: { eventType: 'AgentSeedMaterialListed', traceRequired: true }
  },
  startPromptRiskReview: {
    actionId: 'action-agentadmin-start-prompt-risk-review',
    label: 'Start prompt-risk review',
    intent: 'workflow',
    capabilityId: agentPromptRiskStartCapability,
    governedToolId: agentPromptRiskStartCapability,
    browserToolId: 'action-agentadmin-start-prompt-risk-review',
    inputSchemaRef: 'schema.agent-admin.prompt-risk-review.start.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-prompt-risk-review', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminPromptRiskReviewStarted', traceRequired: true }
  },
  readPromptRiskReview: {
    actionId: 'action-agentadmin-read-prompt-risk-review',
    label: 'Read prompt-risk review',
    intent: 'read',
    capabilityId: agentPromptRiskReadCapability,
    governedToolId: agentPromptRiskReadCapability,
    browserToolId: 'action-agentadmin-read-prompt-risk-review',
    inputSchemaRef: 'schema.agent-admin.prompt-risk-review.read.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-prompt-risk-review', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminPromptRiskReviewRead', traceRequired: true }
  },
  cancelPromptRiskReview: {
    actionId: 'action-agentadmin-cancel-prompt-risk-review',
    label: 'Cancel prompt-risk review',
    intent: 'command',
    capabilityId: agentPromptRiskCancelCapability,
    governedToolId: agentPromptRiskCancelCapability,
    browserToolId: 'action-agentadmin-cancel-prompt-risk-review',
    inputSchemaRef: 'schema.agent-admin.prompt-risk-review.cancel.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-prompt-risk-review', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminPromptRiskReviewCancelled', traceRequired: true }
  },
  acceptPromptRiskReviewResult: {
    actionId: 'action-agentadmin-accept-prompt-risk-review-result',
    label: 'Accept advisory prompt-risk result',
    intent: 'approval',
    capabilityId: agentPromptRiskAcceptCapability,
    governedToolId: agentPromptRiskAcceptCapability,
    browserToolId: 'action-agentadmin-accept-prompt-risk-review-result',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-prompt-risk-review', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminPromptRiskReviewAccepted', traceRequired: true }
  },
  rejectPromptRiskReviewResult: {
    actionId: 'action-agentadmin-reject-prompt-risk-review-result',
    label: 'Reject advisory prompt-risk result',
    intent: 'approval',
    capabilityId: agentPromptRiskRejectCapability,
    governedToolId: agentPromptRiskRejectCapability,
    browserToolId: 'action-agentadmin-reject-prompt-risk-review-result',
    inputSchemaRef: 'schema.agent-admin.prompt-risk-review.reject.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-prompt-risk-review', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminPromptRiskReviewRejected', traceRequired: true }
  },
  openAgentTrace: {
    actionId: 'action-open-agent-trace',
    label: 'Open agent work trace',
    intent: 'trace',
    capabilityId: 'audit.trace.read',
    governedToolId: 'audit.trace.read',
    browserToolId: 'action-open-agent-trace',
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
    governedToolId: myAccountCapabilities.viewSummary,
    browserToolId: 'action-show-my-account-dashboard',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-my-account-dashboard', openPlacement: 'inline' },
    audit: { eventType: 'MyAccountSummaryDisplayed', traceRequired: true }
  },
  showProfile: {
    actionId: 'action-show-my-profile',
    label: 'Show user profile',
    intent: 'read',
    capabilityId: myAccountCapabilities.viewSummary,
    governedToolId: myAccountCapabilities.viewSummary,
    browserToolId: 'action-show-my-profile',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-my-profile', openPlacement: 'inline' },
    audit: { eventType: 'UserProfileDisplayed', traceRequired: true }
  },
  showSettings: {
    actionId: 'action-show-my-settings',
    label: 'Show user settings',
    intent: 'read',
    capabilityId: myAccountCapabilities.viewSummary,
    governedToolId: myAccountCapabilities.viewSummary,
    browserToolId: 'action-show-my-settings',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-my-settings', openPlacement: 'inline' },
    audit: { eventType: 'UserSettingsDisplayed', traceRequired: true }
  },
  updateProfile: {
    actionId: 'action-update-my-profile',
    label: 'Save profile changes',
    intent: 'command',
    capabilityId: myAccountCapabilities.updateProfileSettings,
    governedToolId: myAccountCapabilities.updateProfileSettings,
    browserToolId: 'action-update-my-profile',
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
    governedToolId: myAccountCapabilities.updateProfileSettings,
    browserToolId: 'action-update-my-settings',
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
    governedToolId: myAccountCapabilities.openAuthorizedWorkstream,
    browserToolId: 'action-open-user-admin',
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
    governedToolId: myAccountCapabilities.openAuthorizedWorkstream,
    browserToolId: 'action-open-agent-admin',
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
    governedToolId: myAccountCapabilities.viewOwnTraceRefs,
    browserToolId: 'action-open-audit-trace',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-timeline', openPlacement: 'inline' },
    audit: { eventType: 'AuditTimelineOpened', traceRequired: true }
  },
  signOut: {
    actionId: 'action-sign-out',
    label: 'Sign out',
    intent: 'command',
    capabilityId: myAccountCapabilities.viewSummary,
    governedToolId: myAccountCapabilities.viewSummary,
    browserToolId: 'action-sign-out',
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
      { cardId: 'card-my-settings', label: 'Theme', value: 'Aurora Light', severity: 'info' },
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
      { fieldId: 'preferredThemeId', label: 'Theme', value: 'aurora-light', editable: true, inputType: 'select', options: [{ value: 'aurora-light', label: 'Aurora Light' }, { value: 'cobalt-light', label: 'Cobalt Light' }, { value: 'obsidian-dark', label: 'Obsidian Dark' }, { value: 'midnight-dark', label: 'Midnight Dark' }, { value: 'dark-night', label: 'Dark Night' }] },
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
  '## Agent Admin\n\n### Available now\n- Ask AgentAdminAgent for bounded guidance about seeded AgentDefinition records, prompts, skills, references, manifests, ToolPermissionBoundary records, model refs, seed/default material, and behavior-change proposals.\n- Successful guidance uses governed readSkill(skillId), readReferenceDoc(referenceId), and agentAdminEvidence.read tool evidence through the model-backed WorkstreamRuntimeAgent path.\n- Guidance is read-only: no direct mutation, no approval, no activation, no rollback, no reseed, and no provider/model configuration change. Open deterministic Agent Admin surfaces for lifecycle commands.\n\n### Provider/runtime blocked\nMissing provider or runtime configuration returns a typed system_message with safe recovery steps, blocked_provider_or_runtime status, and PromptAssemblyTrace/AgentWorkTrace links; it never returns provider secrets or deterministic canned success.'
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
    governedToolId: 'audit.trace.dashboard.read',
    browserToolId: 'action-audit-trace-dashboard',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-dashboard', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceDashboardRead', traceRequired: true }
  },
  search: {
    actionId: 'action-audit-trace-search',
    label: 'Search scoped traces',
    intent: 'read',
    capabilityId: 'audit.trace.search',
    governedToolId: 'audit.trace.search',
    browserToolId: 'action-audit-trace-search',
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
    governedToolId: 'audit.trace.detail.read',
    browserToolId: 'action-audit-trace-detail',
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
    governedToolId: 'audit.trace.timeline.read',
    browserToolId: 'action-audit-trace-timeline',
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
    governedToolId: 'audit.trace.failureEvidence.read',
    browserToolId: 'action-audit-trace-failure-evidence',
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
    governedToolId: 'audit.trace.investigationGuide.read',
    browserToolId: 'action-audit-trace-investigation-guide',
    inputSchemaRef: 'schema.audit-trace.investigation-guide.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-investigation-guide', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceInvestigationGuideRequested', traceRequired: true }
  },
  startSummaryTask: {
    actionId: 'action-audit-trace-summary-task-start',
    label: 'Start audit summary task',
    intent: 'workflow',
    capabilityId: 'audit.trace.summary_task.start',
    governedToolId: 'audit.trace.summaryTask.start',
    browserToolId: 'action-audit-trace-summary-task-start',
    inputSchemaRef: 'schema.audit-trace.summary-task.start.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-summary-progress', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceSummaryTaskStarted', traceRequired: true }
  },
  readSummaryTask: {
    actionId: 'action-audit-trace-summary-task-read',
    label: 'Refresh summary task',
    intent: 'read',
    capabilityId: 'audit.trace.summary_task.read',
    governedToolId: 'audit.trace.summaryTask.read',
    browserToolId: 'action-audit-trace-summary-task-read',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-summary-progress', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceSummaryTaskRead', traceRequired: true }
  },
  acceptSummaryResult: {
    actionId: 'action-audit-trace-summary-task-accept-result',
    label: 'Accept advisory summary',
    intent: 'approval',
    capabilityId: 'audit.trace.summary_task.accept_result',
    governedToolId: 'audit.trace.summaryTask.acceptResult',
    browserToolId: 'action-audit-trace-summary-task-accept-result',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-summary-review', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceSummaryResultAccepted', traceRequired: true }
  },
  rejectSummaryResult: {
    actionId: 'action-audit-trace-summary-task-reject-result',
    label: 'Reject advisory summary',
    intent: 'approval',
    capabilityId: 'audit.trace.summary_task.reject_result',
    governedToolId: 'audit.trace.summaryTask.rejectResult',
    browserToolId: 'action-audit-trace-summary-task-reject-result',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-summary-review', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceSummaryResultRejected', traceRequired: true }
  },
  openSummaryEvidence: {
    actionId: 'action-audit-trace-summary-task-open-evidence',
    label: 'Open cited evidence',
    intent: 'trace',
    capabilityId: 'audit.trace.summary_task.open_evidence',
    governedToolId: 'audit.trace.summaryTask.openEvidence',
    browserToolId: 'action-audit-trace-summary-task-open-evidence',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-detail', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceSummaryEvidenceOpened', traceRequired: true }
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
      { workstreamId: 'agent-audit-trace-summary-task', label: 'Start audit summary task', allowed: true, capabilityIds: ['audit.trace.summary_task.start'], traceId: 'trace-audit-summary-task-start' }
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
      { actionId: 'action-audit-trace-search', label: 'Refine search', browserToolId: 'action-audit-trace-search', governedToolId: 'audit.trace.search', capabilityId: 'audit.trace.search' },
      { actionId: 'action-audit-trace-timeline', label: 'Open timeline', browserToolId: 'action-audit-trace-timeline', governedToolId: 'audit.trace.timeline.read', capabilityId: 'audit.trace.timeline.read' }
    ],
    disabledActions: [
      { actionId: 'action-audit-trace-summary-task-start-scheduled', reason: 'Scheduled audit summary cadence remains future work; manual backend-governed start is wired.' }
    ],
    risk: 'low',
    traceLinks: ['corr-provider-blocked-002']
  },
  [auditTraceSurfaceActions.search, auditTraceSurfaceActions.openTimeline, auditTraceSurfaceActions.openFailureEvidence]
);

export const auditTraceSummaryProgressSurface = envelope(
  'surface-audit-trace-summary-progress',
  'detail-edit',
  'Audit/Trace summary progress',
  'agent-audit-trace',
  {
    surfaceContract: 'audit.trace.summaryProgress.v1',
    summaryTaskId: 'audit-summary-tenant-1-window-2026-05-25',
    autonomousAgentTaskId: 'akka-task-audit-summary-tenant-1-window-2026-05-25',
    status: 'blocked_provider_or_runtime',
    progressSummary: 'Provider/runtime readiness failed closed; no deterministic, fixture, fake, or model-less audit summary success is rendered.',
    blockerReason: 'Configure governed model provider, AuditTraceSummaryAutonomousAgent binding, ToolPermissionBoundary grants, readSkill/readReferenceDoc, and auditTraceSummaryEvidence.read.',
    selectedContext: { tenantId: 'tenant-1', customerId: null, capabilityId: 'audit.trace.summary_task.start' },
    window: { windowStart: '2026-05-20T00:00:00Z', windowEnd: '2026-05-25T00:00:00Z' },
    evidenceCategories: ['admin_audit', 'authorization_denial', 'provider_readiness', 'agent_work', 'workstream_event'],
    sourceRefs: ['projection:audit-summary-tenant-1-window-2026-05-25', 'autonomous_task:akka-task-audit-summary-tenant-1-window-2026-05-25'],
    traceRefs: ['trace-audit-summary-start', 'trace-audit-summary-provider-blocked'],
    redactionSummary: 'Raw JWTs, provider credentials, hidden prompts, raw tool payloads, invitation tokens, and cross-tenant evidence are omitted.',
    noDirectMutation: true,
    actions: ['action-audit-trace-summary-task-read', 'action-audit-trace-summary-task-open-evidence']
  },
  [auditTraceSurfaceActions.readSummaryTask, auditTraceSurfaceActions.openSummaryEvidence]
);

export const auditTraceSummaryReviewSurface = envelope(
  'surface-audit-trace-summary-review',
  'decision',
  'Audit/Trace summary review',
  'agent-audit-trace',
  {
    surfaceContract: 'audit.trace.summaryReview.v1',
    summaryTaskId: 'audit-summary-tenant-1-window-2026-05-25',
    autonomousAgentTaskId: 'akka-task-audit-summary-tenant-1-window-2026-05-25',
    status: 'completed_review_required',
    noDirectMutation: true,
    humanDecisionRequired: true,
    overallRisk: 'review_required',
    executiveSummary: 'Model-backed AutonomousAgent completed a redacted advisory synthesis; reviewer decides accept/reject and protected records remain unchanged.',
    findings: [
      { findingId: 'finding-provider-readiness', category: 'provider_readiness', severity: 'warning', safeSummary: 'Provider readiness gaps appeared in recent traces.', evidenceRefs: ['auditTraceSummaryEvidence.read'], traceRefs: ['trace-provider-blocked-002'], redactionApplied: true }
    ],
    providerReadinessFindings: ['finding-provider-readiness'],
    authorizationDenialFindings: ['trace-auth-denied-001'],
    agentWorkFindings: ['trace-agent-work-002'],
    omittedEvidenceSummary: 'not_found_or_redacted is used for hidden or unauthorized trace ids.',
    redactionSummary: 'No raw prompts, raw tool payloads, raw JWTs, provider credentials, invitation tokens, or cross-tenant evidence are included.',
    actions: ['action-audit-trace-summary-task-accept-result', 'action-audit-trace-summary-task-reject-result', 'action-audit-trace-summary-task-open-evidence']
  },
  [auditTraceSurfaceActions.acceptSummaryResult, auditTraceSurfaceActions.rejectSummaryResult, auditTraceSurfaceActions.openSummaryEvidence]
);

export const auditTraceStructuredSurfaces = [auditTraceDashboardSurface, auditTraceSearchSurface, auditTraceDetailSurface, auditTraceTimelineSurface, auditTraceFailureEvidenceSurface, auditTraceInvestigationGuideSurface, auditTraceSummaryProgressSurface, auditTraceSummaryReviewSurface];

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
      authorityBoundary: 'SkillDocument and ReferenceDocument text cannot grant roles, tenant scope, governed-tool access, approval rights, or backend capabilities.',
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
    userAdminSurfaceActions.acceptAccessReviewResult,
    userAdminSurfaceActions.rejectAccessReviewResult,
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
    userAdminSurfaceActions.acceptAccessReviewResult,
    userAdminSurfaceActions.rejectAccessReviewResult,
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
      { id: 'access-review', rowType: 'autonomous-task-capability', role: 'Access Review', capabilityId: 'user_admin.access_review.start', assignable: false, policy: 'blocked_provider_or_runtime until durable AutonomousAgent lifecycle is enabled', traceId: 'trace-useradmin-access-review-blocked' }
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
  'surface-user-admin-access-review',
  'workflow-status',
  'User Admin access review',
  'agent-user-admin',
  {
    surfaceContract: 'user_admin.access_review_task.v1',
    workflowId: 'access-review-task-acme-001',
    taskId: 'access-review-task-acme-001',
    status: 'blocked_provider_or_runtime',
    summary: 'Start creates a durable access-review task record, but worker execution is blocked until governed AutonomousAgent provider/runtime configuration is enabled. This is a blocked surface, not fake progress.',
    traceIds: ['trace-useradmin-access-review-blocked', 'trace-useradmin-access-review-task-001'],
    requiredCapabilityId: userAdminCapabilities.startAccessReviewTask,
    initiatingCapabilityId: userAdminCapabilities.startAccessReviewTask,
    taskKind: 'autonomous-agent-analysis',
    scope: { scopeType: 'TENANT', tenantId: authContext.tenantId, customerId: authContext.customerId },
    progress: [
      { snapshotId: 'access-review-created', label: 'Durable task accepted by deterministic User Admin lifecycle', status: 'completed', traceId: 'trace-useradmin-access-review-task-001' },
      { snapshotId: 'access-review-provider-blocked', label: 'Governed worker provider/runtime check failed closed', status: 'blocked_provider_or_runtime', traceId: 'trace-useradmin-access-review-blocked' }
    ],
    resultSummary: 'No model-backed recommendation is available while provider/runtime is blocked; no direct mutation occurred.',
    accessReview: {
      surfaceContract: 'user_admin.access_review_task.v1',
      taskId: 'access-review-task-acme-001',
      lifecycleState: 'blocked_provider_or_runtime',
      progressPercent: 25,
      blockers: [
        { code: 'blocked_provider_or_runtime', message: 'Governed AutonomousAgent provider/runtime is not configured; the starter fails closed instead of returning model-less access-review recommendations.' }
      ],
      evidenceRefs: [
        { refId: 'evidence-membership-stale-admin', label: 'Tenant admin membership age', summary: 'Scoped evidence reference only; row details remain backend redacted.', traceId: 'trace-useradmin-access-review-evidence-001' },
        { refId: 'evidence-role-capability-delta', label: 'Role/capability basis', summary: 'Read through userAdminEvidence.read with tenant/customer scope.', traceId: 'trace-useradmin-access-review-evidence-002' }
      ],
      recommendations: [
        { recommendationId: 'rec-review-admin-access', label: 'Review stale admin access', risk: 'medium', confidence: 'provider-blocked', summary: 'Recommendation content requires model-backed worker completion before human acceptance.' }
      ],
      providerFailures: ['blocked_provider_or_runtime'],
      resultReviewStates: ['pending_worker_result', 'completed_review_required', 'result_accepted', 'result_rejected', 'cancelled'],
      resultReviewState: 'pending_worker_result',
      noDirectMutation: true,
      safety: 'Access-review output cannot directly mutate memberships, invitations, roles, capabilities, authorization state, or provider configuration.',
      traceIds: ['trace-useradmin-access-review-blocked', 'trace-useradmin-access-review-task-001']
    },
    steps: [
      { stepId: 'authorize-task-start', label: 'Authorize user_admin.access_review.start in selected AuthContext', status: 'completed' },
      { stepId: 'resolve-autonomous-agent', label: 'Resolve durable AutonomousAgent runtime and provider configuration', status: 'blocked_provider_or_runtime' },
      { stepId: 'emit-trace', label: 'Emit blocked-provider/runtime trace reference for the user', status: 'completed' },
      { stepId: 'await-human-review', label: 'Accept/reject remains disabled until a model-backed result exists', status: 'waiting-for-human' }
    ]
  },
  [userAdminSurfaceActions.startAccessReview, userAdminSurfaceActions.readAccessReview, userAdminSurfaceActions.cancelAccessReview, userAdminSurfaceActions.acceptAccessReviewResult, userAdminSurfaceActions.rejectAccessReviewResult, userAdminSurfaceActions.openAdminAudit]
);

export const agentAdminCatalogSurface = envelope(
  'surface-agent-admin-catalog',
  'list-search',
  'Agent Admin catalog',
  'agent-agent-admin',
  {
    surfaceContract: 'agent_admin.catalog.v1',
    query: 'tenant:tenant-acme scoped:true',
    rows: [
      { id: 'agent-agent-admin', rowType: 'AgentDefinition', displayName: 'Agent Admin Agent', status: 'active', authorityLevel: 'APPROVAL_REQUIRED', placement: 'WORKSTREAM', functionalAreaId: 'agent-admin', modelConfigRefId: 'model-safe-default', providerStatus: 'ready', seedStatus: 'starter-v1', tracePolicy: 'PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace', traceId: 'trace-agent-admin-definition-agent-agent-admin' },
      { id: 'agent-user-admin', rowType: 'AgentDefinition', displayName: 'User Admin Agent', status: 'active', authorityLevel: 'APPROVAL_REQUIRED', placement: 'WORKSTREAM', functionalAreaId: 'user-admin', modelConfigRefId: 'model-safe-default', providerStatus: 'ready', seedStatus: 'starter-v1', tracePolicy: 'PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace', traceId: 'trace-agent-admin-definition-agent-user-admin' }
    ],
    pageInfo: { totalKnownCount: 2 },
    providerReadiness: { status: 'ready', readyAgents: 2, totalAgents: 2, secretVisibility: 'redacted' },
    seedMaterial: 'starter-v1 defaults visible through surface-agent-seed-material; tenant-customized overrides are marked without exposing raw hidden text',
    redaction: 'browserSafe=true; omittedFieldKeys=rawPromptBody, rawSkillBody, rawReferenceBody, rawProviderCredential, providerCredentialValue, rawJwt; previews are redacted',
    traceLinks: ['trace-agent-admin-catalog'],
    emptyCopy: 'Empty when no governed AgentDefinition records are seeded.',
    mobileFallback: 'table-to-card',
    systemStates: ['loading', 'empty', 'forbidden', 'validation-error', 'approval-required', 'blocked_provider_or_runtime', 'stale'],
    stateFixtures: {
      loading: 'Loading surface while backend agent_admin.list_definitions reads scoped AgentDefinition projections.',
      empty: 'Empty when no governed AgentDefinition records are seeded.',
      forbidden: 'TARGET_NOT_FOUND_OR_FORBIDDEN hides cross-tenant AgentDefinition ids and row counts.',
      validation: 'validation-error preserves correlation id and safe input summary for behavior-change actions.',
      approvalRequired: 'approval-required is shown before prompt, manifest, model, or tool-boundary activation.',
      providerBlocked: 'blocked_provider_or_runtime renders safe recovery without provider secrets.',
      traceLinked: 'PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, and protected-read traces are linked.'
    }
  },
  [agentAdminSurfaceActions.displayCatalog, agentAdminSurfaceActions.openAgentDetail, agentAdminSurfaceActions.listSeedMaterial, agentAdminSurfaceActions.openAgentTrace]
);

export const agentAdminDetailSurface = envelope(
  'surface-agent-admin-detail',
  'detail-edit',
  'Agent Admin readiness detail',
  'agent-agent-admin',
  {
    surfaceContract: 'agent_admin.definition.v1',
    recordId: 'agent-agent-admin',
    recordLabel: 'Agent Admin Agent',
    recordKind: 'AgentDefinition',
    summary: 'Backend-authoritative AgentDefinition detail; behavior changes must use deterministic proposal/review/activation commands and AgentAdminAgent guidance remains read-only.',
    fields: [
      { fieldId: 'status', label: 'Status', value: 'active', editable: false, inputType: 'text', disabledReason: 'Disabled AgentDefinitions cannot be invoked or load governed skills/references.' },
      { fieldId: 'authorityLevel', label: 'Authority tier', value: 'APPROVAL_REQUIRED', editable: false, inputType: 'text', disabledReason: 'Authority expansion requires deterministic review and human approval.' },
      { fieldId: 'promptDocumentId', label: 'Prompt', value: 'prompt-agent-admin-system@1', editable: false, inputType: 'text' },
      { fieldId: 'skillManifestId', label: 'Skill manifest', value: 'manifest-agent-admin@1', editable: false, inputType: 'text' },
      { fieldId: 'referenceManifestId', label: 'Reference manifest', value: 'reference-manifest-agent-admin@1', editable: false, inputType: 'text' },
      { fieldId: 'toolBoundaryId', label: 'Tool boundary', value: 'tool-boundary-agent-admin@1', editable: false, inputType: 'text' },
      { fieldId: 'modelConfigRef', label: 'Model ref', value: 'model-safe-default', editable: false, inputType: 'text', disabledReason: 'Provider secret values are never browser-visible; provider credential values are backend-only.' }
    ],
    relatedArtifacts: [
      { artifactKind: 'prompt', artifactId: 'prompt-agent-admin-system', status: 'active', capabilityId: agentPromptReadCapability },
      { artifactKind: 'skill_manifest', artifactId: 'manifest-agent-admin', status: 'active', capabilityId: agentManifestReadCapability },
      { artifactKind: 'reference_manifest', artifactId: 'reference-manifest-agent-admin', status: 'active', capabilityId: agentReferenceReadCapability },
      { artifactKind: 'tool_boundary', artifactId: 'tool-boundary-agent-admin', status: 'active', capabilityId: 'agent_admin.get_tool_boundary' },
      { artifactKind: 'model_ref', artifactId: 'model-safe-default', status: 'active', capabilityId: agentModelsReadCapability }
    ],
    providerReadiness: { status: 'ready', safeReason: 'Provider alias is configured; credentials remain backend-only.', providerAlias: 'approved-primary', secretVisibility: 'redacted' },
    seedStatus: { seedBundleId: 'starter-v1', contentVersion: 'v1', resourceId: 'agent-definition-agent-admin.yaml', tenantCustomized: false },
    permissionState: {
      canEdit: false,
      reason: 'Read surface only. Use inert behavior-change proposals; no direct mutation from frontend state or AgentAdminAgent guidance.',
      authoritativeCapabilityId: agentDefinitionReadCapability
    },
    audit: {
      lastEventType: 'AgentDefinitionDetailDisplayed',
      lastActor: 'Tenant Admin',
      traceIds: ['trace-agent-admin-definition-agent-agent-admin', 'trace-agent-admin-protected-read']
    },
    redaction: { browserSafe: true, omittedFieldKeys: ['rawPromptBody', 'rawSkillBody', 'rawReferenceBody', 'rawProviderCredential', 'providerCredentialValue', 'rawJwt'], previewLimitChars: 220 },
    noDirectMutation: true
  },
  [agentAdminSurfaceActions.proposePromptDiff, agentAdminSurfaceActions.simulateToolBoundary, agentAdminSurfaceActions.manageModelRef, agentAdminSurfaceActions.openAgentTrace]
);

export const agentPromptGovernanceSurface = envelope(
  'surface-agent-prompt-governance',
  'governance-diff',
  'Prompt governance review',
  'agent-agent-admin',
  {
    surfaceContract: 'agent_admin.prompt_version.v1',
    proposalId: 'proposal-agent-admin-prompt-001',
    lifecycleState: 'draft',
    source: 'Deterministic AgentAdminService redacted prompt preview plus human-entered proposed change; AgentAdminAgent may draft rationale only.',
    riskClassification: 'medium',
    requiredApproval: agentSubmitReviewCapability + ' then ' + agentSkillsCapability + ' before activation',
    simulationSummary: 'Validation checks secret-like content, authority expansion language, tenant scope, and no-direct-mutation boundaries.',
    activationStatus: 'not active until separately approved and activated',
    traceLinks: ['trace-agent-admin-prompt-prompt-agent-admin-system', 'trace-agent-admin-behavior-draft'],
    beforeSummary: 'Active prompt requires backend authorization, ToolPermissionBoundary enforcement, provider fail-closed behavior, and no direct mutation.',
    afterSummary: 'Draft adds clearer evidence citation and redaction wording; full prompt body remains hidden with redactedPreview only.',
    changes: [
      { path: 'surfaceContract', before: 'agent_admin.prompt_version.v1', after: 'agent_admin.behavior_change_proposal.v1', impact: 'Browser sees redacted prompt metadata and proposal evidence, not raw hidden prompt text.' },
      { path: 'redactedPreview', before: 'You are AgentAdminAgent. Use governed evidence...', after: 'You are AgentAdminAgent. Cite PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace...', impact: 'Preview is browser-safe and limited; raw prompt body omitted.' },
      { path: 'lifecycle', before: 'draft', after: 'approval-required then activate_behavior_change', impact: 'Activation remains backend-owned and cannot be performed by model output or frontend state.' }
    ]
  },
  [agentAdminSurfaceActions.proposePromptDiff, agentAdminSurfaceActions.submitBehaviorChange, agentAdminSurfaceActions.testPrompt, agentAdminSurfaceActions.openAgentTrace]
);

export const agentSkillManifestSurface = envelope(
  'surface-agent-skill-manifest-diff',
  'governance-diff',
  'Skill/reference manifest and loader review',
  'agent-agent-admin',
  {
    surfaceContract: 'agent_admin.manifest.v1',
    proposalId: 'manifest-proposal-agent-admin-001',
    lifecycleState: 'in_review',
    source: 'Compact AgentSkillManifest and AgentReferenceManifest entries only; readSkill/readReferenceDoc load full documents at runtime after ToolPermissionBoundary checks.',
    riskClassification: 'high',
    requiredApproval: agentSkillsCapability,
    simulationSummary: 'Adding references broadens evidence access and requires human review before activation.',
    activationStatus: 'approved manifest changes still require action-activate-behavior-change.',
    traceLinks: ['trace-agent-admin-manifest-manifest-agent-admin', 'trace-agent-admin-manifest-reference-manifest-agent-admin'],
    beforeSummary: 'Manifest exposes compact hints for agent-admin-starter-guidance and reference docs; unassigned loads are denied with traces.',
    afterSummary: 'Proposal adds behavior-review guidance and readiness reference entries while preserving compact manifest and redactedPreview boundaries.',
    changes: [
      { path: 'skillManifest.entries[0]', before: 'agent-admin-starter-guidance@1', after: 'agent-admin-starter-guidance@1, agent-admin-behavior-review@1', impact: 'New guidance is compact until readSkill(skillId) is authorized.' },
      { path: 'referenceManifest.entries[+]', before: 'agent-admin-reference-guide@1', after: 'agent-admin-provider-readiness@1', impact: 'Reference evidence access broadens and requires review.' },
      { path: 'ToolPermissionBoundary', before: 'readSkill, readReferenceDoc, agentAdminEvidence.read', after: 'unchanged', impact: 'Tool access remains backend-enforced; manifest text cannot grant authority.' }
    ]
  },
  [agentAdminSurfaceActions.approveSkillManifest, agentAdminSurfaceActions.activateBehaviorChange, agentAdminSurfaceActions.rollbackBehaviorChange, agentAdminSurfaceActions.openAgentTrace]
);

export const agentToolBoundarySurface = envelope(
  'surface-agent-tool-boundary-diff',
  'governance-diff',
  'Tool boundary simulation review',
  'agent-agent-admin',
  {
    surfaceContract: 'agent_admin.tool_boundary.v1',
    proposalId: 'tool-boundary-proposal-agent-admin-001',
    lifecycleState: 'blocked',
    source: 'Deterministic ToolPermissionBoundary simulation; AgentAdminAgent can explain denials but cannot grant tools.',
    riskClassification: 'critical',
    requiredApproval: agentToolBoundariesCapability + ' plus ' + agentSkillsCapability,
    simulationSummary: 'Side-effecting tool grant is policy-blocked; current read-only evidence tools remain allowed.',
    activationStatus: 'blocked until policy simulation and human approval pass',
    traceLinks: ['trace-agent-admin-tool-boundary-tool-boundary-agent-admin', 'trace-agent-admin-tool-denied-email-send'],
    beforeSummary: 'AgentAdminAgent may use readSkill(skillId), readReferenceDoc(referenceId), and read-only agentAdminEvidence.read.',
    afterSummary: 'Proposal requests external email side effect; backend simulation returns TOOL_BOUNDARY_DENIED and no direct mutation.',
    changes: [
      { path: 'grants.agentAdminEvidence.read', before: 'DATA_LOOKUP read-only allowed', after: 'unchanged', impact: 'Runtime evidence remains scoped, redacted, and trace-linked.' },
      { path: 'grants.email.send', before: 'not assigned', after: 'requested', impact: 'External side effect requires separate approval and is denied in SMB starter.' },
      { path: 'simulation.result', before: 'not run', after: 'TOOL_BOUNDARY_DENIED', impact: 'Frontend renders blocked state and cannot bypass backend denial.' }
    ]
  },
  [agentAdminSurfaceActions.simulateToolBoundary, agentAdminSurfaceActions.rejectBehaviorChange, agentAdminSurfaceActions.openAgentTrace]
);

export const agentModelRefsSurface = envelope(
  'surface-agent-model-refs',
  'detail-edit',
  'Agent model refs',
  'agent-agent-admin',
  {
    surfaceContract: 'agent_admin.model_ref.v1',
    recordId: 'model-safe-default',
    recordLabel: 'Approved primary model alias',
    recordKind: 'ModelConfigRef',
    summary: 'Browser-safe model reference details. Provider alias and readiness are visible; credentials and provider secret values are redacted.',
    fields: [
      { fieldId: 'displayName', label: 'Display name', value: 'Approved primary model alias', editable: false, inputType: 'text' },
      { fieldId: 'providerAlias', label: 'Provider alias', value: 'approved-primary', editable: false, inputType: 'text' },
      { fieldId: 'providerCredential', label: 'Provider credential', value: '[REDACTED]', editable: false, inputType: 'text', disabledReason: 'Provider credentials are backend-only and never browser-visible.' },
      { fieldId: 'status', label: 'Status', value: 'active', editable: false, inputType: 'text' },
      { fieldId: 'allowedModes', label: 'Allowed modes', value: 'reasoning, tool-use', editable: false, inputType: 'text' }
    ],
    providerReadiness: { status: 'ready', safeReason: 'Provider alias is configured; credentials remain backend-only.', secretVisibility: 'redacted' },
    permissionState: { canEdit: false, reason: 'Model changes are behavior-changing proposals and require review/activation.', authoritativeCapabilityId: agentModelsReadCapability },
    audit: { lastEventType: 'AgentModelRefDisplayed', lastActor: 'Tenant Admin', traceIds: ['trace-agent-admin-model-ref-model-safe-default'] },
    redactionMetadata: { omittedFieldKeys: ['providerCredentialValue', 'rawProviderCredential'], browserSafe: true },
    noDirectMutation: true
  },
  [agentAdminSurfaceActions.manageModelRef, agentAdminSurfaceActions.openAgentTrace]
);

export const agentSeedMaterialSurface = envelope(
  'surface-agent-seed-material',
  'list-search',
  'Agent seed material',
  'agent-agent-admin',
  {
    surfaceContract: 'agent_admin.seed_material.v1',
    query: { bundle: 'starter-v1', tenantScoped: true },
    rows: [
      { id: 'seed-agent-agent-admin', artifactKind: 'AgentDefinition', artifactId: 'agent-agent-admin', status: 'active', seedBundleId: 'starter-v1', tenantCustomized: false, checksum: 'sha256:agent-admin-definition', traceId: 'trace-agent-admin-seed-agent-agent-admin' },
      { id: 'seed-prompt-agent-admin-system', artifactKind: 'PromptDocument', artifactId: 'prompt-agent-admin-system', status: 'active', seedBundleId: 'starter-v1', tenantCustomized: false, checksum: 'sha256:prompt-agent-admin-system', traceId: 'trace-agent-admin-seed-prompt-agent-admin-system' },
      { id: 'seed-tool-boundary-agent-admin', artifactKind: 'ToolPermissionBoundary', artifactId: 'tool-boundary-agent-admin', status: 'active', seedBundleId: 'starter-v1', tenantCustomized: false, checksum: 'sha256:tool-boundary-agent-admin', traceId: 'trace-agent-admin-seed-tool-boundary-agent-admin' }
    ],
    pageInfo: { totalKnownCount: 3 },
    redaction: 'Seed provenance shows resource ids, checksums, tenantCustomized, and trace links; raw prompt/skill/reference bodies and provider secrets are omitted.',
    traceLinks: ['trace-agent-admin-seed-material'],
    mobileFallback: 'table-to-card',
    systemStates: ['loading', 'empty', 'forbidden', 'stale']
  },
  [agentAdminSurfaceActions.listSeedMaterial, agentAdminSurfaceActions.openAgentTrace]
);

export const agentTestConsoleSurface = envelope(
  'surface-agent-test-console',
  'workflow-status',
  'No-side-effect agent test console',
  'agent-agent-admin',
  {
    surfaceContract: 'agent_admin.no_side_effect_test.v1',
    workflowId: 'agent-runtime-test-agent-admin',
    status: 'completed',
    summary: 'No-side-effect test resolves AuthContext, AgentDefinition, prompt assembly, compact manifests, readSkill/readReferenceDoc, agentAdminEvidence.read, and ToolPermissionBoundary checks without committing behavior changes.',
    traceIds: ['trace-agent-admin-test-prompt-assembly', 'trace-agent-work-88'],
    requiredCapabilityId: agentRuntimeTestCapability,
    taskKind: 'request-response-agent-test',
    steps: [
      { stepId: 'resolve-agent-definition', label: 'Resolve active AgentDefinition and selected AuthContext', status: 'completed' },
      { stepId: 'assemble-prompt', label: 'PromptAssemblyTrace emitted with compact manifests and redactions', status: 'completed' },
      { stepId: 'read-skill', label: 'readSkill(skillId) allowed for assigned Agent Admin guidance and denied for unassigned skill', status: 'completed' },
      { stepId: 'read-reference', label: 'readReferenceDoc(referenceId) allowed for assigned references only', status: 'completed' },
      { stepId: 'read-evidence', label: 'agentAdminEvidence.read returned scoped redacted catalog/proposal/provider evidence', status: 'completed' },
      { stepId: 'tool-boundary-check', label: 'Side-effecting tools disabled in test mode; no direct mutation occurred', status: 'completed' }
    ],
    resultSummary: 'Model-backed normal runtime must use WorkstreamRuntimeAgent; fixture test surfaces never claim provider-backed success when provider/runtime is absent.',
    noDirectMutation: true,
    safety: 'No prompt, manifest, tool-boundary, model, seed, approval, activation, rollback, or provider configuration mutation occurred.'
  },
  [agentAdminSurfaceActions.testPrompt, agentAdminSurfaceActions.openAgentTrace]
);

export const agentBehaviorProposalSurface = envelope(
  'surface-agent-behavior-proposal',
  'decision',
  'Review agent behavior proposal',
  'agent-agent-admin',
  {
    surfaceContract: 'agent_admin.behavior_change_proposal.v1',
    decisionId: 'decision-agent-behavior-12',
    recommendation: 'Submit the safe prompt wording for review, reject the email tool expansion, and activate only after deterministic approval records exist.',
    riskScore: 81,
    confidenceScore: 77,
    lifecycleState: 'in_review',
    targetArtifact: 'PROMPT',
    idempotencyKeySource: 'client-generated',
    requiredCapabilities: [agentSubmitReviewCapability, agentSkillsCapability, agentActivateCapability, agentRollbackCapability],
    noDirectMutation: true,
    disabledActions: [
      { actionId: 'action-activate-behavior-change', reason: 'Activation stays blocked until backend confirms approved proposal, active version, rollback metadata, and idempotency key.' },
      { actionId: 'action-rollback-behavior-change', reason: 'Rollback requires activated proposal metadata and backend command authority.' }
    ],
    allowedActions: [
      { actionId: 'action-submit-behavior-change', label: 'Submit behavior change for review', browserToolId: 'action-submit-behavior-change', governedToolId: agentSubmitReviewCapability, capabilityId: agentSubmitReviewCapability },
      { actionId: 'action-approve-skill-manifest', label: 'Approve reviewed change', browserToolId: 'action-approve-skill-manifest', governedToolId: agentSkillsCapability, capabilityId: agentSkillsCapability },
      { actionId: 'action-reject-behavior-change', label: 'Reject behavior change', browserToolId: 'action-reject-behavior-change', governedToolId: agentRejectCapability, capabilityId: agentRejectCapability }
    ],
    evidence: [
      { evidenceId: 'evidence-prompt-diff', label: 'Prompt diff', summary: 'Diff improves trace citation wording; redactedPreview omits raw hidden prompt text.' },
      { evidenceId: 'evidence-tool-simulation', label: 'Tool simulation', summary: 'External email side effect remains TOOL_BOUNDARY_DENIED and approval-required.' },
      { evidenceId: 'evidence-provider-readiness', label: 'Provider readiness', summary: 'Model ref aliases are browser-safe and provider credentials remain redacted.' },
      { evidenceId: 'evidence-agent-admin-traces', label: 'Trace links', summary: 'PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, agentAdminEvidence.read, and AgentWorkTrace are linked for review.' }
    ],
    traceLinks: ['trace-agent-admin-behavior-draft', 'trace-agent-admin-behavior-review', 'trace-agent-admin-behavior-activation-blocked']
  },
  [agentAdminSurfaceActions.submitBehaviorChange, agentAdminSurfaceActions.approveSkillManifest, agentAdminSurfaceActions.rejectBehaviorChange, agentAdminSurfaceActions.activateBehaviorChange, agentAdminSurfaceActions.cancelBehaviorChange, agentAdminSurfaceActions.rollbackBehaviorChange, agentAdminSurfaceActions.openAgentTrace]
);

export const agentAdminPromptRiskReviewSurface = envelope(
  'surface-agent-admin-prompt-risk-review',
  'workflow-status',
  'Agent Admin prompt-risk review',
  'agent-agent-admin',
  {
    surfaceContract: 'agent_admin.prompt_risk_review_task.v1',
    workflowId: 'workflow-agent-admin-prompt-risk-review-001',
    taskId: 'prompt-risk-review-001',
    autonomousAgentTaskId: 'akka-task-prompt-risk-review-001',
    status: 'completed',
    summary: 'Prompt-risk AutonomousAgent completed model-backed advisory review; activation remains blocked until human Agent Admin decision.',
    initiatingCapabilityId: agentPromptRiskStartCapability,
    requiredCapabilityId: agentPromptRiskReadCapability,
    taskKind: 'autonomous-agent-analysis',
    progress: { percent: 100, summary: 'completed_review_required' },
    scope: { scopeType: 'CUSTOMER', tenantId: authContext.tenantId, customerId: authContext.customerId },
    targetAgentDefinitionId: 'agent-agent-admin',
    proposalId: 'proposal-agent-admin-prompt-risk-001',
    proposalStatus: 'in_review',
    artifactDeltas: [
      { artifactKind: 'PROMPT_DOCUMENT', artifactId: 'agent-admin-system', changeSummary: 'Tighten governance prompt wording', redactedDiffRef: 'diff:proposal-agent-admin-prompt-risk-001:prompt' },
      { artifactKind: 'TOOL_PERMISSION_BOUNDARY', artifactId: 'tool-boundary-agent-admin', changeSummary: 'Requested side-effecting grant remains blocked', redactedDiffRef: 'diff:proposal-agent-admin-prompt-risk-001:tool-boundary' }
    ],
    riskSummary: 'High risk because the proposal touches prompt authority and ToolPermissionBoundary grants; No direct activation or mutation is allowed by this review.',
    overallRisk: 'HIGH',
    findings: [
      { findingId: 'finding-prompt-hierarchy', riskLevel: 'HIGH', artifactKind: 'PROMPT_DOCUMENT', artifactId: 'agent-admin-system', category: 'prompt instruction hierarchy conflict', browserSafeDescription: 'Prompt wording could be read as authority expansion without explicit refusal behavior.', evidenceRefs: ['agentAdminEvidence.read', 'readSkill:agent-admin-prompt-risk-review'], requiresHumanReview: true },
      { findingId: 'finding-tool-boundary', riskLevel: 'CRITICAL', artifactKind: 'TOOL_PERMISSION_BOUNDARY', artifactId: 'tool-boundary-agent-admin', category: 'side-effecting tool exposure', browserSafeDescription: 'Side-effecting tool exposure requires separate approval, idempotency, and activation checks.', evidenceRefs: ['readReferenceDoc:agent-admin-prompt-risk-review'], requiresHumanReview: true }
    ],
    recommendations: [
      { recommendationId: 'rec-reject-tool-boundary', label: 'Reject tool-boundary expansion', risk: 'critical', confidence: 'high', summary: 'Do not activate side-effecting grants from prompt-risk acceptance; use a separate governed proposal path.' }
    ],
    requiredHumanReviewReasons: ['prompt authority change', 'ToolPermissionBoundary expansion', 'model-backed advisory result'],
    evidenceRefs: ['agentAdminEvidence.read', 'readSkill:agent-admin-prompt-risk-review', 'readReferenceDoc:agent-admin-prompt-risk-review', 'proposal:proposal-agent-admin-prompt-risk-001'],
    providerFailures: [],
    traceIds: ['trace-prompt-risk-assembly-001', 'trace-prompt-risk-skill-load-001', 'trace-prompt-risk-reference-load-001', 'trace-prompt-risk-model-call-001'],
    resultReviewState: 'completed_review_required',
    noDirectMutation: true,
    activationBlockedUntilHumanDecision: true,
    safety: 'Advisory-only prompt-risk result. Human Agent Admin review is required before any activation or behavior artifact mutation; accepting this result does not activate prompts, skills, references, models, or tool boundaries.'
  },
  [agentAdminSurfaceActions.readPromptRiskReview, agentAdminSurfaceActions.acceptPromptRiskReviewResult, agentAdminSurfaceActions.rejectPromptRiskReviewResult, agentAdminSurfaceActions.cancelPromptRiskReview, agentAdminSurfaceActions.openAgentTrace]
);

export const agentAdminAgentBlockedSystemMessageSurface = envelope(
  'surface-agent-admin-agent-provider-blocked',
  'system_message',
  'AgentAdminAgent unavailable',
  'agent-agent-admin',
  {
    status: 'blocked_provider_or_runtime',
    severity: 'warning',
    title: 'AgentAdminAgent unavailable',
    summary: 'Model-backed AgentAdminAgent guidance was blocked before a response was produced.',
    message: 'Model-backed AgentAdminAgent guidance was blocked before a response was produced. Backend provider/runtime configuration must be restored before guidance can run.',
    recoverySteps: ['Verify model provider configuration and active ModelConfigRef on the backend.', 'Review PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, agentAdminEvidence.read, and AgentWorkTrace entries for this correlation id.', 'Retry after backend configuration is restored; use deterministic Agent Admin surfaces for catalog reads and behavior-change lifecycle commands.'],
    workstreamEntryId: 'item-agent-admin-agent-provider-blocked',
    producingAgentId: 'agent-agent-admin',
    capabilityId: 'agent_admin.submit_turn',
    governedToolId: 'agent_admin.submit_turn',
    browserToolId: 'action-audit-trace-investigation-guide',
    sourceRefs: [{ refType: 'trace', refId: 'trace-agentadmin-agent-provider-blocked', label: 'Blocked AgentAdminAgent runtime trace' }, { refType: 'capability', refId: agentDefinitionsCapability, label: 'Scoped Agent Admin read capability' }],
    safety: { sanitized: true, redactionNote: 'Provider secrets, raw JWTs, hidden prompts, raw skill/reference bodies, and unauthorized tenant/customer evidence are omitted.' },
    trace: { correlationId: 'corr-agentadmin-agent-provider-blocked', traceIds: ['trace-agentadmin-agent-provider-blocked', 'trace-agentadmin-agent-work-blocked'] }
  },
  [agentAdminSurfaceActions.openAgentTrace]
);

export const agentAdminTraceSurface = envelope(
  'surface-agent-admin-trace',
  'audit-timeline',
  'Agent Admin traces',
  'agent-agent-admin',
  {
    events: [
      { eventId: 'protected-read-catalog', occurredAt: generatedAt, actor: 'AgentAdminService', action: 'Protected agent_admin.list_definitions read emitted scoped catalog trace', traceId: 'trace-agent-admin-catalog' },
      { eventId: 'prompt-assembly-42', occurredAt: generatedAt, actor: 'AgentRuntimeService', action: 'PromptAssemblyTrace emitted with compact AgentSkillManifest and AgentReferenceManifest redactions', traceId: 'trace-prompt-assembly-42' },
      { eventId: 'skill-load-17', occurredAt: generatedAt, actor: 'readSkill(skillId)', action: 'SkillLoadTrace emitted for allowed assigned skill and denied unassigned skill', traceId: 'trace-skill-load-17' },
      { eventId: 'reference-load-19', occurredAt: generatedAt, actor: 'readReferenceDoc(referenceId)', action: 'ReferenceLoadTrace emitted for assigned Agent Admin reference', traceId: 'trace-reference-load-19' },
      { eventId: 'agent-admin-evidence-read', occurredAt: generatedAt, actor: 'agentAdminEvidence.read', action: 'Read-only evidence tool returned scoped redacted catalog/proposal/provider data', traceId: 'trace-agent-admin-evidence-read' },
      { eventId: 'agent-work-88', occurredAt: generatedAt, actor: 'WorkstreamRuntimeAgent', action: 'AgentWorkTrace recorded no direct mutation and provider fail-closed semantics', traceId: 'trace-agent-work-88' }
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
  startImpactAnalysis: 'governance.policy.impact_analysis.start',
  readImpactAnalysis: 'governance.policy.impact_analysis.read',
  cancelImpactAnalysis: 'governance.policy.impact_analysis.cancel',
  acceptImpactResult: 'governance.policy.impact_analysis.accept_result',
  rejectImpactResult: 'governance.policy.impact_analysis.reject_result',
  requestImpactChanges: 'governance.policy.impact_analysis.request_changes',
  openTrace: 'audit.trace.read'
} as const;

export const governancePolicySurfaceActions = {
  showDashboard: {
    actionId: 'action-govpol-show-dashboard',
    label: 'Refresh governance dashboard',
    intent: 'read',
    capabilityId: governancePolicyCapabilities.readDashboard,
    governedToolId: governancePolicyCapabilities.readDashboard,
    browserToolId: 'action-govpol-show-dashboard',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-dashboard', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyDashboardRead', traceRequired: true }
  },
  showInventory: {
    actionId: 'action-govpol-show-policy-inventory',
    label: 'Show policy inventory',
    intent: 'read',
    capabilityId: governancePolicyCapabilities.readDashboard,
    governedToolId: governancePolicyCapabilities.readDashboard,
    browserToolId: 'action-govpol-show-policy-inventory',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-inventory', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyInventoryRead', traceRequired: true }
  },
  draftProposal: {
    actionId: 'action-govpol-draft-proposal',
    label: 'Draft policy proposal',
    intent: 'proposal',
    capabilityId: governancePolicyCapabilities.draftProposal,
    governedToolId: governancePolicyCapabilities.draftProposal,
    browserToolId: 'action-govpol-draft-proposal',
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
    governedToolId: governancePolicyCapabilities.simulateProposal,
    browserToolId: 'action-govpol-simulate-proposal',
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
    governedToolId: governancePolicyCapabilities.approveProposal,
    browserToolId: 'action-govpol-decide-proposal',
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
    governedToolId: governancePolicyCapabilities.activatePolicyChange,
    browserToolId: 'action-govpol-activate-policy-change',
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
    governedToolId: governancePolicyCapabilities.rollbackPolicyChange,
    browserToolId: 'action-govpol-rollback-policy-change',
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
    governedToolId: governancePolicyCapabilities.startImpactAnalysis,
    browserToolId: 'action-govpol-start-impact-analysis',
    inputSchemaRef: 'schema.governance-policy.impact-analysis.start.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-impact-analysis-task', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactAnalysisStartedOrBlocked', traceRequired: true }
  },
  readImpactAnalysis: {
    actionId: 'action-govpol-read-impact-analysis',
    label: 'Read policy impact task',
    intent: 'read',
    capabilityId: governancePolicyCapabilities.readImpactAnalysis,
    governedToolId: governancePolicyCapabilities.readImpactAnalysis,
    browserToolId: 'action-govpol-read-impact-analysis',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-impact-analysis-task', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactAnalysisRead', traceRequired: true }
  },
  cancelImpactAnalysis: {
    actionId: 'action-govpol-cancel-impact-analysis',
    label: 'Cancel policy impact task',
    intent: 'command',
    capabilityId: governancePolicyCapabilities.cancelImpactAnalysis,
    governedToolId: governancePolicyCapabilities.cancelImpactAnalysis,
    browserToolId: 'action-govpol-cancel-impact-analysis',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-impact-analysis-task', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactAnalysisCancelled', traceRequired: true }
  },
  acceptImpactResult: {
    actionId: 'action-govpol-accept-impact-result',
    label: 'Accept advisory impact result',
    intent: 'approval',
    capabilityId: governancePolicyCapabilities.acceptImpactResult,
    governedToolId: governancePolicyCapabilities.acceptImpactResult,
    browserToolId: 'action-govpol-accept-impact-result',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-impact-analysis-result', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactResultAccepted', traceRequired: true }
  },
  rejectImpactResult: {
    actionId: 'action-govpol-reject-impact-result',
    label: 'Reject advisory impact result',
    intent: 'approval',
    capabilityId: governancePolicyCapabilities.rejectImpactResult,
    governedToolId: governancePolicyCapabilities.rejectImpactResult,
    browserToolId: 'action-govpol-reject-impact-result',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-impact-analysis-result', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactResultRejected', traceRequired: true }
  },
  requestImpactChanges: {
    actionId: 'action-govpol-request-impact-changes',
    label: 'Request analysis changes',
    intent: 'approval',
    capabilityId: governancePolicyCapabilities.requestImpactChanges,
    governedToolId: governancePolicyCapabilities.requestImpactChanges,
    browserToolId: 'action-govpol-request-impact-changes',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-impact-analysis-result', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactChangesRequested', traceRequired: true }
  },
  openTrace: {
    actionId: 'action-govpol-open-trace',
    label: 'Open governance trace',
    intent: 'trace',
    capabilityId: governancePolicyCapabilities.openTrace,
    governedToolId: governancePolicyCapabilities.openTrace,
    browserToolId: 'action-govpol-open-trace',
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
      { sectionId: 'impact-worker', label: 'Impact worker', summary: 'AutonomousAgent impact analysis is backend-derived, emits workflow.governance_policy.impact_analysis.* and worker.task.* events, and fails closed when provider/runtime configuration is missing.' }
    ],
    nextSteps: [
      { workstreamId: 'agent-governance-policy', label: 'Review policy inventory', allowed: true, capabilityIds: [governancePolicyCapabilities.readDashboard], traceId: 'trace-govpol-inventory' },
      { workstreamId: 'agent-governance-policy', label: 'Simulate proposal impact', allowed: true, capabilityIds: [governancePolicyCapabilities.simulateProposal], traceId: 'trace-govpol-simulation' },
      { workstreamId: 'agent-governance-policy', label: 'Start impact analysis', allowed: true, capabilityIds: [governancePolicyCapabilities.startImpactAnalysis], traceId: 'trace-govpol-impact-analysis-start' }
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
      expectedDenials: ['governance.policy.activate without approved proposal', 'governance.policy.impact_analysis.start when provider/runtime is missing'],
      warnings: ['Simulation is deterministic/advisory and never grants authority.', 'Unsupported evidence scopes are omitted with trace links.'],
      confidence: 'bounded fixture confidence based on scoped policy rows',
      evidenceTraceIds: ['trace-govpol-simulation', 'trace-govpol-impact-analysis-blocked']
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
      { actionId: 'action-govpol-decide-proposal', label: 'Approve proposal', browserToolId: 'action-govpol-decide-proposal', governedToolId: governancePolicyCapabilities.approveProposal, capabilityId: governancePolicyCapabilities.approveProposal },
      { actionId: 'action-govpol-open-trace', label: 'Open decision trace', browserToolId: 'action-govpol-open-trace', governedToolId: governancePolicyCapabilities.openTrace, capabilityId: governancePolicyCapabilities.openTrace }
    ],
    disabledActions: [
      { actionId: 'action-govpol-activate-policy-change', reason: 'Activation stays blocked until backend confirms approved version and idempotency key.' },
      { actionId: 'action-govpol-activate-policy-change', reason: 'Activation stays a separate backend-governed policy capability; impact analysis acceptance cannot activate policy state.' }
    ],
    risk: 'Authority-changing approval',
    traceLinks: ['trace-govpol-decision', 'trace-govpol-approval-basis']
  },
  [governancePolicySurfaceActions.decideProposal, governancePolicySurfaceActions.activateProposal, governancePolicySurfaceActions.rollbackProposal, governancePolicySurfaceActions.openTrace]
);

export const governancePolicyImpactAnalysisTaskSurface = envelope(
  'surface-governance-policy-impact-analysis-task',
  'workflow-status',
  'Policy impact analysis task',
  'agent-governance-policy',
  {
    surfaceContract: 'governance.policy.impact_analysis.task.v1',
    workflowId: 'govpol-impact-task-001',
    taskId: 'governance-impact-fixture-001',
    taskKind: 'autonomous-agent-analysis',
    status: 'blocked_provider_or_runtime',
    summary: 'Policy-impact analysis is a durable AutonomousAgent task; backend projection emits worker.task.blocked_provider_or_runtime and workflow.governance_policy.impact_analysis.blocked_provider_or_runtime when provider/runtime config is missing.',
    requiredCapabilityId: governancePolicyCapabilities.readImpactAnalysis,
    initiatingCapabilityId: governancePolicyCapabilities.startImpactAnalysis,
    traceIds: ['trace-govpol-impact-analysis-blocked', 'trace-govpol-impact-worker-event'],
    progress: { percent: 0, summary: 'Blocked by provider/runtime readiness; no frontend-only or deterministic success is rendered.' },
    blockers: [{ code: 'blocked_provider_or_runtime', message: 'Real Akka AutonomousAgent provider/runtime binding is required before impact findings can be marked impact_ready.' }],
    evidenceRefs: ['governancePolicyEvidence.read', 'readSkill:governance-policy-impact-analysis', 'readReferenceDoc:governance-policy-impact-analysis', 'proposal:proposal-govpol-001'],
    providerFailures: ['provider/model/governed runtime missing; fail-closed no fake success'],
    resultReviewState: 'blocked_provider_or_runtime',
    noDirectMutation: true,
    safety: 'Frontend reloads this state from backend projections; raw prompts, JWTs, provider secrets, raw tool payloads, and cross-tenant evidence are redacted.',
    resultSummary: 'No model-less, simulated, deterministic, or fake impact_ready result is produced.'
  },
  [governancePolicySurfaceActions.readImpactAnalysis, governancePolicySurfaceActions.cancelImpactAnalysis, governancePolicySurfaceActions.openTrace]
);

export const governancePolicyImpactAnalysisResultSurface = envelope(
  'surface-governance-policy-impact-analysis-result',
  'decision',
  'Policy impact result review',
  'agent-governance-policy',
  {
    decisionId: 'governance-impact-fixture-001',
    recommendation: 'Review advisory policy impact findings, accept or reject the analysis as evidence, then use separate proposal decision and activation capabilities if still authorized.',
    riskScore: 'high',
    confidenceScore: 'model-backed when runtime is available; blocked_provider_or_runtime otherwise',
    evidence: [
      { evidenceId: 'governancePolicyEvidence.read', label: 'Scoped policy evidence', summary: 'Proposal, affected capabilities, approval gate, ToolPermissionBoundary, and simulation refs are browser-safe and tenant-scoped.' },
      { evidenceId: 'trace-govpol-impact-worker-event', label: 'v3 event linkage', summary: 'workflow.governance_policy.impact_analysis.completed_review_required and worker.task.completed_review_required drive attention.' },
      { evidenceId: 'redaction', label: 'Redaction guardrail', summary: 'Raw prompts, hidden prompt text, provider credentials, JWTs, raw tool payloads, and cross-tenant/customer data are omitted.' }
    ],
    allowedActions: [
      { actionId: 'action-govpol-accept-impact-result', label: 'Accept advisory result evidence', browserToolId: 'action-govpol-accept-impact-result', governedToolId: governancePolicyCapabilities.acceptImpactResult, capabilityId: governancePolicyCapabilities.acceptImpactResult },
      { actionId: 'action-govpol-reject-impact-result', label: 'Reject advisory result evidence', browserToolId: 'action-govpol-reject-impact-result', governedToolId: governancePolicyCapabilities.rejectImpactResult, capabilityId: governancePolicyCapabilities.rejectImpactResult },
      { actionId: 'action-govpol-request-impact-changes', label: 'Request analysis changes', browserToolId: 'action-govpol-request-impact-changes', governedToolId: governancePolicyCapabilities.requestImpactChanges, capabilityId: governancePolicyCapabilities.requestImpactChanges }
    ],
    disabledActions: [
      { actionId: 'action-govpol-activate-policy-change', reason: 'Worker result acceptance never activates policy; activation remains a separate human-authorized backend capability.' }
    ],
    risk: 'Advisory-only policy impact; activationBlockedUntilHumanDecision=true; noDirectMutation=true',
    traceLinks: ['trace-govpol-impact-result', 'trace-govpol-impact-worker-event']
  },
  [governancePolicySurfaceActions.acceptImpactResult, governancePolicySurfaceActions.rejectImpactResult, governancePolicySurfaceActions.requestImpactChanges, governancePolicySurfaceActions.openTrace]
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
      { eventId: 'govpol-impact-analysis-blocked', occurredAt: generatedAt, actor: 'Governance/Policy Agent', action: 'AutonomousAgent impact analysis emitted workflow.governance_policy.impact_analysis.blocked_provider_or_runtime and worker.task.blocked_provider_or_runtime; attention points to surface-governance-policy-impact-analysis-task.', traceId: 'trace-govpol-impact-analysis-blocked' }
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
  governancePolicyImpactAnalysisTaskSurface,
  governancePolicyImpactAnalysisResultSurface,
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

export const displayGovernancePolicyImpactTaskActionResult: CapabilityActionResult = {
  status: 'blocked_provider_or_runtime',
  message: 'Displayed backend-derived governance.policy.impact_analysis task state with v3 worker.task.* / workflow.governance_policy.impact_analysis.* event and attention linkage; no fake success is rendered.',
  correlationId: 'corr-display-governance-policy-impact-task',
  traceIds: ['trace-govpol-impact-analysis-blocked', 'trace-govpol-impact-worker-event'],
  resultSurface: governancePolicyImpactAnalysisTaskSurface
};

export const displayGovernancePolicyImpactResultActionResult: CapabilityActionResult = {
  status: 'approval-required',
  message: 'Displayed advisory Governance/Policy impact result review surface. Accept/reject/request-changes record worker-result disposition only and cannot activate policy state.',
  correlationId: 'corr-display-governance-policy-impact-result',
  traceIds: ['trace-govpol-impact-result', 'trace-govpol-impact-worker-event'],
  resultSurface: governancePolicyImpactAnalysisResultSurface
};

export const dashboardSurface = envelope('surface-dashboard', 'dashboard', 'Tenant attention dashboard', 'agent-my-account', { cards: [{ cardId: 'card-open-decisions', label: 'Open decisions', value: 2, severity: 'warning' }], attentionSource: 'attention.list_workstream_items', attentionItems: [{ itemId: 'attention-agent-admin-readiness', label: 'Agent Admin provider readiness is blocked', summary: 'Model/runtime provider readiness is blocked until governed provider configuration is available.', status: 'open', severity: 'blocked', category: 'provider_readiness', capabilityId: 'agent_admin.list_definitions', governedToolId: 'attention.open_attention_item', traceId: 'trace-agent-admin-provider-readiness', sourceWorkstreamId: 'agent-agent-admin', surfaceRef: { targetFunctionalAgentId: 'agent-agent-admin', targetSurfaceId: 'surface-agent-admin-catalog', targetSurfaceType: 'dashboard', targetItemId: 'attention-agent-admin-readiness', defaultActionId: 'attention.open_attention_item', requiredCapabilityId: 'agent_admin.list_definitions' }, redaction: 'full' }], scopeNote: 'Full-core/demo surface; actionable attention is backend-derived through attention.list_workstream_items and separate from transient unseen-response badges.' }, [surfaceActionsByIntent.read]);
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
  agentSeedMaterialSurface,
  agentTestConsoleSurface,
  agentBehaviorProposalSurface,
  agentAdminPromptRiskReviewSurface,
  agentAdminAgentBlockedSystemMessageSurface,
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
  'blocked_provider_or_runtime': { status: 'blocked_provider_or_runtime', message: 'Provider or runtime prerequisite blocked safely.', ...resultBase, resultSurface: agentAdminAgentBlockedSystemMessageSurface }
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

export const displayAgentSeedMaterialActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Displayed Agent Admin seed/default material with scoped provenance, checksums, tenant-customized state, redaction, and trace links.',
  correlationId: 'corr-display-agent-seed-material',
  traceIds: ['trace-agent-admin-seed-material'],
  resultSurface: agentSeedMaterialSurface
};

export const displayAgentPromptGovernanceActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Displayed redacted prompt version evidence and inert proposal draft. AgentAdminAgent guidance and frontend state cannot activate behavior directly.',
  correlationId: 'corr-display-agent-prompt-governance',
  traceIds: ['trace-agent-admin-prompt-prompt-agent-admin-system', 'trace-agent-admin-behavior-draft'],
  resultSurface: agentPromptGovernanceSurface
};

export const displayAgentManifestActionResult: CapabilityActionResult = {
  status: 'approval-required',
  message: 'Manifest review requires deterministic approval and separate activation; compact manifests, readSkill, readReferenceDoc, and agentAdminEvidence.read remain backend-governed.',
  correlationId: 'corr-display-agent-manifest',
  traceIds: ['trace-agent-admin-manifest-manifest-agent-admin'],
  resultSurface: agentSkillManifestSurface
};

export const displayAgentToolBoundaryActionResult: CapabilityActionResult = {
  status: 'blocked_provider_or_runtime',
  message: 'Tool-boundary simulation denied a side-effecting grant safely with TOOL_BOUNDARY_DENIED evidence and no direct mutation.',
  correlationId: 'corr-display-agent-tool-boundary',
  traceIds: ['trace-agent-admin-tool-boundary-tool-boundary-agent-admin', 'trace-agent-admin-tool-denied-email-send'],
  resultSurface: agentToolBoundarySurface
};

export const displayAgentModelRefsActionResult: CapabilityActionResult = {
  status: 'denied',
  message: 'Model ref change request was denied safely; provider aliases are browser-safe and provider credentials remain redacted.',
  correlationId: 'corr-display-agent-model-refs',
  traceIds: ['trace-agent-admin-model-ref-model-safe-default'],
  resultSurface: agentModelRefsSurface
};

export const displayAgentTestConsoleActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Displayed no-side-effect Agent Admin test surface with prompt assembly, loader tool, evidence tool, trace, and no-direct-mutation evidence.',
  correlationId: 'corr-display-agent-test-console',
  traceIds: ['trace-agent-admin-test-prompt-assembly', 'trace-agent-work-88'],
  resultSurface: agentTestConsoleSurface
};

export const displayAgentBehaviorProposalActionResult: CapabilityActionResult = {
  status: 'approval-required',
  message: 'Displayed behavior-change proposal lifecycle state; submit/review/approval/activation/rollback remain deterministic backend commands.',
  correlationId: 'corr-display-agent-behavior-proposal',
  traceIds: ['trace-agent-admin-behavior-review', 'trace-agent-admin-behavior-activation-blocked'],
  resultSurface: agentBehaviorProposalSurface
};

export const displayAgentPromptRiskReviewActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Displayed Agent Admin prompt-risk AutonomousAgent review surface with v3 event, attention, no direct mutation, and human decision evidence.',
  correlationId: 'corr-display-agent-prompt-risk-review',
  traceIds: ['trace-prompt-risk-assembly-001', 'trace-prompt-risk-model-call-001'],
  resultSurface: agentAdminPromptRiskReviewSurface
};

export const displayAgentAdminTraceActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Displayed Agent Admin protected-read, prompt assembly, loader tool, agentAdminEvidence.read, provider, and AgentWorkTrace timeline.',
  correlationId: 'corr-display-agent-admin-trace',
  traceIds: ['trace-agent-admin-catalog', 'trace-agent-work-88'],
  resultSurface: agentAdminTraceSurface
};

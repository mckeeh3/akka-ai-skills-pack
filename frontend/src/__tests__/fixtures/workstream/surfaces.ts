import type { CapabilityActionResult, SurfaceAction, SurfaceEnvelope } from '../../../workstream/types';
import { tenantAdminAuthContext } from './me';

const generatedAt = '2026-05-19T12:00:00.000Z';
const authContext = {
  tenantId: tenantAdminAuthContext.tenantId,
  customerId: tenantAdminAuthContext.customerId,
  selectedContextId: tenantAdminAuthContext.selectedContextId,
  visibleCapabilityIds: tenantAdminAuthContext.capabilityIds
};

const secureTenantUserFoundation = 'user_admin.view_overview';
const userAdminCapabilities = {
  overview: 'user_admin.view_overview',
  listInvitations: 'user_admin.acceptance_status.read',
  sendInvitation: 'user_admin.invite_user',
  resendInvitation: 'user_admin.resend_invitation',
  revokeInvitation: 'user_admin.revoke_invitation',
  listMembers: 'user_admin.list_members',
  updateMemberStatus: 'user_admin.update_member_status',
  listRolesCapabilities: 'user_admin.preview_role_change',
  previewRoleChange: 'user_admin.preview_role_change',
  changeMemberRoles: 'user_admin.change_member_roles',
  agentTurn: 'USER_ADMIN_AGENT_TURN',
  startAccessReviewTask: 'user_admin.access_review.start',
  viewAccessReviewTask: 'user_admin.access_review.read',
  cancelAccessReviewTask: 'user_admin.access_review.cancel',
  acceptAccessReviewResult: 'user_admin.access_review.accept_result',
  rejectAccessReviewResult: 'user_admin.access_review.reject_result',
  viewTraceReference: 'admin.audit.read',
  listOrganizations: 'saas_owner.organization.list',
  readOrganization: 'saas_owner.organization.read',
  createOrganization: 'saas_owner.organization.create',
  renameOrganization: 'saas_owner.organization.rename',
  suspendOrganization: 'saas_owner.organization.suspend',
  reactivateOrganization: 'saas_owner.organization.reactivate',
  inviteOrganizationAdmin: 'saas_owner.organization_admin.invite'
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
      targetFunctionalAgentId: 'user-admin-agent',
      targetSurfaceId: 'surface-user-admin-users',
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
    actionId: 'action-governance-policy-simulate',
    label: 'Run simulation',
    intent: 'governance',
    capabilityId: 'governance.policy.simulate',
    governedToolId: 'governance.policy.simulate',
    browserToolId: 'action-governance-policy-simulate',
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
    label: 'Show users',
    intent: 'read',
    capabilityId: userAdminCapabilities.listMembers,
    governedToolId: userAdminCapabilities.listMembers,
    browserToolId: 'action-display-user-list',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-users', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminListDisplayed', traceRequired: true }
  },
  showUsers: {
    actionId: 'action-user-admin-show-users',
    label: 'Show Users',
    intent: 'read',
    capabilityId: userAdminCapabilities.listMembers,
    governedToolId: 'search-user-directory',
    browserToolId: 'user-admin.show-users',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-users', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminBranchReturnDisplayed', traceRequired: true }
  },
  displayOrganizationAdmin: {
    actionId: 'action-display-organization-admin',
    label: 'Open Organization Admin',
    intent: 'read',
    capabilityId: userAdminCapabilities.listOrganizations,
    governedToolId: 'manage-organizations',
    browserToolId: 'action-display-organization-admin',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-directory', openPlacement: 'inline' },
    audit: { eventType: 'OrganizationAdminDisplayed', traceRequired: true }
  },
  openOrganizationCreate: {
    actionId: 'action-open-organization-create',
    label: 'Open Organization create form',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.createOrganization,
    governedToolId: 'manage-organizations',
    browserToolId: 'action-open-organization-create',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-organization-create', displayText: 'Open Organization create form' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-create', openPlacement: 'inline' },
    audit: { eventType: 'OrganizationCreateFormDisplayed', traceRequired: true }
  },
  openOrganizationRename: {
    actionId: 'action-open-organization-rename',
    label: 'Open Organization rename form',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.renameOrganization,
    governedToolId: 'manage-organizations',
    browserToolId: 'action-open-organization-rename',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-organization-rename', displayText: 'Open Organization rename form' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-rename', openPlacement: 'inline' },
    audit: { eventType: 'OrganizationRenameFormDisplayed', traceRequired: true }
  },
  openOrganizationSuspend: {
    actionId: 'action-open-organization-suspend',
    label: 'Open Organization suspend confirmation',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.suspendOrganization,
    governedToolId: 'manage-organizations',
    browserToolId: 'action-open-organization-suspend',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-organization-suspend-confirmation', displayText: 'Open Organization suspend confirmation' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-suspend-confirmation', openPlacement: 'inline' },
    audit: { eventType: 'OrganizationSuspendConfirmationDisplayed', traceRequired: true }
  },
  openOrganizationReactivate: {
    actionId: 'action-open-organization-reactivate',
    label: 'Open Organization reactivate confirmation',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.reactivateOrganization,
    governedToolId: 'manage-organizations',
    browserToolId: 'action-open-organization-reactivate',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-organization-reactivate-confirmation', displayText: 'Open Organization reactivate confirmation' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-reactivate-confirmation', openPlacement: 'inline' },
    audit: { eventType: 'OrganizationReactivateConfirmationDisplayed', traceRequired: true }
  },
  openOrganizationAdminInvitationCreate: {
    actionId: 'action-open-organization-admin-invitation-create',
    label: 'Invite Organization Admin',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.inviteOrganizationAdmin,
    governedToolId: 'manage-organization-admins',
    browserToolId: 'user-admin.open-organization-admin-invite',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-organization-admin-invitation-create', displayText: 'Invite Organization Admin' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-admin-invitation-create', openPlacement: 'inline' },
    audit: { eventType: 'OrganizationAdminInvitationCreateDisplayed', traceRequired: true }
  },
  listOrganizations: {
    actionId: 'action-organization-list',
    label: 'Refresh Organizations',
    intent: 'read',
    capabilityId: userAdminCapabilities.listOrganizations,
    governedToolId: 'manage-organizations',
    browserToolId: 'action-organization-list',
    inputSchemaRef: 'schema.organization-admin.search.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-directory', openPlacement: 'inline' },
    audit: { eventType: 'OrganizationListDisplayed', traceRequired: true }
  },
  showOrganizations: {
    actionId: 'action-user-admin-show-organizations',
    label: 'Back to organizations',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.listOrganizations,
    governedToolId: 'manage-organizations',
    browserToolId: 'user-admin.show-organizations',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-organization-directory', displayText: 'Show organizations' },
    inputSchemaRef: 'schema.organization-admin.open.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-directory', openPlacement: 'inline' },
    audit: { eventType: 'OrganizationDirectoryDisplayed', traceRequired: true }
  },
  readOrganization: {
    actionId: 'action-organization-read',
    label: 'Open Organization detail',
    intent: 'read',
    capabilityId: userAdminCapabilities.readOrganization,
    governedToolId: 'manage-organizations',
    browserToolId: 'action-organization-read',
    inputSchemaRef: 'schema.organization-admin.detail.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-detail', openPlacement: 'inline' },
    audit: { eventType: 'OrganizationDetailDisplayed', traceRequired: true }
  },
  createOrganization: {
    actionId: 'action-organization-create',
    label: 'Create Organization',
    intent: 'command',
    capabilityId: userAdminCapabilities.createOrganization,
    governedToolId: 'manage-organizations',
    browserToolId: 'action-organization-create',
    inputSchemaRef: 'schema.organization-admin.create.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-detail', openPlacement: 'inline' },
    audit: { eventType: 'ORGANIZATION_CREATE', traceRequired: true }
  },
  renameOrganization: {
    actionId: 'action-organization-rename',
    label: 'Rename Organization',
    intent: 'command',
    capabilityId: userAdminCapabilities.renameOrganization,
    governedToolId: 'manage-organizations',
    browserToolId: 'action-organization-rename',
    inputSchemaRef: 'schema.organization-admin.rename.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-detail', openPlacement: 'inline' },
    audit: { eventType: 'ORGANIZATION_RENAME', traceRequired: true }
  },
  suspendOrganization: {
    actionId: 'action-organization-suspend',
    label: 'Suspend Organization',
    intent: 'command',
    capabilityId: userAdminCapabilities.suspendOrganization,
    governedToolId: 'manage-organizations',
    browserToolId: 'action-organization-suspend',
    inputSchemaRef: 'schema.organization-admin.lifecycle.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-detail', openPlacement: 'inline' },
    audit: { eventType: 'ORGANIZATION_SUSPEND', traceRequired: true }
  },
  reactivateOrganization: {
    actionId: 'action-organization-reactivate',
    label: 'Reactivate Organization',
    intent: 'command',
    capabilityId: userAdminCapabilities.reactivateOrganization,
    governedToolId: 'manage-organizations',
    browserToolId: 'action-organization-reactivate',
    inputSchemaRef: 'schema.organization-admin.lifecycle.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-organization-detail', openPlacement: 'inline' },
    audit: { eventType: 'ORGANIZATION_REACTIVATE', traceRequired: true }
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-users', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminDirectorySearched', traceRequired: true }
  },
  displayUserDetail: {
    actionId: 'action-display-user-detail',
    label: 'Display user detail',
    intent: 'read',
    capabilityId: userAdminCapabilities.listMembers,
    governedToolId: userAdminCapabilities.listMembers,
    browserToolId: 'action-display-user-detail',
    inputSchemaRef: 'schema.user-admin.detail.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-user-detail', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminDetailDisplayed', traceRequired: true }
  },
  displayInvitationDetail: {
    actionId: 'action-display-invitation-detail',
    label: 'View invitation',
    intent: 'read',
    capabilityId: userAdminCapabilities.listInvitations,
    governedToolId: userAdminCapabilities.listInvitations,
    browserToolId: 'action-display-invitation-detail',
    inputSchemaRef: 'schema.user-admin.invitation-detail.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-invitation-detail', openPlacement: 'inline' },
    audit: { eventType: 'InvitationDetailDisplayed', traceRequired: true }
  },
  openInvitationResendConfirmation: {
    actionId: 'action-open-useradmin-invitation-resend-confirmation',
    label: 'Open resend confirmation',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.resendInvitation,
    governedToolId: userAdminCapabilities.resendInvitation,
    browserToolId: 'action-open-useradmin-invitation-resend-confirmation',
    inputSchemaRef: 'schema.user-admin.invitation-resend.open.v1',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-invitation-resend-confirmation', displayText: 'Open resend confirmation' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-invitation-resend-confirmation', openPlacement: 'inline' },
    audit: { eventType: 'InvitationResendConfirmationDisplayed', traceRequired: true }
  },
  openInvitationRevokeConfirmation: {
    actionId: 'action-open-useradmin-invitation-revoke-confirmation',
    label: 'Open revoke confirmation',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.revokeInvitation,
    governedToolId: userAdminCapabilities.revokeInvitation,
    browserToolId: 'action-open-useradmin-invitation-revoke-confirmation',
    inputSchemaRef: 'schema.user-admin.invitation-revoke.open.v1',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-invitation-revoke-confirmation', displayText: 'Open revoke confirmation' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-invitation-revoke-confirmation', openPlacement: 'inline' },
    audit: { eventType: 'InvitationRevokeConfirmationDisplayed', traceRequired: true }
  },
  openMembershipStatusConfirmation: {
    actionId: 'action-open-useradmin-membership-status-confirmation',
    label: 'Open membership status confirmation',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-open-useradmin-membership-status-confirmation',
    inputSchemaRef: 'schema.user-admin.member-status.open.v1',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-membership-status-confirmation', displayText: 'Open membership status confirmation' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-membership-status-confirmation', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminMembershipStatusConfirmationDisplayed', traceRequired: true }
  },
  openSupportAccessGrant: {
    actionId: 'action-open-useradmin-support-access-grant',
    label: 'Open support access grant',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.viewTraceReference,
    governedToolId: userAdminCapabilities.viewTraceReference,
    browserToolId: 'action-open-useradmin-support-access-grant',
    inputSchemaRef: 'schema.user-admin.support-access-grant.open.v1',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-support-access-grant', displayText: 'Open support access grant' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-support-access-grant', openPlacement: 'inline' },
    audit: { eventType: 'SupportAccessGrantSurfaceDisplayed', traceRequired: true }
  },
  openSupportAccessRevokeConfirmation: {
    actionId: 'action-open-useradmin-support-access-revoke-confirmation',
    label: 'Open support access revoke confirmation',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.viewTraceReference,
    governedToolId: userAdminCapabilities.viewTraceReference,
    browserToolId: 'action-open-useradmin-support-access-revoke-confirmation',
    inputSchemaRef: 'schema.user-admin.support-access-revoke.open.v1',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-support-access-revoke-confirmation', displayText: 'Open support access revoke confirmation' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-support-access-revoke-confirmation', openPlacement: 'inline' },
    audit: { eventType: 'SupportAccessRevokeConfirmationDisplayed', traceRequired: true }
  },
  openIdentityExceptionReview: {
    actionId: 'action-open-useradmin-identity-exception-review',
    label: 'Open identity exception review',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-open-useradmin-identity-exception-review',
    inputSchemaRef: 'schema.user-admin.identity-exception.open.v1',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-identity-exception-review', displayText: 'Open identity exception review' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-identity-exception-review', openPlacement: 'inline' },
    audit: { eventType: 'IdentityExceptionReviewDisplayed', traceRequired: true }
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
  openInvitationCreate: {
    actionId: 'action-open-useradmin-invitation-create',
    label: 'Invite user',
    intent: 'surface-request',
    capabilityId: userAdminCapabilities.sendInvitation,
    governedToolId: userAdminCapabilities.sendInvitation,
    browserToolId: 'action-open-useradmin-invitation-create',
    inputSchemaRef: 'schema.user-admin.invitation-create.open.v1',
    shellRequest: { requestType: 'show_surface', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-invitation-create', displayText: 'Show Invite User' },
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-invitation-create', openPlacement: 'inline' },
    audit: { eventType: 'InvitationCreateFormDisplayed', traceRequired: true }
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-user-detail', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-invitation-detail', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-invitation-detail', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-invitation-detail', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-user-detail', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-users', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-users', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-user-detail', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-user-detail', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-user-detail', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-user-detail', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-users', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-user-detail', openPlacement: 'inline' },
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
  grantUserAdminSupportAccess: {
    actionId: 'action-useradmin-grant-support-access',
    label: 'Grant support access',
    intent: 'command',
    capabilityId: userAdminCapabilities.viewTraceReference,
    governedToolId: userAdminCapabilities.viewTraceReference,
    browserToolId: 'action-useradmin-grant-support-access',
    inputSchemaRef: 'schema.user-admin.support-access.grant.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-user-detail', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminSupportAccessGranted', traceRequired: true }
  },
  revokeUserAdminSupportAccess: {
    actionId: 'action-useradmin-revoke-support-access',
    label: 'Revoke support access',
    intent: 'command',
    capabilityId: userAdminCapabilities.viewTraceReference,
    governedToolId: userAdminCapabilities.viewTraceReference,
    browserToolId: 'action-useradmin-revoke-support-access',
    inputSchemaRef: 'schema.user-admin.support-access.revoke.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-user-detail', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminSupportAccessRevoked', traceRequired: true }
  },
  extendUserAdminSupportAccess: {
    actionId: 'action-useradmin-extend-support-access',
    label: 'Extend support access',
    intent: 'command',
    capabilityId: userAdminCapabilities.viewTraceReference,
    governedToolId: userAdminCapabilities.viewTraceReference,
    browserToolId: 'action-useradmin-extend-support-access',
    inputSchemaRef: 'schema.user-admin.support-access.extend.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-user-detail', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminSupportAccessExtended', traceRequired: true }
  },
  requestUserAdminIdentityRelink: {
    actionId: 'action-useradmin-request-identity-relink',
    label: 'Request identity recovery',
    intent: 'proposal',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-useradmin-request-identity-relink',
    inputSchemaRef: 'schema.user-admin.identity-relink.request.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-identity-exception-review', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminIdentityRelinkRequested', traceRequired: true }
  },
  readUserAdminIdentityRelink: {
    actionId: 'action-useradmin-read-identity-relink',
    label: 'Refresh identity recovery status',
    intent: 'read',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-useradmin-read-identity-relink',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-identity-exception-review', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminIdentityRelinkRead', traceRequired: true }
  },
  approveUserAdminIdentityRelink: {
    actionId: 'action-useradmin-approve-identity-relink',
    label: 'Approve identity recovery',
    intent: 'approval',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-useradmin-approve-identity-relink',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-identity-exception-review', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminIdentityRelinkApproved', traceRequired: true }
  },
  denyUserAdminIdentityRelink: {
    actionId: 'action-useradmin-deny-identity-relink',
    label: 'Deny identity recovery',
    intent: 'approval',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-useradmin-deny-identity-relink',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-identity-exception-review', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminIdentityRelinkDenied', traceRequired: true }
  },
  completeUserAdminIdentityRelink: {
    actionId: 'action-useradmin-complete-identity-relink',
    label: 'Complete identity recovery',
    intent: 'approval',
    capabilityId: userAdminCapabilities.updateMemberStatus,
    governedToolId: userAdminCapabilities.updateMemberStatus,
    browserToolId: 'action-useradmin-complete-identity-relink',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'server-issued' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-user-detail', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminIdentityRelinkCompleted', traceRequired: true }
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-access-review-task', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-access-review-task', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-access-review-task', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-access-review-task', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-access-review-task', openPlacement: 'inline' },
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
const agentDefinitionsManageCapability = 'agent.definitions.manage';
const agentDefinitionReadCapability = 'agent_admin.get_definition';
const agentPromptReadCapability = 'agent_admin.get_prompt_version';
const agentSkillReadCapability = 'agent_admin.get_skill_version';
const agentReferenceReadCapability = 'agent_admin.get_reference_version';
const agentManifestReadCapability = 'agent_admin.get_manifest';
const agentToolBoundaryReadCapability = 'agent_admin.get_tool_boundary';
const agentPromptsCapability = 'agent_admin.draft_behavior_change';
const agentSubmitReviewCapability = 'agent_admin.submit_behavior_change_for_review';
const agentSkillsCapability = 'agent_admin.approve_behavior_change';
const agentRejectCapability = 'agent_admin.reject_behavior_change';
const agentActivateCapability = 'agent_admin.activate_behavior_change';
const agentDeactivateCapability = 'agent_admin.deactivate_behavior_version';
const agentCancelCapability = 'agent_admin.cancel_behavior_change';
const agentRollbackCapability = 'agent_admin.rollback_behavior_change';
const agentToolBoundariesCapability = 'agent_admin.simulate_tool_boundary';
const agentModelsReadCapability = 'agent_admin.get_model_ref';
const agentModelsManageCapability = 'agent_admin.activate_behavior_change';
const agentSeedReadCapability = 'agent_admin.list_seed_material';
const agentSeedImportCapability = 'agent_admin.reseed_missing_defaults';
const agentRuntimeTestCapability = 'agent_admin.draft_behavior_change';
const agentPromptRiskStartCapability = 'agent_admin.prompt_risk_review.start';
const agentPromptRiskReadCapability = 'agent_admin.prompt_risk_review.read';
const agentPromptRiskCancelCapability = 'agent_admin.prompt_risk_review.cancel';
const agentPromptRiskAcceptCapability = 'agent_admin.prompt_risk_review.accept_result';
const agentPromptRiskRejectCapability = 'agent_admin.prompt_risk_review.reject_result';

export const agentAdminSurfaceActions = {
  showBlank: {
    actionId: 'action-agent-admin-show-blank',
    label: 'Clear workstream',
    intent: 'surface-request',
    capabilityId: agentDefinitionsCapability,
    governedToolId: 'list-agent-doc-agents',
    browserToolId: 'action-agent-admin-show-blank',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-blank', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminBlankDisplayed', traceRequired: true }
  },
  showDashboard: {
    actionId: 'action-agent-admin-show-dashboard',
    label: 'Show dashboard',
    intent: 'read',
    capabilityId: agentDefinitionsCapability,
    governedToolId: 'list-agent-doc-agents',
    browserToolId: 'action-agent-admin-show-dashboard',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-dashboard', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminDashboardDisplayed', traceRequired: true }
  },
  showAgents: {
    actionId: 'action-agent-admin-show-agents',
    label: 'Show agents',
    intent: 'read',
    capabilityId: agentDefinitionsCapability,
    governedToolId: 'list-agent-doc-agents',
    browserToolId: 'action-agent-admin-show-agents',
    inputSchemaRef: 'schema.agent-admin.agent-list.filter.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-agent-list', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminAgentListDisplayed', traceRequired: true }
  },
  listAgents: {
    actionId: 'action-agent-admin-list-agents',
    label: 'Refresh agent list',
    intent: 'read',
    capabilityId: agentDefinitionsCapability,
    governedToolId: 'list-agent-doc-agents',
    browserToolId: 'action-agent-admin-list-agents',
    inputSchemaRef: 'schema.agent-admin.agent-list.filter.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-agent-list', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminAgentListDisplayed', traceRequired: true }
  },
  openAgentDetailDoc: {
    actionId: 'action-agent-admin-open-agent-detail',
    label: 'Open agent',
    intent: 'read',
    capabilityId: agentDefinitionReadCapability,
    governedToolId: 'read-agent-doc-agent',
    browserToolId: 'action-agent-admin-open-agent-detail',
    inputSchemaRef: 'schema.agent-admin.agent-detail.open.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-agent-detail', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminAgentDetailDisplayed', traceRequired: true }
  },
  saveAgentProfile: {
    actionId: 'action-agent-admin-save-agent-profile',
    label: 'Save agent name/purpose',
    intent: 'command',
    capabilityId: 'saas_owner.admin.manage',
    governedToolId: 'update-agent-name-purpose',
    browserToolId: 'action-agent-admin-save-agent-profile',
    inputSchemaRef: 'schema.agent-admin.agent-profile.update.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-agent-detail', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminAgentProfileUpdated', traceRequired: true }
  },
  openPromptDoc: {
    actionId: 'action-agent-admin-open-prompt-doc',
    label: 'Open prompt doc',
    intent: 'read',
    capabilityId: agentPromptReadCapability,
    governedToolId: 'read-agent-prompt-doc',
    browserToolId: 'action-agent-admin-open-prompt-doc',
    inputSchemaRef: 'schema.agent-admin.doc.read.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-prompt-doc', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminPromptDocRead', traceRequired: true }
  },
  openSkillDoc: {
    actionId: 'action-agent-admin-open-skill-doc',
    label: 'Open skill doc',
    intent: 'read',
    capabilityId: agentSkillReadCapability,
    governedToolId: 'read-agent-skill-doc',
    browserToolId: 'action-agent-admin-open-skill-doc',
    inputSchemaRef: 'schema.agent-admin.doc.read.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-skill-doc', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminSkillDocRead', traceRequired: true }
  },
  openReferenceDoc: {
    actionId: 'action-agent-admin-open-reference-doc',
    label: 'Open reference doc',
    intent: 'read',
    capabilityId: agentReferenceReadCapability,
    governedToolId: 'read-agent-skill-reference-doc',
    browserToolId: 'action-agent-admin-open-reference-doc',
    inputSchemaRef: 'schema.agent-admin.doc.read.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-skill-reference-doc', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminReferenceDocRead', traceRequired: true }
  },
  docEditStart: {
    actionId: 'action-agent-doc-edit-start',
    label: 'Improve behavior',
    intent: 'workflow',
    capabilityId: agentPromptsCapability,
    governedToolId: 'draft-agent-doc-edit',
    browserToolId: 'action-agent-doc-edit-start',
    inputSchemaRef: 'schema.agent-admin.doc-edit.start.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-edit-session', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminDocEditStarted', traceRequired: true }
  },
  docEditRevise: {
    actionId: 'action-agent-doc-edit-revise',
    label: 'Revise proposal',
    intent: 'workflow',
    capabilityId: agentPromptsCapability,
    governedToolId: 'revise-agent-doc-edit',
    browserToolId: 'action-agent-doc-edit-revise',
    inputSchemaRef: 'schema.agent-admin.doc-edit.revise.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-edit-session', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminDocEditRevised', traceRequired: true }
  },
  docEditSave: {
    actionId: 'action-agent-doc-edit-save',
    label: 'Save',
    intent: 'command',
    capabilityId: agentActivateCapability,
    governedToolId: 'save-agent-doc-edit',
    browserToolId: 'action-agent-doc-edit-save',
    inputSchemaRef: 'schema.agent-admin.doc-edit.save.v1',
    requiresConfirmation: true,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-prompt-doc', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminDocEditSaved', traceRequired: true }
  },
  docEditCancel: {
    actionId: 'action-agent-doc-edit-cancel',
    label: 'Cancel',
    intent: 'command',
    capabilityId: agentCancelCapability,
    governedToolId: 'cancel-agent-doc-edit',
    browserToolId: 'action-agent-doc-edit-cancel',
    inputSchemaRef: 'schema.agent-admin.doc-edit.cancel.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-edit-session', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminDocEditCancelled', traceRequired: true }
  },
  versionHistory: {
    actionId: 'action-agent-doc-version-history',
    label: 'Version history',
    intent: 'read',
    capabilityId: agentPromptReadCapability,
    governedToolId: 'read-agent-doc-version-history',
    browserToolId: 'action-agent-doc-version-history',
    inputSchemaRef: 'schema.agent-admin.doc-version.history.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-version-history', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminDocVersionHistoryRead', traceRequired: true }
  },
  versionDiff: {
    actionId: 'action-agent-doc-version-diff',
    label: 'Show diff',
    intent: 'read',
    capabilityId: agentPromptReadCapability,
    governedToolId: 'read-agent-doc-version-diff',
    browserToolId: 'action-agent-doc-version-diff',
    inputSchemaRef: 'schema.agent-admin.doc-version.diff.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-version-diff', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminDocVersionDiffRead', traceRequired: true }
  },
  restoreVersion: {
    actionId: 'action-agent-doc-version-restore',
    label: 'Restore this version',
    intent: 'command',
    capabilityId: agentRollbackCapability,
    governedToolId: 'restore-agent-doc-version',
    browserToolId: 'action-agent-doc-version-restore',
    inputSchemaRef: 'schema.agent-admin.doc-version.restore.v1',
    requiresConfirmation: true,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-prompt-doc', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminDocVersionRestored', traceRequired: true }
  },
  openCreateSkill: {
    actionId: 'action-agent-admin-open-create-skill',
    label: 'Create skill',
    intent: 'surface-request',
    capabilityId: 'saas_owner.admin.manage',
    governedToolId: 'create-agent-skill',
    browserToolId: 'action-agent-admin-open-create-skill',
    inputSchemaRef: 'schema.agent-admin.skill.create.open.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-create-skill', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminCreateSkillOpened', traceRequired: true }
  },
  createSkill: {
    actionId: 'action-agent-admin-create-skill',
    label: 'Create skill',
    intent: 'command',
    capabilityId: 'saas_owner.admin.manage',
    governedToolId: 'create-agent-skill',
    browserToolId: 'action-agent-admin-create-skill',
    inputSchemaRef: 'schema.agent-admin.skill.create.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-skill-doc', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminSkillCreated', traceRequired: true }
  },
  openDeleteSkill: {
    actionId: 'action-agent-admin-open-delete-skill',
    label: 'Delete skill',
    intent: 'surface-request',
    capabilityId: 'saas_owner.admin.manage',
    governedToolId: 'delete-agent-skill',
    browserToolId: 'action-agent-admin-open-delete-skill',
    inputSchemaRef: 'schema.agent-admin.skill.delete.open.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-delete-skill-confirmation', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminDeleteSkillOpened', traceRequired: true }
  },
  deleteSkill: {
    actionId: 'action-agent-admin-delete-skill',
    label: 'Delete skill permanently',
    intent: 'command',
    capabilityId: 'saas_owner.admin.manage',
    governedToolId: 'delete-agent-skill',
    browserToolId: 'action-agent-admin-delete-skill',
    inputSchemaRef: 'schema.agent-admin.skill.delete.v1',
    requiresConfirmation: true,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-agent-detail', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminSkillDeleted', traceRequired: true }
  },
  openCreateReferenceDoc: {
    actionId: 'action-agent-admin-open-create-reference-doc',
    label: 'Create reference doc',
    intent: 'surface-request',
    capabilityId: 'saas_owner.admin.manage',
    governedToolId: 'create-agent-skill-reference-doc',
    browserToolId: 'action-agent-admin-open-create-reference-doc',
    inputSchemaRef: 'schema.agent-admin.reference.create.open.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-create-reference-doc', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminCreateReferenceDocOpened', traceRequired: true }
  },
  createReferenceDoc: {
    actionId: 'action-agent-admin-create-reference-doc',
    label: 'Create reference doc',
    intent: 'command',
    capabilityId: 'saas_owner.admin.manage',
    governedToolId: 'create-agent-skill-reference-doc',
    browserToolId: 'action-agent-admin-create-reference-doc',
    inputSchemaRef: 'schema.agent-admin.reference.create.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-skill-reference-doc', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminReferenceDocCreated', traceRequired: true }
  },
  openDeleteReferenceDoc: {
    actionId: 'action-agent-admin-open-delete-reference-doc',
    label: 'Delete reference doc',
    intent: 'surface-request',
    capabilityId: 'saas_owner.admin.manage',
    governedToolId: 'delete-agent-skill-reference-doc',
    browserToolId: 'action-agent-admin-open-delete-reference-doc',
    inputSchemaRef: 'schema.agent-admin.reference.delete.open.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-delete-reference-doc-confirmation', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminDeleteReferenceDocOpened', traceRequired: true }
  },
  deleteReferenceDoc: {
    actionId: 'action-agent-admin-delete-reference-doc',
    label: 'Delete reference doc permanently',
    intent: 'command',
    capabilityId: 'saas_owner.admin.manage',
    governedToolId: 'delete-agent-skill-reference-doc',
    browserToolId: 'action-agent-admin-delete-reference-doc',
    inputSchemaRef: 'schema.agent-admin.reference.delete.v1',
    requiresConfirmation: true,
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-agent-detail', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminReferenceDocDeleted', traceRequired: true }
  },
  openRuntimeTraces: {
    actionId: 'action-agent-admin-open-runtime-traces',
    label: 'Runtime reads',
    intent: 'read',
    capabilityId: agentDefinitionReadCapability,
    governedToolId: 'read-agent-doc-runtime-traces',
    browserToolId: 'action-agent-admin-open-runtime-traces',
    inputSchemaRef: 'schema.agent-admin.runtime-traces.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-runtime-traces', openPlacement: 'inline' },
    audit: { eventType: 'AgentAdminRuntimeTracesRead', traceRequired: true }
  },
  displayDashboard: {
    actionId: 'action-display-agent-admin-dashboard',
    label: 'Open Agent Admin dashboard',
    intent: 'surface-request',
    capabilityId: agentDefinitionsCapability,
    governedToolId: agentDefinitionsCapability,
    browserToolId: 'action-display-agent-admin-dashboard',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-dashboard', openPlacement: 'inline' },
    shellRequest: { requestType: 'open_workstream', targetFunctionalAgentId: 'agent-admin-agent', targetSurfaceId: 'surface-agent-admin-dashboard', displayText: 'Open Agent Admin dashboard' },
    audit: { eventType: 'AgentAdminDashboardDisplayed', traceRequired: true }
  },
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
  activateAgentDefinition: {
    actionId: 'action-activate-agent-definition',
    label: 'Activate AgentDefinition',
    intent: 'command',
    capabilityId: agentDefinitionsManageCapability,
    governedToolId: agentDefinitionsManageCapability,
    browserToolId: 'action-activate-agent-definition',
    inputSchemaRef: 'schema.agent-definition.lifecycle.activate.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-activation-confirmation', openPlacement: 'inline' },
    audit: { eventType: 'AgentDefinitionActivationReviewRequested', traceRequired: true }
  },
  deactivateAgentDefinition: {
    actionId: 'action-deactivate-agent-definition',
    label: 'Deactivate AgentDefinition',
    intent: 'command',
    capabilityId: agentDefinitionsManageCapability,
    governedToolId: agentDefinitionsManageCapability,
    browserToolId: 'action-deactivate-agent-definition',
    inputSchemaRef: 'schema.agent-definition.lifecycle.deactivate.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-deactivation-confirmation', openPlacement: 'inline' },
    audit: { eventType: 'AgentDefinitionDeactivationReviewRequested', traceRequired: true }
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
  promptGovernanceRefresh: {
    actionId: 'action-agent-prompt-governance-refresh',
    label: 'Refresh prompt governance',
    intent: 'read',
    capabilityId: agentPromptReadCapability,
    governedToolId: agentPromptReadCapability,
    browserToolId: 'action-agent-prompt-governance-refresh',
    inputSchemaRef: 'schema.agent-admin.prompt-governance.refresh.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-prompt-governance', openPlacement: 'inline' },
    audit: { eventType: 'AgentPromptGovernanceRefreshed', traceRequired: true }
  },
  promptGovernanceSimulate: {
    actionId: 'action-agent-prompt-governance-simulate',
    label: 'Run no-side-effect prompt governance simulation',
    intent: 'workflow',
    capabilityId: agentRuntimeTestCapability,
    governedToolId: agentRuntimeTestCapability,
    browserToolId: 'action-agent-prompt-governance-simulate',
    inputSchemaRef: 'schema.agent-admin.prompt-governance.simulate.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-test-console', openPlacement: 'inline' },
    audit: { eventType: 'AgentPromptGovernanceSimulationRequested', traceRequired: true }
  },
  promptGovernanceSubmitReview: {
    actionId: 'action-agent-prompt-governance-submit-review',
    label: 'Submit prompt governance review',
    intent: 'proposal',
    capabilityId: agentSubmitReviewCapability,
    governedToolId: agentSubmitReviewCapability,
    browserToolId: 'action-agent-prompt-governance-submit-review',
    inputSchemaRef: 'schema.agent-admin.prompt-governance.submit-review.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentPromptGovernanceSubmitted', traceRequired: true }
  },
  promptGovernanceApprove: {
    actionId: 'action-agent-prompt-governance-approve',
    label: 'Approve prompt governance review',
    intent: 'approval',
    capabilityId: agentSkillsCapability,
    governedToolId: agentSkillsCapability,
    browserToolId: 'action-agent-prompt-governance-approve',
    inputSchemaRef: 'schema.agent-admin.prompt-governance.approve.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentPromptGovernanceApproved', traceRequired: true }
  },
  promptGovernanceReject: {
    actionId: 'action-agent-prompt-governance-reject',
    label: 'Reject prompt governance review',
    intent: 'approval',
    capabilityId: agentRejectCapability,
    governedToolId: agentRejectCapability,
    browserToolId: 'action-agent-prompt-governance-reject',
    inputSchemaRef: 'schema.agent-admin.prompt-governance.reject.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentPromptGovernanceRejected', traceRequired: true }
  },
  promptGovernanceOpenRiskReview: {
    actionId: 'action-agent-prompt-governance-open-risk-review',
    label: 'Open prompt-risk review',
    intent: 'read',
    capabilityId: agentPromptRiskReadCapability,
    governedToolId: agentPromptRiskReadCapability,
    browserToolId: 'action-agent-prompt-governance-open-risk-review',
    inputSchemaRef: 'schema.agent-admin.prompt-governance.open-risk-review.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-prompt-risk-review', openPlacement: 'inline' },
    audit: { eventType: 'AgentPromptGovernanceRiskReviewOpened', traceRequired: true }
  },
  promptGovernanceOpenTrace: {
    actionId: 'action-agent-prompt-governance-open-trace',
    label: 'Open prompt governance trace',
    intent: 'trace',
    capabilityId: 'audit.trace.read',
    governedToolId: 'audit.trace.read',
    browserToolId: 'action-agent-prompt-governance-open-trace',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-trace', openPlacement: 'deep-link' },
    audit: { eventType: 'AgentPromptGovernanceTraceOpened', traceRequired: true }
  },
  promptGovernanceBackToDetail: {
    actionId: 'action-agent-prompt-governance-back-to-detail',
    label: 'Back to agent detail',
    intent: 'read',
    capabilityId: agentDefinitionReadCapability,
    governedToolId: agentDefinitionReadCapability,
    browserToolId: 'action-agent-prompt-governance-back-to-detail',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-detail', openPlacement: 'inline' },
    audit: { eventType: 'AgentDefinitionDetailDisplayed', traceRequired: true }
  },
  skillManifestRefresh: {
    actionId: 'action-agent-skill-manifest-refresh',
    label: 'Refresh skill manifest diff',
    intent: 'read',
    capabilityId: agentManifestReadCapability,
    governedToolId: agentManifestReadCapability,
    browserToolId: 'action-agent-skill-manifest-refresh',
    inputSchemaRef: 'schema.agent-admin.skill-manifest.refresh.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-skill-manifest-diff', openPlacement: 'inline' },
    audit: { eventType: 'AgentSkillManifestDiffRefreshed', traceRequired: true }
  },
  skillManifestSimulate: {
    actionId: 'action-agent-skill-manifest-simulate',
    label: 'Run no-side-effect manifest simulation',
    intent: 'workflow',
    capabilityId: agentRuntimeTestCapability,
    governedToolId: agentRuntimeTestCapability,
    browserToolId: 'action-agent-skill-manifest-simulate',
    inputSchemaRef: 'schema.agent-admin.skill-manifest.simulate.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-test-console', openPlacement: 'inline' },
    audit: { eventType: 'AgentSkillManifestSimulationRequested', traceRequired: true }
  },
  skillManifestSubmitReview: {
    actionId: 'action-agent-skill-manifest-submit-review',
    label: 'Submit manifest diff for review',
    intent: 'proposal',
    capabilityId: agentSubmitReviewCapability,
    governedToolId: agentSubmitReviewCapability,
    browserToolId: 'action-agent-skill-manifest-submit-review',
    inputSchemaRef: 'schema.agent-admin.skill-manifest.submit-review.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentSkillManifestSubmitted', traceRequired: true }
  },
  skillManifestApprove: {
    actionId: 'action-agent-skill-manifest-approve',
    label: 'Approve manifest diff review',
    intent: 'approval',
    capabilityId: agentSkillsCapability,
    governedToolId: agentSkillsCapability,
    browserToolId: 'action-agent-skill-manifest-approve',
    inputSchemaRef: 'schema.agent-admin.skill-manifest.approve.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentSkillManifestApproved', traceRequired: true }
  },
  skillManifestReject: {
    actionId: 'action-agent-skill-manifest-reject',
    label: 'Reject manifest diff review',
    intent: 'approval',
    capabilityId: agentRejectCapability,
    governedToolId: agentRejectCapability,
    browserToolId: 'action-agent-skill-manifest-reject',
    inputSchemaRef: 'schema.agent-admin.skill-manifest.reject.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentSkillManifestRejected', traceRequired: true }
  },
  skillManifestOpenToolBoundary: {
    actionId: 'action-agent-skill-manifest-open-tool-boundary',
    label: 'Open related tool-boundary review',
    intent: 'read',
    capabilityId: agentToolBoundariesCapability,
    governedToolId: agentToolBoundariesCapability,
    browserToolId: 'action-agent-skill-manifest-open-tool-boundary',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-tool-boundary-diff', openPlacement: 'inline' },
    audit: { eventType: 'AgentSkillManifestToolBoundaryOpened', traceRequired: true }
  },
  skillManifestOpenModelRefs: {
    actionId: 'action-agent-skill-manifest-open-model-refs',
    label: 'Open related model references',
    intent: 'read',
    capabilityId: agentModelsReadCapability,
    governedToolId: agentModelsReadCapability,
    browserToolId: 'action-agent-skill-manifest-open-model-refs',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-model-refs', openPlacement: 'inline' },
    audit: { eventType: 'AgentSkillManifestModelRefsOpened', traceRequired: true }
  },
  skillManifestOpenTrace: {
    actionId: 'action-agent-skill-manifest-open-trace',
    label: 'Open manifest diff trace',
    intent: 'trace',
    capabilityId: 'audit.trace.read',
    governedToolId: 'audit.trace.read',
    browserToolId: 'action-agent-skill-manifest-open-trace',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-trace', openPlacement: 'deep-link' },
    audit: { eventType: 'AgentSkillManifestTraceOpened', traceRequired: true }
  },
  skillManifestBackToDetail: {
    actionId: 'action-agent-skill-manifest-back-to-detail',
    label: 'Back to agent detail',
    intent: 'read',
    capabilityId: agentDefinitionReadCapability,
    governedToolId: agentDefinitionReadCapability,
    browserToolId: 'action-agent-skill-manifest-back-to-detail',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-detail', openPlacement: 'inline' },
    audit: { eventType: 'AgentDefinitionDetailDisplayed', traceRequired: true }
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
    resultSurface: { updateSurfaceId: 'surface-agent-activation-confirmation', openPlacement: 'inline' },
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
    resultSurface: { updateSurfaceId: 'surface-agent-rollback-confirmation', openPlacement: 'inline' },
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
  toolBoundaryRefresh: {
    actionId: 'action-agent-tool-boundary-refresh',
    label: 'Refresh tool-boundary review',
    intent: 'read',
    capabilityId: agentToolBoundaryReadCapability,
    governedToolId: agentToolBoundaryReadCapability,
    browserToolId: 'action-agent-tool-boundary-refresh',
    inputSchemaRef: 'schema.agent-admin.tool-boundary.refresh.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-tool-boundary-diff', openPlacement: 'inline' },
    audit: { eventType: 'AgentToolBoundaryReviewRead', traceRequired: true }
  },
  toolBoundarySimulate: {
    actionId: 'action-agent-tool-boundary-simulate',
    label: 'Run no-side-effect tool-boundary simulation',
    intent: 'workflow',
    capabilityId: agentToolBoundariesCapability,
    governedToolId: agentToolBoundariesCapability,
    browserToolId: 'action-agent-tool-boundary-simulate',
    inputSchemaRef: 'schema.agent-admin.tool-boundary.simulate.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-test-console', openPlacement: 'inline' },
    audit: { eventType: 'AgentToolBoundarySimulationOpened', traceRequired: true }
  },
  toolBoundarySubmitReview: {
    actionId: 'action-agent-tool-boundary-submit-review',
    label: 'Submit tool-boundary change for review',
    intent: 'proposal',
    capabilityId: agentSubmitReviewCapability,
    governedToolId: agentSubmitReviewCapability,
    browserToolId: 'action-agent-tool-boundary-submit-review',
    inputSchemaRef: 'schema.agent-admin.tool-boundary.submit-review.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentToolBoundaryReviewSubmitted', traceRequired: true }
  },
  toolBoundaryApprove: {
    actionId: 'action-agent-tool-boundary-approve',
    label: 'Approve tool-boundary review',
    intent: 'approval',
    capabilityId: agentSkillsCapability,
    governedToolId: agentSkillsCapability,
    browserToolId: 'action-agent-tool-boundary-approve',
    inputSchemaRef: 'schema.agent-admin.tool-boundary.approve.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentToolBoundaryReviewApproved', traceRequired: true }
  },
  toolBoundaryReject: {
    actionId: 'action-agent-tool-boundary-reject',
    label: 'Reject tool-boundary review',
    intent: 'approval',
    capabilityId: agentRejectCapability,
    governedToolId: agentRejectCapability,
    browserToolId: 'action-agent-tool-boundary-reject',
    inputSchemaRef: 'schema.agent-admin.tool-boundary.reject.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentToolBoundaryReviewRejected', traceRequired: true }
  },
  toolBoundaryOpenModelRefs: {
    actionId: 'action-agent-tool-boundary-open-model-refs',
    label: 'Open related model references',
    intent: 'read',
    capabilityId: agentModelsReadCapability,
    governedToolId: agentModelsReadCapability,
    browserToolId: 'action-agent-tool-boundary-open-model-refs',
    inputSchemaRef: 'schema.agent-admin.tool-boundary.open-model-refs.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-model-refs', openPlacement: 'inline' },
    audit: { eventType: 'AgentToolBoundaryModelRefsOpened', traceRequired: true }
  },
  toolBoundaryOpenTrace: {
    actionId: 'action-agent-tool-boundary-open-trace',
    label: 'Open tool-boundary review trace',
    intent: 'trace',
    capabilityId: 'audit.trace.read',
    governedToolId: 'audit.trace.read',
    browserToolId: 'action-agent-tool-boundary-open-trace',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-trace', openPlacement: 'deep-link' },
    audit: { eventType: 'AgentToolBoundaryTraceOpened', traceRequired: true }
  },
  toolBoundaryBackToDetail: {
    actionId: 'action-agent-tool-boundary-back-to-detail',
    label: 'Back to agent detail',
    intent: 'read',
    capabilityId: agentDefinitionReadCapability,
    governedToolId: agentDefinitionReadCapability,
    browserToolId: 'action-agent-tool-boundary-back-to-detail',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-detail', openPlacement: 'inline' },
    audit: { eventType: 'AgentDefinitionDetailDisplayed', traceRequired: true }
  },
  modelRefsRefresh: {
    actionId: 'action-agent-model-refs-refresh',
    label: 'Refresh model-reference review',
    intent: 'read',
    capabilityId: agentModelsReadCapability,
    governedToolId: agentModelsReadCapability,
    browserToolId: 'action-agent-model-refs-refresh',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-model-refs', openPlacement: 'inline' },
    audit: { eventType: 'AgentModelRefsRefreshed', traceRequired: true }
  },
  modelRefsRunTest: {
    actionId: 'action-agent-model-refs-run-test',
    label: 'Run no-side-effect model readiness test',
    intent: 'workflow',
    capabilityId: agentRuntimeTestCapability,
    governedToolId: agentRuntimeTestCapability,
    browserToolId: 'action-agent-model-refs-run-test',
    disabled: { reasonCode: 'blocked_provider_or_runtime', message: 'Provider/runtime readiness is not configured; no fixture/model-less success is claimed.' },
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-test-console', openPlacement: 'inline' },
    audit: { eventType: 'AgentModelRefsReadinessTestRequested', traceRequired: true }
  },
  modelRefsSubmitReview: {
    actionId: 'action-agent-model-refs-submit-review',
    label: 'Submit model-reference change for review',
    intent: 'proposal',
    capabilityId: agentSubmitReviewCapability,
    governedToolId: agentSubmitReviewCapability,
    browserToolId: 'action-agent-model-refs-submit-review',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentModelRefsReviewSubmitted', traceRequired: true }
  },
  modelRefsApprove: {
    actionId: 'action-agent-model-refs-approve',
    label: 'Approve model-reference review',
    intent: 'approval',
    capabilityId: agentSkillsCapability,
    governedToolId: agentSkillsCapability,
    browserToolId: 'action-agent-model-refs-approve',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentModelRefsApproved', traceRequired: true }
  },
  modelRefsReject: {
    actionId: 'action-agent-model-refs-reject',
    label: 'Reject model-reference review',
    intent: 'approval',
    capabilityId: agentRejectCapability,
    governedToolId: agentRejectCapability,
    browserToolId: 'action-agent-model-refs-reject',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-behavior-proposal', openPlacement: 'inline' },
    audit: { eventType: 'AgentModelRefsRejected', traceRequired: true }
  },
  modelRefsOpenPromptGovernance: {
    actionId: 'action-agent-model-refs-open-prompt-governance',
    label: 'Open related prompt governance',
    intent: 'read',
    capabilityId: agentPromptReadCapability,
    governedToolId: agentPromptReadCapability,
    browserToolId: 'action-agent-model-refs-open-prompt-governance',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-prompt-governance', openPlacement: 'inline' },
    audit: { eventType: 'AgentModelRefsPromptGovernanceOpened', traceRequired: true }
  },
  modelRefsOpenToolBoundary: {
    actionId: 'action-agent-model-refs-open-tool-boundary',
    label: 'Open related tool-boundary review',
    intent: 'read',
    capabilityId: agentToolBoundariesCapability,
    governedToolId: agentToolBoundariesCapability,
    browserToolId: 'action-agent-model-refs-open-tool-boundary',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-tool-boundary-diff', openPlacement: 'inline' },
    audit: { eventType: 'AgentModelRefsToolBoundaryOpened', traceRequired: true }
  },
  modelRefsOpenTrace: {
    actionId: 'action-agent-model-refs-open-trace',
    label: 'Open model-reference trace',
    intent: 'trace',
    capabilityId: 'audit.trace.read',
    governedToolId: 'audit.trace.read',
    browserToolId: 'action-agent-model-refs-open-trace',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-trace', openPlacement: 'deep-link' },
    audit: { eventType: 'AgentModelRefsTraceOpened', traceRequired: true }
  },
  modelRefsBackToDetail: {
    actionId: 'action-agent-model-refs-back-to-detail',
    label: 'Back to agent detail',
    intent: 'read',
    capabilityId: agentDefinitionReadCapability,
    governedToolId: agentDefinitionReadCapability,
    browserToolId: 'action-agent-model-refs-back-to-detail',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-detail', openPlacement: 'inline' },
    audit: { eventType: 'AgentDefinitionDetailDisplayed', traceRequired: true }
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
  importSeedDefaults: {
    actionId: 'action-import-agent-seed-defaults',
    label: 'Import missing seed defaults',
    intent: 'workflow',
    capabilityId: agentSeedImportCapability,
    governedToolId: agentSeedImportCapability,
    browserToolId: 'action-import-agent-seed-defaults',
    inputSchemaRef: 'schema.agent-seed.import-defaults.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-agent-seed-material', openPlacement: 'inline' },
    audit: { eventType: 'AgentSeedDefaultsImported', traceRequired: true }
  },
  seedMaterialOpenAgentDetail: {
    actionId: 'action-agent-seed-material-open-agent-detail',
    label: 'Open target agent detail',
    intent: 'read',
    capabilityId: agentDefinitionReadCapability,
    governedToolId: agentDefinitionReadCapability,
    browserToolId: 'action-agent-seed-material-open-agent-detail',
    inputSchemaRef: 'schema.agent-admin.seed-material.open-agent-detail.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-agent-admin-detail', openPlacement: 'inline' },
    audit: { eventType: 'AgentDefinitionDetailDisplayed', traceRequired: true }
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
    reusableByFunctionalAgentIds: ownerFunctionalAgentId === 'agent-admin-agent' ? ['audit-trace-agent', 'governance-policy-agent'] : ['audit-trace-agent'],
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
    resultSurface: { updateSurfaceId: 'surface-user-admin-users', openPlacement: 'deep-link' },
    shellRequest: { requestType: 'open_workstream', targetFunctionalAgentId: 'user-admin-agent', targetSurfaceId: 'surface-user-admin-users', displayText: 'Open User Admin' },
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
    resultSurface: { updateSurfaceId: 'surface-agent-admin-dashboard', openPlacement: 'deep-link' },
    shellRequest: { requestType: 'open_workstream', targetFunctionalAgentId: 'agent-admin-agent', targetSurfaceId: 'surface-agent-admin-dashboard', displayText: 'Open Agent Admin' },
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
    resultSurface: { updateSurfaceId: 'surface-audit-trace-timeline', openPlacement: 'inline' },
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
  'my-account-agent',
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
      { workstreamId: 'user-admin-agent', label: 'Review users and invitations', allowed: true, capabilityIds: ['user_admin.view_overview'], traceId: 'trace-my-account-next-user-admin' },
      { workstreamId: 'agent-admin-agent', label: 'Review governed agent readiness', allowed: true, capabilityIds: ['agent_admin.list_definitions'], traceId: 'trace-my-account-next-agent-admin' },
      { workstreamId: 'audit-trace-agent', label: 'Open My Account traces', allowed: true, capabilityIds: [myAccountCapabilities.viewOwnTraceRefs], traceId: 'trace-my-account-next-audit' },
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
  'my-account-agent',
  {
    recordId: 'acct-admin-profile',
    recordLabel: 'Tenant Admin · admin@example.test',
    recordKind: 'profile',
    summary: 'Signed-in user profile. Editable fields are advisory UI hints; backend validation enforces allowed self-service fields and tenant isolation.',
    fields: [
      { fieldId: 'displayName', label: 'Display name', value: 'Tenant Admin', editable: true, inputType: 'text' },
      { fieldId: 'email', label: 'Email', value: 'admin@example.test', editable: false, inputType: 'email', disabledReason: 'Email is owned by WorkOS/AuthKit identity reconciliation.' },
      { fieldId: 'locale', label: 'Locale', value: 'en-US', editable: false, inputType: 'select', disabledReason: 'Locale changes are deferred beyond My Account.' },
      { fieldId: 'timeZone', label: 'Time zone', value: 'America/New_York', editable: false, inputType: 'text', disabledReason: 'Time zone changes are deferred beyond My Account.' }
    ],
    version: 1,
    permissionState: { canEdit: true, reason: 'Only self-owned display name is mutable in My Account.', authoritativeCapabilityId: myAccountCapabilities.updateProfileSettings },
    audit: { lastEventType: 'UserProfileDisplayed', lastActor: 'Tenant Admin', traceIds: ['trace-my-profile', 'trace-my-account-profile-settings'] },
    denialExamples: ['unsupported self-service field', 'cross-user target', 'disabled account', 'inactive membership']
  },
  [myAccountSurfaceActions.updateProfile, myAccountSurfaceActions.openAuditTrace]
);

export const myAccountSettingsSurface = envelope(
  'surface-my-settings',
  'detail-edit',
  'User settings',
  'my-account-agent',
  {
    recordId: 'acct-admin-settings',
    recordLabel: 'Tenant Admin settings',
    recordKind: 'settings',
    summary: 'Personal settings for the workstream shell. Backend persistence, no-op, idempotency, and validation errors are reflected as capability action results.',
    fields: [
      { fieldId: 'preferredThemeId', label: 'Theme', value: 'aurora-light', editable: true, inputType: 'select', options: [{ value: 'aurora-light', label: 'Aurora Light' }, { value: 'cobalt-light', label: 'Cobalt Light' }, { value: 'obsidian-dark', label: 'Obsidian Dark' }, { value: 'midnight-dark', label: 'Midnight Dark' }, { value: 'dark-night', label: 'Dark Night' }] },
      { fieldId: 'notificationDigest', label: 'Notification digest', value: 'daily', editable: false, inputType: 'select', disabledReason: 'Notification digest is deferred beyond My Account.' },
      { fieldId: 'composerDensity', label: 'Composer density', value: 'comfortable', editable: false, inputType: 'select', disabledReason: 'Composer density is deferred beyond My Account.' }
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
  'my-account-agent',
  {
    events: [
      { eventId: 'trace-my-summary', occurredAt: generatedAt, actor: 'My Account Agent', action: 'Protected summary read emitted my_account.view_summary trace', traceId: 'trace-surface-my-account-dashboard' },
      { eventId: 'trace-my-settings-write', occurredAt: generatedAt, actor: 'Tenant Admin', action: 'Self-service settings update audited with idempotency key', traceId: 'trace-my-account-profile-settings' },
      { eventId: 'trace-my-agent-turn', occurredAt: generatedAt, actor: 'WorkstreamRuntimeAgent', action: 'PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, and AgentWorkTrace linked to Ask My Account', traceId: 'trace-my-account-dashboard' }
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

export const auditTraceSurfaceActions = {
  showDashboard: {
    actionId: 'action-audit-trace-dashboard',
    label: 'Refresh Audit/Trace dashboard',
    intent: 'read',
    capabilityId: 'audit.trace.dashboard.read',
    governedToolId: 'read-audit-trace-dashboard',
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
    governedToolId: 'search-audit-traces',
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
    governedToolId: 'read-trace-detail',
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
    governedToolId: 'read-trace-timeline',
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
    governedToolId: 'read-trace-failure-evidence',
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
    governedToolId: 'read-investigation-guide',
    browserToolId: 'action-audit-trace-investigation-guide',
    inputSchemaRef: 'schema.audit-trace.investigation-guide.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-investigation-guide', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceInvestigationGuideRequested', traceRequired: true }
  },
  appendInvestigationNote: {
    actionId: 'action-audit-trace-append-investigation-note',
    label: 'Append investigation note',
    intent: 'command',
    capabilityId: 'audit.trace.investigation_note.append',
    governedToolId: 'draft-investigation-note',
    browserToolId: 'action-audit-trace-append-investigation-note',
    inputSchemaRef: 'schema.audit-trace.investigation-note.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-investigation-note', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceInvestigationNoteAppended', traceRequired: true }
  },
  startSummaryTask: {
    actionId: 'action-audit-trace-summary-task-start',
    label: 'Start audit summary task',
    intent: 'workflow',
    capabilityId: 'audit.trace.summary_task.start',
    governedToolId: 'start-audit-summary-task',
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
    governedToolId: 'read-audit-summary-task',
    browserToolId: 'action-audit-trace-summary-task-read',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-summary-progress', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceSummaryTaskRead', traceRequired: true }
  },
  reviewSummary: {
    actionId: 'action-audit-trace-summary-review',
    label: 'Open summary review',
    intent: 'read',
    capabilityId: 'audit.trace.summary_task.review',
    governedToolId: 'review-audit-summary-task',
    browserToolId: 'action-audit-trace-summary-review',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-summary-review', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceSummaryReviewRead', traceRequired: true }
  },
  acceptSummaryResult: {
    actionId: 'action-audit-trace-summary-accept',
    label: 'Accept advisory summary',
    intent: 'approval',
    capabilityId: 'audit.trace.summary_task.accept',
    governedToolId: 'accept-audit-summary-task',
    browserToolId: 'action-audit-trace-summary-accept',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-summary-review', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceSummaryAccepted', traceRequired: true }
  },
  rejectSummaryResult: {
    actionId: 'action-audit-trace-summary-reject',
    label: 'Reject advisory summary',
    intent: 'approval',
    capabilityId: 'audit.trace.summary_task.reject',
    governedToolId: 'reject-audit-summary-task',
    browserToolId: 'action-audit-trace-summary-reject',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-audit-trace-summary-review', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceSummaryRejected', traceRequired: true }
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
  },
  requestRedactedExport: {
    actionId: 'action-audit-trace-request-redacted-export',
    label: 'Request redacted export',
    intent: 'approval',
    capabilityId: 'audit.trace.export.request',
    governedToolId: 'request-redacted-export',
    browserToolId: 'action-audit-trace-request-redacted-export',
    inputSchemaRef: 'schema.audit-trace.export-request.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    requiresApproval: true,
    resultSurface: { updateSurfaceId: 'surface-audit-trace-export-request', openPlacement: 'inline' },
    audit: { eventType: 'AuditTraceRedactedExportRequested', traceRequired: true }
  }
} satisfies Record<string, SurfaceAction>;

export const auditTraceDashboardSurface = envelope(
  'surface-audit-trace-dashboard',
  'dashboard',
  'Audit/Trace dashboard',
  'audit-trace-agent',
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
      { workstreamId: 'audit-trace-agent', label: 'Search scoped traces', allowed: true, capabilityIds: ['audit.trace.search'], traceId: 'trace-audit-dashboard-search' },
      { workstreamId: 'audit-trace-agent', label: 'Open failure evidence', allowed: true, capabilityIds: ['audit.trace.failureEvidence.read'], traceId: 'trace-audit-dashboard-failure' },
      { workstreamId: 'audit-trace-agent', label: 'Start audit summary task', allowed: true, capabilityIds: ['audit.trace.summary_task.start'], traceId: 'trace-audit-summary-task-start' }
    ]
  },
  [auditTraceSurfaceActions.search, auditTraceSurfaceActions.openTimeline, auditTraceSurfaceActions.openFailureEvidence, auditTraceSurfaceActions.showInvestigationGuide, auditTraceSurfaceActions.requestRedactedExport]
);

export const auditTraceSearchSurface = envelope(
  'surface-audit-trace-search',
  'list-search',
  'Trace search results',
  'audit-trace-agent',
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
  'audit-trace-agent',
  {
    traceId: 'trace-provider-blocked-002',
    eventKind: 'PROVIDER_BLOCKED',
    timestamp: generatedAt,
    actor: 'WorkstreamRuntimeAgent',
    source: 'audit-trace-agent',
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
  'audit-trace-agent',
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
  'audit-trace-agent',
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
  'audit-trace-agent',
  {
    recommendation: 'Continue only with backend-authorized, tenant-scoped evidence.',
    allowedActions: [
      { actionId: 'action-audit-trace-search', label: 'Refine search', browserToolId: 'action-audit-trace-search', governedToolId: 'search-audit-traces', capabilityId: 'audit.trace.search' },
      { actionId: 'action-audit-trace-timeline', label: 'Open timeline', browserToolId: 'action-audit-trace-timeline', governedToolId: 'read-trace-timeline', capabilityId: 'audit.trace.timeline.read' }
    ],
    disabledActions: [
      { actionId: 'action-audit-trace-summary-task-start-scheduled', reason: 'Scheduled audit summary cadence remains future work; manual backend-governed start is wired.' }
    ],
    risk: 'low',
    traceLinks: ['corr-provider-blocked-002']
  },
  [auditTraceSurfaceActions.search, auditTraceSurfaceActions.openTimeline, auditTraceSurfaceActions.openFailureEvidence, auditTraceSurfaceActions.requestRedactedExport, auditTraceSurfaceActions.appendInvestigationNote]
);

export const auditTraceExportRequestSurface = envelope(
  'surface-audit-trace-export-request',
  'decision',
  'Redacted audit export request',
  'audit-trace-agent',
  {
    surfaceContract: 'audit.trace.exportRequest.v1',
    exportId: 'audit-export-tenant-1-redacted-001',
    status: 'approval_required',
    requestedFormat: 'jsonl-redacted',
    policyDecision: 'redacted_export_requires_policy_gate',
    recommendation: 'Approve only scoped redacted export bundles; unredacted exports are not a default browser action.',
    risk: 'medium',
    bundleMetadata: { tenantId: authContext.tenantId, customerId: authContext.customerId, redactionProfile: 'browser-safe', omittedFieldKeys: ['rawJwt', 'rawProviderCredential', 'hiddenPromptText', 'rawToolPayload', 'invitationToken'] },
    allowedActions: [
      { actionId: 'action-audit-trace-search', label: 'Refine scoped evidence', browserToolId: 'action-audit-trace-search', governedToolId: 'search-audit-traces', capabilityId: 'audit.trace.search' },
      { actionId: 'action-audit-trace-timeline', label: 'Review correlation timeline', browserToolId: 'action-audit-trace-timeline', governedToolId: 'read-trace-timeline', capabilityId: 'audit.trace.timeline.read' }
    ],
    disabledActions: [{ actionId: 'action-audit-trace-unredacted-export', reason: 'Unredacted export requires a separate policy exception and is not produced by this browser surface.' }],
    traceLinks: ['trace-audit-export-request'],
    redaction: 'Export request stores scoped metadata only; raw evidence, tokens, provider secrets, hidden prompts, and cross-tenant facts are omitted.'
  },
  [auditTraceSurfaceActions.search, auditTraceSurfaceActions.openTimeline, auditTraceSurfaceActions.openFailureEvidence]
);

export const auditTraceInvestigationNoteSurface = envelope(
  'surface-audit-trace-investigation-note',
  'system-message',
  'Investigation note recorded',
  'audit-trace-agent',
  {
    surfaceContract: 'audit.trace.investigationNote.v1',
    status: 'recorded',
    traceId: 'trace-provider-blocked-002',
    noteSummary: 'Human reviewer noted that provider failure evidence is redacted and tenant-scoped.',
    retainedAuthority: 'Human-authored investigation notes annotate traces only; they do not mutate source traces, policy, authorization, or retained evidence.',
    redactionMetadata: { omittedFieldKeys: ['rawJwt', 'rawProviderCredential', 'hiddenPromptText', 'rawToolPayload'], nonEnumerating: false }
  },
  [auditTraceSurfaceActions.openTimeline, auditTraceSurfaceActions.showInvestigationGuide]
);

export const auditTraceSummaryProgressSurface = envelope(
  'surface-audit-trace-summary-progress',
  'detail-edit',
  'Audit/Trace summary progress',
  'audit-trace-agent',
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
  'audit-trace-agent',
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
    actions: ['action-audit-trace-summary-review', 'action-audit-trace-summary-accept', 'action-audit-trace-summary-reject', 'action-audit-trace-summary-task-open-evidence']
  },
  [auditTraceSurfaceActions.reviewSummary, auditTraceSurfaceActions.acceptSummaryResult, auditTraceSurfaceActions.rejectSummaryResult, auditTraceSurfaceActions.openSummaryEvidence]
);

export const auditTraceStructuredSurfaces = [auditTraceDashboardSurface, auditTraceSearchSurface, auditTraceDetailSurface, auditTraceTimelineSurface, auditTraceFailureEvidenceSurface, auditTraceInvestigationGuideSurface, auditTraceExportRequestSurface, auditTraceInvestigationNoteSurface, auditTraceSummaryProgressSurface, auditTraceSummaryReviewSurface];

export const userAdminDashboardSurface = envelope(
  'surface-user-admin-dashboard',
  'dashboard',
  'User Admin Dashboard',
  'user-admin-agent',
  {
    surfaceContract: 'user_admin.dashboard.v1',
    hero: { title: 'User Admin Dashboard', scopeLabel: 'Tenant scope', scopeType: 'tenant', adminLevel: 'Tenant Admin', administeredPopulationLabels: ['Tenant employees', 'Customer users'], supportAccessState: '1 visible support grant expires soon.', redactionSummary: 'Hidden users, hidden counts, provider internals, and raw trace mechanics are omitted from the default dashboard.', traceRefs: ['trace-user-admin-dashboard'], correlationId: 'corr-surface-user-admin-dashboard' },
    cards: [
      { cardId: 'card-pending-invitations', label: 'Pending invitations', value: 3, severity: 'warning', targetSurfaceId: 'surface-user-admin-users', actionId: 'action-user-admin-show-users' },
      { cardId: 'card-active-users', label: 'Active users', value: 18, severity: 'info', targetSurfaceId: 'surface-user-admin-users', actionId: 'action-user-admin-show-users' },
      { cardId: 'card-access-review', label: 'Access review items', value: 2, severity: 'critical', targetSurfaceId: 'surface-user-admin-access-review-task', actionId: 'action-useradmin-read-access-review' },
      { cardId: 'card-support-access', label: 'Expiring support grants', value: 1, severity: 'warning', targetSurfaceId: 'surface-user-admin-users', actionId: 'action-read-support-access' }
    ],
    attentionCounts: [
      { attentionType: 'invitation_delivery', label: 'Invitation delivery', count: 2, severity: 'warning', statusText: 'Needs resend or revoke review', targetSurfaceId: 'surface-user-admin-users', filter: 'rowType:invitation', sourceCapabilityId: userAdminCapabilities.listInvitations, traceRefs: ['trace-invite-robin'], redactionState: 'full', openActionId: 'action-user-admin-show-users' },
      { attentionType: 'access_review', label: 'Access review results', count: 2, severity: 'critical', statusText: 'Human review needed', targetSurfaceId: 'surface-user-admin-access-review-task', sourceCapabilityId: userAdminCapabilities.viewAccessReviewTask, traceRefs: ['trace-useradmin-access-review-blocked'], redactionState: 'summary_only', openActionId: 'action-useradmin-read-access-review' },
      { attentionType: 'support_access', label: 'Support access expiring', count: 1, severity: 'warning', statusText: 'Review visible grant', targetSurfaceId: 'surface-user-admin-users', filter: 'supportAccess:true', sourceCapabilityId: userAdminCapabilities.viewTraceReference, traceRefs: ['trace-support-grant'], redactionState: 'full', openActionId: 'action-read-support-access' },
      { attentionType: 'admin_audit', label: 'Recent admin audit', count: 0, severity: 'info', statusText: 'No evidence needs action', targetSurfaceId: 'surface-audit-trace-timeline', sourceCapabilityId: userAdminCapabilities.viewTraceReference, traceRefs: ['trace-user-admin-dashboard'], redactionState: 'summary_only', openActionId: 'action-open-admin-audit' }
    ],
    administeredPopulations: [
      { populationType: 'tenant_employees', label: 'Tenant employees', visibleCount: 18, attentionCount: 3, activeCount: 17, pendingInvitationCount: 2, suspendedOrDisabledCount: 1, staleOrExpiredCount: 1, reviewCount: 2, roleCoverageSummary: 'Tenant Admin and Member roles visible', targetSurfaceId: 'surface-user-admin-users', openActionId: 'action-user-admin-show-users', capabilityIds: [userAdminCapabilities.listMembers], traceRefs: ['trace-useradmin-population-tenant'] },
      { populationType: 'customer_users', label: 'Customer users', visibleCount: 7, attentionCount: 0, activeCount: 7, pendingInvitationCount: 0, suspendedOrDisabledCount: 0, staleOrExpiredCount: 0, reviewCount: 0, roleCoverageSummary: 'Customer user role visible', targetSurfaceId: 'surface-user-admin-users', openActionId: 'action-user-admin-show-users', capabilityIds: [userAdminCapabilities.listMembers], traceRefs: ['trace-useradmin-population-customer'] }
    ],
    authorizedActions: [
      { actionId: 'action-user-admin-show-users', label: 'Open users', governedToolId: 'search-user-directory', capabilityId: userAdminCapabilities.listMembers, resultSurfaceId: 'surface-user-admin-users', denialHint: 'Open the backend-filtered directory.' },
      { actionId: 'action-open-useradmin-invitation-create', label: 'Invite user', governedToolId: 'create-or-resend-invitation', capabilityId: userAdminCapabilities.sendInvitation, resultSurfaceId: 'surface-user-admin-invitation-create', approvalRequired: false, denialHint: 'Backend validates target scope, role options, idempotency, and outbox readiness.' },
      { actionId: 'action-useradmin-read-access-review', label: 'Open access review', governedToolId: 'run-access-review', capabilityId: userAdminCapabilities.viewAccessReviewTask, resultSurfaceId: 'surface-user-admin-access-review-task', approvalRequired: true, denialHint: 'Review advisory output before any deterministic access change.' },
      { actionId: 'action-open-admin-audit', label: 'Open admin audit', governedToolId: 'admin.audit.read', capabilityId: userAdminCapabilities.viewTraceReference, resultSurfaceId: 'surface-audit-trace-timeline', denialHint: 'Open role-gated evidence when authorized.' }
    ],
    recentActivity: [
      { activityId: 'activity-invite-robin', label: 'Invitation delivery failed', summary: 'Robin Reviewer invitation needs recovery.', traceId: 'trace-invite-robin', redaction: 'browser-safe', occurredAt: generatedAt },
      { activityId: 'activity-last-admin', label: 'Last-admin protection applied', summary: 'Risky role downgrade remains approval-gated.', traceId: 'trace-useradmin-last-admin-denied', redaction: 'summary_only', occurredAt: generatedAt }
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
    userAdminSurfaceActions.showUsers,
    userAdminSurfaceActions.displayUserList,
    userAdminSurfaceActions.openInvitationCreate,
    userAdminSurfaceActions.readAccessReview,
    userAdminSurfaceActions.readSupportAccess,
    userAdminSurfaceActions.openAdminAudit,
    userAdminSurfaceActions.displayOrganizationAdmin,
    userAdminSurfaceActions.showOrganizations
  ]
);

const organizationAdminSurfaceData = {
    surfaceContract: 'user_admin.organization_directory.v1',
    scopeLabel: 'SaaS Owner scope',
    scopeType: 'SAAS_OWNER',
    authorityBasis: 'Backend checks selected AuthContext and saas_owner.tenant.read/manage; browser state cannot grant Organization authority.',
    boundaryNotice: 'Organization administration manages the Tenant lifecycle boundary only; it does not grant tenant/customer application-data access, support access, provider secret access, or billing-derived authority.',
    organizations: [
      { organizationId: 'tenant-starter', organizationName: 'Starter Organization', status: 'active', safeLifecycleSummary: 'Active Tenant boundary', actionAvailability: ['rename', 'suspend'], traceRefs: ['trace-organization-tenant-starter'] },
      { organizationId: 'tenant-suspended', organizationName: 'Suspended Organization', status: 'suspended', safeLifecycleSummary: 'Suspended Tenant boundary', actionAvailability: ['rename', 'reactivate'], traceRefs: ['trace-organization-tenant-suspended'] }
    ],
    organizationDetail: {
      organizationId: 'tenant-starter',
      organizationName: 'Starter Organization',
      status: 'active',
      safeBoundaryNotice: 'Safe detail omits tenant application data, customer records, hidden counts, provider secrets, support-access internals, and billing-derived authority.',
      visibleActions: ['rename', 'suspend'],
      recentAuditEvents: [],
      traceRefs: ['trace-organization-detail'],
      correlationId: 'corr-organization-detail'
    },
    filters: { query: '', status: '' },
    systemStates: ['loading', 'empty', 'ready', 'submitting', 'success', 'validation-error', 'forbidden', 'not_found_or_redacted', 'no-op', 'conflict', 'stale', 'error'],
    lastResult: { status: 'no-op', message: 'Requested Organization name already matches current state.', correlationId: 'corr-organization-no-op', traceRefs: ['trace-organization-no-op'] },
    branchNavigation: {
      branchRootSurfaceId: 'surface-user-admin-organization-directory',
      branchReturnActionId: 'action-user-admin-show-organizations',
      branchReturnLabel: 'Back to organizations',
      browserToolId: 'user-admin.show-organizations',
      governedToolId: 'manage-organizations',
      capabilityId: 'saas_owner.organization.list',
      safeFilterPreservation: 'backend-authored-only',
      traceRefs: ['trace-organization-branch-return'],
      correlationId: 'corr-organization-detail'
    },
    redaction: ['tenant-app-data-redacted', 'provider-secrets-redacted', 'billing-authority-redacted', 'support-access-internals-redacted', 'hidden-counts-redacted']
  };

export const userAdminOrganizationDirectorySurface = envelope(
  'surface-user-admin-organization-directory',
  'list-search',
  'Organization Directory',
  'user-admin-agent',
  organizationAdminSurfaceData,
  [
    userAdminSurfaceActions.listOrganizations,
    userAdminSurfaceActions.readOrganization,
    userAdminSurfaceActions.openOrganizationCreate,
    userAdminSurfaceActions.openAdminAudit
  ]
);

export const userAdminOrganizationDetailSurface = envelope(
  'surface-user-admin-organization-detail',
  'show-inspection',
  'Organization Detail',
  'user-admin-agent',
  { ...organizationAdminSurfaceData, surfaceContract: 'user_admin.organization_detail.v1' },
  [
    userAdminSurfaceActions.showOrganizations,
    userAdminSurfaceActions.openOrganizationRename,
    userAdminSurfaceActions.openOrganizationSuspend,
    userAdminSurfaceActions.openOrganizationReactivate,
    userAdminSurfaceActions.openOrganizationAdminInvitationCreate,
    userAdminSurfaceActions.openAdminAudit
  ]
);

export const userAdminOrganizationCreateSurface = envelope(
  'surface-user-admin-organization-create',
  'create-form',
  'Create Organization',
  'user-admin-agent',
  { ...organizationAdminSurfaceData, surfaceContract: 'user_admin.organization_create.v1' },
  [userAdminSurfaceActions.showOrganizations, userAdminSurfaceActions.createOrganization]
);

export const userAdminOrganizationRenameSurface = envelope(
  'surface-user-admin-organization-rename',
  'edit-form',
  'Rename Organization',
  'user-admin-agent',
  { ...organizationAdminSurfaceData, surfaceContract: 'user_admin.organization_rename.v1' },
  [userAdminSurfaceActions.showOrganizations, userAdminSurfaceActions.renameOrganization]
);

export const userAdminOrganizationSuspendSurface = envelope(
  'surface-user-admin-organization-suspend-confirmation',
  'destructive-lifecycle-confirmation',
  'Suspend Organization',
  'user-admin-agent',
  { ...organizationAdminSurfaceData, surfaceContract: 'user_admin.organization_suspend_confirmation.v1' },
  [userAdminSurfaceActions.showOrganizations, userAdminSurfaceActions.suspendOrganization]
);

export const userAdminOrganizationReactivateSurface = envelope(
  'surface-user-admin-organization-reactivate-confirmation',
  'lifecycle-confirmation',
  'Reactivate Organization',
  'user-admin-agent',
  { ...organizationAdminSurfaceData, surfaceContract: 'user_admin.organization_reactivate_confirmation.v1' },
  [userAdminSurfaceActions.showOrganizations, userAdminSurfaceActions.reactivateOrganization]
);


// Legacy fixture alias preserved for stale-screen quarantine tests: old user-admin-user-list now resolves conceptually to surface-user-admin-users.
export const userAdminListSearchSurface = envelope(
  'surface-user-admin-users',
  'list-search',
  'Users, invitations, and memberships',
  'user-admin-agent',
  {
    query: 'status:active OR invitation:pending',
    rows: [
      { id: 'user-acct-admin', rowType: 'user-directory', email: 'admin@example.test', displayName: 'Tenant Admin', role: 'Tenant Admin', status: 'active', targetSurfaceId: 'surface-user-admin-user-detail', openActionId: 'action-display-user-detail', lastAdmin: true, traceId: 'trace-user-admin-row' },
      { id: 'invite-robin', rowType: 'invitation-queue', email: 'robin@example.test', displayName: 'Robin Reviewer', role: 'Reviewer', status: 'pending', delivery: 'failed', targetSurfaceId: 'surface-user-admin-invitation-detail', openActionId: 'action-display-invitation-detail', expiresInHours: 18, traceId: 'trace-invite-robin' },
      { id: 'membership-member', rowType: 'membership', email: 'member@example.test', displayName: 'Member User', role: 'Member', status: 'active', supportAccess: false, targetSurfaceId: 'surface-user-admin-user-detail', openActionId: 'action-display-user-detail', traceId: 'trace-membership-member' },
      { id: 'support-grant-1', rowType: 'support-access', email: 'support@example.test', displayName: 'Support Engineer', role: 'Support', status: 'expiring', supportAccess: true, expiresInHours: 6, targetSurfaceId: 'surface-user-admin-user-detail', openActionId: 'action-display-user-detail', traceId: 'trace-support-grant' },
      { id: 'audit-invite-1', rowType: 'admin-audit-excerpt', email: 'admin@example.test', displayName: 'Tenant Admin', role: 'Audit actor', status: 'invited-user', targetSurfaceId: 'surface-audit-trace-timeline', openActionId: 'action-open-admin-audit', traceId: 'trace-invite' }
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
    userAdminSurfaceActions.displayInvitationDetail,
    userAdminSurfaceActions.displayRoleCapabilityMatrix,
    userAdminSurfaceActions.openInvitationCreate,
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
  'surface-user-admin-user-detail',
  'show-inspection',
  'Tenant Admin account detail',
  'user-admin-agent',
  {
    recordId: 'user-acct-admin',
    recordLabel: 'Tenant Admin · admin@example.test',
    recordKind: 'account',
    summary: 'Scoped show/inspection surface for a tenant user. Consequential changes open dedicated task, decision, workflow, or system-message surfaces.',
    fields: [
      { fieldId: 'displayName', label: 'Display name', value: 'Tenant Admin', editable: false, inputType: 'text', disabledReason: 'User detail is inspection-only; profile changes require a dedicated task surface.' },
      { fieldId: 'email', label: 'Email', value: 'admin@example.test', editable: false, inputType: 'email', disabledReason: 'Email changes require an identity-provider reconciliation workflow.' },
      { fieldId: 'role', label: 'Membership role', value: 'Tenant Admin', editable: false, inputType: 'select', options: [{ value: 'tenant-admin', label: 'Tenant Admin' }, { value: 'member', label: 'Member' }], disabledReason: 'Backend authorization denied role replacement for the last tenant admin in this fixture.' },
      { fieldId: 'status', label: 'Account status', value: 'active', editable: false, inputType: 'text', disabledReason: 'Account suspension requires a governed support-access decision.' }
    ],
    version: 3,
    permissionState: {
      canEdit: false,
      reason: 'Detail is inspection-only; role, membership, support-access, invitation, identity, and profile changes route to dedicated backend-authored surfaces.',
      authoritativeCapabilityId: userAdminCapabilities.listMembers
    },
    accessManagement: {
      advisoryNotice: 'Frontend controls are advisory only; /api/workstream/actions and backend User Admin services remain authoritative for user_admin.update_member_status, user_admin.preview_role_change, and user_admin.change_member_roles.',
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
        capabilityDelta: { added: [], removed: ['user_admin.change_member_roles', 'user_admin.update_member_status'], unchanged: ['user_admin.list_members'] },
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
    userAdminSurfaceActions.showUsers,
    userAdminSurfaceActions.openMembershipStatusConfirmation,
    userAdminSurfaceActions.previewRoleChange,
    userAdminSurfaceActions.openSupportAccessGrant,
    userAdminSurfaceActions.openSupportAccessRevokeConfirmation,
    userAdminSurfaceActions.openIdentityExceptionReview,
    userAdminSurfaceActions.readAccessReview,
    userAdminSurfaceActions.openAdminAudit
  ]
);


export const userAdminInvitationDetailSurface = envelope(
  'surface-user-admin-invitation-detail',
  'show-inspection',
  'Invitation detail',
  'user-admin-agent',
  {
    surfaceContract: 'user_admin.invitation_detail.v1',
    branchNavigation: { branchRootSurfaceId: 'surface-user-admin-users', branchReturnActionId: 'action-user-admin-show-users', branchReturnLabel: 'Show Users', browserToolId: 'user-admin.show-users', governedToolId: 'search-user-directory', capabilityId: userAdminCapabilities.listMembers, safeFilterPreservation: 'backend-authored-only', traceRefs: ['trace-useradmin-invitation-branch-return'], correlationId: 'corr-useradmin-invitation-detail' },
    recordId: 'invite-robin',
    recordLabel: 'Robin Reviewer invitation',
    recordKind: 'invitation',
    summary: 'Lifecycle-aware invitation inspection. Resend and revoke open dedicated confirmation surfaces; delivery/outbox/provider failures fail closed without fake success.',
    fields: [
      { fieldId: 'email', label: 'Email', value: 'robin@example.test', editable: false, inputType: 'email' },
      { fieldId: 'status', label: 'Invitation status', value: 'pending', editable: false, inputType: 'text' },
      { fieldId: 'delivery', label: 'Delivery', value: 'failed', editable: false, inputType: 'text' },
      { fieldId: 'expiresAt', label: 'Expires', value: '2026-05-20T06:00:00Z', editable: false, inputType: 'text' }
    ],
    deliveryState: { currentStatus: 'failed', attempts: 2, retryEligible: true, providerReadiness: 'outbox-fail-closed', lastSafeError: 'Email provider is not configured for normal runtime delivery.', providerBoundary: 'Provider message ids, raw Resend payloads, email bodies, tokens, and secrets are redacted.' },
    recoverySteps: ['Open resend confirmation after provider readiness is restored.', 'Revoke the invitation if the recipient should no longer receive access.', 'Open audit evidence for the failure trace.'],
    actionContext: { invitationId: 'invite-robin' },
    audit: { lastEventType: 'InvitationDetailDisplayed', lastActor: 'Tenant Admin', traceIds: ['trace-invite-robin', 'trace-useradmin-invitation-detail'] },
    noFakeSuccess: true,
    redaction: ['invitation-token-redacted', 'provider-payload-redacted', 'raw-jwt-redacted']
  },
  [userAdminSurfaceActions.showUsers, userAdminSurfaceActions.openInvitationResendConfirmation, userAdminSurfaceActions.openInvitationRevokeConfirmation, userAdminSurfaceActions.openAdminAudit]
);

export const userAdminInvitationResendConfirmationSurface = envelope('surface-user-admin-invitation-resend-confirmation', 'lifecycle-confirmation', 'Resend invitation', 'user-admin-agent', { surfaceContract: 'user_admin.invitation_resend_confirmation.v1', branchRootSurfaceId: 'surface-user-admin-users', branchReturnActionId: 'action-user-admin-show-users', branchReturnLabel: 'Show Users', safeFilterPreservation: 'backend-authored-only', recordId: 'invite-robin', recordLabel: 'Robin Reviewer invitation', recordKind: 'invitation', status: 'ready', confirmationCopy: 'Resend this invitation through the provider-backed outbox only after backend eligibility is rechecked.', reasonRequired: false, confirmationRequired: true, deliveryState: { currentStatus: 'failed', attempts: 2, retryEligible: true, providerReadiness: 'blocked_provider_or_runtime', lastSafeError: 'Provider configuration missing.' }, recoverySteps: ['Restore provider configuration or keep the invitation pending.', 'Use revoke if access should no longer be offered.'], actionContext: { invitationId: 'invite-robin' }, idempotencyKeyHint: 'client-generated', traceRefs: ['trace-useradmin-invitation-resend-confirmation'], correlationId: 'corr-useradmin-invitation-resend', redaction: ['invitation-token-redacted', 'provider-payload-redacted'] }, [userAdminSurfaceActions.resendInvitation, userAdminSurfaceActions.showUsers, userAdminSurfaceActions.openAdminAudit]);

export const userAdminInvitationRevokeConfirmationSurface = envelope('surface-user-admin-invitation-revoke-confirmation', 'destructive-lifecycle-confirmation', 'Revoke invitation', 'user-admin-agent', { surfaceContract: 'user_admin.invitation_revoke_confirmation.v1', branchRootSurfaceId: 'surface-user-admin-users', branchReturnActionId: 'action-user-admin-show-users', branchReturnLabel: 'Show Users', safeFilterPreservation: 'backend-authored-only', recordId: 'invite-robin', recordLabel: 'Robin Reviewer invitation', recordKind: 'invitation', status: 'ready', consequenceCopy: 'Revoking prevents this invitation from being accepted. Existing memberships are not changed.', reasonRequired: true, confirmationRequired: true, actionContext: { invitationId: 'invite-robin' }, idempotencyKeyHint: 'client-generated', traceRefs: ['trace-useradmin-invitation-revoke-confirmation'], correlationId: 'corr-useradmin-invitation-revoke', redaction: ['invitation-token-redacted', 'provider-payload-redacted'] }, [userAdminSurfaceActions.revokeInvitation, userAdminSurfaceActions.showUsers, userAdminSurfaceActions.openAdminAudit]);

export const userAdminMembershipStatusConfirmationSurface = envelope('surface-user-admin-membership-status-confirmation', 'destructive-lifecycle-confirmation', 'Confirm membership status change', 'user-admin-agent', { surfaceContract: 'user_admin.membership_status_confirmation.v1', branchRootSurfaceId: 'surface-user-admin-users', branchReturnActionId: 'action-user-admin-show-users', branchReturnLabel: 'Show Users', safeFilterPreservation: 'backend-authored-only', recordId: 'membership-member', recordLabel: 'Member User', recordKind: 'membership', currentStatus: 'active', proposedStatus: 'removed', statusOptions: [{ status: 'removed', label: 'Deactivate member', actionId: 'action-useradmin-disable-member' }, { status: 'active', label: 'Reactivate member', actionId: 'action-useradmin-reactivate-member' }], consequenceCopy: 'Membership/account lifecycle changes enforce self-action, last-admin, selected scope, idempotency, and audit guardrails.', reasonRequired: true, confirmationRequired: true, actionContext: { accountId: 'membership-member', membershipId: 'membership-member' }, idempotencyKeyHint: 'client-generated', traceRefs: ['trace-useradmin-membership-status-confirmation'], correlationId: 'corr-useradmin-membership-status', redaction: ['cross-scope-users-redacted', 'raw-jwt-redacted'] }, [userAdminSurfaceActions.suspendMembership, userAdminSurfaceActions.reactivateMembership, userAdminSurfaceActions.showUsers, userAdminSurfaceActions.openAdminAudit]);

export const userAdminSupportAccessGrantSurface = envelope('surface-user-admin-support-access-grant', 'create-form', 'Grant support access', 'user-admin-agent', { surfaceContract: 'user_admin.support_access_grant.v1', branchRootSurfaceId: 'surface-user-admin-users', branchReturnActionId: 'action-user-admin-show-users', branchReturnLabel: 'Show Users', safeFilterPreservation: 'backend-authored-only', recordId: 'support-grant-1', recordLabel: 'Support Engineer', recordKind: 'support-access', summary: 'Grant or extend time-boxed support access with purpose, expiry, approval policy, idempotency, and audit trace.', purposeRequired: true, supportExpiryOptions: [{ value: '2', label: '2 hours' }, { value: '8', label: '8 hours' }], policyOptions: { expiryHours: [{ value: '2', label: '2 hours' }, { value: '8', label: '8 hours' }] }, actionContext: { accountId: 'support-grant-1', membershipId: 'support-grant-1' }, idempotencyKeyHint: 'client-generated', traceRefs: ['trace-useradmin-support-access-grant'], correlationId: 'corr-useradmin-support-grant', redaction: ['support-provider-internals-redacted', 'raw-jwt-redacted'] }, [userAdminSurfaceActions.grantUserAdminSupportAccess, userAdminSurfaceActions.extendUserAdminSupportAccess, userAdminSurfaceActions.showUsers, userAdminSurfaceActions.openAdminAudit]);

export const userAdminSupportAccessRevokeConfirmationSurface = envelope('surface-user-admin-support-access-revoke-confirmation', 'destructive-lifecycle-confirmation', 'Revoke support access', 'user-admin-agent', { surfaceContract: 'user_admin.support_access_revoke_confirmation.v1', branchRootSurfaceId: 'surface-user-admin-users', branchReturnActionId: 'action-user-admin-show-users', branchReturnLabel: 'Show Users', safeFilterPreservation: 'backend-authored-only', recordId: 'support-grant-1', recordLabel: 'Support Engineer', recordKind: 'support-access', currentSupportAccess: true, consequenceCopy: 'Revoking support access removes only the time-boxed support grant. It does not change ordinary memberships or roles.', reasonRequired: true, confirmationRequired: true, actionContext: { accountId: 'support-grant-1', membershipId: 'support-grant-1' }, idempotencyKeyHint: 'client-generated', traceRefs: ['trace-useradmin-support-access-revoke'], correlationId: 'corr-useradmin-support-revoke', redaction: ['support-provider-internals-redacted', 'raw-jwt-redacted'] }, [userAdminSurfaceActions.revokeUserAdminSupportAccess, userAdminSurfaceActions.showUsers, userAdminSurfaceActions.openAdminAudit]);

export const userAdminIdentityExceptionReviewSurface = envelope('surface-user-admin-identity-exception-review', 'decision-card', 'Identity exception review', 'user-admin-agent', { surfaceContract: 'user_admin.identity_exception_review.v1', branchRootSurfaceId: 'surface-user-admin-users', branchReturnActionId: 'action-user-admin-show-users', branchReturnLabel: 'Show Users', safeFilterPreservation: 'backend-authored-only', recordId: 'identity-exception-1', recordLabel: 'Robin Reviewer identity recovery', recordKind: 'identity-exception', status: 'approval-required', lifecycleStatus: 'waiting_for_review', summary: 'Review provider-redacted identity link/relink evidence. Approval routes to deterministic recovery/status surfaces without exposing provider internals.', risk: 'provider-boundary', noDirectMutation: true, providerBoundary: 'Raw WorkOS ids, JWTs, and provider payloads are hidden.', evidenceRefs: ['trace-useradmin-identity-provider-redacted', 'trace-useradmin-identity-policy'], actionContext: { accountId: 'invite-robin', recoveryId: 'identity-exception-1' }, traceRefs: ['trace-useradmin-identity-exception-review'], correlationId: 'corr-useradmin-identity-exception', redaction: ['raw-workos-id-redacted', 'raw-jwt-redacted', 'provider-payload-redacted'] }, [userAdminSurfaceActions.requestUserAdminIdentityRelink, userAdminSurfaceActions.readUserAdminIdentityRelink, userAdminSurfaceActions.approveUserAdminIdentityRelink, userAdminSurfaceActions.denyUserAdminIdentityRelink, userAdminSurfaceActions.completeUserAdminIdentityRelink, userAdminSurfaceActions.showUsers, userAdminSurfaceActions.displayUserDetail, userAdminSurfaceActions.openAdminAudit]);

export const userAdminSystemMessageSurface = envelope('surface-user-admin-system-message', 'system_message', 'User Admin action unavailable', 'user-admin-agent', { surfaceContract: 'user_admin.system_message.v1', status: 'not_found_or_redacted', severity: 'warning', title: 'Action unavailable', message: 'The requested User Admin object is hidden, stale, forbidden, or unavailable in the selected context.', recoverySteps: ['Return to the User Directory.', 'Refresh selected context.', 'Open authorized audit evidence if available.'], safeReasonCode: 'TARGET_NOT_FOUND_OR_FORBIDDEN', noFakeSuccess: true, noDirectMutation: true, traceRefs: ['trace-useradmin-system-message'], safety: { sanitized: true, redactionNote: 'Hidden users, memberships, invitations, provider payloads, raw JWTs, and cross-scope facts are redacted.' }, trace: { correlationId: 'corr-useradmin-system-message', traceIds: ['trace-useradmin-system-message'] } }, [userAdminSurfaceActions.showUsers, userAdminSurfaceActions.openAdminAudit]);

export const userAdminRoleChangePreviewSurface = envelope(
  'surface-user-admin-role-change-preview',
  'decision-card',
  'User Admin role change preview',
  'user-admin-agent',
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
    surfaceContract: 'user_admin.role_change_preview.v1',
    status: 'approval-required',
    capabilityDelta: { added: [], removed: ['user_admin.change_member_roles', 'user_admin.update_member_status'], unchanged: ['user_admin.list_members', 'user_admin.preview_role_change'] },
    affectedWorkstreams: ['User Admin', 'Agent Admin', 'Governance/Policy', 'Audit/Trace'],
    policyHints: ['last-admin preservation blocks commit', 'approval required for role downgrade', 'manual replay with the same idempotency key returns no-op evidence'],
    lastAdminImpact: 'last-admin-denied: would remove the final effective Tenant Admin for tenant-acme',
    accessManagement: {
      advisoryNotice: 'This preview does not grant authority. Backend user_admin.change_member_roles enforces tenant/customer scope, last-admin preservation, disabled-user denial, approval policy, idempotency, and audit traces.',
      roleChangePreview: {
        surfaceContract: 'user_admin.role_change_preview.v1',
        currentRoles: ['Tenant Admin'],
        proposedRoles: ['Tenant Employee'],
        capabilityDelta: { added: [], removed: ['user_admin.change_member_roles', 'user_admin.update_member_status'], unchanged: ['user_admin.list_members', 'user_admin.preview_role_change'] },
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
  [userAdminSurfaceActions.showUsers, userAdminSurfaceActions.previewRoleChange, userAdminSurfaceActions.changeMemberRoles, userAdminSurfaceActions.openAdminAudit]
);

export const userAdminRoleCapabilityMatrixSurface = envelope(
  'user-admin-role-capability-matrix',
  'list-search',
  'User Admin role/capability matrix',
  'user-admin-agent',
  {
    query: 'assignable:true OR protected:last-admin',
    rows: [
      { id: 'role-tenant-admin', rowType: 'role-capability', role: 'Tenant Admin', capabilityId: 'user_admin.change_member_roles', assignable: false, policy: 'last-admin and delegation checks required', traceId: 'trace-useradmin-role-matrix-tenant-admin' },
      { id: 'role-tenant-employee', rowType: 'role-capability', role: 'Tenant Employee', capabilityId: 'user_admin.preview_role_change', assignable: true, policy: 'preview before commit', traceId: 'trace-useradmin-role-matrix-employee' },
      { id: 'access-review', rowType: 'autonomous-task-capability', role: 'Access Review', capabilityId: 'user_admin.access_review.start', assignable: false, policy: 'blocked_provider_or_runtime until durable AutonomousAgent lifecycle is enabled', traceId: 'trace-useradmin-access-review-blocked' }
    ],
    pageInfo: { totalKnownCount: 3 },
    emptyMessage: 'No role/capability rows are visible for this scoped AuthContext.',
    mobileFallback: 'table-to-card',
    stateFixtures: {
      loading: 'Loading role/capability policy evidence from backend-authoritative scope.',
      forbidden: 'Forbidden matrix hides roles and capabilities when user_admin.preview_role_change is denied.',
      validation: 'Invalid role deltas return server validation copy with correlation id.',
      trace: 'Every preview, denial, no-op, and applied role change carries trace links.'
    }
  },
  [userAdminSurfaceActions.previewRoleChange, userAdminSurfaceActions.changeMemberRoles, userAdminSurfaceActions.openAdminAudit]
);

export const userAdminAccessReviewSurface = envelope(
  'surface-user-admin-access-review-task',
  'workflow-status',
  'User Admin access review',
  'user-admin-agent',
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
  [userAdminSurfaceActions.showUsers, userAdminSurfaceActions.startAccessReview, userAdminSurfaceActions.readAccessReview, userAdminSurfaceActions.cancelAccessReview, userAdminSurfaceActions.acceptAccessReviewResult, userAdminSurfaceActions.rejectAccessReviewResult, userAdminSurfaceActions.openAdminAudit]
);

export const agentAdminDashboardSurface = envelope(
  'surface-agent-admin-dashboard',
  'dashboard',
  'Agent Admin dashboard',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.dashboard.v1',
    cards: [
      { cardId: 'agent-admin-card-provider', label: 'Provider readiness', value: 'Ready', status: 'Model refs configured; secrets redacted', severity: 'info', actionId: 'action-display-agent-catalog', targetSurfaceId: 'surface-agent-admin-catalog' },
      { cardId: 'agent-admin-card-approvals', label: 'Behavior approvals', value: 2, status: 'Human review required before activation', severity: 'blocked', actionId: 'action-submit-behavior-change', targetSurfaceId: 'surface-agent-behavior-proposal' },
      { cardId: 'agent-admin-card-tool-boundary', label: 'Tool-boundary risks', value: 1, status: 'Side-effecting grant denied until separate review', severity: 'urgent', actionId: 'action-simulate-tool-boundary', targetSurfaceId: 'surface-agent-tool-boundary-diff' },
      { cardId: 'agent-admin-card-seed', label: 'Seed material', value: 3, status: 'Starter defaults visible; tenant overrides preserved', severity: 'info', actionId: 'action-list-agent-seed-material', targetSurfaceId: 'surface-agent-seed-material' }
    ],
    attentionQueues: [
      { queueId: 'provider-readiness', label: 'Provider/model readiness', count: 0, severity: 'info', statusText: 'Open readiness detail', sourceCapabilityId: 'agent_admin.list_definitions', targetSurfaceId: 'surface-agent-admin-catalog', actionId: 'action-display-agent-catalog', traceRefs: ['trace-agent-admin-catalog'], redaction: 'provider secrets redacted' },
      { queueId: 'behavior-approval', label: 'Behavior proposals awaiting human decision', count: 2, severity: 'blocked', statusText: 'Approval required', sourceCapabilityId: 'agent_admin.submit_behavior_change_for_review', targetSurfaceId: 'surface-agent-behavior-proposal', actionId: 'action-submit-behavior-change', traceRefs: ['trace-agent-admin-behavior-review'], redaction: 'raw prompt/skill bodies omitted' },
      { queueId: 'tool-boundary-risk', label: 'Risky tool-boundary expansion attempts', count: 1, severity: 'urgent', statusText: 'Simulation denied side effect', sourceCapabilityId: 'agent_admin.simulate_tool_boundary', targetSurfaceId: 'surface-agent-tool-boundary-diff', actionId: 'action-simulate-tool-boundary', traceRefs: ['trace-agent-admin-tool-denied-email-send'], redaction: 'tool output omitted' },
      { queueId: 'prompt-risk-review', label: 'Prompt-risk autonomous review results', count: 1, severity: 'warning', statusText: 'Worker readiness deferred; open blocked review state', sourceCapabilityId: 'agent_admin.prompt_risk_review.read', targetSurfaceId: 'surface-agent-admin-prompt-risk-review', actionId: 'action-agentadmin-read-prompt-risk-review', traceRefs: ['trace-prompt-risk-provider-blocked-001'], redaction: 'browser-safe blocker summaries only' },
      { queueId: 'manifest-drift', label: 'Manifest drift and loader denials', count: 0, severity: 'info', statusText: 'Open manifest review history', sourceCapabilityId: 'agent_admin.get_manifest', targetSurfaceId: 'surface-agent-skill-manifest-diff', actionId: 'action-approve-skill-manifest', traceRefs: ['trace-agent-admin-manifest-manifest-agent-admin'], redaction: 'compact manifest summaries only' },
      { queueId: 'seed-import-readiness', label: 'Seed import readiness', count: 0, severity: 'info', statusText: 'Open seed provenance', sourceCapabilityId: 'agent_admin.list_seed_material', targetSurfaceId: 'surface-agent-seed-material', actionId: 'action-list-agent-seed-material', traceRefs: ['trace-agent-admin-seed-material'], redaction: 'raw seed content omitted' },
      { queueId: 'authority-expansion-risk', label: 'Authority expansion attempts', count: 1, severity: 'urgent', statusText: 'Review denied expansion evidence', sourceCapabilityId: 'agent_admin.simulate_tool_boundary', targetSurfaceId: 'surface-agent-tool-boundary-diff', actionId: 'action-simulate-tool-boundary', traceRefs: ['trace-agent-admin-tool-denied-email-send'], redaction: 'side-effect details summarized' }
    ],
    authorizedActions: [
      { actionId: 'action-display-agent-catalog', label: 'Browse managed agents', capabilityId: 'agent_admin.list_definitions', resultSurfaceId: 'surface-agent-admin-catalog' },
      { actionId: 'action-propose-prompt-diff', label: 'Draft prompt proposal', capabilityId: 'agent_admin.draft_behavior_change', resultSurfaceId: 'surface-agent-prompt-governance', approvalRequired: true },
      { actionId: 'action-test-agent-prompt', label: 'Run no-side-effect test', capabilityId: 'agent_admin.draft_behavior_change', resultSurfaceId: 'surface-agent-test-console' },
      { actionId: 'action-list-agent-seed-material', label: 'Review seed material', capabilityId: 'agent_admin.list_seed_material', resultSurfaceId: 'surface-agent-seed-material' },
      { actionId: 'action-open-agent-trace', label: 'Open Agent Admin traces', capabilityId: 'audit.trace.read', resultSurfaceId: 'surface-agent-admin-trace' }
    ],
    recentActivity: [
      { activityId: 'activity-agent-admin-protected-read', label: 'Catalog read protected by selected AuthContext', summary: 'Scoped AgentDefinition projection returned browser-safe readiness summaries.', traceId: 'trace-agent-admin-catalog' },
      { activityId: 'activity-agent-admin-tool-denial', label: 'Tool-boundary denial preserved', summary: 'Side-effecting email grant request was denied and routed to review.', traceId: 'trace-agent-admin-tool-denied-email-send' }
    ],
    hero: { title: 'Govern managed agents safely', scopeLabel: 'Tenant Admin · organization scope', scopeType: 'TENANT', adminLevel: 'Agent steward', redactionSummary: 'Provider secrets, raw prompts, raw skills, raw references, hidden authority, and cross-tenant evidence are omitted.' },
    readiness: 'ready_with_attention',
    capabilityIds: ['agent_admin.list_definitions', 'agent_admin.draft_behavior_change', 'agent_admin.simulate_tool_boundary', 'agent_admin.prompt_risk_review.read', 'audit.trace.read'],
    redaction: { browserSafe: true, omittedFieldKeys: ['rawPromptBody', 'rawSkillBody', 'rawReferenceBody', 'providerCredentialValue', 'rawJwt'], previewLimitChars: 220 },
    traceRefs: ['trace-agent-admin-dashboard', 'trace-agent-admin-catalog'],
    systemStates: ['loading', 'empty', 'forbidden', 'stale', 'partial-data', 'blocked_provider_or_runtime']
  },
  [agentAdminSurfaceActions.displayDashboard, agentAdminSurfaceActions.displayCatalog, agentAdminSurfaceActions.proposePromptDiff, agentAdminSurfaceActions.submitBehaviorChange, agentAdminSurfaceActions.simulateToolBoundary, agentAdminSurfaceActions.listSeedMaterial, agentAdminSurfaceActions.testPrompt, agentAdminSurfaceActions.readPromptRiskReview, agentAdminSurfaceActions.openAgentTrace]
);

export const agentAdminCatalogSurface = envelope(
  'surface-agent-admin-catalog',
  'list-search',
  'Agent Admin catalog',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.catalog.v1',
    query: 'tenant:tenant-acme scoped:true',
    rows: [
      { id: 'agent-admin-agent', rowType: 'AgentDefinition', displayName: 'Agent Admin Agent', status: 'active', authorityLevel: 'APPROVAL_REQUIRED', readinessSummary: 'Ready with 2 approval-gated behavior tasks', attentionSummary: 'Prompt-risk review requires human decision', targetSurfaceId: 'surface-agent-admin-detail', openActionId: 'action-open-agent-detail', safeActionContext: { agentDefinitionId: 'agent-admin-agent' }, placement: 'WORKSTREAM', functionalAreaId: 'agent-admin', modelConfigRefId: 'model-safe-default', providerStatus: 'ready', seedStatus: 'starter-v1', tracePolicy: 'PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace', traceId: 'trace-agent-admin-definition-agent-admin-agent' },
      { id: 'user-admin-agent', rowType: 'AgentDefinition', displayName: 'User Admin Agent', status: 'active', authorityLevel: 'APPROVAL_REQUIRED', readinessSummary: 'Ready with no provider blockers', attentionSummary: 'No behavior approval waiting', targetSurfaceId: 'surface-agent-admin-detail', openActionId: 'action-open-agent-detail', safeActionContext: { agentDefinitionId: 'user-admin-agent' }, placement: 'WORKSTREAM', functionalAreaId: 'user-admin', modelConfigRefId: 'model-safe-default', providerStatus: 'ready', seedStatus: 'starter-v1', tracePolicy: 'PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace', traceId: 'trace-agent-admin-definition-user-admin-agent' }
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
      loading: 'Loading view while backend agent_admin.list_definitions reads scoped AgentDefinition projections.',
      empty: 'Empty when no governed AgentDefinition records are seeded.',
      forbidden: 'TARGET_NOT_FOUND_OR_FORBIDDEN hides cross-tenant AgentDefinition ids and row counts.',
      validation: 'validation-error preserves correlation id and safe input summary for behavior-change actions.',
      approvalRequired: 'approval-required is shown before prompt, manifest, model, or tool-boundary activation.',
      providerBlocked: 'blocked_provider_or_runtime renders safe recovery without provider secrets.',
      traceLinked: 'PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, and protected-read traces are linked.'
    }
  },
  [agentAdminSurfaceActions.displayCatalog, agentAdminSurfaceActions.openAgentDetail, agentAdminSurfaceActions.openAgentTrace]
);

export const agentAdminDetailSurface = envelope(
  'surface-agent-admin-detail',
  'show-inspection',
  'Agent Admin readiness detail',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.detail.v1',
    surfaceContractAliases: ['agent_admin.definition.v1'],
    recordId: 'agent-admin-agent',
    recordLabel: 'Agent Admin Agent',
    recordKind: 'AgentDefinition',
    summary: 'Backend-authoritative AgentDefinition detail; behavior changes must use deterministic proposal/review/activation commands and AgentAdminAgent guidance remains read-only.',
    detailSummary: { surfaceId: 'surface-agent-admin-detail', title: 'Agent readiness/behavior inspection', type: 'show-inspection', contract: 'agent_admin.detail.v1', lifecycleState: 'active', readinessState: 'blocked_provider_or_runtime', readOnlyNotice: 'No inline mutation; use dedicated governed task surfaces.' },
    scopeSummary: { selectedAuthContextId: 'membership-admin', scopeType: 'tenant', governanceAuthorized: true, visibilityDecision: 'visible' },
    readinessNarrative: { outcome: 'blocked_provider_or_runtime', providerModelReadinessCategory: 'blocked_provider_or_runtime', promptRiskStatus: 'deferred_until_provider_runtime_configured', blockedReasons: ['Provider/model runtime is not fully ready; no fake success is shown.'], noFakeSuccess: true },
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
    behaviorArtifactCards: [
      { artifactCategory: 'prompt', displayLabel: 'Prompt', actionId: 'action-agent-detail-open-prompt-governance', targetSurfaceId: 'surface-agent-prompt-governance', governedCapability: agentPromptReadCapability, redactionNote: 'Raw prompt text omitted.' },
      { artifactCategory: 'skill_manifest', displayLabel: 'Skill manifest', actionId: 'action-agent-detail-open-skill-manifest', targetSurfaceId: 'surface-agent-skill-manifest-diff', governedCapability: agentManifestReadCapability, redactionNote: 'Full skill/reference bodies omitted.' },
      { artifactCategory: 'tool_boundary', displayLabel: 'Governed tool boundary', actionId: 'action-agent-detail-open-tool-boundary', targetSurfaceId: 'surface-agent-tool-boundary-diff', governedCapability: 'agent_admin.get_tool_boundary', redactionNote: 'ToolPermissionBoundary denials preserved.' },
      { artifactCategory: 'model_ref', displayLabel: 'Model reference', actionId: 'action-agent-detail-open-model-refs', targetSurfaceId: 'surface-agent-model-refs', governedCapability: agentModelsReadCapability, redactionNote: 'Provider credentials redacted.' }
    ],
    taskEntryPoints: [
      { actionId: 'action-agent-detail-refresh', targetSurfaceId: 'surface-agent-admin-detail', governedCapability: agentDefinitionReadCapability, disabledReason: null },
      { actionId: 'action-agent-detail-run-test', targetSurfaceId: 'surface-agent-test-console', governedCapability: agentRuntimeTestCapability, disabledReason: null },
      { actionId: 'action-agent-detail-open-prompt-risk-review', targetSurfaceId: 'surface-agent-admin-prompt-risk-review', governedCapability: 'agent_admin.prompt_risk_review.read', disabledReason: 'Model-backed review fails closed when provider/runtime is unavailable.' },
      { actionId: 'action-agent-detail-open-activation', targetSurfaceId: 'surface-agent-activation-confirmation', governedCapability: agentDefinitionsManageCapability, disabledReason: 'Separate approval/provider prerequisites required.' },
      { actionId: 'action-agent-detail-open-deactivation', targetSurfaceId: 'surface-agent-deactivation-confirmation', governedCapability: agentDeactivateCapability, disabledReason: null },
      { actionId: 'action-agent-detail-open-rollback', targetSurfaceId: 'surface-agent-rollback-confirmation', governedCapability: agentRollbackCapability, disabledReason: 'Requires backend-visible activated proposal metadata.' },
      { actionId: 'action-agent-detail-open-trace', targetSurfaceId: 'surface-agent-admin-trace', governedCapability: 'audit.trace.read', disabledReason: null },
      { actionId: 'action-agent-detail-back-to-catalog', targetSurfaceId: 'surface-agent-admin-catalog', governedCapability: agentDefinitionsCapability, disabledReason: null }
    ],
    providerReadiness: { status: 'blocked_provider_or_runtime', safeReason: 'Provider alias readiness is fail-closed in fixture; credentials remain backend-only.', providerAlias: 'approved-primary', secretVisibility: 'redacted' },
    safeRedactionSummary: { rawPromptText: 'omitted', rawSkillReferenceBodies: 'omitted', providerCredentials: 'omitted', hiddenTenantCustomerIdentifiers: 'omitted', jwtSessionMaterial: 'omitted', rawTraceEvidence: 'role-gated' },
    seedStatus: { seedBundleId: 'starter-v1', contentVersion: 'v1', resourceId: 'agent-definition-agent-admin.yaml', tenantCustomized: false },
    permissionState: {
      canEdit: false,
      reason: 'Read surface only. Use inert behavior-change proposals; no direct mutation from frontend state or AgentAdminAgent guidance.',
      authoritativeCapabilityId: agentDefinitionReadCapability
    },
    audit: {
      lastEventType: 'AgentDefinitionDetailDisplayed',
      lastActor: 'Tenant Admin',
      traceIds: ['trace-agent-admin-definition-agent-admin-agent', 'trace-agent-admin-protected-read']
    },
    redaction: { browserSafe: true, omittedFieldKeys: ['rawPromptBody', 'rawSkillBody', 'rawReferenceBody', 'rawProviderCredential', 'providerCredentialValue', 'rawJwt'], previewLimitChars: 220 },
    noDirectMutation: true
  },
  [agentAdminSurfaceActions.activateAgentDefinition, agentAdminSurfaceActions.deactivateAgentDefinition, agentAdminSurfaceActions.proposePromptDiff, agentAdminSurfaceActions.simulateToolBoundary, agentAdminSurfaceActions.manageModelRef, agentAdminSurfaceActions.openAgentTrace]
);

export const agentPromptGovernanceSurface = envelope(
  'surface-agent-prompt-governance',
  'governance-diff',
  'Prompt governance review',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.prompt_governance.v1',
    proposalId: 'proposal-agent-admin-prompt-001',
    lifecycleState: 'draft',
    source: 'Backend-owned prompt governance evidence; AgentAdminAgent may draft rationale only.',
    governanceSummary: {
      surfaceId: 'surface-agent-prompt-governance',
      contract: 'agent_admin.prompt_governance.v1',
      managedAgentDisplayName: 'Agent Admin Agent',
      proposalState: 'draft',
      reviewState: 'approval-required',
      riskClass: 'medium',
      providerModelReadinessCategory: 'blocked_provider_or_runtime',
      noDirectActivation: true
    },
    scopeSummary: { selectedContextId: 'membership-admin', scopeType: 'tenant', governanceAuthorized: true, visibilityDecision: 'visible' },
    redactedPromptDiff: {
      beforeSummary: 'Active prompt requires backend authorization, ToolPermissionBoundary enforcement, provider fail-closed behavior, and no direct mutation.',
      afterSummary: 'Draft adds clearer evidence citation and redaction wording; full prompt body remains hidden with redactedPreview only.',
      redactedPreview: 'You are AgentAdminAgent. Cite PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace...',
      omittedSectionCounts: { rawPromptBody: 1, skillReferenceBodies: 0, providerCredentials: 0 },
      redactionNotes: 'Raw prompt bodies, full skills/references, provider secrets, raw loader inputs, JWTs, and hidden tenant/customer ids are omitted.'
    },
    impactSummary: {
      authorityToolDataBoundaryChanges: 'No authority, tool, or data-boundary expansion is granted by this surface.',
      providerRuntimeReadiness: 'blocked_provider_or_runtime',
      simulationOrRiskReviewRequired: true
    },
    riskAndEvidence: {
      promptRiskReviewStatus: 'blocked_provider_or_runtime',
      safeEvidenceRefs: ['PromptAssemblyTrace', 'SkillLoadTrace', 'ReferenceLoadTrace', 'AgentWorkTrace'],
      requiredHumanReviewReasons: ['behavior-change', 'prompt-governance', 'authority-boundary']
    },
    reviewState: {
      allowedDecisions: ['submit-review', 'approve-after-review', 'reject-with-reason'],
      disabledDecisions: ['activate-directly', 'edit-raw-prompt', 'change-provider-config'],
      rejectionReasonRequired: true,
      approvalRequired: true,
      nextRequiredSurface: 'surface-agent-behavior-proposal',
      noDirectMutation: true
    },
    safeRedactionSummary: {
      rawPromptText: 'omitted',
      skillReferenceBodies: 'omitted',
      providerCredentials: 'omitted',
      hiddenScopes: 'omitted',
      rawTraceEvidence: 'role-gated',
      bearerTokens: 'omitted'
    },
    traceLinks: ['trace-agent-admin-prompt-prompt-agent-admin-system', 'trace-agent-admin-prompt-governance', 'trace-agent-admin-behavior-draft'],
    states: ['loading', 'ready', 'submitting', 'simulating', 'approval-required', 'validation-error', 'conflict', 'forbidden', 'not-found-or-redacted', 'stale/reconnect', 'partial-data', 'provider-fail-closed', 'no-op', 'failure'],
    noDirectActivation: true
  },
  [
    agentAdminSurfaceActions.promptGovernanceRefresh,
    agentAdminSurfaceActions.promptGovernanceSimulate,
    agentAdminSurfaceActions.promptGovernanceSubmitReview,
    agentAdminSurfaceActions.promptGovernanceApprove,
    agentAdminSurfaceActions.promptGovernanceReject,
    agentAdminSurfaceActions.promptGovernanceOpenRiskReview,
    agentAdminSurfaceActions.promptGovernanceOpenTrace,
    agentAdminSurfaceActions.promptGovernanceBackToDetail
  ]
);

export const agentSkillManifestSurface = envelope(
  'surface-agent-skill-manifest-diff',
  'governance-diff',
  'Skill/reference manifest and loader review',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.skill_manifest_diff.v1',
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
  [
    agentAdminSurfaceActions.skillManifestRefresh,
    agentAdminSurfaceActions.skillManifestSimulate,
    agentAdminSurfaceActions.skillManifestSubmitReview,
    agentAdminSurfaceActions.skillManifestApprove,
    agentAdminSurfaceActions.skillManifestReject,
    agentAdminSurfaceActions.skillManifestOpenToolBoundary,
    agentAdminSurfaceActions.skillManifestOpenModelRefs,
    agentAdminSurfaceActions.skillManifestOpenTrace,
    agentAdminSurfaceActions.skillManifestBackToDetail
  ]
);

export const agentToolBoundarySurface = envelope(
  'surface-agent-tool-boundary-diff',
  'governance-diff',
  'Tool boundary simulation review',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.tool_boundary_diff.v1',
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
  [
    agentAdminSurfaceActions.toolBoundaryRefresh,
    agentAdminSurfaceActions.toolBoundarySimulate,
    agentAdminSurfaceActions.toolBoundarySubmitReview,
    agentAdminSurfaceActions.toolBoundaryApprove,
    agentAdminSurfaceActions.toolBoundaryReject,
    agentAdminSurfaceActions.toolBoundaryOpenModelRefs,
    agentAdminSurfaceActions.toolBoundaryOpenTrace,
    agentAdminSurfaceActions.toolBoundaryBackToDetail
  ]
);

export const agentModelRefsSurface = envelope(
  'surface-agent-model-refs',
  'governance-diff',
  'Model reference proposal/review',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.model_refs.v1',
    modelReferenceSummary: {
      surfaceId: 'surface-agent-model-refs',
      title: 'Model reference proposal/review',
      type: 'governance-diff / show-inspection',
      contract: 'agent_admin.model_refs.v1',
      selectedManagedAgent: 'Agent Admin Agent',
      activeModelReferenceLabel: 'Approved primary model alias',
      proposedModelReferenceLabel: 'review-required',
      providerReadinessCategory: 'blocked_provider_or_runtime',
      runtimeReadinessCategory: 'blocked_provider_or_runtime',
      proposalState: 'approval-required',
      reviewState: 'pending-human-review',
      riskClass: 'high',
      approvalRequired: true,
      noDirectActivation: true
    },
    scopeSummary: { selectedAuthContextId: 'membership-agent-admin', scopeType: 'tenant', tenantId: 'tenant-starter', actorRoleSummary: ['TENANT_ADMIN', 'AUDITOR'], governanceAuthorized: true },
    redactedModelReferenceDiff: {
      modelAliasBefore: 'Approved primary model alias',
      modelAliasAfter: 'reviewed-provider-alias',
      providerCategory: 'browser-safe-alias-only',
      routingProfile: 'tenant-scoped managed-agent runtime',
      temperatureLimitClass: 'policy-controlled',
      retrievalReferenceImplications: 'Prompt/skill/reference bodies remain omitted.',
      toolCompatibilityNotes: 'ToolPermissionBoundary remains backend-authoritative.',
      dataBoundaryEffects: 'No cross-tenant/customer visibility.',
      fallbackDegradationBehavior: 'provider-fail-closed',
      omittedSectionCount: 8,
      redactionNotes: ['provider credentials omitted', 'raw provider errors omitted', 'raw prompt/skill/reference bodies omitted', 'hidden scopes omitted'],
      conflictState: 'none'
    },
    providerReadiness: {
      category: 'blocked_provider_or_runtime',
      configuredState: 'blocked_provider_or_runtime',
      failClosedReason: 'Provider/runtime readiness is unavailable in fixtures, so no model-backed success is claimed.',
      runtimeDependencyStatus: 'blocked_provider_or_runtime',
      modelAvailabilityStatus: 'blocked_provider_or_runtime',
      recoveryRoutes: ['action-agent-model-refs-run-test', 'action-agent-model-refs-open-trace', 'action-agent-model-refs-back-to-detail'],
      noFakeSuccess: true,
      secretVisibility: 'redacted'
    },
    impactSummary: { downstreamProposalLifecycleBlockers: ['approval-required', 'provider-readiness', 'activation-separate-surface'], simulationPromptRiskToolBoundaryOrHumanApprovalRequired: true },
    riskAndEvidence: { riskClass: 'high', safeEvidenceRefs: ['ModelConfigRef', 'AgentDefinition', 'AgentWorkTrace'], requiredHumanReviewReasons: ['model-provider-routing', 'runtime-readiness', 'tenant-scope-impact'] },
    reviewState: { allowedDecisions: ['refresh', 'submit-review', 'approve-after-review', 'reject-with-reason', 'open-prompt-governance', 'open-tool-boundary', 'open-trace', 'back-to-detail'], disabledDecisions: ['activate-directly', 'edit-provider-credentials', 'raw-model-config-edit', 'provider-secret-entry', 'cross-scope-routing'], approvalRequired: true, rejectionReasonRequired: true, providerRuntimeBlockedState: 'blocked_provider_or_runtime', nextRequiredSurface: 'surface-agent-behavior-proposal', noDirectMutation: true },
    safeRedactionSummary: { providerCredentials: 'omitted', rawProviderModelResponses: 'omitted', rawPromptText: 'omitted', skillReferenceBodies: 'omitted', hiddenScopes: 'omitted', rawTraceEvidence: 'role-gated', bearerTokens: 'omitted' },
    traceLinks: ['trace-agent-admin-model-refs', 'trace-agent-work-88'],
    states: ['loading', 'ready', 'empty-no-active-model-reference-change', 'submitting', 'testing', 'approval-required', 'review-submitted', 'review-approved', 'review-rejected', 'validation-error', 'conflict', 'forbidden', 'not-found-or-redacted', 'stale/reconnect', 'partial-data', 'provider-fail-closed', 'no-op', 'failure'],
    noFakeSuccess: true,
    noDirectActivation: true,
    noDirectMutation: true
  },
  [agentAdminSurfaceActions.modelRefsRefresh, agentAdminSurfaceActions.modelRefsRunTest, agentAdminSurfaceActions.modelRefsSubmitReview, agentAdminSurfaceActions.modelRefsApprove, agentAdminSurfaceActions.modelRefsReject, agentAdminSurfaceActions.modelRefsOpenPromptGovernance, agentAdminSurfaceActions.modelRefsOpenToolBoundary, agentAdminSurfaceActions.modelRefsOpenTrace, agentAdminSurfaceActions.modelRefsBackToDetail]
);

export const agentSeedMaterialSurface = envelope(
  'surface-agent-seed-material',
  'list-search',
  'Agent seed material',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.seed_material.v1',
    query: { bundle: 'starter-v1', tenantScoped: true },
    rows: [
      { id: 'seed-agent-admin-agent', artifactKind: 'AgentDefinition', artifactId: 'agent-admin-agent', displayName: 'Agent Admin Agent', status: 'active', seedBundleId: 'starter-v1', tenantCustomized: false, checksum: 'sha256:agent-admin-definition', recommendedManagedAgentTarget: 'agent-admin-agent', traceId: 'trace-agent-admin-seed-agent-admin-agent' },
      { id: 'seed-prompt-agent-admin-system', artifactKind: 'PromptDocument', artifactId: 'prompt-agent-admin-system', status: 'active', seedBundleId: 'starter-v1', tenantCustomized: false, checksum: 'sha256:prompt-agent-admin-system', traceId: 'trace-agent-admin-seed-prompt-agent-admin-system' },
      { id: 'seed-tool-boundary-agent-admin', artifactKind: 'ToolPermissionBoundary', artifactId: 'tool-boundary-agent-admin', status: 'active', seedBundleId: 'starter-v1', tenantCustomized: false, checksum: 'sha256:tool-boundary-agent-admin', traceId: 'trace-agent-admin-seed-tool-boundary-agent-admin' }
    ],
    pageInfo: { totalKnownCount: 3 },
    redaction: 'Seed provenance shows resource ids, checksums, tenantCustomized, and trace links; raw prompt/skill/reference bodies and provider secrets are omitted.',
    traceLinks: ['trace-agent-admin-seed-material'],
    provenanceInspection: { state: 'ready-provenance', selectedSeedMaterialId: 'seed-agent-admin-agent', artifactKind: 'AgentDefinition', displayName: 'Agent Admin Agent', sourceLineage: 'starter-v1 / agent-definition-agent-admin.yaml', reviewHistorySummary: 'Seed provenance was imported through the backend seed loader and is trace-linked for Agent Admin review.', rawContentVisible: false },
    mobileFallback: 'table-to-card',
    systemStates: ['loading', 'empty', 'forbidden', 'stale']
  },
  [agentAdminSurfaceActions.listSeedMaterial, agentAdminSurfaceActions.importSeedDefaults, agentAdminSurfaceActions.seedMaterialOpenAgentDetail, agentAdminSurfaceActions.openAgentTrace]
);

export const agentTestConsoleSurface = envelope(
  'surface-agent-test-console',
  'workflow-status',
  'No-side-effect agent test console',
  'agent-admin-agent',
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
  'agent-admin-agent',
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


export const agentActivationConfirmationSurface = envelope(
  'surface-agent-activation-confirmation',
  'lifecycle-confirmation',
  'Confirm agent behavior activation',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.activation_confirmation.v1',
    recordId: 'agent-admin-agent',
    recordLabel: 'Agent Admin Agent',
    lifecycleAction: 'activate',
    currentStatus: 'approved_not_active',
    proposedStatus: 'active',
    impactSummary: 'Activation changes the runtime AgentDefinition only after backend approval, active version, rollback metadata, provider readiness, and idempotency checks pass.',
    approvalState: 'approved proposal required; disabled until backend confirms approval',
    policyBasis: 'managed-agent-governance activation policy; no model or frontend state may activate directly',
    idempotencyKeyHint: 'client-generated activation key bound to proposal id',
    disabledReason: 'Activation remains blocked until backend confirms approved proposal, active version, rollback metadata, and idempotency key.',
    evidenceRefs: ['proposal-agent-admin-prompt-001', 'PromptAssemblyTrace', 'ToolPermissionBoundary simulation'],
    traceRefs: ['trace-agent-admin-behavior-activation-blocked', 'trace-agent-admin-behavior-review'],
    actionContext: { proposalId: 'proposal-agent-admin-prompt-001', agentDefinitionId: 'agent-admin-agent' }
  },
  [agentAdminSurfaceActions.activateBehaviorChange, agentAdminSurfaceActions.openAgentTrace]
);

export const agentDeactivationConfirmationSurface = envelope(
  'surface-agent-deactivation-confirmation',
  'lifecycle-confirmation',
  'Confirm agent deactivation',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.deactivation_confirmation.v1',
    recordId: 'agent-admin-agent',
    recordLabel: 'Agent Admin Agent',
    lifecycleAction: 'deactivate',
    currentStatus: 'active',
    proposedStatus: 'deactivated',
    impactSummary: 'Deactivation prevents runtime invocation and governed loader access for this managed agent after backend approval and audit checks.',
    approvalState: 'human approval required',
    policyBasis: 'managed-agent-governance lifecycle policy',
    idempotencyKeyHint: 'client-generated deactivation key',
    evidenceRefs: ['active AgentDefinition', 'recent AgentWorkTrace'],
    traceRefs: ['trace-agent-admin-definition-agent-admin-agent'],
    actionContext: { agentDefinitionId: 'agent-admin-agent' }
  },
  [agentAdminSurfaceActions.deactivateAgentDefinition, agentAdminSurfaceActions.openAgentTrace]
);

export const agentRollbackConfirmationSurface = envelope(
  'surface-agent-rollback-confirmation',
  'lifecycle-confirmation',
  'Confirm agent behavior rollback',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.rollback_confirmation.v1',
    recordId: 'agent-admin-agent',
    recordLabel: 'Agent Admin Agent',
    lifecycleAction: 'rollback',
    currentStatus: 'activated_proposal',
    proposedStatus: 'previous_active_version',
    impactSummary: 'Rollback restores the prior active behavior snapshot while preserving audit history, proposal evidence, and rollback traces.',
    approvalState: 'rollback metadata required',
    policyBasis: 'managed-agent-governance rollback policy',
    idempotencyKeyHint: 'server-issued rollback-safe key preferred',
    disabledReason: 'Rollback requires activated proposal metadata and backend command authority.',
    evidenceRefs: ['rollback snapshot', 'activation audit event'],
    traceRefs: ['trace-agent-admin-behavior-activation-blocked'],
    actionContext: { proposalId: 'proposal-agent-admin-prompt-001', agentDefinitionId: 'agent-admin-agent' }
  },
  [agentAdminSurfaceActions.rollbackBehaviorChange, agentAdminSurfaceActions.openAgentTrace]
);

export const agentAdminPromptRiskReviewSurface = envelope(
  'surface-agent-admin-prompt-risk-review',
  'workflow-status',
  'Agent Admin prompt-risk review',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.prompt_risk_review_task.v1',
    workflowId: 'workflow-agent-admin-prompt-risk-review-001',
    taskId: 'prompt-risk-review-001',
    autonomousAgentTaskId: 'akka-task-prompt-risk-review-001',
    status: 'blocked_provider_or_runtime',
    summary: 'Prompt-risk AutonomousAgent worker remains deferred for this slice; backend provider/runtime readiness is blocked, so no model-backed advisory success is claimed.',
    initiatingCapabilityId: agentPromptRiskStartCapability,
    requiredCapabilityId: agentPromptRiskReadCapability,
    taskKind: 'autonomous-agent-analysis',
    progress: { percent: 0, summary: 'blocked_provider_or_runtime' },
    scope: { scopeType: 'TENANT', tenantId: authContext.tenantId, customerId: undefined },
    targetAgentDefinitionId: 'agent-admin-agent',
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
    requiredHumanReviewReasons: ['prompt authority change', 'ToolPermissionBoundary expansion', 'provider/runtime readiness deferred'],
    evidenceRefs: ['agentAdminEvidence.read', 'readSkill:agent-admin-prompt-risk-review', 'readReferenceDoc:agent-admin-prompt-risk-review', 'proposal:proposal-agent-admin-prompt-risk-001'],
    providerFailures: ['blocked_provider_or_runtime'],
    traceIds: ['trace-prompt-risk-provider-blocked-001', 'trace-prompt-risk-assembly-blocked-001'],
    resultReviewState: 'blocked_provider_or_runtime',
    noDirectMutation: true,
    activationBlockedUntilHumanDecision: true,
    safety: 'Prompt-risk worker output is deferred/blocked. No model-less, deterministic, or fixture-only advisory success is rendered; accepting this state cannot activate prompts, skills, references, models, or tool boundaries.'
  },
  [agentAdminSurfaceActions.readPromptRiskReview, agentAdminSurfaceActions.acceptPromptRiskReviewResult, agentAdminSurfaceActions.rejectPromptRiskReviewResult, agentAdminSurfaceActions.cancelPromptRiskReview, agentAdminSurfaceActions.openAgentTrace]
);

export const agentAdminAgentBlockedSystemMessageSurface = envelope(
  'surface-agent-admin-agent-provider-blocked',
  'system_message',
  'AgentAdminAgent unavailable',
  'agent-admin-agent',
  {
    status: 'blocked_provider_or_runtime',
    severity: 'warning',
    title: 'AgentAdminAgent unavailable',
    summary: 'Model-backed AgentAdminAgent guidance was blocked before a response was produced.',
    message: 'Model-backed AgentAdminAgent guidance was blocked before a response was produced. Backend provider/runtime configuration must be restored before guidance can run.',
    recoverySteps: ['Verify model provider configuration and active ModelConfigRef on the backend.', 'Review PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, agentAdminEvidence.read, and AgentWorkTrace entries for this correlation id.', 'Retry after backend configuration is restored; use deterministic Agent Admin surfaces for catalog reads and behavior-change lifecycle commands.'],
    workstreamEntryId: 'item-agent-admin-agent-provider-blocked',
    producingAgentId: 'agent-admin-agent',
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
  'agent-admin-agent',
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


const agentAdminDocAgent = {
  agentDefinitionId: 'agent-admin-agent',
  agentName: 'Agent Admin',
  shortPurpose: 'AI-assisted document editing for managed agent prompts, skills, and reference docs.',
  purpose: 'Improve managed-agent behavior through prompt, skill, and reference-doc edits.',
  workstreamDomain: 'core-starter/agent-admin',
  lastEditTime: generatedAt,
  actionId: 'action-agent-admin-open-agent-detail',
  targetSurfaceId: 'surface-agent-admin-agent-detail'
};

const agentAdminPromptDoc = {
  kind: 'prompt',
  documentId: 'prompt-agent-admin-system',
  title: 'Agent Admin prompt',
  description: 'Current system prompt used by the Agent Admin functional agent.',
  currentVersion: 2,
  updatedAt: generatedAt
};

const agentAdminReferenceDoc = {
  stableReferenceId: 'agent-admin-runtime-read-traces',
  documentId: 'reference-agent-admin-runtime-read-traces',
  name: 'Runtime read trace guide',
  description: 'Helps the model decide when to inspect runtime read metadata.',
  currentVersion: 1,
  updatedAt: generatedAt,
  actionId: 'action-agent-admin-open-reference-doc'
};

const agentAdminSkillDoc = {
  stableSkillId: 'agent-admin-doc-editing',
  documentId: 'skill-agent-admin-doc-editing',
  name: 'Document editing guidance',
  purpose: 'Preserve Markdown while proposing safe behavior improvements.',
  currentVersion: 3,
  updatedAt: generatedAt,
  referenceDocs: [agentAdminReferenceDoc],
  actionId: 'action-agent-admin-open-skill-doc'
};

const agentAdminPromptVersion = {
  agentDefinitionId: agentAdminDocAgent.agentDefinitionId,
  kind: 'prompt',
  documentId: agentAdminPromptDoc.documentId,
  version: 2,
  currentVersion: true,
  editable: true,
  title: agentAdminPromptDoc.title,
  description: agentAdminPromptDoc.description,
  contentBody: '# Agent Admin\n\nHelp SaaS admins improve agent behavior by editing prompt, skill, and reference docs while preserving Markdown.',
  contentChecksum: 'sha256-agent-admin-prompt-v2',
  createdAt: generatedAt,
  actorAccountId: 'acct-admin',
  editSessionTranscriptSummary: 'Initial SaaS admin doc-editing prompt baseline.'
};

const agentAdminSkillVersion = {
  agentDefinitionId: agentAdminDocAgent.agentDefinitionId,
  kind: 'skill',
  documentId: agentAdminSkillDoc.documentId,
  version: 3,
  currentVersion: true,
  editable: true,
  title: agentAdminSkillDoc.name,
  description: agentAdminSkillDoc.purpose,
  contentBody: '## Skill\n\nDraft complete Markdown proposals from free-form SaaS admin instructions.',
  contentChecksum: 'sha256-agent-admin-skill-v3',
  createdAt: generatedAt,
  actorAccountId: 'acct-admin',
  editSessionTranscriptSummary: 'Expanded Markdown preservation guidance.'
};

const agentAdminReferenceVersion = {
  agentDefinitionId: agentAdminDocAgent.agentDefinitionId,
  kind: 'reference',
  documentId: agentAdminReferenceDoc.documentId,
  version: 1,
  currentVersion: true,
  editable: true,
  title: agentAdminReferenceDoc.name,
  description: agentAdminReferenceDoc.description,
  contentBody: '## Runtime read traces\n\nTrace rows show metadata only and never include full skill or reference content.',
  contentChecksum: 'sha256-agent-admin-reference-v1',
  createdAt: generatedAt,
  actorAccountId: 'acct-admin',
  editSessionTranscriptSummary: 'Created reference doc for runtime read trace interpretation.'
};

const agentAdminAvailableActions = (...actions: SurfaceAction[]) => actions.map((action) => ({
  actionId: action.actionId,
  label: action.label,
  governedToolId: action.governedToolId,
  capabilityId: action.capabilityId,
  resultSurfaceId: action.resultSurface?.updateSurfaceId,
  approvalRequired: Boolean(action.requiresApproval),
  denialHint: action.disabled?.message
}));

export const agentAdminDocEditingBlankSurface = envelope(
  'surface-agent-admin-blank',
  'blank',
  'Agent Admin',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.blank.v1',
    state: 'ready-empty',
    emptyCopy: 'Choose Show dashboard, Show agents, or use the composer to find an agent document to improve.',
    composerAvailable: true,
    clearWorkstream: { enabled: false, state: 'no-op' },
    availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.showDashboard, agentAdminSurfaceActions.showAgents),
    authorizedActions: ['action-agent-admin-show-dashboard', 'action-agent-admin-show-agents'],
    systemStates: ['ready-empty', 'forbidden', 'failure']
  },
  [agentAdminSurfaceActions.showDashboard, agentAdminSurfaceActions.showAgents]
);

export const agentAdminDocEditingDashboardSurface = envelope(
  'surface-agent-admin-dashboard',
  'dashboard',
  'Agent Admin dashboard',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.dashboard.v1',
    thingsYouCanDo: [{ cardId: 'total-agents', label: 'Agents', count: 1, actionId: 'action-agent-admin-show-agents', targetSurfaceId: 'surface-agent-admin-agent-list' }],
    recentlyChangedAgents: [agentAdminDocAgent],
    thingsNeedAttention: [],
    availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.showDashboard, agentAdminSurfaceActions.showAgents, agentAdminSurfaceActions.openRuntimeTraces),
    authorizedActions: ['action-agent-admin-show-dashboard', 'action-agent-admin-show-agents', 'action-agent-admin-open-runtime-traces'],
    traceRefs: ['trace-agent-admin-doc-dashboard'],
    systemStates: ['ready', 'forbidden', 'failure']
  },
  [agentAdminSurfaceActions.showDashboard, agentAdminSurfaceActions.showAgents, agentAdminSurfaceActions.openRuntimeTraces]
);

export const agentAdminDocEditingAgentListSurface = envelope(
  'surface-agent-admin-agent-list',
  'list-search',
  'Agents',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.agent_list.v1',
    filters: { agentName: '', workstreamOrDomain: '' },
    rows: [agentAdminDocAgent],
    totalCount: 1,
    filteredCount: 1,
    rowActionId: 'action-agent-admin-open-agent-detail',
    availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.showDashboard, agentAdminSurfaceActions.showAgents, agentAdminSurfaceActions.openAgentDetailDoc, agentAdminSurfaceActions.openRuntimeTraces),
    authorizedActions: ['action-agent-admin-show-dashboard', 'action-agent-admin-show-agents', 'action-agent-admin-open-agent-detail', 'action-agent-admin-open-runtime-traces'],
    traceRefs: ['trace-agent-admin-doc-agent-list'],
    systemStates: ['loading', 'ready', 'empty-no-agents', 'empty-no-filter-matches', 'forbidden', 'validation-error', 'failure'],
    state: 'ready'
  },
  [agentAdminSurfaceActions.showDashboard, agentAdminSurfaceActions.showAgents, agentAdminSurfaceActions.openAgentDetailDoc, agentAdminSurfaceActions.openRuntimeTraces]
);

export const agentAdminDocEditingAgentDetailSurface = envelope(
  'surface-agent-admin-agent-detail',
  'show-inspection',
  'Agent detail',
  'agent-admin-agent',
  {
    surfaceContract: 'agent_admin.agent_detail.v1',
    agent: { agentDefinitionId: agentAdminDocAgent.agentDefinitionId, agentName: agentAdminDocAgent.agentName, purpose: agentAdminDocAgent.purpose, workstreamDomain: agentAdminDocAgent.workstreamDomain, lastEditTime: generatedAt },
    prompt: agentAdminPromptDoc,
    skills: [agentAdminSkillDoc],
    referenceDocs: [agentAdminReferenceDoc],
    traceEntryPoints: [{ label: 'Runtime reads', actionId: 'action-agent-admin-open-runtime-traces', targetSurfaceId: 'surface-agent-admin-runtime-traces' }],
    availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.showAgents, agentAdminSurfaceActions.saveAgentProfile, agentAdminSurfaceActions.openPromptDoc, agentAdminSurfaceActions.openSkillDoc, agentAdminSurfaceActions.openReferenceDoc, agentAdminSurfaceActions.openCreateSkill, agentAdminSurfaceActions.openDeleteSkill, agentAdminSurfaceActions.openCreateReferenceDoc, agentAdminSurfaceActions.openDeleteReferenceDoc, agentAdminSurfaceActions.openRuntimeTraces),
    authorizedActions: ['action-agent-admin-show-agents', 'action-agent-admin-save-agent-profile', 'action-agent-admin-open-prompt-doc', 'action-agent-admin-open-skill-doc', 'action-agent-admin-open-reference-doc', 'action-agent-admin-open-create-skill', 'action-agent-admin-open-delete-skill', 'action-agent-admin-open-create-reference-doc', 'action-agent-admin-open-delete-reference-doc', 'action-agent-admin-open-runtime-traces'],
    traceRefs: ['trace-agent-admin-doc-agent-detail'],
    systemStates: ['ready', 'forbidden', 'failure']
  },
  [agentAdminSurfaceActions.showAgents, agentAdminSurfaceActions.saveAgentProfile, agentAdminSurfaceActions.openPromptDoc, agentAdminSurfaceActions.openSkillDoc, agentAdminSurfaceActions.openReferenceDoc, agentAdminSurfaceActions.openCreateSkill, agentAdminSurfaceActions.openDeleteSkill, agentAdminSurfaceActions.openCreateReferenceDoc, agentAdminSurfaceActions.openDeleteReferenceDoc, agentAdminSurfaceActions.openRuntimeTraces]
);

export const agentAdminDocEditingPromptDocSurface = envelope(
  'surface-agent-admin-prompt-doc',
  'document',
  'Agent Admin prompt',
  'agent-admin-agent',
  { surfaceContract: 'agent_admin.prompt_doc.v1', doc: agentAdminPromptVersion, readOnlyBanner: null, editInputEnabled: true, availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.docEditStart, agentAdminSurfaceActions.versionHistory, agentAdminSurfaceActions.versionDiff, agentAdminSurfaceActions.restoreVersion, agentAdminSurfaceActions.openRuntimeTraces, agentAdminSurfaceActions.openAgentDetailDoc), authorizedActions: ['action-agent-doc-edit-start', 'action-agent-doc-version-history', 'action-agent-doc-version-diff', 'action-agent-doc-version-restore', 'action-agent-admin-open-runtime-traces', 'action-agent-admin-open-agent-detail'], traceRefs: ['trace-agent-admin-doc-prompt-read'], systemStates: ['ready', 'historical-read-only', 'forbidden', 'validation-error', 'failure'] },
  [agentAdminSurfaceActions.docEditStart, agentAdminSurfaceActions.versionHistory, agentAdminSurfaceActions.versionDiff, agentAdminSurfaceActions.restoreVersion, agentAdminSurfaceActions.openRuntimeTraces, agentAdminSurfaceActions.openAgentDetailDoc]
);

export const agentAdminDocEditingSkillDocSurface = envelope(
  'surface-agent-admin-skill-doc',
  'document',
  'Document editing guidance',
  'agent-admin-agent',
  { surfaceContract: 'agent_admin.skill_doc.v1', doc: agentAdminSkillVersion, referenceDocs: [agentAdminReferenceDoc], readOnlyBanner: null, editInputEnabled: true, availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.docEditStart, agentAdminSurfaceActions.versionHistory, agentAdminSurfaceActions.versionDiff, agentAdminSurfaceActions.restoreVersion, agentAdminSurfaceActions.openRuntimeTraces, agentAdminSurfaceActions.openAgentDetailDoc), authorizedActions: ['action-agent-doc-edit-start', 'action-agent-doc-version-history', 'action-agent-doc-version-diff', 'action-agent-doc-version-restore', 'action-agent-admin-open-runtime-traces', 'action-agent-admin-open-agent-detail'], traceRefs: ['trace-agent-admin-doc-skill-read'], systemStates: ['ready', 'historical-read-only', 'forbidden', 'validation-error', 'failure'] },
  [agentAdminSurfaceActions.docEditStart, agentAdminSurfaceActions.versionHistory, agentAdminSurfaceActions.versionDiff, agentAdminSurfaceActions.restoreVersion, agentAdminSurfaceActions.openRuntimeTraces, agentAdminSurfaceActions.openAgentDetailDoc]
);

export const agentAdminDocEditingReferenceDocSurface = envelope(
  'surface-agent-admin-skill-reference-doc',
  'document',
  'Runtime read trace guide',
  'agent-admin-agent',
  { surfaceContract: 'agent_admin.skill_reference_doc.v1', doc: agentAdminReferenceVersion, readOnlyBanner: null, editInputEnabled: true, availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.docEditStart, agentAdminSurfaceActions.versionHistory, agentAdminSurfaceActions.versionDiff, agentAdminSurfaceActions.restoreVersion, agentAdminSurfaceActions.openRuntimeTraces, agentAdminSurfaceActions.openAgentDetailDoc), authorizedActions: ['action-agent-doc-edit-start', 'action-agent-doc-version-history', 'action-agent-doc-version-diff', 'action-agent-doc-version-restore', 'action-agent-admin-open-runtime-traces', 'action-agent-admin-open-agent-detail'], traceRefs: ['trace-agent-admin-doc-reference-read'], systemStates: ['ready', 'historical-read-only', 'forbidden', 'validation-error', 'failure'] },
  [agentAdminSurfaceActions.docEditStart, agentAdminSurfaceActions.versionHistory, agentAdminSurfaceActions.versionDiff, agentAdminSurfaceActions.restoreVersion, agentAdminSurfaceActions.openRuntimeTraces, agentAdminSurfaceActions.openAgentDetailDoc]
);

export const agentAdminDocEditingEditSessionSurface = envelope(
  'surface-agent-admin-edit-session',
  'workflow-status',
  'Edit session',
  'agent-admin-agent',
  { surfaceContract: 'agent_admin.edit_session.v1', session: { sessionId: 'edit-session-agent-admin-prompt-2', agentDefinitionId: agentAdminDocAgent.agentDefinitionId, kind: 'prompt', documentId: agentAdminPromptDoc.documentId, baseVersion: 2, status: 'proposal_ready', instructions: [{ actorAccountId: 'acct-admin', instructions: 'Make the prompt more concise.' }], proposedContent: '# Agent Admin\n\nImprove behavior through concise Markdown doc editing guidance.', changeSummary: 'Condensed prompt while preserving heading structure.', clarifyingQuestion: null, warnings: ['Advisory only; SaaS admin Save creates version 3.'], startedAt: generatedAt, endedAt: null }, target: { agentDefinitionId: agentAdminDocAgent.agentDefinitionId, kind: 'prompt', documentId: agentAdminPromptDoc.documentId, baseVersion: 2 }, saveCreatesNewCurrentVersion: true, warningsAdvisoryOnly: true, availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.docEditRevise, agentAdminSurfaceActions.docEditSave, agentAdminSurfaceActions.docEditCancel, agentAdminSurfaceActions.versionDiff), authorizedActions: ['action-agent-doc-edit-revise', 'action-agent-doc-edit-save', 'action-agent-doc-edit-cancel', 'action-agent-doc-version-diff'], traceRefs: ['trace-agent-admin-doc-edit-session'], systemStates: ['drafting', 'clarification-needed', 'proposed', 'refining', 'saving', 'cancelled', 'saved', 'provider-unavailable', 'stale-current-version', 'forbidden', 'failure'] },
  [agentAdminSurfaceActions.docEditRevise, agentAdminSurfaceActions.docEditSave, agentAdminSurfaceActions.docEditCancel, agentAdminSurfaceActions.versionDiff]
);

export const agentAdminDocEditingVersionHistorySurface = envelope(
  'surface-agent-admin-version-history',
  'history',
  'Version history',
  'agent-admin-agent',
  { surfaceContract: 'agent_admin.version_history.v1', agentDefinitionId: agentAdminDocAgent.agentDefinitionId, kind: 'prompt', documentId: agentAdminPromptDoc.documentId, rows: [{ version: 1, currentVersion: false, createdAt: '2026-05-18T12:00:00.000Z', label: 'version 1' }, { version: 2, currentVersion: true, createdAt: generatedAt, label: 'version 2' }], selectedVersion: { ...agentAdminPromptVersion, version: 1, currentVersion: false, editable: false, editSessionTranscriptSummary: 'Original version.' }, readOnlyBanner: 'Historical version: read-only.', availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.versionHistory, agentAdminSurfaceActions.versionDiff, agentAdminSurfaceActions.restoreVersion, agentAdminSurfaceActions.openAgentDetailDoc), authorizedActions: ['action-agent-doc-version-history', 'action-agent-doc-version-diff', 'action-agent-doc-version-restore', 'action-agent-admin-open-agent-detail'], traceRefs: ['trace-agent-admin-doc-version-history'], systemStates: ['ready', 'no-versions', 'forbidden', 'failure'] },
  [agentAdminSurfaceActions.versionHistory, agentAdminSurfaceActions.versionDiff, agentAdminSurfaceActions.restoreVersion, agentAdminSurfaceActions.openAgentDetailDoc]
);

export const agentAdminDocEditingVersionDiffSurface = envelope(
  'surface-agent-admin-version-diff',
  'diff',
  'Version diff',
  'agent-admin-agent',
  { surfaceContract: 'agent_admin.version_diff.v1', agentDefinitionId: agentAdminDocAgent.agentDefinitionId, kind: 'prompt', documentId: agentAdminPromptDoc.documentId, priorVersion: 1, selectedVersion: 2, diffRule: 'selected version N is compared only with N-1', status: 'ready', unifiedDiff: '--- version N-1\n+++ version N\n-Old prompt\n+Current prompt', availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.versionHistory, agentAdminSurfaceActions.restoreVersion), authorizedActions: ['action-agent-doc-version-history', 'action-agent-doc-version-restore'], traceRefs: ['trace-agent-admin-doc-version-diff'], systemStates: ['ready', 'no-prior-version', 'forbidden', 'failure'] },
  [agentAdminSurfaceActions.versionHistory, agentAdminSurfaceActions.restoreVersion]
);

export const agentAdminDocEditingCreateSkillSurface = envelope(
  'surface-agent-admin-create-skill',
  'form',
  'Create skill',
  'agent-admin-agent',
  { surfaceContract: 'agent_admin.create_skill.v1', agentDefinitionId: agentAdminDocAgent.agentDefinitionId, inputs: ['skill name', 'purpose/description', 'free-form initial content request'], availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.createSkill, agentAdminSurfaceActions.openAgentDetailDoc), authorizedActions: ['action-agent-admin-create-skill', 'action-agent-admin-open-agent-detail'], systemStates: ['ready', 'drafting', 'saving', 'cancelled', 'forbidden', 'failure'] },
  [agentAdminSurfaceActions.createSkill, agentAdminSurfaceActions.openAgentDetailDoc]
);

export const agentAdminDocEditingDeleteSkillSurface = envelope(
  'surface-agent-admin-delete-skill-confirmation',
  'confirmation',
  'Delete skill',
  'agent-admin-agent',
  { surfaceContract: 'agent_admin.delete_skill_confirmation.v1', agentDefinitionId: agentAdminDocAgent.agentDefinitionId, skillDocumentId: agentAdminSkillDoc.documentId, skillName: agentAdminSkillDoc.name, permanentDeletionWarning: 'Deleting a skill permanently deletes its reference docs. There is no restore.', referenceDocCount: 1, referenceDocs: [agentAdminReferenceDoc], availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.deleteSkill, agentAdminSurfaceActions.openAgentDetailDoc), authorizedActions: ['action-agent-admin-delete-skill', 'action-agent-admin-open-agent-detail'], systemStates: ['ready', 'confirmed', 'forbidden', 'failure'] },
  [agentAdminSurfaceActions.deleteSkill, agentAdminSurfaceActions.openAgentDetailDoc]
);

export const agentAdminDocEditingCreateReferenceDocSurface = envelope(
  'surface-agent-admin-create-reference-doc',
  'form',
  'Create reference doc',
  'agent-admin-agent',
  { surfaceContract: 'agent_admin.create_reference_doc.v1', agentDefinitionId: agentAdminDocAgent.agentDefinitionId, skillDocumentId: agentAdminSkillDoc.documentId, inputs: ['reference doc name', 'short description', 'free-form initial content request'], availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.createReferenceDoc, agentAdminSurfaceActions.openSkillDoc), authorizedActions: ['action-agent-admin-create-reference-doc', 'action-agent-admin-open-skill-doc'], systemStates: ['ready', 'drafting', 'saving', 'cancelled', 'forbidden', 'failure'] },
  [agentAdminSurfaceActions.createReferenceDoc, agentAdminSurfaceActions.openSkillDoc]
);

export const agentAdminDocEditingDeleteReferenceDocSurface = envelope(
  'surface-agent-admin-delete-reference-doc-confirmation',
  'confirmation',
  'Delete reference doc',
  'agent-admin-agent',
  { surfaceContract: 'agent_admin.delete_reference_doc_confirmation.v1', agentDefinitionId: agentAdminDocAgent.agentDefinitionId, referenceDocumentId: agentAdminReferenceDoc.documentId, referenceDocName: agentAdminReferenceDoc.name, permanentDeletionWarning: 'Deleting a reference doc is permanent. There is no restore.', availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.deleteReferenceDoc, agentAdminSurfaceActions.openAgentDetailDoc), authorizedActions: ['action-agent-admin-delete-reference-doc', 'action-agent-admin-open-agent-detail'], systemStates: ['ready', 'confirmed', 'forbidden', 'failure'] },
  [agentAdminSurfaceActions.deleteReferenceDoc, agentAdminSurfaceActions.openAgentDetailDoc]
);

export const agentAdminDocEditingRuntimeTracesSurface = envelope(
  'surface-agent-admin-runtime-traces',
  'trace-list',
  'Runtime reads',
  'agent-admin-agent',
  { surfaceContract: 'agent_admin.runtime_traces.v1', filters: { agentDefinitionId: '', documentIdOrStableId: '', occurredAtFrom: '', occurredAtTo: '' }, rows: [{ traceId: 'trace-runtime-read-skill-1', agentDefinitionId: agentAdminDocAgent.agentDefinitionId, agentName: agentAdminDocAgent.agentName, documentType: 'skill', documentIdOrStableId: agentAdminSkillDoc.stableSkillId, documentName: agentAdminSkillDoc.name, documentRead: 'readSkill', timestamp: generatedAt, requestSessionId: 'request-session-42', userCustomerContext: 'acct-user / customer-redacted', decision: 'ALLOWED', safeSummary: 'Runtime read metadata only; content omitted.' }, { traceId: 'trace-runtime-read-reference-1', agentDefinitionId: agentAdminDocAgent.agentDefinitionId, agentName: agentAdminDocAgent.agentName, documentType: 'reference', documentIdOrStableId: agentAdminReferenceDoc.stableReferenceId, documentName: agentAdminReferenceDoc.name, documentRead: 'readReferenceDoc', timestamp: generatedAt, requestSessionId: 'request-session-43', userCustomerContext: 'acct-user / customer-redacted', decision: 'ALLOWED', safeSummary: 'Reference doc body not shown in trace row.' }], contentRedaction: 'Trace rows do not include full prompt, skill, or reference content.', availableTaskActions: agentAdminAvailableActions(agentAdminSurfaceActions.openRuntimeTraces, agentAdminSurfaceActions.openAgentDetailDoc), authorizedActions: ['action-agent-admin-open-runtime-traces', 'action-agent-admin-open-agent-detail'], traceRefs: ['trace-agent-admin-doc-runtime-traces'], systemStates: ['ready', 'empty', 'forbidden', 'failure'] },
  [agentAdminSurfaceActions.openRuntimeTraces, agentAdminSurfaceActions.openAgentDetailDoc]
);

export const agentAdminDocEditingSystemMessageSurface = envelope(
  'surface-agent-admin-system-message',
  'system_message',
  'Agent Admin unavailable',
  'agent-admin-agent',
  { surfaceContract: 'agent_admin.system_message.v1', status: 'forbidden', severity: 'error', title: 'SaaS admin authority required', summary: 'Agent Admin doc editing is only available to SaaS Owner/Admin users.', message: 'Missing SaaS admin authority denies access before any prompt, skill, or reference content is exposed.', recoverySteps: ['Sign in with a SaaS Owner/Admin context.', 'Use another authorized workstream if this context is tenant/customer scoped.'], noDirectMutation: true, traceRefs: ['trace-agent-admin-doc-forbidden'], safety: { sanitized: true, redactionNote: 'Prompt, skill, and reference doc bodies are not exposed on denied states.' } },
  [agentAdminSurfaceActions.showDashboard, agentAdminSurfaceActions.showAgents]
);

export const currentAgentAdminSurfaceEnvelopes = [
  agentAdminDocEditingBlankSurface,
  agentAdminDocEditingDashboardSurface,
  agentAdminDocEditingAgentListSurface,
  agentAdminDocEditingAgentDetailSurface,
  agentAdminDocEditingPromptDocSurface,
  agentAdminDocEditingSkillDocSurface,
  agentAdminDocEditingReferenceDocSurface,
  agentAdminDocEditingEditSessionSurface,
  agentAdminDocEditingVersionHistorySurface,
  agentAdminDocEditingVersionDiffSurface,
  agentAdminDocEditingCreateSkillSurface,
  agentAdminDocEditingDeleteSkillSurface,
  agentAdminDocEditingCreateReferenceDocSurface,
  agentAdminDocEditingDeleteReferenceDocSurface,
  agentAdminDocEditingRuntimeTracesSurface,
  agentAdminDocEditingSystemMessageSurface
] as const;

const governancePolicyCapabilities = {
  readDashboard: 'governance.policy.read',
  simulateProposal: 'governance.policy.simulate',
  draftProposal: 'governance.policy.propose',
  reviewProposal: 'governance.proposals.review',
  approveProposal: 'governance.policy.approve',
  activateProposal: 'governance.proposals.activate',
  activatePolicyChange: 'governance.policy.activate',
  rollbackPolicyChange: 'governance.policy.rollback',
  recordOutcome: 'governance.outcomes.record',
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
    actionId: 'action-governance-policy-dashboard',
    label: 'Refresh governance dashboard',
    intent: 'read',
    capabilityId: governancePolicyCapabilities.readDashboard,
    governedToolId: governancePolicyCapabilities.readDashboard,
    browserToolId: 'action-governance-policy-dashboard',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-dashboard', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyDashboardRead', traceRequired: true }
  },
  showInventory: {
    actionId: 'action-governance-policy-list',
    label: 'Show policy inventory',
    intent: 'read',
    capabilityId: governancePolicyCapabilities.readDashboard,
    governedToolId: governancePolicyCapabilities.readDashboard,
    browserToolId: 'action-governance-policy-list',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-inventory', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyInventoryRead', traceRequired: true }
  },
  draftProposal: {
    actionId: 'action-governance-policy-draft-proposal',
    label: 'Draft policy proposal',
    intent: 'proposal',
    capabilityId: governancePolicyCapabilities.draftProposal,
    governedToolId: governancePolicyCapabilities.draftProposal,
    browserToolId: 'action-governance-policy-draft-proposal',
    inputSchemaRef: 'schema.governance-policy.proposal.draft.v1',
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-proposal', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyProposalDrafted', traceRequired: true }
  },
  simulateProposal: {
    actionId: 'action-governance-policy-simulate',
    label: 'Run deterministic simulation',
    intent: 'governance',
    capabilityId: governancePolicyCapabilities.simulateProposal,
    governedToolId: governancePolicyCapabilities.simulateProposal,
    browserToolId: 'action-governance-policy-simulate',
    inputSchemaRef: 'schema.governance-policy.simulation.v1',
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-simulation', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicySimulationRequested', traceRequired: true }
  },
  decideProposal: {
    actionId: 'action-governance-policy-decide',
    label: 'Approve / reject / request changes',
    intent: 'approval',
    capabilityId: governancePolicyCapabilities.approveProposal,
    governedToolId: governancePolicyCapabilities.reviewProposal,
    browserToolId: 'action-governance-policy-decide',
    inputSchemaRef: 'schema.governance-policy.decision.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-decision', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyProposalApproved', traceRequired: true }
  },
  activateProposal: {
    actionId: 'action-governance-policy-activate',
    label: 'Activate approved change',
    intent: 'command',
    capabilityId: governancePolicyCapabilities.activatePolicyChange,
    governedToolId: governancePolicyCapabilities.activateProposal,
    browserToolId: 'action-governance-policy-activate',
    inputSchemaRef: 'schema.governance-policy.activation.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-decision', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyChangeActivated', traceRequired: true }
  },
  outcomeNote: {
    actionId: 'action-governance-policy-outcome-note',
    label: 'Add outcome note',
    intent: 'command',
    capabilityId: governancePolicyCapabilities.recordOutcome,
    governedToolId: governancePolicyCapabilities.recordOutcome,
    browserToolId: 'action-governance-policy-outcome-note',
    inputSchemaRef: 'schema.governance-policy.outcome-note.v1',
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-decision', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyOutcomeNoteRecorded', traceRequired: true }
  },
  rollbackProposal: {
    actionId: 'action-governance-policy-rollback',
    label: 'Roll back change',
    intent: 'command',
    capabilityId: governancePolicyCapabilities.rollbackPolicyChange,
    governedToolId: governancePolicyCapabilities.activateProposal,
    browserToolId: 'action-governance-policy-rollback',
    inputSchemaRef: 'schema.governance-policy.rollback.v1',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-decision', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyChangeRolledBack', traceRequired: true }
  },
  startImpactAnalysis: {
    actionId: 'action-governance-policy-start-impact-analysis',
    label: 'Start policy impact analysis',
    intent: 'workflow',
    capabilityId: governancePolicyCapabilities.startImpactAnalysis,
    governedToolId: governancePolicyCapabilities.startImpactAnalysis,
    browserToolId: 'action-governance-policy-start-impact-analysis',
    inputSchemaRef: 'schema.governance-policy.impact-analysis.start.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'client-generated' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-impact-analysis-task', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactAnalysisStartedOrBlocked', traceRequired: true }
  },
  readImpactAnalysis: {
    actionId: 'action-governance-policy-read-impact-analysis',
    label: 'Read policy impact task',
    intent: 'read',
    capabilityId: governancePolicyCapabilities.readImpactAnalysis,
    governedToolId: governancePolicyCapabilities.readImpactAnalysis,
    browserToolId: 'action-governance-policy-read-impact-analysis',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-impact-analysis-task', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactAnalysisRead', traceRequired: true }
  },
  cancelImpactAnalysis: {
    actionId: 'action-governance-policy-cancel-impact-analysis',
    label: 'Cancel policy impact task',
    intent: 'command',
    capabilityId: governancePolicyCapabilities.cancelImpactAnalysis,
    governedToolId: governancePolicyCapabilities.cancelImpactAnalysis,
    browserToolId: 'action-governance-policy-cancel-impact-analysis',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-impact-analysis-task', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactAnalysisCancelled', traceRequired: true }
  },
  acceptImpactResult: {
    actionId: 'action-governance-policy-accept-impact-result',
    label: 'Accept advisory impact result',
    intent: 'approval',
    capabilityId: governancePolicyCapabilities.acceptImpactResult,
    governedToolId: governancePolicyCapabilities.acceptImpactResult,
    browserToolId: 'action-governance-policy-accept-impact-result',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-impact-analysis-result', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactResultAccepted', traceRequired: true }
  },
  rejectImpactResult: {
    actionId: 'action-governance-policy-reject-impact-result',
    label: 'Reject advisory impact result',
    intent: 'approval',
    capabilityId: governancePolicyCapabilities.rejectImpactResult,
    governedToolId: governancePolicyCapabilities.rejectImpactResult,
    browserToolId: 'action-governance-policy-reject-impact-result',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-impact-analysis-result', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactResultRejected', traceRequired: true }
  },
  requestImpactChanges: {
    actionId: 'action-governance-policy-request-impact-changes',
    label: 'Request analysis changes',
    intent: 'approval',
    capabilityId: governancePolicyCapabilities.requestImpactChanges,
    governedToolId: governancePolicyCapabilities.requestImpactChanges,
    browserToolId: 'action-governance-policy-request-impact-changes',
    requiresConfirmation: true,
    requiresApproval: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-impact-analysis-result', openPlacement: 'inline' },
    audit: { eventType: 'GovernancePolicyImpactChangesRequested', traceRequired: true }
  },
  openTrace: {
    actionId: 'action-open-audit-trace',
    label: 'Open governance trace',
    intent: 'trace',
    capabilityId: governancePolicyCapabilities.openTrace,
    governedToolId: governancePolicyCapabilities.openTrace,
    browserToolId: 'action-open-audit-trace',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-governance-policy-decision-trace', openPlacement: 'deep-link' },
    audit: { eventType: 'GovernancePolicyTraceOpened', traceRequired: true }
  }
} satisfies Record<string, SurfaceAction>;

export const governancePolicyDashboardSurface = envelope(
  'surface-governance-policy-dashboard',
  'dashboard',
  'Governance/Policy dashboard',
  'governance-policy-agent',
  {
    canonicalSurfaceId: 'surface.governance.proposal_queue.v1',
    governedCapabilityIds: ['governance.proposals.review', 'governance.proposals.activate', 'governance.outcomes.record'],
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
      { workstreamId: 'governance-policy-agent', label: 'Review policy inventory', allowed: true, capabilityIds: [governancePolicyCapabilities.readDashboard], traceId: 'trace-govpol-inventory' },
      { workstreamId: 'governance-policy-agent', label: 'Simulate proposal impact', allowed: true, capabilityIds: [governancePolicyCapabilities.simulateProposal], traceId: 'trace-govpol-simulation' },
      { workstreamId: 'governance-policy-agent', label: 'Start impact analysis', allowed: true, capabilityIds: [governancePolicyCapabilities.startImpactAnalysis], traceId: 'trace-govpol-impact-analysis-start' }
    ],
    blockedState: { reasonCode: 'FRONTEND_NOT_AUTHORITY', message: 'Launcher visibility and action buttons are convenience signals only.', recovery: 'Backend capability checks decide every protected action and return safe denial surfaces when authority is missing.' }
  },
  [governancePolicySurfaceActions.showInventory, governancePolicySurfaceActions.draftProposal, governancePolicySurfaceActions.simulateProposal, governancePolicySurfaceActions.startImpactAnalysis, governancePolicySurfaceActions.openTrace]
);

export const governancePolicyInventorySurface = envelope(
  'surface-governance-policy-inventory',
  'list-search',
  'Policy inventory',
  'governance-policy-agent',
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
  'governance-policy-agent',
  {
    canonicalSurfaceId: 'surface.governance.proposal_queue.v1',
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
  'governance-policy-agent',
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
  'governance-policy-agent',
  {
    surfaceContract: 'governance.policy.decision.v1',
    canonicalSurfaceId: 'surface.governance.decision_card.v1',
    activationStatusSurfaceId: 'surface.governance.activation_status.v1',
    decisionId: 'decision-govpol-001',
    decisionSummary: { proposalDisplayRef: 'proposal-govpol-001', lifecycleState: 'in-review', commandMode: 'decide', recommendedOutcome: 'approve after evidence review', freshness: 'backend-authored' },
    riskAndImpact: { riskClass: 'high', expectedAccessEffect: 'stricter activation threshold', impactAnalysisReadiness: 'blocked_provider_or_runtime', rollbackReadiness: 'metadata-required' },
    decisionEvidence: { approvalStatus: 'review-required', simulationStatus: 'available', impactAnalysisStatus: 'blocked_provider_or_runtime', traceRefs: ['trace-govpol-decision', 'trace-govpol-impact-analysis-blocked'] },
    commandForm: { commandMode: 'decide', reasonRequired: true, idempotencyKeyRequired: true, disabledSubmitReasons: ['activation remains a separate backend-governed command'] },
    canonicalActionIds: ['action-governance-policy-decide', 'action-governance-policy-activate', 'action-governance-policy-rollback'],
    recommendation: 'Approve the stricter threshold only after reviewing simulation evidence; keep activation as a separate human action.',
    riskScore: 82,
    confidenceScore: 76,
    noDirectMutation: true,
    noFakeSuccess: true,
    evidence: [
      { evidenceId: 'evidence-simulation', label: 'Simulation trace', summary: 'Deterministic simulation showed expected denials for unapproved activation.' },
      { evidenceId: 'evidence-authority', label: 'Authority basis', summary: 'Actor needs governance.policy.approve; frontend button visibility is not authority.' },
      { evidenceId: 'evidence-redaction', label: 'Redaction', summary: 'Prompt text, backend secrets, and cross-tenant evidence remain omitted.' }
    ],
    allowedActions: [
      { actionId: 'action-governance-policy-decide', label: 'Approve / reject / request changes', browserToolId: 'action-governance-policy-decide', governedToolId: governancePolicyCapabilities.reviewProposal, capabilityId: governancePolicyCapabilities.approveProposal },
      { actionId: 'action-governance-policy-outcome-note', label: 'Add outcome note', browserToolId: 'action-governance-policy-outcome-note', governedToolId: governancePolicyCapabilities.recordOutcome, capabilityId: governancePolicyCapabilities.recordOutcome },
      { actionId: 'action-open-audit-trace', label: 'Open decision trace', browserToolId: 'action-open-audit-trace', governedToolId: governancePolicyCapabilities.openTrace, capabilityId: governancePolicyCapabilities.openTrace }
    ],
    disabledActions: [
      { actionId: 'action-governance-policy-activate', reason: 'Activation stays blocked until backend confirms approved version and idempotency key.' },
      { actionId: 'action-governance-policy-activate', reason: 'Activation stays a separate backend-governed policy capability; impact analysis acceptance cannot activate policy state.' }
    ],
    risk: 'Authority-changing approval',
    traceLinks: ['trace-govpol-decision', 'trace-govpol-approval-basis']
  },
  [governancePolicySurfaceActions.decideProposal, governancePolicySurfaceActions.activateProposal, governancePolicySurfaceActions.rollbackProposal, governancePolicySurfaceActions.outcomeNote, governancePolicySurfaceActions.openTrace]
);

export const governancePolicyImpactAnalysisTaskSurface = envelope(
  'surface-governance-policy-impact-analysis-task',
  'workflow-status',
  'Policy impact analysis task',
  'governance-policy-agent',
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
  'governance-policy-agent',
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
      { actionId: 'action-governance-policy-accept-impact-result', label: 'Accept advisory result evidence', browserToolId: 'action-governance-policy-accept-impact-result', governedToolId: governancePolicyCapabilities.acceptImpactResult, capabilityId: governancePolicyCapabilities.acceptImpactResult },
      { actionId: 'action-governance-policy-reject-impact-result', label: 'Reject advisory result evidence', browserToolId: 'action-governance-policy-reject-impact-result', governedToolId: governancePolicyCapabilities.rejectImpactResult, capabilityId: governancePolicyCapabilities.rejectImpactResult },
      { actionId: 'action-governance-policy-request-impact-changes', label: 'Request analysis changes', browserToolId: 'action-governance-policy-request-impact-changes', governedToolId: governancePolicyCapabilities.requestImpactChanges, capabilityId: governancePolicyCapabilities.requestImpactChanges }
    ],
    disabledActions: [
      { actionId: 'action-governance-policy-activate', reason: 'Worker result acceptance never activates policy; activation remains a separate human-authorized backend capability.' }
    ],
    risk: 'Advisory-only policy impact; activationBlockedUntilHumanDecision=true; noDirectMutation=true',
    traceLinks: ['trace-govpol-impact-result', 'trace-govpol-impact-worker-event']
  },
  [governancePolicySurfaceActions.acceptImpactResult, governancePolicySurfaceActions.rejectImpactResult, governancePolicySurfaceActions.requestImpactChanges, governancePolicySurfaceActions.openTrace]
);

export const governancePolicySystemMessageSurface = envelope(
  'surface-governance-policy-system-message',
  'system-message',
  'Governance/Policy action blocked',
  'governance-policy-agent',
  {
    surfaceContract: 'governance.policy.system_message.v1',
    status: 'blocked-provider-or-runtime',
    severity: 'warning',
    title: 'Governance/Policy action blocked',
    message: 'The requested Governance/Policy work is unavailable, forbidden, stale, blocked, or failed in the selected AuthContext.',
    messageSummary: {
      safeReasonCode: 'GOVERNANCE_POLICY_TENANT_FORBIDDEN',
      originalActionId: 'action-governance-policy-start-impact-analysis',
      status: 'blocked-provider-or-runtime',
      userMessage: 'No proposal, task, result, provider output, or cross-tenant/customer evidence is enumerated.'
    },
    contextSummary: {
      selectedWorkstream: 'Governance/Policy',
      selectedContextLabel: 'Tenant Admin · browser-safe',
      omissionReason: 'hidden proposal/task/result and cross-tenant evidence are not enumerated'
    },
    recoveryOptions: [
      { label: 'Return to Governance/Policy dashboard', targetSurfaceId: 'surface-governance-policy-dashboard', actionId: 'action-governance-policy-dashboard', sideEffect: 'none' },
      { label: 'Open scoped policy inventory', targetSurfaceId: 'surface-governance-policy-inventory', actionId: 'action-governance-policy-list', sideEffect: 'none' },
      { label: 'Retry/open visible impact-analysis state', targetSurfaceId: 'surface-governance-policy-impact-analysis-task', actionId: 'action-governance-policy-read-impact-analysis', sideEffect: 'none' }
    ],
    validationMessages: [
      { field: 'request', reasonCode: 'idempotency-key-required', message: 'Side-effecting retries require idempotency before audit or lifecycle state can change.', sideEffect: 'none' },
      { field: 'proposalId', reasonCode: 'not-found-or-hidden', message: 'Hidden or cross-tenant proposal/task/result references are not enumerated.', sideEffect: 'none' }
    ],
    authorizedActions: ['action-governance-policy-dashboard', 'action-governance-policy-list', 'action-governance-policy-read-impact-analysis'],
    traceRefs: ['trace-governance-policy-denial-fixture', 'trace-governance-policy-provider-runtime-blocked-fixture'],
    redaction: 'browser-safe denial; raw provider/model output, prompts, raw tool payloads, JWTs, secrets, stack traces, correlation ids, idempotency keys, and cross-tenant/customer evidence are omitted',
    readiness: { provider: 'blocked', autonomousAgentRuntime: 'blocked', noFakeSuccess: true },
    requiredStates: ['forbidden', 'missing-context', 'validation-error', 'conflict/stale', 'blocked-provider-or-runtime', 'not-found-or-hidden', 'partial-data', 'retryable-failure', 'terminal-failure', 'ready/recovery'],
    noFakeSuccess: true,
    noDirectMutation: true,
    safety: { sanitized: true, redactionNote: 'Provider secrets, raw JWTs, hidden prompts, raw policy clauses, tool payloads, cross-tenant/customer evidence, stack traces, correlation ids, and idempotency keys are not shown.' }
  },
  [governancePolicySurfaceActions.showDashboard, governancePolicySurfaceActions.showInventory, governancePolicySurfaceActions.readImpactAnalysis, governancePolicySurfaceActions.openTrace]
);

export const governancePolicyDecisionTraceSurface = envelope(
  'surface-governance-policy-decision-trace',
  'audit-timeline',
  'Governance decision traces',
  'governance-policy-agent',
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
  governancePolicySystemMessageSurface,
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

export const dashboardSurface = envelope('surface-dashboard', 'dashboard', 'Tenant attention dashboard', 'my-account-agent', { cards: [{ cardId: 'card-open-decisions', label: 'Open decisions', value: 2, severity: 'warning' }], attentionSource: 'attention.list_workstream_items', attentionItems: [{ itemId: 'attention-agent-admin-readiness', label: 'Agent Admin provider readiness is blocked', summary: 'Model/runtime provider readiness is blocked until governed provider configuration is available.', status: 'open', severity: 'blocked', category: 'provider_readiness', capabilityId: 'agent_admin.list_definitions', governedToolId: 'attention.open_attention_item', traceId: 'trace-agent-admin-provider-readiness', sourceWorkstreamId: 'agent-admin-agent', surfaceRef: { targetFunctionalAgentId: 'agent-admin-agent', targetSurfaceId: 'surface-agent-admin-catalog', targetSurfaceType: 'dashboard', targetItemId: 'attention-agent-admin-readiness', defaultActionId: 'attention.open_attention_item', requiredCapabilityId: 'agent_admin.list_definitions' }, redaction: 'full' }], scopeNote: 'Full-core/demo surface; actionable attention is backend-derived through attention.list_workstream_items and separate from transient unseen-response badges.' }, [surfaceActionsByIntent.read]);
export const listSearchSurface = userAdminListSearchSurface;
export const detailEditSurface = userAdminDetailEditSurface;
export const decisionSurface = envelope('surface-decision-card', 'decision', 'Approve bounded outreach plan', 'governance-policy-agent', { decisionId: 'decision-1', recommendation: 'Approve after evidence review.', riskScore: 72, confidenceScore: 84, evidence: [{ evidenceId: 'evidence-1', label: 'Trace summary', summary: 'Agent stayed within tool boundary.' }] }, [surfaceActionsByIntent.approval, surfaceActionsByIntent.trace]);
export const auditTimelineSurface = envelope('surface-audit-trace-timeline', 'audit-timeline', 'Admin audit timeline', 'audit-trace-agent', { events: [{ eventId: 'audit-1', occurredAt: generatedAt, actor: 'Tenant Admin', action: 'invited user', traceId: 'trace-invite' }] }, [surfaceActionsByIntent.trace]);
export const workflowStatusSurface = envelope('surface-workflow-status', 'workflow-status', 'Invitation workflow status', 'user-admin-agent', { workflowId: 'workflow-invite-1', status: 'waiting-for-human', summary: 'Fixture fallback for non-authoritative workflows. User Admin invitation actions use /api/workstream/actions and backend-aligned action ids.', traceIds: ['trace-useradmin-invitation-workflow'], requiredCapabilityId: userAdminCapabilities.sendInvitation, steps: [{ stepId: 'send-email', label: 'Send invitation email', status: 'waiting-for-human' }] }, [surfaceActionsByIntent.workflow]);
export const userAdminInvitationCreateSurface = envelope('surface-user-admin-invitation-create', 'create-form', 'Invite user', 'user-admin-agent', { surfaceContract: 'user_admin.invitation_create.v1', branchRootSurfaceId: 'surface-user-admin-users', branchReturnActionId: 'action-user-admin-show-users', branchReturnLabel: 'Show Users', safeFilterPreservation: 'backend-authored-only', status: 'ready', summary: 'Create a scoped invitation through the backend InvitationService. Submission requires a client idempotency key and returns invitation detail or a safe validation/denial result.', targetScope: { scopeType: 'TENANT', tenantId: authContext.tenantId, customerId: authContext.customerId }, draft: { email: '', displayName: '', roles: ['TENANT_EMPLOYEE'] }, validationMessages: [], idempotencyKeyHint: 'client-generated', traceRefs: ['trace-useradmin-invitation-create'], correlationId: 'corr-useradmin-invitation-create', redaction: ['invitation-token-redacted', 'provider-payload-redacted', 'raw-jwt-redacted'] }, [userAdminSurfaceActions.createInvitation, userAdminSurfaceActions.showUsers, userAdminSurfaceActions.openAdminAudit]);
export const userAdminInvitationActionStatusSurface = envelope('surface-user-admin-invitation-action-status', 'workflow-status', 'User Admin invitation action status', 'user-admin-agent', { workflowId: 'user-admin-invitation-action', status: 'completed', summary: 'Invitation create/resend/revoke action feedback is rendered from backend-authoritative /api/workstream/actions results with idempotency, audit, trace, and outbox status references.', traceIds: ['trace-useradmin-invitation-action', 'trace-useradmin'], requiredCapabilityId: userAdminCapabilities.sendInvitation, steps: [{ stepId: 'authorize-selected-auth-context', label: 'Backend selected AuthContext and canonical user_admin.* capability authorized', status: 'completed' }, { stepId: 'enqueue-outbox', label: 'Invitation outbox/provider result surfaced without fixture-only success substitution', status: 'completed' }, { stepId: 'system-message-denials', label: 'system_message denials preserve correlation id for forbidden, stale, validation, no-op, or blocked_provider_or_runtime states', status: 'completed' }] }, [userAdminSurfaceActions.createInvitation, userAdminSurfaceActions.resendInvitation, userAdminSurfaceActions.revokeInvitation, userAdminSurfaceActions.openAdminAudit]);
export const userAdminMemberStatusActionSurface = envelope('surface-user-admin-member-status-action', 'workflow-status', 'User Admin member status action', 'user-admin-agent', { workflowId: 'user-admin-member-status-action', status: 'completed', summary: 'Disable/reactivate feedback is rendered from backend-authoritative user_admin.update_member_status results with last-admin, self-disable, disabled-user, idempotency, no-op, audit, and system_message evidence.', traceIds: ['trace-useradmin-status-action', 'trace-useradmin'], requiredCapabilityId: userAdminCapabilities.updateMemberStatus, steps: [{ stepId: 'authorize-selected-auth-context', label: 'Backend selected AuthContext, tenant/customer scope, active actor, and user_admin.update_member_status checked', status: 'completed' }, { stepId: 'validate-guardrails', label: 'last-admin and self-disable guardrails decide allow, denial, validation-error, or no-op idempotency result', status: 'completed' }, { stepId: 'emit-trace', label: 'Browser-safe trace links and system_message denial/no-op text returned with the action result', status: 'completed' }] }, [userAdminSurfaceActions.suspendMembership, userAdminSurfaceActions.reactivateMembership, userAdminSurfaceActions.openAdminAudit]);
export const userAdminRoleChangeActionSurface = envelope('surface-user-admin-role-change-action', 'workflow-status', 'User Admin role change action', 'user-admin-agent', { workflowId: 'user-admin-role-change-action', status: 'waiting-for-human', summary: 'Role preview/commit feedback preserves user_admin.role_change_preview.v1, capability delta, affected workstreams, approval policy, idempotency, no-op, and trace links without letting frontend state grant authority.', traceIds: ['trace-useradmin-role-preview', 'trace-useradmin'], requiredCapabilityId: userAdminCapabilities.changeMemberRoles, steps: [{ stepId: 'preview-role-change', label: 'user_admin.preview_role_change returned user_admin.role_change_preview.v1 capability delta and affected workstreams', status: 'completed' }, { stepId: 'approval-policy', label: 'Apply role change remains approval-gated and backend-authoritative', status: 'waiting-for-human' }, { stepId: 'idempotency-trace', label: 'Commit uses client-generated idempotency and trace-useradmin links for no-op/replay evidence', status: 'completed' }] }, [userAdminSurfaceActions.previewRoleChange, userAdminSurfaceActions.changeMemberRoles, userAdminSurfaceActions.openAdminAudit]);
export const userAdminAgentBlockedSystemMessageSurface = envelope('surface-user-admin-agent-provider-blocked', 'system_message', 'UserAdminAgent unavailable', 'user-admin-agent', { status: 'blocked_provider_or_runtime', severity: 'warning', title: 'UserAdminAgent unavailable', summary: 'Model-backed UserAdminAgent guidance was blocked before a response was produced.', message: 'Model-backed UserAdminAgent guidance was blocked before a response was produced. Backend provider/runtime configuration must be restored before guidance can run.', recoverySteps: ['Verify model provider configuration and active ModelConfigRef on the backend.', 'Review PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, and userAdminEvidence.read tool traces for this correlation id.', 'Retry after backend configuration is restored; use deterministic User Admin surfaces for invite, member status, and role changes.'], workstreamEntryId: 'item-user-admin-agent-provider-blocked', producingAgentId: 'user-admin-agent', capabilityId: userAdminCapabilities.agentTurn, sourceRefs: [{ refType: 'trace', refId: 'trace-useradmin-agent-provider-blocked', label: 'Blocked UserAdminAgent runtime trace' }, { refType: 'capability', refId: userAdminCapabilities.overview, label: 'Scoped User Admin overview capability' }], safety: { sanitized: true, redactionNote: 'Provider secrets, raw JWTs, hidden prompts, invitation tokens, and unauthorized tenant/customer evidence are omitted.' }, trace: { correlationId: 'corr-useradmin-agent-provider-blocked', traceIds: ['trace-useradmin-agent-provider-blocked', 'trace-useradmin-agent-work-blocked'] } }, [userAdminSurfaceActions.openAdminAudit]);
export const governanceDiffSurface = envelope('surface-governance-diff', 'governance-diff', 'Policy proposal diff', 'governance-policy-agent', { proposalId: 'proposal-1', beforeSummary: 'Manual approval over 75 risk.', afterSummary: 'Manual approval over 65 risk.', changes: [{ path: 'risk.approvalThreshold', before: '75', after: '65', impact: 'More decisions require human review.' }] }, [surfaceActionsByIntent.proposal, surfaceActionsByIntent.governance]);
export const outcomeSurface = envelope('surface-outcome-review', 'outcome', 'Outcome review', 'governance-policy-agent', { outcomeId: 'outcome-1', metrics: [{ metricId: 'decision-cycle-time', label: 'Decision cycle time', current: 4, target: 2, unit: 'hours' }] }, [surfaceActionsByIntent.read]);

export const fullCoreDemoSurfaceEnvelopes = [
  ...myAccountStructuredSurfaces,
  userAdminDashboardSurface,
  userAdminOrganizationDirectorySurface,
  userAdminOrganizationDetailSurface,
  userAdminOrganizationCreateSurface,
  userAdminOrganizationRenameSurface,
  userAdminOrganizationSuspendSurface,
  userAdminOrganizationReactivateSurface,
  userAdminListSearchSurface,
  userAdminInvitationCreateSurface,
  userAdminInvitationDetailSurface,
  userAdminInvitationResendConfirmationSurface,
  userAdminInvitationRevokeConfirmationSurface,
  userAdminMembershipStatusConfirmationSurface,
  userAdminRoleChangePreviewSurface,
  userAdminSupportAccessGrantSurface,
  userAdminSupportAccessRevokeConfirmationSurface,
  userAdminIdentityExceptionReviewSurface,
  userAdminSystemMessageSurface,
  userAdminRoleCapabilityMatrixSurface,
  userAdminAccessReviewSurface,
  userAdminInvitationActionStatusSurface,
  userAdminMemberStatusActionSurface,
  userAdminRoleChangeActionSurface,
  userAdminAgentBlockedSystemMessageSurface,
  agentAdminDashboardSurface,
  agentAdminCatalogSurface,
  agentAdminDetailSurface,
  agentPromptGovernanceSurface,
  agentSkillManifestSurface,
  agentToolBoundarySurface,
  agentModelRefsSurface,
  agentSeedMaterialSurface,
  agentTestConsoleSurface,
  agentBehaviorProposalSurface,
  agentActivationConfirmationSurface,
  agentDeactivationConfirmationSurface,
  agentRollbackConfirmationSurface,
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
  ...currentAgentAdminSurfaceEnvelopes,
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

export const displayOrganizationAdminActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Display Organization Admin. Backend authority, SaaS Owner scope, idempotency, audit, and trace checks remain capability-backed.',
  correlationId: 'corr-display-organization-admin',
  traceIds: ['trace-display-organization-admin'],
  resultSurface: userAdminOrganizationDirectorySurface
};

export const displayUserDetailActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Display user detail for admin@example.test. Backend authority, scoped read, audit, and trace checks remain capability-backed.',
  correlationId: 'corr-display-user-detail',
  traceIds: ['trace-display-user-detail', 'trace-user-admin-detail'],
  resultSurface: userAdminDetailEditSurface
};

export const displayAgentBlankActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Agent Admin blank state loaded with Show dashboard, Show agents, and composer entry points.',
  correlationId: 'corr-display-agent-admin-blank',
  traceIds: ['trace-display-agent-admin-blank'],
  resultSurface: agentAdminDocEditingBlankSurface
};

export const displayAgentDashboardActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Display the Agent Admin dashboard for AI-assisted prompt, skill, and reference doc editing.',
  correlationId: 'corr-display-agent-admin-dashboard',
  traceIds: ['trace-display-agent-admin-dashboard'],
  resultSurface: agentAdminDocEditingDashboardSurface
};

export const displayAgentCatalogActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Display the Agent Admin agent list with backend-authorized filters and doc-edit row actions.',
  correlationId: 'corr-display-agent-list',
  traceIds: ['trace-display-agent-list'],
  resultSurface: agentAdminDocEditingAgentListSurface
};

export const displayAgentDetailActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Display Agent Admin doc-editing detail with prompt, skills, reference docs, and runtime trace entry points.',
  correlationId: 'corr-display-agent-detail',
  traceIds: ['trace-display-agent-detail'],
  resultSurface: agentAdminDocEditingAgentDetailSurface
};

export const displayAgentPromptDocActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Display the current Agent Admin prompt doc with Markdown body and version metadata.',
  correlationId: 'corr-display-agent-prompt-doc',
  traceIds: ['trace-display-agent-prompt-doc'],
  resultSurface: agentAdminDocEditingPromptDocSurface
};

export const displayAgentSkillDocActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Display the current Agent Admin skill doc with reference docs and version metadata.',
  correlationId: 'corr-display-agent-skill-doc',
  traceIds: ['trace-display-agent-skill-doc'],
  resultSurface: agentAdminDocEditingSkillDocSurface
};

export const displayAgentReferenceDocActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Display the current Agent Admin skill reference doc with Markdown body and version metadata.',
  correlationId: 'corr-display-agent-reference-doc',
  traceIds: ['trace-display-agent-reference-doc'],
  resultSurface: agentAdminDocEditingReferenceDocSurface
};

export const displayAgentEditSessionActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Edit session started; the AI-assisted proposal can be refined before Save or Cancel.',
  correlationId: 'corr-display-agent-edit-session',
  traceIds: ['trace-display-agent-edit-session'],
  resultSurface: agentAdminDocEditingEditSessionSurface
};

export const displayAgentVersionHistoryActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Version history loaded with simple integer versions and read-only historical selection.',
  correlationId: 'corr-display-agent-version-history',
  traceIds: ['trace-display-agent-version-history'],
  resultSurface: agentAdminDocEditingVersionHistorySurface
};

export const displayAgentVersionDiffActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Adjacent version diff loaded using selected version N compared with N-1.',
  correlationId: 'corr-display-agent-version-diff',
  traceIds: ['trace-display-agent-version-diff'],
  resultSurface: agentAdminDocEditingVersionDiffSurface
};

export const displayAgentCreateSkillActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Create skill surface opened for AI-assisted initial Markdown content drafting.',
  correlationId: 'corr-display-agent-create-skill',
  traceIds: ['trace-display-agent-create-skill'],
  resultSurface: agentAdminDocEditingCreateSkillSurface
};

export const displayAgentDeleteSkillActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Permanent skill delete confirmation opened and lists affected reference docs.',
  correlationId: 'corr-display-agent-delete-skill',
  traceIds: ['trace-display-agent-delete-skill'],
  resultSurface: agentAdminDocEditingDeleteSkillSurface
};

export const displayAgentCreateReferenceDocActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Create reference doc surface opened for AI-assisted initial Markdown content drafting.',
  correlationId: 'corr-display-agent-create-reference-doc',
  traceIds: ['trace-display-agent-create-reference-doc'],
  resultSurface: agentAdminDocEditingCreateReferenceDocSurface
};

export const displayAgentDeleteReferenceDocActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Permanent reference doc delete confirmation opened.',
  correlationId: 'corr-display-agent-delete-reference-doc',
  traceIds: ['trace-display-agent-delete-reference-doc'],
  resultSurface: agentAdminDocEditingDeleteReferenceDocSurface
};

export const displayAgentRuntimeTracesActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Runtime read traces loaded without full skill or reference content.',
  correlationId: 'corr-display-agent-runtime-traces',
  traceIds: ['trace-display-agent-runtime-traces'],
  resultSurface: agentAdminDocEditingRuntimeTracesSurface
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
  status: 'blocked_provider_or_runtime',
  message: 'Model-reference readiness failed closed; provider aliases are browser-safe, credentials remain redacted, and no fixture/model-less success is claimed.',
  correlationId: 'corr-display-agent-model-refs',
  traceIds: ['trace-agent-admin-model-refs', 'trace-agent-work-88'],
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
  message: 'Displayed behavior-change proposal lifecycle state; submit/review/approval remain deterministic backend commands and activation/rollback open separate confirmation surfaces.',
  correlationId: 'corr-display-agent-behavior-proposal',
  traceIds: ['trace-agent-admin-behavior-review', 'trace-agent-admin-behavior-activation-blocked'],
  resultSurface: agentBehaviorProposalSurface
};

export const displayAgentActivationConfirmationActionResult: CapabilityActionResult = {
  status: 'approval-required',
  message: 'Displayed separate Agent Admin activation confirmation with approval, idempotency, impact, policy, and trace evidence.',
  correlationId: 'corr-display-agent-activation-confirmation',
  traceIds: ['trace-agent-admin-behavior-activation-blocked'],
  resultSurface: agentActivationConfirmationSurface
};

export const displayAgentDeactivationConfirmationActionResult: CapabilityActionResult = {
  status: 'approval-required',
  message: 'Displayed separate Agent Admin deactivation confirmation with lifecycle impact and trace evidence.',
  correlationId: 'corr-display-agent-deactivation-confirmation',
  traceIds: ['trace-agent-admin-definition-agent-admin-agent'],
  resultSurface: agentDeactivationConfirmationSurface
};

export const displayAgentRollbackConfirmationActionResult: CapabilityActionResult = {
  status: 'approval-required',
  message: 'Displayed separate Agent Admin rollback confirmation with rollback metadata and trace evidence.',
  correlationId: 'corr-display-agent-rollback-confirmation',
  traceIds: ['trace-agent-admin-behavior-activation-blocked'],
  resultSurface: agentRollbackConfirmationSurface
};

export const displayAgentPromptRiskReviewActionResult: CapabilityActionResult = {
  status: 'blocked_provider_or_runtime',
  message: 'Displayed Agent Admin prompt-risk worker readiness surface as blocked/deferred; no model-backed advisory success is claimed.',
  correlationId: 'corr-display-agent-prompt-risk-review',
  traceIds: ['trace-prompt-risk-provider-blocked-001', 'trace-prompt-risk-assembly-blocked-001'],
  resultSurface: agentAdminPromptRiskReviewSurface
};

export const displayAgentAdminTraceActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Displayed Agent Admin protected-read, prompt assembly, loader tool, agentAdminEvidence.read, provider, and AgentWorkTrace timeline.',
  correlationId: 'corr-display-agent-admin-trace',
  traceIds: ['trace-agent-admin-catalog', 'trace-agent-work-88'],
  resultSurface: agentAdminTraceSurface
};

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
  displayUserDetail: {
    actionId: 'action-display-user-detail',
    label: 'Display user account detail',
    intent: 'read',
    capabilityId: secureTenantUserFoundation,
    inputSchemaRef: 'schema.user-admin.detail.v1',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-user-admin-detail-admin', openPlacement: 'inline' },
    audit: { eventType: 'UserAdminDetailDisplayed', traceRequired: true }
  },
  updateUserProfile: {
    actionId: 'action-update-user-profile',
    label: 'Save profile changes',
    intent: 'command',
    capabilityId: secureTenantUserFoundation,
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
    capabilityId: secureTenantUserFoundation,
    inputSchemaRef: 'schema.membership.role.replace.v1',
    requiresConfirmation: true,
    disabled: { reasonCode: 'last-admin-risk', message: 'Backend authorization denied this fixture action: cannot remove the last tenant admin without an approved replacement.' },
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-user-admin-detail-admin', openPlacement: 'inline' },
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

export const myAccountSurfaceActions = {
  showProfile: {
    actionId: 'action-show-my-profile',
    label: 'Show user profile',
    intent: 'read',
    capabilityId: 'profile.read',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-my-profile', openPlacement: 'inline' },
    audit: { eventType: 'UserProfileDisplayed', traceRequired: true }
  },
  showSettings: {
    actionId: 'action-show-my-settings',
    label: 'Show user settings',
    intent: 'read',
    capabilityId: 'profile.read',
    idempotency: { required: false },
    resultSurface: { updateSurfaceId: 'surface-my-settings', openPlacement: 'inline' },
    audit: { eventType: 'UserSettingsDisplayed', traceRequired: true }
  },
  updateProfile: {
    actionId: 'action-update-my-profile',
    label: 'Save profile changes',
    intent: 'command',
    capabilityId: 'profile.update',
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
    capabilityId: 'profile.update',
    inputSchemaRef: 'schema.my-account.settings.update.v1',
    requiresConfirmation: true,
    idempotency: { required: true, keySource: 'surface-item' },
    resultSurface: { updateSurfaceId: 'surface-my-settings', openPlacement: 'inline' },
    audit: { eventType: 'UserSettingsUpdateRequested', traceRequired: true }
  },
  signOut: {
    actionId: 'action-sign-out',
    label: 'Sign out',
    intent: 'command',
    capabilityId: 'profile.read',
    requiresConfirmation: true,
    idempotency: { required: false },
    audit: { eventType: 'SessionSignOutRequested', traceRequired: true }
  }
} satisfies Record<string, SurfaceAction>;

const agentDefinitionsCapability = 'agent.definitions.manage';
const agentPromptsCapability = 'agent.prompts.govern';
const agentSkillsCapability = 'agent.skills.govern';
const agentToolBoundariesCapability = 'agent.tool_boundaries.manage';
const agentModelsReadCapability = 'agent.models.read';
const agentModelsManageCapability = 'agent.models.manage';
const agentRuntimeTestCapability = 'agent.runtime.test';

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

export const myAccountDashboardSurface = envelope(
  'surface-my-account-dashboard',
  'dashboard',
  'My Account',
  'agent-my-account',
  {
    cards: [
      { cardId: 'card-my-profile', label: 'Profile', value: 'View or edit', severity: 'info' },
      { cardId: 'card-my-settings', label: 'Settings', value: 'Preferences', severity: 'info' },
      { cardId: 'card-sign-out', label: 'Sign out', value: 'End session', severity: 'warning' }
    ],
    sections: [
      { sectionId: 'self-service', label: 'Self-service', summary: 'Profile and settings open as request/response surfaces in this My Account workstream.' },
      { sectionId: 'security-boundary', label: 'Security boundary', summary: 'Roles, memberships, support access, and tenant administration stay in governed admin workstreams.' }
    ]
  },
  [myAccountSurfaceActions.showProfile, myAccountSurfaceActions.showSettings, myAccountSurfaceActions.signOut, surfaceActionsByIntent.trace]
);

export const myProfileSurface = envelope(
  'surface-my-profile',
  'detail-edit',
  'User profile',
  'agent-my-account',
  {
    recordId: 'acct-admin-profile',
    recordLabel: 'Tenant Admin · admin@example.test',
    recordKind: 'profile',
    summary: 'Current signed-in user profile. Administrative role and membership changes are intentionally not editable here.',
    fields: [
      { fieldId: 'displayName', label: 'Display name', value: 'Tenant Admin', editable: true, inputType: 'text' },
      { fieldId: 'email', label: 'Email', value: 'admin@example.test', editable: false, inputType: 'email', disabledReason: 'Email is owned by WorkOS/AuthKit identity reconciliation.' },
      { fieldId: 'locale', label: 'Locale', value: 'en-US', editable: true, inputType: 'select', options: [{ value: 'en-US', label: 'English (US)' }, { value: 'en-GB', label: 'English (UK)' }] },
      { fieldId: 'timeZone', label: 'Time zone', value: 'America/New_York', editable: true, inputType: 'text' }
    ],
    version: 1,
    permissionState: { canEdit: true, authoritativeCapabilityId: 'profile.update' },
    audit: { lastEventType: 'UserProfileDisplayed', lastActor: 'Tenant Admin', traceIds: ['trace-surface-my-profile'] }
  },
  [myAccountSurfaceActions.updateProfile, surfaceActionsByIntent.trace]
);

export const mySettingsSurface = envelope(
  'surface-my-settings',
  'detail-edit',
  'User settings',
  'agent-my-account',
  {
    recordId: 'acct-admin-settings',
    recordLabel: 'Tenant Admin settings',
    recordKind: 'settings',
    summary: 'Current signed-in user preferences for the workstream shell and notifications.',
    fields: [
      { fieldId: 'preferredColorMode', label: 'Color mode', value: 'system', editable: true, inputType: 'select', options: [{ value: 'system', label: 'System' }, { value: 'light', label: 'Light' }, { value: 'dark', label: 'Dark' }] },
      { fieldId: 'notificationDigest', label: 'Notification digest', value: 'daily', editable: true, inputType: 'select', options: [{ value: 'realtime', label: 'Realtime' }, { value: 'daily', label: 'Daily' }, { value: 'off', label: 'Off' }] },
      { fieldId: 'composerDensity', label: 'Composer density', value: 'comfortable', editable: true, inputType: 'select', options: [{ value: 'compact', label: 'Compact' }, { value: 'comfortable', label: 'Comfortable' }] }
    ],
    version: 1,
    permissionState: { canEdit: true, authoritativeCapabilityId: 'profile.update' },
    audit: { lastEventType: 'UserSettingsDisplayed', lastActor: 'Tenant Admin', traceIds: ['trace-surface-my-settings'] }
  },
  [myAccountSurfaceActions.updateSettings, surfaceActionsByIntent.trace]
);

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
    userAdminSurfaceActions.displayUserDetail,
    surfaceActionsByIntent.command,
    userAdminSurfaceActions.resendInvitation,
    userAdminSurfaceActions.revokeInvitation,
    userAdminSurfaceActions.replaceRole,
    surfaceActionsByIntent.trace
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
    summary: 'Scoped detail/edit surface for a tenant user account. Field editability is advisory; the secure tenant user foundation capability remains authoritative.',
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
      authoritativeCapabilityId: secureTenantUserFoundation
    },
    audit: {
      lastEventType: 'UserAdminDetailDisplayed',
      lastActor: 'Tenant Admin',
      traceIds: ['trace-user-admin-detail', 'trace-user-admin-row']
    }
  },
  [
    userAdminSurfaceActions.updateUserProfile,
    userAdminSurfaceActions.replaceRole,
    surfaceActionsByIntent.trace
  ]
);

export const agentAdminCatalogSurface = envelope(
  'surface-agent-admin-catalog',
  'dashboard',
  'Agent Admin command center',
  'agent-agent-admin',
  {
    cards: [
      { cardId: 'agent-definitions', label: 'Agent definitions ready', value: 5, severity: 'info' },
      { cardId: 'prompt-review', label: 'Prompt drafts needing review', value: 2, severity: 'warning' },
      { cardId: 'skill-manifest-review', label: 'Skill manifest approvals', value: 1, severity: 'critical' },
      { cardId: 'tool-boundary-denials', label: 'Recent tool boundary denials', value: 3, severity: 'critical' }
    ],
    sections: [
      { sectionId: 'loading-state', label: 'Loading', summary: 'Agent catalog shows Loading surface… while /api/agent-admin definitions load.' },
      { sectionId: 'empty-state', label: 'Empty', summary: 'No AgentDefinition records yet; create a draft definition before assigning prompts or skills.' },
      { sectionId: 'forbidden-state', label: 'Forbidden', summary: 'Cross-tenant AgentDefinition ids return TARGET_NOT_FOUND_OR_FORBIDDEN and keep draft content hidden.' },
      { sectionId: 'trace-linked-state', label: 'Trace linked', summary: 'PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace links are surfaced without provider secrets.' }
    ]
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

export const dashboardSurface = myAccountDashboardSurface;
export const listSearchSurface = userAdminListSearchSurface;
export const detailEditSurface = userAdminDetailEditSurface;
export const decisionSurface = envelope('surface-decision-card', 'decision', 'Approve bounded outreach plan', 'agent-governance-policy', { decisionId: 'decision-1', recommendation: 'Approve after evidence review.', riskScore: 72, confidenceScore: 84, evidence: [{ evidenceId: 'evidence-1', label: 'Trace summary', summary: 'Agent stayed within tool boundary.' }] }, [surfaceActionsByIntent.approval, surfaceActionsByIntent.trace]);
export const auditTimelineSurface = envelope('surface-audit-timeline', 'audit-timeline', 'Admin audit timeline', 'agent-audit-trace', { events: [{ eventId: 'audit-1', occurredAt: generatedAt, actor: 'Tenant Admin', action: 'invited user', traceId: 'trace-invite' }] }, [surfaceActionsByIntent.trace]);
export const workflowStatusSurface = envelope('surface-workflow-status', 'workflow-status', 'Invitation workflow status', 'agent-user-admin', { workflowId: 'workflow-invite-1', status: 'waiting-for-human', steps: [{ stepId: 'send-email', label: 'Send invitation email', status: 'waiting-for-human' }] }, [surfaceActionsByIntent.workflow]);
export const governanceDiffSurface = envelope('surface-governance-diff', 'governance-diff', 'Policy proposal diff', 'agent-governance-policy', { proposalId: 'proposal-1', beforeSummary: 'Manual approval over 75 risk.', afterSummary: 'Manual approval over 65 risk.', changes: [{ path: 'risk.approvalThreshold', before: '75', after: '65', impact: 'More decisions require human review.' }] }, [surfaceActionsByIntent.proposal, surfaceActionsByIntent.governance]);
export const outcomeSurface = envelope('surface-outcome-review', 'outcome', 'Outcome review', 'agent-governance-policy', { outcomeId: 'outcome-1', metrics: [{ metricId: 'decision-cycle-time', label: 'Decision cycle time', current: 4, target: 2, unit: 'hours' }] }, [surfaceActionsByIntent.read]);

export const canonicalSurfaceEnvelopes = [
  myAccountDashboardSurface,
  myProfileSurface,
  mySettingsSurface,
  userAdminDashboardSurface,
  userAdminListSearchSurface,
  agentAdminCatalogSurface,
  agentAdminDetailSurface,
  agentPromptGovernanceSurface,
  agentSkillManifestSurface,
  agentToolBoundarySurface,
  agentModelRefsSurface,
  agentTestConsoleSurface,
  agentBehaviorProposalSurface,
  agentAdminTraceSurface,
  dashboardSurface,
  detailEditSurface,
  decisionSurface,
  auditTimelineSurface,
  workflowStatusSurface,
  governanceDiffSurface,
  outcomeSurface
];
export const allSurfaceActions: SurfaceAction[] = [...Object.values(surfaceActionsByIntent), ...Object.values(myAccountSurfaceActions), ...Object.values(userAdminSurfaceActions), ...Object.values(agentAdminSurfaceActions)];

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

export const showMyProfileActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Show user profile. The profile surface is appended in the My Account workstream with scoped fields and trace links.',
  correlationId: 'corr-show-my-profile',
  traceIds: ['trace-show-my-profile'],
  resultSurface: myProfileSurface
};

export const showMySettingsActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Show user settings. The settings surface is appended in the My Account workstream with scoped preferences and trace links.',
  correlationId: 'corr-show-my-settings',
  traceIds: ['trace-show-my-settings'],
  resultSurface: mySettingsSurface
};

export const signOutActionResult: CapabilityActionResult = {
  status: 'accepted',
  message: 'Sign out requested. The browser session will be ended by the authenticated shell.',
  correlationId: 'corr-sign-out',
  traceIds: ['trace-sign-out'],
  resultSurface: myAccountDashboardSurface
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

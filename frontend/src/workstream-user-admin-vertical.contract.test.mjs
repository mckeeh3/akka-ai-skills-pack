import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const agents = read('./__tests__/fixtures/workstream/agents.ts');
const me = read('./__tests__/fixtures/workstream/me.ts');
const surfaces = read('./__tests__/fixtures/workstream/surfaces.ts');
const workstream = read('./__tests__/fixtures/workstream/workstream.ts');
const apiClient = read('./__tests__/fixtures/api/FixtureWorkstreamApiClient.ts');
const httpApiClient = read('./api/HttpWorkstreamApiClient.ts');
const main = read('./main.tsx');
const dashboardSurface = read('./workstream/surfaces/DashboardSurface.tsx');
const listSearchSurface = read('./workstream/surfaces/ListSearchSurface.tsx');
const detailEditSurface = read('./workstream/surfaces/DetailEditSurface.tsx');
const userAdminTaskSurface = read('./workstream/surfaces/UserAdminTaskSurface.tsx');
const userAdminScopedAdminSurface = read('./workstream/surfaces/UserAdminScopedAdminSurface.tsx');
const workflowStatusSurface = read('./workstream/surfaces/WorkflowStatusSurface.tsx');
const systemMessageSurface = read('./workstream/surfaces/SystemMessageSurface.tsx');
const adminUsersPage = read('./screens/admin/AdminUsersPage.tsx');
const renderer = read('./workstream/surfaces/SurfaceRenderer.tsx');
const surfaceTypes = read('./workstream/types/surfaces.ts');
const componentsCss = read('./styles/components.css');
const workstreamService = read('../../src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java');
const userAdminFunctionalAgentDescription = read('../../app-description/domains/core-starter/workstreams/user-admin/agents/functional-agent.md');
const userAdminSurfaceDescription = read('../../app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md');
const userAdminAkkaRealization = read('../../app-description/domains/core-starter/workstreams/user-admin/realization/akka-components.md');
const userAdminFrontendRealization = read('../../app-description/domains/core-starter/workstreams/user-admin/realization/frontend-routes.md');

test('User Admin functional agent defaults to markdown_response and uses the governed foundation capability', () => {
  assert.match(agents, /label: 'User Admin'/);
  assert.match(agents, /defaultSurfaceType: 'markdown_response'/);
  assert.match(agents, /secure-tenant-user-foundation/);
  assert.match(me, /secure-tenant-user-foundation/);
});

test('User Admin dashboard, list, and detail surfaces use canonical surface ids and scoped variants', () => {
  assert.match(surfaces, /userAdminDashboardSurface/);
  assert.match(surfaces, /surface-user-admin-dashboard/);
  assert.match(surfaces, /legacySurfaceId: 'user-admin-dashboard'/);
  assert.match(surfaces, /User Admin Dashboard/);
  assert.match(surfaces, /invitation-queue/);
  assert.match(surfaces, /access-review/);
  assert.match(surfaces, /admin-audit/);
  assert.match(surfaces, /userAdminListSearchSurface/);
  assert.match(surfaces, /surface-user-admin-users/);
  assert.match(surfaces, /userAdminDetailEditSurface/);
  assert.match(surfaces, /surface-user-admin-user-detail/);
  assert.match(surfaces, /Tenant Admin account detail/);
  assert.match(surfaces, /userAdminRoleChangePreviewSurface/);
  assert.match(surfaces, /surface-user-admin-role-change-preview/);
  assert.match(surfaces, /userAdminRoleCapabilityMatrixSurface/);
  assert.match(surfaces, /user-admin-role-capability-matrix/);
  assert.match(surfaces, /userAdminAccessReviewSurface/);
  assert.match(surfaces, /user-admin-access-review/);
  assert.match(surfaces, /permissionState/);
  for (const rowKind of ['user-directory', 'invitation-queue', 'membership', 'support-access', 'admin-audit-excerpt']) {
    assert.match(surfaces, new RegExp(rowKind));
  }
  assert.match(surfaces, /last-admin-risk/);
  assert.match(surfaces, /self-disable/);
  assert.match(surfaces, /Backend authorization denied role replacement/);
  assert.match(surfaces, /accessManagement/);
  assert.match(surfaces, /user_admin\.role_change_preview\.v1/);
  assert.match(surfaces, /capabilityDelta/);
  assert.match(surfaces, /affectedWorkstreams/);
  assert.match(surfaces, /trace-user-admin-detail/);
  assert.match(surfaces, /table-to-card/);
  for (const variant of ['SaaS Owner Admin', 'Tenant Admin', 'Customer Admin']) {
    assert.match(surfaces, new RegExp(variant));
  }
  for (const state of ['loading', 'empty', 'error', 'forbidden', 'stale']) {
    assert.match(surfaces, new RegExp(state));
  }
  assert.match(surfaces, /SAAS_OWNER_NO_SUPPORT_ACCESS|SAAS_OWNER_SUPPORT_ACCESS_REQUIRED/);
  assert.match(surfaces, /CUSTOMER_ADMIN_TENANT_ACTION_DENIED/);
});

test('User Admin dashboard follows current actionable command-center rules', () => {
  assert.match(dashboardSurface, /Things that need my attention/);
  assert.match(dashboardSurface, /attention-counter-strip user-admin-attention-strip/);
  assert.match(dashboardSurface, /Every counter opens a backend-authorized queue, including zero-count queues/);
  assert.match(dashboardSurface, /Things I can do/);
  assert.match(dashboardSurface, /user-admin-work-card/);
  assert.match(surfaces, /userAdminDashboardSurface[\s\S]*userAdminSurfaceActions\.displayUserList[\s\S]*\]\n\);/);
  assert.match(dashboardSurface, /Open scoped administration surfaces/);
  assert.match(dashboardSurface, /function userAdminQueueAction/);
  assert.match(dashboardSurface, /backendAuthoredUserAdminQueues/);
  assert.match(dashboardSurface, /backendAuthoredUserAdminPopulationCards/);
  assert.match(surfaceTypes, /attentionCounts\?:/);
  assert.match(surfaceTypes, /administeredPopulations\?:/);
  assert.match(dashboardSurface, /action-useradmin-read-access-review/);
  assert.match(dashboardSurface, /action-read-support-access/);
  assert.match(dashboardSurface, /action-open-admin-audit/);
  assert.match(dashboardSurface, /action-display-user-list/);
  assert.match(dashboardSurface, /function userDirectoryAction/);
  assert.match(dashboardSurface, /action-user-admin-show-users/);
  assert.match(componentsCss, /\.user-admin-attention-strip/);
  assert.match(componentsCss, /\.user-admin-work-card/);
  assert.ok(dashboardSurface.indexOf('Things that need my attention') < dashboardSurface.indexOf('Things I can do'));
  assert.doesNotMatch(dashboardSurface, /Open the queue, decision card, or trace|Queue drilldowns|User Admin attention queue drilldowns|Access operations|Administer scoped users|User Admin selected scope and authority|Access health and blockers|Primary User Admin next actions|user-admin-next-actions/);
});

test('User Admin surface actions map to capability ids and trace or audit affordances', () => {
  assert.match(surfaces, /action-display-user-list/);
  assert.match(surfaces, /action-user-admin-show-users/);
  assert.match(surfaces, /user-admin\.show-users/);
  assert.match(surfaces, /Show Users/);
  assert.match(surfaces, /Show users/);
  assert.match(surfaces, /UserAdminListDisplayed/);
  assert.match(surfaces, /secureTenantUserFoundation = 'secure-tenant-user-foundation'/);
  for (const capabilityId of [
    'USERADMIN_VIEW_OVERVIEW',
    'USERADMIN_LIST_INVITATIONS',
    'USERADMIN_SEND_INVITATION',
    'USERADMIN_RESEND_INVITATION',
    'USERADMIN_REVOKE_INVITATION',
    'USERADMIN_LIST_MEMBERS',
    'USERADMIN_UPDATE_MEMBER_STATUS',
    'USERADMIN_LIST_ROLES_CAPABILITIES',
    'USERADMIN_PREVIEW_ROLE_CHANGE',
    'USERADMIN_CHANGE_MEMBER_ROLES',
    'user_admin.access_review.start',
    'user_admin.access_review.read',
    'user_admin.access_review.cancel',
    'user_admin.access_review.accept_result',
    'user_admin.access_review.reject_result',
    'USERADMIN_VIEW_TRACE_REFERENCE'
  ]) {
    assert.match(surfaces, new RegExp(capabilityId));
  }
  for (const denialCategory of ['cross-tenant', 'disabled actor', 'CUSTOMER_ADMIN_TENANT_ACTION_DENIED', 'SAAS_OWNER_NO_SUPPORT_ACCESS', 'role escalation', 'last-admin']) {
    assert.match(surfaces, new RegExp(denialCategory));
  }
  assert.match(surfaces, /requiresApproval: true/);
  assert.match(surfaces, /idempotency: \{ required: true/);
  assert.match(surfaces, /traceRequired: true/);
  assert.match(surfaces, /action-invite-user/);
  assert.match(surfaces, /action-useradmin-resend-invitation/);
  assert.match(surfaces, /action-useradmin-revoke-invitation/);
  assert.match(surfaces, /action-useradmin-disable-member/);
  assert.match(surfaces, /action-useradmin-reactivate-member/);
  assert.match(surfaces, /action-useradmin-preview-role-change/);
  assert.match(surfaces, /action-useradmin-change-member-roles/);
  assert.match(surfaces, /action-useradmin-start-access-review/);
  assert.match(surfaces, /action-useradmin-read-access-review/);
  assert.match(surfaces, /action-useradmin-cancel-access-review/);
  assert.match(surfaces, /action-useradmin-accept-access-review-result/);
  assert.match(surfaces, /action-useradmin-reject-access-review-result/);
  assert.match(surfaces, /user_admin\.access_review_task\.v1/);
  assert.match(surfaces, /blocked_provider_or_runtime/);
  assert.match(surfaces, /providerFailures/);
  assert.match(surfaces, /noDirectMutation/);
  assert.match(surfaces, /completed_review_required/);
  assert.match(workstreamService, /modelToolDataPolicyUsage/);
  assert.match(workflowStatusSurface, /Model, tool, data, and policy usage/);
  assert.match(workflowStatusSurface, /Access review model tool data policy trace links/);
  assert.match(workflowStatusSurface, /surface-audit-trace-detail/);
  assert.match(surfaces, /result_accepted/);
  assert.match(surfaces, /must not fake progress|not fake progress/);
  assert.match(surfaces, /displayUserListActionResult/);
  assert.match(surfaces, /action-open-useradmin-invitation-create/);
  assert.match(surfaces, /targetSurfaceId: 'surface-user-admin-invitation-create'/);
  assert.match(listSearchSurface, /inviteSurfaceAction/);
  assert.doesNotMatch(listSearchSurface, /user-admin-invite-form|submitInvite|Create scoped invitation/);
  assert.match(surfaces, /targetSurfaceId: 'surface-user-admin-user-detail'/);
  assert.match(surfaces, /openActionId: 'action-display-user-detail'/);
  assert.match(surfaces, /targetSurfaceId: 'surface-user-admin-invitation-detail'/);
  assert.match(surfaces, /openActionId: 'action-display-invitation-detail'/);
  assert.match(surfaces, /action-display-user-detail/);
  assert.match(surfaces, /Display user detail/);
  assert.match(surfaces, /UserAdminDetailDisplayed/);
  assert.match(surfaces, /displayUserDetailActionResult/);
});

test('User Admin frontend contract proves dashboard-to-user-branch traversal and safe trace metadata', () => {
  assert.match(surfaces, /userAdminDashboardSurface[\s\S]*action-user-admin-show-users[\s\S]*surface-user-admin-users/);
  assert.match(surfaces, /userAdminListSearchSurface[\s\S]*targetSurfaceId: 'surface-user-admin-user-detail'/);
  assert.match(surfaces, /userAdminListSearchSurface[\s\S]*openActionId: 'action-display-user-detail'/);
  assert.match(surfaces, /userAdminDetailEditSurface[\s\S]*userAdminSurfaceActions\.showUsers/);
  assert.match(detailEditSurface, /branchRootSurfaceId: branch\?\.branchRootSurfaceId \?\? envelope\.data\.branchRootSurfaceId \?\? 'surface-user-admin-users'/);
  assert.match(detailEditSurface, /branchReturnActionId: branch\?\.branchReturnActionId \?\? envelope\.data\.branchReturnActionId \?\? 'action-user-admin-show-users'/);
  assert.match(dashboardSurface, /userDirectoryAction/);
  assert.match(dashboardSurface, /action-user-admin-show-users/);
  assert.match(listSearchSurface, /row\.openActionId/);
  assert.match(listSearchSurface, /row\.targetSurfaceId/);
  assert.match(surfaces, /trace-user-admin-detail/);
  assert.match(componentsCss, /user-admin-branch-return/);
  assert.doesNotMatch(`${surfaces}\n${dashboardSurface}\n${listSearchSurface}`, /invite-token|tokenHash|Authorization:\s*Bearer|RESEND_API_KEY|sk-secret|api_key=/);
});

test('User Admin task/confirmation descendants render purpose-specific frontend surfaces', () => {
  for (const surfaceId of [
    'surface-user-admin-invitation-create',
    'surface-user-admin-invitation-resend-confirmation',
    'surface-user-admin-invitation-revoke-confirmation',
    'surface-user-admin-membership-status-confirmation',
    'surface-user-admin-support-access-grant',
    'surface-user-admin-support-access-revoke-confirmation',
    'surface-user-admin-identity-exception-review'
  ]) {
    assert.match(userAdminTaskSurface, new RegExp(surfaceId));
    assert.match(workstreamService, new RegExp(surfaceId));
  }
  for (const contract of [
    'user_admin.invitation_create.v1',
    'user_admin.invitation_resend_confirmation.v1',
    'user_admin.invitation_revoke_confirmation.v1',
    'user_admin.membership_status_confirmation.v1',
    'user_admin.support_access_grant.v1',
    'user_admin.support_access_revoke_confirmation.v1',
    'user_admin.identity_exception_review.v1'
  ]) {
    assert.match(userAdminTaskSurface, new RegExp(contract.replaceAll('.', '\\.')));
    assert.match(workstreamService, new RegExp(contract.replaceAll('.', '\\.')));
  }
  assert.match(renderer, /isUserAdminTaskSurface\(selectedEnvelope\)/);
  assert.match(userAdminTaskSurface, /function InvitationCreateTask/);
  assert.match(userAdminTaskSurface, /userAdminRoleOptions/);
  assert.match(userAdminTaskSurface, /userAdminExpiryOptions/);
  assert.match(userAdminTaskSurface, /policyOptions\?\.roles/);
  assert.match(userAdminTaskSurface, /policyOptions\?\.expiryHours/);
  assert.match(userAdminTaskSurface, /function InvitationConfirmationTask/);
  assert.match(userAdminTaskSurface, /function InvitationTaskDeliveryPanel/);
  assert.match(userAdminTaskSurface, /Provider-backed delivery status returns on the invitation detail surface/);
  assert.match(userAdminTaskSurface, /Raw invitation tokens, full email bodies, Resend payloads, provider message ids, and secrets are redacted/);
  assert.match(userAdminTaskSurface, /deliveryState \?\? envelope\.data\.delivery/);
  assert.match(userAdminTaskSurface, /providerReadiness/);
  assert.match(userAdminTaskSurface, /lastSafeError/);
  assert.match(userAdminTaskSurface, /recoverySteps/);
  assert.match(userAdminTaskSurface, /function MembershipStatusTask/);
  assert.match(userAdminTaskSurface, /function SupportAccessGrantTask/);
  assert.match(userAdminTaskSurface, /function SupportAccessRevokeTask/);
  assert.match(userAdminTaskSurface, /function IdentityExceptionReview/);
  assert.match(userAdminTaskSurface, /action-user-admin-show-users/);
  assert.match(userAdminTaskSurface, /action-useradmin-grant-support-access/);
  assert.match(userAdminTaskSurface, /action-useradmin-extend-support-access/);
  assert.match(userAdminTaskSurface, /action-confirm-user-admin-support-access-revoke/);
  assert.match(userAdminTaskSurface, /action-useradmin-revoke-support-access/);
  assert.match(userAdminTaskSurface, /branchRootSurfaceId: branch\?\.branchRootSurfaceId \?\? envelope\.data\.branchRootSurfaceId \?\? 'surface-user-admin-users'/);
  assert.match(userAdminTaskSurface, /safeFilterPreservation: branch\?\.safeFilterPreservation \?\? envelope\.data\.safeFilterPreservation \?\? 'backend-authored-only'/);
  assert.match(userAdminTaskSurface, /aria-label="User Admin branch navigation"/);
  assert.doesNotMatch(userAdminTaskSurface, /raw JWTs, invitation tokens, provider payloads, and secrets are omitted|Browser redaction|Target scope/);
  assert.doesNotMatch(detailEditSurface, /aria-label="Edit user status"|aria-label="Edit user role"|Permanently remove user|submitRoleChange|submitStatusChange/);
  assert.match(detailEditSurface, /Read-only inspection/);
  assert.match(detailEditSurface, /Dedicated task surfaces/);
  assert.match(detailEditSurface, /function InvitationDeliveryStatusPanel/);
  assert.match(detailEditSurface, /Provider-backed delivery/);
  assert.match(detailEditSurface, /Provider\/outbox delivery failed closed/);
  assert.match(detailEditSurface, /No raw tokens, Resend payloads, email bodies, provider message ids, and secrets are not shown|raw tokens, Resend payloads, email bodies, provider message ids, and secrets are not shown/);
  assert.match(adminUsersPage, /quarantined-legacy-screen/);
  assert.match(adminUsersPage, /not be used as a normal runtime path/);
  assert.match(userAdminTaskSurface, /No direct mutation: recovery must route to deterministic backend approval\/status flows or safe user detail/);
  assert.match(userAdminTaskSurface, /action-useradmin-request-identity-relink/);
  assert.match(userAdminTaskSurface, /action-useradmin-approve-identity-relink/);
  assert.match(userAdminTaskSurface, /action-useradmin-deny-identity-relink/);
  assert.match(userAdminTaskSurface, /action-useradmin-complete-identity-relink/);
  assert.match(userAdminTaskSurface, /Raw WorkOS ids, JWTs, and provider payloads are hidden/);
  assert.match(workstreamService, /requestIdentityRelinkAction\(\)/);
  assert.match(workstreamService, /approveIdentityRelinkAction\(\)/);
  assert.match(workstreamService, /completeIdentityRelinkAction\(\)/);
  assert.match(workstreamService, /lifecycleStatus/);
  assert.match(workstreamService, /provider-boundary:redacted/);
  assert.match(workstreamService, /withUserBranchReturn\(List\.of\(validateSupportAccessGrantAction\(\), submitSupportAccessGrantAction\(\), grantSupportAccessAction\(\), extendSupportAccessAction\(\), displayDetailAction\(\), openAuditAction\(\)\)\)/);
  assert.match(workstreamService, /withUserBranchReturn\(List\.of\(confirmUserAdminSupportAccessRevokeAction\(\), revokeSupportAccessAction\(\), displayDetailAction\(\), showUsersAction\(\), openAuditAction\(\)\)\)/);
  assert.match(componentsCss, /\.user-admin-task-surface/);
  assert.match(componentsCss, /\.user-admin-task-form label/);
  assert.doesNotMatch(userAdminTaskSurface, /JSON\.stringify|dangerouslySetInnerHTML|Authorization:\s*Bearer|RESEND_API_KEY|sk-secret|api_key=/);
});

test('User Admin scoped admin surfaces cover SaaS Owner, Organization Admin, Customer, and Customer Admin branches', () => {
  assert.match(renderer, /isUserAdminScopedAdminSurface\(selectedEnvelope\)/);
  assert.match(renderer, /isOrganizationLifecycleSurface\(selectedEnvelope\)/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.saas_owner_admins\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.saas_owner_admin_invitation_create\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.organization_admins\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.organization_admin_invitation_create\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.organization_admin_detail\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.customer_directory\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.customer_detail\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.customer_create\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.customer_rename\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.customer_suspend_confirmation\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.customer_reactivate_confirmation\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.customer_admins\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.customer_admin_invitation_create\.v1/);
  assert.match(userAdminScopedAdminSurface, /user_admin\.customer_admin_detail\.v1/);
  assert.match(userAdminScopedAdminSurface, /function RoleScopedInvitationForm/);
  assert.match(userAdminScopedAdminSurface, /candidate\.actionId === 'action-submit-saas-owner-admin-invitation'/);
  assert.match(userAdminScopedAdminSurface, /candidate\.actionId === 'action-customer-admin-invite'/);
  assert.match(userAdminScopedAdminSurface, /No backend-authorized role option is available for this selected scope/);
  assert.match(userAdminScopedAdminSurface, /The browser does not infer hidden targets, role eligibility, or authority from labels/);
  assert.doesNotMatch(userAdminScopedAdminSurface, /TENANT_ADMIN|SAAS_OWNER_ADMIN/);
  assert.match(userAdminScopedAdminSurface, /action-customer-admin-invite/);
  assert.match(userAdminScopedAdminSurface, /customerId: String\(envelope\.data\.customerId/);
  assert.match(userAdminScopedAdminSurface, /customerName: String\(envelope\.data\.customerName/);
  assert.match(userAdminScopedAdminSurface, /query: String\(envelope\.data\.query/);
  assert.match(userAdminScopedAdminSurface, /status: String\(envelope\.data\.status/);
  assert.match(workstreamService, /action-customer-admin-invite/);
  assert.match(workstreamService, /ScopeType\.CUSTOMER, actor\.selectedContext\(\)\.tenantId\(\), customer\.customer\(\)\.customerId\(\)/);
  assert.match(workstreamService, /targetScopeProof/);
  assert.match(userAdminScopedAdminSurface, /function CustomerTaskForm/);
  assert.match(userAdminScopedAdminSurface, /function ScopedInspection/);
  assert.match(userAdminScopedAdminSurface, /function ScopedDirectory/);
  assert.match(userAdminScopedAdminSurface, /BranchReturn/);
  assert.match(userAdminScopedAdminSurface, /Provider\/outbox failures return a typed system message/);
  assert.match(userAdminScopedAdminSurface, /The browser does not infer hidden targets, role eligibility, or authority from labels/);
  assert.doesNotMatch(userAdminScopedAdminSurface, /Authorization:\s*Bearer|RESEND_API_KEY|WORKOS_API_KEY|sk-secret|api_key=|dangerouslySetInnerHTML/);
});

test('Customer branch action ids are normalized between app-description and runtime evidence', () => {
  const description = [
    userAdminFunctionalAgentDescription,
    userAdminSurfaceDescription,
    userAdminAkkaRealization,
    userAdminFrontendRealization
  ].join('\n');

  for (const canonicalAction of [
    'action-user-admin-show-customers',
    'action-customer-read',
    'action-open-customer-create',
    'action-customer-create',
    'action-open-customer-rename',
    'action-customer-rename',
    'action-open-customer-suspend',
    'action-customer-suspend',
    'action-open-customer-reactivate',
    'action-customer-reactivate',
    'action-user-admin-show-customer-admins',
    'action-open-customer-admin-invitation-create',
    'action-customer-admin-invite'
  ]) {
    assert.match(description, new RegExp(canonicalAction));
    assert.match(workstreamService, new RegExp(canonicalAction));
  }

  for (const documentedNonAlias of ['action-customer-list', 'action-customer-admin-list', 'action-customer-admin-manage']) {
    assert.match(description, new RegExp(`${documentedNonAlias}.*not an active alias|not active.*${documentedNonAlias}|documentation-only shorthand`, 's'));
    assert.doesNotMatch(workstreamService, new RegExp(`new SurfaceAction\\("${documentedNonAlias}"`));
  }
});

test('User Admin full-stack conformance tests cover canonical runtime boundaries', () => {
  assert.match(workstreamService, /surface-user-admin-system-message/);
  assert.match(workstreamService, /user_admin\.system_message\.v1/);
  assert.match(workstreamService, /noFakeSuccess/);
  assert.match(workstreamService, /hiddenUserId/);
  assert.match(workstreamService, /hiddenMembershipId/);
  assert.match(workstreamService, /invitation-not-found-or-forbidden/);
  assert.match(workstreamService, /blocked_provider_or_runtime/);
  assert.match(workstreamService, /invitationDeliveryState/);
  assert.match(workstreamService, /providerReadiness/);
  assert.match(workstreamService, /recoverySurfaceId/);
  assert.match(workstreamService, /user_admin\.system_message\.v1/);
  assert.match(workstreamService, /Provider message ids, raw Resend payloads, email bodies, tokens, and secrets are redacted/);
  assert.match(workstreamService, /noDirectMutation/);
  assert.match(workstreamService, /modelToolDataPolicyUsage/);
  assert.match(workstreamService, /surface-audit-trace-detail/);
  assert.match(workstreamService, /raw prompts, raw tool payloads, provider credentials, JWTs, hidden tenant\/customer evidence, and access secrets are omitted/);
  assert.match(workstreamService, /diagnosticMetadataVisible", false/);
  assert.match(workstreamService, /attentionCounts/);
  assert.match(workstreamService, /administeredPopulations/);
  assert.match(workstreamService, /roleOptionsForSelectedContext/);
  assert.match(workstreamService, /invitationExpiryOptions/);
  assert.match(workstreamService, /supportExpiryOptions/);
  assert.match(workstreamService, /purposeOptions/);

  assert.match(renderer, /case 'show-inspection'/);
  assert.match(renderer, /case 'system-message'/);
  assert.match(renderer, /isUserAdminTaskSurface\(selectedEnvelope\)/);
  assert.match(detailEditSurface, /UserAdminCleanDetail/);
  assert.match(detailEditSurface, /Detail surfaces do not mutate access inline/);
  assert.match(detailEditSurface, /taskEntryActions = envelope\.actions\.filter\(\(action\) => !isUserAdminShowUsersAction\(action\)\)/);
  assert.doesNotMatch(detailEditSurface, /action-useradmin-disable-member[\s\S]*submit|action-useradmin-change-member-roles[\s\S]*submitRoleChange/);
  assert.match(listSearchSurface, /row\.openActionId/);
  assert.match(listSearchSurface, /row\.targetSurfaceId/);
  assert.match(listSearchSurface, /backend-authored/);
  assert.match(userAdminTaskSurface, /userAdminRoleOptions\(envelope\)/);
  assert.match(userAdminTaskSurface, /userAdminExpiryOptions\(envelope\)/);
  assert.match(systemMessageSurface, /Recovery steps/);
  assert.match(systemMessageSurface, /Provider secrets, raw JWTs, hidden prompts, invitation tokens, and unauthorized tenant\/customer evidence are not shown/);

  assert.match(adminUsersPage, /quarantined-legacy-screen/);
  assert.match(adminUsersPage, /not imported by\s+the canonical entry point/);
  assert.doesNotMatch(main, /AdminUsersPage|admin-users|screens\/admin/);
  assert.doesNotMatch(`${main}\n${httpApiClient}\n${dashboardSurface}\n${listSearchSurface}\n${detailEditSurface}\n${userAdminTaskSurface}`, /RESEND_API_KEY|WORKOS_API_KEY|Authorization:\s*Bearer|invite-token|tokenHash|sk-secret|api_key=/);
});

test('workstream and API clients support dashboard-to-list-to-detail navigation feedback', () => {
  assert.match(workstream, /initialWorkstreamItems: WorkstreamItem\[\] = \[\]/);
  assert.match(surfaces, /surface-user-admin-users/);
  assert.match(surfaces, /Display user detail/);
  assert.match(surfaces, /surface-user-admin-user-detail/);
  assert.match(apiClient, /displayUserListActionResult/);
  assert.match(apiClient, /displayUserDetailActionResult/);
  assert.match(apiClient, /action-display-user-list/);
  assert.match(apiClient, /action-display-user-detail/);
  assert.match(apiClient, /action-useradmin-resend-invitation/);
  assert.match(apiClient, /action-useradmin-disable-member/);
  assert.match(apiClient, /userAdminMemberStatusActionSurface/);
  assert.match(apiClient, /userAdminRoleChangePreviewSurface/);
  assert.match(listSearchSurface, /View\/edit user/);
  assert.match(listSearchSurface, /function userAdminRowAction/);
  assert.match(listSearchSurface, /row\.openActionId/);
  assert.match(listSearchSurface, /row\.targetSurfaceId/);
  assert.match(listSearchSurface, /userDetailInput\(row\)/);
  assert.match(workstreamService, /detailSurface\(actor, request\.input\(\), request\.correlationId\(\)\)/);
  assert.match(workstreamService, /user_admin\.user_detail\.v1/);
  assert.match(workstreamService, /membershipId/);
  assert.match(componentsCss, /\.surface-frame tr:last-child td \{ border-bottom: 1px solid var\(--color-border\); \}/);
  const frontendApiContracts = read('../../app-description/55-ui/frontend-api-contracts.md');
  assert.match(frontendApiContracts, /\/api\/admin\/users\/dashboard/);
  assert.match(frontendApiContracts, /\/api\/admin\/users/);
  assert.match(frontendApiContracts, /\/api\/admin\/users\/\{accountId\}/);
  assert.match(frontendApiContracts, /\/api\/admin\/invitations/);
  assert.match(frontendApiContracts, /\/api\/admin\/invitations\/\{invitationId\}\/resend/);
  assert.match(frontendApiContracts, /\/api\/admin\/invitations\/\{invitationId\}\/revoke/);
  assert.match(frontendApiContracts, /UserAdminDashboardPayload/);
  assert.match(frontendApiContracts, /UserAdminUserAccountPayload/);
  assert.match(frontendApiContracts, /raw invitation tokens\/token hashes/);
  assert.match(httpApiClient, /\/api\/workstream\/bootstrap/);
  assert.match(httpApiClient, /\/api\/workstream\/actions/);
  assert.match(httpApiClient, /X-Selected-Context-Id/);
  assert.match(main, /HttpWorkstreamApiClient/);
  assert.doesNotMatch(main, /fixtureWorkstream/);
  assert.match(main, /result\.value\.resultSurface/);
  assert.match(main, /selectedSurfaceId: targetSurface\.surfaceId/);
});

test('composer submits through backend workstream message API instead of page routes or frontend heuristics', () => {
  assert.match(main, /handleComposerSubmit/);
  assert.match(main, /submitWorkstreamMessage/);
  assert.match(main, /selectedSurfaceId: surface\.surfaceId/);
  assert.match(main, /userItem, agentItem/);
  assert.doesNotMatch(main, /showUsers|showUserDetail|requestedSurface/);
  assert.doesNotMatch(main, /window\.location\.assign\('\/users/);
});

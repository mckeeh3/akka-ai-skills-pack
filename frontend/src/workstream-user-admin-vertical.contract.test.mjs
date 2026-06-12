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
const componentsCss = read('./styles/components.css');
const workstreamService = read('../../src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java');

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
  assert.match(surfaces, /Back to users/);
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
  assert.match(surfaces, /result_accepted/);
  assert.match(surfaces, /must not fake progress|not fake progress/);
  assert.match(surfaces, /displayUserListActionResult/);
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

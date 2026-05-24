import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const agents = read('./workstream/fixtures/agents.ts');
const me = read('./workstream/fixtures/me.ts');
const surfaces = read('./workstream/fixtures/surfaces.ts');
const workstream = read('./workstream/fixtures/workstream.ts');
const apiClient = read('./api/FixtureWorkstreamApiClient.ts');
const httpApiClient = read('./api/HttpWorkstreamApiClient.ts');
const main = read('./main.tsx');

test('User Admin functional agent defaults to markdown_response and uses the governed foundation capability', () => {
  assert.match(agents, /label: 'User Admin'/);
  assert.match(agents, /defaultSurfaceType: 'markdown_response'/);
  assert.match(agents, /secure-tenant-user-foundation/);
  assert.match(me, /secure-tenant-user-foundation/);
});

test('User Admin dashboard, list, and detail surfaces use canonical surface ids and scoped variants', () => {
  assert.match(surfaces, /userAdminDashboardSurface/);
  assert.match(surfaces, /user-admin-dashboard/);
  assert.match(surfaces, /User Admin dashboard/);
  assert.match(surfaces, /invitation-queue/);
  assert.match(surfaces, /access-review/);
  assert.match(surfaces, /admin-audit/);
  assert.match(surfaces, /userAdminListSearchSurface/);
  assert.match(surfaces, /user-admin-user-list/);
  assert.match(surfaces, /userAdminDetailEditSurface/);
  assert.match(surfaces, /user-admin-user-account/);
  assert.match(surfaces, /Tenant Admin account detail/);
  assert.match(surfaces, /permissionState/);
  for (const rowKind of ['user-directory', 'invitation-queue', 'membership', 'support-access', 'admin-audit-excerpt']) {
    assert.match(surfaces, new RegExp(rowKind));
  }
  assert.match(surfaces, /last-admin-risk/);
  assert.match(surfaces, /Backend authorization denied role replacement/);
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

test('User Admin surface actions map to capability ids and trace or audit affordances', () => {
  assert.match(surfaces, /action-display-user-list/);
  assert.match(surfaces, /Display user list view/);
  assert.match(surfaces, /UserAdminListDisplayed/);
  assert.match(surfaces, /secureTenantUserFoundation = 'secure-tenant-user-foundation'/);
  for (const capabilityId of [
    'admin.users.dashboard.read',
    'admin.users.search',
    'admin.users.detail.read',
    'admin.invitations.create',
    'admin.invitations.resend',
    'admin.invitations.revoke',
    'admin.users.profile.patch',
    'admin.memberships.add',
    'admin.memberships.suspend',
    'admin.memberships.reactivate',
    'admin.memberships.remove',
    'admin.roles.replace',
    'admin.roles.remove',
    'admin.users.disable',
    'admin.users.reactivate',
    'admin.users.identity_relink.request',
    'admin.users.identity_relink.complete',
    'admin.support_access.read',
    'admin.support_access.grant',
    'admin.support_access.revoke',
    'admin.support_access.extend',
    'admin.access_review.read',
    'admin.access_review.resolve',
    'admin.audit.read'
  ]) {
    assert.match(surfaces, new RegExp(capabilityId.replaceAll('.', '\\.')));
  }
  for (const denialCategory of ['cross-tenant', 'disabled actor', 'CUSTOMER_ADMIN_TENANT_ACTION_DENIED', 'SAAS_OWNER_NO_SUPPORT_ACCESS', 'role escalation', 'last-admin']) {
    assert.match(surfaces, new RegExp(denialCategory));
  }
  assert.match(surfaces, /requiresApproval: true/);
  assert.match(surfaces, /idempotency: \{ required: true/);
  assert.match(surfaces, /traceRequired: true/);
  assert.match(surfaces, /displayUserListActionResult/);
  assert.match(surfaces, /action-display-user-detail/);
  assert.match(surfaces, /Display user account detail/);
  assert.match(surfaces, /UserAdminDetailDisplayed/);
  assert.match(surfaces, /displayUserDetailActionResult/);
});

test('workstream and API clients support five core markdown plus demo dashboard-to-list-to-detail navigation feedback', () => {
  assert.match(workstream, /item-v0-user-admin-markdown/);
  assert.match(workstream, /kind: 'markdown_response'/);
  assert.match(surfaces, /user-admin-user-list/);
  assert.match(surfaces, /Display user account detail/);
  assert.match(surfaces, /user-admin-user-account/);
  assert.match(apiClient, /displayUserListActionResult/);
  assert.match(apiClient, /displayUserDetailActionResult/);
  assert.match(apiClient, /action-display-user-list/);
  assert.match(apiClient, /action-display-user-detail/);
  assert.match(httpApiClient, /\/api\/workstream\/bootstrap/);
  assert.match(httpApiClient, /\/api\/workstream\/actions/);
  assert.match(httpApiClient, /X-Selected-Context-Id/);
  assert.match(main, /HttpWorkstreamApiClient/);
  assert.match(main, /fixtureWorkstream/);
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

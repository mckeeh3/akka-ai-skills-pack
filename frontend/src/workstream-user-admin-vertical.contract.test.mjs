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

test('User Admin functional agent defaults to a dashboard and uses the governed foundation capability', () => {
  assert.match(agents, /label: 'User Admin'/);
  assert.match(agents, /defaultSurfaceType: 'dashboard'/);
  assert.match(agents, /secure-tenant-user-foundation/);
  assert.match(me, /secure-tenant-user-foundation/);
});

test('User Admin dashboard, list, and detail surfaces model the command-center contract', () => {
  assert.match(surfaces, /userAdminDashboardSurface/);
  assert.match(surfaces, /surface-user-admin-dashboard/);
  assert.match(surfaces, /User Admin command center/);
  assert.match(surfaces, /invitation-queue/);
  assert.match(surfaces, /access-review/);
  assert.match(surfaces, /admin-audit/);
  assert.match(surfaces, /userAdminListSearchSurface/);
  assert.match(surfaces, /surface-user-admin-list/);
  assert.match(surfaces, /userAdminDetailEditSurface/);
  assert.match(surfaces, /surface-user-admin-detail-admin/);
  assert.match(surfaces, /Tenant Admin account detail/);
  assert.match(surfaces, /permissionState/);
  for (const rowKind of ['user-directory', 'invitation-queue', 'membership', 'support-access', 'admin-audit-excerpt']) {
    assert.match(surfaces, new RegExp(rowKind));
  }
  assert.match(surfaces, /last-admin-risk/);
  assert.match(surfaces, /Backend authorization denied role replacement/);
  assert.match(surfaces, /trace-user-admin-detail/);
  assert.match(surfaces, /table-to-card/);
});

test('User Admin surface actions map to capability ids and trace or audit affordances', () => {
  assert.match(surfaces, /action-display-user-list/);
  assert.match(surfaces, /Display user list view/);
  assert.match(surfaces, /UserAdminListDisplayed/);
  assert.match(surfaces, /secureTenantUserFoundation = 'secure-tenant-user-foundation'/);
  assert.match(surfaces, /governance-decisions-audit/);
  assert.match(surfaces, /traceRequired: true/);
  assert.match(surfaces, /displayUserListActionResult/);
  assert.match(surfaces, /action-display-user-detail/);
  assert.match(surfaces, /Display user account detail/);
  assert.match(surfaces, /UserAdminDetailDisplayed/);
  assert.match(surfaces, /displayUserDetailActionResult/);
});

test('workstream and API clients support dashboard-to-list-to-detail navigation feedback', () => {
  assert.match(workstream, /item-display-user-list/);
  assert.match(workstream, /Display the user list view/);
  assert.match(workstream, /surface-user-admin-list/);
  assert.match(workstream, /item-display-user-detail/);
  assert.match(workstream, /Display user account detail/);
  assert.match(workstream, /surface-user-admin-detail-admin/);
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
  assert.match(main, /selectedSurfaceId: result\.value\.resultSurface\.surfaceId/);
});

test('composer opens User Admin list/detail surfaces instead of page routes', () => {
  assert.match(main, /handleComposerSubmit/);
  assert.match(main, /showUsers/);
  assert.match(main, /showUserDetail/);
  assert.match(main, /surface-user-admin-list/);
  assert.match(main, /surface-user-admin-detail-admin/);
  assert.match(main, /Composer intent “show users”/);
  assert.match(main, /Composer intent “show admin@example\.test detail”/);
  assert.doesNotMatch(main, /window\.location\.assign\('\/users/);
});

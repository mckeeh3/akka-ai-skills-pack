import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const main = read('./main.tsx');
const agents = read('./__tests__/fixtures/workstream/agents.ts');
const me = read('./__tests__/fixtures/workstream/me.ts');
const surfaces = read('./__tests__/fixtures/workstream/surfaces.ts');
const contextBar = read('./workstream/shell/ContextAuthorityBar.tsx');
const shellState = read('./workstream/shell/shellState.ts');
const railState = read('./workstream/rail/railState.ts');
const surfaceFrame = read('./workstream/surfaces/SurfaceStateFrame.tsx');
const httpRealtime = read('./api/HttpWorkstreamRealtimeClient.ts');

test('governance, audit, admin, and profile work no longer depends on removed screen modules', () => {
  assert.match(main, /<WorkstreamShell/);
  assert.match(main, /<WorkstreamStream/);
  assert.doesNotMatch(main, /import \{ GovernancePoliciesPage \}|import \{ AuditTraceExplorerPage \}|import \{ AdminUsersPage \}|import \{ ProfilePreferencesPage \}/);
  assert.doesNotMatch(main, /route === 'governance'|route === 'audit'|route === 'admin'|route === 'profile'/);
});

test('functional agents represent My Account plus admin, audit, governance, billing, and support work areas', () => {
  for (const agentId of ['my-account-agent', 'user-admin-agent', 'agent-admin-agent', 'audit-trace-agent', 'governance-policy-agent', 'agent-billing', 'agent-support-access']) {
    assert.match(agents, new RegExp(agentId));
  }
  assert.match(agents, /availability: 'denied'/);
  assert.match(agents, /availability: 'hidden'/);
  assert.match(agents, /availability: 'disabled'/);
  assert.match(railState, /visibleRailEntries/);
  assert.match(railState, /isAgentSelectable/);
  assert.match(railState, /agentDisabledReason/);
});

test('/api/me fixtures and context bar cover selected AuthContext, support access, disabled, and forbidden states', () => {
  for (const fixture of ['meTenantAdmin', 'meRegularMember', 'meAuditorSupport', 'meDisabledUser', 'meForbiddenNoMembership']) {
    assert.match(me, new RegExp(`export const ${fixture}`));
  }
  assert.match(contextBar, /Selected AuthContext/);
  assert.match(contextBar, /supportAccess\?\.active/);
  assert.match(contextBar, /pendingApprovalCount/);
  assert.match(contextBar, /traceLinks\.map/);
  assert.match(shellState, /The signed-in account is disabled/);
  assert.match(shellState, /No active tenant membership is available/);
  assert.match(surfaceFrame, /className="ds-card surface-frame forbidden"/);
});

test('admin, audit, governance, and profile work are structured surfaces with capability-backed actions', () => {
  assert.match(surfaces, /userAdminDashboardSurface/);
  assert.match(surfaces, /userAdminListSearchSurface/);
  assert.match(surfaces, /userAdminDetailEditSurface/);
  assert.match(surfaces, /auditTimelineSurface/);
  assert.match(surfaces, /governanceDiffSurface/);
  assert.match(surfaces, /dashboardSurface/);
  assert.match(surfaces, /user_admin.view_overview/);
  assert.match(surfaces, /audit\.trace\.read/);
  assert.match(surfaces, /governance\.policy\.simulate/);
  assert.match(surfaces, /Backend authorization denied/);
});

test('production realtime client uses workstream SSE with stale, reconnect, malformed, and selected-context behavior', () => {
  assert.match(httpRealtime, /\/api\/workstream\/events/);
  assert.match(httpRealtime, /selectedContextId/);
  assert.match(httpRealtime, /lastEventId/);
  assert.match(httpRealtime, /surface\.stale/);
  assert.match(httpRealtime, /surface\.reconnected/);
  assert.match(httpRealtime, /projection\.refresh\.available/);
  assert.match(httpRealtime, /Malformed realtime event was ignored/);
  assert.match(httpRealtime, /Bounded workstream event replay ended/);
});

import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const main = read('./main.tsx');
const agents = read('./workstream/fixtures/agents.ts');
const me = read('./workstream/fixtures/me.ts');
const surfaces = read('./workstream/fixtures/surfaces.ts');
const contextBar = read('./workstream/shell/ContextAuthorityBar.tsx');
const shellState = read('./workstream/shell/shellState.ts');
const railState = read('./workstream/rail/railState.ts');
const surfaceFrame = read('./workstream/surfaces/SurfaceStateFrame.tsx');

const quarantinedLegacyScreens = [
  './screens/governance/GovernancePoliciesPage.tsx',
  './screens/audit/AuditTraceExplorerPage.tsx',
  './screens/admin/AdminUsersPage.tsx',
  './screens/profile/ProfilePreferencesPage.tsx'
];

test('governance, audit, admin, and profile legacy screens are not primary app routes', () => {
  for (const path of quarantinedLegacyScreens) {
    assert.ok(existsSync(new URL(path, import.meta.url)), `${path} remains only as a quarantined mechanics reference`);
  }
  assert.match(main, /<WorkstreamShell/);
  assert.match(main, /<SurfaceRenderer/);
  assert.doesNotMatch(main, /import \{ GovernancePoliciesPage \}|import \{ AuditTraceExplorerPage \}|import \{ AdminUsersPage \}|import \{ ProfilePreferencesPage \}/);
  assert.doesNotMatch(main, /route === 'governance'|route === 'audit'|route === 'admin'|route === 'profile'/);
});

test('functional agents represent admin, audit, governance, profile, billing, and support work areas', () => {
  for (const agentId of ['agent-access-profile', 'agent-user-admin', 'agent-agent-admin', 'agent-audit-trace', 'agent-governance-policy', 'agent-billing', 'agent-support-access']) {
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
  assert.match(surfaces, /secure-tenant-user-foundation/);
  assert.match(surfaces, /audit\.trace\.read/);
  assert.match(surfaces, /governance\.policy\.simulate/);
  assert.match(surfaces, /Backend authorization denied/);
});

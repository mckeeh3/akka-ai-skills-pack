import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const meFixtures = read('./__tests__/fixtures/workstream/me.ts');
const agentFixtures = read('./__tests__/fixtures/workstream/agents.ts');
const surfaceFixtures = read('./__tests__/fixtures/workstream/surfaces.ts');
const authTypes = read('./workstream/types/auth.ts');
const surfaceTypes = read('./workstream/types/surfaces.ts');
const rail = read('./workstream/rail/FunctionalAgentRail.tsx');
const dashboardSurface = read('./workstream/surfaces/DashboardSurface.tsx');

test('My Account fixtures expose authority basis, personal attention, and scoped trace refs', () => {
  assert.match(meFixtures, /authorityBasis/);
  assert.match(meFixtures, /contextCapabilityGroups/);
  assert.match(meFixtures, /traceRefs/);
  assert.match(meFixtures, /my_account\.list_personal_attention/);
  assert.match(authTypes, /AuthorityBasisSummary/);
  assert.match(authTypes, /TraceRef/);
  assert.match(surfaceTypes, /attentionItems\?:/);
  assert.match(surfaceTypes, /traceRefs\?:/);
  assert.match(surfaceFixtures, /personal-attention-user-admin-invitations/);
  assert.match(surfaceFixtures, /personal-attention-agent-admin-provider/);
  assert.match(surfaceFixtures, /blocked_provider_or_runtime/);
  assert.match(surfaceFixtures, /not_found_or_redacted/);
  assert.match(surfaceFixtures, /trace-my-account-personal-attention/);
});

test('My Account preserves lower-left user tile launch and no duplicate top-rail entry', () => {
  assert.match(agentFixtures, /functionalAgentId: 'agent-my-account'/);
  assert.match(rail, /const myAccountFunctionalAgentId = 'agent-my-account'/);
  assert.match(rail, /entry\.functionalAgentId !== myAccountFunctionalAgentId/);
  assert.match(rail, /rail-user-button/);
  assert.doesNotMatch(rail, /entries\.map[\s\S]*agent-my-account[\s\S]*FunctionalAgentRailItem/);
});

test('My Account dashboard renders personal attention and trace refs as workstream-first surfaces', () => {
  assert.match(dashboardSurface, /Personal attention items/);
  assert.match(dashboardSurface, /My Account trace refs/);
  assert.match(dashboardSurface, /capabilityId/);
  assert.match(dashboardSurface, /redaction-note/);
  assert.match(surfaceFixtures, /surface-my-account-dashboard/);
  assert.match(surfaceFixtures, /surface-my-profile/);
  assert.match(surfaceFixtures, /surface-my-settings/);
  assert.match(surfaceFixtures, /open_authorized_workstream/);
});

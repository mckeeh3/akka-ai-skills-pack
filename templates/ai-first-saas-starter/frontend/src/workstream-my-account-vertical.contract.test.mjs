import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const surfaces = read('./workstream/fixtures/surfaces.ts');
const dashboard = read('./workstream/surfaces/DashboardSurface.tsx');
const types = read('./workstream/types/surfaces.ts');
const fixtureClient = read('./api/FixtureWorkstreamApiClient.ts');
const main = read('./main.tsx');

test('My Account fixtures define summary, profile, settings, next steps, and trace surfaces', () => {
  for (const id of ['surface-my-account-dashboard', 'surface-my-profile', 'surface-my-settings', 'surface-my-account-trace']) {
    assert.match(surfaces, new RegExp(id));
  }
  for (const capability of ['my_account.view_summary', 'my_account.view_context', 'my_account.update_profile_settings', 'my_account.list_next_steps', 'my_account.open_authorized_workstream', 'my_account.ask_agent', 'my_account.view_own_trace_refs']) {
    assert.match(surfaces, new RegExp(capability.replace(/[.]/g, '\\.')));
  }
  assert.match(surfaces, /NO_SELECTED_CONTEXT_SAFE_GLOBAL_ONLY/);
  assert.match(surfaces, /PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, and AgentWorkTrace/);
});

test('My Account surface actions preserve backend authority, idempotency, denials, and cross-workstream launch semantics', () => {
  for (const action of ['action-show-my-profile', 'action-show-my-settings', 'action-update-my-profile', 'action-update-my-settings', 'action-open-user-admin', 'action-open-agent-admin', 'action-open-audit-trace']) {
    assert.match(surfaces, new RegExp(action));
  }
  assert.match(surfaces, /schema\.my-account\.profile\.update\.v1/);
  assert.match(surfaces, /schema\.my-account\.settings\.update\.v1/);
  assert.match(surfaces, /idempotency: \{ required: true, keySource: 'surface-item' \}/);
  assert.match(surfaces, /shellRequest: \{ requestType: 'open_workstream'/);
  assert.match(surfaces, /AUTHKIT_SESSION_ACTION/);
});

test('Dashboard renderer exposes My Account blocked state, next steps, and trace-safe rendering hooks', () => {
  assert.match(types, /nextSteps\?:/);
  assert.match(types, /blockedState\?:/);
  assert.match(dashboard, /aria-label="Authorized next steps"/);
  assert.match(dashboard, /Backend capability summary allows opening this workstream/);
  assert.match(dashboard, /Trace: \{step\.traceId\}/);
  assert.doesNotMatch(dashboard, /dangerouslySetInnerHTML|innerHTML\s*=/);
});

test('Fixture client returns My Account action result surfaces for frontend-only validation', () => {
  for (const result of ['displayMyAccountDashboardActionResult', 'displayMyAccountProfileActionResult', 'displayMyAccountSettingsActionResult', 'updateMyAccountSettingsActionResult']) {
    assert.match(surfaces, new RegExp(result));
    assert.match(fixtureClient, new RegExp(result));
  }
  assert.match(main, /buildCapabilityActionRequest/);
  assert.match(main, /onSurfaceAction=\{handleSurfaceAction\}/);
});

import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const agents = read('./workstream/fixtures/agents.ts');
const me = read('./workstream/fixtures/me.ts');
const surfaces = read('./workstream/fixtures/surfaces.ts');
const workstream = read('./workstream/fixtures/workstream.ts');
const apiClient = read('./api/FixtureWorkstreamApiClient.ts');

test('Agent Admin functional agent is visible and capability backed for v0 governed runtime', () => {
  assert.match(agents, /label: 'Agent Admin'[\s\S]*defaultSurfaceType: 'markdown_response'/);
  for (const capability of [
    'agent_admin.submit_turn',
    'agent_admin.list_definitions',
    'agent_admin.get_definition',
    'agent_admin.draft_behavior_change',
    'agent_admin.simulate_tool_boundary',
    'agent_admin.get_model_ref'
  ]) {
    assert.match(agents, new RegExp(capability.replaceAll('.', '\\.')));
    assert.match(me, new RegExp(capability.replaceAll('.', '\\.')));
  }
  assert.match(agents, /attention: \{ count: 4, severity: 'critical' \}/);
  assert.match(agents, /availability: 'visible'/);
});

test('Agent Admin fixtures include catalog, detail, governed diffs, model refs, test console, decisions, and traces', () => {
  for (const fixture of [
    'agentAdminCatalogSurface',
    'agentAdminDetailSurface',
    'agentPromptGovernanceSurface',
    'agentSkillManifestSurface',
    'agentToolBoundarySurface',
    'agentModelRefsSurface',
    'agentTestConsoleSurface',
    'agentBehaviorProposalSurface',
    'agentAdminTraceSurface'
  ]) {
    assert.match(surfaces, new RegExp(fixture));
  }
  for (const surfaceId of [
    'surface-agent-admin-catalog',
    'surface-agent-admin-detail',
    'surface-agent-prompt-governance',
    'surface-agent-skill-manifest-diff',
    'surface-agent-tool-boundary-diff',
    'surface-agent-model-refs',
    'surface-agent-test-console',
    'surface-agent-behavior-proposal',
    'surface-agent-admin-trace'
  ]) {
    assert.match(surfaces, new RegExp(surfaceId));
  }
});

test('Agent Admin surfaces preserve required UI states, approval gates, validation, redaction, and trace links', () => {
  for (const marker of [
    'Loading surface',
    'Empty',
    'TARGET_NOT_FOUND_OR_FORBIDDEN',
    'validation-error',
    'approval-required',
    'MODEL_POLICY_DENIED',
    'TOOL_BOUNDARY_DENIED',
    'Provider secret values are never browser-visible',
    'PromptAssemblyTrace',
    'SkillLoadTrace',
    'AgentWorkTrace',
    'readSkill(skillId)',
    'No-side-effect agent test console'
  ]) {
    assert.match(surfaces, new RegExp(marker.replace(/[()]/g, '\\$&')));
  }
  assert.match(surfaces, /requiresApproval: true/);
  assert.match(surfaces, /secretVisibility: 'redacted'/);
  assert.match(surfaces, /trace-prompt-assembly-42/);
  assert.match(surfaces, /trace-skill-load-17/);
  assert.match(surfaces, /trace-agent-work-88/);
});

test('Agent Admin actions and fixture client return structured surfaces instead of page routes', () => {
  for (const actionId of [
    'action-display-agent-catalog',
    'action-open-agent-detail',
    'action-propose-prompt-diff',
    'action-test-agent-prompt',
    'action-approve-skill-manifest',
    'action-simulate-tool-boundary',
    'action-manage-model-ref',
    'action-open-agent-trace'
  ]) {
    assert.match(surfaces, new RegExp(actionId));
  }
  assert.match(surfaces, /displayAgentCatalogActionResult/);
  assert.match(surfaces, /displayAgentDetailActionResult/);
  assert.match(apiClient, /displayAgentCatalogActionResult/);
  assert.match(apiClient, /displayAgentDetailActionResult/);
  assert.match(apiClient, /agent_admin\.list_definitions/);
});

test('Agent Admin defaults to five core v0 markdown and keeps richer governance surfaces demo-scoped', () => {
  assert.match(workstream, /item-v0-agent-admin-markdown/);
  assert.match(workstream, /kind: 'markdown_response'/);
  assert.match(workstream, /governed agent definitions, prompts, skills, tool boundaries, models, and traces/);
  for (const surfaceId of [
    'surface-agent-admin-catalog',
    'surface-agent-admin-detail',
    'surface-agent-prompt-governance',
    'surface-agent-tool-boundary-diff',
    'surface-agent-test-console'
  ]) {
    assert.match(surfaces, new RegExp(surfaceId));
  }
  assert.match(surfaces, /fullCoreDemoSurfaceEnvelopes/);
  assert.match(surfaces, /trace-agent-work-88/);
});

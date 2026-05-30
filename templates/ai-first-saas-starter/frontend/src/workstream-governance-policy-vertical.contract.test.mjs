import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const fixtures = read('./workstream/fixtures/surfaces.ts');
const me = read('./workstream/fixtures/me.ts');
const workstream = read('./workstream/fixtures/workstream.ts');
const diff = read('./workstream/surfaces/GovernanceDiffSurface.tsx');
const types = read('./workstream/types/surfaces.ts');
const apiClient = read('./api/FixtureWorkstreamApiClient.ts');

test('Governance/Policy v0 exposes contract capabilities, structured surfaces, and trace-linked actions', () => {
  for (const capability of [
    'governance.policy.read',
    'governance.policy.simulate',
    'governance.policy.propose',
    'governance.policy.approve',
    'governance.policy.activate',
    'governance.policy.rollback',
    'governance.policy.analysis.start',
    'governance.policy.analysis.read',
    'audit.trace.read'
  ]) {
    assert.match(fixtures, new RegExp(capability.replaceAll('.', '\\.')));
    assert.match(me, new RegExp(capability.replaceAll('.', '\\.')));
  }
  for (const surface of [
    'governancePolicyDashboardSurface',
    'governancePolicyInventorySurface',
    'governancePolicyProposalSurface',
    'governancePolicySimulationSurface',
    'governancePolicyDecisionSurface',
    'governancePolicyActivationBlockedSurface',
    'governancePolicyRollbackBlockedSurface',
    'governancePolicyAnalysisTaskSurface',
    'governancePolicyDecisionTraceSurface'
  ]) {
    assert.match(fixtures, new RegExp(`export const ${surface}`));
  }
  assert.match(fixtures, /governancePolicyStructuredSurfaces/);
});

test('Governance/Policy UI states preserve backend authority, denials, approval gates, traces, and no fake analysis', () => {
  for (const marker of [
    'Frontend actions only reflect backend capability state',
    'Launcher visibility and action buttons are convenience signals only',
    'Backend capability checks decide every protected action',
    'Simulation is advisory and grants no authority',
    'requiresApproval: true',
    'blocked_provider_or_runtime',
    'the UI must not fake task progress',
    'No model-less or deterministic fake analysis result is produced',
    'trace-govpol-simulation',
    'trace-govpol-analysis-blocked'
  ]) {
    assert.match(fixtures, new RegExp(marker.replace(/[()]/g, '\\$&')));
  }
  assert.match(workstream, /item-v0-governance-policy-markdown/);
  assert.match(workstream, /policy guardrails, approval boundaries/);
});

test('Governance/Policy proposal and simulation rendering is accessible and browser-safe', () => {
  assert.match(types, /riskClassification\?:/);
  assert.match(types, /simulation\?:/);
  assert.match(types, /taskKind\?: 'workflow' \| 'autonomous-agent-analysis'/);
  assert.match(diff, /aria-label="Policy proposal summary"/);
  assert.match(diff, /Policy proposal governance metadata/);
  assert.match(diff, /Deterministic simulation evidence/);
  assert.match(diff, /Trace links:/);
  assert.doesNotMatch(diff, /dangerouslySetInnerHTML|innerHTML\s*=/);
});

test('Governance/Policy fixture client returns structured results for dashboard, inventory, and simulation actions', () => {
  assert.match(apiClient, /displayGovernancePolicyDashboardActionResult/);
  assert.match(apiClient, /displayGovernancePolicyInventoryActionResult/);
  assert.match(apiClient, /displayGovernancePolicySimulationActionResult/);
  for (const actionId of ['action-governance-policy-dashboard', 'action-governance-policy-list', 'action-governance-policy-draft-proposal', 'action-governance-policy-submit-proposal', 'action-governance-policy-simulate', 'action-governance-policy-decide', 'action-governance-policy-activate', 'action-governance-policy-rollback', 'action-governance-policy-start-impact-analysis']) {
    assert.match(fixtures, new RegExp(actionId));
  }
  for (const aliasActionId of ['action-govpol-show-dashboard', 'action-govpol-show-policy-inventory', 'action-govpol-simulate-proposal', 'action-govpol-start-impact-analysis', 'action-simulate-policy', 'action-commit-policy']) {
    assert.match(fixtures, new RegExp(aliasActionId));
  }
  assert.match(fixtures, /surface-governance-policy-impact-analysis/);
  assert.match(fixtures, /legacyAliasSurfaceId: 'surface-governance-policy-analysis-task'/);
  for (const routedActionId of ['action-governance-policy-dashboard', 'action-governance-policy-list', 'action-governance-policy-draft-proposal', 'action-governance-policy-submit-proposal', 'action-governance-policy-simulate', 'action-governance-policy-decide', 'action-governance-policy-activate', 'action-governance-policy-rollback', 'action-governance-policy-start-impact-analysis', 'resolveGovernancePolicyActionId']) {
    assert.match(apiClient, new RegExp(routedActionId));
  }
});

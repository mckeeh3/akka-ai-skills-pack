import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const fixtures = read('./__tests__/fixtures/workstream/surfaces.ts');
const me = read('./__tests__/fixtures/workstream/me.ts');
const workstream = read('./__tests__/fixtures/workstream/workstream.ts');
const diff = read('./workstream/surfaces/GovernanceDiffSurface.tsx');
const types = read('./workstream/types/surfaces.ts');
const apiClient = read('./__tests__/fixtures/api/FixtureWorkstreamApiClient.ts');

test('Governance/Policy v0 exposes contract capabilities, structured surfaces, and trace-linked actions', () => {
  for (const capability of [
    'governance.policy.read',
    'governance.policy.simulate',
    'governance.policy.propose',
    'governance.policy.approve',
    'governance.proposals.review',
    'governance.policy.activate',
    'governance.proposals.activate',
    'governance.policy.rollback',
    'governance.outcomes.record',
    'governance.policy.impact_analysis.start',
    'governance.policy.impact_analysis.read',
    'governance.policy.impact_analysis.cancel',
    'governance.policy.impact_analysis.accept_result',
    'governance.policy.impact_analysis.reject_result',
    'governance.policy.impact_analysis.request_changes',
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
    'governancePolicyImpactAnalysisTaskSurface',
    'governancePolicyImpactAnalysisResultSurface',
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
    'no frontend-only or deterministic success is rendered',
    'No model-less, simulated, deterministic, or fake impact_ready result is produced',
    'trace-govpol-simulation',
    'trace-govpol-impact-analysis-blocked',
    'workflow.governance_policy.impact_analysis.* and worker.task.*',
    'surface-governance-policy-impact-analysis-result',
    'surface.governance.proposal_queue.v1',
    'surface.governance.decision_card.v1',
    'surface.governance.activation_status.v1',
    'action-govpol-add-outcome-note'
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
  assert.match(apiClient, /displayGovernancePolicyImpactTaskActionResult/);
  assert.match(apiClient, /displayGovernancePolicyImpactResultActionResult/);
  for (const actionId of ['action-govpol-show-dashboard', 'action-govpol-show-policy-inventory', 'action-govpol-simulate-proposal', 'action-govpol-start-impact-analysis', 'action-govpol-accept-impact-result', 'action-govpol-request-impact-changes', 'action-govpol-add-outcome-note']) {
    assert.match(fixtures, new RegExp(actionId));
  }
  for (const routedActionId of ['action-govpol-show-dashboard', 'action-govpol-show-policy-inventory', 'action-govpol-simulate-proposal', 'action-govpol-start-impact-analysis']) {
    assert.match(apiClient, new RegExp(routedActionId));
  }
});

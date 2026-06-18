import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');
const escapeRegExp = (value) => value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

const fixtures = read('./__tests__/fixtures/workstream/surfaces.ts');
const me = read('./__tests__/fixtures/workstream/me.ts');
const workstream = read('./__tests__/fixtures/workstream/workstream.ts');
const diff = read('./workstream/surfaces/GovernanceDiffSurface.tsx');
const decision = read('./workstream/surfaces/DecisionSurface.tsx');
const listSearch = read('./workstream/surfaces/ListSearchSurface.tsx');
const outcome = read('./workstream/surfaces/OutcomeSurface.tsx');
const types = read('./workstream/types/surfaces.ts');
const apiClient = read('./__tests__/fixtures/api/FixtureWorkstreamApiClient.ts');

test('Governance/Policy exposes contract capabilities, structured surfaces, and trace-linked actions', () => {
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
  assert.match(workstream, /initialWorkstreamItems: WorkstreamItem\[\] = \[\]/);
  assert.match(fixtures, /governance\.policy\.(read|simulate|propose)/);
});

test('Governance/Policy proposal and simulation rendering is accessible and browser-safe', () => {
  assert.match(types, /riskClassification\?:/);
  assert.match(types, /proposalSummary\?:/);
  assert.match(types, /draftFields\?:/);
  assert.match(types, /lifecycleGate\?:/);
  assert.match(types, /availableTransitions\?:/);
  assert.match(types, /simulation\?:/);
  assert.match(types, /taskKind\?: 'workflow' \| 'autonomous-agent-analysis'/);
  assert.match(diff, /aria-label="Policy proposal summary"/);
  assert.match(diff, /Policy proposal governance metadata/);
  assert.match(diff, /Backend-authored proposal draft fields/);
  assert.match(diff, /Proposal lifecycle gate and provider readiness/);
  assert.match(diff, /Backend-authorized proposal transitions/);
  assert.match(diff, /actionInput=\{envelope\.data\.proposalId/);
  assert.match(diff, /Deterministic simulation evidence/);
  assert.match(diff, /Trace refs:/);
  assert.doesNotMatch(diff, /dangerouslySetInnerHTML|innerHTML\s*=/);
});

test('Governance/Policy decision-card renderer keeps command authority, disabled actions, traces, and browser safety backend-authored', () => {
  for (const marker of [
    'decisionSummary',
    'riskAndImpact',
    'decisionEvidence',
    'commandForm',
    'allowedActions',
    'disabledActions',
    'blocked_provider_or_runtime',
    'action-governance-policy-decide',
    'action-governance-policy-activate',
    'action-governance-policy-rollback',
    'noDirectMutation',
    'noFakeSuccess',
    'trace-govpol-decision',
    'trace-govpol-impact-analysis-blocked'
  ]) {
    assert.match(fixtures, new RegExp(escapeRegExp(marker)));
  }
  for (const marker of [
    'DecisionSurface',
    'disabledById',
    'Backend-authorized investigation actions',
    'Disabled or deferred investigation actions',
    'Role-gated action diagnostics',
    'Investigation guidance trace links',
    'SurfaceActionBar actions={guardedActions}',
    'Advisory output cannot directly mutate prompts, skills, references, model refs, tool boundaries, activation, rollback, provider configuration, retained evidence, policy, authorization, or export delivery.'
  ]) {
    assert.match(decision, new RegExp(escapeRegExp(marker)));
  }
  assert.doesNotMatch(decision, /localStorage\.getItem\(['"](?:token|jwt|apiKey)|Authorization\s*=|dangerouslySetInnerHTML|innerHTML\s*=/);
});

test('Governance/Policy inventory renderer keeps row actions backend-scoped, trace-linked, and browser-safe', () => {
  for (const marker of [
    'GovernancePolicyInventoryView',
    'Governance/Policy policy and proposal inventory',
    'Rows open backend-authorized evidence or lifecycle surfaces',
    'selected AuthContext, row visibility, redaction, and action availability are rechecked server-side',
    'Search visible policies and proposals',
    'action-governance-policy-list',
    'action-governance-policy-read',
    'action-governance-policy-draft-proposal',
    'role="list" aria-label="Authorized policy and proposal rows"',
    'aria-label={`Open ${title} through backend-authorized',
    'backendRowInput(row)',
    'Trace and row evidence are redacted for browser safety'
  ]) {
    assert.match(listSearch, new RegExp(escapeRegExp(marker)));
  }
  assert.doesNotMatch(listSearch, /localStorage\.getItem\(['"](?:token|jwt|apiKey)|Authorization\s*=|dangerouslySetInnerHTML|innerHTML\s*=/);
});

test('Governance/Policy outcome-panel renderer keeps notes governed, trace-linked, and browser-safe', () => {
  for (const marker of [
    'governance.policy.outcome.v1',
    'surface-governance-policy-outcome',
    'GovernancePolicyOutcomePanel',
    'Governance Policy outcome panel',
    'Outcome notes append governed feedback only',
    'cannot approve, activate, roll back, weaken policy, mutate metrics directly, or fabricate advisory evidence',
    'action-governance-policy-outcome-note',
    'targetSurfaceId: envelope.surfaceId',
    'surface-audit-trace-detail',
    'Browser-safe redaction',
    'Provider/runtime'
  ]) {
    assert.match(outcome, new RegExp(escapeRegExp(marker)));
  }
  for (const marker of [
    'proposalId?: string;',
    'outcomeSummary?: Record<string, unknown>;',
    'noteForm?: Record<string, unknown>;',
    'noteHistory?: string[];',
    'noFakeSuccess?: boolean;'
  ]) {
    assert.match(types, new RegExp(escapeRegExp(marker)));
  }
  assert.doesNotMatch(outcome, /localStorage\.getItem\(['"](?:token|jwt|apiKey)|Authorization\s*=|dangerouslySetInnerHTML|innerHTML\s*=/);
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

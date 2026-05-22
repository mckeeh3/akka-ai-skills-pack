import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const main = read('./main.tsx');
const surfaces = read('./workstream/fixtures/surfaces.ts');
const workstream = read('./workstream/fixtures/workstream.ts');
const renderer = read('./workstream/surfaces/SurfaceRenderer.tsx');
const decisionSurface = read('./workstream/surfaces/DecisionSurface.tsx');
const governanceDiffSurface = read('./workstream/surfaces/GovernanceDiffSurface.tsx');
const outcomeSurface = read('./workstream/surfaces/OutcomeSurface.tsx');
const workflowSurface = read('./workstream/surfaces/WorkflowStatusSurface.tsx');
const actions = read('./workstream/actions/capabilityActionState.ts');

const quarantinedLegacyScreens = [
  './screens/goals/GoalWorkbenchPage.tsx',
  './screens/decisions/DecisionQueuePage.tsx'
];

test('goal and decision legacy screens are quarantined behind the workstream shell', () => {
  for (const path of quarantinedLegacyScreens) {
    assert.ok(existsSync(new URL(path, import.meta.url)), `${path} remains only as a quarantined mechanics reference`);
  }
  assert.match(main, /<WorkstreamShell/);
  assert.match(main, /<WorkstreamStream/);
  assert.match(main, /selectedFunctionalAgentId/);
  assert.match(main, /selectedSurfaceId/);
  assert.doesNotMatch(main, /import \{ GoalWorkbenchPage \}|import \{ DecisionQueuePage \}/);
  assert.doesNotMatch(main, /route === 'goals'|route === 'decisions'|<GoalWorkbenchPage|<DecisionQueuePage/);
});

test('decision, governance, workflow, and outcome work are modeled as structured surfaces', () => {
  assert.match(surfaces, /decisionSurface = envelope\('surface-decision-card', 'decision'/);
  assert.match(surfaces, /governanceDiffSurface = envelope\('surface-governance-diff', 'governance-diff'/);
  assert.match(surfaces, /workflowStatusSurface = envelope\('surface-workflow-status', 'workflow-status'/);
  assert.match(surfaces, /outcomeSurface = envelope\('surface-outcome-review', 'outcome'/);
  assert.match(renderer, /case 'decision'/);
  assert.match(renderer, /case 'governance-diff'/);
  assert.match(renderer, /case 'workflow-status'/);
  assert.match(renderer, /case 'outcome'/);
  assert.match(decisionSurface, /Evidence|riskScore|confidenceScore/);
  assert.match(governanceDiffSurface, /beforeSummary|afterSummary|impact/);
  assert.match(workflowSurface, /workflow-steps|waiting-for-human/);
  assert.match(outcomeSurface, /outcome-metrics|target/);
});

test('decision and governance actions preserve approval, proposal, trace, and idempotency contracts', () => {
  assert.match(surfaces, /action-approve-decision/);
  assert.match(surfaces, /requiresApproval: true/);
  assert.match(surfaces, /DecisionApproved/);
  assert.match(surfaces, /action-propose-policy/);
  assert.match(surfaces, /PolicyProposalCreated/);
  assert.match(surfaces, /action-simulate-policy/);
  assert.match(surfaces, /PolicySimulationRequested/);
  assert.match(surfaces, /action-open-trace/);
  assert.match(surfaces, /traceRequired: true/);
  assert.match(actions, /buildCapabilityActionRequest/);
  assert.match(actions, /resolveIdempotencyKey/);
});

test('workstream items carry goal and decision-like work through the stream rather than routes', () => {
  assert.match(workstream, /kind: 'decision'/);
  assert.match(workstream, /kind: 'workflow-status'/);
  assert.match(workstream, /surface-decision-card/);
  assert.match(workstream, /surface-workflow-status/);
  assert.match(workstream, /waiting-for-human/);
  assert.match(main, /Routes are deep links into functional agents, stream items, and structured surfaces/);
});

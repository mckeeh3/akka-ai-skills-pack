import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');
const goals = readFileSync(new URL('./screens/goals/GoalWorkbenchPage.tsx', import.meta.url), 'utf8');
const decisions = readFileSync(new URL('./screens/decisions/DecisionQueuePage.tsx', import.meta.url), 'utf8');
const fixture = readFileSync(new URL('./api/FixtureApiClient.ts', import.meta.url), 'utf8');
const components = readFileSync(new URL('./styles/components.css', import.meta.url), 'utf8');

test('Slice 6 legacy goal and decision screens remain quarantined while app entry uses workstream surfaces', () => {
  const surfaces = readFileSync(new URL('./workstream/fixtures/surfaces.ts', import.meta.url), 'utf8');

  assert.ok(existsSync(new URL('./screens/goals/GoalWorkbenchPage.tsx', import.meta.url)));
  assert.ok(existsSync(new URL('./screens/decisions/DecisionQueuePage.tsx', import.meta.url)));
  assert.match(main, /<WorkstreamShell/);
  assert.match(main, /<SurfaceRenderer/);
  assert.match(surfaces, /surface-decision-card/);
  assert.match(surfaces, /surface-governance-diff|surface-outcome/);
  assert.doesNotMatch(main, /import \{ GoalWorkbenchPage \}/);
  assert.doesNotMatch(main, /route === 'goals'/);
});

test('Goal Workbench implements create, draft plan, launch, and approval acknowledgement states', () => {
  assert.match(goals, /Create a durable goal/);
  assert.match(goals, /validateGoalForm/);
  assert.match(goals, /Request draft plan/);
  assert.match(goals, /Approve launch/);
  assert.match(goals, /Acknowledge the approval gates before launching/);
  assert.match(goals, /focusFirstMappedError/);
  assert.match(fixture, /goals = \{/);
  assert.match(fixture, /createGoal/);
  assert.match(fixture, /draftPlan/);
  assert.match(fixture, /launchGoal/);
});

test('Decision Queue implements evidence-backed actions and stale conflict flow', () => {
  assert.match(decisions, /Decision Queue/);
  assert.match(decisions, /Evidence/);
  assert.match(decisions, /Alternatives considered/);
  assert.match(decisions, /DecisionActionForm/);
  assert.match(decisions, /I reviewed the evidence, risk, confidence, impact, policy trigger, and trace context/);
  assert.match(decisions, /Simulate stale version conflict/);
  assert.match(decisions, /Stale conflict/);
  assert.match(fixture, /actOnDecision/);
  assert.match(fixture, /This decision changed while you were reviewing it/);
});

test('Slice 6 styles preserve two-column desktop and single-column narrow task flow', () => {
  assert.match(components, /\.two-column-flow/);
  assert.match(components, /\.selectable-row/);
  assert.match(components, /\.decision-facts/);
  assert.match(components, /\.checkbox-row/);
  assert.match(components, /@media \(max-width: 1120px\)[\s\S]*\.two-column-flow \{ grid-template-columns: 1fr; \}/);
});

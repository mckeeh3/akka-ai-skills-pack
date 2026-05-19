import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const briefing = readFileSync(new URL('./screens/briefing/BriefingPage.tsx', import.meta.url), 'utf8');
const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');
const components = readFileSync(new URL('./styles/components.css', import.meta.url), 'utf8');
const workstreamShell = readFileSync(new URL('./workstream/shell/WorkstreamShell.tsx', import.meta.url), 'utf8');
const workstreamFixtures = readFileSync(new URL('./workstream/fixtures/workstream.ts', import.meta.url), 'utf8');
const workstreamEvents = readFileSync(new URL('./workstream/fixtures/events.ts', import.meta.url), 'utf8');

test('Mission Control remains a legacy screen while the app entry uses workstream shell fixtures', () => {
  assert.ok(existsSync(new URL('./screens/briefing/BriefingPage.tsx', import.meta.url)));
  assert.match(main, /<WorkstreamShell/);
  assert.match(main, /initialWorkstreamItems/);
  assert.match(main, /canonicalSurfaceEnvelopes/);
  assert.match(workstreamShell, /FunctionalAgentRail/);
  assert.match(workstreamShell, /WorkstreamComposer/);
  assert.doesNotMatch(main, /new FixtureApiClient/);
  assert.doesNotMatch(main, /<BriefingPage/);
});

test('Mission Control renders required validation panels', () => {
  assert.match(briefing, /MissionKpiBand/);
  assert.match(briefing, /AgentActivityTimeline/);
  assert.match(briefing, /NeedsAttentionPanel/);
  assert.match(briefing, /AgentTeamsPanel/);
  assert.match(briefing, /TrustControlsPanel/);
  assert.match(briefing, /UpcomingActionsPanel/);
});

test('Mission Control command strip is safe and does not execute high-impact commands', () => {
  assert.match(briefing, /never executes high-impact commands/);
  assert.match(briefing, /Safe preview prepared from fixture data/);
  assert.match(briefing, /open the relevant decision card or goal gate/);
});

test('Workstream fixtures carry forward stale and duplicate/replay realtime behavior', () => {
  assert.match(workstreamFixtures, /system-status/);
  assert.match(workstreamEvents, /stale/i);
  assert.match(workstreamEvents, /duplicate|replay|reconnect/i);
  assert.match(main, /Routes are deep links into functional agents/);
});

test('Mission Control responsive CSS keeps attention before secondary panels', () => {
  assert.match(components, /grid-template-areas:[\s\S]*"attention activity"/);
  assert.match(components, /@media \(max-width: 1120px\)[\s\S]*"attention"[\s\S]*"activity"[\s\S]*"teams"/);
  assert.match(components, /\.needs-attention-panel \{ grid-area: attention; \}/);
});

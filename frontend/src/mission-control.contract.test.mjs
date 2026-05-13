import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const briefing = readFileSync(new URL('./screens/briefing/BriefingPage.tsx', import.meta.url), 'utf8');
const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');
const components = readFileSync(new URL('./styles/components.css', import.meta.url), 'utf8');

test('Mission Control screen is wired to the briefing route and fixture clients', () => {
  assert.ok(existsSync(new URL('./screens/briefing/BriefingPage.tsx', import.meta.url)));
  assert.match(main, /new FixtureApiClient/);
  assert.match(main, /new FixtureRealtimeClient/);
  assert.match(main, /<BriefingPage apiClient=\{apiClient\} realtimeClient=\{realtimeClient\}/);
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

test('Mission Control subscribes to realtime stale and duplicate/replay fixture behavior', () => {
  assert.match(briefing, /realtimeClient\.connect\(\['decisions', 'goals'\]\)/);
  assert.match(briefing, /Stale · reconnecting/);
  assert.match(briefing, /mergeActivity/);
  assert.match(briefing, /unique realtime event/);
});

test('Mission Control responsive CSS keeps attention before secondary panels', () => {
  assert.match(components, /grid-template-areas:[\s\S]*"attention activity"/);
  assert.match(components, /@media \(max-width: 1120px\)[\s\S]*"attention"[\s\S]*"activity"[\s\S]*"teams"/);
  assert.match(components, /\.needs-attention-panel \{ grid-area: attention; \}/);
});

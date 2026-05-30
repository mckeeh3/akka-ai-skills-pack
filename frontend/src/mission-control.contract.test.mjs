import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const main = read('./main.tsx');
const components = read('./styles/components.css');
const workstreamShell = read('./workstream/shell/WorkstreamShell.tsx');
const panel = read('./workstream/shell/WorkstreamPanel.tsx');
const contextBar = read('./workstream/shell/ContextAuthorityBar.tsx');
const stream = read('./workstream/stream/WorkstreamStream.tsx');
const workstreamFixtures = read('./workstream/fixtures/workstream.ts');
const surfaceFixtures = read('./workstream/fixtures/surfaces.ts');
const workstreamEvents = read('./workstream/fixtures/events.ts');
const realtime = read('./workstream/realtime/workstreamEvents.ts');

test('Mission Control legacy screen is quarantined while the app entry uses the workstream shell', () => {
  assert.ok(existsSync(new URL('./screens/briefing/BriefingPage.tsx', import.meta.url)));
  assert.match(main, /<WorkstreamShell/);
  assert.doesNotMatch(main, /initialWorkstreamItems/);
  assert.doesNotMatch(main, /canonicalSurfaceEnvelopes/);
  assert.match(workstreamShell, /FunctionalAgentRail/);
  assert.match(workstreamShell, /WorkstreamComposer/);
  assert.match(panel, /Workstream interaction flow|workstream-flow/);
  assert.doesNotMatch(main, /new FixtureApiClient/);
  assert.doesNotMatch(main, /<BriefingPage|route === 'briefing'/);
});

test('workstream shell provides mission-control responsibilities through shell regions', () => {
  assert.match(workstreamShell, /Skip to main workstream/);
  assert.match(workstreamShell, /data-selected-functional-agent/);
  assert.match(contextBar, /Selected AuthContext/);
  assert.match(contextBar, /Roles:/);
  assert.match(contextBar, /Browser-safe capabilities/);
  assert.match(stream, /aria-label="Workstream interaction flow"|aria-label="Workstream items"/);
  assert.match(stream, /Empty workstream/);
  assert.doesNotMatch(main, /Reference fixture status/);
  assert.match(main, /withRuntimeNotification/);
  assert.match(main, /realtimeStatusLabel/);
});

test('workstream fixtures cover the five core v0 shell while richer surfaces remain explicit demos', () => {
  assert.doesNotMatch(workstreamFixtures, /kind: 'system-status'/);
  assert.match(workstreamFixtures, /kind: 'markdown_response'/);
  for (const agentId of ['agent-my-account', 'agent-user-admin', 'agent-agent-admin', 'agent-audit-trace', 'agent-governance-policy']) {
    assert.match(workstreamFixtures, new RegExp(agentId));
  }
  assert.match(workstreamFixtures, /traceLinks/);
  assert.match(surfaceFixtures, /fullCoreDemoSurfaceEnvelopes/);
  assert.match(surfaceFixtures, /userAdminDashboardSurface/);
});

test('stale, duplicate, reconnecting, and cross-context realtime behavior is encoded in workstream contracts', () => {
  assert.match(workstreamEvents, /duplicateReplayEvent/);
  assert.match(workstreamEvents, /outOfOrderEvent/);
  assert.match(workstreamEvents, /crossContextDeniedEvent/);
  assert.match(workstreamEvents, /surface\.stale/);
  assert.match(realtime, /Ignored cross-context event/);
  assert.match(realtime, /Ignored out-of-order event/);
  assert.match(realtime, /reconnecting/);
  assert.match(realtime, /Stale:/);
});

test('workstream shell responsive CSS keeps primary workstream regions usable on narrow screens', () => {
  assert.match(stream, /workstream-stream/);
  assert.match(panel, /workstream-item/);
  assert.match(read('./workstream/surfaces/SurfaceStateFrame.tsx'), /surface-frame/);
  assert.match(read('./workstream/surfaces/SurfaceActionBar.tsx'), /surface-action-bar/);
  assert.match(components, /@media \(max-width: 640px\)/);
});

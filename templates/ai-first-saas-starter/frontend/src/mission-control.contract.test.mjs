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
const workstreamEvents = read('./workstream/fixtures/events.ts');
const realtime = read('./workstream/realtime/workstreamEvents.ts');

test('Mission Control legacy screen is quarantined while the app entry uses the workstream shell', () => {
  assert.ok(existsSync(new URL('./screens/briefing/BriefingPage.tsx', import.meta.url)));
  assert.match(main, /<WorkstreamShell/);
  assert.match(main, /HttpWorkstreamApiClient/);
  assert.match(main, /useFixtureWorkstream \? new FixtureWorkstreamApiClient/);
  assert.match(workstreamShell, /FunctionalAgentRail/);
  assert.match(workstreamShell, /WorkstreamComposer/);
  assert.match(panel, /Continuous workstream/);
  assert.doesNotMatch(main, /new FixtureApiClient/);
  assert.doesNotMatch(main, /<BriefingPage|route === 'briefing'/);
});

test('workstream shell provides mission-control responsibilities through shell regions', () => {
  assert.match(workstreamShell, /Skip to main workstream/);
  assert.match(workstreamShell, /data-selected-functional-agent/);
  assert.match(contextBar, /Selected AuthContext/);
  assert.match(contextBar, /Roles:/);
  assert.match(contextBar, /Browser-safe capabilities/);
  assert.match(stream, /aria-label="Workstream items"/);
  assert.match(stream, /Empty workstream/);
  assert.match(main, /aria-label="Workstream API status"/);
  assert.match(main, /Realtime status:/);
});

test('workstream fixtures cover attention, decisions, workflow progress, audit trace, and action feedback', () => {
  for (const kind of ['system-status', 'decision', 'workflow-status', 'audit-trace', 'action-feedback']) {
    assert.match(workstreamFixtures, new RegExp(`kind: '${kind}'`));
  }
  assert.match(workstreamFixtures, /traceLinks/);
  assert.match(workstreamFixtures, /waiting-for-human/);
  assert.match(workstreamFixtures, /surface-user-admin-dashboard/);
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

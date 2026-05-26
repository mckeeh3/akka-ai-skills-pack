import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const visualSessionState = read('./workstream/visual-session/visualSessionState.ts');
const visualSessionIndex = read('./workstream/visual-session/index.ts');
const workstreamIndex = read('./workstream/index.ts');

test('visual-session helpers expose reusable turn-group and session contracts', () => {
  assert.match(visualSessionState, /export type WorkstreamTurnGroup/);
  assert.match(visualSessionState, /requestItem: WorkstreamItem/);
  assert.match(visualSessionState, /responseItems: WorkstreamItem\[\]/);
  assert.match(visualSessionState, /export type WorkstreamVisualSessionSnapshot/);
  assert.match(visualSessionState, /activeTurnGroupId\?: string/);
  assert.match(visualSessionState, /anchorSurfaceId\?: string/);
  assert.match(visualSessionState, /selectedSurfaceId\?: string/);
  assert.match(visualSessionState, /collapsedSurfaceIds: string\[\]/);
  assert.match(visualSessionIndex, /export \* from '\.\/visualSessionState'/);
  assert.match(workstreamIndex, /export \* from '\.\/visual-session'/);
});

test('turn-group helpers preserve traditional chat ordering and append response surfaces below requests', () => {
  assert.match(visualSessionState, /appendNewTurnGroup/);
  assert.match(visualSessionState, /turnGroups: \[\.\.\.withoutExisting, \{ turnGroupId/);
  assert.match(visualSessionState, /requestItem, responseItems: \[\]/);
  assert.match(visualSessionState, /appendResponseToTurnGroup/);
  assert.match(visualSessionState, /responseItems = existingIndex === -1\s*\? \[\.\.\.group\.responseItems, responseItem\]/);
  assert.match(visualSessionState, /sortWorkstreamItemsByCreatedAt\(responseItems\)/);
  assert.match(visualSessionState, /sortWorkstreamItemsByCreatedAt\(input\.items\)/);
});

test('visual-session limits are turn-group first with a secondary rendered-surface cap', () => {
  assert.match(visualSessionState, /maxTurnGroups: 20/);
  assert.match(visualSessionState, /maxRenderedSurfaces: 200/);
  assert.match(visualSessionState, /turnGroups = session\.turnGroups\.slice\(-maxTurnGroups\)/);
  assert.match(visualSessionState, /countRenderedSurfaces\(turnGroups\) > maxRenderedSurfaces/);
  assert.match(visualSessionState, /turnGroups = turnGroups\.slice\(1\)/);
  assert.match(visualSessionState, /total \+ 1 \+ group\.responseItems\.length/);
});

test('snapshot semantics stay in-memory and semantic rather than browser-local or backend persisted', () => {
  assert.match(visualSessionState, /toVisualSessionSnapshot/);
  assert.match(visualSessionState, /loadedTurnGroupIds: session\.loadedTurnGroupIds/);
  assert.match(visualSessionState, /userHasManualScroll: session\.userHasManualScroll/);
  assert.match(visualSessionState, /lastViewedAt: session\.lastViewedAt/);
  assert.doesNotMatch(visualSessionState, /localStorage|sessionStorage|indexedDB|fetch\(|navigator\.sendBeacon/i);
});

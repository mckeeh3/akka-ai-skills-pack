import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const visualSessionState = read('./workstream/visual-session/visualSessionState.ts');
const visualSessionIndex = read('./workstream/visual-session/index.ts');
const workstreamIndex = read('./workstream/index.ts');
const workstreamStream = read('./workstream/stream/WorkstreamStream.tsx');
const main = read('./main.tsx');

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

test('visual sessions are keyed by account, selected auth context, functional agent, and workstream id', () => {
  assert.match(visualSessionState, /export type WorkstreamVisualSessionStore = Record<string, WorkstreamVisualSession>/);
  assert.match(visualSessionState, /createWorkstreamVisualSessionKey/);
  assert.match(visualSessionState, /input\.accountId \?\? 'anonymous-account'/);
  assert.match(visualSessionState, /input\.selectedContextId/);
  assert.match(visualSessionState, /input\.functionalAgentId/);
  assert.match(visualSessionState, /input\.workstreamId/);
  assert.match(visualSessionState, /restoreOrCreateVisualSession/);
  assert.match(visualSessionState, /saveVisualSession/);
  assert.match(visualSessionState, /updateVisualSessionViewState/);
});

test('workstream shell restores per-workstream in-memory visual state on agent switch without browser or backend persistence', () => {
  assert.match(main, /useState<WorkstreamVisualSessionStore>\(\{\}\)/);
  assert.match(main, /requestScrollTargetBySessionKey/);
  assert.match(main, /createWorkstreamVisualSessionKey\(\{\s*accountId: me\.account\.accountId,\s*selectedContextId: me\.selectedAuthContext\.selectedContextId,/s);
  assert.match(main, /restoreOrCreateVisualSession\(\{\s*store: visualSessionsByKey,/s);
  assert.match(main, /const restoredSession = sessionForAgent\(functionalAgentId\)/);
  assert.match(main, /const restoredSurface = restoredSession\.selectedSurfaceId \?\? surfaceForAgent/);
  assert.match(main, /saveVisualSession\(store, restoredSession\)/);
  assert.match(main, /selectedSurfaceId: restoredSurface/);
  assert.match(main, /const currentRequestScrollTargetId = selectedSessionKey \? requestScrollTargetBySessionKey\[selectedSessionKey\] : undefined/);
  assert.match(main, /requestScrollTargetId=\{currentRequestScrollTargetId\}/);
  assert.match(main, /autoAnchorPaused=\{currentVisualSession\?\.userHasManualScroll\}/);
  assert.match(main, /onAutoAnchorPaused=\{\(\) => \{\s*if \(currentVisualSession\) rememberVisualSession\(currentVisualSession, \{ userHasManualScroll: true \}\);\s*\}\}/s);
  assert.doesNotMatch(main, /visualSessionsByKey[\s\S]{0,200}(localStorage|sessionStorage|indexedDB|fetch\(|sendBeacon)/i);
});

test('workstream stream anchors new request surfaces at the top while responses append below', () => {
  assert.match(workstreamStream, /requestScrollTargetId\?: string/);
  assert.match(workstreamStream, /scrollIntoView\(\{ block: 'start', inline: 'nearest', behavior \}\)/);
  assert.match(workstreamStream, /window\.matchMedia\('\(prefers-reduced-motion: reduce\)'\)\.matches \? 'auto' : 'smooth'/);
  assert.match(workstreamStream, /items\.map\(\(item\) =>/);
  assert.match(workstreamStream, /<WorkstreamItemCard item=\{item\}/);
  assert.match(workstreamStream, /<SurfaceRenderer envelopes=\{surfaces\} selectedSurfaceId=\{item\.surfaceId\}/);
  assert.match(workstreamStream, /\[requestScrollTargetId, shouldAutoAnchor, items\.length, surfaces\.length\]/);
});

test('workstream stream pauses automatic request anchoring after manual scroll input', () => {
  assert.match(workstreamStream, /autoAnchorPaused\?: boolean/);
  assert.match(workstreamStream, /onAutoAnchorPaused\?: \(requestScrollTargetId: string\) => void/);
  assert.match(workstreamStream, /pausedAnchorTargetId !== requestScrollTargetId/);
  assert.match(workstreamStream, /setPausedAnchorTargetId\(requestScrollTargetId\)/);
  assert.match(workstreamStream, /onAutoAnchorPaused\?\.\(requestScrollTargetId\)/);
  assert.match(workstreamStream, /onWheel=\{pauseAutoAnchorForManualScroll\}/);
  assert.match(workstreamStream, /onTouchMove=\{pauseAutoAnchorForManualScroll\}/);
  assert.match(workstreamStream, /isManualScrollKey\(event\.key\)/);
  assert.match(workstreamStream, /data-auto-anchor-paused=\{autoAnchorPaused \|\| pausedAnchorTargetId === requestScrollTargetId \? 'true' : 'false'\}/);
});

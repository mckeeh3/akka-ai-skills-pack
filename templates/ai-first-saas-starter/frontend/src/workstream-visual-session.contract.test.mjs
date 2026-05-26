import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const visualSessionState = read('./workstream/visual-session/visualSessionState.ts');
const visualSessionIndex = read('./workstream/visual-session/index.ts');
const workstreamIndex = read('./workstream/index.ts');
const workstreamStream = read('./workstream/stream/WorkstreamStream.tsx');
const workstreamPanel = read('./workstream/shell/WorkstreamPanel.tsx');
const layoutCss = read('./styles/layout.css');
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

test('background responses create in-memory rail unseen indicators that clear on selection', () => {
  assert.match(main, /useState<FunctionalAgentRailAttentionStore>\(\{\}\)/);
  assert.match(main, /markUnseenResponse\(functionalAgentId: string, lastItemId\?: string/);
  assert.match(main, /if \(isCurrentlySelectedFunctionalAgent\(functionalAgentId\)\) return/);
  assert.match(main, /kind: 'background-response'/);
  assert.match(main, /markUnseenBackgroundActivity\(event: WorkstreamEvent\)/);
  assert.match(main, /kind: event\.eventType === 'workstream\.item\.appended' \|\| event\.eventType === 'surface\.created' \? 'background-response' : 'background-activity'/);
  assert.match(main, /clearRailAttention\(functionalAgentId\)/);
  assert.match(main, /railAttentionByAgentId=\{railAttentionByAgentId\}/);
  assert.match(main, /markUnseenResponse\(responseFunctionalAgentId, traceableAgentItem\.itemId, 'info'\)/);
  assert.match(main, /markUnseenResponse\(request\.functionalAgentId, errorItem\.itemId, 'warning'\)/);
  assert.doesNotMatch(main, /railAttentionByAgentId[\s\S]{0,240}(localStorage|sessionStorage|indexedDB|fetch\(|sendBeacon)/i);
});

test('workstream stream anchors new request surfaces at the top of the actual scroll container while responses append below', () => {
  assert.match(workstreamStream, /requestScrollTargetId\?: string/);
  assert.match(workstreamPanel, /data-workstream-scroll-container="true"/);
  assert.match(layoutCss, /\.workstream-panel \{[\s\S]*height: 100vh;[\s\S]*overflow-y: auto;[\s\S]*scroll-padding-top: var\(--space-8\);/);
  assert.match(workstreamStream, /findRequestScrollTarget\(requestScrollTargetId, streamRef\.current\)/);
  assert.match(workstreamStream, /target\.closest<HTMLElement>\('\[data-workstream-scroll-container="true"\]'\)/);
  assert.match(workstreamStream, /scrollContainer\.scrollTo\(\{\s*top: targetRect\.top - containerRect\.top \+ scrollContainer\.scrollTop - scrollPaddingTop,\s*behavior\s*\}\)/s);
  assert.match(workstreamStream, /window\.matchMedia\('\(prefers-reduced-motion: reduce\)'\)\.matches \? 'auto' : 'smooth'/);
  assert.doesNotMatch(workstreamStream, /scrollIntoView\(/);
  assert.match(workstreamStream, /items\.map\(\(item\) =>/);
  assert.match(workstreamStream, /<WorkstreamItemCard item=\{item\}/);
  assert.match(workstreamStream, /<SurfaceRenderer envelopes=\{surfaces\} selectedSurfaceId=\{item\.surfaceId\}/);
  assert.match(workstreamStream, /\[requestScrollTargetId, shouldAutoAnchor, items\.length, surfaces\.length\]/);
});

test('composer submission keeps request items anchored while success and error responses append below', () => {
  assert.match(main, /const userRequestItem: WorkstreamItem = \{/);
  assert.match(main, /const correlationId = `corr-composer-/);
  assert.match(main, /submitWorkstreamMessage\(\{\s*\.\.\.request,\s*correlationId\s*\}\)/s);
  assert.match(main, /setRequestScrollTargetForCurrentSession\(userRequestItem\.itemId, request\.functionalAgentId\)/);
  assert.match(main, /rememberVisualSession\(sessionForAgent\(request\.functionalAgentId\), \{ activeTurnGroupId: correlationId, anchorSurfaceId: userRequestItem\.itemId, userHasManualScroll: false \}\)/);
  assert.match(main, /correlationId,\s*traceIds: \[\],\s*title: safeError\.title/s);
  assert.match(main, /setRequestScrollTargetForCurrentSession\(userRequestItem\.itemId, request\.functionalAgentId\);\s*rememberVisualSession\(sessionForAgent\(request\.functionalAgentId\), \{ anchorSurfaceId: userRequestItem\.itemId, userHasManualScroll: false \}\);/s);
  assert.match(main, /const responseFunctionalAgentId = surface\.ownerFunctionalAgentId \?\? request\.functionalAgentId/);
  assert.match(main, /setRequestScrollTargetForCurrentSession\(userItem\.itemId, responseFunctionalAgentId\)/);
  assert.match(main, /anchorSurfaceId: userItem\.itemId, selectedSurfaceId: surface\.surfaceId/);
  assert.doesNotMatch(main, /setRequestScrollTargetForCurrentSession\(surface\.surfaceId, surface\.ownerFunctionalAgentId \?\? request\.functionalAgentId\)/);
});

test('surface open and surface action requests keep request anchors while response surfaces append below', () => {
  assert.match(main, /itemId: `surface-request-\$\{now\}`/);
  assert.match(main, /correlationId: requestItem\.correlationId,\s*traceIds: surface\.traceIds/s);
  assert.match(main, /setRequestScrollTargetForCurrentSession\(requestItem\.itemId, functionalAgentId\)/);
  assert.match(main, /anchorSurfaceId: requestItem\.itemId, selectedSurfaceId: surface\.surfaceId/);
  assert.match(main, /itemId: `surface-action-request-\$\{now\}`/);
  assert.match(main, /correlationId: actionRequestItem\.correlationId,\s*traceIds: targetSurface\.traceIds/s);
  assert.match(main, /setRequestScrollTargetForCurrentSession\(actionRequestItem\.itemId, actionRequestItem\.functionalAgentId\)/);
  assert.match(main, /anchorSurfaceId: actionRequestItem\.itemId, userHasManualScroll: false/);
  assert.doesNotMatch(main, /setRequestScrollTargetForCurrentSession\(targetSurface\.surfaceId, targetSurface\.ownerFunctionalAgentId\)/);
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

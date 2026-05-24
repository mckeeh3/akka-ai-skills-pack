import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const main = read('./main.tsx');
const apiContract = read('./api/WorkstreamApiClient.ts');
const httpClient = read('./api/HttpWorkstreamApiClient.ts');
const fixtureClient = read('./api/FixtureWorkstreamApiClient.ts');
const stream = read('./workstream/stream/WorkstreamStream.tsx');
const composer = read('./workstream/composer/WorkstreamComposer.tsx');
const markdownSurface = read('./workstream/surfaces/MarkdownResponseSurface.tsx');
const itemCard = read('./workstream/stream/WorkstreamItem.tsx');

test('composer submits normal prompts through backend workstream message API', () => {
  assert.match(apiContract, /submitWorkstreamMessage\(request: WorkstreamMessageRequest\): Promise<ApiResult<WorkstreamMessageResponse>>/);
  assert.match(httpClient, /submitWorkstreamMessage\(request: WorkstreamMessageRequest\)/);
  assert.match(httpClient, /\/api\/workstream\/messages/);
  assert.match(httpClient, /if \(request\.selectedContextId\) this\.selectedContextId = request\.selectedContextId/);
  assert.match(main, /async function handleComposerSubmit/);
  assert.match(main, /workstreamClient\.submitWorkstreamMessage/);
  assert.doesNotMatch(main, /showUserDetail|showUsers|requestedSurface/);
});

test('composer response appends returned items and markdown_response surface', () => {
  assert.match(main, /const \{ userItem, agentItem, surface \} = result\.value/);
  assert.match(main, /traceableAgentItem/);
  assert.match(main, /items: pruneWorkstreamItems\(\[\.\.\.current\.items\.filter\(\(item\) => item\.itemId !== pendingItemId\), userItem, traceableAgentItem\]\)/);
  assert.match(main, /selectedSurfaceId: surface\.surfaceId/);
  assert.match(stream, /item\.kind === 'markdown_response'/);
  assert.match(apiContract, /surface: SurfaceEnvelope<unknown>/);
  assert.match(apiContract, /userItem: WorkstreamItem/);
  assert.match(apiContract, /agentItem: WorkstreamItem/);
});

test('composer exposes in-flight model submission state and preserves safe retry context', () => {
  assert.match(main, /submittingFunctionalAgentId/);
  assert.match(main, /Submitting to model-backed agent/);
  assert.match(main, /status: 'working'/);
  assert.match(main, /selected workstream context is preserved for retry/);
  assert.match(main, /return false/);
  assert.match(composer, /isSubmitting/);
  assert.match(composer, /Model-backed agent is responding/);
  assert.match(composer, /accepted !== false/);
});

test('composer maps provider-missing and forbidden errors to safe system notifications', () => {
  assert.match(main, /safeComposerErrorCopy/);
  assert.match(main, /provider configuration required/);
  assert.match(main, /Configure the backend provider variables/);
  assert.match(main, /secrets are not exposed in the browser/);
  assert.match(main, /Message not submitted · forbidden/);
  assert.match(main, /kind: 'system-notification'/);
  assert.match(main, /status: safeError\.status/);
});

test('successful prompt and model response surfaces render only prompt and response text', () => {
  assert.match(main, /traceableAgentItem/);
  assert.match(main, /traceLinks: agentItem\.traceLinks \?\? agentItem\.traceIds\.map/);
  assert.match(itemCard, /item\.kind === 'user-request'/);
  assert.match(itemCard, /prompt-input-surface/);
  assert.match(itemCard, /\{item\.body \?\? item\.title \?\? ''\}/);
  assert.match(itemCard, /return <ActionFeedbackItem/);
  assert.match(markdownSurface, /markdown-response-only/);
  assert.doesNotMatch(markdownSurface, /Trace links:|\/ui\?traceId=|surface-summary|SurfaceActionBar/);
});

test('fixture client returns backend-equivalent markdown for every initial core workstream', () => {
  for (const agentId of ['agent-my-account', 'agent-user-admin', 'agent-agent-admin', 'agent-audit-trace', 'agent-governance-policy']) {
    assert.match(fixtureClient, new RegExp(agentId));
  }
  assert.match(fixtureClient, /surfaceType: 'markdown_response'/);
  assert.match(fixtureClient, /kind: 'markdown_response'/);
  assert.match(fixtureClient, /producingAgentId: request\.functionalAgentId/);
  assert.match(fixtureClient, /selectedContextId: request\.selectedContextId/);
  assert.match(fixtureClient, /full-core behavior/);
});

import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const main = read('./main.tsx');
const apiContract = read('./api/WorkstreamApiClient.ts');
const httpClient = read('./api/HttpWorkstreamApiClient.ts');
const fixtureClient = read('./api/FixtureWorkstreamApiClient.ts');
const stream = read('./workstream/stream/WorkstreamStream.tsx');

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
  assert.match(main, /items: pruneWorkstreamItems\(\[\.\.\.current\.items, userItem, agentItem\]\)/);
  assert.match(main, /selectedSurfaceId: surface\.surfaceId/);
  assert.match(stream, /item\.kind === 'markdown_response'/);
  assert.match(apiContract, /surface: SurfaceEnvelope<unknown>/);
  assert.match(apiContract, /userItem: WorkstreamItem/);
  assert.match(apiContract, /agentItem: WorkstreamItem/);
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

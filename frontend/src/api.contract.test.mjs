import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const types = readFileSync(new URL('./api/types.ts', import.meta.url), 'utf8');
const apiClient = readFileSync(new URL('./api/ApiClient.ts', import.meta.url), 'utf8');
const fixtureApi = readFileSync(new URL('./__tests__/fixtures/api/FixtureApiClient.ts', import.meta.url), 'utf8');
const httpApi = readFileSync(new URL('./api/HttpApiClient.ts', import.meta.url), 'utf8');
const realtime = readFileSync(new URL('./api/RealtimeClient.ts', import.meta.url), 'utf8');
const fixtureRealtime = readFileSync(new URL('./__tests__/fixtures/api/FixtureRealtimeClient.ts', import.meta.url), 'utf8');
const workstreamApi = readFileSync(new URL('./api/WorkstreamApiClient.ts', import.meta.url), 'utf8');
const fixtureWorkstreamApi = readFileSync(new URL('./__tests__/fixtures/api/FixtureWorkstreamApiClient.ts', import.meta.url), 'utf8');
const workstreamRealtime = readFileSync(new URL('./api/WorkstreamRealtimeClient.ts', import.meta.url), 'utf8');
const fixtureWorkstreamRealtime = readFileSync(new URL('./__tests__/fixtures/api/FixtureWorkstreamRealtimeClient.ts', import.meta.url), 'utf8');
const httpWorkstreamApi = readFileSync(new URL('./api/HttpWorkstreamApiClient.ts', import.meta.url), 'utf8');
const httpWorkstreamRealtime = readFileSync(new URL('./api/HttpWorkstreamRealtimeClient.ts', import.meta.url), 'utf8');
const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');

test('DTOs include starter frontend API contract families', () => {
  assert.match(types, /export type MeResponse/);
  assert.match(types, /export type GoalDetailResponse/);
  assert.match(types, /export type DecisionDetailResponse/);
  assert.match(types, /export type PolicySummary/);
  assert.match(types, /export type TraceDetailResponse/);
  assert.match(types, /export type OrganizationListPayload/);
  assert.match(types, /export type OrganizationDetailPayload/);
  assert.match(types, /export type OrganizationActionResponse/);
  assert.match(types, /export type RealtimeEvent/);
});

test('API client interfaces expose planned client groups', () => {
  assert.match(apiClient, /export interface SessionClient/);
  assert.match(apiClient, /export interface AdminClient/);
  assert.match(apiClient, /export interface GoalsClient/);
  assert.match(apiClient, /export interface DecisionsClient/);
  assert.match(apiClient, /export interface GovernanceClient/);
  assert.match(apiClient, /export interface AuditClient/);
});

test('fixture API client simulates success, validation, forbidden/conflict style behavior seams', () => {
  assert.match(fixtureApi, /class FixtureApiClient implements ApiClient/);
  assert.match(fixtureApi, /validation_error/);
  assert.match(fixtureApi, /conflict/);
  assert.match(fixtureApi, /not_found/);
  assert.match(fixtureApi, /delayedOk/);
  assert.match(fixtureApi, /delayedError/);
});

test('HTTP API client targets same-origin documented API route families', () => {
  assert.match(httpApi, /\/api\/me/);
  assert.match(httpApi, /\/api\/admin\/users/);
  assert.match(httpApi, /\/api\/admin\/organizations/);
  assert.match(httpApi, /\/organizations\/\$\{encodeURIComponent\(organizationId\)\}\/rename/);
  assert.match(httpApi, /\/organizations\/\$\{encodeURIComponent\(organizationId\)\}\/suspend/);
  assert.match(httpApi, /\/organizations\/\$\{encodeURIComponent\(organizationId\)\}\/reactivate/);
  assert.match(httpApi, /\/api\/goals/);
  assert.match(httpApi, /\/api\/decisions/);
  assert.match(httpApi, /\/api\/governance\/policies/);
  assert.match(httpApi, /\/api\/audit\/traces/);
  assert.match(httpApi, /Authorization', `Bearer \$\{token\}`/);
  assert.match(httpApi, /X-Selected-Context-Id/);
  assert.match(httpApi, /X-Correlation-Id/);
});

test('HTTP API error mappers read response bodies only once', () => {
  assert.match(httpApi, /parseJsonOrText\(response\)/);
  assert.match(httpWorkstreamApi, /parseJsonOrText\(response\)/);
  assert.match(main, /parseInvitationErrorBody\(response\)/);
  assert.doesNotMatch(httpApi, /await response\.json\(\)[\s\S]{0,120}catch[\s\S]{0,120}await response\.text\(\)/);
  assert.doesNotMatch(httpWorkstreamApi, /await response\.json\(\)[\s\S]{0,120}catch[\s\S]{0,120}await response\.text\(\)/);
  assert.doesNotMatch(main, /await response\.json\(\)[\s\S]{0,120}catch[\s\S]{0,120}await response\.text\(\)/);
});

test('realtime client includes connection state, event handler, and idempotent merge helper', () => {
  assert.match(realtime, /RealtimeConnectionState/);
  assert.match(realtime, /RealtimeEventHandler/);
  assert.match(realtime, /connect\(topics: RealtimeTopic\[\]\)/);
  assert.match(realtime, /mergeRealtimeEvent/);
  assert.match(realtime, /incoming.version < existing.version/);
});

test('fixture realtime client simulates duplicate events and stale recovery', () => {
  assert.match(fixtureRealtime, /decision.created/);
  assert.match(fixtureRealtime, /this.setState\('stale'\)/);
  assert.match(fixtureRealtime, /this.setState\('connected'\)/);
  assert.match(fixtureRealtime, /decision-3/);
});

test('workstream fixture API client exposes bootstrap, surfaces, actions, and /api/me-shaped contracts', () => {
  assert.match(workstreamApi, /WorkstreamBootstrapResponse/);
  assert.match(workstreamApi, /bootstrap\(\)/);
  assert.match(workstreamApi, /getMe\(\)/);
  assert.match(workstreamApi, /listFunctionalAgents\(\)/);
  assert.match(workstreamApi, /listWorkstreamItems/);
  assert.match(workstreamApi, /getSurface/);
  assert.match(workstreamApi, /runCapabilityAction/);
  assert.match(fixtureWorkstreamApi, /meTenantAdmin/);
  assert.match(fixtureWorkstreamApi, /canonicalSurfaceEnvelopes/);
  assert.match(fixtureWorkstreamApi, /initialWorkstreamItems/);
  assert.match(fixtureWorkstreamApi, /actionResultsByStatus/);
  assert.match(httpWorkstreamApi, /\/api\/workstream\/bootstrap/);
  assert.match(httpWorkstreamApi, /\/api\/workstream\/surfaces\/\$\{encodeURIComponent\(surfaceId\)\}/);
  assert.match(httpWorkstreamApi, /\/api\/workstream\/actions/);
  assert.match(httpWorkstreamApi, /X-Selected-Context-Id/);
});

test('workstream fixture realtime client emits workstream events and visible stale state', () => {
  assert.match(workstreamRealtime, /WorkstreamRealtimeClient/);
  assert.match(workstreamRealtime, /selectedContextId/);
  assert.match(fixtureWorkstreamRealtime, /workstreamEvents/);
  assert.match(fixtureWorkstreamRealtime, /duplicateReplayEvent/);
  assert.match(fixtureWorkstreamRealtime, /outOfOrderEvent/);
  assert.match(fixtureWorkstreamRealtime, /status: 'stale'/);
  assert.match(httpWorkstreamRealtime, /fetch\(`\/api\/workstream\/events/);
  assert.match(httpWorkstreamRealtime, /Authorization', `Bearer \$\{token\}`/);
  assert.match(httpWorkstreamRealtime, /selectedContextId/);
  assert.match(httpWorkstreamRealtime, /lastEventId/);
  assert.match(httpWorkstreamRealtime, /Malformed realtime payload was ignored safely/);
  assert.match(httpWorkstreamRealtime, /surface\.stale/);
});

import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const types = readFileSync(new URL('./api/types.ts', import.meta.url), 'utf8');
const apiClient = readFileSync(new URL('./api/ApiClient.ts', import.meta.url), 'utf8');
const fixtureApi = readFileSync(new URL('./api/FixtureApiClient.ts', import.meta.url), 'utf8');
const httpApi = readFileSync(new URL('./api/HttpApiClient.ts', import.meta.url), 'utf8');
const realtime = readFileSync(new URL('./api/RealtimeClient.ts', import.meta.url), 'utf8');
const fixtureRealtime = readFileSync(new URL('./api/FixtureRealtimeClient.ts', import.meta.url), 'utf8');

test('DTOs include seed frontend API contract families', () => {
  assert.match(types, /export type MeResponse/);
  assert.match(types, /export type GoalDetailResponse/);
  assert.match(types, /export type DecisionDetailResponse/);
  assert.match(types, /export type PolicySummary/);
  assert.match(types, /export type TraceDetailResponse/);
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
  assert.match(httpApi, /\/api\/goals/);
  assert.match(httpApi, /\/api\/decisions/);
  assert.match(httpApi, /\/api\/governance\/policies/);
  assert.match(httpApi, /\/api\/audit\/traces/);
  assert.match(httpApi, /Authorization', `Bearer \$\{token\}`/);
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

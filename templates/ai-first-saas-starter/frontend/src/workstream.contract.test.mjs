import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const authTypes = read('./workstream/types/auth.ts');
const agentTypes = read('./workstream/types/agents.ts');
const actionTypes = read('./workstream/types/actions.ts');
const surfaceTypes = read('./workstream/types/surfaces.ts');
const workstreamTypes = read('./workstream/types/workstream.ts');
const eventTypes = read('./workstream/types/events.ts');
const meFixtures = read('./workstream/fixtures/me.ts');
const agentFixtures = read('./workstream/fixtures/agents.ts');
const surfaceFixtures = read('./workstream/fixtures/surfaces.ts');
const workstreamFixtures = read('./workstream/fixtures/workstream.ts');
const eventFixtures = read('./workstream/fixtures/events.ts');

const allFixtures = [meFixtures, agentFixtures, surfaceFixtures, workstreamFixtures, eventFixtures].join('\n');

test('workstream types define /api/me, AuthContext, functional agents, workstream items, surfaces, actions, and events', () => {
  assert.match(authTypes, /export type MeResponse/);
  assert.match(authTypes, /export type AuthContext/);
  assert.match(agentTypes, /export type FunctionalAgentSummary/);
  assert.match(workstreamTypes, /export type WorkstreamItem/);
  assert.match(surfaceTypes, /export type SurfaceEnvelope/);
  assert.match(actionTypes, /export type CapabilityActionRequest/);
  assert.match(actionTypes, /export type CapabilityActionResult/);
  assert.match(eventTypes, /export type WorkstreamEvent/);
  assert.match(eventTypes, /export type RealtimeConnectionState/);
});

test('me fixtures cover active admin, member, auditor/support, disabled, and forbidden states', () => {
  for (const name of ['meTenantAdmin', 'meRegularMember', 'meAuditorSupport', 'meDisabledUser', 'meForbiddenNoMembership']) {
    assert.match(meFixtures, new RegExp(`export const ${name}`));
  }
  assert.match(meFixtures, /supportAccess: \{ active: true/);
  assert.match(meFixtures, /'disabled'/);
  assert.match(meFixtures, /memberships: \[\]/);
  assert.match(meFixtures, /visibleCapabilityIds: \[\]/);
});

test('functional agent fixtures include foundation agents and visibility states', () => {
  for (const label of ['My Account', 'User Admin', 'Agent Admin', 'Audit/Trace', 'Governance/Policy', 'Billing', 'Support Access']) {
    assert.match(agentFixtures, new RegExp(label.replace('/', '\\/')));
  }
  for (const availability of ["availability: 'visible'", "availability: 'denied'", "availability: 'hidden'", "availability: 'disabled'"]) {
    assert.match(agentFixtures, new RegExp(availability));
  }
  assert.match(agentFixtures, /attention: \{ count: 2, severity: 'warning' \}/);
});

test('workstream fixtures include every canonical item kind including action feedback', () => {
  for (const kind of ['user-request', 'agent-response', 'surface', 'capability-result', 'workflow-status', 'decision', 'audit-trace', 'action-feedback', 'system-status']) {
    assert.match(workstreamFixtures, new RegExp(`kind: '${kind}'`));
  }
  assert.match(workstreamFixtures, /traceLinks/);
});

test('surface fixtures include canonical surface envelopes and all action intents', () => {
  for (const surfaceType of ['dashboard', 'list-search', 'detail-edit', 'decision', 'audit-timeline', 'workflow-status', 'governance-diff', 'outcome']) {
    assert.match(surfaceFixtures, new RegExp(`'${surfaceType}'`));
  }
  for (const intent of ['read', 'command', 'proposal', 'approval', 'workflow', 'governance', 'trace']) {
    assert.match(surfaceFixtures, new RegExp(`${intent}: \\{[\\s\\S]*intent: '${intent}'`));
  }
  assert.match(surfaceFixtures, /requiresConfirmation: true/);
  assert.match(surfaceFixtures, /requiresApproval: true/);
  assert.match(surfaceFixtures, /disabled: \{ reasonCode:/);
  assert.match(surfaceFixtures, /idempotency: \{ required: true/);
  assert.match(surfaceFixtures, /audit: \{ eventType:/);
  assert.match(surfaceFixtures, /resultSurface:/);
});

test('action result fixtures cover all governed result statuses', () => {
  for (const status of ['accepted', 'denied', 'validation-error', 'approval-required', 'conflict', 'no-op', 'failed']) {
    assert.match(surfaceFixtures, new RegExp(`'?${status}'?: \\{ status: '${status}'`));
  }
});

test('realtime fixtures cover normal, duplicate, stale, malformed-safe, and cross-context-denied events', () => {
  for (const eventType of ['surface.created', 'surface.updated', 'surface.action.accepted', 'surface.action.denied', 'surface.workflow.progressed', 'surface.stale', 'surface.reconnected']) {
    const escapedEventType = eventType.replace(/[.]/g, '\\.');
    assert.match(eventFixtures, new RegExp(`eventType: '${escapedEventType}'`));
  }
  assert.match(eventFixtures, /duplicateReplayEvent/);
  assert.match(eventFixtures, /outOfOrderEvent/);
  assert.match(eventFixtures, /malformedSafeEvent/);
  assert.match(eventFixtures, /crossContextDeniedEvent/);
  assert.match(eventFixtures, /tenantId: 'tenant-other'/);
});

test('fixture set avoids backend secrets and raw token fields', () => {
  assert.doesNotMatch(allFixtures, /client_secret|apiKey|rawToken|refreshToken|providerSecret/i);
});

import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const button = read('./workstream/actions/CapabilityActionButton.tsx');
const panel = read('./workstream/actions/CapabilityActionPanel.tsx');
const state = read('./workstream/actions/capabilityActionState.ts');
const actionsIndex = read('./workstream/actions/index.ts');
const workstreamIndex = read('./workstream/index.ts');

test('capability action components preserve disabled, denied, approval, confirmation, trace, and audit affordances', () => {
  assert.match(button, /function CapabilityActionButton/);
  assert.match(button, /data-capability-id=\{action\.capabilityId\}/);
  assert.match(button, /disabled=\{disabled\}/);
  assert.match(button, /denied-reason/);
  assert.match(button, /requiresConfirmation/);
  assert.match(button, /requiresApproval/);
  assert.match(button, /data-trace-required=\{action\.audit\.traceRequired\}/);
  assert.match(button, /auditTraceLabel\(action\)/);
  assert.match(button, /idempotencyLabel\(action\)/);
  assert.match(button, /frontend controls are advisory and backend capability checks remain authoritative/);
});

test('capability action helpers build governed requests with idempotency and selected auth context', () => {
  assert.match(state, /buildCapabilityActionRequest/);
  assert.match(state, /selectedContextId: options\.selectedContextId/);
  assert.match(state, /capabilityId: action\.capabilityId/);
  assert.match(state, /resolveIdempotencyKey/);
  assert.match(state, /defaultClientIdempotencyKey/);
  assert.match(state, /surface-item/);
  assert.match(state, /server-issued/);
});

test('capability action result mapping supports append, update, open, and feedback-only surface behavior', () => {
  assert.match(state, /mapCapabilityActionResult/);
  assert.match(state, /append-surface/);
  assert.match(state, /update-surface/);
  assert.match(state, /open-surface/);
  assert.match(state, /feedback-only/);
  assert.match(state, /capabilityActionResultToWorkstreamItem/);
  assert.match(state, /kind: 'action-feedback'/);
});

test('capability action panel exports reusable action controls from the workstream library', () => {
  assert.match(panel, /CapabilityActionPanel/);
  assert.match(panel, /CapabilityActionButton/);
  assert.match(panel, /onResultSurface/);
  assert.match(actionsIndex, /CapabilityActionButton/);
  assert.match(actionsIndex, /CapabilityActionPanel/);
  assert.match(actionsIndex, /capabilityActionState/);
  assert.match(workstreamIndex, /export \* from '\.\/actions'/);
});

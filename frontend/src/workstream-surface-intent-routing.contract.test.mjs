import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');
const readBackend = (path) => read(`../../${path}`);

const router = readBackend('src/main/java/ai/first/application/coreapp/workstream/DefaultSurfaceIntentRouter.java');
const backendTest = readBackend('src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java');
const composerContract = read('./workstream-composer-message-api.contract.test.mjs');

test('surface intent router has representative no-mutation routes for all five core workstreams', () => {
  for (const marker of [
    'route-my-account-dashboard-open-v1',
    'route-user-admin-organization-create-v1',
    'route-agent-admin-agent-list-open-v1',
    'route-audit-trace-dashboard-open-v1',
    'route-governance-policy-dashboard-open-v1'
  ]) {
    assert.match(router, new RegExp(marker));
    assert.match(backendTest, new RegExp(marker));
  }

  for (const agentId of [
    'my-account-agent',
    'user-admin-agent',
    'agent-admin-agent',
    'audit-trace-agent',
    'governance-policy-agent'
  ]) {
    assert.match(router, new RegExp(agentId));
    assert.match(backendTest, new RegExp(agentId));
  }

  assert.match(backendTest, /Representative deterministic routes for all five core workstreams must not invoke the model-backed runtime/);
  assert.match(backendTest, /Deterministic routing must not submit create commands/);
});

test('surface intent router keeps destructive and approval-gated asks on safe fallback', () => {
  for (const highRiskMarker of ['activate ', 'approve ', 'rollback ', 'export ', 'grant ', 'revoke ', 'suspend ']) {
    assert.match(router, new RegExp(highRiskMarker));
  }
  assert.match(backendTest, /submitMessageBlocksUnsupportedAndHighRiskChatToolPromptsAfterDeterministicRouting/);
  assert.match(backendTest, /activate agent/);
  assert.match(backendTest, /approve proposal/);
  assert.match(backendTest, /CHAT_TOOL_PROMPT_APPROVAL_GATED/);
  assert.match(backendTest, /assertNull\(highRiskAgentLifecycle\.surface\(\)\.data\(\)\.get\("surfaceIntentRoute"\)\)/);
  assert.match(backendTest, /assertNull\(approvalGatedGovernance\.surface\(\)\.data\(\)\.get\("surfaceIntentRoute"\)\)/);
  assert.match(backendTest, /Unsupported\/high-risk execution prompts must fail closed before model fallback or planning\./);
  assert.match(composerContract, /composer accepts backend routed surface responses without client-side mutation or model branching/);
});

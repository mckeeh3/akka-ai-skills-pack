import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');
const readTemplateOrRenderedBackend = (path) => {
  for (const root of ['../../backend', '../..']) {
    try {
      return read(`${root}/${path}`);
    } catch (error) {
      if (error.code !== 'ENOENT') throw error;
    }
  }
  return read(`../../backend/${path}`);
};

const backendWorkstreamService = readTemplateOrRenderedBackend('src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java');
const backendMyAccountService = readTemplateOrRenderedBackend('src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MyAccountService.java');
const backendWorkstreamEndpoint = readTemplateOrRenderedBackend('src/main/java/{{JAVA_PACKAGE_PATH}}/api/workstream/WorkstreamEndpoint.java');
const backendWorkstreamTest = readTemplateOrRenderedBackend('src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java');
const surfaceRenderer = read('./workstream/surfaces/SurfaceRenderer.tsx');
const dashboardSurface = read('./workstream/surfaces/DashboardSurface.tsx');
const detailEditSurface = read('./workstream/surfaces/DetailEditSurface.tsx');
const systemMessageSurface = read('./workstream/surfaces/SystemMessageSurface.tsx');
const actionState = read('./workstream/actions/capabilityActionState.ts');
const shell = read('./workstream/shell/WorkstreamShell.tsx');
const rail = read('./workstream/rail/FunctionalAgentRail.tsx');

const expectedMyAccountMarkers = [
  'surface-my-account-dashboard',
  'surface-my-profile',
  'surface-my-settings',
  'surface-my-context',
  'surface-my-account-open-denied',
  'action-show-my-account-dashboard',
  'action-show-my-profile',
  'action-show-my-settings',
  'action-show-my-context',
  'action-update-my-profile',
  'action-update-my-settings',
  'action-open-user-admin',
  'action-open-agent-admin',
  'action-open-audit-trace',
  'action-open-governance-policy'
];

test('My Account full-core backend exposes dashboard, profile, settings, context, attention, and safe navigation surfaces', () => {
  for (const marker of expectedMyAccountMarkers) assert.match(backendWorkstreamService, new RegExp(marker.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')));
  for (const capabilityId of ['my_account.view_summary', 'my_account.view_context', 'my_account.list_personal_attention', 'my_account.update_profile_settings', 'my_account.open_authorized_workstream', 'my_account.view_own_trace_refs']) {
    assert.match(backendMyAccountService, new RegExp(capabilityId.replace('.', '\\.')));
    assert.match(backendWorkstreamService, new RegExp(capabilityId.replace('.', '\\.')));
  }
  assert.match(backendMyAccountService, /personalAttention\(actor, correlationId\)/);
  assert.match(backendWorkstreamService, /not_found_or_redacted/);
  assert.match(backendWorkstreamService, /blocked_provider_or_runtime/);
  assert.match(backendWorkstreamService, /\/api\/me\?selectedContextId=/);
  assert.match(backendWorkstreamEndpoint, /X-Selected-Context-Id/);
});

test('My Account backend tests cover rich reads, update, idempotent duplicate, no-op, unsupported fields, and hidden workstream denial', () => {
  for (const testName of [
    'myAccountSurfacesAreBackendRetrievedWithAuthorityTraceAndContextData',
    'myAccountProfileSettingsUpdatePersistsAllowedSelfServiceFieldsAndIsIdempotent',
    'myAccountRejectsUnsupportedSelfServiceFieldsBeforeMutation',
    'myAccountProfileSettingsNoOpIsTracedAndReturnsCurrentSurface',
    'myAccountOpenWorkstreamActionReturnsBackendResolvedSurface',
    'myAccountOpenWorkstreamDeniesHiddenTargetsWithSystemMessage'
  ]) assert.match(backendWorkstreamTest, new RegExp(testName));
  assert.match(backendWorkstreamTest, /surface-my-context/);
  assert.match(backendWorkstreamTest, /idem-my-account-update/);
  assert.match(backendWorkstreamTest, /MY_ACCOUNT_UNSUPPORTED_SELF_SERVICE_FIELD/);
  assert.match(backendWorkstreamTest, /no-op/);
  assert.match(backendWorkstreamTest, /not_found_or_redacted/);
});

test('My Account frontend path renders typed dashboard, detail-edit, system_message, and governed actions generically', () => {
  assert.match(surfaceRenderer, /case 'dashboard':\n      return <DashboardSurface/);
  assert.match(surfaceRenderer, /case 'detail-edit':\n      return <DetailEditSurface/);
  assert.match(surfaceRenderer, /case 'system_message':\n      return <SystemMessageSurface/);
  assert.match(dashboardSurface, /attentionItems/);
  assert.match(dashboardSurface, /nextSteps/);
  assert.match(detailEditSurface, /surface-detail-edit-form/);
  assert.match(detailEditSurface, /permissionState/);
  assert.match(systemMessageSurface, /Recovery steps/);
  assert.match(actionState, /idempotencyKey/);
  assert.match(backendWorkstreamService, /validation-error/);
  assert.match(backendWorkstreamService, /no-op/);
  assert.match(actionState, /denied/);
});

test('My Account launches from signed-in user tile and uses backend shell requests rather than frontend-only authority', () => {
  assert.match(rail, /const myAccountFunctionalAgentId = 'agent-my-account'/);
  assert.match(rail, /Open My Account workstream/);
  assert.match(shell, /onSelectAgent/);
  assert.match(shell, /selectAgent\(functionalAgentId/);
  assert.doesNotMatch(shell, /my_account\.open_authorized_workstream\s*=\s*true/);
});

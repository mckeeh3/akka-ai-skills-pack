import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');
const readBackend = (path) => read(`../../${path}`);

const backendWorkstreamService = readBackend('src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java');
const backendMyAccountService = readBackend('src/main/java/ai/first/application/coreapp/myaccount/MyAccountService.java');
const backendWorkstreamEndpoint = readBackend('src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java');
const backendWorkstreamTest = readBackend('src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java');
const backendPersonalAttentionDigestService = readBackend('src/main/java/ai/first/application/coreapp/myaccount/MyAccountPersonalAttentionDigestService.java');
const backendPersonalAttentionDigestTest = readBackend('src/test/java/ai/first/application/coreapp/myaccount/MyAccountPersonalAttentionDigestServiceTest.java');
const surfaceRenderer = read('./workstream/surfaces/SurfaceRenderer.tsx');
const dashboardSurface = read('./workstream/surfaces/DashboardSurface.tsx');
const notificationCenterSurface = read('./workstream/surfaces/NotificationCenterSurface.tsx');
const detailEditSurface = read('./workstream/surfaces/DetailEditSurface.tsx');
const systemMessageSurface = read('./workstream/surfaces/SystemMessageSurface.tsx');
const actionState = read('./workstream/actions/capabilityActionState.ts');
const componentsCss = read('./styles/components.css');
const shell = read('./workstream/shell/WorkstreamShell.tsx');
const rail = read('./workstream/rail/FunctionalAgentRail.tsx');

const expectedMyAccountMarkers = [
  'surface-my-account-dashboard',
  'surface-my-profile',
  'surface-my-settings',
  'surface-my-context',
  'surface.access.profile.context.v1',
  'surface-my-account-open-denied',
  'surface-my-account-notification-center',
  'surface-my-account-personal-attention-digest-progress',
  'surface-my-account-personal-attention-digest-result',
  'surface-my-account-personal-attention-digest-blocked',
  'action-show-my-account-dashboard',
  'action-show-my-profile',
  'action-show-my-settings',
  'action-show-my-context',
  'action-select-my-context',
  'action-show-my-account-notification-center',
  'action-notification-mark-read',
  'action-notification-dismiss',
  'action-notification-archive',
  'action-notification-snooze',
  'action-notification-update-preferences',
  'action-update-my-profile',
  'action-update-my-settings',
  'action-open-user-admin',
  'action-open-agent-admin',
  'action-open-audit-trace',
  'action-open-governance-policy',
  'action-start-my-account-personal-attention-digest',
  'action-read-my-account-personal-attention-digest',
  'action-cancel-my-account-personal-attention-digest',
  'action-accept-my-account-personal-attention-digest',
  'action-reject-my-account-personal-attention-digest'
];

test('My Account full-core backend exposes dashboard, profile, settings, context, attention, and safe navigation surfaces', () => {
  for (const marker of expectedMyAccountMarkers) assert.match(backendWorkstreamService, new RegExp(marker.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')));
  for (const capabilityId of ['my_account.view_summary', 'my_account.view_context', 'my_account.list_personal_attention', 'my_account.update_profile_settings', 'my_account.open_authorized_workstream', 'my_account.view_own_trace_refs', 'core.access.me', 'core.profile.update', 'core.access.context.select']) {
    const matcher = new RegExp(capabilityId.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'));
    assert.match(backendMyAccountService, matcher);
    assert.match(backendWorkstreamService, matcher);
  }
  for (const capabilityId of ['my_account.personal_attention_digest.start', 'my_account.personal_attention_digest.read', 'my_account.personal_attention_digest.cancel', 'my_account.personal_attention_digest.accept_result', 'my_account.personal_attention_digest.reject_result']) {
    const matcher = new RegExp(capabilityId.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'));
    assert.match(backendPersonalAttentionDigestService, matcher);
  }
  for (const capabilityConstant of ['MY_ACCOUNT_DIGEST_START_CAPABILITY', 'MY_ACCOUNT_DIGEST_READ_CAPABILITY', 'MY_ACCOUNT_DIGEST_CANCEL_CAPABILITY', 'MY_ACCOUNT_DIGEST_ACCEPT_CAPABILITY', 'MY_ACCOUNT_DIGEST_REJECT_CAPABILITY']) {
    assert.match(backendWorkstreamService, new RegExp(capabilityConstant));
  }
  assert.match(backendMyAccountService, /personalAttention\(actor, correlationId\)/);
  assert.match(backendWorkstreamService, /not_found_or_redacted/);
  assert.match(backendWorkstreamService, /blocked_provider_or_runtime/);
  assert.match(backendWorkstreamService, /noDirectMutation/);
  assert.match(backendPersonalAttentionDigestService, /publishLifecycle/);
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
  assert.match(backendPersonalAttentionDigestTest, /lifecyclePublishesV3EventsAndDigestTaskAttentionWithoutMutatingSourceAttention/);
  assert.match(backendPersonalAttentionDigestTest, /workflow\.my_account\.personal_attention_digest\.started/);
  assert.match(backendPersonalAttentionDigestTest, /worker\.task\.completed_review_required/);
});

test('My Account frontend path renders typed dashboard, detail-edit, system_message, and governed actions generically', () => {
  assert.match(surfaceRenderer, /case 'dashboard':\n      return <DashboardSurface/);
  assert.match(surfaceRenderer, /case 'detail-edit':\n      return <DetailEditSurface/);
  assert.match(surfaceRenderer, /case 'system_message':\n      return <SystemMessageSurface/);
  assert.match(dashboardSurface, /attentionItems/);
  assert.doesNotMatch(dashboardSurface, /Next safe steps|Capabilities checked:|Authorized traversal/);
  assert.match(dashboardSurface, /Items that need my attention/);
  assert.doesNotMatch(dashboardSurface, /Use these counts to decide where to focus first/);
  assert.match(componentsCss, /\.attention-counter-card \{\n  display: grid;\n  place-items: center;[\s\S]*text-align: center;/);
  assert.match(dashboardSurface, /No personal attention items/);
  assert.match(dashboardSurface, /Manage your personal account surfaces without leaving this workstream/);
  assert.match(surfaceRenderer, /case 'notification-center':\n      return <NotificationCenterSurface/);
  assert.match(notificationCenterSurface, /notification\.list_my_account_center/);
  assert.match(notificationCenterSurface, /Personal in-app triage/);
  assert.match(notificationCenterSurface, /buildTriageLanes/);
  assert.match(notificationCenterSurface, /notification-triage-board/);
  assert.match(notificationCenterSurface, /actionInput=\{\{ notificationId: item\.notificationId \}\}/);
  assert.match(notificationCenterSurface, /Hidden categories are not enumerated/);
  assert.match(componentsCss, /\.notification-triage-metrics div \{\n  display: grid;\n  place-items: center;[\s\S]*text-align: center;/);
  assert.doesNotMatch(notificationCenterSurface, /notification\.email|Resend configuration|channelRegistry|deliveryAttempts|externalOutbox|pushEnabled|emailEnabled|RESEND_API_KEY|RESEND_FROM_EMAIL/);
  assert.match(detailEditSurface, /surface-detail-edit-form/);
  assert.match(detailEditSurface, /onSubmit=\{\(event\) => event\.preventDefault\(\)\}/);
  assert.match(detailEditSurface, /aria-live="polite"/);
  assert.match(detailEditSurface, /backend validation, idempotency, authorization, and audit remain authoritative/);
  assert.match(detailEditSurface, /fieldValues/);
  assert.match(detailEditSurface, /fields\.filter\(\(field\) => field\.editable\)/);
  assert.match(detailEditSurface, /actionInput=\{editableActionInput\}/);
  assert.match(detailEditSurface, /onChange=\{\(event\) => updateFieldValue/);
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

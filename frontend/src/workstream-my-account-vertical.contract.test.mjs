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
const myAccountSurfaces = read('./workstream/surfaces/MyAccountSurfaces.tsx');
const systemMessageSurface = read('./workstream/surfaces/SystemMessageSurface.tsx');
const workflowStatusSurface = read('./workstream/surfaces/WorkflowStatusSurface.tsx');
const outcomeSurface = read('./workstream/surfaces/OutcomeSurface.tsx');
const surfaceTypes = read('./workstream/types/surfaces.ts');
const main = read('./main.tsx');
const actionState = read('./workstream/actions/capabilityActionState.ts');
const componentsCss = read('./styles/components.css');
const shell = read('./workstream/shell/WorkstreamShell.tsx');
const rail = read('./workstream/rail/FunctionalAgentRail.tsx');

const backendWorkstreamTestBody = (testName) => {
  const match = backendWorkstreamTest.match(new RegExp(`void ${testName}\\(\\) \\{[\\s\\S]*?\\n  \\}`));
  assert.ok(match, `Expected backend WorkstreamServiceTest method ${testName}`);
  return match[0];
};

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
  assert.match(backendMyAccountService, /"my_account\.personal_command_center\.v1"/);
  assert.doesNotMatch(backendMyAccountService, /"my_account\.dashboard\.v1"/);
  assert.match(backendMyAccountService, /personalAttention\(actor, correlationId\)/);
  assert.match(backendWorkstreamService, /"surfaceContract", dashboard\.surfaceContract\(\)/);
  assert.match(backendWorkstreamService, /"panelLabel"/);
  assert.match(backendWorkstreamService, /"countOrStatus"/);
  assert.match(backendWorkstreamService, /"targetSurfaceId"/);
  assert.match(backendWorkstreamService, /"capabilityId"/);
  assert.match(backendWorkstreamService, /"denialHint"/);
  assert.match(backendWorkstreamService, /not_found_or_redacted/);
  assert.match(backendWorkstreamService, /blocked_provider_or_runtime/);
  assert.match(backendWorkstreamService, /noDirectMutation/);
  assert.match(backendPersonalAttentionDigestService, /publishLifecycle/);
  assert.match(backendWorkstreamService, /\/api\/me\?selectedContextId=/);
  assert.match(backendWorkstreamEndpoint, /X-Selected-Context-Id/);
});

test('My Account backend tests cover rich reads, update, idempotent duplicate, validation-result denials, no-op, and hidden workstream denial', () => {
  for (const testName of [
    'myAccountSurfacesAreBackendRetrievedWithAuthorityTraceAndContextData',
    'myAccountProfileSettingsUpdatePersistsAllowedSelfServiceFieldsAndIsIdempotent',
    'myAccountRejectsUnsupportedSelfServiceFieldsBeforeMutation',
    'myAccountSettingsRejectInvalidTimezoneBeforeMutation',
    'myAccountProfileSettingsNoOpIsTracedAndReturnsCurrentSurface',
    'myAccountOpenWorkstreamActionReturnsBackendResolvedSurface',
    'myAccountOpenUserAdminAcceptsSaasOwnerUserAdminCapability',
    'myAccountOpenWorkstreamDeniesHiddenTargetsWithSystemMessage'
  ]) assert.match(backendWorkstreamTest, new RegExp(testName));
  const unsupportedFieldValidation = backendWorkstreamTestBody('myAccountRejectsUnsupportedSelfServiceFieldsBeforeMutation');
  const invalidPreferenceValidation = backendWorkstreamTestBody('myAccountSettingsRejectInvalidTimezoneBeforeMutation');
  assert.match(backendWorkstreamTest, /surface-my-context/);
  assert.match(backendWorkstreamTest, /idem-my-account-update/);
  for (const validationBody of [unsupportedFieldValidation, invalidPreferenceValidation]) {
    assert.match(validationBody, /assertEquals\("validation-error", result\.status\(\)\)/);
    assert.match(validationBody, /assertEquals\(true, result\.resultSurface\(\)\.data\(\)\.get\("noDirectMutation"\)\)/);
    assert.match(validationBody, /No profile, setting, authority, source work, or provider state was mutated/);
    assert.doesNotMatch(validationBody, /assertThrows\(AuthorizationException/);
    assert.doesNotMatch(validationBody, /MY_ACCOUNT_UNSUPPORTED_SELF_SERVICE_FIELD/);
  }
  assert.match(unsupportedFieldValidation, /my_account_unsupported_self_service_field/);
  assert.match(invalidPreferenceValidation, /my_account_invalid_preference/);
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
  assert.match(dashboardSurface, /attentionCounters/);
  assert.match(dashboardSurface, /Backend attention evidence retained for trace review/);
  assert.match(dashboardSurface, /actionForAttentionItem/);
  assert.doesNotMatch(dashboardSurface, /const knownActions: Record<string, string>/);
  assert.doesNotMatch(dashboardSurface, /Next safe steps|Capabilities checked:|Authorized traversal/);
  assert.match(dashboardSurface, /Items that need my attention/);
  assert.match(dashboardSurface, /const counterLabel = counter\.workstreamLabel \?\? counter\.label/);
  assert.match(dashboardSurface, /const counterValue = counter\.attentionCount \?\? counter\.value/);
  assert.match(dashboardSurface, /Open \$\{counterLabel\}: \$\{status\}; \$\{counterValue\} attention items/);
  assert.match(dashboardSurface, /panel\.panelLabel \?\? panel\.label/);
  assert.match(dashboardSurface, /panel\.countOrStatus \?\? panel\.value/);
  assert.doesNotMatch(dashboardSurface, /Use these counts to decide where to focus first/);
  assert.match(componentsCss, /\.attention-counter-card \{\n  display: grid;\n  place-items: center;[\s\S]*text-align: center;/);
  assert.match(dashboardSurface, /No personal attention items/);
  assert.match(dashboardSurface, /Manage your personal account tools without leaving this workstream/);
  assert.match(surfaceRenderer, /case 'notification-center':\n      return <NotificationCenterSurface/);
  assert.match(notificationCenterSurface, /notification\.list_my_account_center/);
  assert.match(notificationCenterSurface, /Personal in-app triage/);
  assert.match(notificationCenterSurface, /buildTriageLanes/);
  assert.match(notificationCenterSurface, /notification-triage-board/);
  assert.match(notificationCenterSurface, /actionInput=\{\{ notificationId: item\.notificationId \}\}/);
  assert.match(notificationCenterSurface, /Hidden categories are not enumerated/);
  assert.match(notificationCenterSurface, /Lifecycle actions clear or defer notification visibility; they do not resolve source attention, tasks, workstream events, or audit records/);
  assert.match(notificationCenterSurface, /Source target requires reauthorization before opening/);
  assert.match(backendWorkstreamTest, /myAccountNotificationCenterSurfaceRendersBackendProjectionAndLifecycleActions/);
  assert.match(backendWorkstreamTest, /corr-notification-open-source-denied/);
  assert.match(backendWorkstreamTest, /corr-notification-snooze-invalid/);
  assert.match(backendWorkstreamTest, /corr-notification-pref-email/);
  assert.match(backendWorkstreamTest, /assertEquals\("surface-my-account-open-denied", deniedOpen\.resultSurface\(\)\.surfaceId\(\)\)/);
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
  assert.match(detailEditSurface, /Available choice/);
  assert.match(detailEditSurface, /Current organization/);
  assert.match(systemMessageSurface, /Recovery steps/);
  assert.match(systemMessageSurface, /Role-gated trace details/);
  assert.match(actionState, /idempotencyKey/);
  assert.match(backendWorkstreamService, /personalAttentionDigestService\.start/);
  assert.match(backendWorkstreamService, /digestProgressEvents/);
  assert.match(backendWorkstreamService, /digestEvidenceRefs/);
  assert.doesNotMatch(backendWorkstreamService, /action-notification-email-update-preferences"\.equals\(request\.actionId\(\)\)/);
  assert.match(backendWorkstreamService, /validation-error/);
  assert.match(backendWorkstreamService, /no-op/);
  assert.match(actionState, /denied/);
});

test('My Account frontend automated coverage proves dashboard, self-service, context, recovery, notification, and digest surface contracts', () => {
  assert.match(surfaceRenderer, /isMyAccountRebuiltSurface\(selectedEnvelope\)/);
  assert.match(myAccountSurfaces, /surface-my-profile/);
  assert.match(myAccountSurfaces, /surface-my-settings/);
  assert.match(myAccountSurfaces, /surface-my-context/);
  assert.match(myAccountSurfaces, /surface-my-account-personal-attention-digest-progress/);
  assert.match(myAccountSurfaces, /surface-my-account-personal-attention-digest-result/);
  assert.match(myAccountSurfaces, /surface-my-account-personal-attention-digest-blocked/);
  assert.match(myAccountSurfaces, /surface-my-account-open-denied/);
  assert.match(dashboardSurface, /Items that need my attention by workstream/);
  assert.match(dashboardSurface, /Backend attention evidence retained for trace review/);
  assert.ok(dashboardSurface.indexOf('attention-counter-strip') < dashboardSurface.indexOf('dashboard-evidence-drawer my-account-attention-evidence'));
  assert.match(surfaceTypes, /attentionCounters\?: Array<\{ counterId: string; label: string; workstreamLabel\?: string; value: string \| number; attentionCount\?: string \| number/);
  assert.match(surfaceTypes, /controlPanels\?: Array<\{ panelId: string; label: string; panelLabel\?: string; summary: string; state\?: string; countOrStatus\?: string \| number/);
  assert.match(myAccountSurfaces, /fields\.filter\(\(field: AnyData\) => field\.editable\)\.map/);
  assert.match(myAccountSurfaces, /name=\{field\.fieldId\}/);
  assert.match(myAccountSurfaces, /Field changes are local preview only until a governed save action succeeds/);
  assert.match(myAccountSurfaces, /onFieldValueChange\?\.\(fieldId, value, envelope\.surfaceId\)/);
  assert.doesNotMatch(myAccountSurfaces, /document\.documentElement\.dataset\.theme\s*=/);
  assert.match(main, /function handleSurfaceFieldValueChange\(fieldId: string, value: string\)/);
  assert.match(main, /normalizeThemeId\(value\)/);
  assert.match(main, /persistThemeId\(selectedThemeId\)/);
  assert.match(myAccountSurfaces, /Client context ids are advisory request inputs; backend decides visibility/);
  assert.match(myAccountSurfaces, /No enumeration/);
  assert.match(systemMessageSurface, /hidden workstreams, hidden contexts, source details, provider secrets/);
  assert.match(notificationCenterSurface, /Personal in-app triage/);
  assert.match(notificationCenterSurface, /Lifecycle actions clear or defer notification visibility; they do not resolve source attention/);
  assert.match(notificationCenterSurface, /Hidden categories are not enumerated/);
  assert.match(myAccountSurfaces, /Provider\/runtime blocker/);
  assert.match(myAccountSurfaces, /no fixture, deterministic, or model-less digest success is shown/);
  assert.match(myAccountSurfaces, /Advisory recommendations/);
  assert.match(myAccountSurfaces, /Digest evidence and authorized source links/);
  assert.match(myAccountSurfaces, /TraceRefs refs=\{envelope\.data\.traceRefs \?\? envelope\.traceIds\}/);
  assert.match(workflowStatusSurface, /Personal attention digest/);
  assert.match(outcomeSurface, /Personal attention digest advisory result/);
});

test('My Account frontend secret-boundary coverage omits raw browser secrets, hidden controls, external delivery controls, and arbitrary CSS', () => {
  const myAccountFrontendSources = [dashboardSurface, detailEditSurface, myAccountSurfaces, notificationCenterSurface, workflowStatusSurface, outcomeSurface, systemMessageSurface, surfaceTypes, main].join('\n');
  for (const forbidden of [
    /RESEND_API_KEY/,
    /RESEND_FROM_EMAIL/,
    /WORKOS_API_KEY/,
    /JWT_SECRET/,
    /providerCredential/,
    /providerSecret/,
    /rawProviderPayload/,
    /rawSessionToken/,
    /channelRegistry/,
    /deliveryAttempts/,
    /externalOutbox/,
    /modelConfig/,
    /modelProviderConfig/,
    /dangerouslySetInnerHTML/,
    /style=\{\{/,
    /localStorage\.setItem\('seed-ui-theme'/
  ]) assert.doesNotMatch(myAccountFrontendSources, forbidden);
  assert.doesNotMatch(notificationCenterSurface, /notification\.email|pushEnabled|emailEnabled|webhook|Slack|Teams/);
  assert.doesNotMatch(myAccountSurfaces, /role assignment|support-access grants|tenant-wide branding|raw prompt|raw tool payload/i);
  assert.match(systemMessageSurface, /Provider secrets, raw JWTs, hidden prompts, invitation tokens, and unauthorized tenant\/customer evidence are not shown/);
});

test('My Account representative chat tool plan path is catalog-bound and confirmation rendered by shared surface', () => {
  for (const marker of [
    'myAccountThemePlanSteps',
    'action-update-my-settings',
    'my_account.update_profile_settings',
    'schema.my-account.settings.update.v1',
    'preferredThemeId',
    'obsidian-dark',
    'surface-my-settings',
    'human_chat_tool_plan.step_completed'
  ]) assert.match(backendWorkstreamService, new RegExp(marker.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')));
  assert.match(backendWorkstreamTest, /change my theme to Obsidian Dark/);
  assert.match(backendWorkstreamTest, /representativeChatToolPlansCoverAllFiveFoundationWorkstreamsWithConfirmationAndTraceSemantics/);
  assert.match(backendWorkstreamTest, /expandedMyAccountChatToolPlanConfirmationDenialsReturnSystemMessagesWithoutMutation/);
  assert.match(backendWorkstreamTest, /CHAT_TOOL_BOUNDARY_TOOL_NOT_GRANTED/);
  assert.match(backendWorkstreamService, /persistChatToolPlanConfirmationDenial/);
  assert.match(backendWorkstreamService, /CHAT_TOOL_PLAN_SYSTEM_MESSAGE_CONTRACT/);
});

test('My Account launches from signed-in user tile and uses backend shell requests rather than frontend-only authority', () => {
  assert.match(rail, /const myAccountFunctionalAgentId = 'my-account-agent'/);
  assert.match(rail, /Open My Account workstream/);
  assert.match(shell, /onSelectAgent/);
  assert.match(shell, /selectAgent\(functionalAgentId/);
  assert.doesNotMatch(shell, /my_account\.open_authorized_workstream\s*=\s*true/);
});

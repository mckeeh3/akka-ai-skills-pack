import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const stream = read('./workstream/stream/WorkstreamStream.tsx');
const item = read('./workstream/stream/WorkstreamItem.tsx');
const feedback = read('./workstream/stream/ActionFeedbackItem.tsx');
const traceLinks = read('./workstream/stream/TraceLinkList.tsx');
const streamState = read('./workstream/stream/streamState.ts');
const renderer = read('./workstream/surfaces/SurfaceRenderer.tsx');
const actionBar = read('./workstream/surfaces/SurfaceActionBar.tsx');
const markdownResponse = read('./workstream/surfaces/MarkdownResponseSurface.tsx');
const systemMessage = read('./workstream/surfaces/SystemMessageSurface.tsx');
const dashboardSurface = read('./workstream/surfaces/DashboardSurface.tsx');
const listSearchSurface = read('./workstream/surfaces/ListSearchSurface.tsx');
const notificationCenterSurface = read('./workstream/surfaces/NotificationCenterSurface.tsx');
const detailEditSurface = read('./workstream/surfaces/DetailEditSurface.tsx');
const userAdminTaskSurface = read('./workstream/surfaces/UserAdminTaskSurface.tsx');
const surfaceStyles = read('./styles/components.css');
const packageJson = read('../package.json');
const surfaceTypes = read('./workstream/types/surfaces.ts');
const stateFrame = read('./workstream/surfaces/SurfaceStateFrame.tsx');
const surfaceIndex = read('./workstream/surfaces/index.ts');
const workstreamIndex = read('./workstream/index.ts');

const surfaceComponentFiles = [
  './workstream/surfaces/MarkdownResponseSurface.tsx',
  './workstream/surfaces/SystemMessageSurface.tsx',
  './workstream/surfaces/DashboardSurface.tsx',
  './workstream/surfaces/ListSearchSurface.tsx',
  './workstream/surfaces/DetailEditSurface.tsx',
  './workstream/surfaces/DecisionSurface.tsx',
  './workstream/surfaces/AuditTimelineSurface.tsx',
  './workstream/surfaces/WorkflowStatusSurface.tsx',
  './workstream/surfaces/GovernanceDiffSurface.tsx',
  './workstream/surfaces/OutcomeSurface.tsx',
  './workstream/surfaces/NotificationCenterSurface.tsx',
  './workstream/surfaces/UserAdminTaskSurface.tsx'
].map(read);

const allSurfaceComponents = surfaceComponentFiles.join('\n');

test('workstream stream components cover canonical item kinds and action feedback navigation', () => {
  assert.match(stream, /WorkstreamStream/);
  assert.match(stream, /renderedSurfaceItem/);
  assert.match(stream, /item\.kind === 'surface' \|\| item\.kind === 'markdown_response'/);
  assert.match(stream, /!renderedSurfaceItem && <WorkstreamItemCard/);
  assert.match(item, /item\.kind === 'action-feedback'/);
  assert.match(item, /item\.kind === 'workflow-status'/);
  assert.match(item, /item\.kind === 'system-status'/);
  assert.match(feedback, /Action feedback/);
  assert.match(feedback, /Open result surface/);
  assert.match(traceLinks, /aria-label="Trace links"/);
  assert.match(traceLinks, /surface-audit-trace-detail/);
  assert.match(streamState, /appendOrUpdateWorkstreamItem/);
  assert.match(streamState, /markSurfaceItemsStale/);
  assert.match(streamState, /mergeWorkstreamEvents/);
});

test('structured surface renderer routes every canonical surface type', () => {
  assert.match(renderer, /StructuredSurfaceRenderer/);
  for (const surfaceType of ['markdown_response', 'system_message', 'dashboard', 'list-search', 'detail-edit', 'decision', 'audit-timeline', 'workflow-status', 'governance-diff', 'outcome', 'notification-center']) {
    assert.match(renderer, new RegExp(`case '${surfaceType}'`));
  }
  assert.match(renderer, /MarkdownResponseSurface/);
  assert.match(renderer, /SystemMessageSurface/);
  assert.match(renderer, /UserAdminTaskSurface/);
  assert.match(renderer, /isUserAdminTaskSurface\(selectedEnvelope\)/);
  assert.match(renderer, /JSON\.stringify/);
});

test('markdown_response is typed, sanitized, text-only, and never routed to raw JSON fallback', () => {
  assert.match(surfaceTypes, /MarkdownResponseData/);
  assert.match(surfaceTypes, /surfaceType: CanonicalSurfaceType \| string/);
  assert.match(surfaceTypes, /'markdown_response'/);
  assert.match(surfaceTypes, /markdown: string/);
  assert.match(surfaceTypes, /safety\?:/);
  assert.match(markdownResponse, /function renderMarkdownToSanitizedElements/);
  assert.match(markdownResponse, /ReactMarkdown/);
  assert.match(markdownResponse, /remarkGfm/);
  assert.match(markdownResponse, /skipHtml/);
  assert.match(markdownResponse, /markdown-table-scroll/);
  assert.match(surfaceStyles, /\.markdown-response-only table/);
  assert.match(packageJson, /"react-markdown"/);
  assert.match(packageJson, /"remark-gfm"/);
  assert.match(markdownResponse, /sanitizedBlocks/);
  assert.doesNotMatch(markdownResponse, /dangerouslySetInnerHTML|\.innerHTML\s*=/);
  assert.match(markdownResponse, /unsafeSchemePattern/);
  assert.match(markdownResponse, /javascript\|data\|vbscript/);
  assert.match(markdownResponse, /rel="noopener noreferrer"/);
  assert.match(markdownResponse, /blocked-link/);
  assert.match(markdownResponse, /blocked-image/);
  assert.match(markdownResponse, /SurfaceStateFrame state=\{\{ status: 'empty'/);
  assert.match(markdownResponse, /SurfaceStateFrame state=\{\{ status: 'forbidden'/);
  assert.match(markdownResponse, /markdown-response-only/);
  assert.match(markdownResponse, /<div className="markdown-response-content">\{sanitizedBlocks\}<\/div>/);
  assert.doesNotMatch(markdownResponse, /Trace links:|surface-summary|data-correlation-id|SurfaceActionBar|SurfaceStateFrame envelope=\{envelope\}/);
  assert.match(renderer, /case 'markdown_response':\n      return <MarkdownResponseSurface/);
});

test('system_message surfaces render provider-blocked recovery, trace links, and no-secret UserAdminAgent boundaries', () => {
  assert.match(surfaceTypes, /SystemMessageData/);
  assert.match(surfaceTypes, /'system_message'/);
  assert.match(renderer, /case 'system_message':\n      return <SystemMessageSurface/);
  assert.match(systemMessage, /blocked_provider_or_runtime/);
  assert.match(systemMessage, /Recovery steps/);
  assert.match(systemMessage, /Trace links/);
  assert.match(systemMessage, /Provider secrets, raw JWTs, hidden prompts, invitation tokens, and unauthorized tenant\/customer evidence are not shown/);
  assert.match(systemMessage, /no direct mutation of invitations, memberships, roles, capabilities, authorization state, or provider configuration occurred/);
  assert.match(item, /item\.kind === 'system_message'/);
  assert.match(surfaceStyles, /\.workstream-item\.system_message/);
  assert.match(surfaceStyles, /\.system-message-surface/);
});

test('browser-safe redaction metadata renders as text instead of a React object child', () => {
  assert.match(surfaceTypes, /BrowserSafeRedactionMetadata/);
  assert.match(surfaceTypes, /browserSafe\?: boolean/);
  assert.match(surfaceTypes, /omittedFieldKeys\?: string\[\]/);
  assert.match(surfaceTypes, /previewLimitChars\?: number/);
  assert.match(dashboardSurface, /Redaction: \{renderSurfaceValue\(envelope\.data\.redaction\)\}/);
  assert.match(listSearchSurface, /Redaction: \{renderSurfaceValue\(envelope\.data\.redaction\)\}/);
  assert.doesNotMatch(dashboardSurface, /Redaction: \{envelope\.data\.redaction\}/);
  assert.doesNotMatch(listSearchSurface, /Redaction: \{envelope\.data\.redaction\}/);
});

test('base surface frame and action bar preserve envelope, stale, redaction, disabled, confirmation, approval, and trace affordances', () => {
  assert.match(stateFrame, /data-surface-id=\{visibleEnvelope\.surfaceId\}/);
  assert.match(stateFrame, /data-surface-version=\{visibleEnvelope\.surfaceVersion\}/);
  assert.match(stateFrame, /visibleEnvelope\.stale\?\.isStale/);
  assert.match(stateFrame, /Redaction profile/);
  assert.match(stateFrame, /Correlation:/);
  assert.match(actionBar, /disabled=\{Boolean\(action\.disabled\)\}/);
  assert.match(actionBar, /requiresConfirmation/);
  assert.match(actionBar, /requiresApproval/);
  assert.match(actionBar, /action\.disabled\.message/);
});

test('canonical surface components include dashboard, list/search, detail/edit, decision, audit, workflow, governance diff, and outcome patterns', () => {
  for (const componentName of ['MarkdownResponseSurface', 'SystemMessageSurface', 'DashboardSurface', 'ListSearchSurface', 'DetailEditSurface', 'DecisionSurface', 'AuditTimelineSurface', 'WorkflowStatusSurface', 'GovernanceDiffSurface', 'OutcomeSurface', 'NotificationCenterSurface', 'UserAdminTaskSurface']) {
    assert.match(surfaceIndex, new RegExp(componentName));
    assert.match(allSurfaceComponents, new RegExp(`function ${componentName}`));
  }
  assert.match(allSurfaceComponents, /role="search"/);
  assert.match(allSurfaceComponents, /className="surface-detail-edit-form"/);
  assert.match(allSurfaceComponents, /permissionState/);
  assert.match(allSurfaceComponents, /access-management-evidence/);
  assert.match(allSurfaceComponents, /Role\/capability preview/);
  assert.match(allSurfaceComponents, /Member status authority/);
  assert.match(allSurfaceComponents, /Detail audit trace affordances/);
  assert.match(allSurfaceComponents, /decision-card/);
  assert.match(allSurfaceComponents, /audit-timeline/);
  assert.match(allSurfaceComponents, /workflow-steps/);
  assert.match(allSurfaceComponents, /Access review task/);
  assert.match(allSurfaceComponents, /Access review evidence references/);
  assert.match(allSurfaceComponents, /Provider\/runtime blockers/);
  assert.match(allSurfaceComponents, /No direct mutation/);
  assert.match(allSurfaceComponents, /evidenceKey\(evidence\)/);
  assert.match(allSurfaceComponents, /recommendationKey\(recommendation\)/);
  assert.match(surfaceTypes, /user_admin\.access_review_task\.v1/);
  assert.match(surfaceTypes, /evidenceRefs\?: Array<string \|/);
  assert.match(surfaceTypes, /recommendations\?: Array<string \|/);
  assert.match(surfaceTypes, /UserAdminBranchNavigation/);
  assert.match(surfaceTypes, /branchReturnActionId\?: string/);
  assert.match(detailEditSurface, /UserAdminBranchReturn/);
  assert.match(detailEditSurface, /action-user-admin-show-users/);
  assert.match(detailEditSurface, /backend-authored-only/);
  assert.match(userAdminTaskSurface, /requiredUserTaskSurfaceIds/);
  assert.match(userAdminTaskSurface, /function InvitationCreateTask/);
  assert.match(userAdminTaskSurface, /function MembershipStatusTask/);
  assert.match(userAdminTaskSurface, /function SupportAccessGrantTask/);
  assert.match(userAdminTaskSurface, /function IdentityExceptionReview/);
  assert.match(userAdminTaskSurface, /action-user-admin-show-users/);
  assert.match(allSurfaceComponents, /user-admin-branch-return/);
  assert.match(surfaceStyles, /\.user-admin-branch-return/);
  assert.match(surfaceTypes, /NotificationCenterSurfaceData/);
  assert.match(notificationCenterSurface, /Notification triage lanes/);
  assert.match(notificationCenterSurface, /notification\.list_my_account_center/);
  assert.match(notificationCenterSurface, /Personal in-app triage/);
  assert.match(notificationCenterSurface, /notification-triage-board/);
  assert.match(notificationCenterSurface, /source attention, tasks, workstream events, or audit records/);
  assert.match(notificationCenterSurface, /Hidden categories are not enumerated/);
  assert.doesNotMatch(notificationCenterSurface, /notification\.email|captured_outbox|SMS, push, and webhook|emailEnabled|pushEnabled|RESEND_API_KEY|RESEND_FROM_EMAIL/);
  assert.match(allSurfaceComponents, /governance-diff-summary/);
  assert.match(allSurfaceComponents, /outcome-metrics/);
});

test('detail-edit structured surface form controls use enterprise styling hooks', () => {
  assert.match(detailEditSurface, /<form className="surface-detail-edit-form"/);
  assert.match(detailEditSurface, /<div key=\{field\.fieldId\} className="surface-detail-field">/);
  assert.match(detailEditSurface, /<label htmlFor=\{inputId\}>\{field\.label\}<\/label>/);
  assert.match(detailEditSurface, /name=\{field\.fieldId\}/);
  assert.match(surfaceStyles, /\.surface-detail-edit-form \{/);
  assert.match(surfaceStyles, /grid-template-columns: repeat\(auto-fit, minmax\(min\(18rem, 100%\), 1fr\)\)/);
  assert.match(surfaceStyles, /\.surface-detail-field input,\n\.surface-detail-field textarea,\n\.surface-detail-field select \{/);
  assert.match(surfaceStyles, /border: 1px solid color-mix\(in srgb, var\(--color-primary\) 16%, var\(--color-border\)\)/);
  assert.match(surfaceStyles, /background: linear-gradient\(180deg, var\(--color-surface-raised\)/);
  assert.match(surfaceStyles, /\.surface-detail-field select \{\n  appearance: none;/);
  assert.match(surfaceStyles, /\.surface-detail-field input:focus-visible,\n\.surface-detail-field textarea:focus-visible,\n\.surface-detail-field select:focus-visible \{/);
  assert.match(surfaceStyles, /\.surface-detail-field:has\(\[name="preferredThemeId"\]\)/);
});

test('workstream barrel exports stream and surface component library', () => {
  assert.match(workstreamIndex, /export \* from '\.\/stream'/);
  assert.match(workstreamIndex, /export \* from '\.\/surfaces'/);
});

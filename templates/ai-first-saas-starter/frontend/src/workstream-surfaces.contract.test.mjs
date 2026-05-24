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
const surfaceTypes = read('./workstream/types/surfaces.ts');
const stateFrame = read('./workstream/surfaces/SurfaceStateFrame.tsx');
const surfaceIndex = read('./workstream/surfaces/index.ts');
const workstreamIndex = read('./workstream/index.ts');

const surfaceComponentFiles = [
  './workstream/surfaces/MarkdownResponseSurface.tsx',
  './workstream/surfaces/DashboardSurface.tsx',
  './workstream/surfaces/ListSearchSurface.tsx',
  './workstream/surfaces/DetailEditSurface.tsx',
  './workstream/surfaces/DecisionSurface.tsx',
  './workstream/surfaces/AuditTimelineSurface.tsx',
  './workstream/surfaces/WorkflowStatusSurface.tsx',
  './workstream/surfaces/GovernanceDiffSurface.tsx',
  './workstream/surfaces/OutcomeSurface.tsx'
].map(read);

const allSurfaceComponents = surfaceComponentFiles.join('\n');

test('workstream stream components cover canonical item kinds and action feedback navigation', () => {
  assert.match(stream, /WorkstreamStream/);
  assert.match(item, /item\.kind === 'action-feedback'/);
  assert.match(item, /item\.kind === 'workflow-status'/);
  assert.match(item, /item\.kind === 'system-status'/);
  assert.match(feedback, /Action feedback/);
  assert.match(feedback, /Open result surface/);
  assert.match(traceLinks, /aria-label="Trace links"/);
  assert.match(streamState, /appendOrUpdateWorkstreamItem/);
  assert.match(streamState, /markSurfaceItemsStale/);
  assert.match(streamState, /mergeWorkstreamEvents/);
});

test('structured surface renderer routes every canonical surface type', () => {
  assert.match(renderer, /StructuredSurfaceRenderer/);
  for (const surfaceType of ['markdown_response', 'dashboard', 'list-search', 'detail-edit', 'decision', 'audit-timeline', 'workflow-status', 'governance-diff', 'outcome']) {
    assert.match(renderer, new RegExp(`case '${surfaceType}'`));
  }
  assert.match(renderer, /MarkdownResponseSurface/);
  assert.match(renderer, /JSON\.stringify/);
});

test('markdown_response is typed, sanitized, text-only, and never routed to raw JSON fallback', () => {
  assert.match(surfaceTypes, /MarkdownResponseData/);
  assert.match(surfaceTypes, /surfaceType: CanonicalSurfaceType \| string/);
  assert.match(surfaceTypes, /'markdown_response'/);
  assert.match(surfaceTypes, /markdown: string/);
  assert.match(surfaceTypes, /safety\?:/);
  assert.match(markdownResponse, /function renderMarkdownToSanitizedElements/);
  assert.match(markdownResponse, /sanitizedBlocks/);
  assert.doesNotMatch(markdownResponse, /dangerouslySetInnerHTML|\.innerHTML\s*=/);
  assert.match(markdownResponse, /unsafeSchemePattern/);
  assert.match(markdownResponse, /javascript\|data\|vbscript/);
  assert.match(markdownResponse, /rel="noopener noreferrer"/);
  assert.match(markdownResponse, /blocked-link/);
  assert.match(markdownResponse, /SurfaceStateFrame state=\{\{ status: 'empty'/);
  assert.match(markdownResponse, /SurfaceStateFrame state=\{\{ status: 'forbidden'/);
  assert.match(markdownResponse, /markdown-response-only/);
  assert.match(markdownResponse, /<div className="markdown-response-content">\{sanitizedBlocks\}<\/div>/);
  assert.doesNotMatch(markdownResponse, /Trace links:|surface-summary|data-correlation-id|SurfaceActionBar|SurfaceStateFrame envelope=\{envelope\}/);
  assert.match(renderer, /case 'markdown_response':\n      return <MarkdownResponseSurface/);
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
  for (const componentName of ['MarkdownResponseSurface', 'DashboardSurface', 'ListSearchSurface', 'DetailEditSurface', 'DecisionSurface', 'AuditTimelineSurface', 'WorkflowStatusSurface', 'GovernanceDiffSurface', 'OutcomeSurface']) {
    assert.match(surfaceIndex, new RegExp(componentName));
    assert.match(allSurfaceComponents, new RegExp(`function ${componentName}`));
  }
  assert.match(allSurfaceComponents, /role="search"/);
  assert.match(allSurfaceComponents, /className="surface-detail-edit-form"/);
  assert.match(allSurfaceComponents, /permissionState/);
  assert.match(allSurfaceComponents, /Detail audit trace affordances/);
  assert.match(allSurfaceComponents, /decision-card/);
  assert.match(allSurfaceComponents, /audit-timeline/);
  assert.match(allSurfaceComponents, /workflow-steps/);
  assert.match(allSurfaceComponents, /governance-diff-summary/);
  assert.match(allSurfaceComponents, /outcome-metrics/);
});

test('workstream barrel exports stream and surface component library', () => {
  assert.match(workstreamIndex, /export \* from '\.\/stream'/);
  assert.match(workstreamIndex, /export \* from '\.\/surfaces'/);
});

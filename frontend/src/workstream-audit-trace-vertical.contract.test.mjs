import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const fixtures = read('./__tests__/fixtures/workstream/surfaces.ts');
const types = read('./workstream/types/surfaces.ts');
const timeline = read('./workstream/surfaces/AuditTimelineSurface.tsx');
const detail = read('./workstream/surfaces/DetailEditSurface.tsx');
const decision = read('./workstream/surfaces/DecisionSurface.tsx');
const list = read('./workstream/surfaces/ListSearchSurface.tsx');
const dashboard = read('./workstream/surfaces/DashboardSurface.tsx');
const traceLinks = read('./workstream/stream/TraceLinkList.tsx');
const workstream = read('./__tests__/fixtures/workstream/workstream.ts');

test('Audit/Trace fixture exposes contract capabilities, surfaces, and backend-authoritative actions', () => {
  for (const capability of ['audit.trace.dashboard.read', 'audit.trace.search', 'audit.trace.detail.read', 'audit.trace.timeline.read', 'audit.trace.failureEvidence.read', 'audit.trace.investigationGuide.read', 'audit.trace.investigation_note.append', 'audit.trace.export.request', 'audit.trace.summary_task.start', 'audit.trace.summary_task.read', 'audit.trace.summary_task.accept_result', 'audit.trace.summary_task.reject_result', 'audit.trace.summary_task.open_evidence']) {
    assert.match(fixtures, new RegExp(capability.replace('.', '\\.')));
  }
  for (const surface of ['auditTraceDashboardSurface', 'auditTraceSearchSurface', 'auditTraceDetailSurface', 'auditTraceTimelineSurface', 'auditTraceFailureEvidenceSurface', 'auditTraceInvestigationGuideSurface', 'auditTraceExportRequestSurface', 'auditTraceInvestigationNoteSurface', 'auditTraceSummaryProgressSurface', 'auditTraceSummaryReviewSurface']) {
    assert.match(fixtures, new RegExp(`export const ${surface}`));
  }
  assert.match(fixtures, /auditTraceStructuredSurfaces/);
  assert.match(fixtures, /Backend capabilities|backend-scoped capabilities/);
});

test('Audit/Trace surfaces preserve trace links, denial/provider evidence, redaction, and safe rendering states', () => {
  assert.match(types, /surfaceContract\?: 'audit\.trace\.dashboard\.v1'/);
  assert.match(types, /attentionItems\?: Array/);
  assert.match(types, /nodes\?: Array/);
  assert.match(types, /redactedEvidence\?: string/);
  assert.match(types, /safeReason\?: string/);
  assert.match(types, /allowedActions\?: Array/);
  assert.match(timeline, /Audit correlation timeline/);
  assert.match(timeline, /partial timeline/i);
  assert.match(timeline, /surface-audit-trace-detail/);
  assert.match(detail, /audit-evidence-panel/);
  assert.match(detail, /Denial\/provider\/tool evidence/);
  assert.match(detail, /not_found_or_redacted/);
  assert.match(detail, /Related failure evidence/);
  assert.match(detail, /Redacted details/);
  assert.match(decision, /Backend-authorized investigation actions/);
  assert.match(decision, /Disabled or deferred investigation actions/);
  assert.match(list, /JSON\.stringify\(envelope\.data\.query\)/);
  assert.match(list, /Search scoped traces/);
  assert.match(list, /Return to dashboard/);
  assert.match(list, /Request redacted export/);
  assert.match(list, /Open detail/);
  assert.match(list, /Partial results/);
  assert.match(dashboard, /Audit\/Trace attention items/);
  assert.match(dashboard, /AuditTraceCommandCenter/);
  assert.match(dashboard, /dashboard-card clickable/);
  assert.match(dashboard, /defaultAuditTraceInput\(action, envelope\)/);
  assert.match(dashboard, /Request redacted export/);
  assert.match(dashboard, /Backend capabilities/);
  assert.match(traceLinks, /surface-audit-trace-detail/);
});

test('Audit/Trace bootstrap starts empty while trace surfaces do not claim frontend authorization', () => {
  assert.match(workstream, /initialWorkstreamItems: WorkstreamItem\[\] = \[\]/);
  assert.match(fixtures, /audit\.trace\.exportRequest\.v1/);
  assert.match(fixtures, /action-audit-trace-request-redacted-export/);
  assert.match(fixtures, /audit\.trace\.investigationNote\.v1/);
  assert.match(fixtures, /action-audit-trace-append-investigation-note/);
  assert.match(fixtures, /do not mutate source traces, policy, authorization, or retained evidence/);
  assert.match(fixtures, /audit\.trace\.summaryProgress\.v1/);
  assert.match(fixtures, /audit\.trace\.summaryReview\.v1/);
  assert.match(fixtures, /blocked_provider_or_runtime/);
  assert.match(fixtures, /no deterministic, fixture, fake, or model-less audit summary success/);
  assert.doesNotMatch(fixtures, /frontend-only authorization|frontend grants authority/i);
});

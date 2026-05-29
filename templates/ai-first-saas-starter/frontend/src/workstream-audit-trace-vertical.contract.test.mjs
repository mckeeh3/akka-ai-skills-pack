import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const fixtures = read('./workstream/fixtures/surfaces.ts');
const types = read('./workstream/types/surfaces.ts');
const timeline = read('./workstream/surfaces/AuditTimelineSurface.tsx');
const detail = read('./workstream/surfaces/DetailEditSurface.tsx');
const decision = read('./workstream/surfaces/DecisionSurface.tsx');
const list = read('./workstream/surfaces/ListSearchSurface.tsx');
const dashboard = read('./workstream/surfaces/DashboardSurface.tsx');
const workstream = read('./workstream/fixtures/workstream.ts');

test('Audit/Trace v0 fixture exposes contract capabilities, surfaces, and backend-authoritative actions', () => {
  for (const capability of ['audit.trace.dashboard.read', 'audit.trace.search', 'audit.trace.detail.read', 'audit.trace.timeline.read', 'audit.trace.failureEvidence.read', 'audit.trace.investigationGuide.read']) {
    assert.match(fixtures, new RegExp(capability.replace('.', '\\.')));
  }
  for (const surface of ['auditTraceDashboardSurface', 'auditTraceSearchSurface', 'auditTraceDetailSurface', 'auditTraceTimelineSurface', 'auditTraceFailureEvidenceSurface', 'auditTraceInvestigationGuideSurface']) {
    assert.match(fixtures, new RegExp(`export const ${surface}`));
  }
  assert.match(fixtures, /auditTraceStructuredSurfaces/);
  assert.match(fixtures, /Frontend affordances never grant authority/);
});

test('Audit/Trace surfaces preserve trace links, denial/provider evidence, redaction, and safe rendering states', () => {
  assert.match(types, /nodes\?: Array/);
  assert.match(types, /redactedEvidence\?: string/);
  assert.match(types, /safeReason\?: string/);
  assert.match(types, /allowedActions\?: Array/);
  assert.match(timeline, /Audit correlation timeline/);
  assert.match(timeline, /partial timeline/i);
  assert.match(timeline, /surface-audit-trace-detail/);
  assert.match(detail, /audit-evidence-panel/);
  assert.match(detail, /Denial\/provider\/tool evidence/);
  assert.match(detail, /Redacted details/);
  assert.match(decision, /Backend-authorized investigation actions/);
  assert.match(decision, /Disabled or deferred investigation actions/);
  assert.match(list, /JSON\.stringify\(envelope\.data\.query\)/);
  assert.match(list, /Partial results/);
  assert.match(dashboard, /Backend capabilities/);
});

test('Audit/Trace initial workstream remains trace-linked and does not claim frontend authorization', () => {
  assert.match(workstream, /agent-audit-trace/);
  assert.match(workstream, /traceLinks/);
  assert.match(fixtures, /Autonomous audit-summary task lifecycle is deferred until backend runtime exists/);
  assert.doesNotMatch(fixtures, /frontend-only authorization|frontend grants authority/i);
});

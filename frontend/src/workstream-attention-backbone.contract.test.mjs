import assert from 'node:assert/strict';
import { existsSync, readFileSync } from 'node:fs';
import test from 'node:test';

const resolve = (path) => new URL(path, import.meta.url);
const read = (path) => readFileSync(resolve(path), 'utf8');
const readBackend = (path) => {
  const scaffoldRootPath = `../../${path}`;
  if (existsSync(resolve(scaffoldRootPath))) return read(scaffoldRootPath);
  const sourceRepoTemplatePath = `../../templates/ai-first-saas-starter/backend/${path}`;
  if (existsSync(resolve(sourceRepoTemplatePath))) return read(sourceRepoTemplatePath);
  return read(`../../backend/${path}`);
};

const agentTypes = read('./workstream/types/agents.ts');
const surfaceTypes = read('./workstream/types/surfaces.ts');
const railItem = read('./workstream/rail/FunctionalAgentRailItem.tsx');
const railAttentionState = read('./workstream/rail/railAttentionState.ts');
const dashboardSurface = read('./workstream/surfaces/DashboardSurface.tsx');
const fixtureAgents = read('./__tests__/fixtures/workstream/agents.ts');
const fixtureSurfaces = read('./__tests__/fixtures/workstream/surfaces.ts');
const backendWorkstreamService = readBackend('src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java');
const backendMeResponse = readBackend('src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MeResponse.java');


test('left rail actionable badges are backend-derived and distinct from transient unseen-response state', () => {
  assert.match(backendWorkstreamService, /functionalAgentsWithBackendAttention/);
  assert.match(backendWorkstreamService, /AttentionService\.LIST_RAIL_SUMMARIES_TOOL/);
  assert.match(backendWorkstreamService, /listRailSummaries\(actor, correlationId\)/);
  assert.match(backendMeResponse, /record FunctionalAgentAttention\(int count, String severity, String source\)/);
  assert.match(agentTypes, /source\?: 'attention\.list_rail_summaries' \| string/);
  assert.match(railItem, /data-attention-kind="backend-actionable"/);
  assert.match(railItem, /data-attention-source=\{entry\.attention\.source \?\? 'attention\.list_rail_summaries'\}/);
  assert.match(railItem, /rail-unseen-response-badge/);
  assert.match(railAttentionState, /recordUnseenRailResponse/);
  assert.doesNotMatch(railAttentionState, /attention\.list_rail_summaries/);
  assert.match(fixtureAgents, /source: 'attention\.list_rail_summaries'/);
});


test('dashboard and My Account attention payloads include backend item metadata, action target, redaction, and trace refs', () => {
  for (const marker of [
    'export type AttentionItemStatus',
    'export type AttentionCategory',
    'export type AttentionSurfaceRef',
    'export type AttentionItem',
    "attentionSource?: 'attention.list_workstream_items'",
    'capabilityId?: string',
    'governedToolId?: string',
    'sourceWorkstreamId?: string',
    'redaction?: AttentionRedaction'
  ]) assert.ok(surfaceTypes.includes(marker), marker);

  assert.match(dashboardSurface, /aria-label="Backend-derived attention items; Audit\/Trace attention items"/);
  assert.match(dashboardSurface, /data-attention-source=\{envelope\.data\.attentionSource \?\? 'attention\.list_workstream_items'\}/);
  assert.match(dashboardSurface, /data-attention-redaction=\{item\.redaction \?\? 'full'\}/);
  assert.match(dashboardSurface, /Governed tool:/);
  assert.match(dashboardSurface, /Target surface:/);
  assert.match(fixtureSurfaces, /attention-agent-admin-readiness/);
  assert.match(fixtureSurfaces, /governedToolId: 'attention\.open_attention_item'/);
  assert.match(fixtureSurfaces, /redaction: 'full'/);
});


test('attention fixtures and rendering preserve empty or denied states without using railAttentionState as authority', () => {
  assert.match(dashboardSurface, /attentionItems && envelope\.data\.attentionItems\.length > 0/);
  assert.match(surfaceTypes, /'not_found_or_redacted'/);
  assert.match(backendWorkstreamService, /not_found_or_redacted/);
  assert.match(backendWorkstreamService, /open_attention_item/);
  assert.match(backendWorkstreamService, /TARGET_NOT_FOUND_OR_FORBIDDEN/);
  assert.doesNotMatch(dashboardSurface, /railAttentionState/);
  assert.doesNotMatch(fixtureSurfaces, /railAttentionState/);
});

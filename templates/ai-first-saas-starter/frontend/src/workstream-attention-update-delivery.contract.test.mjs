import assert from 'node:assert/strict';
import { existsSync, readFileSync } from 'node:fs';
import test from 'node:test';

const resolve = (path) => new URL(path, import.meta.url);
const read = (path) => readFileSync(resolve(path), 'utf8');
const readBackend = (path) => {
  const scaffoldRootPath = `../../${path}`;
  if (existsSync(resolve(scaffoldRootPath))) return read(scaffoldRootPath);
  return read(`../../backend/${path}`);
};

const main = read('./main.tsx');
const apiClient = read('./api/WorkstreamApiClient.ts');
const httpClient = read('./api/HttpWorkstreamApiClient.ts');
const railAttentionState = read('./workstream/rail/railAttentionState.ts');
const railItem = read('./workstream/rail/FunctionalAgentRailItem.tsx');
const dashboardSurface = read('./workstream/surfaces/DashboardSurface.tsx');
const backendWorkstreamService = readBackend('src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java');
const backendMyAccountService = readBackend('src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MyAccountService.java');

test('bootstrap and refresh use backend-derived rail summaries instead of frontend-only attention authority', () => {
  assert.match(main, /me: \{ \.\.\.result\.value\.me, functionalAgents: result\.value\.functionalAgents \}/);
  assert.match(main, /refreshBackendAttentionSummaries/);
  assert.match(main, /workstreamClient\.listFunctionalAgents\(\)/);
  assert.match(main, /me: \{ \.\.\.current\.me, functionalAgents: result\.value \}/);
  assert.match(apiClient, /listFunctionalAgents\(\): Promise<ApiResult<FunctionalAgentSummary\[\]>>/);
  assert.match(httpClient, /\/api\/workstream\/functional-agents/);
  assert.match(backendWorkstreamService, /AttentionService\.LIST_RAIL_SUMMARIES_TOOL/);
  assert.match(backendWorkstreamService, /listRailSummaries\(actor, correlationId\)/);
  assert.doesNotMatch(railAttentionState, /attention\.list_rail_summaries/);
  assert.match(railItem, /data-attention-kind="backend-actionable"/);
});

test('producer-affecting actions, workstream open, and shell refresh reload backend attention surfaces', () => {
  assert.match(main, /reason: 'producer-affecting-action-completion'/);
  assert.match(main, /reason: 'workstream-open'/);
  assert.match(main, /reason: 'shell-surface-refresh'/);
  assert.match(main, /eventType === 'projection\.refresh\.available' \|\| event\.eventType === 'surface\.stale'/);
  assert.match(main, /reason: 'event-backed-projection-refresh'/);
  assert.match(main, /refreshBackendDerivedAttentionDelivery/);
  assert.match(main, /workstreamClient\.getSurface\(surfaceId\)/);
  assert.match(main, /dashboardSurfaceIdForAgent\(input\.functionalAgentId\)/);
  assert.match(backendWorkstreamService, /attentionService\.listWorkstreamItems\(actor, workstreamId, correlationId\)/);
  assert.match(backendWorkstreamService, /projection\.refresh\.available/);
  assert.match(backendWorkstreamService, /workstream\.event\.delivery\.refresh/);
  assert.match(backendWorkstreamService, /eventBackedRefreshEvents\(actor, correlationId\)/);
  assert.match(backendMyAccountService, /attentionService\.listMyAccountItems\(actor, correlationId\)/);
  assert.match(dashboardSurface, /data-attention-source=\{envelope\.data\.attentionSource \?\? 'attention\.list_workstream_items'\}/);
});

test('transient railAttentionState remains distinct from actionable backend attention delivery', () => {
  assert.match(main, /Transient railAttentionState remains only for unseen responses/);
  assert.match(main, /recordUnseenRailResponse/);
  assert.match(main, /kind: 'background-response'/);
  assert.match(main, /'background-activity'/);
  const backendSummaryRefresh = main.slice(main.indexOf('async function refreshBackendAttentionSummaries'), main.indexOf('async function refreshBackendSurface'));
  assert.doesNotMatch(backendSummaryRefresh, /setRailAttentionByAgentId/);
  assert.doesNotMatch(backendSummaryRefresh, /railAttentionByAgentId/);
});

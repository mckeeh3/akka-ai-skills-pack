import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const main = read('./main.tsx');
const apiContract = read('./api/WorkstreamApiClient.ts');
const httpClient = read('./api/HttpWorkstreamApiClient.ts');
const fixtureClient = read('./__tests__/fixtures/api/FixtureWorkstreamApiClient.ts');
const stream = read('./workstream/stream/WorkstreamStream.tsx');
const shell = read('./workstream/shell/WorkstreamShell.tsx');
const composer = read('./workstream/composer/WorkstreamComposer.tsx');
const markdownSurface = read('./workstream/surfaces/MarkdownResponseSurface.tsx');
const surfaceFrame = read('./workstream/surfaces/SurfaceStateFrame.tsx');
const itemCard = read('./workstream/stream/WorkstreamItem.tsx');
const componentStyles = read('./styles/components.css');
const tokens = read('./styles/tokens.css');

test('composer submits normal prompts through backend workstream message API', () => {
  assert.match(apiContract, /submitWorkstreamMessage\(request: WorkstreamMessageRequest\): Promise<ApiResult<WorkstreamMessageResponse>>/);
  assert.match(httpClient, /submitWorkstreamMessage\(request: WorkstreamMessageRequest\)/);
  assert.match(httpClient, /\/api\/workstream\/messages/);
  assert.match(httpClient, /if \(request\.selectedContextId\) this\.selectedContextId = request\.selectedContextId/);
  assert.match(main, /async function handleComposerSubmit/);
  assert.match(main, /workstreamClient\.submitWorkstreamMessage/);
  assert.doesNotMatch(main, /showUserDetail|showUsers|requestedSurface/);
});

test('composer routes dashboard prompts through backend shell requests instead of model runtime', () => {
  assert.match(main, /function buildComposerShellRequest\(prompt: string, functionalAgentId: string, selectedContextId: string, correlationId: string\): WorkstreamShellRequest \| undefined/);
  assert.match(main, /'show dashboard'/);
  assert.match(main, /workstreamClient\.runShellRequest\(shellRequest\)/);
  assert.match(main, /dashboardSurfaceIdForAgent\(functionalAgentId\)/);
  assert.match(main, /case 'user-admin-agent'|default: return 'surface-user-admin-dashboard'/);
  assert.match(main, /const shellRequest = buildComposerShellRequest\(request\.prompt, request\.functionalAgentId, me\.selectedAuthContext\.selectedContextId, correlationId\)/);
});

test('standard Show dashboard button is shell-handled and appends request plus dashboard surface', () => {
  assert.match(composer, /className="ds-button secondary icon-button show-dashboard-button"/);
  assert.match(composer, /aria-label="Show dashboard"/);
  assert.match(composer, /<DashboardIcon \/>/);
  assert.match(composer, /onClick=\{showDashboard\}/);
  assert.match(shell, /onShowDashboard=\{onShowDashboard\}/);
  assert.match(main, /async function handleShowDashboard\(functionalAgentId: string\)/);
  assert.match(main, /buildShowDashboardShellRequest\(functionalAgentId, me\.selectedAuthContext\.selectedContextId, correlationId, 'shell_button'\)/);
  assert.match(main, /canonicalPrompt: 'Show dashboard'/);
  assert.match(main, /origin: 'shell_button'|origin,\n    displayText/);
  assert.match(main, /await runShellSurfaceRequest\(shellRequest, functionalAgentId, 'show-dashboard'\)/);
  assert.match(main, /kind: 'user-request'/);
  assert.match(main, /kind: 'surface'/);
  assert.match(componentStyles, /\.show-dashboard-button/);
  assert.match(componentStyles, /\.send-prompt-button,\n\.show-dashboard-button \{[\s\S]*?background: var\(--color-primary\);[\s\S]*?color: var\(--color-inverse-text\);/);
  assert.match(componentStyles, /\.dashboard-button-icon/);
  assert.match(componentStyles, /\.workstream-show-dashboard-tooltip/);
});

test('composer response appends returned items and markdown_response surface', () => {
  assert.match(main, /const \{ userItem, agentItem, surface \} = result\.value/);
  assert.match(main, /traceableAgentItem/);
  assert.match(main, /items: pruneWorkstreamItems\(\[\.\.\.current\.items\.filter\(\(item\) => item\.itemId !== userRequestItem\.itemId\), userItem, traceableAgentItem\]\)/);
  assert.match(main, /const responseFunctionalAgentId = surface\.ownerFunctionalAgentId \?\? request\.functionalAgentId/);
  assert.match(main, /setRequestScrollTargetForCurrentSession\(userItem\.itemId, responseFunctionalAgentId\)/);
  assert.match(main, /selectedSurfaceId: surface\.surfaceId/);
  assert.match(stream, /item\.kind === 'markdown_response'/);
  assert.match(surfaceFrame, /id=\{visibleEnvelope\.surfaceId\}/);
  assert.match(surfaceFrame, /tabIndex=\{-1\}/);
  assert.match(apiContract, /surface: SurfaceEnvelope<unknown>/);
  assert.match(apiContract, /userItem: WorkstreamItem/);
  assert.match(apiContract, /agentItem: WorkstreamItem/);
});

test('composer acknowledges prompts immediately as request surfaces and scrolls them to the top', () => {
  assert.match(main, /const \[requestScrollTargetBySessionKey, setRequestScrollTargetBySessionKey\] = React\.useState<Record<string, string \| undefined>>\(\{\}\)/);
  assert.match(main, /const userRequestItem: WorkstreamItem = \{/);
  assert.match(main, /kind: 'user-request'/);
  assert.match(main, /body: request\.prompt/);
  assert.match(main, /setRequestScrollTargetForCurrentSession\(userRequestItem\.itemId, request\.functionalAgentId\)/);
  assert.match(main, /requestScrollTargetId=\{currentRequestScrollTargetId\}/);
  assert.match(stream, /requestScrollTargetId\?: string/);
  assert.match(stream, /useLayoutEffect/);
  assert.match(stream, /findRequestScrollTarget\(requestScrollTargetId, streamRef\.current\)/);
  assert.match(stream, /data-surface-id/);
  assert.match(stream, /scrollTargetToContainerTop\(requestSurface, streamRef\.current, behavior\)/);
  assert.match(stream, /scrollContainer\.scrollTo\(\{/);
  assert.match(stream, /requestSurface\.focus\(\{ preventScroll: true \}\)/);
});

test('composer exposes in-flight model submission state with an inline request spinner and clears submitted drafts', () => {
  assert.match(main, /submittingFunctionalAgentId/);
  assert.doesNotMatch(main, /Submitting to model-backed agent/);
  assert.match(main, /status: 'working'/);
  assert.match(itemCard, /workstream-request-spinner/);
  assert.match(itemCard, /aria-busy=\{isWorking \|\| undefined\}/);
  assert.match(composer, /isSubmitting/);
  assert.match(composer, /Model-backed agent is responding/);
  assert.match(composer, /setDraft\(''\);\n    await onSubmit\?\.\(request\)/);
});

test('composer maps provider-missing and forbidden errors to safe system notifications', () => {
  assert.match(main, /safeComposerErrorCopy/);
  assert.match(main, /provider configuration required/);
  assert.match(main, /Configure the backend provider variables/);
  assert.match(main, /secrets are not exposed in the browser/);
  assert.match(main, /Message not submitted · forbidden/);
  assert.match(main, /kind: 'system-notification'/);
  assert.match(main, /status: safeError\.status/);
});

test('composer accepts backend typed system_message agent responses for provider/runtime blocked turns', () => {
  assert.match(apiContract, /surface: SurfaceEnvelope<unknown>/);
  assert.match(itemCard, /item\.kind === 'system_message'/);
  assert.match(surfaceFrame, /Redaction profile/);
  assert.match(main, /const \{ userItem, agentItem, surface \} = result\.value/);
  assert.match(main, /traceableAgentItem/);
  assert.match(main, /selectedSurfaceId: surface\.surfaceId/);
  assert.doesNotMatch(main, /blocked_provider_or_runtime[\s\S]*providerCredential|api[_-]?key/i);
});

test('successful prompt and model response surfaces render only prompt and response text', () => {
  assert.match(main, /traceableAgentItem/);
  assert.match(main, /traceLinks: agentItem\.traceLinks \?\? agentItem\.traceIds\.map/);
  assert.match(itemCard, /item\.kind === 'user-request' \|\| item\.kind === 'user-message'/);
  assert.match(itemCard, /prompt-input-surface/);
  assert.match(itemCard, /aria-label="Request received"/);
  assert.match(itemCard, /\{item\.body \?\? item\.title \?\? ''\}/);
  assert.match(itemCard, /return <ActionFeedbackItem/);
  assert.match(markdownSurface, /markdown-response-only/);
  assert.doesNotMatch(markdownSurface, /Trace links:|\/ui\?traceId=|surface-summary|SurfaceActionBar/);
});

test('user request acknowledgement surface is right-justified, compact, and visually distinct from response surfaces', () => {
  assert.match(componentStyles, /\.workstream-item\.user-request\.prompt-input-surface/);
  assert.match(componentStyles, /justify-self: end/);
  assert.match(componentStyles, /max-width: min\(46rem, 62%\)/);
  assert.match(componentStyles, /background: var\(--color-request-surface\)/);
  assert.match(tokens, /--color-request-surface:/);
  assert.doesNotMatch(componentStyles, /\.workstream-item\.user-request\.prompt-input-surface[^}]*background: var\(--color-surface\)/);
});

test('surface and action request acknowledgement surfaces match compact request treatment', () => {
  assert.match(itemCard, /item\.kind === 'surface-request'/);
  assert.match(itemCard, /request-surface/);
  assert.match(itemCard, /action-request-surface/);
  assert.match(itemCard, /View request received/);
  assert.match(itemCard, /Action request received/);
  assert.match(itemCard, /<p>\{item\.title \?\? ''\}<\/p>/);
  assert.doesNotMatch(itemCard, /item\.body && item\.body !== item\.title/);
  assert.match(componentStyles, /\.workstream-item\.surface-request\.request-surface/);
  assert.match(componentStyles, /max-width: min\(46rem, 62%\)/);
  assert.match(componentStyles, /\.workstream-item\.surface-request\.action-request-surface/);
  assert.doesNotMatch(componentStyles, /\.request-surface small/);
});

test('background composer responses update their originating workstream without stealing selected workstream focus', () => {
  assert.match(main, /const selectedFunctionalAgentIdRef = React\.useRef<string \| undefined>\(selectedFunctionalAgentId\)/);
  assert.match(main, /selectedFunctionalAgentIdRef\.current = selectedFunctionalAgentId/);
  assert.match(main, /function updateSelection\(nextSelection: Partial<WorkstreamSelection>\) \{\s*const merged = \{ \.\.\.selection, \.\.\.nextSelection \};\s*selectedFunctionalAgentIdRef\.current = merged\.selectedFunctionalAgentId;\s*setSelection\(merged\);/s);
  assert.match(main, /function isCurrentlySelectedFunctionalAgent\(functionalAgentId: string\) \{\s*return selectedFunctionalAgentIdRef\.current === functionalAgentId;\s*\}/s);
  assert.match(main, /const responseFunctionalAgentId = surface\.ownerFunctionalAgentId \?\? request\.functionalAgentId/);
  assert.match(main, /setBootstrap\(\(current\) => \{[\s\S]*items: pruneWorkstreamItems\(\[\.\.\.current\.items\.filter\(\(item\) => item\.itemId !== userRequestItem\.itemId\), userItem, traceableAgentItem\]\)[\s\S]*\}\);/);
  assert.match(main, /if \(isCurrentlySelectedFunctionalAgent\(responseFunctionalAgentId\)\) \{\s*updateSelection\(\{\s*selectedFunctionalAgentId: responseFunctionalAgentId,/s);
  assert.match(main, /\} else \{\s*markUnseenResponse\(responseFunctionalAgentId, traceableAgentItem\.itemId, 'info'\);\s*\}/s);
  assert.doesNotMatch(main, /updateSelection\(\{\s*selectedFunctionalAgentId: surface\.ownerFunctionalAgentId \?\? request\.functionalAgentId,/s);
});

const updateSelectionBlock = main.match(/function updateSelection[\s\S]*?\n  \}/)?.[0] ?? '';
const composerSuccessBlock = main.match(/const \{ userItem, agentItem, surface \} = result\.value;[\s\S]*?return true;/)?.[0] ?? '';
const surfaceActionBlock = main.match(/async function handleSurfaceAction[\s\S]*?\n  async function handleComposerSubmit/)?.[0] ?? '';

test('async response selection guards use a synchronously updated selected-workstream ref', () => {
  assert.ok(updateSelectionBlock.indexOf('selectedFunctionalAgentIdRef.current = merged.selectedFunctionalAgentId') < updateSelectionBlock.indexOf('setSelection(merged)'));
  assert.ok(updateSelectionBlock.indexOf('selectedFunctionalAgentIdRef.current = merged.selectedFunctionalAgentId') < updateSelectionBlock.indexOf('window.history.pushState'));
  assert.ok(composerSuccessBlock.indexOf('if (isCurrentlySelectedFunctionalAgent(responseFunctionalAgentId))') < composerSuccessBlock.indexOf('updateSelection({'));
  assert.ok(surfaceActionBlock.indexOf('if (targetSurface && isCurrentlySelectedFunctionalAgent(targetSurface.ownerFunctionalAgentId))') < surfaceActionBlock.indexOf('updateSelection({'));
  assert.doesNotMatch(surfaceActionBlock, /if \(targetSurface\) \{\s*updateSelection\(/);
});

test('fixture client returns backend-equivalent markdown for every initial core workstream', () => {
  for (const agentId of ['my-account-agent', 'user-admin-agent', 'agent-admin-agent', 'agent-audit-trace', 'governance-policy-agent']) {
    assert.match(fixtureClient, new RegExp(agentId));
  }
  assert.match(fixtureClient, /surfaceType: 'markdown_response'/);
  assert.match(fixtureClient, /kind: 'markdown_response'/);
  assert.match(fixtureClient, /producingAgentId: request\.functionalAgentId/);
  assert.match(fixtureClient, /selectedContextId: request\.selectedContextId/);
  assert.match(fixtureClient, /full-core behavior/);
});

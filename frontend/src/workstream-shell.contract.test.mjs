import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';
import ts from 'typescript';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const railState = read('./workstream/rail/railState.ts');
const railAttentionState = read('./workstream/rail/railAttentionState.ts');
const rail = read('./workstream/rail/FunctionalAgentRail.tsx');
const railItem = read('./workstream/rail/FunctionalAgentRailItem.tsx');
const workstreamIcon = read('./workstream/rail/WorkstreamIcon.tsx');
const toggle = read('./workstream/rail/CollapsedRailToggle.tsx');
const composer = read('./workstream/composer/WorkstreamComposer.tsx');
const composerState = read('./workstream/composer/composerState.ts');
const panel = read('./workstream/shell/WorkstreamPanel.tsx');
const shell = read('./workstream/shell/WorkstreamShell.tsx');
const stream = read('./workstream/stream/WorkstreamStream.tsx');
const deepLinks = read('./workstream/shell/WorkstreamDeepLinks.ts');
const agentTypes = read('./workstream/types/agents.ts');
const agentFixtures = read('./workstream/fixtures/agents.ts');
const componentsCss = read('./styles/components.css');

function loadTypeScriptExports(source) {
  const transpiled = ts.transpileModule(source, {
    compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2020 }
  }).outputText;
  const module = { exports: {} };
  new Function('module', 'exports', transpiled)(module, module.exports);
  return module.exports;
}

test('functional agent rail is collapsible and lists only allowed workstreams', () => {
  assert.match(railState, /hasRequiredCapabilities/);
  assert.match(railState, /visibleCapabilityIds/);
  assert.match(railState, /railAttentionByAgentId: FunctionalAgentRailAttentionStore = \{\}/);
  assert.match(railState, /entry\.availability === 'visible' && entry\.visibilityReason === 'has-capability'/);
  assert.match(railItem, /aria-current=\{entry\.isSelected \? 'page'/);
  assert.match(railItem, /const workstreamIcon = entry\.workstreamIcon \?\? fallbackIcon/);
  assert.match(railItem, /<WorkstreamIcon descriptor=\{workstreamIcon\}/);
  assert.match(railItem, /aria-label=\{workstreamIcon\.ariaLabel\}/);
  assert.match(railItem, /aria-describedby=\{describedBy\}/);
  assert.match(railItem, /data-workstream-icon-id=\{workstreamIcon\.iconId\}/);
  assert.match(railItem, /data-accent-color-token=\{workstreamIcon\.accentColorToken\}/);
  assert.match(railItem, /className="workstream-icon-tooltip" role="tooltip"/);
  assert.match(railItem, /rail-unseen-response-badge/);
  assert.match(railItem, /aria-label=\{unseenResponseLabel\}/);
  assert.match(railItem, /data-attention-kind=\{entry\.railAttention\.kind\}/);
  assert.match(toggle, /aria-expanded=\{!collapsed\}/);
  assert.match(toggle, /aria-controls="workstream-functional-agent-rail-list"/);
  assert.match(toggle, /collapsed \? 'Expand sidebar' : 'Collapse sidebar'/);
  assert.match(toggle, /workstream-rail-toggle-tooltip/);
  assert.doesNotMatch(toggle, /title=/);
  assert.match(rail, /aria-label="Functional agents"/);
  assert.match(rail, /const myAccountFunctionalAgentId = 'agent-my-account'/);
  assert.match(rail, /entry\.functionalAgentId !== myAccountFunctionalAgentId/);
  assert.match(rail, /className=\{`rail-user-button \$\{myAccountSelected \? 'active' : ''\}`\.trim\(\)\}/);
  assert.match(rail, /aria-label=\{`Open My Account workstream for \$\{userDisplayName\}`\}/);
  assert.match(rail, /onClick=\{openMyAccount\}/);
  assert.doesNotMatch(rail, /aria-haspopup="menu"/);
  assert.doesNotMatch(rail, /rail-user-menu/);
  assert.doesNotMatch(rail, /role="menuitem"/);
  assert.match(rail, /railAttentionByAgentId\?: FunctionalAgentRailAttentionStore/);
  assert.match(rail, /visibleRailEntries\(agents, selectedFunctionalAgentId, visibleCapabilityIds, accountStatus, railAttentionByAgentId\)/);
  assert.match(rail, /onToggleCollapsed/);
});

test('left rail renders descriptor-backed workstream icons for core v0 workstreams', () => {
  for (const [label, iconId, token, tooltip] of [
    ['User Admin', 'users-admin', 'accent-users', 'Open User Admin workstream'],
    ['Agent Admin', 'bot-spark', 'accent-agents', 'Open Agent Admin workstream'],
    ['Audit/Trace', 'timeline-search', 'accent-audit', 'Open Audit/Trace workstream'],
    ['Governance/Policy', 'shield-checklist', 'accent-governance', 'Open Governance/Policy workstream']
  ]) {
    assert.match(agentFixtures, new RegExp(`label: '${label.replace('/', '\\/')}'`));
    assert.match(agentFixtures, new RegExp(`iconId: '${iconId}'`));
    assert.match(agentFixtures, new RegExp(`accentColorToken: '${token}'`));
    assert.match(agentFixtures, new RegExp(`tooltip: '${tooltip.replace('/', '\\/')}'`));
    assert.match(agentFixtures, new RegExp(`ariaLabel: '${tooltip.replace('/', '\\/')}'`));
  }
  assert.match(railItem, /workstreamIcon\.tooltip \? tooltipId/);
  assert.match(railItem, /role="tooltip">\{workstreamIcon\.tooltip\}/);
  assert.match(railItem, /fallbackIcon: WorkstreamIconDescriptor/);
  assert.match(workstreamIcon, /export function deriveWorkstreamIconArtwork/);
  assert.match(workstreamIcon, /<svg viewBox="0 0 24 24"/);
  assert.match(workstreamIcon, /data-icon-artwork=\{artwork\}/);
  assert.match(workstreamIcon, /keywordArtwork/);
  assert.match(workstreamIcon, /'users-admin': 'users'/);
  assert.match(workstreamIcon, /'bot-spark': 'bot'/);
  assert.doesNotMatch(workstreamIcon, /slice\(0, 1\)|toUpperCase\(\)/);
  assert.match(componentsCss, /\.workstream-icon svg/);
  assert.match(componentsCss, /\.workstream-icon\[data-accent-color-token="accent-users"\]/);
  assert.match(componentsCss, /\.workstream-agent-button:focus-visible \.workstream-icon-tooltip/);
  assert.doesNotMatch(railItem, /title=/);
});

test('left rail unseen response indicators are accessible, extensible, and visual-only', () => {
  assert.match(agentTypes, /FunctionalAgentRailAttentionKind = 'background-response' \| 'background-activity'/);
  assert.match(agentTypes, /unseenResponseCount: number/);
  assert.match(agentTypes, /railAttention\?: FunctionalAgentRailAttention/);
  assert.match(componentsCss, /\.rail-unseen-response-badge/);
  assert.match(componentsCss, /workstream-functional-agent-rail\.collapsed \.rail-unseen-response-badge/);
  assert.match(componentsCss, /position: absolute;\s*top: 0\.25rem;\s*right: 0\.2rem;/);
  assert.match(railState, /filter\(\(entry\) => entry\.availability === 'visible' && entry\.visibilityReason === 'has-capability'\)/);
  assert.match(railAttentionState, /recordUnseenRailResponse/);
  assert.match(railAttentionState, /clearRailAttentionForAgent/);
});

test('rail attention state records background responses and clears on selection', () => {
  const { recordUnseenRailResponse, clearRailAttentionForAgent } = loadTypeScriptExports(railAttentionState);
  const selectedStore = recordUnseenRailResponse({}, {
    functionalAgentId: 'agent-user-admin',
    selectedFunctionalAgentId: 'agent-user-admin',
    lastItemId: 'response-current'
  });
  assert.deepEqual(selectedStore, {}, 'selected workstream responses must not create unseen indicators');

  const first = recordUnseenRailResponse({}, {
    functionalAgentId: 'agent-user-admin',
    selectedFunctionalAgentId: 'agent-agent-admin',
    lastItemId: 'response-1',
    severity: 'info',
    kind: 'background-response',
    lastUpdatedAt: '2026-05-26T00:00:00.000Z'
  });
  assert.equal(first['agent-user-admin'].unseenResponseCount, 1);
  assert.equal(first['agent-user-admin'].lastItemId, 'response-1');
  assert.equal(first['agent-user-admin'].kind, 'background-response');

  const second = recordUnseenRailResponse(first, {
    functionalAgentId: 'agent-user-admin',
    selectedFunctionalAgentId: 'agent-agent-admin',
    lastItemId: 'response-2',
    severity: 'warning',
    kind: 'background-activity',
    lastUpdatedAt: '2026-05-26T00:01:00.000Z'
  });
  assert.equal(second['agent-user-admin'].unseenResponseCount, 2);
  assert.equal(second['agent-user-admin'].severity, 'warning');
  assert.equal(second['agent-user-admin'].lastItemId, 'response-2');

  const cleared = clearRailAttentionForAgent(second, 'agent-user-admin');
  assert.equal(cleared['agent-user-admin'], undefined);
});

test('persistent composer is selected-agent aware and exposes disabled states', () => {
  assert.match(composerState, /composerAvailability/);
  assert.match(composerState, /selectedAgent/);
  assert.match(composerState, /The signed-in account is disabled/);
  assert.match(composerState, /No active membership/);
  assert.match(composerState, /Missing required capability/);
  assert.match(composer, /aria-label="Persistent workstream composer"/);
  assert.match(composer, /Ask \{selectedAgent\?\.label/);
  assert.match(composer, /disabled=\{isSubmitting \|\| Boolean\(disabledReason\)\}/);
  assert.match(composer, /disabled=\{submitDisabled\}/);
  assert.match(composer, /rows=\{1\}/);
  assert.match(composer, /useLayoutEffect/);
  assert.match(composer, /input\.style\.height = 'auto'/);
  assert.match(composer, /input\.scrollHeight/);
  assert.match(composer, /autoFocus/);
  assert.match(composer, /function focusComposerInput/);
  assert.match(composer, /input\.focus\(\{ preventScroll: true \}\)/);
  assert.match(composer, /useEffect\(\(\) => \{\s*focusComposerInput\(\);\s*\}, \[selectedAgent\?\.functionalAgentId, disabledReason, isSubmitting\]\)/);
  assert.match(composer, /window\.addEventListener\('focus', focusComposerInput\)/);
  assert.match(composer, /document\.addEventListener\('visibilitychange', refocusVisibleComposer\)/);
  assert.match(composer, /shouldRestoreComposerFocus/);
  assert.match(composer, /activeElement\.closest\('input, textarea, select, \[contenteditable="true"\]'\)/);
  assert.match(composer, /window\.requestAnimationFrame\(\(\) => \{/);
  assert.match(composer, /if \(shouldRestoreComposerFocus\(document\.activeElement\)\) focusComposerInput\(\)/);
  assert.match(composer, /window\.requestAnimationFrame\(focusComposerInput\)/);
  assert.match(composer, /onKeyDown=\{submitFromKeyboard\}/);
  assert.match(composer, /onBlur=\{refocusComposerAfterBlur\}/);
  assert.match(composer, /event\.key !== 'Enter' \|\| event\.shiftKey/);
  assert.match(composer, /event\.currentTarget\.form\?\.requestSubmit\(\)/);
  assert.match(composer, /workstream-send-prompt-tooltip/);
  assert.match(componentsCss, /\.workstream-composer-region \{[\s\S]*?overflow: visible;[\s\S]*?pointer-events: none;/);
  assert.match(componentsCss, /\.workstream-composer \{[\s\S]*?overflow: visible;[\s\S]*?grid-template-columns/);
  assert.match(componentsCss, /\.send-prompt-button \{[\s\S]*?overflow: visible;/);
  assert.match(componentsCss, /\.workstream-send-prompt-tooltip \{[\s\S]*?position: absolute;[\s\S]*?bottom: calc\(100% \+ var\(--space-2\)\)/);
  assert.doesNotMatch(composer, /title=/);
  assert.match(composer, /buildComposerRequest/);
});

test('workstream shell composes left rail, continuous flow, and floating persistent composer regions', () => {
  assert.match(shell, /<FunctionalAgentRail/);
  assert.doesNotMatch(shell, /<ContextAuthorityBar/);
  assert.match(shell, /<WorkstreamPanel/);
  assert.match(shell, /<WorkstreamComposer/);
  assert.match(shell, /submittingFunctionalAgentId === selectedFunctionalAgentId/);
  assert.match(shell, /useEffect\(\(\) => \{/);
  assert.match(shell, /setSelectedFunctionalAgentId\(initialFunctionalAgentId \?\? initialAgentId\)/);
  assert.match(shell, /aria-label="Persistent composer region"/);
  assert.match(panel, /<main id="main-content" className="content workstream-panel"/);
  assert.match(panel, /workstream-flow/);
  assert.match(panel, /aria-labelledby="workstream-panel-title"/);
  assert.match(panel, /tabIndex=\{-1\}/);
});

test('workstream stream renders surface responses inline after requests', () => {
  assert.match(stream, /SurfaceRenderer/);
  assert.match(stream, /item\.kind !== 'surface'/);
  assert.match(stream, /item\.kind === 'surface'/);
  assert.match(stream, /onSurfaceAction/);
});

test('deep links are secondary selectors for agent, item, surface, and placement', () => {
  assert.match(deepLinks, /parseWorkstreamDeepLink/);
  assert.match(deepLinks, /serializeWorkstreamDeepLink/);
  for (const key of ['agent', 'itemId', 'surfaceId', 'placement']) {
    assert.match(deepLinks, new RegExp(key));
  }
});

import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const railState = read('./workstream/rail/railState.ts');
const rail = read('./workstream/rail/FunctionalAgentRail.tsx');
const railItem = read('./workstream/rail/FunctionalAgentRailItem.tsx');
const toggle = read('./workstream/rail/CollapsedRailToggle.tsx');
const composer = read('./workstream/composer/WorkstreamComposer.tsx');
const composerState = read('./workstream/composer/composerState.ts');
const panel = read('./workstream/shell/WorkstreamPanel.tsx');
const shell = read('./workstream/shell/WorkstreamShell.tsx');
const stream = read('./workstream/stream/WorkstreamStream.tsx');
const deepLinks = read('./workstream/shell/WorkstreamDeepLinks.ts');

test('functional agent rail is collapsible and lists only allowed workstreams', () => {
  assert.match(railState, /hasRequiredCapabilities/);
  assert.match(railState, /visibleCapabilityIds/);
  assert.match(railState, /entry\.availability === 'visible' && entry\.visibilityReason === 'has-capability'/);
  assert.match(railItem, /aria-current=\{entry\.isSelected \? 'page'/);
  assert.match(railItem, /iconGlyph/);
  assert.match(toggle, /aria-expanded=\{!collapsed\}/);
  assert.match(toggle, /aria-controls="workstream-functional-agent-rail-list"/);
  assert.match(toggle, /collapsed \? 'Expand sidebar' : 'Collapse sidebar'/);
  assert.match(toggle, /workstream-rail-toggle-tooltip/);
  assert.doesNotMatch(toggle, /title=/);
  assert.match(rail, /aria-label="Functional agents"/);
  assert.match(rail, /rail-user-button/);
  assert.match(rail, /Profile/);
  assert.match(rail, /Settings/);
  assert.match(rail, /Sign out/);
  assert.match(rail, /onToggleCollapsed/);
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

import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const railState = read('./workstream/rail/railState.ts');
const rail = read('./workstream/rail/FunctionalAgentRail.tsx');
const railItem = read('./workstream/rail/FunctionalAgentRailItem.tsx');
const toggle = read('./workstream/rail/CollapsedRailToggle.tsx');
const contextBar = read('./workstream/shell/ContextAuthorityBar.tsx');
const composer = read('./workstream/composer/WorkstreamComposer.tsx');
const composerState = read('./workstream/composer/composerState.ts');
const panel = read('./workstream/shell/WorkstreamPanel.tsx');
const shell = read('./workstream/shell/WorkstreamShell.tsx');
const deepLinks = read('./workstream/shell/WorkstreamDeepLinks.ts');

test('functional agent rail is collapsible and role/capability aware', () => {
  assert.match(railState, /hasRequiredCapabilities/);
  assert.match(railState, /visibleCapabilityIds/);
  assert.match(railState, /filter\(\(entry\) => entry\.availability !== 'hidden'\)/);
  assert.match(railItem, /disabled=\{!selectable\}/);
  assert.match(railItem, /aria-current=\{entry\.isSelected \? 'page'/);
  assert.match(railItem, /aria-describedby=\{disabledReason/);
  assert.match(toggle, /aria-expanded=\{!collapsed\}/);
  assert.match(toggle, /aria-controls="workstream-functional-agent-rail-list"/);
  assert.match(rail, /aria-label="Functional agents"/);
  assert.match(rail, /onToggleCollapsed/);
});

test('context authority bar exposes context, role, capability, support access, recovery, approvals, and trace links', () => {
  assert.match(contextBar, /Selected AuthContext/);
  assert.match(contextBar, /tenantName/);
  assert.match(contextBar, /customerName/);
  assert.match(contextBar, /roleIds/);
  assert.match(contextBar, /capabilityIds\.length/);
  assert.match(contextBar, /supportAccess\?\.active/);
  assert.match(contextBar, /pendingApprovalCount/);
  assert.match(contextBar, /traceLinks\.map/);
  assert.match(contextBar, /Contact an administrator|Request access/);
});

test('persistent composer is selected-agent aware and exposes disabled states', () => {
  assert.match(composerState, /composerAvailability/);
  assert.match(composerState, /selectedAgent/);
  assert.match(composerState, /The signed-in account is disabled/);
  assert.match(composerState, /No active membership/);
  assert.match(composerState, /Missing required capability/);
  assert.match(composer, /aria-label="Persistent workstream composer"/);
  assert.match(composer, /Ask \{selectedAgent\?\.label/);
  assert.match(composer, /disabled=\{Boolean\(disabledReason\)\}/);
  assert.match(composer, /disabled=\{submitDisabled\}/);
  assert.match(composer, /autoFocus/);
  assert.match(composer, /buildComposerRequest/);
});

test('workstream shell composes rail, context bar, panel, and persistent composer regions', () => {
  assert.match(shell, /<FunctionalAgentRail/);
  assert.match(shell, /<ContextAuthorityBar/);
  assert.match(shell, /<WorkstreamPanel/);
  assert.match(shell, /<WorkstreamComposer/);
  assert.match(shell, /aria-label="Persistent composer region"/);
  assert.match(panel, /<main id="main-content" className="content workstream-panel"/);
  assert.match(panel, /aria-labelledby="workstream-panel-title"/);
  assert.match(panel, /tabIndex=\{-1\}/);
});

test('deep links are secondary selectors for agent, item, surface, and placement', () => {
  assert.match(deepLinks, /parseWorkstreamDeepLink/);
  assert.match(deepLinks, /serializeWorkstreamDeepLink/);
  for (const key of ['agent', 'itemId', 'surfaceId', 'placement']) {
    assert.match(deepLinks, new RegExp(key));
  }
});

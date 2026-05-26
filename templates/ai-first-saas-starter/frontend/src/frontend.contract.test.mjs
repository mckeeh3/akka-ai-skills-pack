import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');
const tokens = readFileSync(new URL('./styles/tokens.css', import.meta.url), 'utf8');
const base = readFileSync(new URL('./styles/base.css', import.meta.url), 'utf8');
const layout = readFileSync(new URL('./styles/layout.css', import.meta.url), 'utf8');
const components = readFileSync(new URL('./styles/components.css', import.meta.url), 'utf8');

test('frontend entry composes the canonical workstream shell instead of route pages', () => {
  assert.match(main, /<WorkstreamShell/);
  assert.match(main, /<WorkstreamStream/);
  assert.match(main, /meTenantAdmin/);
  assert.doesNotMatch(main, /function RouteShell/);
  assert.doesNotMatch(main, /function SidebarNav/);
  assert.doesNotMatch(main, /from '.\/screens\//);
});

test('deep links select functional agents, stream items, and surfaces', () => {
  assert.match(main, /parseWorkstreamDeepLink/);
  assert.match(main, /serializeWorkstreamDeepLink/);
  assert.match(main, /selectedFunctionalAgentId/);
  assert.match(main, /selectedItemId/);
  assert.match(main, /selectedSurfaceId/);
  assert.match(main, /surfacePlacement: 'inline'/);
  assert.match(main, /window\.history\.pushState/);
});

test('workstream shell uses fixture contracts and capability action feedback', () => {
  assert.match(main, /FixtureWorkstreamApiClient/);
  assert.match(main, /canonicalSurfaceEnvelopes/);
  assert.match(main, /initialWorkstreamItems/);
  assert.match(main, /workstreamClient\.bootstrap\(\)/);
  assert.match(main, /handleSurfaceAction/);
  assert.match(main, /runCapabilityAction/);
  assert.match(main, /kind: 'surface-request'/);
  assert.match(main, /kind: 'surface'/);
  assert.match(main, /setRequestScrollTargetForCurrentSession\(surface\.surfaceId, functionalAgentId\)/);
  assert.match(main, /setRequestScrollTargetForCurrentSession\(targetSurface\.surfaceId, targetSurface\.ownerFunctionalAgentId\)/);
  assert.match(main, /buildCapabilityActionRequest/);
});

test('workstream entry wires fixture realtime client into stream state', () => {
  assert.match(main, /FixtureWorkstreamRealtimeClient/);
  assert.match(main, /realtimeClient\.connect/);
  assert.match(main, /applyWorkstreamRealtimeEvent/);
  assert.match(main, /realtimeStatusLabel/);
  assert.match(main, /selectedContextId: bootstrap\.me\.selectedAuthContext\.selectedContextId/);
});

test('mode switching uses root data attributes and persists preference', () => {
  assert.match(main, /type ModePreference = 'light' \| 'dark' \| 'system'/);
  assert.match(main, /root\.dataset\.modePreference = mode/);
  assert.match(main, /root\.dataset\.mode = mode === 'system'/);
  assert.match(main, /prefers-color-scheme: dark/);
  assert.match(main, /window\.localStorage\.setItem\(modeStorageKey, mode\)/);
});

test('atlas ops supervisory console tokens include sans UI fonts, mono technical fonts, and warm palettes', () => {
  assert.match(tokens, /--font-sans: Inter/);
  assert.match(tokens, /--font-mono: "JetBrains Mono"/);
  assert.match(tokens, /\[data-mode="light"\]/);
  assert.match(tokens, /\[data-mode="dark"\]/);
  assert.match(tokens, /--color-primary: #ff9f1c/);
  assert.match(tokens, /--color-ai: #c75a6f/);
  assert.match(tokens, /--color-bg: #050a08/);
  assert.match(tokens, /--color-ai: #d65f73/);
});

test('focus, skip link, reduced motion, and responsive shell rules are present', () => {
  assert.match(base, /:focus-visible/);
  assert.match(base, /\.skip-link/);
  assert.match(base, /prefers-reduced-motion/);
  assert.match(layout, /@media \(max-width: 960px\)/);
  assert.match(layout, /@media \(max-width: 640px\)/);
  assert.match(layout, /\.sidebar\.open/);
  assert.match(layout, /\.mobile-menu-button/);
  assert.match(tokens, /--workstream-composer-scroll-clearance: 22rem/);
  assert.match(layout, /padding-bottom: calc\(var\(--workstream-composer-scroll-clearance\)/);
  assert.match(layout, /scroll-padding-bottom: calc\(var\(--workstream-composer-scroll-clearance\)/);
  assert.match(components, /scroll-margin-bottom: calc\(var\(--workstream-composer-scroll-clearance\)/);
});

test('prompt and markdown response surfaces stay text-only and visually distinct', () => {
  assert.match(components, /\.prompt-input-surface p/);
  assert.match(components, /white-space: pre-wrap/);
  assert.match(components, /max-width: min\(46rem, 62%\)/);
  assert.match(components, /justify-self: end/);
  assert.match(components, /background: var\(--color-request-surface\)/);
  assert.match(components, /action-request-surface/);
  assert.match(components, /\.markdown-response-only/);
  assert.match(components, /background: var\(--color-surface\)/);
});

test('status and command-strip components use tokenized semantic classes', () => {
  assert.match(components, /\.command-strip/);
  assert.match(components, /var\(--color-ai\)/);
  assert.match(components, /\.status-pill\.success/);
  assert.match(components, /\.status-pill\.warning/);
  assert.match(components, /\.status-pill\.danger/);
  assert.match(main, /Ready .* workstream shell/);
  assert.match(main, /Pending .* fixture client/);
  assert.match(main, /Guarded .* backend authority/);
});

test('frontend source of record exists separately from generated static resources', () => {
  assert.ok(existsSync(new URL('../package.json', import.meta.url)));
  assert.ok(existsSync(new URL('./styles/tokens.css', import.meta.url)));
});

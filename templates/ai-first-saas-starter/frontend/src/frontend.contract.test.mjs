import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');
const tokens = readFileSync(new URL('./styles/tokens.css', import.meta.url), 'utf8');
const base = readFileSync(new URL('./styles/base.css', import.meta.url), 'utf8');
const layout = readFileSync(new URL('./styles/layout.css', import.meta.url), 'utf8');
const components = readFileSync(new URL('./styles/components.css', import.meta.url), 'utf8');
const surfaceRenderer = readFileSync(new URL('./workstream/surfaces/SurfaceRenderer.tsx', import.meta.url), 'utf8');
const detailEditSurface = readFileSync(new URL('./workstream/surfaces/DetailEditSurface.tsx', import.meta.url), 'utf8');

test('frontend entry composes the canonical workstream shell instead of route pages', () => {
  assert.match(main, /<WorkstreamShell/);
  assert.match(main, /<WorkstreamStream/);
  assert.doesNotMatch(main, /meTenantAdmin/);
  assert.doesNotMatch(main, /initialWorkstreamItems/);
  assert.doesNotMatch(main, /canonicalSurfaceEnvelopes/);
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

test('workstream shell gates fixture contracts and uses capability action feedback', () => {
  assert.doesNotMatch(main, /FixtureWorkstreamApiClient/);
  assert.doesNotMatch(main, /fixtureWorkstreamEnabled/);
  assert.doesNotMatch(main, /VITE_ENABLE_FIXTURE_WORKSTREAM/);
  assert.doesNotMatch(main, /useFixtureWorkstream/);
  assert.doesNotMatch(main, /const ready = bootstrap\.status === 'ready' \? bootstrap : \{ status: 'ready'/);
  assert.match(main, /workstreamClient\.bootstrap\(\)/);
  assert.match(main, /handleSurfaceAction/);
  assert.match(main, /runCapabilityAction/);
  assert.match(main, /kind: 'surface-request'/);
  assert.match(main, /kind: 'surface'/);
  assert.match(main, /setRequestScrollTargetForCurrentSession\(requestItem\.itemId, functionalAgentId\)/);
  assert.match(main, /setRequestScrollTargetForCurrentSession\(actionRequestItem\.itemId, actionRequestItem\.functionalAgentId\)/);
  assert.match(main, /buildCapabilityActionRequest/);
});

test('workstream entry gates fixture realtime client and wires stream state', () => {
  assert.doesNotMatch(main, /FixtureWorkstreamRealtimeClient/);
  assert.match(main, /realtimeClient\.connect/);
  assert.match(main, /applyWorkstreamRealtimeEvent/);
  assert.match(main, /realtimeStatusLabel/);
  assert.match(main, /selectedContextId: bootstrap\.me\.selectedAuthContext\.selectedContextId/);
});

test('named theme selection uses root data-theme, live detail-edit preview, and backend persistence', () => {
  assert.match(main, /type ThemePreference = 'aurora-light' \| 'cobalt-light' \| 'obsidian-dark' \| 'midnight-dark'/);
  assert.match(main, /root\.dataset\.theme = themeId/);
  assert.match(main, /window\.localStorage\.setItem\(themeStorageKey, themeId\)/);
  assert.match(main, /bootstrap\.me\.settings\.preferredThemeId/);
  assert.match(main, /function handleSurfaceFieldValueChange\(fieldId: string, value: string\)/);
  assert.match(main, /if \(fieldId !== 'preferredThemeId'\) return;\n    const previewThemeId = normalizeThemeId\(value\);\n    if \(previewThemeId\) setThemeId\(previewThemeId\);/);
  assert.match(surfaceRenderer, /onFieldValueChange=\{onFieldValueChange\}/);
  assert.match(detailEditSurface, /onFieldValueChange\?: \(fieldId: string, value: string, surfaceId: string\) => void;/);
  assert.match(detailEditSurface, /onChange=\{\(event\) => updateFieldValue\(field\.fieldId, event\.currentTarget\.value\)\}/);
  assert.match(main, /const selectedThemeId = input && typeof input === 'object' && 'preferredThemeId' in input/);
  assert.doesNotMatch(main, /prefers-color-scheme: dark/);
});

test('AI-first workstream enterprise tokens include named themes and semantic aliases', () => {
  assert.match(tokens, /--font-sans: Inter/);
  assert.match(tokens, /--font-mono: "JetBrains Mono"/);
  assert.match(tokens, /\[data-theme="aurora-light"\]/);
  assert.match(tokens, /\[data-theme="cobalt-light"\]/);
  assert.match(tokens, /\[data-theme="obsidian-dark"\]/);
  assert.match(tokens, /\[data-theme="midnight-dark"\]/);
  assert.match(tokens, /--color-canvas: #f7f8fb/);
  assert.match(tokens, /--color-accent: #4f46e5/);
  assert.match(tokens, /--color-accent: #38bdf8/);
  assert.match(tokens, /--color-primary: var\(--color-accent\)/);
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
  assert.match(main, /Pending .* backend configuration/);
  assert.match(main, /Guarded .* backend authority/);
});

test('frontend source of record exists separately from generated static resources', () => {
  assert.ok(existsSync(new URL('../package.json', import.meta.url)));
  assert.ok(existsSync(new URL('./styles/tokens.css', import.meta.url)));
});

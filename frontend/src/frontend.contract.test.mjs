import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');
const tokens = readFileSync(new URL('./styles/tokens.css', import.meta.url), 'utf8');
const base = readFileSync(new URL('./styles/base.css', import.meta.url), 'utf8');
const layout = readFileSync(new URL('./styles/layout.css', import.meta.url), 'utf8');
const components = readFileSync(new URL('./styles/components.css', import.meta.url), 'utf8');

test('slice 1 foundation remains present while slice 2 adds route shells', () => {
  assert.match(main, /data-mode-preference/);
  assert.match(main, /function AppShell/);
  assert.match(main, /function SidebarNav/);
  assert.match(main, /function RouteShell/);
});

test('app shell declares planned seed app routes', () => {
  assert.match(main, /id: 'briefing'/);
  assert.match(main, /id: 'goals'/);
  assert.match(main, /id: 'decisions'/);
  assert.match(main, /id: 'governance'/);
  assert.match(main, /id: 'audit'/);
  assert.match(main, /id: 'admin'/);
  assert.match(main, /id: 'profile'/);
  assert.match(main, /path: '\/ui\/briefing'/);
  assert.match(main, /path: '\/ui\/governance\/policies'/);
});

test('shell includes tenant, notifications, user, and responsive nav seams', () => {
  assert.match(main, /function TenantSwitcher/);
  assert.match(main, /function NotificationsButton/);
  assert.match(main, /function UserMenu/);
  assert.match(main, /aria-expanded=\{navOpen\}/);
  assert.match(main, /aria-controls="sidebar-navigation"/);
  assert.match(main, /nav-backdrop/);
});

test('mode switching uses root data attributes and persists preference', () => {
  assert.match(main, /type ModePreference = 'light' \| 'dark' \| 'system'/);
  assert.match(main, /root\.dataset\.modePreference = mode/);
  assert.match(main, /root\.dataset\.mode = mode === 'system'/);
  assert.match(main, /prefers-color-scheme: dark/);
  assert.match(main, /window\.localStorage\.setItem\(modeStorageKey, mode\)/);
});

test('atlas ops supervisory console tokens include light and dark palettes', () => {
  assert.match(tokens, /--font-sans: Inter/);
  assert.match(tokens, /\[data-mode="light"\]/);
  assert.match(tokens, /\[data-mode="dark"\]/);
  assert.match(tokens, /--color-primary: #2563eb/);
  assert.match(tokens, /--color-ai: #7c3aed/);
  assert.match(tokens, /--color-bg: #07111f/);
  assert.match(tokens, /--color-ai: #a855f7/);
});

test('focus, skip link, reduced motion, and responsive rules are present', () => {
  assert.match(base, /:focus-visible/);
  assert.match(base, /\.skip-link/);
  assert.match(base, /prefers-reduced-motion/);
  assert.match(layout, /@media \(max-width: 960px\)/);
  assert.match(layout, /@media \(max-width: 640px\)/);
  assert.match(layout, /\.sidebar\.open/);
  assert.match(layout, /\.mobile-menu-button/);
});

test('status and command-strip components use tokenized semantic classes', () => {
  assert.match(components, /\.command-strip/);
  assert.match(components, /var\(--color-ai\)/);
  assert.match(components, /\.status-pill\.success/);
  assert.match(components, /\.status-pill\.warning/);
  assert.match(components, /\.status-pill\.danger/);
  assert.match(main, /Ready .* shell route/);
  assert.match(main, /Pending .* fixture client/);
  assert.match(main, /Guarded .* backend authority/);
});

test('frontend source of record exists separately from generated static resources', () => {
  assert.ok(existsSync(new URL('../package.json', import.meta.url)));
  assert.ok(existsSync(new URL('./styles/tokens.css', import.meta.url)));
});

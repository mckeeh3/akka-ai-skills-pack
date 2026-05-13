import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');
const tokens = readFileSync(new URL('./styles/tokens.css', import.meta.url), 'utf8');
const base = readFileSync(new URL('./styles/base.css', import.meta.url), 'utf8');
const layout = readFileSync(new URL('./styles/layout.css', import.meta.url), 'utf8');
const components = readFileSync(new URL('./styles/components.css', import.meta.url), 'utf8');

test('slice 1 renders seed design foundation without product screens', () => {
  assert.match(main, /Localized frontend slice 1/);
  assert.match(main, /This page proves light, dark, and system mode behavior/);
  assert.match(main, /without implementing product screens yet/);
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
});

test('status and command-strip components use tokenized semantic classes', () => {
  assert.match(components, /\.command-strip/);
  assert.match(components, /var\(--color-ai\)/);
  assert.match(components, /\.status-pill\.success/);
  assert.match(components, /\.status-pill\.warning/);
  assert.match(components, /\.status-pill\.danger/);
  assert.match(main, /Success · text label/);
  assert.match(main, /Warning · text label/);
  assert.match(main, /Risk · text label/);
});

test('frontend source of record exists separately from generated static resources', () => {
  assert.ok(existsSync(new URL('../package.json', import.meta.url)));
  assert.ok(existsSync(new URL('./styles/tokens.css', import.meta.url)));
});

import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const componentsCss = readFileSync(new URL('./styles/components.css', import.meta.url), 'utf8');
const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');

const files = [
  'Button.tsx',
  'Card.tsx',
  'CommandStrip.tsx',
  'DataState.tsx',
  'Drawer.tsx',
  'FormField.tsx',
  'IconChip.tsx',
  'KpiCard.tsx',
  'Modal.tsx',
  'PageHeader.tsx',
  'StatusPill.tsx',
  'index.ts'
];

test('slice 4 design-system primitives exist', () => {
  for (const file of files) {
    assert.ok(existsSync(new URL(`./design-system/${file}`, import.meta.url)), `${file} should exist`);
  }
});

test('primitives use semantic classes backed by tokenized CSS', () => {
  assert.match(componentsCss, /\.ds-button/);
  assert.match(componentsCss, /\.ds-card/);
  assert.match(componentsCss, /\.status-pill\.success/);
  assert.match(componentsCss, /\.status-pill\.warning/);
  assert.match(componentsCss, /\.status-pill\.danger/);
  assert.match(componentsCss, /\.form-field/);
  assert.match(componentsCss, /\.data-state/);
  assert.match(componentsCss, /\.modal-panel/);
  assert.match(componentsCss, /\.drawer-panel/);
  assert.match(componentsCss, /var\(--color-/);
});

test('app shell now consumes workstream primitives while design-system primitives remain reusable', () => {
  const shell = readFileSync(new URL('./workstream/shell/WorkstreamShell.tsx', import.meta.url), 'utf8');
  const surface = readFileSync(new URL('./workstream/surfaces/DashboardSurface.tsx', import.meta.url), 'utf8');

  assert.match(main, /<WorkstreamShell/);
  assert.match(shell, /FunctionalAgentRail/);
  assert.match(shell, /ContextAuthorityBar/);
  assert.match(shell, /WorkstreamComposer/);
  assert.match(surface, /ds-card|status-pill/);
  assert.doesNotMatch(main, /PageHeader as DsPageHeader/);
});

test('form and dialog primitives expose accessibility attributes', () => {
  const form = readFileSync(new URL('./design-system/FormField.tsx', import.meta.url), 'utf8');
  const modal = readFileSync(new URL('./design-system/Modal.tsx', import.meta.url), 'utf8');
  const drawer = readFileSync(new URL('./design-system/Drawer.tsx', import.meta.url), 'utf8');
  assert.match(form, /htmlFor=\{id\}/);
  assert.match(form, /aria-invalid/);
  assert.match(form, /aria-describedby/);
  assert.match(modal, /role="dialog"/);
  assert.match(modal, /aria-modal="true"/);
  assert.match(drawer, /role="dialog"/);
  assert.match(drawer, /aria-modal="true"/);
});

import assert from 'node:assert/strict';
import { readFileSync, existsSync } from 'node:fs';
import test from 'node:test';

const main = readFileSync(new URL('./main.tsx', import.meta.url), 'utf8');
const envExample = readFileSync(new URL('../.env.example', import.meta.url), 'utf8');

test('API client sends bearer token to same-origin /api/me', () => {
  assert.match(main, /fetch\('\/api\/me'/);
  assert.match(main, /Authorization: `Bearer \$\{token\}`/);
});

test('shell declares backend authorization boundary in user-facing copy', () => {
  assert.match(main, /Frontend role-aware navigation is UX only/);
  assert.match(main, /Backend APIs still enforce authorization/);
});

test('frontend env example contains only public Vite variables and no backend secrets', () => {
  assert.match(envExample, /VITE_WORKOS_CLIENT_ID/);
  assert.match(envExample, /Never add backend secrets here/);
  assert.doesNotMatch(envExample, /^WORKOS_API_KEY=/m);
  assert.doesNotMatch(envExample, /^RESEND_API_KEY=/m);
  assert.doesNotMatch(envExample, /^ADMIN_USERS=/m);
});

test('source includes selected Northpeak style tokens and responsive/focus rules', () => {
  const css = readFileSync(new URL('./styles.css', import.meta.url), 'utf8');
  assert.match(css, /--color-primary: #2563eb/);
  assert.match(css, /prefers-color-scheme: dark/);
  assert.match(css, /:focus-visible/);
  assert.match(css, /@media \(max-width: 640px\)/);
});

test('frontend source of record exists separately from generated static resources', () => {
  assert.ok(existsSync(new URL('../package.json', import.meta.url)));
});

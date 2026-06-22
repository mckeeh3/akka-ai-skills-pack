import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const surfaceTypes = read('./workstream/types/surfaces.ts');
const prefillHelper = read('./workstream/surfaces/prefill.ts');
const organizationSurface = read('./workstream/surfaces/OrganizationAdminSurface.tsx');
const userAdminTaskSurface = read('./workstream/surfaces/UserAdminTaskSurface.tsx');
const workstreamService = read('../../src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java');
const surfaceIntentRouter = read('../../src/main/java/ai/first/application/coreapp/workstream/DefaultSurfaceIntentRouter.java');

test('routed surface prefill stays browser-safe and review-only', () => {
  assert.match(surfaceTypes, /BrowserSafeSurfacePrefillValue = string \| number \| boolean \| null \| undefined/);
  assert.match(surfaceTypes, /export type BrowserSafeSurfacePrefill = Record<string, BrowserSafeSurfacePrefillValue>/);
  assert.match(surfaceTypes, /export type RoutedSurfaceIntentRoute = \{/);
  assert.match(surfaceTypes, /prefill\?: BrowserSafeSurfacePrefill/);
  assert.match(prefillHelper, /normalizeBrowserSafePrefillValue/);
  assert.match(prefillHelper, /typeof value === 'string'/);
  assert.match(prefillHelper, /typeof value === 'number' \|\| typeof value === 'boolean'/);
  assert.match(prefillHelper, /routedPrefillReviewCopy = 'Prefilled from your request\. Review or edit these fields, then submit the form; nothing is created automatically\.'/);
  assert.match(workstreamService, /surface\.data\(\)\.put\("prefill", route\.prefill\(\)\)/);
  assert.match(surfaceIntentRouter, /surface_create_prefill/);
});

test('Organization Create renders routed prefill without bypassing editable validation or submit', () => {
  assert.match(organizationSurface, /browserSafePrefillString\(envelope\.data, 'organizationName'\)/);
  assert.match(organizationSurface, /hasRoutedPrefill\(envelope\.data\)/);
  assert.match(organizationSurface, /routedPrefillMessage\(envelope\.data\)/);
  assert.match(organizationSurface, /value=\{createName\} onChange=\{\(event\) => setCreateName\(event\.currentTarget\.value\)\} required/);
  assert.match(organizationSurface, /if \(!createName\.trim\(\)\) return setValidationError\('Organization name is required\.'\)/);
  assert.match(organizationSurface, /type="submit">\{form\?\.submitLabel \?\? 'Create Organization'\}/);
  assert.match(organizationSurface, /idempotencyKey\('create', createName\)/);
  assert.doesNotMatch(organizationSurface, /useEffect\(\(\) => .*run\(|onChange=\{\(event\) => run\(|Authorization:\s*Bearer/);
});

test('invitation create renders routed prefill without bypassing editable validation or submit', () => {
  assert.match(userAdminTaskSurface, /browserSafePrefillString\(envelope\.data, 'email'\)/);
  assert.match(userAdminTaskSurface, /browserSafePrefillString\(envelope\.data, 'displayName'\)/);
  assert.match(userAdminTaskSurface, /hasRoutedPrefill\(envelope\.data\)/);
  assert.match(userAdminTaskSurface, /routedPrefillMessage\(envelope\.data\)/);
  assert.match(userAdminTaskSurface, /value=\{email\} onChange=\{\(event\) => setEmail\(event\.currentTarget\.value\)\} required/);
  assert.match(userAdminTaskSurface, /if \(!email\.trim\(\) \|\| !email\.includes\('@'\)\) return setError\('Enter a valid email address before creating an invitation\.'\)/);
  assert.match(userAdminTaskSurface, /type="submit" disabled=\{!action \|\| Boolean\(action\.disabled\) \|\| roleOptions\.length === 0\}>Create invitation/);
  assert.match(userAdminTaskSurface, /idempotencyKey\('invite', email\)/);
  assert.doesNotMatch(userAdminTaskSurface, /useEffect\(\(\) => .*runAction|onChange=\{\(event\) => runAction|Authorization:\s*Bearer|invite-token|tokenHash|RESEND_API_KEY|sk-secret|api_key=/);
});

import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const types = read('./api/types.ts');
const apiClient = read('./api/ApiClient.ts');
const httpApiClient = read('./api/HttpApiClient.ts');
const fixtureApiClient = read('./__tests__/fixtures/api/FixtureApiClient.ts');
const surfaces = read('./__tests__/fixtures/workstream/surfaces.ts');
const fixtureWorkstreamApi = read('./__tests__/fixtures/api/FixtureWorkstreamApiClient.ts');
const renderer = read('./workstream/surfaces/SurfaceRenderer.tsx');
const organizationSurface = read('./workstream/surfaces/OrganizationAdminSurface.tsx');
const main = read('./main.tsx');
const css = read('./styles/components.css');
const frontendApiContracts = read('../../app-description/55-ui/frontend-api-contracts.md');

test('Organization Admin has typed browser-safe API clients aligned with protected backend routes', () => {
  for (const dto of ['OrganizationSummary', 'OrganizationListPayload', 'OrganizationDetailPayload', 'OrganizationActionResponse', 'OrganizationCreateRequest', 'OrganizationRenameRequest', 'OrganizationLifecycleRequest']) {
    assert.match(types, new RegExp(`export type ${dto}`));
  }
  for (const method of ['listOrganizations', 'getOrganization', 'createOrganization', 'renameOrganization', 'suspendOrganization', 'reactivateOrganization']) {
    assert.match(apiClient, new RegExp(`${method}\\(`));
    assert.match(fixtureApiClient, new RegExp(`${method}:`));
  }
  assert.match(httpApiClient, /\/api\/admin\/organizations/);
  assert.match(httpApiClient, /\/organizations\/\$\{encodeURIComponent\(organizationId\)\}\/rename/);
  assert.match(httpApiClient, /\/organizations\/\$\{encodeURIComponent\(organizationId\)\}\/suspend/);
  assert.match(httpApiClient, /\/organizations\/\$\{encodeURIComponent\(organizationId\)\}\/reactivate/);
  assert.match(frontendApiContracts, /SaaS Owner Organization Admin contracts/);
});

test('Organization Admin workstream surface preserves SaaS Owner authority and boundary copy', () => {
  assert.match(surfaces, /surface-user-admin-organization-admin/);
  assert.match(surfaces, /user_admin\.organization_admin\.v1/);
  assert.match(surfaces, /Open Organization Admin/);
  for (const capability of ['saas_owner.organization.list', 'saas_owner.organization.read', 'saas_owner.organization.create', 'saas_owner.organization.rename', 'saas_owner.organization.suspend', 'saas_owner.organization.reactivate']) {
    assert.match(surfaces, new RegExp(capability.replace(/\./g, '\\.')));
  }
  for (const action of ['action-organization-list', 'action-organization-read', 'action-organization-create', 'action-organization-rename', 'action-organization-suspend', 'action-organization-reactivate']) {
    assert.match(surfaces, new RegExp(action));
    assert.match(organizationSurface, new RegExp(action));
  }
  assert.match(surfaces, /tenant\/customer application-data access/);
  assert.match(surfaces, /support access/);
  assert.match(surfaces, /billing-derived authority/);
  assert.match(surfaces, /hidden-counts-redacted/);
  assert.match(fixtureWorkstreamApi, /displayOrganizationAdminActionResult/);
  assert.match(main, /isOrganizationAdminRuntimeAction/);
  assert.match(main, /apiClient\.admin\.listOrganizations/);
  assert.match(main, /apiClient\.admin\.createOrganization/);
  assert.match(main, /apiClient\.admin\.renameOrganization/);
  assert.match(main, /apiClient\.admin\.suspendOrganization/);
  assert.match(main, /apiClient\.admin\.reactivateOrganization/);
  assert.match(main, /protected \/api\/admin\/organizations path/);
});

test('Organization Admin renderer covers safe states, forms, and inaccessible role denials', () => {
  assert.match(renderer, /OrganizationAdminSurface/);
  assert.match(organizationSurface, /Organization Admin is unavailable for this selected context/);
  assert.match(organizationSurface, /Tenant Admin and Customer Admin contexts cannot gain SaaS Owner authority from browser state/);
  for (const state of ['loading', 'empty', 'success', 'validation-error', 'forbidden', 'not_found_or_redacted', 'no-op', 'conflict', 'stale', 'error']) {
    assert.match(surfaces, new RegExp(state));
  }
  for (const label of ['Search Organizations', 'Create Organization', 'Rename selected Organization', 'Suspend', 'Reactivate']) {
    assert.match(organizationSurface, new RegExp(label));
  }
  assert.match(organizationSurface, /idempotencyKey/);
  assert.match(organizationSurface, /Reason is required/);
  assert.match(css, /organization-admin-surface/);
  assert.match(css, /organization-admin-grid/);
});

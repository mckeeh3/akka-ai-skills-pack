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
const workstreamService = read('../../src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java');

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

test('Organization Admin workstream surface graph preserves SaaS Owner authority and boundary copy', () => {
  for (const surfaceId of ['surface-user-admin-organization-directory', 'surface-user-admin-organization-detail', 'surface-user-admin-organization-create', 'surface-user-admin-organization-rename', 'surface-user-admin-organization-suspend-confirmation', 'surface-user-admin-organization-reactivate-confirmation']) {
    assert.match(surfaces, new RegExp(surfaceId));
  }
  for (const contract of ['user_admin.organization_directory.v1', 'user_admin.organization_detail.v1', 'user_admin.organization_create.v1', 'user_admin.organization_rename.v1', 'user_admin.organization_suspend_confirmation.v1', 'user_admin.organization_reactivate_confirmation.v1']) {
    assert.match(surfaces, new RegExp(contract.replace(/\./g, '\\.')));
  }
  assert.match(surfaces, /Open Organization Admin/);
  for (const capability of ['saas_owner.organization.list', 'saas_owner.organization.read', 'saas_owner.organization.create', 'saas_owner.organization.rename', 'saas_owner.organization.suspend', 'saas_owner.organization.reactivate']) {
    assert.match(surfaces, new RegExp(capability.replace(/\./g, '\\.')));
  }
  for (const action of ['action-user-admin-show-organizations', 'action-organization-list', 'action-organization-read', 'action-organization-create', 'action-organization-rename', 'action-organization-suspend', 'action-organization-reactivate', 'action-open-organization-create', 'action-open-organization-rename', 'action-open-organization-suspend', 'action-open-organization-reactivate']) {
    assert.match(surfaces, new RegExp(action));
    assert.match(organizationSurface, new RegExp(action));
  }
  assert.match(surfaces, /tenant\/customer application-data access/);
  assert.match(surfaces, /support access/);
  assert.match(surfaces, /billing-derived authority/);
  assert.match(surfaces, /hidden-counts-redacted/);
  assert.match(surfaces, /branchReturnActionId: 'action-user-admin-show-organizations'/);
  assert.match(surfaces, /browserToolId: 'user-admin\.show-organizations'/);
  assert.match(surfaces, /branchRootSurfaceId: 'surface-user-admin-organization-directory'/);
  assert.match(fixtureWorkstreamApi, /displayOrganizationAdminActionResult/);
  assert.match(main, /workstreamClient\.runCapabilityAction/);
  assert.doesNotMatch(main, /isOrganizationAdminRuntimeAction|apiClient\.admin\.listOrganizations|apiClient\.admin\.createOrganization|protected \/api\/admin\/organizations path|syncOrganizationDirectorySurfaces/);
  assert.match(workstreamService, /action-organization-list/);
  assert.match(workstreamService, /runOrganizationLifecycleAction/);
  assert.match(workstreamService, /listOrganizations\(actor, query, status, correlationId\)/);
  assert.match(workstreamService, /Organization Directory refreshed through the canonical workstream action path/);
});

test('Organization Admin frontend contract proves dashboard-to-organization-branch traversal and safe payload boundaries', () => {
  assert.match(surfaces, /displayOrganizationAdmin[\s\S]*action-display-organization-admin[\s\S]*surface-user-admin-organization-directory/);
  assert.match(surfaces, /showOrganizations[\s\S]*action-user-admin-show-organizations[\s\S]*surface-user-admin-organization-directory/);
  assert.match(surfaces, /userAdminOrganizationDirectorySurface[\s\S]*readOrganization/);
  assert.match(surfaces, /readOrganization[\s\S]*action-organization-read[\s\S]*surface-user-admin-organization-detail/);
  for (const descendant of ['userAdminOrganizationDetailSurface', 'userAdminOrganizationCreateSurface', 'userAdminOrganizationRenameSurface', 'userAdminOrganizationSuspendSurface', 'userAdminOrganizationReactivateSurface']) {
    assert.match(surfaces, new RegExp(`${descendant}[\\s\\S]*showOrganizations`));
  }
  assert.match(surfaces, /branchReturnActionId: 'action-user-admin-show-organizations'/);
  assert.match(organizationSurface, /OrganizationBranchReturn/);
  assert.match(organizationSurface, /organizationBranchReturnInput/);
  assert.match(organizationSurface, /Correlation \$\{data\.lastResult\.correlationId\}/);
  assert.match(organizationSurface, /provider secrets/);
  assert.match(organizationSurface, /Tenant Admin and Customer Admin contexts cannot gain SaaS Owner authority from browser state/);
  assert.doesNotMatch(`${surfaces}\n${organizationSurface}\n${main}\n${apiClient}\n${httpApiClient}`, /Authorization:\s*Bearer|RESEND_API_KEY|sk-secret|api_key=|tenant\/customer application data/);
});

test('Organization Admin renderer covers safe states, forms, and inaccessible role denials', () => {
  assert.match(renderer, /OrganizationAdminSurface/);
  assert.match(organizationSurface, /Organization Admin is unavailable for this selected context/);
  assert.match(organizationSurface, /Tenant Admin and Customer Admin contexts cannot gain SaaS Owner authority from browser state/);
  for (const state of ['loading', 'empty', 'success', 'validation-error', 'forbidden', 'not_found_or_redacted', 'no-op', 'conflict', 'stale', 'error']) {
    assert.match(surfaces, new RegExp(state));
  }
  for (const label of ['Search Organizations', 'Create Organization', 'Rename selected Organization', 'Suspend', 'Reactivate', 'Back to organizations']) {
    assert.match(organizationSurface, new RegExp(label));
  }
  assert.match(organizationSurface, /idempotencyKey/);
  assert.match(organizationSurface, /Reason is required/);
  assert.match(organizationSurface, /OrganizationBranchReturn/);
  assert.match(organizationSurface, /organizationBranchReturnInput/);
  assert.match(organizationSurface, /const renameAction = envelope\.actions\.find/);
  assert.match(organizationSurface, /runAction\(envelope, onAction, renameAction/);
  assert.match(organizationSurface, /Rename is unavailable because the backend did not include an authorized Organization rename task action/);
  assert.match(organizationSurface, /action-user-admin-show-organizations/);
  assert.doesNotMatch(main, /isOrganizationDirectoryAction/);
  assert.match(workstreamService, /action-user-admin-show-organizations/);
  assert.match(css, /organization-admin-surface/);
  assert.match(css, /organization-admin-grid/);
});

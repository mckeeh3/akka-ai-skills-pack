import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');
const readCoreResource = (path) => read(`../../src/main/resources/${path}`);

const surfaces = read('./__tests__/fixtures/workstream/surfaces.ts');
const workstream = read('./__tests__/fixtures/workstream/workstream.ts');
const systemMessage = read('./workstream/surfaces/SystemMessageSurface.tsx');
const seedManifest = readCoreResource('agent-behavior-seeds/starter-v1/manifest.properties');
const expertiseBundle = readCoreResource('agent-behavior-seeds/starter-v1/user-admin-agent-expertise.yaml');
const accessReviewSkill = readCoreResource('agent-behavior-seeds/starter-v1/access-review-triage.md');
const lastAdminReference = readCoreResource('agent-behavior-seeds/starter-v1/last-admin-protection-reference.md');

const seedResources = `${seedManifest}\n${expertiseBundle}`;

test('User Admin expertise bundle seed declares compact skill and reference manifests', () => {
  for (const marker of [
    'expertBundleId=user-admin-agent.expertise',
    'skillManifestId=manifest-user-admin',
    'referenceManifestId=reference-manifest-user-admin',
    'toolBoundaryId=tool-boundary-user-admin',
    'expertBundleResource=agent-behavior-seeds/starter-v1/user-admin-agent-expertise.yaml',
    'skills=skill-ua-access-review-triage,skill-ua-admin-risk-scoring,skill-ua-invitation-drafting,skill-ua-role-recommendation,skill-ua-support-access-review,skill-ua-audit-summary',
    'references=ref-ua-tenant-role-catalog,ref-ua-invitation-onboarding-policy,ref-ua-access-review-policy,ref-ua-support-access-procedure,ref-ua-last-admin-protection,ref-ua-admin-audit-redaction-guide'
  ]) {
    assert.match(seedManifest, new RegExp(marker.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')));
  }
  assert.match(expertiseBundle, /includeFullBodies: false/);
  assert.match(expertiseBundle, /skillSection: Available internal skills/);
  assert.match(expertiseBundle, /referenceSection: Available workstream references/);
});

test('User Admin expertise fixtures expose manifest display without full bodies', () => {
  for (const marker of [
    'expertiseManifest',
    'user-admin-agent.expertise',
    'manifest-user-admin',
    'reference-manifest-user-admin',
    'ua.access-review-triage.v1',
    'ua.invitation-drafting.v1',
    'ua.audit-summary.v1',
    'ua.tenant-role-catalog.v1',
    'ua.access-review-policy.v1',
    'ua.last-admin-protection.v1'
  ]) {
    assert.match(surfaces, new RegExp(marker.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')));
  }
  assert.match(surfaces, /compactManifestOnly: true/);
  assert.doesNotMatch(surfaces, /Use before recommending access-review, membership, role, support-access, invitation, or offboarding changes/);
  assert.match(accessReviewSkill, /Use before recommending access-review, membership, role, support-access, invitation, or offboarding changes/);
});

test('User Admin expertise contract covers authorized skill and reference loads', () => {
  assert.match(surfaces, /readSkill\(skillId\)/);
  assert.match(surfaces, /targetId: 'ua\.access-review-triage\.v1'/);
  assert.match(surfaces, /decision: 'allowed'/);
  assert.match(surfaces, /SkillLoadTrace/);
  assert.match(surfaces, /readReferenceDoc\(referenceId\)/);
  assert.match(surfaces, /targetId: 'ua\.last-admin-protection\.v1'/);
  assert.match(surfaces, /ReferenceLoadTrace/);
  assert.match(lastAdminReference, /Last Admin Protection Rule reference/);
});

test('User Admin expertise contract covers unassigned and tool-boundary denials', () => {
  for (const marker of [
    'unassigned-skill',
    'unassigned skill denied',
    'unassigned-reference',
    'unassigned reference denied',
    'missing read_reference tool-boundary grant returns TOOL_BOUNDARY_DENIED',
    'SkillDocument and ReferenceDocument text cannot grant roles, tenant scope, governed-tool access, approval rights, or backend capabilities'
  ]) {
    assert.match(surfaces, new RegExp(marker.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')));
  }
  assert.match(seedResources, /missing read_skill or read_reference tool-boundary grant/);
  assert.match(seedResources, /text claiming new roles, tenant scope, governed-tool access, approval rights, or backend capabilities/);
});

test('UserAdminAgent guidance fixtures cover read-only evidence and provider-blocked system_message recovery', () => {
  for (const marker of [
    'UserAdminAgent',
    'userAdminEvidence.read',
    'readSkill',
    'readReferenceDoc',
    'system_message',
    'blocked_provider_or_runtime',
    'no direct mutation of invitations, memberships, roles, capabilities, authorization state, or provider configuration',
    'Provider secrets, raw JWTs, hidden prompts, invitation tokens, and unauthorized tenant/customer evidence are omitted',
    'trace-useradmin-agent-provider-blocked',
    'USERADMIN_AGENT_TURN'
  ]) {
    assert.match(surfaces, new RegExp(marker.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')));
  }
  assert.match(systemMessage, /Recovery steps/);
  assert.match(systemMessage, /Trace links/);
  assert.match(systemMessage, /UserAdminAgent guidance is read-only/);
});

test('User Admin expertise traces are visible from seed and workstream fixtures', () => {
  for (const trace of ['PromptAssemblyTrace', 'SkillLoadTrace', 'ReferenceLoadTrace', 'AgentWorkTrace', 'AdminAuditEvent']) {
    assert.match(seedResources, new RegExp(trace));
    assert.match(surfaces, new RegExp(trace));
  }
  assert.match(workstream, /trace-surface-v0-user-admin-markdown/);
  assert.match(surfaces, /trace-user-admin-dashboard/);
  assert.match(surfaces, /trace-user-admin-detail/);
});

import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');
const readBackend = (path) => read(`../../${path}`);

const backendWorkstreamService = readBackend('src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java');
const agents = read('./__tests__/fixtures/workstream/agents.ts');
const me = read('./__tests__/fixtures/workstream/me.ts');
const surfaces = read('./__tests__/fixtures/workstream/surfaces.ts');
const apiTypes = read('./api/types.ts');
const surfaceTypes = read('./workstream/types/surfaces.ts');
const apiClient = read('./__tests__/fixtures/api/FixtureWorkstreamApiClient.ts');
const renderer = read('./workstream/surfaces/SurfaceRenderer.tsx');
const agentAdminDocEditingSurface = read('./workstream/surfaces/AgentAdminDocEditingSurface.tsx');

const currentInventoryBlock = surfaces.slice(
  surfaces.indexOf('export const currentAgentAdminSurfaceEnvelopes = ['),
  surfaces.indexOf('] as const;', surfaces.indexOf('export const currentAgentAdminSurfaceEnvelopes = ['))
);

const canonicalBlock = surfaces.slice(
  surfaces.indexOf('export const canonicalSurfaceEnvelopes = ['),
  surfaces.indexOf('];', surfaces.indexOf('export const canonicalSurfaceEnvelopes = ['))
);

test('Agent Admin fixture rail is doc-editing oriented and SaaS-admin capability backed', () => {
  assert.match(agents, /label: 'Agent Admin'[\s\S]*defaultSurfaceType: 'blank'[\s\S]*defaultSurfaceId: 'surface-agent-admin-blank'/);
  for (const capability of [
    'agent_admin.submit_turn',
    'agent_admin.list_definitions',
    'agent_admin.get_definition',
    'agent_admin.get_prompt_version',
    'agent_admin.get_skill_version',
    'agent_admin.get_reference_version',
    'agent_admin.draft_behavior_change',
    'saas_owner.admin.manage',
    'audit.trace.read'
  ]) {
    assert.match(agents, new RegExp(capability.replaceAll('.', '\\.')));
    assert.match(me, new RegExp(capability.replaceAll('.', '\\.')));
  }
  for (const staleCapability of [
    'agent_admin.get_manifest',
    'agent_admin.get_tool_boundary',
    'agent_admin.simulate_tool_boundary',
    'agent_admin.list_seed_material',
    'agent_admin.reseed_missing_defaults',
    'agent_admin.prompt_risk_review.start'
  ]) {
    assert.doesNotMatch(agents, new RegExp(staleCapability.replaceAll('.', '\\.')));
    assert.doesNotMatch(me, new RegExp(staleCapability.replaceAll('.', '\\.')));
  }
});

test('current Agent Admin surface inventory matches app-description doc-editing surfaces', () => {
  const currentSurfaceIds = [
    'surface-agent-admin-blank',
    'surface-agent-admin-dashboard',
    'surface-agent-admin-agent-list',
    'surface-agent-admin-agent-detail',
    'surface-agent-admin-agent-profile-history',
    'surface-agent-admin-prompt-doc',
    'surface-agent-admin-skill-library',
    'surface-agent-admin-skill-doc',
    'surface-agent-admin-skill-assignment',
    'surface-agent-admin-tool-assignment',
    'surface-agent-admin-skill-reference-doc',
    'surface-agent-admin-edit-session',
    'surface-agent-admin-proposal-review',
    'surface-agent-admin-version-history',
    'surface-agent-admin-version-diff',
    'surface-agent-admin-create-skill',
    'surface-agent-admin-delete-skill-confirmation',
    'surface-agent-admin-create-reference-doc',
    'surface-agent-admin-delete-reference-doc-confirmation',
    'surface-agent-admin-runtime-traces',
    'surface-agent-admin-system-message'
  ];
  for (const surfaceId of currentSurfaceIds) {
    assert.match(surfaces, new RegExp(surfaceId));
  }
  for (const contract of [
    'agent_admin.blank.v1',
    'agent_admin.dashboard.v1',
    'agent_admin.agent_list.v1',
    'agent_admin.agent_detail.v1',
    'agent_admin.agent_profile_history.v1',
    'agent_admin.prompt_doc.v1',
    'agent_admin.skill_library.v1',
    'agent_admin.skill_doc.v1',
    'agent_admin.skill_assignment.v1',
    'agent_admin.tool_assignment.v1',
    'agent_admin.skill_reference_doc.v1',
    'agent_admin.edit_session.v1',
    'agent_admin.proposal_review.v1',
    'agent_admin.version_history.v1',
    'agent_admin.version_diff.v1',
    'agent_admin.create_skill.v1',
    'agent_admin.delete_skill_confirmation.v1',
    'agent_admin.create_reference_doc.v1',
    'agent_admin.delete_reference_doc_confirmation.v1',
    'agent_admin.runtime_traces.v1',
    'agent_admin.system_message.v1'
  ]) assert.match(surfaces, new RegExp(contract.replaceAll('.', '\\.')));
  assert.match(canonicalBlock, /currentAgentAdminSurfaceEnvelopes/);
  for (const staleSurfaceId of [
    'surface-agent-prompt-governance',
    'surface-agent-skill-manifest-diff',
    'surface-agent-tool-boundary-diff',
    'surface-agent-model-refs',
    'surface-agent-seed-material',
    'surface-agent-test-console',
    'surface-agent-admin-prompt-risk-review',
    'surface-agent-activation-confirmation',
    'surface-agent-rollback-confirmation'
  ]) assert.doesNotMatch(currentInventoryBlock, new RegExp(staleSurfaceId));
});

test('Agent Admin fixtures model document editing, versions, proposal lifecycle, traces, and denial states', () => {
  for (const marker of [
    'thingsYouCanDo',
    'recentlyChangedAgents',
    'thingsNeedAttention: []',
    'agentName',
    'workstreamDomain',
    'profile: agentAdminBehaviorProfile',
    'profileHistory: agentAdminBehaviorProfileHistoryRows',
    'prompt: agentAdminPromptDoc',
    'skills: [agentAdminSkillDoc]',
    'referenceDocs: [agentAdminReferenceDoc]',
    'allowedGeneratedTools',
    'contentBody',
    'editInputEnabled: true',
    'Historical version: read-only.',
    'diffRule',
    'selected version N is compared only with N-1',
    'Restore this version',
    'restoreCreatesProposal: true',
    'Skill removal defaults to deprecation',
    'Reference removal follows lifecycle policy',
    'readSkill',
    'readReferenceDoc',
    'Trace rows do not include full prompt, skill, or reference content.',
    'SaaS admin authority required',
    'warningsAdvisoryOnly: true',
    'saveCreatesNonActiveProposal: true',
    'activationSurfaceId'
  ]) assert.match(surfaces, new RegExp(marker.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')));
  assert.match(surfaces, /inputs: \['skill name', 'purpose\/description', 'free-form initial content request'\]/);
  assert.match(surfaces, /inputs: \['reference doc name', 'short description', 'free-form initial content request'\]/);
});

test('Agent Admin interactive doc-editing surfaces are routed to purpose-built renderers', () => {
  assert.match(renderer, /isAgentAdminDocEditingSurface\(selectedEnvelope\)/);
  assert.match(renderer, /<AgentAdminDocEditingSurface envelope=\{selectedEnvelope as never\} onAction=\{onAction\}/);
  for (const marker of [
    'AgentAdminBlankSurface',
    'AgentAdminDashboardSurface',
    'AgentAdminAgentListSurface',
    'AgentAdminAgentDetailSurface',
    'AgentAdminProfileHistorySurface',
    'AgentAdminSkillLibrarySurface',
    'AgentAdminAssignmentSurface',
    'AgentAdminDocumentSurface',
    'AgentAdminEditSessionSurface',
    'AgentAdminProposalReviewSurface',
    'AgentAdminCreateSkillSurface',
    'AgentAdminDeleteSkillSurface',
    'AgentAdminCreateReferenceDocSurface',
    'AgentAdminDeleteReferenceDocSurface',
    'AgentAdminRuntimeTracesSurface',
    'AgentAdminVersionHistorySurface',
    'AgentAdminVersionDiffSurface',
    'agentAdminDocEditingContracts',
    'editInputEnabled === true',
    '!doc.currentVersion ? actionById',
    'Edit input disabled: selected version',
    'User instruction transcript',
    'Full proposed Markdown document',
    'warningsAdvisoryOnly',
    'saveCreatesNonActiveProposal',
    'deprecationWarning',
    'Apply trace filters',
    'role="listitem"',
    'aria-label="Document versions"'
  ]) assert.match(agentAdminDocEditingSurface, new RegExp(marker.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')));
  assert.doesNotMatch(agentAdminDocEditingSurface, /surface-agent-prompt-governance|surface-agent-tool-boundary-diff|prompt_risk_review|JSON\.stringify/);
});

test('Agent Admin frontend API and surface types expose doc-editing DTO contracts', () => {
  for (const typeName of [
    'AgentAdminDocKind',
    'AgentAdminAgentListResponse',
    'AgentAdminAgentDetailResponse',
    'AgentAdminDocumentVersionDetail',
    'AgentAdminVersionHistoryResponse',
    'AgentAdminAdjacentDiffResponse',
    'AgentAdminEditSessionRecord',
    'AgentAdminRuntimeDocReadTraceRow',
    'AgentAdminBehaviorProfileSummary',
    'AgentAdminBehaviorProfileHistoryRow',
    'AgentAdminBehaviorProposalSummary',
    'AgentAdminAssignmentSurfaceResponse',
    'AgentAdminProposalReviewResponse'
  ]) assert.match(apiTypes, new RegExp(`export type ${typeName}`));
  for (const typeName of [
    'AgentAdminSurfaceContract',
    'AgentAdminSurfaceData',
    'AgentAdminDocumentDetail',
    'AgentAdminRuntimeTraceRow',
    'AgentAdminBehaviorProfileSummary',
    'AgentAdminBehaviorProfileHistoryRow',
    'AgentAdminBehaviorProposalSummary',
    'AgentAdminGeneratedToolSummary'
  ]) assert.match(surfaceTypes, new RegExp(`export type ${typeName}`));
});

test('Agent Admin fixture API client routes current doc-editing actions to structured surfaces', () => {
  for (const actionId of [
    'action-agent-admin-show-blank',
    'action-agent-admin-show-dashboard',
    'action-agent-admin-show-agents',
    'action-agent-admin-open-agent-detail',
    'action-agent-admin-open-profile-history',
    'action-agent-admin-open-skill-library',
    'action-agent-admin-open-skill-assignment',
    'action-agent-admin-assign-skills',
    'action-agent-admin-open-tool-assignment',
    'action-agent-admin-assign-generated-tools',
    'action-agent-admin-open-model-config-ref',
    'action-agent-admin-update-model-config-ref',
    'action-agent-admin-open-prompt-doc',
    'action-agent-admin-open-skill-doc',
    'action-agent-admin-open-reference-doc',
    'action-agent-doc-edit-start',
    'action-agent-doc-edit-revise',
    'action-agent-doc-edit-save',
    'action-agent-doc-edit-cancel',
    'action-agent-doc-proposal-review',
    'action-agent-doc-proposal-approve',
    'action-agent-doc-proposal-reject',
    'action-agent-doc-proposal-activate',
    'action-agent-doc-proposal-cancel',
    'action-agent-doc-version-history',
    'action-agent-doc-version-diff',
    'action-agent-doc-version-restore',
    'action-agent-admin-open-create-skill',
    'action-agent-admin-create-skill',
    'action-agent-admin-open-delete-skill',
    'action-agent-admin-delete-skill',
    'action-agent-admin-open-create-reference-doc',
    'action-agent-admin-create-reference-doc',
    'action-agent-admin-open-delete-reference-doc',
    'action-agent-admin-delete-reference-doc',
    'action-agent-admin-open-runtime-traces'
  ]) {
    assert.match(surfaces, new RegExp(actionId));
    assert.match(apiClient, new RegExp(actionId));
    assert.match(backendWorkstreamService, new RegExp(actionId));
  }
  for (const resultName of [
    'displayAgentBlankActionResult',
    'displayAgentProfileHistoryActionResult',
    'displayAgentSkillLibraryActionResult',
    'displayAgentSkillAssignmentActionResult',
    'displayAgentToolAssignmentActionResult',
    'displayAgentModelRefActionResult',
    'displayAgentPromptDocActionResult',
    'displayAgentSkillDocActionResult',
    'displayAgentReferenceDocActionResult',
    'displayAgentEditSessionActionResult',
    'displayAgentProposalReviewActionResult',
    'displayAgentVersionHistoryActionResult',
    'displayAgentVersionDiffActionResult',
    'displayAgentCreateSkillActionResult',
    'displayAgentDeleteSkillActionResult',
    'displayAgentCreateReferenceDocActionResult',
    'displayAgentDeleteReferenceDocActionResult',
    'displayAgentRuntimeTracesActionResult'
  ]) assert.match(apiClient, new RegExp(resultName));
});

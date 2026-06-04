import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const agents = read('./__tests__/fixtures/workstream/agents.ts');
const me = read('./__tests__/fixtures/workstream/me.ts');
const surfaces = read('./__tests__/fixtures/workstream/surfaces.ts');
const workstream = read('./__tests__/fixtures/workstream/workstream.ts');
const apiClient = read('./__tests__/fixtures/api/FixtureWorkstreamApiClient.ts');

test('Agent Admin functional agent is visible and capability backed for governed runtime', () => {
  assert.match(agents, /label: 'Agent Admin'[\s\S]*defaultSurfaceType: 'markdown_response'/);
  for (const capability of [
    'agent_admin.submit_turn',
    'agent_admin.list_definitions',
    'agent_admin.get_definition',
    'agent.definitions.manage',
    'agent_admin.get_prompt_version',
    'agent_admin.get_skill_version',
    'agent_admin.get_reference_version',
    'agent_admin.get_manifest',
    'agent_admin.get_tool_boundary',
    'agent_admin.draft_behavior_change',
    'agent_admin.submit_behavior_change_for_review',
    'agent_admin.approve_behavior_change',
    'agent_admin.reject_behavior_change',
    'agent_admin.activate_behavior_change',
    'agent_admin.cancel_behavior_change',
    'agent_admin.rollback_behavior_change',
    'agent_admin.simulate_tool_boundary',
    'agent_admin.get_model_ref',
    'agent_admin.list_seed_material',
    'agent_admin.reseed_missing_defaults',
    'agent_admin.prompt_risk_review.start',
    'agent_admin.prompt_risk_review.read',
    'agent_admin.prompt_risk_review.cancel',
    'agent_admin.prompt_risk_review.accept_result',
    'agent_admin.prompt_risk_review.reject_result'
  ]) {
    assert.match(agents, new RegExp(capability.replaceAll('.', '\\.')));
    assert.match(me, new RegExp(capability.replaceAll('.', '\\.')));
  }
  assert.match(agents, /attention: \{ count: 4, severity: 'blocked', source: 'attention\.list_rail_summaries' \}/);
  assert.match(agents, /availability: 'visible'/);
});

test('Agent Admin fixtures include catalog, detail, governed diffs, model refs, test console, decisions, and traces', () => {
  for (const fixture of [
    'agentAdminCatalogSurface',
    'agentAdminDetailSurface',
    'agentPromptGovernanceSurface',
    'agentSkillManifestSurface',
    'agentToolBoundarySurface',
    'agentModelRefsSurface',
    'agentSeedMaterialSurface',
    'agentTestConsoleSurface',
    'agentBehaviorProposalSurface',
    'agentAdminAgentBlockedSystemMessageSurface',
    'agentAdminPromptRiskReviewSurface',
    'agentAdminTraceSurface'
  ]) {
    assert.match(surfaces, new RegExp(fixture));
  }
  for (const surfaceId of [
    'surface-agent-admin-catalog',
    'surface-agent-admin-detail',
    'surface-agent-prompt-governance',
    'surface-agent-skill-manifest-diff',
    'surface-agent-tool-boundary-diff',
    'surface-agent-model-refs',
    'surface-agent-seed-material',
    'surface-agent-test-console',
    'surface-agent-behavior-proposal',
    'surface-agent-admin-agent-provider-blocked',
    'surface-agent-admin-prompt-risk-review',
    'surface-agent-admin-trace'
  ]) {
    assert.match(surfaces, new RegExp(surfaceId));
  }
});

test('Agent Admin surfaces preserve required UI states, approval gates, validation, redaction, and trace links', () => {
  for (const marker of [
    'Loading surface',
    'Empty',
    'TARGET_NOT_FOUND_OR_FORBIDDEN',
    'validation-error',
    'approval-required',
    'MODEL_POLICY_DENIED',
    'TOOL_BOUNDARY_DENIED',
    'Provider secret values are never browser-visible',
    'redactedPreview',
    'agent_admin.catalog.v1',
    'agent_admin.definition.v1',
    'agent_admin.prompt_version.v1',
    'agent_admin.manifest.v1',
    'agent_admin.tool_boundary.v1',
    'agent_admin.model_ref.v1',
    'agent_admin.seed_material.v1',
    'agent_admin.behavior_change_proposal.v1',
    'PromptAssemblyTrace',
    'SkillLoadTrace',
    'ReferenceLoadTrace',
    'AgentWorkTrace',
    'agentAdminEvidence.read',
    'readSkill(skillId)',
    'readReferenceDoc(referenceId)',
    'no direct mutation',
    'No-side-effect agent test console',
    'agent_admin.prompt_risk_review_task.v1',
    'completed_review_required',
    'activationBlockedUntilHumanDecision',
    'prompt-risk AutonomousAgent',
    'No direct activation'
  ]) {
    assert.match(surfaces, new RegExp(marker.replace(/[()]/g, '\\$&')));
  }
  assert.match(surfaces, /requiresApproval: true/);
  assert.match(surfaces, /secretVisibility: 'redacted'/);
  assert.match(surfaces, /trace-prompt-assembly-42/);
  assert.match(surfaces, /trace-skill-load-17/);
  assert.match(surfaces, /trace-agent-work-88/);
});

test('Agent Admin actions and fixture client return structured surfaces instead of page routes', () => {
  for (const actionId of [
    'action-display-agent-catalog',
    'action-open-agent-detail',
    'action-activate-agent-definition',
    'action-deactivate-agent-definition',
    'action-propose-prompt-diff',
    'action-test-agent-prompt',
    'action-approve-skill-manifest',
    'action-submit-behavior-change',
    'action-reject-behavior-change',
    'action-activate-behavior-change',
    'action-cancel-behavior-change',
    'action-rollback-behavior-change',
    'action-simulate-tool-boundary',
    'action-manage-model-ref',
    'action-list-agent-seed-material',
    'action-import-agent-seed-defaults',
    'action-open-agent-trace',
    'action-agentadmin-start-prompt-risk-review',
    'action-agentadmin-read-prompt-risk-review',
    'action-agentadmin-cancel-prompt-risk-review',
    'action-agentadmin-accept-prompt-risk-review-result',
    'action-agentadmin-reject-prompt-risk-review-result'
  ]) {
    assert.match(surfaces, new RegExp(actionId));
  }
  assert.match(surfaces, /displayAgentCatalogActionResult/);
  assert.match(surfaces, /displayAgentDetailActionResult/);
  assert.match(surfaces, /displayAgentSeedMaterialActionResult/);
  assert.match(surfaces, /displayAgentBehaviorProposalActionResult/);
  assert.match(apiClient, /displayAgentCatalogActionResult/);
  assert.match(apiClient, /displayAgentDetailActionResult/);
  assert.match(apiClient, /displayAgentSeedMaterialActionResult/);
  assert.match(apiClient, /displayAgentBehaviorProposalActionResult/);
  assert.match(apiClient, /displayAgentPromptRiskReviewActionResult/);
  assert.match(apiClient, /agent_admin\.list_definitions/);
  assert.match(apiClient, /agent_admin\.list_seed_material/);
});

test('Agent Admin starts without bootstrap markdown and keeps structured governance surfaces available', () => {
  assert.match(workstream, /initialWorkstreamItems: WorkstreamItem\[\] = \[\]/);
  assert.match(surfaces, /Model-backed AgentAdminAgent guidance was blocked before a response was produced/);
  assert.match(surfaces, /no direct mutation|ToolPermissionBoundary enforcement/);
  for (const surfaceId of [
    'surface-agent-admin-catalog',
    'surface-agent-admin-detail',
    'surface-agent-prompt-governance',
    'surface-agent-tool-boundary-diff',
    'surface-agent-test-console',
    'surface-agent-admin-prompt-risk-review'
  ]) {
    assert.match(surfaces, new RegExp(surfaceId));
  }
  assert.match(surfaces, /fullCoreDemoSurfaceEnvelopes/);
  assert.match(surfaces, /trace-agent-work-88/);
});

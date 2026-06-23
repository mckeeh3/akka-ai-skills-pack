import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';

const read = (path) => readFileSync(new URL(path, import.meta.url), 'utf8');

const surfaceTypes = read('./workstream/types/surfaces.ts');
const actionTypes = read('./workstream/types/actions.ts');
const workstreamTypes = read('./workstream/types/workstream.ts');
const apiClient = read('./api/WorkstreamApiClient.ts');
const httpClient = read('./api/HttpWorkstreamApiClient.ts');
const renderer = read('./workstream/surfaces/SurfaceRenderer.tsx');
const chatSurface = read('./workstream/surfaces/ChatToolPlanSurface.tsx');
const stream = read('./workstream/stream/WorkstreamStream.tsx');
const main = read('./main.tsx');
const styles = read('./styles/components.css');

test('chat tool plan DTOs model proposal, confirmation snapshot, result, and system-message surfaces', () => {
  for (const contract of ['chat_tool_plan.proposal.v1', 'chat_tool_plan.confirmation.v1', 'chat_tool_plan.result.v1', 'chat_tool_plan.system_message.v1']) {
    assert.match(surfaceTypes, new RegExp(contract.replaceAll('.', '\\.')));
  }
  for (const typeName of ['ChatToolPlanProposal', 'ChatToolPlanStep', 'ChatToolPlanConfirmationSnapshot', 'ChatToolPlanExecutionResult', 'ChatToolPlanStepResult', 'ChatToolPlanSystemMessage', 'ChatToolPlanSurfaceData']) {
    assert.match(surfaceTypes, new RegExp(`export type ${typeName}`));
  }
  assert.match(actionTypes, /export type ChatToolPlanConfirmationRequest/);
  assert.match(actionTypes, /stepHashes: Record<string, string>/);
  assert.match(actionTypes, /confirmationText: string/);
  assert.match(apiClient, /confirmChatToolPlan\(request: ChatToolPlanConfirmationRequest\): Promise<ApiResult<WorkstreamMessageResponse>>/);
  assert.match(httpClient, /\/api\/workstream\/chat-tool-plans\/confirm/);
});

test('chat tool plan surfaces render inline and never auto-confirm or use generic browser confirm', () => {
  assert.match(workstreamTypes, /'chat_tool_plan_proposal'/);
  assert.match(workstreamTypes, /'chat_tool_plan_result'/);
  assert.match(stream, /isChatToolPlanSurfaceItemKind\(item\.kind\)/);
  assert.match(renderer, /case 'chat_tool_plan_proposal':/);
  assert.match(renderer, /return <ChatToolPlanSurface/);
  assert.match(chatSurface, /function ChatToolPlanProposal/);
  assert.match(chatSurface, /onSubmit=\{submitConfirmation\}/);
  assert.match(chatSurface, /confirmationMatches/);
  assert.match(chatSurface, /disabled=\{!canSubmit\}/);
  assert.match(chatSurface, /There is no automatic or background execution/);
  assert.doesNotMatch(chatSurface, /globalThis\.confirm|window\.confirm|confirm\(/);
  assert.doesNotMatch(main, /globalThis\.confirm[\s\S]*action-confirm-chat-tool-plan|action-confirm-chat-tool-plan[\s\S]*globalThis\.confirm/);
});

test('chat tool plan confirmation is plan-bound and submitted through the dedicated backend contract', () => {
  assert.match(chatSurface, /CONFIRM \$\{confirmableSnapshot\.planSnapshotId\}/);
  assert.match(chatSurface, /planId: confirmableSnapshot\.planId/);
  assert.match(chatSurface, /planSnapshotId: confirmableSnapshot\.planSnapshotId/);
  assert.match(chatSurface, /selectedContextId: confirmableSnapshot\.selectedContextId/);
  assert.match(chatSurface, /stepHashes: confirmableSnapshot\.stepHashes/);
  assert.match(chatSurface, /idempotencyKey: `\$\{confirmableSnapshot\.idempotencyRoot\}:confirm:\$\{confirmableSnapshot\.planSnapshotId\}`/);
  assert.match(main, /action\.actionId === 'action-confirm-chat-tool-plan'/);
  assert.match(main, /workstreamClient\.confirmChatToolPlan\(input\)/);
  assert.doesNotMatch(main, /action-confirm-chat-tool-plan[\s\S]{0,500}runCapabilityAction/);
});

test('chat tool plan UI summarizes governance, results, recovery, traces, and browser-safe redaction', () => {
  for (const phrase of ['Required capabilities', 'Approval summary', 'Idempotency root', 'Proposed steps', 'Browser-safe input summary', 'Completed steps', 'Failed steps', 'Skipped steps', 'Recovery']) {
    assert.match(chatSurface, new RegExp(phrase));
  }
  assert.match(chatSurface, /sensitiveKeyPattern/);
  assert.match(chatSurface, /provider secrets\/payloads/);
  assert.match(chatSurface, /hidden capabilities/);
  assert.match(chatSurface, /TraceReferences/);
  assert.match(styles, /\.chat-tool-plan-surface/);
  assert.match(styles, /\.chat-tool-plan-confirmation-form/);
  assert.match(styles, /\.chat-tool-plan-step-grid/);
});

test('chat tool plan surface visually distinguishes executable, approval-gated, and partial-failure result states', () => {
  // Step classification badges are rendered in each step header before the user confirms
  assert.match(chatSurface, /stepClassificationPill/);
  assert.match(chatSurface, /status-pill warning.*Approval required|Approval required.*status-pill warning/);
  assert.match(chatSurface, /status-pill info.*Confirmation required|Confirmation required.*status-pill info/);
  assert.match(chatSurface, /status-pill success.*Executable|Executable.*status-pill success/);
  // Step badge container is rendered in the step header
  assert.match(chatSurface, /chat-tool-plan-step-badges/);
  assert.match(styles, /\.chat-tool-plan-step-badges/);
  // Boundary notice shows approval-gated count when proposal steps require approval
  assert.match(chatSurface, /approvalGatedCount/);
  assert.match(chatSurface, /require separate approval/);
  // Result status uses pills for visual distinction across all states
  assert.match(chatSurface, /resultStatusPill/);
  assert.match(chatSurface, /partial_failure.*warning|warning.*partial_failure/);
  assert.match(chatSurface, /approval_required.*warning|warning.*approval_required/);
  // Error codes are mapped to human-readable copy, not raw codes
  assert.match(chatSurface, /formatErrorCode/);
  assert.match(chatSurface, /approval_required.*pending separate approval/);
  assert.match(chatSurface, /Error detail/);
});

test('chat tool plan approval-gated steps do not bypass confirmation and cannot auto-submit', () => {
  // Classification badge shows requiresApproval state clearly
  assert.match(chatSurface, /step\.requiresApproval/);
  assert.match(chatSurface, /Separate approval required — step will not execute until approval is granted/);
  // canSubmit still requires confirmationMatches; approval-gated steps do not bypass this guard
  assert.match(chatSurface, /canSubmit = Boolean\(confirmAction && confirmationMatches/);
  // No auto-submit or background confirmation bypass
  assert.doesNotMatch(chatSurface, /autoSubmit|auto.submit/i);
  assert.doesNotMatch(chatSurface, /globalThis\.confirm|window\.confirm/);
});

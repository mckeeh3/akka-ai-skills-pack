# Task: Fix Agent Admin managed runtime validation gap

## Objective

Repair the Agent Admin v0 managed-runtime validation gap discovered by terminal verification: the rendered starter fullstack validation currently fails because `AgentBehaviorSeedLoaderTest.allFiveCoreAgentsResolveThroughSameManagedRuntimePathWithDistinctProfiles` prepares the `agent-agent-admin` runtime invocation with an AuthContext that lacks the Agent Admin invocation capability expected by `AgentRuntimeService.invocationCapability(...)`.

## Required inherited reads

- `specs/agent-admin-workstream-v0/workstream-contract.md`
- `specs/agent-admin-workstream-v0/capability-inventory.md`
- `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md`

## In scope

- Update the starter template/backend tests and, only if needed, the minimal runtime/seed authorization mapping so all five core agents resolve through the same governed runtime path with distinct per-agent invocation capabilities.
- Preserve the Agent Admin capability id `agent_admin.submit_turn` for Agent Admin request/response turns.
- Keep user-facing runtime behavior request-based through Akka `Agent`; do not add deterministic/model-less fallback behavior.
- Keep provider fail-closed and ToolPermissionBoundary behavior unchanged.

## Out of scope

- Do not implement unrelated Agent Admin proposal/review/autonomous-task features.
- Do not relax authorization or make prompt/skill/reference text grant authority.
- Do not change other workstream queues except if a directly related validation note must be recorded.

## Expected outputs

- Focused template/backend test or runtime authorization mapping fix.
- Updated queue status and notes.

## Required checks

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `git diff --check`

## Done criteria

- Fullstack starter validation passes.
- Agent Admin managed runtime preparation remains authorized only when the AuthContext has the Agent Admin invocation capability.
- The fix does not weaken ToolPermissionBoundary, provider fail-closed, tenant/AuthContext, or trace requirements.

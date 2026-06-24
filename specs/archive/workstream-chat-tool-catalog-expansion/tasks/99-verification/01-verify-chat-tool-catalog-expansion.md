# TASK-WCTC-99-001: Verify Workstream Chat Tool Catalog Expansion completion

## Purpose

Verify the expanded confirmed chat tool catalog through the intended local runtime/API/UI path, or append follow-up tasks and a new terminal verification task.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/workstream-chat-tool-catalog-expansion/README.md`
- `specs/workstream-chat-tool-catalog-expansion/conversation-capture.md`
- `specs/workstream-chat-tool-catalog-expansion/pending-tasks.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-inventory.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-coverage-map.md`
- completed task notes and changed files

## Skills

- `akka-runtime-feature-verification`
- `akka-agent-testing`
- `akka-web-ui-testing`
- `akka-agent-work-trace`

## Expected outputs

- `specs/workstream-chat-tool-catalog-expansion/verification-notes.md`
- queue update marking verification done only when README done state is achieved
- new bounded follow-up tasks plus a new terminal verification task if material gaps remain
- commit for verification notes and queue updates

## Required checks

- `git diff --check`
- targeted backend/API expanded catalog tests
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- `npm --prefix frontend run build` if frontend runtime output changed materially
- `mvn test` if shared backend workstream/agent behavior changed materially
- runtime-evidence and workstream-contract validators
- local API/UI/manual smoke for representative expanded paths when provider/auth/runtime configuration allows it

## Done criteria

- Verification notes compare completed work against every README done-state bullet.
- Catalog inventory/classification coverage is complete for foundation workstream actions.
- Expanded executable/proposal/approval-gated paths are proven safe at the stated readiness level.
- Blocked/surface-only classifications are documented and cannot execute through chat.
- Provider missing config fails closed and is not counted as successful planning.
- Audit/work trace evidence is recorded.
- If gaps remain, new bounded tasks plus a new terminal verification task are appended.
- Changes and queue update are committed.

# TASK-WCTE-08-001: Render chat tool plan confirmation and result surfaces

## Purpose

Add browser-side API/client/types/components for chat tool plan proposal, confirmation, execution result, denial, and partial-failure surfaces.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- `frontend/src/workstream/composer/**`
- `frontend/src/workstream/surfaces/**`
- `frontend/src/workstream/types/**`
- `frontend/src/api/**`
- backend surface contracts from prior tasks

## Skills

- `akka-web-ui-api-client`
- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-accessibility-responsive`

## Expected outputs

- Typed frontend surface/data contracts for chat tool plans.
- Confirmation UI that summarizes plan, steps, inputs, effects, idempotency, approvals, traces, and confirmation action.
- Result UI for completed, failed, skipped, and recovery states.
- Contract/component tests.
- Queue update.

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- UI never auto-confirms or auto-submits tool execution.
- Confirmation is plan-bound, accessible, and editable only through appropriate repair/reproposal flows.
- No secrets/hidden capabilities/provider payloads are exposed.
- Changes and queue update are committed.

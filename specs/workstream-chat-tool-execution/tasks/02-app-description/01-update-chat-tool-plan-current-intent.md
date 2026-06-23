# TASK-WCTE-02-001: Update app-description current intent for chat tool plans

## Purpose

Update core-starter app-description artifacts so all five foundation workstreams explicitly model `human_chat_tool_plan` exposure alongside surface actions and agent-tool boundaries.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- `app-description/domains/core-starter/workstreams/**`
- `app-description/domains/core-starter/workstreams/surface-catalog.md`

## Skills

- `app-description-functional-agent-modeling`
- `app-description-surface-modeling`
- `app-description-capability-modeling`
- `app-description-auth-security`
- `app-description-test-specification`

## Expected outputs

- App-description workstream/tool/surface/test updates for all five foundation workstreams.
- Shared governed tool ids for surface actions and chat tool-plan adapters.
- Explicit plan proposal, confirmation, transaction/idempotency, denial, trace, and validation expectations.
- Queue update.

## Required checks

- `git diff --check`
- focused search proving all five workstreams mention `human_chat_tool_plan` or equivalent accepted adapter terminology

## Done criteria

- Current intent says chat plan execution is allowed only after explicit confirmation and backend authorization.
- No app-description wording grants unrestricted chat mutation or AI autonomous authority.
- Changes and queue update are committed.

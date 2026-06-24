# TASK-WCTC-02-001: Update expanded chat tool current intent

## Purpose

Update app-description current intent and create a coverage map for the expanded chat tool catalog.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/workstream-chat-tool-catalog-expansion/README.md`
- `specs/workstream-chat-tool-catalog-expansion/catalog-inventory.md`
- `app-description/domains/core-starter/workstreams/**`

## Skills

- `app-description-functional-agent-modeling`
- `app-description-surface-modeling`
- `app-description-capability-modeling`
- `app-description-auth-security`
- `app-description-test-specification`

## Expected outputs

- app-description updates for expanded/blocked `human_chat_tool_plan` catalog coverage
- `specs/workstream-chat-tool-catalog-expansion/catalog-coverage-map.md`
- queue update

## Required checks

- `git diff --check`
- focused search proving all five workstreams have expanded catalog classification and blocked/surface-only rationale

## Done criteria

- Current intent distinguishes executable, proposal-only, approval-gated, surface-only, router-only, internal-only, blocked, and out-of-scope actions.
- No app-description wording grants unrestricted chat mutation or autonomous AI authority.
- Changes and queue update are committed.

# TASK-AUTO-07-001: Verify additional Autonomous Agent examples

## Objective

Verify the additional Autonomous Agent examples and planning tasks after they land, update coverage, and append follow-up tasks only for real remaining gaps.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agents-integration/README.md`
- `specs/autonomous-agents-integration/pending-tasks.md`
- `specs/autonomous-agents-integration/additional-examples-plan.md`
- `specs/autonomous-agents-integration/research-notes.md`
- `docs/agent-coverage-matrix.md`
- changed files from TASK-AUTO-06-* entries

## In scope

- Review implemented examples for runtime-completion doctrine, narrow scope, and test reliability.
- Update `docs/agent-coverage-matrix.md` statuses and cleanup backlog.
- Add verification notes under `specs/autonomous-agents-integration/`.
- Append new follow-up tasks only if uncovered gaps remain important.

## Out of scope

- Do not implement new examples during verification.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "TaskRule|handoff|dependsOn|approval|notificationStream|TeamLeadership|Moderation|ToolPermissionBoundary" docs specs/autonomous-agents-integration src/main/java src/test/java`

## Done criteria

- Verification notes are recorded.
- Coverage matrix reflects the examples that exist after TASK-AUTO-06-*.
- Any remaining gaps are either explicitly accepted or appended as future tasks.
- Focused commit exists with message `autonomous-agents: verify additional examples`.

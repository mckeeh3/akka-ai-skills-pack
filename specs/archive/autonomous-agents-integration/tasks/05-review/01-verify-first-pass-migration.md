# TASK-AUTO-05-001: Verify first-pass Autonomous Agents migration

## Objective

Review the completed first-pass migration for gaps, consistency issues, missed routing updates, incomplete coverage, and example/test needs. Append follow-up tasks as needed.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agents-integration/README.md`
- `specs/autonomous-agents-integration/conversation-capture.md`
- `specs/autonomous-agents-integration/pending-tasks.md`
- `specs/autonomous-agents-integration/research-notes.md`
- all files changed by completed TASK-AUTO tasks
- `docs/agent-coverage-matrix.md`
- official autonomous-agent docs only for disputed semantics.

## In scope

- Check whether Autonomous Agents are integrated across docs, skills, routing, coverage, and examples.
- Check that request-based workstream agents remain correctly routed.
- Check that governed runtime/security/capability-first requirements are preserved.
- Check for `AgentDefinition` naming ambiguity.
- Check examples/tests for real Akka SDK usage and deterministic test-only model providers.
- Append specific follow-up tasks for any gaps.
- If follow-up tasks are appended, also append another verification task that depends on those follow-ups.
- If no follow-up implementation tasks are needed, leave TASK-AUTO-05-002 as the next runnable task.

## Out of scope

- Do not fix large gaps inside the verification task. Append tasks instead.
- Small typo/cross-link fixes are allowed only if they are directly part of review cleanup.

## Expected outputs

- Queue updates with follow-up tasks if needed.
- Optional `specs/autonomous-agents-integration/verification-notes.md`.
- Queue status update and focused commit.

## Required checks

- `git diff --check`
- `rg -n "AutonomousAgent|Autonomous Agent|request-based Agent|Workflow|AgentDefinition|TaskAcceptance|forAutonomousAgent" docs skills src templates specs/autonomous-agents-integration`
- Run relevant tests if verification touches code.

## Done criteria

- Verification findings are recorded.
- Follow-up tasks and another verify task are appended if gaps remain.
- Commit message: `autonomous-agents: verify first pass`.

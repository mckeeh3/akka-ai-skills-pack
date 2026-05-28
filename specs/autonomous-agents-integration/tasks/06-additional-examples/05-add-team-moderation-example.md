# TASK-AUTO-06-005: Add Autonomous Agent team or moderation example

## Objective

Add one focused executable coordination example for either `TeamLeadership` or `Moderation`, choosing the smallest slice that can be tested clearly.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agents-integration/research-notes.md`
- `skills/akka-autonomous-agents/SKILL.md`
- `skills/akka-autonomous-agent-coordination/SKILL.md`
- `skills/akka-autonomous-agent-testing/SKILL.md`
- `akka-context/sdk/autonomous-agents/coordination.html.md`
- `akka-context/sdk/autonomous-agents/capabilities.html.md`
- `akka-context/sdk/autonomous-agents/notifications.html.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`

## In scope

- Choose either a small `TeamLeadership` shared-backlog review or a `Moderation` panel/debate example.
- Add minimal agent/task/result classes and tests that script the relevant built-in coordination tools.
- Keep the example narrow enough for reliable tests.
- Update `docs/agent-coverage-matrix.md` if coverage status changes.

## Out of scope

- Do not implement both team and moderation if that makes the task too broad; append a follow-up task instead.
- No product-grade collaboration UI.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "TeamLeadership|TeamMember|Moderation|createTeam|startScriptedConversation|submitTurn|AutonomousAgentTools" src/main/java src/test/java docs/agent-coverage-matrix.md`

## Done criteria

- Test proves the selected team/moderation coordination path.
- Any unimplemented paired pattern is recorded as a follow-up or coverage gap.
- Focused commit exists with message `autonomous-agents: add team moderation example`.

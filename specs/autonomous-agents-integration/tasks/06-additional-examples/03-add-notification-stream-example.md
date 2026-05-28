# TASK-AUTO-06-003: Add Autonomous Agent notification stream example

## Objective

Add a focused executable example showing agent or task notifications exposed through a small endpoint for progress observation, while task snapshots/results remain the source of truth.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/autonomous-agents-integration/research-notes.md`
- `skills/akka-autonomous-agents/SKILL.md`
- `skills/akka-autonomous-agent-coordination/SKILL.md`
- `skills/akka-autonomous-agent-testing/SKILL.md`
- `akka-context/sdk/autonomous-agents/notifications.html.md`
- `akka-context/sdk/autonomous-agents/client.html.md`
- `akka-context/sdk/autonomous-agents/testing.html.md`

## In scope

- Add a minimal endpoint or endpoint method that exposes task or agent `notificationStream()`.
- Add a test that observes terminal/progress notifications and separately asserts task snapshot/result correctness.
- Document authorization/tenant-filter obligations in comments or coverage notes where the example is reference-only.
- Update `docs/agent-coverage-matrix.md` if coverage status changes.

## Out of scope

- No full browser progress UI.
- No notification-backed business decision logic.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "notificationStream|Notification|serverSentEvents|SSE|TaskNotification|snapshot" src/main/java src/test/java docs/agent-coverage-matrix.md`

## Done criteria

- Test proves notification exposure and independently verifies task result/snapshot.
- Guidance does not treat notifications as source of truth.
- Focused commit exists with message `autonomous-agents: add notification example`.

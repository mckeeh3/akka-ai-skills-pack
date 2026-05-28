# TASK-AUTO-00-001: Create autonomous agents integration queue

## Objective

Create the durable mini-project and pending-task queue for integrating Akka Autonomous Agents into the skills pack.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `docs/pending-task-queue.md`
- `docs/pending-question-queue.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `.agents/skills/project-discussed-idea-to-pending-project/SKILL.md`

## In scope

- Capture the user decisions and concerns.
- Create README, conversation capture, sprint, backlog, task briefs, and pending queue.
- Mark this planning task done only if committed.

## Out of scope

- Do not implement Autonomous Agent docs, skills, examples, or tests in this planning task.

## Expected outputs

- `specs/autonomous-agents-integration/README.md`
- `specs/autonomous-agents-integration/conversation-capture.md`
- `specs/autonomous-agents-integration/pending-tasks.md`
- `specs/autonomous-agents-integration/sprints/01-autonomous-agents-first-pass-sprint.md`
- `specs/autonomous-agents-integration/backlog/01-autonomous-agents-first-pass-backlog.md`
- `specs/autonomous-agents-integration/tasks/**/*.md`

## Required checks

- `git diff --check`
- `rg -n "TASK-AUTO|AutonomousAgent|Autonomous Agents|request-based" specs/autonomous-agents-integration`

## Done criteria

- Queue is self-contained for fresh-session execution.
- Every future task requires one focused git commit.
- Planning scaffold is committed with message `autonomous-agents: add integration queue`.

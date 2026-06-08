# Task: Create real Akka runtime replacement queue

## Objective

Capture the user's stricter decision as a durable mini-project and queue: all normal generated workstream/foundation runtime defaults must be replaced with real Akka components; non-Akka substitute/default/mock/fixture paths are allowed only in tests.

## Required reads

- AGENTS.md
- skills/README.md
- docs/pending-task-queue.md
- docs/pending-question-queue.md
- docs/ai-first-saas-application-architecture.md
- docs/capability-first-backend-architecture.md
- specs/full-core-smb-runtime-durability-remediation/README.md
- specs/full-core-smb-runtime-durability-remediation/pending-tasks.md

## In scope

- Create `specs/real-akka-runtime-replacement/` planning docs, backlog, task briefs, and pending queue.
- Record that the prior local/demo gating compromise is superseded for normal runtime.

## Out of scope

- Do not implement the runtime replacements in this planning task.

## Expected outputs

- `README.md`
- `conversation-capture.md`
- `pending-tasks.md`
- `sprints/01-real-akka-runtime-replacement-sprint.md`
- `backlog/01-real-akka-runtime-replacement-build-backlog.md`
- `tasks/**/*.md`

## Checks

- `git diff --check`

## Done criteria

- The queue has bounded tasks and a terminal verification task.
- The first implementation task is runnable without guessing.
- Planning changes and queue update are committed.

## Commit message

`runtime: add real Akka replacement queue`

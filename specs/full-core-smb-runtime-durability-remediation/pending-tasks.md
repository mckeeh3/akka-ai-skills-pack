# Pending Tasks: Full-Core SMB Runtime Durability Remediation

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `full-core-smb: <short task title>`.

## Tasks

### TASK-FCSMB-DUR-00-001: Create runtime durability remediation queue

- status: done
- source: user confirmed no-in-memory-normal-runtime release bar after source scan found in-memory runtime defaults
- task brief: specs/full-core-smb-runtime-durability-remediation/tasks/00-planning/00-create-runtime-durability-remediation-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - specs/full-core-smb-polish-release-readiness/release-handoff.md
  - specs/full-core-smb-polish-release-readiness/release-readiness-verification.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/full-core-smb-runtime-durability-remediation/README.md
  - specs/full-core-smb-runtime-durability-remediation/conversation-capture.md
  - specs/full-core-smb-runtime-durability-remediation/pending-tasks.md
  - specs/full-core-smb-runtime-durability-remediation/sprints/*.md
  - specs/full-core-smb-runtime-durability-remediation/backlog/*.md
  - specs/full-core-smb-runtime-durability-remediation/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold exists and is committed
- notes:
  - commit message: `full-core-smb: add runtime durability remediation queue`

### TASK-FCSMB-DUR-01-001: Inspect in-memory and fixture runtime paths and define remediation map

- status: pending
- source: specs/full-core-smb-runtime-durability-remediation/backlog/01-runtime-durability-remediation-backlog.md
- task brief: specs/full-core-smb-runtime-durability-remediation/tasks/01-remediation/01-inspect-runtime-durability-boundaries.md
- depends on: [TASK-FCSMB-DUR-00-001]
- required reads:
  - AGENTS.md
  - specs/full-core-smb-runtime-durability-remediation/README.md
  - specs/full-core-smb-runtime-durability-remediation/conversation-capture.md
  - specs/full-core-smb-runtime-durability-remediation/sprints/01-runtime-durability-remediation-sprint.md
  - specs/full-core-smb-runtime-durability-remediation/backlog/01-runtime-durability-remediation-backlog.md
  - specs/full-core-smb-saas-hardening/smb-full-core-baseline.md
  - specs/full-core-smb-polish-release-readiness/release-handoff.md
  - specs/full-core-smb-polish-release-readiness/release-readiness-verification.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - none; repository source-boundary inspection task
- expected outputs:
  - specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
  - updated specs/full-core-smb-runtime-durability-remediation/pending-tasks.md with bounded remediation tasks
  - task briefs for next backend/frontend/docs remediation tasks
- required checks:
  - `git diff --check`
  - `rg -n "InMemory|in-memory|mock|Mock|fake|Fake|fixture|Fixture|demo|Demo|canned|model-less|fallback|stub|Stub" templates/ai-first-saas-starter frontend specs/full-core-smb-polish-release-readiness --glob '!**/node_modules/**' --glob '!**/target/**' --glob '!**/dist/**'`
  - targeted `find` commands listing discovered in-memory classes, fixture clients, static generated assets, and docs claims
  - `rg -n "InMemory|fixture|demo|release|blocker|durable|fail closed|normal runtime|test-only" specs/full-core-smb-runtime-durability-remediation`
- done criteria:
  - all discovered in-memory/fixture/demo paths are classified by release impact
  - remediation tasks can run without guessing source paths or validation commands
  - release-readiness ship recommendation is superseded or explicitly blocked if needed
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: map runtime durability remediation`

### TASK-FCSMB-DUR-99-001: Verify runtime durability remediation readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/full-core-smb-runtime-durability-remediation/tasks/99-verification/01-verify-runtime-durability-remediation.md
- depends on:
  - TASK-FCSMB-DUR-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/full-core-smb-runtime-durability-remediation/README.md
  - specs/full-core-smb-runtime-durability-remediation/conversation-capture.md
  - specs/full-core-smb-runtime-durability-remediation/pending-tasks.md
  - specs/full-core-smb-runtime-durability-remediation/sprints/*.md
  - specs/full-core-smb-runtime-durability-remediation/backlog/*.md
  - specs/full-core-smb-runtime-durability-remediation/tasks/**/*.md
  - specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/full-core-smb-runtime-durability-remediation/pending-tasks.md
  - verification notes or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - targeted checks needed to validate remediation done state
- done criteria:
  - mini-project goals have been compared against completed work
  - if incomplete, bounded tasks are appended before a new terminal verification task
  - if complete, release-readiness status is corrected and explicit
  - task changes and queue update are committed
- notes:
  - commit message: `full-core-smb: verify runtime durability remediation`

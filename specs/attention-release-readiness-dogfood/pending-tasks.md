# Pending Tasks: Attention Release Readiness Dogfood

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `attention-dogfood: <short task title>`.

## Tasks

### TASK-ARD-00-001: Create attention release-readiness dogfood queue

- status: done
- source: current conversation; user reported positive manual testing of left-rail attention, dashboards, and surfaces after v1/v2 attention work
- task brief: specs/attention-release-readiness-dogfood/tasks/00-planning/00-create-attention-dogfood-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/attention-release-readiness-dogfood/README.md
  - specs/attention-release-readiness-dogfood/conversation-capture.md
  - specs/attention-release-readiness-dogfood/dogfood-observations.md
  - specs/attention-release-readiness-dogfood/pending-tasks.md
  - specs/attention-release-readiness-dogfood/sprints/01-dogfood-release-readiness-sprint.md
  - specs/attention-release-readiness-dogfood/backlog/01-dogfood-release-readiness-backlog.md
  - specs/attention-release-readiness-dogfood/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project captures user dogfood evidence, sprint, backlog, task briefs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `attention-dogfood: add release readiness queue`

### TASK-ARD-01-001: Capture dogfood evidence and smoke checklist

- status: done
- source: specs/attention-release-readiness-dogfood/backlog/01-dogfood-release-readiness-backlog.md
- task brief: specs/attention-release-readiness-dogfood/tasks/01-validation/01-capture-evidence-and-smoke-checklist.md
- depends on:
  - TASK-ARD-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/attention-release-readiness-dogfood/README.md
  - specs/attention-release-readiness-dogfood/dogfood-observations.md
  - specs/attention-release-readiness-dogfood/conversation-capture.md
  - specs/attention-release-readiness-dogfood/tasks/01-validation/01-capture-evidence-and-smoke-checklist.md
  - specs/workstream-attention-backbone-v1/pending-tasks.md
  - specs/workstream-attention-event-producers-v2/pending-tasks.md
- skills:
  - none; validation planning task
- expected outputs:
  - specs/attention-release-readiness-dogfood/attention-release-smoke-checklist.md
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
- done criteria:
  - checklist covers left rail, My Account, dashboards/surfaces, producers, lifecycle, redaction, provider/fail-closed, and frontend-only authority guardrails
  - task changes and queue update are committed
- notes:
  - smoke checklist artifact: `specs/attention-release-readiness-dogfood/attention-release-smoke-checklist.md`
  - validation: `git diff --check`
  - commit message: `attention-dogfood: capture smoke checklist`

### TASK-ARD-01-002: Run fresh scaffold automated validation

- status: pending
- source: specs/attention-release-readiness-dogfood/backlog/01-dogfood-release-readiness-backlog.md
- task brief: specs/attention-release-readiness-dogfood/tasks/01-validation/02-run-fresh-scaffold-validation.md
- depends on:
  - TASK-ARD-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/attention-release-readiness-dogfood/README.md
  - specs/attention-release-readiness-dogfood/attention-release-smoke-checklist.md
  - specs/attention-release-readiness-dogfood/tasks/01-validation/02-run-fresh-scaffold-validation.md
- skills:
  - none; validation task
- expected outputs:
  - scaffold validation artifact under specs/attention-release-readiness-dogfood/
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - scaffolded backend Maven tests for attention services/producers/workstreams
  - frontend tests/typecheck/build
- done criteria:
  - fresh scaffold repeatability is proven or blockers are recorded
  - task changes and queue update are committed
- notes:
  - commit message: `attention-dogfood: run scaffold validation`

### TASK-ARD-02-001: Run manual/runtime edge review

- status: pending
- source: specs/attention-release-readiness-dogfood/backlog/01-dogfood-release-readiness-backlog.md
- task brief: specs/attention-release-readiness-dogfood/tasks/02-review/01-run-manual-edge-review.md
- depends on:
  - TASK-ARD-01-002
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/attention-release-readiness-dogfood/README.md
  - specs/attention-release-readiness-dogfood/attention-release-smoke-checklist.md
  - validation artifact from TASK-ARD-01-002
  - specs/attention-release-readiness-dogfood/tasks/02-review/01-run-manual-edge-review.md
- skills:
  - none; validation/review task
- expected outputs:
  - manual/runtime edge review artifact
  - updated pending-tasks.md
  - optional appended blocker tasks if issues are found
- required checks:
  - `git diff --check`
  - manual/runtime notes or clear blocked reason
- done criteria:
  - manual/edge review evidence is captured
  - blockers are converted to bounded tasks or marked non-blocking future work
  - task changes and queue update are committed
- notes:
  - commit message: `attention-dogfood: review runtime edges`

### TASK-ARD-02-002: Update release-readiness handoff

- status: pending
- source: specs/attention-release-readiness-dogfood/backlog/01-dogfood-release-readiness-backlog.md
- task brief: specs/attention-release-readiness-dogfood/tasks/02-review/02-update-release-handoff.md
- depends on:
  - TASK-ARD-02-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/attention-release-readiness-dogfood/README.md
  - specs/attention-release-readiness-dogfood/attention-release-smoke-checklist.md
  - validation artifacts from prior tasks
  - specs/attention-release-readiness-dogfood/tasks/02-review/02-update-release-handoff.md
- skills:
  - none; release handoff task
- expected outputs:
  - release-readiness handoff summary
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` over attention docs/handoff for stale missing-backbone claims if docs are edited
- done criteria:
  - release handoff summary is clear and bounded
  - task changes and queue update are committed
- notes:
  - commit message: `attention-dogfood: update release handoff`

### TASK-ARD-99-001: Verify attention dogfood release readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/attention-release-readiness-dogfood/tasks/99-verification/01-verify-attention-dogfood-readiness.md
- depends on:
  - TASK-ARD-01-001
  - TASK-ARD-01-002
  - TASK-ARD-02-001
  - TASK-ARD-02-002
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/attention-release-readiness-dogfood/README.md
  - specs/attention-release-readiness-dogfood/pending-tasks.md
  - all artifacts and task briefs under specs/attention-release-readiness-dogfood/
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional completion summary or appended follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - review recorded validation commands/results
  - focused `rg` as needed for backend-derived attention and stale doc claims
- done criteria:
  - mini-project done state is assessed
  - if complete, release-readiness completion is recorded
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `attention-dogfood: verify release readiness`

# TASK-REQWS-01-001: Audit input-processing artifacts

## Objective

Review current skills/docs that process app requirements, PRDs, app-description changes, planning backlogs, pending questions, and pending tasks. Classify alignment with the new process and identify exact edit targets.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `docs/workstream-dashboard-attention-event-backbone-wip.md`
- `specs/requirements-to-workstream-process-migration/README.md`
- `specs/requirements-to-workstream-process-migration/conversation-capture.md`
- `specs/requirements-to-workstream-process-migration/sprints/01-audit-and-target-model-sprint.md`
- `specs/requirements-to-workstream-process-migration/backlog/01-audit-and-target-model-backlog.md`

## Suggested files to inspect

- `skills/ai-first-saas/SKILL.md`
- `skills/agent-workstream-apps/SKILL.md`
- `skills/app-description-input-normalization/SKILL.md`
- `skills/app-description-intake-router/SKILL.md`
- `skills/akka-solution-decomposition/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `docs/prd-to-akka-flow.md`
- `docs/module-sprint-planning.md`
- `docs/solution-plan-to-implementation-queue.md`
- `docs/pending-task-queue.md`
- `docs/pending-question-queue.md`

## In scope

- Create an audit file under `specs/requirements-to-workstream-process-migration/`.
- Classify files as aligned, partially aligned, drift risk, or not relevant.
- Identify stale phrases/patterns and exact future edit points.

## Out of scope

- Do not edit installable skills/docs in this audit task unless only fixing obvious broken links in the mini-project.

## Expected outputs

- `specs/requirements-to-workstream-process-migration/audit-input-processing-artifacts.md`
- updated `pending-tasks.md`

## Required checks

- `git diff --check`
- `rg -n "aligned|partially aligned|drift risk|CRUD|page-first|component-first|AutonomousAgent|attention|dashboard" specs/requirements-to-workstream-process-migration/audit-input-processing-artifacts.md`

## Done criteria

- Audit is specific enough to drive doctrine, intake, planning, queue, and example tasks.
- One focused commit is made.

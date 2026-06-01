# Pending Tasks: AutonomousAgent Fullstack Regression Readiness

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `autonomous-agent-regression: <short task title>`.

## Tasks

### TASK-AAFR-00-001: Create AutonomousAgent regression readiness queue

- status: done
- source: current conversation after Governance/Policy Impact AutonomousAgent completion; known full scaffold `mvn test` blocker from stale Audit/Trace summary contract expectation
- task brief: specs/autonomous-agent-fullstack-regression-readiness/tasks/00-planning/00-create-regression-readiness-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - specs/governance-policy-impact-autonomous-agent/pending-tasks.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/autonomous-agent-fullstack-regression-readiness/README.md
  - specs/autonomous-agent-fullstack-regression-readiness/conversation-capture.md
  - specs/autonomous-agent-fullstack-regression-readiness/pending-tasks.md
  - specs/autonomous-agent-fullstack-regression-readiness/sprints/01-regression-readiness-sprint.md
  - specs/autonomous-agent-fullstack-regression-readiness/backlog/01-regression-readiness-backlog.md
  - specs/autonomous-agent-fullstack-regression-readiness/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and queue exist
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-regression: add readiness queue`

### TASK-AAFR-01-001: Fix Audit/Trace summary contract regression

- status: done
- source: specs/autonomous-agent-fullstack-regression-readiness/backlog/01-regression-readiness-backlog.md
- task brief: specs/autonomous-agent-fullstack-regression-readiness/tasks/01-regression/01-fix-audit-trace-summary-contract-regression.md
- depends on:
  - TASK-AAFR-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/autonomous-agent-fullstack-regression-readiness/README.md
  - specs/autonomous-agent-fullstack-regression-readiness/tasks/01-regression/01-fix-audit-trace-summary-contract-regression.md
  - specs/audit-trace-summary-autonomous-agent/README.md
  - specs/audit-trace-summary-autonomous-agent/audit-trace-summary-handoff.md
  - relevant WorkstreamServiceTest and Audit/Trace summary files
- skills:
  - none; regression fix task
- expected outputs:
  - fixed test or implementation contract mismatch
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - targeted backend test including `WorkstreamServiceTest.auditTraceSummaryWorkerFailsClosedUntilRealAutonomousRuntimeExists`
  - focused `rg` for stale `audit.trace.summaryTask.v1`/`audit.trace.summaryProgress.v1` expectations
- done criteria:
  - stale contract mismatch is resolved without weakening runtime meaning
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-regression: fix audit summary contract`
  - completed: updated starter Audit/Trace summary blocked-progress test and protected-read trace marker to canonical `audit.trace.summaryProgress.v1` contract without weakening provider fail-closed/no fake success assertions.
  - validation: fresh scaffold targeted test `mvn -Dtest=WorkstreamServiceTest#auditTraceSummaryWorkerFailsClosedUntilRealAutonomousRuntimeExists test` passed from `/tmp/aafr-targeted-w4HweZ`.
  - validation: `rg -n "audit\\.trace\\.summaryTask\\.v1" templates/ai-first-saas-starter/backend/src templates/ai-first-saas-starter/frontend/src -S || true` returned no source/template matches.
  - validation: `git diff --check` passed.

### TASK-AAFR-02-001: Run fullstack regression validation

- status: done
- source: specs/autonomous-agent-fullstack-regression-readiness/backlog/01-regression-readiness-backlog.md
- task brief: specs/autonomous-agent-fullstack-regression-readiness/tasks/02-validation/01-run-fullstack-regression-validation.md
- depends on:
  - TASK-AAFR-01-001
- required reads:
  - specs/autonomous-agent-fullstack-regression-readiness/README.md
  - specs/autonomous-agent-fullstack-regression-readiness/tasks/02-validation/01-run-fullstack-regression-validation.md
- skills:
  - none; validation task
- expected outputs:
  - validation artifact
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - fresh scaffold full backend `mvn test`
  - frontend `npm ci`, `npm test`, `npm run typecheck`, `npm run build`
  - focused scans for all four AutonomousAgent verticals
- done criteria:
  - fullstack regression readiness evidence is captured
  - blockers are appended as bounded tasks if found
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-regression: run fullstack validation`
  - validation artifact: specs/autonomous-agent-fullstack-regression-readiness/validation/02-fullstack-regression-validation.md
  - validation: fresh scaffold backend `mvn test` passed from `/tmp/aafr-fullstack-FKxiVA/backend` with `Tests run: 173, Failures: 0, Errors: 0, Skipped: 0` and `BUILD SUCCESS`.
  - validation: fresh scaffold frontend `npm ci` passed from `/tmp/aafr-fullstack-FKxiVA/frontend`.
  - blocker: fresh scaffold frontend `npm test`, `npm run typecheck`, and `npm run build` failed because the scaffolded frontend package is missing those scripts; bounded follow-up `TASK-AAFR-02-002` added.
  - validation: focused scans found all four AutonomousAgent verticals and 0 stale `audit.trace.summaryTask.v1` matches.

### TASK-AAFR-02-002: Fix scaffold frontend validation scripts

- status: done
- source: TASK-AAFR-02-001 validation blocker
- task brief: specs/autonomous-agent-fullstack-regression-readiness/tasks/02-validation/02-fix-frontend-validation-scripts.md
- depends on:
  - TASK-AAFR-02-001
- required reads:
  - specs/autonomous-agent-fullstack-regression-readiness/README.md
  - specs/autonomous-agent-fullstack-regression-readiness/validation/02-fullstack-regression-validation.md
  - specs/autonomous-agent-fullstack-regression-readiness/tasks/02-validation/02-fix-frontend-validation-scripts.md
- skills:
  - none; validation-script fix task
- expected outputs:
  - scaffold frontend validation script fix
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - fresh scaffold frontend `npm ci`
  - fresh scaffold frontend `npm test`
  - fresh scaffold frontend `npm run typecheck`
  - fresh scaffold frontend `npm run build`
  - focused scan that scaffolded frontend `package.json` exposes `test`, `typecheck`, and `build`
- done criteria:
  - required frontend validation scripts pass in a fresh scaffold
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-regression: fix frontend validation scripts`
  - validation artifact: specs/autonomous-agent-fullstack-regression-readiness/validation/02-frontend-validation-scripts.md
  - validation: fresh scaffold frontend `npm ci`, `npm test`, `npm run typecheck`, and `npm run build` passed from `/tmp/aafr-frontend-scripts-cCGw0a/frontend`.
  - validation: focused scan confirmed scaffolded frontend `package.json` exposes `test`, `typecheck`, and `build`.

### TASK-AAFR-03-001: Update integrated readiness handoff

- status: done
- source: specs/autonomous-agent-fullstack-regression-readiness/backlog/01-regression-readiness-backlog.md
- task brief: specs/autonomous-agent-fullstack-regression-readiness/tasks/03-docs/01-update-integrated-readiness-handoff.md
- depends on:
  - TASK-AAFR-02-002
- required reads:
  - validation artifact from TASK-AAFR-02-001
  - completed worker handoffs
- skills:
  - none; docs/handoff task
- expected outputs:
  - integrated readiness handoff
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving handoff names all four workers and preserves provider fail-closed/no fake success boundaries
- done criteria:
  - integrated readiness handoff is accurate and bounded
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-regression: update readiness handoff`
  - handoff: `specs/autonomous-agent-fullstack-regression-readiness/integrated-readiness-handoff.md`
  - checks: focused `rg` proved the handoff names User Admin Access Review, Agent Admin Prompt-Risk, Audit/Trace Summary, and Governance/Policy Impact and preserves provider fail-closed/no fake success boundaries.
  - checks: `git diff --check` passed.

### TASK-AAFR-99-001: Verify AutonomousAgent regression readiness

- status: pending
- source: mini-project verification loop
- task brief: specs/autonomous-agent-fullstack-regression-readiness/tasks/99-verification/01-verify-regression-readiness.md
- depends on:
  - TASK-AAFR-01-001
  - TASK-AAFR-02-001
  - TASK-AAFR-02-002
  - TASK-AAFR-03-001
- required reads:
  - all mini-project artifacts
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - review full backend/frontend validation evidence
  - focused scans for all four AutonomousAgent verticals and stale regression language
- done criteria:
  - mini-project done state is assessed
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-regression: verify readiness`

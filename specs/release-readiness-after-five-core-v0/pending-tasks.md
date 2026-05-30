# Pending Tasks: Release Readiness After Five-Core v0

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `release-readiness: <short task title>`.

## Tasks

### TASK-REL-00-001: Create release-readiness planning scaffold

- status: done
- source: user accepted recommendation to run release-readiness before full-core hardening
- task brief: specs/release-readiness-after-five-core-v0/tasks/00-planning/00-create-release-readiness-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - docs/skills-pack-developer-guide.md
  - docs/skills-pack-user-guide.md
  - specs/core-prd-workstream-reconciliation/pending-tasks.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/release-readiness-after-five-core-v0/README.md
  - specs/release-readiness-after-five-core-v0/conversation-capture.md
  - specs/release-readiness-after-five-core-v0/pending-tasks.md
  - specs/release-readiness-after-five-core-v0/sprints/*.md
  - specs/release-readiness-after-five-core-v0/backlog/*.md
  - specs/release-readiness-after-five-core-v0/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlog, task briefs, pending queue, and terminal verification
  - planning scaffold is committed
- notes:
  - commit message: `release-readiness: add validation queue`

### TASK-REL-01-001: Run starter and pack validation

- status: done
- source: specs/release-readiness-after-five-core-v0/backlog/01-release-readiness-backlog.md
- task brief: specs/release-readiness-after-five-core-v0/tasks/01-validation/01-run-starter-and-pack-validation.md
- depends on: [TASK-REL-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/release-readiness-after-five-core-v0/README.md
  - docs/skills-pack-developer-guide.md
  - tools/validate-ai-first-saas-starter-fullstack.sh
  - tools/check-version-consistency.sh
  - tools/build-pack.sh
- skills:
  - none; repository validation task
- expected outputs:
  - specs/release-readiness-after-five-core-v0/validation-results.md
  - bounded fixes or appended blocker tasks only for validation failures directly blocking release
- required checks:
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
  - `bash tools/check-version-consistency.sh`
  - `bash tools/build-pack.sh --clean --no-archive`
  - `git diff --check`
- done criteria:
  - validation results record exact pass/fail/skip evidence
  - generated `dist/` artifacts are not committed
  - task changes and queue update are committed
- notes:
  - commit message: `release-readiness: run starter pack validation`
  - validation recorded in `specs/release-readiness-after-five-core-v0/validation-results.md`
  - fixed direct release blocker by syncing `templates/ai-first-saas-starter/frontend/src` from `frontend/src` so `tools/build-pack.sh --clean --no-archive` passes

### TASK-REL-01-002: Validate source install and scaffold behavior

- status: done
- source: specs/release-readiness-after-five-core-v0/backlog/01-release-readiness-backlog.md
- task brief: specs/release-readiness-after-five-core-v0/tasks/01-validation/02-validate-install-and-scaffold.md
- depends on: [TASK-REL-01-001]
- required reads:
  - specs/release-readiness-after-five-core-v0/validation-results.md
  - docs/skills-pack-developer-guide.md
  - install.sh
  - tools/scaffold-ai-first-saas-starter.sh
  - templates/ai-first-saas-starter/README.md
- skills:
  - none; repository validation task
- expected outputs:
  - updated specs/release-readiness-after-five-core-v0/validation-results.md
  - bounded fixes or appended blocker tasks only for install/scaffold failures directly blocking release
- required checks:
  - source install into a disposable target with `bash install.sh --location project --project <tmp-target>`
  - installed `.agents/bin/scaffold-ai-first-saas-starter.sh --target <tmp-target> --app-name "Release Smoke" --base-package ai.first --dry-run`
  - non-dry-run scaffold into a separate disposable empty target when safe, then verify `specs/scaffold-report.md`, `pom.xml`, `src/`, and `frontend/`
  - `git diff --check`
- done criteria:
  - install and scaffold behavior are validated or blockers are recorded
  - no disposable target artifacts are committed
  - task changes and queue update are committed
- notes:
  - commit message: `release-readiness: validate install scaffold`
  - validation recorded in `specs/release-readiness-after-five-core-v0/validation-results.md`
  - source install, installed scaffold dry-run, installed scaffold non-dry-run, rendered path smoke, and `git diff --check` passed

### TASK-REL-02-001: Review release docs and write handoff

- status: done
- source: specs/release-readiness-after-five-core-v0/backlog/01-release-readiness-backlog.md
- task brief: specs/release-readiness-after-five-core-v0/tasks/02-handoff/01-review-docs-write-handoff.md
- depends on: [TASK-REL-01-002]
- required reads:
  - specs/release-readiness-after-five-core-v0/validation-results.md
  - README.md
  - docs/skills-pack-user-guide.md
  - docs/skills-pack-developer-guide.md
  - pack/README.md
  - pack/AGENTS.md
  - pack/manifest.yaml
  - docs/examples/core-ai-first-saas-input/README.md
  - docs/examples/ai-first-saas-core-app-domain/README.md
- skills:
  - none; repository docs/release task
- expected outputs:
  - specs/release-readiness-after-five-core-v0/release-handoff.md
  - focused doc fixes only if stale release-facing guidance is found
- required checks:
  - `git diff --check`
  - `rg -n "0.2.12|five core|core v0|workstream-oriented core-app domain|older module-sequenced|scaffold-ai-first-saas-starter|validate-ai-first-saas-starter-fullstack" README.md docs/skills-pack-user-guide.md docs/skills-pack-developer-guide.md pack/README.md pack/AGENTS.md pack/manifest.yaml docs/examples/core-ai-first-saas-input/README.md docs/examples/ai-first-saas-core-app-domain/README.md specs/release-readiness-after-five-core-v0/release-handoff.md`
- done criteria:
  - release-handoff states whether repository is ready to cut a version or names blockers
  - release-facing docs are coherent with five-core v0 and core PRD reconciliation
  - task changes and queue update are committed
- notes:
  - commit message: `release-readiness: write release handoff`
  - release handoff recorded in `specs/release-readiness-after-five-core-v0/release-handoff.md`
  - no release-facing doc fixes were required

### TASK-REL-99-001: Verify release-readiness completion

- status: done
- source: mini-project verification loop
- task brief: specs/release-readiness-after-five-core-v0/tasks/99-verification/01-verify-release-readiness.md
- depends on:
  - TASK-REL-01-001
  - TASK-REL-01-002
  - TASK-REL-02-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/release-readiness-after-five-core-v0/README.md
  - specs/release-readiness-after-five-core-v0/conversation-capture.md
  - specs/release-readiness-after-five-core-v0/pending-tasks.md
  - specs/release-readiness-after-five-core-v0/sprints/*.md
  - specs/release-readiness-after-five-core-v0/backlog/*.md
  - specs/release-readiness-after-five-core-v0/tasks/**/*.md
  - specs/release-readiness-after-five-core-v0/validation-results.md
  - specs/release-readiness-after-five-core-v0/release-handoff.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/release-readiness-after-five-core-v0/pending-tasks.md
  - verification notes or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - any targeted checks needed to validate recorded handoff claims
- done criteria:
  - validation evidence and release handoff are compared against mini-project done state
  - if ready, completion is recorded with no new required work
  - if not ready, bounded blocker tasks are appended before a new terminal verification task
  - task changes and queue update are committed
- notes:
  - commit message: `release-readiness: verify completion`
  - verification recorded in `specs/release-readiness-after-five-core-v0/verification-notes.md`
  - no release blockers or follow-up tasks were required

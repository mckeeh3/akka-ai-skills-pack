# Pending Tasks: Pack Release Publication

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `pack-release: <short task title>`.

## Tasks

### TASK-PRP-00-001: Create pack release publication queue

- status: done
- source: current conversation after starter-wide release readiness; user accepted pack release publication mini-project
- task brief: specs/pack-release-publication/tasks/00-planning/00-create-pack-release-publication-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - specs/ai-first-saas-starter-release-readiness/starter-release-notes.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/pack-release-publication/README.md
  - specs/pack-release-publication/conversation-capture.md
  - specs/pack-release-publication/pending-tasks.md
  - specs/pack-release-publication/sprints/01-pack-release-publication-sprint.md
  - specs/pack-release-publication/backlog/01-pack-release-publication-backlog.md
  - specs/pack-release-publication/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and queue exist
  - task changes and queue update are committed
- notes:
  - commit message: `pack-release: add publication queue`

### TASK-PRP-01-001: Review package metadata and resources

- status: done
- source: specs/pack-release-publication/backlog/01-pack-release-publication-backlog.md
- task brief: specs/pack-release-publication/tasks/01-package/01-review-package-metadata-resources.md
- depends on:
  - TASK-PRP-00-001
- required reads:
  - specs/pack-release-publication/README.md
  - specs/pack-release-publication/tasks/01-package/01-review-package-metadata-resources.md
  - specs/ai-first-saas-starter-release-readiness/starter-release-notes.md
  - pack/
  - package/install/scaffold scripts and docs
- skills:
  - none; package review task
- expected outputs:
  - package resource review artifact
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused scans for starter template/resource references and source-only `specs/` leakage into packaged assets
- done criteria:
  - package/resource inclusion and leakage boundaries are assessed
  - task changes and queue update are committed
- notes:
  - package resource review artifact: `specs/pack-release-publication/package-resource-review.md`
  - synchronized `frontend/src` with `templates/ai-first-saas-starter/frontend/src` so the package build resource guard passes
  - commit message: `pack-release: review package resources`

### TASK-PRP-02-001: Run package install scaffold smoke

- status: done
- source: specs/pack-release-publication/backlog/01-pack-release-publication-backlog.md
- task brief: specs/pack-release-publication/tasks/02-validation/01-run-package-install-scaffold-smoke.md
- depends on:
  - TASK-PRP-01-001
- required reads:
  - package resource review artifact from TASK-PRP-01-001
  - specs/pack-release-publication/tasks/02-validation/01-run-package-install-scaffold-smoke.md
- skills:
  - none; package validation task
- expected outputs:
  - package smoke validation artifact
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - package/install command checks appropriate for this repo
  - scaffold smoke from packaged resources or equivalent installed-pack path
  - focused scans confirming starter scaffold files and docs are present where expected
- done criteria:
  - package/install/scaffold smoke evidence is captured or blockers appended
  - task changes and queue update are committed
- notes:
  - package smoke validation artifact: `specs/pack-release-publication/package-smoke-validation.md`
  - package build, project install, installed-pack scaffold, scaffold backend tests, frontend dependency install, frontend typecheck, and frontend contract tests passed
  - commit message: `pack-release: run package smoke`

### TASK-PRP-03-001: Create release changelog and handoff

- status: done
- source: specs/pack-release-publication/backlog/01-pack-release-publication-backlog.md
- task brief: specs/pack-release-publication/tasks/03-docs/01-create-release-changelog-handoff.md
- depends on:
  - TASK-PRP-02-001
- required reads:
  - starter release notes
  - package validation artifact from TASK-PRP-02-001
- skills:
  - none; release docs task
- expected outputs:
  - release changelog/handoff artifact
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused scan proving changelog covers current starter capabilities, validation evidence, install/scaffold instructions, and future-work boundaries
- done criteria:
  - release handoff is clear, bounded, and validated
  - task changes and queue update are committed
- notes:
  - release changelog/handoff artifact: `specs/pack-release-publication/release-handoff.md`
  - focused scan confirmed current starter capabilities, validation evidence, install/scaffold instructions, and future-work boundaries are covered
  - commit message: `pack-release: add release handoff`

### TASK-PRP-99-001: Verify pack release publication readiness

- status: done
- source: mini-project verification loop
- task brief: specs/pack-release-publication/tasks/99-verification/01-verify-pack-release-publication.md
- depends on:
  - TASK-PRP-01-001
  - TASK-PRP-02-001
  - TASK-PRP-03-001
- required reads:
  - all mini-project artifacts
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - review package resource check, package smoke validation, changelog/handoff, and queue state
  - focused scans for source-only leakage and stale release blockers
- done criteria:
  - mini-project done state is assessed
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - verified required mini-project artifacts are present: package resource review, package smoke validation, and release handoff
  - queue state has no remaining pending publication tasks after this terminal verification
  - focused scans found no stale release blocker; prior leakage scans remain documented as 0 unexpected source-only `specs/`, `akka-context`, `node_modules`, or `.env.local` packaged/installed paths
  - no follow-up task required for the current release-publication scope
  - commit message: `pack-release: verify publication readiness`

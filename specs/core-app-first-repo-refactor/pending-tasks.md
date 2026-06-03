# Pending Tasks: Core App First Repository Refactor

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `layout: <short task title>`.
- Runtime features are complete only when the real local Akka/API/UI path works at the stated scope; this refactor should preserve existing core app behavior rather than relying on duplicated template/runtime substitutes.

## Tasks

### TASK-LAYOUT-00-001: Create core app first refactor planning scaffold

- status: done
- source: user discussion about flipping the repo into a runnable core app and isolating skills-pack assets under `skills-pack/`
- task brief: specs/core-app-first-repo-refactor/tasks/00-planning/00-create-core-app-first-refactor-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - docs/pending-question-queue.md
  - conversation context about the core-app-first refactor
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/core-app-first-repo-refactor/README.md
  - specs/core-app-first-repo-refactor/conversation-capture.md
  - specs/core-app-first-repo-refactor/pending-tasks.md
  - specs/core-app-first-repo-refactor/sprints/*.md
  - specs/core-app-first-repo-refactor/backlog/*.md
  - specs/core-app-first-repo-refactor/tasks/**/*.md
- required checks:
  - `git diff --check`
  - `find specs/core-app-first-repo-refactor -maxdepth 3 -type f -print | sort`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlog, task briefs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `layout: add core app first refactor queue`
  - scaffold created and committed with its queue-status update

### TASK-LAYOUT-01-001: Define target repository layout and path map

- status: done
- source: specs/core-app-first-repo-refactor/backlog/01-core-app-first-refactor-build-backlog.md
- task brief: specs/core-app-first-repo-refactor/tasks/01-architecture-inventory/01-target-layout-and-path-map.md
- depends on: [TASK-LAYOUT-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-app-first-repo-refactor/README.md
  - specs/core-app-first-repo-refactor/conversation-capture.md
  - specs/core-app-first-repo-refactor/sprints/01-architecture-inventory-sprint.md
  - specs/core-app-first-repo-refactor/backlog/01-core-app-first-refactor-build-backlog.md
  - specs/core-app-first-repo-refactor/tasks/01-architecture-inventory/01-target-layout-and-path-map.md
  - templates/ai-first-saas-starter/README.md
  - templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md
- skills:
  - none; repository architecture planning task
- expected outputs:
  - specs/core-app-first-repo-refactor/target-layout-and-path-map.md
- required checks:
  - `git diff --check`
- done criteria:
  - target root app layout, `skills-pack/` layout, fixed package policy, old-to-new path map, and extension-boundary model are documented
  - queue is updated and committed
- notes:
  - commit message: `layout: define core app first path map`
  - documented target root app layout, `skills-pack/` layout, fixed `ai.first` package policy, old-to-new path map, full-app template dissolution policy, and domain extension boundaries
  - `git diff --check` passed

### TASK-LAYOUT-01-002: Inventory migration assets and classify actions

- status: done
- source: specs/core-app-first-repo-refactor/backlog/01-core-app-first-refactor-build-backlog.md
- task brief: specs/core-app-first-repo-refactor/tasks/01-architecture-inventory/02-asset-inventory.md
- depends on: [TASK-LAYOUT-01-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-app-first-repo-refactor/target-layout-and-path-map.md
  - specs/core-app-first-repo-refactor/tasks/01-architecture-inventory/02-asset-inventory.md
- skills:
  - none; repository inventory task
- expected outputs:
  - specs/core-app-first-repo-refactor/asset-migration-inventory.md
- required checks:
  - `git diff --check`
- done criteria:
  - app, template, skills-pack, examples, docs, tools, packaging, and active-spec reference areas are classified for migration
  - queue is updated and committed
- notes:
  - commit message: `layout: inventory refactor assets`
  - classified app, template, skills-pack, examples, docs, tools, packaging, generated/local artifacts, and active-spec reference areas in `asset-migration-inventory.md`
  - `git diff --check` passed

### TASK-LAYOUT-02-001: Promote core app source to repository root

- status: done
- source: specs/core-app-first-repo-refactor/backlog/01-core-app-first-refactor-build-backlog.md
- task brief: specs/core-app-first-repo-refactor/tasks/02-core-root/01-promote-core-app-root.md
- depends on: [TASK-LAYOUT-01-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-app-first-repo-refactor/target-layout-and-path-map.md
  - specs/core-app-first-repo-refactor/asset-migration-inventory.md
  - specs/core-app-first-repo-refactor/tasks/02-core-root/01-promote-core-app-root.md
  - templates/ai-first-saas-starter/README.md
- skills:
  - none; repository source-layout refactor task
- expected outputs:
  - root `pom.xml`, `src/**`, `frontend/**`, and app build-resource updates as needed
- required checks:
  - `git diff --check`
  - root backend focused tests or full `mvn test` when practical
  - root frontend `npm test`, `npm run typecheck`, and `npm run build` when practical
- done criteria:
  - root app is the canonical runnable core app at the migrated scope
  - queue is updated and committed
- notes:
  - commit message: `layout: promote core app root source`
  - promoted starter backend into root `src/main/java/ai/first/**`, `src/test/java/ai/first/**`, root resources, and canonical root `pom.xml` with fixed `ai.first` coordinates
  - promoted starter `app-description/**` because root frontend contract tests now treat it as the canonical app description source
  - reconciled root `frontend/**` with the starter frontend, added frontend build scripts, and regenerated root Akka static resources with `npm run build`
  - retained existing `com.example` focused examples in place for the later example-relocation task, but excluded them from the root app Maven compile/test path to avoid duplicate Akka setup/component registration
  - parity note: rendered starter backend and app-description match the promoted root copies; remaining root/template frontend diffs are root-path contract-test adjustments plus local `frontend/.env.local`
  - checks passed: `git diff --check`; `mvn test`; `cd frontend && npm test -- --run && npm run typecheck && npm run build`

### TASK-LAYOUT-02-002: Dissolve the full-app starter template

- status: pending
- source: specs/core-app-first-repo-refactor/backlog/01-core-app-first-refactor-build-backlog.md
- task brief: specs/core-app-first-repo-refactor/tasks/02-core-root/02-dissolve-full-app-template.md
- depends on: [TASK-LAYOUT-02-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-app-first-repo-refactor/target-layout-and-path-map.md
  - specs/core-app-first-repo-refactor/asset-migration-inventory.md
  - specs/core-app-first-repo-refactor/tasks/02-core-root/02-dissolve-full-app-template.md
  - templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md
  - tools/scaffold-ai-first-saas-starter.sh
  - install.sh
- skills:
  - none; repository source-layout refactor task
- expected outputs:
  - full-app template removed or archived according to path map
  - scaffold/template references removed or reclassified
- required checks:
  - `git diff --check`
  - search proof for remaining `templates/ai-first-saas-starter` references and their classification
- done criteria:
  - there is no maintained second full-app source copy
  - queue is updated and committed
- notes:
  - commit message: `layout: dissolve full app template`

### TASK-LAYOUT-03-001: Move skills-pack assets under top-level skills-pack

- status: pending
- source: specs/core-app-first-repo-refactor/backlog/01-core-app-first-refactor-build-backlog.md
- task brief: specs/core-app-first-repo-refactor/tasks/03-skills-pack-isolation/01-move-skills-pack-assets.md
- depends on: [TASK-LAYOUT-02-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-app-first-repo-refactor/target-layout-and-path-map.md
  - specs/core-app-first-repo-refactor/asset-migration-inventory.md
  - specs/core-app-first-repo-refactor/tasks/03-skills-pack-isolation/01-move-skills-pack-assets.md
  - install.sh
  - pack/AGENTS.md
- skills:
  - none; repository source-layout refactor task
- expected outputs:
  - `skills-pack/**` assets
  - updated install/package path references
- required checks:
  - `git diff --check`
  - install/package dry-run or equivalent path-resolution check if available
- done criteria:
  - skills-pack development and maintenance assets are isolated under `skills-pack/`
  - queue is updated and committed
- notes:
  - commit message: `layout: isolate skills pack assets`

### TASK-LAYOUT-03-002: Relocate focused Akka component examples

- status: pending
- source: specs/core-app-first-repo-refactor/backlog/01-core-app-first-refactor-build-backlog.md
- task brief: specs/core-app-first-repo-refactor/tasks/03-skills-pack-isolation/02-relocate-focused-akka-examples.md
- depends on: [TASK-LAYOUT-03-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-app-first-repo-refactor/target-layout-and-path-map.md
  - specs/core-app-first-repo-refactor/asset-migration-inventory.md
  - specs/core-app-first-repo-refactor/tasks/03-skills-pack-isolation/02-relocate-focused-akka-examples.md
  - root `src/main/java/com/example/**` and `src/test/java/com/example/**` inventory
- skills:
  - none; repository example-layout refactor task
- expected outputs:
  - focused Akka examples under skills-pack internal examples path
  - updated skill references to example paths
- required checks:
  - `git diff --check`
  - search proof for updated example references
- done criteria:
  - root `src/` no longer mixes runnable core app source with skills-pack reference examples
  - queue is updated and committed
- notes:
  - commit message: `layout: relocate akka reference examples`

### TASK-LAYOUT-04-001: Update root app docs and domain extension guidance

- status: pending
- source: specs/core-app-first-repo-refactor/backlog/01-core-app-first-refactor-build-backlog.md
- task brief: specs/core-app-first-repo-refactor/tasks/04-guidance-tooling/01-update-root-app-docs.md
- depends on: [TASK-LAYOUT-03-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-app-first-repo-refactor/target-layout-and-path-map.md
  - specs/core-app-first-repo-refactor/tasks/04-guidance-tooling/01-update-root-app-docs.md
  - root README and app docs after migration
- skills:
  - none; repository documentation task
- expected outputs:
  - updated root README/guidance/domain-extension docs
- required checks:
  - `git diff --check`
- done criteria:
  - root docs explain run, fork-and-extend, upstream merge, and domain-specific extension boundaries
  - queue is updated and committed
- notes:
  - commit message: `layout: update core app extension docs`

### TASK-LAYOUT-04-002: Update skills-pack guidance, install, and validation tooling

- status: pending
- source: specs/core-app-first-repo-refactor/backlog/01-core-app-first-refactor-build-backlog.md
- task brief: specs/core-app-first-repo-refactor/tasks/04-guidance-tooling/02-update-skills-pack-guidance-and-tooling.md
- depends on: [TASK-LAYOUT-04-001]
- required reads:
  - AGENTS.md
  - skills/README.md or moved equivalent
  - specs/core-app-first-repo-refactor/target-layout-and-path-map.md
  - specs/core-app-first-repo-refactor/tasks/04-guidance-tooling/02-update-skills-pack-guidance-and-tooling.md
  - moved skills-pack docs, pack guidance, install scripts, and validation scripts
- skills:
  - none; repository guidance/tooling task
- expected outputs:
  - updated skills-pack guidance, install/package scripts, validation scripts, and stale scaffold/template reference cleanup
- required checks:
  - `git diff --check`
  - package/install dry-run or equivalent
  - search proof for stale scaffold/template claims
- done criteria:
  - skills-pack guidance and tooling consistently support the core-app-first model
  - queue is updated and committed
- notes:
  - commit message: `layout: update skills pack tooling`

### TASK-LAYOUT-99-001: Verify core app first refactor completion

- status: pending
- source: mini-project verification loop
- task brief: specs/core-app-first-repo-refactor/tasks/05-validation/01-validate-refactor-completion.md
- depends on:
  - TASK-LAYOUT-04-002
- required reads:
  - AGENTS.md
  - skills/README.md or moved equivalent
  - specs/core-app-first-repo-refactor/README.md
  - specs/core-app-first-repo-refactor/conversation-capture.md
  - specs/core-app-first-repo-refactor/pending-tasks.md
  - specs/core-app-first-repo-refactor/sprints/*.md
  - specs/core-app-first-repo-refactor/backlog/*.md
  - specs/core-app-first-repo-refactor/tasks/**/*.md
  - specs/core-app-first-repo-refactor/target-layout-and-path-map.md
  - specs/core-app-first-repo-refactor/asset-migration-inventory.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/core-app-first-repo-refactor/pending-tasks.md
  - completion summary, verification notes, or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - root backend validation command selected by migrated docs
  - root frontend validation command selected by migrated docs
  - skills-pack install/package validation or documented blocker
  - search proof for stale `templates/ai-first-saas-starter` and scaffold-first claims
- done criteria:
  - task group/sprint goals have been compared against completed work
  - mini-project done state has been compared against completed work
  - unresolved questions/blockers have been reviewed
  - if complete, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
- notes:
  - commit message: `layout: verify core app first refactor`

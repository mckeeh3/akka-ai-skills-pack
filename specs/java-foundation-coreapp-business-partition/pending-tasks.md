# Pending Tasks: Java Foundation/Coreapp/Business Package Partition

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `packages: <short task title>`.
- Package-migration tasks should be as mechanical as possible and preserve existing runtime behavior.

## Tasks

### TASK-PACKAGES-00-001: Create package partition planning scaffold

- status: done
- source: user accepted the `foundation`, `coreapp`, and `business` package partition model inside standard Akka `api` / `application` / `domain` layers
- task brief: specs/java-foundation-coreapp-business-partition/tasks/00-planning/00-create-package-partition-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills-pack/skills/README.md
  - skills-pack/docs/pending-task-queue.md
  - skills-pack/docs/pending-question-queue.md
  - conversation context agreeing to `foundation`, `coreapp`, and `business`
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/java-foundation-coreapp-business-partition/README.md
  - specs/java-foundation-coreapp-business-partition/conversation-capture.md
  - specs/java-foundation-coreapp-business-partition/pending-tasks.md
  - specs/java-foundation-coreapp-business-partition/sprints/*.md
  - specs/java-foundation-coreapp-business-partition/backlog/*.md
  - specs/java-foundation-coreapp-business-partition/tasks/**/*.md
- required checks:
  - `git diff --check`
  - `find specs/java-foundation-coreapp-business-partition -maxdepth 3 -type f -print | sort`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlog, task briefs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `packages: add foundation coreapp business queue`
  - scaffold completed and committed with the queue-status update

### TASK-PACKAGES-01-001: Inventory classes and define package map

- status: done
- source: specs/java-foundation-coreapp-business-partition/backlog/01-package-partition-build-backlog.md
- task brief: specs/java-foundation-coreapp-business-partition/tasks/01-design-inventory/01-inventory-and-package-map.md
- depends on: [TASK-PACKAGES-00-001]
- required reads:
  - AGENTS.md
  - skills-pack/skills/README.md
  - specs/java-foundation-coreapp-business-partition/README.md
  - specs/java-foundation-coreapp-business-partition/conversation-capture.md
  - specs/java-foundation-coreapp-business-partition/sprints/01-design-inventory-sprint.md
  - specs/java-foundation-coreapp-business-partition/backlog/01-package-partition-build-backlog.md
  - specs/java-foundation-coreapp-business-partition/tasks/01-design-inventory/01-inventory-and-package-map.md
  - src/main/java/ai/first/**
  - src/test/java/ai/first/**
- skills:
  - none; repository architecture planning task
- expected outputs:
  - specs/java-foundation-coreapp-business-partition/classification-and-package-map.md
- required checks:
  - `git diff --check`
- done criteria:
  - current classes are classified and old-to-new package map is documented
  - ambiguous classes are resolved or flagged with bounded recommendations
  - queue is updated and committed
- notes:
  - commit message: `packages: map foundation coreapp classes`
  - completed class inventory, package map, ambiguity notes, migration order, and stale-reference search patterns
  - checks: `git diff --check`

### TASK-PACKAGES-02-001: Move foundation domain packages

- status: pending
- source: specs/java-foundation-coreapp-business-partition/backlog/01-package-partition-build-backlog.md
- task brief: specs/java-foundation-coreapp-business-partition/tasks/02-foundation/01-move-foundation-domain.md
- depends on: [TASK-PACKAGES-01-001]
- required reads:
  - AGENTS.md
  - specs/java-foundation-coreapp-business-partition/README.md
  - specs/java-foundation-coreapp-business-partition/classification-and-package-map.md
  - specs/java-foundation-coreapp-business-partition/sprints/02-foundation-migration-sprint.md
  - specs/java-foundation-coreapp-business-partition/tasks/02-foundation/01-move-foundation-domain.md
- skills:
  - none; mechanical Java package refactor task
- expected outputs:
  - `src/main/java/ai/first/domain/foundation/**`
  - updated imports/tests
- required checks:
  - `git diff --check`
  - `mvn test`
  - search proof for moved old foundation domain packages
- done criteria:
  - mapped foundation domain classes live under `ai.first.domain.foundation.*`
  - queue is updated and committed
- notes:
  - commit message: `packages: move foundation domain layer`

### TASK-PACKAGES-02-002: Move foundation application and API packages

- status: pending
- source: specs/java-foundation-coreapp-business-partition/backlog/01-package-partition-build-backlog.md
- task brief: specs/java-foundation-coreapp-business-partition/tasks/02-foundation/02-move-foundation-application-api.md
- depends on: [TASK-PACKAGES-02-001]
- required reads:
  - AGENTS.md
  - specs/java-foundation-coreapp-business-partition/README.md
  - specs/java-foundation-coreapp-business-partition/classification-and-package-map.md
  - specs/java-foundation-coreapp-business-partition/sprints/02-foundation-migration-sprint.md
  - specs/java-foundation-coreapp-business-partition/tasks/02-foundation/02-move-foundation-application-api.md
- skills:
  - none; mechanical Java package refactor task
- expected outputs:
  - `src/main/java/ai/first/application/foundation/**`
  - `src/main/java/ai/first/api/foundation/**`
  - updated imports/tests/resources
- required checks:
  - `git diff --check`
  - `mvn test`
  - stale old-package search proof for moved foundation application/API packages
- done criteria:
  - mapped foundation application/API classes live under `*.foundation.*` packages
  - queue is updated and committed
- notes:
  - commit message: `packages: move foundation application api layers`

### TASK-PACKAGES-03-001: Move core app packages

- status: pending
- source: specs/java-foundation-coreapp-business-partition/backlog/01-package-partition-build-backlog.md
- task brief: specs/java-foundation-coreapp-business-partition/tasks/03-coreapp/01-move-coreapp-packages.md
- depends on: [TASK-PACKAGES-02-002]
- required reads:
  - AGENTS.md
  - specs/java-foundation-coreapp-business-partition/README.md
  - specs/java-foundation-coreapp-business-partition/classification-and-package-map.md
  - specs/java-foundation-coreapp-business-partition/sprints/03-coreapp-migration-sprint.md
  - specs/java-foundation-coreapp-business-partition/tasks/03-coreapp/01-move-coreapp-packages.md
  - app-description/12-workstreams/functional-agents.md
- skills:
  - none; mechanical Java package refactor task
- expected outputs:
  - `src/main/java/ai/first/api/coreapp/**`
  - `src/main/java/ai/first/application/coreapp/**`
  - `src/main/java/ai/first/domain/coreapp/**` if coreapp-specific domain types exist
  - updated imports/tests/resources
- required checks:
  - `git diff --check`
  - `mvn test`
  - frontend tests/typecheck/build if API DTO/package references affect frontend contract tests
  - stale old-package search proof for moved coreapp packages
- done criteria:
  - mapped built-in core app classes live under `*.coreapp.*` packages
  - queue is updated and committed
- notes:
  - commit message: `packages: move core app packages`

### TASK-PACKAGES-04-001: Add business boundary docs and checks

- status: pending
- source: specs/java-foundation-coreapp-business-partition/backlog/01-package-partition-build-backlog.md
- task brief: specs/java-foundation-coreapp-business-partition/tasks/04-business-boundaries/01-add-business-boundary-docs-checks.md
- depends on: [TASK-PACKAGES-03-001]
- required reads:
  - AGENTS.md
  - README.md
  - specs/java-foundation-coreapp-business-partition/README.md
  - specs/java-foundation-coreapp-business-partition/classification-and-package-map.md
  - specs/java-foundation-coreapp-business-partition/sprints/04-business-boundaries-sprint.md
  - specs/java-foundation-coreapp-business-partition/tasks/04-business-boundaries/01-add-business-boundary-docs-checks.md
- skills:
  - none; package-boundary documentation/check task
- expected outputs:
  - Java package boundary docs, package-info files, or lightweight boundary checks
- required checks:
  - `git diff --check`
  - `mvn test`
  - boundary search/check command if added
- done criteria:
  - users can identify where business-specific Java code belongs
  - foundation/coreapp/business dependency rules are documented and checked where practical
  - queue is updated and committed
- notes:
  - commit message: `packages: document business boundaries`

### TASK-PACKAGES-05-001: Update docs and skills-pack package references

- status: pending
- source: specs/java-foundation-coreapp-business-partition/backlog/01-package-partition-build-backlog.md
- task brief: specs/java-foundation-coreapp-business-partition/tasks/05-docs-validation/01-update-docs-and-skills-pack-references.md
- depends on: [TASK-PACKAGES-04-001]
- required reads:
  - AGENTS.md
  - README.md
  - skills-pack/skills/README.md
  - specs/java-foundation-coreapp-business-partition/README.md
  - specs/java-foundation-coreapp-business-partition/classification-and-package-map.md
  - specs/java-foundation-coreapp-business-partition/sprints/05-docs-validation-sprint.md
  - specs/java-foundation-coreapp-business-partition/tasks/05-docs-validation/01-update-docs-and-skills-pack-references.md
- skills:
  - none; repository documentation task
- expected outputs:
  - updated root docs/app-description/specs and skills-pack package guidance
- required checks:
  - `git diff --check`
  - stale package-reference search proof
  - skills-pack install/build checks if pack docs/tooling are touched substantially
- done criteria:
  - documentation consistently explains foundation/coreapp/business from an outside-in user perspective
  - queue is updated and committed
- notes:
  - commit message: `packages: update package guidance`

### TASK-PACKAGES-99-001: Verify package partition completion

- status: pending
- source: mini-project verification loop
- task brief: specs/java-foundation-coreapp-business-partition/tasks/05-docs-validation/02-verify-package-partition-completion.md
- depends on:
  - TASK-PACKAGES-05-001
- required reads:
  - AGENTS.md
  - skills-pack/skills/README.md
  - specs/java-foundation-coreapp-business-partition/README.md
  - specs/java-foundation-coreapp-business-partition/conversation-capture.md
  - specs/java-foundation-coreapp-business-partition/pending-tasks.md
  - specs/java-foundation-coreapp-business-partition/sprints/*.md
  - specs/java-foundation-coreapp-business-partition/backlog/*.md
  - specs/java-foundation-coreapp-business-partition/tasks/**/*.md
  - specs/java-foundation-coreapp-business-partition/classification-and-package-map.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/java-foundation-coreapp-business-partition/pending-tasks.md
  - completion summary, verification notes, or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - `mvn test`
  - stale old-package search proof
  - dependency-boundary check/search if added
  - frontend/skills-pack checks when touched by package refs
- done criteria:
  - current task group and overall mini-project done state are checked
  - if complete, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
  - queue is updated and committed
- notes:
  - commit message: `packages: verify package partition`

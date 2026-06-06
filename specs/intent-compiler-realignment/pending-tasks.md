# Pending Tasks: Intent Compiler Realignment

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, conversation capture, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `intent-compiler: <short task title>`.

## Tasks

### TASK-IC-00-001: Create intent compiler realignment planning scaffold

- status: done
- source: discussion about Capturing Incremental Intent, intent compiler, app-description structure, and archiving/rebuilding old intent docs/skills
- task brief: specs/intent-compiler-realignment/tasks/00-planning/00-create-intent-compiler-realignment-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - skills-pack/docs/intent-compiler-working-note.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/intent-compiler-realignment/README.md
  - specs/intent-compiler-realignment/conversation-capture.md
  - specs/intent-compiler-realignment/pending-tasks.md
  - specs/intent-compiler-realignment/sprints/*.md
  - specs/intent-compiler-realignment/backlog/*.md
  - specs/intent-compiler-realignment/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlog, task briefs, and pending queue
  - terminal verification task exists
  - task changes and queue update are committed
- notes:
  - commit message: `intent-compiler: add realignment queue`

### TASK-IC-01-001: Inventory intent-processing artifacts and archive strategy

- status: done
- source: specs/intent-compiler-realignment/backlog/01-intent-compiler-realignment-build-backlog.md
- task brief: specs/intent-compiler-realignment/tasks/01-inventory-archive/01-inventory-intent-processing-artifacts.md
- depends on:
  - TASK-IC-00-001
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/intent-compiler-realignment/README.md
  - specs/intent-compiler-realignment/conversation-capture.md
  - specs/intent-compiler-realignment/pending-tasks.md
  - specs/intent-compiler-realignment/sprints/01-inventory-and-archive-plan-sprint.md
  - specs/intent-compiler-realignment/backlog/01-intent-compiler-realignment-build-backlog.md
  - specs/intent-compiler-realignment/tasks/01-inventory-archive/01-inventory-intent-processing-artifacts.md
  - skills-pack/docs/intent-compiler-working-note.md
- skills:
  - none; repository planning/audit task
- expected outputs:
  - specs/intent-compiler-realignment/intent-processing-inventory.md
  - updated specs/intent-compiler-realignment/pending-tasks.md
- required checks:
  - `git diff --check`
- done criteria:
  - intent-processing skills/docs are inventoried and classified
  - archive/replacement mechanics and validation risks are documented
  - queue is updated and task changes are committed
- notes:
  - commit message: `intent-compiler: inventory intent-processing artifacts`

### TASK-IC-02-001: Create canonical intent compiler docs

- status: done
- source: specs/intent-compiler-realignment/backlog/01-intent-compiler-realignment-build-backlog.md
- task brief: specs/intent-compiler-realignment/tasks/02-canonical-docs/01-create-canonical-intent-compiler-docs.md
- depends on:
  - TASK-IC-01-001
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/intent-compiler-realignment/README.md
  - specs/intent-compiler-realignment/conversation-capture.md
  - specs/intent-compiler-realignment/pending-tasks.md
  - specs/intent-compiler-realignment/intent-processing-inventory.md
  - specs/intent-compiler-realignment/sprints/02-canonical-docs-sprint.md
  - specs/intent-compiler-realignment/tasks/02-canonical-docs/01-create-canonical-intent-compiler-docs.md
  - skills-pack/docs/intent-compiler-working-note.md
- skills:
  - none; skills-pack documentation task
- expected outputs:
  - canonical skills-pack/docs intent compiler doc set
  - updated references or indexes as needed
  - updated specs/intent-compiler-realignment/pending-tasks.md
- required checks:
  - `git diff --check`
- done criteria:
  - active docs define current-intent, intent graph, workstream binding, and intent-to-code realization
  - queue is updated and task changes are committed
- notes:
  - commit message: `intent-compiler: add canonical docs`

### TASK-IC-02-002: Archive or retire legacy intent docs

- status: done
- source: specs/intent-compiler-realignment/backlog/01-intent-compiler-realignment-build-backlog.md
- task brief: specs/intent-compiler-realignment/tasks/02-canonical-docs/02-archive-or-retire-legacy-docs.md
- depends on:
  - TASK-IC-02-001
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/intent-compiler-realignment/README.md
  - specs/intent-compiler-realignment/pending-tasks.md
  - specs/intent-compiler-realignment/intent-processing-inventory.md
  - specs/intent-compiler-realignment/sprints/02-canonical-docs-sprint.md
  - specs/intent-compiler-realignment/tasks/02-canonical-docs/02-archive-or-retire-legacy-docs.md
  - canonical intent compiler docs
- skills:
  - none; skills-pack documentation task
- expected outputs:
  - archived/retired legacy docs or deprecation markers
  - updated active doc links
  - updated specs/intent-compiler-realignment/pending-tasks.md
- required checks:
  - `git diff --check`
  - install reference check if doc moves affect skill references
- done criteria:
  - active docs no longer present legacy docs as source of truth
  - queue is updated and task changes are committed
- notes:
  - commit message: `intent-compiler: retire legacy intent docs`

### TASK-IC-03-001: Realign intake and normalization skills

- status: done
- source: specs/intent-compiler-realignment/backlog/01-intent-compiler-realignment-build-backlog.md
- task brief: specs/intent-compiler-realignment/tasks/03-skills-replacement/01-realign-intake-normalization-skills.md
- depends on:
  - TASK-IC-02-002
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/intent-compiler-realignment/README.md
  - specs/intent-compiler-realignment/pending-tasks.md
  - specs/intent-compiler-realignment/intent-processing-inventory.md
  - specs/intent-compiler-realignment/sprints/03-skills-replacement-sprint.md
  - specs/intent-compiler-realignment/tasks/03-skills-replacement/01-realign-intake-normalization-skills.md
  - canonical intent compiler docs
- skills:
  - none; skills-pack skill-maintenance task
- expected outputs:
  - updated/replacement intake and normalization skills
  - updated specs/intent-compiler-realignment/pending-tasks.md
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- done criteria:
  - intake/normalization skills compile incremental input into current intent deltas
  - queue is updated and task changes are committed
- notes:
  - commit message: `intent-compiler: realign intake skills`

### TASK-IC-03-002: Realign app-description capture skills

- status: done
- source: specs/intent-compiler-realignment/backlog/01-intent-compiler-realignment-build-backlog.md
- task brief: specs/intent-compiler-realignment/tasks/03-skills-replacement/02-realign-app-description-capture-skills.md
- depends on:
  - TASK-IC-03-001
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/intent-compiler-realignment/README.md
  - specs/intent-compiler-realignment/pending-tasks.md
  - specs/intent-compiler-realignment/intent-processing-inventory.md
  - specs/intent-compiler-realignment/sprints/03-skills-replacement-sprint.md
  - specs/intent-compiler-realignment/tasks/03-skills-replacement/02-realign-app-description-capture-skills.md
  - canonical intent compiler docs
- skills:
  - none; skills-pack skill-maintenance task
- expected outputs:
  - updated/replacement app-description capture/review/generation skills
  - updated specs/intent-compiler-realignment/pending-tasks.md
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- done criteria:
  - app-description skills use app/domain/workstream/global artifact structure
  - queue is updated and task changes are committed
- notes:
  - commit message: `intent-compiler: realign app-description skills`

### TASK-IC-03-003: Realign planning and queue skills

- status: done
- source: specs/intent-compiler-realignment/backlog/01-intent-compiler-realignment-build-backlog.md
- task brief: specs/intent-compiler-realignment/tasks/03-skills-replacement/03-realign-planning-queue-skills.md
- depends on:
  - TASK-IC-03-002
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/intent-compiler-realignment/README.md
  - specs/intent-compiler-realignment/pending-tasks.md
  - specs/intent-compiler-realignment/intent-processing-inventory.md
  - specs/intent-compiler-realignment/sprints/03-skills-replacement-sprint.md
  - specs/intent-compiler-realignment/tasks/03-skills-replacement/03-realign-planning-queue-skills.md
  - canonical intent compiler docs
- skills:
  - none; skills-pack skill-maintenance task
- expected outputs:
  - updated/replacement planning and queue skills
  - updated specs/intent-compiler-realignment/pending-tasks.md
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- done criteria:
  - planning skills preserve traceability from current intent graph to specs/backlogs/tasks
  - queue is updated and task changes are committed
- notes:
  - commit message: `intent-compiler: realign planning skills`

### TASK-IC-04-001: Realign router skills, examples, and templates

- status: pending
- source: specs/intent-compiler-realignment/backlog/01-intent-compiler-realignment-build-backlog.md
- task brief: specs/intent-compiler-realignment/tasks/04-reference-migration/01-realign-router-examples-and-templates.md
- depends on:
  - TASK-IC-03-003
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/intent-compiler-realignment/README.md
  - specs/intent-compiler-realignment/pending-tasks.md
  - specs/intent-compiler-realignment/intent-processing-inventory.md
  - specs/intent-compiler-realignment/sprints/04-reference-migration-and-validation-sprint.md
  - specs/intent-compiler-realignment/tasks/04-reference-migration/01-realign-router-examples-and-templates.md
  - canonical intent compiler docs
- skills:
  - none; skills-pack skill/doc/example-maintenance task
- expected outputs:
  - updated router skills/examples/templates as indicated by inventory
  - updated specs/intent-compiler-realignment/pending-tasks.md
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- done criteria:
  - high-level routing and examples consistently point to intent compiler model
  - queue is updated and task changes are committed
- notes:
  - commit message: `intent-compiler: realign routers and examples`

### TASK-IC-99-001: Verify intent compiler realignment completion

- status: pending
- source: mini-project verification loop
- task brief: specs/intent-compiler-realignment/tasks/99-verification/01-verify-intent-compiler-realignment-completion.md
- depends on:
  - TASK-IC-04-001
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/intent-compiler-realignment/README.md
  - specs/intent-compiler-realignment/conversation-capture.md
  - specs/intent-compiler-realignment/pending-tasks.md
  - specs/intent-compiler-realignment/sprints/*.md
  - specs/intent-compiler-realignment/backlog/*.md
  - specs/intent-compiler-realignment/tasks/**/*.md
  - specs/intent-compiler-realignment/intent-processing-inventory.md
  - canonical intent compiler docs
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/intent-compiler-realignment/pending-tasks.md
  - completion summary, verification notes, or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - task group/sprint goals have been compared against completed work
  - mini-project done state has been compared against completed work
  - unresolved questions/blockers have been reviewed
  - if complete, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
- notes:
  - commit message: `intent-compiler: verify realignment completion`

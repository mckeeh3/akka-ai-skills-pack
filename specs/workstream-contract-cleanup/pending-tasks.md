# Pending Tasks: Workstream Contract Cleanup

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `<prefix>: <short task title>`.

## Tasks

### TASK-WCC-00-001: Create workstream contract cleanup planning scaffold

- status: done
- source: user request to create a specs mini-project from the workstream cleanup review and accepted decisions
- task brief: specs/workstream-contract-cleanup/tasks/00-planning/00-create-workstream-contract-cleanup-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - /home/hxmc/ai/akka-ai-skills-pack/.agents/skills/project-discussed-idea-to-pending-project/SKILL.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/workstream-contract-cleanup/README.md
  - specs/workstream-contract-cleanup/conversation-capture.md
  - specs/workstream-contract-cleanup/pending-tasks.md
  - specs/workstream-contract-cleanup/sprints/01-workstream-contract-cleanup-sprint.md
  - specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md
  - specs/workstream-contract-cleanup/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlog, task briefs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `specs: add workstream contract cleanup queue`

### TASK-WCC-01-001: Align manifest required fields and attention vocabulary

- status: done
- source: specs/workstream-contract-cleanup/conversation-capture.md decisions 1, 2, 3, 4, and 13
- task brief: specs/workstream-contract-cleanup/tasks/01-contract-schema/01-align-manifest-required-fields.md
- depends on:
  - TASK-WCC-00-001
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/workstream-contract-cleanup/README.md
  - specs/workstream-contract-cleanup/conversation-capture.md
  - specs/workstream-contract-cleanup/sprints/01-workstream-contract-cleanup-sprint.md
  - specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md
  - specs/workstream-contract-cleanup/tasks/01-contract-schema/01-align-manifest-required-fields.md
  - skills-pack/docs/workstream-contract.md
  - skills-pack/docs/workstream-manifest-schema.md
  - skills-pack/docs/workstream-manifest.schema.json
  - skills-pack/docs/workstream-attention-contracts.md
  - skills-pack/docs/workstream-ui-reference-architecture.md
  - skills-pack/tools/validate-workstream-manifest.py
  - skills-pack/templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-manifest.json
- skills:
  - none; skills-pack docs/tooling maintenance task
- expected outputs:
  - updated workstream manifest docs/schema/validator/template
  - updated queue status and commit
- required checks:
  - `git diff --check`
  - `python3 skills-pack/tools/validate-workstream-manifest.py skills-pack/templates/ai-first-saas-core-app/app-description`
  - `bash skills-pack/tools/validate-workstream-contracts.sh skills-pack/templates/ai-first-saas-core-app/app-description`
- done criteria:
  - required governed-agent id and icon tooltip are enforced consistently
  - attention category and severity semantics are aligned
  - empty attention arrays require explicit explanation
  - capabilities must be non-empty
  - checks pass and changes are committed
- notes:
  - commit message: `skills-pack: align workstream manifest required fields`
  - checks: `git diff --check`; `python3 skills-pack/tools/validate-workstream-manifest.py skills-pack/templates/ai-first-saas-core-app/app-description`; `bash skills-pack/tools/validate-workstream-contracts.sh skills-pack/templates/ai-first-saas-core-app/app-description`

### TASK-WCC-01-002: Add mapping, runtime evidence, and internal worker manifest contracts

- status: pending
- source: specs/workstream-contract-cleanup/conversation-capture.md decisions 5, 6, 10, 11, and 12
- task brief: specs/workstream-contract-cleanup/tasks/02-runtime-evidence/01-add-mapping-evidence-internal-worker-contracts.md
- depends on:
  - TASK-WCC-01-001
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/workstream-contract-cleanup/README.md
  - specs/workstream-contract-cleanup/conversation-capture.md
  - specs/workstream-contract-cleanup/sprints/01-workstream-contract-cleanup-sprint.md
  - specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md
  - specs/workstream-contract-cleanup/tasks/02-runtime-evidence/01-add-mapping-evidence-internal-worker-contracts.md
  - skills-pack/docs/workstream-contract.md
  - skills-pack/docs/workstream-manifest-schema.md
  - skills-pack/docs/workstream-manifest.schema.json
  - skills-pack/docs/structured-surface-contracts.md
  - skills-pack/tools/validate-workstream-manifest.py
  - skills-pack/templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-manifest.json
- skills:
  - none; skills-pack docs/tooling maintenance task
- expected outputs:
  - updated manifest docs/schema/validator/template for mappings, evidence, and internal workers
  - updated queue status and commit
- required checks:
  - `git diff --check`
  - `python3 skills-pack/tools/validate-workstream-manifest.py skills-pack/templates/ai-first-saas-core-app/app-description`
  - `bash skills-pack/tools/validate-workstream-contracts.sh skills-pack/templates/ai-first-saas-core-app/app-description`
- done criteria:
  - `surfaceActionMappings` or equivalent is required at `capability-ready` and above
  - runtime/production readiness requires explicit evidence
  - internal workers are structured when present and optional/empty otherwise
  - checks pass and changes are committed
- notes:
  - commit message: `skills-pack: add workstream readiness mapping contracts`

### TASK-WCC-01-003: Add installed-layout reference validation

- status: pending
- source: specs/workstream-contract-cleanup/conversation-capture.md decisions 7, 8, and 9
- task brief: specs/workstream-contract-cleanup/tasks/03-installed-reference-check/01-add-installed-layout-reference-validation.md
- depends on:
  - TASK-WCC-01-002
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/workstream-contract-cleanup/README.md
  - specs/workstream-contract-cleanup/conversation-capture.md
  - specs/workstream-contract-cleanup/sprints/01-workstream-contract-cleanup-sprint.md
  - specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md
  - specs/workstream-contract-cleanup/tasks/03-installed-reference-check/01-add-installed-layout-reference-validation.md
  - skills-pack/install-skills.sh
  - skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh
- skills:
  - none; skills-pack installer/tooling maintenance task
- expected outputs:
  - installed-layout reference documentation/tooling
  - updated queue status and commit
- required checks:
  - `git diff --check`
  - `./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune`
  - `./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - installed `.agents/skills` layout is documented as normative for skill relative references
  - validation catches genuinely broken installed-layout references without requiring source-layout rewrites
  - checks pass and changes are committed
- notes:
  - commit message: `skills-pack: validate installed skill references`

### TASK-WCC-01-004: Run focused workstream contract consistency sweep

- status: pending
- source: specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md
- task brief: specs/workstream-contract-cleanup/tasks/01-contract-schema/02-run-workstream-contract-consistency-sweep.md
- depends on:
  - TASK-WCC-01-003
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/workstream-contract-cleanup/README.md
  - specs/workstream-contract-cleanup/conversation-capture.md
  - specs/workstream-contract-cleanup/sprints/01-workstream-contract-cleanup-sprint.md
  - specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md
  - specs/workstream-contract-cleanup/tasks/01-contract-schema/02-run-workstream-contract-consistency-sweep.md
- skills:
  - none; focused consistency/review task
- expected outputs:
  - focused consistency edits under skills-pack/** if needed
  - updated queue status and commit
- required checks:
  - `git diff --check`
  - `python3 skills-pack/tools/validate-workstream-manifest.py skills-pack/templates/ai-first-saas-core-app/app-description`
  - `bash skills-pack/tools/validate-workstream-contracts.sh skills-pack/templates/ai-first-saas-core-app/app-description`
  - `./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune`
  - `./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - direct drift from accepted decisions has been searched and repaired or recorded
  - checks pass and changes are committed
- notes:
  - commit message: `skills-pack: sweep workstream contract consistency`

### TASK-WCC-99-001: Verify workstream contract cleanup completion

- status: pending
- source: mini-project verification loop
- task brief: specs/workstream-contract-cleanup/tasks/99-verification/01-verify-workstream-contract-cleanup.md
- depends on:
  - TASK-WCC-01-004
- required reads:
  - AGENTS.md
  - skills-pack/AGENTS.md
  - specs/workstream-contract-cleanup/README.md
  - specs/workstream-contract-cleanup/conversation-capture.md
  - specs/workstream-contract-cleanup/pending-tasks.md
  - specs/workstream-contract-cleanup/sprints/01-workstream-contract-cleanup-sprint.md
  - specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md
  - specs/workstream-contract-cleanup/tasks/99-verification/01-verify-workstream-contract-cleanup.md
  - specs/workstream-contract-cleanup/tasks/**/*.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/workstream-contract-cleanup/pending-tasks.md
  - optional completion summary or newly appended follow-up tasks
- required checks:
  - `git diff --check`
  - `python3 skills-pack/tools/validate-workstream-manifest.py skills-pack/templates/ai-first-saas-core-app/app-description`
  - `bash skills-pack/tools/validate-workstream-contracts.sh skills-pack/templates/ai-first-saas-core-app/app-description`
  - `./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune`
  - `./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
  - `bash skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh`
- done criteria:
  - sprint and mini-project done state have been compared against completed work
  - unresolved blockers have been reviewed
  - completion is recorded with no new required work, or follow-up tasks plus a new terminal verification task are appended
  - checks pass and changes are committed
- notes:
  - commit message: `specs: verify workstream contract cleanup`

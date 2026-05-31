# Pending Tasks: Requirements Intake Alignment Cleanup

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, conversation capture, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `riac: <short task title>`.
- The verification task must append new bounded tasks plus a new terminal verification task when material gaps remain.

## Tasks

### TASK-RIAC-00-001: Create requirements intake alignment cleanup planning scaffold

- status: done
- source: user request to create a mini-project for aligning requirements intake content with current skills-pack goals
- task brief: specs/requirements-intake-alignment-cleanup/tasks/00-planning/00-create-requirements-intake-alignment-cleanup-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - docs/pending-question-queue.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/requirements-intake-alignment-cleanup/README.md
  - specs/requirements-intake-alignment-cleanup/conversation-capture.md
  - specs/requirements-intake-alignment-cleanup/pending-tasks.md
  - specs/requirements-intake-alignment-cleanup/sprints/*.md
  - specs/requirements-intake-alignment-cleanup/backlog/*.md
  - specs/requirements-intake-alignment-cleanup/tasks/**/*.md
- required checks:
  - `git diff --check -- specs/requirements-intake-alignment-cleanup`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlogs, task briefs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `riac: add requirements intake cleanup queue`

### TASK-RIAC-01-001: Audit active intake content

- status: done
- source: specs/requirements-intake-alignment-cleanup/backlog/01-inventory-and-prune-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/01-inventory/01-audit-active-intake-content.md
- depends on: [TASK-RIAC-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/README.md
  - specs/requirements-intake-alignment-cleanup/conversation-capture.md
  - specs/requirements-intake-alignment-cleanup/sprints/01-inventory-and-prune-sprint.md
  - specs/requirements-intake-alignment-cleanup/backlog/01-inventory-and-prune-build-backlog.md
  - specs/requirements-intake-alignment-cleanup/tasks/01-inventory/01-audit-active-intake-content.md
  - docs/ai-first-saas-application-architecture.md
  - docs/requirements-to-workstream-development-process.md
  - docs/agent-workstream-application-architecture.md
  - docs/capability-first-backend-architecture.md
- skills:
  - none; repository review task
- expected outputs:
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
- required checks:
  - `git diff --check`
  - search proof that all high-priority files from conversation capture appear in the inventory
- done criteria:
  - inventory classifies active intake/planning files as keep, rewrite, heavy rewrite, remove, or demote-to-mechanics-only
  - task changes and queue update are committed
- notes:
  - commit message: `riac: audit intake content`
  - completed: created `content-inventory.md` and verified all high-priority conversation-capture paths are covered

### TASK-RIAC-01-002: Define prune and rewrite criteria

- status: done
- source: specs/requirements-intake-alignment-cleanup/backlog/01-inventory-and-prune-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/01-inventory/02-define-prune-rewrite-criteria.md
- depends on: [TASK-RIAC-01-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/README.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/tasks/01-inventory/02-define-prune-rewrite-criteria.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-workstream-design-review-checklist.md
- skills:
  - none; repository review task
- expected outputs:
  - specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
- required checks:
  - `git diff --check`
- done criteria:
  - criteria define keep/rewrite/remove/demote decisions and stale-term verification searches
  - task changes and queue update are committed
- notes:
  - commit message: `riac: define cleanup criteria`
  - completed: created `prune-and-rewrite-criteria.md` with keep/rewrite/remove/demote criteria, mechanics-only labeling, reference-update rules, and stale-term searches

### TASK-RIAC-02-001: Align bootstrap, normalization, router, and change-impact skills

- status: done
- source: specs/requirements-intake-alignment-cleanup/backlog/02-description-intake-skills-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/02-intake-skills/01-align-bootstrap-normalization-router-impact.md
- depends on: [TASK-RIAC-01-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
  - specs/requirements-intake-alignment-cleanup/sprints/02-description-intake-skills-sprint.md
  - specs/requirements-intake-alignment-cleanup/backlog/02-description-intake-skills-build-backlog.md
  - specs/requirements-intake-alignment-cleanup/tasks/02-intake-skills/01-align-bootstrap-normalization-router-impact.md
  - docs/minimum-ai-first-saas-app.md
  - docs/examples/ai-first-saas-seed-app-description/README.md
  - skills/app-description-bootstrap/SKILL.md
  - skills/app-description-input-normalization/SKILL.md
  - skills/app-description-intake-router/SKILL.md
  - skills/app-description-change-impact/SKILL.md
- skills:
  - app-descriptions
  - app-description-input-normalization
  - app-description-intake-router
- expected outputs:
  - updated app-description intake skills
- required checks:
  - `git diff --check`
  - focused stale-term search over edited files
- done criteria:
  - five-core starter and workstream/surface/capability routing are consistent in edited skills
  - task changes and queue update are committed
- notes:
  - commit message: `riac: align app description intake skills`
  - completed: aligned bootstrap, input normalization, intake router, and change-impact guidance to five core workstream v0 starter, AI-first seed preference, surface/capability/55-ui handoffs, and mechanics-only legacy example treatment
  - checks: `git diff --check`; focused stale-term search over edited files (remaining hits reviewed as mechanics-only references, canonical anti-patterns, or explicit non-target wording)

### TASK-RIAC-02-002: Align readiness, generation, and app-description orchestration

- status: done
- source: specs/requirements-intake-alignment-cleanup/backlog/02-description-intake-skills-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/02-intake-skills/02-align-readiness-generation-orchestration.md
- depends on: [TASK-RIAC-02-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/sprints/02-description-intake-skills-sprint.md
  - specs/requirements-intake-alignment-cleanup/tasks/02-intake-skills/02-align-readiness-generation-orchestration.md
  - skills/app-descriptions/SKILL.md
  - skills/app-description-readiness-assessment/SKILL.md
  - skills/app-generate-app/SKILL.md
- skills:
  - app-descriptions
  - app-description-readiness-assessment
  - app-generate-app
- expected outputs:
  - updated readiness/generation/orchestration skills
- required checks:
  - `git diff --check`
  - focused stale-term search over edited files
- done criteria:
  - generation/readiness path requires current workstream UI/runtime doctrine
  - task changes and queue update are committed
- notes:
  - commit message: `riac: align app generation readiness`
  - completed: aligned app-description orchestration, readiness assessment, and generation guidance to the five core workstream v0 minimum starter, mandatory `12-workstreams`/`55-ui`/managed-runtime readiness gates, canonical workstream UI generation path, and mechanics-only legacy references
  - checks: `git diff --check`; focused stale-term search over edited files (remaining hits reviewed as explicit mechanics-only legacy references or intentional anti-pattern/blocking guidance)

### TASK-RIAC-03-001: Align solution decomposition and PRD planning

- status: done
- source: specs/requirements-intake-alignment-cleanup/backlog/03-prd-spec-backlog-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/03-planning-queues/01-align-solution-and-prd-planning.md
- depends on: [TASK-RIAC-02-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
  - specs/requirements-intake-alignment-cleanup/sprints/03-prd-spec-backlog-sprint.md
  - specs/requirements-intake-alignment-cleanup/tasks/03-planning-queues/01-align-solution-and-prd-planning.md
  - docs/requirements-to-workstream-development-process.md
  - docs/examples/requirements-to-workstream-mini-example.md
  - skills/akka-solution-decomposition/SKILL.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
- skills:
  - akka-solution-decomposition
  - akka-prd-to-specs-backlog
- expected outputs:
  - updated solution decomposition and PRD planning skills
- required checks:
  - `git diff --check`
  - focused stale-term search over edited files
- done criteria:
  - direct PRD planning is workstream/surface/capability-first
  - task changes and queue update are committed
- notes:
  - commit message: `riac: align prd planning flow`
  - completed: aligned solution decomposition and PRD-to-specs planning to five core workstream v0 minimum starter, canonical requirements-to-workstream examples, full-core core-workstream coverage, and mechanics-only conventional PRD examples
  - checks: `git diff --check`; focused stale-term search over edited files (remaining hits reviewed as mechanics-only references, anti-pattern guidance, review-checklist wording, or intentional full-core User Admin surface acceptance details)

### TASK-RIAC-03-002: Align revised PRD, change request, and backlog skills

- status: done
- source: specs/requirements-intake-alignment-cleanup/backlog/03-prd-spec-backlog-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/03-planning-queues/02-align-revision-and-backlog-skills.md
- depends on: [TASK-RIAC-03-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/sprints/03-prd-spec-backlog-sprint.md
  - specs/requirements-intake-alignment-cleanup/tasks/03-planning-queues/02-align-revision-and-backlog-skills.md
  - skills/akka-revised-prd-reconciliation/SKILL.md
  - skills/akka-change-request-to-spec-update/SKILL.md
  - skills/akka-slice-spec-to-backlog/SKILL.md
  - skills/akka-backlog-to-pending-tasks/SKILL.md
  - skills/akka-backlog-item-to-task-brief/SKILL.md
- skills:
  - akka-revised-prd-reconciliation
  - akka-change-request-to-spec-update
  - akka-slice-spec-to-backlog
  - akka-backlog-to-pending-tasks
  - akka-backlog-item-to-task-brief
- expected outputs:
  - updated revision/change/backlog skills
- required checks:
  - `git diff --check`
  - focused stale-term search over edited files
- done criteria:
  - backlog generation and repair preserve vertical contracts and block stale task shapes
  - task changes and queue update are committed
- notes:
  - commit message: `riac: align backlog change flows`
  - completed: aligned revised-PRD, change-request, slice-to-backlog, backlog-to-pending, and backlog-item brief skills with five-core starter/readiness checks, reference governance/readReferenceDoc/ReferenceLoadTrace coverage, and stale CRUD/page/component-only task blocking or repair
  - checks: `git diff --check`; focused stale-term search over edited skills (remaining hits reviewed as intentional anti-pattern/blocking guidance, five-core readiness checks, or required reference-governance terms)

### TASK-RIAC-03-003: Align pending question and pending task execution flows

- status: done
- source: specs/requirements-intake-alignment-cleanup/backlog/03-prd-spec-backlog-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/03-planning-queues/03-align-pending-question-task-flows.md
- depends on: [TASK-RIAC-03-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/tasks/03-planning-queues/03-align-pending-question-task-flows.md
  - docs/pending-question-queue.md
  - docs/pending-task-queue.md
  - docs/workstream-expertise-model.md
  - skills/akka-pending-question-generation/SKILL.md
  - skills/akka-pending-question-queue-maintenance/SKILL.md
  - skills/akka-pending-task-queue-maintenance/SKILL.md
  - skills/akka-do-next-pending-question/SKILL.md
  - skills/akka-do-next-pending-task/SKILL.md
- skills:
  - akka-pending-question-generation
  - akka-pending-task-queue-maintenance
  - akka-do-next-pending-task
- expected outputs:
  - updated pending question/task/do-next skills and docs if needed
- required checks:
  - `git diff --check`
  - focused stale-term search over edited files
- done criteria:
  - pending flows preserve workstream/surface/capability/expertise/runtime context
  - task changes and queue update are committed
- notes:
  - commit message: `riac: align pending task flows`
  - completed: aligned pending question/task queue docs plus question generation, question maintenance, task maintenance, do-next-question, and do-next-task skills to preserve workstream expertise, reference governance, `readReferenceDoc`, loader/tool-boundary, trace, expertise-surface, and runtime-completion blockers
  - checks: `git diff --check`; focused stale-term search over edited files (remaining hits reviewed as intentional anti-pattern/blocking guidance, Java package guardrail, mechanics-only purchase-request queue link, or generic phrase usage)

### TASK-RIAC-04-001: Rewrite usage and app-description flow docs

- status: done
- source: specs/requirements-intake-alignment-cleanup/backlog/04-docs-examples-ui-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/04-docs-examples-ui/01-rewrite-usage-and-app-description-flow-docs.md
- depends on: [TASK-RIAC-03-003]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
  - specs/requirements-intake-alignment-cleanup/tasks/04-docs-examples-ui/01-rewrite-usage-and-app-description-flow-docs.md
  - docs/intent-driven-usage-flow.md
  - docs/prd-to-akka-flow.md
  - docs/app-description-end-to-end-workflow-example.md
  - docs/requirements-to-workstream-development-process.md
  - docs/examples/requirements-to-workstream-mini-example.md
- skills:
  - none; docs cleanup task
- expected outputs:
  - updated usage/app-description flow docs
- required checks:
  - `git diff --check`
  - reference search for removed/renamed docs if applicable
- done criteria:
  - concise usage docs teach current workstream/surface/capability flow
  - task changes and queue update are committed
- notes:
  - commit message: `riac: rewrite intake flow docs`
  - completed: rewrote intent-driven usage, PRD-to-Akka, and app-description workflow docs around secure AI-first SaaS, five-core starter, workstream/surface/capability sequencing, governed runtime, and local runtime/API/UI validation
  - checks: `git diff --check`; focused stale-term search over edited docs (remaining hits reviewed as anti-pattern wording or mechanics-only purchase-request references); no docs were removed or renamed so reference-removal search was not applicable

### TASK-RIAC-04-002: Remove or rewrite stale app-description skill-plan doc

- status: done
- source: specs/requirements-intake-alignment-cleanup/backlog/04-docs-examples-ui-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/04-docs-examples-ui/02-remove-or-rewrite-stale-app-description-plan-doc.md
- depends on: [TASK-RIAC-04-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
  - specs/requirements-intake-alignment-cleanup/tasks/04-docs-examples-ui/02-remove-or-rewrite-stale-app-description-plan-doc.md
  - docs/app-description-skills-plan-backlog.md
  - docs/internal-app-description-architecture.md
  - docs/app-description-maintenance-flow.md
- skills:
  - none; docs cleanup task
- expected outputs:
  - removed or rewritten stale app-description skill-plan doc
  - updated references
- required checks:
  - `git diff --check`
  - `rg -n "app-description-skills-plan-backlog" . --glob '!specs/requirements-intake-alignment-cleanup/**'`
- done criteria:
  - stale plan doc no longer misleads active guidance
  - task changes and queue update are committed
- notes:
  - commit message: `riac: clean app description plan doc`
  - completed: removed obsolete `docs/app-description-skills-plan-backlog.md` and removed/updated active references in docs, skills, pack README, and historical specs
  - checks: `git diff --check` over task-scoped files; `rg -n "app-description-skills-plan-backlog" . --glob '!specs/requirements-intake-alignment-cleanup/**'` returned no matches

### TASK-RIAC-04-003: Rewrite domain workstream and web UI docs

- status: pending
- source: specs/requirements-intake-alignment-cleanup/backlog/04-docs-examples-ui-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/04-docs-examples-ui/03-rewrite-domain-and-web-ui-docs.md
- depends on: [TASK-RIAC-04-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
  - specs/requirements-intake-alignment-cleanup/tasks/04-docs-examples-ui/03-rewrite-domain-and-web-ui-docs.md
  - docs/domain-workstream-prd-structure.md
  - docs/web-ui-api-contract-patterns.md
  - docs/web-ui-style-guide.md
  - docs/web-ui-ux-patterns.md
  - docs/web-ui-frontend-decomposition.md
  - docs/structured-surface-contracts.md
  - docs/workstream-ui-reference-architecture.md
- skills:
  - none; docs cleanup task
- expected outputs:
  - updated domain PRD and web UI/API/UX docs
- required checks:
  - `git diff --check`
  - focused stale-term search over edited docs
- done criteria:
  - UI/API docs are surface-first and rail/workstream-shell oriented
  - task changes and queue update are committed
- notes:
  - commit message: `riac: align workstream ui docs`

### TASK-RIAC-04-004: Demote or remove legacy examples from active intake guidance

- status: pending
- source: specs/requirements-intake-alignment-cleanup/backlog/04-docs-examples-ui-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/04-docs-examples-ui/04-demote-or-remove-legacy-examples.md
- depends on: [TASK-RIAC-04-003]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
  - specs/requirements-intake-alignment-cleanup/tasks/04-docs-examples-ui/04-demote-or-remove-legacy-examples.md
  - docs/examples/README.md
  - docs/examples/purchase-request-prd.md
  - docs/examples/purchase-request-app-description/README.md
- skills:
  - none; docs/examples cleanup task
- expected outputs:
  - updated or removed legacy example references/content
- required checks:
  - `git diff --check`
  - focused legacy-example search with intentional hits reviewed
- done criteria:
  - legacy examples are mechanics-only, rewritten, or removed according to criteria
  - task changes and queue update are committed
- notes:
  - commit message: `riac: demote legacy examples`

### TASK-RIAC-05-001: Whole-pack stale content pass

- status: pending
- source: specs/requirements-intake-alignment-cleanup/backlog/05-trim-and-repeat-review-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/05-trim-verification/01-whole-pack-stale-content-pass.md
- depends on: [TASK-RIAC-04-004]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/README.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
  - specs/requirements-intake-alignment-cleanup/sprints/05-trim-and-repeat-review-sprint.md
  - specs/requirements-intake-alignment-cleanup/tasks/05-trim-verification/01-whole-pack-stale-content-pass.md
  - docs/agent-workstream-design-review-checklist.md
- skills:
  - none; repository cleanup review task
- expected outputs:
  - specs/requirements-intake-alignment-cleanup/stale-content-pass-01.md
  - bounded active guidance rewrites/removals or appended follow-up tasks
- required checks:
  - `git diff --check`
  - recorded stale-term searches
- done criteria:
  - remaining stale active content is reviewed and a bounded batch is resolved or queued
  - task changes and queue update are committed
- notes:
  - commit message: `riac: run stale content pass`

### TASK-RIAC-05-002: Check package and reference consistency

- status: pending
- source: specs/requirements-intake-alignment-cleanup/backlog/05-trim-and-repeat-review-build-backlog.md
- task brief: specs/requirements-intake-alignment-cleanup/tasks/05-trim-verification/02-package-reference-consistency.md
- depends on: [TASK-RIAC-05-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
  - specs/requirements-intake-alignment-cleanup/tasks/05-trim-verification/02-package-reference-consistency.md
  - pack/manifest.yaml if present
- skills:
  - none; repository consistency task
- expected outputs:
  - specs/requirements-intake-alignment-cleanup/package-reference-consistency.md
  - updated stale references if needed
- required checks:
  - `git diff --check`
  - reference searches for removed/renamed files
- done criteria:
  - installable-pack and active docs references are consistent after cleanup
  - task changes and queue update are committed
- notes:
  - commit message: `riac: check package references`

### TASK-RIAC-99-001: Verify requirements intake alignment cleanup completion

- status: pending
- source: mini-project verification loop
- task brief: specs/requirements-intake-alignment-cleanup/tasks/99-verification/01-verify-requirements-intake-alignment-cleanup.md
- depends on:
  - TASK-RIAC-01-001
  - TASK-RIAC-01-002
  - TASK-RIAC-02-001
  - TASK-RIAC-02-002
  - TASK-RIAC-03-001
  - TASK-RIAC-03-002
  - TASK-RIAC-03-003
  - TASK-RIAC-04-001
  - TASK-RIAC-04-002
  - TASK-RIAC-04-003
  - TASK-RIAC-04-004
  - TASK-RIAC-05-001
  - TASK-RIAC-05-002
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/requirements-intake-alignment-cleanup/README.md
  - specs/requirements-intake-alignment-cleanup/conversation-capture.md
  - specs/requirements-intake-alignment-cleanup/pending-tasks.md
  - specs/requirements-intake-alignment-cleanup/content-inventory.md
  - specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
  - specs/requirements-intake-alignment-cleanup/sprints/*.md
  - specs/requirements-intake-alignment-cleanup/backlog/*.md
  - specs/requirements-intake-alignment-cleanup/tasks/**/*.md
  - docs/agent-workstream-design-review-checklist.md
- skills:
  - none; repository verification task
- expected outputs:
  - specs/requirements-intake-alignment-cleanup/final-verification.md or numbered verification artifact
  - updated specs/requirements-intake-alignment-cleanup/pending-tasks.md
  - newly appended follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - stale-term searches recorded in verification artifact
  - reference checks for removed files when applicable
- done criteria:
  - current task group/sprint goals have been compared against completed work
  - mini-project done state has been compared against completed work
  - unresolved questions/blockers have been reviewed
  - if complete, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
  - task changes and queue update are committed
- notes:
  - commit message: `riac: verify requirements intake cleanup`

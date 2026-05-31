# Pending Tasks: Workstream Graph and Governed-Tools Architecture

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, conversation capture, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `wggt: <short task title>`.
- Verification must append new bounded tasks plus a new terminal verification task when material gaps remain.

## Tasks

### TASK-WGGT-00-001: Create workstream graph governed-tools planning scaffold

- status: done
- source: user decision that workstream graph, role dashboards, internal agent graphs, workstream expertise, and governed-tools are the core pack architecture now
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/00-planning/00-create-workstream-graph-governed-tools-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - docs/pending-question-queue.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/conversation-capture.md
  - specs/workstream-graph-governed-tools-architecture/pending-tasks.md
  - specs/workstream-graph-governed-tools-architecture/sprints/*.md
  - specs/workstream-graph-governed-tools-architecture/backlog/*.md
  - specs/workstream-graph-governed-tools-architecture/tasks/**/*.md
- required checks:
  - `git diff --check -- specs/workstream-graph-governed-tools-architecture`
- done criteria:
  - mini-project captures rationale, sprint sequence, backlogs, task briefs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: add workstream graph governed tools queue`

### TASK-WGGT-01-001: Update core architecture docs

- status: done
- source: specs/workstream-graph-governed-tools-architecture/backlog/01-doctrine-vocabulary-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/01-doctrine/01-update-core-architecture-docs.md
- depends on: [TASK-WGGT-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/conversation-capture.md
  - specs/workstream-graph-governed-tools-architecture/sprints/01-doctrine-vocabulary-sprint.md
  - specs/workstream-graph-governed-tools-architecture/backlog/01-doctrine-vocabulary-build-backlog.md
  - specs/workstream-graph-governed-tools-architecture/tasks/01-doctrine/01-update-core-architecture-docs.md
  - docs/ai-first-saas-application-architecture.md
  - docs/requirements-to-workstream-development-process.md
  - docs/agent-workstream-application-architecture.md
- skills:
  - ai-first-saas
  - agent-workstream-apps
- expected outputs:
  - updated core architecture docs
- required checks:
  - `git diff --check`
  - search touched files for new canonical terms
- done criteria:
  - core docs present role dashboards, surface graphs, internal workstream agent graphs, governed-tools, and incremental input handling as canonical
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update core architecture docs`
  - completed: updated core architecture docs with role-specific dashboards, surface graphs, internal workstream agent graphs, governed-tools, and incremental input handling; checks: `git diff --check`; focused canonical-term search over touched docs

### TASK-WGGT-01-002: Update surface, capability, domain, and expertise docs

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/01-doctrine-vocabulary-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/01-doctrine/02-update-surface-capability-domain-expertise-docs.md
- depends on: [TASK-WGGT-01-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/tasks/01-doctrine/02-update-surface-capability-domain-expertise-docs.md
  - docs/structured-surface-contracts.md
  - docs/capability-first-backend-architecture.md
  - docs/domain-workstream-prd-structure.md
  - docs/workstream-expertise-model.md
- skills:
  - capability-first-backend
  - agent-workstream-apps
- expected outputs:
  - updated supporting doctrine docs
- required checks:
  - `git diff --check`
  - focused term search over touched docs
- done criteria:
  - supporting docs coherently define surface graphs, governed-tools, capability grouping, and workstream expertise
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update graph vocabulary docs`

### TASK-WGGT-01-003: Update routing map and terminology

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/01-doctrine-vocabulary-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/01-doctrine/03-update-routing-map-and-terminology.md
- depends on: [TASK-WGGT-01-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/tasks/01-doctrine/03-update-routing-map-and-terminology.md
  - docs/ai-first-saas-application-architecture.md
  - docs/requirements-to-workstream-development-process.md
- skills:
  - none; routing/docs task
- expected outputs:
  - updated skills routing map and terminology notes
- required checks:
  - `git diff --check`
  - `rg -n "surface graph|governed-tool|browser-tool|agent-tool|internal workstream agent graph|role-specific dashboard" skills/README.md docs/ai-first-saas-application-architecture.md docs/requirements-to-workstream-development-process.md docs/agent-workstream-application-architecture.md`
- done criteria:
  - top-level routing reflects graph/governed-tool decomposition
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update routing terminology`

### TASK-WGGT-02-001: Update app-description architecture docs

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/02-app-description-model-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/02-app-description/01-update-app-description-architecture-docs.md
- depends on: [TASK-WGGT-01-003]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/sprints/02-app-description-model-sprint.md
  - specs/workstream-graph-governed-tools-architecture/tasks/02-app-description/01-update-app-description-architecture-docs.md
  - docs/internal-app-description-architecture.md
  - docs/app-description-maintenance-flow.md
  - docs/description-first-application-doctrine.md
- skills:
  - app-descriptions
- expected outputs:
  - updated app-description architecture docs
- required checks:
  - `git diff --check`
  - focused term search over touched docs
- done criteria:
  - app-description docs locate graph/governed-tool concepts in existing layers
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update app description architecture`

### TASK-WGGT-02-002: Update bootstrap, normalization, and router skills

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/02-app-description-model-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/02-app-description/02-update-bootstrap-normalization-router-skills.md
- depends on: [TASK-WGGT-02-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/tasks/02-app-description/02-update-bootstrap-normalization-router-skills.md
  - skills/app-description-bootstrap/SKILL.md
  - skills/app-description-input-normalization/SKILL.md
  - skills/app-description-intake-router/SKILL.md
- skills:
  - app-description-bootstrap
  - app-description-input-normalization
  - app-description-intake-router
- expected outputs:
  - updated app-description intake skills
- required checks:
  - `git diff --check`
  - focused term search over edited skills
- done criteria:
  - intake skills extract and route workstream graph/governed-tool concepts for new and incremental inputs
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update app intake graph skills`

### TASK-WGGT-02-003: Update functional-agent, surface, and capability modeling skills

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/02-app-description-model-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/02-app-description/03-update-modeling-skills.md
- depends on: [TASK-WGGT-02-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/tasks/02-app-description/03-update-modeling-skills.md
  - skills/app-description-functional-agent-modeling/SKILL.md
  - skills/app-description-surface-modeling/SKILL.md
  - skills/app-description-capability-modeling/SKILL.md
  - docs/workstream-expertise-model.md
  - docs/structured-surface-contracts.md
  - docs/capability-first-backend-architecture.md
- skills:
  - app-description-functional-agent-modeling
  - app-description-surface-modeling
  - app-description-capability-modeling
- expected outputs:
  - updated focused app-description modeling skills
- required checks:
  - `git diff --check`
  - focused term search over edited skills
- done criteria:
  - modeling skills maintain role dashboards, surface graphs, workstream expertise, internal agent graphs, and governed-tools
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update graph modeling skills`

### TASK-WGGT-02-004: Update impact, readiness, UI, and generation-adjacent skills

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/02-app-description-model-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/02-app-description/04-update-impact-readiness-ui-generation-skills.md
- depends on: [TASK-WGGT-02-003]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/tasks/02-app-description/04-update-impact-readiness-ui-generation-skills.md
  - skills/app-description-behavior-specification/SKILL.md
  - skills/app-description-change-impact/SKILL.md
  - skills/app-description-readiness-assessment/SKILL.md
  - skills/app-description-ui/SKILL.md
  - skills/app-generate-app/SKILL.md
- skills:
  - app-description-change-impact
  - app-description-readiness-assessment
  - app-description-ui
  - app-generate-app
- expected outputs:
  - updated impact/readiness/UI/generation skills
- required checks:
  - `git diff --check`
  - focused term search over edited skills
- done criteria:
  - graph/governed-tool changes drive impact, readiness, UI, and generation decisions
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update graph readiness skills`

### TASK-WGGT-03-001: Update solution decomposition and PRD planning

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/03-intake-planning-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/03-intake-planning/01-update-solution-and-prd-planning.md
- depends on: [TASK-WGGT-02-004]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/sprints/03-intake-planning-sprint.md
  - specs/workstream-graph-governed-tools-architecture/tasks/03-intake-planning/01-update-solution-and-prd-planning.md
  - skills/akka-solution-decomposition/SKILL.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - docs/requirements-to-workstream-development-process.md
  - docs/prd-to-akka-flow.md
- skills:
  - akka-solution-decomposition
  - akka-prd-to-specs-backlog
- expected outputs:
  - updated PRD planning skills/docs
- required checks:
  - `git diff --check`
  - focused term search over edited skills/docs
- done criteria:
  - PRD planning output contracts include workstream graph and governed-tool decomposition
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update prd graph planning`

### TASK-WGGT-03-002: Update incremental change planning skills

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/03-intake-planning-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/03-intake-planning/02-update-incremental-change-planning.md
- depends on: [TASK-WGGT-03-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/tasks/03-intake-planning/02-update-incremental-change-planning.md
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
  - updated incremental change/backlog skills
- required checks:
  - `git diff --check`
  - focused term search over edited skills
- done criteria:
  - incremental changes reconcile against existing graph/governed-tool context
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update incremental graph planning`

### TASK-WGGT-03-003: Update pending question/task queue flows

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/03-intake-planning-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/03-intake-planning/03-update-pending-queues.md
- depends on: [TASK-WGGT-03-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/tasks/03-intake-planning/03-update-pending-queues.md
  - docs/pending-question-queue.md
  - docs/pending-task-queue.md
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
  - updated queue docs and pending/do-next skills
- required checks:
  - `git diff --check`
  - focused term search over edited docs/skills
- done criteria:
  - pending queues preserve graph/governed-tool/expertise context across fresh sessions
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update graph queue flows`

### TASK-WGGT-03-004: Update concise usage and planning docs

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/03-intake-planning-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/03-intake-planning/04-update-usage-planning-docs.md
- depends on: [TASK-WGGT-03-003]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/tasks/03-intake-planning/04-update-usage-planning-docs.md
  - docs/intent-driven-usage-flow.md
  - docs/prd-to-akka-flow.md
  - docs/module-sprint-planning.md
  - docs/solution-plan-to-implementation-queue.md
- skills:
  - none; docs task
- expected outputs:
  - updated concise usage/planning docs
- required checks:
  - `git diff --check`
  - focused term search over edited docs
- done criteria:
  - concise docs teach graph/governed-tool and incremental planning flow
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update graph planning docs`

### TASK-WGGT-04-001: Update canonical examples

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/04-examples-implementation-routing-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/04-examples-implementation/01-update-canonical-examples.md
- depends on: [TASK-WGGT-03-004]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/tasks/04-examples-implementation/01-update-canonical-examples.md
  - docs/examples/requirements-to-workstream-mini-example.md
  - docs/examples/ai-first-saas-seed-app-description/README.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/surfaces-index.md
  - docs/examples/ai-first-saas-seed-app-description/app-description/10-capabilities/capabilities-index.md
- skills:
  - none; example/docs task
- expected outputs:
  - updated canonical examples and seed app-description references
- required checks:
  - `git diff --check`
  - focused term search over updated examples
- done criteria:
  - canonical examples demonstrate role dashboards, surface graphs, governed-tools, internal agent graphs, and workstream expertise
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update graph examples`

### TASK-WGGT-04-002: Update UI/API and browser-tool routing

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/04-examples-implementation-routing-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/04-examples-implementation/02-update-ui-api-routing.md
- depends on: [TASK-WGGT-04-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/tasks/04-examples-implementation/02-update-ui-api-routing.md
  - docs/web-ui-api-contract-patterns.md
  - docs/web-ui-frontend-decomposition.md
  - docs/web-ui-ux-patterns.md
  - docs/workstream-ui-reference-architecture.md
  - skills/akka-web-ui-apps/SKILL.md
  - skills/akka-web-ui-api-client/SKILL.md
  - skills/akka-web-ui-state-rendering/SKILL.md
  - skills/akka-web-ui-ux-design/SKILL.md
- skills:
  - akka-web-ui-apps
  - akka-web-ui-api-client
  - akka-web-ui-state-rendering
  - akka-web-ui-ux-design
- expected outputs:
  - updated UI/API docs and web UI skills
- required checks:
  - `git diff --check`
  - focused term search over edited docs/skills
- done criteria:
  - UI/API guidance implements surface graphs and browser-tools
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update browser tool routing`

### TASK-WGGT-04-003: Update agent, internal-agent, and component routing

- status: pending
- source: specs/workstream-graph-governed-tools-architecture/backlog/04-examples-implementation-routing-build-backlog.md
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/04-examples-implementation/03-update-agent-and-component-routing.md
- depends on: [TASK-WGGT-04-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/tasks/04-examples-implementation/03-update-agent-and-component-routing.md
  - skills/agent-workstream-apps/SKILL.md
  - skills/ai-first-saas/SKILL.md
  - skills/capability-first-backend/SKILL.md
  - skills/akka-agents/SKILL.md
  - skills/akka-autonomous-agents/SKILL.md
- skills:
  - agent-workstream-apps
  - ai-first-saas
  - capability-first-backend
  - akka-agents
  - akka-autonomous-agents
- expected outputs:
  - updated agent/component routing skills
- required checks:
  - `git diff --check`
  - focused term search over edited skills
- done criteria:
  - agent and component routing align with governed-tool and internal workstream agent graph model
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: update agent graph routing`

### TASK-WGGT-99-001: Verify workstream graph governed-tools completion

- status: pending
- source: mini-project verification loop
- task brief: specs/workstream-graph-governed-tools-architecture/tasks/99-verification/01-verify-workstream-graph-governed-tools.md
- depends on:
  - TASK-WGGT-01-001
  - TASK-WGGT-01-002
  - TASK-WGGT-01-003
  - TASK-WGGT-02-001
  - TASK-WGGT-02-002
  - TASK-WGGT-02-003
  - TASK-WGGT-02-004
  - TASK-WGGT-03-001
  - TASK-WGGT-03-002
  - TASK-WGGT-03-003
  - TASK-WGGT-03-004
  - TASK-WGGT-04-001
  - TASK-WGGT-04-002
  - TASK-WGGT-04-003
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/workstream-graph-governed-tools-architecture/README.md
  - specs/workstream-graph-governed-tools-architecture/conversation-capture.md
  - specs/workstream-graph-governed-tools-architecture/pending-tasks.md
  - specs/workstream-graph-governed-tools-architecture/sprints/*.md
  - specs/workstream-graph-governed-tools-architecture/backlog/*.md
  - specs/workstream-graph-governed-tools-architecture/tasks/**/*.md
  - docs/agent-workstream-design-review-checklist.md
- skills:
  - none; repository verification task
- expected outputs:
  - specs/workstream-graph-governed-tools-architecture/final-verification.md or numbered verification artifact
  - updated specs/workstream-graph-governed-tools-architecture/pending-tasks.md
  - newly appended follow-up tasks if gaps remain
- required checks:
  - `git diff --check`
  - recorded searches for required and stale terms
- done criteria:
  - current task group and overall mini-project done state assessed
  - if complete, completion is recorded with no new required tasks
  - if incomplete, new bounded tasks are appended before a new terminal verification task
  - task changes and queue update are committed
- notes:
  - commit message: `wggt: verify graph architecture`

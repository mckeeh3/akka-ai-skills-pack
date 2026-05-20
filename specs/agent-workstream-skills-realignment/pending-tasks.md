# Pending Tasks: Agent Workstream Skills Realignment

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task must make one git commit before being marked `done`.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- At the completion of every sprint, the sprint review task must identify remaining refinement areas and add more tasks/sprints when needed.

## Tasks

### TASK-AWSR-01-001: Audit top-level routing alignment

- status: done
- source: user request to review skills-pack alignment with agent workstreams and structured surfaces
- task brief: specs/agent-workstream-skills-realignment/tasks/01-routing-intake/01-audit-top-level-routing.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - .agents/skills/README.md if present
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
  - docs/agent-workstream-design-review-checklist.md
  - skills/ai-first-saas/SKILL.md
  - skills/agent-workstream-apps/SKILL.md
  - skills/capability-first-backend/SKILL.md
  - skills/akka-solution-decomposition/SKILL.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
- skills:
  - agent-workstream-apps
  - capability-first-backend
- expected outputs:
  - specs/agent-workstream-skills-realignment/routing-gap-matrix.md
- required checks:
  - git diff --check
- done criteria:
  - gap matrix identifies first routing changes needed
  - task changes and queue update are committed
- notes:
  - commit message: Audit agent workstream routing alignment

### TASK-AWSR-01-002: Align AI-first and agent-workstream routing

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/01-routing-intake-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/01-routing-intake/02-align-ai-first-workstream-routing.md
- depends on: [TASK-AWSR-01-001]
- required reads:
  - specs/agent-workstream-skills-realignment/routing-gap-matrix.md
  - skills/README.md
  - skills/ai-first-saas/SKILL.md
  - skills/agent-workstream-apps/SKILL.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/agent-workstream-design-review-checklist.md
- skills:
  - ai-first-saas
  - agent-workstream-apps
- expected outputs:
  - updated top-level routing skills/docs
- required checks:
  - git diff --check
  - text search confirming touched routing files mention functional agents, surfaces, capabilities, and Akka components in order
- done criteria:
  - top-level routing makes the workstream model the normal handoff for generated SaaS apps
  - task changes and queue update are committed
- notes:
  - commit message: Align AI-first workstream routing

### TASK-AWSR-01-003: Align capability-first and Akka decomposition routing

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/01-routing-intake-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/01-routing-intake/03-align-capability-decomposition-routing.md
- depends on: [TASK-AWSR-01-002]
- required reads:
  - specs/agent-workstream-skills-realignment/routing-gap-matrix.md
  - skills/capability-first-backend/SKILL.md
  - skills/akka-solution-decomposition/SKILL.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
- skills:
  - capability-first-backend
  - akka-solution-decomposition
- expected outputs:
  - updated capability-first and decomposition guidance
- required checks:
  - git diff --check
  - text search confirming decomposition output sections include functional agents, surfaces, capabilities, and components
- done criteria:
  - decomposition cannot plausibly skip from product intent directly to Akka components for generated SaaS apps
  - task changes and queue update are committed
- notes:
  - commit message: Align capability and decomposition routing

### TASK-AWSR-01-004: Review routing sprint and create next tasks

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/01-routing-intake-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/01-routing-intake/04-review-routing-sprint.md
- depends on: [TASK-AWSR-01-003]
- required reads:
  - specs/agent-workstream-skills-realignment/README.md
  - specs/agent-workstream-skills-realignment/sprints/01-routing-intake-sprint.md
  - specs/agent-workstream-skills-realignment/backlog/01-routing-intake-build-backlog.md
  - specs/agent-workstream-skills-realignment/routing-gap-matrix.md
  - docs/agent-workstream-design-review-checklist.md
- skills:
  - agent-workstream-apps
- expected outputs:
  - specs/agent-workstream-skills-realignment/sprint-01-review.md
  - additional pending tasks if needed
- required checks:
  - git diff --check
- done criteria:
  - Sprint 01 review exists and Sprint 02 is either unblocked or superseded by more routing tasks
  - task changes and queue update are committed
- notes:
  - commit message: Review routing alignment sprint

### TASK-AWSR-02-001: Audit app-description and PRD planning alignment

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/02-planning-description-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/02-planning-description/01-audit-app-description-planning.md
- depends on: [TASK-AWSR-01-004]
- required reads:
  - specs/agent-workstream-skills-realignment/sprint-01-review.md
  - skills/app-descriptions/SKILL.md
  - skills/app-description-intake-router/SKILL.md
  - skills/app-description-functional-agent-modeling/SKILL.md
  - skills/app-description-surface-modeling/SKILL.md
  - skills/app-description-capability-modeling/SKILL.md
  - skills/app-description-ui/SKILL.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - docs/internal-app-description-architecture.md
  - docs/app-description-maintenance-flow.md
  - docs/agent-workstream-design-review-checklist.md
- skills:
  - app-descriptions
  - app-description-functional-agent-modeling
  - app-description-surface-modeling
  - akka-prd-to-specs-backlog
- expected outputs:
  - specs/agent-workstream-skills-realignment/planning-description-gap-matrix.md
- required checks:
  - git diff --check
- done criteria:
  - planning/description gap matrix exists
  - task changes and queue update are committed
- notes:
  - commit message: Audit workstream description planning alignment

### TASK-AWSR-02-002: Align app-description skills with workstream ownership

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/02-planning-description-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/02-planning-description/02-align-app-description-skills.md
- depends on: [TASK-AWSR-02-001]
- required reads:
  - specs/agent-workstream-skills-realignment/planning-description-gap-matrix.md
  - skills/app-descriptions/SKILL.md
  - skills/app-description-functional-agent-modeling/SKILL.md
  - skills/app-description-surface-modeling/SKILL.md
  - skills/app-description-capability-modeling/SKILL.md
  - skills/app-description-ui/SKILL.md
  - docs/internal-app-description-architecture.md
  - docs/structured-surface-contracts.md
- skills:
  - app-descriptions
  - app-description-functional-agent-modeling
  - app-description-surface-modeling
  - app-description-capability-modeling
  - app-description-ui
- expected outputs:
  - updated app-description skills/docs
- required checks:
  - git diff --check
  - text search over touched files for 12-workstreams, 55-ui, functional agent, surface, and capability
- done criteria:
  - description-first guidance clearly preserves workstream/surface ownership
  - task changes and queue update are committed
- notes:
  - commit message: Align app description workstream ownership

### TASK-AWSR-02-003: Align PRD/spec/backlog generation with vertical workstreams

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/02-planning-description-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/02-planning-description/03-align-prd-backlog-generation.md
- depends on: [TASK-AWSR-02-002]
- required reads:
  - specs/agent-workstream-skills-realignment/planning-description-gap-matrix.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - docs/module-sprint-planning.md
  - docs/pending-task-queue.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-workstream-application-architecture.md
- skills:
  - akka-prd-to-specs-backlog
- expected outputs:
  - updated PRD/backlog planning guidance
- required checks:
  - git diff --check
- done criteria:
  - PRD-generated queues are constrained to implementation-ready vertical work
  - task changes and queue update are committed
- notes:
  - commit message: Align PRD backlog workstream tasks

### TASK-AWSR-02-004: Review planning sprint and create next tasks

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/02-planning-description-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/02-planning-description/04-review-planning-sprint.md
- depends on: [TASK-AWSR-02-003]
- required reads:
  - specs/agent-workstream-skills-realignment/sprint-01-review.md
  - specs/agent-workstream-skills-realignment/planning-description-gap-matrix.md
  - docs/agent-workstream-design-review-checklist.md
  - specs/agent-workstream-skills-realignment/sprints/03-implementation-skills-sprint.md
  - specs/agent-workstream-skills-realignment/backlog/03-implementation-skills-build-backlog.md
- skills:
  - app-descriptions
  - akka-prd-to-specs-backlog
- expected outputs:
  - specs/agent-workstream-skills-realignment/sprint-02-review.md
  - additional pending tasks if needed
- required checks:
  - git diff --check
- done criteria:
  - Sprint 02 review exists and Sprint 03 is either unblocked or superseded by more planning tasks
  - task changes and queue update are committed
- notes:
  - commit message: Review planning alignment sprint

### TASK-AWSR-03-001: Audit implementation skills for input-contract drift

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/03-implementation-skills-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/03-implementation-skills/01-audit-implementation-skills.md
- depends on: [TASK-AWSR-02-004]
- required reads:
  - specs/agent-workstream-skills-realignment/sprint-02-review.md
  - specs/agent-workstream-skills-realignment/sprints/03-implementation-skills-sprint.md
  - docs/agent-workstream-design-review-checklist.md
  - relevant web UI, agent, endpoint, component, and testing skills
- skills:
  - akka-web-ui-apps
  - akka-agents
  - capability-first-backend
- expected outputs:
  - specs/agent-workstream-skills-realignment/implementation-skill-gap-matrix.md
- required checks:
  - git diff --check
- done criteria:
  - implementation skill gap matrix exists and prioritizes follow-up updates
  - task changes and queue update are committed
- notes:
  - commit message: Audit workstream implementation skills

### TASK-AWSR-03-002: Align web UI and agent implementation skills

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/03-implementation-skills-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/03-implementation-skills/02-align-web-ui-agent-skills.md
- depends on: [TASK-AWSR-03-001]
- required reads:
  - specs/agent-workstream-skills-realignment/implementation-skill-gap-matrix.md
  - skills/akka-web-ui-apps/SKILL.md
  - focused skills/akka-web-ui-*/SKILL.md files identified by the gap matrix
  - skills/akka-agents/SKILL.md
  - focused skills/akka-agent-*/SKILL.md files identified by the gap matrix
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
- skills:
  - akka-web-ui-apps
  - akka-agents
- expected outputs:
  - updated web UI and agent skills
- required checks:
  - git diff --check
  - text search over touched files for functional agent, surface, capability, AuthContext, and trace
- done criteria:
  - web UI and agent implementation guidance consumes the workstream/surface/capability contract
  - task changes and queue update are committed
- notes:
  - commit message: Align web UI and agent implementation skills

### TASK-AWSR-03-003: Align endpoint, component, and test skills

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/03-implementation-skills-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/03-implementation-skills/03-align-endpoint-component-test-skills.md
- depends on: [TASK-AWSR-03-002]
- required reads:
  - specs/agent-workstream-skills-realignment/implementation-skill-gap-matrix.md
  - endpoint/component/test skills identified by the gap matrix
  - docs/capability-first-backend-architecture.md
  - docs/structured-surface-contracts.md
  - docs/agent-workstream-application-architecture.md
- skills:
  - akka-http-endpoints
  - akka-event-sourced-entities
  - akka-key-value-entities
  - akka-workflows
  - akka-views
  - akka-consumers
  - akka-timed-actions
- expected outputs:
  - updated endpoint/component/test skills
- required checks:
  - git diff --check
- done criteria:
  - focused implementation skills no longer invite component-first implementation for generated SaaS work
  - task changes and queue update are committed
- notes:
  - commit message: Align endpoint component test skills

### TASK-AWSR-03-004: Review implementation sprint and create next tasks

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/03-implementation-skills-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/03-implementation-skills/04-review-implementation-sprint.md
- depends on: [TASK-AWSR-03-003]
- required reads:
  - specs/agent-workstream-skills-realignment/implementation-skill-gap-matrix.md
  - docs/agent-workstream-design-review-checklist.md
  - specs/agent-workstream-skills-realignment/sprints/04-starter-dogfood-sprint.md
  - specs/agent-workstream-skills-realignment/backlog/04-starter-dogfood-build-backlog.md
- skills:
  - agent-workstream-apps
- expected outputs:
  - specs/agent-workstream-skills-realignment/sprint-03-review.md
  - additional pending tasks if needed
- required checks:
  - git diff --check
- done criteria:
  - Sprint 03 review exists and Sprint 04 is either unblocked or superseded by more implementation-skill tasks
  - task changes and queue update are committed
- notes:
  - commit message: Review implementation alignment sprint

### TASK-AWSR-04-001: Audit starter queue against realigned workstream model

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/04-starter-dogfood-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/04-starter-dogfood/01-audit-starter-queue.md
- depends on: [TASK-AWSR-03-004]
- required reads:
  - specs/agent-workstream-skills-realignment/sprint-03-review.md
  - specs/ai-first-saas-starter-app-template/pending-tasks.md
  - specs/ai-first-saas-starter-app-template/sprints/07-fullstack-gap-closure-sprint.md
  - specs/ai-first-saas-starter-app-template/backlog/07-fullstack-gap-closure-build-backlog.md
  - specs/ai-first-saas-starter-app-template/tasks/07-fullstack-gap-closure/**
  - docs/agent-workstream-design-review-checklist.md
- skills:
  - agent-workstream-apps
  - capability-first-backend
- expected outputs:
  - specs/agent-workstream-skills-realignment/starter-queue-gap-matrix.md
- required checks:
  - git diff --check
- done criteria:
  - starter queue gap matrix identifies supersession targets
  - task changes and queue update are committed
- notes:
  - commit message: Audit starter queue workstream alignment

### TASK-AWSR-04-002: Rewrite starter queue as workstream/surface/capability tasks

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/04-starter-dogfood-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/04-starter-dogfood/02-rewrite-starter-queue.md
- depends on: [TASK-AWSR-04-001]
- required reads:
  - specs/agent-workstream-skills-realignment/starter-queue-gap-matrix.md
  - specs/ai-first-saas-starter-app-template/pending-tasks.md
  - specs/ai-first-saas-starter-app-template/starter-app-scope-and-acceptance.md
  - templates/ai-first-saas-starter/README.md
  - templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
- skills:
  - agent-workstream-apps
  - akka-prd-to-specs-backlog
- expected outputs:
  - superseded vague starter tasks
  - new vertical full-core starter tasks
- required checks:
  - git diff --check
- done criteria:
  - starter queue is workstream-first and implementation-ready
  - task changes and queue update are committed
- notes:
  - commit message: Rewrite starter queue workstream first
  - check passed: `git diff --check`

### TASK-AWSR-04-003: Final realignment review and next-sprint decision

- status: done
- source: specs/agent-workstream-skills-realignment/backlog/04-starter-dogfood-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/04-starter-dogfood/03-final-realignment-review.md
- depends on: [TASK-AWSR-04-002]
- required reads:
  - specs/agent-workstream-skills-realignment/README.md
  - all sprint review files created so far
  - all gap matrices created so far
  - docs/agent-workstream-design-review-checklist.md
  - skills/README.md
  - touched routing, app-description, PRD, web UI, agent, endpoint, and component skills
  - updated starter queue files
- skills:
  - agent-workstream-apps
- expected outputs:
  - specs/agent-workstream-skills-realignment/final-realignment-review.md
  - next sprint/backlog/tasks if gaps remain
- required checks:
  - git diff --check
- done criteria:
  - final review either closes realignment or creates a concrete next sprint
  - task changes and queue update are committed
- notes:
  - commit message: Review agent workstream skills realignment
  - check passed: `git diff --check`
  - final review closes realignment; PRD-driven starter implementation can begin from `TASK-STARTER-08-001`.

### TASK-AWSR-05-001: Refresh installed pack and validate source parity

- status: pending
- source: follow-up review after broad realignment found stale `.agents/` dogfood output
- task brief: specs/agent-workstream-skills-realignment/tasks/05-focused-cleanup/01-refresh-installed-pack-parity.md
- depends on: [TASK-AWSR-04-003]
- required reads:
  - install.sh
  - .gitignore
  - skills/README.md
  - skills/akka-solution-decomposition/SKILL.md
  - skills/akka-web-ui-api-client/SKILL.md
  - skills/capability-first-backend/SKILL.md
  - specs/agent-workstream-skills-realignment/final-realignment-review.md
  - specs/agent-workstream-skills-realignment/sprints/05-focused-cleanup-sprint.md
- skills:
  - none; install/parity validation task
- expected outputs:
  - specs/agent-workstream-skills-realignment/installed-pack-parity-check.md
- required checks:
  - git diff --check
  - git status --short must not show tracked .agents changes
- done criteria:
  - installed dogfood output is refreshed and spot-checked
  - task changes and queue update are committed
- notes: []

### TASK-AWSR-05-002: Align core SaaS foundation with workstream-first foundation verticals

- status: pending
- source: follow-up review found `core-saas-foundation` too object/foundation-first
- task brief: specs/agent-workstream-skills-realignment/tasks/05-focused-cleanup/02-align-core-foundation-workstreams.md
- depends on: [TASK-AWSR-05-001]
- required reads:
  - skills/core-saas-foundation/SKILL.md
  - skills/agent-workstream-apps/SKILL.md
  - docs/core-ai-first-saas-foundation.md
  - docs/core-saas-identity-tenancy-admin.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
  - docs/agent-workstream-design-review-checklist.md
- skills:
  - core-saas-foundation
  - agent-workstream-apps
- expected outputs:
  - updated core SaaS foundation routing and output checklist
- required checks:
  - git diff --check
  - rg check for foundation functional-agent/surface terms
- done criteria:
  - core foundation routes through foundation workstream verticals before components
  - task changes and queue update are committed
- notes: []

### TASK-AWSR-05-003: Add input-contract gates to remaining focused skills

- status: pending
- source: follow-up review found high-use focused skills without standard generated SaaS input gates
- task brief: specs/agent-workstream-skills-realignment/tasks/05-focused-cleanup/03-add-input-contract-to-remaining-focused-skills.md
- depends on: [TASK-AWSR-05-002]
- required reads:
  - specs/agent-workstream-skills-realignment/implementation-skill-gap-matrix.md
  - specs/agent-workstream-skills-realignment/sprint-03-review.md
  - skills/akka-web-ui-api-client/SKILL.md
- skills:
  - akka-web-ui-apps
  - akka-agents
  - akka-http-endpoints
  - akka-workflows
  - akka-views
- expected outputs:
  - generated SaaS input contract gates in remaining high-use focused skills
- required checks:
  - git diff --check
  - rg check for Generated SaaS input contract over touched candidate skills
- done criteria:
  - remaining high-use focused skills no longer invite mechanics-first generated SaaS implementation
  - task changes and queue update are committed
- notes: []

### TASK-AWSR-05-004: Normalize structured-surface and exposure-channel terminology

- status: pending
- source: follow-up review found ambiguous use of `surface`
- task brief: specs/agent-workstream-skills-realignment/tasks/05-focused-cleanup/04-normalize-surface-terminology.md
- depends on: [TASK-AWSR-05-003]
- required reads:
  - skills/akka-solution-decomposition/SKILL.md
  - skills/capability-first-backend/SKILL.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
- skills:
  - capability-first-backend
  - akka-solution-decomposition
  - akka-prd-to-specs-backlog
- expected outputs:
  - top-level terminology distinguishes structured surfaces from exposure channels/paths
- required checks:
  - git diff --check
  - targeted rg review over touched files
- done criteria:
  - top-level planning language avoids structured-surface/exposure-channel ambiguity
  - task changes and queue update are committed
- notes: []

### TASK-AWSR-05-005: Align AI-first companion skills with surface/action handoffs

- status: pending
- source: follow-up review found AI-first companion skills not consistently workstream-aware
- task brief: specs/agent-workstream-skills-realignment/tasks/05-focused-cleanup/05-align-ai-first-companion-surface-handoffs.md
- depends on: [TASK-AWSR-05-004]
- required reads:
  - skills/ai-first-saas-policy-governance/SKILL.md
  - skills/ai-first-saas-decision-cards/SKILL.md
  - skills/ai-first-saas-audit-trace/SKILL.md
  - skills/ai-first-saas-admin-agents/SKILL.md
  - skills/ai-first-saas-outcomes-metrics/SKILL.md
  - skills/agent-workstream-apps/SKILL.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
- skills:
  - ai-first-saas-policy-governance
  - ai-first-saas-decision-cards
  - ai-first-saas-audit-trace
  - ai-first-saas-admin-agents
  - ai-first-saas-outcomes-metrics
  - agent-workstream-apps
- expected outputs:
  - AI-first companion outputs include functional-agent/surface/action/capability handoffs
- required checks:
  - git diff --check
  - rg check for workstream/surface/action/capability terms over touched companion skills
- done criteria:
  - AI-first companion outputs are implementation-ready for workstream/surface/capability planning
  - task changes and queue update are committed
- notes: []

### TASK-AWSR-05-006: Validate and repair source skill path references

- status: pending
- source: follow-up review found fragile source skill relative paths
- task brief: specs/agent-workstream-skills-realignment/tasks/05-focused-cleanup/06-validate-source-skill-paths.md
- depends on: [TASK-AWSR-05-005]
- required reads:
  - skills/README.md
  - install.sh
- skills:
  - none; source hygiene/validation task
- expected outputs:
  - specs/agent-workstream-skills-realignment/source-skill-path-reference-audit.md
  - highest-impact source path fixes or queued follow-up
- required checks:
  - git diff --check
  - path audit command from the report, or documented limitations
- done criteria:
  - source skill path issues are documented and highest-impact breakages are fixed or queued
  - task changes and queue update are committed
- notes: []

### TASK-AWSR-05-007: Update starter acceptance consistency for Sprint 08 queue

- status: pending
- source: follow-up review found starter acceptance docs conflict with later Sprint 08 queue
- task brief: specs/agent-workstream-skills-realignment/tasks/05-focused-cleanup/07-update-starter-acceptance-consistency.md
- depends on: [TASK-AWSR-05-006]
- required reads:
  - specs/ai-first-saas-starter-app-template/final-acceptance-review.md
  - specs/ai-first-saas-starter-app-template/migration-completion-summary.md
  - specs/ai-first-saas-starter-app-template/pending-tasks.md
  - specs/agent-workstream-skills-realignment/starter-queue-gap-matrix.md
  - specs/agent-workstream-skills-realignment/final-realignment-review.md
- skills:
  - none; documentation consistency task
- expected outputs:
  - updated starter acceptance or migration summary language
- required checks:
  - git diff --check
- done criteria:
  - starter docs no longer conflict with pending Sprint 08 tasks
  - task changes and queue update are committed
- notes: []

### TASK-AWSR-05-008: Review focused cleanup sprint and close or create Sprint 06

- status: pending
- source: specs/agent-workstream-skills-realignment/backlog/05-focused-cleanup-build-backlog.md
- task brief: specs/agent-workstream-skills-realignment/tasks/05-focused-cleanup/08-review-focused-cleanup-sprint.md
- depends on: [TASK-AWSR-05-007]
- required reads:
  - specs/agent-workstream-skills-realignment/sprints/05-focused-cleanup-sprint.md
  - specs/agent-workstream-skills-realignment/backlog/05-focused-cleanup-build-backlog.md
  - specs/agent-workstream-skills-realignment/installed-pack-parity-check.md
  - specs/agent-workstream-skills-realignment/source-skill-path-reference-audit.md
  - docs/agent-workstream-design-review-checklist.md
  - touched skills/docs from Sprint 05
- skills:
  - agent-workstream-apps
- expected outputs:
  - specs/agent-workstream-skills-realignment/sprint-05-review.md
  - Sprint 06 tasks if gaps remain
- required checks:
  - git diff --check
- done criteria:
  - Sprint 05 review exists and either closes realignment or creates concrete next tasks
  - task changes and queue update are committed
- notes: []

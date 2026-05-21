# Pending Tasks: Minimum AI-First App Migration

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task must make one git commit before being marked `done`; the commit should include only that task's intended changes and its queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- This queue is for the minimum AI-first app migration, rooted at `specs/minimum-ai-first-app-migration/`.

## Tasks

### TASK-MINAPP-00-001: Create minimum app migration planning scaffold

- status: done
- source: user request to create a migration for the User Admin workstream v0 minimum app adjustments
- task brief: specs/minimum-ai-first-app-migration/tasks/00-planning-scaffold/00-create-minimum-app-migration-plan.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/capability-first-backend-architecture.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/minimum-ai-first-app-migration/README.md
  - specs/minimum-ai-first-app-migration/conversation-capture.md
  - specs/minimum-ai-first-app-migration/pending-tasks.md
  - specs/minimum-ai-first-app-migration/sprints/*.md
  - specs/minimum-ai-first-app-migration/backlog/*.md
  - specs/minimum-ai-first-app-migration/tasks/**/*.md
- required checks:
  - verify git status contains only migration planning scaffold files before commit
  - git diff --check
- done criteria:
  - migration has captured rationale, sprint sequence, backlogs, task briefs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `Add minimum AI-first app migration plan`

### TASK-MINAPP-01-001: Add canonical minimum app doctrine

- status: done
- source: specs/minimum-ai-first-app-migration/backlog/01-minimum-app-doctrine-build-backlog.md
- task brief: specs/minimum-ai-first-app-migration/tasks/01-doctrine/01-add-minimum-app-doctrine.md
- depends on: [TASK-MINAPP-00-001]
- required reads:
  - AGENTS.md
  - specs/minimum-ai-first-app-migration/README.md
  - specs/minimum-ai-first-app-migration/conversation-capture.md
  - specs/minimum-ai-first-app-migration/sprints/01-minimum-app-doctrine-sprint.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/capability-first-backend-architecture.md
- skills:
  - agent-workstream-apps
  - core-saas-foundation
- expected outputs:
  - docs/minimum-ai-first-saas-app.md
  - docs/ai-first-saas-application-architecture.md as needed for links/summary
- required checks:
  - git diff --check
  - verify the doctrine says minimum app is User Admin workstream v0, not generic chatbot
  - verify full-core readiness remains stricter than minimum starter readiness
- done criteria:
  - canonical minimum app doctrine exists and is linked from AI-first doctrine
  - task changes and queue update are committed
- notes:
  - commit message: `Add canonical minimum app doctrine`

### TASK-MINAPP-01-002: Integrate minimum workstream into agent workstream doctrine

- status: done
- source: specs/minimum-ai-first-app-migration/backlog/01-minimum-app-doctrine-build-backlog.md
- task brief: specs/minimum-ai-first-app-migration/tasks/01-doctrine/02-integrate-workstream-doctrine.md
- depends on: [TASK-MINAPP-01-001]
- required reads:
  - specs/minimum-ai-first-app-migration/README.md
  - docs/minimum-ai-first-saas-app.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
- skills:
  - agent-workstream-apps
  - ai-first-saas-ui-surfaces
- expected outputs:
  - docs/agent-workstream-application-architecture.md
- required checks:
  - git diff --check
  - verify the minimum initial workstream section names User Admin workstream v0 and `markdown_response`
  - verify Audit/Trace UI can be later but audit/work trace substrate is first-slice
- done criteria:
  - workstream doctrine can guide minimum starter planning without page/chatbot drift
  - task changes and queue update are committed
- notes:
  - commit message: `Integrate minimum workstream doctrine`

### TASK-MINAPP-01-003: Define markdown_response structured surface contract

- status: done
- source: specs/minimum-ai-first-app-migration/backlog/01-minimum-app-doctrine-build-backlog.md
- task brief: specs/minimum-ai-first-app-migration/tasks/01-doctrine/03-define-markdown-response-surface.md
- depends on: [TASK-MINAPP-01-002]
- required reads:
  - docs/structured-surface-contracts.md
  - docs/minimum-ai-first-saas-app.md
  - docs/agent-workstream-application-architecture.md
  - skills/app-description-surface-modeling/SKILL.md
  - skills/ai-first-saas-ui-surfaces/SKILL.md
- skills:
  - app-description-surface-modeling
  - ai-first-saas-ui-surfaces
- expected outputs:
  - docs/structured-surface-contracts.md
  - skills/app-description-surface-modeling/SKILL.md if needed for the new base surface
- required checks:
  - git diff --check
  - verify markdown rendering security/sanitization is explicit
  - verify the surface has payload, trace, states, accessibility, and tests
- done criteria:
  - `markdown_response` is a first-class structured surface contract
  - task changes and queue update are committed
- notes:
  - commit message: `Define markdown response surface contract`

### TASK-MINAPP-02-001: Split core foundation guidance into minimum starter and full-core readiness

- status: pending
- source: specs/minimum-ai-first-app-migration/backlog/02-foundation-routing-build-backlog.md
- task brief: specs/minimum-ai-first-app-migration/tasks/02-foundation-routing/01-split-core-foundation-readiness.md
- depends on: [TASK-MINAPP-01-003]
- required reads:
  - docs/minimum-ai-first-saas-app.md
  - docs/core-ai-first-saas-foundation.md
  - skills/core-saas-foundation/SKILL.md
  - specs/minimum-ai-first-app-migration/sprints/02-foundation-routing-sprint.md
- skills:
  - core-saas-foundation
- expected outputs:
  - docs/core-ai-first-saas-foundation.md
  - skills/core-saas-foundation/SKILL.md
- required checks:
  - git diff --check
  - verify Slice 0 is User Admin workstream v0 with bootstrap auth, workstream log, traces, and no public self-registration
  - verify full-core requirements are preserved as required follow-up/readiness gates
- done criteria:
  - foundation guidance supports a valid first slice without weakening full-core SaaS readiness
  - task changes and queue update are committed
- notes: []

### TASK-MINAPP-02-002: Update top-level routing for minimum app interpretation

- status: pending
- source: specs/minimum-ai-first-app-migration/backlog/02-foundation-routing-build-backlog.md
- task brief: specs/minimum-ai-first-app-migration/tasks/02-foundation-routing/02-update-minimum-routing.md
- depends on: [TASK-MINAPP-02-001]
- required reads:
  - skills/README.md
  - skills/agent-workstream-apps/SKILL.md
  - skills/ai-first-saas/SKILL.md
  - docs/minimum-ai-first-saas-app.md
- skills:
  - ai-first-saas
  - agent-workstream-apps
- expected outputs:
  - skills/README.md
  - skills/agent-workstream-apps/SKILL.md
  - skills/ai-first-saas/SKILL.md as needed
- required checks:
  - git diff --check
  - verify starter/basic chatbot/minimum app language routes to User Admin workstream v0
  - verify generated app routing still proceeds through capability-first backend before component selection
- done criteria:
  - harness routing reflects the minimum app rule
  - task changes and queue update are committed
- notes: []

### TASK-MINAPP-02-003: Update readiness, generation, and planning gates

- status: pending
- source: specs/minimum-ai-first-app-migration/backlog/02-foundation-routing-build-backlog.md
- task brief: specs/minimum-ai-first-app-migration/tasks/02-foundation-routing/03-update-readiness-planning-gates.md
- depends on: [TASK-MINAPP-02-002]
- required reads:
  - docs/minimum-ai-first-saas-app.md
  - skills/app-description-readiness-assessment/SKILL.md
  - skills/app-generate-app/SKILL.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - skills/akka-solution-decomposition/SKILL.md
- skills:
  - app-description-readiness-assessment
  - app-generate-app
  - akka-prd-to-specs-backlog
  - akka-solution-decomposition
- expected outputs:
  - readiness/generation/planning skill updates as needed
- required checks:
  - git diff --check
  - verify minimum starter ready, full-core ready, and app-specific ready are separate states
  - verify minimum starter output carries follow-up tasks to complete full core
- done criteria:
  - planning and generation can target minimum starter without falsely claiming full-core readiness
  - task changes and queue update are committed
- notes: []

### TASK-MINAPP-03-001: Update app-description guidance for User Admin v0

- status: pending
- source: specs/minimum-ai-first-app-migration/backlog/03-description-starter-build-backlog.md
- task brief: specs/minimum-ai-first-app-migration/tasks/03-description-starter/01-update-app-description-guidance.md
- depends on: [TASK-MINAPP-02-003]
- required reads:
  - docs/minimum-ai-first-saas-app.md
  - docs/internal-app-description-architecture.md
  - docs/app-description-maintenance-flow.md
  - skills/app-description-bootstrap/SKILL.md
  - skills/app-description-functional-agent-modeling/SKILL.md
  - skills/app-description-surface-modeling/SKILL.md
- skills:
  - app-descriptions
  - app-description-bootstrap
  - app-description-functional-agent-modeling
  - app-description-surface-modeling
- expected outputs:
  - app-description docs/skills updated as needed
- required checks:
  - git diff --check
  - verify minimal app-description tree can express User Admin workstream v0, `markdown_response`, bootstrap auth, logs, traces, and follow-up gaps
- done criteria:
  - description-first path supports minimum starter semantics
  - task changes and queue update are committed
- notes: []

### TASK-MINAPP-03-002: Update seed example references and starter progression docs

- status: pending
- source: specs/minimum-ai-first-app-migration/backlog/03-description-starter-build-backlog.md
- task brief: specs/minimum-ai-first-app-migration/tasks/03-description-starter/02-update-seed-and-starter-docs.md
- depends on: [TASK-MINAPP-03-001]
- required reads:
  - docs/minimum-ai-first-saas-app.md
  - docs/examples/ai-first-saas-seed-app-description/README.md
  - docs/examples/core-ai-first-saas-input/01-core-seed-progression-plan.md if present
  - specs/ai-first-saas-starter-app-template/README.md
  - specs/core-app-full-stack-readiness/README.md
- skills:
  - core-saas-foundation
  - agent-workstream-apps
- expected outputs:
  - seed example/starter progression docs updated as needed
- required checks:
  - git diff --check
  - verify first implementation slice is User Admin workstream v0 before Agent Admin/Audit UI/domain work
  - verify explicit progression to full core remains visible
- done criteria:
  - examples and starter docs teach the same minimum-first growth path
  - task changes and queue update are committed
- notes: []

### TASK-MINAPP-03-003: Update starter template/scaffold guidance and queues

- status: pending
- source: specs/minimum-ai-first-app-migration/backlog/03-description-starter-build-backlog.md
- task brief: specs/minimum-ai-first-app-migration/tasks/03-description-starter/03-update-starter-template-guidance.md
- depends on: [TASK-MINAPP-03-002]
- required reads:
  - docs/minimum-ai-first-saas-app.md
  - templates/ai-first-saas-starter/README.md if present
  - .agents/resources/templates/ai-first-saas-starter/README.md if present
  - specs/ai-first-saas-starter-app-template/pending-tasks.md
  - specs/core-app-full-stack-readiness/pending-tasks.md
- skills:
  - akka-change-request-to-spec-update
  - akka-pending-task-queue-maintenance
- expected outputs:
  - starter template docs and/or affected spec queues updated as needed
- required checks:
  - git diff --check
  - verify generated starter first slice aligns with User Admin v0 where template guidance exists
  - verify any changed pending queues preserve task IDs and statuses
- done criteria:
  - starter/scaffold work is aligned or follow-up tasks are queued where implementation changes are too large
  - task changes and queue update are committed
- notes: []

### TASK-MINAPP-04-001: Final consistency review and completion summary

- status: pending
- source: specs/minimum-ai-first-app-migration/backlog/04-final-review-build-backlog.md
- task brief: specs/minimum-ai-first-app-migration/tasks/04-review/01-final-consistency-review.md
- depends on: [TASK-MINAPP-03-003]
- required reads:
  - specs/minimum-ai-first-app-migration/README.md
  - specs/minimum-ai-first-app-migration/conversation-capture.md
  - docs/minimum-ai-first-saas-app.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/core-ai-first-saas-foundation.md
  - docs/structured-surface-contracts.md
- skills:
  - agent-workstream-apps
  - core-saas-foundation
- expected outputs:
  - specs/minimum-ai-first-app-migration/migration-completion-summary.md
  - small consistency fixes or new follow-up tasks if needed
- required checks:
  - git diff --check
  - rg -n "generic chatbot|simple chatbot|chatbot" docs skills templates specs --glob '!specs/minimum-ai-first-app-migration/**'
  - rg -n "minimum AI-first|minimum app|markdown_response|User Admin workstream v0" docs skills templates specs
- done criteria:
  - migration completion summary exists
  - no known canonical drift remains, or follow-up tasks are queued
  - task changes and queue update are committed
- notes: []

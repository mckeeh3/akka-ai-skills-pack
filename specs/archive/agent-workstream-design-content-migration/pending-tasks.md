# Pending Tasks

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task must make one git commit before being marked `done`; the commit should include only that task's intended changes and its queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- This queue is for the agent workstream design content migration, rooted at `specs/agent-workstream-design-content-migration/`.

## Tasks

### TASK-AWDD-00-001: Create agent workstream design content migration planning scaffold

- status: done
- source: user request to create a task-based migration plan for design cleanup after functional/context-area agent UI consolidation
- task brief: specs/agent-workstream-design-content-migration/tasks/00-planning-scaffold/00-create-agent-workstream-design-content-migration-plan.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
  - docs/capability-first-backend-architecture.md
  - skills/agent-workstream-apps/SKILL.md
  - skills/app-description-ui/SKILL.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/agent-workstream-design-content-migration/README.md
  - specs/agent-workstream-design-content-migration/conversation-capture.md
  - specs/agent-workstream-design-content-migration/pending-tasks.md
  - specs/agent-workstream-design-content-migration/sprints/*.md
  - specs/agent-workstream-design-content-migration/backlog/*.md
  - specs/agent-workstream-design-content-migration/tasks/**/*.md
- required checks:
  - verify git status contains only migration planning scaffold files before commit
- done criteria:
  - migration has captured rationale, sprint sequence, backlogs, task briefs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `Add agent workstream design content migration plan`
  - completed: created planning scaffold, conversation capture, sprint specs, backlogs, task briefs, and queued migration tasks.

### TASK-AWDD-01-001: Inventory agent workstream design-content drift

- status: done
- source: specs/agent-workstream-design-content-migration/backlog/01-inventory-and-target-backlog.md
- task brief: specs/agent-workstream-design-content-migration/tasks/01-inventory-and-target/01-inventory-design-content-drift.md
- depends on: [TASK-AWDD-00-001]
- required reads:
  - AGENTS.md
  - specs/agent-workstream-design-content-migration/README.md
  - specs/agent-workstream-design-content-migration/conversation-capture.md
  - specs/agent-workstream-design-content-migration/sprints/01-inventory-and-target-sprint.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
  - skills/agent-workstream-apps/SKILL.md
- skills:
  - agent-workstream-apps
- expected outputs:
  - specs/agent-workstream-design-content-migration/design-content-drift-inventory.md
- required checks:
  - inventory docs, skills, examples, readiness/generation guidance, and web UI routing references
  - no doctrine or skill rewrite in this inventory task except the queue update
- done criteria:
  - inventory records files to align, retire, label, or leave unchanged
  - task changes and queue update are committed
- notes:
  - commit message: `Inventory agent workstream design content drift`
  - completed: created design-content drift inventory across doctrine, app-description skills, AI-first routing, web UI guidance, readiness/generation gates, and examples; no doctrine or skill rewrites were performed.

### TASK-AWDD-01-002: Define canonical content targets and design review checklist

- status: done
- source: specs/agent-workstream-design-content-migration/backlog/01-inventory-and-target-backlog.md
- task brief: specs/agent-workstream-design-content-migration/tasks/01-inventory-and-target/02-define-canonical-content-targets-and-checklist.md
- depends on: [TASK-AWDD-01-001]
- required reads:
  - specs/agent-workstream-design-content-migration/design-content-drift-inventory.md
  - docs/agent-workstream-application-architecture.md
  - docs/internal-app-description-architecture.md
  - docs/workstream-ui-reference-architecture.md
- skills:
  - agent-workstream-apps
  - app-description-ui
- expected outputs:
  - docs/agent-workstream-design-review-checklist.md
  - target rules added to specs/agent-workstream-design-content-migration/design-content-drift-inventory.md or a new target summary
- required checks:
  - checklist covers functional agents, surfaces, capabilities, auth, traces, routes-as-deep-links, style-guide selection, and legacy quarantine
- done criteria:
  - future migration tasks have compact target rules to apply
  - task changes and queue update are committed
- notes:
  - commit message: `Add agent workstream design review targets`
  - completed: created reusable design review checklist and migration target summary; linked target rules from the drift inventory.

### TASK-AWDD-02-001: Align core doctrine terminology and ownership boundaries

- status: done
- source: specs/agent-workstream-design-content-migration/backlog/02-doctrine-and-description-backlog.md
- task brief: specs/agent-workstream-design-content-migration/tasks/02-doctrine-and-description/01-align-core-doctrine-terminology-and-boundaries.md
- depends on: [TASK-AWDD-01-002]
- required reads:
  - docs/agent-workstream-design-review-checklist.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
  - docs/capability-first-backend-architecture.md
- skills:
  - agent-workstream-apps
- expected outputs:
  - updated core doctrine docs as needed
- required checks:
  - first mention uses functional/context-area agent or defines the alias
  - `12-workstreams/` is application model and `55-ui/` is browser realization wherever the description layer is mentioned
- done criteria:
  - doctrine docs have no conflicting terminology or ownership guidance
  - task changes and queue update are committed
- notes:
  - commit message: `Align agent workstream doctrine terminology`
  - completed: aligned core doctrine first-use terminology around functional/context-area agents and added app-description layer ownership rules for `12-workstreams/` application meaning and `55-ui/` browser realization.

### TASK-AWDD-02-002: Standardize app-description architecture and bootstrap UI file sets

- status: done
- source: specs/agent-workstream-design-content-migration/backlog/02-doctrine-and-description-backlog.md
- task brief: specs/agent-workstream-design-content-migration/tasks/02-doctrine-and-description/02-standardize-app-description-ui-file-sets.md
- depends on: [TASK-AWDD-02-001]
- required reads:
  - docs/agent-workstream-design-review-checklist.md
  - docs/internal-app-description-architecture.md
  - skills/app-description-bootstrap/SKILL.md
  - skills/app-description-ui/SKILL.md
  - templates/ai-first-saas-starter/app-description/README.md
- skills:
  - app-description-ui
  - app-description-functional-agent-modeling
  - app-description-surface-modeling
- expected outputs:
  - updated app-description architecture/bootstrap/UI guidance
- required checks:
  - canonical `55-ui/` file set is consistent across files
  - managed-agent UI files are marked mandatory for full core scope and optional/deferred only under explicit narrower scope
- done criteria:
  - app-description bootstraps and UI updates will produce consistent workstream-first structures
  - task changes and queue update are committed
- notes:
  - commit message: `Standardize app-description UI file sets`
  - completed: aligned internal architecture, bootstrap, and UI skill guidance on the canonical seed `55-ui/` file set; clarified `12-workstreams/` versus `55-ui/` ownership; marked managed-agent UI files mandatory for full core and deferrable only under explicit narrower scope.

### TASK-AWDD-03-001: Revise AI-first UI surface routing for workstream placement

- status: done
- source: specs/agent-workstream-design-content-migration/backlog/03-skill-routing-backlog.md
- task brief: specs/agent-workstream-design-content-migration/tasks/03-skill-routing/01-revise-ai-first-ui-surfaces-for-workstream-placement.md
- depends on: [TASK-AWDD-02-002]
- required reads:
  - docs/agent-workstream-design-review-checklist.md
  - skills/ai-first-saas-ui-surfaces/SKILL.md
  - skills/agent-workstream-apps/SKILL.md
  - docs/structured-surface-contracts.md
- skills:
  - ai-first-saas-ui-surfaces
  - agent-workstream-apps
- expected outputs:
  - updated `skills/ai-first-saas-ui-surfaces/SKILL.md`
- required checks:
  - every selected surface requires owning/reusable functional agents and workstream placement
  - routes/deep links remain implementation details
- done criteria:
  - UI surface planning cannot bypass functional-agent workstream modeling
  - task changes and queue update are committed
- notes:
  - commit message: `Revise AI-first UI surface workstream routing`
  - completed: updated AI-first UI surface selection so every surface family requires owning/reusable functional agents, workstream placement, payload/query sources, capability-backed actions, audit/work-trace links, and routes/deep links only as implementation details.

### TASK-AWDD-03-002: Strengthen web UI and generation routing around canonical workstream reference

- status: done
- source: specs/agent-workstream-design-content-migration/backlog/03-skill-routing-backlog.md
- task brief: specs/agent-workstream-design-content-migration/tasks/03-skill-routing/02-strengthen-web-ui-and-generation-routing.md
- depends on: [TASK-AWDD-03-001]
- required reads:
  - docs/agent-workstream-design-review-checklist.md
  - docs/web-ui-pattern-selection.md
  - docs/web-ui-frontend-decomposition.md
  - docs/web-ui-quality-checklist.md
  - skills/akka-web-ui-apps/SKILL.md
  - skills/app-generate-app/SKILL.md
  - skills/app-description-readiness-assessment/SKILL.md
- skills:
  - akka-web-ui-apps
  - app-generate-app
  - app-description-readiness-assessment
- expected outputs:
  - updated web UI docs and generation/readiness guidance as needed
- required checks:
  - generated SaaS UI points to `frontend/src/workstream/**` and User Admin reference vertical
  - legacy `frontend/src/screens/**` and static resources are explicitly mechanics/legacy references only
- done criteria:
  - web UI and generation routing consistently reject page-first generated SaaS realization
  - task changes and queue update are committed
- notes:
  - commit message: `Strengthen web UI generation routing`
  - completed: strengthened web UI pattern selection, generation, and readiness guidance so generated SaaS UI starts from `frontend/src/workstream/**` and the User Admin reference vertical; legacy screens/static examples are mechanics-only and route/page tests alone are insufficient.

### TASK-AWDD-03-003: Review top-level skill routing for design cleanup consistency

- status: done
- source: specs/agent-workstream-design-content-migration/backlog/03-skill-routing-backlog.md
- task brief: specs/agent-workstream-design-content-migration/tasks/03-skill-routing/03-review-top-level-skill-routing-consistency.md
- depends on: [TASK-AWDD-03-002]
- required reads:
  - skills/README.md
  - skills/agent-workstream-apps/SKILL.md
  - skills/app-descriptions/SKILL.md
  - skills/app-description-functional-agent-modeling/SKILL.md
  - skills/app-description-surface-modeling/SKILL.md
  - docs/agent-workstream-design-review-checklist.md
- skills:
  - agent-workstream-apps
  - app-descriptions
- expected outputs:
  - targeted updates to top-level routing or no-op note in task notes if already consistent
- required checks:
  - natural-language UI/dashboard/admin requests route to functional-agent/surface/capability modeling rather than page trees
- done criteria:
  - top-level routing is consistent with lower-level skills
  - task changes and queue update are committed
- notes:
  - commit message: `Tighten top-level workstream routing`
  - completed: clarified that ordinary UI/dashboard/admin/portal/workflow requests route through functional-agent, structured-surface, and capability modeling before browser UI realization or routes.

### TASK-AWDD-04-001: Refresh or label the DCA app-description UI example

- status: done
- source: specs/agent-workstream-design-content-migration/backlog/04-examples-backlog.md
- task brief: specs/agent-workstream-design-content-migration/tasks/04-examples/01-refresh-or-label-dca-ui-example.md
- depends on: [TASK-AWDD-03-003]
- required reads:
  - docs/agent-workstream-design-review-checklist.md
  - docs/examples/ai-first-dca-app-description/README.md
  - docs/examples/ai-first-dca-app-description/app-description/55-ui/README.md
  - docs/examples/ai-first-dca-app-description/app-description/55-ui/ui-surfaces.md
  - templates/ai-first-saas-starter/app-description/README.md
- skills:
  - app-description-ui
  - app-description-functional-agent-modeling
  - app-description-surface-modeling
- expected outputs:
  - updated DCA example UI/readme files or split workstream/surface/UI files if selected by the task
- required checks:
  - DCA example does not appear to be the canonical seed structure if it remains consolidated
  - DCA functional agents, surfaces, capabilities, routes/deep links, and style guidance are clearly placed
- done criteria:
  - DCA example reinforces, or explicitly defers migration to, current canonical structure
  - task changes and queue update are committed
- notes:
  - commit message: `Label DCA UI example as consolidated reference`
  - completed: labeled the DCA UI example as a compact DCA-specific consolidated contract, reaffirmed the seed example as the canonical structure, and clarified placement of functional agents, structured surfaces, capabilities, routes/deep links, state/realtime, accessibility, and style guidance.

### TASK-AWDD-04-002: Verify example cross-links and legacy labels

- status: done
- source: specs/agent-workstream-design-content-migration/backlog/04-examples-backlog.md
- task brief: specs/agent-workstream-design-content-migration/tasks/04-examples/02-verify-example-cross-links-and-legacy-labels.md
- depends on: [TASK-AWDD-04-001]
- required reads:
  - docs/agent-workstream-design-review-checklist.md
  - templates/ai-first-saas-starter/app-description/README.md
  - docs/examples/purchase-request-app-description/README.md
  - docs/examples/ai-first-app-description-gaps.md
  - docs/examples/**/README.md
- skills:
  - app-descriptions
- expected outputs:
  - updated example README/cross-link guidance as needed
- required checks:
  - starter core app is identified as preferred current reference
  - purchase-request and conventional examples remain mechanics-only/non-target where appropriate
- done criteria:
  - example set has no ambiguous generated SaaS target architecture cues
  - task changes and queue update are committed
- notes:
  - commit message: `Clarify example reference labels`
  - completed: added an examples index, labeled the starter core app-description as the preferred current generated-SaaS reference, clarified core PRD inputs versus app-description structure, reinforced purchase-request as mechanics-only, and marked DCA as a domain-rich non-canonical structural reference.

### TASK-AWDD-05-001: Run final design-content consistency review

- status: done
- source: specs/agent-workstream-design-content-migration/backlog/05-final-review-backlog.md
- task brief: specs/agent-workstream-design-content-migration/tasks/05-review/01-final-design-content-consistency-review.md
- depends on: [TASK-AWDD-04-002]
- required reads:
  - specs/agent-workstream-design-content-migration/README.md
  - specs/agent-workstream-design-content-migration/pending-tasks.md
  - docs/agent-workstream-design-review-checklist.md
  - skills/README.md
  - docs/agent-workstream-application-architecture.md
- skills:
  - agent-workstream-apps
- expected outputs:
  - specs/agent-workstream-design-content-migration/migration-completion-summary.md
  - follow-up tasks if drift remains
- required checks:
  - rg searches for page-first/screen-first/CRUD/chatbot drift and inconsistent terminology
  - verify all tasks are done or explicitly superseded
- done criteria:
  - completion summary records final state and remaining follow-ups
  - task changes and queue update are committed
- notes:
  - commit message: `Complete agent workstream design migration review`
  - completed: ran final design-content drift searches, verified queue state, linked the design review checklist from the agent-workstream routing skill, recorded completion summary, and found no required follow-up migration tasks.

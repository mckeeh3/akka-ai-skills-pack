# Pending Tasks

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task must make one git commit before being marked `done`; the commit should include only that task's intended changes and its queue-status update.
- Record the task commit hash in that task's `notes` when possible.
- This queue is for the AI-first skills pack migration, rooted at `specs/ai-first-skills-pack-migration/`.

## Tasks

### TASK-01-001: Promote canonical AI-first SaaS doctrine

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/01-architectural-pivot-and-doctrine-build-backlog.md
- task brief: specs/ai-first-skills-pack-migration/tasks/01-architectural-pivot-and-doctrine/01-canonical-doctrine-promotion.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/ai-first-saas-coding-agent-framework.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/skills-pack-tech-stack.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/ai-first-saas-ui-patterns.md
  - specs/ai-first-skills-pack-migration/sprints/01-architectural-pivot-and-doctrine-sprint.md
  - specs/ai-first-skills-pack-migration/backlog/01-architectural-pivot-and-doctrine-build-backlog.md
  - specs/ai-first-skills-pack-migration/tasks/01-architectural-pivot-and-doctrine/01-canonical-doctrine-promotion.md
- skills:
  - none; use repository guidance and local docs
- expected outputs:
  - docs/ai-first-saas-application-architecture.md
- required checks:
  - verify new links point to existing files or are clearly marked planned/future
  - search for accidental broken links to non-existent AI-first skills
- done criteria:
  - canonical architecture doc exists and states the AI-first default target architecture
  - inbox docs are treated as source material, not authority
- notes:
  - planning-only; no source implementation code

### TASK-01-002: Pivot repository guidance to AI-first default

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/01-architectural-pivot-and-doctrine-build-backlog.md
- task brief: specs/ai-first-skills-pack-migration/tasks/01-architectural-pivot-and-doctrine/02-repository-guidance-pivot.md
- depends on: [TASK-01-001]
- required reads:
  - AGENTS.md
  - README.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/tasks/01-architectural-pivot-and-doctrine/02-repository-guidance-pivot.md
- skills:
  - none; use repository guidance and local docs
- expected outputs:
  - AGENTS.md
  - README.md if needed
- required checks:
  - verify guidance still distinguishes source-pack development from installed-pack usage
  - verify AI-first is described as default architecture without forcing every app to use every pattern
- done criteria:
  - session-start guidance makes the AI-first pivot clear
  - pending queue updated to done
- notes:
  - source requirement: user approved evolving this repository in place
  - completed: repository guidance pivots to AI-first default while preserving source-pack vs installed-pack distinction
  - commit hash: not pre-recorded because the queue status update is included in the same task commit

### TASK-01-003: Add AI-first entry guidance to skill routing map

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/01-architectural-pivot-and-doctrine-build-backlog.md
- task brief: specs/ai-first-skills-pack-migration/tasks/01-architectural-pivot-and-doctrine/03-routing-map-ai-first-entry.md
- depends on: [TASK-01-001, TASK-01-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/tasks/01-architectural-pivot-and-doctrine/03-routing-map-ai-first-entry.md
- skills:
  - none; use repository guidance and local docs
- expected outputs:
  - skills/README.md
- required checks:
  - search for broken direct links to non-existent AI-first skill files
  - verify routing map still supports current skills until Sprint 2 creates new ones
- done criteria:
  - high-level product inputs are routed toward AI-first interpretation before CRUD/component decomposition
- notes:
  - completed: routing map now applies AI-first interpretation before app-description, decomposition, PRD-to-specs/backlog, or Stage 3 implementation routing
  - commit hash: not pre-recorded because the queue status update is included in the same task commit

### TASK-01-004: Document inbox provenance and cleanup plan

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/01-architectural-pivot-and-doctrine-build-backlog.md
- task brief: specs/ai-first-skills-pack-migration/tasks/01-architectural-pivot-and-doctrine/04-inbox-provenance-cleanup-plan.md
- depends on: [TASK-01-001]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/ai-first-saas-coding-agent-framework.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/ai-first-saas-ui-patterns.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/cai-agent-first-saas-design-framework.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/cai-dca-agentic-reconstruction.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/oai-agent-first-dca-office-device-lifecycle.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/oai-agent-first-operating-systems.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/skills-pack-tech-stack.md
  - specs/ai-first-skills-pack-migration/tasks/01-architectural-pivot-and-doctrine/04-inbox-provenance-cleanup-plan.md
- skills:
  - none; use repository guidance and local docs
- expected outputs:
  - specs/ai-first-skills-pack-migration/inbox-provenance-and-disposition.md
- required checks:
  - all current inbox markdown files are listed
  - no competing canonical destinations are introduced without explanation
- done criteria:
  - future cleanup tasks can intentionally promote, merge, archive, or delete each inbox file
- notes:
  - completed: documented provenance and disposition for all current `specs/ai-first-skills-pack-migration/archive/inbox/docs/*.md` files
  - commit hash: not pre-recorded because the queue status update is included in the same task commit

### TASK-02-001: Create top-level AI-first SaaS skill

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/02-ai-first-routing-skill-family-build-backlog.md
- task brief: none
- depends on: [TASK-01-001, TASK-01-003]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/sprints/02-ai-first-routing-skill-family-sprint.md
  - specs/ai-first-skills-pack-migration/backlog/02-ai-first-routing-skill-family-build-backlog.md
- skills:
  - none; create skill source using repo conventions
- expected outputs:
  - skills/ai-first-saas/SKILL.md
- required checks:
  - skill has frontmatter name/description
  - skill routes to app-description, decomposition, and implementation substrate skills without duplicating them
- done criteria:
  - top-level AI-first SaaS entry skill exists and is concise
- notes:
  - completed: added concise top-level AI-first SaaS routing skill with interpretation workflow, anti-chatbot rule, downstream path selection, and Akka substrate mapping
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Add top-level AI-first SaaS skill`

### TASK-02-002: Create object-model and agent-team companion skills

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/02-ai-first-routing-skill-family-build-backlog.md
- task brief: none
- depends on: [TASK-02-001]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - skills/ai-first-saas/SKILL.md
  - specs/ai-first-skills-pack-migration/backlog/02-ai-first-routing-skill-family-build-backlog.md
- skills:
  - ai-first-saas
- expected outputs:
  - skills/ai-first-saas-object-model/SKILL.md
  - skills/ai-first-saas-agent-team-design/SKILL.md
- required checks:
  - companion skills route to existing Akka agent/entity/workflow/view skills where appropriate
- done criteria:
  - durable objects and agent-team design have focused routing guidance
- notes:
  - completed: added focused object-model and agent-team design companion skills that route durable AI-first objects and bounded agent teams to existing Akka implementation families
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Add AI-first object model and agent team skills`

### TASK-02-003: Create governance, decision-card, and audit companion skills

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/02-ai-first-routing-skill-family-build-backlog.md
- task brief: none
- depends on: [TASK-02-001]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - skills/ai-first-saas/SKILL.md
  - specs/ai-first-skills-pack-migration/backlog/02-ai-first-routing-skill-family-build-backlog.md
- skills:
  - ai-first-saas
- expected outputs:
  - skills/ai-first-saas-policy-governance/SKILL.md
  - skills/ai-first-saas-decision-cards/SKILL.md
  - skills/ai-first-saas-audit-trace/SKILL.md
- required checks:
  - no broken links to future/uncreated skills
- done criteria:
  - governance, approval/exception, and audit trace guidance exists
- notes:
  - completed: added focused AI-first policy governance, decision-card, and audit-trace companion skills that route to existing Akka implementation families
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Add AI-first governance decision and audit skills`

### TASK-02-004: Create UI-surfaces and outcomes companion skills

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/02-ai-first-routing-skill-family-build-backlog.md
- task brief: none
- depends on: [TASK-02-001]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/ai-first-saas-ui-patterns.md
  - skills/ai-first-saas/SKILL.md
  - specs/ai-first-skills-pack-migration/backlog/02-ai-first-routing-skill-family-build-backlog.md
- skills:
  - ai-first-saas
- expected outputs:
  - skills/ai-first-saas-ui-surfaces/SKILL.md
  - skills/ai-first-saas-outcomes-metrics/SKILL.md
- required checks:
  - UI guidance points to `akka-web-ui-*` and HTTP endpoint skills instead of replacing them
- done criteria:
  - AI-first UI and outcome validation companion guidance exists
- notes:
  - completed: added AI-first UI surfaces and outcomes/metrics companion skills that route supervision, decision, governance, digest, audit, and outcome loops to existing Akka web UI, endpoint, view, workflow, agent, consumer, timer, and entity skills
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Add AI-first UI surfaces and outcomes skills`

### TASK-02-005: Wire AI-first skills into routing map

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/02-ai-first-routing-skill-family-build-backlog.md
- task brief: none
- depends on: [TASK-02-001, TASK-02-002, TASK-02-003, TASK-02-004]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/backlog/02-ai-first-routing-skill-family-build-backlog.md
- skills:
  - ai-first-saas
- expected outputs:
  - skills/README.md
- required checks:
  - every linked AI-first skill exists
- done criteria:
  - routing map exposes the new AI-first family cleanly
- notes:
  - completed: replaced the Sprint 2 transition note with the top-level AI-first SaaS skill and companion skill routing list in `skills/README.md`
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Wire AI-first skills into routing map`

### TASK-03-001: Refactor app-description architecture docs for AI-first

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/03-app-description-ai-first-refactor-build-backlog.md
- task brief: none
- depends on: [TASK-02-005]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - docs/internal-app-description-architecture.md
  - docs/app-description-maintenance-flow.md
  - specs/ai-first-skills-pack-migration/sprints/03-app-description-ai-first-refactor-sprint.md
  - specs/ai-first-skills-pack-migration/backlog/03-app-description-ai-first-refactor-build-backlog.md
- skills:
  - app-descriptions
  - ai-first-saas
- expected outputs:
  - updated app-description architecture/maintenance docs
- required checks:
  - no contradiction between app-description doctrine and AI-first default
- done criteria:
  - app-description architecture supports agentic substrate sections
- notes:
  - completed: added AI-first operating-model sections to app-description architecture, maintenance flow, and doctrine guidance without forcing them on clearly non-agentic apps
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Refactor app-description docs for AI-first`

### TASK-03-002: Refactor app-description entry and bootstrap skills

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/03-app-description-ai-first-refactor-build-backlog.md
- task brief: none
- depends on: [TASK-03-001]
- required reads:
  - AGENTS.md
  - skills/app-descriptions/SKILL.md
  - skills/app-description-bootstrap/SKILL.md
  - skills/app-description-intake-router/SKILL.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/backlog/03-app-description-ai-first-refactor-build-backlog.md
- skills:
  - app-descriptions
  - ai-first-saas
- expected outputs:
  - updated app-description entry/bootstrap/intake skills
- required checks:
  - broad AI-first product input routes correctly
- done criteria:
  - bootstrapped descriptions can include AI-first sections
- notes:
  - completed: updated app-description entry, bootstrap, and intake routing skills so broad AI-first product input routes through AI-first interpretation and bootstraps `15-operating-model/` sections when delegated operations are in scope
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Refactor app-description entry skills for AI-first`

### TASK-03-003: Refactor capability and behavior app-description skills

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/03-app-description-ai-first-refactor-build-backlog.md
- task brief: none
- depends on: [TASK-03-001]
- required reads:
  - AGENTS.md
  - skills/app-description-capability-modeling/SKILL.md
  - skills/app-description-behavior-specification/SKILL.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/backlog/03-app-description-ai-first-refactor-build-backlog.md
- skills:
  - app-description-capability-modeling
  - app-description-behavior-specification
  - ai-first-saas
- expected outputs:
  - updated capability and behavior skills
- required checks:
  - operational delegation and human governance are represented
- done criteria:
  - capability/behavior layers can express AI-first operating semantics
- notes:
  - completed: updated capability and behavior app-description skills to preserve delegated work, retained human authority, policy/approval/exception semantics, audit traces, learning loops, and outcome accountability before implementation framing
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Refactor capability and behavior skills for AI-first`

### TASK-03-004: Refactor test/security/observability/UI app-description skills

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/03-app-description-ai-first-refactor-build-backlog.md
- task brief: none
- depends on: [TASK-03-001]
- required reads:
  - AGENTS.md
  - skills/app-description-test-specification/SKILL.md
  - skills/app-description-auth-security/SKILL.md
  - skills/app-description-observability/SKILL.md
  - skills/app-description-ui/SKILL.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/backlog/03-app-description-ai-first-refactor-build-backlog.md
- skills:
  - app-description-test-specification
  - app-description-auth-security
  - app-description-observability
  - app-description-ui
  - ai-first-saas
- expected outputs:
  - updated test/security/observability/UI description skills
- required checks:
  - AI-first audit, eval, permissions, traces, and UI surfaces are covered
- done criteria:
  - cross-cutting app-description layers support AI-first apps
- notes:
  - completed: updated test, auth/security, observability, and UI app-description skills to cover AI-first evaluation, permissions, approval gates, audit/work/decision traces, policy/tool/data evidence, supervision and digest surfaces, and outcome links without replacing downstream implementation skills
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Refactor cross-cutting app-description skills for AI-first`

### TASK-03-005: Update app-description examples or placeholders

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/03-app-description-ai-first-refactor-build-backlog.md
- task brief: none
- depends on: [TASK-03-002, TASK-03-003, TASK-03-004]
- required reads:
  - AGENTS.md
  - docs/examples/purchase-request-app-description/README.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/backlog/03-app-description-ai-first-refactor-build-backlog.md
- skills:
  - app-descriptions
  - ai-first-saas
- expected outputs:
  - minimal example notes or placeholders for Sprint 6 example
- required checks:
  - old examples are not force-fit into AI-first without intent
- done criteria:
  - app-description examples/gaps are clearly documented
- notes:
  - completed: documented the purchase-request app description as a low-agentic approval-workflow reference and added `docs/examples/ai-first-app-description-gaps.md` as the Sprint 6 DCA example placeholder and coverage gap list
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Document AI-first app-description example gaps`

### TASK-04-001: Refactor solution decomposition for AI-first interpretation

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/04-prd-spec-backlog-intake-refactor-build-backlog.md
- task brief: none
- depends on: [TASK-02-005]
- required reads:
  - AGENTS.md
  - skills/akka-solution-decomposition/SKILL.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/backlog/04-prd-spec-backlog-intake-refactor-build-backlog.md
- skills:
  - akka-solution-decomposition
  - ai-first-saas
- expected outputs:
  - updated `akka-solution-decomposition` skill and related docs if needed
- required checks:
  - high-level input is not decomposed as CRUD by default
- done criteria:
  - solution decomposition includes an AI-first interpretation phase
- notes:
  - completed: updated solution decomposition to classify high-level inputs through AI-first interpretation before CRUD/component decomposition, add AI-first output fields, route through companion skills, and map substrate objects to Akka components
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Refactor solution decomposition for AI-first`

### TASK-04-002: Refactor PRD-to-specs/backlog generation for AI-first outputs

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/04-prd-spec-backlog-intake-refactor-build-backlog.md
- task brief: none
- depends on: [TASK-04-001]
- required reads:
  - AGENTS.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/backlog/04-prd-spec-backlog-intake-refactor-build-backlog.md
- skills:
  - akka-prd-to-specs-backlog
  - ai-first-saas
- expected outputs:
  - updated PRD-to-specs/backlog skill
- required checks:
  - generated planning package includes AI-first sections when applicable
- done criteria:
  - PRD planning outputs reflect agentic operating model before Akka tasks
- notes:
  - completed: updated PRD-to-specs/backlog generation to require AI-first interpretation in solution plans, cross-cutting specs, module/sprint or slice specs, backlogs, pending questions, and pending tasks before Akka task breakdown
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Refactor PRD planning skill for AI-first`

### TASK-04-003: Refactor iterative change and revised PRD reconciliation

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/04-prd-spec-backlog-intake-refactor-build-backlog.md
- task brief: none
- depends on: [TASK-04-001]
- required reads:
  - AGENTS.md
  - skills/akka-change-request-to-spec-update/SKILL.md
  - skills/akka-revised-prd-reconciliation/SKILL.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/backlog/04-prd-spec-backlog-intake-refactor-build-backlog.md
- skills:
  - akka-change-request-to-spec-update
  - akka-revised-prd-reconciliation
  - ai-first-saas
- expected outputs:
  - updated iterative planning skills
- required checks:
  - governance, audit, policy, and outcome implications are preserved in changes
- done criteria:
  - change/revised PRD flows remain AI-first-aware
- notes:
  - completed: updated iterative change and revised PRD reconciliation skills to preserve AI-first operating-model semantics, governance, audit, policy, approval, UI supervision, and outcome implications during planning deltas and queue reconciliation
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Refactor iterative planning skills for AI-first`

### TASK-04-004: Refactor pending question generation for AI-first blockers

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/04-prd-spec-backlog-intake-refactor-build-backlog.md
- task brief: none
- depends on: [TASK-04-001]
- required reads:
  - AGENTS.md
  - skills/akka-pending-question-generation/SKILL.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/backlog/04-prd-spec-backlog-intake-refactor-build-backlog.md
- skills:
  - akka-pending-question-generation
  - ai-first-saas
- expected outputs:
  - updated pending-question generation skill
- required checks:
  - AI-first blockers are actionable and not cosmetic
- done criteria:
  - unresolved delegation/policy/approval/risk/outcome decisions can be queued safely
- notes:
  - completed: updated pending-question generation to queue actionable AI-first blockers for delegation, authority, approval gates, policies, evidence, risk thresholds, supervision UI, trace obligations, evaluations, and outcome metrics without broad cosmetic interviews
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Refactor pending question generation for AI-first`

### TASK-04-005: Refactor pending task materialization/execution guidance

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/04-prd-spec-backlog-intake-refactor-build-backlog.md
- task brief: none
- depends on: [TASK-04-002, TASK-04-004]
- required reads:
  - AGENTS.md
  - skills/akka-backlog-to-pending-tasks/SKILL.md
  - skills/akka-do-next-pending-task/SKILL.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/backlog/04-prd-spec-backlog-intake-refactor-build-backlog.md
- skills:
  - akka-backlog-to-pending-tasks
  - akka-do-next-pending-task
  - ai-first-saas
- expected outputs:
  - updated task materialization/execution guidance if needed
- required checks:
  - AI-first required reads and skills flow into pending tasks
- done criteria:
  - pending task execution remains bounded and AI-first-aware
- notes:
  - completed: updated backlog-to-pending and do-next-pending-task guidance so AI-first reads, companion skills, blockers, constraints, and no-guessing rules flow into materialized and executed tasks while preserving bounded one-task execution
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Refactor pending task guidance for AI-first`

### TASK-05-001: Reframe agent and workflow implementation skills

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/05-substrate-implementation-skill-reframing-build-backlog.md
- task brief: none
- depends on: [TASK-04-001]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - skills/akka-agents/SKILL.md
  - skills/akka-workflows/SKILL.md
  - specs/ai-first-skills-pack-migration/backlog/05-substrate-implementation-skill-reframing-build-backlog.md
- skills:
  - akka-agents
  - akka-workflows
  - ai-first-saas
- expected outputs:
  - concise updates to agent and workflow skill families
- required checks:
  - avoid duplicating broad doctrine inside component skills
- done criteria:
  - agents/workflows are framed as AI-first substrate implementation options
- notes:
  - completed: added concise AI-first substrate framing to agent and workflow skills, covering bounded operational workers, durable execution plans, approval/exception paths, retries, compensation, and trace/outcome obligations without duplicating broad doctrine
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Reframe agent and workflow skills for AI-first`

### TASK-05-002: Reframe entity and view implementation skills

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/05-substrate-implementation-skill-reframing-build-backlog.md
- task brief: none
- depends on: [TASK-04-001]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - skills/akka-event-sourced-entities/SKILL.md
  - skills/akka-key-value-entities/SKILL.md
  - skills/akka-views/SKILL.md
  - specs/ai-first-skills-pack-migration/backlog/05-substrate-implementation-skill-reframing-build-backlog.md
- skills:
  - akka-event-sourced-entities
  - akka-key-value-entities
  - akka-views
  - ai-first-saas
- expected outputs:
  - concise updates to entity and view skill families
- required checks:
  - event sourcing/audit-grade state guidance remains semantically correct
- done criteria:
  - entity/view skills explain AI-first substrate roles
- notes:
  - completed: added concise AI-first substrate framing to Event Sourced Entity, Key Value Entity, and View skills, distinguishing audit-grade temporal facts from replaceable current state and derived supervision/accountability read models
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Reframe entity and view skills for AI-first`

### TASK-05-003: Reframe consumer, timer, and endpoint skills

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/05-substrate-implementation-skill-reframing-build-backlog.md
- task brief: none
- depends on: [TASK-04-001]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - skills/akka-consumers/SKILL.md
  - skills/akka-timed-actions/SKILL.md
  - skills/akka-http-endpoints/SKILL.md
  - skills/akka-grpc-endpoints/SKILL.md
  - skills/akka-mcp-endpoints/SKILL.md
  - specs/ai-first-skills-pack-migration/backlog/05-substrate-implementation-skill-reframing-build-backlog.md
- skills:
  - akka-consumers
  - akka-timed-actions
  - akka-http-endpoints
  - akka-grpc-endpoints
  - akka-mcp-endpoints
  - ai-first-saas
- expected outputs:
  - concise updates to consumer/timer/endpoint skill families
- required checks:
  - component-specific implementation guidance remains focused
- done criteria:
  - async, scheduled, and edge-delivery skills explain AI-first substrate roles
- notes:
  - completed: added concise AI-first substrate role sections to consumer, timed action, HTTP, gRPC, and MCP endpoint skills covering trace fanout, scheduled digests/replay, browser/service control surfaces, and authority-sensitive MCP exposure while preserving focused implementation guidance
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Reframe async timer and endpoint skills for AI-first`

### TASK-05-004: Reframe web UI implementation skills

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/05-substrate-implementation-skill-reframing-build-backlog.md
- task brief: none
- depends on: [TASK-04-001]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - skills/akka-web-ui-apps/SKILL.md
  - skills/akka-web-ui-ux-design/SKILL.md
  - skills/akka-web-ui-state-rendering/SKILL.md
  - skills/akka-web-ui-realtime/SKILL.md
  - specs/ai-first-skills-pack-migration/backlog/05-substrate-implementation-skill-reframing-build-backlog.md
- skills:
  - akka-web-ui-apps
  - akka-web-ui-ux-design
  - ai-first-saas
- expected outputs:
  - concise updates to web UI skill family
- required checks:
  - UI surfaces align with AI-first doctrine and existing web UI quality guidance
- done criteria:
  - web UI skills support command center, decision, governance, digest, and audit surfaces
- notes:
  - completed: added concise AI-first framing to web UI app, UX, state/rendering, realtime, and accessibility/responsive skills for command center, decision-card, governance, digest, audit/work-trace, outcome, stale-state, and human-authority surfaces
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Reframe web UI skills for AI-first`

### TASK-05-005: Identify missing AI-first examples and tests

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/05-substrate-implementation-skill-reframing-build-backlog.md
- task brief: none
- depends on: [TASK-05-001, TASK-05-002, TASK-05-003, TASK-05-004]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/backlog/05-substrate-implementation-skill-reframing-build-backlog.md
- skills:
  - ai-first-saas
- expected outputs:
  - docs or migration gap list for missing AI-first examples/tests
- required checks:
  - distinguish required gaps from nice-to-have examples
- done criteria:
  - future example/test work is explicit and prioritized
- notes:
  - completed: added `docs/ai-first-examples-and-tests-gap-list.md` with P0/P1/P2 gaps for the DCA worked example, implementation slices, acceptance/evaluation tests, AI-first ESE/View/UI/trace examples, and optional MCP/gRPC/outcome examples
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Identify AI-first example and test gaps`

### TASK-06-001: Design AI-first DCA example structure

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/06-worked-example-and-inbox-cleanup-build-backlog.md
- task brief: none
- depends on: [TASK-03-005, TASK-04-002]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/cai-dca-agentic-reconstruction.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/oai-agent-first-dca-office-device-lifecycle.md
  - specs/ai-first-skills-pack-migration/backlog/06-worked-example-and-inbox-cleanup-build-backlog.md
- skills:
  - app-descriptions
  - ai-first-saas
- expected outputs:
  - docs/examples/agent-first-dca-app-description/README.md
  - initial example file tree
- required checks:
  - example is clearly a reference asset, not this repo's business app
- done criteria:
  - example structure and source mapping are established
- notes:
  - completed: added the agent-first DCA app-description scaffold, source-material mapping, layer README files, and updated the AI-first example gap note to point at the new scaffold
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Design agent-first DCA example structure`

### TASK-06-002: Create DCA product vision and operating model example

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/06-worked-example-and-inbox-cleanup-build-backlog.md
- task brief: none
- depends on: [TASK-06-001]
- required reads:
  - AGENTS.md
  - docs/examples/agent-first-dca-app-description/README.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/oai-agent-first-dca-office-device-lifecycle.md
  - specs/ai-first-skills-pack-migration/backlog/06-worked-example-and-inbox-cleanup-build-backlog.md
- skills:
  - app-descriptions
  - ai-first-saas
- expected outputs:
  - product vision and agentic operating model example files
- required checks:
  - operational delegation and retained human governance are explicit
- done criteria:
  - example has clear AI-first product and operating model foundation
- notes:
  - completed: added DCA app manifest, capability/lifecycle foundation, durable goals, human roles, delegated work, retained human authority, agent-family placeholders, and fail-safe lifecycle behavior guidance
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Create DCA product vision and operating model`

### TASK-06-003: Create DCA agent team, policy, decisions, and workflow examples

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/06-worked-example-and-inbox-cleanup-build-backlog.md
- task brief: none
- depends on: [TASK-06-002]
- required reads:
  - AGENTS.md
  - docs/examples/agent-first-dca-app-description/README.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/oai-agent-first-dca-office-device-lifecycle.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/cai-dca-agentic-reconstruction.md
  - specs/ai-first-skills-pack-migration/backlog/06-worked-example-and-inbox-cleanup-build-backlog.md
- skills:
  - ai-first-saas
  - ai-first-saas-agent-team-design
  - ai-first-saas-policy-governance
  - ai-first-saas-decision-cards
- expected outputs:
  - agent team, policies/approval gates, workflows/decisions example files
- required checks:
  - examples map to Akka substrate concepts where useful
- done criteria:
  - example demonstrates agentic governance and decision flow
- notes:
  - completed: added DCA agent-team design, policy/approval gate examples, decision-card and exception evidence schema, supplies autopilot flow, lifecycle/exception flows, and approval/fail-safe rules with Akka substrate mappings
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Create DCA governance and workflow examples`

### TASK-06-004: Create DCA UI, audit, outcomes, and implementation slices examples

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/06-worked-example-and-inbox-cleanup-build-backlog.md
- task brief: none
- depends on: [TASK-06-003]
- required reads:
  - AGENTS.md
  - docs/examples/agent-first-dca-app-description/README.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/archive/inbox/docs/ai-first-saas-ui-patterns.md
  - specs/ai-first-skills-pack-migration/backlog/06-worked-example-and-inbox-cleanup-build-backlog.md
- skills:
  - ai-first-saas-ui-surfaces
  - ai-first-saas-audit-trace
  - ai-first-saas-outcomes-metrics
  - akka-prd-to-specs-backlog
- expected outputs:
  - UI surfaces, audit/trace/outcomes, and implementation slices example files
- required checks:
  - UI surfaces are supervision/governance oriented, not CRUD dashboards by default
- done criteria:
  - worked example is complete enough to guide future agents
- notes:
  - completed: added DCA supervision-oriented UI surfaces, supply decision/trace screens, business audit trace and outcome metric examples, realization slice sequencing, and AI-first coverage mapping for future agents
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Create DCA UI audit outcomes and slices`

### TASK-06-005: Archive or remove temporary inbox material

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/06-worked-example-and-inbox-cleanup-build-backlog.md
- task brief: none
- depends on: [TASK-01-004, TASK-06-004]
- required reads:
  - AGENTS.md
  - specs/ai-first-skills-pack-migration/inbox-provenance-and-disposition.md
  - specs/ai-first-skills-pack-migration/backlog/06-worked-example-and-inbox-cleanup-build-backlog.md
- skills:
  - ai-first-saas
- expected outputs:
  - inbox files promoted, merged, archived, or deleted according to documented disposition
- required checks:
  - no remaining inbox concept file competes with canonical docs
  - references to moved/deleted files are updated
- done criteria:
  - `skills/inbox/` no longer contains unclassified temporary AI-first concept material
- notes:
  - completed: moved former `skills/inbox/` docs, images, and draft skills to `specs/ai-first-skills-pack-migration/archive/inbox/`, deleted `.DS_Store`, updated active guidance to treat archive material as provenance only, and refreshed final disposition documentation
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Archive temporary AI-first inbox material`

### TASK-07-001: Align installed pack packaging and installed guidance

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- task brief: none
- depends on: [TASK-06-005]
- required reads:
  - AGENTS.md
  - pack/AGENTS.md
  - pack/README.md
  - pack/manifest.yaml
  - install.sh
  - docs/ai-first-saas-application-architecture.md
  - skills/README.md
  - specs/ai-first-skills-pack-migration/sprints/07-ai-first-alignment-hardening-sprint.md
  - specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- skills:
  - ai-first-saas
- expected outputs:
  - pack/AGENTS.md
  - pack/README.md
  - pack/manifest.yaml
  - install.sh
  - optional dist/release generation notes if needed
- required checks:
  - manifest includes all `ai-first-saas*` skill directories and any missing existing skill directory such as `akka-web-ui-ux-design`
  - installer packages AI-first doctrine and DCA worked-example docs referenced by installed skills
  - installed-pack guidance says AI-first SaaS is the default target architecture when applicable
- done criteria:
  - installed pack can provide AI-first docs, routing skills, and worked-example references needed by installed skills
  - git commit created for this task
- notes:
  - completed: aligned installed-pack guidance with the AI-first default, added AI-first skills and missing existing skill directories to the manifest/bundle, and packaged AI-first doctrine plus the DCA worked app-description docs in the installer
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Align installed pack with AI-first guidance`

### TASK-07-002: Audit and fix skill/doc relative paths

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- task brief: none
- depends on: [TASK-07-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - skills/ai-first-saas/SKILL.md
  - skills/ai-first-saas-object-model/SKILL.md
  - skills/ai-first-saas-agent-team-design/SKILL.md
  - skills/ai-first-saas-policy-governance/SKILL.md
  - skills/ai-first-saas-decision-cards/SKILL.md
  - skills/ai-first-saas-audit-trace/SKILL.md
  - skills/ai-first-saas-ui-surfaces/SKILL.md
  - skills/ai-first-saas-outcomes-metrics/SKILL.md
  - skills/akka-solution-decomposition/SKILL.md
  - install.sh
  - specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- skills:
  - ai-first-saas
- expected outputs:
  - corrected relative paths in affected `SKILL.md` files
  - optional link/path audit note or command in task notes
- required checks:
  - source skill paths resolve relative to each skill directory
  - install-time rewrite rules still produce installed-pack paths for `AGENTS.md`, `skills/README.md`, docs, and examples
  - no new broken links to missing docs or skills are introduced
- done criteria:
  - AI-first and decomposition skill required-read paths are correct in source and compatible with install rewriting
  - git commit created for this task
- notes:
  - completed: corrected AI-first and solution-decomposition skill paths to resolve from each skill directory, removed the missing warmup read, updated installer rewrites to handle corrected source example paths, and audited affected source/installed path rewrites
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Audit AI-first skill relative paths`

### TASK-07-003: Align core flow docs with AI-first default

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- task brief: none
- depends on: [TASK-07-002]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - docs/intent-driven-usage-flow.md
  - docs/prd-to-akka-flow.md
  - docs/module-sprint-planning.md
  - docs/solution-plan-to-implementation-queue.md
  - docs/pending-question-queue.md
  - docs/pending-task-queue.md
  - specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- skills:
  - ai-first-saas
  - akka-solution-decomposition
  - akka-prd-to-specs-backlog
- expected outputs:
  - updated core flow docs listed above
- required checks:
  - docs teach AI-first interpretation before CRUD/component decomposition when applicable
  - docs preserve low-token utility and do not duplicate the full doctrine
  - pending question/task docs mention AI-first blockers and context preservation where relevant
- done criteria:
  - core docs are consistent with AI-first routing in `skills/README.md` and `akka-solution-decomposition`
  - git commit created for this task
- notes:
  - completed: aligned core flow, PRD flow, module/sprint planning, solution-to-queue, pending-question, and pending-task docs with AI-first interpretation and context-preservation rules
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Align core flow docs with AI-first default`

### TASK-07-004: Add AI-first checks to app-description lifecycle skills

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- task brief: none
- depends on: [TASK-07-003]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - docs/internal-app-description-architecture.md
  - docs/app-description-maintenance-flow.md
  - skills/app-description-change-impact/SKILL.md
  - skills/app-description-readiness-assessment/SKILL.md
  - skills/app-description-readiness-summary/SKILL.md
  - skills/app-generate-app/SKILL.md
  - specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- skills:
  - app-descriptions
  - app-description-change-impact
  - app-description-readiness-assessment
  - app-description-readiness-summary
  - app-generate-app
  - ai-first-saas
- expected outputs:
  - updated app-description lifecycle skills listed above
- required checks:
  - readiness checks include `15-operating-model/` when delegated operations are in scope
  - change impact detects authority, policy, decision, trace, outcome, and AI-first UI ripple effects
  - generation blocks or surfaces missing AI-first semantics instead of inventing them
- done criteria:
  - app-description lifecycle cannot silently ignore AI-first operating-model completeness
  - git commit created for this task
- notes:
  - completed: updated change-impact, readiness assessment/summary, and generation lifecycle skills to check `15-operating-model/`, delegated authority, policies, decisions, traces, outcomes, and AI-first UI surfaces when in scope; generation now blocks or surfaces missing operating-model semantics instead of inventing them
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Add AI-first app-description lifecycle checks`

### TASK-07-005: Preserve AI-first context in leaf planning and queue skills

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- task brief: none
- depends on: [TASK-07-003]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - skills/akka-slice-spec-to-backlog/SKILL.md
  - skills/akka-backlog-item-to-task-brief/SKILL.md
  - skills/akka-pending-task-queue-maintenance/SKILL.md
  - skills/akka-pending-question-queue-maintenance/SKILL.md
  - skills/akka-do-next-pending-question/SKILL.md
  - skills/akka-entity-type-selection/SKILL.md
  - specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- skills:
  - ai-first-saas
  - akka-slice-spec-to-backlog
  - akka-backlog-item-to-task-brief
  - akka-pending-task-queue-maintenance
  - akka-pending-question-queue-maintenance
  - akka-do-next-pending-question
  - akka-entity-type-selection
- expected outputs:
  - updated leaf planning and queue skills listed above
- required checks:
  - slice/backlog/task brief generation preserves delegated work, retained authority, policies, decisions, traces, UI surfaces, and outcomes when present
  - queue maintenance treats unresolved AI-first authority, approval, policy, risk, trace, UI, evaluation, or outcome decisions as blockers for affected work
  - entity type selection includes AI-first audit-grade object heuristics
- done criteria:
  - follow-on planning and queue maintenance no longer strip AI-first context
  - git commit created for this task
- notes:
  - completed: updated leaf planning, question/task queue maintenance, next-question reconciliation, and entity selection skills to preserve delegated work, retained authority, policy, decision-card, audit/trace, UI-surface, evaluation, and outcome context; unresolved AI-first authority, approval, policy, risk, trace, UI, evaluation, or outcome decisions now block affected work instead of being guessed
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Preserve AI-first context in leaf planning skills`

### TASK-07-006: Reconcile DCA example and AI-first gap docs

- status: done
- source: specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- task brief: none
- depends on: [TASK-07-004, TASK-07-005]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - docs/ai-first-examples-and-tests-gap-list.md
  - docs/examples/ai-first-app-description-gaps.md
  - docs/examples/agent-first-dca-app-description/README.md
  - docs/examples/agent-first-dca-app-description/app-description/15-operating-model/README.md
  - docs/examples/agent-first-dca-app-description/app-description/50-observability/audit-trace-and-outcomes.md
  - docs/examples/agent-first-dca-app-description/app-description/60-generation/implementation-slices.md
  - specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- skills:
  - ai-first-saas
  - app-descriptions
- expected outputs:
  - updated DCA example README/layer notes and gap docs
- required checks:
  - gap docs distinguish completed DCA app-description coverage from remaining executable implementation/test gaps
  - no stale “planned fill order” language remains for completed Sprint 6 tasks
  - expected app-description file placement is consistent for audit/trace/outcome material
- done criteria:
  - DCA example and gap docs accurately reflect current AI-first coverage and remaining work
  - git commit created for this task
- notes:
  - completed: reconciled DCA app-description and gap docs to mark Sprint 6 reference-description coverage complete, distinguish remaining executable implementation/test gaps, remove stale planned-fill language, and clarify that trace/outcome meaning belongs in `15-operating-model/` while concrete audit/metric/test expectations live in `50-observability/`
  - commit hash: not embedded because amending the queue note changes the commit hash; see the task commit `Reconcile DCA example and gap docs`

### TASK-07-007: Plan the first executable AI-first reference implementation slice

- status: pending
- source: specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- task brief: none
- depends on: [TASK-07-006]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - docs/ai-first-examples-and-tests-gap-list.md
  - docs/examples/agent-first-dca-app-description/README.md
  - docs/examples/agent-first-dca-app-description/app-description/60-generation/implementation-slices.md
  - specs/ai-first-skills-pack-migration/sprints/07-ai-first-alignment-hardening-sprint.md
  - specs/ai-first-skills-pack-migration/backlog/07-ai-first-alignment-hardening-build-backlog.md
- skills:
  - ai-first-saas
  - akka-prd-to-specs-backlog
  - akka-solution-decomposition
- expected outputs:
  - future sprint/backlog/task plan for one executable AI-first reference slice
  - optional updates to `docs/ai-first-examples-and-tests-gap-list.md` pointing to the new plan
- required checks:
  - selected slice is small enough for follow-on implementation tasks
  - plan names concrete Akka substrate components, web/API surfaces, and tests
  - plan does not implement code
- done criteria:
  - a follow-on implementation plan exists for the first executable AI-first reference slice
  - git commit created for this task

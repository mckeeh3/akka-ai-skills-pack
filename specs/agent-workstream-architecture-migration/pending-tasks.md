# Pending Tasks

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task must make one git commit before being marked `done`; the commit should include only that task's intended changes and its queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- This queue is for the agent workstream architecture migration, rooted at `specs/agent-workstream-architecture-migration/`.

## Tasks

### TASK-AW-00-001: Create agent workstream migration planning scaffold

- status: done
- source: user request to realign the skills pack around agent workstream architecture
- task brief: specs/agent-workstream-architecture-migration/tasks/00-planning-scaffold/00-create-agent-workstream-migration-plan.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/agent-workstream-architecture-migration/README.md
  - specs/agent-workstream-architecture-migration/conversation-capture.md
  - specs/agent-workstream-architecture-migration/pending-tasks.md
  - specs/agent-workstream-architecture-migration/sprints/*.md
  - specs/agent-workstream-architecture-migration/backlog/*.md
- required checks:
  - verify git status contains only migration planning scaffold files before commit
- done criteria:
  - migration has captured rationale, sprint sequence, backlogs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `Add agent workstream architecture migration plan`
  - completed: created planning scaffold, conversation capture, sprint specs, build backlogs, and queued implementation tasks.

### TASK-AW-01-001: Create canonical agent workstream doctrine

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/01-agent-workstream-doctrine-and-routing-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/01-doctrine-routing/01-create-agent-workstream-doctrine.md
- depends on: [TASK-AW-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - specs/agent-workstream-architecture-migration/README.md
  - specs/agent-workstream-architecture-migration/conversation-capture.md
  - specs/agent-workstream-architecture-migration/sprints/01-agent-workstream-doctrine-and-routing-sprint.md
- skills:
  - none; doctrine task
- expected outputs:
  - docs/agent-workstream-application-architecture.md
- required checks:
  - doctrine states agent workstream is the default generated-app UI/application architecture
  - doctrine distinguishes functional agents, internal agents, surfaces, capabilities, and Akka horizontals
  - doctrine does not present page-first or chatbot-bolt-on alternatives as equivalent defaults
- done criteria:
  - canonical doctrine exists and can guide routing and downstream skill updates
  - task changes and queue update are committed
- notes:
  - commit message: `Add agent workstream application doctrine`
  - completed: created canonical doctrine defining the mandatory agent workstream default, functional and internal agents, surfaces, capability mapping, and horizontal Akka implementation rules.

### TASK-AW-01-002: Integrate workstream architecture into AI-first SaaS doctrine

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/01-agent-workstream-doctrine-and-routing-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/01-doctrine-routing/02-integrate-ai-first-doctrine.md
- depends on: [TASK-AW-01-001]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-workstream-application-architecture.md
  - specs/agent-workstream-architecture-migration/sprints/01-agent-workstream-doctrine-and-routing-sprint.md
- skills:
  - none; doctrine integration task
- expected outputs:
  - docs/ai-first-saas-application-architecture.md
- required checks:
  - secure SaaS foundation remains mandatory
  - capability-first backend substrate remains intact
  - primary UX model becomes agent workstream shell, not page hierarchy
- done criteria:
  - AI-first doctrine points to agent workstreams as the default generated UI/application architecture
  - task changes and queue update are committed
- notes:
  - commit message: `Integrate workstream architecture into AI-first doctrine`
  - completed: revised the AI-first SaaS doctrine to name the agent workstream shell as the default generated UI/application model while preserving mandatory secure SaaS foundation and capability-first backend sequencing.

### TASK-AW-01-003: Update skill routing map for agent workstream architecture

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/01-agent-workstream-doctrine-and-routing-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/01-doctrine-routing/03-update-routing-map.md
- depends on: [TASK-AW-01-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-workstream-application-architecture.md
- skills:
  - none; routing-map task
- expected outputs:
  - skills/README.md
- required checks:
  - routing map names agent workstream architecture as mandatory default for generated full-stack AI-first SaaS apps
  - routing still sends backend behavior through capability-first modeling
  - no broken links to uncreated skills unless marked as created in same task
- done criteria:
  - top-level routing reflects the new architecture
  - task changes and queue update are committed
- notes:
  - commit message: `Update routing map for workstream architecture`
  - completed: updated top-level routing, description-first routing, intent flow, PRD planning, solution decomposition, and web UI routing to interpret generated full-stack AI-first SaaS apps as agent workstream applications before capability modeling and Akka implementation.
  - checks: verified agent workstream/capability-first references in `skills/README.md`; verified no references to uncreated `agent-workstream-apps` skill were introduced.

### TASK-AW-01-004: Create top-level agent workstream apps skill

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/01-agent-workstream-doctrine-and-routing-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/01-doctrine-routing/04-create-agent-workstream-skill.md
- depends on: [TASK-AW-01-003]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/agent-workstream-application-architecture.md
  - docs/capability-first-backend-architecture.md
- skills:
  - none; create skill source using repo conventions
- expected outputs:
  - skills/agent-workstream-apps/SKILL.md
  - pack/manifest.yaml if skills are explicitly listed there
- required checks:
  - skill frontmatter has name and description
  - skill routes to app-description, capability-first, web UI, agent, and decomposition skills
  - skill says functional agents and surfaces are verticals; Akka components are horizontals
- done criteria:
  - top-level workstream routing skill exists and is exposed as appropriate
  - task changes and queue update are committed
- notes:
  - commit message: `Add agent workstream apps skill`
  - completed: created the top-level `agent-workstream-apps` routing skill, exposed it in `skills/README.md`, and added it plus the canonical workstream doctrine reference to `pack/manifest.yaml`.
  - checks: verified skill frontmatter, routing to app-description/capability-first/web UI/agent/decomposition paths, and vertical functional-agent/surface versus horizontal Akka implementation language.

### TASK-AW-01-005: Sprint 1 consistency review

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/01-agent-workstream-doctrine-and-routing-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/01-doctrine-routing/05-sprint-1-consistency-review.md
- depends on: [TASK-AW-01-004]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-workstream-application-architecture.md
  - skills/agent-workstream-apps/SKILL.md
  - specs/agent-workstream-architecture-migration/README.md
- skills:
  - agent-workstream-apps
- expected outputs:
  - review notes in pending task and small doctrine/routing fixes if needed
- required checks:
  - `rg -n "agent workstream|functional agent|context-area agent|surface" docs skills pack --glob '!specs/**'`
  - verify no broad app-description/UI skill rewrite occurred in Sprint 1
- done criteria:
  - Sprint 1 is ready for app-description realignment
  - task changes and queue update are committed
- notes:
  - commit message: `Review Sprint 1 workstream consistency`
  - completed: reviewed Sprint 1 doctrine, routing map, new top-level skill, manifest exposure, and migration plan for default architecture clarity and scope control.
  - checks: ran the required `rg` query; reviewed Sprint 1 commit stats and confirmed no broad app-description or UI skill rewrite occurred.
  - small fix: aligned `docs/capability-first-backend-architecture.md` default sequence, design sequence, and routing implications so capability-first remains the backend substrate after secure SaaS and agent workstream interpretation.

### TASK-AW-02-001: Revise app-description architecture for workstream verticals

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/02-description-model-realignment-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/02-app-description/01-revise-app-description-architecture.md
- depends on: [TASK-AW-01-005]
- required reads:
  - AGENTS.md
  - docs/internal-app-description-architecture.md
  - docs/app-description-maintenance-flow.md
  - docs/agent-workstream-application-architecture.md
  - skills/app-descriptions/SKILL.md
- skills:
  - app-descriptions
  - agent-workstream-apps
- expected outputs:
  - docs/internal-app-description-architecture.md
  - docs/app-description-maintenance-flow.md as needed
- required checks:
  - app-description model has functional agents, internal agents, surfaces, capabilities, horizontals
  - legacy page/screen hierarchy is not primary
- done criteria:
  - description architecture supports the new vertical/horizontal model
  - task changes and queue update are committed
- notes:
  - commit message: `Revise app-description architecture for workstreams`
  - completed: revised internal app-description architecture to add `12-workstreams/`, functional-agent/internal-agent/surface contracts, surface-to-capability and capability-to-horizontal maps, workstream-first UI artifacts, and cross-layer invariants that keep page/screen hierarchy subordinate.
  - checks: verified the app-description model names functional agents, internal agents, structured surfaces, governed capabilities, and horizontal Akka implementation maps; verified legacy page/screen language is only retained as subordinate route/deep-link guidance or anti-primary warnings.

### TASK-AW-02-002: Add functional-agent app-description modeling guidance

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/02-description-model-realignment-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/02-app-description/02-functional-agent-modeling.md
- depends on: [TASK-AW-02-001]
- required reads:
  - AGENTS.md
  - skills/app-description-capability-modeling/SKILL.md
  - skills/app-description-ui/SKILL.md
  - docs/agent-workstream-application-architecture.md
- skills:
  - app-descriptions
  - agent-workstream-apps
- expected outputs:
  - new or revised app-description skill for functional-agent modeling
  - skills/README.md updates if adding a skill
- required checks:
  - guidance captures role authorization, prompt intent, skills, tools, surfaces, capabilities, traces, tests
- done criteria:
  - app-description path can model user-facing context-area agents
  - task changes and queue update are committed
- notes:
  - commit message: `Add functional agent app-description skill`
  - completed: created `app-description-functional-agent-modeling`, exposed it in routing and manifest metadata, and linked capability/UI/app-description orchestration to the new functional-agent modeling path.
  - checks: verified the skill frontmatter and guidance covers role authorization, prompt intent, skills, tools, surfaces, capabilities, traces, and tests.

### TASK-AW-02-003: Add surface modeling guidance

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/02-description-model-realignment-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/02-app-description/03-surface-modeling.md
- depends on: [TASK-AW-02-002]
- required reads:
  - AGENTS.md
  - docs/agent-workstream-application-architecture.md
  - skills/app-description-ui/SKILL.md
  - docs/capability-first-backend-architecture.md
- skills:
  - app-descriptions
  - agent-workstream-apps
- expected outputs:
  - new or revised app-description surface guidance/skill
- required checks:
  - surface types include dashboards, forms, tables, charts, decision cards, diffs, audit timelines, detail cards, approvals, workflow status
  - surfaces map to capabilities and may be reused across agents
- done criteria:
  - app-description path can specify structured workstream surfaces
  - task changes and queue update are committed
- notes:
  - commit message: `Add app-description surface modeling skill`
  - completed: created `app-description-surface-modeling`, exposed it in routing and manifest metadata, and linked app-description orchestration, functional-agent modeling, and UI guidance to the new structured-surface contract path.
  - checks: verified the new guidance includes dashboards, forms, tables, charts, decision cards, diffs, audit timelines, detail cards, approvals, workflow status, capability mappings, and reusable functional-agent placement.

### TASK-AW-02-004: Update app-description readiness gates

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/02-description-model-realignment-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/02-app-description/04-update-readiness-gates.md
- depends on: [TASK-AW-02-003]
- required reads:
  - AGENTS.md
  - skills/app-description-readiness-assessment/SKILL.md
  - docs/agent-workstream-application-architecture.md
- skills:
  - app-descriptions
- expected outputs:
  - readiness skill/doc updates
- required checks:
  - missing functional agents/surfaces for consequential authenticated work marks app not ready
  - missing User Admin or Agent Admin marks full core app not ready unless narrower scope is explicit
- done criteria:
  - readiness assessment enforces workstream architecture
  - task changes and queue update are committed
- notes:
  - commit message: `Update app-description readiness gates for workstreams`
  - completed: revised readiness assessment to add an agent workstream application completeness dimension, block generation when consequential authenticated work lacks functional agents or structured surfaces, and require User Admin and Agent Admin for full core SaaS scope unless an explicit narrower scope is recorded.
  - checks: verified readiness guidance marks missing functional agents/surfaces as not ready and marks missing User Admin or Agent Admin as blocking for full core app scope unless explicitly narrowed.

### TASK-AW-02-005: Refresh app-description examples for workstream model

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/02-description-model-realignment-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/02-app-description/05-refresh-description-examples.md
- depends on: [TASK-AW-02-004]
- required reads:
  - AGENTS.md
  - templates/ai-first-saas-starter/app-description/README.md
  - docs/examples/purchase-request-app-description/README.md
  - docs/agent-workstream-application-architecture.md
- skills:
  - app-descriptions
  - agent-workstream-apps
- expected outputs:
  - refreshed starter core app-description example files
  - purchase-request example notes revised or marked mechanics-only where needed
- required checks:
  - examples do not teach page-first primary structure
- done criteria:
  - examples demonstrate functional agents, surfaces, capabilities, and horizontals
  - task changes and queue update are committed
- notes:
  - commit message: `Refresh app-description examples for workstreams`
  - completed: refreshed the starter core app-description example with `12-workstreams/`, functional-agent/internal-agent guidance, structured surface contracts, workstream shell UI files, route/deep-link subordinate guidance, traceability maps, and a horizontal implementation map; marked the purchase-request example as mechanics-only.
  - checks: verified examples demonstrate functional agents, surfaces, capabilities, and horizontal maps; ran `rg -n "page-first|CRUD-first|chatbot|primary screen|primary screens|screens-and-navigation|functional agent|structured surface|horizontal" templates/ai-first-saas-starter/app-description docs/examples/purchase-request-app-description` and confirmed page/screen references are compatibility or anti-primary notes rather than primary structure guidance.

### TASK-AW-03-001: Revise web UI skills for workstream shell

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/03-ui-and-agent-skill-realignment-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/03-ui-skills/01-revise-web-ui-skills.md
- depends on: [TASK-AW-02-005]
- required reads:
  - AGENTS.md
  - docs/agent-workstream-application-architecture.md
  - skills/akka-web-ui-apps/SKILL.md
  - skills/akka-web-ui-frontend-project/SKILL.md
  - skills/akka-http-endpoint-web-ui/SKILL.md
  - docs/web-ui-pattern-selection.md
- skills:
  - agent-workstream-apps
  - akka-web-ui-apps
- expected outputs:
  - revised web UI skills/docs
- required checks:
  - left rail functional agents, stream panel, bottom composer, and structured surfaces are default
  - conventional route/page navigation is implementation detail/deep-linking only
- done criteria:
  - web UI skill path targets the workstream shell
  - task changes and queue update are committed
- notes:
  - commit message: `Revise web UI skills for workstream shell`
  - completed: revised the top-level web UI app skill, frontend project integration skill, Akka HTTP web UI hosting skill, and companion web UI docs/UX guidance to make the left-rail functional-agent shell, main workstream, bottom composer, context indicators, structured surfaces, capability-backed actions, and deep links the default generated SaaS UI model.
  - checks: verified left rail functional agents, stream/workstream panel, bottom composer/persistent composer, and structured surfaces are described as the default; verified conventional route/page navigation is framed as implementation and deep-link detail only.

### TASK-AW-03-002: Define structured surface contracts

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/03-ui-and-agent-skill-realignment-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/03-ui-skills/02-structured-surface-contracts.md
- depends on: [TASK-AW-03-001]
- required reads:
  - AGENTS.md
  - docs/agent-workstream-application-architecture.md
  - docs/capability-first-backend-architecture.md
- skills:
  - agent-workstream-apps
  - capability-first-backend
- expected outputs:
  - doc or skill guidance for surface payload schemas, actions, events, auth, and rendering tests
- required checks:
  - surface action payloads map to capabilities
  - backend authorization remains authoritative
- done criteria:
  - structured response surfaces have implementable contracts
  - task changes and queue update are committed
- notes:
  - commit message: `Add structured surface contract guidance`
  - completed: added canonical structured surface contract guidance covering surface envelopes, action envelopes, realtime events, capability mapping, backend-authoritative auth, audit/trace fields, UI states, and rendering/action/realtime/security tests.
  - checks: verified surface action payloads map to governed capabilities and backend authorization remains authoritative in the new doc and linked skill/checklist guidance.

### TASK-AW-03-003: Revise agent skills for functional and internal agents

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/03-ui-and-agent-skill-realignment-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/03-ui-skills/03-revise-agent-skills.md
- depends on: [TASK-AW-03-002]
- required reads:
  - AGENTS.md
  - docs/agent-workstream-application-architecture.md
  - docs/agent-coverage-matrix.md
  - skills/ai-first-saas-agent-team-design/SKILL.md
  - skills/akka-agent-behavior-profiles/SKILL.md
- skills:
  - agent-workstream-apps
- expected outputs:
  - revised agent skills/docs where needed
- required checks:
  - functional/context-area agents are distinguished from internal agents
  - tools remain capability exposure surfaces, not root design objects
- done criteria:
  - agent guidance supports the new UX/application model
  - task changes and queue update are committed
- notes:
  - commit message: `Revise agent skills for workstream agents`
  - completed: revised top-level agent guidance, agent team design, behavior profiles, agent component guidance, and agent coverage routing to distinguish functional/context-area agents from internal backend agents.
  - checks: verified functional/context-area and internal-agent guidance appears in revised agent skills; verified agent tools are described as capability exposure surfaces rather than root design objects.

### TASK-AW-04-001: Create canonical core app PRD for workstream foundation

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/04-core-app-prd-and-seed-realignment-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/04-core-app-prd/01-create-core-app-prd.md
- depends on: [TASK-AW-03-003]
- required reads:
  - AGENTS.md
  - docs/agent-workstream-application-architecture.md
  - docs/examples/core-ai-first-saas-input/README.md
  - docs/examples/core-ai-first-saas-input/01-core-seed-progression-plan.md
  - docs/examples/core-ai-first-saas-input/04-module-user-admin-prd.md
  - docs/examples/core-ai-first-saas-input/05-module-agent-definition-prd.md
- skills:
  - agent-workstream-apps
  - app-descriptions
- expected outputs:
  - canonical core app PRD or revised core input docs
- required checks:
  - full core scope requires User Admin and Agent Admin functional agents
  - Module 1-only scope is explicit if user/admin/agent admin are deferred
- done criteria:
  - getting-started generation has a hard PRD target
  - task changes and queue update are committed
- notes:
  - commit message: `Add canonical core app PRD`
  - completed: added `10-canonical-core-app-prd.md` as the hard full-core PRD target and updated the core input README/progression plan to require the workstream shell plus Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional agents.
  - checks: verified full core requires User Admin and Agent Admin functional agents and that Module 1-only scope is explicitly labeled `Module 1-only / not full core` when User/Admin/Agent Admin are deferred.

### TASK-AW-04-002: Update getting-started prompt/docs

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/04-core-app-prd-and-seed-realignment-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/04-core-app-prd/02-update-getting-started.md
- depends on: [TASK-AW-04-001]
- required reads:
  - AGENTS.md
  - docs/agent-workstream-application-architecture.md
  - docs/examples/core-ai-first-saas-input/README.md
  - skills/README.md
- skills:
  - agent-workstream-apps
- expected outputs:
  - revised getting-started docs/prompts where present
- required checks:
  - prompt launches from canonical core PRD
  - asks/records scope choice before generation
  - does not allow full core app to omit user admin or agent admin
- done criteria:
  - getting-started path is PRD-backed and scoped
  - task changes and queue update are committed
- notes:
  - commit message: `Update getting-started core PRD prompt`
  - completed: revised README and user-guide getting-started prompts to copy the canonical core PRD, create an explicit scope-choice input, require Full core versus `Module 1-only / not full core` before generation, and reject treating full core as complete without User Admin and Agent Admin functional agents.
  - packaging: added `10-canonical-core-app-prd.md` to pack manifest references, install layout docs, and build-pack doc copy list so the prompt's installed path is available.
  - checks: ran `bash -n tools/build-pack.sh`, `git diff --check`, and `rg -n "10-canonical-core-app-prd|core-app-prd|scope-choice|Full core|Module 1-only / not full core|User Admin and Agent Admin" README.md docs/skills-pack-user-guide.md pack/README.md pack/manifest.yaml tools/build-pack.sh`.

### TASK-AW-05-001: Inventory legacy architecture drift

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/05-legacy-content-removal-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/05-legacy-cleanup/01-inventory-legacy-drift.md
- depends on: [TASK-AW-04-002]
- required reads:
  - AGENTS.md
  - specs/agent-workstream-architecture-migration/README.md
  - docs/agent-workstream-application-architecture.md
  - skills/README.md
- skills:
  - agent-workstream-apps
- expected outputs:
  - specs/agent-workstream-architecture-migration/legacy-content-inventory.md
  - new cleanup tasks appended to this queue if needed
- required checks:
  - search docs, skills, examples, and pack for page-first/CRUD-first/chatbot-bolt-on/default screen language
- done criteria:
  - migration has an actionable legacy-content inventory
  - task changes and queue update are committed
- notes:
  - commit message: `Inventory legacy workstream drift`
  - completed: created an actionable legacy-content inventory covering docs, skills, examples, pack guidance, and README exposure.
  - checks: ran the required drift search across `docs`, `skills`, `pack`, and `README.md`; identified remaining cleanup candidates around app-description UI/bootstrap and example `screens-and-navigation` references.
  - follow-up: no new task IDs were needed because existing `TASK-AW-05-002` is broad enough to perform the identified cleanup.

### TASK-AW-05-002: Remove or revise legacy UI alternatives

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/05-legacy-content-removal-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/05-legacy-cleanup/02-remove-revise-legacy-ui.md
- depends on: [TASK-AW-05-001]
- required reads:
  - AGENTS.md
  - specs/agent-workstream-architecture-migration/legacy-content-inventory.md
  - docs/agent-workstream-application-architecture.md
- skills:
  - agent-workstream-apps
- expected outputs:
  - removed/revised docs, skills, examples, and manifest entries identified by the inventory
- required checks:
  - current installed-pack guidance no longer presents conflicting default UX architectures
- done criteria:
  - known legacy UI alternatives are removed or reframed
  - task changes and queue update are committed
- notes:
  - commit message: `Remove legacy UI architecture drift`
  - completed: reframed app-description UI routing around the workstream shell, functional-agent surfaces, typed surface contracts, and routes/deep links; updated bootstrap UI skeleton; removed the seed example legacy `screens-and-navigation.md`; and refreshed DCA UI/example wording.
  - checks: ran the whole-pack drift search across `docs`, `skills`, `pack`, and `README.md`; ran a pack-only drift check confirming no installed-pack guidance presents a conflicting default UX architecture; ran `git diff --check`.

### TASK-AW-06-001: Final migration consistency review

- status: done
- source: specs/agent-workstream-architecture-migration/backlog/06-final-consistency-review-build-backlog.md
- task brief: specs/agent-workstream-architecture-migration/tasks/06-review/01-final-consistency-review.md
- depends on: [TASK-AW-05-002]
- required reads:
  - AGENTS.md
  - specs/agent-workstream-architecture-migration/README.md
  - specs/agent-workstream-architecture-migration/pending-tasks.md
  - docs/agent-workstream-application-architecture.md
  - skills/README.md
- skills:
  - agent-workstream-apps
- expected outputs:
  - specs/agent-workstream-architecture-migration/migration-completion-summary.md
  - follow-up tasks if gaps remain
- required checks:
  - whole-pack search for drift terms
  - verify routing, docs, examples, and skills agree
- done criteria:
  - migration completion status is documented
  - task changes and queue update are committed
- notes:
  - commit message: `Complete agent workstream migration review`
  - completed: wrote the migration completion summary, verified routing/docs/examples/skills alignment, confirmed remaining legacy-term hits are anti-drift or route/deep-link implementation notes, and found no required follow-up tasks.
  - fixes: added workstream doctrine and structured surface docs to pack build output, updated stale seed UI packaging entries, and refreshed installed layout documentation.
  - checks: ran whole-pack drift search; verified `PACK_DOC_FILES` has no missing paths; ran `bash -n tools/build-pack.sh`, `git diff --check`, and `bash tools/build-pack.sh --output-dir /tmp/akka-ai-skills-pack-review-build --clean --no-archive`.

# Pending Tasks

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task must make one git commit before being marked `done`; the commit should include only that task's intended changes and its queue-status update.
- Record the task commit hash in that task's `notes` when practical. If embedding the hash would require amending the same commit, reference the commit message instead.
- This queue is for the capability-first backend migration, rooted at `specs/capability-first-backend-migration/`.

## Tasks

### TASK-00-001: Create capability-first migration planning scaffold

- status: done
- source: user request to start the capability-first backend architecture migration
- task brief: none
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - specs/ai-first-skills-pack-migration/README.md
  - specs/ai-first-skills-pack-migration/pending-tasks.md
- skills:
  - none; repository planning task
- expected outputs:
  - specs/capability-first-backend-migration/README.md
  - specs/capability-first-backend-migration/pending-tasks.md
  - specs/capability-first-backend-migration/sprints/*.md
  - specs/capability-first-backend-migration/backlog/*.md
- required checks:
  - verify git status contains only migration planning files before commit
- done criteria:
  - migration has sprint sequence, backlogs, and initial pending queue
- notes:
  - completed: created planning scaffold for six-sprint capability-first backend migration
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Add capability-first backend migration plan`

### TASK-01-001: Create canonical capability-first backend doctrine

- status: done
- source: specs/capability-first-backend-migration/backlog/01-capability-first-doctrine-and-routing-build-backlog.md
- task brief: none
- depends on: [TASK-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-coverage-matrix.md
  - akka-context/sdk/agents/extending.html.md
  - skills/akka-agent-tools/SKILL.md
  - skills/akka-agent-component-tools/SKILL.md
  - specs/capability-first-backend-migration/README.md
  - specs/capability-first-backend-migration/sprints/01-capability-first-doctrine-and-routing-sprint.md
  - specs/capability-first-backend-migration/backlog/01-capability-first-doctrine-and-routing-build-backlog.md
- skills:
  - none; doctrine task using repository guidance and official Akka docs
- expected outputs:
  - docs/capability-first-backend-architecture.md
- required checks:
  - doctrine distinguishes Capability from agent tool
  - doctrine keeps secure SaaS foundation mandatory
  - doctrine explains exposure surfaces without implying all capabilities should be tool-exposed
  - links point to existing files or are clearly marked planned/future
- done criteria:
  - canonical capability-first architecture doc exists and can guide later routing/skill tasks
- notes:
  - completed: created `docs/capability-first-backend-architecture.md` as canonical capability-first backend doctrine
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Add capability-first backend doctrine`

### TASK-01-002: Integrate capability-first substrate into AI-first doctrine

- status: done
- source: specs/capability-first-backend-migration/backlog/01-capability-first-doctrine-and-routing-build-backlog.md
- task brief: none
- depends on: [TASK-01-001]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - specs/capability-first-backend-migration/sprints/01-capability-first-doctrine-and-routing-sprint.md
- skills:
  - none; doctrine integration task
- expected outputs:
  - docs/ai-first-saas-application-architecture.md
- required checks:
  - secure SaaS foundation remains mandatory
  - AI-first operating model remains intact
  - capability-first language does not collapse into agent-tool-only design
- done criteria:
  - AI-first doctrine explicitly positions capabilities as the backend substrate below the AI-first operating model
- notes:
  - completed: updated AI-first SaaS doctrine to position governed backend capabilities below the secure AI-first operating model and before Akka components or exposure surfaces
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Integrate capability-first substrate into AI-first doctrine`

### TASK-01-003: Update skill routing map for capability-first design

- status: done
- source: specs/capability-first-backend-migration/backlog/01-capability-first-doctrine-and-routing-build-backlog.md
- task brief: none
- depends on: [TASK-01-001, TASK-01-002]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - specs/capability-first-backend-migration/sprints/01-capability-first-doctrine-and-routing-sprint.md
- skills:
  - none; routing-map task
- expected outputs:
  - skills/README.md
- required checks:
  - high-level implementation/product input routes through capability modeling before component implementation
  - no broken links to uncreated skills unless clearly marked planned/future
  - existing Stage 1/2/3 model still makes sense
- done criteria:
  - routing map reflects capability-first backend architecture as the next step after secure AI-first SaaS interpretation
- notes:
  - completed: updated `skills/README.md` so secure AI-first SaaS routing now models capability-first backend contracts before description, decomposition, PRD planning, or Stage 3 component implementation
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Update skill routing for capability-first backend`

### TASK-01-004: Create top-level capability-first backend skill

- status: done
- source: specs/capability-first-backend-migration/backlog/01-capability-first-doctrine-and-routing-build-backlog.md
- task brief: none
- depends on: [TASK-01-001, TASK-01-003]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/capability-first-backend-architecture.md
  - docs/ai-first-saas-application-architecture.md
  - specs/capability-first-backend-migration/sprints/01-capability-first-doctrine-and-routing-sprint.md
- skills:
  - none; create skill source using repo conventions
- expected outputs:
  - skills/capability-first-backend/SKILL.md
- required checks:
  - skill has frontmatter name/description
  - skill routes to app-description, decomposition, and Stage 3 component skills without duplicating them
  - skill states agent tools are one exposure path, not the root abstraction
- done criteria:
  - top-level capability-first routing skill exists and is concise
- notes:
  - completed: created `skills/capability-first-backend/SKILL.md` and updated routing references that previously described the skill as planned/future
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Add capability-first backend skill`

### TASK-01-005: Sprint 1 consistency review

- status: done
- source: specs/capability-first-backend-migration/backlog/01-capability-first-doctrine-and-routing-build-backlog.md
- task brief: none
- depends on: [TASK-01-001, TASK-01-002, TASK-01-003, TASK-01-004]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - skills/capability-first-backend/SKILL.md
  - specs/capability-first-backend-migration/README.md
  - specs/capability-first-backend-migration/sprints/01-capability-first-doctrine-and-routing-sprint.md
- skills:
  - capability-first-backend
- expected outputs:
  - pending-tasks notes and small doc/routing fixes if needed
- required checks:
  - search for broken links to capability-first docs/skills
  - verify no broad component-skill rewrite occurred in Sprint 1
  - verify no language says all backend operations must be exposed as agent tools
- done criteria:
  - Sprint 1 is ready for Sprint 2 description/decomposition integration
- notes:
  - completed: reviewed Sprint 1 doctrine/routing outputs; capability-first doc and skill references resolve, Sprint 1 changes stayed limited to doctrine/routing/planning files, and guidance explicitly says agent tools are optional exposure surfaces rather than the root abstraction
  - checks: `rg` review of capability-first references; `find` existence check for `docs/capability-first-backend-architecture.md` and `skills/capability-first-backend/SKILL.md`; `git diff --name-only 5f71098^..HEAD` confirmed no broad component-skill rewrite
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Review Sprint 1 capability-first consistency`

### TASK-02-001: Design app-description capability inventory

- status: done
- source: specs/capability-first-backend-migration/backlog/02-description-and-decomposition-integration-build-backlog.md
- task brief: none
- depends on: [TASK-01-005]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - docs/internal-app-description-architecture.md
  - docs/app-description-maintenance-flow.md
  - specs/capability-first-backend-migration/sprints/02-description-and-decomposition-integration-sprint.md
- skills:
  - app-descriptions
  - capability-first-backend
- expected outputs:
  - app-description docs/skills updated or task briefs created for capability inventory
- required checks:
  - capability inventory includes auth/scope/side effects/audit/approval/exposure surfaces
- done criteria:
  - description-first path has a clear place for capability models
- notes:
  - completed: defined the app-description capability inventory as governed backend capability contracts in `10-capabilities/`, including auth/scope, schemas, side effects, idempotency, approval, audit/trace, exposure surfaces, tests, and cross-layer links
  - updated readiness guidance so missing capability contract details block generation readiness when they would otherwise be invented downstream
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Design app-description capability inventory`

### TASK-02-002: Update app-description skills for capability changes

- status: done
- source: specs/capability-first-backend-migration/backlog/02-description-and-decomposition-integration-build-backlog.md
- task brief: none
- depends on: [TASK-02-001]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - skills/app-descriptions/SKILL.md
  - specs/capability-first-backend-migration/sprints/02-description-and-decomposition-integration-sprint.md
- skills:
  - app-descriptions
  - capability-first-backend
- expected outputs:
  - focused app-description skill updates
- required checks:
  - capability changes route to behavior, auth/security, tests, UI, observability, and readiness when affected
- done criteria:
  - app-description maintenance can revise capabilities consistently
- notes:
  - completed: updated app-description normalization, routing, capability modeling, behavior, tests, auth/security, observability, UI, and change-impact skills so capability contract changes preserve linked behavior, auth/security, tests, UI, observability, readiness, and traceability impacts
  - checks: `rg` verified capability-modeling/change-impact/intake routing references to behavior, auth/security, tests, UI, observability, and readiness
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Update app-description skills for capability changes`

### TASK-02-003: Update Akka solution decomposition for capability-first planning

- status: done
- source: specs/capability-first-backend-migration/backlog/02-description-and-decomposition-integration-build-backlog.md
- task brief: none
- depends on: [TASK-01-005]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - skills/akka-solution-decomposition/SKILL.md
  - specs/capability-first-backend-migration/sprints/02-description-and-decomposition-integration-sprint.md
- skills:
  - akka-solution-decomposition
  - capability-first-backend
- expected outputs:
  - skills/akka-solution-decomposition/SKILL.md
- required checks:
  - decomposition derives capabilities before Akka component selection
  - output contract preserves capability semantics for implementation tasks
- done criteria:
  - direct Akka planning path is capability-first
- notes:
  - completed: updated `skills/akka-solution-decomposition/SKILL.md` so direct Akka planning now derives governed capabilities before component and exposure selection, adds capability-to-component mapping, and preserves capability ids, AuthContext/scope, schemas, side effects, idempotency, approval, audit/trace, exposure surfaces, and tests in the implementation handoff
  - checks: `rg` verified capability-before-component language and implementation handoff semantics
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Make Akka solution decomposition capability-first`

### TASK-02-004: Update PRD/spec/backlog intake for capability semantics

- status: done
- source: specs/capability-first-backend-migration/backlog/02-description-and-decomposition-integration-build-backlog.md
- task brief: none
- depends on: [TASK-01-005]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - specs/capability-first-backend-migration/sprints/02-description-and-decomposition-integration-sprint.md
- skills:
  - akka-prd-to-specs-backlog
  - capability-first-backend
- expected outputs:
  - skills/akka-prd-to-specs-backlog/SKILL.md
  - specs guidance updates if needed
- required checks:
  - generated tasks preserve capability authority, schemas, side effects, audit, approval, and exposure decisions
- done criteria:
  - PRD/spec/backlog path emits capability-aware implementation tasks
- notes:
  - completed: updated `skills/akka-prd-to-specs-backlog/SKILL.md` so PRD/spec/backlog planning derives governed capabilities before component selection and carries capability ids, authority/scope, schemas, side effects, idempotency, approval, audit/trace, exposure surfaces, and tests into solution plans, specs, backlogs, and pending tasks
  - checks: `rg` verified generated task guidance preserves capability authority, schemas, side effects, audit, approval, and exposure decisions; `test -f` verified the capability-first backend skill reference resolves
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Make PRD backlog planning capability-aware`

### TASK-02-005: Add or revise a small app-description capability example

- status: done
- source: specs/capability-first-backend-migration/backlog/02-description-and-decomposition-integration-build-backlog.md
- task brief: none
- depends on: [TASK-02-001, TASK-02-002]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - docs/examples/ai-first-saas-seed-app-description/README.md
  - specs/capability-first-backend-migration/sprints/02-description-and-decomposition-integration-sprint.md
- skills:
  - app-descriptions
  - capability-first-backend
- expected outputs:
  - smallest useful example app-description capability inventory or existing example revision
- required checks:
  - example remains reference material for the pack, not this repo's own business app
- done criteria:
  - future agents have one capability-description example to follow
- notes:
  - completed: revised the seed app-description capability index and expanded `01-secure-tenant-user-foundation.md` into a capability-first reference contract with AuthContext/scope, schemas, side effects, idempotency, approval, audit/trace, exposure surfaces, tests, and linked layers
  - checks: verified updated markdown file references resolve; `rg` verified required capability contract fields and explicit reference-material language
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Add app-description capability example`

### TASK-03-001: Update agent tool and component-tool skills for capability-first design

- status: done
- source: specs/capability-first-backend-migration/backlog/03-component-skill-reframing-build-backlog.md
- task brief: none
- depends on: [TASK-02-003]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - akka-context/sdk/agents/extending.html.md
  - skills/akka-agent-tools/SKILL.md
  - skills/akka-agent-component-tools/SKILL.md
- skills:
  - akka-agent-tools
  - akka-agent-component-tools
  - capability-first-backend
- expected outputs:
  - skills/akka-agent-tools/SKILL.md
  - skills/akka-agent-component-tools/SKILL.md
- required checks:
  - agent tool guidance treats tools as governed capability exposures
  - no prompt-only authorization language remains unqualified
- done criteria:
  - agent tool skills align with capability-first doctrine
- notes:
  - completed: updated local/external agent tool and component-tool skills to start from named capability contracts, treat tool exposure as a selected surface, require AuthContext/scope/permission checks, preserve idempotency/approval/audit semantics, and reject prompt/tool-description-only authorization
  - checks: `rg` verified capability, AuthContext, scope, approval, audit, idempotency, and prompt-authorization guidance in both updated skill files
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Make agent tool skills capability-first`

### TASK-03-002: Update entity skills for capability surfaces

- status: done
- source: specs/capability-first-backend-migration/backlog/03-component-skill-reframing-build-backlog.md
- task brief: none
- depends on: [TASK-02-003]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - skills/README.md
- skills:
  - capability-first-backend
- expected outputs:
  - Event Sourced Entity and Key Value Entity skills updated
- required checks:
  - entity command/query guidance references capability semantics, idempotency, auth/scope, audit, and tool exposure choices
- done criteria:
  - entity skills no longer read as raw CRUD-first guidance for broad product work
- notes:
  - completed: reframed Event Sourced Entity and Key Value Entity suite, application entity, domain modeling, edge/flow, and type-selection guidance around named capability contracts, AuthContext/scope, idempotency, audit/trace, and deliberate endpoint/workflow/tool exposure choices
  - checks: `rg` verified capability semantics, idempotency, AuthContext/scope, audit, and tool exposure guidance across updated entity skills; `git diff --check` passed
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Make entity skills capability-first`

### TASK-03-003: Update workflow skills for consequential capabilities

- status: done
- source: specs/capability-first-backend-migration/backlog/03-component-skill-reframing-build-backlog.md
- task brief: none
- depends on: [TASK-02-003]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - skills/README.md
- skills:
  - capability-first-backend
- expected outputs:
  - workflow skills updated
- required checks:
  - workflow guidance covers supervised, long-running, approval-gated capability execution
- done criteria:
  - workflows are positioned as consequential capability carriers
- notes:
  - completed: updated workflow suite guidance to frame workflows as governed capability carriers for long-running, consequential, supervised, approval-gated, compensating, auditable, and idempotent execution
  - checks: `rg` verified workflow skill guidance covers capability contracts, AuthContext/scope, approval, supervision, audit/trace, and idempotency; `git diff --check` passed
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Make workflow skills capability-first`

### TASK-03-004: Update view skills for curated read capabilities

- status: done
- source: specs/capability-first-backend-migration/backlog/03-component-skill-reframing-build-backlog.md
- task brief: none
- depends on: [TASK-02-003]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - skills/README.md
- skills:
  - capability-first-backend
- expected outputs:
  - view skills updated
- required checks:
  - view guidance treats views as curated read/evidence capabilities with scope filters
- done criteria:
  - views are positioned as safe evidence/read surfaces for agents and UIs
- notes:
  - completed: updated the view skill suite to frame Views as curated read/evidence capabilities with AuthContext, tenant/customer scope filters, redaction, audit/data-access traces, and safe UI/API/MCP/agent exposure guidance
  - checks: `rg` verified view guidance covers read/evidence capabilities, AuthContext, tenant/customer scope, redaction, audit, and agent-safe exposure; `git diff --check` passed
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Make view skills capability-first`

### TASK-03-005: Update endpoint and MCP skills for selective capability exposure

- status: done
- source: specs/capability-first-backend-migration/backlog/03-component-skill-reframing-build-backlog.md
- task brief: none
- depends on: [TASK-02-003]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - skills/README.md
- skills:
  - capability-first-backend
- expected outputs:
  - HTTP/gRPC/MCP endpoint skill updates
- required checks:
  - external exposure guidance preserves capability auth/scope/audit/approval rules
- done criteria:
  - endpoint/MCP guidance treats exposure as a selected surface of a capability
- notes:
  - completed: updated HTTP, gRPC, and MCP endpoint skill suites so routes, methods, tools, resources, and prompts are framed as selected exposure surfaces for named backend capabilities, preserving AuthContext, tenant/customer scope, role/scope/capability authorization, validation, idempotency, approval, audit/trace, and cross-surface consistency
  - checks: `rg` verified capability exposure guidance, auth/scope, audit, approval, HTTP denial, gRPC denial, and MCP prompt/tool-description authorization language across endpoint skills; `git diff --check` passed
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Make endpoint skills capability-first`

### TASK-03-006: Update consumer, timer, and testing skills for capability execution

- status: done
- source: specs/capability-first-backend-migration/backlog/03-component-skill-reframing-build-backlog.md
- task brief: none
- depends on: [TASK-02-003]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - skills/README.md
- skills:
  - capability-first-backend
- expected outputs:
  - consumer, timed action, and testing skill updates
- required checks:
  - async/scheduled behavior has capability authority, audit, idempotency, and denial/retry semantics
- done criteria:
  - non-interactive execution paths are capability-aware
- notes:
  - completed: updated consumer, topic ingestion/publication, timed action, timer scheduling, and related testing skills so non-interactive reactive and scheduled execution paths preserve capability authority, tenant/customer scope, audit/trace obligations, idempotency, and denial/retry semantics
  - checks: `rg` verified capability authority, audit, idempotency, tenant/customer scope, system principal, denial/no-op, and retry guidance across consumer/timer/testing skills; `git diff --check` passed
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Make async execution skills capability-first`

### TASK-04-001: Add read-only capability component-tool example

- status: done
- source: specs/capability-first-backend-migration/backlog/04-reference-examples-and-tests-build-backlog.md
- task brief: none
- depends on: [TASK-03-001]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - src/main/java/com/example/application/ShoppingCartEntity.java
  - src/main/java/com/example/application/CartInspectorAgent.java
  - src/test/java/com/example/application/CartInspectorAgentTest.java
- skills:
  - capability-first-backend
  - akka-agent-component-tools
  - akka-agent-testing
- expected outputs:
  - revised or new read-only capability example and test
- required checks:
  - example does not imply raw state leakage is always acceptable
  - test verifies deterministic tool invocation behavior
- done criteria:
  - canonical read-only component-tool capability example exists
- notes:
  - completed: revised the shopping cart component-tool example so the agent exposes a named read-only `cart.inspect-summary` capability via `ShoppingCartEntity#inspectCartSummary`, returns a curated `CartSummary` rather than raw entity state, and updates `CartInspectorAgent` plus the component-tool skill guidance to use the selected capability surface
  - checks: `mvn -q -Dtest=CartInspectorAgentTest test` verified deterministic component-tool invocation and curated summary output
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Add read-only component tool capability example`

### TASK-04-002: Add consequential proposal/approval capability example

- status: done
- source: specs/capability-first-backend-migration/backlog/04-reference-examples-and-tests-build-backlog.md
- task brief: none
- depends on: [TASK-03-003]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - specs/capability-first-backend-migration/sprints/04-reference-examples-and-tests-sprint.md
- skills:
  - capability-first-backend
  - akka-agent-tools
  - akka-workflows
  - akka-agent-testing
- expected outputs:
  - minimal proposal/approval capability example and test
- required checks:
  - side-effecting action requires approval unless policy explicitly grants autonomy
- done criteria:
  - canonical consequential capability pattern exists
- notes:
  - completed: added a minimal `refund.issue` consequential capability example with `RefundApprovalAgent`, proposal-only `RefundProposalTools`, `RefundApprovalWorkflow`, `RefundApprovalState`, and `RefundApprovalCapabilityTest`; side effects wait for approval unless an explicit bounded autonomous policy grant applies
  - checks: `mvn -q -Dtest=RefundApprovalCapabilityTest test`; `git diff --check`
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Add consequential approval capability example`

### TASK-04-003: Add workflow-backed capability example

- status: done
- source: specs/capability-first-backend-migration/backlog/04-reference-examples-and-tests-build-backlog.md
- task brief: none
- depends on: [TASK-03-003]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - specs/capability-first-backend-migration/sprints/04-reference-examples-and-tests-sprint.md
- skills:
  - capability-first-backend
  - akka-workflows
- expected outputs:
  - workflow-backed capability example and test
- required checks:
  - workflow includes trace/supervision semantics appropriate to the example
- done criteria:
  - workflow capability pattern exists
- notes:
  - completed: added `SupervisedExportWorkflow`, `SupervisedExportState`, and `SupervisedExportWorkflowIntegrationTest` as a workflow-backed `customer.data-export.prepare` capability example with tenant/customer scope, AuthContext reference, trace ids, audit trace, risk-based supervision pause, approval/denial, and idempotency-key handling
  - checks: `mvn -q -Dtest=SupervisedExportWorkflowIntegrationTest test`; `git diff --check`
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Add supervised workflow capability example`

### TASK-04-004: Add view-backed evidence capability example

- status: done
- source: specs/capability-first-backend-migration/backlog/04-reference-examples-and-tests-build-backlog.md
- task brief: none
- depends on: [TASK-03-004]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - specs/capability-first-backend-migration/sprints/04-reference-examples-and-tests-sprint.md
- skills:
  - capability-first-backend
  - akka-views
- expected outputs:
  - view-backed evidence capability example and test
- required checks:
  - view output is curated and scoped for agent/UI consumption
- done criteria:
  - read-model evidence capability pattern exists
- notes:
  - completed: added `SupervisedExportEvidenceView` as a workflow-backed `customer.data-export.evidence.list` read/evidence capability with tenant/customer scope, status and risk filters, and curated agent/UI-safe rows that omit raw AuthContext, idempotency keys, result URIs, and full audit traces
  - checks: `mvn -q -Dtest=SupervisedExportEvidenceViewIntegrationTest test`; `git diff --check`
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Add supervised export evidence view`

### TASK-04-005: Add MCP-exposed capability example or guidance update

- status: done
- source: specs/capability-first-backend-migration/backlog/04-reference-examples-and-tests-build-backlog.md
- task brief: none
- depends on: [TASK-03-005]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - skills/akka-agent-mcp-tools/SKILL.md
  - skills/akka-mcp-endpoints/SKILL.md
- skills:
  - capability-first-backend
  - akka-agent-mcp-tools
  - akka-mcp-endpoints
- expected outputs:
  - MCP capability example or focused guidance update
- required checks:
  - remote exposure is selective and scoped
- done criteria:
  - MCP is represented as a capability exposure boundary
- notes:
  - completed: updated remote MCP tool guidance and the shopping-cart MCP example so MCP is represented as a selective capability exposure boundary for the read-only `cart.summary.inspect` capability, with allowed-tool filtering, scoped/curated output, service ACL guidance, and no mutation authority.
  - checks: `mvn -q -Dtest=RemoteShoppingCartAgentTest test`; `git diff --check`; `rg` verified capability id, allowed tool filtering, remote boundary, service ACL, and curated/no-raw-state language.
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Represent MCP as capability exposure boundary`

### TASK-04-006: Add UI/API reuse capability example or guidance update

- status: done
- source: specs/capability-first-backend-migration/backlog/04-reference-examples-and-tests-build-backlog.md
- task brief: none
- depends on: [TASK-03-005]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - specs/capability-first-backend-migration/sprints/04-reference-examples-and-tests-sprint.md
- skills:
  - capability-first-backend
  - akka-http-endpoints
  - akka-web-ui
- expected outputs:
  - UI/API reuse example or focused guidance update
- required checks:
  - same capability semantics apply across browser action and agent/tool usage
- done criteria:
  - capability reuse across surfaces is demonstrated or documented
- notes:
  - completed: added a browser/API exposure for the read-only `cart.inspect-summary` capability at `GET /carts/{cartId}/summary`, reusing the same `ShoppingCartEntity#inspectCartSummary` backend capability used by the `CartInspectorAgent` component tool and returning a curated summary response.
  - checks: `mvn -q -Dtest=ShoppingCartIntegrationTest,CartInspectorAgentTest test`; `git diff --check`; test verifies the browser/API response preserves the same capability id and summary semantics as the component-tool capability.
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Add UI API capability reuse example`

### TASK-05-001: Review doctrine and routing for stale/conflicting content

- status: done
- source: specs/capability-first-backend-migration/backlog/05-review-and-stale-content-cleanup-build-backlog.md
- task brief: none
- depends on: [TASK-04-006]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - specs/capability-first-backend-migration/sprints/05-review-and-stale-content-cleanup-sprint.md
- skills:
  - capability-first-backend
- expected outputs:
  - review report and safe cleanup edits
- required checks:
  - no broad input path bypasses capability-first modeling without explicit reason
- done criteria:
  - doctrine/routing stale content findings are resolved or queued
- notes:
  - completed: reviewed top-level doctrine/routing and added `specs/capability-first-backend-migration/doctrine-routing-review.md`; refined `AGENTS.md` so high-level product/routing guidance explicitly routes through governed backend capability modeling before description/decomposition/component implementation
  - checks: `rg` review of doctrine/routing broad-input, CRUD, Stage 3, and agent-tool language; `git diff --check`
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Review capability-first doctrine routing`

### TASK-05-002: Review app-description and decomposition paths for stale content

- status: done
- source: specs/capability-first-backend-migration/backlog/05-review-and-stale-content-cleanup-build-backlog.md
- task brief: none
- depends on: [TASK-05-001]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - specs/capability-first-backend-migration/sprints/05-review-and-stale-content-cleanup-sprint.md
- skills:
  - app-descriptions
  - akka-solution-decomposition
  - capability-first-backend
- expected outputs:
  - review report section and safe cleanup edits
- required checks:
  - description/decomposition paths preserve capability semantics
- done criteria:
  - stale description/decomposition content is resolved or queued
- notes:
  - completed: reviewed description-first and direct decomposition paths; added `specs/capability-first-backend-migration/description-decomposition-review.md`; refined stale CRUD-oriented intake wording; expanded the older app-description skill planning contract; updated purchase-request app-description and solution-plan mechanics examples to preserve capability ids/classes, AuthContext/scope, schemas, side effects, idempotency, approval, audit/trace, exposure surfaces, tests, and capability-to-component mapping
  - checks: `rg` review of app-description/decomposition docs and skills for CRUD/component/tool-first language; `git diff --check`
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Review description decomposition capability paths`

### TASK-05-003: Review component skills for stale CRUD/entity/tool-first language

- status: done
- source: specs/capability-first-backend-migration/backlog/05-review-and-stale-content-cleanup-build-backlog.md
- task brief: none
- depends on: [TASK-05-001]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - skills/README.md
  - specs/capability-first-backend-migration/sprints/05-review-and-stale-content-cleanup-sprint.md
- skills:
  - capability-first-backend
- expected outputs:
  - review report section and safe cleanup edits
- required checks:
  - component skills do not route broad product input directly to CRUD/component implementation
- done criteria:
  - stale component-skill language is resolved or queued
- notes:
  - completed: added `specs/capability-first-backend-migration/component-skills-review.md`; qualified top-level Stage 3 agent, workflow, view, consumer, timer, HTTP, gRPC, MCP, and web UI skill openings so broad product/PRD/feature input routes through capability-first decomposition before component implementation; fixed one malformed workflow example-test link
  - checks: searched component skills for stale CRUD/entity/endpoint/tool-first and broad-product routing language; verified updated openings require selected capability/component or exposure-surface context; `git diff --check` passed
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Review component skills for capability routing`

### TASK-05-004: Review examples and tests for stale unsafe tool patterns

- status: done
- source: specs/capability-first-backend-migration/backlog/05-review-and-stale-content-cleanup-build-backlog.md
- task brief: none
- depends on: [TASK-05-001]
- required reads:
  - AGENTS.md
  - docs/capability-first-backend-architecture.md
  - docs/agent-coverage-matrix.md
  - specs/capability-first-backend-migration/sprints/05-review-and-stale-content-cleanup-sprint.md
- skills:
  - capability-first-backend
  - akka-agent-testing
- expected outputs:
  - review report section and safe cleanup edits
- required checks:
  - examples do not normalize auth bypass, raw state leakage, or unbounded tool authority
- done criteria:
  - stale example/test patterns are resolved or queued
- notes:
  - completed: added `specs/capability-first-backend-migration/example-test-safety-review.md`; tightened older MCP/support examples so they use selected read-only capabilities, service-scoped MCP ACLs, curated/redacted evidence, explicit AuthContext/tenant-audit caveats, and no raw header/token exposure; confirmed current agent-tool tests use deterministic `TestModelProvider` patterns and bounded/proposal-only authority for consequential refund examples
  - checks: reviewed agent/MCP/tool examples and tests with `rg`; `mvn -q -Dtest=SecureSupportMcpEndpointTest,ShoppingCartMcpEndpointTest test`; targeted search for remaining MCP `ALL` ACL, raw-state, raw bearer/header, and generic cart-state language
  - commit hash: not embedded because this queue update is included in the same task commit; see commit `Review examples for safe tool authority`

### TASK-05-005: Remove, archive, or supersede duplicate capability migration content

- status: pending
- source: specs/capability-first-backend-migration/backlog/05-review-and-stale-content-cleanup-build-backlog.md
- task brief: none
- depends on: [TASK-05-001, TASK-05-002, TASK-05-003, TASK-05-004]
- required reads:
  - AGENTS.md
  - specs/capability-first-backend-migration/sprints/05-review-and-stale-content-cleanup-sprint.md
- skills:
  - capability-first-backend
- expected outputs:
  - focused deletion/archive/supersession edits if safe
- required checks:
  - no referenced file is deleted without updating references
- done criteria:
  - duplicate or superseded migration content is intentionally handled
- notes:
  - pending

### TASK-06-001: Whole-pack capability-first consistency review

- status: pending
- source: specs/capability-first-backend-migration/backlog/06-final-consistency-review-build-backlog.md
- task brief: none
- depends on: [TASK-05-005]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - specs/capability-first-backend-migration/sprints/06-final-consistency-review-sprint.md
- skills:
  - capability-first-backend
- expected outputs:
  - final consistency review report and small fixes
- required checks:
  - one coherent doctrine from natural language input through implementation routing
- done criteria:
  - whole-pack consistency issues are resolved or explicitly queued
- notes:
  - pending

### TASK-06-002: Security and governance consistency review

- status: pending
- source: specs/capability-first-backend-migration/backlog/06-final-consistency-review-build-backlog.md
- task brief: none
- depends on: [TASK-06-001]
- required reads:
  - AGENTS.md
  - docs/ai-first-saas-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - specs/capability-first-backend-migration/sprints/06-final-consistency-review-sprint.md
- skills:
  - capability-first-backend
  - core-saas-foundation
- expected outputs:
  - security/governance review report and small fixes
- required checks:
  - capability-first changes preserve mandatory auth, tenant scope, permissions, audit, approval, and tests
- done criteria:
  - security/governance alignment is verified
- notes:
  - pending

### TASK-06-003: Example and test coverage review

- status: pending
- source: specs/capability-first-backend-migration/backlog/06-final-consistency-review-build-backlog.md
- task brief: none
- depends on: [TASK-06-001]
- required reads:
  - AGENTS.md
  - docs/agent-coverage-matrix.md
  - docs/capability-first-backend-architecture.md
  - specs/capability-first-backend-migration/sprints/06-final-consistency-review-sprint.md
- skills:
  - capability-first-backend
  - akka-agent-testing
- expected outputs:
  - coverage matrix update or residual example/test backlog
- required checks:
  - capability-first examples cover key exposure surfaces or gaps are explicit
- done criteria:
  - example/test coverage state is clear
- notes:
  - pending

### TASK-06-004: Write migration completion summary

- status: pending
- source: specs/capability-first-backend-migration/backlog/06-final-consistency-review-build-backlog.md
- task brief: none
- depends on: [TASK-06-001, TASK-06-002, TASK-06-003]
- required reads:
  - AGENTS.md
  - specs/capability-first-backend-migration/README.md
  - specs/capability-first-backend-migration/pending-tasks.md
  - specs/capability-first-backend-migration/sprints/06-final-consistency-review-sprint.md
- skills:
  - capability-first-backend
- expected outputs:
  - final migration summary under specs/capability-first-backend-migration/
  - residual backlog if needed
- required checks:
  - all remaining pending tasks are intentional residuals or completed
- done criteria:
  - migration status is explicit and ready for normal repository evolution
- notes:
  - pending

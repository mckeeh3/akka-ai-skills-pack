# Module-oriented sprint planning

Use this pattern when a large PRD, revised PRD, app-description realization, or broad requirements set is too large for a single solution plan plus flat slice backlog.

Goal: decompose work into **vertical module sprints** that can be implemented and tested full stack, one module at a time.

For generated full-stack AI-first SaaS, `vertical` means each sprint, backlog item, task brief, and pending task is anchored to functional agent ownership, the workstream attention category and dashboard contract, a structured surface/action or workstream event, a governed capability id/class, AuthContext and role/capability rules, selected Akka substrate, frontend/API/realtime work, notifications/projections, audit/work traces, and required tests. When durable internal/background model-driven work is part of the increment, preserve the AutonomousAgent task lifecycle, result surface, notification, dependency/failure/cancellation, and tool-authority contract. When a functional agent has LLM behavior, the vertical contract must also preserve its workstream expert bundle requirements: prompt intent, procedural skills, reference documents, compact manifests, tool boundaries, authorized skill/reference loaders, traces, governance surfaces, seed/import behavior, and tests. Do not treat component family, page, dashboard, generic module names, or vague `agent expertise` labels as sufficient implementation boundaries.

Before splitting modules, classify the generated product as full-stack secure AI-first SaaS by default: delegated operational work, agents, approvals/exceptions, policy-controlled automation, supervision UI, audit traces, and outcome accountability should be represented as operating-model scope before CRUD/module decomposition.

For SaaS app planning, the first sprint or slice is always the full-stack core secure SaaS foundation unless the task is explicitly non-SaaS reference material. Create `specs/cross-cutting/01-auth-tenancy-audit.md`, then make the first foundation sprint or foundation slice cover Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, WorkOS/JWT seam, `/api/me`, central authorization, full invitation lifecycle, email delivery/outbox, InvitationWorkflow, expiry/reminder timers, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, governed runtime agent foundation (`AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `ReferenceDocument`/`ReferenceVersion`, `AgentSkillManifest`, `AgentReferenceManifest`, deterministic prompt assembly, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, `ToolPermissionBoundary`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`), behavior editing agent proposal/review flow, agent catalog/detail, prompt/skill/reference/manifest/tool-boundary UI, AI admin responsibilities, decision cards for risky admin actions, mandatory admin UI surfaces, audit, frontend shell/context selection, and security/admin/frontend/agent-governance tests before app-specific CRM/domain features.

## When to use

Use module-oriented sprint planning when the input includes any of:

- multiple business modules or bounded capability areas
- backend and browser UI work for the same capability
- enough scope that a flat `specs/backlog/` queue would become hard to navigate
- a need to complete and test one feature area end-to-end before moving to the next

Do not use it for tiny changes where one slice spec and one backlog are enough.

## Repository shape

Preferred shape for large PRD outputs:

```text
specs/
  README.md
  akka-solution-plan.md
  pending-questions.md
  pending-tasks.md
  cross-cutting/
    00-common-domain-and-conventions.md
    01-auth-tenancy-audit.md
    02-ui-style-guide.md
  modules/
    01-identity-access.md
    02-purchase-requests.md
    03-approvals.md
  sprints/
    01-identity-access-sprint.md
    02-purchase-request-core-sprint.md
    03-approval-flow-sprint.md
  backlog/
    README.md
    01-identity-access-build-backlog.md
    02-purchase-request-core-build-backlog.md
    03-approval-flow-build-backlog.md
  tasks/
    README.md
    02-purchase-request-core/
      01-request-entity.md
```

`specs/modules/` captures durable module boundaries.  
`specs/sprints/` captures the ordered vertical delivery plan.  
`specs/backlog/` remains the implementation-ready task source used to materialize `specs/pending-tasks.md`.

For smaller projects, `specs/slices/` is still acceptable. For larger projects, prefer `modules/` + `sprints/`; do not create both `slices/` and `sprints/` unless existing project history already uses slices and you are preserving compatibility.

## Module spec contract

Each `specs/modules/NN-<module>.md` should contain:

- module boundary and purpose
- capabilities owned by the module
- actors and authorization boundaries
- domain objects and state ownership
- backend components likely owned by the module
- functional agents, structured surfaces, surface actions, and route/deep-link details owned by the module
- integrations and events entering/leaving the module
- cross-cutting specs referenced by the module
- AI-first scope for generated SaaS: delegated work, retained human authority, durable goals/plans, agents, policies, decisions, traces, UI surfaces, and outcomes owned or used by the module
- out-of-scope adjacent modules
- related sprints/backlogs

A module spec should be relatively stable across several sprints. It is not the task list.

## Sprint spec contract

Each `specs/sprints/NN-<sprint>.md` should be a full-stack delivery contract:

- sprint goal
- parent module or modules
- dependencies and prerequisite decisions
- vertical workstream increment: functional agent(s), attention categories, dashboard summaries, workstream event or structured surface/action, mapped capability id(s)/class(es), AuthContext and role/capability rules, selected Akka substrate, AutonomousAgent task semantics where applicable, notification/projection behavior, frontend/API/realtime work, audit/work traces, and required tests
- backend scope: entities, workflows, views, consumers, timers, endpoints as implementation of those capabilities
- frontend scope: functional-agent workstream shell changes, structured surfaces, forms/surface actions, route/deep-link details, API client calls, realtime behavior
- AI-first increment for generated SaaS: goals/plans, agent/team responsibilities, authority limits, approval gates, policy clauses, evidence/risk/confidence/impact surfaces, trace records, evaluations, and outcome metrics
- workstream expertise increment for each new or changed functional agent: expert bundle id, prompt/skill/reference document families, `AgentSkillManifest`/`AgentReferenceManifest`, `ToolPermissionBoundary`, `readSkill`/`readReferenceDoc`, `SkillLoadTrace`/`ReferenceLoadTrace`, seed/import behavior, governance UI, and tests
- acceptance behavior: happy paths, validation, no-op/idempotent cases, error cases
- pending questions that block or affect the sprint
- implementation task groups
- module-level full-stack test plan
- done criteria, including local-run/manual-test expectations for the named sprint goal
- explicit defer list, with impact on whether the sprint goal remains fully working

A sprint should end with a named, working app state. For generated full-stack Akka apps, the sprint goal is not complete until the user-visible capability named by the sprint can be exercised through the locally running Akka app, including the relevant backend, API, frontend/workstream surface, authorization boundary, audit/trace behavior, and required tests. If a planned deferral prevents that capability from working end to end, narrow or rename the sprint goal, mark the affected work blocked/incomplete, or split the sprint; do not call the original feature implemented.

## Backlog alignment

Each sprint should have one matching backlog file:

```text
specs/sprints/02-purchase-request-core-sprint.md
specs/backlog/02-purchase-request-core-build-backlog.md
```

The backlog should break the sprint into harness-sized vertical tasks. Each implementation task should carry:

- functional agent(s), or explicit internal-only/foundation scope;
- attention category/dashboard, structured surface/action or workstream event, or explicit non-UI trigger;
- capability id(s)/class(es);
- AuthContext, tenant/customer scope, and role/capability rules;
- side effects, idempotency, approval, notification/projection behavior, audit, and trace obligations;
- AutonomousAgent task lifecycle/result/notification semantics when durable internal/background work is in scope;
- selected Akka substrate and endpoint/tool/workflow/timer/consumer exposure;
- frontend/API/realtime work where user-facing;
- required success, validation, forbidden, tenant-isolation, idempotency, audit/trace, rendering/API/realtime, and smoke tests;
- the local app-run or manual verification path that proves the task contributes to the sprint's working state, or an explicit statement that the task is non-runtime/internal-only.

Prefer this order when applicable:

1. secure foundation records and contracts: Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, AuthContext, central authorization, audit metadata, and tenant/customer scope types
2. WorkOS/JWT seam, `/api/me`, complete invitation lifecycle, email delivery/outbox, InvitationWorkflow, expiry/reminder timers, InvitationView, UserDirectoryView, MembershipView, AdminAuditView, AccessReviewQueueView, membership/role management, admin audit/search, managed-agent foundation tasks for `AgentDefinition`, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, `AgentSkillManifest`, `AgentReferenceManifest`, `readSkill`, `readReferenceDoc`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, behavior editing agent proposals, agent catalog/detail and governance UI, AI admin responsibilities such as AdminRiskAgent and AccessReviewAgent or a skilled UserAdminAgent, decision cards for risky admin actions, admin UI surfaces, and security/admin/agent-governance tests
3. shared domain/API records for the app-specific module
4. write model entity or workflow
5. views/read models
6. consumers or timed actions
7. HTTP/gRPC/MCP endpoints
8. frontend API client, context-selection shell, and state
9. functional-agent workstream surfaces/forms/realtime behavior
10. component tests
11. endpoint, frontend, forbidden-access, and tenant-isolation tests
12. full-stack module smoke or integration test

## Pending questions

Use `specs/pending-questions.md` for decisions that would otherwise force the harness to guess.

Rules:

- associate each question with the exact module/sprint/backlog work it blocks
- block only affected tasks, not the whole project
- allow unblocked foundation or module work to continue
- let unknown security-provider details block provider-specific WorkOS/JWT integration tasks, but not the foundation slice's local authorization contracts, Tenant/Customer boundaries, `/api/me` contract, audit model, or tenant-isolation test design
- if no style guide is selected for generated AI-first SaaS, create a UI style-selection question and block web UI implementation tasks, including agent catalog/detail surfaces, prompt governance surfaces, skill governance surfaces, manifest management, tool-boundary, behavior editing proposal, and trace surfaces, until answered
- for AI-first scope, create scoped questions when implementation would otherwise guess delegated authority, approval gates, policy/risk thresholds, evidence requirements, trace visibility, evaluation strategy, or outcome metrics

## Pending tasks

`specs/pending-tasks.md` should group or annotate tasks by sprint/module while preserving stable task IDs. A pending task is not implementation-ready if it names only a component, page, dashboard, module, generic UI feature, or vague `make the agent expert` work without the vertical workstream/surface/capability/expertise contract above; block it or create a task brief before queueing it as runnable.

For every new or materially changed functional agent with LLM behavior, create bounded workstream expertise tasks that a fresh harness session can execute independently. Split work into expert bundle/app-description ownership, seeded prompt content, procedural `SkillDocument` content, factual/process `ReferenceDocument` content, compact skill/reference manifests, tool-boundary and loader grants, runtime authorized/denied load behavior, expertise manifest/governance UI surfaces, and assigned-load/denied-load/boundary/trace/no-authority-expansion tests whenever those concerns would otherwise make one task too broad.

Acceptable ID patterns:

```text
TASK-001
TASK-002
```

or for large plans:

```text
TASK-02-001
TASK-02-002
TASK-03-001
```

Each task should include required reads for the smallest useful set:

- `specs/akka-solution-plan.md`
- the relevant module spec when module boundaries matter
- the relevant sprint spec
- the matching backlog
- related cross-cutting specs
- AI-first doctrine or companion skills only when the task implements or verifies delegated work, agents, policies, decisions, traces, UI surfaces, governance, or outcomes
- a task brief when one exists

## Done criteria for a module sprint

A module sprint is done only when the named sprint goal is fully working at the stated scope. "Working" means a developer can run the Akka app locally, exercise the visible feature or operational surface named by the sprint, and observe the expected authorized success, denial/error, audit/trace, and UI/API behavior. Akka local execution is treated as production-like validation for the increment.

A module sprint is done only when:

- all tasks required for the named sprint goal are `done`; tasks may be `deferred` only when they do not prevent the named goal from working, or when the sprint goal is explicitly narrowed/renamed
- backend component tests pass
- endpoint tests pass for the sprint API surface
- frontend checks/tests pass for generated full-stack AI-first SaaS
- at least one module-level integration or smoke path verifies the full stack for the sprint
- the app has been run locally, or the sprint completion summary records the exact reason local execution was not possible and whether that blocks completion
- a manual test checklist exists for the sprint's visible feature(s), including sign-in/context, happy path, forbidden/disabled/tenant-scope behavior where relevant, and the expected audit/trace evidence
- deferred items and blocked questions are visible in the sprint spec, backlog, or queues and state whether they narrow the sprint goal or block completion
- AI-first authority, policy, trace, evaluation, and outcome constraints required for the named goal are implemented; constraints may be deferred only when the sprint scope explicitly excludes the affected behavior and the output is not described as fully implemented

## Anti-patterns

Avoid:

- layer-only sprints such as “all entities” or “all UI” for large PRDs
- pending tasks such as “build dashboard”, “add admin module”, “create CRUD pages”, “implement workflow”, or “make the agent expert” without functional-agent ownership, surface/action or workstream event, capability id/class, AuthContext/rules, selected Akka substrate, frontend/API/realtime scope, workstream expertise scope when relevant, and tests
- module specs that become giant PRD copies
- sprint specs that omit frontend work when the user-facing capability requires it
- pending tasks that jump across unrelated modules in one harness run
- blocking all work because one later module has an unresolved question
- duplicating cross-cutting security/style/audit rules in every sprint

## Related skills and examples

- `examples/requirements-to-workstream-mini-example.md` — preferred generated-SaaS planning shape
- `examples/purchase-request-module-sprint-plan.md` — conventional module/sprint mechanics only, not generated-SaaS target architecture
- `../skills/akka-prd-to-specs-backlog/SKILL.md`
- `../skills/akka-slice-spec-to-backlog/SKILL.md`
- `../skills/akka-backlog-to-pending-tasks/SKILL.md`
- `pending-task-queue.md`
- `pending-question-queue.md`

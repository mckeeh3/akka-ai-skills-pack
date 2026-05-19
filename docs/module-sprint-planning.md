# Module-oriented sprint planning

Use this pattern when a large PRD, revised PRD, app-description realization, or broad requirements set is too large for a single solution plan plus flat slice backlog.

Goal: decompose work into **vertical module sprints** that can be implemented and tested full stack, one module at a time.

Before splitting modules, classify the generated product as full-stack secure AI-first SaaS by default: delegated operational work, agents, approvals/exceptions, policy-controlled automation, supervision UI, audit traces, and outcome accountability should be represented as operating-model scope before CRUD/module decomposition.

For SaaS app planning, the first sprint or slice is always the full-stack core secure SaaS foundation unless the task is explicitly non-SaaS reference material. Create `specs/cross-cutting/01-auth-tenancy-audit.md`, then make the first foundation sprint or foundation slice cover Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, WorkOS/JWT seam, `/api/me`, central authorization, full invitation lifecycle, email delivery/outbox, InvitationWorkflow, expiry/reminder timers, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, governed runtime agent foundation (`AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, deterministic prompt assembly, authorized `readSkill(skillId)`, `ToolPermissionBoundary`, `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`), behavior editing agent proposal/review flow, agent catalog/detail, prompt/skill/manifest/tool-boundary UI, AI admin responsibilities, decision cards for risky admin actions, mandatory admin UI surfaces, audit, frontend shell/context selection, and security/admin/frontend/agent-governance tests before app-specific CRM/domain features.

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
- backend scope: entities, workflows, views, consumers, timers, endpoints
- frontend scope: functional-agent workstream shell changes, structured surfaces, forms/surface actions, route/deep-link details, API client calls, realtime behavior
- AI-first increment for generated SaaS: goals/plans, agent/team responsibilities, authority limits, approval gates, policy clauses, evidence/risk/confidence/impact surfaces, trace records, evaluations, and outcome metrics
- acceptance behavior: happy paths, validation, no-op/idempotent cases, error cases
- pending questions that block or affect the sprint
- implementation task groups
- module-level full-stack test plan
- done criteria
- explicit defer list

A sprint should end with something demonstrable and testable through the relevant backend and frontend surface.

## Backlog alignment

Each sprint should have one matching backlog file:

```text
specs/sprints/02-purchase-request-core-sprint.md
specs/backlog/02-purchase-request-core-build-backlog.md
```

The backlog should break the sprint into harness-sized tasks. Prefer this order when applicable:

1. secure foundation records and contracts: Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, AuthContext, central authorization, audit metadata, and tenant/customer scope types
2. WorkOS/JWT seam, `/api/me`, complete invitation lifecycle, email delivery/outbox, InvitationWorkflow, expiry/reminder timers, InvitationView, UserDirectoryView, MembershipView, AdminAuditView, AccessReviewQueueView, membership/role management, admin audit/search, managed-agent foundation tasks for `AgentDefinition`, `PromptDocument`, `SkillDocument`, `AgentSkillManifest`, `readSkill`, `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`, behavior editing agent proposals, agent catalog/detail and governance UI, AI admin responsibilities such as AdminRiskAgent and AccessReviewAgent or a skilled UserAdminAgent, decision cards for risky admin actions, admin UI surfaces, and security/admin/agent-governance tests
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

`specs/pending-tasks.md` should group or annotate tasks by sprint/module while preserving stable task IDs.

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

A module sprint is done only when:

- all runnable tasks for the sprint are done or explicitly deferred
- backend component tests pass
- endpoint tests pass for the sprint API surface
- frontend checks/tests pass for generated full-stack AI-first SaaS
- at least one module-level integration or smoke path verifies the full stack for the sprint
- deferred items and blocked questions are visible in the sprint spec, backlog, or queues
- AI-first authority, policy, trace, evaluation, and outcome constraints are either implemented, explicitly deferred, or blocked by named questions

## Anti-patterns

Avoid:

- layer-only sprints such as “all entities” or “all UI” for large PRDs
- module specs that become giant PRD copies
- sprint specs that omit frontend work when the user-facing capability requires it
- pending tasks that jump across unrelated modules in one harness run
- blocking all work because one later module has an unresolved question
- duplicating cross-cutting security/style/audit rules in every sprint

## Related skills and examples

- `examples/purchase-request-module-sprint-plan.md`
- `../skills/akka-prd-to-specs-backlog/SKILL.md`
- `../skills/akka-slice-spec-to-backlog/SKILL.md`
- `../skills/akka-backlog-to-pending-tasks/SKILL.md`
- `pending-task-queue.md`
- `pending-question-queue.md`

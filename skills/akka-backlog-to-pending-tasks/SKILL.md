---
name: akka-backlog-to-pending-tasks
description: Create or repair specs/pending-tasks.md from existing specs/backlog/*-build-backlog.md files when backlogs already exist but the durable pending-task queue is missing, stale, or incomplete.
---

# Akka Backlog to Pending Tasks

Use this skill when a project already has planning artifacts under `specs/`, especially `specs/backlog/*.md`, but does not yet have a usable durable queue at:

```text
specs/pending-tasks.md
```

This is a queue repair/materialization skill. It does not redo PRD decomposition and it does not implement application code.

## Goal

Create or repair `specs/pending-tasks.md` so future harness runs can execute one focused task at a time with `akka-do-next-pending-task`.

The skill must:
- read existing solution, slice, backlog, and task-brief artifacts
- preserve AI-first SaaS operating-model, governance, audit, UI-surface, and outcome context from those artifacts when applicable
- derive queue tasks from backlog `Suggested harness task breakdown` sections
- preserve existing queue task IDs and statuses when a queue already exists
- create stable task IDs for missing queue entries
- add dependencies based on backlog order, explicit prerequisite notes, and component dependencies
- include required reads, skills, expected outputs, required checks, done criteria, and notes
- avoid implementation code changes

## Use this skill when

Use this skill when the user says things like:
- "create pending-tasks.md from the existing backlog"
- "repair the pending task queue"
- "the backlogs exist but there is no specs/pending-tasks.md"
- "regenerate the pending task queue"
- "sync pending-tasks.md with specs/backlog"

Do **not** use this skill when:
- the user is still at PRD level and no slice/backlog planning exists; use `akka-prd-to-specs-backlog`
- the user wants a single backlog item narrowed into a task brief; use `akka-backlog-item-to-task-brief`
- the user wants to execute a pending task; use `akka-do-next-pending-task`
- the user asks for application code directly; use the focused implementation skills

## Required reading

Read these first if present:
- `../README.md`
- `../core-saas-foundation/SKILL.md` for the mandatory secure SaaS baseline and first-slice implementation order
- `../../docs/ai-first-saas-application-architecture.md` when backlog work involves delegated operations, agents, governance, decisions, supervision, audit, or outcomes
- `../../docs/pending-question-queue.md`
- `../../docs/pending-task-queue.md`
- `../../docs/solution-plan-to-implementation-queue.md`
- `../../docs/module-sprint-planning.md` when `specs/modules/` or `specs/sprints/` exists
- `../../docs/web-ui-style-guide.md` when materializing browser UI tasks
- `../../specs/README.md`
- `../../specs/akka-solution-plan.md`
- `../../specs/pending-questions.md` if it exists
- `../../specs/pending-tasks.md` if it already exists
- all relevant `../../specs/backlog/*-build-backlog.md` files
- `../../specs/tasks/README.md` if present
- relevant `../../specs/tasks/**/*.md` task briefs if present
- relevant `../../specs/modules/*.md` and `../../specs/sprints/*.md` files when present
- relevant `../../specs/slices/*.md` files when needed to resolve dependencies or reads

Do not reread the original PRD unless the existing backlogs are too ambiguous to create queue tasks. Prefer carrying forward the AI-first interpretation already captured in solution, sprint, slice, backlog, task brief, app-description, and pending-question artifacts.

## Output

Create or update:

```text
specs/pending-tasks.md
```

Use the contract in `../../docs/pending-task-queue.md`.

## Queue derivation rules

### Source of tasks

Derive queue tasks from each backlog file's `Suggested harness task breakdown` section.

Security baseline tasks must never be omitted as cross-cutting polish. For SaaS app queues, ensure the first runnable tasks implement or verify the secure foundation before CRM/domain-specific features: Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, WorkOS/JWT seam, `/api/me`, central authorization, full invitation lifecycle, email delivery/outbox, InvitationWorkflow, expiry/reminder timers, InvitationView, UserDirectoryView, MembershipView, AdminAuditView, AccessReviewQueueView, membership/role management, admin audit/search, AI admin agents including AdminRiskAgent and AccessReviewAgent, decision cards for risky admin actions, admin UI surfaces, frontend shell/context selection when applicable, and security/admin tests. If the source backlogs lack those tasks, repair the queue only after adding or flagging the missing foundation backlog coverage instead of silently proceeding to domain work.

Do not create one queue task per class name unless the backlog explicitly frames each class as a separate harness-sized task.

### AI-first context preservation

When source artifacts classify work as AI-first or include delegated operations, agents, policy governance, approvals, exceptions, audit traces, supervision UI, or outcome loops:
- include `docs/ai-first-saas-application-architecture.md` in required reads unless a more focused AI-first task brief already contains the needed context
- include the smallest relevant AI-first companion skill alongside component skills, such as `ai-first-saas-agent-team-design`, `ai-first-saas-policy-governance`, `ai-first-saas-decision-cards`, `ai-first-saas-audit-trace`, `ai-first-saas-ui-surfaces`, or `ai-first-saas-outcomes-metrics`
- preserve authority, approval, policy, evidence, trace, and outcome constraints in task notes or done criteria when those constraints affect implementation
- keep implementation tasks bounded to the Akka substrate component being built; do not create broad doctrine-reading tasks unless the backlog explicitly asks for planning or docs

### Question gate

If `specs/pending-questions.md` exists, inspect unresolved `blocking` questions before materializing tasks.

Rules:
- do not create runnable tasks for work blocked by unresolved `blocking` questions
- treat unknown security-provider setup details as blockers only for provider-specific integration tasks; keep local authorization contracts, tenancy models, AuthContext, `/api/me`, audit, and tenant-isolation tasks runnable when their semantics are otherwise defined
- if a backlog item is entirely blocked, create a `blocked` task only when useful for visibility and note the blocking question IDs
- if only part of a backlog is blocked, create tasks for unblocked work and leave blocked work out or blocked with explicit question references
- if a question is `answered` but not `resolved`, reconcile it or leave affected tasks blocked
- do not silently choose defaults unless the question is `deferred` with an accepted default or limitation
- if browser UI tasks exist and no selected style guide or pending style question exists, add/update `specs/pending-questions.md` with the style-selection question from `../../docs/web-ui-style-guide.md` and block only the affected UI tasks

### Task sizing

Each queue task should be executable in one fresh harness context.

Good queue tasks usually map to:
- one shared domain package
- one entity or workflow plus direct tests
- one view family plus tests
- one consumer or timed action plus tests
- one endpoint family plus tests
- one focused task brief under `specs/tasks/`

If a backlog task item is too broad:
- prefer creating multiple smaller queue tasks when the split is obvious from the backlog
- split any broad `auth/admin`, `user administration`, or `foundation` item that spans invitation lifecycle plus admin AI plus UI into separate queue tasks for invitation lifecycle, email delivery/outbox, user directory/search views, membership/role management, admin audit/search, access review queues, AI admin agents, decision cards for risky admin actions, admin UI surfaces, and security/admin tests
- otherwise create one `blocked` queue task with a note that `akka-backlog-item-to-task-brief` should split it first

### Existing queue preservation

If `specs/pending-tasks.md` already exists:
- preserve task IDs
- preserve statuses unless the user asks to reset them
- preserve useful notes
- do not delete completed tasks
- mark obsolete non-done tasks as `superseded` when the matching backlog work was replaced
- append missing tasks or update stale task metadata carefully
- do not renumber tasks just to improve aesthetics

When a backlog item appears to match an existing queue task, update that existing entry rather than creating a duplicate.

### Dependency rules

Set dependencies conservatively:
- secure foundation tasks for Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, WorkOS/JWT seam, `/api/me`, central authorization, invitation lifecycle, email delivery/outbox, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin agents, decision cards for risky admin actions, admin UI surfaces, audit, frontend shell/context selection when applicable, and security/admin tests come before app-specific CRM/domain tasks
- foundational domain/config tasks usually have no dependencies
- entity/workflow tasks depend on required domain tasks
- views depend on their source component tasks
- endpoint tasks depend on components they call
- end-to-end tests depend on the components under test
- later sprint or slice tasks depend only on earlier tasks that are genuinely required

Avoid over-serializing independent work.

### Required reads

Each task should list the smallest useful reads, usually:
- `specs/akka-solution-plan.md`
- the source backlog file
- the matching task brief when one exists
- relevant cross-cutting spec files, including `specs/cross-cutting/01-auth-tenancy-audit.md` for secure foundation, authorization, tenant/customer scope, audit, or tenant-isolation work, and AI-first operating-model, governance, audit, outcome, and UI-surface specs when they constrain the task
- `docs/ai-first-saas-application-architecture.md` when the task must preserve AI-first semantics and the local specs do not fully capture them
- relevant cross-cutting spec files, including `*ui-style-guide*.md` for browser UI tasks
- relevant module and sprint specs when module-oriented planning is present
- relevant slice spec only when needed

Do not list the original PRD by default.

### Skills

List exact skills required for the task's component family. Add `core-saas-foundation` to secure foundation, authorization, tenancy, `/api/me`, audit, frontend context shell, and tenant-isolation tasks. Add `ai-first-saas` and the smallest relevant AI-first companion skill only when the task must implement or preserve agentic operating-model semantics; do not add the whole AI-first family by default.

Examples:
- entity task: `akka-event-sourced-entities`, `akka-ese-application-entity`, `akka-ese-unit-testing`
- workflow task: `akka-workflows`, `akka-workflow-component`, `akka-workflow-testing`
- view task: `akka-views`, `akka-view-from-event-sourced-entity`, `akka-view-testing`
- endpoint task: `akka-http-endpoints`, `akka-http-endpoint-component-client`, `akka-http-endpoint-testing`
- consumer task: `akka-consumers`, `akka-consumer-from-topic`, `akka-consumer-testing`
- timed action task: `akka-timed-actions`, `akka-timed-action-component`, `akka-timed-action-testing`

## Required queue entry shape

Each task must look like:

```md
### TASK-001: <short title>

- status: pending
- source: specs/backlog/01-<slice>-build-backlog.md
- task brief: none
- depends on: []
- required reads:
  - specs/akka-solution-plan.md
  - specs/backlog/01-<slice>-build-backlog.md
- skills:
  - <skill-name>
- expected outputs:
  - <bounded output>
- required checks:
  - mvn test
- done criteria:
  - <specific stopping criterion>
- notes:
  - <optional note>
```

If a task is not ready because it needs a task brief first, use:

```md
- status: blocked
- notes:
  - blocked: split this backlog item with akka-backlog-item-to-task-brief before implementation
```

## Final review checklist

Before finishing, verify:
- `specs/pending-tasks.md` exists
- secure foundation tasks are present and runnable before CRM/domain-specific tasks for SaaS app queues; missing foundation work is not treated as cross-cutting polish
- each runnable backlog task item has a queue entry
- no obvious duplicate queue entries were created
- obsolete non-done queue entries were superseded rather than deleted
- existing task IDs and statuses were preserved where possible
- unresolved blocking questions are reflected as blocked/omitted task work, not hidden assumptions
- dependencies are neither missing nor over-serialized
- required reads are minimal and sufficient, including AI-first doctrine or specs only where they affect the task
- skills match the component family plus any necessary AI-first companion skill
- required checks and done criteria are concrete
- no application code was changed

## Response style

When using this skill:
- summarize the backlog files used as input
- report whether the queue was created or repaired
- name any blocking pending questions that prevented task materialization
- name the first runnable pending task when one exists
- recommend continuing with `akka-do-next-pending-question` if questions block the queue, or a fresh context with `akka-do-next-pending-task` when tasks are ready
- do not implement code

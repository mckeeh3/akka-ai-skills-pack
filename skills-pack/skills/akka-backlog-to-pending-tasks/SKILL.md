---
name: akka-backlog-to-pending-tasks
description: Create or repair specs/pending-tasks.md from existing specs/backlog/*-build-backlog.md files when backlogs already exist but the durable pending-task queue is missing, stale, or incomplete.
---

# Akka Backlog to Pending Tasks

Use this skill when a project already has planning artifacts under `specs/`, especially `specs/backlog/*.md`, but does not yet have a usable durable queue at:

```text
specs/pending-tasks.md
```

This is a queue repair/materialization skill. It does not redo PRD decomposition, does not revise app meaning, and does not implement application code. It translates already-accepted backlog/task-brief work into the durable execution queue while preserving source semantics. For generated secure AI-first SaaS, source semantics include the existing workstream graph chain: workstream → role-specific dashboard attention → human surface graph node/action or workstream event → internal workstream agent graph delegation/result when applicable → governed-tool inside capability and surface/action maps → selected Akka substrate/exposure channel → request-based Agent or durable AutonomousAgent task → notification/projection → audit/work trace.

## Goal

Create or repair `specs/pending-tasks.md` so future harness runs can execute one focused task at a time with `akka-do-next-pending-task`. Each generated or repaired queue entry must be an execution handoff, not a planning placeholder: it should include the source backlog/task brief, dependencies, smallest required reads, exact skills, expected outputs, checks, done criteria, and the inherited workstream id, dashboard/attention, surface graph node/action, internal-agent graph context, governed-tool id, capability id, AuthContext/scope, role/capability rules, approval, audit/trace, UI/style, existing-app, and Java base-package context needed to implement safely without rereading the PRD.

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
- `../docs/ai-first-saas-application-architecture.md` when backlog work involves delegated operations, agents, governance, decisions, supervision, audit, or outcomes
- `../docs/requirements-to-workstream-development-process.md` when materializing generated SaaS workstreams, attention, dashboards, surface actions, capabilities, AutonomousAgent tasks, notifications/projections, or trace-aware queues
- `../docs/pending-question-queue.md`
- `../docs/pending-task-queue.md`
- `../docs/solution-plan-to-implementation-queue.md`
- `../docs/module-sprint-planning.md` when `specs/modules/` or `specs/sprints/` exists
- `../docs/web-ui-style-guide.md` when materializing browser UI tasks
- `../../../specs/README.md`
- `../../../specs/akka-solution-plan.md`
- `../../../specs/pending-questions.md` if it exists
- `../../../specs/pending-tasks.md` if it already exists
- all relevant `../../../specs/backlog/*-build-backlog.md` files
- `../../../specs/tasks/README.md` if present
- relevant `../../../specs/tasks/**/*.md` task briefs if present
- relevant `../../../specs/modules/*.md` and `../../../specs/sprints/*.md` files when present
- relevant `../../../specs/slices/*.md` files when needed to resolve dependencies or reads

Do not reread the original PRD unless the existing backlogs are too ambiguous to create queue tasks. Prefer carrying forward the AI-first interpretation already captured in solution, sprint, slice, backlog, task brief, app-description, and pending-question artifacts.

## Output

Create or update:

```text
specs/pending-tasks.md
```

Use the contract in `../docs/pending-task-queue.md`, especially the required `Vertical workstream contract` block. For generated full-stack AI-first SaaS, do not emit a runnable queue entry unless that contract is present or the task explicitly declares `internal-only`, `foundation-only`, `cross-cutting`, `docs-only`, or `non-runtime` scope with a non-attention/non-UI reason, trace expectations, and validation path.

## Queue derivation rules

### Source of tasks

Derive queue tasks from each backlog file's `Suggested harness task breakdown` section. A runnable generated-SaaS task must inherit its workstream id, role-specific dashboard, attention category/item, surface graph node/edge or surface action, governed-tool id/class, capability id/class, internal-agent delegation/result context, AuthContext/scope, selected substrate/exposure channel, notification/projection, and audit/work trace obligations. If the backlog item only says `build dashboard`, `CRUD`, `page`, `component`, or `agent work` without that vertical context, repair the backlog first or create a blocked task for `akka-backlog-item-to-task-brief` instead of materializing it as runnable.

Security and web UI baseline tasks must never be omitted as cross-cutting polish. For SaaS app queues, ensure the first runnable tasks implement or verify the full-stack secure foundation and, for minimum/basic/core app/chatbot-like generated SaaS, the five-core workstream domain shell before domain-specific features: Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, WorkOS/JWT seam, `/api/me`, central authorization, full invitation lifecycle, email delivery/outbox, InvitationWorkflow, expiry/reminder timers, InvitationView, UserDirectoryView, MembershipView, AdminAuditView, AccessReviewQueueView, membership/role management, admin audit/search, governed runtime agent foundation, AI admin agents including AdminRiskAgent and AccessReviewAgent or a skilled UserAdminAgent, decision cards for risky admin actions, My Account/User Admin/Agent Admin/Audit or Trace/Governance or Policy workstream surfaces, admin UI surfaces, frontend shell/context selection, and security/admin/frontend tests. If the source backlogs lack those tasks or still describe a User-Admin-only core app, repair the queue only after adding or flagging the missing foundation backlog coverage instead of silently proceeding to domain work.

Governed runtime agent foundation work must be materialized as multiple bounded tasks, not one broad `agent governance` task. Split queue entries by component/UI/test family, for example: `AgentDefinition` lifecycle/profile and agent catalog/detail; `PromptDocument`/`PromptVersion` governance, prompt assembly, and `PromptAssemblyTrace`; `SkillDocument`/`SkillVersion`, `ReferenceDocument`/`ReferenceVersion`, `AgentSkillManifest`/`AgentReferenceManifest`, authorized `readSkill(skillId)`/`readReferenceDoc(referenceId)`, and `SkillLoadTrace`/`ReferenceLoadTrace`; `ToolPermissionBoundary` management; `AgentWorkTrace` recording/search/detail; behavior editing agents and proposed-diff approval flows; prompt/skill/reference/manifest/tool-boundary UI surfaces; and security/admin/agent-governance tests.

Do not create one queue task per class name unless the backlog explicitly frames each class as a separate harness-sized task.

### AI-first context preservation

When source artifacts classify work as AI-first or include delegated operations, agents, policy governance, approvals, exceptions, audit traces, supervision UI, or outcome loops:
- include `docs/ai-first-saas-application-architecture.md` in required reads unless a more focused AI-first task brief already contains the needed context
- include the smallest relevant AI-first companion skill alongside component skills, such as `ai-first-saas-agent-team-design`, `ai-first-saas-policy-governance`, `ai-first-saas-decision-cards`, `ai-first-saas-audit-trace`, `ai-first-saas-ui-surfaces`, or `ai-first-saas-outcomes-metrics`
- preserve authority, approval, policy, evidence, trace, and outcome constraints in task notes or done criteria when those constraints affect implementation
- preserve workstream attention, role-specific dashboard, surface graph node/action, governed-tool, internal workstream agent graph delegation/result, AutonomousAgent task lifecycle, notification/projection, and audit/work trace context so the next harness run does not replan component-first
- keep implementation tasks bounded to the Akka substrate component being built; do not create broad doctrine-reading tasks unless the backlog explicitly asks for planning or docs

### Question gate

If `specs/pending-questions.md` exists, inspect unresolved `blocking` questions before materializing tasks.

Rules:
- do not create runnable tasks for work blocked by unresolved `blocking` questions
- treat unknown security-provider setup details as blockers only for provider-specific integration tasks; WorkOS is the supported browser auth service and Resend (resend.com) is the supported production email service, so provider-selection is not a blocker; keep local authorization contracts, tenancy models, AuthContext, `/api/me`, audit, and tenant-isolation tasks runnable when their semantics are otherwise defined
- if a backlog item is entirely blocked, create a `blocked` task only when useful for visibility and note the blocking question IDs
- if only part of a backlog is blocked, create tasks for unblocked work and leave blocked work out or blocked with explicit question references
- if a question is `answered` but not `resolved`, reconcile it or leave affected tasks blocked
- do not silently choose defaults unless the question is `deferred` with an accepted default or limitation
- if browser UI tasks exist and no selected style guide or pending style question exists, add/update `specs/pending-questions.md` with the style-selection question from `../docs/web-ui-style-guide.md` and block only the affected UI tasks

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
- split any broad managed-agent item that spans `AgentDefinition`, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, `AgentSkillManifest`, `AgentReferenceManifest`, `readSkill`, `readReferenceDoc`, `SkillLoadTrace`, `ReferenceLoadTrace`, `PromptAssemblyTrace`, behavior editing agents, tool boundaries, traces, UI, and tests into separate component/UI/test queue tasks; a task covering all of these is too broad and must not be runnable
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
- secure foundation tasks for Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, WorkOS/JWT seam, `/api/me`, central authorization, invitation lifecycle, email delivery/outbox, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin agents, decision cards for risky admin actions, admin UI surfaces, audit, frontend shell/context selection, and security/admin/frontend tests come before app-specific domain-specific tasks
- foundational domain/config tasks usually have no dependencies
- entity/workflow tasks depend on required domain tasks
- views depend on their source component tasks
- endpoint tasks depend on components they call
- end-to-end tests depend on the components under test
- later sprint or slice tasks depend only on earlier tasks that are genuinely required

Avoid over-serializing independent work.

### Required reads and preserved task context

Each generated task must preserve the source capability and governed-tool ids when available, the workstream id, role-specific dashboard, attention category/item, surface graph node/edge or surface action, internal-agent graph delegation/result context, fixed Java base package `ai.first` for Java source tasks, the actor/caller and `AuthContext`, required role/scope or permission checks, approval gates, selected Akka substrate/exposure channel, AutonomousAgent task lifecycle/notification/result semantics when applicable, audit/trace obligations, UI surfaces affected, and concrete checks. For governed runtime agent foundation tasks, record which foundation scope is in the task (`AgentDefinition`, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, `AgentSkillManifest`, `AgentReferenceManifest`, `readSkill`, `readReferenceDoc`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, behavior editing, tool-boundary UI, or tests) so later runs do not merge adjacent agent-governance work. Do not create Java package-selection questions; generated Java source uses `ai.first`.

Each task should list the smallest useful reads, usually:
- `specs/akka-solution-plan.md`
- the source backlog file
- the matching task brief when one exists
- relevant cross-cutting spec files, including `specs/cross-cutting/00-common-domain-and-conventions.md` for Java base package/package-root conventions and `specs/cross-cutting/01-auth-tenancy-audit.md` for secure foundation, authorization, tenant/customer scope, audit, or tenant-isolation work, and AI-first operating-model, governance, audit, outcome, and UI-surface specs when they constrain the task
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

### Validator gate

After creating or repairing the queue, run the lightweight queue contract validator when the script is available in the pack source or downstream tooling:

```bash
# source attention
bash skills-pack/tools/validate-pending-task-workstream-contract.sh specs/pending-tasks.md

# skills-library install
bash .agents/skills/tools/validate-pending-task-workstream-contract.sh specs/pending-tasks.md
```

Pack tools are installed under `.agents/skills/tools/**`; when working from this repository source attention, the same validator is available under `skills-pack/tools/**`. Treat validator failures as queue repair work, not implementation blockers for a later coding run. The validator checks that runnable generated-SaaS tasks carry the required vertical contract vocabulary; it does not prove semantic correctness.

## Final review checklist

Before finishing, verify:
- `specs/pending-tasks.md` exists
- secure foundation tasks are present and runnable before domain-specific tasks for SaaS app queues; missing foundation work is not treated as cross-cutting polish
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

---
name: akka-backlog-to-pending-tasks
description: Create or repair specs/pending-tasks.md from existing specs/backlog/*-build-backlog.md files when backlogs already exist but the durable pending-task queue is missing, stale, or incomplete.
---

# Akka Backlog to Pending Tasks

Use this skill when a project already has planning artifacts under `specs/`, especially `specs/backlog/*.md`, but does not yet have a usable durable queue at:

```text
specs/pending-tasks.md
```

This is a queue repair/materialization skill. It does not redo PRD decomposition, does not revise app meaning, and does not implement application code. It translates already-accepted backlog/task-brief work into the durable execution queue while preserving current-intent graph provenance and source semantics. For generated secure AI-first SaaS, source semantics include the existing workstream graph chain: workstream → role-specific dashboard attention → human surface graph node/action or workstream event → internal workstream agent graph delegation/result when applicable → governed-tool inside capability and surface/action maps → selected Akka substrate/exposure channel → request-based Agent or durable AutonomousAgent task → notification/projection → audit/work trace.

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
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/ai-first-saas-application-architecture.md` when backlog work involves delegated operations, agents, governance, decisions, supervision, audit, or outcomes
- `../docs/requirements-to-workstream-development-process.md` when materializing generated SaaS workstreams, attention, dashboards, surface actions, capabilities, AutonomousAgent tasks, notifications/projections, or trace-aware queues
- `../docs/pending-question-queue.md`
- `../docs/pending-task-queue.md`
- `../docs/solution-plan-to-implementation-queue.md`
- `../docs/module-sprint-planning.md` when `specs/modules/` or `specs/sprints/` exists
- `../docs/web-ui-style-guide.md` when materializing browser UI tasks
- target project path: specs/README.md
- target project path: specs/akka-solution-plan.md
- target project path: specs/pending-questions.md if it exists
- target project path: specs/pending-tasks.md if it already exists
- all relevant target project path: specs/backlog/*-build-backlog.md files
- target project path: specs/tasks/README.md if present
- relevant target project path: specs/tasks/**/*.md task briefs if present
- relevant target project path: specs/modules/*.md and target project path: specs/sprints/*.md files when present
- relevant target project path: specs/slices/*.md files when needed to resolve dependencies or reads

Do not reread the original PRD unless the existing backlogs are too ambiguous to create queue tasks. Prefer carrying forward the AI-first interpretation already captured in solution, sprint, slice, backlog, task brief, app-description, and pending-question artifacts.

## Output

Create or update:

```text
specs/pending-tasks.md
```

Use the contract in `../docs/pending-task-queue.md`, especially the required `Vertical workstream contract` block. For generated full-stack AI-first SaaS, do not emit a runnable queue entry unless that contract is present or the task explicitly declares `internal-only`, `foundation-only`, `cross-cutting`, `docs-only`, or `non-runtime` scope with a non-attention/non-UI reason, trace expectations, and validation path.

## Queue derivation rules

Use `../docs/intent-compiler-skill-contracts.md` and `../docs/intent-to-realization-flow.md` for the shared queue/task/reconciliation contract. Preserve existing ids, statuses, dependencies, implementation history, current-intent graph provenance, capability/workstream/surface/agent context, AuthContext/scope, authorization, traces, idempotency, tests, acceptance checks, and explicit out-of-scope items.

For SaaS Foundation App planning, keep required coverage for invitation lifecycle, email delivery, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin/AdminRiskAgent/AccessReviewAgent, decision cards for risky admin, AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, readSkill, PromptAssemblyTrace, SkillLoadTrace, behavior editing, agent catalog, and agent detail in the relevant task sequence.

## Final review checklist

Before finishing, verify:
- `specs/pending-tasks.md` exists
- secure foundation tasks are present and runnable before domain-specific tasks for SaaS app queues; missing foundation work is not treated as cross-cutting polish
- each runnable backlog task item has a queue entry with current-intent provenance
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

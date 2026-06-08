---
name: akka-pending-task-queue-maintenance
description: Audit, repair, and maintain a long-lived specs/pending-tasks.md queue; detect stale, duplicate, blocked, superseded, and inconsistent tasks without implementing application code.
---

# Akka Pending Task Queue Maintenance

Use this skill to keep a large or long-lived `specs/pending-tasks.md` reliable as a project evolves through many PRDs, feature requests, bug fixes, and implementation sessions.

This is a queue hygiene and reconciliation skill. It does not implement application code.

## Goal

Audit and maintain the pending task queue so future `akka-do-next-pending-task` runs remain safe and bounded.

The skill should:
- validate queue shape against the queue and intent-compiler contracts
- preserve stable task IDs and status history
- detect duplicate or overlapping tasks
- detect stale tasks whose source specs changed or disappeared
- detect blocked tasks that may now be unblocked
- detect pending tasks with missing dependencies, reads, skills, checks, or done criteria
- detect tasks that are runnable only because unresolved pending questions were ignored
- preserve AI-first operating-model context such as delegated authority, policies, decisions, traces, UI surfaces, evaluations, and outcomes across queue repairs
- preserve workstream-expertise and reference-governance context for LLM-backed functional agents: model binding, prompt/skill/reference governance, compact manifests, `readSkill`/`readReferenceDoc`, loader authorization, tool boundaries, load traces, expertise surfaces, default-content policy, and tests
- repair or block stale generated-SaaS tasks that have regressed into component-only, CRUD-only, page-only, or dashboard-only work without workstream, role-specific dashboard attention, human surface graph nodes/edges, governed-tool exposure, capability id, API/exposure, Akka substrate, internal workstream agent graph context when relevant, auth, traces, and tests
- mark obsolete tasks as `superseded` when justified
- append maintenance follow-up tasks only when needed
- report the next runnable task

## Use this skill when

The task sounds like:
- "audit the pending task queue"
- "clean up specs/pending-tasks.md"
- "sync the queue after several changes"
- "find stale or duplicate tasks"
- "review blocked tasks"
- "is the next task still valid?"
- "the queue is getting large; maintain it"

Use `akka-backlog-to-pending-tasks` when the queue is missing or needs materialization from backlogs.

Use `akka-revised-prd-reconciliation` when a full revised PRD is the reason the queue may be stale.

Use `akka-change-request-to-spec-update` when a specific feature/bug/change needs to update specs and queue together.

## Required reading

Read these first if present:
- `../README.md`
- `../docs/pending-question-queue.md`
- `../docs/pending-task-queue.md`
- `../docs/solution-plan-to-implementation-queue.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/ai-first-saas-application-architecture.md` when the queue contains or references delegated work, agents, approvals, exceptions, governance, audit, supervision UI, or outcomes
- `../docs/workstream-expertise-model.md` when the queue contains or references LLM-backed functional agents, workstream expertise, reference governance, `readReferenceDoc`, model binding, manifests, loader authorization, tool boundaries, or load traces
- `../akka-do-next-pending-task/SKILL.md`
- `../akka-backlog-to-pending-tasks/SKILL.md`
- target project path: specs/README.md if present
- target project path: specs/akka-solution-plan.md if present
- target project path: specs/pending-questions.md if present
- target project path: specs/pending-tasks.md
- relevant `specs/backlog/*.md` files referenced by queue tasks
- relevant `specs/tasks/**/*.md` task briefs referenced by queue tasks

Read slice specs only when needed to determine whether a queue entry is stale or duplicated.
Do not read the original PRD by default.

## Queue status values

Recognize these statuses:
- `pending`
- `in-progress`
- `blocked`
- `done`
- `deferred`
- `superseded`

If a queue does not yet document `superseded`, update its local queue rules to include it when you mark any task superseded.

## Maintenance workflow

Use `../docs/intent-compiler-skill-contracts.md` and `../docs/intent-to-realization-flow.md` for the shared queue/task/reconciliation contract. Preserve existing ids, statuses, dependencies, implementation history, current-intent graph provenance, capability/workstream/surface/agent context, AuthContext/scope, authorization, traces, idempotency, tests, acceptance checks, and explicit out-of-scope items.

For SaaS Foundation App planning, keep required coverage for invitation lifecycle, email delivery, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin/AdminRiskAgent/AccessReviewAgent, decision cards for risky admin, AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, readSkill, PromptAssemblyTrace, SkillLoadTrace, behavior editing, agent catalog, and agent detail in the relevant task sequence.

## Anti-patterns

Avoid:
- treating queue maintenance as permission to code
- deleting stale tasks instead of preserving history
- renumbering existing tasks
- changing done tasks to pending
- ignoring blocked tasks indefinitely
- accepting duplicate pending tasks without notes
- leaving stale component-only, CRUD-only, page-only, or dashboard-only generated-SaaS tasks runnable when their workstream/attention/surface-graph/governed-tool/capability-id contract is missing
- reading the full PRD for ordinary queue hygiene

## Final review checklist

Before finishing, verify:
- queue rules mention all statuses in use
- task IDs and dependency references are valid
- non-done tasks have usable required reads, skills, and current-intent provenance or a valid exemption
- stale/duplicate tasks are blocked or superseded
- AI-first task entries preserve authority, policy, decision, trace, UI-surface, evaluation, and outcome context when present in source artifacts
- generated-SaaS task entries preserve or inherit workstream, attention category, role-specific dashboard, human surface graph node/action edge, governed-tool id/exposure, capability id, API/exposure, selected Akka substrate, internal workstream agent graph result handling, autonomous task notification/result mapping, auth, traces, and tests
- LLM-backed functional-agent task entries preserve or inherit workstream expertise, model binding, skill/reference governance, `readReferenceDoc`, manifest assignment, loader authorization, tool boundaries, load traces, expertise surfaces, default-content governance policy, and tests
- unresolved AI-first blockers are not left runnable
- completed task history is preserved
- no code was implemented
- the next runnable task is reported

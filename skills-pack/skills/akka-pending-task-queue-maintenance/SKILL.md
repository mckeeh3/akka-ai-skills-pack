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
- validate queue shape against the queue contract
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
- `../docs/ai-first-saas-application-architecture.md` when the queue contains or references delegated work, agents, approvals, exceptions, governance, audit, supervision UI, or outcomes
- `../docs/workstream-expertise-model.md` when the queue contains or references LLM-backed functional agents, workstream expertise, reference governance, `readReferenceDoc`, model binding, manifests, loader authorization, tool boundaries, or load traces
- `../akka-do-next-pending-task/SKILL.md`
- `../akka-backlog-to-pending-tasks/SKILL.md`
- target project `specs/README.md` if present
- target project `specs/akka-solution-plan.md` if present
- target project `specs/pending-questions.md` if present
- target project `specs/pending-tasks.md`
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

### 1. Validate queue structure

Check:
- task IDs are stable and unique
- statuses are valid
- each task has source, task brief, dependencies, required reads, skills, expected outputs, required checks, done criteria, and notes when useful
- dependencies reference existing task IDs
- no task depends on a superseded task unless the dependency is intentionally replaced
- no more than one task is `in-progress` unless the project explicitly permits parallel work

### 2. Validate source links

For each non-done task, verify:
- source backlog exists or the queue note explains why not
- task brief exists when listed
- required reads exist or are intentionally external
- source backlog still contains matching work or equivalent scope

If a source disappeared or changed materially, mark the task `blocked` unless the supersession target is clear.

### 3. Detect duplicates and overlap

Compare task titles, sources, expected outputs, and done criteria.

Recommended actions:
- if both are pending and clearly duplicate, keep the earlier ID and mark the later `superseded`
- if one is done and one is pending, keep both only if the pending task is a legitimate follow-up
- if overlap is partial, leave both but add notes or recommend task-brief split

### 4. Review blocked tasks

For each `blocked` task:
- check whether missing source files, decisions, or dependencies now exist
- if unblocked and dependencies are satisfied, set status to `pending`
- if still blocked, preserve or clarify the blocker note
- if the blocker was resolved by a replacement task, mark `superseded`

### 5. Review pending-question blockers

If `specs/pending-questions.md` exists:
- find unresolved `blocking` questions
- identify non-done tasks whose source, expected outputs, or notes are blocked by those questions
- treat unresolved AI-first authority, approval, policy, risk, trace, UI-surface, evaluation, or outcome decisions as blockers for only the affected tasks
- mark affected tasks `blocked` or clarify their blocker notes
- unblock tasks only when the relevant question is `resolved` or explicitly `deferred` with an accepted default

### 6. Review AI-first context preservation

For non-done tasks sourced from AI-first slices, backlogs, task briefs, or app-description operating-model sections, verify the task metadata still names relevant:
- delegated work and retained authority
- policies, approvals, exceptions, permissions, and thresholds
- decision-card, audit trace, work trace, tool/data-access, and outcome obligations
- supervision, governance, digest, audit, or outcome UI surfaces
- agent evaluation, replay, simulation, or outcome-metric checks
- workstream expert bundle scope, model binding, governed prompt/skill/reference docs, compact manifests, authorized `readSkill`/`readReferenceDoc`, ToolPermissionBoundary, SkillLoadTrace/ReferenceLoadTrace/AgentWorkTrace, expertise UI surfaces, default-content governance policy, and tests when an LLM-backed functional agent is in scope

If source artifacts contain these obligations but the queue entry omits them, update required reads, skills, required checks, done criteria, or notes to preserve the vertical contract: workstream/functional agent or internal/foundation scope, attention category, role-specific dashboard purpose, human surface graph node/action edge, governed-tool id and qualified exposure, capability id/API exposure, selected Akka substrate, internal workstream agent graph delegation/result surface when relevant, events/notifications/projections, audit/work trace, and tests. If LLM-backed workstream expertise is in scope, preserve the model-binding, skill/reference-governance, `readReferenceDoc`, manifest, loader/boundary, trace, surface, default-content, and test contract instead of collapsing it into a generic agent task. If the omission hides an unresolved decision, block the affected task and reference or append the pending question instead of guessing. If an AutonomousAgent or autonomous task is in scope, require task start/query/result/lifecycle capabilities, progress/result surfaces, notification mapping, failure/cancellation attention behavior, and lifecycle tests before leaving it runnable.

### 7. Review stale pending tasks

A pending task is stale when:
- its source requirement changed
- its source backlog item no longer exists
- expected outputs were already produced by another task
- its listed skills no longer match the component family
- its dependencies are invalid
- it conflicts with current app-description/specs

Actions:
- update metadata when the same task remains valid
- block when a decision is needed
- supersede when a replacement exists or must be appended

### 8. Optional queue compaction guidance

Do not delete tasks by default.

If the queue is very large, recommend optional archiving rather than doing it silently:

```text
specs/archive/pending-tasks-done-YYYY-MM.md
```

Only archive if the user explicitly asks. Even then, preserve dependency references or leave a stub/index in `specs/pending-tasks.md`.

### 9. Report next runnable task

Use the standard selection algorithm:
1. ignore `done`, `blocked`, `deferred`, and `superseded`
2. consider `pending` tasks in file order
3. select the first one whose dependencies are all `done` or empty
4. if none exists, report the earliest blocking dependency chain

## Safe edits

Allowed:
- fix malformed task metadata
- update stale required reads/skills/checks/done criteria
- change task status between `blocked` and `pending` when evidence supports it
- mark duplicates/obsolete tasks `superseded`
- append replacement or maintenance tasks when necessary
- update queue rules to include `superseded`

Not allowed:
- implement application code
- delete completed tasks
- renumber tasks for aesthetics
- rewrite backlogs broadly unless the user asked for spec maintenance
- make product decisions silently

## Maintenance report shape

Use this response shape:

```md
# Pending Task Queue Maintenance

## Queue summary
- total:
- pending:
- in-progress:
- blocked:
- done:
- deferred:
- superseded:

## Findings
- invalid metadata:
- missing sources/reads:
- duplicates/overlap:
- stale tasks:
- blocked task review:

## Queue edits
- updated:
- unblocked:
- blocked:
- superseded:
- appended:

## Remaining risks or questions
- ...

## Next runnable task
- ...
```

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
- non-done tasks have usable required reads and skills
- stale/duplicate tasks are blocked or superseded
- AI-first task entries preserve authority, policy, decision, trace, UI-surface, evaluation, and outcome context when present in source artifacts
- generated-SaaS task entries preserve or inherit workstream, attention category, role-specific dashboard, human surface graph node/action edge, governed-tool id/exposure, capability id, API/exposure, selected Akka substrate, internal workstream agent graph result handling, autonomous task notification/result mapping, auth, traces, and tests
- LLM-backed functional-agent task entries preserve or inherit workstream expertise, model binding, skill/reference governance, `readReferenceDoc`, manifest assignment, loader authorization, tool boundaries, load traces, expertise surfaces, default-content governance policy, and tests
- unresolved AI-first blockers are not left runnable
- completed task history is preserved
- no code was implemented
- the next runnable task is reported

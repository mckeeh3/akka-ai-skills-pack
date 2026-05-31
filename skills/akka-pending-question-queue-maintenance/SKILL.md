---
name: akka-pending-question-queue-maintenance
description: Audit, repair, and maintain specs/pending-questions.md; detect stale, duplicate, blocked, answered-but-unreconciled, superseded, and task-blocking questions without implementing application code.
---

# Akka Pending Question Queue Maintenance

Use this skill to keep a long-lived `specs/pending-questions.md` reliable as requirements, specs, backlogs, and implementation queues evolve.

This is a question queue hygiene and reconciliation skill. It does not implement application code.

## Goal

Audit and maintain the pending question queue so planning can progress without losing decisions or forcing the user through duplicate questions.

The skill should:
- validate queue shape against the question queue contract
- preserve stable question IDs and history
- detect duplicate or overlapping questions
- detect stale questions whose source specs changed or disappeared
- find `answered` questions that still need reconciliation
- find blocked questions that may now be unblocked
- mark obsolete questions as `superseded` when justified
- ensure `blocking` questions accurately identify what they block
- preserve AI-first decisions about delegated authority, policies, decision cards, traces, UI surfaces, evaluation, and outcomes while repairing the queue
- preserve requirements-to-workstream decisions about workstream responsibility, attention category lifecycle, role-specific dashboard scope, human surface graph nodes/edges, surface action authority, governed-tool id/exposure, capability id/API exposure, internal workstream agent graph delegation/result handling, AutonomousAgent lifecycle/result handling, notification visibility, task result/progress surfaces, and human/agent worker assignment while repairing the queue
- preserve workstream-expertise and reference-governance decisions about model binding, skills, references, manifests, authorized `readSkill`/`readReferenceDoc`, `ToolPermissionBoundary`, load traces, expertise surfaces, seed policy, and tests while repairing the queue
- report whether pending task generation is safe

## Use this skill when

The task sounds like:
- "audit pending-questions.md"
- "clean up the question queue"
- "sync questions after planning changes"
- "find stale or duplicate questions"
- "review answered questions"
- "are there blockers before pending-tasks?"
- "is the question queue clear?"

Use `akka-pending-question-generation` when no queue exists or new open questions need to be generated from a plan.
Use `akka-do-next-pending-question` when the user wants to answer or reconcile one question.

## Required reading

Read these first if present:
- `../README.md`
- `../../docs/pending-question-queue.md`
- `../../docs/pending-task-queue.md`
- `../../docs/ai-first-saas-application-architecture.md` when questions involve delegated work, agents, approvals, exceptions, governance, audit, supervision UI, or outcomes
- `../../docs/workstream-expertise-model.md` when questions involve functional-agent expertise, reference governance, `readReferenceDoc`, model binding, manifests, loader authorization, tool boundaries, or load traces
- `../akka-pending-question-generation/SKILL.md`
- `../akka-do-next-pending-question/SKILL.md`
- target project `specs/README.md` if present
- target project `specs/pending-questions.md`
- target project `specs/akka-solution-plan.md` if present
- relevant `specs/slices/*.md`, `specs/backlog/*.md`, and app-description files referenced by questions
- `specs/pending-tasks.md` if it exists and questions claim to block or unblock tasks

Do not read the original PRD by default unless source links are insufficient to judge a question.

## Recognized status values

- `pending`
- `asked`
- `answered`
- `resolved`
- `blocked`
- `deferred`
- `superseded`

## Maintenance workflow

### 1. Validate queue structure

Check:
- question IDs are stable and unique
- statuses and priorities are valid
- dependencies reference existing question IDs
- each question has category, blocks, source, question, why-it-matters, answer, decision, decision impact, and reconciled-into fields
- no question depends on a superseded question unless the replacement is documented

### 2. Review answered questions

For each `answered` question:
- verify whether affected artifacts already reflect the decision
- if yes, mark `resolved` and list reconciled files
- if not, either reconcile the smallest relevant artifacts or leave `answered` with a precise note naming what must be updated

Answered questions should not remain unanswered in planning indefinitely.

### 3. Review blocked and pending questions

For each `blocked` question:
- check whether its dependency or missing source is now available
- if unblocked, set status to `pending`
- if still blocked, clarify the blocker note

For each `pending` or `asked` question:
- confirm it still affects a real decision
- downgrade priority if it no longer blocks work
- mark `superseded` if the plan changed enough that it is irrelevant

### 4. Detect duplicates and overlap

Compare titles, question text, blocked areas, source links, and categories.

Recommended actions:
- if two pending questions ask the same decision, keep the earlier ID and mark the later `superseded`
- if one question is a narrower dependency of another, add a dependency rather than deleting either
- if a question is too broad, split it only when the split is necessary for one-at-a-time answering

### 5. Validate AI-first blocker coverage

For questions sourced from AI-first app-description, solution-plan, slice, backlog, or task artifacts, verify unresolved decisions are represented as actionable blockers when they affect:
- delegated work or retained human authority
- policy clauses, approval gates, permissions, thresholds, or escalation rules
- decision-card evidence, risk, confidence, impact, alternatives, or actions
- audit/work/decision traces, tool/data-access records, evaluation, replay, simulation, or outcome metrics
- supervision, governance, digest, audit, decision, or outcome UI surfaces
- workstream attention semantics, role-specific dashboard scope, human surface graph nodes/edges, surface action authority, governed-tool id/exposure, capability id/API exposure, internal workstream agent graph delegation/result handling, autonomous task lifecycle/results, notification visibility, task progress/result surfaces, or human/agent worker assignment
- LLM-backed workstream expertise: approved model binding, governed prompt/skill/reference ownership, compact manifests, `readSkill`/`readReferenceDoc` loader authority, reference redaction/denial semantics, ToolPermissionBoundary, load traces, expertise surfaces, seed/import policy, or tests

Do not downgrade these to optional cosmetic questions when implementation would otherwise guess consequential behavior.

### 6. Validate relationship to pending tasks

If `specs/pending-tasks.md` exists:
- identify tasks that are blocked by unresolved `blocking` questions
- ensure such tasks are `blocked`, not silently runnable
- if a question was resolved, check whether blocked tasks can become pending

If `specs/pending-tasks.md` does not exist:
- report whether unresolved `blocking` questions prevent task generation
- identify unblocked areas where tasks could safely be generated

## Safe edits

Allowed:
- fix malformed question metadata
- update statuses based on evidence
- reconcile answered questions into planning artifacts
- mark stale/duplicate questions `superseded`
- unblock questions whose dependencies are resolved
- append replacement questions when a split is required

Not allowed:
- implement application code
- delete resolved question history
- renumber existing questions
- silently decide product behavior without a recorded answer or safe default
- create implementation tasks that remain blocked by unresolved questions
- strip AI-first operating-model context from question text, decisions, blocks, or reconciliation targets during cleanup

## Maintenance report shape

Use this response shape:

```md
# Pending Question Queue Maintenance

## Queue summary
- total:
- pending:
- asked:
- answered:
- resolved:
- blocked:
- deferred:
- superseded:

## Findings
- invalid metadata:
- answered but unreconciled:
- duplicates/overlap:
- stale questions:
- blocked question review:
- task-generation blockers:

## Queue edits
- updated:
- reconciled:
- unblocked:
- blocked:
- superseded:
- appended:

## Readiness for pending tasks
- safe to create/update pending-tasks.md:
- blocked areas:

## Next pending question
- ...
```

## Final review checklist

Before finishing, verify:
- all statuses and priorities are valid
- answered questions are reconciled or explicitly still awaiting reconciliation
- blocking questions name the exact blocked area
- AI-first authority, approval, policy, risk, trace, UI-surface, evaluation, and outcome questions remain blocking when implementation would otherwise guess
- requirements-to-workstream questions about attention, role-specific dashboards, human surface graph nodes/edges, surface actions, governed-tool ids/exposure, capability ids, internal workstream agent graph delegations/results, AutonomousAgent lifecycle/notification/result surfaces, and human/agent worker assignment remain blocking when implementation would otherwise guess
- workstream-expertise/reference-governance questions about model binding, manifests, `readReferenceDoc`, loader authorization, tool boundaries, load traces, expertise surfaces, seed/import behavior, and tests remain blocking when implementation would otherwise guess
- stale/duplicate questions are resolved, deferred, or superseded
- question history is preserved
- no implementation code was changed
- the next actionable question or readiness for task generation is reported

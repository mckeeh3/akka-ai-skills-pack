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
- preserve workstream-expertise and reference-governance decisions about model binding, skills, references, manifests, authorized `readSkill`/`readReferenceDoc`, `ToolPermissionBoundary`, load traces, expertise surfaces, default-content policy, and tests while repairing the queue
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
- `../docs/pending-question-queue.md`
- `../docs/pending-task-queue.md`
- `../docs/ai-first-saas-application-architecture.md` when questions involve delegated work, agents, approvals, exceptions, governance, audit, supervision UI, or outcomes
- `../docs/workstream-expertise-model.md` when questions involve functional-agent expertise, reference governance, `readReferenceDoc`, model binding, manifests, loader authorization, tool boundaries, or load traces
- `../akka-pending-question-generation/SKILL.md`
- `../akka-do-next-pending-question/SKILL.md`
- target project path: specs/README.md if present
- target project path: specs/pending-questions.md
- target project path: specs/akka-solution-plan.md if present
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

Use `../docs/planning-skill-output-contracts.md` for the detailed output contract. Return only the actionable summary, affected artifacts/layers, required edits or queue changes, assumptions/questions, and next step. Preserve secure SaaS foundation, generated-SaaS runtime completion, tenant/customer scoping, backend authorization, governed agents/tools, traces, and tests when in scope.

## Final review checklist

Before finishing, verify:
- all statuses and priorities are valid
- answered questions are reconciled or explicitly still awaiting reconciliation
- blocking questions name the exact blocked area
- AI-first authority, approval, policy, risk, trace, UI-surface, evaluation, and outcome questions remain blocking when implementation would otherwise guess
- requirements-to-workstream questions about attention, role-specific dashboards, human surface graph nodes/edges, surface actions, governed-tool ids/exposure, capability ids, internal workstream agent graph delegations/results, AutonomousAgent lifecycle/notification/result surfaces, and human/agent worker assignment remain blocking when implementation would otherwise guess
- workstream-expertise/reference-governance questions about model binding, manifests, `readReferenceDoc`, loader authorization, tool boundaries, load traces, expertise surfaces, default-content governance, and tests remain blocking when implementation would otherwise guess
- stale/duplicate questions are resolved, deferred, or superseded
- question history is preserved
- no implementation code was changed
- the next actionable question or readiness for task generation is reported

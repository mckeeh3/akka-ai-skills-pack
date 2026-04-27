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

### 5. Validate relationship to pending tasks

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
- stale/duplicate questions are resolved, deferred, or superseded
- question history is preserved
- no implementation code was changed
- the next actionable question or readiness for task generation is reported

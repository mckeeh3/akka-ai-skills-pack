---
name: akka-pending-question-generation
description: Create or update specs/pending-questions.md from a PRD, app-description, solution plan, specs, or backlog when unresolved decisions should be answered before safe task generation or implementation.
---

# Akka Pending Question Generation

Use this skill when planning has discovered uncertainty that should be captured as a durable, one-question-at-a-time clarification queue instead of a large ad hoc question list.

This is a planning and clarification skill. It does not implement application code.

## Goal

Create or update:

```text
specs/pending-questions.md
```

The queue should capture only questions that affect downstream meaning, architecture, specs, backlog, task generation, tests, security, observability, UI/API contracts, or safe implementation.

The skill must:
- inspect the current PRD, app-description, solution plan, specs, backlogs, or existing question queue
- identify decisions the harness would otherwise have to guess
- order questions by dependency and impact
- mark blocking questions clearly
- preserve existing question IDs and statuses when updating a queue
- avoid asking a giant question list in chat
- report the next question to ask with `akka-do-next-pending-question`

## Use this skill when

Use this skill when the user says things like:
- "create pending-questions.md"
- "turn the open questions into a queue"
- "grill me, but one question at a time"
- "before pending-tasks, capture the unresolved decisions"
- "what decisions are blocking the plan?"
- "make a clarification backlog"

Also use it during PRD/spec planning when unresolved decisions would otherwise make `specs/pending-tasks.md` speculative.

Do **not** use this skill when:
- the next implementation step is already concrete and safe
- all uncertainties are minor optional refinements
- the user wants to execute implementation work; use `akka-do-next-pending-task`
- the user wants to answer an existing question; use `akka-do-next-pending-question`
- the user wants to audit an existing question queue; use `akka-pending-question-queue-maintenance`

## Required reading

Read these first if present:
- `../README.md`
- `../../docs/pending-question-queue.md`
- `../../docs/pending-task-queue.md`
- `../../docs/intent-driven-usage-flow.md`
- `../../specs/README.md`
- `../../specs/pending-questions.md` if it already exists
- `../../specs/akka-solution-plan.md` if present
- relevant `../../specs/slices/*.md`
- relevant `../../specs/backlog/*-build-backlog.md`
- relevant app-description files if the project uses description-first artifacts
- the user-provided PRD or requirements file when it is the source of the plan

Do not read the entire codebase. This skill is about planning decisions, not implementation details.

## Question discovery rules

Create questions only when the answer can change one or more of:
- Akka component selection
- Event Sourced Entity vs Key Value Entity choice
- workflow shape, pause/resume, compensation, or deadline behavior
- view/query/reporting requirements
- consumer/integration behavior
- timer/reminder/expiry behavior
- HTTP/gRPC/MCP/API contract
- UI behavior or realtime requirements
- auth, tenancy, audit, retention, or privacy model
- failure handling, retries, idempotency, or compensation
- acceptance, regression, or edge-case tests
- backlog slicing, dependencies, or task generation

Avoid questions that are:
- cosmetic
- answerable from existing artifacts
- implementation trivia the harness can decide from conventions
- duplicates of existing unresolved questions
- broad multi-part questions that should be split

## Prioritization

Use `priority: blocking` only when task generation or implementation would require guessing.

Use `priority: important` when the answer affects quality or later scope but does not block all safe progress.

Use `priority: optional` when the answer can safely wait.

If a question blocks only one slice or component family, say so in `blocks:`. Do not block unrelated work.

## Required output shape

Follow `../../docs/pending-question-queue.md` exactly.

Each question must include:
- stable question ID and short title
- status
- priority
- category
- dependencies
- blocked artifacts/decisions/work areas
- source/provenance
- one focused question
- why it matters
- options when useful
- default if deferred, or `none`
- answer/decision/decision impact placeholders
- reconciled-into list
- notes when useful

## Existing queue preservation

If `specs/pending-questions.md` already exists:
- preserve question IDs
- preserve `resolved`, `deferred`, and `superseded` history
- keep `answered` questions as `answered` until reconciliation is done
- update stale metadata when the same question remains valid
- mark obsolete unresolved questions `superseded` instead of deleting them
- append newly discovered questions using the next stable ID
- do not renumber questions for aesthetics

## Relationship to pending tasks

Before creating or updating `specs/pending-tasks.md`, check whether unresolved `blocking` questions affect planned tasks.

If blocking questions exist:
- create or update `specs/pending-questions.md`
- do not create implementation tasks for blocked work unless explicitly marked deferred with a safe default
- create tasks only for unblocked work, or stop and ask the next question depending on the user's requested workflow

## Final review checklist

Before finishing, verify:
- `specs/pending-questions.md` exists or no durable questions were needed
- every question has a clear design impact
- blocking questions name what they block
- question dependencies are valid
- existing statuses and IDs were preserved
- no application code was changed
- the next actionable question is identified

## Response style

When using this skill:
- summarize why a question queue was or was not needed
- list counts by status and priority
- name the next question to answer
- recommend continuing with `akka-do-next-pending-question`
- do not dump all question text in chat unless the user asks for the full queue

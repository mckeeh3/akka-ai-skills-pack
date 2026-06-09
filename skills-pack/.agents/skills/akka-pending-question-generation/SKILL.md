---
name: akka-pending-question-generation
description: Create or update specs/pending-questions.md from a PRD, app-description, solution plan, specs, or backlog when unresolved decisions should be answered before safe task generation or implementation.
---

# Akka Pending Question Generation

Use this skill when planning has discovered uncertainty that should be captured as a durable, one-question-at-a-time clarification queue instead of a large ad hoc question list.

This is a planning and clarification skill. It does not implement application code. For generated secure AI-first SaaS, use questions to block only the specific missing link in the requirements-to-workstream chain: workstream, role-specific dashboard attention, human surface graph node/edge, structured surface action, governed-tool identity/exposure, capability/API, Akka substrate, internal workstream agent graph delegation/result handling, AutonomousAgent task lifecycle, notification/projection, audit/work trace, or local validation.

## Goal

Create or update:

```text
specs/pending-questions.md
```

The queue should capture only questions that block safe compilation of current intent graph nodes into downstream meaning, architecture, specs, backlog, task generation, tests, security, observability, UI/API contracts, or implementation.

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
- `../core-saas-foundation/SKILL.md` for the mandatory secure SaaS baseline and the provider-uncertainty rule
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/requirements-to-workstream-development-process.md` when unresolved decisions affect workstreams, attention, dashboards, surface actions, capabilities, AutonomousAgent tasks, notifications/projections, or task materialization
- `../docs/workstream-expertise-model.md` when unresolved decisions affect LLM-backed functional-agent expertise, model binding, skills, references, manifests, `readSkill`, `readReferenceDoc`, tool boundaries, traces, or expertise surfaces
- `../docs/pending-question-queue.md`
- `../docs/pending-task-queue.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/web-ui-style-guide.md`
- `../ai-first-saas/SKILL.md`
- target project path: specs/README.md
- target project path: specs/pending-questions.md if it already exists
- target project path: specs/akka-solution-plan.md if present
- relevant target project path: specs/slices/*.md
- relevant target project path: specs/backlog/*-build-backlog.md
- relevant app-description files if the project uses description-first artifacts
- the user-provided PRD or requirements file when it is the source of the plan

Do not read the entire codebase. This skill is about planning decisions, not implementation details.

## Question discovery rules

Use `../docs/intent-compiler-skill-contracts.md` and `../docs/incremental-intent-processing.md` for the detailed question output contract. Return only the actionable summary, affected graph nodes/artifacts, required edits or queue changes, assumptions/questions, and next step. Preserve secure SaaS foundation, generated-SaaS runtime completion, tenant/customer scoping, backend authorization, governed agents/tools, traces, and tests when in scope.

## Final review checklist

Before finishing, verify:
- `specs/pending-questions.md` exists or no durable questions were needed
- Java source generation records and uses the fixed base package `ai.first`
- browser UI work has a selected style guide or a pending/deferred style-selection question that blocks only affected UI tasks
- every question identifies the current-intent node or realization step it blocks
- every question has a clear design impact
- AI-first questions are actionable blockers or meaningful quality decisions, not cosmetic prompts
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

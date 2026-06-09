---
name: akka-do-next-pending-question
description: Select the next actionable item from specs/pending-questions.md, ask one pending question or reconcile one answered question, and update the queue before continuing planning.
---

# Akka Do Next Pending Question

Use this skill when the user wants to work through the durable question queue one item at a time.

Typical user prompts:
- "what is the next pending question?"
- "ask the next question"
- "continue the design interview"
- "answer Q-003"
- "use the next pending question"
- "/do-next-pending-question"

This is a clarification queue skill, not an implementation skill.

## Goal

Reliably process one actionable item from:

```text
specs/pending-questions.md
```

The skill must:
- select the next `answered` question needing reconciliation, or the next askable pending question
- ask exactly one question when user input is needed
- record the user's answer when provided
- reconcile answered questions into relevant current-intent graph or planning artifacts when enough context exists
- update the question status before finishing
- report the next question or state that blocking questions are clear

## Required reading

Read these first if present:
- `../README.md`
- `../docs/pending-question-queue.md`
- `../docs/pending-task-queue.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/ai-first-saas-application-architecture.md` when the selected question involves delegated work, agents, approvals, exceptions, governance, audit, supervision UI, or outcomes
- `../docs/workstream-expertise-model.md` when the selected question involves LLM-backed functional-agent expertise, model binding, skill/reference governance, `readReferenceDoc`, manifests, loader authorization, tool boundaries, load traces, or expertise surfaces
- `../docs/web-ui-style-guide.md` when selected question is a UI style-guide question
- target project path: specs/pending-questions.md

Then read only the selected question's `source` and any artifacts needed to reconcile its answer.

Do not read the whole PRD unless the selected question requires it.
Do not read or edit application code unless reconciling the answer explicitly requires a planning artifact that references code state.

## Use this skill when

Use this skill when:
- `specs/pending-questions.md` exists and the user asks for the next question
- the user provides an answer to a named question
- an answered question needs to be reconciled into specs/app-description/backlog before more questions are asked
- pending task creation is blocked by unresolved design questions

Do **not** use this skill when:
- no question queue exists; use `akka-pending-question-generation`
- the user asks to execute implementation tasks; use `akka-do-next-pending-task`
- the user asks to audit many questions at once; use `akka-pending-question-queue-maintenance`
- the answer requires broad PRD replanning; use `akka-change-request-to-spec-update` or `akka-revised-prd-reconciliation` as appropriate

## Selection algorithm

Use `../docs/intent-compiler-skill-contracts.md` and `../docs/incremental-intent-processing.md` for the detailed one-question processing contract. Return only the actionable summary, affected graph nodes/artifacts, required edits or queue changes, assumptions/questions, and next step. Preserve secure SaaS foundation, generated-SaaS runtime completion, tenant/customer scoping, backend authorization, governed agents/tools, traces, and tests when in scope.

## Final review checklist

Before finishing, verify:
- exactly one question was processed unless the user explicitly requested a batch
- the queue status was updated
- answers were not treated as resolved until reconciled into current-intent or planning artifacts
- AI-first authority, policy, decision, trace, UI-surface, evaluation, and outcome semantics from the answer were preserved in reconciliation targets when relevant
- requirements-to-workstream semantics from the answer were preserved in reconciliation targets when relevant, including attention/dashboard/surface-graph/surface-action/governed-tool/capability-id/internal-agent-graph/autonomous-task notification context
- workstream-expertise/reference-governance semantics from the answer were preserved in reconciliation targets when relevant, including model-binding, manifests, `readReferenceDoc`, loader authorization, tool-boundary, load-trace, expertise-surface, default-content, and test context
- only relevant planning artifacts were edited
- no application implementation was started
- the next actionable question or next planning step was reported

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
- reconcile answered questions into relevant artifacts when enough context exists
- update the question status before finishing
- report the next question or state that blocking questions are clear

## Required reading

Read these first if present:
- `../README.md`
- `../docs/pending-question-queue.md`
- `../docs/pending-task-queue.md`
- `../docs/ai-first-saas-application-architecture.md` when the selected question involves delegated work, agents, approvals, exceptions, governance, audit, supervision UI, or outcomes
- `../docs/workstream-expertise-model.md` when the selected question involves LLM-backed functional-agent expertise, model binding, skill/reference governance, `readReferenceDoc`, manifests, loader authorization, tool boundaries, load traces, or expertise surfaces
- `../docs/web-ui-style-guide.md` when selected question is a UI style-guide question
- target project `specs/pending-questions.md`

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

If the user named a question ID:
1. find that question
2. process only that question
3. verify dependencies are satisfied, or report why it remains blocked

If the user did not name a question ID:
1. first select the earliest `answered` question not yet reconciled
2. otherwise ignore `resolved`, `deferred`, and `superseded`
3. skip `blocked` questions unless the blocker is now resolved
4. among `pending` and `asked` questions whose dependencies are resolved, select by priority: `blocking`, then `important`, then `optional`
5. preserve file order within the same priority
6. if no question is actionable, report why and do not invent a new one unless the user asks for queue maintenance/generation

## Asking workflow

When the selected question is `pending` or `asked` and the user has not already answered it:
1. update status to `asked`
2. ask exactly one question
3. include:
   - question ID and title
   - the question text
   - why it matters
   - options, if listed
   - default if deferred, if one exists
4. stop; do not ask the next question in the same response

## Answer-recording workflow

When the user provides an answer:
1. identify the matching question from the prompt or conversation
2. preserve AI-first meaning in the normalized answer when present: delegated work, retained authority, policies, approvals, risk thresholds, evidence, traces, UI surfaces, evaluations, and outcomes
3. preserve requirements-to-workstream meaning when present: workstream responsibility, attention category lifecycle, role-specific dashboard scope, human surface graph nodes/edges, surface action authority, governed-tool id/exposure, capability id/API exposure, internal workstream agent graph delegation/result behavior, AutonomousAgent lifecycle/result behavior, notification visibility, task result/progress surfaces, and human/agent worker assignment
4. preserve workstream-expertise meaning when present: model binding, governed prompt/skill/reference docs, compact manifests, `readSkill`/`readReferenceDoc` loader authorization, ToolPermissionBoundary, load traces, expertise surfaces, seed/import behavior, and tests
5. update:
   - `status: answered`
   - `answer: >` with the user's answer
   - `decision:` with the normalized decision if clear
   - `decision impact:` with the expected planning impact
   - `notes:` with provenance if useful
6. if the affected artifacts can be updated safely in this run, reconcile immediately and mark `resolved`
7. if reconciliation needs a separate planning pass, leave status `answered` and report what must be reconciled

## Reconciliation workflow

For an `answered` question:
1. read the artifacts listed under `source`, `blocks`, and `reconciled into` if present
2. update the smallest relevant authoritative artifacts:
   - app-description files for description-first projects, including `15-operating-model/` when the answer changes delegated work, authority, policies, approvals, decisions, traces, UI supervision, or outcomes
   - `specs/akka-solution-plan.md`
   - affected `specs/slices/*.md`
   - affected `specs/backlog/*.md`
   - `app-description/55-ui/style-guide.md` or `specs/cross-cutting/*ui-style-guide*.md` when reconciling a web UI style-guide answer
   - `specs/pending-tasks.md` only if it already exists and the decision changes tasks
3. when the answer resolves an AI-first blocker, carry the decision into affected backlog/task metadata so delegated authority, policy, decision-card, trace, UI-surface, evaluation, and outcome constraints remain visible to future implementation runs
4. when the answer resolves a requirements-to-workstream blocker, carry the decision into affected backlog/task metadata so workstream, attention, role-specific dashboard, human surface graph node/edge, surface action, governed-tool id/exposure, capability id, API/exposure, Akka substrate, internal workstream agent graph delegation/result surface, autonomous task lifecycle/notification/result surface, auth, trace, and test constraints remain visible to future implementation runs
5. when the answer resolves a workstream-expertise or reference-governance blocker, carry the decision into affected app-description/spec/backlog/task metadata so model binding, skill/reference manifests, authorized `readSkill`/`readReferenceDoc`, loader denials/redaction, tool boundaries, SkillLoadTrace/ReferenceLoadTrace/AgentWorkTrace, expertise surfaces, seed/import policy, and tests remain visible to future implementation runs
6. for web UI style-guide answers, write the selected AI-first style id/name, source reference, named-theme contract, available/default theme ids, My Account preference behavior when in scope, key token expectations, and brand adaptation notes into the authoritative style-guide artifact before resolving the question
7. update the question:
   - `status: resolved`
   - `decision:` final concise decision
   - `decision impact:` concrete artifact/component impact
   - `reconciled into:` list exact files updated
8. if the answer creates new dependent questions, append them or recommend `akka-pending-question-generation`

## Deferral and supersession

If the user defers a question:
- set `status: deferred`
- record the chosen default or limitation
- ensure blocked work is either still blocked or explicitly allowed to proceed using the default

If later planning makes a question irrelevant:
- set `status: superseded`
- note the decision or artifact that superseded it

## Final response shape

Use this shape after processing one question:

```md
# Pending Question Result

## Processed question
- question:
- status:

## Action taken
- asked / recorded answer / reconciled / deferred / blocked:

## Artifact updates
- ...

## Remaining impact
- ...

## Next pending question
- ...
```

If there are no blocking questions left, say whether it is safe to create or update `specs/pending-tasks.md`.

## Final review checklist

Before finishing, verify:
- exactly one question was processed unless the user explicitly requested a batch
- the queue status was updated
- answers were not treated as resolved until reconciled
- AI-first authority, policy, decision, trace, UI-surface, evaluation, and outcome semantics from the answer were preserved in reconciliation targets when relevant
- requirements-to-workstream semantics from the answer were preserved in reconciliation targets when relevant, including attention/dashboard/surface-graph/surface-action/governed-tool/capability-id/internal-agent-graph/autonomous-task notification context
- workstream-expertise/reference-governance semantics from the answer were preserved in reconciliation targets when relevant, including model-binding, manifests, `readReferenceDoc`, loader authorization, tool-boundary, load-trace, expertise-surface, seed, and test context
- only relevant planning artifacts were edited
- no application implementation was started
- the next actionable question or next planning step was reported

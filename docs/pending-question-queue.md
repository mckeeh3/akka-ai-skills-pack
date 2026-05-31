# Pending question queue

Use this contract when planning discovers unresolved decisions that would otherwise force the harness to guess before creating or safely executing implementation tasks.

Purpose:
- persist design, product, architecture, security, testing, and delivery questions across harness sessions
- preserve AI-first blockers when delegated work, authority, policies, decisions, supervision, audit, or outcomes would otherwise be guessed
- let the user answer one focused question at a time instead of receiving a large interview list
- record why each answer matters and which artifacts it affects
- distinguish answered questions from reconciled decisions
- gate `specs/pending-tasks.md` creation only where unresolved questions genuinely block implementation

## Canonical location

In a target application project, use:

```text
specs/pending-questions.md
```

This file belongs in the target project workspace, not inside the installed `.agents/` pack.

## Queue rules

1. Ask one question at a time unless the user explicitly requests a batch.
2. Preserve question IDs and history; do not renumber questions casually.
3. Prefer questions that block architecture, specs, backlog, task generation, or safe implementation.
4. For AI-first scope, add questions when work would otherwise guess delegated authority, human-only decisions, approval gates, policy/risk thresholds, evidence requirements, trace retention/visibility, supervision UI mode, evaluation/replay approach, or outcome metrics.
5. For generated full-stack AI-first SaaS, add focused blocking questions when implementation or task generation would otherwise guess affected workstream boundaries, role-specific dashboard purpose, attention item lifecycle, human surface graph nodes/edges, surface action authority, internal workstream agent graph delegation/result handling, governed-tool identity/exposure, or workstream expertise ownership.
6. For generated full-stack AI-first SaaS, add focused blocking questions when LLM-backed workstream-agent work would otherwise guess the workstream expert bundle: model binding, prompt/skill/reference ownership, compact expertise manifests, `readSkill`/`readReferenceDoc` loader authority, `ToolPermissionBoundary`, load traces, surfaces, or tests.
7. For generated full-stack AI-first SaaS, if no style guide is selected, add a `category: ui` style-selection question using `docs/web-ui-style-guide.md`; it should block web UI implementation/generation work until answered.
8. Do not create implementation tasks for work blocked by unresolved `blocking` questions.
9. A user answer moves a question to `answered`; the question becomes `resolved` only after affected artifacts are updated.
10. Defer questions only when the plan can safely proceed without that answer.
11. Mark questions `superseded` when later requirements or decisions make them irrelevant.
12. Keep questions short enough to answer without rereading the full PRD.
13. Include why the question matters and the expected design impact.
14. At the end of planning responses, report the next pending or answered question that needs attention.

## Status values

Use these exact status values:

- `pending` — not yet answered
- `asked` — asked in the current or recent interaction and awaiting user answer
- `answered` — user answered, but affected artifacts have not yet been reconciled
- `resolved` — answer has been reconciled into the relevant app-description/specs/backlogs/queue
- `blocked` — cannot be answered until another question, input, or artifact exists
- `deferred` — intentionally postponed and not blocking current safe progress
- `superseded` — no longer relevant because the plan changed or a later decision replaced it

## Priority values

Use these exact priority values:

- `blocking` — implementation planning or execution would require guessing without this answer
- `important` — affects quality, completeness, or later scope but does not block all current work
- `optional` — useful refinement that can safely wait

## Question ID format

Use stable, sortable question IDs:

```text
Q-001
Q-002
Q-003
```

For large multi-slice plans, a slice-prefixed ID is also acceptable:

```text
Q-01-001
Q-01-002
Q-02-001
```

Choose one format per project and keep it consistent.

## Required queue shape

Use this structure:

```md
# Pending Questions

## Queue rules

- Ask one question at a time unless the user requests a batch.
- Resolve `answered` questions by reconciling them into the relevant artifacts.
- Do not create or execute implementation tasks blocked by unresolved `blocking` questions.
- Preserve question IDs; supersede obsolete questions rather than deleting them.

## Questions

### Q-001: <short question title>

- status: pending
- priority: blocking
- category: <workstream|workstream-expertise|model-binding|skill-governance|reference-governance|tool-boundary|attention|dashboard|surface-graph|surface-action|governed-tool|capability|api|internal-agent-graph|autonomous-task-lifecycle|notification-visibility|task-result-surface|human-agent-worker-assignment|behavior|state-model|workflow|integration|security|authorization|policy-governance|approval|risk-evidence|audit-trace|outcomes|observability|testing|ui|data-retention|failure-handling|deployment>
- depends on: []
- blocks:
  - <artifact, decision, slice, backlog, or task generation area>
- source:
  - <PRD section, app-description file, spec, backlog, or plan>
- question: >
    <one focused question the user can answer directly>
- why it matters: >
    <how the answer changes architecture, behavior, tests, security, or implementation ordering>
- options:
  - A: <option>
  - B: <option>
  - C: <option>
- default if deferred: <safe default, or none>
- answer: none
- decision: pending
- decision impact: pending
- reconciled into:
  - none
- notes:
  - <optional provenance or constraints>
```

## Selection algorithm

To choose the next question:

1. Read `specs/pending-questions.md`.
2. First select the earliest `answered` question whose answer has not been reconciled; update affected artifacts before asking more.
3. Ignore questions with status `resolved`, `deferred`, or `superseded`.
4. Ignore `blocked` questions unless their blocker is now resolved.
5. Among remaining `pending` or `asked` questions, choose the first whose dependencies are resolved.
6. Prefer priority `blocking`, then `important`, then `optional`.
7. Ask exactly one question and include why it matters plus options when useful.

If no questions remain needing attention, report that the question queue is clear and proceed to the next planning step, such as creating `specs/pending-tasks.md`.

## Relationship to pending tasks

`specs/pending-questions.md` is the durable clarification queue.
`specs/pending-tasks.md` is the durable implementation queue.

Create or execute pending tasks only when either:
- no unresolved `blocking` questions affect that work, or
- the user explicitly deferred the blocking question and accepted the documented default or limitation.

A pending task may reference a question in notes, for example:

```md
- notes:
  - depends on decision from Q-003
```

If an implementation task discovers a missing decision, block the task and add or update a pending question instead of guessing. For AI-first work, missing authority, approval, policy, evidence, trace, supervision UI, evaluation, or outcome semantics are blockers for the affected work, not implementation details to invent. For generated full-stack AI-first SaaS, also create focused blocking questions when implementation would otherwise guess workstream responsibility, attention category lifecycle, role-specific dashboard scope, human surface graph node/edge behavior, surface action authority, governed-tool id/exposure channel, capability id/API exposure, internal workstream agent graph delegation/result handling, AutonomousAgent lifecycle/result handling, notification visibility, task result/progress surfaces, or human-vs-agent worker assignment. For LLM-backed workstream-agent work, unresolved workstream expertise, reference-governance, `readReferenceDoc`, model-binding, manifest, loader authorization, tool-boundary, load-trace, or expertise-surface decisions block the affected task until answered or explicitly deferred with scope impact.

## End-of-response reminder

When `specs/pending-questions.md` exists and questions remain, end planning responses with a short reminder:

```md
Pending questions remain.

Next question:
- Q-003: <title>

To continue, ask:
"Use akka-do-next-pending-question to ask the next pending question from specs/pending-questions.md."
```

## Related skills and docs

- `web-ui-style-guide.md`
- `../skills/akka-pending-question-generation/SKILL.md`
- `../skills/akka-do-next-pending-question/SKILL.md`
- `../skills/akka-pending-question-queue-maintenance/SKILL.md`
- `pending-task-queue.md`
- `solution-plan-to-implementation-queue.md`
- `intent-driven-usage-flow.md`

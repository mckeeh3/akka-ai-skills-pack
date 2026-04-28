# Pending question queue

Use this contract when planning discovers unresolved decisions that would otherwise force the harness to guess before creating or safely executing implementation tasks.

Purpose:
- persist design, product, architecture, security, testing, and delivery questions across harness sessions
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
4. If a browser UI is in scope and no style guide/theme is selected, add a `category: ui` style-selection question using `docs/web-ui-style-guide.md`; it should block only web UI implementation/generation work.
5. Do not create implementation tasks for work blocked by unresolved `blocking` questions.
6. A user answer moves a question to `answered`; the question becomes `resolved` only after affected artifacts are updated.
7. Defer questions only when the plan can safely proceed without that answer.
8. Mark questions `superseded` when later requirements or decisions make them irrelevant.
9. Keep questions short enough to answer without rereading the full PRD.
10. Include why the question matters and the expected design impact.
11. At the end of planning responses, report the next pending or answered question that needs attention.

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
- category: <capability|behavior|state-model|workflow|integration|security|authorization|observability|testing|ui|api|data-retention|failure-handling|deployment>
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

If an implementation task discovers a missing decision, block the task and add or update a pending question instead of guessing.

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

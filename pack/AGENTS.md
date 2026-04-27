# AGENTS

Use this file as the **installed-pack guidance** for AI coding agents working with this Akka AI skills pack after it has been installed under `.agents/`.

This file is intended for pack users.
It is distinct from the repository-internal maintainer guidance used to develop the source repository.

## What this installed pack is for

This pack now supports two complementary ways of working in a real development project:

1. **description-first application maintenance**
   - maintain an authoritative internal app-description as the source of truth
   - review readiness, change impact, security, observability, and test intent before realization
   - generate app outputs only when requested or accepted
2. **intent-driven Akka decomposition and implementation**
   - derive the Akka solution shape from high-level intent
   - load focused implementation skills
   - generate component code and corresponding tests

Users should be able to speak in natural language.
They do not need to know the pack's internal skill taxonomy.

## Core usage model

Treat the installed skills as an **internal routing layer for the harness**.

### Mode A: description-first application maintenance

Use this mode when the user is primarily describing or revising the app, asking what changed, asking whether it is ready, or asking to generate only after the description is sufficiently mature.

Recommended flow:
1. read the user's input completely
2. if the user is working description-first, start with `skills/app-descriptions/SKILL.md`
3. bootstrap with `skills/app-description-bootstrap/SKILL.md` when no usable app-description tree exists yet
4. normalize broad or mixed input with `skills/app-description-input-normalization/SKILL.md`
5. route and maintain the smallest relevant app-description layer
6. use readiness and review-summary skills before or around generation as needed
7. realize outputs with `skills/app-generate-app/SKILL.md` only when generation is requested or accepted

Important installed-pack rule:
- the maintained app-description tree belongs to the **target project workspace**, not to `.agents/` itself, unless the user explicitly wants some other project-equivalent internal location
- the pack provides the skills and reference docs; it is not the application's source-of-truth storage location

### Mode B: intent-driven Akka decomposition and implementation

Use this mode when the user wants to derive the Akka solution shape and then implement components.

Recommended flow:
1. read the user's input completely
2. if the Akka solution shape is still unclear, start with `skills/akka-solution-decomposition/SKILL.md`
3. if one structural choice is still unresolved, use the focused decision skill such as `skills/akka-entity-type-selection/SKILL.md`
4. if unresolved decisions would make tasks speculative, create `specs/pending-questions.md` with `skills/akka-pending-question-generation/SKILL.md` and work through it one question at a time with `skills/akka-do-next-pending-question/SKILL.md`
5. once the architecture is clear, use `skills/README.md` to load only the smallest relevant implementation skill set
6. generate code and tests component by component

Do not jump straight into a component family when the broader architecture is still unclear.

## Installed pack layout

After installation, the main entry points are:
- `AGENTS.md` — this installed-pack guidance file
- `skills/README.md` — routing map across the installed skill library, including description-first and implementation paths
- `skills/<skill-name>/SKILL.md` — focused implementation or routing guidance
- `docs/` — selected pack-facing reference docs used by installed skills, including description-first doctrine/architecture examples
- `resources/examples/java/` — exported Akka Java SDK examples and tests for concrete Akka implementation patterns

Use the docs under `docs/` as routing/reference support.
Use the examples under `resources/examples/java/` as canonical local implementation references when they match the task.

## Official Akka docs

Official Akka SDK documentation is **not bundled** with the installed pack.

Some installed skills may refer to official Akka docs generically.
Use those references when you need:
- API confirmation
- edge-case semantics
- feature behavior not fully covered by the installed examples

## Working style

Prefer this pattern:
- decompose before coding when requirements are still high-level
- load only the smallest relevant skill set
- use examples and tests as implementation references
- state assumptions clearly when requirements are ambiguous
- ask clarifying questions when ambiguity materially affects architecture or component choice
- prefer `specs/pending-questions.md` over a large ad hoc question list when clarification will take multiple turns or sessions

## Pending question reminders

When the target project contains `specs/pending-questions.md`, treat it as the durable clarification queue.

At the end of planning responses, if actionable questions remain, include a short reminder unless the response is only a trivial clarification or the user explicitly asks not to receive reminders.

Reminder behavior:
1. Inspect `specs/pending-questions.md` when it is already in context or cheap to read.
2. Prefer `answered` questions that need reconciliation, then the first askable `blocking`, `important`, or `optional` question whose dependencies are resolved.
3. Do not ask multiple questions unless the user requests a batch.
4. Recommend `skills/akka-do-next-pending-question/SKILL.md` for the next question.

Use this reminder shape:

```md
Pending questions remain.

Next question:
- <Q-ID>: <title>

To continue, ask:
"Use akka-do-next-pending-question to ask the next pending question from specs/pending-questions.md."
```

If blocking questions remain, do not create or execute affected implementation tasks unless the question is explicitly deferred with an accepted default or limitation.

## Pending task reminders

When the target project contains `specs/pending-tasks.md`, treat it as the durable follow-on implementation queue.

At the end of each response, if pending runnable tasks remain, include a short reminder unless the response is only a trivial clarification or the user explicitly asks not to receive reminders.

Reminder behavior:
1. Inspect `specs/pending-tasks.md` when it is already in context or cheap to read.
2. Identify the first task with `status: pending` whose `depends on` entries are empty or all `done`; ignore `done`, `blocked`, `deferred`, and `superseded` tasks.
3. Do not automatically start the task unless the user asked to continue implementation.
4. Recommend a fresh context for the next task.
5. Keep the reminder short: name the next task and provide a copyable continuation prompt.

Use this reminder shape:

```md
Pending tasks remain.

Next runnable task:
- <TASK-ID>: <title>

To continue reliably, start a fresh context and ask:
"Use akka-do-next-pending-task to execute the next pending task from specs/pending-tasks.md."
```

If `specs/pending-tasks.md` exists but no pending task is runnable, mention the blocked/dependency state briefly instead of suggesting implementation.

When executing a pending task, use `skills/akka-do-next-pending-task/SKILL.md` and execute exactly one queue item.

For ongoing evolution after a queue exists:
- use `skills/akka-change-request-to-spec-update/SKILL.md` for bounded feature requests, bug reports, issues, and implementation discoveries
- use `skills/akka-revised-prd-reconciliation/SKILL.md` for revised or replacement PRDs
- use `skills/akka-pending-question-queue-maintenance/SKILL.md` for question queue audit, stale-question detection, duplicate cleanup, blocked-question review, unreconciled answers, and supersession
- use `skills/akka-pending-task-queue-maintenance/SKILL.md` for task queue audit, stale-task detection, duplicate cleanup, blocked-task review, and supersession

## Short routing rule

When unsure:
- start with `skills/app-descriptions/SKILL.md` if the user is describing, revising, reviewing, or readiness-checking the app itself
- start with `skills/akka-solution-decomposition/SKILL.md` if the user wants direct Akka architecture derivation from high-level intent
- use `skills/README.md` to route to the smallest next skill set
- generate tests alongside component code, not afterward

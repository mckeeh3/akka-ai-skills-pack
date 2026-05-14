# Akka AI Skills Pack User Guide

This guide is for developers using the installed Akka AI Skills Pack to build or evolve an Akka application with an AI coding harness.

The pack is not an application framework you call directly. It is an opinionated guidance and routing library for the harness with one default product target: full-stack secure AI-first SaaS on Akka. You describe the product, behavior, bug, issue, or change in normal language; the harness uses the pack to maintain specs, queue decisions, develop sprints, create implementation tasks, write backend and frontend code, run checks, update task status, and carry the app forward over many sessions. The human role is supervisory: provide intent, answer questions, review outputs, approve or redirect, and manually test the resulting increments.

## Core idea

The pack is valuable for more than code generation. In the recommended description-first workflow, it helps the harness create and maintain a structured `app-description/` plus related `specs/` artifacts that capture the application's intent, behavior, goals, objectives, security model, UI expectations, tests, observability, governance, open questions, and implementation readiness.

These documents become a durable source of truth for both humans and AI harnesses. Developers supervise the work by asking questions such as “what is this app supposed to do?”, “why does this behavior exist?”, “what would this change impact?”, “which decisions are still open?”, “what should I test manually?”, or “is the implementation still aligned with the product intent?” The harness can answer from maintained project artifacts instead of inferring everything from code or stale chat history.

Use the skills pack as a durable, iterative development workflow driven by the harness and supervised by the human:

1. the human provides PRDs, specs, issues, or rough ideas
2. the harness ingests the input and queues clarifying questions instead of guessing
3. the human answers, defers, approves, or redirects decisions
4. the harness creates or updates app-description/spec artifacts
5. the harness plans vertical module sprints when the scope is large
6. the harness creates and maintains the pending task queue
7. the harness executes one task per fresh harness session and marks it completed or blocked
8. the human manually tests each sprint or feature increment
9. the harness reconciles findings, tweaks, issues, and revised specs back into the workflow
10. repeat until the application is functioning and accepted

Your durable project state should live in your application workspace. Human-supplied source input belongs under `docs/input/`; harness-maintained planning and derived intent belong under `app-description/` and `specs/`; implementation belongs under source directories and tests. The installed `.agents/` directory is the harness support library, not your app source.

## Install the pack

Current manifest version:
- `0.1.7`

Project install from the current directory:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.1.7/install-akka-ai-skills-pack-0.1.7.sh | bash -s --
```

Project install into a specific directory:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.1.7/install-akka-ai-skills-pack-0.1.7.sh | bash -s -- --target-dir /path/to/project
```

Install from an unpacked archive:

```bash
tar -xzf akka-ai-skills-pack-0.1.7.tar.gz
cd akka-ai-skills-pack-0.1.7
bash install.sh --location project --project /path/to/project
```

Global install from an unpacked archive or source checkout:

```bash
bash install.sh --location global
```

Interactive install:

```bash
bash install.sh
```

Dry run:

```bash
bash install.sh --location project --project /path/to/project --dry-run
```

A project install creates `.agents/` under the target project. A global install creates `~/.agents`.

## Getting started

After installing the pack into a new target project, you can ask your harness to bootstrap only the secure AI-first SaaS foundation:

```text
Create a new AI-first SaaS app with only the core foundation functionality:
secure tenant/customer/account model, WorkOS/JWT auth seam, email-invite onboarding,
admin user management, memberships/roles/capabilities, admin audit/search,
and AI-assisted admin offload. Do not add any domain-specific CRM/product features yet.
```

The harness should create or update planning artifacts first, queue questions instead of guessing, and only move to implementation when the plan is clear enough.

## Installed layout

A project install creates a directory like this:

```text
.agents/
├── AGENTS.md
├── docs/
├── manifests/
│   └── akka-ai-skills-pack.yaml
├── resources/
│   └── examples/
│       └── java/
│           ├── pom.xml
│           ├── README.md
│           └── src/
└── skills/
    ├── README.md
    ├── references/
    └── ...
```

Important files and directories:

- `.agents/AGENTS.md` — installed guidance for the harness when working in a real Akka project
- `.agents/docs/` — installed reference docs used by the harness
- `.agents/skills/` — internal routing and implementation guidance loaded by the harness
- `.agents/resources/examples/java/` — Akka Java SDK reference examples available to the harness
- `.agents/manifests/akka-ai-skills-pack.yaml` — installed manifest metadata

As a user, you usually interact with the harness rather than directly reading the installed skill files.

## Recommended project artifacts

For non-trivial apps, use a project layout like this:

```text
docs/
  input/                          # human-supplied PRDs, briefs, issue exports, revised specs, notes, diagrams
app-description/                  # harness-maintained description-first source of truth for non-trivial apps
specs/                            # harness-maintained derived plans, queues, sprints, backlogs, and task artifacts
  akka-solution-plan.md
  pending-questions.md
  pending-tasks.md
  modules/
  sprints/
  backlog/
  tasks/
src/
```

`docs/input/` is the handoff directory for human-authored or externally sourced material that the harness should ingest. Put initial PRDs, product briefs, API sketches, UI briefs, revised PRDs, customer notes, exported tickets, meeting notes, diagrams, and other source material there. Organize it with subdirectories when useful, such as `docs/input/initial/`, `docs/input/revisions/`, or `docs/input/testing/`.

Treat `app-description/` and `specs/` as strictly harness-maintained outputs. The human changes them by asking the harness to reconcile new input, answers, approvals, test findings, or corrections. Avoid manually editing `specs/` as if it were an input drop zone; the harness owns its contents, consistency, task statuses, and cross-references.

The `app-description/` tree is the best place for the harness to preserve product meaning over time: capabilities, user-visible behavior, security and tenant boundaries, UI surfaces, acceptance criteria, audit/observability expectations, AI-first governance, and readiness for generation. `specs/` then carries harness-maintained planning, decomposition, sprint, backlog, question, and task artifacts derived from or reconciled with that description and the source inputs in `docs/input/`.

Small apps may need fewer files. Large apps benefit from module and sprint specs so implementation stays organized and testable.

## Phase 1: Ingest the initial PRD or spec

Start by placing the best available source of intent under `docs/input/`, then point the harness at those files. This can be a full PRD, a rough idea captured in a note, a ticket export, a UI brief, an existing external spec, or several files.

Example prompts:

```text
Read docs/input/order-management-prd.md and use the Akka skills pack to plan this application. Queue questions instead of guessing.
```

```text
Read docs/input/initial-product-idea.md. Create the app description and implementation planning artifacts needed to build it as an Akka application.
```

```text
Read docs/input/api-sketch.md and docs/input/ui-brief.md together. Produce a solution plan, identify open questions, and prepare the backlog only for decisions that are clear.
```

For early or ambiguous requirements, ask the harness to maintain an app description first. For clearer implementation-ready specs, ask it to produce an Akka solution plan and backlog.

## Phase 2: Resolve pending questions

The pack encourages the harness to record open decisions instead of silently inventing product behavior. Questions usually live in:

```text
specs/pending-questions.md
```

Work through them one at a time.

Example prompts:

```text
What is the next pending question?
```

```text
Answer Q-003: guest checkout is allowed only for digital goods. Update the specs and question queue.
```

```text
Defer this question. Use the simplest safe default, record the limitation, and unblock only the tasks that can safely proceed.
```

Good answers are specific enough to update behavior, security, UI, tests, or implementation tasks. If you are unsure, explicitly defer the decision with a safe default rather than letting the harness guess.

## Phase 3: Plan module sprints

For larger apps, ask the harness to develop vertical module sprints rather than one giant backlog or layer-only work such as “all entities” followed by “all UI.” The skills pack guides the harness to define sprint scope, sequencing, dependencies, and testable increments. The human supervises by approving, changing, or reprioritizing the proposed sprint plan; the human does not need to manually decompose the work into sprints.

A good sprint produces something demonstrable and testable for one capability area.

Example prompt:

```text
Turn the approved solution plan into module-oriented sprints. Each sprint should be a vertical, testable increment with backend, API, frontend, tests, pending questions, and deferred items where applicable.
```

A sprint plan should make clear:

- sprint goal
- parent module or capability
- backend components
- API or integration surfaces
- frontend screens if applicable
- acceptance behavior
- manual test paths
- automated checks
- deferred work
- implementation tasks

## Phase 4: Create the pending task queue

Implementation work should be queued by the harness in:

```text
specs/pending-tasks.md
```

`specs/pending-tasks.md` is the harness execution queue. The harness creates, refreshes, and maintains it from the approved app description, solution plan, sprint plan, backlog, questions, and current implementation state. Each task should be small enough for one harness session and should include required reads, expected outputs, checks, dependencies, and done criteria.

Example prompt:

```text
Create or refresh specs/pending-tasks.md from the approved sprint plan. Make each task executable in a fresh harness context and include required reads and checks.
```

For large projects, task IDs may be sprint-prefixed, such as `TASK-02-001`, so the next runnable task remains obvious.

Task status changes are also harness-owned. During execution the harness should update each task to completed when its done criteria and checks are satisfied, or blocked when a dependency, missing decision, failing check, or external issue prevents safe completion. The human supervises these changes by reviewing the harness report, answering questions, and deciding whether to approve, defer, reprioritize, or request follow-up work.

## Phase 5: Execute implementation tasks

Default rule: the harness executes one pending task per fresh harness session.

Use a fresh session prompt like:

```text
Use the Akka skills pack to execute the next pending task from specs/pending-tasks.md. Execute only that one task, load only its required reads and listed skills, update its status when finished, and report the next runnable pending task.
```

For a specific task:

```text
Use the Akka skills pack to execute TASK-02-003 from specs/pending-tasks.md in a fresh context. Do not work on any other queue item. Update the queue before finishing.
```

This is usually more efficient than doing an entire sprint in one long harness session because it avoids context bloat, keeps changes reviewable, and makes failures or blocked work easier to recover from. At the end of the session, the harness should report what it changed, which checks ran, whether the task is completed or blocked, and what task is next runnable.

## Phase 6: Review and manually test the sprint

After the runnable tasks for a sprint are complete, ask the harness to summarize what changed and how to test it. Then perform manual testing through the relevant surfaces: API, UI, integrations, or local app behavior.

Example prompts:

```text
Summarize sprint 02: completed tasks, changed files, automated checks, deferred items, and manual test instructions.
```

```text
Create a manual test checklist for the purchase request sprint, including happy path, validation failures, authorization failures, idempotent repeats, and observable logs or metrics.
```

```text
I manually tested sprint 02. These scenarios failed: ... Reconcile these findings with the specs and create follow-up tasks.
```

Manual testing findings should become normal change input. Save substantial findings under `docs/input/testing/` when they span multiple scenarios or need durable provenance, then ask the harness to reconcile them. The harness should update specs, questions, and tasks before coding fixes unless the fix is tiny and unambiguous.

## Phase 7: Iterate on features, tweaks, and issues

After the app exists, keep using the same supervised harness loop for every meaningful change:

1. the human provides the new issue, feature request, bug report, or tweak
2. the harness reconciles it with existing app-description/specs
3. the harness queues any new questions; the human answers or defers them
4. the harness refreshes affected sprint/backlog/task files
5. the harness executes one task per fresh session and updates task status
6. the human tests the increment and repeats the loop with any findings

Example prompts:

```text
Read docs/input/revisions/partial-shipment-refund-change.md. Reconcile it with the current specs, identify impacted tasks or completed work, and queue implementation changes before coding.
```

```text
Read docs/input/revisions/support-dashboard-filter-export.md. Update the UI/API specs, add acceptance criteria, and create the next pending tasks.
```

```text
Read docs/input/bugs/duplicate-approval-500.md. Expected behavior is idempotent success. Update the behavior spec, tests, and task queue.
```

```text
Apply this small copy tweak to the UI and update any tests if needed. If no planning changes are necessary, explain why.
```

## Prompt patterns by situation

### Start a new app

```text
Use the Akka skills pack to ingest docs/input/product-prd.md. Create or update app-description/specs, queue questions instead of guessing, and do not start coding yet.
```

### Move from planning to build tasks

```text
Using the approved specs, create module sprints and specs/pending-tasks.md. Tasks should be fresh-context sized and include required checks.
```

### Continue implementation

```text
Use the Akka skills pack to do the next pending task from specs/pending-tasks.md. Only do one task.
```

### Finish a sprint

```text
Review the current sprint status. If all runnable tasks are done, produce a sprint completion summary and manual test checklist. If not, report the next runnable task.
```

### Feed back manual testing

```text
Manual test results for sprint 03: <results>. Update specs and queues, mark any affected tasks blocked or completed as appropriate, and create follow-up tasks for confirmed issues.
```

### Add a feature after the app is working

```text
Read docs/input/revisions/<feature-request-file>.md. Reconcile it with the existing app description and specs, identify impacted modules, queue questions, and prepare implementation tasks.
```

### Fix a bug

```text
Read docs/input/bugs/<bug-report-file>.md. Determine the intended behavior from the specs, update tests/specs if the behavior is missing, and create the smallest safe implementation task.
```

## Practical operating rules

- Speak in product and engineering terms; you do not need to name internal skills.
- Ask the harness to queue questions instead of guessing.
- Put human-authored source input under `docs/input/`.
- Treat `app-description/` and `specs/` as harness-maintained outputs, not human-edited input folders.
- Keep planning artifacts in your app workspace, not in `.agents/`.
- Ask the harness to create vertical module sprints for large apps.
- Ask the harness to execute one pending task per fresh session.
- Do not ask the harness to combine unrelated pending tasks just because they touch nearby files.
- Let the harness update task status; it should mark tasks completed only after required checks pass, or blocked when safe completion is not possible.
- Treat manual testing output as first-class input to the next planning/update cycle.
- When requirements change, reconcile specs before coding broad changes.

## Minimal end-to-end example

```text
User: Read docs/input/returns-prd.md and plan this Akka app. Queue questions instead of guessing.
Harness: Created specs/akka-solution-plan.md and specs/pending-questions.md.

User: What is the next question?
Harness: Should warehouse users be allowed to override an expired return window?

User: Yes, but only supervisors, and overrides require a reason.
Harness: Updated specs. Two questions remain.

User: Create module sprints and pending tasks from the approved plan.
Harness: Created specs/sprints/, specs/backlog/, and specs/pending-tasks.md.

User: Use the Akka skills pack to execute the next pending task. Only do one task.
Harness: Implemented TASK-01-001, ran checks, marked it done, and reported TASK-01-002 as next.

User: <new session> Use the Akka skills pack to execute the next pending task. Only do one task.
...

User: Summarize sprint 01 and create a manual test checklist.
Harness: Provides completed work, checks, deferred items, and manual test paths.

User: Manual test found that duplicate approvals show the wrong message. Update specs and queue a fix.
Harness: Updates behavior/tests and creates a focused follow-up task.
```

The important habit is to keep durable state in specs, queues, code, and tests, while using fresh harness sessions to execute bounded increments.

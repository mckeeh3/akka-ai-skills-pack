# Akka AI Skills Pack User Guide

This guide is for developers using the installed Akka AI Skills Pack to build or evolve an Akka application with an AI coding harness.

The pack is not an application framework you call directly. It is a guidance and routing library for the harness. You describe the product, behavior, bug, issue, or change in normal language; the harness uses the pack to maintain specs, queue decisions, create implementation tasks, write code, run checks, and carry the app forward over many sessions.

## Core idea

Use the skills pack as a durable, iterative development workflow:

1. ingest PRDs, specs, issues, or rough ideas
2. clarify decisions without guessing
3. create or update app-description/spec artifacts
4. plan vertical module sprints when the scope is large
5. create a pending task queue
6. execute one task per fresh harness session
7. manually test each sprint or feature increment
8. feed findings, tweaks, issues, and revised specs back into the same workflow
9. repeat until the application is functioning and accepted

Your durable project state should live in your application workspace, usually under `specs/`, `app-description/`, source directories, and tests. The installed `.agents/` directory is the harness support library, not your app source.

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

For non-trivial apps, expect the harness to create or maintain files like:

```text
app-description/                 # optional description-first source of truth
specs/
  akka-solution-plan.md
  pending-questions.md
  pending-tasks.md
  modules/
  sprints/
  backlog/
  tasks/
src/
```

Small apps may need fewer files. Large apps benefit from module and sprint specs so implementation stays organized and testable.

## Phase 1: Ingest the initial PRD or spec

Start by pointing the harness at the best available source of intent. This can be a full PRD, a rough idea, a ticket, a UI brief, an existing spec, or several files.

Example prompts:

```text
Read docs/order-management-prd.md and use the Akka skills pack to plan this application. Queue questions instead of guessing.
```

```text
Here is the initial product idea. Create the app description and implementation planning artifacts needed to build it as an Akka application.
```

```text
Read docs/api-sketch.md and docs/ui-brief.md together. Produce a solution plan, identify open questions, and prepare the backlog only for decisions that are clear.
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

For larger apps, prefer vertical module sprints rather than one giant backlog or layer-only work such as “all entities” followed by “all UI.” A good sprint produces something demonstrable and testable for one capability area.

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

Implementation work should be queued in:

```text
specs/pending-tasks.md
```

Each task should be small enough for one harness session and should include required reads, expected outputs, checks, dependencies, and done criteria.

Example prompt:

```text
Create or refresh specs/pending-tasks.md from the approved sprint plan. Make each task executable in a fresh harness context and include required reads and checks.
```

For large projects, task IDs may be sprint-prefixed, such as `TASK-02-001`, so the next runnable task remains obvious.

## Phase 5: Execute implementation tasks

Default rule: execute one pending task per fresh harness session.

Use a fresh session prompt like:

```text
Use the Akka skills pack to execute the next pending task from specs/pending-tasks.md. Execute only that one task, load only its required reads and listed skills, update its status when finished, and report the next runnable pending task.
```

For a specific task:

```text
Use the Akka skills pack to execute TASK-02-003 from specs/pending-tasks.md in a fresh context. Do not work on any other queue item. Update the queue before finishing.
```

This is usually more efficient than doing an entire sprint in one long harness session because it avoids context bloat, keeps changes reviewable, and makes failures or blocked work easier to recover from.

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

Manual testing findings should become normal change input. The harness should update specs, questions, and tasks before coding fixes unless the fix is tiny and unambiguous.

## Phase 7: Iterate on features, tweaks, and issues

After the app exists, keep using the same loop for every meaningful change:

1. provide the new issue, feature request, bug report, or tweak
2. ask the harness to reconcile it with existing app-description/specs
3. queue or answer any new questions
4. refresh affected sprint/backlog/task files
5. execute one task per fresh session
6. test and repeat

Example prompts:

```text
This issue changes refund behavior for partially shipped orders. Reconcile it with the current specs, identify impacted tasks or completed work, and queue implementation changes before coding.
```

```text
The support dashboard needs a new filter and export action. Update the UI/API specs, add acceptance criteria, and create the next pending tasks.
```

```text
Bug: approving a request twice returns a 500. Expected behavior is idempotent success. Update the behavior spec, tests, and task queue.
```

```text
Apply this small copy tweak to the UI and update any tests if needed. If no planning changes are necessary, explain why.
```

## Prompt patterns by situation

### Start a new app

```text
Use the Akka skills pack to ingest docs/product-prd.md. Create or update app-description/specs, queue questions instead of guessing, and do not start coding yet.
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
Manual test results for sprint 03: <results>. Update specs and queues, mark anything blocked or done as appropriate, and create follow-up tasks for confirmed issues.
```

### Add a feature after the app is working

```text
Here is a new feature request: <request>. Reconcile it with the existing app description and specs, identify impacted modules, queue questions, and prepare implementation tasks.
```

### Fix a bug

```text
Here is a bug report: <report>. Determine the intended behavior from the specs, update tests/specs if the behavior is missing, and create the smallest safe implementation task.
```

## Practical operating rules

- Speak in product and engineering terms; you do not need to name internal skills.
- Ask the harness to queue questions instead of guessing.
- Keep planning artifacts in your app workspace, not in `.agents/`.
- Prefer vertical module sprints for large apps.
- Prefer one pending task per fresh harness session.
- Do not combine unrelated pending tasks just because they touch nearby files.
- Mark tasks done only after required checks pass or are explicitly not runnable.
- Treat manual testing output as first-class input to the next planning/update cycle.
- When requirements change, reconcile specs before coding broad changes.

## Minimal end-to-end example

```text
User: Read docs/returns-prd.md and plan this Akka app. Queue questions instead of guessing.
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

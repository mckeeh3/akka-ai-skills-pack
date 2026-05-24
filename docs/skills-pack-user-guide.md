# Akka AI Skills Pack User Guide

This guide is for developers using the installed Akka AI Skills Pack to build or evolve an Akka application with an AI coding harness.

The pack is not an application framework you call directly. It is an opinionated guidance and routing library for the harness with one default product target: full-stack secure AI-first SaaS on Akka, implemented through capability-first backend architecture. You describe the product, behavior, bug, issue, or change in natural language by placing source input files under `docs/input/` and prompting the harness to review new or changed input. The harness uses the pack to maintain specs, queue decisions, develop sprints, create implementation tasks, write backend and frontend code, run checks, update task status, and carry the app forward over many sessions. The human role is supervisory: provide intent, answer questions, review outputs, approve or redirect, and manually test the resulting increments.

## Core idea

The pack is valuable for more than code generation. In the recommended description-first workflow, it helps the harness create and maintain a structured `app-description/` plus related `specs/` artifacts that capture the application's intent, behavior, goals, objectives, governed backend capabilities, security model, UI expectations, tests, observability, governance, open questions, and implementation readiness.

Capability-first means the harness should model backend behavior as named operations or queries with explicit actors/AuthContext, tenant/customer scope, input/output schemas, side effects, idempotency, audit/work traces, approval policy, exposure surfaces, and tests before choosing Akka components or exposing agent tools. Agent tools, MCP tools/resources, APIs, browser actions, workflow steps, timers, consumers, views, and component methods are selected surfaces of a capability, not authorization controls by themselves.

These documents become a durable source of truth for both humans and AI harnesses. Developers supervise the work by asking questions such as “what is this app supposed to do?”, “why does this behavior exist?”, “what would this change impact?”, “which decisions are still open?”, “what should I test manually?”, or “is the implementation still aligned with the product intent?” The harness can answer from maintained project artifacts instead of inferring everything from code or stale chat history.

Use the skills pack as a durable, iterative pair-programming workflow driven by the harness and supervised by the human. The harness has the hands on the keyboard; the human drives with natural-language intent, decisions, review, and testing feedback.

1. the human adds or updates PRDs, specs, issues, rough ideas, test findings, or decision notes under `docs/input/`
2. the human prompts the harness to review the new or modified `docs/input/` files
3. the harness ingests the input and queues clarifying questions instead of guessing
4. the human records answers, deferrals, approvals, or redirects under `docs/input/` and prompts the harness to review them
5. the harness creates or updates app-description/spec artifacts
6. the harness plans vertical module sprints when the scope is large
7. the harness creates and maintains the pending task queue
8. the harness executes one task per fresh harness session and marks it completed or blocked
9. the harness writes and updates all generated backend and frontend source code and tests
10. the human manually tests each sprint or feature increment
11. the harness reconciles findings, tweaks, issues, and revised input docs back into the workflow
12. repeat until the application is functioning and accepted

Your durable project state should live in your application workspace. Human-supplied source input belongs only under `docs/input/`; harness-maintained planning and derived intent belong under `app-description/` and `specs/`; harness-generated implementation belongs under source directories and tests. The installed `.agents/` directory is the harness support library, not your app source.

## Install the pack

Current manifest version:
- `0.2.8`

Project install from the current directory:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.2.8/install-akka-ai-skills-pack-0.2.8.sh | bash -s --
```

Project install into a specific directory:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.2.8/install-akka-ai-skills-pack-0.2.8.sh | bash -s -- --target-dir /path/to/project
```

Install from an unpacked archive:

```bash
tar -xzf akka-ai-skills-pack-0.2.8.tar.gz
cd akka-ai-skills-pack-0.2.8
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

A project install creates `.agents/` under the target project. A global install creates `~/.agents`. Both modes are skills/resource installs only by default; they do not copy starter application code into the project root.

After a project install, explicitly scaffold the starter into an empty or bootstrap-only project when you want the packaged five core v0 starter app:

```bash
.agents/bin/scaffold-ai-first-saas-starter.sh \
  --target /path/to/project \
  --app-name "My App" \
  --base-package ai.first
```

Use `--dry-run` first to inspect rendered paths and conflicts. If `--base-package` is omitted, the command prompts for the Java base package; pressing Enter uses `ai.first`. The scaffold command refuses existing application files by default and writes `specs/scaffold-report.md` after a successful run.

The scaffold includes backend source and the React/Vite workstream frontend under `frontend/`. It also writes `.env.example`. Copy it to `.env` for local manual testing that needs real providers:

```bash
cp .env.example .env
set -a
source .env
set +a
```

Backend-only variables include `WORKOS_API_KEY`, `WORKOS_API_BASE_URL`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, `ADMIN_USERS`, `APP_PUBLIC_BASE_URL`, `RESEND_API_KEY`, `RESEND_FROM_EMAIL`, `INVITE_EMAIL_FROM`, `INVITE_EMAIL_SUBJECT`, `RESEND_API_BASE_URL`, and model-provider variables such as `OPENAI_API_KEY`, model id, endpoint, and timeout when workstream agents are model-backed. The frontend build uses browser-public `VITE_WORKOS_CLIENT_ID` and `VITE_WORKOS_REDIRECT_URI`. Never put backend secrets in frontend env files or built assets. Generated Java config loaders should log each missing required backend env var as an error with the exact env var name and no secret value. Generated Akka apps should load `ADMIN_USERS` from the service's single `@Setup` `ServiceSetup.onStartup()` bootstrap path, with idempotent startup behavior for repeated service-instance starts. Missing provider configuration should block provider-backed message submission with an actionable error instead of silently returning deterministic placeholder text.

Initial scaffold validation commands:

```bash
mvn test
cd frontend
npm install
npm test -- --run
npm run typecheck
npm run build
cd ..
mvn compile exec:java
```

When the workstream-agent runtime is implemented, also run a manual real-model smoke before calling the five core v0 app functional: load backend-only provider variables from `.env`, sign in, select My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy, submit one prompt in each workstream, verify provider-backed `markdown_response` output plus prompt/model/work traces, and confirm no provider secret appears in `/api/me`, workstream payloads, frontend env, built assets, or trace displays. Repeat once with provider variables unset and verify submission is blocked with an actionable provider-configuration error rather than deterministic fallback text.

After `npm run build`, Akka serves the workstream UI from `/`, `/ui`, `/workstream`, and `/assets/**`; protected data and actions still go through JWT-secured `/api/...` endpoints.

### Choose skills-only vs starter scaffold

Use **skills-only install** when you already have an application, want the harness to plan from PRDs before code exists, or only need pack guidance in a repository. In this mode `.agents/` is the support library and the harness creates or updates `app-description/`, `specs/`, source, and tests in your project workspace.

Use **starter scaffold** for a new secure AI-first SaaS app when you want the packaged five core v0 shell as the first implementation baseline. Scaffold into an empty or bootstrap-only project, commit the scaffolded files, make My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy functional with real model-backed `markdown_response` behavior, then roll out the full core workstreams from the packaged core-app domain PRDs under `.agents/docs/examples/ai-first-saas-core-app-domain/`. Copy those PRDs into the project workspace, for example `docs/input/core-app-domain/`, before asking the harness to plan and execute the workstream rollout one task at a time. After the core foundation is usable, extend by adding product input under `docs/input/` and asking the harness to reconcile that input into the existing `app-description/`, `specs/`, and `specs/pending-tasks.md` queue. A scaffolded project is identifiable by `specs/scaffold-report.md`; the harness should treat the starter code and descriptions as the extension base rather than regenerating a separate app from scratch.

## Getting started: build the core app from PRD inputs

For scaffolded starter projects, the preferred full-core rollout input is the workstream-oriented core-app domain PRD set under `.agents/docs/examples/ai-first-saas-core-app-domain/`. Use it after the five core v0 shell is running locally: copy it into `docs/input/core-app-domain/`, ask the harness to create/update `specs/pending-tasks.md`, then implement My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy one workstream at a time through real local Akka runtime behavior.

For broader skills-pack release testing, the older module-sequenced core input set under `docs/examples/core-ai-first-saas-input/` can still be used as an end-to-end sample: PRD inputs should drive app-description/specs, module/sprint backlogs, implementation tasks, generated Akka + React code, tests, and manually testable live-app increments.

Recommended release-test loop:

1. In this skills-pack source repository, update the core input PRDs under `docs/examples/core-ai-first-saas-input/`.
2. Create a versioned skills-pack release.
3. Create a fresh target project outside the skills-pack repository.
4. Install the released pack into the target project as `.agents/`.
5. Copy the installed PRD inputs into the target project's `docs/input/initial/`.
6. Ask the harness to create planning artifacts first, not code.
7. Resolve pending questions.
8. Execute one pending implementation task per fresh harness session.
9. At the end of each sprint, run Akka locally and manually test the visible app feature(s).
10. Record manual test findings under `docs/input/testing/` and ask the harness to reconcile them.

Copy the installed core PRD input set into a fresh target project:

```bash
mkdir -p docs/input/initial
cp .agents/docs/examples/core-ai-first-saas-input/*.md docs/input/initial/
cat > docs/input/initial/scope-choice.md <<'EOF'
# Core app scope choice

Selected scope: full core

Use the full core PRD sequence. Full core includes:
- Module 1: Minimal Auth and App Access MVP
- Module 2: Agent Workstream Runtime Bootstrap
- Module 3: User Administration
- Module 4: Agent Definition Foundation
- Module 5: Prompt Governance
- Module 6: Skill Governance
- Module 7: Audit and Work Trace
- Module 8: Evaluation and Closed-Loop Improvement

The agent runtime bootstrap must happen before full User Administration because
User Admin is a functional-agent workstream, not a page-first CRUD console.
EOF
```

Then ask your harness to bootstrap planning artifacts from those inputs:

```text
First read .agents/AGENTS.md and .agents/skills/README.md.
Then read all files under docs/input/initial/.

Use the installed core PRDs as the source input for a full-core secure AI-first SaaS app.
Record the selected scope as full core in app-description/specs. Preserve the module order:
1 Minimal Auth and App Access, 2 Agent Workstream Runtime Bootstrap, 3 User Administration,
4 Agent Definition Foundation, 5 Prompt Governance, 6 Skill Governance, 7 Audit and Work Trace,
8 Evaluation and Closed-Loop Improvement.

Bootstrap or update the app-description, solution plan, module specs, sprint specs,
pending questions, and pending task queue. Do not generate application source code yet.
Queue questions instead of guessing. Each sprint must produce live-app behavior that can
be manually tested with Akka running locally. Do not treat a full core app as complete
unless User Admin and Agent Admin functional agents are included and backed by the
agent workstream/runtime, authorization, audit, and tests described in the PRDs.
```

Full core is the canonical PRD-backed target. If User Admin or Agent Admin is deferred, the selected scope must be recorded as `Module 1-only / not full core` or another explicitly named narrower scope rather than full core. The harness should also ask for the Java base package before it generates Java code: "What Java base package should I use for generated code? Press Enter to use `ai.first`." If you accept or defer, `ai.first` is used. `com.example` appears only in reference examples and is not the generated-code default. The harness should create or update planning artifacts first, queue questions instead of guessing, and only move to implementation when the plan is clear enough.

### Core app module order

The full-core PRD input set is intentionally multi-module. The important ordering constraint is that the agent runtime bootstrap comes before full User Administration:

| Module | Purpose | Manual-test target |
|---|---|---|
| 1. Minimal Auth and App Access MVP | WorkOS/AuthKit seam, `/api/me`, selected AuthContext, shell, Access/Profile. | Sign in, enter shell, inspect context, see safe no-access/forbidden/disabled states. |
| 2. Agent Workstream Runtime Bootstrap | Seeded AgentDefinitions, prompts, skills, manifests, tool boundaries, runtime resolver, composer invocation, traces. | Select Access/Profile or User Admin bootstrap agent and get a governed backend-agent-runtime-backed workstream response; if model-backed, verify configured provider invocation or an actionable fail-closed provider error. |
| 3. User Administration | Invitations, captured outbox/Resend boundary, memberships, roles, disable/reactivate, access review, admin audit. | Invite/resend/revoke/accept users and manage memberships through User Admin workstream surfaces. |
| 4. Agent Definition Foundation | Durable AgentDefinition lifecycle and Agent Admin catalog/detail. | Create/view/update/activate/disable/archive agent definitions. |
| 5. Prompt Governance | Prompt documents, versions, diff/review/activation, prompt assembly/test console. | Draft/review/activate a prompt and run a safe prompt-backed test. |
| 6. Skill Governance | Skill documents, versions, manifests, authorized `readSkill(skillId)`. | Assign a skill and observe allowed/denied skill loading. |
| 7. Audit and Work Trace | Tenant-scoped audit/trace search, timelines, redaction, correlation. | Investigate correlated admin, prompt, skill, tool, and denial activity. |
| 8. Evaluation and Closed-Loop Improvement | Evaluations, findings, proposals, approvals, activation/rollback. | Turn a finding into a reviewed proposal and governed behavior change. |

## Installed layout

A project install creates a directory like this:

```text
.agents/
├── AGENTS.md
├── bin/
│   └── scaffold-ai-first-saas-starter.sh
├── docs/
├── manifests/
│   └── akka-ai-skills-pack.yaml
├── resources/
│   ├── templates/
│   │   └── ai-first-saas-starter/
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
- `.agents/resources/templates/ai-first-saas-starter/` — read-only starter scaffold source
- `.agents/bin/scaffold-ai-first-saas-starter.sh` — explicit starter scaffold command; default installs never run it automatically
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

`docs/input/` is the only normal handoff directory for human-authored or externally sourced material that the harness should ingest. Put initial PRDs, product briefs, API sketches, UI briefs, revised PRDs, customer notes, exported tickets, meeting notes, diagrams, manual test results, and other source material there. Organize it with subdirectories when useful, such as `docs/input/initial/`, `docs/input/revisions/`, `docs/input/bugs/`, or `docs/input/testing/`.

Treat every other project artifact as harness-maintained. The human changes `app-description/`, `specs/`, backend source, frontend source, tests, and generated assets by asking the harness to reconcile new input, answers, approvals, test findings, or corrections. Do not manually edit `app-description/`, `specs/`, or source code as the normal workflow; the harness owns their contents, consistency, task statuses, cross-references, and implementation alignment.

Think of `app-description/` and `specs/` as source code written by the harness for the product intent, and think of generated backend/frontend code as the compiled binary produced from that maintained intent. Even though the generated application code is text, it should be treated operationally like binary output: reviewed, tested, and corrected through harness-driven changes rather than hand-edited by the human.

The `app-description/` tree is the best place for the harness to preserve product meaning over time: capabilities, user-visible behavior, security and tenant boundaries, UI surfaces, acceptance criteria, audit/observability expectations, AI-first governance, and readiness for generation. `specs/` then carries harness-maintained planning, decomposition, sprint, backlog, question, and task artifacts derived from or reconciled with that description and the source inputs in `docs/input/`.

Small apps may need fewer files. Large apps benefit from module and sprint specs so implementation stays organized and testable.

## Phase 1: Ingest the initial PRD or spec

Start by placing the best available source of intent under `docs/input/`, then prompt the harness to review the new or modified files. This can be a full PRD, a rough idea captured in a note, a ticket export, a UI brief, an existing external spec, or several files. Do not paste large evolving requirements into `specs/`, `app-description/`, or source files; deposit them under `docs/input/` and let the harness reconcile them.

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
Read docs/input/decisions/Q-003-guest-checkout.md and update the specs and question queue.
```

```text
Read docs/input/decisions/Q-004-deferral.md. Use the documented safe default, record the limitation, and unblock only the tasks that can safely proceed.
```

Good answers are specific enough to update behavior, security, UI, tests, or implementation tasks. Record answers and deferrals under `docs/input/decisions/`, then prompt the harness to reconcile them. If you are unsure, explicitly defer the decision with a safe default rather than letting the harness guess.

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
- functional-agent workstream surfaces and route/deep-link details if applicable
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

Completion rule: the harness should mark a runtime task done only after the real local Akka path works at the task's stated scope. Akka local execution is production-like validation for generated apps. Normal user-facing behavior for auth, workstream agents, durability, provider-backed model calls, protected capabilities, denials, and audit/work traces must not be deterministic/demo/mock/simulated/model-less unless the task explicitly says it is adding a test double or fixture mode. Test doubles, mocks, and fixtures are useful for automated tests, but they must stay isolated from the default runtime path and cannot by themselves prove feature readiness.

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

A sprint goal is complete only when the named app state works at the selected scope in the locally running Akka app through the intended runtime/API/UI surface. A documented local test substitute is acceptable only when the sprint explicitly narrows scope to a test adapter or non-runtime validation. If a deferral prevents the named feature from working, treat the sprint as narrowed, blocked, or incomplete rather than completed.

Example prompts:

```text
Summarize sprint 02: completed tasks, changed files, automated checks, local app-run status, deferred items, and manual test instructions. Say whether the sprint goal is fully working, narrowed, blocked, or incomplete.
```

```text
Create a manual test checklist for the purchase request sprint, including happy path, validation failures, authorization failures, idempotent repeats, and observable logs or metrics.
```

```text
Read docs/input/testing/sprint-02-results.md. Reconcile these findings with the specs and create follow-up tasks.
```

Manual testing findings should become normal change input. Save findings under `docs/input/testing/` when they span multiple scenarios or need durable provenance, then ask the harness to reconcile them. The harness should update specs, questions, tasks, tests, and generated source code before coding fixes unless the fix is tiny and unambiguous.

## Phase 7: Iterate on features, tweaks, and issues

After the app exists, keep using the same supervised harness loop for every meaningful change:

1. the human writes the new issue, feature request, bug report, or tweak under `docs/input/`
2. the human prompts the harness to review the new or modified input file
3. the harness reconciles it with existing app-description/specs
4. the harness queues any new questions; the human answers or defers them
5. the harness refreshes affected sprint/backlog/task files
6. the harness executes one task per fresh session, updates task status, and changes generated source code/tests when needed
7. the human tests the increment and repeats the loop with any findings

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
Read docs/input/revisions/small-ui-copy-tweak.md. Apply the documented copy tweak to the UI and update any tests if needed. If no planning changes are necessary, explain why.
```

## Prompt patterns by situation

### Start a new app

For the fastest secure AI-first SaaS starting point, install the pack into an empty project and explicitly scaffold the packaged starter:

```bash
bash install.sh --location project --project /path/to/project
/path/to/project/.agents/bin/scaffold-ai-first-saas-starter.sh --target /path/to/project --app-name "My App" --base-package ai.first
```

Then commit the scaffolded baseline. First make the five core v0 workstreams functional for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy with real model-backed `markdown_response`, backend authorization, capability checks, provider/configuration failure handling, and audit/work traces. To extend it, put feature PRDs, domain notes, bugs, or manual test results under `docs/input/` and ask the harness to update the existing app-description/specs before coding. The scaffold's `specs/scaffold-report.md` records the Java base package and rendered paths; the rendered backend is rooted at `pom.xml` and `src/` (not a `backend/` subdirectory), so the harness should preserve those paths and create new vertical capability tasks instead of replacing the starter foundation.

Example extension prompt for a scaffolded app:

```text
Read docs/input/initial/product-prd.md and specs/scaffold-report.md. Extend the scaffolded AI-first SaaS starter rather than generating a separate app. Reconcile the PRD into the existing app-description and specs, preserve the starter foundation, queue questions instead of guessing, and create vertical capability tasks in specs/pending-tasks.md only for unblocked work.
```

For the packaged core foundation planning flow without scaffolding, use the canonical core PRD plus an explicit scope choice:

```text
Use the Akka skills pack to ingest docs/input/initial/core-app-prd.md and docs/input/initial/scope-choice.md. Ask for and record Full core or Module 1-only / not full core before generation, create or update app-description/specs, queue questions instead of guessing, and do not start coding yet. Do not mark full core complete without User Admin and Agent Admin functional agents.
```

For a domain-specific app PRD:

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
Read docs/input/testing/sprint-03-results.md. Update specs and queues, mark any affected tasks blocked or completed as appropriate, and create follow-up tasks for confirmed issues.
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
- Put human-authored source input only under `docs/input/`.
- Prompt the harness to review new or modified `docs/input/` files.
- Treat `app-description/`, `specs/`, backend source, frontend source, tests, and generated assets as harness-maintained outputs, not human-edited input folders.
- Keep planning artifacts in your app workspace, not in `.agents/`.
- Ask the harness to create vertical module sprints for large apps.
- Ask the harness to execute one pending task per fresh session.
- Do not ask the harness to combine unrelated pending tasks just because they touch nearby files.
- Let the harness update task status; it should mark tasks completed only after required checks pass, or blocked when safe completion is not possible.
- Treat manual testing output as first-class input to the next planning/update cycle by recording it under `docs/input/testing/` when it needs durable provenance.
- When requirements change, put the change under `docs/input/` and ask the harness to reconcile specs before coding broad changes.

## Minimal end-to-end example

```text
User: Read docs/input/returns-prd.md and plan this Akka app. Queue questions instead of guessing.
Harness: Created specs/akka-solution-plan.md and specs/pending-questions.md.

User: What is the next question?
Harness: Should warehouse users be allowed to override an expired return window?

User: I added docs/input/decisions/warehouse-override-policy.md. Review it and update the specs.
Harness: Updated specs. Two questions remain.

User: Create module sprints and pending tasks from the approved plan.
Harness: Created specs/sprints/, specs/backlog/, and specs/pending-tasks.md.

User: Use the Akka skills pack to execute the next pending task. Only do one task.
Harness: Implemented TASK-01-001, ran checks, marked it done, and reported TASK-01-002 as next.

User: <new session> Use the Akka skills pack to execute the next pending task. Only do one task.
...

User: Summarize sprint 01 and create a manual test checklist.
Harness: Provides completed work, checks, deferred items, and manual test paths.

User: I added docs/input/testing/duplicate-approval-message.md. Review it, update specs, and queue a fix.
Harness: Updates behavior/tests and creates a focused follow-up task.
```

The important habit is to put human input in `docs/input/`, let the harness maintain specs, queues, code, and tests, and use fresh harness sessions to execute bounded increments.

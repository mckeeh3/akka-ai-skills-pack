# Akka AI Skills Pack

The **Akka AI Skills Pack** is an installable `.agents/` resource pack for AI coding harnesses such as Claude Code, Codex, and Pi. It helps the harness turn normal product and engineering intent into full-stack, secure, AI-first SaaS application plans, Akka Java SDK code, React/Vite/TypeScript web UI assets, tests, and delivery artifacts.

This pack is intentionally **opinionated**. Its goal is to help agents design and build SaaS products where AI does bounded operational work, humans supervise and govern outcomes, security is present from the first planning step, backend behavior is modeled as governed capabilities before component/tool exposure, and the browser UI is a required supervision, administration, decision, audit, and outcome surface.

This pack is **not** intended as a general-purpose generator for traditional CRUD applications, backend-only services, or human-only workflow apps with no delegated AI work, governance model, audit trail, or outcome loop. Conventional forms, tables, and admin screens may exist, but they are subordinate to the secure AI-first SaaS operating model.

The pack is designed so users can speak naturally to the harness. You should not need to know the internal skill names, stages, or routing files.

A primary benefit of the pack is that it can maintain a durable **application description** in addition to generating code. For non-trivial apps, the harness can capture the app's intent, behavior, goals, objectives, security posture, UI expectations, tests, observability, governance rules, open questions, and realization readiness in structured project documents. Those documents become an authoritative source of truth that developers can interrogate through their AI harness: asking what the app is supposed to do, why a behavior exists, what a change impacts, which decisions remain open, and whether generated code is still aligned with product intent.

## Who this is for

### Skills pack users

Use this pack when you are building or evolving an Akka application and want your AI harness to help with:

- PRD/spec ingestion and implementation planning
- secure AI-first SaaS foundation design
- capability-first backend design: governed operations/queries with explicit authority, scope, schemas, side effects, audit, approval, exposure surfaces, and tests
- WorkOS/AuthKit user authentication, WorkOS JWT validation, and tenant/customer/user administration
- app-description, specs, question queues, and pending task queues
- Akka components such as entities, workflows, views, consumers, timed actions, endpoints, and agents
- mandatory Akka-hosted web UI delivery for full-stack AI-first SaaS
- tests, reviews, and iterative change reconciliation

Start here:

- [Skills Pack User Guide](docs/skills-pack-user-guide.md) — install, getting started, usage workflow, prompt patterns, question queues, and task queues

### Skills pack developers

Use this repository when you are maintaining the pack itself: skills, docs, examples, installers, packaging metadata, and releases.

Start here:

- [Skills Pack Developer Guide](docs/skills-pack-developer-guide.md) — repository layout, development commands, packaging model, and release instructions
- [Repository maintainer guidance](AGENTS.md) — required context for AI agents working in this source repository
- [Skill routing map](skills/README.md) — internal skill map used by the harness

## What gets installed

The pack installs into one of these locations:

- **Project install:** `<your-project>/.agents`
- **Global install:** `~/.agents`

The installed `.agents/` directory is a harness support library. Your app source, specs, `app-description/`, `specs/pending-questions.md`, and `specs/pending-tasks.md` normally stay in your application workspace, not inside `.agents/`.

Installed layout, at a high level:

```text
.agents/
├── AGENTS.md
├── bin/
│   └── scaffold-ai-first-saas-starter.sh
├── docs/
├── manifests/
├── resources/
│   ├── examples/java/
│   ├── examples/frontend/
│   └── templates/ai-first-saas-starter/
└── skills/
```

Default installs are skills/resource-only. To start a new app from the packaged starter, explicitly run the scaffold command after installing into an empty or bootstrap-only project:

```bash
.agents/bin/scaffold-ai-first-saas-starter.sh \
  --target /path/to/project \
  --app-name "My App" \
  --base-package ai.first
```

The scaffold writes `specs/scaffold-report.md`, backend source, `frontend/` React/Vite workstream UI source, and a project `.env.example` documenting local WorkOS/AuthKit, JWT, Resend, admin-bootstrap, frontend public AuthKit values, and optional model-provider variables. Backend secrets such as `WORKOS_API_KEY`, `RESEND_API_KEY`, JWT configuration, and `OPENAI_API_KEY` belong only in backend environment/deployment configuration; only `VITE_` variables are browser-public.

## Quick install

Current manifest version:
- `0.1.14`

Install the current GitHub release into the current directory as `<current-directory>/.agents`:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.1.14/install-akka-ai-skills-pack-0.1.14.sh | bash -s --
```

Install into a specific project directory:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.1.14/install-akka-ai-skills-pack-0.1.14.sh | bash -s -- --target-dir /path/to/project
```

For global installs, dry runs, archive installs, and detailed usage, see the [Skills Pack User Guide](docs/skills-pack-user-guide.md).

## Getting started: core app readiness test

A good way to test a skills-pack release is to create a fresh independent Akka project, install the released pack, copy the packaged core PRD input set into `docs/input/`, and ask the harness to work from those PRDs to manually testable live-app sprints.

Recommended release-test loop:

1. update or add PRDs in this skills-pack repository under `docs/examples/core-ai-first-saas-input/`;
2. create a versioned skills-pack release;
3. create a fresh target project outside this repository;
4. install the released pack into that project as `.agents/`;
5. copy the installed core PRD inputs into the target project's `docs/input/initial/`;
6. ask the harness to create app-description/specs/pending questions and tasks;
7. execute one implementation task per fresh harness session;
8. after each sprint, run Akka locally and manually test the visible app feature(s).

Start a fresh target project from the packaged core input documents:

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

When planning is complete and questions are resolved, use fresh harness sessions to execute the next pending task. The harness should ask for the Java base package before generating Java code; press Enter to use `ai.first` unless you want another package. Use `Module 1-only / not full core` only when you intentionally want a narrow first-slice test rather than the complete core app.

## Repository status

This repository is the source project for `akka-ai-skills-pack`. It is not primarily a generated Akka application. The Akka code under `src/` is executable reference material for the skills pack.

For development, testing, packaging, and release instructions, see the [Skills Pack Developer Guide](docs/skills-pack-developer-guide.md).

## License

See [LICENSE](LICENSE).

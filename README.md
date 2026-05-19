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
├── docs/
├── manifests/
├── resources/examples/java/
└── skills/
```

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

## Getting started prompt

After installing the pack into a new target project, start from the packaged canonical core PRD and record the intended scope before generation:

```bash
mkdir -p docs/input/initial
cp .agents/docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md \
  docs/input/initial/core-app-prd.md
cat > docs/input/initial/scope-choice.md <<'EOF'
# Core app scope choice

Selected scope: undecided

Choose one before generation:
- Full core — includes the agent workstream shell plus Access/Profile, User Admin,
  Agent Admin, Audit/Trace, and Governance/Policy functional agents.
- Module 1-only / not full core — minimal auth, /api/me, selected AuthContext,
  profile/context display, authenticated shell, and explicit deferral of User Admin,
  Agent Admin, invitation lifecycle, governed prompt/skill/manifest/tool-boundary
  management, unified audit/work trace UI, and governance/policy/evaluation loops.
EOF
```

Then ask your harness to bootstrap planning artifacts from those inputs:

```text
First read .agents/AGENTS.md and .agents/skills/README.md.
Then read docs/input/initial/core-app-prd.md and docs/input/initial/scope-choice.md.

Ask me to choose Full core or Module 1-only / not full core before generation if the
scope is still undecided, and record the selected scope in app-description/specs.
Bootstrap the app-description, solution plan, pending questions, and pending task queue
for the selected secure AI-first SaaS core scope. Do not generate application source code yet.
Queue questions instead of guessing. Do not treat a full core app as complete unless User
Admin and Agent Admin functional agents are included.
```

Use Full core as the canonical target when you want the complete generated core foundation. Use `Module 1-only / not full core` only as an explicitly recorded first slice. The harness should create or update planning artifacts first, queue questions instead of guessing, and only move to implementation when the plan is clear enough and explicitly approved.

## Repository status

This repository is the source project for `akka-ai-skills-pack`. It is not primarily a generated Akka application. The Akka code under `src/` is executable reference material for the skills pack.

For development, testing, packaging, and release instructions, see the [Skills Pack Developer Guide](docs/skills-pack-developer-guide.md).

## License

See [LICENSE](LICENSE).

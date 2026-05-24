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

The scaffold writes `specs/scaffold-report.md`, backend source, `frontend/` React/Vite workstream UI source, and a project `.env.example` documenting local WorkOS/AuthKit, JWT, Resend, admin-bootstrap, frontend public AuthKit values, and optional model-provider variables. The `ai.first` package in the command above is the accepted/deferred default example; generated apps should use the selected Java base package and must not silently inherit `com.example` from reference examples. Backend secrets such as `WORKOS_API_KEY`, `RESEND_API_KEY`, JWT configuration, and `OPENAI_API_KEY` belong only in backend environment/deployment configuration; only `VITE_` variables are browser-public.

## Quick install

Current manifest version:
- `0.2.7`

Install the current GitHub release into the current directory as `<current-directory>/.agents`:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.2.7/install-akka-ai-skills-pack-0.2.7.sh | bash -s --
```

Install into a specific project directory:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.2.7/install-akka-ai-skills-pack-0.2.7.sh | bash -s -- --target-dir /path/to/project
```

For global installs, dry runs, archive installs, and detailed usage, see the [Skills Pack User Guide](docs/skills-pack-user-guide.md).

## Getting started: implement your initial AI-first app

The recommended first-user path is incremental. Start with the packaged secure AI-first SaaS starter, make the starter functional, then use the same pattern to add domain-specific capabilities. The starter is not just boilerplate; it is a working training vertical for the app's future feature work:

```text
intent → functional agent/workstream → structured surface → governed backend capability
→ Akka components → tests → UI integration → audit/security review
```

### Step 1 — Create a target app project and install the pack

Create a fresh empty project outside this skills-pack repository with the Akka CLI, then install the pack into that project as `.agents/`.

```bash
akka code init
# Select: Empty project
# Project directory name: my-ai-first-app
# Which AI assistant: None
cd my-ai-first-app
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.2.7/install-akka-ai-skills-pack-0.2.7.sh | bash -s --
```

### Step 2 — Ask the harness to scaffold the starter

Use your AI coding harness from the target project directory. Start with a direct prompt like this:

```text
Read .agents/AGENTS.md and .agents/skills/README.md.

I want to build the initial secure AI-first SaaS app from the installed skills pack.
Use the packaged AI-first SaaS starter scaffold as the baseline.
App name: <your app name>.
Java base package: <press Enter/use ai.first unless I provide another package>.

Scaffold the starter into this project. Do not invent a different architecture.
Preserve the skills-pack defaults: secure SaaS foundation, agent workstream shell,
five core v0 workstreams (My Account, User Admin, Agent Admin, Audit/Trace,
and Governance/Policy), markdown_response surfaces, capability-first backend boundaries,
audit/work trace substrate, backend authorization, and frontend secret boundaries.
After scaffolding, summarize what was created and what I need to configure next.
```

The harness should run the installed scaffold command, roughly:

```bash
.agents/bin/scaffold-ai-first-saas-starter.sh \
  --target . \
  --app-name "<your app name>" \
  --base-package ai.first
```

Use a different Java base package if you already have one. Do not use `com.example` unless you explicitly want that package.

### Step 3 — Ask for an actionable starter readiness triage

After scaffolding, ask the harness to inspect the generated project before changing code. This is a readiness gate, not a general audit: the goal is to confirm what landed, identify only the configuration and checks needed now, and separate true blockers from later foundation follow-up work.

```text
Review the scaffolded starter app for minimum AI-first SaaS starter readiness.
Read specs/scaffold-report.md, app-description/, specs/, backend/, frontend/, .env.example,
and frontend/.env.example if present.

Do not implement changes yet. Do not give me a broad perfect-SaaS backlog.
Return the answer in this exact format:

1. Current status
   - Ready to build/test? yes/no, with one sentence why.
   - Ready to run locally? yes/no, with one sentence why.
   - Ready for app-specific domain features? yes/no, with one sentence why.
   - Five core v0 workstream status: one line for My Account, User Admin, Agent Admin,
     Audit/Trace, and Governance/Policy.

2. Required local configuration
   - List only values I must set now.
   - For each value, say where it goes and whether a local/test placeholder is acceptable.
   - Clearly mark backend-only secrets versus browser-public VITE_ values.

3. Commands to run next
   - Give exact commands in order.
   - Include backend checks and frontend checks.
   - Say what success should look like for each command.

4. Blocking issues
   - Include only issues that prevent build/test/local run or invalidate the starter foundation.
   - For each issue, include the exact next action or prompt I should give you.

5. Non-blocking foundation follow-up
   - Include only starter/foundation tasks that can wait until after the starter builds/runs.
   - Do not include app-specific domain features.

6. Recommended next prompt
   - Give me the single next prompt I should send.
```

### Step 4 — Configure local environment placeholders

Copy the generated environment example and fill only the values needed for local testing. Keep backend secrets out of frontend files.

```text
Help me configure the local environment for this scaffolded app.
Read .env.example and frontend/.env.example.
Explain which values are backend-only secrets, which VITE_ values are browser-public,
and which values can remain as local/test placeholders for now.
Do not commit real secrets.
```

Typical later values include WorkOS/AuthKit, JWT configuration, Resend, bootstrap admin settings, and optional model-provider keys. Local/dev/test may use captured outbox behavior where supported.

### Step 5 — Run the app checks and fix scaffold-level issues first

Ask the harness to run the generated checks, then fix only starter/foundation issues:

```text
Run the generated backend and frontend checks for the scaffolded starter.
If something fails, fix only scaffold-level or configuration-related issues needed for the
minimum starter to run. Do not add domain-specific features yet.
Report every command run and its result.
```

### Step 6 — Make the five core v0 workstreams functional

Once basic checks pass, continue with the first starter target rather than jumping to domain work:

```text
Use the installed skills pack to continue the initial app rollout.
Focus on making all five core v0 workstreams functional end to end:
My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
For each workstream, preserve bootstrap-authorized access, selected AuthContext,
durable workstream entries, markdown_response rendering, backend capability checks,
denials, and audit/work traces.
Update app-description/specs/pending-tasks as needed before code changes.
Implement one small task at a time and include tests.
```

This phase teaches the repeatable app pattern: functional agent/workstream, structured surface, backend capability, Akka implementation, tests, UI integration, and audit/security review.

### Step 7 — Advance from minimum starter to full core readiness

After the minimum starter works locally, ask the harness to plan and execute the remaining core foundation in small live-app increments:

```text
Assess this project against full core secure AI-first SaaS readiness.
Start from the five core v0 workstreams: My Account, User Admin, Agent Admin,
Audit/Trace, and Governance/Policy. Create or update specs/pending-tasks.md with
small implementation tasks for the missing core foundation: richer My Account,
full User Admin, invitations/onboarding with Resend or captured outbox, full Agent Admin,
governed prompt/skill/tool-boundary documents, audit/trace search UI, Governance/Policy
surfaces, support access, tenant-isolation tests, forbidden-access tests, frontend
secret-boundary tests, and security review.
Do not add app-specific domain features until the core readiness gaps are explicit.
```

Then execute tasks one at a time, preferably in fresh harness sessions:

```text
Read .agents/AGENTS.md, .agents/skills/README.md, specs/pending-tasks.md, and the files relevant to the next task.
Select the next runnable pending task for the initial core app rollout.
Implement only that task, update tests, run the relevant checks, and update specs/pending-tasks.md with the result.
```

### Step 8 — Add domain-specific features after the foundation is usable

When the initial app is functional, use natural product prompts to extend it. The pack should make reasonable decisions, record assumptions, and ask only for blocking information.

```text
Now extend this AI-first SaaS app with this domain feature:
<describe the feature in normal product language>

Use the established pattern from the initial app rollout:
functional agent/workstream, structured surfaces, governed backend capabilities,
Akka component selection, tests, UI integration, authorization, audit/work traces, and readiness review.
Make best-judgment decisions where safe, record assumptions, and ask only for blocking questions.
```

A good feature iteration should preserve the app-description, specs, pending questions, pending tasks, code, tests, and UI as aligned artifacts.

## Repository status

This repository is the source project for `akka-ai-skills-pack`. It is not primarily a generated Akka application. The Akka code under `src/` is executable reference material for the skills pack.

For development, testing, packaging, and release instructions, see the [Skills Pack Developer Guide](docs/skills-pack-developer-guide.md).

## License

See [LICENSE](LICENSE).

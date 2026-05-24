# Akka AI Skills Pack

The **Akka AI Skills Pack** is an installable `.agents/` resource pack for AI coding harnesses such as Claude Code, Codex, and Pi. It helps the harness turn normal product and engineering intent into full-stack, secure, AI-first SaaS application plans, Akka Java SDK code, React/Vite/TypeScript web UI assets, tests, and delivery artifacts.

This pack is intentionally **opinionated**. Its goal is to help agents design and build SaaS products where AI does bounded operational work, humans supervise and govern outcomes, security is present from the first planning step, backend behavior is modeled as governed capabilities before component/tool exposure, and the browser UI is a required supervision, administration, decision, audit, and outcome surface.

This pack is **not** intended as a general-purpose generator for traditional CRUD applications, backend-only services, or human-only workflow apps with no delegated AI work, governance model, audit trail, or outcome loop. Conventional forms, tables, and admin screens may exist, but they are subordinate to the secure AI-first SaaS operating model.

The pack is designed so users can speak naturally to the harness. You should not need to know the internal skill names, stages, or routing files.

Generated-app features are considered complete only when the real local Akka runtime path works at the stated scope. Akka local execution is production-like validation for this pack: workstream agents, auth, durability, provider-backed model calls, protected capabilities, denials, traces, API responses, and frontend surfaces should be exercised through normal runtime paths before being called done. Deterministic/demo/mock/simulated/model-less behavior belongs only in tests or explicitly named fixture modes; it must not be the user-facing substitute for implemented runtime features.

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

The scaffold writes `specs/scaffold-report.md`, backend source, `frontend/` React/Vite workstream UI source, and a project `.env.example` documenting local WorkOS/AuthKit, JWT, Resend, admin-bootstrap, frontend public AuthKit values, and model-provider variables when workstream agents are model-backed. The `ai.first` package in the command above is the accepted/deferred default example; generated apps should use the selected Java base package and must not silently inherit `com.example` from reference examples. Backend secrets such as `WORKOS_API_KEY`, `RESEND_API_KEY`, JWT configuration, and `OPENAI_API_KEY` belong only in backend environment/deployment configuration; only `VITE_` variables are browser-public. Missing provider configuration should fail closed with an actionable runtime error instead of a deterministic canned response.

## Quick install

Current manifest version:
- `0.2.9`

Install the current GitHub release into the current directory as `<current-directory>/.agents`:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.2.9/install-akka-ai-skills-pack-0.2.9.sh | bash -s --
```

Install into a specific project directory:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.2.9/install-akka-ai-skills-pack-0.2.9.sh | bash -s -- --target-dir /path/to/project
```

For global installs, dry runs, archive installs, and detailed usage, see the [Skills Pack User Guide](docs/skills-pack-user-guide.md).

## Getting started: implement your initial AI-first app

The recommended first-user path is incremental and production-oriented. Start with the packaged secure AI-first SaaS starter, make the five core v0 workstreams functional with real model-backed agents, then use the packaged core-app domain PRDs to implement the full core workstreams one at a time before adding product-specific capabilities. The starter is not just boilerplate; it is the working runtime shell and training vertical for the app's future feature work:

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
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.2.9/install-akka-ai-skills-pack-0.2.9.sh | bash -s --
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
five core v0 workstreams, four primary left-rail workstream links (User Admin,
Agent Admin, Audit/Trace, and Governance/Policy), the signed-in user tile at the
bottom of the rail opening My Account, markdown_response surfaces,
capability-first backend boundaries, audit/work trace substrate, backend
authorization, and frontend secret boundaries.
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
Read specs/scaffold-report.md, app-description/, specs/, pom.xml, src/, frontend/, .env.example,
and frontend/.env.example if present.

Do not implement changes yet. Do not give me a broad perfect-SaaS backlog.
Return the answer in this exact format:

1. Current status
   - Ready to build/test? yes/no, with one sentence why.
   - Ready to run locally? yes/no, with one sentence why.
   - Ready for app-specific domain features? yes/no, with one sentence why.
   - Five core v0 workstream status: one line for My Account via the signed-in user tile,
     plus User Admin, Agent Admin, Audit/Trace, and Governance/Policy in the left rail.

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

Typical values include WorkOS/AuthKit, JWT configuration, Resend, bootstrap admin settings, and backend-only model-provider keys such as `OPENAI_API_KEY` when validating real model-backed workstream agents. Local/dev/test may use captured outbox behavior for email where supported, but model-backed workstream message submission must be blocked with an actionable provider-configuration error when provider variables are missing; it must not silently return deterministic placeholder text.

### Step 5 — Run the app checks and fix scaffold-level issues first

Ask the harness to run the generated checks, then fix only starter/foundation issues:

```text
Run the generated backend and frontend checks for the scaffolded starter.
If something fails, fix only scaffold-level or configuration-related issues needed for the
minimum starter to run. Do not add domain-specific features yet.
Report every command run and its result.
```

### Step 6 — Make the five core v0 workstreams functional

Once basic checks pass, continue with the first starter target rather than jumping to domain work. The five core v0 app is not functional until normal workstream message submission goes through real governed prompt assembly, a concrete Akka `Agent` component, and a configured backend model provider. A service-only provider call that bypasses the Akka Agent is not a completed workstream-agent runtime. Missing provider configuration should produce a safe blocked/error response, not a canned deterministic answer.

```text
Use the installed skills pack to continue the initial app rollout.
Focus on making all five core v0 workstreams functional end to end with real
model-backed workstream-agent responses through the Akka Agent component path.
My Account is opened by clicking the signed-in user tile at the bottom of the
left rail; the primary left-rail workstream links are User Admin, Agent Admin,
Audit/Trace, and Governance/Policy.
For each workstream, preserve bootstrap-authorized access, selected AuthContext,
durable workstream entries, markdown_response rendering, backend capability checks,
denials, prompt/model/work traces, and frontend secret boundaries.
Use backend-only provider variables such as OPENAI_API_KEY and configured model id/endpoint
from .env. If provider configuration is missing, block message submission with an
actionable error; do not use deterministic placeholder text as normal runtime behavior.
Update app-description/specs/pending-tasks as needed before code changes.
Implement one small task at a time and include tests.
```

Manual model-backed smoke checklist after the workstream-agent runtime is implemented:

1. Start the Akka app with backend-only provider variables loaded from `.env`; keep `OPENAI_API_KEY` out of `frontend/.env*` and static assets.
2. Sign in through AuthKit as a configured `ADMIN_USERS` account.
3. Select each core workstream: open My Account from the signed-in user tile, then select User Admin, Agent Admin, Audit/Trace, and Governance/Policy from the left rail.
4. Submit a short prompt in each workstream and verify the response is an Akka Agent-backed, provider-backed `markdown_response`, not deterministic fixture copy or a service-only provider bypass.
5. Check prompt/model/work trace surfaces or trace APIs for correlation ids, AgentWorkTrace shape, and redacted provider metadata.
6. Re-run once with provider variables absent and verify message submission is safely blocked with actionable recovery copy and no secret leakage.

This phase teaches the repeatable app pattern: functional agent/workstream, structured surface, backend capability, Akka implementation, tests, UI integration, and audit/security review.

### Step 7 — Roll out the full core app workstreams from the packaged PRDs

After the five core v0 workstreams work locally with real model-backed responses, use the packaged core-app domain PRDs as the implementation source for the full core foundation. These PRDs live in the installed pack at:

```text
.agents/docs/examples/ai-first-saas-core-app-domain/
  README.md
  my-account-workstream/README.md
  user-admin-workstream/README.md
  agent-admin-workstream/README.md
  audit-trace-workstream/README.md
  governance-policy-workstream/README.md
```

Copy them into the project workspace so they become project input, not hidden pack internals:

```bash
mkdir -p docs/input/core-app-domain
cp -R .agents/docs/examples/ai-first-saas-core-app-domain/* docs/input/core-app-domain/
```

Then ask the harness to create the rollout queue before coding:

```text
Read .agents/AGENTS.md, .agents/skills/README.md, specs/scaffold-report.md,
app-description/, specs/, and docs/input/core-app-domain/.

The five core v0 workstreams are now running locally with real model-backed
markdown_response behavior. My Account is accessed through the signed-in user
tile at the bottom of the left rail; do not add a redundant My Account rail link.
Use docs/input/core-app-domain/ as the source PRD for the full core app domain.
Create or update specs/pending-tasks.md with a production-ready rollout plan
that implements the core workstreams one at a time:
1. My Account via signed-in user tile
2. User Admin
3. Agent Admin
4. Audit/Trace
5. Governance/Policy

For each workstream, derive structured surfaces, surface actions, governed backend
capabilities, Akka components, frontend integration, real workstream-agent skills/tools,
authorization, tenant isolation, audit/work traces, tests, and manual local smoke checks.
Do not add product-specific domain features until the full core workstream rollout
queue exists and the current next core task is clear.
```

Execute the rollout one task at a time, preferably in fresh harness sessions:

```text
Read .agents/AGENTS.md, .agents/skills/README.md, specs/pending-tasks.md,
docs/input/core-app-domain/, and the files relevant to the next task.
Select the next runnable pending task for the full core app rollout.
Implement only that task through the real local Akka runtime path, update tests,
run the relevant checks, perform any required local smoke validation, and update
specs/pending-tasks.md with the result.
```

Do not treat PRD processing as a paperwork step. Each core workstream is done only when it works through the authenticated shell, real backend capabilities, real governed agents where applicable, durable state/traces, React surfaces, tests, and local Akka smoke validation.

### Step 8 — Add product-specific features after the core foundation is usable

When the full core foundation is usable, use natural product prompts to extend it. The pack should make reasonable decisions, record assumptions, and ask only for blocking information.

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

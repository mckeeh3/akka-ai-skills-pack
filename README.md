# Akka AI Skills Pack

The **Akka AI Skills Pack** is an installable `.agents/` resource pack for AI coding harnesses such as Claude Code, Codex, and Pi. It helps the harness turn normal product and engineering intent into full-stack, secure, AI-first SaaS application plans, Akka Java SDK code, React/Vite/TypeScript web UI assets, tests, and delivery artifacts.

This pack is intentionally **opinionated**. Its goal is to help agents design and build SaaS products where AI does bounded operational work, humans supervise and govern outcomes, security is present from the first planning step, AI-first managed agents are configuration-driven core runtime actors, backend behavior is modeled as governed capabilities before component/tool exposure, and the browser UI is a required supervision, administration, decision, audit, and outcome surface.

This pack is **not** intended as a general-purpose generator for traditional CRUD applications, backend-only services, or human-only workflow apps with no delegated AI work, governance model, audit trail, or outcome loop. Conventional forms, tables, and admin screens may exist, but they are subordinate to the secure AI-first SaaS operating model.

The pack is designed so users can speak naturally to the harness. You should not need to know the internal skill names, stages, or routing files.

Generated-app features are considered complete only when the real local Akka runtime path works at the stated scope. Akka local execution is production-like validation for this pack: workstream agents, auth, durability, provider-backed model calls, protected capabilities, denials, traces, API responses, and frontend surfaces should be exercised through normal runtime paths before being called done. Model-backed workstream agents must use the configuration-driven managed-agent path: active runtime config resolution, compact prompt/skill/reference manifests, governed `readSkill`/`readReferenceDoc` tools, `ToolPermissionBoundary`, `effects().tools(runtimeTools)`, concrete Akka `Agent` invocation, and durable traces. Deterministic/demo/mock/simulated/model-less behavior belongs only in tests or explicitly named fixture modes; it must not be the user-facing substitute for implemented runtime features.

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
  --app-slug "my-app" \
  --base-package ai.first \
  --maven-group-id ai.first \
  --force-empty \
  --yes
```

The scaffold writes `specs/scaffold-report.md`, backend source, `frontend/` React/Vite workstream UI source, app-description/spec seed artifacts, and environment examples documenting local WorkOS/AuthKit, JWT, Resend, admin-bootstrap, frontend public AuthKit values, and model-provider variables when workstream agents are model-backed. The starter includes configuration-driven core managed agents with seeded `AgentDefinition`, prompt, skill, reference, manifest, `ToolPermissionBoundary`, governed loader tools, and runtime trace records; normal message submission must register the resolved tool bindings with `effects().tools(runtimeTools)`. The `ai.first` package in the command above is the accepted/deferred default example; generated apps should use the selected Java base package and must not silently inherit `com.example` from reference examples. Backend secrets such as `WORKOS_API_KEY`, `RESEND_API_KEY`, JWT configuration, and `OPENAI_API_KEY` belong only in backend environment/deployment configuration; only `VITE_` variables are browser-public. Missing provider configuration should fail closed with an actionable runtime error instead of a deterministic canned response.

## Quick install

Current manifest version:
- `0.2.12`

Install the current release into the current directory as `<current-directory>/.agents`:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.2.12/install-akka-ai-skills-pack-0.2.12.sh | bash -s --
```

Install into a specific project directory:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.2.12/install-akka-ai-skills-pack-0.2.12.sh | bash -s -- --target-dir /path/to/project
```

For global installs, dry runs, archive installs, and detailed usage, see the [Skills Pack User Guide](docs/skills-pack-user-guide.md).

## Getting started: scaffold and validate the AI-first SaaS starter

The recommended first-user path is now direct: install the pack into a fresh Akka project, scaffold the packaged secure AI-first SaaS starter, configure local environment values, run the generated checks, smoke the runtime workstreams, then add product-specific capabilities. A separate “starter readiness triage” prompt is no longer a required step because the packaged starter now includes the five-core baseline and writes `specs/scaffold-report.md` as its own scaffold summary.

The starter is the working SMB-oriented baseline for the five mandatory core workstreams:

- **My Account** — opened from the signed-in user tile/email at the bottom of the rail; profile/settings, selected context, authority basis, personal attention, own trace refs, notifications, preferences, and MyAccountAgent guidance.
- **User Admin** — left-rail workstream for invitations, members, roles/capabilities, access management, UserAdminAgent guidance, and access-review task surfaces.
- **Agent Admin** — left-rail workstream for governed managed-agent definitions, prompts, skills, references, manifests, model refs, tool boundaries, seeds, behavior-change lifecycle, and AgentAdminAgent prompt-risk guidance.
- **Audit/Trace** — left-rail workstream for trace dashboard/search/detail/timeline, redacted evidence cards, provider/tool/model/worker failure evidence, and AuditTraceAgent summaries/explanations.
- **Governance/Policy** — left-rail workstream for policy posture, proposals, decisions, blocked analysis-task readiness, and GovernancePolicyAgent impact analysis.

The starter also includes backend-owned attention/events, in-app notifications, Resend-backed production email with captured local/test outbox behavior, invitation/onboarding flows, admin audit traces, and bounded AutonomousAgent worker verticals for the core workstreams.

The implementation pattern remains:

```text
intent → functional agent/workstream → structured surface → governed backend capability
→ durable Akka/runtime component → tests → UI integration → audit/security review
```

Normal completed runtime paths must be production-like for the stated starter scope. Model-backed workstream messages use the governed Akka `Agent` runtime path with active managed configuration, compact prompt/skill/reference manifests, `ToolPermissionBoundary`, governed loader/evidence tools, `effects().tools(runtimeTools)`, concrete Akka Agent invocation, provider-backed responses, and durable traces. Missing provider/security/email configuration must fail closed with actionable behavior. Test fakes and frontend fixture inspection modes may exist only as explicitly named test/local inspection paths; they are not the completed normal runtime substitute.

### Step 1 — Create a target app project and install the pack

Create a fresh empty project outside this skills-pack repository with the Akka CLI, then install the pack into that project as `.agents/`.

```bash
akka code init
# Select: Empty project
# Project directory name: my-ai-first-app
# Which AI assistant: None
cd my-ai-first-app
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.2.12/install-akka-ai-skills-pack-0.2.12.sh | bash -s --
```

If you are installing from a locally built release bundle instead of GitHub, run that bundle's `install.sh` with the same target-project location.

### Step 2 — Scaffold the starter

Use your AI coding harness from the target project directory. Start with a direct prompt like this:

```text
Read .agents/AGENTS.md and .agents/skills/README.md.

I want to build the initial secure AI-first SaaS app from the installed skills pack.
Use the packaged AI-first SaaS starter scaffold as the baseline.
App name: <your app name>.
App slug: <your-app-slug>.
Java base package: <press Enter/use ai.first unless I provide another package>.

Scaffold the starter into this project using .agents/bin/scaffold-ai-first-saas-starter.sh.
Do not invent a different architecture. Preserve the skills-pack defaults: secure SaaS
foundation, five core AI-first workstreams, backend-owned attention/events,
in-app notifications, Resend-backed email delivery, AutonomousAgent worker verticals,
governed backend capabilities, durable Akka-backed runtime state/traces, governed
model-backed Agent runtime, provider fail-closed behavior, backend authorization,
tenant/customer isolation, and frontend secret boundaries.

After scaffolding, show the scaffold command used, summarize specs/scaffold-report.md,
and tell me the local configuration and validation commands to run next.
```

The harness should run the installed scaffold command, roughly:

```bash
.agents/bin/scaffold-ai-first-saas-starter.sh \
  --target . \
  --app-name "<your app name>" \
  --app-slug "<your-app-slug>" \
  --base-package ai.first \
  --maven-group-id ai.first \
  --force-empty \
  --yes
```

Use a different Java base package and Maven group id if you already have them. Do not use `com.example` unless you explicitly want that package.

Commit the scaffolded baseline before making product-specific changes.

### Step 3 — Configure local environment

Copy the generated environment examples and fill only the values needed for local testing. Keep backend secrets out of frontend files.

```text
Help me configure the local environment for this scaffolded app.
Read .env.example and frontend/.env.example.
Explain which values are backend-only secrets, which VITE_ values are browser-public,
and which values can remain as local/test placeholders for now.
Do not commit real secrets.
```

Typical values include WorkOS/AuthKit, JWT configuration, Resend, explicit `ADMIN_USERS` bootstrap entries, and backend-only model-provider keys such as `OPENAI_API_KEY` when validating real model-backed workstream agents. Production email must use Resend; local/dev/test email capture must be clearly labelled captured, not sent. If provider or required production email variables are missing, normal runtime paths should be blocked with actionable recovery copy rather than deterministic placeholder success.

### Step 4 — Run generated checks

Ask the harness to run generated checks, then fix only scaffold/foundation issues:

```text
Run the generated backend and frontend checks for the scaffolded starter.
If something fails, fix only scaffold-level or configuration-related issues needed for the
five-core starter to build and run. Do not add domain-specific features yet.
Report every command run and its result.
```

Expected local checks normally include:

```bash
mvn test
npm --prefix frontend install
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
```

From this skills-pack source repository, maintainers can also validate the rendered starter with:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

### Step 5 — Smoke the runtime baseline

Before adding product-specific features, validate the starter as an AI-first runtime:

1. Start the Akka app with backend-only WorkOS/AuthKit/JWT/admin, Resend/email mode, and model-provider variables loaded from `.env` as needed for the smoke scope.
2. Sign in through AuthKit as an explicitly configured `ADMIN_USERS` account.
3. Open My Account from the signed-in user tile at the bottom of the rail.
4. Open User Admin, Agent Admin, Audit/Trace, and Governance/Policy from the left rail.
5. Check in-app notification center behavior and email/captured-outbox behavior for notification paths in scope.
6. Submit a short prompt in each workstream and verify each normal response is generated through the governed Akka Agent runtime, not fixture copy or a service-only provider bypass.
7. Inspect trace/correlation ids and verify provider metadata and unauthorized source details are redacted.
8. Re-run once with provider variables absent and verify message submission is safely blocked with actionable recovery copy and no secret leakage.

Prompt the harness:

```text
Validate the scaffolded five-core AI-first SaaS starter end to end.
Use the normal authenticated shell and backend workstream APIs.
Verify My Account from the signed-in user tile and the four left-rail workstreams:
User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
Also verify backend-owned attention/events, in-app notifications, Resend email or captured
local/test outbox behavior, preferences/category allowlist behavior, redaction, idempotency,
audit traces, provider fail-closed behavior, and frontend secret boundaries.
Do not use frontend fixtures or model-less fallback text as normal runtime success.
Record any blockers in specs/pending-tasks.md before fixing them one task at a time.
```

### Step 6 — Add product-specific capabilities after the core baseline is healthy

When the five-core baseline builds, runs, and smokes successfully at the starter scope, use natural product prompts to extend the app. New app-specific work should preserve the established pattern: functional agent/workstream, structured surfaces, governed backend capabilities, Akka component selection, tests, UI integration, authorization, audit/work traces, and readiness review.

```text
Now extend this AI-first SaaS app with this domain feature:
<describe the feature in normal product language>

Use the established five-core starter pattern:
functional agent/workstream, structured surfaces, governed backend capabilities,
Akka component selection, tests, UI integration, authorization, audit/work traces,
and readiness review. Make best-judgment decisions where safe, record assumptions,
and ask only for blocking questions.
```

A good feature iteration keeps `app-description/`, `specs/`, pending questions, pending tasks, code, tests, and UI aligned.

## Repository status

This repository is the source project for `akka-ai-skills-pack`. It is not primarily a generated Akka application. The Akka code under `src/` is executable reference material for the skills pack.

For development, testing, packaging, and release instructions, see the [Skills Pack Developer Guide](docs/skills-pack-developer-guide.md).

## License

See [LICENSE](LICENSE).

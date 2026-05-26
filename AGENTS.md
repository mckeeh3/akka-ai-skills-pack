## Project Scope: This Repository Develops the Skills Pack

This repository is the **source project for the `akka-ai-skills-pack` itself**.
It is **not** primarily a normal Akka application project that happens to use the skills.

The pack produced here is intended to be installed for AI harnesses such as **Claude code, Codex** or **Pi** in one of two places:
- **project-level install**: `<target-project>/.agents`
- **global install**: `~/.agents`

That installed pack is then used to help developers build **other Akka application projects**.

### Critical distinction

There are two very different contexts:

1. **Developing this repository**
   - You are maintaining the skills pack source.
   - Typical work includes editing `skills/`, `docs/`, `pack/`, installers, manifests, exported examples, and repository guidance.
   - Any Akka code created under `src/` or exported examples is usually **reference/example material for the pack**, not the main business product of this repository.

2. **Using the installed skills pack in another project**
   - A developer has installed this pack into `.agents/` and is using it to help build a separate Akka service or system.
   - In that context, the operative guidance is the **installed-pack guidance** (`pack/AGENTS.md` in source form, installed as `.agents/AGENTS.md`).
   - That is a different role from this repository's maintainer guidance.

### How to behave in this repository

Assume the default task is **improving the skills pack** unless the user explicitly says otherwise.
When asked to create or revise Akka components here, first interpret them as one of the following:
- a **reference example** used by the pack
- a **test fixture** validating pack guidance
- a **source asset** that will be packaged for downstream use

Do **not** silently treat this repository as if it were the end-user's Akka application unless the user clearly says they want to work on an example app in this repo.
If the user's real goal is to build a separate Akka app that merely **uses** this pack, make that distinction explicit.

### Runtime completion doctrine for generated apps

When this repository's guidance, templates, or tasks describe a generated Akka feature as implemented, complete, or ready, that claim must mean the real local runtime path works at the stated scope. Akka local execution is production-like validation for generated apps and should be used aggressively for runtime/API/UI features rather than avoided in favor of static or fixture-only checks.

Do not teach downstream harnesses to accept deterministic/demo/mock/simulated/model-less normal runtime behavior for workstream agents, auth, durability, provider calls, protected capabilities, authorization denials, or audit/work traces. Model-backed workstream agents must invoke a concrete Akka `Agent` component through the governed runtime path; direct service/provider calls that bypass the Agent are not a completed user-facing runtime. Missing provider or security configuration should fail closed with actionable errors. Fixtures, mocks, deterministic fakes, and test doubles belong in tests or explicitly named test adapters only; they must not be the user-facing runtime substitute used to mark features done.

---

## Core repo principle

**Users should talk to the harness naturally.**
They should not need to know the repository's internal skill taxonomy, stage model, or file layout.

Terminology guardrail: use `domain-specific` or the user's actual domain name for app-specific follow-up work. Do not use `DCA`, `DCA-specific`, or other historical/example domain names as generic placeholders unless the target product is explicitly that domain.

The repository's skills, docs, examples, and routing structure exist primarily to help the harness:
- interpret intent correctly
- choose the right Akka design path
- decompose work before coding
- load only the smallest relevant guidance
- generate consistent code and tests over time
- refuse to count deterministic/demo/mock/simulated normal runtime paths as implemented generated-app features

Implications:
- skills are **internal routing assets**, not the user interface
- users may start with a PRD, feature request, change request, rough idea, or iterative follow-up prompt
- if the harness can already infer the right path from normal language, prefer improving internal routing over adding new discoverability-oriented front doors

---

## What this repository is

Treat this repository as a **requirements-first, intent-driven Akka pack for AI coding agents** whose generated-application target is **secure AI-first SaaS on Akka**.

The canonical doctrine is `docs/ai-first-saas-application-architecture.md`. Former temporary concept files are archived under `specs/ai-first-skills-pack-migration/archive/inbox/` as provenance/source material only.

For high-level product input, begin from the secure AI-first SaaS operating model: durable goals and plans, bounded agent or agent-team execution, policy/permission controls, human supervision, decision/exception handling, audit traces, outcome loops, and a mandatory SaaS security foundation. Then model backend behavior as governed capabilities before choosing the description-first, decomposition, or focused implementation path.

Security, the web UI, and AI-first managed agents are **mandatory from project start** for every generated application unless the user explicitly asks for repository-maintenance-only or non-SaaS reference material. The baseline foundation must model identity, local authorization, Tenant/Customer boundaries, memberships, roles/scopes, `/api/me`, backend authorization checks, audit events, tenant-isolation tests, security review, the agent workstream shell (role-authorized functional agents, continuous workstreams, persistent composer, context/authority indicators, and structured surfaces for sign-in, context selection, administration, supervision, decisions, governance, audit, and outcomes), and the configuration-driven managed-agent runtime (`AgentDefinition`, governed prompts/skills/references, compact manifests, `ToolPermissionBoundary`, governed `readSkill`/`readReferenceDoc` loader tools, `effects().tools(runtimeTools)`, and durable prompt/skill/reference/work traces) before app-specific features are treated as generation-ready.

AI-first is the default interpretation for delegated operational work, autonomous or semi-autonomous decisions, human governance of automation, or outcome accountability. It is **not** a requirement that every app use every AI-first pattern; apply only the substrate objects, governance surfaces, and Akka components justified by the product intent, while keeping the secure SaaS foundation mandatory.

Its job is to help an agent go from:
1. high-level input
2. secure AI-first SaaS operating-model interpretation
3. governed backend capability inventory and authority/exposure semantics
4. either to authoritative app-description maintenance and review, or to Akka solution decomposition
5. to foundation-first skill selection and realization planning
6. to component-by-component code and test generation when realization is requested

Important distinction:
- `akka-context/` contains official Akka reference material and semantic source-of-truth docs
- this repository repackages that knowledge into **agent-oriented patterns, routing, examples, and tests**

When there is a choice between:
- a human-friendly narrative structure, and
- an agent-friendly structure,

prefer the **agent-friendly structure**.

---

## Default working model

This repository now has an AI-first SaaS interpretation layer followed by two first-class operating modes.

For broad product input, inspect the intent through the AI-first architecture doctrine, agent workstream doctrine, and capability-first backend doctrine before decomposing into CRUD screens, page trees, or isolated Akka components. Existing Akka component skills remain the horizontal implementation substrate; they are selected after the operating model, functional agents, structured surfaces, governed capabilities, authority/exposure rules, mandatory UI surfaces, and delivery path are clear enough.

### Mode A: description-first application maintenance
Use this when the user is primarily describing, revising, reviewing, or validating the app as a maintained internal description before realization.

Typical entry skill:
- `app-descriptions`

In this source repository, description-first assets usually appear as one of:
- skill source under `skills/app-description*/`
- supporting doctrine/architecture docs under `docs/`
- reference example app-description trees under `docs/examples/`

If a user asks to create or revise an `app-description/` tree **in this repository**, interpret that by default as:
- a reference example
- a test fixture for the pack
- a source asset that will be packaged for downstream use

Do **not** silently treat a repo-local example app-description tree as the business source of truth for this repository itself.

### Mode B: intent-driven Akka decomposition and implementation
Use this when the goal is to derive the Akka solution shape and then write or revise concrete code/tests.

For most Mode B tasks, follow this flow:
1. read the user's input completely
2. model governed backend capabilities before component selection
3. if the solution shape is not yet clear, decompose from the capability contracts
4. resolve any focused architecture choice that remains open
5. load only the smallest relevant implementation skills
6. generate code and tests component by component
7. repeat that loop as the app evolves over time

A good decomposition result is not a stopping point.
It is an **implementation contract** for downstream coding.

---

## Visible 3-stage skill model

The 3-stage model below applies to the **intent-driven Akka decomposition and implementation path**.
It sits alongside the description-first `app-descriptions` path rather than replacing it.

### Stage 1: Intent and architecture
Use when the task starts from requirements, a prompt, a PRD, a specification file, a feature request, or any other high-level input and the Akka solution shape is not yet known.

For high-level product input, apply the AI-first SaaS default first when the product involves delegated work, agents, governance, decisions, supervision, audit, or outcomes; then identify governed backend capabilities before decomposing into the Akka substrate.

Start with:
- `akka-solution-decomposition`

### Stage 2: Structural decisions
Use when the broader shape is mostly known, but one architecture choice is still unresolved.

Primary example:
- `akka-entity-type-selection` for Event Sourced Entity vs Key Value Entity

### Stage 3: Focused component implementation
Use only after the architecture is settled enough to write code.

This includes focused implementation skill families for:
- entities
- workflows
- views
- consumers
- timed actions
- HTTP/gRPC/MCP endpoints
- web UI delivery
- agents

Rules of thumb:
- do **not** jump straight into a component family when the broader architecture is still unclear
- do **not** use Stage 2 when Stage 1 decomposition is still needed
- use Stage 3 only when the task is already concrete component work

---

## How to use `akka-context/`

Treat `akka-context/`, especially `akka-context/sdk/`, as the **semantic source of truth** for Akka.

Use it for:
- API confirmation
- component semantics
- edge-case behavior
- official terminology
- coverage gaps in local repo guidance

But do **not** copy its human-oriented style directly.
The goal in this repository is to turn official Akka knowledge into **small, explicit, reusable, low-token guidance for agents**.

In short:
- `akka-context/` explains Akka
- this repository packages Akka knowledge for agent use

---

## AI-first SaaS doctrine and inbox provenance

Use `docs/ai-first-saas-application-architecture.md` as the repository's canonical AI-first architecture doctrine.
It defines the default target architecture for generated applications and the vocabulary for goals, agent teams, policies, decisions, traces, governance, UI surfaces, and outcome loops.

`specs/ai-first-skills-pack-migration/archive/inbox/` contains archived source material gathered for the migration. Do not treat archived inbox docs or draft skills as authoritative routing or implementation guidance unless a task explicitly says to use them for provenance.

## How to interpret the main repo areas

### `skills/`
This is the repository's **source-of-truth routing layer**.

A skill should help answer:
- what kind of task is this?
- which files should I read first?
- which pattern applies?
- what should I load next?

Design expectations for skills:
- one broad entry skill per area
- smaller focused companion skills
- explicit “when to use” guidance
- minimal overlap
- low reading cost

### `src/`
This is the **executable example layer**.

Default interpretation:
- `domain` = pure domain logic and immutable models
- `application` = Akka components and component-facing orchestration
- `api` = edge-facing endpoint code
- `test` = verification plus reusable execution patterns

Tests are part of the reference set and should demonstrate:
- success behavior
- validation behavior
- no-op/idempotent behavior
- intended calling patterns
- integration behavior where relevant

### `docs/`
Focused local reference material for recurring patterns, comparisons, and routing aids.

---

## Current strong local coverage

The repository already has strong local patterns across much of the Akka application stack, especially:
- description-first application doctrine, app-description architecture, maintenance flow, readiness, and review summaries
- Event Sourced Entities and Key Value Entities
- workflows, including compensation and pause/resume patterns
- views across multiple source types
- consumers and async integration patterns
- timed actions and timer-backed flows
- HTTP endpoints, including static asset hosting, SSE, WebSocket, JWT, and web UI delivery
- gRPC endpoints
- MCP endpoints
- agents, including tools, structured responses, memory, orchestration, guardrails, evaluation, and testing
- unit and integration testing patterns across component families

Do not force-fit new work into entity-centric structures when it belongs elsewhere.
If local coverage is missing, create a new focused skill/example set instead of stretching an unrelated family.

---

## Session-start checklist

At the start of a new session:
1. read `AGENTS.md`
2. read this file
3. read `skills/README.md`
4. read `docs/ai-first-saas-application-architecture.md` and `docs/capability-first-backend-architecture.md` for high-level product, routing, doctrine, app-description, PRD/spec/backlog, or skill-design work
5. if the task is agent-related, read `docs/agent-coverage-matrix.md`
6. if the task is app-description-related, read:
   - `docs/description-first-application-doctrine.md`
   - `docs/internal-app-description-architecture.md`
   - `docs/app-description-maintenance-flow.md`
   - `docs/examples/ai-first-saas-seed-app-description/README.md` for the secure AI-first SaaS seed reference
   - the smallest relevant example under `docs/examples/purchase-request-app-description/` only for description-layer mechanics
7. classify the task before choosing files:
   - AI-first SaaS doctrine / operating model / governance / routing
   - description-first app maintenance / review / realization
   - requirements decomposition / architecture selection
   - focused structural decision
   - component implementation
   - testing
   - docs/snippet generation
   - cross-cutting topic
   - repo structure / skill design
8. for high-level product input, interpret agentic operating model needs and governed backend capabilities before CRUD/component decomposition
9. if the task starts from high-level input and the user wants description-first maintenance or realization, start with `app-descriptions` and preserve capability inventory semantics
10. if the task starts from high-level input and the component set is not yet known, start with `akka-solution-decomposition` after identifying capability contracts
11. if the task is narrowed to a stateful core but entity type is still open, use `akka-entity-type-selection`
12. otherwise, load the smallest relevant local skill set first
13. use `akka-context/sdk/...` when you need official semantics or a feature not yet well represented locally
14. prefer local agent-optimized patterns when creating new repo content

---

## Rules for creating new repository content

When adding new skills, docs, or examples, optimize in this order:
1. agent usefulness
2. token efficiency
3. correct Akka semantics
4. consistency with repo conventions
5. human readability

Preferred qualities:
- focused
- reusable
- low-ambiguity
- low-token-cost
- easy to route to by file name alone
- easy for a future agent to extend safely

Preferred shape:
- one topic per file when possible
- exact references instead of broad prose where possible
- examples that isolate one pattern
- tests that clarify intended behavior
- docs that answer:
  - when should I use this?
  - what should I read next?
  - what is the canonical example?

If local coverage is missing:
- consult `akka-context/sdk/...`
- do not stretch unrelated skill families to absorb the task
- create or propose a new focused skill family
- create the smallest useful local example set

---

## Bottom line

The most important objective of this project is:

> turn Akka SDK knowledge into AI-first SaaS skills, examples, and guidance that are optimized for AI coding agents rather than for human readers

Use `AGENTS.md` for detailed constraints.
Use this file for startup orientation.
Use `skills/README.md` for routing.
Use `akka-context/` for official Akka semantics.


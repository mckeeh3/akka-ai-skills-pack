# Akka AI Skills Pack

This repository packages **Akka SDK knowledge for AI coding agents** into an **intent-driven and description-first flow**.

Its primary job is to help an agent go from:
- **PRD, requirements doc, user story, process description, API sketch, UI brief, or revision request**
- to either **authoritative app-description maintenance/review** or **Akka solution decomposition**
- to **focused skill selection and realization planning**
- to **code and test generation when realization is requested**

It combines:
- **agent-optimized skills** under `skills/`
- **executable Akka Java SDK examples** under `src/`
- **tests that double as reference patterns** under `src/test/`
- **packaging and installer tooling** for shipping those resources as an installable distribution

In this repository, `skills/` is the canonical authored source path.
When installed into a target project or user profile, those files land under `.agents/skills/`.

## What this project is for

This is **not just an Akka sample service** and **not just a docs mirror**.

It is a **requirements-first system for AI coding agents** that need to start from high-level intent and derive the correct Akka architecture before coding.

The repository now supports two primary usage flows:

1. **description-first application maintenance**
   - read a high-level input or revision request
   - maintain the authoritative internal app description
   - assess change impact, readiness, security, observability, and tests
   - realize outputs only when generation is requested or accepted
2. **intent-driven Akka decomposition and implementation**
   - read a high-level input
   - decompose it into the right Akka components and boundaries
   - resolve any focused architecture decisions that are still open
   - turn that plan into an implementation contract and route to the focused skills needed for each implementation task
   - generate code and tests component by component

The component-family skills in `skills/` are therefore **downstream implementation assets**, not the only front door.
The decomposition output is not the final deliverable by itself; it is the implementation contract that feeds the downstream coding phase.

## Source-repo vs installed-pack usage

This repository contains both:
- the **source project** for developing the skills pack itself
- the content that will be installed into real development projects under `.agents/`

Keep these roles separate:
- repository-root guidance such as `AGENTS.md` is for **maintaining this pack source project**
- `pack/AGENTS.md` is the source for the installed `.agents/AGENTS.md` used in **real development projects**
- example app-description trees under `docs/examples/` are **reference assets for the pack**, not the source of truth for this repository itself
- when the installed pack is used in a real project, the project's maintained `app-description/` tree belongs in the **project workspace**, not inside `.agents/`, unless that project explicitly chooses another internal location

## Description-first usage flow

Use this flow when the user is primarily describing, revising, reviewing, or readiness-checking the app before realization:

1. start with `skills/app-descriptions/SKILL.md`
2. bootstrap with `skills/app-description-bootstrap/SKILL.md` when no usable app-description tree exists yet
3. normalize broad or mixed requests with `skills/app-description-input-normalization/SKILL.md`
4. route to the smallest relevant maintenance skill
5. update capability, behavior, test, auth/security, observability, and traceability layers as needed
6. assess readiness before generation
7. use `skills/app-generate-app/SKILL.md` only when generation is requested or accepted

Key supporting references:
- `docs/description-first-application-doctrine.md`
- `docs/app-description-skills-plan-backlog.md`
- `docs/internal-app-description-architecture.md`
- `docs/app-description-maintenance-flow.md`
- `docs/app-description-end-to-end-workflow-example.md`
- `docs/examples/purchase-request-app-description/README.md`

## Intent-driven usage flow

Use the repository in this order:

1. **Read the requirements input first** — start from the PRD, spec, user story, API sketch, UI brief, or other high-level artifact.
2. **Decompose the solution before coding** — use `skills/akka-solution-decomposition/SKILL.md` to identify the needed Akka components and boundaries.
3. **Resolve any remaining structural choices** — use focused decision help such as `skills/akka-entity-type-selection/SKILL.md` when one architecture choice is still open.
4. **Queue unresolved decisions when needed** — use `skills/akka-pending-question-generation/SKILL.md` to create `specs/pending-questions.md`, then `skills/akka-do-next-pending-question/SKILL.md` to work through questions one at a time.
5. **Load only the focused implementation skills** — use `skills/README.md` to route from the accepted solution plan to the exact Stage 3 skills for entities, workflows, views, consumers, endpoints, timed actions, or agents.
6. **Generate code and tests last** — implement component by component using the selected skills plus the example patterns in `src/` and focused references in `docs/`.

**Important:** code generation is a downstream phase. Do **not** start writing Akka components until decomposition is complete and any key structural decisions are resolved.
Treat the accepted solution plan as the handoff artifact for coding: each chosen component should map to corresponding implementation skills, test-generation skills, and any endpoint, web UI, or documentation/snippet work that belongs downstream.

For a reusable short version of this flow, see `docs/intent-driven-usage-flow.md`.
For a concrete requirements-to-plan example, see `docs/prd-to-akka-flow.md` and `docs/examples/purchase-request-prd.md`.
For a lightweight plan-to-work-queue template, see `docs/solution-plan-to-implementation-queue.md`.
For durable one-at-a-time clarification, see `docs/pending-question-queue.md`, `skills/akka-pending-question-generation/SKILL.md`, `skills/akka-do-next-pending-question/SKILL.md`, and `skills/akka-pending-question-queue-maintenance/SKILL.md`.
For durable multi-session task execution, see `docs/pending-task-queue.md`, `docs/examples/purchase-request-pending-tasks.md`, `skills/akka-backlog-to-pending-tasks/SKILL.md`, and `skills/akka-do-next-pending-task/SKILL.md`.
For ongoing iteration after a queue exists, use `skills/akka-change-request-to-spec-update/SKILL.md` for bounded changes, `skills/akka-revised-prd-reconciliation/SKILL.md` for revised PRDs, `skills/akka-pending-question-queue-maintenance/SKILL.md` for question queue hygiene, and `skills/akka-pending-task-queue-maintenance/SKILL.md` for task queue hygiene.

## Visible 3-stage skill model

This repository uses a visible 3-stage routing model:

### Stage 1: Intent and architecture
Start here when the input is still a PRD, requirements doc, user story, process description, API sketch, UI brief, or other high-level specification **and you are taking the direct Akka decomposition path rather than the description-first path**.

Primary Stage 1 skill:
- `skills/akka-solution-decomposition/SKILL.md`

### Stage 2: Structural decisions
Use this stage when the high-level shape is partly known, but one important architecture choice is still unresolved.
This is a narrower follow-on stage, not the default front door for broad requirements.

Primary Stage 2 skill:
- `skills/akka-entity-type-selection/SKILL.md`

### Stage 3: Focused component implementation
Use this stage when the architecture is already clear enough to write code and tests from the solution plan's implementation contract.

Stage 3 includes focused implementation skill families for peer building blocks such as:
- workflows
- views
- consumers
- timed actions
- endpoints, web UI delivery, and fully capable lightweight frontend apps
- agents
- entities

Entities are one Stage 3 family among several peers, not the default front door for every task.
Not every task starts at Stage 3.
If all you have is a requirements artifact or other broad specification input, start at Stage 1.
Even if the problem sounds stateful, use Stage 1 first when the overall component set is still unknown.
Use Stage 2 only when the task is already narrowed to a stateful core and you have not yet chosen between Event Sourced Entity and Key Value Entity.
Move to Stage 3 only when planning has narrowed the task to concrete component work.

The repository exists to make that workflow efficient for AI agents through:
- small, focused skills
- predictable naming and routing
- example code organized by component type
- tests that show intended calling patterns
- an installable pack that can be dropped into `.agents`

A useful mental model is:
- `akka-context/` explains Akka for humans and serves as a semantic reference
- this repository repackages that knowledge for **requirements-to-architecture-to-code workflows for AI agents**

## What is included

### 1. Source skill library: `skills/`
Agent-routing and implementation skills for:
- description-first app-description maintenance, review, and generation
- Stage 1 solution decomposition
- Stage 2 structural decision support, including entity type selection
- Workflows
- Views
- Consumers
- Timed Actions
- HTTP endpoints, web UI delivery, and lightweight frontend app design/implementation
- gRPC endpoints
- MCP endpoints
- Agents
- Event Sourced Entities
- Key Value Entities

These skills are meant to support the same staged flow used throughout this repository:
- Stage 1: start from requirements or specification input
- Stage 2: resolve focused structural decisions that are still open
- Stage 3: load only the focused implementation skills that match the chosen components

If you are using this repository as a pack source or reference library, start with:
- `skills/README.md`
- `skills/akka-solution-decomposition/SKILL.md` when the task begins from high-level requirements or a specification file
- `pack/README.md` (for install layout and packaging details)
- this `README.md` for repository-level build and install flow

`CONTEXT-WARMUP.md` is intentionally **not** part of the general pack-consumer usage flow. It is a repo-internal session bootstrap/context warmup for AI coding agents contributing to this repository itself.

### 2. Executable examples: `src/`
Reference Akka Java SDK code organized as:
- `src/main/java/com/example/domain` - domain logic and immutable models
- `src/main/java/com/example/application` - Akka components
- `src/main/java/com/example/api` - HTTP, gRPC, and MCP endpoints
- `src/main/proto` - protobuf contracts
- `src/test/java` - unit and integration tests

The examples cover patterns such as:
- workflows, compensation, pause/resume, and orchestration
- views and query models
- consumers and topic/service-stream integration
- timed actions and timer-backed flows
- HTTP, gRPC, and MCP endpoints
- Akka-served web UI, static content, SSE pages, WebSocket pages, and complete lightweight TypeScript frontend app patterns
- agents, tools, structured responses, memory, streaming, guardrails, and evaluation
- Event Sourced Entity and Key Value Entity design
- deterministic testing patterns for each component family

### 3. Reference docs: `docs/`
Focused local reference material for recurring patterns, such as:
- description-first doctrine, app-description architecture, and maintenance flow
- agent coverage
- workflow/endpoint patterns
- timer pattern selection
- consumer references
- service-to-service consumer and view patterns

### 4. Pack metadata and release tooling
- `pack/manifest.yaml` - versioned pack definition
- `pack/README.md` - install layout and packaging model
- `tools/build-pack.sh` - builds release assets under `dist/`
- `tools/install-release-template.sh` - template used to generate a versioned GitHub release installer script
- `install.sh` - installs the full pack into a cross-harness `.agents` directory

### 5. Maintainer-only Akka reference mirror: `akka-context/`
This directory is intentionally kept in the repository as a **maintainer/reference input**.

It is used to:
- confirm official Akka semantics
- resolve API details
- fill local coverage gaps

It is **not bundled into installable distributions**.

## Related guidance files in this repository

This repository contains several guidance files with different roles:
- `README.md` - top-level repository overview and the primary human-facing build/install guide
- `CONTEXT-WARMUP.md` - development-only session bootstrap and context warmup for AI coding agents contributing changes to this repository
- `AGENTS.md` - repository-internal authoritative project rules, Akka implementation constraints, and testing expectations
- `pack/AGENTS.md` - source file for the installed pack-facing `.agents/AGENTS.md` guidance
- `skills/README.md` - skill routing map across all local skill suites
- `pack/README.md` - install target layout, packaging model, and path rewrite rules
- `dist/.../README.md` and `dist/.../pack/README.md` - generated copies included in built distributions when a pack is built locally

If your goal is to build, install, or consume the resource pack, use this `README.md`, `pack/README.md`, the installed `.agents/AGENTS.md`, and the installed `skills/...` files.
Only open `CONTEXT-WARMUP.md` and the repository root `AGENTS.md` if you are starting a new AI-agent development session for work on this repository.

## Top-level repository layout

```text
.
├── CONTEXT-WARMUP.md        # repo-development session bootstrap/context warmup
├── AGENTS.md                # authoritative project rules and coding constraints
├── skills/                  # source skill library
├── akka-context/            # maintainer/reference Akka docs (not bundled)
├── docs/                    # focused local reference docs
├── pack/                    # pack manifest and packaging docs
├── src/                     # executable Akka Java SDK examples
├── tools/build-pack.sh      # distribution builder
├── tools/install-release-template.sh
├── install.sh               # installer for source checkout or unpacked bundle
└── dist/                    # generated release artifacts (not source-controlled)
```

## Prerequisites

For day-to-day development and pack building, use:
- **JDK 21**
- **Maven 3.x**
- **bash**
- **python3** (used by `install.sh` to rewrite installed skill references)
- standard archive tools such as `tar` and `gzip`

The Maven project currently inherits from:
- `io.akka:akka-javasdk-parent:3.5.18`

## Working with the example project

If you want to compile or test the example Akka service contained in `src/`:

### Compile

```bash
mvn compile
```

### Run tests

```bash
mvn test
```

### Start locally

```bash
mvn compile exec:java
```

### Build the Maven artifact

```bash
mvn clean install
```

> The Maven build validates the example service.
> The **installable resource-pack distribution** is built separately with `tools/build-pack.sh`.

## Distribution model

This repository ships an installable resource pack named:
- `akka-ai-skills-pack`

Current manifest version:
- `0.1.0`

The distribution includes:
- `skills/**`, including description-first and implementation-routing skills
- selected pack-facing docs under `docs/**`, including description-first doctrine/architecture references and examples
- selected pack metadata
- exported examples from `src/main` and `src/test`
- the repository `pom.xml`
- this `README.md`
- `install.sh`
- `LICENSE`
- `pack/AGENTS.md` as the source for the installed `.agents/AGENTS.md` guidance file
- `pack/EXAMPLES-README.md` as the source for the installed `.agents/resources/examples/java/README.md` file

The build also generates a versioned GitHub release installer script alongside the archive:
- `dist/install-akka-ai-skills-pack-0.1.0.sh`

Source skills are authored under `skills/` in this repository and installed into `.agents/skills/` by the installer.

The distribution intentionally excludes the repository-internal maintainer-only guidance files from the installed pack experience, including:
- `akka-context/**`
- the repository root `AGENTS.md`
- `CONTEXT-WARMUP.md`

During installation, skill files are rewritten so they:
- point to installed example paths under `.agents/resources/examples/java/...`
- no longer contain broken repo-local `akka-context/...` references
- no longer contain broken references to repository-internal `CONTEXT-WARMUP.md`
- use the installed `.agents/AGENTS.md` guidance file where appropriate
- instead reference official Akka SDK docs generically

The installer always installs the full packaged skill library, shared references, and exported examples.

## How to build a distribution

### Quick build

From the repository root:

```bash
bash tools/build-pack.sh --clean
```

This creates:
- an expanded pack directory under `dist/`
- a `.tar.gz` archive for the pack
- a versioned GitHub release installer script

`dist/` is generated output and is not intended to be source-controlled.

With the current manifest, the output names are:
- `dist/akka-ai-skills-pack-0.1.0/`
- `dist/akka-ai-skills-pack-0.1.0.tar.gz`
- `dist/install-akka-ai-skills-pack-0.1.0.sh`

### Recommended build flow

1. **Verify the example project still builds**

   ```bash
   mvn test
   ```

2. **Build the distribution pack**

   ```bash
   bash tools/build-pack.sh --clean
   ```

   CAUTION: the `--clean` option should only be used to overwrite an existing staged directory.  

3. **Inspect the staged pack**

   ```bash
   find dist/akka-ai-skills-pack-0.1.0 -maxdepth 3 | sort
   ```

4. **Inspect the release assets**

   ```bash
   ls -lh dist/akka-ai-skills-pack-0.1.0.tar.gz dist/install-akka-ai-skills-pack-0.1.0.sh
   ```

5. **Publish both files to the matching GitHub release tag**

   Publish these assets on release tag `v0.1.0`:
   - `dist/akka-ai-skills-pack-0.1.0.tar.gz`
   - `dist/install-akka-ai-skills-pack-0.1.0.sh`

GitHub Actions automation is included:
- `.github/workflows/build-test.yml` checks `README.md` and `pack/README.md` pack-version references, runs `mvn verify`, and runs `tools/build-pack.sh` on PRs, pushes to `main`, and manual dispatch
- `.github/workflows/cut-tag.yml` creates and pushes tag `v<manifest-version>` from a selected ref after validating the manifest version and running `mvn verify`
- `.github/workflows/release.yml` reruns the docs/version consistency check, refuses to overwrite an already published release for the same tag, and attaches the versioned archive and installer to an automatically created **draft** GitHub Release when tag `v<manifest-version>` is pushed

#### How to use it

1. Update `pack/manifest.yaml` to the version you want to release.
2. Merge that change to `main`.
3. Run the **Cut release tag** workflow, or manually push matching tag `v<manifest-version>`.
4. The **Create draft release assets** workflow builds:
   - `akka-ai-skills-pack-<version>.tar.gz`
   - `install-akka-ai-skills-pack-<version>.sh`
5. Review and publish the draft GitHub Release.
6. After the draft release is published, the curl-based installer URLs become publicly usable.

The build refuses to overwrite an existing staged directory, archive, or generated release installer unless you pass `--clean`.

### Build options

#### Write output somewhere other than `dist/`

```bash
bash tools/build-pack.sh --clean --output-dir /tmp/akka-pack-builds
```

#### Override the GitHub release repo used in generated installer URLs

```bash
bash tools/build-pack.sh --clean --github-repo your-org/your-repo
```

#### Build only the expanded directory, no archive

```bash
bash tools/build-pack.sh --clean --no-archive
```

#### Show help

```bash
bash tools/build-pack.sh --help
```

### What `tools/build-pack.sh` validates

Before building, the script checks that key source inputs exist, including:
- `skills/README.md`
- `skills/references/...`
- `pom.xml`
- `README.md`
- `LICENSE`
- `pack/README.md`
- `pack/AGENTS.md`
- `pack/EXAMPLES-README.md`
- `pack/manifest.schema.yaml`
- every skill directory having a `SKILL.md`

## How to install a distribution

You can install in three ways:
- from a **GitHub release asset** using the versioned curl-based installer
- from the **repository checkout** using the root `install.sh`
- from an **unpacked distribution archive** using the bundled `install.sh`

The installation target is always one of these cross-harness locations:
- **project mode**: `<project-root>/.agents`
- **global mode**: `~/.agents`

### 1. Install directly from a GitHub release with `curl`

This installs into `<target-dir>/.agents`, where `--target-dir` defaults to the current directory.

Install into the current directory:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.1.0/install-akka-ai-skills-pack-0.1.0.sh | bash -s --
```

Install into a specific target directory:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.1.0/install-akka-ai-skills-pack-0.1.0.sh | bash -s -- --target-dir /path/to/project
```

The release installer downloads `akka-ai-skills-pack-0.1.0.tar.gz`, unpacks it in a temporary directory, and runs the bundled `install.sh` in project mode.

### 2. Build and unpack the distribution

If you are starting from this repository:

```bash
bash tools/build-pack.sh --clean
```

Then unpack it:

```bash
tar -xzf dist/akka-ai-skills-pack-0.1.0.tar.gz -C /tmp
cd /tmp/akka-ai-skills-pack-0.1.0
```

If you already received a built archive from somewhere else, unpack that archive instead.

### 3. Install to a project-specific `.agents` directory

```bash
bash install.sh \
  --location project \
  --project /path/to/your/project
```

This installs into:
- `/path/to/your/project/.agents/AGENTS.md`
- `/path/to/your/project/.agents/docs`
- `/path/to/your/project/.agents/skills`
- `/path/to/your/project/.agents/manifests`
- `/path/to/your/project/.agents/resources/examples/java`

### 4. Install globally for your user account

```bash
bash install.sh --location global
```

This installs into:
- `~/.agents/AGENTS.md`
- `~/.agents/docs`
- `~/.agents/skills`
- `~/.agents/manifests`
- `~/.agents/resources/examples/java`

### 5. Interactive install

If you omit `--location`, the installer prompts you to choose project or global mode:

```bash
bash install.sh
```

### Useful installer options

#### Dry run

```bash
bash install.sh --location project --project /path/to/project --dry-run
```

#### Show help

```bash
bash install.sh --help
```

## Installed layout

After installation, the target directory looks like this:

```text
.agents/
├── AGENTS.md
├── docs/
│   ├── description-first-application-doctrine.md
│   ├── app-description-skills-plan-backlog.md
│   ├── internal-app-description-architecture.md
│   ├── app-description-maintenance-flow.md
│   ├── agent-coverage-matrix.md
│   ├── prd-to-akka-flow.md
│   ├── timer-pattern-selection.md
│   ├── workflow-endpoint-pattern.md
│   └── examples/
│       ├── purchase-request-app-description/
│       ├── purchase-request-prd.md
│       └── purchase-request-solution-plan.md
├── manifests/
│   └── akka-ai-skills-pack.yaml
├── resources/
│   └── examples/
│       └── java/
│           ├── pom.xml
│           ├── README.md
│           └── src/
│               ├── main/
│               └── test/
└── skills/
    ├── README.md
    ├── references/
    ├── app-descriptions/
    ├── app-description-bootstrap/
    ├── akka-solution-decomposition/
    ├── akka-workflows/
    ├── akka-http-endpoints/
    ├── akka-agents/
    ├── akka-event-sourced-entities/
    └── ...
```

## Source install vs distribution install

### Install directly from a source checkout
Useful during development:

```bash
bash install.sh --location project --project /path/to/project
```

### Install from a built distribution
Useful for release testing and sharing:

```bash
tar -xzf akka-ai-skills-pack-0.1.0.tar.gz
cd akka-ai-skills-pack-0.1.0
bash install.sh --location global
```

### Install from a GitHub release in one step
Useful for end users who want a version-pinned install into a project directory:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.1.0/install-akka-ai-skills-pack-0.1.0.sh | bash -s -- --target-dir /path/to/project
```

In both cases, the installer performs the same target layout and path rewriting behavior.

## Recommended reading order when developing this repository with AI agents

This section is for contributors working on this repository itself.
It is **not** the recommended reading order for pack consumers.

1. `CONTEXT-WARMUP.md` for session bootstrap, repo mental model, and routing approach
2. `AGENTS.md` for authoritative project rules and Akka coding constraints
3. `skills/README.md` for skill routing
4. the focused skill(s) for the task at hand
5. `akka-context/sdk/...` when official Akka semantics or API confirmation is needed

If you are here to use the pack rather than develop it, skip `CONTEXT-WARMUP.md` and `AGENTS.md` and follow the build/install sections in this `README.md` instead.

## License

See `LICENSE`.

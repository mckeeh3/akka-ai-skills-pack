# Akka AI Skills Pack

This repository produces the **Akka AI Skills Pack**: an installable `.agents/` resource pack that helps AI coding harnesses turn product intent into Akka application plans, code, tests, and supporting frontend/backend delivery assets.

The README is organized for two audiences:

1. **Skill-pack users** — developers who install the pack into an Akka application project or into `~/.agents` and then work with their AI harness naturally.
2. **Skill-pack developers** — contributors who maintain this repository, its skill source, reference examples, docs, installer, and release artifacts.

## 1. For skill-pack users

Use this section when your goal is to install the pack and use it from an AI harness while building an Akka application.

### What the installed pack is

After installation, the pack lives in one of these locations:

- **Project install:** `<your-project>/.agents`
- **Global install:** `~/.agents`

The `.agents/` directory is a support library for the AI harness. It contains guidance, routing files, documentation, and reference examples that the harness can load while helping you work on your own project.

Your project artifacts normally stay outside `.agents/`, for example:

- application source under your normal source tree
- PRDs, specs, issues, or design notes wherever your project keeps them
- generated planning artifacts such as `specs/`, `app-description/`, `specs/pending-questions.md`, and `specs/pending-tasks.md` in the project workspace

Do **not** treat `.agents/` as your application source directory. It is the installed pack.

### You do not need to know which skill to use

The intent of this pack is **not** that you learn the internal skill names, stages, or routing model.

As a skill-pack user, you should talk to your AI harness naturally:

- “Read this PRD and plan the implementation.”
- “Here is a feature request; update the current plan.”
- “This bug changes the behavior; reconcile it with the existing specs.”
- “What is the next question?”
- “Do the next task.”

The harness decides which installed files under `.agents/` to load. The pack is designed so the harness can infer the right path from normal product, engineering, or support language.

### Typical usage pattern

The normal user flow is:

1. **Install the pack** into your project or globally.
2. **Provide intent or behavior-changing input** to the harness.
3. **Let the harness create or update planning artifacts** in your project workspace.
4. **Answer queued questions one at a time** when the harness needs clarification.
5. **Work through queued implementation tasks one at a time** when planning is ready.
6. **Repeat** as new PRDs, issues, fixes, or revisions arrive.

The input you provide can be any ordinary project artifact or prompt, including:

- PRDs
- requirements documents
- technical specs
- issue descriptions
- user stories
- support tickets
- bug reports
- feature requests
- API sketches
- workflow descriptions
- UI briefs or frontend design notes
- behavior changes discovered during implementation or testing

You can provide a complete document, point the harness at a file, paste a rough idea, or describe a change conversationally.

### Question queue flow

For non-trivial work, the harness may determine that it should not guess about open decisions. In that case it can create or update a question queue in the project workspace, typically:

```text
specs/pending-questions.md
```

You then handle the queue one question at a time. A typical interaction looks like:

```text
User: Read docs/checkout-prd.md and plan the implementation.
Harness: I found several decisions that need clarification. I created specs/pending-questions.md.

User: What is the next question?
Harness: Should guest checkout be allowed, or must every checkout have an authenticated account?

User: Guest checkout is allowed for digital goods only.
Harness: Recorded. There are 3 questions remaining.

User: What is the next question?
...
```

Each question can be:

- answered
- skipped when it is not important
- deferred with an accepted default or limitation
- superseded by newer input
- marked blocked if it depends on another decision

The goal is to reach a state where the harness can safely create or update the pending implementation work without making hidden product or architecture assumptions.

### Pending task queue flow

Once the app description, specs, or backlog are clear enough, the harness can create or update a task queue in the project workspace, typically:

```text
specs/pending-tasks.md
```

Then you can ask the harness to execute work in small, reviewable increments:

```text
User: Do the next task.
Harness: I will implement the next runnable task from specs/pending-tasks.md.
```

The harness should select one runnable task, load only the relevant installed `.agents/` guidance, change the project code/tests/docs for that task, update the task status, and report what remains.

This gives you a durable multi-session workflow:

1. provide intent
2. clarify open questions
3. generate or refresh the task queue
4. ask “do the next task” until the queue is done
5. provide the next change request or revised PRD

### Backend and optional frontend work

Akka applications built with this pack normally have an **Akka backend** and may also include an **optional frontend web UI**.

You can provide PRDs, specs, issues, feature requests, or fixes for either side:

- backend domain behavior
- workflows and long-running processes
- APIs and integration points
- persistence and query behavior
- security and authorization behavior
- observability and audit behavior
- frontend screens and navigation
- forms and validation
- typed frontend API expectations
- realtime UI behavior such as SSE or WebSockets
- accessibility and responsive layout requirements
- UI tests or acceptance criteria

You do not need to pre-classify the work as “backend skill” or “frontend skill.” Describe the desired user and system behavior. The harness uses the installed pack to decide how backend and frontend work should be decomposed and implemented.

### Install the pack

Current manifest version:
- `0.1.0`

#### Project install from GitHub release

This installs into `<current-directory>/.agents`:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.1.0/install-akka-ai-skills-pack-0.1.0.sh | bash -s --
```

Install into a specific project directory:

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/v0.1.0/install-akka-ai-skills-pack-0.1.0.sh | bash -s -- --target-dir /path/to/project
```

The release installer downloads `akka-ai-skills-pack-0.1.0.tar.gz`, unpacks it in a temporary directory, and runs the bundled installer in project mode.

#### Install from an unpacked archive

```bash
tar -xzf akka-ai-skills-pack-0.1.0.tar.gz
cd akka-ai-skills-pack-0.1.0
bash install.sh --location project --project /path/to/project
```

#### Global install

```bash
bash install.sh --location global
```

This installs into `~/.agents`.

#### Interactive install

```bash
bash install.sh
```

If you omit `--location`, the installer prompts you to choose project or global mode.

#### Dry run

```bash
bash install.sh --location project --project /path/to/project --dry-run
```

### Installed layout

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

### Example user prompts

Initial planning from a PRD:

```text
Read docs/order-management-prd.md and create the implementation plan. If there are open decisions, queue them instead of guessing.
```

Clarify one question:

```text
What is the next question?
```

Skip or defer a question:

```text
Defer that question. Use the simplest safe default and mark the limitation in the queue.
```

Start implementation:

```text
Do the next task.
```

Apply a change request:

```text
This issue changes refund behavior for partially shipped orders. Update the current specs and task queue before coding.
```

Provide frontend UI requirements:

```text
Here is a web UI brief for the support dashboard. Plan the frontend screens, backend APIs, realtime updates, and tests. Queue questions for anything ambiguous.
```

### What to read as a skill-pack user

If you want to inspect the installed pack, start with:

- `.agents/AGENTS.md`
- `.agents/docs/`
- `.agents/resources/examples/java/README.md`

You normally do **not** need to choose or invoke individual files under `.agents/skills/` yourself.

## 2. For skill-pack developers

Use this section when your goal is to develop, test, package, or release this repository itself.

This repository is the **source project for the `akka-ai-skills-pack`**. It is not primarily an Akka application product. The Akka code under `src/` is executable reference material used to validate and demonstrate the pack.

### Development scope

Skill-pack development includes work such as:

- editing source skills under `skills/`
- maintaining pack-facing docs under `docs/`
- maintaining installed-pack guidance source under `pack/AGENTS.md`
- maintaining packaging metadata under `pack/`
- maintaining exported Java examples under `src/main` and `src/test`
- maintaining installers and release scripts
- improving routing, planning, question queue, task queue, and generation guidance
- adding or revising examples that help downstream harnesses build Akka applications correctly

When working in this repository, keep source and installed roles separate:

- `AGENTS.md` is repository-internal maintainer guidance.
- `pack/AGENTS.md` is installed as `.agents/AGENTS.md` for real downstream projects.
- `skills/` is the authored source skill library.
- Installed skill files are copied and rewritten into `.agents/skills/` during installation.
- `docs/examples/` contains reference assets for this pack, not this repository’s own business app description.
- `src/` contains executable examples and tests for the pack, not the main product of this repository.

### Source repository layout

```text
.
├── AGENTS.md                         # repository-internal maintainer guidance
├── README.md                         # this file
├── docs/                             # pack-facing reference docs and examples
├── pack/                             # manifest, packaging docs, installed AGENTS source
├── skills/                           # source skill library
├── src/                              # executable Akka Java SDK reference examples
├── tools/build-pack.sh               # distribution builder
├── tools/check-version-consistency.sh
├── tools/install-release-template.sh
├── install.sh                        # installer for source checkout or unpacked bundle
└── dist/                             # generated release artifacts, not source-controlled
```

### Development prerequisites

Use:

- JDK 21
- Maven 3.x
- bash
- python3
- standard archive tools such as `tar` and `gzip`

The Maven example project currently inherits from:

- `io.akka:akka-javasdk-parent:3.5.18`

### Recommended development reading order

For an AI-agent development session in this repository, read:

1. `AGENTS.md`
2. `skills/README.md`
3. task-specific source skill files under `skills/`
4. relevant local docs under `docs/`
5. official Akka SDK documentation when SDK semantics or API confirmation is needed

### Source skill library

The source skill library lives under `skills/`.

It is designed as an internal routing and implementation layer for harnesses. Maintain it with these goals:

- users can speak naturally without knowing skill names
- broad intent is decomposed before coding
- open decisions are queued instead of guessed
- pending tasks are sized for focused harness runs
- only the smallest relevant guidance is loaded for a task
- backend, endpoint, integration, agent, and optional frontend concerns are represented without forcing everything into an entity-centric model

When adding or revising skills, optimize for:

1. agent usefulness
2. token efficiency
3. correct Akka semantics
4. consistency with repo conventions
5. human readability

### Executable reference examples

The `src/` tree is the executable example layer:

```text
src/main/java/com/example/domain        # pure domain logic and immutable models
src/main/java/com/example/application   # Akka components and orchestration examples
src/main/java/com/example/api           # HTTP, gRPC, and MCP endpoint examples
src/main/proto                          # protobuf contracts
src/test/java                           # unit and integration tests
```

Examples and tests should demonstrate reusable patterns for the installed pack, including:

- workflows, compensation, pause/resume, and orchestration
- views and query models
- consumers and topic/service-stream integration
- timed actions and timer-backed flows
- HTTP, gRPC, and MCP endpoints
- Akka-hosted web UI, static content, SSE, WebSocket, and lightweight TypeScript frontend patterns
- agents, tools, structured responses, memory, streaming, guardrails, and evaluation
- Event Sourced Entity and Key Value Entity design
- deterministic testing patterns for each component family

### Validate the example project

Compile:

```bash
mvn compile
```

Run tests:

```bash
mvn test
```

Start locally:

```bash
mvn compile exec:java
```

Build the Maven artifact:

```bash
mvn clean install
```

The Maven build validates the executable example service. The installable resource-pack distribution is built separately.

### Distribution model

This repository ships an installable resource pack named:

- `akka-ai-skills-pack`

Current manifest version:
- `0.1.0`

The distribution includes:

- source-authored skills copied from `skills/**`
- selected pack-facing docs from `docs/**`
- selected reference examples exported from `src/main` and `src/test`
- `pom.xml` and example support files
- `README.md`
- `install.sh`
- `LICENSE`
- `pack/AGENTS.md` as the source for installed `.agents/AGENTS.md`
- `pack/EXAMPLES-README.md` as the source for installed `.agents/resources/examples/java/README.md`
- `pack/manifest.yaml` as installed manifest metadata

The build also generates a versioned GitHub release installer script:

- `dist/install-akka-ai-skills-pack-0.1.0.sh`

The installed pack intentionally excludes repository-internal maintainer-only guidance, including:

- root `AGENTS.md`
- `akka-context/**`

During installation, copied skill files are rewritten so installed references point to `.agents/` paths and do not depend on repository-local maintainer paths.

### Build a distribution

Quick build:

```bash
bash tools/build-pack.sh --clean
```

This creates:

- `dist/akka-ai-skills-pack-0.1.0/`
- `dist/akka-ai-skills-pack-0.1.0.tar.gz`
- `dist/install-akka-ai-skills-pack-0.1.0.sh`

Recommended release-prep flow:

1. Verify examples and tests:

   ```bash
   mvn test
   ```

2. Check version references:

   ```bash
   bash tools/check-version-consistency.sh
   ```

3. Build the distribution:

   ```bash
   bash tools/build-pack.sh --clean
   ```

4. Inspect the staged pack:

   ```bash
   find dist/akka-ai-skills-pack-0.1.0 -maxdepth 3 | sort
   ```

5. Inspect release assets:

   ```bash
   ls -lh dist/akka-ai-skills-pack-0.1.0.tar.gz dist/install-akka-ai-skills-pack-0.1.0.sh
   ```

6. Publish both files to release tag `v0.1.0`:

   - `dist/akka-ai-skills-pack-0.1.0.tar.gz`
   - `dist/install-akka-ai-skills-pack-0.1.0.sh`

The build refuses to overwrite an existing staged directory, archive, or generated release installer unless `--clean` is passed.

### Build options

Write output somewhere other than `dist/`:

```bash
bash tools/build-pack.sh --clean --output-dir /tmp/akka-pack-builds
```

Override the GitHub release repo used in generated installer URLs:

```bash
bash tools/build-pack.sh --clean --github-repo your-org/your-repo
```

Build only the expanded directory, no archive:

```bash
bash tools/build-pack.sh --clean --no-archive
```

Show help:

```bash
bash tools/build-pack.sh --help
```

### Development install for testing

To test the current source checkout against a separate target project:

```bash
bash install.sh --location project --project /path/to/test/project
```

To test a built archive:

```bash
tar -xzf dist/akka-ai-skills-pack-0.1.0.tar.gz -C /tmp
cd /tmp/akka-ai-skills-pack-0.1.0
bash install.sh --location project --project /path/to/test/project
```

Use `--dry-run` when checking installer behavior without writing files.

### GitHub Actions release flow

Automation is included under `.github/workflows/`:

- `build-test.yml` checks version references, runs Maven verification, and builds the pack on PRs, pushes to `main`, and manual dispatch.
- `cut-tag.yml` creates and pushes tag `v<manifest-version>` from a selected ref after validation.
- `release.yml` builds the versioned archive and installer and attaches them to a draft GitHub Release when the matching tag is pushed.

Release checklist:

1. Update `pack/manifest.yaml` to the intended version.
2. Update versioned references in `README.md` and `pack/README.md`.
3. Merge to `main`.
4. Run the cut-tag workflow or manually push `v<manifest-version>`.
5. Review the generated draft GitHub Release.
6. Publish the draft release.

### What the pack builder validates

`tools/build-pack.sh` checks key source inputs before building, including:

- `skills/README.md`
- `skills/references/...`
- every skill directory having a `SKILL.md`
- `docs/...` files required by the pack manifest
- `pom.xml`
- `README.md`
- `LICENSE`
- `pack/README.md`
- `pack/AGENTS.md`
- `pack/EXAMPLES-README.md`
- `pack/manifest.schema.yaml`

`tools/check-version-consistency.sh` verifies that documented artifact and release references match the current manifest version.

### License

See `LICENSE`.

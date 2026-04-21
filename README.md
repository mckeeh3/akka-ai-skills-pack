# Akka AI Resource Pack

This repository packages **Akka SDK knowledge for AI coding agents**.

It combines:
- **agent-optimized skills** under `skills/`
- **executable Akka Java SDK examples** under `src/`
- **tests that double as reference patterns** under `src/test/`
- **packaging and installer tooling** for shipping those resources as an installable distribution
- a local copy of **official Akka reference material** under `akka-context/` for maintainers and source verification

In this repository, `skills/` is the canonical authored source path.
When installed into a target project or user profile, those files land under `.agents/skills/`.

## What this project is for

This is **not just an Akka sample service** and **not just a docs mirror**.

The project exists to translate Akka SDK concepts into forms that AI coding agents can use efficiently:
- small, focused skills
- predictable naming and routing
- example code organized by component type
- tests that show intended calling patterns
- installable bundles that can be dropped into `.agents`

A useful mental model is:
- `akka-context/` explains Akka for humans and serves as a semantic reference
- this repository repackages that knowledge for **AI-agent-oriented implementation workflows**

## What is included

### 1. Source skill library: `skills/`
Agent-routing and implementation skills for:
- Agents
- Event Sourced Entities
- Key Value Entities
- Views
- Workflows
- Consumers
- Timed Actions
- HTTP endpoints
- gRPC endpoints
- MCP endpoints

Start with:
- `AGENT-README.md`
- `AGENTS.md`
- `skills/README.md`

### 2. Executable examples: `src/`
Reference Akka Java SDK code organized as:
- `src/main/java/com/example/domain` - domain logic and immutable models
- `src/main/java/com/example/application` - Akka components
- `src/main/java/com/example/api` - HTTP, gRPC, and MCP endpoints
- `src/main/proto` - protobuf contracts
- `src/test/java` - unit and integration tests

The examples cover patterns such as:
- Event Sourced Entity and Key Value Entity design
- views and query models
- consumers and topic/service-stream integration
- workflows, compensation, pause/resume, and orchestration
- timed actions and timer-backed flows
- agents, tools, structured responses, memory, streaming, guardrails, and evaluation
- HTTP, gRPC, and MCP endpoints
- deterministic testing patterns for each component family

### 3. Reference docs: `docs/`
Focused local reference material for recurring patterns, such as:
- agent coverage
- workflow/endpoint patterns
- timer pattern selection
- consumer references
- service-to-service consumer and view patterns

### 4. Pack metadata and release tooling
- `pack/manifest.yaml` - versioned pack definition
- `pack/README.md` - install layout and bundle model
- `tools/build-pack.sh` - builds a release bundle under `dist/`
- `install.sh` - installs a bundle into a cross-harness `.agents` directory

### 5. Maintainer-only Akka reference mirror: `akka-context/`
This directory is intentionally kept in the repository as a **maintainer/reference input**.

It is used to:
- confirm official Akka semantics
- resolve API details
- fill local coverage gaps

It is **not bundled into installable distributions**.

## Related README files in this repository

This repository contains several README files with different roles:
- `README.md` - this top-level repository overview
- `AGENT-README.md` - startup guidance for AI coding agents working in this repo
- `skills/README.md` - skill routing map across all local skill suites
- `pack/README.md` - install target layout, bundle model, and path rewrite rules
- `akka-specify-plugin/akka/README.md` - `/akka:*` command workflow for the Akka specify plugin
- `dist/.../README.md` and `dist/.../pack/README.md` - generated copies included in built distributions

## Top-level repository layout

```text
.
├── AGENT-README.md          # agent startup instructions
├── AGENTS.md                # detailed project coding constraints
├── skills/                  # source skill library
├── akka-context/            # maintainer/reference Akka docs (not bundled)
├── akka-specify-plugin/     # plugin-specific workflow docs
├── docs/                    # focused local reference docs
├── pack/                    # pack manifest and packaging docs
├── src/                     # executable Akka Java SDK examples
├── tools/build-pack.sh      # distribution builder
├── install.sh               # installer for source checkout or unpacked bundle
└── dist/                    # generated release artifacts
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
- `akka-ai-pack`

Current manifest version:
- `0.1.0`

The distribution includes:
- `skills/**`
- selected pack metadata
- exported examples from `src/main` and `src/test`
- the repository `pom.xml`
- this `README.md`
- `install.sh`
- `LICENSE`

Source skills are authored under `skills/` in this repository and installed into `.agents/skills/` by the installer.

The distribution intentionally excludes:
- `akka-context/**`

During installation, skill files are rewritten so they:
- point to installed example paths under `.agents/resources/examples/java/...`
- no longer contain broken repo-local `akka-context/...` references
- instead reference official Akka SDK docs generically

## Available install bundles

The installer currently supports these bundle ids:
- `all`
- `entities-core`
- `ese-core`
- `kve-core`

Use `entities-core` if you want the current packaged entity-focused suite.
Use `all` if you want everything currently included in the manifest.

## How to build a distribution

### Quick build

From the repository root:

```bash
bash tools/build-pack.sh --clean
```

This creates:
- an expanded bundle directory under `dist/`
- a `.tar.gz` archive for the bundle

With the current manifest, the output names are:
- `dist/akka-ai-pack-0.1.0/`
- `dist/akka-ai-pack-0.1.0.tar.gz`

### Recommended build flow

1. **Verify the example project still builds**

   ```bash
   mvn test
   ```

2. **Build the distribution bundle**

   ```bash
   bash tools/build-pack.sh --clean
   ```

3. **Inspect the staged bundle**

   ```bash
   find dist/akka-ai-pack-0.1.0 -maxdepth 3 | sort
   ```

4. **Use the archive for installation or release**

   ```bash
   ls -lh dist/akka-ai-pack-0.1.0.tar.gz
   ```

### Build options

#### Write output somewhere other than `dist/`

```bash
bash tools/build-pack.sh --clean --output-dir /tmp/akka-pack-builds
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
- `pack/manifest.schema.yaml`
- every skill directory having a `SKILL.md`

## How to install a distribution

You can install either:
- from the **repository checkout** using the root `install.sh`, or
- from an **unpacked distribution bundle** using the bundled `install.sh`

The installation target is always one of these cross-harness locations:
- **project mode**: `<project-root>/.agents`
- **global mode**: `~/.agents`

### 1. Build and unpack the distribution

If you are starting from this repository:

```bash
bash tools/build-pack.sh --clean
```

Then unpack it:

```bash
tar -xzf dist/akka-ai-pack-0.1.0.tar.gz -C /tmp
cd /tmp/akka-ai-pack-0.1.0
```

If you already received a built archive from somewhere else, unpack that archive instead.

### 2. Inspect available bundles

From inside the unpacked bundle:

```bash
bash install.sh --list-bundles
```

### 3. Install to a project-specific `.agents` directory

```bash
bash install.sh \
  --location project \
  --project /path/to/your/project \
  --bundle entities-core
```

This installs into:
- `/path/to/your/project/.agents/skills`
- `/path/to/your/project/.agents/manifests`
- `/path/to/your/project/.agents/resources/examples/java`

### 4. Install globally for your user account

```bash
bash install.sh --location global --bundle entities-core
```

This installs into:
- `~/.agents/skills`
- `~/.agents/manifests`
- `~/.agents/resources/examples/java`

### 5. Interactive install

If you omit `--location`, the installer prompts you to choose project or global mode:

```bash
bash install.sh --bundle entities-core
```

### Useful installer options

#### Dry run

```bash
bash install.sh --location project --project /path/to/project --bundle all --dry-run
```

#### Show help

```bash
bash install.sh --help
```

## Installed layout

After installation, the target directory looks like this:

```text
.agents/
├── manifests/
│   └── akka-ai-pack.yaml
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
    ├── akka-entity-type-selection/
    ├── akka-event-sourced-entities/
    └── ...
```

## Source install vs distribution install

### Install directly from a source checkout
Useful during development:

```bash
bash install.sh --location project --project /path/to/project --bundle all
```

### Install from a built distribution
Useful for release testing and sharing:

```bash
tar -xzf akka-ai-pack-0.1.0.tar.gz
cd akka-ai-pack-0.1.0
bash install.sh --location global --bundle all
```

In both cases, the installer performs the same target layout and path rewriting behavior.

## Akka specify plugin note

The nested plugin README at `akka-specify-plugin/akka/README.md` documents a separate command-driven workflow:
- `/akka:setup`
- `/akka:specify`
- `/akka:clarify`
- `/akka:plan`
- `/akka:tasks`
- `/akka:implement`
- `/akka:build`
- `/akka:deploy`
- `/akka:review`

That workflow is related to Akka development ergonomics, but it is separate from the resource-pack build and install flow described above.

## Recommended reading order for maintainers and coding agents

1. `AGENT-README.md`
2. `AGENTS.md`
3. `skills/README.md`
4. the focused skill(s) for the task at hand
5. `akka-context/sdk/...` when official Akka semantics or API confirmation is needed

## License

See `LICENSE`.

# Akka AI Skills Pack Developer Guide

This guide is for contributors maintaining this repository and producing releases of the `akka-ai-skills-pack`.

For user-facing installation and usage guidance, see [`skills-pack-user-guide.md`](skills-pack-user-guide.md).

## Repository purpose

This repository is the **source project for the Akka AI Skills Pack**. It is not primarily an end-user Akka application.

The pack turns Akka SDK knowledge into AI-first SaaS and capability-first backend skills, examples, and guidance optimized for AI coding agents. The installable output is a `.agents/` resource pack that downstream projects can install at either:

- `<target-project>/.agents`
- `~/.agents`

The Akka Java SDK code under `src/` is executable reference material used to validate and demonstrate pack guidance.

## Important source vs installed distinction

- `AGENTS.md` is repository-internal maintainer guidance.
- `pack/AGENTS.md` is installed as `.agents/AGENTS.md` for downstream projects.
- `skills/` is the source skill library.
- `docs/` contains pack-facing doctrine, references, and examples.
- `docs/examples/` contains reference assets for the pack, not this repository's own business app description.
- `src/` contains reference Akka Java SDK examples and tests.
- `akka-context/` is official Akka reference material for maintainers and is not installed in the pack.

## Source repository layout

```text
.
├── AGENTS.md                         # repository-internal maintainer guidance
├── README.md                         # high-level user-facing introduction
├── docs/                             # pack-facing reference docs and examples
├── pack/                             # manifest, packaging docs, installed AGENTS source
├── skills/                           # source skill library
├── src/                              # executable Akka Java SDK reference examples
├── frontend/                         # frontend seed shell/reference assets
├── templates/                        # scaffoldable starter app template source
├── tools/scaffold-ai-first-saas-starter.sh
├── tools/build-pack.sh               # distribution builder
├── tools/check-version-consistency.sh
├── tools/install-release-template.sh
├── tools/release.sh                  # release helper
├── install.sh                        # installer for source checkout or unpacked bundle
└── dist/                             # generated release artifacts, not source-controlled
```

## Development prerequisites

Use:

- JDK 21
- Maven 3.x
- Node.js and npm for frontend/static web UI example builds
- bash
- python3
- standard archive tools such as `tar` and `gzip`

The Maven example project currently inherits from `io.akka:akka-javasdk-parent:3.5.18`.

## Recommended reading order for maintainer work

1. `AGENTS.md`
2. `skills/README.md`
3. task-specific source skill files under `skills/`
4. relevant local docs under `docs/`
5. official Akka SDK material under `akka-context/` when SDK semantics or API confirmation is needed

For high-level AI-first SaaS, routing, doctrine, app-description, PRD/spec/backlog, or skill-design work, also read:

- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`

## Maintaining skills and docs

The source skill library is an internal routing and implementation layer for AI harnesses. Maintain it so that:

- users can speak naturally without knowing skill names
- broad intent is interpreted through secure AI-first SaaS operating-model concepts when applicable
- secure SaaS foundation work comes before domain-specific features
- backend behavior is modeled as governed capabilities before Akka components or agent tools are selected
- broad intent is decomposed before coding
- open decisions are queued instead of guessed
- pending tasks are sized for focused harness runs
- only the smallest relevant guidance is loaded for a task

When adding or revising skills, optimize for:

1. agent usefulness
2. token efficiency
3. correct Akka semantics
4. consistency with repository conventions
5. human readability

## Build and test the reference app

Use these commands from the repository root.

Compile:

```bash
mvn compile
```

Run tests:

```bash
mvn test
```

Run full Maven verification:

```bash
mvn verify
```

Build and install the local Maven artifact:

```bash
mvn clean install
```

Run the opinionated AI-first SaaS pack guardrail before changing core routing, doctrine, packaging, onboarding, user-admin, or AI-admin guidance:

```bash
npm run verify:opinionated-ai-first-saas
```

## Local environment for the seed app

The repository provides a root environment template:

```bash
cp .env.example .env
# edit .env and provide local values
set -a
source .env
set +a
```

Do not commit `.env`. Backend secrets belong in the root `.env`, shell environment, deployment secret store, or another backend-only mechanism. Do not put backend secrets in `frontend/.env.local` or built static assets.

Common variables include:

- `ADMIN_USERS`
- `OPENAI_API_KEY`
- `WORKOS_API_KEY`
- `WORKOS_API_BASE_URL`
- `WORKOS_JWT_ISSUER`
- `WORKOS_JWT_AUDIENCE`
- `APP_PUBLIC_BASE_URL`
- `RESEND_API_KEY`
- `RESEND_FROM_EMAIL` or feature-specific senders such as `INVITE_EMAIL_FROM`
- `INVITE_EMAIL_SUBJECT`
- `VITE_WORKOS_CLIENT_ID`
- `VITE_WORKOS_REDIRECT_URI`

Only `VITE_` variables are public and may be embedded into the frontend bundle.

## Running the seed app locally

The seed app declares topic components. Local Akka broker support is disabled by default, so start a local Kafka broker on `localhost:9092` or use another Akka-supported local broker configuration.

One Docker option:

```bash
docker run --rm --name akka-seed-kafka \
  -p 9092:9092 \
  apache/kafka:3.9.1
```

Start the seed app with Kafka dev-mode eventing support:

```bash
mvn compile exec:java -Dakka.javasdk.dev-mode.eventing.support=kafka
```

## Frontend reference checks

Build/check TypeScript web UI reference files under `src/main/web-ui`:

```bash
npm install
npm run check:web-ui
npm run build:web-ui
```

Build and test the Vite/React frontend seed shell under `frontend/`:

```bash
cd frontend
npm install
npm test
npm run build
```

Alternatively, create `frontend/.env.local` from `frontend/.env.example` and set only public WorkOS/AuthKit browser values there. Never add backend secrets such as `WORKOS_API_KEY`, `RESEND_API_KEY`, `ADMIN_USERS`, `OPENAI_API_KEY`, invite sender settings, JWT key material, or service credentials to `frontend/.env.local`.

## Distribution model

The installable resource pack is named `akka-ai-skills-pack`.

The distribution includes:

- source-authored skills copied from `skills/**`
- selected pack-facing docs from `docs/**`
- selected reference examples exported from `src/main` and `src/test`
- starter template resources exported from `templates/ai-first-saas-starter/**`
- explicit starter scaffold command exported from `tools/scaffold-ai-first-saas-starter.sh`
- `pom.xml` and example support files
- `README.md`
- `install.sh`
- `LICENSE`
- `pack/AGENTS.md` as the source for installed `.agents/AGENTS.md`
- `pack/EXAMPLES-README.md` as the source for installed `.agents/resources/examples/java/README.md`
- `pack/manifest.yaml` as installed manifest metadata

The installed pack intentionally excludes repository-internal maintainer-only guidance, including:

- root `AGENTS.md`
- `akka-context/**`

During installation, copied skill files are rewritten so installed references point to `.agents/` paths and do not depend on repository-local maintainer paths.

Default installs are skills/resource-only. Starter application files are copied into a target project only when the user explicitly runs `.agents/bin/scaffold-ai-first-saas-starter.sh`; the scaffold command is fail-closed and should be validated with `--dry-run` before release.

## Build a distribution

Quick build:

```bash
bash tools/build-pack.sh --clean
```

This creates versioned outputs under `dist/`, including:

- `dist/akka-ai-skills-pack-<version>/`
- `dist/akka-ai-skills-pack-<version>.tar.gz`
- `dist/install-akka-ai-skills-pack-<version>.sh`

Build options:

```bash
bash tools/build-pack.sh --clean --output-dir /tmp/akka-pack-builds
bash tools/build-pack.sh --clean --github-repo your-org/your-repo
bash tools/build-pack.sh --clean --no-archive
bash tools/build-pack.sh --help
```

The builder refuses to overwrite existing staged directories, archives, or generated release installers unless `--clean` is passed.

## Development install for testing

Install the current source checkout into a separate target project:

```bash
bash install.sh --location project --project /path/to/test/project
```

Test a built archive:

```bash
tar -xzf dist/akka-ai-skills-pack-<version>.tar.gz -C /tmp
cd /tmp/akka-ai-skills-pack-<version>
bash install.sh --location project --project /path/to/test/project
/path/to/test/project/.agents/bin/scaffold-ai-first-saas-starter.sh --target /path/to/test/project --app-name "Test App" --base-package ai.first --dry-run
```

Run the scaffold without `--dry-run` only in a disposable empty target and verify `specs/scaffold-report.md` plus rendered backend paths.

Use `--dry-run` to check installer behavior without writing files.

## Recommended release flow

Use:

```bash
bash tools/release.sh
```

The release script:

1. requires a clean git working tree
2. shows the current manifest version and latest local tag
3. prompts for the next release version
4. updates tracked text files that contain the current hardcoded version
5. runs `tools/check-version-consistency.sh`
6. runs `mvn verify --no-transfer-progress`
7. builds the versioned archive and release installer under `dist/`
8. commits the version changes
9. creates an annotated git release tag
10. asks whether to push the release commit and tag
11. when `gh` is installed and the tag was pushed, asks whether to create/update the GitHub release and publish it so curl install URLs work

## GitHub Actions release flow

Automation is included under `.github/workflows/`:

- `build-test.yml` checks version references, runs Maven verification, and builds the pack on PRs, pushes to `main`, and manual dispatch.
- `cut-tag.yml` creates and pushes tag `v<manifest-version>` from a selected ref after validation.
- `release.yml` builds the versioned archive and installer and attaches them to a draft GitHub Release when the matching tag is pushed.

Release checklist:

1. Update `pack/manifest.yaml` to the intended version.
2. Update versioned references in `README.md`, `docs/skills-pack-user-guide.md`, and `pack/README.md` if needed.
3. Merge to `main`.
4. Run the cut-tag workflow or manually push `v<manifest-version>`.
5. Review the generated draft GitHub Release.
6. Publish the draft release.

## What the pack builder validates

`tools/build-pack.sh` checks key source inputs before building, including:

- `skills/README.md`
- skill reference files
- every skill directory having a `SKILL.md`
- docs required by the pack manifest/build list
- `pom.xml`
- `README.md`
- `LICENSE`
- `pack/README.md`
- `pack/AGENTS.md`
- `pack/EXAMPLES-README.md`
- `pack/manifest.schema.yaml`

`tools/check-version-consistency.sh` verifies that documented artifact and release references match the current manifest version.

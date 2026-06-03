# Akka AI Skills Pack Developer Guide

This guide is for contributors maintaining the isolated `skills-pack/` source area and producing releases.

For user-facing installation and usage guidance, see [`skills-pack-user-guide.md`](skills-pack-user-guide.md).

## Repository model

The repository root is the canonical runnable Akka Java SDK + React/Vite core app. The installable skills pack source lives under `skills-pack/`.

Skills-pack work includes:

- `skills-pack/skills/**`
- `skills-pack/docs/**`
- `skills-pack/pack/**`
- `skills-pack/examples/**`
- `skills-pack/tools/**`
- `skills-pack/install.sh`

Do not add core app runtime code under `skills-pack/**`. Do not add skills-pack reference examples back into root `src/**`.

## Source vs installed distinction

- root `AGENTS.md` — contributor guidance for the runnable core app plus the skills-pack pointer
- `skills-pack/AGENTS.md` — maintainer guidance for pack source work
- `skills-pack/pack/AGENTS.md` — installed as `.agents/AGENTS.md` for downstream projects
- `skills-pack/skills/**` — source skill library
- `skills-pack/docs/**` — pack-facing doctrine, references, and mechanics examples
- `skills-pack/examples/akka-components/**` — focused Akka Java reference examples
- root `frontend/**` — core app frontend and exported frontend workstream reference
- `skills-pack/akka-context/**` — official Akka reference material for maintainers only; not installed

## Source layout

```text
skills-pack/
├── AGENTS.md
├── README.md
├── install.sh
├── pack/
│   ├── AGENTS.md
│   ├── README.md
│   └── manifest.yaml
├── skills/
├── docs/
├── examples/
│   └── akka-components/
├── akka-context/
├── tools/
└── dist/
```

## Maintaining skills and docs

Maintain the pack so downstream harnesses:

- interpret broad product input as secure AI-first SaaS unless explicitly out of scope
- model governed backend capabilities before choosing Akka components or exposing tools
- preserve mandatory identity, tenancy, authorization, audit, UI, and governed agent runtime foundations
- queue unresolved decisions instead of guessing
- execute one pending task per fresh context
- validate runtime features through real local Akka/API/UI paths
- use `domain-specific` or the user's actual domain name for app follow-up work

The pack no longer owns a duplicate full-app core app baseline. Guidance for a new implementation baseline should point users to fork/copy the upstream runnable core app repository root, then extend their own workspace.

## Common checks

Run the smallest check set that proves the task. Common pack checks:

```bash
git diff --check
./install.sh --location project --project /tmp/akka-install-dry-run --dry-run
bash tools/build-pack.sh --github-repo example/repo --output-dir /tmp/akka-pack-build-check --clean --no-archive
bash tools/verify-opinionated-ai-first-saas-pack.sh
```

When root app runtime or frontend files are touched, also run the relevant root checks from root `AGENTS.md`.

## Distribution model

The installable resource pack is named `akka-ai-skills-pack`.

The distribution includes:

- source-authored skills copied from `skills-pack/skills/**`
- pack-facing docs copied from `skills-pack/docs/**`
- focused Java examples copied from `skills-pack/examples/akka-components/**`
- frontend workstream reference source copied from root `frontend/**`
- root `pom.xml` for Java example support
- `skills-pack/install.sh`
- root `LICENSE`
- `skills-pack/pack/AGENTS.md` as installed `.agents/AGENTS.md`
- `skills-pack/pack/EXAMPLES-README.md` as installed `.agents/resources/examples/java/README.md`
- `skills-pack/pack/manifest.yaml` as installed manifest metadata

The distribution excludes:

- root app runtime source as application source
- `skills-pack/akka-context/**`
- repository-internal maintainer-only guidance
- duplicate full-app core app baselines or scaffold commands

During installation, copied skill and doc files are rewritten so installed references point to `.agents/` paths and do not depend on maintainer checkout paths.

## Build a distribution

From `skills-pack/`:

```bash
bash tools/build-pack.sh --clean
```

Useful options:

```bash
bash tools/build-pack.sh --clean --output-dir /tmp/akka-pack-builds
bash tools/build-pack.sh --clean --github-repo your-org/your-repo
bash tools/build-pack.sh --clean --no-archive
bash tools/build-pack.sh --help
```

## Development install for testing

From `skills-pack/`:

```bash
bash install.sh --location project --project /path/to/test/project --dry-run
bash install.sh --location project --project /path/to/test/project
```

Test a built archive:

```bash
tar -xzf dist/akka-ai-skills-pack-<version>.tar.gz -C /tmp
cd /tmp/akka-ai-skills-pack-<version>
bash install.sh --location project --project /path/to/test/project --dry-run
```

## Release flow

Use `skills-pack/tools/release.sh` when cutting a release. The release flow should check version consistency, run relevant root/pack tests, build the versioned archive and release installer, commit version changes, tag the release, and publish GitHub release assets when requested.

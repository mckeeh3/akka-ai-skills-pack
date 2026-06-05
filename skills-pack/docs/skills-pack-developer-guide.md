# Akka AI Skills Pack Developer Guide

This guide is for contributors maintaining the isolated `skills-pack/` source area and producing releases.

For user-facing installation and usage guidance, see [`skills-pack-user-guide.md`](skills-pack-user-guide.md).

## Repository model

The repository root is the canonical runnable Akka Java SDK + React/Vite core app. The skills library source lives under `skills-pack/`; `skills-pack/skills/**`, shared skill references, and referenced pack docs/examples/templates/tools are installed into a harness skills directory.

Skills-pack work includes:

- `skills-pack/skills/**`
- `skills-pack/docs/**`
- `skills-pack/pack/**`
- `skills-pack/examples/**`
- `skills-pack/tools/**`
- `skills-pack/install-skills.sh`

Do not add core app runtime code under `skills-pack/**`. Do not add skills-pack reference examples back into root `src/**`.

## Source vs installed distinction

- root `AGENTS.md` — contributor guidance for the runnable core app plus the skills-pack pointer
- `skills-pack/AGENTS.md` — maintainer guidance for pack source work
- `skills-pack/pack/AGENTS.md` — source metadata guidance only; not installed
- `skills-pack/skills/**` — source skill library copied/symlinked to `.agents/skills`
- `skills-pack/docs/**` — pack doctrine, references, and mechanics examples copied/symlinked to `.agents/skills/docs`
- `skills-pack/examples/**` — focused reference examples copied/symlinked to `.agents/skills/examples`
- `skills-pack/templates/**` — pack templates copied/symlinked to `.agents/skills/templates`
- `skills-pack/tools/**` — pack tools copied/symlinked to `.agents/skills/tools`
- root `frontend/**` — core app frontend and source-checkout frontend workstream reference; not installed into `.agents`
- root `akka-context/**` — official Akka reference material for maintainers only; not installed because standard Akka projects keep `akka-context/**` as an independently maintained top-level directory

## Source layout

```text
skills-pack/
├── AGENTS.md
├── README.md
├── install-skills.sh
├── pack/
│   ├── AGENTS.md
│   ├── README.md
│   └── manifest.yaml
├── skills/
├── docs/
├── examples/
│   └── akka-components/
└── tools/

../akka-context/   # repository-top-level Akka reference material, not installed
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

The skills library no longer owns a duplicate full-app core app baseline. Guidance for a new implementation baseline should point users to fork or clone this repository and extend the runnable root app workspace.

## Common checks

Run the smallest check set that proves the task. Common pack checks:

```bash
git diff --check
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check
bash tools/verify-opinionated-ai-first-saas-pack.sh
```

When root app runtime or frontend files are touched, also run the relevant root checks from root `AGENTS.md`.

## Installation model

The installable resource pack is named `akka-ai-skills-pack`, but the repository checkout/tag is now the unit of installation. Normal use does not require a generated distribution archive.

The current installer copies or symlinks:

- `skills-pack/skills/README.md`
- `skills-pack/skills/references/**`
- every `skills-pack/skills/<skill-name>/SKILL.md` skill directory

The installer writes `<target-skills-dir>/.akka-ai-skills-pack-install-manifest`. That manifest records pack-owned installed paths so `--prune` can remove retired skills and `--uninstall` can remove this pack without deleting unrelated harness skills.

## Development install for testing

From `skills-pack/`:

```bash
bash install-skills.sh --target /path/to/project/.agents/skills --dry-run
bash install-skills.sh --target /path/to/project/.agents/skills --prune
bash install-skills.sh --target /path/to/project/.agents/skills --check
```

Useful options:

```bash
bash install-skills.sh --location project --project /path/to/project
bash install-skills.sh --location global
bash install-skills.sh --mode symlink --target /path/to/project/.agents/skills
bash install-skills.sh --uninstall --target /path/to/project/.agents/skills
```

## Release flow

Use `skills-pack/tools/release.sh` when cutting a release. The reduced flow checks version consistency, verifies the skills installer, runs whitespace checks, commits version changes, and creates an annotated tag. It does not build or publish distribution assets.

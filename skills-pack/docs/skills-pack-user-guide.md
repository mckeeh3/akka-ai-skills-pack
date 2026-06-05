# Akka AI Skills Pack User Guide

This guide is for developers using this repository with an AI coding harness.

The repository contains both the runnable Akka full-stack core app and the Akka AI skills library. The skills library is guidance and routing for the harness; it does not provide a separate callable framework, duplicate app baseline, or generated distribution bundle. The installer does install pack docs/examples/templates/tools referenced by skills under `.agents/skills/**`. The default generated-product target remains full-stack secure AI-first SaaS on Akka, implemented through capability-first backend architecture and validated through real local Akka/API/UI paths.

## Core idea

Keep durable project state in the application workspace:

```text
docs/input/          # human-authored PRDs, issues, notes, test findings
app-description/     # harness-maintained source of truth
specs/               # plans, questions, queues, sprints, backlogs, task briefs
src/                 # generated or maintained backend source
frontend/            # generated or maintained browser app
```

The installed `.agents/skills` directory is a harness support library. Do not treat it as application source, documentation storage, examples storage, or app-description/spec storage.

For a new app with an implementation baseline, fork or clone this repository, then extend the root app workspace. For an existing app, install the skills library into that app's harness skills directory and let the harness update the existing app-description/specs/source incrementally.

## Make the skills available to the harness

Current manifest version: `0.3.0`

Clone or check out the desired release tag. This repository clone/fork is the source attention for the runnable core app. Then copy or symlink the skills library, including referenced pack docs/examples/templates/tools, into your harness skills directory:

```bash
git clone https://github.com/mckeeh3/akka-ai-skills-pack.git
cd akka-ai-skills-pack
./install-skills.sh --target /path/to/project/.agents/skills --prune
```

Global install:

```bash
./install-skills.sh --location global --prune
```

Development symlink install:

```bash
./install-skills.sh --mode symlink --target /path/to/project/.agents/skills --prune
```

Dry run and check:

```bash
./install-skills.sh --target /path/to/project/.agents/skills --dry-run
./install-skills.sh --target /path/to/project/.agents/skills --check
```

The installer creates or updates only the harness skills directory. It writes `.akka-ai-skills-pack-install-manifest` in that directory so `--prune` can remove retired pack-owned entries and `--uninstall` can remove this skills library without deleting unrelated skills. It installs referenced pack assets under `.agents/skills/**`; it does not install `.agents/AGENTS.md`, `.agents/docs`, `.agents/resources/examples`, manifests, `akka-context/**`, target-application backend/frontend source, or a duplicate app baseline. Some installed `examples/**` files are read-only Java source snapshots for pattern lookup; they are not installed as the target app's `src/**`, are not independently buildable, and must not be copied wholesale as an application baseline. Keep `akka-context/**` as an independently maintained top-level project/repository directory when Akka SDK reference docs are needed.

Project-file references inside skills, such as `../../../AGENTS.md`, `../../../specs/**`, `../../../app-description/**`, `../../../frontend/**`, and `../../../src/**`, mean the target project workspace. They are not global-install-relative paths when skills are installed under `~/.agents/skills`.

## Choose a starting mode

### Existing application or PRD-first app

Use the harness skills-library install, put source input under `docs/input/`, then ask the harness to maintain `app-description/`, `specs/`, and `specs/pending-tasks.md` before coding. The harness should queue questions instead of guessing and execute one task per fresh session.

### New app with baseline implementation

Fork or clone this repository as the baseline. Keep the fixed `ai.first` package. Add product/domain input under `docs/input/`, then ask the harness to reconcile it into the existing root `app-description/`, `specs/`, backend, and frontend. Extend with `domain-specific` packages and frontend/app-description extension zones rather than editing skills-pack internals.

## Runtime validation expectations

A generated-app feature is complete only when the intended local runtime path works at the stated scope. For secure AI-first SaaS work, normal behavior must go through the generated Akka components, protected APIs, authorization checks, durable state, provider boundaries, audit/work traces, and browser UI where applicable.

Model-backed workstream agents must invoke a concrete Akka `Agent` component through the governed runtime path: active `AgentDefinition`, prompt/skill/reference manifest resolution, authorized `readSkill`/`readReferenceDoc`, `ToolPermissionBoundary` enforcement, `effects().tools(runtimeTools)`, provider invocation, and durable traces. Missing provider/security configuration should fail closed with actionable errors, not deterministic placeholder success.

## Installed layout

```text
.agents/
└── skills/
    ├── .akka-ai-skills-pack-install-manifest
    ├── README.md
    ├── references/
    ├── docs/
    ├── examples/
    ├── templates/
    ├── tools/
    └── <skill-name>/SKILL.md
```

Important installed files:

- `.agents/skills/README.md` — routing map for selecting the smallest relevant skills
- `.agents/skills/.akka-ai-skills-pack-install-manifest` — ownership manifest used by prune/uninstall
- `.agents/skills/references/` — shared skill reference files
- `.agents/skills/docs/`, `.agents/skills/examples/`, `.agents/skills/templates/`, `.agents/skills/tools/` — pack assets referenced by installed skills

Important project/application references remain in the cloned/forked repository, not `.agents`: root `frontend/**`, root `src/**`, `app-description/**`, and `specs/**`. Akka SDK context remains in the top-level `akka-context/**` directory when present.

## Recommended harness prompts

Planning from input:

```text
First read the repository root AGENTS.md and .agents/skills/README.md.
Then read all files under docs/input/initial/.
Bootstrap or update app-description, specs, pending questions, and pending tasks before coding.
Queue questions instead of guessing. Do not mark runtime features complete without real local Akka/API/UI validation.
```

Executing the queue:

```text
Use the Akka skills pack to do the next pending task from specs/pending-tasks.md.
Execute only that one task, load only its required reads and listed skills, update its status, commit the task changes, and report the next runnable task.
```

## Pack vs app boundaries

- Edit `.agents/skills` only when refreshing the harness-visible skills copy/symlink.
- Put product requirements and issue notes under `docs/input/`.
- Put application source under the target project's `src/`, `frontend/`, `app-description/`, and `specs/`.
- Use `domain-specific` or the user's actual domain name for app-specific follow-up work; do not use historical example names as generic placeholders.

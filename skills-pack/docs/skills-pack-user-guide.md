# Akka AI Skills Pack User Guide

This guide is for developers using the installed Akka AI Skills Pack with an AI coding harness.

The pack is a guidance, routing, and reference library. It does not provide a callable application framework and it no longer installs a duplicate full-app core app baseline. The default generated-product target remains full-stack secure AI-first SaaS on Akka, implemented through capability-first backend architecture and validated through real local Akka/API/UI paths.

## Core idea

Keep durable project state in the application workspace:

```text
docs/input/          # human-authored PRDs, issues, notes, test findings
app-description/     # harness-maintained source of truth
specs/               # plans, questions, queues, sprints, backlogs, task briefs
src/                 # generated or maintained backend source
frontend/            # generated or maintained browser app
```

The installed `.agents/` directory is the harness support library. Do not treat it as application source.

For a new app with an implementation baseline, fork or copy from the upstream runnable core app repository root, then extend that workspace. For an existing app, install the pack and let the harness update the existing app-description/specs/source incrementally.

## Install the skills

Current manifest version: `0.3.0`

Clone or check out the desired release tag, then install the skills into your harness skills directory:

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

The installer creates or updates only the harness skills directory. It writes `.akka-ai-skills-pack-install-manifest` in that directory so `--prune` can remove retired pack-owned skills and `--uninstall` can remove this pack without deleting unrelated skills.

## Choose a starting mode

### Existing application or PRD-first app

Use a skills-only install, put source input under `docs/input/`, then ask the harness to maintain `app-description/`, `specs/`, and `specs/pending-tasks.md` before coding. The harness should queue questions instead of guessing and execute one task per fresh session.

### New app with baseline implementation

Fork or copy the upstream runnable core app repository root as the baseline. Keep the default `ai.first` package unless you deliberately perform a product package rename. Add product/domain input under `docs/input/`, then ask the harness to reconcile it into the existing root `app-description/`, `specs/`, backend, and frontend. Extend with `domain-specific` packages and frontend/app-description extension zones rather than editing pack internals.

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
    └── <skill-name>/SKILL.md
```

Important files:

- `.agents/skills/README.md` — routing map for selecting the smallest relevant skills
- `.agents/skills/.akka-ai-skills-pack-install-manifest` — ownership manifest used by prune/uninstall
- `.agents/skills/references/` — shared skill reference files
- `.agents/resources/examples/frontend/` — React/Vite workstream UI reference source

## Recommended harness prompts

Planning from input:

```text
First read .agents/AGENTS.md and .agents/skills/README.md.
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

- Edit `.agents/` only when maintaining or upgrading the installed pack.
- Put product requirements and issue notes under `docs/input/`.
- Put application source under the target project's `src/`, `frontend/`, `app-description/`, and `specs/`.
- Use `domain-specific` or the user's actual domain name for app-specific follow-up work; do not use historical example names as generic placeholders.

# Skills-Pack Maintainer Guidance

This directory is the source area for the installable Akka AI skills pack inside the core-app-first repository.

## Scope

Work here when a task targets installable `.agents` assets:

- `skills-pack/skills/**`
- `skills-pack/docs/**`
- `skills-pack/pack/**`
- `skills-pack/examples/**`
- `skills-pack/tools/**`
- `skills-pack/install-skills.sh`

The repository root is the canonical runnable Akka Java SDK + React/Vite core app. Do not add core app runtime code under `skills-pack/**`. Do not move focused skills-pack examples back into root `src/**`.

## Skills-only harness install model

This repository is the product baseline: pack users clone or fork this repo, run the root Akka full-stack app, and build their domain-specific workstreams in the root app workspace. There is no separate full-pack installer, generated distribution bundle, or installed duplicate app baseline.

The only install step copies or symlinks `skills-pack/skills/**` plus shared `skills-pack/skills/references/**` into a harness-accessible skills directory such as `.agents/skills` or `~/.agents/skills`. The installed `.agents/` directory is a support library for the harness, not the target application's source tree, documentation tree, examples tree, or app-description/spec storage location.

Pack docs, examples, templates, `akka-context/**`, manifests, and tools remain source-checkout assets under `skills-pack/**` unless a future task explicitly changes the skills-only install contract.

## Runtime completion doctrine

Do not teach downstream harnesses to count deterministic/demo/mock/simulated/model-less normal runtime behavior as implemented for generated-app auth, durability, provider calls, protected capabilities, authorization denials, audit/work traces, or workstream agents.

Model-backed workstream agents must invoke a concrete Akka `Agent` component through the governed runtime path with active configuration, governed loader tools, tool permission boundaries, registered runtime tools, and durable traces. Missing provider or security configuration should fail closed with actionable errors.

## Required checks

Choose the smallest checks that prove the task. Common skills-pack checks are:

```bash
git diff --check
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check
bash tools/verify-opinionated-ai-first-saas-pack.sh
```

Use root app checks only when a task explicitly touches root app runtime/frontend paths.

## Path rules

- Pack source paths are relative to `skills-pack/`.
- Installed-skill guidance may refer to `.agents/skills` for loaded skills and shared skill references only; it must not imply `.agents/docs`, `.agents/resources/examples`, templates, manifests, or application source are installed.
- Root app guidance should remain in root `AGENTS.md`, `README.md`, `docs/**`, `app-description/**`, and runtime source paths.
- Use `domain-specific` or the user's actual domain name for app-specific follow-up work; do not use historical placeholder names as generic labels.

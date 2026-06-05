# Skills-Pack Maintainer Guidance

This directory is the source area for the installable Akka AI skills pack inside the secure AI-first SMB SaaS core app repository.

The repository has a two-fold purpose: maintain the harness skills library and provide the runnable core app that users clone or fork. The skills pack supports both purposes by giving harness agents focused guidance, referenced docs, templates, tools, and code examples for maintaining the core app and adding downstream business-specific SaaS domains, workstreams, surfaces, agents, Akka components, frontend extensions, app-description extensions, specs, docs, and tests.

## Scope

Work here when a task targets installable `.agents` assets:

- `skills-pack/skills/**`
- `skills-pack/docs/**`
- `skills-pack/pack/**`
- `skills-pack/examples/**`
- `skills-pack/tools/**`
- `skills-pack/install-skills.sh`

The repository root is the canonical runnable secure AI-first SMB SaaS core app: an Akka Java SDK + React/Vite application with the built-in five core workstreams. Do not add core app runtime code under `skills-pack/**`. Do not move focused skills-pack examples back into root `src/**`.

## Harness install model

This repository is the product baseline: pack users clone or fork this repo, run the root Akka full-stack app, and build their business-specific domains and workstreams in the root app workspace. The skills install supports harness-assisted development of that app; it is not a separate full-pack installer, generated distribution bundle, or installed duplicate app baseline.

The install step copies or symlinks `skills-pack/skills/**`, shared `skills-pack/skills/references/**`, and referenced pack assets such as `skills-pack/docs/**`, curated `skills-pack/examples/**`, `skills-pack/templates/**`, and downstream-safe `skills-pack/tools/**` into a harness-accessible skills directory such as `.agents/skills` or `~/.agents/skills`. The installed `.agents/skills` directory is a support library for the harness, not the target application's source tree, app-description/spec storage location, or duplicate app baseline. Code examples under `skills-pack/examples/**` exist to guide real code generation and implementation decisions; they are not application source to copy wholesale. Maintainer-only release/version tooling belongs under `skills-pack/pack/maintainer/**`, not in the installed tools payload.

`akka-context/**` is the exception: it is official Akka reference material maintained independently by the Akka team and expected as a top-level, usually git-ignored project/repository directory. Do not install it into `.agents/skills`; installed skill references should point to the top-level `akka-context/**` location.

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
bash pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh
```

Use root app checks only when a task explicitly touches root app runtime/frontend paths.

## Path rules

- Pack source paths are relative to `skills-pack/`.
- Installed-skill guidance may refer to pack docs/examples/templates/tools under `.agents/skills/**` and shared skill references under `.agents/skills/references/**`; it must not imply `.agents/docs`, `.agents/resources/examples`, manifests, application source, or `akka-context/**` are installed under `.agents`.
- Keep source assets in their canonical top-level directories (`skills/`, `docs/`, `examples/`, `templates/`, `tools/`); do not add installed-layout symlinks inside `skills/`.
- Root app guidance should remain in root `AGENTS.md`, `README.md`, `docs/**`, `app-description/**`, and runtime source paths.
- Use `domain-specific` or the user's actual domain name for app-specific follow-up work; do not use historical placeholder names as generic labels.

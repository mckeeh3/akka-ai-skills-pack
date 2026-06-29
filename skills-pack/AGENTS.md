# Skills-Pack Maintainer Guidance

This directory is the opt-in skills-pack maintenance mode for the installable Akka AI skills pack inside the SaaS Foundation App repository.

Use this mode only when the user explicitly mentions skills-pack, `.agents/skills`, skill authoring, installer behavior, package manifests, reusable examples/templates/docs, installed-tool payloads, or release tooling. Otherwise, default to root app realization and follow the repository root `AGENTS.md` plus the nearest root app directory `AGENTS.md`.

The repository has a two-fold purpose: maintain the harness skills library and provide the runnable SaaS Foundation App that users clone or fork. The skills pack supports both purposes by giving harness agents focused guidance, referenced docs, templates, tools, and code examples for maintaining the SaaS Foundation App and adding downstream business-specific SaaS domains, workstreams, surfaces, agents, Akka components, frontend extensions, app-description extensions, specs, docs, and tests.

The canonical app development lifecycle is the three-phase Interview → Build/compile → Runtime validation loop documented in `docs/app-development-lifecycle.md` and exposed directly through `skills/app-development-lifecycle/SKILL.md`. Keep this lifecycle visible in top-level routing whenever changing process, routing, app-description, planning, realization, or verification guidance.

## Scope

Work here when a task targets installable `.agents` assets:

- `skills-pack/skills/**`
- `skills-pack/docs/**`
- `skills-pack/pack/**`
- `skills-pack/examples/**`
- `skills-pack/tools/**`
- `skills-pack/install-skills.sh`

The repository root is the canonical runnable SaaS Foundation App: an Akka Java SDK + React/Vite application with the built-in five core workstreams. Do not add SaaS Foundation App runtime code under `skills-pack/**`. Do not move focused skills-pack examples back into root `src/**`.

## Harness install model

This repository is the product baseline: pack users clone or fork this repo, run the root Akka full-stack app, and build their business-specific domains and workstreams in the root app workspace. The skills install supports harness-assisted development of that app; it is not a separate full-pack installer, generated distribution bundle, or installed duplicate app baseline.

The install step copies or symlinks `skills-pack/skills/**`, shared `skills-pack/references/**`, and referenced pack assets such as `skills-pack/docs/**`, curated `skills-pack/examples/**`, `skills-pack/templates/**`, and downstream-safe `skills-pack/tools/**` into a harness-accessible skills directory such as `.agents/skills` or `~/.agents/skills`. The installed `.agents/skills` directory is a support library for the harness, not the target application's source tree, app-description/spec storage location, or duplicate app baseline. Code examples under `skills-pack/examples/**` exist to guide real code generation and implementation decisions; they are not application source to copy wholesale. Maintainer-only release/version tooling belongs under `skills-pack/pack/maintainer/**`, not in the installed tools payload.

`akka-context/**` is the exception: it is official Akka reference material maintained independently by the Akka team and expected as a top-level, usually git-ignored project/repository directory. Do not install it into `.agents/skills`; installed skill references should point to the top-level `akka-context/**` location.

## Runtime completion doctrine

Do not teach downstream harnesses to count deterministic/demo/mock/simulated/model-less normal runtime behavior as implemented for generated-app auth, durability, provider calls, protected capabilities, authorization denials, audit/work traces, or workstream agents. Skills, docs, and templates must distinguish `surface-ready`, `backend-ready`, `frontend-rendered`, `api-smoked`, `browser-smoked`, `manual-ready`, and `runtime-ready`; only the last level may close a user-visible runtime feature without qualification.

All generated-app model-backed Akka Agents and AutonomousAgents must invoke a concrete Akka `Agent`/`AutonomousAgent` component through the governed runtime path with a governed `AgentDefinition`, runtime-loaded prompt, compact assigned skill list in the system prompt, mandatory `readSkill(skillId)` tool registration, governed loader tools, tool permission boundaries, registered runtime tools resolved from governed logical ids and/or approved Java bindings, and durable traces. Missing provider, security, prompt, skill-manifest, tool-boundary, or runtime configuration should fail closed with actionable errors.

## Required checks

Choose the smallest checks that prove the task. Common skills-pack checks are:

```bash
git diff --check
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check
python3 tools/validate-runtime-completion-evidence.py ../specs/pending-tasks.md  # when validating target app runtime task evidence
bash pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh
```

Use root app checks only when a task explicitly touches root app runtime/frontend paths.

## Path rules

- Pack source paths are relative to `skills-pack/`.
- Installed-skill guidance may refer to pack docs/examples/templates/tools under `.agents/skills/**` and shared skill references under `.agents/skills/references/**`; it must not imply `.agents/docs`, `.agents/resources/examples`, manifests, application source, or `akka-context/**` are installed under `.agents`.
- References written inside `skills/*/SKILL.md` as `../docs/...`, `../references/...`, `../examples/...`, `../templates/...`, or `../tools/...` are intentionally installed-layout relative. Validate them after install with `tools/validate-installed-skill-references.py` or `./install-skills.sh --check`; do not rewrite them to source-layout paths.
- Keep source assets in their canonical top-level directories (`skills/`, `docs/`, `examples/`, `templates/`, `tools/`); do not add installed-layout symlinks inside `skills/`.
- Root app guidance should remain in root `AGENTS.md`, `README.md`, `docs/**`, `app-description/**`, and runtime source paths.
- Use `domain-specific` or the user's actual domain name for app-specific follow-up work; do not use historical placeholder names as generic labels.

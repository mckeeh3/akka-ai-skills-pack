# Akka AI Skills Pack Layout

This directory defines the installable packaging model for the Akka AI skills and reference examples.

## Scope

The pack includes:

- `skills/**` — AI-first SaaS routing, description-first, planning, and Akka implementation skills
- `docs/**` — pack-facing doctrine, planning references, and mechanics examples
- `examples/akka-components/**` exported as Java reference examples
- the repository root `frontend/**` exported as frontend workstream reference examples
- pack manifests and installer scripts
- `pack/AGENTS.md`, installed as `<agents-root>/AGENTS.md`

The pack does not include:

- `akka-context/**`
- repository-internal maintainer guidance from the root app
- a duplicate full-app core app baseline or scaffold command

The installed pack is a harness support library. Application source, `app-description/`, `specs/`, backend, and frontend files belong in the target project workspace. Downstream projects that want an implementation baseline should fork or copy from the upstream runnable core app repository root, then extend domain-specific behavior in their own workspace.

## Install target layout

The installer places files into one of two cross-harness locations:

- project mode: `<project-root>/.agents`
- global mode: `~/.agents`

Installed layout:

```text
<agents-root>/
  AGENTS.md
  docs/
  manifests/
    akka-ai-skills-pack.yaml
  resources/
    examples/
      java/
      frontend/
  skills/
    README.md
    references/
    <skill-name>/SKILL.md
```

## Install model

Each install copies the full packaged skill library, shared references, selected docs, Java examples, frontend reference source, and installed-pack guidance. There is no bundle selection during install and no automatic project-source generation.

## Path rewrite rules

Installed skills and docs must not point back to maintainer-repo-only paths.

- Example source references under `skills-pack/examples/akka-components/src/**` are rewritten to `.agents/resources/examples/java/src/**`.
- Frontend reference paths are rewritten to `.agents/resources/examples/frontend/**`.
- Repo-internal maintainer guidance references are rewritten to installed `<agents-root>/AGENTS.md` where needed.
- `akka-context/**` references are rewritten to generic official Akka SDK documentation notes because `akka-context` is not bundled.

## Maintainer flow

Recommended release flow:

1. validate skill references and guardrails
2. build a release bundle from `skills-pack/`
3. verify install-time path rewriting
4. publish `akka-ai-skills-pack-<version>.tar.gz` and `install-akka-ai-skills-pack-<version>.sh`
5. test a project install and a global install dry run

## Installer UX

- versioned release installers install into `<target-dir>/.agents`; `--target-dir` defaults to the current directory
- unpacked/source installs use `install.sh --location project --project <dir>` or `install.sh --location global`
- project mode can offer to install pack guidance at `<project-root>/AGENTS.md`
- generated application code should use the project's selected Java base package, defaulting to `ai.first` only when the user accepts or defers that choice; bundled `com.example` paths are reference examples only

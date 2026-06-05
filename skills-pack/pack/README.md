# Akka AI Skills Pack Metadata

This directory documents the harness install model for the Akka AI skills pack.

## Scope

The current installer includes:

- `skills/**` — AI-first SaaS routing, description-first, planning, and Akka implementation skills
- `skills/README.md` — skill routing map
- `skills/references/**` — shared skill reference files
- `docs/**`, `examples/**`, `templates/**`, and `tools/**` — pack assets referenced by installed skills

The source checkout also contains the runnable Akka full-stack core app and app-description/spec assets. Use those application files directly from a clone or fork of this repository. The installer does not copy manifests, application source, `akka-context/**`, repository-internal maintainer guidance, or duplicate full-app baselines. `akka-context/**` is expected as an independently maintained top-level project/repository directory.

The installed skills directory is a harness support library. Application source, `app-description/`, `specs/`, backend, and frontend files belong in the cloned/forked repository workspace. Downstream projects should fork this repository and extend domain-specific behavior in the root app workspace.

## Install target layout

The installer places files into a harness skills directory:

- project mode: `<project-root>/.agents/skills`
- global mode: `~/.agents/skills`
- explicit mode: `--target <skills-dir>`

Installed layout:

```text
<skills-dir>/
  .akka-ai-skills-pack-install-manifest
  README.md
  references/
  docs/
  examples/
  templates/
  tools/
  <skill-name>/SKILL.md
```

## Install model

Each install copies or symlinks the full current skill library, shared references, and referenced pack docs/examples/templates/tools into the harness skills directory. There is no duplicate application baseline, automatic project-source generation, or bundled `akka-context` install.

The ownership manifest records pack-owned entries. `--prune` removes manifest-owned entries that no longer exist in source, which covers retired skills. `--uninstall` removes all manifest-owned entries and the manifest. Unrelated skills in the target directory are not deleted.

## Maintainer flow

Recommended release flow:

1. validate skill references and guardrails
2. verify `install-skills.sh` with dry-run, install, `--check`, and prune behavior
3. verify installed-skill references resolve inside `.agents/skills/**`, except `akka-context/**` references which must target the project/repository top-level
4. update version references
5. commit version changes
6. create an annotated git tag
7. push the release commit/tag when ready

## Installer UX

- source/tag installs use `install-skills.sh --target <skills-dir>` or `install-skills.sh --location project|global`
- `--mode copy` is the default; `--mode symlink` supports development installs
- `--prune` removes retired pack-owned skills based on the manifest
- generated application code should use the project's selected Java base package, defaulting to `ai.first` only when the user accepts or defers that choice; bundled `com.example` paths are reference examples only

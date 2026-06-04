# Akka AI Skills Pack Layout

This directory documents the installable skills model for the Akka AI skills pack.

## Scope

The current installer includes:

- `skills/**` — AI-first SaaS routing, description-first, planning, and Akka implementation skills
- `skills/README.md` — skill routing map
- `skills/references/**` — shared skill reference files

The source pack also contains docs, examples, templates, and validation tools referenced by `pack/manifest.yaml`, including `templates/ai-first-saas-starter/app-description/**` and `tools/validate-surface-contracts.sh`. When working from a source checkout, use those source-controlled files directly. The skills-only installer does not copy docs, examples, templates, application source, `akka-context/**`, repository-internal maintainer guidance, or duplicate full-app baselines.

The installed pack is a harness support library. Application source, `app-description/`, `specs/`, backend, and frontend files belong in the target project workspace. Downstream projects that want an implementation baseline should fork or copy from the upstream runnable core app repository root, then extend domain-specific behavior in their own workspace.

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
  <skill-name>/SKILL.md
```

## Install model

Each install copies or symlinks the full current skill library and shared references. There is no distribution build, bundle selection, or automatic project-source generation.

The ownership manifest records pack-owned entries. `--prune` removes manifest-owned entries that no longer exist in source, which covers retired skills. `--uninstall` removes all manifest-owned entries and the manifest. Unrelated skills in the target directory are not deleted.

## Maintainer flow

Recommended release flow:

1. validate skill references and guardrails
2. verify `install-skills.sh` with dry-run, install, `--check`, and prune behavior
3. update version references
4. commit version changes
5. create an annotated git tag
6. push the release commit/tag when ready

## Installer UX

- source/tag installs use `install-skills.sh --target <skills-dir>` or `install-skills.sh --location project|global`
- `--mode copy` is the default; `--mode symlink` supports development installs
- `--prune` removes retired pack-owned skills based on the manifest
- generated application code should use the project's selected Java base package, defaulting to `ai.first` only when the user accepts or defers that choice; bundled `com.example` paths are reference examples only

# Akka AI Skills Pack Layout

This directory defines the installable packaging model for the Akka AI skills and reference examples in this repository.

## Scope

This pack intentionally includes:
- `skills/**`, including description-first and implementation-routing skills
- selected pack-facing docs under `docs/**`, including description-first doctrine/architecture references and example app-description artifacts
- reference examples exported from `src/**`
- pack manifests
- installer scripts
- a pack-facing `AGENTS.md` that is installed as `<agents-root>/AGENTS.md`

The installable pack always includes the full currently packaged skill library, shared references, and exported examples.
There is no bundle selection during install.

This pack intentionally does **not** include:
- `akka-context/**`
- the repository-internal maintainer guidance files from the repo root

`akka-context` is kept in this repository only as a maintainer/reference input. Installed packs must not depend on local `akka-context` files being present.

The installed pack uses `pack/AGENTS.md` as the source for `<agents-root>/AGENTS.md`.
It also uses `pack/EXAMPLES-README.md` as the source for `<agents-root>/resources/examples/java/README.md`.
Those installed files are for pack users and are distinct from the repository-internal maintainer guidance files.

Important distinction for real development projects:
- the installed pack under `<agents-root>/` provides skills, guidance, and examples
- a project's maintained `app-description/` tree belongs in the **target project workspace**, not inside the pack itself, unless that project explicitly chooses another internal location

## Install target layout

The installer places files into one of two cross-harness locations:

- project mode: `<project-root>/.agents`
- global mode: `~/.agents`

Installed layout:

```text
<agents-root>/
  AGENTS.md
  docs/
    description-first-application-doctrine.md
    app-description-skills-plan-backlog.md
    internal-app-description-architecture.md
    app-description-maintenance-flow.md
    app-description-end-to-end-workflow-example.md
    agent-coverage-matrix.md
    pending-question-queue.md
    pending-task-queue.md
    prd-to-akka-flow.md
    timer-pattern-selection.md
    workflow-endpoint-pattern.md
    examples/
      purchase-request-app-description/
      purchase-request-prd.md
      purchase-request-solution-plan.md
      purchase-request-pending-tasks.md
    ...
  manifests/
    akka-ai-skills-pack.yaml
  resources/
    examples/
      java/
        pom.xml
        README.md
        src/
          main/
            java/...
            resources/...
          test/
            java/...
  skills/
    README.md
    references/
      akka-entity-comparison.md
    app-descriptions/
      SKILL.md
    app-description-bootstrap/
      SKILL.md
    app-description-input-normalization/
      SKILL.md
    app-generate-app/
      SKILL.md
    akka-solution-decomposition/
      SKILL.md
    akka-backlog-to-pending-tasks/
      SKILL.md
    akka-change-request-to-spec-update/
      SKILL.md
    akka-revised-prd-reconciliation/
      SKILL.md
    akka-pending-task-queue-maintenance/
      SKILL.md
    akka-do-next-pending-task/
      SKILL.md
    akka-workflows/
      SKILL.md
    akka-views/
      SKILL.md
    akka-http-endpoints/
      SKILL.md
    akka-agents/
      SKILL.md
    akka-event-sourced-entities/
      SKILL.md
    ...
```

Where `<agents-root>` is either `<project-root>/.agents` or `~/.agents`.

## Install model

The pack is versioned as one release artifact.
Each install copies the full packaged skill library, shared references, exported examples, and selected description-first reference docs.

## Path rewrite rules

Installed skills must be rewritten so they do not point back to maintainer-repo-only paths.

### Example path rewrite

Rewrite references like:

```text
../../../src/main/java/com/example/application/ShoppingCartEntity.java
```

to:

```text
../../resources/examples/java/src/main/java/com/example/application/ShoppingCartEntity.java
```

### Repo-internal guidance rewrite

Installed skill files must not reference repository-internal maintainer guidance files from the source repository.
When needed, repo-internal `AGENTS.md` references should be rewritten to the installed `<agents-root>/AGENTS.md` guidance file.

### Akka docs reference rewrite

Rewrite references like:

```text
akka-context/sdk/event-sourced-entities.html.md
```

to a non-local note such as:

```text
Official Akka SDK docs for this topic (not bundled with this pack)
```

That keeps installed skills free of broken local file references while still reminding agents to consult official Akka docs.

## Maintainer flow

Recommended release flow:
1. validate all skill references in CI
2. generate a release bundle from repo content
3. rewrite install-time paths in copied skill files
4. publish both `akka-ai-skills-pack-<version>.tar.gz` and `install-akka-ai-skills-pack-<version>.sh` as GitHub release assets
5. install from the versioned release installer with `curl ... | bash` or unpack the archive and run `install.sh`

## Installer UX

- the versioned GitHub release installer installs into `<target-dir>/.agents`; `--target-dir` defaults to the current directory
- if `--location project` is provided, `install.sh` installs into `<project-root>/.agents` without prompting
- if `--location global` is provided, `install.sh` installs into `~/.agents` without prompting
- if `--location` is omitted, `install.sh` prompts the user to choose between those two modes
- `--project <dir>` can be used to set the project root for project mode; otherwise the current directory is used

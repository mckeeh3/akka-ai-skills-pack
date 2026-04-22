# Akka AI Skills Pack Layout

This directory defines the installable packaging model for the Akka AI skills and reference examples in this repository.

## Scope

This pack intentionally includes:
- `skills/**`
- reference examples exported from `src/**`
- pack manifests
- installer scripts

The installable pack always includes the full currently packaged skill library, shared references, and exported examples.
There is no bundle selection during install.

This pack intentionally does **not** include:
- `akka-context/**`

`akka-context` is kept in this repository only as a maintainer/reference input. Installed packs must not depend on local `akka-context` files being present.

## Install target layout

The installer places files into one of two cross-harness locations:

- project mode: `<project-root>/.agents`
- global mode: `~/.agents`

Installed layout:

```text
<agents-root>/
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
    akka-solution-decomposition/
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
Each install copies the full packaged skill library, shared references, and exported examples.

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

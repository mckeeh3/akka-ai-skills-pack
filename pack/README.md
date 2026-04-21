# Akka AI Resource Pack Layout

This directory defines the installable packaging model for the Akka AI skills and reference examples in this repository.

## Scope

This pack intentionally includes:
- `skills/**`
- reference examples exported from `src/**`
- pack manifests
- installer scripts

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
    akka-ai-pack.yaml
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
    akka-entity-type-selection/
      SKILL.md
    akka-event-sourced-entities/
      SKILL.md
    akka-ese-domain-modeling/
      SKILL.md
    akka-ese-application-entity/
      SKILL.md
    ...
```

Where `<agents-root>` is either `<project-root>/.agents` or `~/.agents`.

## Bundle model

The pack is versioned as one release artifact, but installation can target smaller bundles.

Current planned bundles:
- `all`
- `entities-core`
- `ese-core`
- `kve-core`

Future bundles can add the remaining Akka component families and cross-cutting topics such as:
- `components-agents`
- `components-views`
- `components-workflows`
- `components-http-endpoints`
- `components-grpc-endpoints`
- `components-consumers`
- `components-timed-actions`
- `security`
- `local-testing`
- `deployment`
- `observability`

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

Recommended future release flow:
1. validate all skill references in CI
2. generate a release bundle from repo content
3. rewrite install-time paths in copied skill files
4. publish the bundle as a GitHub release asset
5. install from the release asset with `install.sh` or `install.ps1`

## Installer UX

- if `--location project` is provided, install into `<project-root>/.agents` without prompting
- if `--location global` is provided, install into `~/.agents` without prompting
- if `--location` is omitted, prompt the user to choose between those two modes
- `--project <dir>` can be used to set the project root for project mode; otherwise the current directory is used

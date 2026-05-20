# Starter App Template Layout and Extension Workflow

## Purpose

This document chooses the canonical source layout, installed-pack resource path, scaffold behavior, overwrite policy, and downstream extension workflow for the full-core AI-first SaaS starter app template.

It is a planning contract for later starter implementation and packaging tasks. It does not itself create the executable starter skeleton.

## Decisions

| Concern | Decision |
| --- | --- |
| Canonical source path in this repository | `templates/ai-first-saas-starter/` |
| Installed-pack resource path | `.agents/resources/templates/ai-first-saas-starter/` for project installs and `~/.agents/resources/templates/ai-first-saas-starter/` for global installs |
| Release bundle source path | `templates/ai-first-saas-starter/` copied into the versioned bundle and then installed under `resources/templates/ai-first-saas-starter/` |
| Scaffold command surface | An explicit starter scaffold/init command added in the packaging sprint; skills-only install remains the default |
| Default generated Java base package | `ai.first`, only after the user accepts or defers the package prompt |
| Legacy example relationship | Existing `src/main/java/com/example/**`, `src/test/java/com/example/**`, `frontend/**`, and docs examples remain reference/migration sources, not the canonical starter path |

## Canonical source layout

Use a top-level `templates/` directory so starter source is isolated from executable reference examples under `src/` and from installed-pack metadata under `pack/`.

Target layout:

```text
templates/
  ai-first-saas-starter/
    README.md
    TEMPLATE-MANIFEST.md
    scaffold-rules.md
    app-description/
      ...                         # starter-owned maintained description seed
    specs/
      ...                         # starter-owned initial specs/backlog/checklists
    backend/
      pom.xml
      src/main/java/{{JAVA_PACKAGE_PATH}}/...
      src/main/resources/...
      src/test/java/{{JAVA_PACKAGE_PATH}}/...
    frontend/
      package.json
      package-lock.json
      tsconfig.json
      vite.config.ts
      index.html
      public/...
      src/...
```

Rationale:

- `templates/` makes the starter visibly different from reusable examples.
- `backend/` and `frontend/` allow the scaffold command to copy into a normal target project root without inheriting this repository's example-project layout.
- `app-description/` and `specs/` make the scaffolded app description-first and extension-ready from the first commit.
- Template placeholders avoid making `com.example` a generated-code default.

## Template placeholders

The scaffold command must support these placeholders wherever they appear in template file paths or file contents:

| Placeholder | Meaning | Default |
| --- | --- | --- |
| `{{APP_NAME}}` | Human-readable application name | `AI First SaaS Starter` |
| `{{APP_SLUG}}` | Safe lower-kebab project/app identifier | `ai-first-saas-starter` |
| `{{JAVA_BASE_PACKAGE}}` | Java package selected by the user | `ai.first` after acceptance/deferral |
| `{{JAVA_PACKAGE_PATH}}` | Slash path derived from `JAVA_BASE_PACKAGE` | `ai/first` |
| `{{MAVEN_GROUP_ID}}` | Maven group id | same as `JAVA_BASE_PACKAGE` |

The scaffold flow must ask for the Java base package before materializing Java files:

> What Java base package should I use for generated code? Press Enter to use `ai.first`.

`com.example` may appear only in reference examples and migration notes, not in rendered starter output unless explicitly requested by the user.

## Installed-pack resource layout

After installation, the resource pack should include:

```text
.agents/
  resources/
    templates/
      ai-first-saas-starter/
        README.md
        TEMPLATE-MANIFEST.md
        scaffold-rules.md
        app-description/...
        specs/...
        backend/...
        frontend/...
```

For global installs the same layout lives under `~/.agents/resources/templates/ai-first-saas-starter/`.

The installed template is a read-only scaffold source. The maintained application artifacts belong in the target workspace after scaffold, not under `.agents/`.

## Install and scaffold behavior

### Skills-only install remains default

Existing install behavior must remain safe:

- `bash install.sh --location project --project <dir>` installs guidance, skills, docs, examples, and starter template resources under `<dir>/.agents`.
- `bash install.sh --location global` installs the same support library under `~/.agents`.
- Neither mode copies starter application code into the project root by default.
- Existing projects are never modified beyond `.agents/` and the explicitly accepted `AGENTS.md` guidance behavior already handled by the installer.

### Explicit scaffold/init mode

A later packaging task should add an explicit scaffold command. Acceptable implementation shapes are:

1. a script installed under `.agents/bin/scaffold-ai-first-saas-starter.sh`; or
2. an `install.sh` option such as `--scaffold ai-first-saas-starter` that delegates to the same script.

The command must require explicit user intent. It should not run during global installs or default project installs.

Recommended command shape:

```bash
.agents/bin/scaffold-ai-first-saas-starter.sh \
  --target /path/to/empty-project \
  --app-name "My App" \
  --base-package ai.first
```

If `--base-package` is omitted, the command or harness must ask the standard package prompt before creating Java source files.

## Scaffold safety and overwrite policy

The scaffold command must fail closed by default.

### Empty target detection

A target is considered scaffold-safe when it is empty or contains only explicitly allowed bootstrap files such as:

- `.git/`
- `.gitignore`
- `README.md`
- an existing `.agents/` install
- empty `docs/`, `specs/`, or `app-description/` directories

### Existing application detection

The command must refuse to scaffold without an explicit override when it detects any of:

- `pom.xml`, `build.gradle`, or `build.gradle.kts`
- `src/`
- `frontend/`
- `package.json`
- non-empty `app-description/` or `specs/`
- target files that would be overwritten

### Overwrite modes

Use explicit modes instead of implicit replacement:

| Mode | Behavior |
| --- | --- |
| default | refuse if any rendered path already exists |
| `--dry-run` | print planned copies, rendered paths, and conflicts without writing |
| `--force-empty` | allow scaffolding into a target that has only safe bootstrap files |
| `--force-overwrite` | reserved for deliberate replacement; must list every conflicting path before proceeding in interactive mode, or require a non-interactive confirmation flag in CI |

The scaffold operation should write a short report such as `specs/scaffold-report.md` in the target project recording template version, selected package, app name, copied paths, skipped paths, and follow-up checks.

## What the scaffolded project should contain

A successful scaffold leaves the user with a normal application workspace, not a dependency on `.agents` for app source:

```text
AGENTS.md                         # project guidance, if accepted during install
.agents/                          # installed pack support library
README.md                         # starter app run/extension guide
app-description/                  # maintained product/source-of-truth description
specs/                            # maintained planning, tasks, questions, checklists
pom.xml
src/main/java/<selected package>/...
src/main/resources/...
src/test/java/<selected package>/...
frontend/
  package.json
  src/...
```

The starter's generated source may import skills-pack ideas, but it must not import files from `.agents/` at runtime. `.agents/` is guidance and template storage only.

## Extension workflow for downstream users

The canonical workflow after scaffold is:

1. **Scaffold the starter** into an empty or new target project.
2. **Run baseline checks** documented by the starter, beginning with backend compile/tests and frontend tests/build as soon as each slice is present.
3. **Maintain app intent through `docs/input/`, `app-description/`, and `specs/`** rather than manually editing generated source as the normal product-change path.
4. **Model governed capabilities** for each new feature: actors/callers, selected `AuthContext`, tenant/customer scope, schemas, side effects, idempotency, policy/approval, audit/trace, exposure surfaces, and tests.
5. **Extend implementation by vertical capability slices**: domain model, Akka components, endpoints/streams, workstream surfaces, tests, and security checks.
6. **Keep backend authorization authoritative** for every protected endpoint, component command, view query, stream, workflow step, consumer side effect, timed action, internal agent operation, and tool call.
7. **Update app-description/specs and pending tasks** whenever user input, implementation discoveries, manual test findings, or security-review findings change the plan.
8. **Run acceptance/security checks** before calling a slice complete.

In short:

```text
scaffold starter
→ update app-description/specs from user input
→ model capabilities
→ extend Akka components, APIs, workstream UI, and tests
→ run acceptance/security checks
→ repeat
```

## Migration guidance for subsequent tasks

- Backend starter implementation should migrate only the security/admin and agent-governance semantics inventoried as `migrate`; it should not copy `com.example` packages verbatim.
- Frontend starter implementation should reuse `frontend/src/workstream/**`, workstream API/realtime clients, design-system primitives, and workstream contract tests as the canonical UI source reference.
- `docs/examples/ai-first-saas-seed-app-description/**` remains the primary description contract to align with the starter.
- DCA/supplies assets stay quarantined as domain-rich vertical examples until the starter full-core path replaces them in canonical guidance.
- Static-resource frontend outputs are build artifacts or endpoint mechanics references, not starter source.

## Acceptance checklist for packaging tasks

Later packaging/install tasks should verify:

- `templates/ai-first-saas-starter/**` is copied into release bundles.
- project and global installs place the template under `resources/templates/ai-first-saas-starter/`.
- default installs do not materialize starter code into the project root.
- explicit scaffold mode refuses unsafe targets by default and supports dry-run conflict reporting.
- rendered Java output uses the selected package consistently in group id, paths, declarations, imports, tests, and docs.
- scaffolded docs explain the extension workflow and preserve the mandatory secure SaaS, governed agent runtime, workstream UI, audit/trace, and security-test foundation.

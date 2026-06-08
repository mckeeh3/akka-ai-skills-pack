# Target Layout and Path Map: Core App First Repository Refactor

## Purpose

This document is the authoritative layout contract for the core-app-first refactor. It records the target repository shape, fixed package policy, old-to-new path map, and extension boundary model that later migration tasks must use before moving files.

## Architecture decision

The repository root becomes the single canonical runnable core app. The skills pack becomes a top-level supporting product area under `skills-pack/`.

After this migration:

- the root is a normal Akka Java SDK + frontend application repository that downstream teams can fork and extend;
- `skills-pack/` owns installable agent guidance, Akka reference material, packaging/install tooling, and focused component examples;
- `templates/ai-first-saas-starter/` is not a maintained full-app source of truth;
- source-package placeholder rendering is not part of the normal fork-and-extend workflow;
- focused `com.example` examples remain examples only and must not be mixed into the runnable core app source.

## Target root app layout

The repository root should be understandable as the core application itself.

```text
./
  AGENTS.md                         # root contributor/agent guidance for the core app plus pointer to skills-pack maintenance
  README.md                         # how to run, test, fork, extend, and merge upstream core changes
  LICENSE
  pom.xml                           # canonical root backend build
  src/
    main/java/ai/first/             # canonical core app Java package path
      api/
      application/
      domain/
      security/                     # if retained as a separate package after migration
    main/resources/
      static-resources/             # frontend production build output served by Akka
    test/java/ai/first/
  frontend/
    package.json
    package-lock.json
    src/
    public/
    vite.config.ts
    tsconfig.json
  app-description/                  # canonical core app description, promoted from starter seed
  specs/                            # core app specs and active core-app/domain-extension planning queues
  docs/                             # core app docs and extension guidance
  tools/                            # core app validation/runtime tools only
  .env.example                      # root local environment template
```

Root app source may keep current domain/application/api layering, but all canonical runtime code must live under the fixed core package path and root app build. Root `src/` must not contain skills-pack reference examples after example relocation.

## Fixed Java package policy

The core app uses a fixed Java package instead of scaffold-time placeholders:

- canonical Java base package: `ai.first`
- canonical Java source path: `src/main/java/ai/first/**`
- canonical test source path: `src/test/java/ai/first/**`
- Maven group id: `ai.first`

Rules:

- Do not render `{{JAVA_BASE_PACKAGE}}`, `{{JAVA_PACKAGE_PATH}}`, or `{{MAVEN_GROUP_ID}}` into the root app during normal use.
- Do not use `com.example` for root generated-app/runtime source.
- `com.example` remains acceptable only inside focused skills-pack reference examples.
- Downstream forks may rename the Java package as their own deliberate product refactor, but the supported merge-friendly default is to keep `ai.first` and add domain-specific packages below it.

## Domain extension boundary model

Downstream product work should be additive and merge-friendly. The core app owns secure SaaS foundation, five core workstreams, shared shell, governed runtime-agent substrate, and cross-cutting platform services. Domain-specific extensions should prefer stable extension zones rather than editing core internals.

Recommended extension zones:

```text
src/main/java/ai/first/domain/extensions/<domain>/
src/main/java/ai/first/application/extensions/<domain>/
src/main/java/ai/first/api/extensions/<domain>/
src/test/java/ai/first/extensions/<domain>/
frontend/src/extensions/<domain>/
app-description/extensions/<domain>/
specs/extensions/<domain>/
docs/extensions/<domain>/
```

Extension registries should be explicit where the core app discovers or renders extension behavior. Later implementation/docs tasks should define the concrete registry files, but the intended boundaries are:

- backend capability/governed-tool registration entries for domain capabilities;
- workstream and surface registration entries for domain workstreams;
- frontend navigation/surface renderer registration entries for domain UI;
- app-description extension indexes that map domain surfaces/actions to governed capabilities;
- tests that prove extensions do not bypass core auth, tenant/customer scoping, audit/work traces, or provider fail-closed behavior.

When an extension requires changing core foundation behavior, prefer a small core hook or registry change plus a domain-specific implementation under the extension path.

## Target `skills-pack/` layout

`skills-pack/` is the source area for installable pack assets and pack-maintenance material. It is not the runnable app root.

```text
skills-pack/
  AGENTS.md                         # source-maintainer guidance for skills-pack work, derived from current root scope guidance
  README.md                         # pack development, install, validation, and release notes
  install.sh                        # skills-pack install entrypoint, or wrapper referenced from root
  pack/
    AGENTS.md                       # installed-pack guidance, emitted as .agents/AGENTS.md
    manifest.yaml
    ...
  skills/
    README.md
    <skill-name>/SKILL.md
    references/
  docs/
    ...                             # skills-pack doctrine and agent-optimized references
  examples/
    akka-components/                # focused Akka examples moved out of root src/test
    app-descriptions/               # description mechanics/reference examples if retained
  akka-context/                     # official Akka reference material, if still vendored in this repo
  templates/
    snippets/                       # small templates/snippets only; no duplicate full-app starter
  tools/
    ...                             # pack validation, packaging, doc/search checks
  dist/                             # generated release artifacts; may remain ignored/generated if preferred
```

Skills-pack examples are allowed to use example packages such as `com.example` when they are clearly reference fixtures and are never treated as runnable core app source.

## Full-app template policy

`templates/ai-first-saas-starter/` currently contains the scaffold source for the full app, including backend, frontend, app-description, and specs seeds. Under the target model it is dissolved:

- backend starter source is reconciled into root `pom.xml` and root `src/**`;
- frontend starter source is reconciled into root `frontend/**`;
- starter `app-description/` is promoted or reconciled into root `app-description/`;
- starter `specs/` seed material is promoted, archived, or reclassified under root `specs/` only if still useful to the core app;
- template-only manifests/rules are archived or removed after their remaining claims are represented in root docs or skills-pack docs;
- no second full-app source copy is maintained after dissolution.

Small snippet templates may remain under `skills-pack/templates/` only when they are not a full runnable app duplicate.

## Old-to-new path map

| Current path | Target path | Action | Notes |
| --- | --- | --- | --- |
| `pom.xml` | `pom.xml` | retain/reconcile | Root build remains canonical; reconcile with starter backend build if needed. |
| `src/main/java/com/example/**` | `src/main/java/ai/first/**` or `skills-pack/examples/akka-components/**` | split/promote or move | Core app runtime code migrates to `ai.first`; focused examples move to skills-pack examples. |
| `src/test/java/com/example/**` | `src/test/java/ai/first/**` or `skills-pack/examples/akka-components/**` | split/promote or move | Runtime tests follow root app package; reference tests follow examples. |
| `src/main/resources/**` | `src/main/resources/**` | retain/reconcile | Include Akka resources and frontend static output. |
| `frontend/**` | `frontend/**` | retain/reconcile | Root frontend becomes canonical; reconcile with starter frontend before template removal. |
| `templates/ai-first-saas-starter/backend/pom.xml` | `pom.xml` | reconcile then remove template copy | Do not keep as second build source. |
| `templates/ai-first-saas-starter/backend/src/**` | `src/**` | reconcile/promote then remove template copy | Rendered placeholders are replaced by fixed `ai.first` package. |
| `templates/ai-first-saas-starter/frontend/**` | `frontend/**` | reconcile/promote then remove template copy | Root frontend becomes the only maintained app frontend. |
| `templates/ai-first-saas-starter/app-description/**` | `app-description/**` | promote/reconcile | Description seed becomes root app description if not already present. |
| `templates/ai-first-saas-starter/specs/**` | `specs/**` or `specs/archive/**` | promote/archive | Keep only useful core-app planning/provenance. |
| `templates/ai-first-saas-starter/.env.example` | `.env.example` | reconcile | Root environment example is canonical. |
| `templates/ai-first-saas-starter/README.md` | `README.md`, `docs/**`, or archive | reclassify | Convert scaffold claims to fork-and-extend docs; do not preserve as canonical template README. |
| `templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md` | `specs/core-app-first-repo-refactor/archive/` or remove | archive/remove | Manifest is scaffold-era provenance after dissolution. |
| `templates/ai-first-saas-starter/scaffold-rules.md` | `skills-pack/docs/**` or archive/remove | reclassify | Keep only non-obsolete generation guidance. |
| `skills/**` | `skills-pack/skills/**` | move | Installed skill source moves under skills-pack. |
| `skills/README.md` | `skills-pack/skills/README.md` | move/update references | Root references should point to moved file. |
| `pack/**` | `skills-pack/pack/**` | move | Installed-pack guidance and manifest move together. |
| `install.sh` | `skills-pack/install.sh` plus optional root wrapper | move/wrap | Root may keep a small pointer/wrapper only if useful. |
| `docs/**` | split between `docs/**` and `skills-pack/docs/**` | classify | Core app docs stay root; skills/doctrine/agent-routing docs move. |
| `akka-context/**` | `skills-pack/akka-context/**` | move | Official reference material supports skills-pack guidance. |
| `examples/**` | `skills-pack/examples/**` or root app examples if truly app-facing | classify/move | Focused component/reference examples belong under skills-pack. |
| `tools/**` | split between `tools/**` and `skills-pack/tools/**` | classify | Runtime/app validation stays root; pack/scaffold/package validation moves. |
| `dist/**` | `skills-pack/dist/**` or generated artifact outside source | move/reclassify | Pack release output belongs with pack tooling if retained. |
| `package.json`, `package-lock.json`, `tsconfig.web-ui.json` | root or `skills-pack/` based on purpose | classify | Root app frontend dependencies stay root/frontend; pack doc/tool dependencies move. |
| `AGENTS.md` | `AGENTS.md` plus `skills-pack/AGENTS.md` | split/update | Root guidance becomes core-app-oriented; pack guidance moves under skills-pack. |
| `.agents/skills/project-discussed-idea-to-pending-project/**` | unchanged local harness config | retain | Project-local harness skill is not an installable pack asset. |
| `specs/core-app-first-repo-refactor/**` | `specs/core-app-first-repo-refactor/**` | retain | This active refactor queue remains root until the refactor completes. |
| other active `specs/**` | `specs/**`, `specs/archive/**`, or `skills-pack/specs/**` | classify later | Asset inventory task decides app vs pack vs archive. |

## Migration sequencing constraints

1. Complete this path map before moving files.
2. Inventory assets and classify ambiguous paths before broad migration.
3. Promote/reconcile the root core app before deleting the starter template.
4. Dissolve the full-app template only after root backend/frontend/app-description ownership is clear.
5. Move skills-pack assets under `skills-pack/` after root app ownership is stable.
6. Move focused Akka examples out of root source after distinguishing runtime app code from reference fixtures.
7. Update docs/tooling after final paths exist, then run terminal validation.

## Reference and compatibility rules for later tasks

- Update active task briefs and docs only when they are in scope for the selected task; do not rewrite the whole repository opportunistically.
- When an old path appears in an active queue, interpret it through this map unless the task explicitly says it depends on pre-migration source.
- Keep search proof for stale `templates/ai-first-saas-starter` references during template-dissolution and final verification tasks.
- A remaining `templates/ai-first-saas-starter` reference is acceptable only when it is historical/provenance text, an archive pointer, or a compatibility note that explicitly says the full-app template is no longer canonical.
- Runtime completion claims must continue to be validated through the root Akka/API/UI path at the stated scope.

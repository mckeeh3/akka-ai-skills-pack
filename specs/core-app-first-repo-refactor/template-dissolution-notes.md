# Template Dissolution Notes

## Task

TASK-LAYOUT-02-002 dissolved the maintained full-app starter template after TASK-LAYOUT-02-001 promoted the core backend, frontend, resources, and app-description into canonical root paths.

## Removed maintained duplicate source

- Removed `templates/ai-first-saas-starter/**` as an active full-app template source.
- Removed obsolete scaffold/render scripts:
  - `tools/scaffold-ai-first-saas-starter.sh`
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
  - `tools/smoke-ai-first-saas-starter-real-model.sh`

The canonical runnable app source is now the repository root:

- `pom.xml`
- `src/**`
- `frontend/**`
- `app-description/**`
- `.env.example`

## Updated immediate tooling boundaries

- `install.sh` no longer requires, copies, or advertises the full-app template or scaffold command.
- `tools/build-pack.sh` no longer validates/copies the full-app template or scaffold command into release bundles.
- `pack/manifest.yaml` no longer exports `resources/templates/ai-first-saas-starter` and now references root `app-description/**` for the core app-description assets.
- `tools/verify-opinionated-ai-first-saas-pack.sh` now checks root `app-description/**` instead of the removed template app-description.
- `tools/prove-workstream-icons-v0.sh` now verifies the root core app source directly instead of scaffolding a temporary rendered copy.

## Remaining reference classification

A search for `templates/ai-first-saas-starter` or `scaffold-ai-first-saas-starter` is expected to find references outside this task's bounded scope. Classify remaining references as follows:

- Historical/provenance: this mini-project's README, path map, asset inventory, task briefs, and completed task notes that describe the pre-refactor source path or the dissolution decision.
- Pending migration: root README, pack docs, skills, general docs, and non-selected tools that still describe scaffold-first or template-first workflows; these belong to later TASK-LAYOUT-04-001 and TASK-LAYOUT-04-002 guidance/tooling cleanup.
- Active blocker if found after this task: any remaining maintained source directory at `templates/ai-first-saas-starter/**`, installer copy operation for that directory, release-bundle copy operation for that directory, or manifest template export for that directory.

## Non-goals deferred to later tasks

- Broad docs and skill rewrites from scaffold-first to fork-and-extend.
- Moving skills-pack assets under `skills-pack/`.
- Relocating focused `com.example` reference examples.
- Final repository-wide stale-reference cleanup.

# Core App First Repository Refactor

## Purpose

Plan the repository refactor from a skills-pack-first source tree with a duplicated starter template into a flat, standard runnable Akka Java + frontend core app repository with all skills-pack development and maintenance assets isolated under a top-level `skills-pack/` directory.

The intended outcome is easier for real users to understand: the repository root looks like the core application they fork and extend, while the skills pack becomes supporting agent guidance for maintaining and extending that app.

## Background and trigger

This mini-project captures the discussion that the repository now has two roles that are hard to manage in the current layout:

1. developing and maintaining the skills pack, doctrine, docs, packaging, and focused Akka reference examples;
2. developing the runnable core app base layer used by all workstreams, including the five core app workstreams.

The existing full-app template under `templates/ai-first-saas-starter/` and the root `src`/`frontend` copies create synchronization problems. The newer proposal is to eliminate the full-app template requirement, make the repository itself the runnable core app, and let downstream domain implementations fork the repo and merge upstream core changes through normal Git workflows.

## Scope

In scope:

- Make the repository root a conventional runnable Akka Java + frontend app layout.
- Promote the core app source to top-level `pom.xml`, `src/`, `frontend/`, `app-description/`, `specs/`, `docs/`, and app tools.
- Move skills-pack development assets under top-level `skills-pack/`.
- Remove the large full-app template as a maintained source of truth.
- Preserve focused Akka component examples used by skills under `skills-pack/examples/akka-components/` or equivalent.
- Update repo guidance, installed-pack guidance, docs, install/package tooling, and validation commands to match the new fork-and-extend model.
- Keep domain-specific extension work additive and merge-friendly through stable extension directories/registries.

## Non-goals

- Do not implement new domain-specific product features as part of this refactor.
- Do not broaden core app functional scope beyond preserving the existing core app behavior.
- Do not rewrite every skill for style if only path/layout updates are needed.
- Do not keep a second full-app template unless a later explicit decision reverses the fork-and-extend model.
- Do not treat fixture/mock/demo/model-less paths as completed runtime behavior.

## Affected repository areas

Likely affected paths include:

- top-level `src/`, `frontend/`, `pom.xml`, app docs/specs/tools;
- `templates/ai-first-saas-starter/` removal or dissolution;
- `skills/`, `pack/`, `install.sh`, `README.md`, `docs/`, `akka-context/`, and packaging assets moved to `skills-pack/`;
- root and installed guidance files such as `AGENTS.md`, `pack/AGENTS.md`, `skills/README.md`;
- validation scripts under `tools/`;
- existing mini-project references under `specs/` that mention template paths.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run its required checks or block with a precise reason, and make one focused commit before being marked `done`.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills/README.md`
3. `docs/pending-task-queue.md`
4. this mini-project `README.md`
5. `conversation-capture.md`
6. selected sprint, backlog, pending-task entry, and task brief
7. the smallest relevant source files listed by the task

After the refactor moves skills-pack assets, task briefs should be updated or interpreted with the new path map produced by TASK-LAYOUT-01-001.

## Sprint sequence

1. Architecture decision and inventory.
2. Core app root promotion and template dissolution.
3. Skills-pack isolation and reference-example relocation.
4. Guidance, packaging, and tooling updates.
5. Validation, compatibility review, and terminal verification.

## Done state

This mini-project is complete when:

- the repository root is the single canonical runnable core app source;
- the large full-app template is removed as a maintained duplicate source;
- skills-pack development and maintenance assets are isolated under `skills-pack/`;
- focused Akka examples used by the skills are retained in an internal/reference location and are no longer confused with core app source;
- top-level docs explain fork-and-extend, upstream merge, and domain-specific extension boundaries;
- installed-pack/user guidance no longer tells users to scaffold a duplicate starter app when the supported workflow is fork-and-extend;
- validation commands prove the root core app backend/frontend/runtime assets still build/test at the stated scope;
- packaging/install checks still work for the skills pack after the path move, or blocked/deferred packaging gaps are recorded explicitly;
- terminal verification records no material unqueued gaps in this mini-project scope.

## Open concerns and recommendations

- Several existing specs and task queues reference `templates/ai-first-saas-starter/`; tasks should either update active references or produce a compatibility/path map before broad rewrites.
- If a skills-only install remains a product deliverable, packaging can still install `skills-pack/` assets, but it should not carry a full duplicate app template.
- Domain-specific extension points should be explicit before users start modifying core files heavily; otherwise upstream core merges will become difficult.

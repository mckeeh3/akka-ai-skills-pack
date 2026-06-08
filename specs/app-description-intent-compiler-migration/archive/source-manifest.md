# Temporary Legacy App-description Source Manifest

## Archive path

- Archived tree: `specs/app-description-intent-compiler-migration/archive/legacy-app-description/`
- Source tree at capture time: `app-description/`
- Captured for task: `TASK-ADICM-01-001`
- Capture date: 2026-06-08
- Captured file count: 88 Markdown files and other files under the legacy tree

## Authority status

This archive is **temporary migration provenance only**. It is **not authoritative product current intent** and must not be used as the source of truth for generated-app behavior, backend authorization, UI surfaces, workstream agents, policies, tests, or runtime readiness.

Active current intent remains under the root `app-description/` tree until later migration tasks replace it with the intent-compiler graph shape. During reconstruction, future tasks may cite this archive only as evidence to compare against current implementation and accepted migration decisions.

## Intended use

Use this archive to:

- recover legacy rough workstream and foundation-description content while the root `app-description/` is reconstructed;
- identify statements that should become current starter intent, reusable foundation references, stale exclusions, or drift candidates;
- support source-inventory and traceability notes during this mini-project.

Do not use this archive to:

- override current implementation evidence;
- preserve historical phrasing in active current-intent graph nodes;
- justify runtime readiness without real implementation/test evidence;
- add broad reusable foundation doctrine to the root app-description when skills-pack docs should be referenced instead.

## Removal and scrub requirement

Before this mini-project is complete, active content must be scrubbed so no app-description/spec/task artifact depends on archived legacy files as product authority. The temporary archive must then be removed or reduced to clearly non-authoritative migration notes, and the terminal verification task must prove that active current-intent content does not rely on this archive.

## Recovery proof

The archived source is recoverable by reading files under:

```text
specs/app-description-intent-compiler-migration/archive/legacy-app-description/
```

The legacy `app-description/README.md` and `app-description/00-system/app-manifest.md` were included in the copied tree and can be inspected at:

```text
specs/app-description-intent-compiler-migration/archive/legacy-app-description/README.md
specs/app-description-intent-compiler-migration/archive/legacy-app-description/00-system/app-manifest.md
```

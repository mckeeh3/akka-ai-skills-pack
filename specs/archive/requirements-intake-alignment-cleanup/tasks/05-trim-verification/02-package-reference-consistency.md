# TASK-RIAC-05-002: Check package and reference consistency

## Objective

Verify that removed/rewritten docs and skills do not leave stale references in installable pack manifests, README files, templates, or examples.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
- pack/manifest.yaml if present
- installer/package docs or manifest files discovered by search

## In scope

- Search for removed file names and old preferred example paths.
- Update manifests/docs where required.
- Record consistency notes in `package-reference-consistency.md`.

## Checks

- `git diff --check`
- Reference searches for removed/renamed files.

## Done criteria

- No active installable-pack reference points to removed stale content.
- Queue updated and committed.

# TASK-RIAC-05-001: Whole-pack stale content pass

## Objective

Run a broad stale-term/reference pass across active skills/docs, then remove or rewrite one bounded batch of remaining stale intake/planning content.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/README.md
- specs/requirements-intake-alignment-cleanup/content-inventory.md
- specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
- specs/requirements-intake-alignment-cleanup/sprints/05-trim-and-repeat-review-sprint.md
- docs/agent-workstream-design-review-checklist.md

## In scope

- Run and record stale searches for CRUD/page/screen/nav/chatbot/component-first/example-reference language.
- Inspect active guidance hits.
- Rewrite/remove a bounded batch, or append follow-up tasks if the batch is too large.

## Checks

- `git diff --check`
- Recorded `rg` commands and summary in `stale-content-pass-01.md`.

## Done criteria

- A review artifact records search results and dispositions.
- At least one bounded stale-content batch is resolved or follow-up tasks are appended.
- Queue updated and committed.

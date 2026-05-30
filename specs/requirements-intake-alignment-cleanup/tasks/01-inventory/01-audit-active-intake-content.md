# TASK-RIAC-01-001: Audit active intake content

## Objective

Create `content-inventory.md` covering active skills/docs/examples that process user input or shape generated-app requirements interpretation.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/README.md
- specs/requirements-intake-alignment-cleanup/conversation-capture.md
- specs/requirements-intake-alignment-cleanup/sprints/01-inventory-and-prune-sprint.md
- specs/requirements-intake-alignment-cleanup/backlog/01-inventory-and-prune-build-backlog.md
- docs/ai-first-saas-application-architecture.md
- docs/requirements-to-workstream-development-process.md
- docs/agent-workstream-application-architecture.md
- docs/capability-first-backend-architecture.md

## In scope

- Inventory all high-priority files from conversation capture.
- Add discovered related active files.
- Classify each file: keep, focused rewrite, heavy rewrite, remove, or demote-to-mechanics-only.
- Record rationale and expected follow-up sprint.

## Out of scope

- Do not edit active guidance except to add new follow-up tasks if necessary.

## Checks

- `git diff --check`
- Search proof that each high-priority path in conversation capture appears in `content-inventory.md`.

## Done criteria

- Inventory exists and is actionable.
- Queue updated and committed.

# Sprint 01: Inventory and Prune Criteria

## Objective

Create the authoritative inventory for this cleanup: identify every active skill/doc/example that participates in user-input intake or planning, classify it as keep, rewrite, remove, or demote-to-mechanics-only, and define the criteria future tasks will use to trim stale content.

## Scope

- Intake and app-description skills.
- PRD/spec/backlog/pending queue skills.
- Docs and examples referenced by those skills.
- Active web UI docs that shape requirements-to-UI interpretation.

## Ordered work areas

1. Build `content-inventory.md` with file-by-file classification.
2. Build `prune-and-rewrite-criteria.md` with active guidance rules.
3. Identify broken-reference risks before any removals.
4. Queue follow-up tasks for ambiguous or large removal/rewrite work.

## Acceptance criteria

- Inventory covers the high-priority files from `conversation-capture.md`.
- Every file gets a proposed action and rationale.
- Removal/rewrite/demotion criteria are explicit enough for future fresh sessions.
- No implementation changes beyond inventory/criteria happen in this sprint unless the task explicitly adds narrow follow-up queue entries.

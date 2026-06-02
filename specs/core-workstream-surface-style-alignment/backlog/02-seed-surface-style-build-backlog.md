# Backlog: Seed Surface Style Review

## Goal

Check seed app-description surface contracts for consistency after core domain surface style alignment.

## Implementation notes

- This is a review-and-small-patch task, not a broad redesign.
- Prefer adding concise `UI style notes` or equivalent sections only when needed.
- If substantial mismatches are found, append a bounded follow-up task instead of expanding scope.

## Suggested harness task breakdown

1. Review and patch seed app-description surface contracts.

## Dependencies

- Core domain workstream surface docs updated.

## Required checks

- `git diff --check`
- Focused stale style/theme search over seed app-description `12-workstreams` and `55-ui` files.

## Acceptance criteria

- Seed app-description surfaces and core domain workstream docs do not contradict each other on style/theme semantics.
- No stale active mode-first or old-style guidance remains in the checked seed surface docs.

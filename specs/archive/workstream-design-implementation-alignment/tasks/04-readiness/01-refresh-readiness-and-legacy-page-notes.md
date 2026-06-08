# TASK-WDA-04-001: Refresh readiness and legacy page notes

## Objective

Update readiness/review/UI documentation to accurately reflect current workstream implementation state, remaining full-core gaps, and the status of legacy page-style frontend artifacts.

## Required reads

- mini-project README, conversation capture, backlog, queue entry, and this task brief
- `app-description/00-system/readiness-status.md`
- `app-description/80-review/latest-readiness-summary.md`
- `app-description/55-ui/ui-index.md`
- `app-description/55-ui/routes-and-deep-links.md`
- `app-description/55-ui/workstream-shell.md`
- `frontend/src/screens/**`
- current workstream backend/frontend files as needed for accurate statements

## Skills

- `app-description-readiness-assessment`
- `app-description-readiness-summary`
- `app-description-ui`

## In scope

- Remove stale planning-only wording where implementation now exists.
- Keep full-core gaps explicit and do not claim production readiness unless proved.
- Fix duplicate Governance/Policy wording.
- Classify `frontend/src/screens/**` as legacy/reference/deep-link compatibility, active routes, or candidates for a future removal/refactor task.
- Append a bounded follow-up task if legacy page artifacts require code cleanup beyond documentation.

## Out of scope

- Removing or refactoring page-style frontend files unless the brief is amended and checks cover the change.
- Full-core SaaS readiness completion.

## Expected outputs

- Updated readiness/review/UI docs.
- Optional appended follow-up task if legacy artifacts need implementation cleanup.

## Required checks

- `git diff --check`
- focused `rg` for corrected readiness wording and legacy page classification

## Done criteria

- Readiness docs match current runtime state and gaps.
- Page-first artifacts are not presented as the primary architecture.
- Changes and queue update are committed.

## Commit message

`workstream-align: refresh readiness notes`

# Task Brief: Review Seed Surface Style Alignment

## Objective

Review starter core app-description surface contracts against the updated core domain workstream surface style guidance and patch small style/theme gaps if found.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/core-workstream-surface-style-alignment/README.md`
- `specs/core-workstream-surface-style-alignment/conversation-capture.md`
- `specs/core-workstream-surface-style-alignment/pending-tasks.md`
- `specs/core-workstream-surface-style-alignment/sprints/02-seed-surface-style-sprint.md`
- `specs/core-workstream-surface-style-alignment/backlog/02-seed-surface-style-build-backlog.md`
- `specs/core-workstream-surface-style-alignment/tasks/02-seed-surface-style/01-review-seed-surface-style-alignment.md`
- `docs/web-ui-style-guide.md`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/style-guide.md`
- `templates/ai-first-saas-starter/app-description/app-description/12-workstreams/surface-contracts/*.md`

## In scope

- Review seed surface contracts for style/theme consistency.
- Add concise style notes only where needed.
- Update queue and optionally create a short findings note if useful.

## Out of scope

- Broad starter core app-description redesign.
- Frontend/backend implementation.

## Expected outputs

- Updated seed surface contract docs if gaps are found.
- Optional findings note.
- Updated queue status.

## Skills

- `app-description-ui`
- `app-description-surface-modeling`

## Required checks

- `git diff --check`
- stale style/theme search over seed `12-workstreams` and `55-ui` files

## Done criteria

- Seed surface contracts are consistent with canonical style guide and updated core domain docs.
- Any remaining larger gaps are queued as follow-up or explicitly documented as non-material.
- Changes and queue update are committed.

## Commit message convention

- `ui-theme: review seed surface style alignment`

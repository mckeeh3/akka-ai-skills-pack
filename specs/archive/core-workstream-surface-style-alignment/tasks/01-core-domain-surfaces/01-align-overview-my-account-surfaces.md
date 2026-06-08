# Task Brief: Align Core Overview and My Account Surface Style

## Objective

Update the core app domain overview and My Account workstream README to inherit the replacement style/theme contract and specify named-theme settings behavior.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/core-workstream-surface-style-alignment/README.md`
- `specs/core-workstream-surface-style-alignment/conversation-capture.md`
- `specs/core-workstream-surface-style-alignment/pending-tasks.md`
- `specs/core-workstream-surface-style-alignment/sprints/01-core-domain-surfaces-sprint.md`
- `specs/core-workstream-surface-style-alignment/backlog/01-core-domain-surfaces-build-backlog.md`
- `specs/core-workstream-surface-style-alignment/tasks/01-core-domain-surfaces/01-align-overview-my-account-surfaces.md`
- `docs/web-ui-style-guide.md`
- `docs/examples/ai-first-saas-core-app-domain/README.md`
- `docs/examples/ai-first-saas-core-app-domain/my-account-workstream/README.md`

## In scope

- Add shared style/theme contract to core domain overview.
- Update My Account settings/theme wording to named theme and `preferredThemeId` semantics.
- Add concise My Account dashboard/settings surface style notes.
- Preserve capability/action semantics.

## Out of scope

- Editing other workstream docs.
- Frontend/backend implementation.

## Expected outputs

- Updated core domain overview and My Account workstream README.
- Updated queue status.

## Skills

- `app-description-ui`

## Required checks

- `git diff --check`
- `rg -n "preferredColorMode|uiMode|light/dark/system|system mode|atlas-ops-supervisory-console|orange|coral" docs/examples/ai-first-saas-core-app-domain/README.md docs/examples/ai-first-saas-core-app-domain/my-account-workstream/README.md` and review results for stale contradictions

## Done criteria

- Overview names or links the replacement style/theme contract.
- My Account settings surface uses named-theme selection with available theme ids and `preferredThemeId` semantics.
- No stale mode-first or old default style contradiction remains in touched files.
- Changes and queue update are committed.

## Commit message convention

- `ui-theme: align core my account surface style`

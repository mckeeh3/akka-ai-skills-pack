# Task Brief: Refresh Starter Theme Tokens

## Objective

Update reference/starter frontend CSS tokens so the replacement style and four named themes are represented as named theme token bundles rather than only light/dark mode bundles.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/web-ui-style-theme-refresh/README.md`
- `specs/web-ui-style-theme-refresh/conversation-capture.md`
- `specs/web-ui-style-theme-refresh/sprints/02-reference-runtime-sprint.md`
- `specs/web-ui-style-theme-refresh/backlog/02-reference-runtime-build-backlog.md`
- `specs/web-ui-style-theme-refresh/tasks/02-reference-runtime/02-refresh-starter-theme-tokens.md`
- `docs/web-ui-style-guide.md`
- `frontend/src/styles/tokens.css`
- `frontend/src/styles/base.css`
- `frontend/src/styles/layout.css`
- `frontend/src/styles/components.css`
- `templates/ai-first-saas-starter/frontend/src/styles/tokens.css`
- template frontend style files identified by search

## In scope

- Define named theme CSS token bundles for four initial themes.
- Align current reference and starter template style tokens with the replacement visual direction.
- Keep component anatomy stable across themes.
- Use document/root theme attributes such as `data-theme`, not scattered hard-coded style branches.
- Update duplicated template/frontend token files consistently.

## Out of scope

- Implementing My Account selector behavior; that is TASK-WUTR-02-003.
- Backend settings persistence.
- Redesigning all React components beyond token/class adjustments needed for theme alignment.

## Expected outputs

- Updated reference frontend CSS tokens/styles
- Updated starter template frontend CSS tokens/styles
- Updated `specs/web-ui-style-theme-refresh/pending-tasks.md`

## Skills

- `akka-web-ui-frontend-project`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`

## Required checks

- `git diff --check`
- frontend type/build checks documented by repository guidance for changed frontend assets, or a clear blocker if prerequisites are unavailable
- targeted search proving old default token names/values are not still the active default where replaced

## Done criteria

- Reference/starter CSS exposes four named theme token bundles.
- Replacement style colors, surfaces, borders, typography, status colors, and functional color usage are reflected in token names/values.
- Existing component classes consume semantic tokens rather than theme-specific hard-coded colors where practical.
- Queue is updated and task changes are committed.

## Commit message convention

- `ui-theme: refresh starter theme tokens`

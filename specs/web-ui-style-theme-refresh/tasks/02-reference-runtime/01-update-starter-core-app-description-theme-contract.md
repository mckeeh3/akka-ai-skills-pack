# Task Brief: Update Starter Core App-Description Theme Contract

## Objective

Update the starter core app-description UI layer so it uses the replacement workstream style and named-theme model, including simple My Account theme selection semantics.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/web-ui-style-theme-refresh/README.md`
- `specs/web-ui-style-theme-refresh/conversation-capture.md`
- `specs/web-ui-style-theme-refresh/sprints/02-reference-runtime-sprint.md`
- `specs/web-ui-style-theme-refresh/backlog/02-reference-runtime-build-backlog.md`
- `specs/web-ui-style-theme-refresh/tasks/02-reference-runtime/01-update-starter-core-app-description-theme-contract.md`
- `docs/web-ui-style-guide.md`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/style-guide.md`
- starter core app-description My Account/settings files identified by search

## In scope

- Update seed `55-ui/style-guide.md` to record replacement style, four initial named themes, default theme, and future theme-extension policy.
- Update My Account/settings description artifacts if they mention theme/mode selection.
- Preserve workstream/surface/capability ownership: `55-ui` owns browser realization and links back to My Account capability/surface contracts instead of redefining backend authorization.

## Out of scope

- Editing frontend CSS or TypeScript.
- Implementing backend persistence.
- Adding app-specific demo names or copied Atlas content.

## Expected outputs

- Updated starter core app-description UI files
- Updated `specs/web-ui-style-theme-refresh/pending-tasks.md`

## Skills

- `app-description-ui`
- `app-description-surface-modeling` if My Account surface contracts need focused updates

## Required checks

- `git diff --check`
- `rg -n "orange|coral|system with light|system mode|light/dark/system|warm near-black" templates/ai-first-saas-starter/app-description/app-description/55-ui templates/ai-first-saas-starter/app-description/app-description/12-workstreams` and review results for stale contradictions

## Done criteria

- Starter core app-description uses the replacement style as selected default.
- Starter core app-description records named themes with two light and two dark initial options.
- My Account theme selection is described as selecting one available theme and applying it to the UI.
- Queue is updated and task changes are committed.

## Commit message convention

- `ui-theme: update starter core app theme contract`

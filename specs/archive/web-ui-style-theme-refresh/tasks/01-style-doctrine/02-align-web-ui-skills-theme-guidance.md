# Task Brief: Align Web UI Skills with Named Theme Guidance

## Objective

Update web UI planning, app-description, generation, and testing skills so future generated UI work uses the replacement style guide and named-theme model without falling back to stale mode-first or old-default assumptions.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/web-ui-style-theme-refresh/README.md`
- `specs/web-ui-style-theme-refresh/conversation-capture.md`
- `specs/web-ui-style-theme-refresh/sprints/01-style-doctrine-sprint.md`
- `specs/web-ui-style-theme-refresh/backlog/01-style-doctrine-build-backlog.md`
- `specs/web-ui-style-theme-refresh/tasks/01-style-doctrine/02-align-web-ui-skills-theme-guidance.md`
- `docs/web-ui-style-guide.md`
- `skills/app-description-ui/SKILL.md`
- `skills/akka-web-ui-apps/SKILL.md`
- focused web UI skills found by targeted search for style/theme/mode references

## In scope

- Update style-guide field expectations to include selected style id/name, available theme ids, default theme id, and My Account theme-selection behavior.
- Update pending style-selection question language if needed so it does not imply mode-first selection.
- Update web UI implementation/testing guidance to require named theme token application and light/dark theme coverage through named themes.
- Remove or rewrite stale old-default assumptions in touched skill docs.

## Out of scope

- Updating canonical docs already handled by TASK-WUTR-01-001.
- Updating starter core app-description and frontend assets; those are Sprint 02 tasks.
- Adding this project-only skill to installable pack assets.

## Expected outputs

- Updated web UI/app-description skill files as needed
- Updated `skills/README.md` only if routing summary text needs theme wording alignment
- Updated `specs/web-ui-style-theme-refresh/pending-tasks.md`

## Skills

- `app-description-ui`
- `akka-web-ui-apps`
- `akka-web-ui-ux-design`
- `akka-web-ui-accessibility-responsive`
- `akka-web-ui-testing`

## Required checks

- `git diff --check`
- `rg -n "mode policy|system with light|light/dark/system|orange|coral|warm near-black|style-gallery|style gallery" skills docs/web-ui-style-guide.md docs/web-ui-quality-checklist.md docs/web-ui-ux-patterns.md` and review results for stale contradictions in touched guidance

## Done criteria

- Web UI skills expect named theme guidance before implementation.
- App-description UI guidance records available/default theme semantics and simple My Account selection where relevant.
- Testing/accessibility guidance checks named themes and status semantics.
- Queue is updated and task changes are committed.

## Commit message convention

- `ui-theme: align web ui skills with named themes`

# Task Brief: Update Canonical Style and Theme Guide

## Objective

Replace the canonical generated AI-first SaaS web UI visual direction in `docs/web-ui-style-guide.md` with the style described by `web-ui-high-level-style-guide.md`, and define the named-theme contract with four initial themes.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/web-ui-style-theme-refresh/README.md`
- `specs/web-ui-style-theme-refresh/conversation-capture.md`
- `specs/web-ui-style-theme-refresh/sprints/01-style-doctrine-sprint.md`
- `specs/web-ui-style-theme-refresh/backlog/01-style-doctrine-build-backlog.md`
- `specs/web-ui-style-theme-refresh/tasks/01-style-doctrine/01-update-canonical-style-theme-guide.md`
- `web-ui-high-level-style-guide.md`
- `docs/web-ui-style-guide.md`
- `docs/web-ui-ux-patterns.md`
- `docs/web-ui-quality-checklist.md`

## In scope

- Update canonical style guide visual direction, component style patterns, token guidance, and style-guide artifact template.
- Define a named-theme model with four initial themes, two light and two dark.
- Document that users select an available named theme, especially through My Account settings.
- Update UX/quality docs only where needed to align with the replacement style and named themes.
- Preserve AI-first workstream shell and structured-surface anatomy.

## Out of scope

- Editing skills; that is TASK-WUTR-01-002.
- Editing seed app-description or frontend CSS; those are Sprint 02 tasks.
- Creating generic style-gallery choices.

## Expected outputs

- Updated `docs/web-ui-style-guide.md`
- Focused updates to `docs/web-ui-ux-patterns.md` and/or `docs/web-ui-quality-checklist.md` if needed
- Updated `specs/web-ui-style-theme-refresh/pending-tasks.md`

## Skills

- none; repository docs task

## Required checks

- `git diff --check`
- `rg -n "orange|coral|system with light|system mode|atlas-ops-supervisory-console|warm near-black" docs/web-ui-style-guide.md docs/web-ui-ux-patterns.md docs/web-ui-quality-checklist.md` and review results for stale default contradictions

## Done criteria

- Canonical style guide clearly treats the high-level guide direction as the replacement default.
- Named themes, not mode-first selection, are the user-facing preference model.
- Four initial themes are specified or explicitly required with two light and two dark.
- Theme token guidance allows future additions without changing component anatomy.
- Queue is updated and task changes are committed.

## Commit message convention

- `ui-theme: replace canonical web ui style guide`

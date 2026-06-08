# Task Brief: Fix Detail-Edit Form Styling and Live Theme Preview

## Objective

Fix the My Account settings `detail-edit` surface so fields are styled and selecting `preferredThemeId` applies the theme immediately.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-form-theme-live-remediation/README.md`
- `specs/workstream-form-theme-live-remediation/conversation-capture.md`
- `specs/workstream-form-theme-live-remediation/pending-tasks.md`
- `specs/workstream-form-theme-live-remediation/sprints/01-runtime-remediation-sprint.md`
- `specs/workstream-form-theme-live-remediation/backlog/01-runtime-remediation-build-backlog.md`
- `specs/workstream-form-theme-live-remediation/tasks/01-runtime-remediation/01-fix-detail-edit-form-and-live-theme-preview.md`
- `user-settings-surface.png`
- `frontend/src/workstream/surfaces/DetailEditSurface.tsx`
- `frontend/src/styles/components.css`
- `frontend/src/main.tsx`
- matching files under `templates/ai-first-saas-starter/frontend/src/`

## In scope

- Add tokenized CSS for `.surface-detail-edit-form`, `.surface-detail-field`, and nested `input/select/textarea` controls.
- Improve layout, label hierarchy, helper/error text, select styling, and read-only/disabled states.
- Add a callback from `DetailEditSurface` or surface action flow so changing editable `preferredThemeId` immediately updates root `data-theme`.
- Mirror changes between reference and starter template frontend.

## Out of scope

- Backend persistence redesign.
- Broad surface redesign unrelated to form controls and theme preview.

## Skills

- `akka-web-ui-state-rendering`
- `akka-web-ui-forms-validation`
- `akka-web-ui-accessibility-responsive`

## Required checks

- `git diff --check`
- `cd frontend && npm test && npm run typecheck && npm run build`
- `cd templates/ai-first-saas-starter/frontend && npm test && npm run typecheck && npm run build`

## Done criteria

- Structured detail-edit form controls no longer render as browser-default controls.
- `preferredThemeId` selection applies the selected named theme immediately.
- Save/Confirm still passes selected field values to the backend action path.
- Changes and queue update are committed.

## Commit message convention

- `ui-theme: fix workstream form styling and theme preview`

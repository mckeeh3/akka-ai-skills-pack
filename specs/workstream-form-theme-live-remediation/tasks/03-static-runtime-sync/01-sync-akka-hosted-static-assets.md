# Task Brief: Sync Akka-Hosted Static Frontend Assets

## Objective

Rebuild and commit the Akka-hosted static frontend assets so the served runtime path contains the structured-surface form styling and live `preferredThemeId` preview behavior already present in the frontend source.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `specs/workstream-form-theme-live-remediation/README.md`
- `specs/workstream-form-theme-live-remediation/conversation-capture.md`
- `specs/workstream-form-theme-live-remediation/pending-tasks.md`
- `specs/workstream-form-theme-live-remediation/tasks/03-static-runtime-sync/01-sync-akka-hosted-static-assets.md`
- `frontend/src/main.tsx`
- `frontend/src/workstream/surfaces/DetailEditSurface.tsx`
- `frontend/src/styles/components.css`
- `src/main/resources/static-resources/index.html`
- `templates/ai-first-saas-starter/frontend/src/main.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DetailEditSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/styles/components.css`
- `templates/ai-first-saas-starter/src/main/resources/static-resources/index.html`

## In scope

- Run the reference frontend production build and commit the updated `src/main/resources/static-resources/**` output.
- Run the starter-template frontend production build and commit the updated `templates/ai-first-saas-starter/src/main/resources/static-resources/**` output.
- Remove obsolete hashed static assets only when replaced by the new build output.
- Verify the committed static JS/CSS contains the live theme-preview propagation and styled detail-edit controls.

## Out of scope

- New frontend source behavior changes beyond correcting an obvious build-output sync issue.
- Broad UI redesign.
- Backend settings persistence changes.

## Skills

- none; build-output synchronization task

## Required checks

- `git diff --check`
- `cd frontend && npm test && npm run typecheck && npm run build`
- `cd templates/ai-first-saas-starter/frontend && npm test && npm run typecheck && npm run build`
- targeted search in `src/main/resources/static-resources/**` and `templates/ai-first-saas-starter/src/main/resources/static-resources/**` for `onFieldValueChange`/live theme callback evidence and `.surface-detail-field` styling evidence

## Done criteria

- Akka-hosted static resources are in sync with frontend source for reference and starter template.
- Served static assets contain detail-edit structured-surface form styling.
- Served static assets contain immediate `preferredThemeId` preview propagation, not only post-save theme application.
- Changes and queue update are committed.

## Commit message convention

- `ui-theme: sync workstream static theme assets`

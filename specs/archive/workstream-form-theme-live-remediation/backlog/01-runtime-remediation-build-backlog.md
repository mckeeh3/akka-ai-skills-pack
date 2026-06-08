# Backlog: Runtime Remediation

## Goal

Make the My Account settings/detail-edit surface look like a first-class enterprise workstream surface and make named theme selection apply immediately.

## Suggested tasks

1. Fix structured-surface form CSS and live theme preview behavior.
2. Add/update frontend tests for styled detail-edit fields and immediate theme preview.

## Required checks

- `git diff --check`
- `cd frontend && npm test && npm run typecheck && npm run build`
- `cd templates/ai-first-saas-starter/frontend && npm test && npm run typecheck && npm run build`

## Acceptance criteria

- Screenshot class of native/default controls is no longer possible in the structured detail-edit path.
- `preferredThemeId` selection changes `data-theme` immediately before save.

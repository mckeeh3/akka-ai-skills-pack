# Sprint 01: Runtime Remediation

## Objective

Fix the structured workstream settings/detail-edit surface so form controls are styled and theme selection previews immediately.

## Scope

- Reference frontend `frontend/src/**`.
- Starter template frontend `templates/ai-first-saas-starter/frontend/src/**`.
- CSS and TypeScript only as needed for this bug.

## Acceptance criteria

- `.surface-detail-field` controls are styled with tokenized input/select/textarea styles.
- The detail-edit form has a coherent grid/stack layout and polished field labels/helper text.
- Changing `preferredThemeId` in a detail-edit surface applies `data-theme` immediately.
- Save/Confirm action still sends the selected theme to backend action handling for persistence.
- Frontend checks pass for both reference and starter template frontends.

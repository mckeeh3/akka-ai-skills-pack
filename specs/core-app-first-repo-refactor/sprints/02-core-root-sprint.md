# Sprint 02: Core App Root Promotion and Template Dissolution

## Objective

Make the repository root the single canonical runnable core app source and remove the large starter template as a maintained duplicate.

## Scope

- Promote or reconcile core app backend/frontend/app-description/spec assets into top-level app paths.
- Convert Java source to the fixed core package/location selected by the architecture decision.
- Remove or archive duplicate template source after parity is proven.
- Preserve generated/static asset rules and root app validation.

## Acceptance criteria

- Root app source is canonical.
- No full-app duplicate source remains under `templates/`.
- Backend/frontend checks pass for the root core app.

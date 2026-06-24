# Task: Wire prefilled surface rendering

## Objective

Standardize and render prefill data for routed surfaces, starting with User Admin Organization Create and invitation forms.

## Required reads

- `AGENTS.md`
- `specs/workstream-surface-intent-routing/README.md`
- `specs/workstream-surface-intent-routing/sprints/02-frontend-prefill-tests.md`
- `frontend/src/main.tsx`
- `frontend/src/workstream/surfaces/OrganizationAdminSurface.tsx`
- `frontend/src/workstream/surfaces/UserAdminTaskSurface.tsx`
- `frontend/src/workstream/types/surfaces.ts`
- `frontend/src/workstream/actions/capabilityActionState.ts`

## Skills

- akka-web-ui-forms-validation
- akka-web-ui-state-rendering
- akka-web-ui-accessibility-responsive

## Expected outputs

- Browser-safe `prefill` handling convention for routed surfaces.
- Organization Create and invitation create forms initialize from prefill while preserving editable fields.
- UI copy indicates fields were prefilled from the request and require user review.
- Frontend tests or contract tests for prefill rendering.
- Queue update.

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- Prefill does not bypass validation, confirmation, idempotency, or submit buttons.
- Users can edit or clear prefilled fields.
- No frontend-only authorization is introduced.
- Changes and queue update are committed.

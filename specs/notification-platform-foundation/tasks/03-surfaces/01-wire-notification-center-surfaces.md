# TASK-NPF-03-001: Wire My Account notification center surfaces

## Objective

Wire backend-derived notification list/preferences into My Account notification center surfaces.

## In scope

- My Account notification center/list surface.
- Notification state badges/counts from backend projection only.
- Actions for mark-read/archive/preference update where backend supports them.
- Frontend tests/typecheck/build.

## Required checks

- `git diff --check`
- frontend tests/typecheck/build
- scaffolded backend tests if API/action contracts change

## Commit message

`notification-foundation: wire surfaces`

# TASK-10-006: Add seed security acceptance tests and PoC alignment notes

## Purpose

Add cross-cutting acceptance coverage for the authenticated seed app and document how the implemented pattern aligns with the working PoC.

## Required reads

- `AGENTS.md`
- `docs/security-workos-auth-and-admin.md`
- `specs/ai-first-skills-pack-migration/sprints/10-authenticated-seed-app-foundation-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/10-authenticated-seed-app-foundation-build-backlog.md`
- outputs from `TASK-10-002` through `TASK-10-005`
- `examples/poc-user-auth-onboarding/AI_REVIEW_NOTES.md`
- `examples/poc-user-auth-onboarding/docs/frontend-with-akka-backend.md`

## Scope

- Add seed-level integration/acceptance tests for authentication, authorization, admin audit, frontend API auth, route hosting, and secret boundaries.
- Cover happy path and denial path behavior.
- Add or update a short reference note mapping implemented seed patterns to PoC guidance and caveats.

## Non-goals

- No new product features.
- No full compliance test suite.
- No acceptance of optional impersonation without explicit previous implementation.

## Skills

- `akka-workos-user-auth`
- `akka-basic-user-admin`
- `akka-http-endpoint-testing`
- `akka-web-ui-testing`
- `app-description-auth-security`

## Expected outputs

- Seed-level acceptance tests.
- PoC alignment/caveat note under an appropriate docs or specs location.

## Required checks

- Run focused seed security acceptance tests.
- Run frontend build/test if frontend project exists.
- Run backend compile/test if practical.

## Done criteria

- The seed foundation is verified as a reusable, authenticated, role-aware base for future DCA slices.

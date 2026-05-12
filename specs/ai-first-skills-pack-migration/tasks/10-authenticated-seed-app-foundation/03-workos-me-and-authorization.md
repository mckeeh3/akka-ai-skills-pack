# TASK-10-003: Implement WorkOS/JWT `/api/me` and backend authorization helper

## Purpose

Implement the authenticated browser bootstrap path and central backend authorization helper for the DCA seed app.

## Required reads

- `AGENTS.md`
- `docs/security-workos-auth-and-admin.md`
- `skills/akka-workos-user-auth/SKILL.md`
- `skills/akka-basic-user-admin/SKILL.md`
- `specs/ai-first-skills-pack-migration/sprints/10-authenticated-seed-app-foundation-sprint.md`
- `specs/ai-first-skills-pack-migration/backlog/10-authenticated-seed-app-foundation-build-backlog.md`
- security domain/components from `TASK-10-002`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/api/MeEndpoint.java`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/security/AuthorizationService.java`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/security/AuthContext.java`
- `examples/poc-user-auth-onboarding/src/main/java/com/example/security/WorkosUserLookup.java`

## Scope

- Add JWT-protected `/api/me`.
- Extract WorkOS subject/email/display claims from request context.
- Link matching invited accounts idempotently and activate when allowed.
- Reject disabled or uninvited users according to the app-description security rules.
- Add centralized authorization helper methods for role/scope checks.
- Add endpoint tests for success, missing JWT, uninvited, disabled, and idempotent linking.

## Non-goals

- No admin management endpoints.
- No frontend AuthKit shell.
- No optional impersonation.

## Skills

- `akka-workos-user-auth`
- `akka-http-endpoints`
- `akka-http-endpoint-jwt`
- `akka-http-endpoint-request-context`
- `akka-http-endpoint-testing`
- `akka-basic-user-admin`

## Expected outputs

- `/api/me` endpoint and browser-facing DTOs.
- Authorization helper/service.
- Endpoint integration tests.

## Required checks

- Run focused `/api/me` and authorization tests.
- Verify response DTOs do not expose internal secrets or full entity state.

## Done criteria

- Browser users can establish local app identity while backend authorization remains authoritative.

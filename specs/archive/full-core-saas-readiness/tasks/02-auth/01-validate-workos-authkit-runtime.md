# TASK-FCSR-02-001: Validate WorkOS/AuthKit runtime boundary

## Objective

Close or precisely block the WorkOS/AuthKit runtime validation and frontend secret-boundary gap for the full-core target.

## Required reads

- full-core readiness gap contract from `TASK-FCSR-01-001`
- `AGENTS.md`
- `app-description/global/policies/foundation-security-and-governance.md`
- `app-description/global/policies/foundation-security-and-governance.md` and `app-description/global/roles/foundation-roles.md`
- `frontend/src/main.tsx`
- `frontend/src/api/HttpWorkstreamApiClient.ts`
- `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`
- `src/main/java/ai/first/application/foundation/identity/**`
- relevant WorkOS/AuthKit tests

## Skills

- `akka-workos-user-auth`
- `akka-http-endpoint-jwt`
- `akka-web-ui-testing`

## In scope

- Validate fail-closed behavior when WorkOS/AuthKit browser config is missing.
- Validate JWT claim extraction and `/api/me` selected AuthContext behavior at the local scope available.
- Add/update tests or smoke documentation for frontend secret boundaries and backend auth denial behavior.
- Block with exact missing provider/config prerequisite if production-like validation cannot run.

## Out of scope

- Invitation acceptance/onboarding implementation; that is a separate task.

## Expected outputs

- Updated auth/security docs or validation notes as needed.
- Backend/frontend tests or smoke evidence.
- Queue update.

## Required checks

- `git diff --check`
- focused backend auth tests
- focused frontend tests/typecheck/build when frontend changes

## Done criteria

- AuthKit/WorkOS runtime boundary is either validated at local scope or blocked with precise actionable prerequisites.
- No frontend secrets are introduced.
- Changes and queue update are committed.

## Commit message

`full-core-ready: validate auth runtime`

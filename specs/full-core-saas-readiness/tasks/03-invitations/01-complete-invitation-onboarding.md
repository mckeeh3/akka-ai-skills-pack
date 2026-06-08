# TASK-FCSR-03-001: Complete invitation onboarding and email outbox readiness

## Objective

Close the invitation onboarding readiness gap at the selected full-core scope, including Resend production boundary, local/dev/test captured outbox, lifecycle actions, idempotency, audit, and tests.

## Required reads

- full-core readiness gap contract from `TASK-FCSR-01-001`
- `AGENTS.md`
- `app-description/domains/core-starter/capabilities/user-and-access-administration.md` and `app-description/domains/core-starter/data-state/auth-context-and-membership-state.md`
- `app-description/domains/core-starter/workstreams/user-admin/behavior.md`
- `app-description/global/policies/foundation-security-and-governance.md`
- `src/main/java/ai/first/application/foundation/invitation/**`
- `src/main/java/ai/first/application/foundation/email/**`
- `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`
- `src/test/java/ai/first/application/foundation/invitation/**`
- `src/test/java/ai/first/application/coreapp/useradmin/InvitationAndUserAdminServiceTest.java`

## Skills

- `akka-saas-invitation-onboarding`
- `akka-resend-email-service`
- `akka-workflow-testing` if workflow lifecycle is added/changed
- `akka-timed-actions` if expiry/reminders are added/changed

## In scope

- Invitation create/resend/revoke/accept/expiry/reminder behavior as needed for full-core scope.
- Resend production email boundary and local/dev/test captured outbox behavior.
- Admin audit/work traces and idempotency.
- Tests for success, duplicate/retry, forbidden, tenant isolation, expired/revoked acceptance, delivery blocked/failure visibility.

## Out of scope

- Marketing/bulk email features.
- App-specific email providers other than Resend.

## Expected outputs

- Backend invitation/email code and tests, and app-description/readiness updates as needed.

## Required checks

- `git diff --check`
- focused invitation/email tests
- broader `mvn test` if shared foundation behavior changes

## Done criteria

- Invitation onboarding works through intended backend/API/workstream path at stated local scope, or precise blockers are recorded and affected tasks are blocked.
- Changes and queue update are committed.

## Commit message

`full-core-ready: complete invitations`

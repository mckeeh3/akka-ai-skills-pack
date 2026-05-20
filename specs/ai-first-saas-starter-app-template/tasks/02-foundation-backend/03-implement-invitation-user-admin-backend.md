# TASK-STARTER-02-003: Implement invitation onboarding and user admin backend slice

## Purpose

Complete the backend foundation slice for invitations and user administration.

## Required reads

- `specs/core-app-full-stack-readiness/invitation-onboarding-reference-slice.md`
- `specs/core-app-full-stack-readiness/user-admin-reference-slice.md`
- `skills/akka-saas-invitation-onboarding/SKILL.md`
- `skills/akka-resend-email-service/SKILL.md`
- `skills/akka-workflows/SKILL.md`
- `skills/akka-timed-actions/SKILL.md`
- `skills/akka-consumers/SKILL.md`
- `skills/akka-views/SKILL.md`

## Expected outputs

- Invitation lifecycle implementation with email/outbox seam.
- User Admin capabilities, endpoints, views, and tests.

## Done criteria

- Invite create/resend/revoke/accept/expire/remind flows are idempotent, audited, and tested.
- User administration enforces membership, role/capability, tenant isolation, and last-admin style safety where in scope.
- Required checks pass, queue status is updated, and changes are committed.

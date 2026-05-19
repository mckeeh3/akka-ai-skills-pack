# TASK-CORE-02-002: Specify invitation onboarding reference slice

## Purpose

Create an implementation-ready reference slice for complete invite onboarding: Invitation state, workflow, outbox/email delivery, expiry/reminder, acceptance/linking, views, endpoints, UI contract, and tests.

## Required reads

- `specs/core-app-full-stack-readiness/auth-user-admin-gap-inventory.md`
- `skills/akka-saas-invitation-onboarding/SKILL.md`
- `skills/akka-workflows/SKILL.md`
- `skills/akka-consumers/SKILL.md`
- `skills/akka-timed-actions/SKILL.md`
- `skills/akka-views/SKILL.md`
- `docs/core-saas-identity-tenancy-admin.md`

## Expected outputs

- `specs/core-app-full-stack-readiness/invitation-onboarding-reference-slice.md`
- follow-up pending tasks may be added if executable code is needed

## Required checks

- Slice covers create, send/capture, delivery failure, resend, revoke, expire, accept, duplicate/idempotent behavior, audit, tenant isolation, and disabled inviter denial.
- `git diff --check`

## Done criteria

- Future code task can implement the reference slice without architecture decisions.
- Queue status and changes are committed.

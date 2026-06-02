# TASK-CORE-02-003: Specify full user administration reference slice

## Purpose

Create an implementation-ready slice for full user, membership, role, support-access, admin-audit, and access-review administration across SaaS Owner, Tenant, and Customer scopes.

## Required reads

- `specs/core-app-full-stack-readiness/auth-user-admin-gap-inventory.md`
- `specs/core-app-full-stack-readiness/invitation-onboarding-reference-slice.md`
- `skills/akka-basic-user-admin/SKILL.md`
- `docs/core-saas-identity-tenancy-admin.md`
- `docs/core-ai-first-saas-foundation.md`
- `templates/ai-first-saas-starter/app-description/app-description/10-capabilities/01-secure-tenant-user-foundation.md`

## Expected outputs

- `specs/core-app-full-stack-readiness/user-admin-reference-slice.md`

## Required checks

- Slice covers UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, role replace/remove, membership suspend/reactivate/remove, account disable/reactivate, identity relink, support access, last-admin protection, and tests.
- `git diff --check`

## Done criteria

- Future implementation tasks have concrete component/API/view/test contracts.
- Queue status and changes are committed.

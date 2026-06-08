# TASK-CORE-02-001: Inventory auth and user admin reference gaps

## Purpose

Compare current docs, skills, frontend fixtures, and Java reference code against the full-core User Admin/onboarding target.

## Required reads

- `specs/core-app-full-stack-readiness/full-core-realization-map.md`
- `docs/core-saas-identity-tenancy-admin.md`
- `skills/akka-workos-user-auth/SKILL.md`
- `skills/akka-basic-user-admin/SKILL.md`
- `skills/akka-saas-invitation-onboarding/SKILL.md`
- `src/main/java/com/example/domain/security/**`
- `src/main/java/com/example/application/security/**`
- `src/main/java/com/example/api/security/**`

## Expected outputs

- `specs/core-app-full-stack-readiness/auth-user-admin-gap-inventory.md`

## Required checks

- Inventory covers WorkOS, `/api/me`, invitation lifecycle, outbox, membership lifecycle, support access, admin views, User Admin UI, and tests.
- No production code rewrite in this inventory task.
- `git diff --check`

## Done criteria

- Gaps are concrete and ordered for follow-up implementation/reference tasks.
- Queue status and changes are committed.

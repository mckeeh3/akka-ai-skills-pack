# TASK-FSFR-06-001: Repair bootstrap audit capability mismatch

## Purpose

Reconcile bootstrap admin `/api/me` expectations around audit capability ids.

## Required reads

- `AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/failure-inventory.md`
- MeService source/test files named by the inventory
- relevant identity/authorization app-description files if named by inventory

## Skills

- `akka-workos-user-auth`
- `akka-basic-user-admin`
- `capability-first-backend`

## Expected outputs

- implementation/test/current-intent repair for `saas_owner.audit.read` vs `audit.trace.read` or equivalent accepted capability semantics
- queue update

## Required checks

- `git diff --check`
- targeted MeService test
- related auth/user-admin tests if capability mapping changes

## Done criteria

- `/api/me` remains frontend-safe and role/capability accurate.
- Audit/Trace authorization is not weakened.
- Changes and queue update are committed.

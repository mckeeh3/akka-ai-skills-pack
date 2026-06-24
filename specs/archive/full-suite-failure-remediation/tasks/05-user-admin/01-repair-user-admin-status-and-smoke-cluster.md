# TASK-FSFR-05-001: Repair User Admin status and browser-smoke cluster

## Purpose

Fix User Admin status disable/reactivate behavior and related browser-smoke failures without weakening admin safeguards.

## Required reads

- `AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/failure-inventory.md`
- User Admin source/test files named by the inventory
- `app-description/domains/core-starter/workstreams/user-admin/**`

## Skills

- `akka-basic-user-admin`
- `akka-saas-invitation-onboarding`
- `capability-first-backend`
- `akka-http-endpoint-testing`

## Expected outputs

- implementation/test/current-intent repair for member status no-op vs accepted behavior
- browser-smoke repair for support-access/system-message coverage where owned by this cluster
- queue update

## Required checks

- `git diff --check`
- targeted User Admin status test
- targeted UserAdminBrowserWorkstreamSmokeTest methods named by inventory

## Done criteria

- Status lifecycle behavior is accepted, idempotent, and traceable.
- Self-action and last-admin safeguards remain enforced.
- Changes and queue update are committed.

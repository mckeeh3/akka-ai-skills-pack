# TASK-FCSR-08-005: Repair or retire stale workstream icon validation tool

## Objective

Remove the false blocker from `tools/prove-workstream-icons-v0.sh`, which currently expects the old package layout.

## Required reads

- `AGENTS.md`
- `specs/full-core-saas-readiness/full-core-readiness-verification.md`
- `specs/full-core-saas-readiness/full-core-runtime-smoke.md`
- `tools/prove-workstream-icons-v0.sh`
- `src/main/java/ai/first/application/foundation/identity/MeResponse.java`
- `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`

## Skills

- none; repository tooling task

## In scope

- Update the script to the current package/layout, or retire/remove it with readiness notes explaining replacement evidence.
- Keep required static secret-boundary scanning intact.

## Out of scope

- Changing workstream icon/product behavior unrelated to the stale proof script.

## Expected outputs

- Repaired script or documented retirement/removal.
- Queue/readiness updates if retained as required evidence.

## Required checks

- `git diff --check`
- `tools/prove-workstream-icons-v0.sh` if repaired and retained.

## Done criteria

- Stale optional validation tooling no longer reports false blockers against the current package layout.
- Changes and queue update are committed.

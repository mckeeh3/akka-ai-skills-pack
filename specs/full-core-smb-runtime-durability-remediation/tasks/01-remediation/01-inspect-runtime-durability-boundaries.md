# Task: Inspect in-memory and fixture runtime paths and define remediation map

## Objective

Inventory all in-memory/mock/fake/fixture/demo/canned/model-less paths in the starter and classify release impact. Append bounded remediation tasks.

## In scope

- Backend normal runtime repositories/defaults.
- Backend test-only fakes.
- Frontend fixture/demo clients and gating.
- Generated static assets that may be stale or fixture-backed.
- Documentation and release-handoff claims.
- Remediation task planning.

## Out of scope

- Do not implement remediation source changes in this inspection task.
- Do not remove test-only fakes simply because they are fakes.

## Expected outputs

- `specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md`
- updated `pending-tasks.md`
- task briefs for appended remediation tasks

## Checks

- Required `rg` inventory scan from `pending-tasks.md`
- targeted `find` discovery commands
- `git diff --check`

## Commit message

- `full-core-smb: map runtime durability remediation`

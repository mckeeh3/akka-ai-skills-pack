# Task: Resolve root frontend mirror check drift

## Objective

Resolve the root `frontend/` mirror check failures discovered while gating frontend fixtures so the mirror can be validated or explicitly retired from the runtime durability remediation scope.

## Required reads

- AGENTS.md
- specs/full-core-smb-runtime-durability-remediation/README.md
- specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
- specs/full-core-smb-runtime-durability-remediation/tasks/01-remediation/04-gate-frontend-fixtures-and-static-assets.md
- frontend/src/workstream-user-admin-expertise.contract.test.mjs
- frontend/src/workstream-user-admin-vertical.contract.test.mjs
- frontend/src/workstream/fixtures/surfaces.ts
- frontend/src/workstream/types/actions.ts
- frontend/src/api/FixtureWorkstreamApiClient.ts

## In scope

- Root `frontend/` mirror test/typecheck drift that prevents validating the fixture gating changes.
- Missing root-side resource paths used by root contract tests, if those tests should still run from the repository root mirror.
- Root fixture/type contract mismatches for `surface-request`, `blocked_provider_or_runtime`, and User Admin surface ids.
- Documentation of no-sync/no-check rationale if the root frontend mirror is intentionally not a supported validation target.

## Out of scope

- Template frontend behavior already gated in task 01-004.
- Backend durability remediation.
- Visual redesign or broad root/template resynchronization beyond making root mirror validation meaningful.

## Expected outputs

- Updated root frontend source/tests or documented no-sync rationale.
- Updated pending queue notes.

## Required checks

- `git diff --check`
- `cd frontend && npm test -- --run && npm run typecheck && npm run build` or a documented no-sync rationale explaining why this root mirror check is no longer required.

## Done criteria

- Root frontend mirror check failures from task 01-004 are resolved or explicitly removed from this remediation scope with rationale.
- Follow-on validation task dependencies are accurate.
- Changes are committed.

## Commit message

- `full-core-smb: resolve root frontend mirror drift`

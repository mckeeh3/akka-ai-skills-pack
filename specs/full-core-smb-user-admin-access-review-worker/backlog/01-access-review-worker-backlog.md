# Backlog: User Admin Access-Review Worker

## Goal

Implement the SMB User Admin access-review worker as a durable, governed, trace-linked workflow that assists admins without taking over deterministic access control.

## Suggested harness task breakdown

1. Inspect current source boundaries and produce `access-review-worker-implementation-map.md` with bounded backend/frontend validation tasks.
2. Implement deterministic access-review task lifecycle and backend capability tests.
3. Implement governed worker/agent runtime integration, scoped evidence, provider-blocked states, and no-direct-mutation tests.
4. Implement frontend task surface/progress/result/blocked-state rendering and contract tests.
5. Run targeted plus broad fullstack validation and verify the mini-project.

## Dependencies

- `specs/full-core-smb-user-admin/user-admin-vertical-contracts.md`
- `specs/full-core-smb-user-admin-access-management/access-management-implementation-map.md`
- `specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md`
- shared baseline/UX contracts from `specs/full-core-smb-baseline-and-ux/`

## Required check categories

- `git diff --check`
- source-boundary proof searches with `find`/`rg`
- backend tests for lifecycle, authorization, tenant isolation, provider blocked states, traces, and no direct mutations
- frontend tests/typecheck/build for workstream surfaces when UI changes
- broad `tools/validate-ai-first-saas-starter-fullstack.sh` before completion

## Acceptance criteria

- Future implementation tasks can run without guessing source paths or validation commands.
- The final implemented worker is durable, governed, scoped, trace-linked, provider-fail-closed, and SMB-bounded.

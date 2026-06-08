# Backlog: UserAdminAgent Guidance

## Goal

Make the User Admin request/response agent useful and safe for SMB administrators.

## Suggested harness task breakdown

1. Inspect current UserAdminAgent seed/runtime/evidence boundaries and define the guidance implementation map.
2. Implement backend governed runtime/evidence/seed updates and tests.
3. Implement frontend guidance/denial surface updates if needed.
4. Validate fullstack runtime and verify mini-project completion.

## Required checks

- `git diff --check`
- targeted backend tests for agent runtime/seed/tool-boundary/evidence behavior
- targeted frontend tests/typecheck when UI changes
- `tools/validate-ai-first-saas-starter-fullstack.sh` for broad runtime validation

## Acceptance criteria

- The UserAdminAgent guidance slice is useful, scoped, traceable, and provider-backed.
- Later access-review worker tasks can rely on deterministic evidence plus agent guidance without using the request/response agent as a worker substitute.

# Backlog: User Admin Access Management

## Goal

Deliver the next practical SMB User Admin slice after invitations: manage member status and roles/capabilities safely.

## Suggested harness task breakdown

1. Inspect source boundaries and refine implementation contracts for member status and role/capability changes.
2. Implement backend deterministic status/role capabilities and tests.
3. Implement frontend member/role surfaces/actions and tests.
4. Run fullstack validation and verify mini-project completion.

## Required checks

- `git diff --check`
- targeted rendered-starter backend tests for User Admin services/workstream actions
- targeted frontend contract tests/typecheck/build
- `tools/validate-ai-first-saas-starter-fullstack.sh` when broad runtime/API/UI behavior is claimed

## Acceptance criteria

- A small/medium business admin can manage member access through the User Admin workstream at the stated scope.
- Later UserAdminAgent and access-review worker tasks can rely on deterministic status/role capabilities rather than model-owned authority.

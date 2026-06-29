# User Admin Implementation Alignment Plan

## Scope

Align refreshed User Admin current intent with backend/frontend/tests/runtime-validation evidence.

## Current-intent anchors

- `app-description/domains/core-starter/workstreams/user-admin/**`
- capability: `user-and-access-administration`
- data state: auth context, membership, invitation, admin audit, access review state

## Evidence to inspect

- User/invitation/membership/role/support-access backend services and endpoints.
- Invitation workflow/outbox/timer/view evidence.
- User directory, membership, invitation, admin audit, access-review tests.
- User Admin frontend surfaces and API client calls.
- Runtime-validation scenario state for invite, role/status/support, denials, last-admin guard, and audit traces.

## Expected alignment output

- Update source-alignment/lifecycle evidence where proven.
- Queue exact remediation tasks for implementation, tests, or runtime-validation gaps.

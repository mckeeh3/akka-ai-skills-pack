# Task: Inspect UserAdminAgent runtime boundaries and define guidance implementation map

## Objective

Inspect UserAdminAgent seed/runtime/tool/evidence boundaries and append bounded implementation tasks for request/response guidance.

## Expected outputs

- `specs/full-core-smb-user-admin-agent-guidance/agent-guidance-implementation-map.md`
- updated queue with backend/frontend source-edit tasks

## Guardrails

- Request/response guidance only; no direct access mutations.
- Governed Akka Agent runtime path and provider fail-closed behavior remain mandatory.
- Evidence reads must be scoped and authorized.

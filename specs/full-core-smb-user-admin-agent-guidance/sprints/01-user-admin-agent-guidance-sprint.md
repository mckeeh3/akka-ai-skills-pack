# Sprint 01: UserAdminAgent Guidance Slice

## Objective

Implement or queue the UserAdminAgent request/response guidance slice using deterministic User Admin evidence and governed Akka Agent runtime semantics.

## Scope

- guidance contract and source boundary map;
- seed prompt/skill/reference/tool-boundary updates where needed;
- safe evidence read tools/facades if needed;
- runtime tests for allowed/denied evidence, provider fail-closed behavior, no mutation, and traces;
- frontend rendering updates only where needed.

## Acceptance criteria

- UserAdminAgent can explain User Admin state, blocked invitations, member status, role/capability changes, and safe next steps using authorized evidence.
- UserAdminAgent cannot mutate users, roles, invitations, or access state directly.
- Missing provider config produces actionable `system_message`/blocked behavior and traces.
- Fullstack validation passes or concrete blocker tasks are appended.

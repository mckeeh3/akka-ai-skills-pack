# Sprint 02: Shared Runtime Substrate

## Goal

Implement the backend substrate for confirmed workstream chat tool plans before workstream-specific execution flows are added.

## Scope

- Add typed plan proposal, plan step, confirmation, execution result, and partial-failure DTOs/surfaces.
- Add governed runtime plan proposal through the workstream agent path with provider fail-closed behavior.
- Add a backend-owned chat tool catalog and dispatcher that maps plan steps to existing governed tool/action execution paths.
- Ensure every step carries governed tool id, capability id, actor adapter, input schema, idempotency key, policy/approval metadata, and trace ids.
- Preserve deterministic surface routing before plan proposal where applicable.

## Completion signal

Sprint 02 is complete when the app can propose and persist/display a plan without executing it, and can execute a confirmed test plan through a shared dispatcher in targeted backend tests.

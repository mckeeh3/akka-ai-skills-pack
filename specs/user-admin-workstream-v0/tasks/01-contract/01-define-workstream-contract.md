# Task: Define User Admin Workstream v0 contract and capability inventory

## Objective

Define the v0 workstream contract for `User Admin Agent` before coding.

## Expected outputs

- `specs/user-admin-workstream-v0/workstream-contract.md`
- `specs/user-admin-workstream-v0/capability-inventory.md`

## Done criteria

- Capability inventory includes actors/callers, AuthContext, schemas, side effects, idempotency, approval/policy, audit/trace, exposure channels, tests, and agent-type selection.


## Required inherited reads

- `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md`
- `specs/five-core-workstreams-v0-plan/workstream-dependency-map.md`

## Shared-contract alignment

The contract and capability inventory must explicitly preserve AuthContext, backend authorization, request/response Akka Agent default for user-facing turns, optional AutonomousAgent use only for durable task-oriented work, deterministic non-AI service boundaries, ToolPermissionBoundary, trace/audit requirements, provider fail-closed behavior, UI/API validation, and terminal verification expectations from the shared five-core v0 contract.

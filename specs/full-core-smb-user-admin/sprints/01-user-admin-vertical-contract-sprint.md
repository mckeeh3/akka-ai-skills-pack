# Sprint 01: User Admin Vertical Contract

## Objective

Define implementation-ready User Admin vertical slices for SMB full-core hardening.

## Scope

- User Admin dashboard/surface contracts.
- Capability ids, AuthContext, tenant/customer, role/capability, and disabled-user rules.
- Deterministic services for invitations, memberships, roles, status changes, access-review support, projections, and traces.
- Request/response User Admin Agent behavior and provider fail-closed path.
- Access-review internal-worker task contract where durable lifecycle is justified.
- Local runtime/API/UI validation map.

## Acceptance criteria

- The next source-edit task can implement one bounded User Admin vertical slice without guessing.
- User Admin remains workstream/surface-first and attention-first.
- Agent, worker, deterministic-service, audit, and validation boundaries are explicit.

# Sprint 02: Five Core Workstream Verticals

## Objective

Implement each core workstream as a real vertical slice: backend capabilities, Akka substrate, managed-agent expertise where applicable, frontend surfaces, action handling, audit/work traces, tests, and local smoke validation.

## Vertical order

1. My Account
2. User Admin
3. Agent Admin
4. Audit/Trace
5. Governance/Policy

## Acceptance criteria

For each workstream:

- role-specific dashboard or aggregate dashboard surface exists;
- protected list/detail/form/card/timeline surfaces render through real backend APIs;
- every surface action maps to a governed capability/governed-tool;
- backend authorization, tenant/customer scope, idempotency, audit, denial, and trace behavior are tested;
- functional-agent prompts/tool boundaries/expertise surfaces are updated when the agent can invoke or explain the capability;
- local runtime/API/UI smoke validates the named visible capability.

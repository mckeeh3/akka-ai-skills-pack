# TASK-FC5-07-001: Implement Governance/Policy full-core vertical

## Objective

Implement Governance/Policy full-core surfaces and capabilities for policy registry, clauses, simulations, proposals, approval/decision cards, exceptions, and activation/rollback where allowed.

## Required reads

- `specs/full-core-five-workstreams/full-core-contract-matrix.md`
- `skills/ai-first-saas-policy-governance/SKILL.md`
- `skills/ai-first-saas-decision-cards/SKILL.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- `docs/capability-first-backend-architecture.md`

## Expected outputs

- Policy dashboard, policy list/detail, clause card, simulation result, proposal diff, approval/decision card, exception/deviation surfaces.
- Governed capabilities for policy read/search, simulate, propose, approve/reject, activate/deprecate/rollback where in scope.
- Backend approval gates, idempotency, policy/audit/work trace events, tenant isolation and forbidden tests.
- GovernancePolicyAgent expertise/tool-boundary updates.
- Frontend rendering/action tests and local smoke path.

## Checks

- `mvn test`
- `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- local smoke path for policy list/simulation/proposal decision
- `git diff --check`

## Done criteria

Governance/Policy supports real governed policy work through protected surfaces/actions and approval-aware backend capabilities.

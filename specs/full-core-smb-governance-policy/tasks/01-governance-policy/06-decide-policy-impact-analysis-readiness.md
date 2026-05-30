# Task: Decide policy-impact analysis worker readiness

## Objective

Decide and implement only the bounded Governance/Policy impact-analysis readiness path justified by completed deterministic policy/proposal/simulation foundations.

## Required reads

- AGENTS.md
- specs/full-core-smb-governance-policy/README.md
- specs/full-core-smb-governance-policy/governance-policy-implementation-map.md
- specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
- specs/full-core-smb-user-admin-access-review-worker/access-review-worker-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- task outputs from `TASK-FCSMB-GP-01-002`, `TASK-FCSMB-GP-01-003`, and `TASK-FCSMB-GP-01-005`

## In scope

- Compare Governance/Policy impact-analysis needs against the worker selection rule and existing User Admin access-review worker seam.
- If no real worker task runtime is justified, preserve or improve the typed blocked/provider-runtime surface and tests; record follow-up recommendation only.
- If a deterministic task record is justified, implement start/read/cancel/accept-result task state only with provider-blocked normal execution unless a real model-backed worker is also implemented and validated.
- Ensure worker output cannot approve, activate, roll back, mutate policy, mutate users, mutate agent behavior, change provider config, or bypass deterministic simulation/authorization.
- Update queue if this task discovers a bounded real worker implementation is needed beyond readiness/blocked path.

## Out of scope

- Do not claim successful model-backed policy-impact analysis unless a concrete provider-backed AutonomousAgent/task runtime path is implemented and locally validated.
- Do not broaden into exception clustering, replay/evaluation batches, approval-summary drafting, or stale exception review unless appended as separate future tasks.

## Expected outputs

- updated backend/frontend blocked readiness surfaces or bounded task lifecycle files if justified
- updated tests proving blocked/readiness semantics and no fake progress
- optional appended follow-up task if real worker implementation is selected but too large

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=GovernancePolicyServiceTest,WorkstreamServiceTest,UserAdminAccessReviewServiceTest,UserAdminAccessReviewWorkerTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-governance-policy-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs
rg -n "governance\.policy\.analysis|impact analysis|AutonomousAgent|blocked_provider_or_runtime|no fake|no direct mutation|provider|ToolPermissionBoundary|AgentWorkTrace" templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- Policy-impact analysis worker status is explicit, safe, tested, and not model-less successful.
- Any follow-up real worker work is appended as bounded tasks before verification.
- Deterministic policy/proposal/simulation/decision lifecycle remains the authority boundary.

## Commit message

- `full-core-smb: decide governance policy worker readiness`

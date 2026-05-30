# Task: Implement Governance/Policy simulation and decision lifecycle

## Objective

Implement deterministic proposal simulation, approve/reject decision cards, activation blocking/activation where safe, rollback blocking/rollback where safe, idempotency, and audit/work traces.

## Required reads

- AGENTS.md
- specs/full-core-smb-governance-policy/README.md
- specs/full-core-smb-governance-policy/governance-policy-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/governance-policy-workstream-v0/workstream-contract.md
- task output from `TASK-FCSMB-GP-01-002`
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/GovernancePolicyService.java
- templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/GovernancePolicyServiceTest.java

## In scope

- Add deterministic simulation over scoped proposal/policy evidence available in the starter.
- Implement approve/reject proposal lifecycle with explicit human actor, authority basis, rationale, trace ids, and no-op duplicate behavior.
- Implement activation and rollback commands only where a proposal has safe version/rollback metadata; otherwise fail closed with typed blocked/approval-required surfaces.
- Update Workstream action routing to use the service-backed lifecycle.
- Add tests for success, validation, forbidden, tenant isolation, idempotency/no-op, stale proposal, missing approval, missing rollback metadata, and trace links.

## Out of scope

- Do not let GovernancePolicyAgent or model output approve, activate, or roll back changes.
- Do not implement enterprise policy-as-code or compliance frameworks.
- Do not implement a policy-impact worker success path.

## Expected outputs

- updated `GovernancePolicyService.java`, repository/domain records, and `WorkstreamService.java`
- updated/new backend tests for simulation and decision lifecycle

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=GovernancePolicyServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest
rg -n "governance\.policy\.(simulate|approve|reject|activate|rollback)|simulation|approve|reject|activate|rollback|approval-required|blocked_provider_or_runtime|rollback metadata|idempotency|no direct mutation|trace" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- Simulation is advisory and deterministic.
- Approval/rejection/activation/rollback lifecycle is backend-owned, idempotent where required, scoped, audited, and fail-closed for unsafe states.
- Prompt text, skills, frontend affordances, and model output cannot commit policy changes.

## Commit message

- `full-core-smb: implement governance policy decision lifecycle`

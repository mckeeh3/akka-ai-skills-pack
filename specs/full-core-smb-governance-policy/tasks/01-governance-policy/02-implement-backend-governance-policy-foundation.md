# Task: Implement deterministic backend Governance/Policy foundation

## Objective

Add a focused backend Governance/Policy service/repository/domain boundary for dashboard, inventory, policy detail, and proposal draft/submit/read lifecycle.

## Required reads

- AGENTS.md
- specs/full-core-smb-governance-policy/README.md
- specs/full-core-smb-governance-policy/governance-policy-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/governance-policy-workstream-v0/workstream-contract.md
- templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
- templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java

## In scope

- Create or update deterministic Governance/Policy service/repository/domain files named in the implementation map.
- Move or wrap dashboard/inventory/detail/proposal draft/submit/read behavior behind backend-authoritative service methods.
- Add proposal lifecycle state and idempotency/no-op behavior for draft and submit.
- Add dynamic surface routing for Governance/Policy surface ids.
- Add focused backend tests for authorized reads, forbidden/tenant mismatch, idempotent proposal draft/submit, browser-safe DTOs, and trace ids.

## Out of scope

- Do not implement simulation/approval/activation/rollback lifecycle beyond safe placeholders needed by this foundation.
- Do not implement GovernancePolicyAgent evidence tools.
- Do not implement frontend rendering beyond backend DTO compatibility if tests require minimal shape fixes.
- Do not implement policy-impact worker success behavior.

## Expected outputs

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/GovernancePolicyService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/GovernancePolicyRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/SubstituteGovernancePolicyRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/GovernancePolicyProposal.java`
- updated `WorkstreamService.java`
- backend tests, likely `GovernancePolicyServiceTest.java` and `WorkstreamServiceTest.java`

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=GovernancePolicyServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest
rg -n "GovernancePolicyService|GovernancePolicyRepository|GovernancePolicyProposal|governance\.policy\.(dashboard|list|read|proposal)|proposal lifecycle|idempotency|tenant|system_message|trace" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- Backend reads and proposal draft/submit/read are service-backed, tenant-scoped, redacted, idempotent where required, and traced.
- Future simulation/decision lifecycle can build on a real proposal record without guessing source paths.
- No model output or frontend state grants policy authority.

## Commit message

- `full-core-smb: implement governance policy backend foundation`

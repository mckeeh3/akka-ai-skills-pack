# Sprint 06: Executable Reference-Governance Coverage

## Objective

Close the gap between documented reference-governance doctrine and starter backend runtime behavior. The starter should have executable first-class coverage for `ReferenceDocument`, `AgentReferenceManifest`, compact reference manifest assembly, authorized `readReferenceDoc(referenceId)`, denied reference loads, `read_reference` tool-boundary denial, and `ReferenceLoadTrace`/equivalent trace emission.

## Scope

Likely source files:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/**`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/**`
- `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/**`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/**`
- `templates/ai-first-saas-starter/frontend/src/workstream-user-admin-expertise.contract.test.mjs` only if fixture assertions need alignment
- `docs/agent-coverage-matrix.md`
- starter validation scripts only if they need to include the new backend tests

## Deliverables

- First-class starter domain records or clearly named interim records for `ReferenceDocument` and `AgentReferenceManifest`.
- Seed loader imports User Admin reference resources into governed state with provenance, checksum, idempotency, and customization-preserving upgrade behavior.
- Runtime prompt assembly includes separate compact skill and reference manifest sections without full bodies.
- Runtime `readReferenceDoc(referenceId)` enforces tenant, active agent, active reference manifest assignment, active reference version/status, mode, token/secret/redaction checks, and `READ_REFERENCE` boundary grant.
- Backend tests cover assigned reference success, unassigned reference denial, missing `read_reference` grant denial, disabled agent denial, compact manifest-only assembly, and trace emission.
- Coverage matrix updated to reflect executable starter coverage.

## Checks

- `git diff --check`
- Targeted backend tests for `AgentBehaviorSeedLoaderTest`, `AgentRuntimeServiceTest`, and any new reference-governance tests.
- `tools/validate-ai-first-saas-starter-fullstack.sh` when starter template changes are broad enough to warrant full validation.

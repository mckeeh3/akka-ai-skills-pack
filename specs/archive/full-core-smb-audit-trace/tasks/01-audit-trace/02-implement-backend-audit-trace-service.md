# Task: Implement backend deterministic Audit/Trace service

## Objective

Implement the deterministic backend Audit/Trace service/repository facade and tests for dashboard, search, detail, timeline, failure evidence, and investigation guidance.

## Required reads

- AGENTS.md
- specs/full-core-smb-audit-trace/README.md
- specs/full-core-smb-audit-trace/audit-trace-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/audit-trace-workstream-v0/workstream-contract.md

## In scope

- Add or extract `AuditTraceService` and repository/DTO helpers as mapped.
- Preserve `WorkstreamService` action ids and surface ids.
- Authorize selected AuthContext, tenant/customer scope, active membership, non-disabled actor, and Audit/Trace capabilities.
- Validate query scope/page/filter inputs and return non-enumerating hidden evidence results.
- Normalize available `AgentRuntimeTrace` and workstream log evidence; include extension seams for User Admin, Agent Admin, worker, and Governance/Policy trace sources.
- Add backend tests for allowed reads, validation errors, tenant/customer denial, redaction, correlation, and failure evidence.

## Out of scope

- Do not implement frontend rendering changes.
- Do not implement AuditTraceAgent evidence tools.
- Do not implement audit-summary worker lifecycle.
- Do not broaden to SIEM, legal hold, e-discovery, or retention consoles.

## Expected outputs

- Backend source changes under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/`.
- Backend tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/`.
- Updated queue status.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=WorkstreamServiceTest,AdminEndpointIntegrationTest,AgentRuntimeTraceEntityTest,AgentRuntimeTraceViewTest,AgentRuntimeTraceSinkTest
rg -n "AuditTraceService|AuditTraceRepository|audit\.trace\.(dashboard|search|detail|timeline|failureEvidence|investigationGuide)|redacted|correlation|not_found_or_redacted|provider|tool|model|tenant" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- Backend Audit/Trace reads are deterministic, tenant-scoped, redacted, correlated, and tested.
- Future frontend and agent-tool tasks can consume stable backend DTOs without guessing.
- No model-backed or worker behavior is claimed complete.

## Commit message

- `full-core-smb: implement audit trace backend service`

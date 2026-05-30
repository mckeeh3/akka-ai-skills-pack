# Task: Implement AuditTraceAgent evidence tool and governed runtime tests

## Objective

Add a governed read-only Audit/Trace evidence tool for AuditTraceAgent, update seeds/tool boundaries, and test provider fail-closed/model-backed explanation behavior.

## Required reads

- AGENTS.md
- specs/full-core-smb-audit-trace/README.md
- specs/full-core-smb-audit-trace/audit-trace-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- docs/agent-component-selection-guide.md
- skills/akka-agents/SKILL.md
- skills/akka-agent-tools/SKILL.md
- skills/akka-agent-tool-boundaries/SKILL.md
- skills/akka-agent-seed-documents/SKILL.md

## In scope

- Implement `AuditTraceEvidenceTools` as a request-scoped read-only facade over deterministic Audit/Trace evidence.
- Register `auditTraceEvidence.read` in `ToolRegistry` with stable tool id, capability, category, and no side effects.
- Grant the tool in AuditTraceAgent's seeded `ToolPermissionBoundary` only as read-only data lookup.
- Update Audit/Trace prompt/skill/reference seed guidance only to explain evidence-tool use and boundaries.
- Add tests for tool resolution, allowed/denied tool calls, tenant mismatch denial, provider fail-closed workstream message behavior, and no deterministic/model-less successful normal response.

## Out of scope

- Do not let AuditTraceAgent own trace ingestion, authorization, redaction, correlation, export, or worker lifecycle.
- Do not add side-effecting tools.
- Do not expose raw prompt text, provider credentials, JWTs, invitation tokens, or cross-tenant data.

## Expected outputs

- Backend source changes under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/` and related seed resources.
- Backend tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/` and/or `.../application/security/`.
- Updated queue status.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,WorkstreamServiceTest
rg -n "AuditTraceAgent|auditTraceEvidence\.read|ToolPermissionBoundary|readSkill|readReferenceDoc|AgentWorkTrace|PromptAssemblyTrace|provider|blocked_provider_or_runtime|no direct mutation|tenant" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/main/resources templates/ai-first-saas-starter/backend/src/test/java --glob '!**/node_modules/**'
git diff --check
```

## Done criteria

- AuditTraceAgent can access scoped deterministic evidence only through governed tools and loader tools.
- Missing provider/model config fails closed with typed system-message surfaces and traces.
- Tests prove no deterministic/model-less normal explanation is used to claim model-backed completion.

## Commit message

- `full-core-smb: implement audit trace agent evidence tool`

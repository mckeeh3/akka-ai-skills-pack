# Task: Run integrated Audit/Trace validation

## Objective

Run targeted and broad validation for the completed Audit/Trace implementation group, fix bounded blockers if they are within scope, and append follow-up tasks before terminal verification if material gaps remain.

## Required reads

- AGENTS.md
- specs/full-core-smb-audit-trace/README.md
- specs/full-core-smb-audit-trace/conversation-capture.md
- specs/full-core-smb-audit-trace/pending-tasks.md
- specs/full-core-smb-audit-trace/audit-trace-implementation-map.md
- specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md
- specs/audit-trace-workstream-v0/workstream-contract.md

## In scope

- Run targeted backend, frontend, and search-proof checks from the implementation map.
- Run broad starter fullstack validation or record a concrete blocker.
- Fix bounded validation blockers only when they belong to the completed Audit/Trace group and can be addressed in this task.
- Append new bounded tasks plus a new terminal verification task if material gaps remain.

## Out of scope

- Do not expand into Governance/Policy implementation or enterprise audit scope.
- Do not implement unrelated UI redesigns or worker features.

## Expected outputs

- Validation notes in queue/task notes or a small verification artifact if needed.
- Bounded fixes for validation blockers, if any.
- Updated `pending-tasks.md` with follow-up tasks if gaps remain.

## Required checks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=WorkstreamServiceTest,AdminEndpointIntegrationTest,AgentRuntimeTraceEntityTest,AgentRuntimeTraceViewTest,AgentRuntimeTraceSinkTest
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-audit-trace-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/api.contract.test.mjs
rg -n "Audit/Trace|AuditTraceAgent|audit\.trace|auditTraceEvidence\.read|trace dashboard|trace search|timeline|correlation|evidence|redacted|provider|tool|model|worker|system_message|AgentWorkTrace|PromptAssemblyTrace|no secret|tenant" templates/ai-first-saas-starter --glob '!**/node_modules/**'
tools/validate-ai-first-saas-starter-fullstack.sh
git diff --check
```

## Done criteria

- Targeted backend/frontend checks pass or blockers are recorded as bounded follow-up tasks.
- Broad starter validation passes or a concrete blocker is recorded and queued.
- Terminal verification can determine whether Audit/Trace full-core readiness has been achieved.

## Commit message

- `full-core-smb: validate audit trace full core`

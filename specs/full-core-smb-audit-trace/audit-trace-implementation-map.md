# Audit/Trace Implementation Map

## Discovery commands used

```bash
find templates/ai-first-saas-starter -path '*/node_modules' -prune -o -type f -print | sort | rg -n "(Audit|audit|Trace|trace|Timeline|timeline|Correlation|correlation|Evidence|evidence|AgentWorkTrace|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|WorkstreamLog|WorkstreamService|AgentRuntime|AuditTraceAgent|frontend|surface|Surface|test|api)"
rg -n "Audit/Trace|AuditTrace|audit\.trace|audit_trace|audit-trace|trace dashboard|trace search|timeline|correlation|evidence|redacted|provider|tool|model|worker|system_message|AgentWorkTrace|PromptAssemblyTrace|no secret|tenant" templates/ai-first-saas-starter --glob '!**/node_modules/**'
rg -n "AUDIT|Audit|audit\.trace|agent-audit|audit-trace|surface-audit|TRACE|trace" templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts templates/ai-first-saas-starter/frontend/src/workstream-audit-trace-vertical.contract.test.mjs templates/ai-first-saas-starter/frontend/src/workstream/types/surfaces.ts
rg -n "agent-audit-trace|AuditTrace|audit trace|audit_trace|readSkill|readReferenceDoc|ToolPermissionBoundary|auditSummary|summary" templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1 templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoaderTest.java
rg -n "auditTrace|audit.trace|Audit/Trace|summaryTask|Frontend affordances|Autonomous audit" templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts
```

## Current source state

The starter has a useful Audit/Trace v0 surface layer, but the full-core substrate is still mixed into `WorkstreamService` and does not yet expose a focused deterministic Audit/Trace service/repository boundary.

Implemented foundations:

- `WorkstreamService` defines Audit/Trace capability/action constants for dashboard, search, detail, timeline, failure evidence, and investigation guidance:
  - `audit.trace.dashboard.read`
  - `audit.trace.search`
  - `audit.trace.detail.read`
  - `audit.trace.timeline.read`
  - `audit.trace.failureEvidence.read`
  - `audit.trace.investigationGuide.read`
- `WorkstreamService` handles action ids:
  - `action-audit-trace-dashboard`
  - `action-audit-trace-search`
  - `action-audit-trace-detail`
  - `action-audit-trace-timeline`
  - `action-audit-trace-failure-evidence`
  - `action-audit-trace-investigation-guide`
- `WorkstreamService` has initial dashboard/search/detail/timeline/failure/guidance surface shaping with selected-tenant checks and browser-safe redaction copy.
- `AgentRuntimeTraceEntity`, `AgentRuntimeTraceSink`, `AgentRuntimeTraceView`, `AkkaAgentRuntimeTraceSink`, and `SubstituteAgentRuntimeTraceSink` provide governed-agent trace persistence/search foundations for prompt assembly, skill/reference loads, tool invocation/denial, model invocation, provider failures, behavior edits, and AgentWorkTrace facts.
- `WorkstreamLogRepository`, `DurableWorkstreamLogEntity`, and `AkkaWorkstreamLogRepository` persist workstream message items/surfaces by tenant and selected context; Audit/Trace can use these as correlation/workstream evidence.
- `AuthContextResolver` and existing User Admin/Agent Admin services emit protected-read/denial traces and source-specific trace ids.
- Audit/Trace seeded managed-agent records exist through `AgentBehaviorSeedLoader`:
  - `agent-audit-trace`
  - `prompt-audit-trace-system`
  - `skill-audit-trace-starter-guidance`
  - `ref-audit-trace-starter-scope`
  - `manifest-audit-trace`
  - `reference-manifest-audit-trace`
  - `tool-boundary-audit-trace`
- AuditTraceAgent can currently use governed loader tools `readSkill` and `readReferenceDoc` through the managed runtime; no Audit/Trace evidence tool exists yet.
- Frontend fixtures and renderers already cover `dashboard`, `list-search`, `detail-edit`, `audit-timeline`, `decision`, `system_message`, trace links, partial/redacted state, denial/provider/tool evidence, and blocked audit-summary worker copy.
- `workstream-audit-trace-vertical.contract.test.mjs` proves the fixture-level Audit/Trace surface vocabulary and renderer support.

Material gaps for SMB full-core:

1. Audit/Trace reads are browser-shaped directly inside `WorkstreamService`; there is no focused deterministic `AuditTraceService` with reusable contracts for authorization, query validation, redaction, correlation assembly, failure evidence, and investigation next steps.
2. Search currently combines one synthetic auth-context row plus Akka component-backed `agentRuntimeService.traces()` rows. It does not query `AgentRuntimeTraceView`, workstream log entries, User Admin audit/admin-change evidence, Agent Admin behavior-change evidence, access-review task evidence, or Governance/Policy traces through a unified repository facade.
3. Detail/timeline reads are limited to agent runtime traces and synthetic rows. They do not normalize event kinds across AdminAuditEvent, workstream messages, surface actions, capability denials, invitation/member/role events, behavior-change proposals, worker tasks, or provider/tool failures.
4. Redaction is represented in copy and DTO fields, but there is no deterministic redaction policy object/test suite proving no raw JWTs, invitation tokens, provider credentials, hidden prompt text, raw tool payloads, or cross-tenant data reach browser DTOs.
5. Cross-workstream trace links exist, but they are not all routed through an Audit/Trace detail/timeline service that returns non-enumerating not-found-or-redacted results for hidden evidence.
6. AuditTraceAgent has prompt/skill/reference seeds but lacks a read-only `auditTraceEvidence.read` tool. Model-backed explanations therefore cannot load scoped trace search/detail/timeline evidence through a named governed tool boundary.
7. `tool-boundary-audit-trace` grants only `readSkill` and `readReferenceDoc`; it does not grant an Audit/Trace evidence data lookup.
8. AuditTraceAgent explanation is possible through the generic workstream message path, but tests must explicitly prove provider fail-closed behavior and forbid deterministic/model-less successful normal explanations.
9. The audit-summary worker is only fixture/deferred copy. It should remain blocked until deterministic trace search/detail/timeline foundations and provider/tool-boundary task semantics justify a durable worker.
10. Frontend fixtures are richer than backend DTOs in denials/failures cards, trace health cards, and cross-workstream link copy. Runtime DTOs and tests need alignment.

## Vertical slice sequence

### Slice 1 — Deterministic Audit/Trace service, repository facade, and runtime DTOs

Goal: extract the current `WorkstreamService` Audit/Trace behavior into a focused deterministic service that can safely search, read detail, assemble timelines, shape failure evidence, and provide deterministic investigation next steps.

Capabilities:

- `audit.trace.dashboard.read`
- `audit.trace.search`
- `audit.trace.detail.read`
- `audit.trace.timeline.read`
- `audit.trace.failureEvidence.read`
- `audit.trace.investigationGuide.read`

Deterministic responsibilities:

- authorize selected `AuthContext`, active membership, non-disabled actor, tenant/customer scope, and exact Audit/Trace read capability;
- validate query filters for page size, time range, actor/workstream/capability/event kind/outcome/trace id/correlation id, tenant id, and customer id;
- return non-enumerating not-found-or-redacted results for hidden or cross-scope trace ids;
- normalize events from `AgentRuntimeTrace`, workstream message logs, protected-read/denial traces, AdminAuditEvent-like source records, User Admin changes, Agent Admin behavior changes, access-review task lifecycle, and Governance/Policy proposal/decision traces where source data exists;
- construct browser-safe dashboard/search/detail/timeline/failure/guidance DTOs;
- redact provider credentials, raw JWTs, invitation tokens, hidden prompt text, raw tool payloads, support-only data, and unauthorized tenant/customer evidence;
- keep deterministic search/detail/timeline/correlation logic outside model behavior;
- emit protected-read and denied-scope traces for every Audit/Trace read.

Primary source paths:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AuditTraceService.java`
- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AuditTraceRepository.java`
- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/SubstituteAuditTraceRepository.java`
- optional adapter `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AkkaAuditTraceRepository.java` if component-client access to trace views/log entities is needed in the same task
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamLogRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeTraceView.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/AgentRuntimeTrace.java`
- tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/`

### Slice 2 — Frontend runtime-aligned Audit/Trace surfaces and cross-workstream links

Goal: make the browser render backend Audit/Trace DTOs with polished, workstream-first investigation surfaces and no frontend-authority assumptions.

Frontend responsibilities:

- align `SurfaceEnvelope`/surface data types with backend Audit/Trace DTO fields for dashboard cards, filters, rows, detail/evidence, timelines, failure evidence, investigation actions, redaction, partial results, and non-enumerating hidden evidence;
- preserve `DashboardSurface`, `ListSearchSurface`, `DetailEditSurface`, `AuditTimelineSurface`, `DecisionSurface`, `SystemMessageSurface`, `TraceLinkList`, and `CapabilityActionButton` behavior;
- render trace links from User Admin, Agent Admin, Governance/Policy, workstream messages, denials, provider/tool failures, and worker surfaces through authorized Audit/Trace action ids;
- keep disabled/hidden controls advisory only; backend denials remain authoritative;
- show provider/model/tool/worker failure evidence without secrets or hidden prompts;
- preserve accessible labels, keyboard/focus behavior, responsive density, and partial/redacted state copy.

Primary source paths:

- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/types/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DashboardSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/ListSearchSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DetailEditSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/AuditTimelineSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DecisionSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/stream/TraceLinkList.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/actions/capabilityActionState.ts`
- `templates/ai-first-saas-starter/frontend/src/api/HttpWorkstreamApiClient.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream-audit-trace-vertical.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-actions.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-surfaces.contract.test.mjs`
- root `frontend/` only if touched starter UI files have mirrored root copies by repository convention.

### Slice 3 — AuditTraceAgent governed evidence tool, seeds, and provider fail-closed tests

Goal: make AuditTraceAgent explanations useful while proving the model can only summarize already-authorized deterministic evidence.

Capabilities/tools:

- `audit.trace.explain` for the request/response AuditTraceAgent turn through the governed workstream message path.
- New read-only data tool candidate: `auditTraceEvidence.read`, capability `audit.trace.search` or narrower `audit.trace.evidence.read` if introduced by the implementation task.
- Existing loader tools: `readSkill(skillId)` and `readReferenceDoc(referenceId)`.

Model-backed responsibilities:

- explain scoped trace evidence, denials, provider/tool/model failures, behavior changes, worker/task failures, correlation timelines, and safe next investigative steps;
- use concrete `WorkstreamRuntimeAgent` through `AgentRuntimeService`/`AgentRuntimeToolResolver` and configured provider boundaries;
- call `auditTraceEvidence.read` for live scoped evidence rather than relying on hidden prompt context;
- fail closed as typed `system_message` with trace ids when provider/model/tool-boundary configuration is absent;
- never reveal hidden prompt text, provider secrets, raw JWTs, invitation tokens, raw tool payloads, or unauthorized tenant/customer data;
- never claim to ingest traces, grant authority, override redaction, mutate records, start workers, export evidence, or bypass deterministic authorization.

Deterministic responsibilities:

- implement `AuditTraceEvidenceTools` as a request-scoped read-only facade over `AuditTraceService`;
- register the tool in `ToolRegistry` with a stable tool id and `DATA_LOOKUP` category;
- grant the tool in `tool-boundary-audit-trace` seed records only with read operation and full work trace semantics;
- enforce `AuthContext`, selected tenant/customer, active AuditTraceAgent definition, tool boundary, capability, and redaction before returning evidence;
- emit tool invocation/denial traces linked to the same AgentWorkTrace/correlation id;
- update Audit/Trace seed guidance/reference only as scoped procedural guidance, not authority.

Primary source paths:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/ToolRegistry.java`
- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AuditTraceEvidenceTools.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeToolResolver.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java`
- `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/audit-trace-system.md`
- `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/audit-trace-starter-guidance.md`
- `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/audit-trace-starter-scope-reference.md`
- tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java`

### Slice 4 — Audit-summary worker readiness decision

Goal: keep the scheduled audit-summary worker SMB-bounded and fail-closed unless durable task semantics are justified by implemented deterministic trace foundations.

Worker candidate capabilities if implemented later:

- `audit.trace.summaryTask.start`
- `audit.trace.summaryTask.read`
- `audit.trace.summaryTask.cancel`
- `audit.trace.summaryTask.acceptResult`

Decision boundary for this mini-project:

- If deterministic search/detail/timeline and AuditTraceAgent evidence tooling are complete but no actual `AutonomousAgent` task runtime is selected, expose only typed blocked/provider-runtime surfaces and a follow-up queue note.
- If implementation confirms a reusable internal worker seam from User Admin access review can be reused safely, add a bounded task only for durable task records and provider-blocked lifecycle; do not claim model-backed summary completion without concrete provider-backed execution.
- In all cases, the worker may summarize authorized evidence and recommend next steps only. It must not own trace ingestion, authorization, tenant filtering, redaction, retention, export, or cross-workstream mutation.

Primary source paths:

- likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AuditTraceSummaryTaskService.java` if a deterministic task record is added
- likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/AuditTraceSummaryTask.java` if a task record is added
- existing worker pattern files from User Admin access review:
  - `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserAdminAccessReviewService.java`
  - `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/AccessReviewTask.java`
  - `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/UserAdminAccessReviewWorker.java`
- frontend `workflow-status` renderer and Audit/Trace fixture blocked task copy.

## Target validation commands for implementation tasks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=WorkstreamServiceTest,AdminEndpointIntegrationTest,AgentRuntimeTraceEntityTest,AgentRuntimeTraceViewTest,AgentRuntimeTraceSinkTest
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-audit-trace-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/api.contract.test.mjs
rg -n "Audit/Trace|AuditTraceAgent|audit\.trace|auditTraceEvidence\.read|trace dashboard|trace search|timeline|correlation|evidence|redacted|provider|tool|model|worker|system_message|AgentWorkTrace|PromptAssemblyTrace|no secret|tenant" templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

Before the mini-project is complete, run broad validation or record a concrete blocker:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

## Appended implementation tasks

- `TASK-FCSMB-AT-01-002`: implement backend deterministic Audit/Trace service, repository facade, DTOs, and tests.
- `TASK-FCSMB-AT-01-003`: implement frontend Audit/Trace runtime-aligned surfaces, cross-workstream trace links, and contract tests.
- `TASK-FCSMB-AT-01-004`: implement AuditTraceAgent evidence tool, seed/tool-boundary updates, provider fail-closed tests, and no-secret checks.
- `TASK-FCSMB-AT-01-005`: decide and implement only the bounded audit-summary worker blocked/readiness path justified by completed deterministic foundations.
- `TASK-FCSMB-AT-01-006`: run integrated Audit/Trace validation and close or append blockers.

These tasks keep Audit/Trace in SMB investigation scope and preserve deterministic ownership of authorization, tenant/customer filtering, redaction, correlation, projection, and trace-link routing.

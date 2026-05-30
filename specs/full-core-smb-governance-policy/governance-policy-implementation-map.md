# Governance/Policy Implementation Map

## Discovery commands used

```bash
find templates/ai-first-saas-starter -path '*/node_modules' -prune -o -type f -print | sort | rg -n "(Governance|governance|Policy|policy|proposal|Proposal|simulate|Simulation|approval|Decision|decision|AgentWorkTrace|PromptAssemblyTrace|ToolPermissionBoundary|WorkstreamService|frontend|surface|Surface|test|api|worker|AutonomousAgent)"
rg -n "Governance/Policy|GovernancePolicyAgent|governance\.policy|governance-policy|policy dashboard|proposal|simulate|approve|reject|activate|rollback|exception|decision|AgentWorkTrace|PromptAssemblyTrace|ToolPermissionBoundary|provider|system_message|tenant|no direct mutation|blocked_provider_or_runtime" templates/ai-first-saas-starter --glob '!**/node_modules/**'
rg -n "GOVERNANCE|governance|policy|proposal|simulate|decision|agent-governance-policy|GovernancePolicy" templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}} templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}} templates/ai-first-saas-starter/frontend/src --glob '!**/node_modules/**'
rg -n "governancePolicy|govpol|governance-policy|Governance/Policy|action-govpol|action-governance-policy|policy inventory|policy simulation|blocked_provider_or_runtime" templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts templates/ai-first-saas-starter/frontend/src/api/FixtureWorkstreamApiClient.ts templates/ai-first-saas-starter/frontend/src/api/HttpWorkstreamApiClient.ts templates/ai-first-saas-starter/frontend/src/workstream/types/surfaces.ts templates/ai-first-saas-starter/frontend/src/workstream/surfaces/GovernanceDiffSurface.tsx templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DecisionSurface.tsx templates/ai-first-saas-starter/frontend/src/workstream-actions.contract.test.mjs templates/ai-first-saas-starter/frontend/src/workstream-surfaces.contract.test.mjs
```

## Current source state

The starter already contains a Governance/Policy v0 surface and action vocabulary, but most runtime behavior is still shaped directly in `WorkstreamService` and is not yet a focused deterministic policy/proposal subsystem.

Implemented foundations:

- `WorkstreamService` defines Governance/Policy capability constants for read, simulate, propose, approve, activate, rollback, and analysis start.
- `WorkstreamService` handles dashboard, inventory, read, draft proposal, submit proposal, simulate, decide, activate-blocked, rollback-blocked, and impact-analysis-blocked action paths.
- Governance/Policy structured surfaces exist in backend helper methods: dashboard, inventory, detail, proposal, simulation, decision, activation blocked, rollback blocked, and impact-analysis blocked.
- Governance/Policy seeded managed-agent records exist through `AgentBehaviorSeedLoader` and seed resources:
  - `agent-governance-policy`
  - `prompt-governance-policy-system`
  - `skill-governance-policy-starter-guidance`
  - `ref-governance-policy-starter-scope`
  - `manifest-governance-policy`
  - `reference-manifest-governance-policy`
  - `tool-boundary-governance-policy`
- `AgentRuntimeService` maps GovernancePolicyAgent request/response invocation to `governance.policy.read` and preserves prompt assembly, model binding, loader tools, provider fail-closed behavior, and behavior-change proposal traces.
- Existing Agent Admin deterministic behavior-change lifecycle can provide policy evidence for behavior-authority changes, but Governance/Policy should not re-own Agent Admin artifact mutation.
- Audit/Trace now has a deterministic `AuditTraceService` and `AuditTraceEvidenceTools` pattern that Governance/Policy can mirror for scoped evidence tools.
- Frontend fixtures and renderers already include rich `action-govpol-*` fixture actions and `governancePolicy*Surface` fixtures, while backend runtime action ids are `action-governance-policy-*` plus legacy `action-simulate-policy`/`action-commit-policy`.
- `workstream-governance-policy-vertical.contract.test.mjs` proves fixture-level Governance/Policy vocabulary, not full runtime alignment.

Material gaps for SMB full-core:

1. Governance/Policy reads and commands are browser-shaped inside `WorkstreamService`; there is no focused `GovernancePolicyService`/repository boundary for authorization, policy inventory, proposal lifecycle, simulation, decisions, activation, rollback, redaction, idempotency, and trace emission.
2. `dynamicSurface` does not independently expose Governance/Policy surfaces by id. They are reachable through action result routing but not through the generic `/api/workstream/surfaces/{surfaceId}` path.
3. Proposal state is not durable or domain-specific. `governancePolicyDraftProposal` reuses `AgentRuntimeService.proposeBehaviorChange` against a prompt target, while submit/decision/activation/rollback surfaces fabricate state from correlation/idempotency inputs instead of reading a governed policy proposal record.
4. Deterministic simulation is a static browser-safe surface. It does not inspect scoped proposal state, active policy inventory, Agent Admin behavior-change evidence, tool boundaries, User Admin authority changes, or Audit/Trace evidence through a repository facade.
5. Approve/reject/activate/rollback do not yet enforce proposal status transitions, version checks, duplicate/no-op semantics, rollback metadata, or rejection/activation blockers through a deterministic service.
6. GovernancePolicyAgent can use `readSkill` and `readReferenceDoc`, but no read-only `governancePolicyEvidence.read` tool exists. The model cannot load scoped dashboard/proposal/simulation/decision evidence through a named governed tool boundary.
7. `tool-boundary-governance-policy` appears seeded for loader tools only; it should grant a read-only Governance/Policy evidence facade only after deterministic service boundaries exist.
8. Frontend fixture ids (`action-govpol-*`, `surface-governance-policy-analysis-task`) and backend runtime ids (`action-governance-policy-*`, `surface-governance-policy-impact-analysis`) diverge. UI tests should be updated to prove runtime ids and fixture aliases preserve the same capability semantics.
9. Policy-impact analysis worker remains correctly blocked/provider-runtime in v0. It should not become runnable until deterministic proposal/simulation/decision state exists and a durable worker task lifecycle is intentionally introduced.

## Vertical slice sequence

### Slice 1 — Deterministic Governance/Policy service, inventory, and proposal lifecycle

Goal: extract backend-authoritative Governance/Policy behavior out of ad hoc `WorkstreamService` helpers into focused deterministic service/state boundaries.

Capabilities:

- `governance.policy.dashboard.read`
- `governance.policy.list`
- `governance.policy.read`
- `governance.policy.proposal.draft`
- `governance.policy.proposal.submit`
- `governance.policy.proposal.read`

Implementation may preserve existing shorter capability ids as aliases where already present, but queue/tasks should prefer the full-core capability ids in new service/test names.

Deterministic responsibilities:

- authorize selected `AuthContext`, active membership, non-disabled actor, tenant/customer scope, and exact Governance/Policy capability;
- return dashboard, inventory, policy detail, proposal list/detail, and safe first-run/empty states from deterministic records;
- create or return proposal records idempotently with explicit `draft`, `in_review`, `approved`, `rejected`, `activated`, `rolled_back`, and `blocked` lifecycle states;
- classify authority expansion, affected capabilities/artifacts, required approval, evidence requirements, and rollback requirements without using model output as authority;
- emit protected-read, proposal-draft, submit, denial, validation, and no-op traces;
- keep User Admin, Agent Admin, and Audit/Trace state as evidence links, not ownership transfers.

Primary source paths:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/GovernancePolicyService.java`
- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/GovernancePolicyRepository.java`
- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/InMemoryGovernancePolicyRepository.java`
- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/GovernancePolicyProposal.java`
- tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/`

### Slice 2 — Simulation, decision, activation, and rollback lifecycle

Goal: make simulation advisory and deterministic, while approval/rejection/activation/rollback become real lifecycle commands with idempotent/no-op behavior and audit traces.

Capabilities:

- `governance.policy.simulate`
- `governance.policy.approve`
- `governance.policy.reject`
- `governance.policy.activate`
- `governance.policy.rollback`

Deterministic responsibilities:

- simulate only scoped proposals and selected evidence; output expected allows/denials, affected capabilities/artifacts, warnings, confidence/source limitations, and trace links;
- record human approve/reject decision cards with actor, authority basis, rationale, outcome, correlation id, and evidence refs;
- activate only approved proposals with version and rollback metadata checks;
- preserve no-op/idempotent semantics for duplicate submit/decision/activation/rollback requests;
- fail closed for missing proposal, wrong tenant/customer, stale proposal, missing rollback reference, unsupported activation target, missing approval, or disabled actor;
- never let GovernancePolicyAgent, prompt text, skill text, simulation output, or frontend affordances commit activation.

Primary source paths:

- same backend service/repository/domain files as Slice 1
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java` only as evidence source/related behavior-change lifecycle, not as Governance/Policy proposal owner
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java`
- new likely `GovernancePolicyServiceTest.java`

### Slice 3 — Frontend runtime-aligned Governance/Policy surfaces/actions

Goal: align browser fixtures, runtime DTO assumptions, action ids, and tests around backend-authoritative Governance/Policy surfaces.

Frontend responsibilities:

- align fixture `action-govpol-*` ids with backend `action-governance-policy-*` ids or support explicit aliases while tests prove the mapping;
- render dashboard, inventory, proposal, simulation, decision, blocked activation, blocked rollback, and impact-analysis blocked surfaces with ready/empty/forbidden/validation/no-op/approval-needed/blocked_provider_or_runtime states;
- show deterministic-vs-model-backed participation clearly: deterministic policy/proposal/simulation/decision services own state transitions, GovernancePolicyAgent only explains/drafts guidance, impact worker stays blocked unless implemented;
- keep disabled/hidden controls advisory only; backend denials remain authoritative;
- keep trace links and redaction markers close to consequential actions and decision cards;
- preserve visual, accessible, responsive `atlas-ops-supervisory-console` behavior and no secret/hidden prompt exposure.

Primary source paths:

- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/types/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/GovernanceDiffSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DecisionSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/WorkflowStatusSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/SystemMessageSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/actions/capabilityActionState.ts`
- `templates/ai-first-saas-starter/frontend/src/api/FixtureWorkstreamApiClient.ts`
- `templates/ai-first-saas-starter/frontend/src/api/HttpWorkstreamApiClient.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream-governance-policy-vertical.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-actions.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-surfaces.contract.test.mjs`

### Slice 4 — GovernancePolicyAgent evidence tool, seed guidance, and provider fail-closed tests

Goal: make GovernancePolicyAgent useful through governed request/response Akka Agent runtime without granting mutation, approval, activation, rollback, or simulation authority.

Capabilities/tools:

- `governance.policy.ask_agent` or the existing workstream message capability mapping to `governance.policy.read`
- existing loader tools: `readSkill(skillId)` and `readReferenceDoc(referenceId)`
- new read-only evidence tool candidate: `governancePolicyEvidence.read` with capability `governance.policy.read` or narrower `governance.policy.evidence.read`

Model-backed responsibilities:

- explain active policies, proposal states, simulation evidence, approval requirements, denials, activation blockers, rollback blockers, and trace links;
- draft rationale or proposal copy as inert input only;
- call scoped evidence and loader tools through the governed runtime/tool-boundary path;
- fail closed with typed `system_message` and trace ids when provider/model/tool-boundary configuration is absent;
- never claim that it approved, activated, rolled back, changed policy, changed tool boundaries, granted roles, changed model refs, or bypassed tenant scope.

Deterministic responsibilities:

- implement evidence tool facade over `GovernancePolicyService`;
- register the tool in `ToolRegistry` and grant it in `tool-boundary-governance-policy` only as read-only/data lookup;
- enforce `AuthContext`, tenant/customer scope, active GovernancePolicyAgent definition, capability, `ToolPermissionBoundary`, redaction, and non-enumerating denials before returning evidence;
- emit tool invocation/denial traces linked to `AgentWorkTrace`/correlation id;
- update seed prompt/skill/reference copy only to instruct safe evidence use and no-direct-mutation boundaries.

Primary source paths:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/ToolRegistry.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeToolResolver.java`
- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/GovernancePolicyEvidenceTools.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java`
- seed resources under `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/`
- tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/`

### Slice 5 — Policy-impact analysis worker readiness decision

Goal: keep worker scope honest after deterministic policy/proposal/simulation foundations land.

Decision boundary:

- If deterministic proposal/simulation/decision lifecycle and GovernancePolicyAgent evidence tooling are complete but no real AutonomousAgent task runtime is selected, expose only typed blocked/provider-runtime surfaces and a follow-up queue note.
- If implementation confirms the User Admin access-review worker seam can be generalized safely, add only a bounded task-record/provider-blocked lifecycle for `governance.policy.analysis.start/read/cancel/acceptResult`; do not claim model-backed analysis completion without concrete provider-backed execution.
- Worker output may recommend and summarize evidence only. It must not approve, activate, rollback, mutate policies, mutate users, mutate agent behavior, alter provider config, or bypass deterministic simulation/authorization.

## Target validation commands for implementation tasks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=GovernancePolicyServiceTest,WorkstreamServiceTest,AdminEndpointIntegrationTest
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-governance-policy-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/api.contract.test.mjs
rg -n "Governance/Policy|GovernancePolicyAgent|governance\.policy|governancePolicyEvidence\.read|policy dashboard|proposal|simulate|approve|reject|activate|rollback|exception|decision|AgentWorkTrace|PromptAssemblyTrace|ToolPermissionBoundary|provider|system_message|tenant|no direct mutation|blocked_provider_or_runtime" templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

Before the mini-project is complete, run broad validation or record a concrete blocker:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

## Appended implementation tasks

- `TASK-FCSMB-GP-01-002`: implement deterministic backend Governance/Policy service, repository, inventory/proposal lifecycle, and tests.
- `TASK-FCSMB-GP-01-003`: implement deterministic simulation, decision, activation/rollback lifecycle, and tests.
- `TASK-FCSMB-GP-01-004`: implement frontend Governance/Policy runtime-aligned surfaces/actions/fixtures/tests.
- `TASK-FCSMB-GP-01-005`: implement GovernancePolicyAgent evidence tool, seed/tool-boundary updates, provider fail-closed tests, and no-secret checks.
- `TASK-FCSMB-GP-01-006`: decide and implement only the bounded policy-impact analysis blocked/readiness path justified by completed deterministic foundations.
- `TASK-FCSMB-GP-01-007`: run integrated Governance/Policy validation and close or append blockers.

These tasks keep Governance/Policy in SMB operator scope, preserve deterministic ownership of authorization, tenant/customer filtering, proposal lifecycle, simulation, approval, activation/rollback, redaction, idempotency, and traces, and reserve model-backed behavior for governed explanation/drafting or explicitly justified durable worker analysis.

# Agent Admin Implementation Map

## Discovery commands used

```bash
find templates/ai-first-saas-starter -path '*/node_modules' -prune -o -type f -print | sort | rg -n "(AgentAdmin|agent-admin|agent_admin|AgentDefinition|AgentBehavior|AgentRuntime|WorkstreamRuntimeAgent|ToolPermissionBoundary|AgentSkillManifest|AgentReferenceManifest|readSkill|readReferenceDoc|PromptAssemblyTrace|SkillLoadTrace|ReferenceLoadTrace|AgentWorkTrace|provider|system_message|seed|frontend|surface|Surface|WorkstreamService|test)"
rg -n "AgentAdmin|agent-admin|agent_admin|AgentDefinition|ToolPermissionBoundary|AgentSkillManifest|AgentReferenceManifest|model ref|behavior change|proposal|activate|rollback|seed|provider|system_message|AgentWorkTrace|PromptAssemblyTrace|no direct mutation|readSkill|readReferenceDoc|Manifest|Boundary|WorkstreamRuntimeAgent|DefaultWorkstreamAgentRuntimeInvoker|FailClosedWorkstreamAgentRuntimeInvoker|blocked_provider_or_runtime" templates/ai-first-saas-starter --glob '!**/node_modules/**'
rg -n "agent-admin|Agent Admin|surface-agent|action-(display-agent|open-agent|propose-prompt|test-agent|approve-skill|simulate-tool|manage-model)|system_message|blocked_provider_or_runtime|governance-diff|workflow-status|tool-boundary|readSkill|readReferenceDoc" templates/ai-first-saas-starter/frontend/src --glob '!**/node_modules/**'
```

## Current source state

The starter already has a v0 Agent Admin vertical, but several pieces are demo-scoped or incomplete for SMB full-core.

Implemented foundations:

- `WorkstreamService` exposes Agent Admin catalog/detail/diff/model/test/trace surfaces through structured surface ids and action ids.
- `AgentBehaviorSeedLoader` seeds `agent-agent-admin`, `prompt-agent-admin-system`, `manifest-agent-admin`, `reference-manifest-agent-admin`, `tool-boundary-agent-admin`, starter guidance skill/reference, shared model config, and shared model policy.
- `AgentRuntimeService` resolves active `AgentDefinition`, prompt, skill/reference manifests, `ToolPermissionBoundary`, model binding, prompt assembly, `readSkill`, `readReferenceDoc`, model/provider fail-closed path, and in-memory behavior-change proposal records.
- `WorkstreamRuntimeAgent` is the concrete request/response Akka `Agent` runtime invoked through `DefaultWorkstreamAgentRuntimeInvoker` in production wiring.
- User Admin access-review work introduced an example durable task lifecycle pattern with provider-blocked semantics and no-direct-mutation surfaces.
- Frontend fixtures, renderers, actions, and tests already know Agent Admin v0 surfaces and can render `dashboard`, `list-search`, `detail-edit`, `governance-diff`, `decision`, `workflow-status`, `audit-timeline`, `system_message`, and blocked provider/runtime states.

Material gaps for SMB full-core:

1. Agent Admin reads are currently browser-shaped directly inside `WorkstreamService`; there is no focused deterministic `AgentAdminService`/read facade with scoped catalog, artifact detail, seed status, provider readiness, redacted previews, trace links, and tenant isolation tests.
2. `dynamicSurface` omits Agent Admin surface ids, so some surfaces are only action-return surfaces rather than independently retrievable structured surfaces.
3. Capability visibility for `agent_admin.*` is not explicitly handled in `isActionCapabilityVisible`; it relies on the selected context already carrying exact capability ids, but should be made explicit and tested.
4. Behavior-change lifecycle is partial: draft and unsafe-boundary denial exist; submit/review/reject/activate/cancel/rollback are not exposed as deterministic Agent Admin commands. `approveProposal` activates immediately and uses `agent.behavior.manage`, not SMB-specific review/activation capability ids.
5. Proposal storage is in-memory inside `AgentRuntimeService`, with no durable audited proposal repository/entity and no rollback metadata.
6. `BehaviorChangeProposal.TargetArtifact` supports `PROMPT`, `SKILL`, and `TOOL_BOUNDARY`; references, manifests, and model refs are not first-class proposal targets.
7. Agent Admin seed material contains only one starter guidance skill/reference. Full-core guidance needs Agent Admin-specific prompt/skill/reference content for definition review, prompt diff review, skill/reference manifest review, tool-boundary review, provider/model readiness, seed/default material, and no-direct-mutation lifecycle boundaries.
8. `tool-boundary-agent-admin` grants only `readSkill` and `readReferenceDoc`; there is no model-facing read-only Agent Admin evidence tool. The AgentAdminAgent can load seed guidance but cannot ask for scoped live catalog/proposal/readiness evidence through a governed tool.
9. Frontend Agent Admin fixtures include strong demo surfaces, but runtime DTOs are thinner and do not yet cover full-core catalog dashboard cards, redacted artifact previews, provider readiness, proposal lifecycle, seed material, or behavior-review worker states.
10. Prompt-risk/behavior-review worker is not yet justified as runnable until deterministic behavior-change lifecycle and proposal/read surfaces exist. It should initially fail closed or be queued after lifecycle implementation.

## Vertical slice sequence

### Slice 1 — Deterministic catalog, artifact reads, seed/provider readiness

Goal: turn the current Agent Admin v0 surfaces into backend-authoritative structured reads for seeded managed-agent behavior.

Capabilities:

- `agent_admin.list_definitions`
- `agent_admin.get_definition`
- `agent_admin.get_prompt_version`
- `agent_admin.get_skill_version`
- `agent_admin.get_reference_version`
- `agent_admin.get_manifest`
- `agent_admin.get_model_ref`
- `agent_admin.get_tool_boundary`
- `agent_admin.list_seed_material`
- `agent_admin.reseed_missing_defaults` only if implementation can keep it idempotent and internal; otherwise expose blocked/review-needed state.

Deterministic responsibilities:

- authorize selected `AuthContext`, active membership, non-disabled actor, tenant/customer scope, and exact Agent Admin read capability;
- read only tenant-scoped governed records from `AgentBehaviorRepository`;
- shape browser-safe DTOs with redacted prompt previews, compact manifests, model aliases only, provider readiness, seed provenance, checksums, lifecycle status, and trace ids;
- emit protected-read traces through existing trace sink/resolver patterns;
- preserve secret boundary: no provider credential, raw hidden prompt text beyond allowed preview, raw JWT, cross-tenant data, or support-only details.

Primary source paths:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- new likely `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentAdminService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorRepository.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java`
- domain records under `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/`
- tests under `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/` and `.../application/security/WorkstreamServiceTest.java`

### Slice 2 — Deterministic behavior-change lifecycle

Goal: make prompt/skill/reference/manifest/model/tool-boundary changes inert until deterministic review/activation commands authorize them.

Capabilities:

- `agent_admin.draft_behavior_change`
- `agent_admin.submit_behavior_change_for_review`
- `agent_admin.approve_behavior_change`
- `agent_admin.reject_behavior_change`
- `agent_admin.activate_behavior_change`
- `agent_admin.cancel_behavior_change`
- `agent_admin.rollback_behavior_change`
- `agent_admin.compare_versions`
- `agent_admin.simulate_tool_boundary`

Deterministic responsibilities:

- validate target artifact, current active version, proposed version, idempotency key, rationale, redaction, and authority-impact classification;
- detect prompt/skill/reference text that claims authority expansion;
- require human approval for authority expansion, model changes, reference-access expansion, manifest expansion, and side-effecting tool grants;
- keep approval and activation separate;
- activate only approved proposals through backend command semantics;
- preserve active behavior on reject/cancel/failed activation;
- record rollback metadata before activation; fail closed when rollback metadata is missing;
- emit proposal/review/activation/rollback traces.

Primary source paths:

- `AgentRuntimeService.java` for existing proposal helpers, likely split or wrap with a deterministic `AgentAdminBehaviorChangeService`.
- `BehaviorChangeProposal.java` and related domain records.
- `AgentBehaviorRepository` implementations: in-memory and Akka/durable repository/entity files.
- `WorkstreamService.java` Agent Admin actions and surfaces.
- tests: `AgentRuntimeServiceTest`, `DurableAgentBehaviorRepositoryStateTest`, `ManifestBoundaryEntityTest`, `WorkstreamServiceTest`, plus new focused service tests.

### Slice 3 — AgentAdminAgent governed request/response guidance

Goal: make AgentAdminAgent useful without granting mutation authority.

Capabilities/tools:

- `agent_admin.submit_turn`
- governed loader tools `readSkill(skillId)` and `readReferenceDoc(referenceId)`
- new read-only facade tool candidate `agentAdminEvidence.read` with capability `agent_admin.list_definitions` or narrower `agent_admin.read_evidence`

Model-backed responsibilities:

- explain active behavior configuration, provider readiness, seed status, prompt/skill/reference/tool-boundary meaning, denials, and proposal next steps;
- draft safe proposal rationale/content for deterministic lifecycle review;
- never claim it activated, rolled back, approved, reseeded, changed model config, or modified tool boundaries;
- use concrete `WorkstreamRuntimeAgent` through governed runtime assembly and fail closed as typed `system_message` when provider/runtime config is absent.

Deterministic responsibilities:

- register only read-only evidence/loading tools granted by `ToolPermissionBoundary`;
- enforce AuthContext/tenant/capability/redaction before evidence tool returns data;
- emit prompt assembly, skill/reference load, tool access/denial, model call, provider failure, and AgentWorkTrace records;
- keep proposal creation/review/activation outside direct model authority.

Primary source paths:

- `ToolRegistry.java`
- `AgentRuntimeToolResolver.java`
- `AgentRuntimeLoaderTools.java`
- `AgentRuntimeService.java`
- `WorkstreamRuntimeAgent.java`
- new likely `AgentAdminEvidenceTools.java`
- seed resources under `backend/src/main/resources/agent-behavior-seeds/starter-v1/`
- tests: `AgentRuntimeToolResolverTest`, `AgentRuntimeServiceTest`, `WorkstreamRuntimeAgentTest`, seed loader tests, and frontend composer/system-message tests.

### Slice 4 — Frontend runtime-aligned Agent Admin surfaces

Goal: replace demo-only assumptions with runtime DTO coverage and polished workstream-first UI.

Frontend responsibilities:

- render catalog dashboard/detail/artifact/proposal/provider-blocked/seed/tool-boundary/trace surfaces with `system_message` non-happy states;
- expose actions with backend capability ids and idempotency requirements;
- make prompt/reference previews visibly redacted and trace-linked;
- distinguish deterministic lifecycle actions from AgentAdminAgent guidance;
- keep fixture surfaces synchronized with runtime DTO shape;
- no route/page-first CRUD prompt editor.

Primary source paths:

- `templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/api/FixtureWorkstreamApiClient.ts`
- `templates/ai-first-saas-starter/frontend/src/api/HttpWorkstreamApiClient.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/types/surfaces.ts`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/*`
- `templates/ai-first-saas-starter/frontend/src/workstream-agent-admin-vertical.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-actions.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/workstream-surfaces.contract.test.mjs`
- root `frontend/` only if touched starter UI files have mirrored root copies by repository convention.

### Slice 5 — Prompt-risk / behavior-review worker candidate

Defer real worker implementation until Slices 1 and 2 are complete. First acceptable state is an explicit blocked/provider/runtime surface and task brief that proves no model-less successful behavior-review result is claimed.

Worker capabilities when justified:

- `agent_admin.start_behavior_review_task`
- `agent_admin.read_behavior_review_task`
- `agent_admin.cancel_behavior_review_task`
- `agent_admin.accept_behavior_review_result`
- `agent_admin.reject_behavior_review_result`

Worker may recommend or attach review evidence only. It must not approve, activate, rollback, reseed, change model refs, or mutate tool boundaries directly.

## Appended implementation tasks

- `TASK-FCSMB-AA-01-002`: backend deterministic catalog/artifact reads, seed/provider readiness, source-boundary fixes, and tests.
- `TASK-FCSMB-AA-01-003`: backend deterministic behavior-change lifecycle and tests.
- `TASK-FCSMB-AA-01-004`: AgentAdminAgent evidence tool, seed guidance, tool-boundary/runtime updates, and tests.
- `TASK-FCSMB-AA-01-005`: frontend Agent Admin runtime-aligned surfaces/actions/fixtures/tests.
- `TASK-FCSMB-AA-01-006`: integrated Agent Admin validation and worker-readiness decision.

## Target validation commands for implementation tasks

```bash
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentBehaviorSeedLoaderTest,AgentRuntimeServiceTest,AgentRuntimeToolResolverTest,WorkstreamRuntimeAgentTest,WorkstreamServiceTest
cd templates/ai-first-saas-starter/backend && mvn test -Dtest=AgentDefinitionEntityTest,AgentDefinitionViewIntegrationTest,ManifestBoundaryEntityTest,ManifestBoundaryViewTest,DurableAgentBehaviorRepositoryStateTest
cd templates/ai-first-saas-starter/frontend && npm test -- --runTestsByPath src/workstream-agent-admin-vertical.contract.test.mjs src/workstream-actions.contract.test.mjs src/workstream-surfaces.contract.test.mjs src/workstream-composer-message-api.contract.test.mjs src/api.contract.test.mjs
rg -n "Agent Admin|AgentAdminAgent|AgentDefinition|ToolPermissionBoundary|AgentSkillManifest|AgentReferenceManifest|model ref|behavior change|proposal|activate|rollback|seed|provider|system_message|AgentWorkTrace|PromptAssemblyTrace|no direct mutation|agentAdminEvidence\.read" templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

Run `tools/validate-ai-first-saas-starter-fullstack.sh` after backend and frontend Agent Admin runtime behavior both land, or record a concrete blocker in the queue.

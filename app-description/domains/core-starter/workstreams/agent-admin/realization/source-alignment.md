# Agent Admin source alignment

Lifecycle: ../lifecycle.md
Last reviewed: 2026-06-29
Alignment state: stale-description-changed

The 2026-06-29 app-description refresh changed Agent Admin current intent after the prior AABP-05-003 implementation evidence. Treat this file as a stale-description-changed source map and graph proof for docs-only refresh work, not as runtime-readiness evidence.

Prior AABP-05-003 evidence still indicates the older Agent Admin behavior-profile slice reached `api-smoked/frontend-rendered` for protected WorkstreamEndpoint/API routing, SaaS Owner/Admin authorization, proposal-first save/activation, stale/high-risk denial, restore proposals, skill/reference lifecycle, behavior-profile versions/assignments, runtime active-profile/loader/tool-boundary traces, model-backed editing-agent test path, and frontend current surface contracts. The refreshed graph now additionally requires explicit attention categories, governance/test-console surfaces, canonical governed-tool ids, model-policy/tool-boundary assignment semantics, `ReferenceLoadTrace`, provider/config blocker behavior, explicit chat confirmation, idempotency/no-op/partial-failure result surfaces, and runtime-validation references.

## Alignment entries

| Entry id | App-description files | Implementation files | Test / validation files | Last aligned evidence | Notes |
| --- | --- | --- | --- | --- | --- |
| `agent-admin.workstream-slice` | `../workstream.md`, `../access.md`, `../behavior.md`, `../agents/**`, `../workers/**`, `../surfaces/**`, `../tools/**`, `../policies/**`, `../traces/**`, `../tests/**`, `akka-components.md`, `api-contracts.md`, `frontend-routes.md`, legacy capability artifact `../../../capabilities/agent-doc-administration.md` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `src/main/java/ai/first/application/coreapp/agentadmin/**`, `src/main/java/ai/first/domain/coreapp/agentadmin/**`, `src/main/java/ai/first/application/foundation/agent/**`, `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/coreapp/workstream/**`, `frontend/src/workstream/**`, `frontend/src/api/**`, `frontend/src/design-system/**` | `src/test/java/ai/first/application/coreapp/agentadmin/**`, `src/test/java/ai/first/application/coreapp/workstream/AgentAdminBrowserWorkstreamSmokeTest.java`, `src/test/java/ai/first/application/foundation/agent/**`, `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`, `frontend/src/workstream-surfaces.contract.test.mjs`, `frontend/src/workstream-actions.contract.test.mjs`, future runtime-validation scenarios listed in `../tests/coverage.md` | 2026-06-27 `AABP-05-003` terminal verification for older graph; 2026-06-29 docs-only refresh ran `git diff --check` only. | Current app-description now links SaaS-admin human, Agent Admin functional agent, editing agent, and runtime system worker through `surface_action`, confirmed `human_chat_tool_plan`, bounded `agent_tool_call`, API, and internal runtime loader adapters to canonical AgentDefinition/PromptDocument/SkillDocument/ReferenceDocument/manifest/model-policy/tool-boundary/test-console/trace governed tools, managed-agent governance capability scope, realization maps, tests, runtime-validation scenarios, and trace obligations. Implementation must be re-reviewed before claiming partial/runtime alignment for the refreshed graph. |

## Unmapped current-intent files

- None recorded in this workstream directory after the 2026-06-29 refresh. This means no known Agent Admin app-description file was intentionally excluded, not that implementation is aligned.

## Unmapped implementation files

- None recorded during this docs-only refresh. Shared foundation/workstream files may support multiple workstreams and need finer-grained classification in future reviews.

## Alignment notes

- Current state is `stale-description-changed`: the current-intent graph changed without corresponding runtime/API/UI implementation validation.
- Runtime-validation coverage expected by the refreshed graph includes SaaS-admin authorization, catalog/detail/governance surfaces, proposal lifecycle, approval-required authority expansion, provider fail-closed test-console behavior, loader/tool-boundary denials, idempotency/no-op/partial-failure results, provider secret boundaries, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, and `AgentWorkTrace` visibility.
- Do not use this source-alignment file as runtime-readiness evidence. Runtime readiness still requires real local API/UI/agent/runtime-loader validation for the selected scope.

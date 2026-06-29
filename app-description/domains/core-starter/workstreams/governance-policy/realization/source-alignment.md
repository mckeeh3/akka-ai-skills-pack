# Governance Policy source alignment

Lifecycle: ../lifecycle.md
Last reviewed: 2026-06-29
Alignment state: stale-description-changed

This file tracks app-description-to-source alignment candidates only. The Governance/Policy graph was refreshed on 2026-06-29 to cover policy lifecycle, simulations, decision cards, activation, rollback, exceptions, actor adapters, governed tools, traces, and runtime-validation expectations. Treat every mapping below as candidate evidence for a future alignment review, not as proof that implementation matches current intent.

## Alignment entries

| Entry id | App-description files | Implementation files | Test / validation files | Last aligned evidence | Notes |
| --- | --- | --- | --- | --- | --- |
| `governance-policy.lifecycle-slice` | `../workstream.md`, `../access.md`, `../behavior.md`, `../workers/**`, `../agents/**`, `../surfaces/**`, `../tools/**`, `../policies/**`, `../traces/**`, `../tests/**`, `akka-components.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/governance-policy-lifecycle.md` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `src/main/java/ai/first/application/coreapp/governance/**`, `src/main/java/ai/first/domain/coreapp/governance/**`, `src/main/java/ai/first/application/foundation/governance/**`, `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/foundation/workstream/**`, `src/main/java/ai/first/application/coreapp/workstream/**`, `frontend/src/workstream/**`, `frontend/src/api/**`, `frontend/src/design-system/**` | `src/test/java/ai/first/application/coreapp/governance/**`, `src/test/java/ai/first/application/foundation/governance/**`, workflow/API tests for decision cards, activation, rollback, exceptions, tenant isolation, denials, and traces, `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`, `frontend/src/governance-audit-admin-profile.contract.test.mjs`, `frontend/src/workstream-actions.contract.test.mjs` | Description refreshed only; no aligned runtime/API/UI evidence recorded. | Candidate mapping for the refreshed workstream. A focused review must split catalog/detail, draft/simulation, decision/approval, activation, rollback, exception, runtime enforcement, frontend, and trace entries before claiming alignment. |

## Unmapped current-intent files

- None recorded during this refresh. This means no known Governance/Policy app-description file was intentionally excluded, not that all current-intent files are aligned.

## Unmapped implementation files

- None recorded during this refresh. Shared foundation/workstream files may support multiple workstreams and need finer-grained classification in future reviews.

## Alignment notes

- Current state is `stale-description-changed` because this review updated worker artifacts, actor adapters, governed-tool contracts, policy lifecycle semantics, decision-card approval behavior, simulation/exception/rollback surfaces, trace obligations, tests, realization mappings, lifecycle, and runtime-validation references without compiling or validating runtime code.
- The refreshed graph links Governance/Policy workers to execution harnesses and actor adapters (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `api_call`, `workflow_step`, `internal_call`), governed tools (`governance.policy.search`, `read`, `draft`, `simulate`, `submit_for_approval`, `approve`, `activate`, `rollback`, `review_exception`, `read_history`), capability `governance-policy-lifecycle`, realization candidates, tests/runtime-validation expectations, and traces.
- If mapped app-description files are newer than mapped implementation/test files, keep lifecycle implementation alignment as `stale-description-changed` unless a no-code-impact review is recorded.
- If mapped implementation files changed without app-description reconciliation, set lifecycle implementation alignment to `stale-code-changed` or `partially-aligned`.
- Do not use this source-alignment file as runtime-readiness evidence. Runtime readiness still requires automated checks and real local API/UI/agent-path validation for the selected scope.

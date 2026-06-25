# Governance Policy source alignment

Lifecycle: ../lifecycle.md
Last reviewed: 2026-06-25
Alignment state: unknown

This file was added during the initial source-alignment migration. The user explicitly stated that none of the existing workstreams are currently aligned. Treat every mapping below as candidate evidence for a future alignment review, not as proof that implementation matches current intent.

## Alignment entries

| Entry id | App-description files | Implementation files | Test / validation files | Last aligned evidence | Notes |
| --- | --- | --- | --- | --- | --- |
| `governance-policy.workstream-slice` | `../workstream.md`, `../access.md`, `../behavior.md`, `../agents/**`, `../surfaces/**`, `../tools/**`, `../policies/**`, `../traces/**`, `../tests/**`, `akka-components.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/governance-policy-lifecycle.md` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `src/main/java/ai/first/application/coreapp/governance/**`, `src/main/java/ai/first/domain/coreapp/governance/**`, `src/main/java/ai/first/application/foundation/governance/**`, `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/foundation/workstream/**`, `src/main/java/ai/first/application/coreapp/workstream/**`, `frontend/src/workstream/**`, `frontend/src/api/**`, `frontend/src/design-system/**` | `src/test/java/ai/first/application/coreapp/governance/**`, `src/test/java/ai/first/application/foundation/governance/**`, `frontend/src/workstream-governance-policy-vertical.contract.test.mjs`, `frontend/src/governance-audit-admin-profile.contract.test.mjs` | Initial migration only; no aligned evidence recorded. | Broad candidate mapping for the current workstream. A focused review must split this into smaller entries and classify stale-description/stale-code gaps before claiming alignment. |

## Unmapped current-intent files

- None recorded during the initial migration. This means no known app-description file was intentionally excluded, not that all current-intent files are aligned.

## Unmapped implementation files

- None recorded during the initial migration. Shared foundation/workstream files may support multiple workstreams and need finer-grained classification in future reviews.

## Alignment notes

- Current state is `unknown` because no existing workstream is considered aligned.
- If mapped app-description files are newer than mapped implementation/test files, set the lifecycle implementation alignment to `stale-description-changed` unless a no-code-impact review is recorded.
- If mapped implementation files changed without app-description reconciliation, set the lifecycle implementation alignment to `stale-code-changed` or `partially-aligned`.
- Do not use this source-alignment file as runtime-readiness evidence. Runtime readiness still requires automated checks and real local API/UI/agent-path verification for the selected scope.

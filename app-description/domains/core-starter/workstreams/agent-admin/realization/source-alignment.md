# Agent Admin source alignment

Lifecycle: ../lifecycle.md
Last reviewed: 2026-06-27
Alignment state: stale-description-changed

This file was added during the initial source-alignment migration. The user explicitly stated that none of the existing workstreams are currently aligned. A 2026-06-27 compile scout found the current implementation still reflects older direct document-editing/governance-console behavior and does not yet match the updated behavior-profile proposal/review/activation app-description. Treat every mapping below as candidate evidence for a future alignment review, not as proof that implementation matches current intent.

## Alignment entries

| Entry id | App-description files | Implementation files | Test / validation files | Last aligned evidence | Notes |
| --- | --- | --- | --- | --- | --- |
| `agent-admin.workstream-slice` | `../workstream.md`, `../access.md`, `../behavior.md`, `../agents/**`, `../surfaces/**`, `../tools/**`, `../policies/**`, `../traces/**`, `../tests/**`, `akka-components.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/agent-doc-administration.md` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `src/main/java/ai/first/application/coreapp/agentadmin/**`, `src/main/java/ai/first/domain/coreapp/agentadmin/**`, `src/main/java/ai/first/application/foundation/agent/**`, `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/coreapp/workstream/**`, `frontend/src/workstream/**`, `frontend/src/api/**`, `frontend/src/design-system/**` | `src/test/java/ai/first/application/coreapp/agentadmin/**`, `src/test/java/ai/first/application/foundation/agent/**`, `frontend/src/workstream-agent-admin-vertical.contract.test.mjs` | 2026-06-27 compile scout only; no aligned runtime evidence recorded. | Broad candidate mapping for the current workstream. Current known drift: direct save/restore/create/delete mutations collapse the required proposal/review/activation lifecycle; tenant-scoped behavior-profile clone/versioning, skill/tool assignment, model-config reference, profile history, proposal review, and richer runtime trace surfaces are missing or partial; generated agent name/purpose edits and whole-agent lifecycle surfaces are stale. Split into smaller compile tasks before claiming alignment. |

## Unmapped current-intent files

- None recorded during the initial migration. This means no known app-description file was intentionally excluded, not that all current-intent files are aligned.

## Unmapped implementation files

- None recorded during the initial migration. Shared foundation/workstream files may support multiple workstreams and need finer-grained classification in future reviews.

## Alignment notes

- Current state is `stale-description-changed` because the app-description now prioritizes behavior-profile proposal/review/activation, while implementation and tests still contain older direct document-save, create/delete, restore, and whole-agent lifecycle assumptions.
- If mapped app-description files are newer than mapped implementation/test files, set the lifecycle implementation alignment to `stale-description-changed` unless a no-code-impact review is recorded.
- If mapped implementation files changed without app-description reconciliation, set the lifecycle implementation alignment to `stale-code-changed` or `partially-aligned`.
- Do not use this source-alignment file as runtime-readiness evidence. Runtime readiness still requires automated checks and real local API/UI/agent-path verification for the selected scope.

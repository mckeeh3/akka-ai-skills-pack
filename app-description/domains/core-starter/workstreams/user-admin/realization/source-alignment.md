# User Admin source alignment

Lifecycle: ../lifecycle.md
Last reviewed: 2026-06-29
Alignment state: stale-description-changed

This file was added during the initial source-alignment migration. TASK-ADR-02-002 refreshed the User Admin current-intent graph without changing runtime code, so mapped implementation evidence is candidate evidence only and must be treated as stale until a focused alignment/runtime-validation review proves current source, frontend, API, test, and manual-runtime behavior matches the updated description.

## Alignment entries

| Entry id | App-description files | Implementation files | Test / validation files | Last aligned evidence | Notes |
| --- | --- | --- | --- | --- | --- |
| `user-admin.workstream-slice` | `../workstream.md`, `../access.md`, `../behavior.md`, `../workers/**`, `../agents/**`, `../surfaces/**`, `../tools/**`, `../policies/**`, `../traces/**`, `../tests/**`, `akka-components.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/user-and-access-administration.md` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `src/main/java/ai/first/api/foundation/invitation/**`, `src/main/java/ai/first/application/coreapp/useradmin/**`, `src/main/java/ai/first/domain/coreapp/useradmin/**`, `src/main/java/ai/first/application/foundation/identity/**`, `src/main/java/ai/first/application/foundation/invitation/**`, `src/main/java/ai/first/application/foundation/email/**`, `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/coreapp/workstream/**`, `frontend/src/workstream/**`, `frontend/src/api/**`, `frontend/src/design-system/**` | `src/test/java/ai/first/application/coreapp/useradmin/**`, `src/test/java/ai/first/application/foundation/identity/**`, `src/test/java/ai/first/application/foundation/invitation/**`, `frontend/src/workstream-user-admin-vertical.contract.test.mjs`, `frontend/src/workstream-user-admin-expertise.contract.test.mjs`, `frontend/src/workstream-organization-admin-vertical.contract.test.mjs` | TASK-ADR-02-002 description refresh; validation limited to `git diff --check`. | Broad candidate mapping for the refreshed current-intent graph. The graph now explicitly links human/functional-agent/system workers, `surface_action`/`human_chat_tool_plan`/`agent_tool_call`/API/workflow/timer/consumer/internal adapters, invitation/membership/role/access/support/admin-audit governed tools, capability `user-and-access-administration`, realization maps, tests/runtime-validation expectations, and trace obligations. Implementation must be reviewed as `stale-description-changed` before alignment can be claimed. |

## Unmapped current-intent files

- None recorded during the initial migration. This means no known app-description file was intentionally excluded, not that all current-intent files are aligned.

## Unmapped implementation files

- None recorded during the initial migration. Shared foundation/workstream files may support multiple workstreams and need finer-grained classification in future reviews.

## Alignment notes

- Current state is `stale-description-changed` because the User Admin app-description was refreshed after the last known source/runtime evidence and no code/API/UI validation was run for this task.
- Runtime-validation coverage remains expected for invitation lifecycle, user list/detail, role change denial/success, last-admin protection, audit trace evidence, chat-plan confirmation/partial-failure behavior, and provider/model/outbox fail-closed paths.
- If mapped app-description files are newer than mapped implementation/test files, set the lifecycle implementation alignment to `stale-description-changed` unless a no-code-impact review is recorded.
- If mapped implementation files changed without app-description reconciliation, set the lifecycle implementation alignment to `stale-code-changed` or `partially-aligned`.
- Do not use this source-alignment file as runtime-readiness evidence. Runtime readiness still requires automated checks and real local API/UI/agent-path verification for the selected scope.

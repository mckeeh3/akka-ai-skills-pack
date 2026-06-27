# My Account source alignment

Lifecycle: ../lifecycle.md
Last reviewed: 2026-06-27
Alignment state: partially-aligned

This file was added during the initial source-alignment migration and updated during the current skills-pack My Account app-description review. The user explicitly stated that none of the existing workstreams were aligned before compile work began. The broad candidate mapping has now been split into slice-level entries so later automated tasks can update one runtime area at a time without overstating readiness. Only the dashboard command-center contract slice has focused compile evidence; every other entry is a conservative source-alignment map until its linked validation task passes.

## Alignment status summary

| Entry id | Slice | Status | Runtime readiness claim |
| --- | --- | --- | --- |
| `my-account.dashboard` | Personal command-center dashboard and counter/control-panel payload | `partially-aligned` | No runtime readiness claim; focused compile/static frontend contract evidence only. |
| `my-account.profile-settings` | Profile and settings self-service reads/saves | `pending-validation` | None. |
| `my-account.context-authority` | Selected `AuthContext`, context switch, authority explanations | `pending-validation` | None. |
| `my-account.notification-center` | In-app notification center reads/lifecycle/preferences/source-open | `pending-validation` | None. |
| `my-account.digest-export` | Personal attention digest/export progress/result/blocked lifecycle | `pending-validation` with provider-success config dependency | None; provider-backed success remains unclaimed. |
| `my-account.chat-plan` | Bounded `human_chat_tool_plan` proposal/confirmation/result/system-message path | `pending-validation` | None. |
| `my-account.trace-audit` | Durable work/audit traces and browser-safe trace refs | `pending-validation` | None. |
| `my-account.no-access-recovery` | No-membership, denied/open-unavailable, stale/hidden recovery | `pending-validation` | None. |

## Alignment entries

### `my-account.dashboard`

- App-description files: `../workstream.md`, `../surfaces/surfaces.md` (`surface-my-account-dashboard`), `../behavior.md`, `../access.md`, `../tools/governed-tools.md`, `../tests/coverage.md`, `akka-components.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/account-context-and-profile.md`.
- Implementation files: `src/main/java/ai/first/application/coreapp/myaccount/MyAccountService.java`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`, `frontend/src/workstream/surfaces/DashboardSurface.tsx`, `frontend/src/workstream/types/surfaces.ts`, `frontend/src/api/**`.
- Test / validation files: `frontend/src/workstream-my-account-vertical.contract.test.mjs`, `frontend/src/workstream-shell.contract.test.mjs`, `frontend/src/workstream-attention-backbone.contract.test.mjs`, `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`.
- Last aligned evidence: 2026-06-27 focused compile for dashboard contract id `my_account.personal_command_center.v1`, required `controlPanels[]` payload aliases, accessible counter rendering, and static frontend contract coverage.
- Remaining validation gaps: protected API/dashboard open path, zero-count counter opening, hidden/forbidden workstream denial, stale/reconnect recovery, tenant/customer isolation, durable trace evidence, and manual browser smoke remain unclaimed.

### `my-account.profile-settings`

- App-description files: `../surfaces/surfaces.md` (`surface-my-profile`, `surface-my-settings`), `../tools/governed-tools.md`, `../workers/signed-in-member-human.md`, `../workers/my-account-system-worker.md`, `../agents/functional-agent.md`, `../policies/policy-bindings.md`, `../tests/coverage.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/account-context-and-profile.md`.
- Implementation files: `src/main/java/ai/first/application/coreapp/myaccount/MyAccountService.java`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`, `frontend/src/workstream/surfaces/DetailEditSurface.tsx`, `frontend/src/workstream/actions/**`, `frontend/src/api/**`.
- Test / validation files: `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`, `src/test/java/ai/first/application/coreapp/myaccount/**`, `frontend/src/workstream-my-account-vertical.contract.test.mjs`, `frontend/src/workstream-actions.contract.test.mjs`, `frontend/src/workstream-surfaces.contract.test.mjs`.
- Last aligned evidence: 2026-06-27 source-alignment split only; no automated runtime proof recorded for current profile/settings behavior.
- Remaining validation gaps: editable-only field submission, named-theme persistence versus local preview, unsupported-field/role/capability/provider-secret denials, no-op/conflict/stale handling, selected `AuthContext`, idempotency, tenant/customer isolation, and trace/audit evidence.

### `my-account.context-authority`

- App-description files: `../access.md`, `../surfaces/surfaces.md` (`surface-my-context`), `../workers/signed-in-member-human.md`, `../workers/my-account-system-worker.md`, `../tools/governed-tools.md`, `../tests/coverage.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/account-context-and-profile.md`.
- Implementation files: `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`, `src/main/java/ai/first/application/foundation/identity/**`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `frontend/src/workstream/shell/ContextAuthorityBar.tsx`, `frontend/src/workstream/surfaces/DetailEditSurface.tsx`, `frontend/src/api/**`.
- Test / validation files: `src/test/java/ai/first/application/foundation/identity/MeServiceTest.java`, `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`, `frontend/src/workstream-shell.contract.test.mjs`, `frontend/src/workstream-my-account-vertical.contract.test.mjs`.
- Last aligned evidence: 2026-06-27 source-alignment split only; selected-context runtime behavior is not yet revalidated against the current description.
- Remaining validation gaps: backend-selected context refresh, authorized context switching, no-op current-context selection, hidden/cross-tenant/inactive context denial without enumeration, stale surface marking, support-access visibility, and trace/correlation evidence.

### `my-account.notification-center`

- App-description files: `../surfaces/surfaces.md` (`surface-my-account-notification-center`), `../tools/governed-tools.md`, `../workers/signed-in-member-human.md`, `../workers/my-account-functional-agent-worker.md`, `../workers/my-account-system-worker.md`, `../behavior.md`, `../tests/coverage.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/account-context-and-profile.md`.
- Implementation files: `src/main/java/ai/first/application/foundation/notification/**`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `frontend/src/workstream/surfaces/NotificationCenterSurface.tsx`, `frontend/src/workstream/actions/**`, `frontend/src/api/**`.
- Test / validation files: `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`, notification-focused backend tests when present, `frontend/src/workstream-my-account-vertical.contract.test.mjs`, `frontend/src/workstream-surfaces.contract.test.mjs`.
- Last aligned evidence: 2026-06-27 source-alignment split only; notification lifecycle and rendering are not yet proven against the current triage contract.
- Remaining validation gaps: authorized/empty center, mark-read/dismiss/archive/snooze/preference lifecycle, repeated-action no-op/idempotency, source-open reauthorization, no source-work mutation, hidden-source denial, in-app-only external-provider omission, tenant/customer isolation, responsive/frontend secret-boundary coverage, and traces.

### `my-account.digest-export`

- App-description files: `../surfaces/surfaces.md` (`surface-my-account-personal-attention-digest-progress`, `surface-my-account-personal-attention-digest-result`, `surface-my-account-personal-attention-digest-blocked`), `../tools/governed-tools.md`, `../workers/**`, `../agents/functional-agent.md`, `../policies/policy-bindings.md`, `../traces/work-traces.md`, `../tests/coverage.md`, `akka-components.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/account-context-and-profile.md`.
- Implementation files: `src/main/java/ai/first/application/coreapp/myaccount/DigestExportService.java`, `src/main/java/ai/first/application/coreapp/myaccount/MyAccountPersonalAttentionDigestService.java`, `src/main/java/ai/first/application/coreapp/myaccount/MyAccountPersonalAttentionDigestTasks.java`, `src/main/java/ai/first/application/coreapp/myaccount/MyAccountPersonalAttentionDigestAutonomousAgent.java`, `src/main/java/ai/first/application/coreapp/myaccount/*MyAccountPersonalAttentionDigest*Runtime.java`, `src/main/java/ai/first/application/coreapp/myaccount/*MyAccountPersonalAttentionDigest*Repository*.java`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `frontend/src/workstream/surfaces/WorkflowStatusSurface.tsx`, `frontend/src/workstream/surfaces/OutcomeSurface.tsx`, `frontend/src/workstream/surfaces/SystemMessageSurface.tsx`.
- Test / validation files: `src/test/java/ai/first/application/coreapp/myaccount/DigestExportServiceTest.java`, `src/test/java/ai/first/application/coreapp/myaccount/MyAccountPersonalAttentionDigestServiceTest.java`, `src/test/java/ai/first/application/coreapp/myaccount/MyAccountPersonalAttentionDigestAutonomousAgentTest.java`, `frontend/src/workstream-my-account-vertical.contract.test.mjs`, digest/frontend contract tests when present.
- Last aligned evidence: 2026-06-27 source-alignment split only. Existing digest tests may be candidate evidence, but provider-backed success and current surface contracts have not been revalidated in this alignment task.
- Remaining validation gaps: start/read/cancel/result/accept/reject/export paths, fail-closed provider/runtime/tool-boundary blocked surface with `noFakeSuccess`, no source-attention mutation, advisory-only review disposition, task ownership and tenant/customer denials, durable task events, and provider-backed happy path if concrete configuration is available.

### `my-account.chat-plan`

- App-description files: `../workstream.md` (`Confirmed human chat tool-plan exposure`), `../agents/functional-agent.md`, `../workers/signed-in-member-human.md`, `../workers/my-account-functional-agent-worker.md`, `../workers/my-account-system-worker.md`, `../tools/governed-tools.md` (`human_chat_tool_plan` catalog), `../surfaces/surfaces.md` (`Shared chat tool-plan surfaces`), `../traces/work-traces.md`, `../tests/coverage.md`, `../../surface-catalog.md`, `api-contracts.md`, `frontend-routes.md`.
- Implementation files: `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `src/main/java/ai/first/application/foundation/agent/AgentBehaviorSeedLoader.java`, `src/main/java/ai/first/application/coreapp/myaccount/MyAccountService.java`, `src/main/java/ai/first/application/foundation/notification/**`, `frontend/src/workstream/**`, `frontend/src/api/**`.
- Test / validation files: `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`, `src/test/java/ai/first/application/foundation/agent/AgentBehaviorSeedLoaderTest.java`, tool-boundary/trace tests when present, `frontend/src/workstream-chat-tool-plan.contract.test.mjs`, `frontend/src/workstream-my-account-vertical.contract.test.mjs`.
- Last aligned evidence: 2026-06-27 source-alignment split only; no chat-plan runtime execution or proposal proof is claimed.
- Remaining validation gaps: deterministic surface routing before planning, no-mutation proposal, exact plan/snapshot confirmation, unsupported/stale/cross-context/out-of-catalog denials, per-step idempotency/transaction boundaries, partial-failure reporting, provider/model unavailable fail-closed state, tool-boundary enforcement, and durable chat-plan traces.

### `my-account.trace-audit`

- App-description files: `../traces/work-traces.md`, `../tools/governed-tools.md`, `../workers/**`, `../surfaces/surfaces.md`, `../tests/coverage.md`, `akka-components.md`, `api-contracts.md`, `../../../capabilities/account-context-and-profile.md`, `../../../../../global/traces/foundation-trace-patterns.md`.
- Implementation files: `src/main/java/ai/first/application/foundation/audit/**`, `src/main/java/ai/first/application/foundation/workstream/**`, `src/main/java/ai/first/application/coreapp/myaccount/MyAccountEvidenceTools.java`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `frontend/src/workstream/actions/TraceLinkList.tsx`, `frontend/src/workstream/surfaces/**`.
- Test / validation files: trace/audit-focused backend tests when present, `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`, `src/test/java/ai/first/application/coreapp/myaccount/**`, `frontend/src/workstream-my-account-vertical.contract.test.mjs`.
- Last aligned evidence: 2026-06-27 source-alignment split only; trace obligations are mapped, not proven.
- Remaining validation gaps: durable trace fact persistence for protected reads/actions and chat-plan lifecycle, actor adapter/source classification, selected `AuthContext`, capability/tool/action ids, policy decisions, idempotency/correlation keys, denial/provider-blocked traces, redaction, browser-safe trace refs, and audit/work-trace retrieval evidence.

### `my-account.no-access-recovery`

- App-description files: `../access.md`, `../behavior.md`, `../surfaces/surfaces.md` (`surface-my-account-open-denied` and no-membership/no-selected-context states), `../tools/governed-tools.md`, `../workers/signed-in-member-human.md`, `../workers/my-account-system-worker.md`, `../tests/coverage.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/account-context-and-profile.md`.
- Implementation files: `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`, `src/main/java/ai/first/application/foundation/identity/**`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `frontend/src/workstream/surfaces/SystemMessageSurface.tsx`, `frontend/src/workstream/shell/**`, `frontend/src/api/**`.
- Test / validation files: `src/test/java/ai/first/application/foundation/identity/MeServiceTest.java`, `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`, `frontend/src/workstream-my-account-vertical.contract.test.mjs`, `frontend/src/workstream-shell.contract.test.mjs`, `frontend/src/workstream-surfaces.contract.test.mjs`.
- Last aligned evidence: 2026-06-27 source-alignment split only; no-access and open-denied runtime recovery are not yet revalidated.
- Remaining validation gaps: no active membership/no selected context recovery, disabled account, hidden workstream/source/context denial without enumeration, authorized dashboard return, context refresh, retry-safe denial/success, request-access guidance availability, missing bearer/auth failure routing, tenant/customer isolation, and trace/correlation evidence.

## Unmapped current-intent files

- None recorded during this split. This means no known My Account app-description file was intentionally excluded, not that all current-intent files are aligned.

## Unmapped implementation files

- None recorded during this split. Shared foundation/workstream files may support multiple workstreams and need finer-grained classification in future reviews.

## Alignment notes

- Current state is `partially-aligned` because the 2026-06-27 compile reconciled a bounded command-center/dashboard contract slice, while the other feature-bearing graph nodes are split into pending validation entries.
- `compile-ready` remains a description/build-planning state. This file is not runtime-readiness evidence.
- If mapped app-description files are newer than mapped implementation/test files, set the lifecycle implementation alignment to `stale-description-changed` unless a no-code-impact review is recorded.
- If mapped implementation files changed without app-description reconciliation, set the lifecycle implementation alignment to `stale-code-changed` or `partially-aligned`.
- Do not use this source-alignment file as runtime-readiness evidence. Runtime readiness still requires automated checks and real local API/UI/agent-path verification for the selected scope.

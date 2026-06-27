# My Account source alignment

Lifecycle: ../lifecycle.md
Last reviewed: 2026-06-27
Alignment state: partially-aligned

This file was added during the initial source-alignment migration and updated during the current skills-pack My Account app-description review. The user explicitly stated that none of the existing workstreams were aligned before compile work began. The broad candidate mapping has now been split into slice-level entries so later automated tasks can update one runtime area at a time without overstating readiness. Only the dashboard command-center contract slice has focused compile evidence; every other entry is a conservative source-alignment map until its linked validation task passes.

## Alignment status summary

| Entry id | Slice | Status | Runtime readiness claim |
| --- | --- | --- | --- |
| `my-account.dashboard` | Personal command-center dashboard and counter/control-panel payload | `backend-api-aligned` | `api-smoked` for protected WorkstreamEndpoint dashboard, attention-counter open routing, selected `AuthContext`, and safe open-denied paths; frontend/manual and durable trace depth remain separate. |
| `my-account.profile-settings` | Profile and settings self-service reads/saves | `backend-api-aligned` | `api-smoked` for protected profile/settings reads, save/no-op/idempotent replay, validation errors, unsupported/provider-backed field denial, invalid theme/timezone, selected `AuthContext`, and browser-safe payloads. |
| `my-account.context-authority` | Selected `AuthContext`, context switch, authority explanations | `backend-api-aligned` | `api-smoked` for protected context read, authorized selected-context switch/no-op, hidden context denial, selected tenant/customer scope, and browser-safe payloads. |
| `my-account.notification-center` | In-app notification center reads/lifecycle/preferences/source-open | `pending-validation` | None. |
| `my-account.digest-export` | Personal attention digest/export progress/result/blocked lifecycle | `pending-validation` with provider-success config dependency | None; provider-backed success remains unclaimed. |
| `my-account.chat-plan` | Bounded `human_chat_tool_plan` proposal/confirmation/result/system-message path | `pending-validation` | None. |
| `my-account.trace-audit` | Durable work/audit traces and browser-safe trace refs | `pending-validation` | None. |
| `my-account.no-access-recovery` | No-membership, denied/open-unavailable, stale/hidden recovery | `backend-api-aligned` | `api-smoked` for protected open-denied recovery plus no-active-membership and disabled-account My Account recovery without tenant/customer/workstream enumeration. |

## Alignment entries

### `my-account.dashboard`

- App-description files: `../workstream.md`, `../surfaces/surfaces.md` (`surface-my-account-dashboard`), `../behavior.md`, `../access.md`, `../tools/governed-tools.md`, `../tests/coverage.md`, `akka-components.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/account-context-and-profile.md`.
- Implementation files: `src/main/java/ai/first/application/coreapp/myaccount/MyAccountService.java`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`, `frontend/src/workstream/surfaces/DashboardSurface.tsx`, `frontend/src/workstream/types/surfaces.ts`, `frontend/src/api/**`.
- Test / validation files: `frontend/src/workstream-my-account-vertical.contract.test.mjs`, `frontend/src/workstream-shell.contract.test.mjs`, `frontend/src/workstream-attention-backbone.contract.test.mjs`, `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`.
- Last aligned evidence: 2026-06-27 focused compile for dashboard contract id `my_account.personal_command_center.v1`, required `controlPanels[]` payload aliases, accessible counter rendering, and static frontend contract coverage; MAFA-02-001 protected backend/API smoke `MyAccountBrowserWorkstreamSmokeTest` now covers dashboard read through `/api/workstream/surfaces`, attention counter payload/action mapping, authorized counter open routing, tenant/member/owner selected-context behavior, and hidden/forbidden workstream safe denial.
- Remaining validation gaps: frontend browser/manual rendering, stale/reconnect recovery depth, durable trace persistence beyond returned trace refs, and broader terminal runtime readiness remain unclaimed.

### `my-account.profile-settings`

- App-description files: `../surfaces/surfaces.md` (`surface-my-profile`, `surface-my-settings`), `../tools/governed-tools.md`, `../workers/signed-in-member-human.md`, `../workers/my-account-system-worker.md`, `../agents/functional-agent.md`, `../policies/policy-bindings.md`, `../tests/coverage.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/account-context-and-profile.md`.
- Implementation files: `src/main/java/ai/first/application/coreapp/myaccount/MyAccountService.java`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`, `frontend/src/workstream/surfaces/DetailEditSurface.tsx`, `frontend/src/workstream/actions/**`, `frontend/src/api/**`.
- Test / validation files: `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`, `src/test/java/ai/first/application/coreapp/myaccount/**`, `frontend/src/workstream-my-account-vertical.contract.test.mjs`, `frontend/src/workstream-actions.contract.test.mjs`, `frontend/src/workstream-surfaces.contract.test.mjs`.
- Last aligned evidence: MAFA-02-001 protected backend/API smoke `MyAccountBrowserWorkstreamSmokeTest` covers profile/settings surface reads, allowed display-name/theme/locale/timezone persistence, idempotent replay/no-op behavior, unsupported role/provider-backed/account-status field denial, invalid named theme/timezone validation, selected `AuthContext`, post-denial no-mutation checks, and browser-safe payload assertions through `/api/workstream`.
- Remaining validation gaps: frontend editable-only submission contract, local named-theme preview UI behavior, conflict/stale UI recovery, durable trace/audit evidence, and manual/browser runtime evidence remain unclaimed.

### `my-account.context-authority`

- App-description files: `../access.md`, `../surfaces/surfaces.md` (`surface-my-context`), `../workers/signed-in-member-human.md`, `../workers/my-account-system-worker.md`, `../tools/governed-tools.md`, `../tests/coverage.md`, `api-contracts.md`, `frontend-routes.md`, `../../../capabilities/account-context-and-profile.md`.
- Implementation files: `src/main/java/ai/first/api/foundation/security/MeEndpoint.java`, `src/main/java/ai/first/application/foundation/identity/**`, `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`, `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`, `frontend/src/workstream/shell/ContextAuthorityBar.tsx`, `frontend/src/workstream/surfaces/DetailEditSurface.tsx`, `frontend/src/api/**`.
- Test / validation files: `src/test/java/ai/first/application/foundation/identity/MeServiceTest.java`, `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`, `frontend/src/workstream-shell.contract.test.mjs`, `frontend/src/workstream-my-account-vertical.contract.test.mjs`.
- Last aligned evidence: MAFA-02-001 protected backend/API smoke `MyAccountBrowserWorkstreamSmokeTest` covers context authority surface read, selected tenant/customer `AuthContext`, authorized customer-context switch/no-op result, hidden/cross-tenant context denial without enumeration, stale-impact/result metadata, missing-bearer denial, and browser-safe payload assertions through `/api/workstream`.
- Remaining validation gaps: frontend shell refresh behavior after context switch, inactive context variants beyond hidden/cross-tenant denial, durable trace/audit evidence, and manual/browser runtime evidence remain unclaimed.

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
- Last aligned evidence: MAFA-02-001 protected backend/API smoke `MyAccountBrowserWorkstreamSmokeTest` covers open-denied direct/action surfaces, hidden workstream denial without capability/name enumeration, authorized dashboard/counter recovery routing, no-active-membership recovery, disabled-account recovery, selected-context headers, missing-bearer failure, tenant/customer redaction, and correlation/trace references through protected `/api/workstream` routes.
- Remaining validation gaps: no selected-context recovery variants beyond backend defaulting, frontend recovery rendering, durable denial trace persistence, request-access follow-through, and manual/browser runtime evidence remain unclaimed.

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

# Foundation Surface Completion Tracker

## Harness instructions

This document is the durable status board for foundation dashboard/surface completion. Use it together with `pending-tasks.md`.

For the next pending sub-task:

1. Select the first `pending` entry in `pending-tasks.md` whose dependencies are satisfied.
2. Mark only that task `in-progress` before edits.
3. Complete exactly one objective for exactly one dashboard/surface.
4. Update the matching row/objective below with status and evidence.
5. Run the required checks or record a blocker.
6. Mark the task `done` only when the objective's criteria are met; otherwise mark `blocked` with a precise blocker.
7. Commit the task changes and queue/tracker update together when possible.

## Objective definitions

### fully-specified

Goal: prove or repair the app-description contract so a developer/generator can implement and review the surface without inventing product meaning.

Evidence must cover: surface identity, owner functional agent, placement, purpose, payload schema, redaction, data source, actions/events, governed capability/tool mapping, authority/AuthContext/tenant rules, audit/work trace, UI states, accessibility/responsive expectations, style/catalog binding, and tests. If the description is already sufficient, record the evidence path and mark `done`. If not, update app-description or mark `blocked` with the missing decision.

### fully-implemented

Goal: prove or implement the real runtime path for the surface at the stated scope.

Evidence must cover: browser surface/action or non-UI trigger, frontend component/client path, protected API/workstream endpoint path, backend Akka component/service/substrate path, authorization and tenant scoping, side effects/projections, typed result/system-message handling, audit/work trace, provider configured or fail-closed behavior, and no normal fixture/demo/mock runtime path. Implementation may be marked `done` only when runtime code exists and aligns with the app-description objective.

### fully-tested

Goal: prove the implemented surface through tests and/or recorded manual/API/browser smoke evidence.

Evidence must cover: success path, validation/error path, forbidden/denied/hidden path where applicable, tenant/customer isolation, stale/conflict/idempotency where applicable, audit/work trace/correlation, provider fail-closed behavior, frontend secret boundary, and local commands/manual-smoke results. Unit, service, contract, typecheck, and build checks support but do not alone prove user-visible runtime readiness.

## Status values

- `pending` — not yet assessed or completed.
- `in-progress` — currently being worked.
- `blocked` — missing prerequisite, decision, code, provider config, seed data, or validation path.
- `done` — objective complete with evidence.
- `deferred` — intentionally postponed.
- `superseded` — replaced by newer intent/spec.

## Tracker

## My Account

### `surface-my-account-dashboard`

- Workstream: My Account
- Type: `dashboard`
- Contract: `my_account.personal_command_center.v1`
- Purpose: Personal command center for attention, authority, settings, notifications, and digest/export work.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract verified and completed in `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`: includes surface identity/owner/placement/purpose, primary `attentionCounters[]` and secondary `controlPanels[]` payload schema, forbidden payload/redaction boundaries, governed action-to-capability mappings, selected AuthContext/tenant authorization rules, trace/audit contract, UI states, accessibility/responsive expectations, style/catalog bindings, and acceptance/security/observability regression coverage. Sufficiency review says the dashboard is implementable without invented fields/actions/states/auth/traces/tests. | 2026-06-16 |
| fully-implemented | done | Runtime implementation verified for `surface-my-account-dashboard`: protected `/api/workstream/surfaces/{surfaceId}` and `/api/workstream/actions` in `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` resolve JWT identity plus `X-Selected-Context-Id`; `WorkstreamService#myAccountDashboardSurface` returns the backend-owned `my_account.personal_command_center.v1` envelope with `attentionCounters`, `controlPanels`, selected AuthContext/account context, redaction, trace refs, notification and digest summaries, and governed action edges; `MyAccountService` enforces `my_account.view_summary`, `my_account.view_context`, `my_account.list_personal_attention`, and `my_account.open_authorized_workstream`; the React `DashboardSurface` renders the My Account command center from the backend surface envelope and routes counter/control-panel actions through `SurfaceActionBar`/capability action requests rather than frontend-only authority. Existing tests prove backend retrieval, safe workstream open/denial, trace/context data, and frontend typed rendering. Checks passed: focused Maven `WorkstreamServiceTest` invocation (1 My Account runtime test executed), `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs` (154 node contract tests passed), focused `rg` evidence for surface id/contract/counters/actions/auth/traces, and `git diff --check`. | 2026-06-16 |
| fully-tested | done | Added and passed `src/test/java/ai/first/application/coreapp/workstream/MyAccountBrowserWorkstreamSmokeTest.java`, a TestKit/httpClient hosted-shell and protected workstream API smoke that loads `/ui`, bootstraps `/api/workstream/bootstrap` with JWT and `X-Selected-Context-Id`, fetches `surface-my-account-dashboard`, verifies backend-owned `my_account.personal_command_center.v1` payload/actions/trace/correlation/secret-boundary evidence, starts the personal digest action and observes fail-closed `surface-my-account-personal-attention-digest-blocked` with `noFakeSuccess`/`noDirectMutation`, opens an authorized sibling workstream, verifies regular-member hidden-workstream denial as `not_found_or_redacted`, and confirms missing bearer access is rejected. Supporting frontend contract/typecheck evidence: `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs` and `npm --prefix frontend run typecheck`. Runtime readiness level: `runtime-ready` for the dashboard testing scope via Akka-hosted UI shell + protected API/action path. | 2026-06-16 |

### `surface-my-profile`

- Workstream: My Account
- Type: `detail-edit`
- Contract: `my_account.profile.self_service.v1`
- Purpose: Browser-safe identity/profile self-service with clear immutable/provider-backed fields.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract verified and completed in `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`: includes `surface-my-profile` identity/owner/placement/purpose, `my_account.profile.self_service.v1` payload schema for `profileSummary`, `providerBoundarySummary`, `fields[]`, permission state, audit/trace/correlation summaries, forbidden payload and unsupported mutation boundaries, governed action-to-capability mappings for refresh/save/open-trace, selected AuthContext and self-account authorization rules, tenant/customer isolation, trace/audit contract, UI states, accessibility/responsive expectations, style/catalog bindings, and acceptance/security/observability regression coverage. Sufficiency review says the profile self-service surface is implementable without invented fields/actions/states/auth/traces/tests. | 2026-06-16 |
| fully-implemented | done | Runtime implementation completed and verified for `surface-my-profile`: protected `/api/workstream/surfaces/{surfaceId}` and `/api/workstream/actions` in `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` resolve JWT identity plus `X-Selected-Context-Id`; `WorkstreamService#myProfileSurface` now reauthorizes `my_account.view_summary`, emits a profile self-service read trace, returns the backend-owned `my_account.profile.self_service.v1` envelope with `profileSummary`, `providerBoundarySummary`, editable/read-only `fields[]`, `permissionState`, redaction metadata, `traceRefs`, and audit links; `action-update-my-profile` persists only allowed self-service fields through `MyAccountService#updateProfileSettings`/`AuthContextResolver#updateOwnProfileSettings`, denies unsupported role/status/provider mutations before mutation, and returns the refreshed profile surface with idempotent action handling; `DetailEditSurface` renders the profile boundary, account/status/context summary, styled form controls, governed action bar, audit affordances, and browser-safe secret boundaries. Checks passed: focused `WorkstreamServiceTest` My Account profile methods, `MyAccountBrowserWorkstreamSmokeTest` protected Akka-hosted UI/workstream API smoke, frontend contract test, frontend typecheck, focused `rg` evidence, and `git diff --check`. Runtime readiness level: `api-smoked` for implementation verification with protected API/action path, tenant admin AuthContext in `tenant-starter`, unsupported-field denial/no-op/idempotency evidence, trace/correlation evidence, and no frontend-only/fixture-only normal runtime path. | 2026-06-16 |
| fully-tested | done | Added and passed `src/test/java/ai/first/application/coreapp/workstream/MyAccountBrowserWorkstreamSmokeTest.java` coverage for the protected Akka-hosted My Account profile path: JWT plus selected `AuthContext` loads `/api/workstream/surfaces/surface-my-profile`, verifies `my_account.profile.self_service.v1` payload fields/permission state/provider-boundary/redaction/trace/correlation, saves display-name changes through `/api/workstream/actions` with `action-update-my-profile`, verifies `/api/workstream/bootstrap` reflects persisted backend state, exercises repeat/no-op behavior, denies unsupported role-field self-service mutation before state change, rejects missing bearer access, and checks browser-safe secret boundaries. Supporting frontend contract/typecheck evidence: `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs` and `npm --prefix frontend run typecheck`. Runtime readiness level: `runtime-ready` for the profile testing scope via protected API/action path with tenant admin AuthContext in `tenant-starter`, denial/no-op/trace/correlation/provider-boundary evidence, and no fixture-only/frontend-only normal runtime path. | 2026-06-16 |

### `surface-my-settings`

- Workstream: My Account
- Type: `detail-edit`
- Contract: `my_account.preferences.self_service.v1`
- Purpose: Personal preferences, named theme selection, locale/timezone, and preference save state.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract verified and completed in `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`: includes `surface-my-settings` identity/owner/placement/purpose, `my_account.preferences.self_service.v1` payload schema for `settingsSummary`, named `availableThemes[]`, locale/timezone, notification/digest summaries, editable `fields[]`, `permissionState`, forbidden payload and unsupported mutation boundaries, governed action-to-capability mappings for preview/save/open-notifications/open-trace, selected AuthContext and self-account authorization rules, tenant/customer isolation, trace/audit contract, UI states, accessibility/responsive expectations, style/catalog bindings, and acceptance/security/observability regression coverage. Sufficiency review says the settings/preferences surface is implementable without invented fields/actions/states/auth/traces/tests. | 2026-06-16 |
| fully-implemented | done | Runtime implementation completed and verified for `surface-my-settings`: protected `/api/workstream/surfaces/surface-my-settings` and `/api/workstream/actions` in `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` resolve JWT identity plus selected `X-Selected-Context-Id`; `WorkstreamService#mySettingsSurface` returns backend-owned `my_account.preferences.self_service.v1` with `settingsSummary`, named `availableThemes[]`, editable `preferredThemeId`/`locale`/`timeZone` fields, notification/digest summaries, permission state, redaction metadata, trace refs, and action edges for save/open-notification/open-trace; `UserSettings`, `AuthContextResolver`, `MyAccountService`, and `MeResponse` now persist and expose allowed theme/locale/timezone preferences through the governed `my_account.update_profile_settings` path while rejecting unsupported/invalid preference fields before mutation; frontend auth typing accepts the persisted settings shape and existing `DetailEditSurface` renders named-theme preview and governed save inputs. Checks passed: focused `WorkstreamServiceTest` settings methods, protected Akka-hosted `MyAccountBrowserWorkstreamSmokeTest#protectedWorkstreamApiExercisesMySettingsRuntimePath`, frontend My Account contract test, frontend typecheck, and `git diff --check`. Runtime readiness level: `api-smoked` for implementation verification with protected API/action path, tenant admin AuthContext in `tenant-starter`, invalid-timezone and unsupported-field denial/no-mutation coverage, trace/correlation evidence, browser-safe redaction/provider/secret boundaries, and no frontend-only/fixture-only normal runtime path. | 2026-06-16 |
| fully-tested | done | Expanded and passed `src/test/java/ai/first/application/coreapp/workstream/MyAccountBrowserWorkstreamSmokeTest.java` coverage for the protected Akka-hosted My Account settings path: JWT plus selected `AuthContext` loads `/api/workstream/surfaces/surface-my-settings`, verifies backend-owned `my_account.preferences.self_service.v1` payload fields/available named themes/notification action/trace/correlation/secret-boundary evidence, saves preferred theme/locale/timezone through `/api/workstream/actions`, opens the notification-center action edge, confirms `/api/workstream/bootstrap` reflects persisted backend state, exercises no-op behavior, denies unsupported provider-secret and invalid-timezone mutations before state change, rejects missing bearer access, and checks browser-safe redaction for provider secrets, hidden categories, arbitrary CSS, fake providers, and fake models. Supporting frontend contract/typecheck evidence: `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs` and `npm --prefix frontend run typecheck`. Runtime readiness level: `runtime-ready` for the settings testing scope via protected API/action path with tenant admin AuthContext in `tenant-starter`, denial/no-op/trace/correlation/notification-link/provider-boundary evidence, and no fixture-only/frontend-only normal runtime path. | 2026-06-16 |

### `surface-my-context`

- Workstream: My Account
- Type: `detail-edit / authority panel`
- Contract: `my_account.context_authority.v1`
- Purpose: Selected AuthContext, active membership, role/capability basis, and context-switch targets.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract verified and completed in `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`: includes `surface-my-context` identity/type/contract/purpose, authority-panel ownership by `my-account-agent`, selected `AuthContext` and available-context payload schema, browser-safe role/capability/support-access/redaction summaries, governed refresh/select/open-trace action semantics, backend authorization and tenant/customer rules, hidden/inactive/cross-tenant denial behavior, stale-surface impact, audit/work trace contract, accessibility/responsive expectations, and acceptance/security/observability regression coverage. Sufficiency review says the context authority surface is implementable without invented fields/actions/states/auth/traces/tests. | 2026-06-16 |
| fully-implemented | done | Runtime implementation completed and verified for `surface-my-context`: protected `/api/workstream/surfaces/surface-my-context`, `/api/workstream/actions`, and `/api/workstream/bootstrap` now expose the backend-owned `my_account.context_authority.v1` authority surface with `selectedContext`, `authorityBasis`, role/capability category summaries, support-access summary, authorized `availableContexts[]`, redaction/hidden-context semantics, trace refs, correlation id, and stale-impact state. `WorkstreamService#contextAuthoritySurface` reauthorizes `my_account.view_context`, omits hidden/inactive contexts, returns browser-safe context metadata only, and maps `action-select-my-context` to the canonical `surface-my-context`; frontend context cards pass the requested selected context through the governed action path and refresh bootstrap so the selected AuthContext, workstream counters, traces, notifications, and surfaces are backend-derived rather than frontend-granted. Runtime evidence: readiness level `api-smoked` for implementation verification with protected API/surface/action/bootstrap paths, tenant-admin AuthContext in `tenant-starter`, authorized customer-context switch to `membership-admin-customer`, hidden/cross-tenant selected-context denial, missing-bearer rejection, trace/correlation evidence, and browser-safe redaction for raw JWT/provider/hidden context fields. Checks passed: focused `WorkstreamServiceTest` My Account context assertion, protected Akka-hosted `MyAccountBrowserWorkstreamSmokeTest#protectedWorkstreamApiExercisesMyContextRuntimePathAndSelection`, frontend My Account contract test, frontend typecheck, `git diff --check`, and runtime evidence validator. | 2026-06-16 |
| fully-tested | done | Expanded and passed `src/test/java/ai/first/application/coreapp/workstream/MyAccountBrowserWorkstreamSmokeTest.java` coverage for the protected Akka-hosted My Account context authority path: JWT plus selected `AuthContext` loads `/api/workstream/surfaces/surface-my-context`, verifies backend-owned `my_account.context_authority.v1` selected context, available-context, visible-capability, support-access, stale-impact, trace/correlation, and browser-safe redaction payloads; exercises `action-select-my-context` no-op/current-context feedback, validates `/api/workstream/bootstrap` for the authorized customer context, denies hidden/cross-tenant context action and surface requests without enumeration, rejects missing bearer access, and checks raw JWT/provider/hidden-context secret boundaries. Supporting service/frontend evidence: focused `WorkstreamServiceTest` context assertions, `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs`, and `npm --prefix frontend run typecheck`. Runtime readiness level: `runtime-ready` for the context testing scope via protected API/action/bootstrap path with tenant admin/customer AuthContext coverage, denial/no-op/trace/correlation/provider-boundary evidence, and no fixture-only/frontend-only normal runtime path. | 2026-06-16 |

### `surface-my-account-notification-center`

- Workstream: My Account
- Type: `notification-center`
- Contract: `my_account.notification_center.v1`
- Purpose: Personal in-app triage for authorized notifications.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract verified and completed in `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`: includes `surface-my-account-notification-center` identity/owner/placement/purpose, `my_account.notification_center.v1` in-app-only notification-center contract, triage-first payload schema for `triageSections[]`, `items[]`, preferences/source summaries, lifecycle actions, forbidden external-channel/provider/outbox boundaries, governed action-to-capability mappings, selected AuthContext and tenant/customer authorization rules, safe hidden/cross-tenant denial behavior, trace/audit/correlation contract, UI states, accessibility/responsive expectations, style/catalog bindings, and acceptance/security/observability regression coverage. Sufficiency review says the notification center is implementable without invented fields/actions/states/auth/traces/tests. | 2026-06-16 |
| fully-implemented | done | Runtime implementation verified for `surface-my-account-notification-center`: protected `/api/workstream/surfaces/surface-my-account-notification-center` and `/api/workstream/actions` in `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` resolve JWT identity plus selected `X-Selected-Context-Id`; `WorkstreamService#myAccountNotificationCenterSurface` returns the backend-owned `my_account.notification_center.v1` in-app-only envelope with unread/visible counts, authorized notification items, lifecycle action edges, preference summaries, source summaries, trace refs, correlation id, redaction, and notification capability ids; `NotificationService#listMyAccountCenter` projects authorized attention/workstream events into durable in-app notification state, enforces selected AuthContext capabilities and tenant/customer scoping, omits external channel/provider/outbox/delivery internals, and backs mark-read/dismiss/archive/snooze/preference actions without mutating source work; `NotificationCenterSurface` renders triage lanes, counts, lifecycle controls, in-app preference boundary, evidence links, responsive card grids, and browser-safe secret boundaries. Checks passed: focused Maven `WorkstreamServiceTest#myAccountNotificationCenterSurfaceRendersBackendProjectionAndLifecycleActions` plus protected Akka-hosted `MyAccountBrowserWorkstreamSmokeTest#protectedWorkstreamApiExercisesMySettingsRuntimePath`, frontend My Account contract test, frontend typecheck, focused `rg` evidence, `git diff --check`, and runtime evidence validator. Runtime readiness level: `api-smoked` for implementation verification with protected API/surface/action paths, tenant admin AuthContext in `tenant-starter`, source-state-unchanged lifecycle evidence, provider/external-channel fail-closed omission evidence, trace/correlation evidence, and no fixture-only/frontend-only normal runtime path. | 2026-06-16 |
| fully-tested | done | Added and passed protected Akka-hosted My Account notification-center runtime smoke coverage in `src/test/java/ai/first/application/coreapp/workstream/MyAccountBrowserWorkstreamSmokeTest.java`: seeded backend-owned in-app notifications through the Akka notification repository, loaded `/api/workstream/surfaces/surface-my-account-notification-center` with JWT plus selected AuthContext, verified `my_account.notification_center.v1` channel/payload/actions/trace/correlation and browser-safe external-channel/provider/outbox boundaries, exercised mark-read including repeat/no-source-mutation behavior, dismiss, archive, snooze, and in-app preference update through `/api/workstream/actions`, denied regular-member access to another user's notification without enumeration, and rejected missing-bearer access. Supporting frontend evidence: `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs` and `npm --prefix frontend run typecheck`. Runtime readiness level: `runtime-ready` for the notification-center testing scope via protected API/action path with tenant admin AuthContext in `tenant-starter`, denial/idempotent lifecycle/trace/correlation/provider-boundary evidence, and no fixture-only/frontend-only normal runtime path. | 2026-06-16 |

### `surface-my-account-personal-attention-digest-progress`

- Workstream: My Account
- Type: `workflow-status`
- Contract: `my_account.personal_attention_digest.progress.v1`
- Purpose: Autonomous personal briefing/digest task progress.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract verified and completed in `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`: includes `surface-my-account-personal-attention-digest-progress` identity/owner/placement/purpose, `my_account.personal_attention_digest.progress.v1` workflow-status payload schema for task ids, selected account context, status/phase, evidence window, durable `progressEvents[]`, blocker/result preview, available actions, redaction, no-direct-mutation, trace/correlation, governed start/read/cancel/open-result/open-blocked/open-trace action mappings, selected AuthContext plus tenant/customer/task visibility rules, provider/runtime fail-closed and no-fake-success behavior, accessibility/responsive expectations, style/component anatomy, and acceptance/security/observability regression coverage. Sufficiency review says the progress surface is implementable without invented fields/actions/states/auth/traces/tests. | 2026-06-16 |
| fully-implemented | done | Runtime implementation verified for `surface-my-account-personal-attention-digest-progress`: protected `/api/workstream/surfaces/surface-my-account-personal-attention-digest-progress` and `/api/workstream/actions` in `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` resolve JWT identity plus selected `X-Selected-Context-Id`; `WorkstreamService` exposes the backend-owned `my_account.personal_attention_digest.progress.v1` workflow-status surface, start/read/cancel action edges, durable `digestTaskId`/`autonomousAgentTaskId` projection fields, `authorizedAttentionCount`, evidence refs, durable `progressEvents`, trace refs, correlation id, redaction, and `noDirectMutation`; `MyAccountPersonalAttentionDigestService` starts and reads tenant/account-scoped digest tasks through the governed runtime path, persists durable task state via the Akka repository/runtime binding in `StarterSecurityComponents`, publishes lifecycle/attention/workstream events, and fails closed to the blocked surface when provider/runtime readiness is unavailable instead of returning fake success; `WorkflowStatusSurface` renders the personal digest progress timeline/action path from backend envelopes. Checks passed: focused Maven service/API smoke, frontend My Account contract test, frontend typecheck, and focused `rg` evidence. Runtime readiness level: `api-smoked` for implementation verification with protected API/action path, tenant admin AuthContext in `tenant-starter`, provider fail-closed/no-fake-success evidence, trace/correlation evidence, browser-safe redaction, and no fixture-only/frontend-only normal runtime path. | 2026-06-16 |
| fully-tested | done | Added and passed protected Akka-hosted My Account personal attention digest progress runtime smoke coverage in `src/test/java/ai/first/application/coreapp/workstream/MyAccountBrowserWorkstreamSmokeTest.java`: JWT plus selected AuthContext loads `surface-my-account-personal-attention-digest-progress`, verifies backend-owned `my_account.personal_attention_digest.progress.v1` not-started workflow-status payload/actions/trace/noDirectMutation/secret boundaries, starts digest through `/api/workstream/actions` and observes fail-closed blocked recovery with `noFakeSuccess`, reads the provider-blocked task, cancels it to a progress surface, verifies repeated terminal cancel preserves idempotent state, denies a member/cross-account read without enumeration, and rejects missing bearer access. Supporting service/frontend evidence: `MyAccountPersonalAttentionDigestServiceTest` covers queued/working projection, completed-review-required routing, advisory-only cancellation/accept/reject, cross-account denial, authorized evidence redaction, lifecycle events, and no source-attention mutation; `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs` and `npm --prefix frontend run typecheck` passed. Runtime readiness level: `runtime-ready` for the digest progress testing scope via protected API/action path with tenant admin AuthContext in `tenant-starter`, denial/idempotent cancel/trace/correlation/provider-fail-closed/no-fake-success/noDirectMutation evidence, and no fixture-only/frontend-only normal runtime path. | 2026-06-16 |

### `surface-my-account-personal-attention-digest-result`

- Workstream: My Account
- Type: `outcome-panel`
- Contract: `my_account.personal_attention_digest.result.v1`
- Purpose: Advisory digest/export result review with evidence, omissions, and accept/reject actions.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract verified and completed in `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`: includes `surface-my-account-personal-attention-digest-result` identity/owner/placement/purpose, `my_account.personal_attention_digest.result.v1` outcome-panel payload schema for advisory summary, recommendations, material events, pending decisions, evidence window, omissions/redaction, authorized source counts, confidence/quality notes, source refs, decision/review state, actions, trace/correlation, and `advisoryOnly`; governed read/accept/reject/open-source/export/open-trace action mappings; selected AuthContext plus tenant/customer/task visibility rules; review-only no-source-mutation semantics; provider/policy-blocked export recovery; accessibility/responsive expectations; acceptance/security/observability regression coverage; and sufficiency review. Sufficiency review says the result surface is implementable without invented fields/actions/states/auth/traces/tests. | 2026-06-16 |
| fully-implemented | done | Runtime implementation completed and verified for `surface-my-account-personal-attention-digest-result`: protected `/api/workstream/actions` read/accept/reject paths call `MyAccountPersonalAttentionDigestService` through `WorkstreamService`, return backend-owned `my_account.personal_attention_digest.result.v1` outcome-panel envelopes for completed review-required/accepted/rejected tasks, include account context, review status, advisory-only summary, recommendations, material events/evidence refs, pending decisions, omissions/redaction summaries, authorized source counts, evidence window, confidence/quality notes, source refs requiring reauthorization, trace refs, and `advisoryOnly`/`noDirectMutation`; direct `/api/workstream/surfaces/surface-my-account-personal-attention-digest-result` now returns an empty/no-result outcome surface instead of a progress fixture; repeated same decision is idempotent/no-op while non-completed or conflicting terminal review remains denied. Runtime evidence: readiness level `api-smoked` for implementation verification with protected API/action path, tenant admin AuthContext in `tenant-starter`, completed-review routing, accept/reject advisory-only behavior, cross-account denial in service coverage, provider/model-less success forbidden by runtime contracts, trace/correlation evidence, browser-safe redaction, and no fixture-only/frontend-only normal runtime path. Checks passed: `mvn -q -Dtest=MyAccountPersonalAttentionDigestServiceTest test`, `mvn -q -Dtest=MyAccountBrowserWorkstreamSmokeTest#protectedWorkstreamApiExercisesMyAccountPersonalAttentionDigestProgressRuntimePath test`, `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs`, `npm --prefix frontend run typecheck`, and `git diff --check`. | 2026-06-16 |
| fully-tested | done | Added and passed protected Akka-hosted My Account personal attention digest result runtime smoke coverage in `src/test/java/ai/first/application/coreapp/workstream/MyAccountBrowserWorkstreamSmokeTest.java`: JWT plus selected AuthContext loads the empty/no-result outcome panel, seeds completed backend digest tasks through the Akka task repository, reads the authorized `my_account.personal_attention_digest.result.v1` result via `/api/workstream/actions`, verifies advisory-only summary/recommendation/material-event/pending-decision/omission/trace/correlation/secret-boundary payloads, accepts with source-attention unchanged evidence, verifies repeated accept idempotency, denies conflicting terminal rejection, rejects missing rejection reason, records a valid rejection on a separate completed task, denies member/cross-account reads without enumeration, and rejects missing bearer access. Supporting service/frontend evidence: `MyAccountPersonalAttentionDigestServiceTest` and `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs` plus `npm --prefix frontend run typecheck` passed. Runtime readiness level: `runtime-ready` for the digest result testing scope via protected API/action path with tenant admin AuthContext in `tenant-starter`, denial/idempotent review/validation/trace/correlation/provider-secret/model-secret redaction/noDirectMutation evidence, and no fixture-only/frontend-only normal runtime path. | 2026-06-16 |

### `surface-my-account-personal-attention-digest-blocked`

- Workstream: My Account
- Type: `system-message`
- Contract: `my_account.personal_attention_digest.blocked.v1`
- Purpose: Provider/runtime fail-closed explanation and recovery.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract verified and completed in `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`: includes `surface-my-account-personal-attention-digest-blocked` identity/owner/placement/purpose, `my_account.personal_attention_digest.blocked.v1` system-message payload schema for blocker category/code, provider/runtime/tool readiness, retry eligibility, recovery steps, evidence window, actions, trace/correlation, redaction, mandatory `noFakeSuccess` and `noDirectMutation`, forbidden payload boundaries, governed action-to-capability mappings, selected AuthContext/task visibility rules, fail-closed provider/runtime behavior, accessibility/responsive expectations, acceptance/security/observability tests, and sufficiency review. Sufficiency review says the blocked surface is implementable without invented fields/actions/states/auth/traces/tests. | 2026-06-16 |
| fully-implemented | done | Runtime implementation completed and verified for `surface-my-account-personal-attention-digest-blocked`: protected `/api/workstream/surfaces/surface-my-account-personal-attention-digest-blocked` and `/api/workstream/actions` in `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` resolve JWT identity plus selected `X-Selected-Context-Id`; `WorkstreamService#personalAttentionDigestBlockedSurface` returns backend-owned `my_account.personal_attention_digest.blocked.v1` system-message payloads with task ids when authorized, browser-safe account context, blocker category/code, retry eligibility, recovery steps, admin-readiness hints, required capability/tool ids, provider/runtime readiness summaries, evidence-window/no-source-mutation data, trace/correlation refs, and mandatory `noFakeSuccess`/`noDirectMutation`; digest start/read actions route provider/runtime fail-closed tasks to this blocked surface rather than fixture/model-less success, with generic direct-surface recovery when no task is selected. Runtime evidence: readiness level `api-smoked` for implementation verification with protected API/action path, tenant admin AuthContext in `tenant-starter`, provider/runtime fail-closed/no-fake-success/no-direct-mutation coverage, missing-bearer rejection in the protected smoke path, cross-account denial inherited from focused digest service coverage, trace/correlation evidence, browser-safe redaction, and no frontend-only/fixture-only normal runtime path. Checks passed: focused protected Akka-hosted smoke, frontend My Account contract test, frontend typecheck, focused `rg` evidence, and `git diff --check`. | 2026-06-16 |
| fully-tested | done | Added and passed dedicated protected Akka-hosted My Account personal attention digest blocked runtime smoke coverage in `src/test/java/ai/first/application/coreapp/workstream/MyAccountBrowserWorkstreamSmokeTest.java`: JWT plus selected AuthContext loads the direct `surface-my-account-personal-attention-digest-blocked` generic fail-closed recovery surface, verifies backend-owned `my_account.personal_attention_digest.blocked.v1` system-message payload, provider/runtime readiness summaries, retry/admin recovery steps, trace/correlation, disabled/no-selected-task state, `noFakeSuccess`, `noDirectMutation`, and browser-safe secret/tool/model boundaries; starts a digest through `/api/workstream/actions` and observes the provider/runtime blocked surface with a durable task id and source-state-unchanged evidence; reads the blocked task through the governed action path; denies member/cross-account read without enumeration; and rejects missing-bearer access. Supporting frontend evidence: `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs` and `npm --prefix frontend run typecheck` passed. Runtime readiness level: `runtime-ready` for the blocked digest testing scope via protected API/action path with tenant admin and member AuthContext coverage, provider-fail-closed/no-fake-success/no-direct-mutation/trace/correlation/secret-boundary evidence, and no fixture-only/frontend-only normal runtime path. | 2026-06-16 |

### `surface-my-account-open-denied`

- Workstream: My Account
- Type: `system-message`
- Contract: `my_account.open_denied.v1`
- Purpose: Safe not-found/redacted/unavailable workstream recovery.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract verified and completed in `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`: includes `surface-my-account-open-denied` identity/owner/purpose, `my_account.open_denied.v1` system-message payload schema, safe denial decisions/reason codes, selected AuthContext/account context, no-enumeration redaction rules, governed recovery actions for dashboard return/context refresh/retry/request-access/trace, backend authorization and tenant/customer isolation expectations, trace/audit/correlation contract, accessibility/responsive behavior, explicit states, acceptance/security/observability regression coverage, and sufficiency review. The surface is implementable without inventing fields/actions/states/auth/traces/tests and preserves hidden-target secrecy. | 2026-06-16 |
| fully-implemented | done | Runtime implementation completed and verified for `surface-my-account-open-denied`: protected `/api/workstream/surfaces/surface-my-account-open-denied` and `/api/workstream/actions` in `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` resolve JWT identity plus selected `X-Selected-Context-Id`; `WorkstreamService#myAccountOpenDeniedSurface` returns the backend-owned `my_account.open_denied.v1` system-message envelope with `decision`, `safeReasonCode`, browser-safe `accountContext`, redacted requested-target/source-action summaries, ordered recovery steps/details, authorized recovery `availableActions`, trace refs, correlation id, `noEnumeration`, and secret-boundary safety metadata. Hidden workstream/action denials from `MyAccountService#openAuthorizedWorkstream` route to this surface without naming missing capabilities or hidden workstreams, and direct protected surface reads return a generic unavailable recovery surface rather than a fixture or frontend-only error. Runtime evidence: readiness level `api-smoked` for implementation verification with protected API/surface/action paths, tenant member/admin AuthContext coverage in `tenant-starter`, hidden-target denial/no-enumeration evidence, trace/correlation evidence, browser-safe redaction, missing-bearer rejection inherited from the protected smoke path, and no frontend-only/fixture-only normal runtime path. Checks passed: `mvn -q -Dtest=WorkstreamServiceTest#myAccountOpenWorkstreamDeniesHiddenTargetsWithSystemMessage+myAccountOpenDeniedSurfaceIsDirectlyRetrievableThroughProtectedRuntimePath test`, `mvn -q -Dtest=MyAccountBrowserWorkstreamSmokeTest#hostedShellAndProtectedWorkstreamApiExerciseMyAccountDashboardRuntimePath test`, `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs`, focused `rg` evidence, `git diff --check`, and runtime evidence validator. | 2026-06-16 |
| fully-tested | done | Added and passed dedicated protected Akka-hosted My Account open-denied runtime smoke coverage in `src/test/java/ai/first/application/coreapp/workstream/MyAccountBrowserWorkstreamSmokeTest.java`: JWT plus selected member AuthContext loads the My Account dashboard, attempts to open hidden Agent Admin through `/api/workstream/actions`, verifies backend-owned `my_account.open_denied.v1` system-message payload with safe `not_found_or_redacted` status/decision, no-enumeration recovery actions, trace/correlation evidence, request-access guidance, and browser-safe redaction that omits hidden workstream names, missing capabilities/roles, provider secrets, model/provider fixture data, and bearer/provider payloads; direct protected `/api/workstream/surfaces/surface-my-account-open-denied` returns the generic recovery surface; missing-bearer access is rejected. Supporting frontend evidence: `npm --prefix frontend test -- --run src/workstream-my-account-vertical.contract.test.mjs` and `npm --prefix frontend run typecheck` passed. Runtime readiness level: `runtime-ready` for the open-denied testing scope via protected API/action/surface paths with tenant member AuthContext in `tenant-starter`, denial/no-enumeration/trace/correlation/secret-boundary evidence, and no fixture-only/frontend-only normal runtime path. | 2026-06-16 |

## User Admin

### `surface-user-admin-dashboard`

- Workstream: User Admin
- Type: `dashboard`
- Contract: `user_admin.dashboard.v1`
- Purpose: Attention-first User Admin command center for SaaS Owner Admin, Organization, Organization Admin, directory, invitation, role, support, review, provider, and audit health.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract verified in `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`: includes the `surface-user-admin-dashboard` identity/type/contract, owning User Admin workstream and `user-admin-agent`, selected AuthContext/admin-level/authority requirements, backend-authored trunk and branch navigation, attention-first UX model, frontend-safe payload fields (`hero`, `attentionCounts[]`, `administeredPopulations[]`, `summaryCards[]`, `authorizedActions[]`, `recentActivity[]`), forbidden/redacted payload boundaries, governed capability/tool action mappings, direct-denial recovery through `surface-user-admin-system-message`, trace/correlation/audit requirements, rendering states, keyboard/responsive expectations through the User Admin graph tests, and sufficiency-review statements that the dashboard/tree contract is implementable without inventing surface ids, actions, states, auth/tenant behavior, trace links, tests, or visual semantics. | 2026-06-17 |
| fully-implemented | done | Runtime implementation verified for `surface-user-admin-dashboard`: protected `/api/workstream/surfaces/surface-user-admin-dashboard`, `/api/workstream/bootstrap`, and `/api/workstream/actions` in `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` resolve JWT identity plus selected `X-Selected-Context-Id`; `WorkstreamService#dashboardSurface` returns backend-authored User Admin dashboard variants with canonical `surface-user-admin-dashboard` metadata, `user_admin.*_dashboard.v1` contracts, attention counts, administered populations, branch actions, authorized actions, selected AuthContext, trace/correlation refs, redaction, and provider/runtime fail-closed access-review state; `DashboardSurface` renders the User Admin command center from the backend envelope with clickable attention counters, authorized actions, and population cards rather than frontend-derived authority. Runtime evidence: readiness level `api-smoked` for implementation verification with Akka-hosted `/ui` shell plus protected API/surface/action paths, tenant admin and SaaS Owner AuthContext coverage, tenant member/hidden organization denial coverage, access-review provider/runtime fail-closed `blocked_provider_or_runtime`/`noDirectMutation` evidence, trace/correlation refs, browser-safe token/provider/secret redaction, and no frontend-only/fixture-only normal runtime path. Checks passed: `mvn -q -Dtest=UserAdminBrowserWorkstreamSmokeTest,WorkstreamServiceTest#userAdminDashboardAndUsersListAreBackendDerivedAndScoped+userAdminConformancePathCoversBackendAuthoredRoutingTypedResultsAndSafePayloads+tenantUserAdminOmitsOrganizationBranchAndDirectAccessIsDeniedSafely+saasOwnerUserAdminDashboardExposesOrganizationAdminSurface test`, `npm --prefix frontend test -- --run src/workstream-user-admin-vertical.contract.test.mjs`, `npm --prefix frontend run typecheck`, and `git diff --check`. | 2026-06-17 |
| fully-tested | done | Expanded and passed protected Akka-hosted User Admin dashboard runtime smoke coverage in `src/test/java/ai/first/application/coreapp/workstream/UserAdminBrowserWorkstreamSmokeTest.java`: `/ui` shell loads from Akka static resources, missing-bearer bootstrap/surface/action requests are rejected, JWT plus selected tenant-admin `AuthContext` bootstraps `/api/workstream/bootstrap`, opens canonical `surface-user-admin-dashboard` as backend-authored tenant dashboard variant `user_admin.tenant_dashboard.v1`, verifies dashboard actions, trace refs, correlation id, and browser-safe secret boundaries, starts access review and observes fail-closed `blocked_provider_or_runtime` with `noDirectMutation`, traverses dashboard-to-users-to-detail and invitation-create/detail action paths through `/api/workstream/actions`, exercises identity-exception review routing, and verifies hidden invitation denial through `surface-user-admin-system-message` without enumeration. Supporting frontend evidence: `npm --prefix frontend test -- --run src/workstream-user-admin-vertical.contract.test.mjs` and `npm --prefix frontend run typecheck` passed. Runtime readiness level: `runtime-ready` for the User Admin dashboard testing scope via Akka-hosted UI shell plus protected API/action paths with tenant admin AuthContext in `tenant-starter`, denial/provider-fail-closed/trace/correlation/provider-secret/token redaction evidence, and no fixture-only/frontend-only normal runtime path. | 2026-06-17 |

### `surface-user-admin-saas-owner-admins`

- Workstream: User Admin
- Type: `list-search`
- Contract: `user_admin.saas_owner_admins.v1`
- Purpose: SaaS Owner scoped directory for app-owner/admin users and invitations.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract completed in `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`: includes `surface-user-admin-saas-owner-admins` identity/type/contract, owning User Admin workstream and `user-admin-agent`, first-level SaaS Owner branch placement, selected SaaS Owner/App Admin `AuthContext` and `saas_owner.admin.list` authority, discovery-only purpose, frontend-safe envelope/summary/row/filter/action payload schema, forbidden/redacted payload boundaries, governed capability/tool action mappings for list/reload, invite-form open, visible admin/invitation row open, audit drilldown, and dashboard return, typed forbidden/hidden/stale/partial/failure states, no-enumeration recovery through `surface-user-admin-system-message`, trace/audit/correlation requirements, accessibility/responsive and keyboard expectations through tests, and a sufficiency-review statement that the list/search objective is implementable without invented fields/actions/states/auth/tenant behavior/trace links/tests/visual semantics. | 2026-06-17 |
| fully-implemented | done | Runtime implementation completed and verified for `surface-user-admin-saas-owner-admins`: protected `/api/workstream/surfaces/surface-user-admin-saas-owner-admins` and `/api/workstream/actions` in `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` resolve JWT identity plus selected `X-Selected-Context-Id`; `WorkstreamService#saasOwnerAdminsSurface` returns the backend-owned `user_admin.saas_owner_admins.v1` list-search envelope with selected SaaS Owner scope, branch metadata, backend-authored filters/sort/page state, summary counts, admin membership and app-owner invitation rows, detail/invitation-detail target actions, invite-form and audit actions, trace refs/correlation, no-enumeration redaction, and boundary copy; `InvitationView`/`UserDirectoryView` provide browser-safe app-owner rows without raw WorkOS/JWT/provider/invitation-token data; `UserAdminScopedAdminSurface` renders the canonical list/search branch from the backend envelope and submits governed actions rather than frontend authority. Runtime evidence: readiness level `api-smoked` for implementation verification with protected workstream API/action path, SaaS Owner Admin AuthContext in tests, tenant-admin direct access denial as `CAPABILITY_FORBIDDEN`, seeded app-owner invitation row evidence, trace/correlation/redaction evidence, and no fixture-only/frontend-only normal runtime path. Checks passed: `mvn -q -Dtest=WorkstreamServiceTest#saasOwnerUserAdminDashboardExposesOrganizationAdminSurface test`, `npm --prefix frontend test -- --run src/workstream-user-admin-vertical.contract.test.mjs`, `npm --prefix frontend run typecheck`, focused `rg` evidence, and `git diff --check`. | 2026-06-17 |
| fully-tested | done | Added and passed protected Akka-hosted User Admin SaaS Owner Admins runtime smoke coverage in `src/test/java/ai/first/application/coreapp/workstream/UserAdminBrowserWorkstreamSmokeTest.java`: seeded a SaaS Owner Admin identity, verified missing-bearer rejection for `/api/workstream/surfaces/surface-user-admin-saas-owner-admins`, bootstrapped `/api/workstream/bootstrap` with JWT plus selected SaaS Owner `AuthContext`, traversed SaaS Owner dashboard to `action-user-admin-show-saas-owner-admins` through `/api/workstream/actions`, verified backend-owned `user_admin.saas_owner_admins.v1` list-search payload with SaaS Owner scope, branch metadata, backend-authored summary/filter/action/row data, detail row routing, trace/correlation evidence, and browser-safe redaction, loaded the direct protected surface path, and verified a Tenant Admin selected context is forbidden from opening the SaaS Owner branch. Supporting frontend evidence: `npm --prefix frontend test -- --run src/workstream-user-admin-vertical.contract.test.mjs` and `npm --prefix frontend run typecheck` passed. Runtime readiness level: `runtime-ready` for the SaaS Owner Admins list testing scope via protected API/action/surface paths with SaaS Owner and Tenant Admin AuthContext coverage, denial/trace/correlation/provider-token/secret-boundary evidence, and no fixture-only/frontend-only normal runtime path. | 2026-06-17 |

### `surface-user-admin-saas-owner-admin-invitation-create`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.saas_owner_admin_invitation_create.v1`
- Purpose: Invitation form for another SaaS Owner Admin with role validation, idempotency, outbox boundary, and audit.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | done | App-description contract completed in `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`: includes `surface-user-admin-saas-owner-admin-invitation-create` identity/type/contract, owning User Admin workstream and `user-admin-agent`, SaaS Owner Admin branch placement and return metadata, selected SaaS Owner/App Admin `AuthContext` plus `saas_owner.admin.invite` authority, single-purpose invite semantics, frontend-safe envelope/form/policy-context/delivery-readiness/action payload schema, forbidden/redacted payload boundaries for invitation tokens, WorkOS/JWT/provider/outbox secrets and hidden app-owner population details, governed capability/tool action mappings for open, submit, branch return, audit, and dashboard return, validation/duplicate/no-op/provider-outbox-blocked/forbidden/stale/failure states, no-enumeration recovery through `surface-user-admin-system-message`, trace/audit/correlation requirements, accessibility/responsive and keyboard test expectations, and a sufficiency-review statement that the create-form objective is implementable without invented fields/actions/states/auth/tenant behavior/trace links/tests/visual semantics. | 2026-06-17 |
| fully-implemented | done | Runtime implementation completed and verified for `surface-user-admin-saas-owner-admin-invitation-create`: protected `/api/workstream/surfaces/surface-user-admin-saas-owner-admin-invitation-create` and `/api/workstream/actions` in `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java` resolve JWT identity plus selected `X-Selected-Context-Id`; `WorkstreamService#saasOwnerAdminInvitationCreateSurface` now returns the backend-owned `user_admin.saas_owner_admin_invitation_create.v1` create-form envelope with SaaS Owner scope, branch return metadata, `form`, `policyContext`, `deliveryReadiness`, submit action, trace/correlation refs, and redaction boundaries; `action-submit-saas-owner-admin-invitation` reauthorizes the selected SaaS Owner AuthContext against `saas_owner.admin.invite`, limits role submission to `SAAS_OWNER_ADMIN`, creates/reuses invitations through `InvitationService` with idempotency/outbox/provider boundaries, and routes to browser-safe invitation detail without exposing tokens/provider payloads; `UserAdminScopedAdminSurface` submits the dedicated SaaS Owner action instead of the generic user-invite action. Runtime evidence: readiness level `api-smoked` for implementation verification with protected API/surface/action path, SaaS Owner Admin AuthContext in `membership-owner`, Tenant Admin denial inherited from the SaaS Owner branch, successful app-owner invitation submission and idempotency/outbox path evidence, trace/correlation and browser-safe redaction evidence, and no fixture-only/frontend-only normal runtime path. Checks passed: `mvn -q -Dtest=WorkstreamServiceTest#saasOwnerUserAdminDashboardExposesOrganizationAdminSurface test`, `mvn -q -Dtest=UserAdminBrowserWorkstreamSmokeTest#protectedWorkstreamApiExercisesSaasOwnerAdminsRuntimePath test`, `npm --prefix frontend test -- --run src/workstream-user-admin-vertical.contract.test.mjs`, `npm --prefix frontend run typecheck`, focused `rg` evidence, and `git diff --check`. | 2026-06-17 |
| fully-tested | done | Expanded and passed protected Akka-hosted SaaS Owner Admin invitation create runtime smoke coverage in `src/test/java/ai/first/application/coreapp/workstream/UserAdminBrowserWorkstreamSmokeTest.java`: JWT plus selected SaaS Owner `AuthContext` opens the dashboard, list branch, and create-form path, verifies backend-owned `user_admin.saas_owner_admin_invitation_create.v1` form/delivery-readiness/action/trace/correlation and browser-safe redaction payloads, submits `action-submit-saas-owner-admin-invitation` through `/api/workstream/actions`, verifies invitation-detail result routing and token/provider secret boundaries, exercises repeated idempotency-key duplicate/open-invite behavior, validates unsupported role and missing-idempotency failures, denies tenant-admin SaaS Owner branch access without enumeration, and rejects missing-bearer surface access. Supporting frontend evidence: `npm --prefix frontend test -- --run src/workstream-user-admin-vertical.contract.test.mjs` and `npm --prefix frontend run typecheck` passed. Runtime readiness level: `runtime-ready` for the create-form testing scope via protected API/action/surface paths with SaaS Owner Admin AuthContext `membership-owner`, tenant-admin denial, validation/idempotency/trace/correlation/outbox-provider boundary evidence, and no fixture-only/frontend-only normal runtime path. | 2026-06-17 |

### `surface-user-admin-users`

- Workstream: User Admin
- Type: `list-search`
- Contract: `user_admin.users.v1`
- Purpose: Scoped searchable directory for users/memberships.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-user-detail`

- Workstream: User Admin
- Type: `show-inspection`
- Contract: `user_admin.user_detail.v1`
- Purpose: Scoped account, membership, invitation, support-access, access-review, identity, and audit inspection.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-invitation-create`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.invitation_create.v1`
- Purpose: Single-purpose invitation creation form.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-invitation-detail`

- Workstream: User Admin
- Type: `show-inspection / workflow-status`
- Contract: `user_admin.invitation_detail.v1`
- Purpose: Lifecycle-aware invitation inspection.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-invitation-resend-confirmation`

- Workstream: User Admin
- Type: `lifecycle-confirmation`
- Contract: `user_admin.invitation_resend_confirmation.v1`
- Purpose: Single-purpose resend confirmation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-invitation-revoke-confirmation`

- Workstream: User Admin
- Type: `destructive-lifecycle-confirmation`
- Contract: `user_admin.invitation_revoke_confirmation.v1`
- Purpose: Single-purpose invitation revoke confirmation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-membership-status-confirmation`

- Workstream: User Admin
- Type: `destructive-lifecycle-confirmation`
- Contract: `user_admin.membership_status_confirmation.v1`
- Purpose: Disable/suspend/reactivate/remove membership or account confirmation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-role-change-preview`

- Workstream: User Admin
- Type: `decision-card / diff`
- Contract: `user_admin.role_change_preview.v1`
- Purpose: Capability delta and approval preview before role mutation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-support-access-grant`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.support_access_grant.v1`
- Purpose: Support-access grant/extend form.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-support-access-revoke-confirmation`

- Workstream: User Admin
- Type: `destructive-lifecycle-confirmation`
- Contract: `user_admin.support_access_revoke_confirmation.v1`
- Purpose: Support-access revoke confirmation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-access-review-task`

- Workstream: User Admin
- Type: `workflow-status / outcome-panel`
- Contract: `user_admin.access_review_task.v1`
- Purpose: Durable access-review task progress, result, blockers, and human review.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-identity-exception-review`

- Workstream: User Admin
- Type: `decision-card / workflow-status`
- Contract: `user_admin.identity_exception_review.v1`
- Purpose: Identity-link/relink exception review and approved recovery routing.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-directory`

- Workstream: User Admin
- Type: `list-search`
- Contract: `user_admin.organization_directory.v1`
- Purpose: SaaS Owner Organization directory.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-detail`

- Workstream: User Admin
- Type: `show-inspection`
- Contract: `user_admin.organization_detail.v1`
- Purpose: Lifecycle-aware Organization inspection.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-admins`

- Workstream: User Admin
- Type: `list-search`
- Contract: `user_admin.organization_admins.v1`
- Purpose: Directory of Organization Admin users/invitations for one selected Organization/Tenant.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-admin-invitation-create`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.organization_admin_invitation_create.v1`
- Purpose: Bootstrap/invite form for a TENANT_ADMIN.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-admin-detail`

- Workstream: User Admin
- Type: `show-inspection`
- Contract: `user_admin.organization_admin_detail.v1`
- Purpose: Shows one Organization Admin membership/invitation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-create`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.organization_create.v1`
- Purpose: Organization creation form.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-rename`

- Workstream: User Admin
- Type: `edit-form`
- Contract: `user_admin.organization_rename.v1`
- Purpose: Organization display-name edit surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-suspend-confirmation`

- Workstream: User Admin
- Type: `destructive-lifecycle-confirmation`
- Contract: `user_admin.organization_suspend_confirmation.v1`
- Purpose: Organization suspension confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-organization-reactivate-confirmation`

- Workstream: User Admin
- Type: `lifecycle-confirmation`
- Contract: `user_admin.organization_reactivate_confirmation.v1`
- Purpose: Organization reactivation confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-directory`

- Workstream: User Admin
- Type: `list-search`
- Contract: `user_admin.customer_directory.v1`
- Purpose: Organization Admin Customer directory.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-detail`

- Workstream: User Admin
- Type: `show-inspection`
- Contract: `user_admin.customer_detail.v1`
- Purpose: Lifecycle-aware Customer inspection surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-admins`

- Workstream: User Admin
- Type: `list-search`
- Contract: `user_admin.customer_admins.v1`
- Purpose: Customer Admin users/invitations for one selected Customer.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-admin-invitation-create`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.customer_admin_invitation_create.v1`
- Purpose: Bootstrap/invite form for a CUSTOMER_ADMIN.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-admin-detail`

- Workstream: User Admin
- Type: `show-inspection`
- Contract: `user_admin.customer_admin_detail.v1`
- Purpose: Shows one Customer Admin membership/invitation.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-create`

- Workstream: User Admin
- Type: `create-form`
- Contract: `user_admin.customer_create.v1`
- Purpose: Customer creation form.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-rename`

- Workstream: User Admin
- Type: `edit-form`
- Contract: `user_admin.customer_rename.v1`
- Purpose: Customer display-name/profile edit surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-suspend-confirmation`

- Workstream: User Admin
- Type: `destructive-lifecycle-confirmation`
- Contract: `user_admin.customer_suspend_confirmation.v1`
- Purpose: Customer suspension/archive confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-customer-reactivate-confirmation`

- Workstream: User Admin
- Type: `lifecycle-confirmation`
- Contract: `user_admin.customer_reactivate_confirmation.v1`
- Purpose: Customer reactivation confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-user-admin-system-message`

- Workstream: User Admin
- Type: `system-message`
- Contract: `user_admin.system_message.v1`
- Purpose: Safe denial, validation, provider/outbox/model blocked, stale, conflict, and no-op recovery.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

## Agent Admin

### `surface-agent-admin-dashboard`

- Workstream: Agent Admin
- Type: `dashboard`
- Contract: `agent_admin.dashboard.v1`
- Purpose: Agent Admin command center.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-admin-catalog`

- Workstream: Agent Admin
- Type: `list-search`
- Contract: `agent_admin.catalog.v1`
- Purpose: Managed agent catalog.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-admin-detail`

- Workstream: Agent Admin
- Type: `show-inspection`
- Contract: `agent_admin.detail.v1`
- Purpose: Agent readiness/behavior inspection.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-prompt-governance`

- Workstream: Agent Admin
- Type: `governance-diff / show-inspection`
- Contract: `agent_admin.prompt_governance.v1`
- Purpose: Prompt governance and behavior artifact review.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-skill-manifest-diff`

- Workstream: Agent Admin
- Type: `governance-diff / show-inspection`
- Contract: `agent_admin.skill_manifest_diff.v1`
- Purpose: Skill manifest diff/review.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-tool-boundary-diff`

- Workstream: Agent Admin
- Type: `governance-diff / show-inspection`
- Contract: `agent_admin.tool_boundary_diff.v1`
- Purpose: Tool-boundary simulation and review.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-model-refs`

- Workstream: Agent Admin
- Type: `governance-diff / show-inspection`
- Contract: `agent_admin.model_refs.v1`
- Purpose: Model reference proposal/review.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-test-console`

- Workstream: Agent Admin
- Type: `workflow-status`
- Contract: `agent_admin.test_console.v1`
- Purpose: No-side-effect runtime test surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-activation-confirmation`

- Workstream: Agent Admin
- Type: `lifecycle-confirmation`
- Contract: `agent_admin.activation_confirmation.v1`
- Purpose: Activation confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-deactivation-confirmation`

- Workstream: Agent Admin
- Type: `lifecycle-confirmation`
- Contract: `agent_admin.deactivation_confirmation.v1`
- Purpose: Deactivation confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-rollback-confirmation`

- Workstream: Agent Admin
- Type: `lifecycle-confirmation`
- Contract: `agent_admin.rollback_confirmation.v1`
- Purpose: Rollback confirmation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-behavior-proposal`

- Workstream: Agent Admin
- Type: `decision-card / decision`
- Contract: `agent_admin.behavior_proposal.v1`
- Purpose: Behavior proposal decision card.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-admin-prompt-risk-review`

- Workstream: Agent Admin
- Type: `workflow-status`
- Contract: `agent_admin.prompt_risk_review.v1`
- Purpose: Prompt-risk autonomous review result.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-admin-trace`

- Workstream: Agent Admin
- Type: `audit-timeline`
- Contract: `agent_admin.trace.v1`
- Purpose: Agent Admin trace timeline.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-agent-seed-material`

- Workstream: Agent Admin
- Type: `list-search / workflow-status`
- Contract: `agent_admin.seed_material.v1`
- Purpose: Seed material discovery/import workflow surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

## Audit Trace

### `surface-audit-trace-dashboard`

- Workstream: Audit Trace
- Type: `dashboard`
- Contract: `audit.trace.dashboard.v1`
- Purpose: Investigation command center.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-search`

- Workstream: Audit Trace
- Type: `list-search`
- Contract: `audit.trace.search.v1`
- Purpose: Scoped trace search.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-detail`

- Workstream: Audit Trace
- Type: `detail-edit as read-only evidence`
- Contract: `audit.trace.detail.v1`
- Purpose: Browser-safe trace/event evidence detail.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-timeline`

- Workstream: Audit Trace
- Type: `audit-timeline`
- Contract: `audit.trace.timeline.v1`
- Purpose: Correlation timeline.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-failure-evidence`

- Workstream: Audit Trace
- Type: `detail-edit as read-only evidence`
- Contract: `audit.trace.failureEvidence.v1`
- Purpose: Denial/provider/tool/model/runtime failure evidence.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-investigation-guide`

- Workstream: Audit Trace
- Type: `decision-card`
- Contract: `audit.trace.investigationGuide.v1`
- Purpose: Human investigation guidance.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-export-request`

- Workstream: Audit Trace
- Type: `decision-card`
- Contract: `audit.trace.exportRequest.v1`
- Purpose: Policy-gated scoped redacted export request.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-investigation-note`

- Workstream: Audit Trace
- Type: `system-message`
- Contract: `audit.trace.investigationNote.v1`
- Purpose: Human investigation note append result.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-summary-progress`

- Workstream: Audit Trace
- Type: `workflow-status`
- Contract: `audit.trace.summaryProgress.v1`
- Purpose: Audit summary worker progress or fail-closed blocker.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-audit-trace-summary-review`

- Workstream: Audit Trace
- Type: `decision-card`
- Contract: `audit.trace.summaryReview.v1`
- Purpose: Human review of redacted advisory summary.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

## Governance Policy

### `surface-governance-policy-dashboard`

- Workstream: Governance Policy
- Type: `dashboard`
- Contract: `governance.policy.dashboard.v1`
- Purpose: Governance/Policy dashboard.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-inventory`

- Workstream: Governance Policy
- Type: `list-search`
- Contract: `governance.policy.inventory.v1`
- Purpose: Policy/proposal inventory and queue.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-proposal`

- Workstream: Governance Policy
- Type: `governance-diff`
- Contract: `governance.policy.proposal.v1`
- Purpose: Policy proposal lifecycle/diff surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-simulation`

- Workstream: Governance Policy
- Type: `governance-diff`
- Contract: `governance.policy.simulation.v1`
- Purpose: Advisory simulation surface.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-decision`

- Workstream: Governance Policy
- Type: `decision-card`
- Contract: `governance.policy.decision.v1`
- Purpose: Policy decision/activation/rollback card.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-outcome`

- Workstream: Governance Policy
- Type: `outcome-panel`
- Contract: `governance.policy.outcome.v1`
- Purpose: Policy outcome notes/metrics/evidence.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-impact-analysis-task`

- Workstream: Governance Policy
- Type: `workflow-status`
- Contract: `governance.policy.impact_analysis.task.v1`
- Purpose: Impact-analysis task progress/status.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-impact-analysis-result`

- Workstream: Governance Policy
- Type: `decision-card`
- Contract: `governance.policy.impact_analysis.result.v1`
- Purpose: Impact-analysis advisory result review.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |

### `surface-governance-policy-system-message`

- Workstream: Governance Policy
- Type: `system-message`
- Contract: `governance.policy.system_message.v1`
- Purpose: Governance/Policy safe system message.

| Objective | Status | Evidence / blocker | Last updated |
|---|---|---|---|
| fully-specified | pending |  |  |
| fully-implemented | pending |  |  |
| fully-tested | pending |  |  |
